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
package org.chai.memms.security

import org.chai.memms.AbstractEntityController;

/**
 * @author Eugene Munyaneza
 *
 */
class RoleController extends AbstractEntityController{
	
	def roleService
	
	def getEntity(def id) {
		return Role.get(id);
	}

	def createEntity() {
		return new Role();
	}

	def getTemplate() {
		return "/entity/role/createRole"
	}
	
	def getLabel() {
		return "role.label";
	}

	def getEntityClass() {
		return Role.class;
	}
	def deleteEntity(def entity) {
		if (entity.users.size() != 0)
			flash.message = message(code: 'role.haseusers', args: [message(code: getLabel(), default: 'entity'), params.id], default: '{0} still has associated users.')
		else
			super.deleteEntity(entity);
	}
	
	def bindParams(def entity) {		
		entity.properties = params		
	}
	
	def getModel(def entity) {
		[
			role: entity,
		]
	}
	def list = {
		adaptParamsForList()
		List<Role> roles = Role.list(offset:params.offset,max:params.max,sort:params.sort ?:"id",order: params.order ?:"desc");
		if(request.xhr)
			this.ajaxModel(roles,"")
		else{
			render(view:"/entity/list",model:model(roles) << [
				template: "role/roleList",
				listTop:"role/listTop"				
			])
		}
	}
	
	def search = {
		adaptParamsForList()
		List<Role> roles = roleService.searchRole(params['q'], params)
		if(request.xhr)
			this.ajaxModel(roles,params['q'])
		else {
			render(view:"/entity/list",model:model(roles) << [
				template: "role/roleList",
				listTop:"role/listTop"				
			])
		}
	}
	
	def model(def entities) {
		return [
			entities: entities,
			entityCount: entities.totalCount,
			code: getLabel()
		]
	}
	
	def ajaxModel(def entities,def searchTerm) {
		def model = model(entities) << [q:searchTerm]
		def listHtml = g.render(template:"/entity/role/roleList", model:model)
		render(contentType:"text/json") { results = [listHtml] }
	}
}
