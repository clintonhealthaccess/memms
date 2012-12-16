package org.chai.memms.inventory

import org.chai.memms.Initializer;
import org.chai.memms.IntegrationTests;
import org.chai.memms.inventory.EquipmentStatus.Status;
import org.chai.memms.inventory.Equipment;
import org.chai.memms.inventory.EquipmentStatus;


class EquipmentStatusServiceSpec extends IntegrationTests{
	
  def equipmentStatusService
  
  def "can create and save an equipment status"(){
		setup:
		setupLocationTree()
		setupEquipment()
		setupSystemUser()
		when:
		def user  = newUser("admin", "Admin UID")
		def equipment = Equipment.findBySerialNumber(CODE(123))
		equipmentStatusService.createEquipmentStatus(user,Status.OPERATIONAL,equipment,Initializer.now(),[:])
		then:
		EquipmentStatus.count()==1
	}
  
  def "search equipmentStatus by equipment"(){
	  setup:
	  setupLocationTree()
	  setupEquipment()
	  setupSystemUser()
	  def user  = newUser("admin", "Admin UID")
	  def equipment = Equipment.findBySerialNumber(CODE(123))
	  equipmentStatusService.createEquipmentStatus(user,Status.OPERATIONAL,equipment,Initializer.now(),[:])
	  when:
	  def stats = equipmentStatusService.getEquipmentStatusByEquipment(equipment,[:])
	  then:
	  EquipmentStatus.count()==1
	  EquipmentStatus.list().equals(equipment.status.asList())
	  
  }
}
