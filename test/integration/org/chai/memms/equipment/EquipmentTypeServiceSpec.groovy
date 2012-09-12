package org.chai.memms.equipment

import org.chai.memms.Initializer;
import org.chai.memms.IntegrationTests
import org.chai.memms.equipment.EquipmentType.Observation;
import org.chai.memms.equipment.EquipmentType

class EquipmentTypeServiceSpec extends IntegrationTests{
	
	def equipmentTypeService
	
	def "can search an equipment type by code"() {
		setup:
		def observation = Observation.USEDINMEMMS;
		Initializer.newEquipmentType(CODE(123),["en":"testName"],["en":"testObservations"],observation,Initializer.now(),Initializer.now())
		when:
		List<EquipmentType> equipmentTypes = equipmentTypeService.searchEquipmentType(CODE(123),["":""])
		then:
		equipmentTypes.size() == 1
		equipmentTypes[0].code.equals(CODE(123))
	}
	
	def "can search an equipment type by name"() {
		setup:
		def observation = Observation.USEDINMEMMS;
		Initializer.newEquipmentType(CODE(123),["en":"test Name"],["en":"testObservations"],observation,Initializer.now(),Initializer.now())
		when:
		List<EquipmentType> equipmentTypes = equipmentTypeService.searchEquipmentType("Na",["":""])
		then:
		equipmentTypes.size() == 1
		equipmentTypes[0].code.equals(CODE(123))
	}
	
	def "can search an equipment type by observation"() {
		setup:
		def observation = Observation.USEDINMEMMS;
		Initializer.newEquipmentType(CODE(123),["en":"test Name"],["en":"testObservations"],observation,Initializer.now(),Initializer.now())
		when:
		List<EquipmentType> equipmentTypes = equipmentTypeService.searchEquipmentType("used",["":""])
		then:
		equipmentTypes.size() == 1
		equipmentTypes[0].code.equals(CODE(123))
	}
}