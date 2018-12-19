package org.chai.memms.inventory

import org.chai.memms.inventory.Provider.Type;
import org.chai.memms.inventory.ProviderService;

import grails.plugin.spock.UnitSpec;


class ProviderServiceUtilSpec extends UnitSpec {

	def "test enum matcher"() {
		
		expect:
		ProviderService.getEnumeMatcher("both") == [Type.BOTH]
		ProviderService.getEnumeMatcher("manufact") == [Type.MANUFACTURER]
		ProviderService.getEnumeMatcher("suppl") == [Type.SUPPLIER,Type.SUPPLIERANDSERVICEPROVIDER]
		ProviderService.getEnumeMatcher("") == []
		
	}
	
}
