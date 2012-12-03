
package org.chai.memms.inventory

import org.chai.location.DataLocation;
import org.chai.memms.Initializer;
import org.chai.memms.IntegrationTests;
import org.chai.memms.inventory.EquipmentStatus.Status;
import org.chai.memms.inventory.EquipmentStatusController;
import org.chai.memms.inventory.Equipment;
import org.chai.memms.inventory.EquipmentStatus;

class EquipmentStatusControllerSpec extends IntegrationTests{
	
	def equipmentStatusController
	
	def "create equipment status with correct required data in fields - for english input"(){
		setup:
		setupLocationTree()
		setupEquipment()
		setupSystemUser()
		def user = newUser("user","user")
		def equipment = Equipment.findBySerialNumber(CODE(123))
		equipmentStatusController = new EquipmentStatusController();
		when:
		equipmentStatusController.params.reason = "reason bha"
		equipmentStatusController.params.current = true
		equipmentStatusController.params.status="DISPOSED"
		equipmentStatusController.params.dateOfEvent=Initializer.now()
		equipmentStatusController.params."equipment.id" = equipment.id
		equipmentStatusController.save()
		then:
		EquipmentStatus.count() == 1;
		EquipmentStatus.findByStatus(Status.DISPOSED).status == Status.DISPOSED
	}
	
	def "list equipment status"(){
		setup:
		setupLocationTree()
		setupEquipment()
		setupSystemUser()
		def user = newUser("user","user")
		def equipment = Equipment.findBySerialNumber(CODE(123))
		def equipmentOne = newEquipment(CODE(124),DataLocation.findByCode(KIVUYE))
		Initializer.newEquipmentStatus(Initializer.now() ,user,Status.OPERATIONAL, equipmentOne,["en":"not of these"])
		Initializer.newEquipmentStatus(Initializer.now() ,user,Status.INSTOCK, equipment,["en":"Of this one"])
		Initializer.newEquipmentStatus(Initializer.now() ,user,Status.INSTOCK, equipment,["en":"Of this two"])
		Initializer.newEquipmentStatus(Initializer.now() ,user,Status.INSTOCK, equipment,["en":"Of this three"])
		equipmentStatusController = new EquipmentStatusController();
		
		when: "fails if no equipment supplied"
		equipmentStatusController.list()
		then:
		EquipmentStatus.count() == 4;
		equipmentStatusController.response.status == 404
		
		when: "not using ajax"
		equipmentStatusController.params."equipment.id" = equipment.id
		equipmentStatusController.list()
		then:
		EquipmentStatus.count() == 4;
		equipmentStatusController.modelAndView.model.entities.size() == 3
		
		when: "using ajax"
		equipmentStatusController.params."equipment.id" = equipment.id
		equipmentStatusController.request.makeAjaxRequest()
		equipmentStatusController.list()
		then:
		EquipmentStatus.count() == 4;
		!equipmentStatusController.response.json.results[0].contains(EquipmentStatus.findByStatus(Status.OPERATIONAL).reasons)
		EquipmentStatus.findByStatus(Status.INSTOCK).each{equipmentStatusController.response.json.results[0].contains(it.reasons)}
	}
}
