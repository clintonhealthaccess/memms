package org.chai.memms.task

import grails.validation.ValidationException

import org.apache.commons.io.FileUtils;
import org.chai.memms.Initializer;
import org.chai.memms.IntegrationTests
import org.chai.memms.inventory.Equipment;
import org.chai.memms.inventory.EquipmentStatus;
import org.chai.location.CalculationLocation;
import org.chai.location.DataLocation
import org.chai.location.DataLocationType
import org.chai.location.Location
import org.chai.location.LocationLevel
import org.chai.memms.security.Role;
import org.chai.memms.security.User
import org.chai.task.Task.TaskStatus
import org.chai.task.EquipmentExportFilter;
import org.chai.task.EquipmentExportTask;
import org.chai.task.ExportFilter;
import org.chai.task.Task

class DataExportTaskSpec extends IntegrationTests {

	static transactional = false
	
	def cleanup() {
		Task.executeUpdate("delete Task")
		ExportFilter.executeUpdate("delete ExportFilter")
		EquipmentStatus.executeUpdate("delete EquipmentStatus")
		Equipment.executeUpdate("delete Equipment")
		DataLocation.executeUpdate("delete DataLocation")
		Location.executeUpdate("delete Location")
		LocationLevel.executeUpdate("delete LocationLevel")
		DataLocationType.executeUpdate("delete DataLocationType")
		User.list().each{it.delete()}
		Role.list().each{it.delete()}
		
		sessionFactory.currentSession.flush()
	}
	
	def "null constraints"() {
		setup:
		def user = newUser('user', 'uuid')
		
		when:
		new EquipmentExportTask(user: user, status: TaskStatus.NEW, exportFilterId: 1).save(failOnError: true, flush: true)
		
		then:
		Task.count() == 1
		
		when:
		new EquipmentExportTask(user: user, status: TaskStatus.NEW).save(failOnError: true, flush: true)
		
		then:
		thrown ValidationException
	}
		
	def "task is unique"() {
		setup:
		def user = newUser('user', 'uuid')
		def task = new EquipmentExportTask(user: user, status: TaskStatus.NEW, exportFilterId: 1).save(failOnError: true, flush: true)
		
		when:
		def sameTask = new EquipmentExportTask(user: user, status: TaskStatus.NEW, exportFilterId: 1)
		
		then:
		!sameTask.isUnique()
		
		when:
		def otherTask = new EquipmentExportTask(user: user, status: TaskStatus.NEW, exportFilterId: 2)
		
		then:
		otherTask.isUnique()
		
		when:
		task.status = TaskStatus.COMPLETED
		task.save(failOnError: true, flush: true)
		
		then:
		sameTask.isUnique()
		
		when:
		task.status = TaskStatus.ABORTED
		task.save(failOnError: true, flush: true)
		
		then:
		sameTask.isUnique()
	}
	//TODO failing the clean up work on it
//	def "execute task - EquipmentExportTask with ExportFilter"() {
//		setup:
//		Initializer.createDummyStructure()
//		Initializer.createUsers()
//		Initializer.createInventoryStructure()
//		
//		def user = User.findByUsername('admin')
//		def locations = []
//		def equipmentExportFilter = new EquipmentExportFilter(calculationLocation:[CalculationLocation.findByCode( BUTARO)] , obsolete:'true').save(failOnError: true,flush: true)
//		def task = new EquipmentExportTask(user: user, status: TaskStatus.NEW, exportFilterId: equipmentExportFilter.id).save(failOnError: true, flush: true)
//		FileUtils.deleteDirectory(task.folder)
//
//		when:
//		task.executeTask()
//
//		then:
//		new File(task.folder, task.outputFilename).exists()
//
//		// tearDown
//		FileUtils.deleteDirectory(task.folder)
//	}

	
	def "test clean task"() {
		setup:
		def user = newUser('user', 'uuid')
		setupLocationTree();
		def equipmentExportFilter = new EquipmentExportFilter().save(failOnError: true,flush: true)
		when:
		def task = new EquipmentExportTask(user: user, status: TaskStatus.NEW, exportFilterId: equipmentExportFilter.id).save(failOnError: true, flush: true)
		task.getFolder()

		then:
		new File('files/'+task.id).exists()

		when:
		task.cleanTask()

		then:
		!new File('files/'+task.id).exists()
	}
	
}