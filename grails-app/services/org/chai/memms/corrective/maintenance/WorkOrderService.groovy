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
import org.chai.memms.util.Utils;
import org.chai.memms.corrective.maintenance.NotificationWorkOrderService;


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
				equipment{ eq('dataLocation',dataLocation)}
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
		def currentStatus = workOrder.getTimeBasedStatus().status

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
}
