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
package org.chai.memms.equipment

import org.chai.memms.AbstractEntityController;
import org.chai.memms.equipment.Provider.Type;
import org.chai.memms.equipment.Provider;

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
		if(entity.manufactures.size() > 0 || entity.suppliers.size() > 0 ){
			flash.message = message(code: 'provider.hasequipments', args: [message(code: getLabel(), default: 'entity'), params.id], default: '{0} still has associated equipments.')
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
		List<Provider> providers = Provider.list(offset:params.offset,max:params.max,sort:params.sort ?:"id",order: params.order ?:"desc")
		render(view:"/entity/list", model:[
			template:"provider/providerList",
			entities: providers,
			entityCount: Provider.count(),
			code: getLabel(),
			entityClass: getEntityClass()
			])
	}
	
	def search = {
		adaptParamsForList()
		List<Provider> providers = providerService.searchProvider(null, params['q'], params)		
		render (view: '/entity/list', model:[
			template:"provider/providerList",
			entities: providers,
			entityCount: providers.totalCount,
			code: getLabel(),
			q:params['q']
		])
		
	}
	def getAjaxData = {
		log.debug("=================== Params: " + params)
		def type =params['type']
		type = Type."$type";
		List<Provider> providers = providerService.searchProvider(type, params['term'], [:])
		render(contentType:"text/json") {
			elements = array {
				providers.each { provider ->
					elem (
						key: provider.id,
						value: provider.contact.contactName + ' ['+provider.code+']'
					)
				}
			}
		}
	}
}
