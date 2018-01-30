/**
 * Copyright (c) 2012, Clinton Health Access Initiative.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the <organization> nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.chai.memms.spare.part

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.chai.location.CalculationLocation
import org.chai.location.DataLocation;
import org.chai.location.DataLocationType;
import org.chai.location.Location;
import org.chai.location.LocationLevel;
import org.chai.memms.inventory.Equipment;
import org.chai.memms.spare.part.SparePart;
import org.chai.memms.spare.part.SparePart.StockLocation;
import org.chai.memms.security.User;
import org.chai.memms.spare.part.SparePart.SparePartPurchasedBy;
import org.chai.memms.spare.part.SparePartType;
import org.chai.memms.spare.part.SparePart.SparePartStatus;
import org.chai.memms.security.User.UserType;
import org.supercsv.io.CsvListWriter;
import org.supercsv.io.ICsvListWriter;
import org.supercsv.prefs.CsvPreference;
import org.chai.memms.util.ImportExportConstant;
import org.chai.memms.corrective.maintenance.UsedSpareParts;
import org.chai.memms.inventory.EquipmentService

/**
 * @author Aphrodice Rwagaju
 *
 */
class SparePartService {
	static transactional = true
	def languageService;
	def userService
	def equipmentService
	
	
	public def getRemoveFromStock(def sparePart, def numberToRemove){
		if(!sparePart || (numberToRemove - sparePart.inStockQuantity < 0)) 
			return false
		else{ 
			sparePart.inStockQuantity = numberToRemove - sparePart.inStockQuantity
			sparePart.save(failOnError:true)
			return true
		}
	}

	public def findSpareParts(def text,def calculationLocations,def stockLocation,Map<String, String> params){
		//If nothing is specified in the form list all sparepart
		def criteria = SparePart.createCriteria();
		if(calculationLocations.isEmpty() && stockLocation.equals(StockLocation.NONE) && text==null)
			return criteria.list(offset:params.offset,max:params.max,sort:params.sort ?:"id",order: params.order ?:"desc"){
				eq("status",SparePartStatus.INSTOCK)
			    gt("inStockQuantity",0)	
			}

		if(text!=null) text = text.trim()
		def dataLocations = equipmentService.getDataLocationsOfCalculationLocation(calculationLocations);
		def dbFieldTypeNames = 'names_'+languageService.getCurrentLanguagePrefix();
		def dbFieldDescriptions = 'descriptions_'+languageService.getCurrentLanguagePrefix();
		
		return  criteria.list(offset:params.offset,max:params.max,sort:params.sort ?:"id",order: params.order ?:"desc"){			    
				if(dataLocations.size()>0)
					'in'('dataLocation',dataLocations)
				if(stockLocation && !stockLocation.equals(StockLocation.NONE))
					eq("stockLocation",stockLocation)
				eq("status",SparePartStatus.INSTOCK)
			    gt("inStockQuantity",0)				    
			    if(text!=null){
			    	createAlias("type","t")
					or{
						ilike("room","%"+text+"%")
						ilike("shelf","%"+text+"%")
						ilike(dbFieldDescriptions,"%"+text+"%") 
						ilike("t.code","%"+text+"%")
						ilike("t.partNumber","%"+text+"%")
						ilike("t.model","%"+text+"%")
						ilike("t."+dbFieldTypeNames,"%"+text+"%")
						ilike("t."+dbFieldDescriptions,"%"+text+"%")
				   	}
				}
		}

	}

	//Assign sparePart to workOrder - need to be intensively tested
	public def assignSparePartsToWorkOrder(def order, def sparePartType, def user, def quantity){
		def usedSpareParts = order.usedSpareParts
		def changedSpareParts = [:]
		def diff = null	
		def dataLocation = order.equipment.dataLocation
		def remainingToAdd = 0
		def tempSpareParts =   getInStockSparePartOfTypes([sparePartType],user,dataLocation)
		for(def sparePart: tempSpareParts){
			if(diff==null || diff > 0) diff = sparePart.inStockQuantity-(quantity - remainingToAdd)
			else diff = sparePart.inStockQuantity - (diff*-1)
			def usedSparePart = order.getSparePartTypeUsed(sparePartType);
			if(diff >= 0){
				if(usedSparePart)
					if(remainingToAdd != 0) usedSparePart.quantity = usedSparePart.quantity+remainingToAdd
					else usedSparePart.quantity = usedSparePart.quantity+quantity
				else newUsedSpareParts(order,null,sparePart.type,quantity)
				changedSpareParts.put(sparePart,diff)
				break;
			}else{
				remainingToAdd = (diff*-1)
				if(usedSparePart)
					usedSparePart.quantity = usedSparePart.quantity+sparePart.inStockQuantity
				else  newUsedSpareParts(order,null,sparePart.type,sparePart.inStockQuantity)
				changedSpareParts.put(sparePart,0)
			}
		}
		for(def sparePart : changedSpareParts){
			sparePart.key.lastModified = user
			sparePart.key.inStockQuantity= sparePart.value
			sparePart.key.save(failOnError:true)
		}		

		return order.save(failOnError:true, flush:true)
	}
	//Assign sparePart to prevention (PreventiveOrder) - need to be intensively tested 
	public def assignSparePartsToPrevention(def prevention, def sparePartType, def user, def quantity){
		def usedSpareParts = prevention.usedSpareParts
		def order = prevention.order
		def dataLocation = order.equipment.dataLocation
		def changedSpareParts = [:]
		def diff = null	
		def remainingToAdd = 0

		def usedSparePart = prevention.getSparePartTypeUsed(sparePartType);
		def tempSpareParts =   getInStockSparePartOfTypes([sparePartType],user,dataLocation)
		
		for(def sparePart: tempSpareParts){
			if(diff==null || diff > 0) diff = sparePart.inStockQuantity-quantity
			else diff = sparePart.inStockQuantity - (diff*-1)
			
			if(diff >= 0){
				if(usedSparePart)
					if(remainingToAdd != 0) usedSparePart.quantity = usedSparePart.quantity+remainingToAdd
					else usedSparePart.quantity = usedSparePart.quantity+quantity
				else newUsedSpareParts(order,prevention,sparePart.type,quantity)
				changedSpareParts.put(sparePart,diff)
				break;
			}else{
				remainingToAdd = (diff*-1)
				if(usedSparePart) usedSparePart.quantity = usedSparePart.quantity+sparePart.inStockQuantity
				else  newUsedSpareParts(order,prevention,sparePart.type,sparePart.inStockQuantity)
				changedSpareParts.put(sparePart,0)
			}
		}
		for(def sparePart : changedSpareParts){
			sparePart.key.lastModified = user
			sparePart.key.inStockQuantity= sparePart.value
			sparePart.key.save(failOnError:true)
		}		
		return prevention.save(failOnError:true, flush:true)
	}

	//To be intensively tested
    public def getCompatibleSparePart(def equipmentType, def user,def dataLocation){
    	def tempSpareParts =  getInStockSparePartOfTypes(equipmentType.sparePartTypes,user,dataLocation)
		def spareParts = [:]
		for(def sparePart : tempSpareParts){
			if(spareParts.get(sparePart.type)==null){ 
				spareParts.put(sparePart.type,sparePart.inStockQuantity)
			}else{
				//Update the map
				def value = spareParts.get(sparePart.type)
				def inStockQuantity = sparePart.inStockQuantity
				spareParts.put(sparePart.type,value+inStockQuantity)
			}
		}
		return spareParts
	}
	//To be intensively tested
	//Make sure location are well considered while loading sparePart
	public def getInStockSparePartOfTypes(def types, def user,def dataLocation){
    	def criteria = SparePart.createCriteria()
    	if(!types || types.size()==0)
    		return []
    	return  criteria.list(){

			and{
				'in'("type",types)
				gt("inStockQuantity",0)
				ne("status",SparePartStatus.PENDINGORDER)
				if(user.userType == UserType.TECHNICIANMMC)
					eq("stockLocation",StockLocation.MMC)
				if(user.userType == UserType.TECHNICIANDH)
					eq("stockLocation",StockLocation.FACILITY)
				if(dataLocation && (user.userType != UserType.TECHNICIANMMC))
					eq("dataLocation",dataLocation)
			}
		}

	}

	public def searchSparePart(String text,User user, SparePartType type,Map<String,String> params) {
		//Remove unnecessary blank space
		text= text.trim()
		def dbFieldTypeNames = 'names_'+languageService.getCurrentLanguagePrefix();
		def dbFieldDescriptions = 'descriptions_'+languageService.getCurrentLanguagePrefix();
		def dataLocations = []
		def criteria = SparePart.createCriteria();

		//if(!user.userType.equals(UserType.ADMIN) && !user.userType.equals(UserType.TECHNICIANMMC) && !user.userType.equals(UserType.SYSTEM) && !user.userType.equals(UserType.TECHNICIANDH))
		if(!user.userType.equals(UserType.ADMIN) && !user.userType.equals(UserType.TECHNICIANMMC) && !user.userType.equals(UserType.SYSTEM))
		{
			if(user.location instanceof Location)
				dataLocations.addAll(user.location.getDataLocations([:], [:]))
			else{
				dataLocations.add((DataLocation)user.location)
				if(userService.canViewManagedSpareParts(user)) dataLocations.addAll((user.location as DataLocation).manages?.asList())
			}
		}

		return  criteria.list(offset:params.offset,max:params.max,sort:params.sort ?:"id",order: params.order ?:"desc"){
			createAlias("type","t")
			if(type!=null)
				eq("type",type)
			if(!dataLocations.isEmpty())
				inList('dataLocation',dataLocations)
			or{
				ilike(dbFieldDescriptions,"%"+text+"%")
				ilike("t."+dbFieldTypeNames,"%"+text+"%")
				ilike("t."+dbFieldDescriptions,"%"+text+"%")
				ilike("t.code","%"+text+"%")
				ilike("t.model","%"+text+"%")
			}
		}
	}

	public def getSpareParts(User user, SparePartType type,Map<String,String> params) {
		def dataLocations = []
		def criteria = SparePart.createCriteria();

		//if(user.userType.equals(UserType.ADMIN) || user.userType.equals(UserType.TECHNICIANMMC) || user.userType.equals(UserType.SYSTEM) || user.userType.equals(UserType.TECHNICIANDH))
		if(user.userType.equals(UserType.ADMIN) || user.userType.equals(UserType.TECHNICIANMMC) || user.userType.equals(UserType.SYSTEM))
			return criteria.list(offset:params.offset,max:params.max,sort:params.sort ?:"id",order: params.order ?:"desc"){
				if(type!=null) eq("type",type)
		}
		else{
			if(user.location instanceof Location)
				dataLocations.addAll(user.location.collectDataLocations(null))
			else{
				def location = (DataLocation)user.location
				dataLocations.add(location)
				if(userService.canViewManagedSpareParts(user)) 
					(location.manages==null)?:dataLocations.addAll(location.manages?.asList())
			}

			if(log.isDebugEnabled()) log.debug("user: " + user + " user's managed dataLocations: " + dataLocations)

			return criteria.list(offset:params.offset,max:params.max,sort:params.sort ?:"id",order: params.order ?:"desc"){  
				inList('dataLocation',dataLocations)  
			}

		}
	}

	public def filterSparePart(User user, def supplier, def type,def stockLocation,def sparePartPurchasedBy,def status,Map<String, String> params){

		def dataLocations = []
		
		//if(!user.userType.equals(UserType.ADMIN) && !user.userType.equals(UserType.TECHNICIANMMC) && !user.userType.equals(UserType.SYSTEM) && !user.userType.equals(UserType.TECHNICIANDH)){
		if(!user.userType.equals(UserType.ADMIN) && !user.userType.equals(UserType.TECHNICIANMMC) && !user.userType.equals(UserType.SYSTEM)){
			dataLocations = []
			if(user.location instanceof Location)
				dataLocations.addAll(user.location.collectDataLocations(null))
			else{
				def location = (DataLocation)user.location
				dataLocations.add(location)
				if(userService.canViewManagedSpareParts(user)) 
					(location.manages==null)?:dataLocations.addAll(location.manages?.asList())
			}

			if(log.isDebugEnabled()) log.debug(" user: " + user + " user's managed dataLocations: " + dataLocations)
		}

		def criteria = SparePart.createCriteria();
		if (log.isDebugEnabled()) 
			log.debug("spare.parts.filter dataLocations="+dataLocations+" type="+type+" supplier="+supplier+" stockLocation="+stockLocation+" sparePartPurchasedBy="+sparePartPurchasedBy+" status="+status+" params="+params)
		
		return criteria.list(offset:params.offset,max:params.max,sort:params.sort ?:"id",order: params.order ?:"desc"){
			if(dataLocations != null && !dataLocations.empty)
				inList('dataLocation',dataLocations)
			if(supplier != null)
				eq ("supplier", supplier)
			if(type != null)
				eq ("type", type)
			if(sparePartPurchasedBy != null && !sparePartPurchasedBy.equals(SparePartPurchasedBy.NONE))
				eq ("sparePartPurchasedBy",sparePartPurchasedBy)
			if(status != null && !status.equals(SparePartStatus.NONE))
				eq ("status",status)
			if(stockLocation != null && !stockLocation.equals(StockLocation.NONE))
				eq ("stockLocation",stockLocation)
		}
	}
	//TO DO APHRO TO CONTINUE WORKING ON THIS METHODE TO MAKE SURE USER IS ACCESSING ONLY HIS SPARE PART
	public def userScopeSparePartExport(User user, Map<String,String> params){
		def dataLocations = []
		def criteria = SparePart.createCriteria();
		
		if(!user.userType.equals(UserType.ADMIN) && user.userType.equals(!UserType.TECHNICIANMMC) && user.userType.equals(!UserType.SYSTEM)){
			dataLocations = []
			if(user.location instanceof Location)
				dataLocations.addAll(user.location.collectDataLocations(null))
			else{
				def location = (DataLocation)user.location
				dataLocations.add(location)
				if(userService.canViewManagedSpareParts(user))
					(location.manages==null)?:dataLocations.addAll(location.manages?.asList())
			}

			if(log.isDebugEnabled()) log.debug(" user: " + user + " user's managed dataLocations: " + dataLocations)
		}

		if (log.isDebugEnabled())
			log.debug("spare.parts.filter dataLocations="+dataLocations)
		
		return criteria.list(offset:params.offset,max:params.max,sort:params.sort ?:"id",order: params.order ?:"desc"){
			if(dataLocations != null && !dataLocations.empty)
				inList('dataLocation',dataLocations)
		}
	}
	public File exporter(def location,List<SparePart> spareParts){
		if (log.isDebugEnabled()) log.debug("sparePartService.exporter, location code: "+location.code + ", ImportExportConstant: "+ImportExportConstant.CSV_FILE_EXTENSION)
		File csvFile = File.createTempFile(location.code+"_"+location.getNames(new Locale("en")).replaceAll(" ", "_")+"_spare_part_export",ImportExportConstant.CSV_FILE_EXTENSION);
		FileWriter csvFileWriter = new FileWriter(csvFile);
		ICsvListWriter writer = new CsvListWriter(csvFileWriter, CsvPreference.EXCEL_PREFERENCE);
		this.writeFile(writer,spareParts);
		return csvFile;
	}

	private void writeFile(ICsvListWriter writer,List<SparePart> spareParts) throws IOException {
		try{
			String[] csvHeaders = null;
			// headers
			if(csvHeaders == null){
				csvHeaders = this.getExportDataHeaders()
				writer.writeHeader(csvHeaders);
			}
			for(SparePart sparePart: spareParts){
				List<String> line = [
					sparePart.type?.code?:"",
					sparePart.type?.partNumber?:"",
					sparePart.type?.getNames(new Locale("en"))?:"",
					sparePart.type?.getNames(new Locale("fr"))?:"",
					sparePart.sparePartPurchasedBy?.name?:"",
					sparePart.dataLocation?.code?:"",
					sparePart.dataLocation?.getNames(new Locale("en"))?:"",
					sparePart.dataLocation?.getNames(new Locale("fr"))?:"",
					sparePart.type?.manufacturer?.contact?.contactName?:"",
					sparePart.type?.model?:"",
					sparePart.supplier?.code?:"",
					sparePart.supplier?.contact?.contactName?:"",
					sparePart.purchaseDate?:"",
					sparePart.orderedQuantity?:"",
					sparePart.receivedQuantity?:"",
					sparePart.inStockQuantity?:"",
					sparePart.purchaseCost?:"",
					sparePart.currency?:"",
				]
				writer.write(line)
			}
		} catch (IOException ioe){
			// TODO throw something that make sense
			throw ioe;
		} finally {
			writer.close();
		}
	}
	public List<String> getBasicInfo(){
		List<String> basicInfo = new ArrayList<String>();
		basicInfo.add("sparePart.export")
		return basicInfo;
	}
	public List<String> getExportDataHeaders() {
		List<String> headers = new ArrayList<String>();

		headers.add(ImportExportConstant.SPARE_PART_TYPE_CODE)
		headers.add(ImportExportConstant.SPARE_PART_TYPE_PART_NUMBER)
		headers.add(ImportExportConstant.SPARE_PART_TYPE_NAME_EN)
		headers.add(ImportExportConstant.SPARE_PART_TYPE_NAME_FR)
		headers.add(ImportExportConstant.SPARE_PART_PURCHASED_BY)
		headers.add(ImportExportConstant.LOCATION_CODE)
		headers.add(ImportExportConstant.LOCATION_NAME_EN)
		headers.add(ImportExportConstant.LOCATION_NAME_FR)
		headers.add(ImportExportConstant.MANUFACTURER_CONTACT_NAME)
		headers.add(ImportExportConstant.SPARE_PART_TYPE_MODEL)
		headers.add(ImportExportConstant.SUPPLIER_CODE)
		headers.add(ImportExportConstant.SUPPLIER_CONTACT_NAME)
		headers.add(ImportExportConstant.SUPPLIER_DATE)
		headers.add(ImportExportConstant.SPARE_PART_ORDERED_QUANTITY)
		headers.add(ImportExportConstant.SPARE_PART_RECEIVED_QUANTITY)
		headers.add(ImportExportConstant.SPARE_PART_INSTOCK_QUANTITY)
		headers.add(ImportExportConstant.SPARE_PART_PURCHASE_COST)
		headers.add(ImportExportConstant.SPARE_PART_PURCHASE_COST_CURRENCY)

		return headers;
	}
	public def newUsedSpareParts(def order,def prevention,def sparePartType,def quantity){
		def usedSpareParts = new UsedSpareParts(
				maintenanceOrder:order,
				prevention:prevention,
				sparePartType:sparePartType,
				quantity:quantity
		)
		return usedSpareParts.save(failOnError:true)
	}

}
