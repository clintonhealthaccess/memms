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
package org.chai.location;

import org.chai.memms.AbstractEntityController;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


class LocationController extends AbstractEntityController {

	def locationService
	
	def bindParams(def entity) {
		entity.properties = params		
	}

	def getModel(def entity) {
		def locations = []
		if (entity.parent != null) locations << entity.parent
		[	location: entity, 
			locations: locations, 
			levels: LocationLevel.list([cache: true])
			]
	}

	def getEntityClass(){
		return Location.class;
	}
	
	def getEntity(def id) {
		return Location.get(id);
	}

	def createEntity() {
		return new Location();
	}

	def deleteEntity(def entity) {
		if (entity.children.size() != 0) {
			flash.message = message(code: 'location.haschildren', args: [message(code: getLabel(), default: 'entity'), params.id], default: '{0} still has associated children.')
		}
		else if (entity.dataLocations.size() != 0) {
			flash.message = message(code: 'location.hasdataentities', args: [message(code: getLabel(), default: 'entity'), params.id], default: '{0} still has associated facility.')
		}
		else {
			super.deleteEntity(entity)
		}
	}
	
	def getTemplate() {
		return '/entity/location/createLocation'
	}

	def getLabel() {
		return 'location.label';
	}

	def list = {
		adaptParamsForList()
		List<Location> locations = Location.list(offset:params.offset,max:params.max,sort:params.sort ?:"level",order: params.order ?:"asc");
		render (view: '/entity/list', model:[
			template:"location/locationList",
			entities: locations,
			entityCount: locations.totalCount,
			code: getLabel(),
			entityClass: getEntityClass(),
			names:names
		])
	}
	
	def search = {
		adaptParamsForList()
		
		List<Location> locations = locationService.searchLocation(Location.class, params['q'], params)
				
		render (view: '/entity/list', model:[
			template:"location/locationList",
			entities: locations,
			entityCount: locations.totalCount,
			code: getLabel(),
			q:params['q'],
			names:names
		])
	}
	
	def getAjaxData = {
		def clazz = Location.class
		if (params['class'] != null) clazz = Class.forName('org.chai.location.'+params['class'], true, Thread.currentThread().contextClassLoader)
		
		def locations = locationService.searchLocation(clazz, params['term'], [:])
		render(contentType:"text/json") {
			elements = array {
				locations.each { location ->
					elem (
						key: location.id,
						value: location.names + ' ['+location.class.simpleName+']'
					)
				}
			}
		}
	}
	
}
