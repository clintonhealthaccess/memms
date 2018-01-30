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

import org.apache.shiro.SecurityUtils;
import org.chai.memms.AbstractEntityController;
import org.chai.memms.Contact;
import org.chai.memms.Initializer;
import org.chai.memms.inventory.Equipment.Donor;
import org.chai.memms.inventory.Equipment.PurchasedBy;
import org.chai.memms.inventory.EquipmentStatus.Status;
import org.chai.memms.security.User;
import org.chai.memms.security.User.UserType;
import org.chai.memms.util.Utils;
import org.chai.memms.inventory.Equipment;
import org.chai.memms.inventory.Provider;
import org.chai.location.DataLocation;
import org.chai.location.CalculationLocation;
import org.chai.location.DataLocationType;
import org.chai.location.Location;
import org.chai.location.LocationLevel;
import org.chai.memms.inventory.Provider.Type;

import java.util.HashSet;
import java.util.Set;
import java.util.Date;

/**
 * @author Jean Kahigiso M.
 *
 */
class EquipmentController extends AbstractEntityController{

	def equipmentStatusService
	def equipmentService


	def index = {
		redirect(controller: "equipmentView", action: "summaryPage", params: params)
	}

	def getEntity(def id) {
		return Equipment.get(id);
	}

	def createEntity() {
		return new Equipment();
	}

	def getTemplate() {
		return "/entity/equipment/createEquipment";
	}
	
	def getLabel() {
		return "equipment.label";
	}

	def getEntityClass() {
		return Equipment.class;
	}
	

	def bindParams(def entity) {
		if(log.isDebugEnabled()) log.debug("Equipment params: before bind "+params)
		if(!entity.id){
			entity.addedBy = user
		}else{
		    params.oldStatus =  entity.currentStatus
			entity.lastModifiedBy = user
			if(params["warranty.sameAsSupplier"]=="on"){
				params["warranty.contact.contactName"]=""
				params["warranty.contact.email"]=""
				params["warranty.contact.phone"]=""
				params["warranty.contact.poBox"]=""
				params["warranty.contact.city"]=""
				params["warranty.contact.country"]=""
				grailsApplication.config.i18nFields.locales.each{loc ->
					params["warranty.contact.addressDescriptions_"+loc] = ""
				}
				entity.warranty.contact=null
			}
		}
		if(params["purchaser"]!="BYDONOR"){
			params["donor"] =""
			params["donorName"] = ""
		}
		//Making sure a disposed equipment cannot be modified 
		//TODO add this check to method that modified an equipment
		if(!params.oldStatus.equals(Status.DISPOSED))
			bindData(entity,params,[exclude:["status","dateOfEvent"]])
		if(log.isDebugEnabled()) log.debug("Equipment params: after bind  "+entity)
	}
	
	
	

	def validateEntity(def entity) {
		boolean validStatus = true
		if(entity.id==null){
			//Checking if the dateOfEvent is not after parchase date and add error
			//TODO to be uncommented after first data collection, as the date of purchase of old equipment might be unknown
			//if(!(entity.purchaseDate?.before(params.cmd.dateOfEvent) || entity.purchaseDate?.compareTo(params.cmd.dateOfEvent)==0)) 
			//	params.cmd.errors.rejectValue('dateOfEvent','date.of.event.before.parchase.date')
			validStatus = (!params.cmd.hasErrors()) 
			if(log.isDebugEnabled()) log.debug("Rejecting EquipmentStatus: "+params.cmd.errors)
		}
		return (validStatus & entity.validate())
	}

	def saveEntity(def entity) {
		EquipmentStatus status
		if(entity.dataLocation) hasAccess(entity.dataLocation)
		if(entity.id==null){
			entity.currentStatus = Status."$params.cmd.status"
			equipmentStatusService.createEquipmentStatus(user,params.cmd.status,entity,params.cmd.dateOfEvent,[:])
		}
		else entity.save(failOnError:true)
	}

	def save = { StatusCommand cmd ->
		params.cmd = cmd
		super.saveWithoutTokenCheck()
	}

	def getModel(def entity) {
		def manufacturers = Provider.findAllByTypeInList([Type.MANUFACTURER,Type.BOTH],[sort:'contact.contactName']); 
		def suppliers = Provider.findAllByTypeInList([Type.SUPPLIER,Type.BOTH,Type.SUPPLIERANDSERVICEPROVIDER],[sort:'contact.contactName']);  
		def serviceProviders = Provider.findAllByTypeInList([Type.SERVICEPROVIDER,Type.SUPPLIERANDSERVICEPROVIDER],[sort:'contact.contactName']);  
		def departments = Department.list(sort: names); 
		
		def types = []; 
		def dataLocations = [];
		if (entity.type!=null) types << entity.type
		if (entity.dataLocation!=null) dataLocations << entity.dataLocation

		[
					equipment: entity,
					departments: departments,
					manufacturers: manufacturers,
					suppliers: suppliers,
					serviceProviders:serviceProviders,
					types: types,
					dataLocations: dataLocations,
					numberOfStatusToDisplay: grailsApplication.config.status.to.display.on.equipment.form,
					cmd:params.cmd,
					currencies: grailsApplication.config.site.possible.currency,
					now:now

				]
	}
	
	
	def generateEndDate = {
		def equipments = equipmentService.updateWarrantyEndDate()
		equipments.each { equipment ->
			log.debug("currentEquipment= "+equipment.id+" dataLocation= "+equipment.dataLocation.id)
			equipment.generateWarrantyEndDate()
			equipment.save(failOnError:true)
		}
	}

	
	def getAjaxData = {
		List<EquipmentType> types = equipmentTypeService.searchEquipmentType(params['term'],Observation."$params.observation",[:])
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

class StatusCommand {
	Status status
	Date dateOfEvent

	static constraints = {
		status nullable: false, inList: [Status.DISPOSED,Status.FORDISPOSAL,Status.PARTIALLYOPERATIONAL,Status.INSTOCK,Status.OPERATIONAL,Status.UNDERMAINTENANCE]
		dateOfEvent nullable: false, validator:{ val, obj ->
			//TODO be uncomment after first data collection
			return (val <= new Date()) //&&  (val.after(obj.equipment.purchaseDate) || (val.compareTo(obj.equipment.purchaseDate)==0))
		}
	}

	String toString(){
		return "StatusCommand [Status "+status+" dateOfEvent: "+dateOfEvent+"]";
	}
}

