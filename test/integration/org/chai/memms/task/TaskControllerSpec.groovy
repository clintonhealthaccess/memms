package org.chai.memms.task

import net.sf.json.JSONNull;
import org.chai.memms.IntegrationTests;
import org.chai.memms.task.Task.TaskStatus;
import org.chai.memms.util.JSONUtils;
import org.codehaus.groovy.grails.plugins.testing.GrailsMockMultipartFile;

class TaskControllerSpec extends IntegrationTests {

	def taskController
	
	def "create task"() {
		setup:
		def user = newUser('user', 'uuid')
		setupSecurityManager(user)
		taskController = new TaskController()
		
		when:
		taskController.params['class'] = 'EquipmentExportTask'
		taskController.params['exportFilterId'] = 1
		taskController.create()
		
		then:
		Task.count() == 1
		Task.list()[0].exportFilterId == 1
		Task.list()[0].max == 0
		Task.list()[0].current == null
		taskController.response.redirectedUrl == '/'
	}
	
	def "create non-unique EquipmentExportTask"() {
		setup:
		def user = newUser('user', 'uuid')
		setupSecurityManager(user)
		new EquipmentExportTask(exportFilterId:1,user: user, status: TaskStatus.NEW).save(failOnError: true)
		taskController = new TaskController()
		
		when:
		taskController.params['class'] = 'EquipmentExportTask'
		taskController.params['exportFilterId'] = 1
		taskController.create()
		
		then:
		Task.count() == 1
		taskController.response.redirectedUrl == '/'
	}
	
	def "create unique task that is a new task of an already completed one"() {
		setup:
		def user = newUser('user', 'uuid')
		setupSecurityManager(user)
		new EquipmentExportTask(exportFilterId: 1, user: user, status: TaskStatus.COMPLETED).save(failOnError: true)
		taskController = new TaskController()
		
		when:
		taskController.params['class'] = 'EquipmentExportTask'
		taskController.params['exportFilterId'] = 1
		taskController.create()
		
		then:
		Task.count() == 2
		taskController.response.redirectedUrl == '/'
	}
	
	def "create task validation"() {
		setup:
		def user = newUser('user', 'uuid')
		setupSecurityManager(user)
		taskController = new TaskController()
		
		when:
		taskController.params['class'] = 'EquipmentExportTask'
		taskController.create()
		
		then:
		Task.count() == 0
		taskController.response.redirectedUrl == '/'
	}
	
	def "create task with wrong class"() {
		setup:
		def user = newUser('user', 'uuid')
		setupSecurityManager(user)
		taskController = new TaskController()
		
		when:
		taskController.params['class'] = 'Inexistant'
		taskController.create()
		
		then:
		taskController.modelAndView == null
	}
	
	def "task list"() {
		setup:
		def user = newUser('user', 'uuid')
		setupSecurityManager(user)
		def task = new EquipmentExportTask(exportFilterId: 1, user: user, status: TaskStatus.NEW).save(failOnError: true)
		taskController = new TaskController()
		
		when:
		taskController.list()
		
		then:
		taskController.modelAndView.model.entities == [task] 
	}
	
	def "delete task"() {
		setup:
		def user = newUser('user', 'uuid')
		setupSecurityManager(user)
		def task = new EquipmentExportTask(exportFilterId: 1, user: user, status: TaskStatus.NEW, sentToQueue: false).save(failOnError: true)
		taskController = new TaskController()
		
		when:
		taskController.params.id = task.id
		taskController.delete()
		
		then:
		Task.count() == 0
		taskController.response.redirectedUrl == '/'
	}
	
	def "delete complete task"() {
		setup:
		def user = newUser('user', 'uuid')
		setupSecurityManager(user)
		def task = new EquipmentExportTask(exportFilterId: 1, user: user, status: TaskStatus.COMPLETED, sentToQueue: true).save(failOnError: true)
		taskController = new TaskController()
		
		when:
		taskController.params.id = task.id
		taskController.delete()
		
		then:
		Task.count() == 0
		taskController.response.redirectedUrl == '/'
	}
	
//	// cannot test because of withNewTransaction call, and abort()
//	// seems to not be overridable using metaClass (task.metaClass.abort = {aborted = true})
////	def "delete in progress task already sent aborts the task"() {
////		setup:
////		def user = newUser('user', 'uuid')
////		setupSecurityManager(user)
////		def task = new TestCalculateTask(dataId: 1, user: user, status: TaskStatus.IN_PROGRESS, sentToQueue: true).save(failOnError: true)
////		taskController = new TaskController()
////		
////		when:
////		taskController.params.id = task.id
////		taskController.delete()
////		
////		then:
////		Task.count() == 1
////		Task.list()[].aborted == true
////		taskController.response.redirectedUrl == '/'
////	}
	
	
	def "delete new task already sent aborts the task"() {
		setup:
		def user = newUser('user', 'uuid')
		setupSecurityManager(user)
		def task = new EquipmentExportTask(exportFilterId: 1, user: user, status: TaskStatus.NEW, sentToQueue: true).save(failOnError: true)
		taskController = new TaskController()
		
		when:
		taskController.params.id = task.id
		taskController.delete()
		
		then:
		Task.count() == 0
		taskController.response.redirectedUrl == '/'
	}
	
	def "delete inexistant task"() {
		setup:
		taskController = new TaskController()
		
		when:
		taskController.params.id = '1'
		taskController.delete()
		
		then:
		Task.count() == 0
		taskController.response.redirectedUrl == '/'
	}
	
	def "purge tasks"() {
		setup:
		def user = newUser('user', 'uuid')
		def task1 = new EquipmentExportTask(exportFilterId: 1, user: user, status: TaskStatus.NEW).save(failOnError: true)
		def task2 = new EquipmentExportTask(exportFilterId: 2, user: user, status: TaskStatus.COMPLETED).save(failOnError: true)
		def task3 = new EquipmentExportTask(exportFilterId: 3, user: user, status: TaskStatus.IN_PROGRESS).save(failOnError: true)
		def task4 = new EquipmentExportTask(exportFilterId: 4, user: user, status: TaskStatus.COMPLETED).save(failOnError: true)
		def task5 = new EquipmentExportTask(exportFilterId: 5, user: user, status: TaskStatus.ABORTED).save(failOnError: true)
		taskController = new TaskController()
		
		when:
		taskController.purge()
		
		then:
		Task.count() == 2
		Task.list()[0].status == TaskStatus.NEW
		Task.list()[1].status == TaskStatus.IN_PROGRESS
	}
	
	def "progress when new"() {
		setup:
		def user = newUser('user', 'uuid')
		def task1 = new EquipmentExportTask(exportFilterId: 1, user: user, status: TaskStatus.NEW).save(failOnError: true)
		taskController = new TaskController()
		
		when:
		taskController.params['ids'] = [task1.id]
		taskController.progress()
		def content = taskController.response.contentAsString
		def jsonResult = JSONUtils.getMapFromJSON(content)
		
		then:
		jsonResult.tasks.size() == 1
		jsonResult.tasks[0].status == "NEW"
		jsonResult.tasks[0].progress == JSONNull.instance
		jsonResult.tasks[0].id == task1.id
	}
	
	def "progress when in progress"() {
		setup:
		def user = newUser('user', 'uuid')
		def task1 = new EquipmentExportTask(exportFilterId: 1, user: user, status: TaskStatus.IN_PROGRESS, max: 100, current: 50).save(failOnError: true)
		taskController = new TaskController()
		
		when:
		taskController.params['ids'] = [task1.id]
		taskController.progress()
		def jsonResult = JSONUtils.getMapFromJSON(taskController.response.contentAsString)
		
		then:
		jsonResult.tasks.size() == 1
		jsonResult.tasks[0].status == "IN_PROGRESS"
		jsonResult.tasks[0].progress == 0.5
		jsonResult.tasks[0].id == task1.id
	}
	
	def "progress when task does not exist"() {
		setup:
		taskController = new TaskController()
		
		when:
		taskController.params['ids'] = ['1']
		taskController.progress()
		def jsonResult = JSONUtils.getMapFromJSON(taskController.response.contentAsString)
		
		then:
		jsonResult.tasks.size() == 0
	}
	
	
	def "task form with inexisting class"() {
		setup:
		taskController = new TaskController()
		
		when:
		taskController.params['class'] = 'not_existant'
		taskController.taskForm()
		
		then:
		taskController.response.redirectedUrl == null
	}
	
	def "task form"() {
		setup:
		taskController = new TaskController()
		
		when:
		taskController.params['class'] = 'EquipmentTypeImportTask'
		taskController.params['targetURI'] = '/equipmentType/list'
		taskController.taskForm()
		
		then:
		taskController.modelAndView.model.targetURI != null
		taskController.modelAndView.model.targetURI == '/equipmentType/list'
		taskController.modelAndView.model.task != null
		taskController.modelAndView.viewName == '/task/equipmentTypeImport'		
	}
	
	def "create task with file - normal behaviour"() {
		setup:
		def user = newUser('user', 'uuid')
		setupSecurityManager(user) 
		File tempFileZip = new File("test/integration/org/chai/memms/imports/equipments.zip")
		GrailsMockMultipartFile grailsMockMultipartFileZip = new GrailsMockMultipartFile(
			"equipmentsTestFile", "equipments.zip", "", tempFileZip.getBytes())
		taskController = new TaskController()
		
		when:
		taskController.params['class'] = 'EquipmentTypeImportTask'
		taskController.params.encoding = 'UTF-8'
		taskController.params.delimiter = ','
		taskController.params.file = grailsMockMultipartFileZip
		taskController.createTaskWithFile()
		
		then:
		taskController.response.redirectedUrl == '/'
		Task.count() == 1
		Task.list()[0].encoding == 'UTF-8'
		Task.list()[0].delimiter == ','
		Task.list()[0].inputFilename == 'equipments.zip'
		
	}
	
	def "create task with file validation - no fields"() {
		setup:
		def user = newUser('user', 'uuid')
		setupSecurityManager(user)
		taskController = new TaskController()
		
		when:
		taskController.params['class'] = 'EquipmentTypeImportTask'
		taskController.createTaskWithFile()
		
		then:
		taskController.modelAndView.model.task != null
		taskController.modelAndView.model.taskWithFile != null
		taskController.modelAndView.viewName == '/task/equipmentTypeImport'
		taskController.modelAndView.model.taskWithFile.errors.getFieldErrors('file').size() == 1
		taskController.modelAndView.model.taskWithFile.errors.getFieldErrors('inputFilename').size() == 1
		taskController.modelAndView.model.taskWithFile.errors.getFieldErrors('encoding').size() == 1
		taskController.modelAndView.model.taskWithFile.errors.getFieldErrors('delimiter').size() == 1
		Task.count() == 0
	}
	
	def "create task with file validation - file name not correct"() {
		setup:
		def user = newUser('user', 'uuid')
		setupSecurityManager(user)
		File tempFileZip = new File("test/integration/org/chai/memms/imports/equipments.zip")
		GrailsMockMultipartFile grailsMockMultipartFileZip = new GrailsMockMultipartFile(
			"equipmentsTestFile", "equipments.wrong", "", tempFileZip.getBytes())
		taskController = new TaskController()
		
		when:
		taskController.params['class'] = 'EquipmentTypeImportTask'
		taskController.params.encoding = 'UTF-8'
		taskController.params.delimiter = ','
		taskController.params.file = grailsMockMultipartFileZip
		taskController.createTaskWithFile()
		
		then:
		taskController.modelAndView.model.task != null
		taskController.modelAndView.model.taskWithFile != null
		taskController.modelAndView.viewName == '/task/equipmentTypeImport'
		taskController.modelAndView.model.taskWithFile.errors.getFieldErrors('file').size() == 0
		taskController.modelAndView.model.taskWithFile.errors.getFieldErrors('inputFilename').size() == 1
		taskController.modelAndView.model.taskWithFile.errors.getFieldErrors('encoding').size() == 0
		taskController.modelAndView.model.taskWithFile.errors.getFieldErrors('delimiter').size() == 0
		Task.count() == 0
	}
	
	def "download output when task class does not exist"() {
		setup:
		taskController = new TaskController()
		
		when:
		taskController.params['id'] = 1
		taskController.downloadOutput()
		
		then:
		taskController.response.redirectedUrl == null
	}
	
	def "download output when task is not completed"() {
		setup:
		def user = newUser('user', 'uuid')
		def task = new EquipmentExportTask(exportFilterId: 1, user: user, status: TaskStatus.NEW).save(failOnError: true)
		taskController = new TaskController()
		
		when:
		taskController.params['id'] = task.id
		taskController.downloadOutput()
		
		then:
		taskController.response.redirectedUrl == null
	}
	
	def "download output when task has no output"() {
		setup:
		def user = newUser('user', 'uuid')
		def task = new EquipmentExportTask(exportFilterId: 1, user: user, status: TaskStatus.COMPLETED).save(failOnError: true)
		taskController = new TaskController()
		
		when:
		taskController.params['id'] = task.id
		taskController.downloadOutput()
		
		then:
		taskController.response.redirectedUrl == null
	}
	
	def "download output when output file not found"() {
		setup:
		def user = newUser('user', 'uuid')
		def task = new EquipmentTypeImportTask(user: user, status: TaskStatus.COMPLETED, delimiter: ',', encoding: 'UTF-8',inputFilename: 'test.csv').save(failOnError: true)
		taskController = new TaskController()
		
		when:
		taskController.params['id'] = task.id
		taskController.downloadOutput()
		
		then:
		taskController.flash.message == null
		taskController.response.contentType != "application/zip";
	}
	
	def "download output when output file found"() {
		setup:
		def user = newUser('user', 'uuid')
		def task = new EquipmentTypeImportTask(user: user, status: TaskStatus.COMPLETED,inputFilename: 'test.csv', delimiter: ',', encoding: 'UTF-8').save(failOnError: true)
		task.metaClass.getFolder = { return new File('test/integration/org/chai/memms/imports/') }
		task.metaClass.getOutputFilename = { return "equipments.zip"}
		taskController = new TaskController()
		
		when:
		taskController.params['id'] = task.id
		taskController.downloadOutput()
		
		then:
		taskController.flash.message == null
		taskController.response.outputStream != null
		taskController.response.contentType == "application/zip";
	}
	
}
