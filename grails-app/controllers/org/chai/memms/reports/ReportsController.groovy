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
package org.chai.memms.reports

import org.chai.memms.AbstractController;

/**
 * @author Jean Kahigiso M.
 *
 */
class ReportsController extends AbstractController{

	def dashboard ={
		render(view: '/entity/reports', model: 
			[
				template:"/entity/reports/dashboard"
			])
	}	

	def listing ={
		render(view: '/entity/reports', model: 
			[
				template:"/entity/reports/listing"
			])
	}

	def step1 ={
		if (log.isDebugEnabled()) log.debug("reports.step1, params:"+params)

		def reportType = getReportType('reportType')
		def reportSubType = getReportType('reportSubType')

		render(template:"/entity/reports/step1", 
			model: 
			[
				reportType: reportType,
				reportSubType: reportSubType		
			])
	}

	def step2 ={
		if (log.isDebugEnabled()) log.debug("reports.step2, params:"+params)

		def reportType = getReportType('reportType')
		def reportSubType = getReportType('reportSubType')

		render(template:"/entity/reports/step2", 
			model: 
			[
				reportType: reportType,
				reportSubType: reportSubType		
			])
	}

	def step3 ={
		if (log.isDebugEnabled()) log.debug("reports.step3, params:"+params)

		def reportType = params.get('reportType')
		def reportSubType = params.get('reportSubType')

		render(template:"/entity/reports/step3", 
			model: 
			[
				reportType: reportType,
				reportSubType: reportSubType		
			])
	}

	def step4 ={
		if (log.isDebugEnabled()) log.debug("reports.step4, params:"+params)

		def reportType = params.get('reportType')
		def reportSubType = params.get('reportSubType')

		render(template:"/entity/reports/step4", 
			model: 
			[
				reportType: reportType,
				reportSubType: reportSubType		
			])
	}

	def customizedListing ={
		// TODO
	}

	def customizedReportSubType ={
		if (log.isDebugEnabled()) 
			log.debug("reports.step1.customizedReportSubType, params:"+params)

		def reportType = getReportType('reportType')

		render(template:"/entity/reports/customizedReportSubType", 
			model: 
			[
				reportType: reportType
			])
	}
}
