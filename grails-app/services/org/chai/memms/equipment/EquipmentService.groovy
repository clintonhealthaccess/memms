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

import grails.gorm.DetachedCriteria
import java.util.List;
import java.util.Map;
import org.chai.memms.equipment.Equipment
import org.chai.memms.equipment.EquipmentStatus.Status;
import org.chai.memms.location.CalculationLocation;
import org.chai.memms.location.DataLocation
import org.chai.memms.location.Location

import org.chai.memms.util.Utils;
import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode
import org.hibernate.criterion.Order
import org.hibernate.criterion.Projections
import org.hibernate.criterion.Restrictions
import org.apache.commons.lang.StringUtils

/**
 * @author Jean Kahigiso M.
 *
 */
class EquipmentService {
	
	static transactional = true
	def languageService;
	def sessionFactory;
		
	public List<Equipment> searchEquipment(String text,DataLocation location,Map<String, String> params) {
		def dbFieldTypeNames = 'names_'+languageService.getCurrentLanguagePrefix();
		def dbFieldDescriptions = 'descriptions_'+languageService.getCurrentLanguagePrefix();
		def criteria = Equipment.createCriteria();
		
		return  criteria.list(offset:params.offset,max:params.max,sort:params.sort ?:"id",order: params.order ?:"desc"){
			    createAlias("type","t")
				eq('dataLocation',location)
				or{
					ilike("serialNumber","%"+text+"%")
					ilike(dbFieldDescriptions,"%"+text+"%") 
					ilike("t."+dbFieldTypeNames,"%"+text+"%")
			    }
		}
	}
		
	public List<Equipment> getEquipmentsByDataLocation(DataLocation dataLocation,Map<String, String> params) {
		def criteria = Equipment.createCriteria();
			return criteria.list(offset:params.offset,max:params.max,sort:params.sort ?:"id",order: params.order ?:"desc"){
				eq('dataLocation',dataLocation)
			}
	}
	

	public List<Equipment> filterEquipment(def dataLocation, def supplier, def manufacturer, def equipmentType, 
		def donation, def obsolete,def status,Map<String, String> params){
		
		def criteria = Equipment.createCriteria();
		return criteria.list(offset:params.offset,max:params.max,sort:params.sort ?:"id",order: params.order ?:"desc"){
			if(dataLocation != null){
				eq('dataLocation',dataLocation)
			}
			if(supplier != null){
				eq ("supplier", supplier)
			}
			if(manufacturer != null){
				eq ("manufacture", manufacturer)
			}
			if(equipmentType != null){
				eq ("type", equipmentType)
			}
			if(donation){
				eq ("donation", (donation.equals('true'))?true:false)
			}
			if(obsolete){
				eq ("obsolete", (obsolete.equals('true'))?true:false)
			}
			if(!status.equals(Status.NONE)){
				createAlias("status","t")
				and{
					eq ("t.status", status)
					eq ("t.current", true)
				}
			}
				
		}
	}

}
