package org.chai.memms.task

import org.apache.commons.lang.NotImplementedException
import org.chai.memms.AbstractController
import org.chai.memms.task.Task.TaskStatus
import org.chai.memms.util.Utils
import org.springframework.web.multipart.MultipartFile

class TaskController extends AbstractController {
	
	def grailsApplication
	def taskService
	
	/**
	 * Lists all tasks currently saved in the database
	 */
	def list = {
		adaptParamsForList()
		
		def tasks = Task.list(params)
		
		render (view: '/entity/task/list', model:[
			entities: tasks,
			template: "task/taskList",
			code: 'task.label',
			entityCount: Task.count(),
			entityClass: Task.class,
			search: true
		])
	}
	
	/**
	 * Ajax call that gets the progress of an individual task
	 */
	def progress = {
		if (log.isDebugEnabled()) log.debug("task.progress, params:"+params)
		
		def taskIds = params.list('ids')
		def taskList = taskIds.collect {Task.get(it)}
		taskList = taskList - null
		
		render(contentType:"text/json") {
			tasks = array {
				taskList.each { task ->
					t (
						id: task.id,
						status: task.status.name(),
						progress: task.retrievePercentage()
					)
				}
			}
		}
	}
	
	/**
	 * Displays the task creation form according to the specified task class
	 */
	def taskForm = {
		Class taskClass = getTaskClass()
		if (taskClass != null) {
			if (log.isDebugEnabled()) log.debug("task.taskForm, taskClass:"+taskClass)
			def task = taskClass.newInstance()
			render (view: '/task/'+task.getFormView(), model: task.getFormModel())
		}
		else {
			if (log.isDebugEnabled()) log.debug("task.taskForm, taskClass.notfound")
			response.sendError(404)
		}
	}
	
	/**
	 * Creates a task from a form, renders the form if the task is not valid, 
	 * redirects to targetURI otherwise. Expects a file, and creates a temp file.
	 */
	def createTaskWithFile = { TaskWithFileCommand cmd ->
		if (log.isDebugEnabled()) log.debug("task.createTaskWithFile, params:"+params)
		
		Class taskClass = getTaskClass()
		if (taskClass == null) {
			response.sendError(404)
		}
		else {
			def task = taskClass.newInstance()
			def hasErrors = false
			if (!cmd.hasErrors()) {
				task.inputFilename = cmd.file.originalFilename
				
				task.properties = params
				if (create(task)) {
					// create input file and copy content to file on disk
					def file = new File(task.folder, task.inputFilename)
					cmd.file.transferTo(file)
				}
				else {
					hasErrors = true
				}
			}
			else {
				hasErrors = true
				
				task.properties = params
				fillFields(task)
				task.validate()
			}
						
			if (hasErrors) {
				if (task.errors.hasFieldErrors('inputFilename')) cmd.errors.addError(task.errors.getFieldError('inputFilename'))
				
				def model = task.getFormModel()
				model.taskWithFile = cmd
				render (view: '/task/'+task.getFormView(), model: model)
			}
			else redirect(uri: targetURI)
		}
	}
	
	def downloadOutput = {
		def task = Task.get(params.int('id'))
		if (task != null && task.status == TaskStatus.COMPLETED && task.outputFilename != null) {
			def file = new File(task.getFolder(), task.outputFilename)
			if (!file.exists()) {
				flash.message = message(code: 'task.output.not.found')
			}
			else {
				def zipFile = Utils.getZipFile(file, 'output.zip')
				
				if(zipFile.exists()){
					response.setHeader("Content-disposition", "attachment; filename=" + zipFile.getName());
					response.setContentType("application/zip");
					response.setHeader("Content-length", zipFile.length().toString());
					response.outputStream << zipFile.newInputStream()
				}
			}
		}
		else {
			response.sendError(404)
		}
	}
	
	/**
	 * Creates a task from a form, renders the form if the task is not valid, 
	 * redirects to targetURI otherwise
	 */
	def createTask = {
		throw new NotImplementedException()
	}
	
	/**
	 * Silently creates a task and sends it for processing
	 */
	def create = {
		if (log.isDebugEnabled()) log.debug("task.create, params:"+params)
		
		Class taskClass = getTaskClass()
		if (taskClass != null) {
			def task = taskClass.newInstance()
			
			task.properties = params
			def valid = create(task)
			if (!valid && !flash.message) flash.message = message(code: 'task.creation.validation.error')
			
			redirect(uri: targetURI)
		}
		else {
			response.sendError(404)
		}
	}
	
	private def getTaskClass() {
		Class taskClass
		try {
			if (params.get('class') != null){
				log.debug("task.taskForm class param in: params=" + params)
				taskClass = Class.forName('org.chai.memms.task.'+params['class'], true, Thread.currentThread().contextClassLoader)
			}
		} catch (ClassNotFoundException e) {}
		return taskClass
	}
	
	private def fillFields(def task) {
		// we fill the default fields
		task.status = TaskStatus.NEW
		task.user = user
		task.added = new Date()
	}
	
	private def create(def task) {
		// we set the fields
		fillFields(task)
		
		if (task.validate()) {
			// we check that it doesn't already exist
			if (!task.isUnique()) {
				flash.message = message(code: 'task.creation.notunique.error', args: [createLink(controller: 'task', action: 'list')])
				return false;
			}
			else {
				// we save it
				task.save(failOnError: true)
				
				// we send it for processing
				taskService.sendToQueue(task)
				
				// we redirect to the list
				flash.message = message(code: 'task.creation.success', args: [createLink(controller: 'task', action: 'list')])
				return true;
			}
		}
		else {
			if (log.isInfoEnabled()) log.info ("validation error in ${task}: ${task.errors}}")
			return false;
		}
	}
	
	def purge = {
		def tasks = Task.findAllByStatusInList([TaskStatus.COMPLETED, TaskStatus.ABORTED])
		
		tasks.each { task -> 
			task.cleanTask()
			task.delete()
		}
		redirect(action: 'list');
	}
	
	def delete = {
		if (log.isDebugEnabled()) log.debug("task.delete, params:"+params)
		
		def entity = Task.get(params.int('id'))
		if (entity != null) {
			if (entity.status != TaskStatus.IN_PROGRESS || !entity.sentToQueue) {
				try {
					entity.cleanTask()
					entity.delete()
					
					if (!flash.message) flash.message = message(code: 'default.deleted.message', args: [message(code: 'task.label', default: 'entity'), params.id])
					redirect(uri: targetURI)
				}
				catch (org.springframework.dao.DataIntegrityViolationException e) {
					flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'task.label', default: 'entity'), params.id])
					redirect(uri: targetURI)
				}
			}
			else {
				entity.abort()
				
				flash.message = message(code: 'task.aborting.message')
				redirect(uri: targetURI)
			}
		}
		else {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'task.label', default: 'entity'), params.id])
			redirect(uri: targetURI)
		}
	}
	
}

class TaskWithFileCommand {
	String encoding
	String delimiter
	MultipartFile file
	
	static constraints = {
		file(blank:false, nullable:false)
		delimiter(blank:false, nullable:false)
		encoding(blank:false, nullable:false)
	}
}
	