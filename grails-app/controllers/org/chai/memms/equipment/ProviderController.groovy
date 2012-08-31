/**
 * 
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
