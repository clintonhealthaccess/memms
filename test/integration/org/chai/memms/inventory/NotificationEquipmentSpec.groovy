package org.chai.memms.inventory

import java.util.Date;

import org.chai.memms.Initializer;
import org.chai.memms.IntegrationTests
import org.chai.memms.security.User;
import org.chai.location.DataLocation;
import org.chai.location.Location;

class NotificationEquipmentSpec  extends IntegrationTests{
	def "can create a notification"(){
		setup:
		setupLocationTree()
		def sender = newOtherUser("sender", "sender", DataLocation.findByCode(KIVUYE))
		def receiver = newOtherUser("receiver", "receiver", DataLocation.findByCode(BUTARO))
		
		when:
		new NotificationEquipment(sender:sender, receiver:receiver, writtenOn: Initializer.now(), content:"I have a new equipment in my health center.",read:true,dataLocation:sender.location).save(failOnError:true)
		then:
		NotificationEquipment.count() == 1
	}

	def "all required fields needed on notification"(){
		setup:
		setupLocationTree()
		def sender = newOtherUser("sender", "sender", DataLocation.findByCode(KIVUYE))
		def receiver = newOtherUser("receiver", "receiver", DataLocation.findByCode(BUTARO))
		
		def notificationWithErrors = new NotificationEquipment()
		def expectedFieldErrors = ["sender","receiver","writtenOn","content","dataLocation"]

		when://All required fields in
		notificationWithErrors.save()
		then:
		notificationWithErrors.errors.fieldErrorCount == 5
		expectedFieldErrors.each{notificationWithErrors.errors.hasFieldErrors(it)}
		NotificationEquipment.count() == 0

		when://notification date should be before or equal to  today
		notificationWithErrors = new NotificationEquipment(sender:sender, receiver:receiver, writtenOn: Initializer.now()+1, content:" check this out",read:true,dataLocation:sender.location)
		notificationWithErrors.save()
		then:
		notificationWithErrors.errors.hasFieldErrors("writtenOn") == true
		NotificationEquipment.count() == 0
	}
}
