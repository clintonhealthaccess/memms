package org.chai.memms.inventory


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

import java.util.Map;
import java.util.Set;
import org.chai.location.CalculationLocation;
import org.chai.location.DataLocationType;
import org.chai.memms.AbstractEntityController;
import org.chai.task.EquipmentTypeExportFilter;
import org.chai.memms.inventory.EquipmentStatus.Status;
import org.chai.memms.inventory.EquipmentType;
import org.chai.memms.inventory.EquipmentType.Observation;
import org.chai.memms.spare.part.SparePartType
import org.chai.memms.inventory.PreventiveAction 
import org.chai.memms.corrective.maintenance.PreventionService

/**
 * @author Eugene Munyaneza
 *
 */
class EquipmentTypeController extends AbstractEntityController{
	def equipmentTypeService
	def preventionService

	def getEntity(def id) {
		return EquipmentType.get(id);
	}

	def createEntity() {
		return new EquipmentType();
	}

	def getTemplate() {
		return "/entity/equipmentType/createEquipmentType";
	}

	def getLabel() {
		return "equipment.type.label";
	}

	def getEntityClass() {
		return EquipmentType.class;
	}
	def deleteEntity(def entity) {
		if (entity.equipments.size() != 0 || entity.sparePartTypes.size() != 0)
			flash.message = message(code: 'equipment.type.hasequipment', args: [message(code: getLabel(), default: 'entity'), params.id], default: '{0} still has associated equipment or spare part type.')
		else{
			equipmentTypeService.removeProcessFromPrevention(entity)
			super.deleteEntity(entity);
		}
	}

	def bindParams(def entity) {
		entity.properties = params
	}

	def getExportClass() {
		return "EquipmentTypeExportTask"
	}

	def getModel(def entity) {
		[
			type: entity
		]
	}
			
	def list = {
		adaptParamsForList()
		def types = EquipmentType.list(offset:params.offset,max:params.max,sort:params.sort ?:"id",order: params.order ?:"desc");
		if(request.xhr)
			this.ajaxModel(types,"")
		else{
			render(view:"/entity/list",model:model(types) << [
				template:"equipmentType/equipmentTypeList",
				listTop:"equipmentType/listTop",
				importTask:'EquipmentTypeImportTask',
				exportTask:'EquipmentTypeExportTask',
			])
		}
	}
	
	def search = {
		adaptParamsForList()
		def types  = equipmentTypeService.searchEquipmentType(params['q'],null,params)

		if(request.xhr)
			this.ajaxModel(types,params['q'])	
		else {
			render(view:"/entity/list",model:model(types) << [
				template:"equipmentType/equipmentTypeList",
				listTop:"equipmentType/listTop",
				importTask:'EquipmentTypeImportTask',
				exportTask:'EquipmentTypeExportTask'
			])
		}
	}	
		
	
	def model(def entities) {
		return [
			entities: entities,
			entityCount: entities.totalCount,
			entityClass:getEntityClass(),
			code: getLabel()
		]
	}
	
	def ajaxModel(def entities,def searchTerm) {
		def model = model(entities) << [q:searchTerm]
		def listHtml = g.render(template:"/entity/equipmentType/equipmentTypeList",model:model)
		render(contentType:"text/json") { results = [listHtml] }
	}

	
	def getAjaxData = {
		def types = equipmentTypeService.searchEquipmentType(params['term'],Observation."$params.observation",['hmis':params['hmis']])
		render(contentType:"text/json") {
			elements = array {
				types.each { type ->				
					elem (
							key: type.id,
							value: type.getNames(languageService.getCurrentLanguage()) + ' ['+type.code+']'
							)
				}
			}
			htmls = array {
				types.each { type ->
					elem (
							key: type.id,
							html: g.render(template:"/templates/typeFormSide",model:[type:type,label:label,cssClass:"form-aside-hidden",field:'type'])
							)
				}
			}
		}
	}	
	
		
	def export = {
		def equipmentTypeExportTask = new EquipmentTypeExportFilter().save(failOnError: true,flush: true)
		params.exportFilterId = equipmentTypeExportTask.id
		params.class = "EquipmentTypeExportTask"
		redirect(controller: "task", action: "create", params: params)
	}

	def addAction = {
		def type = EquipmentType.get(params.int("type.id"))
		def value = params["value"]
		def result = false
		def html =""
		if (type == null || value.equals(""))
			response.sendError(404)
		else {
				if (log.isDebugEnabled()) log.debug("addPreventiveAction params: "+params)
				def action  = new PreventiveAction(description: value)
				type.addToPreventiveActions(action)
				//This has to be flushed to get newly created action on the ux
				type.save(failOnError: true, flush: true)
				if(type!=null){
					result=true
					html = g.render(template:"/templates/preventiveActionList",model:[type:type])
				}
				render(contentType:"text/json") { results = [result,html,"action"] }
		}
	}

	def removeAction = {
		def  action = PreventiveAction.get(params.int("action.id"))
		def result = false
		def html =""
		if(!action) 
			response.sendError(404)
		else{
			EquipmentType type = action.equipmentType
			//Remove action from prevention action
			preventionService.removeFromPrevention(action)
			type.removeFromPreventiveActions(action)
			action.delete()
			type.save(failOnError:true)
			if(type){ 
				result = true
				html = g.render(template:"/templates/preventiveActionList",model:[type:type])
			}
		}
		render(contentType:"text/json") { results = [result,html,"action"]}
	}
}
