package org.chai.memms.inventory

import org.chai.memms.Initializer;
import org.chai.memms.IntegrationTests
import org.chai.memms.inventory.EquipmentType.Observation;
import org.chai.memms.inventory.EquipmentType;

class EquipmentTypeSpec extends IntegrationTests{

    def "can create and save an equipment type"() {
		setup:
		def observation = Observation.USEDINMEMMS;
		when:
		Initializer.newEquipmentType(CODE(123),["en":"testName"],["en":"testObservations"],observation,Initializer.now())
		def equipmentType = new EquipmentType(code:CODE(122),names:["en":"Accelerometers"],descriptions:["en":"used in memms"],observation:observation,lastModifiedOn:Initializer.now())
		equipmentType.save(failOnError: true)
		then:
		EquipmentType.count() == 2
	}
	
	def "can't create and save an equipment type without a code"() {
		setup:
		def observation = Observation.USEDINMEMMS;
		Initializer.newEquipmentType(CODE(123), ["en":"Accelerometers"],["en":"used in memms"],observation,Initializer.now())
		when:
		def equipmentType = new EquipmentType(names:["en":"Accelerometers"],descriptions:["en":"used in memms"],observation:observation,lastModifiedOn:Initializer.now())
		equipmentType.save()
		then:
		EquipmentType.count() == 1
		equipmentType.errors.hasFieldErrors('code') == true
	}
	
	def "can't create and save an equipment type with a duplicate code"() {
		setup:
		def observation = Observation.USEDINMEMMS;
		Initializer.newEquipmentType(CODE(123), ["en":"Accelerometers"],["en":"used in memms"],observation,Initializer.now())
		when:
		def equipmentType = new EquipmentType(code:CODE(123),names:["en":"Accelerometers"],descriptions:["en":"used in memms"],observation:observation,lastModifiedOn:Initializer.now())
		equipmentType.save()
		then:
		EquipmentType.count() == 1
		equipmentType.errors.hasFieldErrors('code') == true
	}
}
