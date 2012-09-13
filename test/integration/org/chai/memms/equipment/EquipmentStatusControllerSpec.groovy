package org.chai.memms.equipment

import org.chai.memms.IntegrationTests;
import org.chai.memms.Initializer
import org.chai.memms.equipment.EquipmentType.Observation;
import org.chai.location.DataLocation;
import org.chai.memms.equipment.EquipmentStatus.Status;
import org.chai.memms.security.User
import org.chai.memms.equipment.Provider.Type;

class EquipmentStatusControllerSpec extends IntegrationTests{
	
	def equipmentStatusController
	
	def "create equipment status with correct required data in fields - for english input"(){
		
//		setup:
//		setupSystemUse()
//		def manufactureContact = Initializer.newContact(['en':'Address Descriptions '],"Manufacture","jkl@yahoo.com","0768-889-787","Street 154","6353")
//		def supplierContact = Initializer.newContact([:],"Supplier","jk@yahoo.com","0768-888-787","Street 1654","6353")
//		
//		def manufacture = Initializer.newProvider(CODE(123), Type.MANUFACTURE,manufactureContact)
//		def supplier = Initializer.newProvider(CODE(124), Type.SUPPLIER,supplierContact)
//		
//		equipmentStatusController = new EquipmentStatusController();
//		setupLocationTree()
//		def user  = newUser("admin", "Admin UID")
//		def department = Initializer.newDepartment(['en':"testName"], CODE(123),['en':"testDescription"])
//		def equipmentModel = Initializer.newEquipmentModel(['en':"testName"], CODE(123),['en':"testDescription"])
//		def equipmentType = Initializer.newEquipmentType(CODE(15810),["en":"Accelerometers"],["en":"used in memms"],Observation.USEDINMEMMS,Initializer.now(),Initializer.now())
//		def equipment = Initializer.newEquipment(
//					"SERIAL10",true,false,32,"ROOM A1"
//					,"2900.23",
//					['en':'Equipment Descriptions'],
//					Initializer.getDate(22,07,2010),
//					Initializer.getDate(10,10,2010),
//					new Date(),
//					"equipmentModel",
//					DataLocation.list().first(),
//					department,
//					equipmentType,manufacture,supplier
//					)
//		when:
//		equipmentStatusController.params.current = true
//		equipmentStatusController.params.status="DISPOSED"
//		equipmentStatusController.params.dateOfEvent=Initializer.now()
//		equipmentStatusController.params.equipment = equipment.id
//		equipmentStatusController.save(failOnError: true)
//		
//		then:
//		EquipmentStatus.findByStatus(Status.DISPOSED).status == Status.DISPOSED
//		EquipmentStatus.count() == 1;
//		
	}
}
