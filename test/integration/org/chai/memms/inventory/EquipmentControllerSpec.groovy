package org.chai.memms.inventory

import org.chai.memms.IntegrationTests;
import org.chai.memms.Initializer;
import org.chai.memms.inventory.EquipmentController;
import org.chai.memms.inventory.EquipmentViewController;
import org.chai.memms.inventory.FilterCommand;
import org.chai.memms.inventory.Equipment.Donor;
import org.chai.memms.inventory.Equipment.PurchasedBy;
import org.chai.memms.inventory.EquipmentStatus.Status;
import org.chai.memms.inventory.EquipmentType.Observation;
import org.chai.location.DataLocation;
import org.chai.memms.security.User;
import org.chai.memms.inventory.Provider.Type;
import org.chai.memms.inventory.Equipment;

class EquipmentControllerSpec extends IntegrationTests{

	def equipmentController
	
	def "create equipment with correct required data in fields - for english input"(){
		
		setup:
		setupLocationTree()
		setupSystemUser()
		
		def department = Initializer.newDepartment(['en':"testName"], CODE(123),['en':"testDescription"])
		def equipmentType = Initializer.newEquipmentType(CODE(15810),["en":"Accelerometers"],["en":"used in memms"],Observation.USEDINMEMMS,Initializer.now())
		
		def manufactureContact = Initializer.newContact(['en':'Address Descriptions '],"Manufacture","jkl@yahoo.com","0768-889-787","Street 154","6353")
		def supplierContact = Initializer.newContact(['en':'Address Descriptions '],"Supplier","jk@yahoo.com","0768-888-787","Street 1654","6353")
		def serviceProvider = Initializer.newContact(['en':'Address Descriptions '],"service Provider","jk@yahoo.com","0768-888-787","Street 1654","6353")
		
		def manufacture = Initializer.newProvider(CODE(123), Type.MANUFACTURER,manufactureContact)
		def supplier = Initializer.newProvider(CODE(124), Type.SUPPLIER,supplierContact)
		def servicePr = Initializer.newProvider(CODE(125), Type.SERVICEPROVIDER,serviceProvider)
	
		
		equipmentController = new EquipmentController();
		when:
		equipmentController.params.serialNumber = 'SERIAL12129'
		equipmentController.params.purchaseCost = "32000"
		equipmentController.params.currency = "USD"
		equipmentController.params.model = "model one"
		equipmentController.params.room = "ROOM A1"
		equipmentController.params.purchaser = PurchasedBy.BYFACILITY
		equipmentController.params.obsolete = false
		equipmentController.params.descriptions_en = "test_english_descriptions"
		equipmentController.params.manufactureDate = Initializer.getDate(1,1,2012)
		equipmentController.params.purchaseDate = Initializer.getDate(2,1,2012)
		equipmentController.params.department = department
		equipmentController.params.type = equipmentType
		
		equipmentController.params."warranty.startDate" = Initializer.getDate(10,1,2012)
		equipmentController.params."warranty.sameAsSupplier" = true
		equipmentController.params."warranty.descriptions_en" = "new warranty for testing"
		equipmentController.params.warrantyPeriod = "struct"
		equipmentController.params.warrantyPeriod_years = "1"
		equipmentController.params.warrantyPeriod_months = "4"
		
		equipmentController.params."serviceProvider.id" = servicePr.id
		equipmentController.params.serviceContractStartDate = Initializer.getDate(2,1,2012)
		equipmentController.params.serviceContractPeriod = "struct"
		equipmentController.params.serviceContractPeriod_years = ""
		equipmentController.params.serviceContractPeriod_months = "3"
		
		equipmentController.params."manufacturer.id" = manufacture.id
		equipmentController.params."supplier.id" = supplier.id
		equipmentController.params.expectedLifeTime = "struct"
		equipmentController.params.expectedLifeTime_years = "1"
		equipmentController.params.expectedLifeTime_months = "3"
		equipmentController.params.dataLocation = DataLocation.list().first()
		equipmentController.params.status="INSTOCK"
		equipmentController.params.dateOfEvent=Initializer.now()
		equipmentController.save()
				
		then:
		Equipment.count() == 1;
		Equipment.findBySerialNumber("SERIAL12129").serialNumber.equals("SERIAL12129")
		Equipment.findBySerialNumber("SERIAL12129").expectedLifeTime.numberOfMonths == 15
		Equipment.findBySerialNumber("SERIAL12129").warrantyPeriod.numberOfMonths == 16
		Equipment.findBySerialNumber("SERIAL12129").serviceContractPeriod.numberOfMonths == 3
		Equipment.findByDescriptions_en("test_english_descriptions").getDescriptions(new Locale("en")).equals("test_english_descriptions")
	}
	
	def "create equipment with correct required data in fields - for all locale"(){
		
		setup:
		setupLocationTree()
		setupSystemUser()
		
		def manufactureContact = Initializer.newContact(['en':'Address Descriptions '],"Manufacture","jkl@yahoo.com","0768-889-787","Street 154","6353")
		def supplierContact = Initializer.newContact(['en':'Address Descriptions '],"Supplier","jk@yahoo.com","0768-888-787","Street 1654","6353")
		def serviceProvider = Initializer.newContact(['en':'Address Descriptions '],"service Provider","jk@yahoo.com","0768-888-787","Street 1654","6353")
		
		
		def manufacture = Initializer.newProvider(CODE(123), Type.MANUFACTURER,manufactureContact)
		def supplier = Initializer.newProvider(CODE(124), Type.SUPPLIER,supplierContact)
		def servicePr = Initializer.newProvider(CODE(125), Type.SERVICEPROVIDER,serviceProvider)
		
		def department = Initializer.newDepartment(['en':"testName"], CODE(123),['en':"testDescription"])
		def equipmentType = Initializer.newEquipmentType(CODE(15810),["en":"Accelerometers"],["en":"used in memms"],Observation.USEDINMEMMS,Initializer.now())
		
		equipmentController = new EquipmentController();
		when:
		equipmentController.params.serialNumber = 'SERIAL129'
		equipmentController.params.purchaseCost = ""
		equipmentController.params.currency = ""
		equipmentController.params.model = "model one"
		equipmentController.params.room = "ROOM A1"
		equipmentController.params.purchaser = PurchasedBy.BYFACILITY
		equipmentController.params.obsolete = false
		
		equipmentController.params."warranty.startDate" = Initializer.getDate(1,1,2012)
		equipmentController.params."warranty.sameAsSupplier" = true
		equipmentController.params.warrantyPeriod = "struct"
		equipmentController.params.warrantyPeriod_years = "1"
		equipmentController.params.warrantyPeriod_months = "4"
		grailsApplication.config.i18nFields.locales.each{
			equipmentController.params."warranty.descriptions_$it" = "new warranty for testing $it"
		}
		
		equipmentController.params."serviceProvider.id" = servicePr.id
		equipmentController.params.serviceContractStartDate = Initializer.getDate(1,1,2012)
		equipmentController.params.serviceContractPeriod = "struct"
		equipmentController.params.serviceContractPeriod_years = "2"
		equipmentController.params.serviceContractPeriod_months = "3"
	
		grailsApplication.config.i18nFields.locales.each{
			equipmentController.params."descriptions_$it" = "test descriptions $it"
		}
		equipmentController.params.manufactureDate = Initializer.getDate(1,1,2012)
		equipmentController.params.purchaseDate = Initializer.getDate(1,1,2012)
		equipmentController.params.department = department
		equipmentController.params.type = equipmentType
		equipmentController.params.expectedLifeTime = "struct"
		equipmentController.params.expectedLifeTime_years = "1"
		equipmentController.params.expectedLifeTime_months = "3"
		equipmentController.params."manufacturer.id" = manufacture.id
		equipmentController.params."supplier.id" = supplier.id
		equipmentController.params.dataLocation = DataLocation.list().first()
		equipmentController.params.status="FORDISPOSAL"
		equipmentController.params.dateOfEvent=Initializer.now()
		equipmentController.save()
		
		then:
		Equipment.count() == 1;
		Equipment.findBySerialNumber("SERIAL129").serialNumber.equals("SERIAL129")
		Equipment.findBySerialNumber("SERIAL129").expectedLifeTime.numberOfMonths == 15
		Equipment.findBySerialNumber("SERIAL129").warrantyPeriod.numberOfMonths == 16
		Equipment.findBySerialNumber("SERIAL129").serviceContractPeriod.numberOfMonths == 27
		grailsApplication.config.i18nFields.locales.each{
			Equipment."findByDescriptions_$it"("test descriptions $it").getDescriptions(new Locale("$it")).equals("test descriptions $it")
		}
	}
}
