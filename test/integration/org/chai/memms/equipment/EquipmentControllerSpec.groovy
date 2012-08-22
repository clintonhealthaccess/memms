package org.chai.memms.equipment

import org.chai.memms.IntegrationTests;
import org.chai.memms.Initializer;
import org.chai.memms.equipment.EquipmentType.Observation;
import org.chai.memms.location.DataLocation;
import org.chai.memms.security.User;
import org.chai.memms.equipment.Provider.Type;

class EquipmentControllerSpec extends IntegrationTests{

	def equipmentController
	
	def "create equipment with correct required data in fields - for english input"(){
		
		setup:
		setupLocationTree()
		setupSystemUse()
		
		def department = Initializer.newDepartment(['en':"testName"], CODE(123),['en':"testDescription"])
		def equipmentModel = Initializer.newEquipmentModel(['en':"testName"], CODE(123),['en':"testDescription"])
		def equipmentType = Initializer.newEquipmentType(CODE(15810),["en":"Accelerometers"],["en":"used in memms"],Observation.USEDINMEMMS,Initializer.now(),Initializer.now())
		
		def manufactureContact = Initializer.newContact(['en':'Address Descriptions '],"Manufacture","jkl@yahoo.com","0768-889-787","Street 154","6353")
		def supplierContact = Initializer.newContact(['en':'Address Descriptions '],"Supplier","jk@yahoo.com","0768-888-787","Street 1654","6353")
		
		def manufacture = Initializer.newProvider(CODE(123), Type.MANUFACTURE,manufactureContact)
		def supplier = Initializer.newProvider(CODE(124), Type.SUPPLIER,supplierContact)
		log.debug("-------Supplier created is: " + supplier.id + "\n\r")
	
		
		equipmentController = new EquipmentController();
		when:
		equipmentController.params.serialNumber = 'SERIAL12129'
		equipmentController.params.purchaseCost = "32000"
		equipmentController.params.model = "model one"
		equipmentController.params.room = "ROOM A1"
		equipmentController.params.expectedLifeTime = 32
		equipmentController.params.donation = true
		equipmentController.params.obsolete = false
		equipmentController.params.descriptions_en = "test_english_descriptions"
		equipmentController.params.manufactureDate = Initializer.getDate(1,1,2012)
		equipmentController.params.purchaseDate = Initializer.getDate(1,1,2012)
		equipmentController.params.registeredOn = Initializer.now()
		equipmentController.params.department = department
		equipmentController.params.type = equipmentType
		equipmentController.params.manufacture = manufacture
		equipmentController.params.supplier = supplier
		equipmentController.params.dataLocation = DataLocation.list().first()
		equipmentController.params.status="DISPOSED"
		equipmentController.params.dateOfEvent=Initializer.now()
		equipmentController.save(failOnError: true)
		
		then:
		Equipment.count() == 1;
		Equipment.findBySerialNumber("SERIAL12129").serialNumber.equals("SERIAL12129")
		Equipment.findByDescriptions_en("test_english_descriptions").getDescriptions(new Locale("en")).equals("test_english_descriptions")
	}
	
	def "create equipment with correct required data in fields - for all locale"(){
		
		setup:
		setupLocationTree()
		setupSystemUse()
		
		def manufactureContact = Initializer.newContact(['en':'Address Descriptions '],"Manufacture","jkl@yahoo.com","0768-889-787","Street 154","6353")
		def supplierContact = Initializer.newContact(['en':'Address Descriptions '],"Supplier","jk@yahoo.com","0768-888-787","Street 1654","6353")
		
		def manufacture = Initializer.newProvider(CODE(123), Type.MANUFACTURE,manufactureContact)
		def supplier = Initializer.newProvider(CODE(124), Type.SUPPLIER,supplierContact)
		def department = Initializer.newDepartment(['en':"testName"], CODE(123),['en':"testDescription"])
		def equipmentModel = Initializer.newEquipmentModel(['en':"testName"], CODE(123),['en':"testDescription"])
		def equipmentType = Initializer.newEquipmentType(CODE(15810),["en":"Accelerometers"],["en":"used in memms"],Observation.USEDINMEMMS,Initializer.now(),Initializer.now())
		
		equipmentController = new EquipmentController();
		when:
		equipmentController.params.serialNumber = 'SERIAL129'
		equipmentController.params.purchaseCost = "32000"
		equipmentController.params.model = "model one"
		equipmentController.params.room = "ROOM A1"
		equipmentController.params.expectedLifeTime = 32
		equipmentController.params.donation = true
		equipmentController.params.obsolete = false
		grailsApplication.config.i18nFields.locales.each{
			equipmentController.params."descriptions_$it" = "test descriptions $it"
		}
		equipmentController.params.manufactureDate = Initializer.getDate(1,1,2012)
		equipmentController.params.purchaseDate = Initializer.getDate(1,1,2012)
		equipmentController.params.registeredOn = Initializer.getDate(1,1,2012)
		equipmentController.params.department = department
		equipmentController.params.model = "equipmentModel"
		equipmentController.params.type = equipmentType
		equipmentController.params.manufacture = manufacture
		equipmentController.params.supplier = supplier
		equipmentController.params.dataLocation = DataLocation.list().first()
		equipmentController.params.status="FORDISPOSAL"
		equipmentController.params.dateOfEvent=Initializer.now()
		equipmentController.save()
		
		then:
		Equipment.count() == 1;
		Equipment.findBySerialNumber("SERIAL129").serialNumber.equals("SERIAL129")
		grailsApplication.config.i18nFields.locales.each{
			Equipment."findByDescriptions_$it"("test descriptions $it").getDescriptions(new Locale("$it")).equals("test descriptions $it")
		}
	}
}
