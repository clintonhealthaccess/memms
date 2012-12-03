package org.chai.memms.inventory

import org.chai.memms.Initializer;
import org.chai.memms.IntegrationTests;
import org.chai.memms.inventory.EquipmentTypeController;
import org.chai.memms.inventory.EquipmentType.Observation;

class EquipmentTypeControllerSpec extends IntegrationTests{

	def equipmentTypeController
	def equipmentTypeService

	def "can create and save an equipment type with default values - all locales"(){
		setup:
		equipmentTypeController = new EquipmentTypeController()
		def equipmentType
		when:

		equipmentTypeController.params.code = CODE(123)
		
		grailsApplication.config.i18nFields.locales.each{
			equipmentTypeController.params."names_$it" = "X-Ray Film Cutter $it"
			equipmentTypeController.params."descriptions_$it" = "some kind of description $it"
		}
		equipmentTypeController.params.observation = "USEDINMEMMS"
		equipmentTypeController.params.addedOn = new Date()
		equipmentTypeController.params.lastModifiedOn = new Date()
		equipmentTypeController.params.expectedLifeTime = "struct"
		equipmentTypeController.params.expectedLifeTime_years = "1"
		equipmentTypeController.params.expectedLifeTime_months = "3"
		equipmentTypeController.save()
		
		equipmentType = equipmentTypeService.searchEquipmentType(CODE(123),null,[:])[0]

		then:

		equipmentType.code.equals(CODE(123))
		equipmentType.expectedLifeTime.numberOfMonths == 15

		grailsApplication.config.i18nFields.locales.each{
			equipmentType.getNames(new Locale("$it")).equals("X-Ray Film Cutter $it")
			equipmentType.getDescriptions(new Locale("$it")).equals("some kind of description $it")
		}
	}
	
	def "list equipment types"(){
		setup:
		equipmentTypeController = new EquipmentTypeController();
		Initializer.newEquipmentType(CODE(123), ["en":"names equipment type one"], ["en":"descriptions equipment type one"],Observation.RETIRED ,  Initializer.now(), Initializer.now())
		Initializer.newEquipmentType(CODE(124), ["en":"names equipment type two"], ["en":"descriptions equipment type two"],Observation.NOTINSCOPE ,  Initializer.now(), Initializer.now())
		Initializer.newEquipmentType(CODE(125), ["en":"names equipment type three"], ["en":"descriptions equipment type three"],Observation.TOODETAILED ,  Initializer.now(), Initializer.now())
		Initializer.newEquipmentType(CODE(126), ["en":"names equipment type four"], ["en":"descriptions equipment type four"],Observation.USEDINMEMMS ,  Initializer.now(), Initializer.now())
		
		when: "none ajax"
		equipmentTypeController.list()
		then:
		EquipmentType.count() == 4
		equipmentTypeController.modelAndView.model.entities.size() == 4
		
		when: "with ajax"
		equipmentTypeController.request.makeAjaxRequest()
		equipmentTypeController.list()
		then:
		EquipmentType.count() == 4
		equipmentTypeController.response.json.results[0].contains(EquipmentType.findByCode(CODE(123)).code)
		equipmentTypeController.response.json.results[0].contains(EquipmentType.findByCode(CODE(124)).code)
		equipmentTypeController.response.json.results[0].contains(EquipmentType.findByCode(CODE(125)).code)
		equipmentTypeController.response.json.results[0].contains(EquipmentType.findByCode(CODE(126)).code)
	}
	
	def "search equipment types"(){
		setup:
		equipmentTypeController = new EquipmentTypeController();
		Initializer.newEquipmentType(CODE(123), ["en":"names equipment type one"], ["en":"descriptions equipment type one"],Observation.RETIRED ,  Initializer.now(), Initializer.now())
		Initializer.newEquipmentType(CODE(124), ["en":"names equipment type two"], ["en":"descriptions equipment type two"],Observation.NOTINSCOPE ,  Initializer.now(), Initializer.now())
		Initializer.newEquipmentType(CODE(125), ["en":"names equipment type three"], ["en":"descriptions equipment type three"],Observation.TOODETAILED ,  Initializer.now(), Initializer.now())
		Initializer.newEquipmentType(CODE(126), ["en":"names equipment type four"], ["en":"descriptions equipment type four"],Observation.USEDINMEMMS ,  Initializer.now(), Initializer.now())
		
		when: "none ajax fails"
		equipmentTypeController.params.q = "three"
		equipmentTypeController.search()
		then:
		EquipmentType.count() == 4
		equipmentTypeController.response.status == 404
		
		when: "with ajax"
		equipmentTypeController.params.q = "three"
		equipmentTypeController.request.makeAjaxRequest()
		equipmentTypeController.search()
		then:
		EquipmentType.count() == 4
		!equipmentTypeController.response.json.results[0].contains(EquipmentType.findByCode(CODE(123)).code)
		!equipmentTypeController.response.json.results[0].contains(EquipmentType.findByCode(CODE(124)).code)
		equipmentTypeController.response.json.results[0].contains(EquipmentType.findByCode(CODE(125)).code)
		!equipmentTypeController.response.json.results[0].contains(EquipmentType.findByCode(CODE(126)).code)
	}
	
	//TODO find a way to test if the export works by calling the task plugin. Task plugin is supposed to have it's own tests
}