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
package org.chai.memms.reports.hmis

import org.chai.location.Location
import org.chai.memms.AbstractEntityController
import org.chai.memms.inventory.EquipmentType
import org.chai.memms.security.User
import org.apache.shiro.SecurityUtils
import org.apache.commons.lang.math.NumberUtils

/**
 * @author Eric Dusabe, Jean Kahigiso M.
 *
 */
class HmisEquipmentTypeController extends AbstractEntityController {

	def hmisEquipmentTypeService

	def getEntity(def id) {
		return HmisEquipmentType.get(id);
	}
	
	def createEntity() {
		return new HmisEquipmentType();
	}
	
	def getTemplate() {
		return "/entity/hmis/createHmisEquipmentType";
	}

	def getLabel() {
		return "hmis.equipment.type.label";
	}	
		
	def getEntityClass() {
		return HmisEquipmentType.class;
	}


	def bindParams(def entity) {
		params.oldEquipmentTypes = entity.equipmentTypes
		def equipmentTypes = new HashSet<EquipmentType>()
		params.list('equipmentTypes').each { id ->
			if (NumberUtils.isDigits(id)) {
				def equipmentType = EquipmentType.get(id)
				(!equipmentType)?:equipmentTypes.add(equipmentType);
			}
		}
		entity.equipmentTypes = equipmentTypes
		bindData(entity,params,[exclude:["equipmentTypes"]])
	}

	def saveEntity(def entity) {
		entity.save(failOnError:true)

		if(entity.id==null) entity.params.oldEquipmentTypes = []

		entity.equipmentTypes.each{ equipmentType ->
			equipmentType.hmisType = entity
			equipmentType.save(failOnError:true)
		}

		params.oldEquipmentTypes.each{ equipmentType ->
			if(!entity.equipmentTypes.contains(equipmentType)){
				equipmentType.hmisType = null
				equipmentType.save(failOnError:true)
			}
		}
		
	}

	
	def getModel(def entity) {
		def equipmentTypes = []
		if(entity.equipmentTypes != null) equipmentTypes = new ArrayList(entity.equipmentTypes)
		[
			type:entity,
			equipmentTypes:equipmentTypes
		]
	}

		
	

	def deleteEntity(def entity) {
	}
	
	

	def list = {
		adaptParamsForList()
		def types = HmisEquipmentType.list(offset:params.offset,max:params.max,sort:params.sort ?:"id",order: params.order ?:"desc");
		if(request.xhr)
			this.ajaxModel(types,"")
		else{
			render(view:"/entity/list",model:model(types) << [
				template:"hmis/hmisEquipmentTypeList",
				listTop:"hmis/listTop"
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
		def listHtml = g.render(template:"/entity/hmis/hmisEquipmentTypeList",model:model)
		render(contentType:"text/json") { results = [listHtml] }
	}

	def search = {
		adaptParamsForList()
		List<HmisEquipmentType> types = hmisEquipmentTypeService.searchHmisEquipmentType(params['q'],params)
		if(request.xhr)
			this.ajaxModel(types,params['q'])
		else {
			render(view:"/entity/list",model:model(types) << [
				template:"hmisEquipmentType/hmisEquipmentTypeList",
				listTop:"hmisEquipmentType/listTop"
			
			])
		}
	}

	def getAjaxData = {
		List<HmisEquipmentType> types = hmisEquipmentTypeService.searchHmisEquipmentType(params['term'],[:])
		render(contentType:"text/json") {
			elements = array {
				types.each { type ->
					elem (
							key: type.id,
							value: type.names + ' - ['+type.code+']' + ' - ['+type.equipmentTypes+']'
							)
				}
			}
		}
	}	
}