package org.chai.memms.task

import org.chai.memms.IntegrationTests;
import org.chai.memms.security.User;
import org.chai.task.Task.TaskStatus;
import org.chai.task.Task
import org.apache.commons.io.FileUtils

class TaskServiceSpec extends IntegrationTests {

//	static transactional = false
//	def taskService
//	def cleanup() {
//		Task.executeUpdate("delete Task")
//		User.executeUpdate("delete User")
//		sessionFactory.currentSession.flush()
//	} 
//	def "handle message sets status to completed"() {
//		setup:
//		def user = newUser('user', 'uuid')
//		def task = new EquipmentExportTask(exportFilterId:1, user: user, status: TaskStatus.NEW).save(failOnError:true)
//		when:
//		taskService.handleMessage(task.id)
//
//		then:
//		Task.list()[0].status == TaskStatus.COMPLETED
//		// tearDown
//		FileUtils.deleteDirectory(task.folder)
//	}
//	
//	def "handle message increments number of tries"() {
//		setup:
//		def user = newUser('user', 'uuid')
//		def task = new EquipmentExportTask(exportFilterId:1, user: user, status: TaskStatus.NEW).save(failOnError:true)
//		when:
//		taskService.handleMessage(task.id)
//		
//		then:
//		Task.list()[0].numberOfTries == 1
//		// tearDown
//		FileUtils.deleteDirectory(task.folder)
//	}
//	
//	def "send to queue sets senttoqueue"() {
//		setup:
//		def user = newUser('user', 'uuid')
//		def task = new EquipmentExportTask(exportFilterId:1, user: user, status: TaskStatus.NEW).save(failOnError:true)
//		
//		when:
//		taskService.metaClass.rabbitSend = { Object[] args ->  }
//		taskService.sendToQueue(task)
//		
//		then:
//		Task.list()[0].sentToQueue == true
	
		// TODO somehow it does not work to override a metaClass method twice inside the same test
//		when:
//		taskService.metaClass.rabbitSend = { Object[] args -> throw new RuntimeException() }
//		taskService.sendToQueue(task)
//		
//		then:
//		Task.list()[0].sentToQueue == false
//	}	

//	def "execute task does not do anything if task is completed"() {
//		setup:
//		def user = newUser('user', 'uuid')
//		def task = new EquipmentExportTask(exportFilterId:1, user: user, status: TaskStatus.COMPLETED).save(failOnError:true)
//		task.metaClass.executeTask {throw new RuntimeException()}
//		
//		when:
//		taskService.executeTask(task.id)
//		
//		then:
//		notThrown RuntimeException
//	}
//	
//	def "execute task does not do anything if task is aborted"() {
//		setup:
//		def user = newUser('user', 'uuid')
//		def task = new EquipmentExportTask(exportFilterId:1, user: user, status: TaskStatus.ABORTED).save(failOnError:true)
//		task.metaClass.executeTask {throw new RuntimeException()}
//		
//		when:
//		taskService.executeTask(task.id)
//		
//		then:
//		notThrown RuntimeException
//	}
//	
//	def "task is set as aborted when abort exception is thrown"() {
//		setup:
//		def user = newUser('user', 'uuid')
//		def task = new EquipmentExportTask(exportFilterId:1, user: user, status: TaskStatus.NEW).save(failOnError:true)
//		task.metaClass.executeTask {throw new TaskAbortedException()}
//		
//		when:
//		taskService.executeTask(task.id)
//		
//		then:
//		Task.list()[0].status == TaskStatus.COMPLETED
//	}
}
