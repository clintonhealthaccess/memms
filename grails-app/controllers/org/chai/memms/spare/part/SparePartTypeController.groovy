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

import org.apache.commons.lang.math.NumberUtils;
import org.chai.location.CalculationLocation;
import org.chai.location.DataLocationType;
import org.chai.memms.inventory.EquipmentStatus.Status;
import org.chai.memms.AbstractEntityController;
import org.chai.memms.spare.part.SparePartType;
import org.chai.memms.inventory.Provider.Type;
import org.chai.memms.inventory.EquipmentType;
import org.chai.memms.inventory.EquipmentType.Observation;
import org.chai.memms.inventory.Provider;
import org.chai.memms.Contact



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
			flash.message = message(code: 'sparePart.type.hassparePart', args: [message(code: getLabel(), default: 'entity'), params.id], default: '{0} still has associated spare part.')
		else
			super.deleteEntity(entity);
	}
	
	def bindParams(def entity) {
		params.oldCompatibleEquipmentTypes = entity.compatibleEquipmentTypes
		params.oldVendors = entity.vendors

		def compatibleEquipmentTypes = new HashSet<EquipmentType>()
		params.list('compatibleEquipmentTypes').each { id ->
			if (NumberUtils.isDigits(id)) {
				def equipmentType = EquipmentType.get(id)
				(!equipmentType)?:compatibleEquipmentTypes.add(equipmentType);
			}
		}
		entity.compatibleEquipmentTypes = compatibleEquipmentTypes
		
		def vendors = new HashSet<Provider>()
		params.list('vendors').each { id ->
			if (NumberUtils.isDigits(id)) {
				def vendor = Provider.get(id)
				(!vendor)?:vendors.add(vendor);
			}
		}
		entity.vendors = vendors
		bindData(entity,params,[exclude:["compatibleEquipmentTypes","vendors"]])
	}
	
	def saveEntity(def entity) {
		entity.save(failOnError:true)
		if(entity.id==null){
			entity.params.oldCompatibleEquipmentTypes = []
			entity.params.oldVendors = []
		}
		params.oldCompatibleEquipmentTypes.each{ type ->
				if(!entity.compatibleEquipmentTypes.contains(type)){
					type.sparePartTypes.remove(entity)
					type.save(failOnError:true)
				}
			}
		params.oldVendors.each{ vendor ->
				if(!entity.vendors.contains(vendor)){
					vendor.sparePartTypes.remove(entity)
					vendor.save(failOnError:true)
				}
			}
	}
		
	def getModel(def entity) {
		def compatibleEquipmentTypes = []

		def vendors = Provider.findAllByTypeInList([Type.SUPPLIER,Type.BOTH],[sort:'contact.contactName'])
		def manufacturers = Provider.findAllByTypeInList([Type.MANUFACTURER,Type.BOTH],[sort:'contact.contactName'])
						
		if(entity.compatibleEquipmentTypes != null)  compatibleEquipmentTypes = new ArrayList(entity.compatibleEquipmentTypes)
		if(entity.vendors != null) vendors = new ArrayList(entity.vendors)
		[
			vendors: vendors,
			compatibleEquipmentTypes:compatibleEquipmentTypes,
			type: entity,
		    manufacturers: manufacturers	
         ]
	}
							
	def list = {	
		adaptParamsForList()
		List<SparePartType> types = SparePartType.list(offset:params.offset,max:params.max,sort:params.sort ?:"id",order: params.order ?:"desc")
		if(request.xhr)
			this.ajaxModel(types,"")
		else{
		render(view:"/entity/list",model:model(types) <<[
				template:"sparePartType/sparePartTypeList",
				listTop:"sparePartType/listTop",
				entities: types,
				entityClass: getEntityClass()
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
							value: type.names + ' - ['+type.code+']' + ' - ['+type.partNumber+']'
							)
				}
			}
			htmls = array {
				types.each { type ->
					elem (
							key: type.id,
							html: g.render(template:"/templates/sparePartTypeFormSide",model:[type:type,label:label,cssClass:"form-aside-hidden",field:'type'])
							)
				}
			}
		}
	}	
	
}
