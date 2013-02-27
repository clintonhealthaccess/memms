package org.chai.memms.inventory

import org.chai.memms.Initializer;
import org.chai.memms.IntegrationTests
import org.chai.memms.inventory.EquipmentType.Observation;
import org.chai.memms.inventory.Provider.Type;
import org.chai.memms.inventory.Provider;

class ProviderServiceSpec extends IntegrationTests{
	
	def providerService	
	
	def "test search provider based on type as text"(){
		
		setup:
		
	
		def manufactureContact = Initializer.newContact(['en':'Manufacture Address Descriptions One'],"Manufacture Nokia","jkl@yahoo.com","0768-889-787","Street 154","8988")
		def supplierContact = Initializer.newContact(['en':'Supplier Address Descriptions Two'],"Supplier Siemens","jk2@yahoo.com","0768-432-787","Street 112654","89388")
		def othersContact = Initializer.newContact(['en':'Unknown Address Descriptions Two'],"Unknown Other","jk1@yahoo.com","0768-111-787","Street 12","89288")
		def manufacturer = new Provider(code:CODE(123), type: Type.MANUFACTURER, contact:manufactureContact)
		manufacturer.save(failOnError: true)
		def supplier = new Provider(code:CODE(124), type: Type.SUPPLIER, contact:supplierContact)
		supplier.save(failOnError: true)
		def both = new Provider(code:CODE(125), type: Type.BOTH, contact:othersContact)
		both.save()
		def servicePro = new Provider(code:CODE(126), type: Type.SERVICEPROVIDER, contact:othersContact)
		servicePro.save()
		List<Provider> providers
		
		when:"test search provider based on type as text not follwed by a space" 
		providers = providerService.searchProvider(null,"Manu",["":""])
		
		then:
		Provider.count()==4
		providers.size()==1
		providers[0].code.equals("CODE123")
		
		when:
		providers = providerService.searchProvider(null,"Manu  ",["":""])
		
		then:"test search provider based on type as text followed by a space "
		Provider.count()==4
		providers.size()==1
		providers[0].code.equals("CODE123")
	}
	
	def "test search provider based on type as object"(){
		
		setup:
		def manufactureContact = Initializer.newContact(['en':'Manufacture Address Descriptions One'],"Manufacture Nokia","jkl@yahoo.com","0768-889-787","Street 154","8988")
		def supplierContact = Initializer.newContact(['en':'Supplier Address Descriptions Two'],"Supplier Siemens","jk2@yahoo.com","0768-432-787","Street 112654","89388")
		def othersContact = Initializer.newContact(['en':'Unknown Address Descriptions Two'],"Unknown Other","jk1@yahoo.com","0768-111-787","Street 12","89288")
		def manufacturer = new Provider(code:CODE(123), type: Type.MANUFACTURER, contact:manufactureContact)
		manufacturer.save(failOnError: true)
		def supplier = new Provider(code:CODE(124), type: Type.SUPPLIER, contact:supplierContact)
		supplier.save(failOnError: true)
		def both = new Provider(code:CODE(125), type: Type.BOTH, contact:othersContact)
		both.save()
		def servicePro = new Provider(code:CODE(126), type: Type.SERVICEPROVIDER, contact:othersContact)
		servicePro.save()
		when:
		List<Provider> providers = providerService.searchProvider(Type.SERVICEPROVIDER,"",["":""])
		
		then:
		Provider.count()==4
		providers.size()==1
		providers[0].code.equals("CODE126")
	}
	
	def "test search provider"(){
		
		setup:
		def manufactureContact = Initializer.newContact(['en':'Manufacture Address Descriptions One'],"Manufacture Nokia","jkl@yahoo.com","0768-889-787","Street 154","8988")
		def supplierContact = Initializer.newContact(['en':'Supplier Address Descriptions Two'],"Supplier Siemens","jk2@yahoo.com","0768-432-787","Street 112654","89388")
		def othersContact = Initializer.newContact(['en':'Unknown Address Descriptions Two'],"Unknown Other","jk1@yahoo.com","0768-111-787","Street 12","89288")
		def manufacturer = new Provider(code:CODE(123), type: Type.MANUFACTURER, contact:manufactureContact)
		manufacturer.save(failOnError: true)
		def supplier = new Provider(code:CODE(124), type: Type.SUPPLIER, contact:supplierContact)
		supplier.save(failOnError: true)
		def both = new Provider(code:CODE(125), type: Type.BOTH, contact:othersContact)
		both.save()
		def servicePro = new Provider(code:CODE(126), type: Type.SERVICEPROVIDER, contact:othersContact)
		servicePro.save()
		when:
		List<Provider> providers = providerService.searchProvider(null,"address",["":""])
		
		then:
		Provider.count()==4
		providers.size()==4
		providers.equals([servicePro,both,supplier,manufacturer])
	}


	
}
