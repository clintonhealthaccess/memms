package org.chai.memms.equipment

import org.chai.memms.IntegrationTests;

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
}
