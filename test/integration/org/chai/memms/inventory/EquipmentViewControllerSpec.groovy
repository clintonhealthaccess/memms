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

class EquipmentViewControllerSpec extends IntegrationTests{

	def equipmentViewController

	def "can list equipments by dataLocation"(){
		setup:
		setupLocationTree()

		def manufactureContact = Initializer.newContact(['en':'Address Descriptions '],"Manufacture","jkl@yahoo.com","0768-889-787","Street 154","6353")
		def supplierContact = Initializer.newContact([:],"Supplier","jk@yahoo.com","0768-888-787","Street 1654","6353")
		def manufacture = Initializer.newProvider(CODE(111), Type.MANUFACTURER,manufactureContact)
		def supplier = Initializer.newProvider(CODE(222), Type.SUPPLIER,supplierContact)
		
		def user  = newUser("admin", "Admin UID")
		user.location = DataLocation.findByCode('Butaro DH')
		user.save(failOnError:true)
		setupSecurityManager(user)
		def department = Initializer.newDepartment(['en':"testName"], CODE(123),['en':"testDescription"])
		def equipmentType = Initializer.newEquipmentType(CODE(15810),["en":"Accelerometers"],["en":"used in memms"],Observation.USEDINMEMMS,Initializer.now(),Initializer.now())

		def equipmentOne = Initializer.newEquipment("SERIAL10",PurchasedBy.BYFACILITY,null,null,false,Initializer.newPeriod(32),"ROOM A1","2900.23",['en':'Equipment Descriptions one'],Initializer.getDate(22,07,2010)
				,Initializer.getDate(10,10,2010),"USD",Initializer.now(),"equipmentModel",DataLocation.findByCode('Kivuye HC'),department,equipmentType,manufacture,supplier)
		def equipmentTwo = Initializer.newEquipment("SERIAL11",PurchasedBy.BYFACILITY,null,null,true,Initializer.newPeriod(32),"ROOM A1","2900.23",['en':'Equipment Descriptions two'],Initializer.getDate(22,07,2010)
				,Initializer.getDate(10,10,2010),"EUR",Initializer.now(),"equipmentModel",DataLocation.findByCode('Butaro DH'),department,equipmentType,manufacture,supplier)
		
		Initializer.newEquipment("SERIAL12",PurchasedBy.BYFACILITY,null,null,true,Initializer.newPeriod(32),"ROOM A1","2900.23",['en':'Equipment Descriptions two'],Initializer.getDate(22,07,2010)
			,Initializer.getDate(10,10,2010),"RWF",Initializer.now(),"equipmentModel",DataLocation.findByCode('Butaro DH'),department,equipmentType,manufacture,supplier)
		Initializer.newEquipment("SERIAL13",PurchasedBy.BYFACILITY,null,null,true,Initializer.newPeriod(32),"ROOM A1","2900.23",['en':'Equipment Descriptions two'],Initializer.getDate(22,07,2010)
			,Initializer.getDate(10,10,2010),"RWF",Initializer.now(),"equipmentModel",DataLocation.findByCode('Butaro DH'),department,equipmentType,manufacture,supplier)
	
		
		def List<Equipment> equipmentsOne, equipmentsTwo, equipmentsThree
		def equipmentStatusOneActive = Initializer.newEquipmentStatus(Initializer.now(),User.findByUsername("admin"),Status.INSTOCK,equipmentOne,false,[:])
		def equipmentStatusOneInActive = Initializer.newEquipmentStatus(Initializer.now(),User.findByUsername("admin"),Status.OPERATIONAL,equipmentOne,true,[:])
		def equipmentStatusTwo = Initializer.newEquipmentStatus(Initializer.now(),User.findByUsername("admin"),Status.DISPOSED,equipmentOne,true,[:])
		
		equipmentOne.addToStatus(equipmentStatusOneInActive).save(failOnError:true,flush: true)
		equipmentOne.addToStatus(equipmentStatusOneActive).save(failOnError:true,flush: true)
		equipmentTwo.addToStatus(equipmentStatusTwo).save(failOnError:true,flush: true)

		equipmentViewController = new EquipmentViewController();
		when:
		equipmentViewController.params.'dataLocation.id' = DataLocation.findByCode('Butaro DH').id
		equipmentViewController.list()
		
		then:
		equipmentViewController.modelAndView.model.entities.size() == 3
	}
	
	def "redirects to listing when accessing summary page by a user with a datalocation"(){
		setup:
		setupLocationTree()

		Initializer.createDummyStructure()
		Initializer.createUsers()
		setupSecurityManager(User.findByUsername('titulaireHC'))//data location

		equipmentViewController = new EquipmentViewController();
		when:
		equipmentViewController.summaryPage()
		
		then:
		equipmentViewController.response.redirectedUrl == '/equipmentView/list?dataLocation.id=' + User.findByUsername('titulaireHC').location.id
	}
	
	def "does not redirects to listing when accessing summary page by a user with a location"(){
		setup:
		setupLocationTree()

		Initializer.createDummyStructure()
		Initializer.createUsers()
		setupSecurityManager(User.findByUsername('user1'))

		equipmentViewController = new EquipmentViewController();
		when:
		equipmentViewController.summaryPage()
		
		then:
		equipmentViewController.response.redirectedUrl != '/equipment/list/' + User.findByUsername('user1').location.id
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

		def equipmentOne = Initializer.newEquipment("SERIAL10",PurchasedBy.BYDONOR,Donor.MOHPARTNER,"CHAI",false,Initializer.newPeriod(32),"ROOM A1","",['en':'Equipment Descriptions one'],Initializer.getDate(22,07,2010)
				,Initializer.getDate(10,10,2010),"",Initializer.now(),"equipmentModel",DataLocation.findByCode('Kivuye HC'),department,equipmentType,manufacture,supplier)
		def equipmentTwo = Initializer.newEquipment("SERIAL11",PurchasedBy.BYFACILITY,null,null,false,Initializer.newPeriod(32),"ROOM A1","2900.23",['en':'Equipment Descriptions two'],Initializer.getDate(22,07,2010)
				,Initializer.getDate(10,10,2010),"EUR",Initializer.now(),"equipmentModel",DataLocation.findByCode('Kivuye HC'),department,equipmentType,manufacture,supplier)
		Initializer.newEquipment("SERIAL12",PurchasedBy.BYMOH,null,null,true,Initializer.newPeriod(32),"ROOM A1","2900.23",['en':'Equipment Descriptions two'],Initializer.getDate(22,07,2010)
			,Initializer.getDate(10,10,2010),"EUR",Initializer.now(),"equipmentModel",DataLocation.findByCode('Butaro DH'),department,equipmentType,manufacture,supplier)
		Initializer.newEquipment("SERIAL13",PurchasedBy.BYMOH,null,null,true,Initializer.newPeriod(32),"ROOM A1","2900.23",['en':'Equipment Descriptions two'],Initializer.getDate(22,07,2010)
			,Initializer.getDate(10,10,2010),"EUR",Initializer.now(),"equipmentModel",DataLocation.findByCode('Butaro DH'),department,equipmentType,manufacture,supplier)
	
		
		def List<Equipment> equipmentsOne, equipmentsTwo, equipmentsThree
		def equipmentStatusOneActive = Initializer.newEquipmentStatus(Initializer.now(),User.findByUsername("admin"),Status.INSTOCK,equipmentOne,false,[:])
		def equipmentStatusOneInActive = Initializer.newEquipmentStatus(Initializer.now(),User.findByUsername("admin"),Status.DISPOSED,equipmentOne,true,[:])
		def equipmentStatusTwo = Initializer.newEquipmentStatus(Initializer.now(),User.findByUsername("admin"),Status.OPERATIONAL,equipmentOne,true,[:])
		
		equipmentOne.addToStatus(equipmentStatusOneInActive).save(failOnError:true,flush: true)
		equipmentOne.addToStatus(equipmentStatusOneActive).save(failOnError:true,flush: true)
		equipmentTwo.addToStatus(equipmentStatusTwo).save(failOnError:true,flush: true)

		equipmentViewController = new EquipmentViewController();
		when:
		equipmentViewController.params.dataLocation = DataLocation.findByCode('Kivuye HC')
		equipmentViewController.params.equipmentType = equipmentType
		equipmentViewController.params.manufacturer = manufacture
		equipmentViewController.params.supplier = supplier
		equipmentViewController.params.obsolete = "false"
		equipmentViewController.params.purchaser = PurchasedBy.BYFACILITY
		equipmentViewController.params.status = Status.OPERATIONAL
		equipmentViewController.filter()
		
		then:
		equipmentViewController.modelAndView.model.entities.size() == 1
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

		def equipmentOne = Initializer.newEquipment("SERIAL10",PurchasedBy.BYDONOR,Donor.MOHPARTNER,"CHAI",false,Initializer.newPeriod(32),"ROOM A1","",['en':'Equipment Descriptions one'],Initializer.getDate(22,07,2010)
				,Initializer.getDate(10,10,2010),"",Initializer.now(),"equipmentModel",DataLocation.findByCode('Kivuye HC'),department,equipmentType,manufacture,supplier)
		def equipmentTwo = Initializer.newEquipment("SERIAL11",PurchasedBy.BYDONOR,Donor.MOHPARTNER,"CHAI",false,Initializer.newPeriod(32),"ROOM A1","",['en':'Equipment Descriptions two'],Initializer.getDate(22,07,2010)
				,Initializer.getDate(10,10,2010),"",Initializer.now(),"equipmentModel",DataLocation.findByCode('Butaro DH'),department,equipmentType,manufacture,supplier)		
		equipmentOne.save(flush: true)
		equipmentTwo.save(flush: true)
			
		when:
		equipmentViewController = new EquipmentViewController();
		equipmentViewController.params.field = "obsolete"
		equipmentViewController.params['equipment.id'] = equipmentTwo.id
		equipmentViewController.updateObsolete()
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
		def warranty = Initializer.newWarranty(warrantyContact,Initializer.getDate(10, 12, 2010),false,[:])
		
		def user  = newUser("user", "user", true, true)
		setupSecurityManager(user)
		def department = Initializer.newDepartment(['en':"testName"], CODE(123),['en':"testDescription"])
		def equipmentType = Initializer.newEquipmentType(CODE(15810),["en":"Accelerometers"],["en":"used in memms"],Observation.USEDINMEMMS,Initializer.now(),Initializer.now())

		def equipmentOne = Initializer.newEquipment("SERIAL10",PurchasedBy.BYFACILITY,null,null,true,Initializer.newPeriod(32),"ROOM A1","",['en':'Equipment Descriptions one'],Initializer.getDate(22,07,2010)
				,Initializer.getDate(10,10,2010),"",Initializer.now(),"equipmentModel",DataLocation.findByCode(KIVUYE),department,equipmentType,manufacture,supplier)
		def equipmentTwo = Initializer.newEquipment("SERIAL11",PurchasedBy.BYMOH,null,null,true,Initializer.newPeriod(32),"ROOM A1","2900.23",['en':'Equipment Descriptions two'],Initializer.getDate(22,07,2010)
				,Initializer.getDate(10,10,2010),"EUR",Initializer.now(),"equipmentModel",DataLocation.findByCode(KIVUYE),department,equipmentType,manufacture,supplier)
		def equipmentThree = Initializer.newEquipment("SERIAL12",PurchasedBy.BYFACILITY,null,null,true,Initializer.newPeriod(32),"ROOM A1","2900.23",['en':'Equipment Descriptions two'],Initializer.getDate(22,07,2010)
			,Initializer.getDate(10,10,2010),"EUR",Initializer.now(),"equipmentModel",DataLocation.findByCode(KIVUYE),department,equipmentType,manufacture,supplier)
		def equipmentFour = Initializer.newEquipment("SERIAL13",PurchasedBy.BYFACILITY,null,null,true,Initializer.newPeriod(32),"ROOM A1","2900.23",['en':'Equipment Descriptions two'],Initializer.getDate(22,07,2010)
			,Initializer.getDate(10,10,2010),"EUR",Initializer.now(),"equipmentModel",DataLocation.findByCode(BUTARO),department,equipmentType,manufacture,supplier)
	
		
		def List<Equipment> equipmentsOne, equipmentsTwo, equipmentsThree
		def equipmentStatusOneActive = Initializer.newEquipmentStatus(Initializer.now(),User.findByUsername("user"),Status.INSTOCK,equipmentOne,false,[:])
		def equipmentStatusOneInActive = Initializer.newEquipmentStatus(Initializer.now(),User.findByUsername("user"),Status.OPERATIONAL,equipmentOne,true,[:])
		def equipmentStatusTwo = Initializer.newEquipmentStatus(Initializer.now(),User.findByUsername("user"),Status.DISPOSED,equipmentOne,false,[:])
		
		equipmentOne.warranty=warranty
		equipmentOne.warrantyPeriod=Initializer.newPeriod(21)
		equipmentOne.addToStatus(equipmentStatusOneActive).save(failOnError:true,flush: true)
		equipmentTwo.warranty=warranty
		equipmentTwo.warrantyPeriod=Initializer.newPeriod(22)
		equipmentTwo.addToStatus(equipmentStatusOneActive).save(failOnError:true,flush: true)
		equipmentThree.warranty=warranty
		equipmentThree.warrantyPeriod=Initializer.newPeriod(23)
		equipmentThree.addToStatus(equipmentStatusOneActive).save(failOnError:true,flush: true)
		equipmentFour.warranty=warranty
		equipmentFour.warrantyPeriod=Initializer.newPeriod(20)
		equipmentFour.addToStatus(equipmentStatusTwo).save(failOnError:true,flush: true)

		equipmentViewController = new EquipmentViewController();
		when:
		equipmentViewController.params.'dataLocation.id' = DataLocation.findByCode(KIVUYE).id
		equipmentViewController.params.'location.id' = DataLocation.findByCode(KIVUYE).id
		equipmentViewController.params.equipmentType = equipmentType
		equipmentViewController.params.manufacturer = manufacture
		equipmentViewController.params.supplier = supplier
		equipmentViewController.params.obsolete = "true"
		equipmentViewController.params.purchaser = PurchasedBy.BYFACILITY
		equipmentViewController.params.status = Status.OPERATIONAL
		equipmentViewController.export()
		
		then:
		equipmentViewController.flash.message == null
		equipmentViewController.response.outputStream != null
		equipmentViewController.response.contentType == "text/csv";
	}
}
