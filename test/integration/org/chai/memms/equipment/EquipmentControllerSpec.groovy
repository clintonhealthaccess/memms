package org.chai.memms.equipment

import org.chai.memms.IntegrationTests;
import org.chai.memms.Initializer;
import org.chai.memms.equipment.Equipment.Donor;
import org.chai.memms.equipment.Equipment.PurchasedBy;
import org.chai.memms.equipment.EquipmentStatus.Status;
import org.chai.memms.equipment.EquipmentType.Observation;
import org.chai.location.DataLocation;
import org.chai.memms.security.User;
import org.chai.memms.equipment.Provider.Type;

class EquipmentControllerSpec extends IntegrationTests{

	def equipmentController
	
	def "create equipment with correct required data in fields - for english input"(){
		
		setup:
		setupLocationTree()
		setupSystemUser()
		
		def department = Initializer.newDepartment(['en':"testName"], CODE(123),['en':"testDescription"])
		def equipmentType = Initializer.newEquipmentType(CODE(15810),["en":"Accelerometers"],["en":"used in memms"],Observation.USEDINMEMMS,Initializer.now(),Initializer.now())
		
		def manufactureContact = Initializer.newContact(['en':'Address Descriptions '],"Manufacture","jkl@yahoo.com","0768-889-787","Street 154","6353")
		def supplierContact = Initializer.newContact(['en':'Address Descriptions '],"Supplier","jk@yahoo.com","0768-888-787","Street 1654","6353")
		
		def manufacture = Initializer.newProvider(CODE(123), Type.MANUFACTURER,manufactureContact)
		def supplier = Initializer.newProvider(CODE(124), Type.SUPPLIER,supplierContact)
	
		
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
		equipmentController.params.registeredOn = Initializer.now()
		equipmentController.params.department = department
		equipmentController.params.type = equipmentType
		
		equipmentController.params."warranty.startDate" = Initializer.getDate(1,1,2012)
		equipmentController.params."warranty.sameAsSupplier" = true
		equipmentController.params."warranty.descriptions_en" = "new warranty for testing"
		
		equipmentController.params.numberOfMonths_years = 1
		equipmentController.params.numberOfMonths_months = 3
		
		equipmentController.params.manufacturer = manufacture
		equipmentController.params.supplier = supplier
		equipmentController.params.expectedLifeTime_years = 1
		equipmentController.params.expectedLifeTime_months = 3
		equipmentController.params.dataLocation = DataLocation.list().first()
		equipmentController.params.status="DISPOSED"
		equipmentController.params.dateOfEvent=Initializer.now()
		equipmentController.save()
				
		then:
		Equipment.count() == 1;
		Equipment.findBySerialNumber("SERIAL12129").serialNumber.equals("SERIAL12129")
		Equipment.findBySerialNumber("SERIAL12129").expectedLifeTime == 15
		Equipment.findByDescriptions_en("test_english_descriptions").getDescriptions(new Locale("en")).equals("test_english_descriptions")
	}
	
	def "create equipment with correct required data in fields - for all locale"(){
		
		setup:
		setupLocationTree()
		setupSystemUser()
		
		def manufactureContact = Initializer.newContact(['en':'Address Descriptions '],"Manufacture","jkl@yahoo.com","0768-889-787","Street 154","6353")
		def supplierContact = Initializer.newContact(['en':'Address Descriptions '],"Supplier","jk@yahoo.com","0768-888-787","Street 1654","6353")
		
		def manufacture = Initializer.newProvider(CODE(123), Type.MANUFACTURER,manufactureContact)
		def supplier = Initializer.newProvider(CODE(124), Type.SUPPLIER,supplierContact)
		def department = Initializer.newDepartment(['en':"testName"], CODE(123),['en':"testDescription"])
		def equipmentType = Initializer.newEquipmentType(CODE(15810),["en":"Accelerometers"],["en":"used in memms"],Observation.USEDINMEMMS,Initializer.now(),Initializer.now())
		
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
		
		grailsApplication.config.i18nFields.locales.each{
			equipmentController.params."warranty.descriptions_$it" = "new warranty for testing $it"
		}
		
		equipmentController.params.numberOfMonths_years = 1
		equipmentController.params.numberOfMonths_months = 3
		
		grailsApplication.config.i18nFields.locales.each{
			equipmentController.params."descriptions_$it" = "test descriptions $it"
		}
		equipmentController.params.manufactureDate = Initializer.getDate(1,1,2012)
		equipmentController.params.purchaseDate = Initializer.getDate(1,1,2012)
		equipmentController.params.registeredOn = Initializer.getDate(1,1,2012)
		equipmentController.params.department = department
		equipmentController.params.type = equipmentType
		equipmentController.params.manufacturer = manufacture
		equipmentController.params.expectedLifeTime_years = 1
		equipmentController.params.expectedLifeTime_months = 3
		equipmentController.params.supplier = supplier
		equipmentController.params.dataLocation = DataLocation.list().first()
		equipmentController.params.status="FORDISPOSAL"
		equipmentController.params.dateOfEvent=Initializer.now()
		equipmentController.save()
		
		then:
		Equipment.count() == 1;
		Equipment.findBySerialNumber("SERIAL129").serialNumber.equals("SERIAL129")
		Equipment.findBySerialNumber("SERIAL129").expectedLifeTime == 15
		grailsApplication.config.i18nFields.locales.each{
			Equipment."findByDescriptions_$it"("test descriptions $it").getDescriptions(new Locale("$it")).equals("test descriptions $it")
		}
	}

	def "can list equipments by dataLocation"(){
		setup:
		setupLocationTree()

		def manufactureContact = Initializer.newContact(['en':'Address Descriptions '],"Manufacture","jkl@yahoo.com","0768-889-787","Street 154","6353")
		def supplierContact = Initializer.newContact([:],"Supplier","jk@yahoo.com","0768-888-787","Street 1654","6353")
		def manufacture = Initializer.newProvider(CODE(111), Type.MANUFACTURER,manufactureContact)
		def supplier = Initializer.newProvider(CODE(222), Type.SUPPLIER,supplierContact)
		
		def user  = newUser("admin", "Admin UID")
		def department = Initializer.newDepartment(['en':"testName"], CODE(123),['en':"testDescription"])
		def equipmentType = Initializer.newEquipmentType(CODE(15810),["en":"Accelerometers"],["en":"used in memms"],Observation.USEDINMEMMS,Initializer.now(),Initializer.now())

		def equipmentOne = Initializer.newEquipment("SERIAL10",PurchasedBy.BYFACILITY,null,null,false,32,"ROOM A1","2900.23",['en':'Equipment Descriptions one'],Initializer.getDate(22,07,2010)
				,Initializer.getDate(10,10,2010),"USD",Initializer.now(),"equipmentModel",DataLocation.findByCode('Kivuye HC'),department,equipmentType,manufacture,supplier)
		def equipmentTwo = Initializer.newEquipment("SERIAL11",PurchasedBy.BYFACILITY,null,null,true,32,"ROOM A1","2900.23",['en':'Equipment Descriptions two'],Initializer.getDate(22,07,2010)
				,Initializer.getDate(10,10,2010),"EUR",Initializer.now(),"equipmentModel",DataLocation.findByCode('Butaro DH'),department,equipmentType,manufacture,supplier)
		
		Initializer.newEquipment("SERIAL12",PurchasedBy.BYFACILITY,null,null,true,32,"ROOM A1","2900.23",['en':'Equipment Descriptions two'],Initializer.getDate(22,07,2010)
			,Initializer.getDate(10,10,2010),"RWF",Initializer.now(),"equipmentModel",DataLocation.findByCode('Butaro DH'),department,equipmentType,manufacture,supplier)
		Initializer.newEquipment("SERIAL13",PurchasedBy.BYFACILITY,null,null,true,32,"ROOM A1","2900.23",['en':'Equipment Descriptions two'],Initializer.getDate(22,07,2010)
			,Initializer.getDate(10,10,2010),"RWF",Initializer.now(),"equipmentModel",DataLocation.findByCode('Butaro DH'),department,equipmentType,manufacture,supplier)
	
		
		def List<Equipment> equipmentsOne, equipmentsTwo, equipmentsThree
		def equipmentStatusOneActive = Initializer.newEquipmentStatus(Initializer.now(),User.findByUsername("admin"),Status.INSTOCK,equipmentOne,false,[:])
		def equipmentStatusOneInActive = Initializer.newEquipmentStatus(Initializer.now(),User.findByUsername("admin"),Status.OPERATIONAL,equipmentOne,true,[:])
		def equipmentStatusTwo = Initializer.newEquipmentStatus(Initializer.now(),User.findByUsername("admin"),Status.DISPOSED,equipmentOne,true,[:])
		
		equipmentOne.addToStatus(equipmentStatusOneInActive).save(failOnError:true,flush: true)
		equipmentOne.addToStatus(equipmentStatusOneActive).save(failOnError:true,flush: true)
		equipmentTwo.addToStatus(equipmentStatusTwo).save(failOnError:true,flush: true)

		equipmentController = new EquipmentController();
		when:
		equipmentController.params.'dataLocation.id' = DataLocation.findByCode('Butaro DH').id
		equipmentController.list()
		
		then:
		equipmentController.modelAndView.model.entities.size() == 3
	}
	
	def "redirects to listing when accessing summary page by a user with a datalocation"(){
		setup:
		setupLocationTree()

		Initializer.createDummyStructure()
		Initializer.createUsers()
		setupSecurityManager(User.findByUsername('user'))//data location

		equipmentController = new EquipmentController();
		when:
		equipmentController.summaryPage()
		
		then:
		equipmentController.response.redirectedUrl == '/equipment/list?dataLocation.id=' + User.findByUsername('user').location.id
	}
	
	def "does not redirects to listing when accessing summary page by a user with a location"(){
		setup:
		setupLocationTree()

		Initializer.createDummyStructure()
		Initializer.createUsers()
		setupSecurityManager(User.findByUsername('user1'))

		equipmentController = new EquipmentController();
		when:
		equipmentController.summaryPage()
		
		then:
		equipmentController.response.redirectedUrl != '/equipment/list/' + User.findByUsername('user1').location.id
	}
	
	def "can filter equipments"(){
		setup:
		setupLocationTree()

		def manufactureContact = Initializer.newContact(['en':'Address Descriptions '],"Manufacture","jkl@yahoo.com","0768-889-787","Street 154","6353")
		def supplierContact = Initializer.newContact([:],"Supplier","jk@yahoo.com","0768-888-787","Street 1654","6353")
		def manufacture = Initializer.newProvider(CODE(111), Type.MANUFACTURER,manufactureContact)
		def supplier = Initializer.newProvider(CODE(222), Type.SUPPLIER,supplierContact)
		
		def user  = newUser("admin", "Admin UID")
		def department = Initializer.newDepartment(['en':"testName"], CODE(123),['en':"testDescription"])
		def equipmentType = Initializer.newEquipmentType(CODE(15810),["en":"Accelerometers"],["en":"used in memms"],Observation.USEDINMEMMS,Initializer.now(),Initializer.now())

		def equipmentOne = Initializer.newEquipment("SERIAL10",PurchasedBy.BYDONOR,Donor.MOHPARTNER,"CHAI",false,32,"ROOM A1","",['en':'Equipment Descriptions one'],Initializer.getDate(22,07,2010)
				,Initializer.getDate(10,10,2010),"",Initializer.now(),"equipmentModel",DataLocation.findByCode('Kivuye HC'),department,equipmentType,manufacture,supplier)
		def equipmentTwo = Initializer.newEquipment("SERIAL11",PurchasedBy.BYFACILITY,null,null,false,32,"ROOM A1","2900.23",['en':'Equipment Descriptions two'],Initializer.getDate(22,07,2010)
				,Initializer.getDate(10,10,2010),"EUR",Initializer.now(),"equipmentModel",DataLocation.findByCode('Kivuye HC'),department,equipmentType,manufacture,supplier)
		Initializer.newEquipment("SERIAL12",PurchasedBy.BYMOH,null,null,true,32,"ROOM A1","2900.23",['en':'Equipment Descriptions two'],Initializer.getDate(22,07,2010)
			,Initializer.getDate(10,10,2010),"EUR",Initializer.now(),"equipmentModel",DataLocation.findByCode('Butaro DH'),department,equipmentType,manufacture,supplier)
		Initializer.newEquipment("SERIAL13",PurchasedBy.BYMOH,null,null,true,32,"ROOM A1","2900.23",['en':'Equipment Descriptions two'],Initializer.getDate(22,07,2010)
			,Initializer.getDate(10,10,2010),"EUR",Initializer.now(),"equipmentModel",DataLocation.findByCode('Butaro DH'),department,equipmentType,manufacture,supplier)
	
		
		def List<Equipment> equipmentsOne, equipmentsTwo, equipmentsThree
		def equipmentStatusOneActive = Initializer.newEquipmentStatus(Initializer.now(),User.findByUsername("admin"),Status.INSTOCK,equipmentOne,false,[:])
		def equipmentStatusOneInActive = Initializer.newEquipmentStatus(Initializer.now(),User.findByUsername("admin"),Status.DISPOSED,equipmentOne,true,[:])
		def equipmentStatusTwo = Initializer.newEquipmentStatus(Initializer.now(),User.findByUsername("admin"),Status.OPERATIONAL,equipmentOne,true,[:])
		
		equipmentOne.addToStatus(equipmentStatusOneInActive).save(failOnError:true,flush: true)
		equipmentOne.addToStatus(equipmentStatusOneActive).save(failOnError:true,flush: true)
		equipmentTwo.addToStatus(equipmentStatusTwo).save(failOnError:true,flush: true)

		equipmentController = new EquipmentController();
		when:
		equipmentController.params.dataLocation = DataLocation.findByCode('Kivuye HC')
		equipmentController.params.equipmentType = equipmentType
		equipmentController.params.manufacturer = manufacture
		equipmentController.params.supplier = supplier
		equipmentController.params.obsolete = "false"
		equipmentController.params.purchaser = PurchasedBy.BYFACILITY
		equipmentController.params.status = Status.OPERATIONAL
		equipmentController.filter()
		
		then:
		equipmentController.modelAndView.model.entities.size() == 1
	}
	
	def "filter command validation passes"(){
		setup:
		setupLocationTree()
		
		def manufactureContact = Initializer.newContact(['en':'Address Descriptions '],"Manufacture","jkl@yahoo.com","0768-889-787","Street 154","6353")
		def supplierContact = Initializer.newContact([:],"Supplier","jk@yahoo.com","0768-888-787","Street 1654","6353")
		def manufacture = Initializer.newProvider(CODE(111), Type.MANUFACTURER,manufactureContact)
		def supplier = Initializer.newProvider(CODE(222), Type.SUPPLIER,supplierContact)
		
		def equipmentType = Initializer.newEquipmentType(CODE(15810),["en":"Accelerometers"],["en":"used in memms"],Observation.USEDINMEMMS,Initializer.now(),Initializer.now())
		def commandFilter = new FilterCommand()
		when:
		commandFilter.dataLocation = DataLocation.findByCode('Butaro DH')
		commandFilter.equipmentType = equipmentType
		commandFilter.manufacturer = manufacture
		commandFilter.supplier = supplier
		commandFilter.status = Status.DISPOSED
		commandFilter.purchaser = PurchasedBy.BYFACILITY
		commandFilter.obsolete = "true"
		
		commandFilter.validate()
		
		then:
		commandFilter.hasErrors() == false
	}
	
	def "filter command validation fails when only the dataLocation is specified"(){
		setup:
		setupLocationTree()
		def commandFilter = new FilterCommand()
		when:
		commandFilter.dataLocation = DataLocation.findByCode('Butaro DH')
		
		commandFilter.validate()
		
		then:
		commandFilter.hasErrors() == true
	}
	
	def "filter command validation fails when only the dataLocation is specified and status is none"(){
		setup:
		setupLocationTree()
		def commandFilter = new FilterCommand()
		when:
		commandFilter.dataLocation = DataLocation.findByCode('Butaro DH')
		commandFilter.status = Status.NONE
		commandFilter.validate()
		
		then:
		commandFilter.hasErrors() == true
	}
	def "test update donation and obsolete ajax updateObsolete()"(){
		setup:
		setupLocationTree()

		def manufactureContact = Initializer.newContact(['en':'Address Descriptions '],"Manufacture","jkl@yahoo.com","0768-889-787","Street 154","6353")
		def supplierContact = Initializer.newContact([:],"Supplier","jk@yahoo.com","0768-888-787","Street 1654","6353")
		def manufacture = Initializer.newProvider(CODE(111), Type.MANUFACTURER,manufactureContact)
		def supplier = Initializer.newProvider(CODE(222), Type.SUPPLIER,supplierContact)
		
		def user  = newUser("admin", "Admin UID")
		def department = Initializer.newDepartment(['en':"testName"], CODE(123),['en':"testDescription"])
		def equipmentType = Initializer.newEquipmentType(CODE(15810),["en":"Accelerometers"],["en":"used in memms"],Observation.USEDINMEMMS,Initializer.now(),Initializer.now())

		def equipmentOne = Initializer.newEquipment("SERIAL10",PurchasedBy.BYDONOR,Donor.MOHPARTNER,"CHAI",false,32,"ROOM A1","",['en':'Equipment Descriptions one'],Initializer.getDate(22,07,2010)
				,Initializer.getDate(10,10,2010),"",Initializer.now(),"equipmentModel",DataLocation.findByCode('Kivuye HC'),department,equipmentType,manufacture,supplier)
		def equipmentTwo = Initializer.newEquipment("SERIAL11",PurchasedBy.BYDONOR,Donor.MOHPARTNER,"CHAI",false,32,"ROOM A1","",['en':'Equipment Descriptions two'],Initializer.getDate(22,07,2010)
				,Initializer.getDate(10,10,2010),"",Initializer.now(),"equipmentModel",DataLocation.findByCode('Butaro DH'),department,equipmentType,manufacture,supplier)		
		equipmentOne.save(flush: true)
		equipmentTwo.save(flush: true)
			
		when:
		equipmentController = new EquipmentController();
		equipmentController.params.field = "obsolete"
		equipmentController.params['equipment.id'] = equipmentTwo.id
		equipmentController.updateObsolete()
		then:
		equipmentTwo.obsolete == true		
	}
	
	def "can export equipments"(){
		setup:
		setupLocationTree()

		def manufactureContact = Initializer.newContact(['en':'Address Descriptions '],"Manufacture","jkl@yahoo.com","0768-889-787","Street 154","6353")
		def supplierContact = Initializer.newContact([:],"Supplier","jk@yahoo.com","0768-888-787","Street 1654","6353")
		def manufacture = Initializer.newProvider(CODE(111), Type.MANUFACTURER,manufactureContact)
		def supplier = Initializer.newProvider(CODE(222), Type.SUPPLIER,supplierContact)
		def warrantyContact = Initializer.newContact(['fr':'Warranty Address Descriptions One'],"Warranty","jk@yahoo.com","0768-888-787","Street 654","8988")
		def warranty = Initializer.newWarranty(warrantyContact,Initializer.getDate(10, 12, 2010),22,false,[:])
		
		def user  = newUser("user", "user", true, true)
		setupSecurityManager(user)
		def department = Initializer.newDepartment(['en':"testName"], CODE(123),['en':"testDescription"])
		def equipmentType = Initializer.newEquipmentType(CODE(15810),["en":"Accelerometers"],["en":"used in memms"],Observation.USEDINMEMMS,Initializer.now(),Initializer.now())

		def equipmentOne = Initializer.newEquipment("SERIAL10",PurchasedBy.BYFACILITY,null,null,true,32,"ROOM A1","",['en':'Equipment Descriptions one'],Initializer.getDate(22,07,2010)
				,Initializer.getDate(10,10,2010),"",Initializer.now(),"equipmentModel",DataLocation.findByCode(KIVUYE),department,equipmentType,manufacture,supplier)
		def equipmentTwo = Initializer.newEquipment("SERIAL11",PurchasedBy.BYMOH,null,null,true,32,"ROOM A1","2900.23",['en':'Equipment Descriptions two'],Initializer.getDate(22,07,2010)
				,Initializer.getDate(10,10,2010),"EUR",Initializer.now(),"equipmentModel",DataLocation.findByCode(KIVUYE),department,equipmentType,manufacture,supplier)
		def equipmentThree = Initializer.newEquipment("SERIAL12",PurchasedBy.BYFACILITY,null,null,true,32,"ROOM A1","2900.23",['en':'Equipment Descriptions two'],Initializer.getDate(22,07,2010)
			,Initializer.getDate(10,10,2010),"EUR",Initializer.now(),"equipmentModel",DataLocation.findByCode(KIVUYE),department,equipmentType,manufacture,supplier)
		def equipmentFour = Initializer.newEquipment("SERIAL13",PurchasedBy.BYFACILITY,null,null,true,32,"ROOM A1","2900.23",['en':'Equipment Descriptions two'],Initializer.getDate(22,07,2010)
			,Initializer.getDate(10,10,2010),"EUR",Initializer.now(),"equipmentModel",DataLocation.findByCode(BUTARO),department,equipmentType,manufacture,supplier)
	
		
		def List<Equipment> equipmentsOne, equipmentsTwo, equipmentsThree
		def equipmentStatusOneActive = Initializer.newEquipmentStatus(Initializer.now(),User.findByUsername("user"),Status.INSTOCK,equipmentOne,false,[:])
		def equipmentStatusOneInActive = Initializer.newEquipmentStatus(Initializer.now(),User.findByUsername("user"),Status.OPERATIONAL,equipmentOne,true,[:])
		def equipmentStatusTwo = Initializer.newEquipmentStatus(Initializer.now(),User.findByUsername("user"),Status.DISPOSED,equipmentOne,false,[:])
		
		equipmentOne.warranty=warranty
		equipmentOne.addToStatus(equipmentStatusOneActive).save(failOnError:true,flush: true)
		equipmentTwo.warranty=warranty
		equipmentTwo.addToStatus(equipmentStatusOneActive).save(failOnError:true,flush: true)
		equipmentThree.warranty=warranty
		equipmentThree.addToStatus(equipmentStatusOneActive).save(failOnError:true,flush: true)
		equipmentFour.warranty=warranty
		equipmentFour.addToStatus(equipmentStatusTwo).save(failOnError:true,flush: true)

		equipmentController = new EquipmentController();
		when:
		equipmentController.params.'dataLocation.id' = DataLocation.findByCode(KIVUYE).id
		equipmentController.params.'location.id' = DataLocation.findByCode(KIVUYE).id
		equipmentController.params.equipmentType = equipmentType
		equipmentController.params.manufacturer = manufacture
		equipmentController.params.supplier = supplier
		equipmentController.params.obsolete = "true"
		equipmentController.params.purchaser = PurchasedBy.BYFACILITY
		equipmentController.params.status = Status.OPERATIONAL
		equipmentController.export()
		
		then:
		equipmentController.flash.message == null
		equipmentController.response.outputStream != null
		equipmentController.response.contentType == "text/csv";
	}
}
