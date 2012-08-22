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

import java.util.List;
import java.util.Map;
import org.chai.memms.equipment.Equipment
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
	
	Integer countEquipment(String text) {
		return getSearchCriteria(text).setProjection(Projections.count("id")).uniqueResult()
	}
	
	public List<Equipment> searchEquipment(String text, Map<String, String> params) {
		def criteria = getSearchCriteria(text)
		List<Equipment> equipments = []
		if (params['offset'] != null) criteria.setFirstResult(params['offset'])
		if (params['max'] != null) criteria.setMaxResults(params['max'])
		if (params['order'] != null) equipments criteria.addOrder(Order.asc(params['order'])).list()
		else equipments = criteria.addOrder(Order.asc("id")).list()
		
		StringUtils.split(text).each { chunk ->
			equipments.retainAll { equipment ->
				Utils.matches(chunk, equipment.serialNumber) ||
				Utils.matches(chunk, equipment.getDescriptions(languageService.getCurrentLanguage()))
			}
		}
		return equipments
	}
	
	private Criteria getSearchCriteria(String text) {
		def dbFieldDescriptions = 'descriptions_'+languageService.getCurrentLanguagePrefix();
		def criteria = sessionFactory.getCurrentSession().createCriteria(Equipment.class)
		
		def textRestrictions = Restrictions.conjunction()
		StringUtils.split(text).each { chunk ->
			def disjunction = Restrictions.disjunction();
			disjunction.add(Restrictions.ilike("serialNumber", chunk, MatchMode.ANYWHERE))
			disjunction.add(Restrictions.ilike(dbFieldDescriptions, chunk, MatchMode.ANYWHERE))
			textRestrictions.add(disjunction)
		}
		criteria.add(textRestrictions)
		return criteria
	}
	

	public List<Equipment> filterEquipment(){
		return null
	}
	public List<Equipment> getEquipmentsByDataLocation(CalculationLocation location) {
		
		List<Equipment> equipments = []
		
		if(location instanceof DataLocation){
			equipments = Equipment.findAll{
				dataLocation == location
			}
		}else if (location instanceof Location){
			equipments = Equipment.findAll{
				dataLocation in ((Location)location).dataLocations
			}
		}
		
		return equipments

	}

}
