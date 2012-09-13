package org.chai.memms.equipment

import java.util.Date;

import org.apache.commons.el.parser.Token;
import org.chai.memms.Contact;
import org.chai.memms.Initializer;
import org.chai.memms.IntegrationTests

import org.chai.memms.equipment.EquipmentType.Observation;
import org.chai.location.DataLocation;

class EquipmentModelSpec extends IntegrationTests {
	
    def "can create and save an equipment model"() {
		when:
		def equipmentModelOne = Initializer.newEquipmentModel(['en':"testName"], CODE(123),['en':"testDescription"])
		def equipmentModelTwo = new EquipmentModel(code:CODE(12),descriptions:['en':"testDescription"],names:['en':"testNames"])
		equipmentModelTwo.save(failOnError: true)
		then:
		EquipmentModel.count() == 2
	}
	
	def "can't create and save an equipment model without the code"() {
		when:
		def equipmentModelOne= Initializer.newEquipmentModel(['en':"testName"], CODE(123),['en':"testDescription"])
		def equipmentModelTwo = new EquipmentModel(descriptions:['en':"testDescription"],names:['en':"testNames"])
		equipmentModelTwo.save()
		then:
		EquipmentModel.count() == 1
		equipmentModelTwo.errors.hasFieldErrors('code') == true
	}
	
	def "can't create and save an equipment model without duplicated code"() {
		when:
		def equipmentModelOne= Initializer.newEquipmentModel(['en':"testName"], CODE(123),['en':"testDescription"])
		def equipmentModelTwo = new EquipmentModel(code:CODE(123),descriptions:['en':"testDescription"],names:['en':"testNames"])
		equipmentModelTwo.save()
		then:
		EquipmentModel.count() == 1
		equipmentModelTwo.errors.hasFieldErrors('code') == true
	}
}
