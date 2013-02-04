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

import java.util.List;
import java.util.Map;

import org.chai.location.CalculationLocation;
import org.chai.location.DataLocation;
import org.chai.memms.Notification;
import org.chai.memms.corrective.maintenance.NotificationWorkOrder;
import org.chai.memms.corrective.maintenance.WorkOrder;
import org.chai.memms.inventory.Equipment;
import org.chai.memms.security.User
import org.chai.memms.security.User.UserType;
import org.chai.memms.util.Utils;

class NotificationWorkOrderService {
	def userService
	
    public int sendNotifications(WorkOrder workOrder, String content,User sender,List<User> receivers) {
		if(log.isDebugEnabled()) log.debug("Notification workOrder receivers group: "+receivers+" , for workorder " +  workOrder)
		int numberOfNotificationSent = 0
		receivers.each{ receiver ->
			if(receiver.active){
					def notification = new NotificationWorkOrder(workOrder:workOrder,sender:sender,receiver:receiver,content:content,read:false).save(failOnError: true)
					if(notification)
						numberOfNotificationSent++
				}
			}
		return numberOfNotificationSent
    }
	
	public int newNotification(def workOrder,def content, def sender,boolean escalate){
		def receivers = userService.getNotificationWorkOrderGroup(workOrder,sender,escalate)
		return sendNotifications(workOrder,content,sender,receivers)
	}
	
	public int getUnreadNotifications(User user){
		def criteria = NotificationWorkOrder.createCriteria()
		return  criteria.get{
			and { 
				eq('receiver',user)
				eq('read',false)
				}
			projections{rowCount()}
		}
	}
	public def searchNotificition(String text,User user,WorkOrder workOrder, Boolean read,Map<String, String> params) {
		def criteria = NotificationWorkOrder.createCriteria()
		return  criteria.list(offset:params.offset,max:params.max,sort:params.sort ?:"id",order: params.order ?:"desc"){
			if(user)
				eq('receiver',user)
			if(workOrder)  
				eq('workOrder',workOrder)
			if(read)  
				eq('read',read)
			if(text) 
				ilike("content","%"+text+"%")
		}
	}
	
	public def filterNotifications(WorkOrder workOrder,User receiver,Date from, Date to,Boolean read, Map<String, String> params){
		def criteria = NotificationWorkOrder.createCriteria();
		return criteria.list(offset:params.offset,max:params.max,sort:params.sort ?:"id",order: params.order ?:"desc"){
			if(workOrder) 
				eq("workOrder",workOrder)
			if(from) 
				ge("dateCreated",Utils.getMinDateFromDateTime(from))
			if(to) 
				le("dateCreated",Utils.getMaxDateFromDateTime(to))
			if(receiver) 
				eq("receiver",receiver)
			if(read!=null) 
				eq("read",read)
		}
	}
	
	public def setNotificationRead(NotificationWorkOrder notification){
		notification.read = true
		notification.save(failOnError:true)
		return notification
	}
}
