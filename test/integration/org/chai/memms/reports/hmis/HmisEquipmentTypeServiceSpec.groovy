package org.chai.memms.reports.hmis

import org.chai.memms.Initializer;
import org.chai.memms.IntegrationTests;
import org.chai.memms.inventory.EquipmentType;
import org.chai.memms.reports.hmis.HmisEquipmentType;

class HmisEquipmentTypeServiceSpec extends IntegrationTests{
	
	def hmisEquipmentTypeService
	
	
	def "can search an hmis equipment type by code"() {
		setup:
		Initializer.newHmisEquipmentType(CODE(123),["en":"testName","en":"testObservations"])
		when:
		List<HmisEquipmentType> hmisEquipmentTypes = hmisEquipmentTypeService.searchHmisEquipmentType(CODE(123), ["order":"desc"])
		then:
		hmisEquipmentTypes.size() == 1
		hmisEquipmentTypes[0].code.equals(CODE(123))
	}
	
	def "can search an hmis equipment type by name"() {
		setup:
		Initializer.newHmisEquipmentType(CODE(123),["en":"test Name","en":"testObservations"])
		when:
		List<HmisEquipmentType> hmisEquipmentTypes = hmisEquipmentTypeService.searchHmisEquipmentType(CODE(123), ["order":"desc"])
		then:
		hmisEquipmentTypes.size() == 1
		hmisEquipmentTypes[0].code.equals(CODE(123))
	}
}