package org.chai.memms.equipment

import org.chai.memms.equipment.EquipmentType.Observation;
import grails.plugin.spock.UnitSpec;


class EquipmentTypeServiceUtilSpec extends UnitSpec {

	def "test enum matcher"() {
		
		expect:
		EquipmentTypeService.getEnumeMatcher("retired") == [Observation.RETIRED]
		EquipmentTypeService.getEnumeMatcher("used") == [Observation.USEDINMEMMS]
		EquipmentTypeService.getEnumeMatcher("detailed") == [Observation.TOODETAILED]
		EquipmentTypeService.getEnumeMatcher("") == []
		
	}
	
}
