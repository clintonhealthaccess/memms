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

import java.util.List;

import org.chai.location.CalculationLocation;
import org.chai.memms.AbstractEntityController
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.chai.memms.security.User;
import org.chai.memms.security.User.UserType;

import org.chai.location.DataLocation;
import org.chai.location.Location;
import org.chai.location.CalculationLocation;

class UserController  extends  AbstractEntityController{
	def userService

	def getEntity(def id) {
		return User.get(id)
	}

	def createEntity() {
		return new User()
	}

	def getLabel() {
		return 'user.label'
	}
	
	def getModel(def entity) {
		def dataLocations = []
		if (entity.location != null) dataLocations << entity.location
		[
			user:entity,
			roles: Role.list(),
			dataLocations: dataLocations,
			cmd: params['cmd']
		]
	}
	
	def getEntityClass(){
		return User.class;
	}
	
	def deleteEntity(def entity) {
		flash.message = message(code: 'user.cannot.be.delete', args: [message(code: getLabel(), default: 'entity'), params.id], default: '{0} cannot but can deactivated instead.')
	}
	
	def getTemplate() {
		return "/entity/user/createUser"
	}
	
	def validateEntity(def entity) {
		//TODO check for duplicate code
		boolean valid = entity.validate() && params['cmd'].validate()
		if (log.isDebugEnabled()) log.debug ("validation for command object ${params['cmd']}: ${params['cmd'].errors}}")
		return valid;
	}
	
	def saveEntity(def entity) {
		entity.save()
	}
	
	def bindParams(def entity) {
		if (log.isDebugEnabled()) log.debug('binding params: '+params)
		bindData(entity,params,[exclude:['uuid','passwordHash']])
		
		if(entity.id==null)
			entity.uuid = UUID.randomUUID().toString();
					
		if(params['cmd']?.password != null && !params['cmd']?.password.equals(''))
			entity.passwordHash = new Sha256Hash(params['cmd'].password).toHex();
	}
	
	def save = { PasswordCommand cmd ->
		if (log.isDebugEnabled()) log.debug("create.userPassword, params:"+params+"command"+cmd)
		params['cmd'] = cmd;
		super.save()
	}
	
	def list = {
		adaptParamsForList()
		def users = User.list(offset:params.offset,max:params.max,sort:params.sort ?:"id",order: params.order ?:"desc");
		if(request.xhr)
			this.ajaxModel(users,"")
		else{
			render (view: '/entity/list', model: model(users) << [
				template:"user/userList",
				listTop:"user/listTop",				
			])
		}
	}

	def search = {
		adaptParamsForList()
		def users = userService.searchUser(params['q'], params);
		if(request.xhr)
			this.ajaxModel(users,params['q'])
		else {
			render (view: '/entity/list', model: model(users) << [
				template:"user/userList",
				listTop:"user/listTop",				
			])
		}
	}
	
	def model(def entities) {
		return [
			entities: entities,
			entityCount: entities.totalCount,
			code: getLabel(),
			entityClass: getEntityClass()
		]
	}
	
	def ajaxModel(def entities,def searchTerm) {
		def model = model(entities) << [q: searchTerm]
		def listHtml = g.render(template:"/entity/user/userList",model:model)
		render(contentType:"text/json") { results = [listHtml] }
	}
	
	def getAjaxData = {
		def dataLocation = CalculationLocation.get(params["dataLocation"])
		List<UserType> userTypes = []
		for(def type: params['userTypes']) 
			userTypes.add(UserType."$type")

       	def users = userService.searchActiveUserByTypeAndLocation(params['term'],userTypes,dataLocation)
		render(contentType:"text/json") {
			elements = array {
				users.each { user ->
					elem (
						key: user.id,
						value: user.names
						                                                
					)
				}
			}
		}
		
	}		
	
}

class PasswordCommand {
	String password
	String repeat

	static constraints = {
		password(blank: true,nullable:true, minSize: 4)
		repeat(nullable:true, validator: {val, obj ->
			val == obj.password
		})
	}
	
	String toString() {
		return "PasswordCommand[password="+password+", repeat="+repeat+"]"
	}
}
