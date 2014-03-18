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

package org.chai.memms.reports.hmis

import org.chai.memms.Initializer;
import org.chai.memms.IntegrationTests;
import org.chai.memms.reports.hmis.HmisEquipmentTypeController;
import org.chai.memms.reports.hmis.HmisEquipmentTypeService;
import org.chai.memms.inventory.EquipmentType;
import org.chai.memms.inventory.EquipmentType.Observation;
/**
 *
 * @author Dusabe Eric, Kahigiso M. Jean
 */

class HmisEquipmentTypeControllerSpec extends IntegrationTests{

	def hmisEquipmentTypeService
	

	def "can create and save an hmis equipment type with default values - all locales"(){
		setup:
		def hmisEquipmentTypeController = new HmisEquipmentTypeController()
		def hmisEquipmentType
		Initializer.newEquipmentType(CODE(123), ["en":"names equipment type one"],["en":"used in memms"], Observation.USEDINMEMMS,new Date(),25)
		Initializer.newEquipmentType(CODE(124), ["en":"names equipment type two"],["en":"used in memms"], Observation.USEDINMEMMS,new Date(),25)
		when:

		hmisEquipmentTypeController.params.code = CODE(123)
		
		grailsApplication.config.i18nFields.locales.each{
			hmisEquipmentTypeController.params."names_$it" = "X-Ray Film Cutter $it"
		}

		hmisEquipmentTypeController.params.equipmentType = "struct"
		hmisEquipmentTypeController.params.equipmentType = EquipmentType.findByCode(CODE(123)).id
		hmisEquipmentTypeController.params.equipmentType = EquipmentType.findByCode(CODE(124)).id
		hmisEquipmentTypeController.save()
		hmisEquipmentType = HmisEquipmentType.findByCode(CODE(123))
		then:
		HmisEquipmentType.list()[0].code.equals(CODE(123))
		HmisEquipmentType.list().size() == 1

		grailsApplication.config.i18nFields.locales.each{
			hmisEquipmentType.getNames(new Locale("$it")).equals("X-Ray Film Cutter $it")
		}
	}
	
	def "list hmis equipment types"(){
		setup:
		def hmisEquipmentTypeController = new HmisEquipmentTypeController();
		Initializer.newHmisEquipmentType(CODE(123), ["en":"names equipment type one"])
		Initializer.newHmisEquipmentType(CODE(124), ["en":"names equipment type two"])
		Initializer.newHmisEquipmentType(CODE(125), ["en":"names equipment type three"])
		Initializer.newHmisEquipmentType(CODE(126), ["en":"names equipment type four"])
		
		when: "none ajax"
		hmisEquipmentTypeController.list()
		then:
		HmisEquipmentType.count() == 4
		hmisEquipmentTypeController.modelAndView.model.entities.size() == 4
		
		when: "with ajax"
		hmisEquipmentTypeController.request.makeAjaxRequest()
		hmisEquipmentTypeController.list()
		then:
		HmisEquipmentType.count() == 4
		hmisEquipmentTypeController.response.json.results[0].contains(HmisEquipmentType.findByCode(CODE(123)).code)
		hmisEquipmentTypeController.response.json.results[0].contains(HmisEquipmentType.findByCode(CODE(124)).code)
		hmisEquipmentTypeController.response.json.results[0].contains(HmisEquipmentType.findByCode(CODE(125)).code)
		hmisEquipmentTypeController.response.json.results[0].contains(HmisEquipmentType.findByCode(CODE(126)).code)
	}

			
	def "search hmis equipment types"(){
		setup:
		def hmisEquipmentTypeController = new HmisEquipmentTypeController();
		Initializer.newHmisEquipmentType(CODE(123), ["en":"names equipment type one"])
		Initializer.newHmisEquipmentType(CODE(124), ["en":"names equipment type two"])
		Initializer.newHmisEquipmentType(CODE(125), ["en":"names equipment type three"])
		Initializer.newHmisEquipmentType(CODE(126), ["en":"names equipment type four"])
		
		when: "none ajax fails"
		hmisEquipmentTypeController.params.q = "three"
		hmisEquipmentTypeController.search()
		then:
		HmisEquipmentType.count() == 4
		hmisEquipmentTypeController.modelAndView.model.entities == [HmisEquipmentType.findByCode(CODE(125))]
		
		when: "with ajax"
		hmisEquipmentTypeController.params.q = "three"
		hmisEquipmentTypeController.request.makeAjaxRequest()
		hmisEquipmentTypeController.search()
		then:
		HmisEquipmentType.count() == 4
		!hmisEquipmentTypeController.response.json.results[0].contains(HmisEquipmentType.findByCode(CODE(123)).code)
		!hmisEquipmentTypeController.response.json.results[0].contains(HmisEquipmentType.findByCode(CODE(124)).code)
		hmisEquipmentTypeController.response.json.results[0].contains(HmisEquipmentType.findByCode(CODE(125)).code)
		!hmisEquipmentTypeController.response.json.results[0].contains(HmisEquipmentType.findByCode(CODE(126)).code)
	}
}