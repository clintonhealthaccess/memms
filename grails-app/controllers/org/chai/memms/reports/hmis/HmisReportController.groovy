/**
 * Copyright (c) 2012, Clinton Health Access Initiative.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the <organization> nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.chai.memms.reports.hmis

import org.chai.location.Location
import org.chai.memms.AbstractController
import org.chai.memms.inventory.EquipmentType
import org.chai.memms.security.User
import org.apache.shiro.SecurityUtils
import org.apache.commons.lang.math.NumberUtils
import org.chai.task.HmisEquipmentTypeExportFilter
import org.joda.time.DateTime
import org.joda.time.Instant


/**
 * @author Eric Dusabe, Jean Kahigiso M.
 *
 */
class HmisReportController extends AbstractController {

	def hmisReportService

	def getEntityClass() {
		return HmisReport.class;
	}

	def getLabel() {
		return "hmis.report.label";
	}	

	def generateReport = {
		hmisReportService.generateHmisReport(new DateTime(now))
	}


	def export = {
		if(params.int('id')) {
			def hmisReport = HmisReport.get(params.int('id'))
			def hmisReportExportTask = new HmisEquipmentTypeExportFilter(hmisReport:hmisReport).save(failOnError: true,flush: true)
			params.exportFilterId = hmisReportExportTask.id
			params.class = "HmisEquipmentTypeTask"
			params.targetURI = "/hmisReport/list"
			redirect(controller: "task", action: "create", params: params)
		}
	}

	def list = {
		adaptParamsForList()
		def types = HmisReport.list(offset:params.offset,max:params.max,sort:params.sort ?:"id",order: params.order ?:"desc");
		if(request.xhr)
			this.ajaxModel(types,"")
		else{
			render(view:"/entity/list",model:model(types) << [
				template:"hmis/hmisReportList",
				listTop:"hmis/listTop"
			])
		}
	}

	def search = {
		adaptParamsForList()
		List<HmisReport> types = hmisReportService.searchHmisReport(params['q'],params)
		if(request.xhr)
			this.ajaxModel(types,params['q'])
		else {
			render(view:"/entity/list",model:model(types) << [
				template:"hmisReport/hmisReportList",
				listTop:"hmisReport/listTop"
			
			])
		}
	}

	def model(def entities) {
		return [
			entities: entities,
			entityCount: entities.totalCount,
			entityClass:getEntityClass(),
			code: getLabel()
		]
	}
	
	def ajaxModel(def entities,def searchTerm) {
		def model = model(entities) << [q:searchTerm]
		def listHtml = g.render(template:"/entity/hmis/hmisReportList",model:model)
		render(contentType:"text/json") { results = [listHtml] }
	}

	
}