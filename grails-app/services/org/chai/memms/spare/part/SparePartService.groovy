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
import org.chai.memms.spare.part.SparePartStatus;
import org.chai.memms.security.User;
import org.chai.memms.spare.part.SparePart.SparePartPurchasedBy;
import org.chai.memms.spare.part.SparePartStatus.StatusOfSparePart;
import org.chai.memms.spare.part.SparePartType;
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
	
	public void updateCurrentSparePartStatus(SparePart sparePart,SparePartStatus sparePartStatus,User user, Equipment equipment){
		if(sparePartStatus!=null){
			sparePart.statusOfSparePart = sparePartStatus.statusOfSparePart
			sparePart.addToStatus(sparePartStatus)
		}else{
			sparePart.statusOfSparePart = sparePart.timeBasedStatus.sparePartStatus
		}
		sparePart.lastModified = user
		sparePart.usedOnEquipment=equipment
		if(log.isDebugEnabled()) log.debug("VALUE OF EQUIPMENT FROM THE FORM: " + equipment)
		if(log.isDebugEnabled()) log.debug("Updating SparePart status params: "+sparePart)
		sparePart.save(failOnError:true)
	}

	public def searchSparePart(String text,User user, SparePartType type,StatusOfSparePart status,Map<String,String> params) {
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
			if(status!=null)
				eq("statusOfSparePart",status)
			if(!dataLocations.isEmpty())
				inList('dataLocation',dataLocations)
			or{
				ilike("code","%"+text+"%")
				ilike("serialNumber","%"+text+"%")
				ilike(dbFieldDescriptions,"%"+text+"%")
				ilike(dbFieldTypeNames,"%"+text+"%")
				ilike("t."+dbFieldTypeNames,"%"+text+"%")
				ilike("t."+dbFieldDescriptions,"%"+text+"%")
				ilike("t.partNumber","%"+text+"%")
				ilike("t.code","%"+text+"%")
			}
		}
	}


	public def getSpareParts(User user, SparePartType type,StatusOfSparePart status,Map<String,String> params) {
		def dataLocations = []
		def criteria = SparePart.createCriteria();

		if(user.userType.equals(UserType.ADMIN) || user.userType.equals(UserType.TECHNICIANMMC) || user.userType.equals(UserType.SYSTEM) || user.userType.equals(UserType.TECHNICIANDH))
			return criteria.list(offset:params.offset,max:params.max,sort:params.sort ?:"id",order: params.order ?:"desc"){
				if(type!=null)
					eq("type",type)
				if(status!=null)
					eq("statusOfSparePart",status)
		}
		else{
			if(user.location instanceof Location)
				dataLocations.addAll(user.location.getDataLocations([:], [:]))
			else{
				dataLocations.add((DataLocation)user.location)
				if(userService.canViewManagedSpareParts(user)) dataLocations.addAll((user.location as DataLocation).manages?.asList())
			}

			if(log.isDebugEnabled()) log.debug("Current user: " + user + " Current user's managed dataLocations: " + dataLocations)

			return criteria.list(offset:params.offset,max:params.max,sort:params.sort ?:"id",order: params.order ?:"desc"){  inList('dataLocation',dataLocations)  }

		}

	}
	public def filterSparePart(def location, def supplier, def type,def sparePartPurchasedBy,def sameAsManufacturer,def sparePartStatus, Map<String, String> params){

		def dataLocations = []		
			
		if(location instanceof Location) 
			dataLocations.addAll(location.getDataLocations(null,null))
		else{
			dataLocations.add(location)
			if(log.isDebugEnabled()) log.debug("ADDED LOCATION: " + location)
			//It always takes null value WHY? What logic behind? Will we need to remove this line or not?
			//dataLocations.addAll((location as DataLocation)?.manages?.asList())
		}
		def criteria = SparePart.createCriteria();
		
		return criteria.list(offset:params.offset,max:params.max,sort:params.sort ?:"id",order: params.order ?:"desc"){
			
			if(!dataLocations.isEmpty())
				inList('dataLocation',dataLocations)
			if(supplier != null)
				eq ("supplier", supplier)
			if(type != null)
				eq ("type", type)
			if(sparePartPurchasedBy && !sparePartPurchasedBy.equals(SparePartPurchasedBy.NONE))
				eq ("sparePartPurchasedBy",sparePartPurchasedBy)
			if(sameAsManufacturer)
				eq ("sameAsManufacturer", (sameAsManufacturer.equals('true'))?true:false)
			if(sparePartStatus && !sparePartStatus.equals(StatusOfSparePart.NONE))
				eq ("statusOfSparePart",sparePartStatus)
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
					sparePart.serialNumber,
					sparePart.type.code,
					sparePart.type?.getNames(new Locale("en")),
					sparePart.type?.getNames(new Locale("fr")),

					sparePart.statusOfSparePart,
					sparePart.sparePartPurchasedBy.name(),
					sparePart.dataLocation?.code,
					sparePart.dataLocation?.getNames(new Locale("en")),
					sparePart.dataLocation?.getNames(new Locale("fr")),
					sparePart.type?.manufacturer?.contact?.contactName,
					sparePart.manufactureDate,
					sparePart.supplier?.code,
					sparePart.supplier?.contact?.contactName,
					sparePart.purchaseDate,
					sparePart.purchaseCost?:"n/a",
					sparePart.currency?:"n/a",
					sparePart.sameAsManufacturer,
					sparePart?.warranty?.startDate,
					sparePart?.warrantyPeriod?.numberOfMonths?:""
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

		headers.add(ImportExportConstant.SPARE_PART_SERIAL_NUMBER)
		headers.add(ImportExportConstant.SPARE_PART_TYPE_CODE)
		headers.add(ImportExportConstant.SPARE_PART_TYPE_NAME_EN)
		headers.add(ImportExportConstant.SPARE_PART_TYPE_NAME_FR)
		headers.add(ImportExportConstant.SPARE_PART_STATUS)
		headers.add(ImportExportConstant.SPARE_PART_PURCHASED_BY)
		headers.add(ImportExportConstant.LOCATION_CODE)
		headers.add(ImportExportConstant.LOCATION_NAME_EN)
		headers.add(ImportExportConstant.MANUFACTURER_CONTACT_NAME)
		headers.add(ImportExportConstant.SPARE_PART_MANUFACTURE_DATE)
		headers.add(ImportExportConstant.SUPPLIER_CODE)
		headers.add(ImportExportConstant.SUPPLIER_CONTACT_NAME)
		headers.add(ImportExportConstant.SUPPLIER_DATE)
		headers.add(ImportExportConstant.SPARE_PART_PURCHASE_COST)
		headers.add(ImportExportConstant.SPARE_PART_PURCHASE_COST_CURRENCY)
		headers.add(ImportExportConstant.SPARE_PART_SAME_AS_MANUFACTURER)
		headers.add(ImportExportConstant.SPARE_PART_WARRANTY_START)
		headers.add(ImportExportConstant.SPARE_PART_WARRANTY_PERIOD)

		return headers;
	}

}
