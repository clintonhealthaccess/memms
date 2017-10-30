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


import org.chai.memms.inventory.EquipmentType.Observation;
import java.util.List;
import java.util.Map;
import org.chai.memms.inventory.Provider.Type;
import org.chai.memms.inventory.Provider;
import org.chai.memms.security.User;
import org.chai.memms.spare.part.SparePartType;
import org.chai.memms.spare.part.SparePart.SparePartStatus;
import org.chai.memms.AbstractEntityController;


/**
 * @author Jean Kahigiso M.
 *
 */
class ProviderController  extends AbstractEntityController {

	def providerService

	def  getEntity(def  id) {
		return Provider.get(id);
	}

	def createEntity() {
		return new Provider();
	}

	def getTemplate() {
		return "/entity/provider/createProvider";
	}

	def getLabel() {
		return "provider.label";
	}

	def getEntityClass() {
		return Provider.class;
	}

	def deleteEntity(def entity) {
		if(entity.manufacturers.size() > 0 || entity.suppliers.size() > 0 || entity.sparePartTypes.size()>0 || entity.serviceProviders.size()>0){
			flash.message = message(code: 'provider.hasequipments', args: [ message(code: getLabel(), default: 'entity'), params.id], default: '{0} still has associated equipments or Spare part type.')
		}else super.deleteEntity(entity)
	}

	def getModel(def entity) {
		[
			provider: entity
		]
	}

	def bindParams(def entity) {
		entity.properties = params
	}

	def list = {
		adaptParamsForList()		
		def providers = Provider.list(offset:params.offset,max:params.max,sort:params.sort ?:"id",order: params.order ?:"desc")
		if(request.xhr)		
			this.ajaxModel(providers,"")
		else{
		    render(view:"/entity/list",model:model(providers) << [
				template:"provider/providerList",
				listTop:"provider/listTop"
			])
		}

	}
		
	def search = {
		adaptParamsForList()
		def providers = providerService.searchProvider(null,params['q'], params)		
		if(request.xhr)
			this.ajaxModel(providers,params['q'])
		else {
			render(view:"/entity/list", model: model(providers) << [
				template:"provider/providerList",
				listTop:"provider/listTop",
			])
		}
	}


	def ajaxModel(def entities,def searchTerm) {
		def model = model(entities) << [q:searchTerm]
		def listHtml = g.render(template:"/entity/provider/providerList",model:model)
		render(contentType:"text/json") { results = [listHtml] }
	}

	
	def model(def entities) {
		return [
			entities: entities,
			entityCount: entities.totalCount,
			code: getLabel()
		]
	}

	def getAjaxData = {
		if(log.isDebugEnabled()) log.debug("Provider getAjaxData Sent Params: " + params)

		def detailsLabel='';
		def type =params['type']
		type = Type."$type";
		if(type.equals(Type.MANUFACTURER)) detailsLabel="provider.manufacturer.details"
		else if(type.equals(Type.SUPPLIER)) detailsLabel="provider.supplier.details"
		else detailsLabel="provider.serviceProvider.details"
		
		def providers = providerService.searchProvider(type,params['term'],[:])
		render(contentType:"text/json") {
			elements = array {
				providers.each { provider ->
					elem (
							key: provider.id,
							value: provider.contact?.contactName + ' ['+provider.code+']'
							)
				}
			}
			htmls = array {
				providers.each { provider ->
					elem (
							key: provider.id,
							html: g.render(template:"/templates/providerFormSide",model:[provider:provider,type:type,label:detailsLabel,cssClass:"form-aside-hidden",field:type.name])
							)
				}
			}
		}
	}
}
