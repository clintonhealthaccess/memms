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
package org.chai.memms.inventory

import org.chai.memms.Initializer;
import org.chai.memms.IntegrationTests;
import org.chai.memms.inventory.DepartmentController;
import org.chai.memms.inventory.Department;

class DepartmentControllerSpec extends IntegrationTests{

	def departmentController

	def "create department with correct required data in fields - for english input"(){

		setup:
		departmentController = new DepartmentController();
		when:
		departmentController.params.code = CODE(123)
		departmentController.params.names_en = "testNames"
		departmentController.params.descriptions_en = "test description"
		departmentController.save()

		then:
		Department.count() == 1;
		Department.findByCode(CODE(123)).code.equals(CODE(123))
		Department.findByNames_en("testNames").getNames(new Locale("en")).equals("testNames")
		Department.findByDescriptions_en("test description").getDescriptions(new Locale("en")).equals("test description")
	}

	def "create department with correct required data in fields - for all locales"(){

		setup:
		departmentController = new DepartmentController();
		when:
		departmentController.params.code = CODE(123)
		grailsApplication.config.i18nFields.locales.each{
			departmentController.params."names_$it" = "testNames "+it
			departmentController.params."descriptions_$it" = "test description "+it
		}

		departmentController.save()

		then:
		log.debug("locales " + grailsApplication.config.i18nFields.locales.each{it})
		Department.count() == 1;
		Department.findByCode(CODE(123)).code.equals(CODE(123))

		grailsApplication.config.i18nFields.locales.each{

			Department."findByNames_$it"("testNames $it").getNames(new Locale("$it")).equals("testNames $it")
			Department."findByDescriptions_$it"("test description $it").getDescriptions(new Locale("$it")).equals("test description $it")
		}
	}
	
	def "list department"(){
		setup:
		departmentController = new DepartmentController();
		Initializer.newDepartment(["en":"department test one"],CODE(123),["en":"description department one"])
		Initializer.newDepartment(["en":"department test two"],CODE(124),["en":"description department two"])
		Initializer.newDepartment(["en":"department test three"],CODE(125),["en":"description department three"])
		Initializer.newDepartment(["en":"department test four"],CODE(126),["en":"description department four"])
		
		when: "none ajax"
		departmentController.list()
		then:
		Department.count() == 4;
		departmentController.modelAndView.model.entities.size() == 4
		
		when: "with ajax"
		departmentController.request.content = '{"sort":"code"}'.getBytes()
		departmentController.request.makeAjaxRequest()
		departmentController.list()
		then:
		Department.count() == 4;
		departmentController.response.json.results[0].contains(Department.findByCode(CODE(123)).code)
		departmentController.response.json.results[0].contains(Department.findByCode(CODE(124)).code)
		departmentController.response.json.results[0].contains(Department.findByCode(CODE(125)).code)
		departmentController.response.json.results[0].contains(Department.findByCode(CODE(126)).code)
	}
		
	def "search department"(){
		setup:
		departmentController = new DepartmentController();
		Initializer.newDepartment(["en":"department test one"],CODE(123),["en":"description department one"])
		Initializer.newDepartment(["en":"department test two"],CODE(124),["en":"description department two"])
		Initializer.newDepartment(["en":"department test three"],CODE(125),["en":"description department three"])
		Initializer.newDepartment(["en":"department test four"],CODE(126),["en":"description department four"])

		when:
		departmentController.params.q = "three"
		departmentController.request.makeAjaxRequest()
		departmentController.search()
		then:
		Department.count() == 4;
		!departmentController.response.json.results[0].contains(Department.findByCode(CODE(124)).code)
		!departmentController.response.json.results[0].contains(Department.findByCode(CODE(123)).code)
		departmentController.response.json.results[0].contains(Department.findByCode(CODE(125)).code)
		!departmentController.response.json.results[0].contains(Department.findByCode(CODE(126)).code)
	}
	
	def "none ajax search fails"(){
		setup:
		departmentController = new DepartmentController();
		Initializer.newDepartment(["en":"department test one"],CODE(123),["en":"description department one"])
		Initializer.newDepartment(["en":"department test two"],CODE(124),["en":"description department two"])
		Initializer.newDepartment(["en":"department test three"],CODE(125),["en":"description department three"])
		Initializer.newDepartment(["en":"department test four"],CODE(126),["en":"description department four"])

		when: "none ajax search fails"
		departmentController.params.q = "one"
		departmentController.search()
		then:
		Department.count() == 4
		departmentController.response.status == 404
	}
}
