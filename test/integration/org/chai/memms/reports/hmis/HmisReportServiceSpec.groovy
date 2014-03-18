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
import org.chai.memms.IntegrationTests
import org.chai.memms.Initializer
import org.chai.location.DataLocation;
import org.joda.time.DateTime
import org.joda.time.Instant
import org.chai.memms.inventory.Equipment
import org.chai.memms.inventory.EquipmentType
import org.chai.memms.inventory.EquipmentType.Observation
import org.chai.memms.util.Utils;

/**
 * @author Eric Dusabe, Jean Kahigiso M.
 *
 */


class HmisReportServiceSpec extends IntegrationTests {
	
	def hmisReportService

	def "test if report hmis report label are well generated"() {
		setup:

		def april = new DateTime(createDate(1,4,2014))
		def july = new DateTime(createDate(1,7,2013))
		def october = new DateTime(createDate(1,10,2015))
		def january = new DateTime(createDate(1,1,2016))
		def today = new DateTime(new Date())

		when:

		def mapQrtOne = hmisReportService.generateHmisReportName(april)
		def mapQrtTwo = hmisReportService.generateHmisReportName(july)
		def mapQrtThree = hmisReportService.generateHmisReportName(october)
		def mapQrtFour = hmisReportService.generateHmisReportName(january)
		def map = hmisReportService.generateHmisReportName(today)

		then:

		mapQrtOne.get("periodCode").equals("Q1-"+april.getYear().toString())
		mapQrtTwo.get("periodCode").equals( "Q2-"+july.getYear().toString())
		mapQrtThree.get("periodCode").equals("Q3-"+october.getYear().toString())
		mapQrtFour.get("periodCode").equals("Q4-"+january.getYear().toString())
		map.get("periodCode").equals(today.toString())
	}

	def "test if report hmis report generateHmisReport(def date)"() {
		setup:
		//Set up location structure
		setupLocationTree()
		//Set first equipment
		setupEquipment()
		
		//Create equimentType
		def eqTypeTwo = Initializer.newEquipmentType(CODE(134),["en":"Accelerometers"],["en":"used in memms"],Observation.USEDINMEMMS,Initializer.now(),12)
		def eqTypeThree = Initializer.newEquipmentType(CODE(135),["en":"Accelerometers"],["en":"used in memms"],Observation.USEDINMEMMS,Initializer.now(),11)

		//Create hmisEquipment Types
		def hmisEquipmentTypeOne = Initializer.newHmisEquipmentType(CODE(114), ["en":"hmis Eq type one","fr":"hmis Eq type one fr"]) 
		def hmisEquipmentTypeTwo = Initializer.newHmisEquipmentType(CODE(134), ["en":"hmis Eq type two","fr":"hmis Eq type two fr"]) 
		
		
		//Associate hmisEquipmentType with EquipmentType
		hmisEquipmentTypeOne.addToEquipmentTypes(eqTypeTwo)
		hmisEquipmentTypeOne.addToEquipmentTypes(EquipmentType.findByCode(CODE(123)))
		hmisEquipmentTypeOne.save(failOnError:true)
		hmisEquipmentTypeTwo.addToEquipmentTypes(eqTypeThree)
		hmisEquipmentTypeTwo.save(failOnError:true)

		//Create equipments
		def equiThree = newEquipment(CODE(125),DataLocation.findByCode(KIVUYE))
		def equiFour = newEquipment(CODE(126),DataLocation.findByCode(BUTARO))
		def equiTwo = newEquipment(CODE(124),DataLocation.findByCode(BUTARO))
		def equiSix = newEquipment(CODE(128),DataLocation.findByCode(BUTARO))
		def equiFive = newEquipment(CODE(127),DataLocation.findByCode(MUSANZE))

		//Change equipment's types
		eqTypeTwo.addToEquipments(equiThree)
		eqTypeTwo.save(failOnError:true)
		eqTypeThree.addToEquipments(equiSix)
		eqTypeThree.save(failOnError:true)

		when:
		def today = new DateTime(new Date())
		hmisReportService.generateHmisReport(today)
		then:
		HmisReport.count() == 1
		HmisReport.list()[0].hmisFacilityReports.size() ==  6
	
	}

	

	static  def createDate( def day, def month, def year) {
	   final Calendar calendar = Calendar.getInstance();

	   calendar.clear();
	   calendar.set( Calendar.YEAR, year );
	   calendar.set( Calendar.MONTH, month - 1 );
	   calendar.set( Calendar.DAY_OF_MONTH, day );

	   return calendar.getTime();
   }


}