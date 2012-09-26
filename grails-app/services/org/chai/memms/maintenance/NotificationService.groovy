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

package org.chai.memms.maintenance

import java.util.List;
import java.util.Map;

import org.chai.location.CalculationLocation;
import org.chai.memms.equipment.Equipment;
import org.chai.memms.security.User
import org.chai.memms.security.User.UserType;

class NotificationService {

	def userService
	def newNotification(WorkOrder workOrder, String content,User currentUser){
		List<User> noticationGroup = [currentUser]
		if(currentUser.userType == UserType.DATACLERK){
			noticationGroup += userService.filterByCriterias(UserType.TECHNICIANFACILITY, workOrder.equipment.dataLocation, [:])
		}else if(currentUser.userType == UserType.TECHNICIANFACILITY){
			noticationGroup += userService.filterByCriterias(UserType.TECHNICIANMOH, null, [:])
		}
		
		if(workOrder.notificationGroup != null) workOrder.notificationGroup += (noticationGroup - workOrder.notificationGroup)
		else workOrder.notificationGroup = (noticationGroup - workOrder.notificationGroup)
		workOrder.save(flush:true,failOnError: true)
		sendNotifications(workOrder,content,currentUser)
	}
	/**
	 * Preferable to always call newNotification because it updates the notification group.
	 * Call this when sure that there wont be new users to add to the notification group
	 * @param workOrder
	 * @param content
	 * @param currentUser
	 * @return
	 */
    def sendNotifications(WorkOrder workOrder, String content,User currentUser) {
		workOrder.notificationGroup.findAll{
			if(it != currentUser){
					new Notification(workOrder:workOrder,sender:currentUser,receiver:it,writtenOn:new Date(),content:content,read:false).save(flush:true,failOnError: true)
				}
			}
    }
	
	List<Notification> filterNotifications(WorkOrder workOrder,User sender, User receiver,Boolean read, Map<String, String> params){
		def criteria = Notification.createCriteria();
		return criteria.list(offset:params.offset,max:params.max,sort:params.sort ?:"id",order: params.order ?:"desc"){
			if(workOrder != null) eq("workOrder",workOrder)
			if(sender != null) eq("sender",sender)
			if(receiver != null) eq("receiver",receiver)
			if(read != null) eq("read",read)
		}
	}
	
	Notification readNotification(def notificationID){
		def notification = Notification.get(notificationID)
		if(notification != null){
			notification.read = true
			notification.save(failOnError:true)
		}
		return notification
	}
}
