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

/**
 * @author Aphrodice Rwagaju
 *
 */
class SparePartService {
	static transactional = true
	def languageService;
	def userService
	
	
	public def getRemoveFromStock(def sparePart, def numberToRemove){
		if(!sparePart || (numberToRemove - sparePart.inStockQuantity < 0)) 
			return false
		else{ 
			sparePart.inStockQuantity = numberToRemove - sparePart.inStockQuantity
			sparePart.save(failOnError:true)
			return true
		}
	}
	public def searchSparePart(String text,User user, SparePartType type,Map<String,String> params) {
		//Remove unnecessary blank space
		text= text.trim()
		def dbFieldTypeNames = 'names_'+languageService.getCurrentLanguagePrefix();
		def dbFieldDescriptions = 'descriptions_'+languageService.getCurrentLanguagePrefix();
		def dataLocations = []
		def criteria = SparePart.createCriteria();

		if(!user.userType.equals(UserType.ADMIN) && !user.userType.equals(UserType.TECHNICIANMMC) && !user.userType.equals(UserType.SYSTEM) && !user.userType.equals(UserType.TECHNICIANDH))
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
			}
		}
	}

	public def getSpareParts(User user, SparePartType type,Map<String,String> params) {
		def dataLocations = []
		def criteria = SparePart.createCriteria();

		if(user.userType.equals(UserType.ADMIN) || user.userType.equals(UserType.TECHNICIANMMC) || user.userType.equals(UserType.SYSTEM) || user.userType.equals(UserType.TECHNICIANDH))
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

		def dataLocations = null
		
		if(!user.userType.equals(UserType.ADMIN) && !user.userType.equals(UserType.TECHNICIANMMC) && !user.userType.equals(UserType.SYSTEM) && !user.userType.equals(UserType.TECHNICIANDH)){
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
	public File exporter(def location,List<SparePart> spareParts){
		if (log.isDebugEnabled()) log.debug("sparePartService.exporter, location code: "+location.code + ", ImportExportConstant: "+ImportExportConstant.CSV_FILE_EXTENSION)
		File csvFile = File.createTempFile(location.code+"_export",ImportExportConstant.CSV_FILE_EXTENSION);
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
					sparePart.type.code,
					sparePart.type?.getNames(new Locale("en")),
					sparePart.type?.getNames(new Locale("fr")),
					sparePart.sparePartPurchasedBy.name(),
					sparePart.dataLocation?.code,
					sparePart.dataLocation?.getNames(new Locale("en")),
					sparePart.dataLocation?.getNames(new Locale("fr")),
					sparePart.type?.manufacturer?.contact?.contactName,
					sparePart.supplier?.code,
					sparePart.supplier?.contact?.contactName,
					sparePart.purchaseDate,
					sparePart.purchaseCost?:"n/a",
					sparePart.currency?:"n/a",
					sparePart.initialQuantity
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
		headers.add(ImportExportConstant.SPARE_PART_TYPE_NAME_EN)
		headers.add(ImportExportConstant.SPARE_PART_TYPE_NAME_FR)
		headers.add(ImportExportConstant.SPARE_PART_PURCHASED_BY)
		headers.add(ImportExportConstant.LOCATION_CODE)
		headers.add(ImportExportConstant.LOCATION_NAME_EN)
		headers.add(ImportExportConstant.LOCATION_NAME_FR)
		headers.add(ImportExportConstant.MANUFACTURER_CONTACT_NAME)
		headers.add(ImportExportConstant.SUPPLIER_CODE)
		headers.add(ImportExportConstant.SUPPLIER_CONTACT_NAME)
		headers.add(ImportExportConstant.SUPPLIER_DATE)
		headers.add(ImportExportConstant.SPARE_PART_PURCHASE_COST)
		headers.add(ImportExportConstant.SPARE_PART_PURCHASE_COST_CURRENCY)
		headers.add(ImportExportConstant.SPARE_PART_QUANTITY)

		return headers;
	}

}
