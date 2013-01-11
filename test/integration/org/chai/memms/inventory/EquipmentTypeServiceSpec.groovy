package org.chai.memms.inventory

import org.chai.memms.Initializer;
import org.chai.memms.IntegrationTests
import org.chai.memms.inventory.EquipmentType.Observation;
import org.chai.memms.inventory.EquipmentType;

class EquipmentTypeServiceSpec extends IntegrationTests{
	
	def equipmentTypeService
	
	def "can search an equipment type by code"() {
		setup:
		def observation = Observation.USEDINMEMMS;
		Initializer.newEquipmentType(CODE(123),["en":"testName"],["en":"testObservations"],observation,Initializer.now())
		when:
		List<EquipmentType> equipmentTypes = equipmentTypeService.searchEquipmentType(CODE(123),null,[:])
		then:
		equipmentTypes.size() == 1
		equipmentTypes[0].code.equals(CODE(123))
	}
	
	def "can search an equipment type by name"() {
		setup:
		def observation = Observation.USEDINMEMMS;
		Initializer.newEquipmentType(CODE(123),["en":"test Name"],["en":"testObservations"],observation,Initializer.now())
		when:
		List<EquipmentType> equipmentTypes = equipmentTypeService.searchEquipmentType("Na",null,[:])
		then:
		equipmentTypes.size() == 1
		equipmentTypes[0].code.equals(CODE(123))
	}
	
	def "can search an equipment type by observation"() {
		setup:
		def observation = Observation.USEDINMEMMS;
		Initializer.newEquipmentType(CODE(123),["en":"test Name"],["en":"testObservations"],observation,Initializer.now())
		when:
		List<EquipmentType> equipmentTypes = equipmentTypeService.searchEquipmentType("used",null,[:])
		then:
		equipmentTypes.size() == 1
		equipmentTypes[0].code.equals(CODE(123))
	}
	
	def "can search an equipment type by specified observation"() {
		setup:
		Initializer.newEquipmentType(CODE(123),["en":"retired equipment"],["en":"retired equipment Type"],Observation.RETIRED,Initializer.now())
		Initializer.newEquipmentType(CODE(1234),["en":"test Name"],["en":"testObservations"],Observation.USEDINMEMMS,Initializer.now())
		when:
		List<EquipmentType> equipmentTypes = EquipmentType.list()
		List<EquipmentType> searchEquipmentTypes = equipmentTypeService.searchEquipmentType("test",Observation.USEDINMEMMS,[:])
		then:
		equipmentTypes.size() == 2
		searchEquipmentTypes.size() == 1
		searchEquipmentTypes[0].code.equals(CODE(1234))
	}
	
	def "can search an equipment type by specified observation only"() {
		setup:
		Initializer.newEquipmentType(CODE(123),["en":"retired equipment"],["en":"retired equipment Type"],Observation.RETIRED,Initializer.now())
		Initializer.newEquipmentType(CODE(1234),["en":"test Name"],["en":"testObservations"],Observation.USEDINMEMMS,Initializer.now())
		when:
		List<EquipmentType> equipmentTypes = EquipmentType.list()
		List<EquipmentType> searchEquipmentTypes = equipmentTypeService.searchEquipmentType("",Observation.RETIRED,[:])
		then:
		equipmentTypes.size() == 2
		searchEquipmentTypes.size() == 1
		searchEquipmentTypes[0].code.equals(CODE(123))
	}
}