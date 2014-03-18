package org.chai.memms.task

import org.chai.memms.AbstractController;
import org.chai.task.Task

class TaskListController extends AbstractController{
	/**
	 * Lists all tasks currently saved in the database
	 */
	def list = {
		adaptParamsForList()
		def tasks = Task.list(offset:params.offset,max:params.max,sort:params.sort ?:"id",order: params.order ?:"desc")
		if(request.xhr)
			this.ajaxModel(tasks)
		else{
			render (view: '/entity/task/list', model:[
				entities: tasks,
				template: "task/taskList",
				listTop:"task/listTop",
				code: 'task.label',
				entityCount: Task.count(),
				entityClass: Task.class,
				search: true
			])
		}
	}
	def ajaxModel(def entities) {
		def model = [entities: entities,entityCount: entities.totalCount]
		def listHtml = g.render(template:"/entity/task/taskList",model:model)
		render(contentType:"text/json") { results = [listHtml] }
	}

}
