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

import groovy.transform.EqualsAndHashCode;

import org.chai.memms.security.User;

/**
 * @author Jean Kahigiso M.
 *
 */
public class WorkOrderStatus {
	
	enum OrderStatus{
		NONE("none"),
		OPENATFOSA("open.at.fosa"),
		OPENATMMC("open.at.mmc"),
		CLOSEDFIXED("closed.fixed"),
		CLOSEDFORDISPOSAL("closed.for.disposal")
		String messageCode = "work.order.status"
		String name
		OrderStatus(String name){ this.name=name }
		String getKey() { return name() }
	}

	enum WorkOrderStatusChange{
		NEWORDER("newOrder",
			['previous':[OrderStatus.NONE], 'current':[OrderStatus.OPENATFOSA,OrderStatus.OPENATMMC]]),
		ORDERESCALATEDTOMMC("orderEscalatedToMmc",
			['previous':[OrderStatus.OPENATFOSA], 'current':[OrderStatus.OPENATMMC]]),
		EQUIPMENTRECEIVEDFROMMMCFIXED("equipmentReceivedFromMmcFixed", 
			['previous':[OrderStatus.OPENATMMC], 'current':[OrderStatus.CLOSEDFIXED]]),
		EQUIPMENTRECEIVEDFROMMMCNOTFIXED("equipmentReceivedFromMmcNotFixed",
			['previous':[OrderStatus.OPENATMMC], 'current':[OrderStatus.OPENATFOSA]]),
		CLOSEDORDERFIXED("closedOrderFixed",
			['previous':[OrderStatus.OPENATFOSA,OrderStatus.OPENATMMC], 'current':[OrderStatus.CLOSEDFIXED]]),
		CLOSEDORDERNOTFIXED("closedOrderNotFixed",
			['previous':[OrderStatus.OPENATFOSA,OrderStatus.OPENATMMC], 'current':[OrderStatus.CLOSEDFORDISPOSAL]])

		String messageCode = "reports.corrective.statusChanges"
		final String name
		final Map<String,List<OrderStatus>> statusChange
		WorkOrderStatusChange(String name, Map<String,List<OrderStatus>> statusChange) {
		   this.name=name
		   this.statusChange=statusChange
		 }
		 String getKey() { return name }
		 Map<String,List<OrderStatus>> getStatusChange() { return statusChange }
	}
	
	Date dateCreated
	User changedBy
	OrderStatus status
	Boolean escalation = false
	
	static belongsTo = [workOrder: WorkOrder]
	static mapping = {
		table "memms_work_order_status"
		version false
	}
	
	static constraints = {
		status nullable: false, inList:[OrderStatus.OPENATFOSA,OrderStatus.OPENATMMC,OrderStatus.CLOSEDFIXED,OrderStatus.CLOSEDFORDISPOSAL]
		escalation validator:{ val, obj ->
			def valid = true
			if(val == true && (obj.status != OrderStatus.OPENATMMC)) valid = false
			if(val == false && (obj.status == OrderStatus.OPENATMMC)) valid = false
			return valid
		}
	}
	
	@Override
	public String toString() {
		return "WorkOrderStatus [id= " + id + " status= "+status+"]";
	}
}
