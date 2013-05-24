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
package org.chai.memms.inventory

import grails.gorm.DetachedCriteria
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.chai.memms.inventory.Equipment.Donor;
import org.chai.memms.inventory.Equipment.PurchasedBy;
import org.chai.memms.inventory.EquipmentStatus.Status;
import org.chai.memms.exports.EquipmentExport;
import org.chai.memms.inventory.Equipment;
import org.chai.location.CalculationLocation;
import org.chai.location.DataLocation;
import org.chai.location.DataLocationType;
import org.chai.location.Location;
import org.chai.location.LocationLevel;

import org.chai.memms.util.Utils;
import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode
import org.hibernate.criterion.Order
import org.hibernate.criterion.Projections
import org.hibernate.criterion.Restrictions
import org.apache.commons.lang.StringUtils
import org.chai.memms.preventive.maintenance.PreventiveOrder;
import org.chai.memms.security.User;
import org.chai.memms.util.ImportExportConstant;
import org.chai.memms.util.Utils;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import org.supercsv.io.CsvListWriter;
import org.supercsv.io.ICsvListWriter;
import org.supercsv.prefs.CsvPreference;

/**
 * @author Jean Kahigiso M.
 *
 */
class EquipmentService {
	
	static transactional = true
	def languageService;
	def userService
	
	public void updateCurrentEquipmentStatus(Equipment equipment,EquipmentStatus status,User user){
		if(status!=null){
			equipment.currentStatus = status.status
			equipment.addToStatus(status)
		}else{
			//This assume that there is no equipment without at least one status associated to it
			equipment.currentStatus = equipment.timeBasedStatus.status
		}
		equipment.lastModifiedBy = user
		if(log.isDebugEnabled()) log.debug("Updating Equipment status params: "+equipment)
		equipment.save(failOnError:true)
	}
	public def searchEquipment(String text,User user,DataLocation currentDataLocation,Map<String, String> params) {
		def dataLocations = []
		text = text.trim()
		if(currentDataLocation) dataLocations.add(currentDataLocation)
		else if(user.location instanceof Location) dataLocations.addAll(user.location.getDataLocations([].toSet(), [].toSet()))
		else{
			dataLocations = []
			dataLocations.add(user.location as DataLocation)
			if(userService.canViewManagedEquipments(user)) dataLocations.addAll(((DataLocation)user.location).manages)
		}
		
		def dbFieldTypeNames = 'names_'+languageService.getCurrentLanguagePrefix();
		def dbFieldDescriptions = 'descriptions_'+languageService.getCurrentLanguagePrefix();
		def criteria = Equipment.createCriteria();
		
		return  criteria.list(offset:params.offset,max:params.max,sort:params.sort ?:"id",order: params.order ?:"desc"){
			    createAlias("type","t")
				if(dataLocations)
					inList('dataLocation',dataLocations)
				or{
					ilike("code","%"+text+"%")
					ilike("serialNumber","%"+text+"%")
					ilike("model","%"+text+"%")
					ilike(dbFieldDescriptions,"%"+text+"%") 
					ilike("t."+dbFieldTypeNames,"%"+text+"%")
			    }
		}
	}
		
	public def getMyEquipments(User user,Map<String, String> params) {
		def dataLocations = []
		if(user.location instanceof Location) dataLocations.addAll(user.location.getDataLocations([:], [:]))
		else{
			dataLocations.add((DataLocation)user.location)
			if(userService.canViewManagedEquipments(user)) dataLocations.addAll((user.location as DataLocation).manages?.asList())
		}
		
		if(log.isDebugEnabled()) log.debug("Current user = " + user + " , Current user's managed dataLocations = " + dataLocations)
		
		def criteria = Equipment.createCriteria();

		return criteria.list(offset:params.offset,max:params.max,sort:params.sort ?:"id",order: params.order ?:"desc"){
			inList('dataLocation',dataLocations)
		}
	}
	
	public def getEquipmentsByDataLocationAndManages(DataLocation dataLocation,Map<String, String> params) {
		if(log.isDebugEnabled()) log.debug("getEquipmentsByDataLocationAndManages  dataLocation= "+dataLocation)
		List<DataLocation> dataLocations = [dataLocation]
		(!dataLocation.manages)?:dataLocations.addAll(dataLocation.manages)

		def criteria = Equipment.createCriteria();
		return criteria.list(offset:params.offset,max:params.max,sort:params.sort ?:"id",order: params.order ?:"desc"){
			inList('dataLocation',dataLocations)
		}
	}

	public def getEquipmentsByDataLocation(DataLocation dataLocation,Map<String, String> params) {
		if(log.isDebugEnabled()) log.debug("getEquipmentsByDataLocation  dataLocation= "+dataLocation)
		def criteria = Equipment.createCriteria();
		return criteria.list(offset:params.offset,max:params.max,sort:params.sort ?:"id",order: params.order ?:"desc"){
			eq('dataLocation',dataLocation)
		}
	}

	// fterrier: signature is very long
	public def filterEquipment(def user, def dataLocation, def supplier, def manufacturer,def serviceProvider, def equipmentType, 
		def purchaser,def donor,def obsolete,def status,Map<String, String> params){
		if(log.isDebugEnabled()) log.debug("filterEquipment(user=${user}, dataLocation=${dataLocation}, ...)")
		
		def dataLocations = []
		if(dataLocation) dataLocations.add(dataLocation)
		else if(user.location instanceof Location) dataLocations.addAll(user.location.getDataLocations([:], [:]))
		else{
			dataLocations.add((DataLocation)user.location)
			dataLocations.addAll((user.location as DataLocation).manages)
		}
		
		def criteria = Equipment.createCriteria();
		return criteria.list(offset:params.offset,max:params.max,sort:params.sort ?:"id",order: params.order ?:"desc"){
			if(dataLocations != null)
				inList('dataLocation',dataLocations)
			if(supplier != null)
				eq ("supplier", supplier)
			if(manufacturer != null)
				eq ("manufacturer", manufacturer)
			if(serviceProvider != null)
				eq ("serviceProvider", serviceProvider)
			if(equipmentType != null)
				eq ("type", equipmentType)
			if(purchaser && !purchaser.equals(PurchasedBy.NONE))
				eq ("purchaser",purchaser)
			if(donor && !donor.equals(Donor.NONE))
				eq ("donor",donor)
			if(obsolete)
				eq ("obsolete", (obsolete.equals('true'))?true:false)
			if(status && !status.equals(Status.NONE))
				eq ("currentStatus",status)
		}
	}
	
	public File exporter(DataLocation dataLocation,List<Equipment> equipments){
		if (log.isDebugEnabled()) log.debug("equipmentService.exporter, dataLocation code: "+dataLocation.code + ", ImportExportConstant: "+ImportExportConstant.CSV_FILE_EXTENSION)
		File csvFile = File.createTempFile(dataLocation.code+"_export",ImportExportConstant.CSV_FILE_EXTENSION);
		FileWriter csvFileWriter = new FileWriter(csvFile);
		ICsvListWriter writer = new CsvListWriter(csvFileWriter, CsvPreference.EXCEL_PREFERENCE);
		this.writeFile(writer,equipments);
		return csvFile;
	}
	
	private void writeFile(ICsvListWriter writer,List<Equipment> equipments) throws IOException {
		try{
			String[] csvHeaders = null;
			// headers
			if(csvHeaders == null){
				csvHeaders = this.getExportDataHeaders()
				writer.writeHeader(csvHeaders);
			}
			for(Equipment equipment: equipments){
				List<String> line = [
					equipment.serialNumber,equipment.type.code,equipment.type?.getNames(new Locale("en")),
					equipment.type?.getNames(new Locale("fr")),equipment.model,equipment.currentStatus,
					equipment.dataLocation?.code,equipment.dataLocation?.getNames(new Locale("en")),equipment.dataLocation?.getNames(new Locale("fr")),
					equipment.department?.code,equipment.department?.getNames(new Locale("en")),equipment.department?.getNames(new Locale("fr")),
					equipment.room,equipment.manufacturer?.code,equipment.manufacturer?.contact?.contactName,
					equipment.manufactureDate,equipment.supplier?.code,equipment.supplier?.contact?.contactName,equipment.purchaseDate,
					equipment?.serviceProvider?.code?:"",equipment?.serviceProvider?.contact?.contactName?:"",equipment?.serviceContractStartDate?:"",
					equipment?.serviceContractPeriod?.numberOfMonths?:"",equipment.purchaseCost?:"n/a",equipment.currency?:"n/a",
					equipment.purchaser.name(),equipment.obsolete,equipment?.warranty?.startDate,equipment?.warrantyPeriod?.numberOfMonths?:""
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
		basicInfo.add("equipment.export")
		return basicInfo;
	}
	

	public List<String> getExportDataHeaders() {
		List<String> headers = new ArrayList<String>();
		
		headers.add(ImportExportConstant.EQUIPMENT_SERIAL_NUMBER)
		headers.add(ImportExportConstant.DEVICE_CODE)
		headers.add(ImportExportConstant.DEVICE_NAME_EN)
		headers.add(ImportExportConstant.DEVICE_NAME_FR)
		headers.add(ImportExportConstant.EQUIPMENT_MODEL)
		headers.add(ImportExportConstant.EQUIPMENT_STATUS)
		headers.add(ImportExportConstant.LOCATION_CODE)
		headers.add(ImportExportConstant.LOCATION_NAME_EN)
		headers.add(ImportExportConstant.LOCATION_NAME_FR)
		headers.add(ImportExportConstant.DEPARTMENT_CODE)
		headers.add(ImportExportConstant.DEPARTMENT_NAME_EN)
		headers.add(ImportExportConstant.DEPARTMENT_NAME_FR)
		headers.add(ImportExportConstant.ROOM)
		headers.add(ImportExportConstant.MANUFACTURER_CODE)
		headers.add(ImportExportConstant.MANUFACTURER_CONTACT_NAME)
		headers.add(ImportExportConstant.EQUIPMENT_MANUFACTURE_DATE)
		headers.add(ImportExportConstant.SUPPLIER_CODE)
		headers.add(ImportExportConstant.SUPPLIER_CONTACT_NAME)
		headers.add(ImportExportConstant.SUPPLIER_DATE)
		headers.add(ImportExportConstant.SERVICEPROVIDER_CODE)
		headers.add(ImportExportConstant.SERVICEPROVIDER_CONTACT_NAME)
		headers.add(ImportExportConstant.SERVICEPROVIDER_DATE)
		headers.add(ImportExportConstant.SERVICEPROVIDER_PERIOD)
		headers.add(ImportExportConstant.EQUIPMENT_PURCHASE_COST)
		headers.add(ImportExportConstant.EQUIPMENT_PURCHASE_COST_CURRENCY)
		headers.add(ImportExportConstant.EQUIPMENT_DONOR)
		headers.add(ImportExportConstant.EQUIPMENT_OBSOLETE)
		headers.add(ImportExportConstant.EQUIPMENT_WARRANTY_START)
		headers.add(ImportExportConstant.EQUIPMENT_WARRANTY_END)
		
		return headers;
	}


}
