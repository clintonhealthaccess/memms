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

import java.util.Date;
import java.util.Map;
import org.chai.location.DataLocation;
import org.chai.memms.AbstractEntityController;
import org.chai.memms.Notification;
import org.chai.memms.corrective.maintenance.NotificationWorkOrder;
import org.chai.memms.corrective.maintenance.WorkOrder;
import org.chai.memms.inventory.Equipment;
import org.chai.memms.corrective.maintenance.WorkOrder.Criticality;
import org.chai.memms.security.User;

/**
 * @author Jean Kahigiso M.
 *
 */
class NotificationWorkOrderController extends AbstractEntityController{
	
	def notificationWorkOrderService
	
	def getEntity(def id) {
		return NotificationWorkOrder.get(id);
	}

	def createEntity() {
		return new NotificationWorkOrder();
	}

	def getTemplate() {
		return "/entity/notification/createNotificationWorkOrder";
	}

	def getLabel() {
		return "notification.work.order.label";
	}
	
	def deleteEntity(def entity) {
		super.deleteEntity(entity)
	}
	
	def getEntityClass() {
		return NotificationWorkOrder.class;
	}
	def bindParams(def entity) {
		entity.properties = params
	}
	
	def getModel(def entity) {
		[
			notification:entity
		]
	}
	
	def read={
		adaptParamsForList()
		if(!params.id) redirect(uri: getTargetURI())
		else{
			
			def notification = notificationWorkOrderService.setNotificationRead(Notification.get(params.int('id')))
			if (log.isInfoEnabled()) log.info("reading notification: "+notification)

			if (notification == null) {
				flash.message = message(code: 'default.not.found.message', args: [message(code: getLabel(), default: 'notificationWorkOrder'), params.id])
				redirect(uri: getTargetURI())
			}
			else {
				render(view: '/entity/notification/readNotificationWorkOrder', model:[notification:notification, entityName:getLabel()])
			}
		}
	}
	
	def list={
		adaptParamsForList()
		Boolean read = (params.read) ? params.boolean("read") : null;
		WorkOrder workOrder = WorkOrder.get(params.id)
		def notifications = notificationWorkOrderService.filterNotifications(workOrder, user, null,null,read, params)
		
		if(request.xhr)
			this.ajaxModel(notifications,workOrder,"")
		else{
			render(view:"/entity/list", model:[
				template:"notification/notificationWorkOrderList",
				filterTemplate:"notification/notificationWorkOrderFilter",
				listTop:"notification/notificationWorkOrderListTop",
				entities: notifications,
				entityCount: notifications.totalCount,
				workOrder:workOrder,
				code: getLabel(),
				])
		}
	}
	
	def search = {
		adaptParamsForList()
		Boolean read = (params.read) ? params.boolean("read") : null;
		WorkOrder workOrder = WorkOrder.get(params.workOrder)
		def notifications = notificationWorkOrderService.searchNotificition(params['q'],user,workOrder, read,params)
		
		if(!request.xhr)
			response.sendError(404)
		this.ajaxModel(notifications,workOrder,params['q'])
	}
	
	def ajaxModel(def entities,def workOrder,def searchTerm) {
		def model = [entities: entities,entityCount: entities.totalCount,workOrder:workOrder,q: searchTerm,entityClass: getEntityClass()]
		def listHtml = g.render(template:"/entity/notification/notificationWorkOrderList",model:model)
		render(contentType:"text/json") { results = [listHtml] }
	}

	def getAjaxData = {
		//TODO will be used fetch new notifications using ajax and update page
	}
	
	def save = {NotificationWorkOrderCommand cmd ->
		if (log.isDebugEnabled()) log.debug ('creating notificationsWorkOrder with params:'+params+", Cmd:"+cmd)
		
		adaptParamsForList()
					
		if (!cmd.validate()) {
			if (log.isInfoEnabled()) log.info ("validation error in ${cmd}: ${cmd.errors}}")
			
			def model = [template: getTemplate()]
			model << [targetURI: getTargetURI()]
			model << [workOrder: cmd.workOrder]
			model << [cmd: cmd]
			render(view: '/entity/edit', model: model)
		}
		else {
			def sent = notificationWorkOrderService.newNotification(cmd.workOrder,cmd.content, user,false)
			flash.message = message(code: 'default.saved.message', args: [message(code: getLabel(), default: 'entity')],sent.toString())
			redirect(action: "list", id: cmd.workOrder.id)
		}
	}
	
	def create = {
		WorkOrder workOrder = WorkOrder.get(params.workOrder)
		
		if(workOrder == null){
			flash.message = message(code: 'workOrder.notspecified.message', args: [message(code: getLabel(), default: 'entity'), params.workOrder])
			redirect(url: getTargetURI())
		}else{

			def model = [template: getTemplate()]
			model << [targetURI: getTargetURI()]
			model << [workOrder: workOrder]
			
			render(view: '/entity/edit', model: model)
		}
	}
	
	def filter = {NotificationWorkOrderFilterCommand cmd ->
		adaptParamsForList()
		List<NotificationWorkOrder> notifications = notificationWorkOrderService.filterNotifications(cmd.workOrder, user, cmd.from,cmd.to,cmd.getReadStatus(), params)
		if(!request.xhr)
			response.sendError(404)
		this.ajaxModel(notifications,cmd.workOrder,"")
	}
}

class NotificationWorkOrderCommand{
	WorkOrder workOrder
	String content
	
	static constraints = {
		workOrder nullable:false
		content nullable:false, blank:false
	}
	
	String toString() {
		return "NotificationWorkOrderCommand[WorkOrder="+workOrder+", Content="+content+"]"
	}
}

class NotificationWorkOrderFilterCommand {
	Date from
	Date to
	String read
	WorkOrder workOrder

	public boolean getReadStatus(){
		if(read.equals("true")) return true
		else if(read.equals("false")) return false
		else return null
	}

	static constraints = {
		from  nullable:true
		to nullable:true
		read nullable:true
		workOrder nullable:true
	}

	String toString() {
		return "NotificationWorkOrderFilterCommand[From="+from+", To="+to+", Read="+read+", WorkOrder="+workOrder+", Read Status="+ getReadStatus() + "]"
	}
}

