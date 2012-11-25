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
package org.chai.memms.location;

import java.util.Set;

import org.apache.commons.lang.math.NumberUtils;
import org.chai.location.DataLocation;
import org.chai.location.DataLocationType;
import org.chai.location.Location;
import org.chai.location.LocationLevel;
import org.chai.memms.AbstractEntityController;
import org.chai.memms.inventory.Equipment;

class DataLocationController extends AbstractEntityController {
	def locationService	
	def grailsApplication
	
	def getModel(def entity) {
		def locations = []
		def manages = []
		if(entity.manages!= null)  manages = new ArrayList(entity.manages)
		if (entity.location != null) locations << entity.location
		[	
			location: entity, 
			types: DataLocationType.list([cache: true]), 
			locations: locations,
			manages:manages
			]
	}
	
	def bindParams(def entity) {
		params.oldManages = entity.manages
		if(log.isDebugEnabled()) log.debug("oldManages: "+entity.manages)
		Set<DataLocation> manages = new HashSet<DataLocation>()
		params.list('managesIds').each { id ->
			if (NumberUtils.isDigits(id)) {
				def managed = DataLocation.get(id)
				if (managed && !manages.contains(managed)) manages.add(managed);
			}
		}
		entity.manages = manages
		if(log.isDebugEnabled()) log.debug("newManages: "+entity.manages)
		bindData(entity,params,[exclude:["managesIds"]])
	}
	
	def saveEntity(def entity) {
		entity.save(failOnError:true)
		def removedManaged = (params.oldManages)?(params.oldManages - entity.manages):[]
		entity.manages.each{
			it.managedBy= entity
			it.save(failOnError:true)
		}
		removedManaged.each{
			it.managedBy = null
			it.save(failOnError:true)
		}
		if(log.isDebugEnabled()) log.debug("newManages after save: "+entity.manages)
	}


	def getEntityClass(){
		return DataLocation.class;
	}
	
	def getEntity(def id) {
		return DataLocation.get(id);
	}

	def createEntity() {
		return new DataLocation();
	}

	def getTemplate() {
		return '/entity/location/createDataLocation'
	}

	def getLabel() {
		return 'datalocation.label';
	}
	
	def deleteEntity(def entity) {
		if(Equipment.findByDataLocation(entity)!=null)
			flash.message = message(code: 'datalocation.hasequipment', args: [message(code: getLabel(), default: 'entity'), params.id], default: '{0} still has associated equipment.')
		else entity.delete()
	}
	
	
	
	def list = {
		adaptParamsForList()
		List<DataLocation> locations = DataLocation.list(offset:params.offset,max:params.max,sort:params.sort ?:names,order: params.order ?:"asc");
		if(request.xhr){
			this.ajaxModel(locations,"")
		}else{
			render (view: '/entity/list', model:[
				template:"location/dataLocationList",
				listTop:"location/dataLocationListTop",
				entities: locations,
				entityCount: locations.totalCount,
				code: getLabel(),
				names:names
			])
		}
	}
	def search = {
		adaptParamsForList()
		List<DataLocation> locations = locationService.searchLocation(DataLocation.class, params['q'], params)		
		if(!request.xhr)
			response.sendError(404)
		this.ajaxModel(locations,params['q'])
	}

	def ajaxModel(def entities,def searchTerm) {
		def model = [entities: entities,entityCount: entities.totalCount,names:names,q:searchTerm]
		def listHtml = g.render(template:"/entity/location/dataLocationList",model:model)
		render(contentType:"text/json") { results = [listHtml] }
	}
	
	def getAjaxData = {
		List<DataLocation> dataLocations = locationService.searchLocation(DataLocation.class, params['term'], [:])
		render(contentType:"text/json") {
			elements = array {
				dataLocations.each { dataLocation ->
					elem (
						key: dataLocation.id,
						value: dataLocation.names + ' ['+dataLocation.location.names+']'
					)
				}
			}
		}
	}
	
}
