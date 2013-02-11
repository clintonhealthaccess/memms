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
import java.util.Date;
import java.util.Map;
import org.chai.location.DataLocation;
import org.chai.memms.AbstractEntityController;
import org.chai.memms.Notification;
import org.chai.memms.corrective.maintenance.WorkOrder;
import org.chai.memms.inventory.Equipment;
import org.chai.memms.corrective.maintenance.WorkOrder.Criticality;
import org.chai.memms.security.User;

/**
 * @author Jean Kahigiso M.
 *
 */
class NotificationEquipmentController extends AbstractEntityController{
	
	def notificationEquipmentService
	
	def getEntity(def id) {
		return NotificationEquipment.get(id)
	}

	def createEntity() {
		return new NotificationEquipment();
	}

	def getTemplate() {
		return "/entity/notification/createNotificationEquipment";
	}

	def getLabel() {
		return "notification.equipment.label";
	}
	
	def getEntityClass() {
		return NotificationEquipment.class;
	}
	def bindParams(def entity) {
		entity.properties = params
	}
	
	def getModel(def entity) {
		[
			notification:entity
		]
	}
	
	def read = {
		adaptParamsForList()
		if(!params.id) redirect(uri: getTargetURI())
		else{
			def notification = notificationEquipmentService.setNotificationRead(NotificationEquipment.get(params.int('id')))
			if (log.isDebugEnabled()) log.debug("reading notificationEquipment: "+notification)

			if (notification == null) {
				flash.message = message(code: 'default.not.found.message', args: [message(code: getLabel(), default: 'notificationEquipment'), params.id])
				redirect(uri: getTargetURI())
			}
			else {
				render(view: '/entity/notification/readNotificationEquipment', model:[notification:notification, entityName:getLabel()])
			}
		}
	}
	
	def list = {
		adaptParamsForList()
		Boolean read = (params.read) ? params.boolean("read") : null;
		def notifications = notificationEquipmentService.filterNotifications(null,null, user, null,null,read, params)
		if(request.xhr)
			this.ajaxModel(notifications,"")
		else{
			render(view:"/entity/list", model: model(notifications) << [
				template:"notification/notificationEquipmentList",
				filterTemplate:"notification/notificationEquipmentFilter",
				listTop:"notification/notificationEquipmentListTop"
			])
		}
	}
	
	def search = {
		adaptParamsForList()
		Boolean read = (params.read) ? params.boolean("read") : null;
		def notifications = notificationEquipmentService.searchNotificition(params['q'],user,params)
		if(request.xhr)
			this.ajaxModel(notifications,params['q'])
		else {
			render(view:"/entity/list", model: model(notifications) << [
				template:"notification/notificationEquipmentList",
				filterTemplate:"notification/notificationEquipmentFilter",
				listTop:"notification/notificationEquipmentListTop"
			])
		}
	}
	
	def model(def entities) {
		return [
			entities: entities,
			entityCount: entities.totalCount,
			entityClass: getEntityClass(),
			code: getLabel(),
		]
	}
	
	
	def ajaxModel(def entities,def searchTerm) {
		def model = model(entities) << [q: searchTerm]
		def listHtml = g.render(template:"/entity/notification/notificationEquipmentList",model:model)
		render(contentType:"text/json") { results = [listHtml] }
	}

	def getAjaxData = {
		//TODO will be used fetch new notifications using ajax and update page
	}
	
	def save = {
		if (log.isDebugEnabled()) log.debug ('creating notificationsEquipment with params:'+params)

		adaptParamsForList()
		//TODO find a way to retrieve a department instead of passing in dull
		def sent = notificationEquipmentService.newNotification(null,params.content, user)
		flash.message = message(code: 'default.saved.message', args: [message(code: getLabel(), default: 'entity')],sent.toString())
		redirect(action: "list")
	}
	
	def create = {
		def model = [template: getTemplate()]
		model << [targetURI: getTargetURI()]
		render(view: '/entity/edit', model: model)
	}
	
	def filter = {NotificationEquipmentFilterCommand cmd ->
		adaptParamsForList()
		List<NotificationEquipment> notifications = notificationEquipmentService.filterNotifications(cmd.dataLocation,cmd.department, user, cmd.from,cmd.to,cmd.getReadStatus(), params)
		if(request.xhr)
			this.ajaxModel(notifications,"")
		else {
			render(view:"/entity/list", model: model(notifications) << [
				template:"notification/notificationEquipmentList",
				filterTemplate:"notification/notificationEquipmentFilter",
				listTop:"notification/notificationEquipmentListTop"
			])	
		}
	}
}


class NotificationEquipmentFilterCommand {
	Date from
	Date to
	String read
	DataLocation dataLocation
	Department department

	public boolean getReadStatus(){
		if(read.equals("true")) return true
		else if(read.equals("false")) return false
		else return null
	}

	static constraints = {
		from  nullable:true
		to nullable:true
		read nullable:true
		dataLocation nullable:true
		department nullable:true
	}

	String toString() {
		return "NotificationEquipmentFilterCommand[From="+from+", To="+to+", Read="+read+", DataLocation="+dataLocation+", Department="+department+", Read Status="+ getReadStatus() + "]"
	}
}

