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
package org.chai.memms.corrective.maintenance

import org.chai.memms.corrective.maintenance.WorkOrder.Criticality

import java.util.Map;
import java.util.Set;

import org.chai.location.CalculationLocation;
import org.chai.location.DataLocation;
import org.chai.location.DataLocationType;
import org.chai.location.Location;
import org.chai.location.LocationLevel;
import org.chai.memms.corrective.maintenance.WorkOrder;
import org.chai.memms.corrective.maintenance.WorkOrderStatus;
import org.chai.memms.corrective.maintenance.WorkOrderStatus.OrderStatus;
import org.chai.memms.corrective.maintenance.WorkOrderStatus.WorkOrderStatusChange;
import org.chai.memms.security.User;
import org.chai.memms.security.User.UserType;
import org.chai.memms.util.Utils;
import org.chai.memms.corrective.maintenance.NotificationWorkOrderService;
import org.supercsv.io.CsvListWriter
import org.supercsv.io.ICsvListWriter
import org.supercsv.prefs.CsvPreference
import org.chai.memms.util.ImportExportConstant


/**
 * @author Jean Kahigiso M.
 *
 */
class WorkOrderService {
	static transactional = true
	def notificationWorkOrderService

	/**
	 * Returns a filtered list of WorkOrders according to the passed criteria
	 * Pass a null value for the criteria you want to be ignored in the filter
	 * NB workOrdersEquipment is named like this to avoid conflicting with the navigation property equipment
	 * @param dataLocation
	 * @param equip //Named so to avoid ambiguity in criteria
	 * @param openOn
	 * @param closedOn
	 * @param assistaceRequested
	 * @param open
	 * @param criticality
	 * @param currentStatus
	 * @param params
	 * @return
	 */
	def filterWorkOrders(def dataLocation,def equip,def openOn,def closedOn,def criticality,def currentStatus,def params) {
		def criteria = WorkOrder.createCriteria();
		return criteria.list(offset:params.offset,max:params.max,sort:params.sort ?:"id",order: params.order ?:"desc"){
			if(dataLocation)
				eq('dataLocation',dataLocation)
			if(equip)
				eq("equipment",equip)
			if(openOn)
				between("openOn",Utils.getMinDateFromDateTime(openOn),Utils.getMaxDateFromDateTime(openOn))
			if(closedOn)
				between("closedOn",Utils.getMinDateFromDateTime(closedOn),Utils.getMaxDateFromDateTime(closedOn))
			if(criticality && criticality != Criticality.NONE)
				eq("criticality",criticality)
			if(currentStatus && currentStatus != OrderStatus.NONE)
				eq("currentStatus",currentStatus)
		}
	}
	def getWorkOrdersEscalatedToMMC(def user,Map<String, String> params) {
		
			def criteria = WorkOrder.createCriteria();
			def dataLocations = []
			if(user.location instanceof Location) dataLocations.addAll(user.location.collectDataLocations(null))
			else{
				dataLocations = []
				dataLocations.add(user.location as DataLocation)
			}
			return criteria.list(offset:params.offset,max:params.max,sort:params.sort ?:"id",order: params.order ?:"desc"){
				createAlias("equipment","equip")
				if(dataLocations)
					inList('equip.dataLocation',dataLocations)
				eq ("currentStatus",OrderStatus.OPENATMMC)
			}
		}
	
	def getWorkOrderOnHomePage (def user){
		def criteria = WorkOrder.createCriteria();
		def dataLocations = []
		if(user.location instanceof Location) dataLocations.addAll(user.location.collectDataLocations(null))
		else{
			dataLocations = []
			dataLocations.add(user.location as DataLocation)
			if(user.location!=null)
			dataLocations.addAll((user.location as DataLocation)?.manages)
		}
		return criteria.list(max:20,sort:"lastUpdated",order:"desc"){
			createAlias("equipment","equip")
			if(dataLocations)
				inList('equip.dataLocation',dataLocations)
			if(user.userType.equals(UserType.TECHNICIANMMC))
				eq ("currentStatus",OrderStatus.OPENATMMC) 
			else eq ("currentStatus",OrderStatus.OPENATFOSA)
		}
	}

	def escalateWorkOrder(def workOrder,def content,def escalatedBy){
		workOrder.currentStatus = OrderStatus.OPENATMMC
		WorkOrderStatus status = new WorkOrderStatus(workOrder:workOrder,status:OrderStatus.OPENATMMC,escalation:true,changedBy:escalatedBy)
		workOrder.addToStatus(status)
		workOrder.save(failOnError:true)
		if(status)
			notificationWorkOrderService.newNotification(workOrder,content,escalatedBy,true)
		return workOrder
	}

	public def getWorkOrderTimeBasedStatusChange(WorkOrder workOrder, List<WorkOrderStatusChange> workOrderStatusChanges){
		WorkOrderStatusChange workOrderStatusChange = null

		def previousStatus = workOrder.getTimeBasedPreviousStatus()?.status
		def currentStatus = workOrder.getTimeBasedStatus()?.status

		if(workOrderStatusChanges == null) workOrderStatusChanges = WorkOrderStatusChange.values()
		 workOrderStatusChanges.each{ statusChange ->
			 
			def previousStatusMap = statusChange.getStatusChange()['previous']
			def currentStatusMap = statusChange.getStatusChange()['current']

			def previousStatusChange = previousStatusMap.contains(previousStatus) || (previousStatusMap.contains(OrderStatus.NONE) && previousStatus == null)
			def currentStatusChange = currentStatusMap.contains(currentStatus)

			if(previousStatusChange && currentStatusChange) workOrderStatusChange = statusChange
		 }
		 return workOrderStatusChange
	}
	
	public File exporter(DataLocation dataLocation,List<WorkOrder> workOrders){
		if (log.isDebugEnabled()) log.debug("workOrderService.exporter, dataLocation code: "+dataLocation.code + ", ImportExportConstant: "+ImportExportConstant.CSV_FILE_EXTENSION)
		
		File csvFile = File.createTempFile(dataLocation.code+"_"+dataLocation.getNames(new Locale("en")).replaceAll(" ", "_")+"_work_order_export",ImportExportConstant.CSV_FILE_EXTENSION);
		FileWriter csvFileWriter = new FileWriter(csvFile);
		ICsvListWriter writer = new CsvListWriter(csvFileWriter, CsvPreference.EXCEL_PREFERENCE);
		this.writeFile(writer,workOrders);
		return csvFile;
	}
	
	private void writeFile(ICsvListWriter writer,List<WorkOrder> workOrders) throws IOException {
		try{
			String[] csvHeaders = null;
			// headers
			if(csvHeaders == null){
				csvHeaders = this.getExportDataHeaders()
				writer.writeHeader(csvHeaders);
			}
			for(WorkOrder workOrder: workOrders){
				List<String> line = [
					workOrder.id,workOrder.equipment.code?:"",workOrder.equipment.serialNumber?:"",workOrder.equipment.oldTagNumber?:"",workOrder.equipment.type?.code?:"",workOrder.equipment.type?.getNames(new Locale("en"))?:"",
					workOrder.equipment.type?.getNames(new Locale("fr"))?:"",workOrder.equipment.model?:"",workOrder.equipment.currentStatus?:"",
					workOrder.equipment.dataLocation?.code,workOrder.equipment.dataLocation?.getNames(new Locale("en"))?:"",workOrder.equipment.dataLocation?.getNames(new Locale("fr"))?:"",
					workOrder.equipment.department?.code?:"",workOrder.equipment.department?.getNames(new Locale("en"))?:"",workOrder.equipment.department?.getNames(new Locale("fr"))?:"",
					workOrder.equipment.room?:"",workOrder.equipment.manufacturer?.code?:"",workOrder.equipment.manufacturer?.contact?.contactName?:"",
					workOrder.equipment.manufactureDate?:"",workOrder.equipment.supplier?.code?:"",workOrder.equipment.supplier?.contact?.contactName?:"",workOrder.equipment.purchaseDate?:"",
					workOrder.equipment.serviceProvider?.code?:"",workOrder.equipment.serviceProvider?.contact?.contactName?:"",workOrder.equipment.serviceContractStartDate?:"",
					workOrder.equipment.serviceContractPeriod?.numberOfMonths?:"",workOrder.equipment.purchaseCost?:"n/a",workOrder.equipment.currency?:"n/a",
					workOrder.equipment.purchaser?.name?:"",workOrder.equipment.obsolete?:"",workOrder.equipment.warranty?.startDate?:"",workOrder.equipment.warrantyPeriod?.numberOfMonths?:""
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
		basicInfo.add("workOrder.export")
		return basicInfo;
	}
	

	public List<String> getExportDataHeaders() {
		List<String> headers = new ArrayList<String>();
		
		headers.add(ImportExportConstant.EQUIPMENT_ID)
		headers.add(ImportExportConstant.EQUIPMENT_CODE)
		headers.add(ImportExportConstant.EQUIPMENT_SERIAL_NUMBER)
		headers.add(ImportExportConstant.EQUIPMENT_OLD_TAG_NUMBER)
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
	
	def exportWorkOrders(dataLocation) {
		def criteria = WorkOrder.createCriteria();
		return criteria.list(offset:params.offset,max:params.max,sort:params.sort ?:"id",order: params.order ?:"desc"){
			if(dataLocation)
				eq('dataLocation',dataLocation)
		}
	}
}
