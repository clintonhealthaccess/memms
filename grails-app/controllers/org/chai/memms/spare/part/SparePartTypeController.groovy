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
package org.chai.memms.spare.part

import java.util.Set;

import org.chai.location.CalculationLocation;
import org.chai.location.DataLocationType;
import org.chai.memms.inventory.EquipmentStatus.Status;
import org.chai.memms.AbstractEntityController;
import org.chai.memms.spare.part.SparePartType;
/**
 * @author Alain Inema
 *
 */
class SparePartTypeController  extends AbstractEntityController{
	def sparePartTypeService
	def languageService
	
	def getEntity(def id) {
		return SparePartType.get(id);
	}

	def createEntity() {

		return new SparePartType();
	}

	def getTemplate() {
		return "/entity/sparePartType/createSparePartType";
	}

	def getLabel() {
		return "spare.part.type.label";
	}

	def getEntityClass() {
		return SparePartType.class;
	}

	def deleteEntity(def entity) {
		if (entity.spareParts.size() != 0)
			flash.message = message(code: 'spare.part.type.hasspare part', args: [message(code: getLabel(), default: 'entity'), params.id], default: '{0} still has associated spare part.')
		else
			super.deleteEntity(entity);
	}
	def bindParams(def entity) {
		entity.properties = params
	}
	

	def getModel(def entity) {
		[
			type: entity
		]
	}
	
	def list = {
		adaptParamsForList()
		List<SparePartType> types = SparePartType.list(offset:params.offset,max:params.max,sort:params.sort ?:"id",order: params.order ?:"desc");
		if(request.xhr)
			this.ajaxModel(types,"")
		else{

		render(view:"/entity/list",model:model(types) <<[
				template:"sparePartType/sparePartTypeList",
				listTop:"sparePartType/listTop",
				entities: types,
//				entityCount: types.totalCount,
				entityClass: getEntityClass()
//				code: getLabel(),
//				names:names,
//			    descriptions:descriptions
			])
		}
	}
	
	
	def search = {
		adaptParamsForList()
		List<SparePartType> types = sparePartTypeService.searchSparePartType(params['q'],params)
		if(request.xhr)
			this.ajaxModel(types,params['q'])
		else {
			render(view:"/entity/list",model:model(types) << [
				template:"sparePartType/sparePartTypeList",
				listTop:"sparePartType/listTop"
			
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
		def listHtml = g.render(template:"/entity/sparePartType/sparePartTypeList",model:model)
		render(contentType:"text/json") { results = [listHtml] }
	}
	
	def getAjaxData = {
		List<SparePartType> types = sparePartTypeService.searchSparePartType(params['term'],[:])
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
	
}
