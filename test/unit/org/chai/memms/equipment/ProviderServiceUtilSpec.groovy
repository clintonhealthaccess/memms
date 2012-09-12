package org.chai.memms.equipment

import org.chai.memms.equipment.Provider.Type;

import grails.plugin.spock.UnitSpec;


class ProviderServiceUtilSpec extends UnitSpec {

	def "test enum matcher"() {
		
		expect:
		ProviderService.getEnumeMatcher("both") == [Type.BOTH]
		ProviderService.getEnumeMatcher("manufact") == [Type.MANUFACTURER]
		ProviderService.getEnumeMatcher("suppl") == [Type.SUPPLIER]
		ProviderService.getEnumeMatcher("") == []
		
	}
	
}
