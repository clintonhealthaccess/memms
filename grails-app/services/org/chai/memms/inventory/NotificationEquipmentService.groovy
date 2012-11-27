package org.chai.memms.inventory

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.chai.location.DataLocation
import org.chai.memms.Notification;
import org.chai.memms.corrective.maintenance.NotificationWorkOrder;
import org.chai.memms.corrective.maintenance.WorkOrder;
import org.chai.memms.security.User;
import org.chai.memms.util.Utils;

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

class NotificationEquipmentService {
	def userService
	public int sendNotifications(DataLocation dataLocation, Department department, String content,User sender,List<User> receivers) {
		if(log.isDebugEnabled()) log.debug("Notification equipment group: "+receivers)
		int numberOfNotificationSent = 0
		receivers.each{ receiver ->
			if(receiver.active){
					def notification = new NotificationEquipment(dataLocation:dataLocation,department:department,sender:sender,receiver:receiver,writtenOn:Utils.now(),content:content,read:false).save(failOnError: true)
					if(notification)
						numberOfNotificationSent++
				}
			}
		return numberOfNotificationSent
	}
	
	public int newNotification(def department,def content, def sender){
		//TODO all users who send notification equipment are assumed to belong to a dataLocation
		//TODO if a user belongs to a department, we can use that to retrieve the department
		def receivers = userService.getNotificationEquipmentGroup(sender.location)
		return sendNotifications(sender.location, department, content, sender, receivers)
	}
	
	public int getUnreadNotifications(User user){
		def criteria = NotificationEquipment.createCriteria()
		return  criteria.get{
			and {
				eq('receiver',user)
				eq('read',false)
				}
			projections{rowCount()}
		}
	}
	//TODO refactor and rename
	public List<NotificationEquipment> searchNotificition(String text,User user,Map<String, String> params) {
		def criteria = NotificationEquipment.createCriteria()
		return  criteria.list(offset:params.offset,max:params.max,sort:params.sort ?:"id",order: params.order ?:"desc"){
			if(user)
				eq('receiver',user)
			if(text)
				ilike("content","%"+text+"%")
		}
	}
	
	public List<NotificationEquipment> filterNotifications(DataLocation dataLocation, Department department,User receiver,Date from, Date to,Boolean read, Map<String, String> params){
		def criteria = NotificationEquipment.createCriteria();
		return criteria.list(offset:params.offset,max:params.max,sort:params.sort ?:"id",order: params.order ?:"desc"){
			if(dataLocation)
				eq("dataLocation",dataLocation)
			if(department)
				eq("department",department)
			if(from)
				ge("writtenOn",Utils.getMinDateFromDateTime(from))
			if(to)
				le("writtenOn",Utils.getMaxDateFromDateTime(to))
			if(receiver)
				eq("receiver",receiver)
			if(read!=null)
				eq("read",read)
		}
	}
	
	public NotificationEquipment setNotificationRead(NotificationEquipment notification){
		notification.read = true
		notification.save(failOnError:true)
		return notification
	}
}
