package org.chai.memms.task

import grails.validation.ValidationException;

import org.apache.commons.io.FileUtils;
import org.chai.memms.IntegrationTests;
import org.chai.task.Task.TaskStatus
import org.chai.task.EquipmentTypeImportTask;
import org.chai.task.FileType;
import org.chai.task.ImportTask;
import org.chai.task.Task
import org.codehaus.groovy.grails.plugins.testing.GrailsMockMultipartFile;

class ImportTaskSpec extends IntegrationTests {

	def "test constraints"() {
		setup:
		def user = newUser('user', 'uuid')
		
		when:
		new EquipmentTypeImportTask( user: user, status: TaskStatus.NEW, encoding: 'UTF-8', delimiter: ',', inputFilename: 'test.csv').save(failOnError: true)
		
		then:
		Task.count() == 1
		
		when:
		new EquipmentTypeImportTask( user: user, status: TaskStatus.NEW, delimiter: ',', inputFilename: 'test.csv').save(failOnError: true)
		
		then:
		thrown ValidationException
		
		when:
		new EquipmentTypeImportTask(user: user, status: TaskStatus.NEW, encoding: 'UTF-8', inputFilename: 'test.csv').save(failOnError: true)
		
		then:
		thrown ValidationException
		
		when:
		new EquipmentTypeImportTask(user: user, status: TaskStatus.NEW, encoding: 'UTF-8', delimiter: ',').save(failOnError: true)
		
		then:
		thrown ValidationException
		
		when:
		new EquipmentTypeImportTask(user: user, status: TaskStatus.NEW, encoding: 'UTF-8', delimiter: ',', inputFilename: 'test.pdf').save(failOnError: true)
		
		then:
		thrown ValidationException
	}
	
//	def "test constraints - NominativeImportTask"() {
//		setup:
//		def user = newUser('user', 'uuid')
//		
//		when:
//		new NominativeImportTask(rawDataElementId: 1, periodId: 1, user: user, status: TaskStatus.NEW, encoding: 'UTF-8', delimiter: ',', inputFilename: 'test.csv').save(failOnError: true)
//		
//		then:
//		Task.count() == 1
//		
//		when:
//		new NominativeImportTask(periodId: 1, user: user, status: TaskStatus.NEW, encoding: 'UTF-8', delimiter: ',', inputFilename: 'test.csv').save(failOnError: true)
//		
//		then:
//		thrown ValidationException
//		
//		when:
//		new NominativeImportTask(rawDataElementId: 1, user: user, status: TaskStatus.NEW, encoding: 'UTF-8', delimiter: ',', inputFilename: 'test.csv').save(failOnError: true)
//		
//		then:
//		thrown ValidationException
//	}
//	
//	def "test constraints - GeneralImportTask"() {
//		setup:
//		def user = newUser('user', 'uuid')
//		
//		when:
//		new GeneralImportTask(user: user, status: TaskStatus.NEW, encoding: 'UTF-8', delimiter: ',', inputFilename: 'test.csv').save(failOnError: true)
//		
//		then:
//		Task.count() == 1
//	}
//	
//	def "test constraints - EntityImportTask"() {
//		setup:
//		def user = newUser('user', 'uuid')
//		
//		when:
//		new EntityImportTask(entityClass: 'Class', user: user, status: TaskStatus.NEW, encoding: 'UTF-8', delimiter: ',', inputFilename: 'test.csv').save(failOnError: true)
//		
//		then:
//		Task.count() == 1
//		
//		when:
//		new EntityImportTask(user: user, status: TaskStatus.NEW, encoding: 'UTF-8', delimiter: ',', inputFilename: 'test.csv').save(failOnError: true)
//		
//		then:
//		thrown ValidationException
//	}
	
	def "test execute - NominativeImportTask"() {
		setup:
		def user = newUser('user', 'uuid')
		def task = new EquipmentTypeImportTask(user: user, status: TaskStatus.NEW, encoding: 'UTF-8', delimiter: ',', inputFilename: 'equipments.zip').save(failOnError: true)
		FileUtils.deleteDirectory(task.folder)
		new File(task.folder, task.inputFilename).createNewFile()
		
		when:
		task.executeTask()
		
		then:
		new File(task.folder, 'equipmentTypeimportlog.txt').exists()
		
		// tearDown
		FileUtils.deleteDirectory(task.folder)
	}
	
//	def "test execute - NominativeImportTask when no file"() {
//		setup:
//		def user = newUser('user', 'uuid')
//		def period = newPeriod()
//		def dataElement = newRawDataElement(CODE(1), Type.TYPE_NUMBER())
//		def task = new NominativeImportTask(rawDataElementId: dataElement.id, periodId: period.id, user: user, status: TaskStatus.NEW, encoding: 'UTF-8', delimiter: ',', inputFilename: 'nominativeTestFile.csv.zip').save(failOnError: true)
//		
//		when:
//		task.executeTask()
//		
//		then:
//		thrown IllegalStateException
//		
//		// tearDown
//		FileUtils.deleteDirectory(task.folder)
//	}
//	
//	def "test execute - GeneralImportTask"() {
//		setup:
//		def user = newUser('user', 'uuid')
//		def task = new GeneralImportTask(user: user, status: TaskStatus.NEW, encoding: 'UTF-8', delimiter: ',', inputFilename: 'nominativeTestFile.csv.zip').save(failOnError: true)
//		FileUtils.deleteDirectory(task.folder)
//		new File(task.folder, task.inputFilename).createNewFile()
//		
//		when:
//		task.executeTask()
//		
//		then:
//		new File(task.folder, 'importOutput.txt').exists()
//		
//		// tearDown
//		FileUtils.deleteDirectory(task.folder)
//	}
//	
//	def "test execute - GeneralImportTask when no file"() {
//		setup:
//		def user = newUser('user', 'uuid')
//		def task = new GeneralImportTask(user: user, status: TaskStatus.NEW, encoding: 'UTF-8', delimiter: ',', inputFilename: 'nominativeTestFile.csv.zip').save(failOnError: true)
//		
//		when:
//		task.executeTask()
//		
//		then:
//		thrown IllegalStateException
//		
//		// tearDown
//		FileUtils.deleteDirectory(task.folder)
//	}
//	
//	def "test execute - EntityImportTask"() {
//		setup:
//		def user = newUser('user', 'uuid')
//		def task = new EntityImportTask(entityClass: 'org.chai.kevin.location.DataLocation', user: user, status: TaskStatus.NEW, encoding: 'UTF-8', delimiter: ',', inputFilename: 'nominativeTestFile.csv.zip').save(failOnError: true)
//		FileUtils.deleteDirectory(task.folder)
//		new File(task.folder, task.inputFilename).createNewFile()
//		
//		when:
//		task.executeTask()
//		
//		then:
//		new File(task.folder, 'importOutput.txt').exists()
//		
//		// tearDown
//		FileUtils.deleteDirectory(task.folder)
//	}
//	
//	def "test execute - EntityImportTask when no file"() {
//		setup:
//		def user = newUser('user', 'uuid')
//		def task = new EntityImportTask(entityClass: 'org.chai.kevin.location.DataLocation', user: user, status: TaskStatus.NEW, encoding: 'UTF-8', delimiter: ',', inputFilename: 'nominativeTestFile.csv.zip').save(failOnError: true)
//		
//		when:
//		task.executeTask()
//		
//		then:
//		thrown IllegalStateException
//		
//		// tearDown
//		FileUtils.deleteDirectory(task.folder)
//	}
//	
//	def "test get model and view - NominativeImportTask"() {
//		setup:
//		def user = newUser('user', 'uuid')
//		def period = newPeriod()
//		def dataElement = newRawDataElement(CODE(1), Type.TYPE_NUMBER())
//		def task = new NominativeImportTask(rawDataElementId: dataElement.id, periodId: period.id, user: user, status: TaskStatus.NEW, encoding: 'UTF-8', delimiter: ',', inputFilename: 'nominativeTestFile.csv.zip').save(failOnError: true)
//		
//		expect:
//		task.formModel == [
//			task: task,
//			periods: [period],
//			dataElements: [dataElement]	
//		]
//		task.formView == 'nominativeImport'
//	}
//	
//	def "test get model and view - GeneralImportTask"() {
//		setup:
//		def user = newUser('user', 'uuid')
//		def task = new GeneralImportTask(user: user, status: TaskStatus.NEW, encoding: 'UTF-8', delimiter: ',', inputFilename: 'nominativeTestFile.csv.zip').save(failOnError: true)
//		
//		expect:
//		task.formModel == [
//			task: task
//		]
//		task.formView == 'generalImport'
//	}
//	
//	def "test get model and view - EntityImportTask"() {
//		setup:
//		def user = newUser('user', 'uuid')
//		def task = new EntityImportTask(entityClass: 'org.chai.kevin.location.DataLocation', user: user, status: TaskStatus.NEW, encoding: 'UTF-8', delimiter: ',', inputFilename: 'nominativeTestFile.csv.zip').save(failOnError: true)
//		
//		expect:
//		task.formModel == [
//			task: task
//		]
//		task.formView == 'entityImport'
//	}
	
	def "test get folder"() {
		setup:
		setupLocationTree()
		def user = newUser('user', 'uuid')
		
		when:
		def task = new EquipmentTypeImportTask(user: user, status: TaskStatus.NEW, encoding: 'UTF-8', delimiter: ',', inputFilename: 'equipments.zip').save(failOnError: true)
		
		then:
		task.getFolder() == new File('files/'+task.id)
		
		// tearDown
		FileUtils.deleteDirectory(task.folder)
	}
	
	def "test clean task"() {
		setup:
		setupLocationTree()
		def user = newUser('user', 'uuid')
		
		when:
		def task = new EquipmentTypeImportTask(user: user, status: TaskStatus.NEW, encoding: 'UTF-8', delimiter: ',', inputFilename: 'equipments.zip').save(failOnError: true)
		task.getFolder()
		
		then:
		new File('files/'+task.id).exists()
		
		when:
		task.cleanTask()
		
		then:
		!new File('files/'+task.id).exists()  
	}
	
	def "check the type of files uploaded"() {
		expect:
		ImportTask.getFileType('equipments.zip') == FileType.ZIP
		ImportTask.getFileType('equipments.csv') == FileType.CSV
		ImportTask.getFileType('Git1.pdf') == FileType.NONE
	}
	
}
