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
/**
 *
 * @author Dusabe Eric, Kahigiso M. Jean
 */

class HmisFacilityReportSpec extends IntegrationTests{

	def "can create and save a HmisFacilityReport"() {
		setup:
		setupLocationTree()
		def hmisReport = new HmisReport(periodCode:CODE(123),names:["en":"HmisReport test"]).save(failOnError:true)
		def hmisEquipmentType = Initializer.newHmisEquipmentType(CODE(123),["en":"HmisReport test"])
		when:
		def hmisFacilityReport = new HmisFacilityReport(
			hmisReport:hmisReport,
			dataLocation: DataLocation.findByCode(GITWE),
			hmisEquipmentType:HmisEquipmentType.findByCode(CODE(123)),
			numberOfOpEquipment:0)
		hmisFacilityReport.save(failOnError:true)
		then:
		HmisFacilityReport.count() == 1
		HmisFacilityReport.list()[0].dataLocation.id == DataLocation.findByCode(GITWE).id
	}

	def "can't create and save a HmisFacilityReport without dataLocation"() {
		setup:
		setupLocationTree()
		def hmisReport = new HmisReport(periodCode:CODE(123),names:["en":"HmisReport test"]).save(failOnError:true)
		def hmisEquipmentType = Initializer.newHmisEquipmentType(CODE(123),["en":"HmisReport test"])
		when:
		def hmisFacilityReport = new HmisFacilityReport(
			hmisReport:hmisReport,
			hmisEquipmentType:HmisEquipmentType.findByCode(CODE(123)),
			numberOfOpEquipment:0)
		hmisFacilityReport.save()
		then:
		HmisFacilityReport.count() == 0
		hmisFacilityReport.errors.hasFieldErrors('dataLocation') == true

	}

	def "can't create and save a  HmisFacilityReport with negative numberOfOpEquipment "() {
		setup:
		setupLocationTree()
		def hmisReport = new HmisReport(periodCode:CODE(123),names:["en":"HmisReport test"]).save(failOnError:true)
		def hmisEquipmentType = Initializer.newHmisEquipmentType(CODE(123),["en":"HmisReport test"])
		when:
		def hmisFacilityReport = new HmisFacilityReport(
			hmisReport:hmisReport,
			dataLocation: DataLocation.findByCode(GITWE),
			hmisEquipmentType:HmisEquipmentType.findByCode(CODE(123)),
			numberOfOpEquipment:-1)
		hmisFacilityReport.save()

		def hmisFacilityReportTwo = new HmisFacilityReport(
			hmisReport:hmisReport,
			dataLocation: DataLocation.findByCode(GITWE),
			hmisEquipmentType:HmisEquipmentType.findByCode(CODE(123)),
			numberOfOpEquipment:5)
		hmisFacilityReportTwo.save()
		then:
		HmisFacilityReport.count() == 1
		hmisFacilityReport.errors.hasFieldErrors('numberOfOpEquipment') == true
		HmisFacilityReport.list()[0].numberOfOpEquipment==5

	}
	def "can't create and save a  HmisFacilityReport with lastUpdated date in future "() {
		setup:
		setupLocationTree()
		def hmisReport = new HmisReport(periodCode:CODE(123),names:["en":"HmisReport test"]).save(failOnError:true)
		def hmisEquipmentType = Initializer.newHmisEquipmentType(CODE(123),["en":"HmisReport test"])
		def today = new Date()
		when:
		def hmisFacilityReport = new HmisFacilityReport(
			hmisReport:hmisReport,
			dataLocation: DataLocation.findByCode(GITWE),
			hmisEquipmentType:HmisEquipmentType.findByCode(CODE(123)),
			numberOfOpEquipment:1,
			lastUpdated:today)
		hmisFacilityReport.save()

		def hmisFacilityReportTwo = new HmisFacilityReport(
			hmisReport:hmisReport,
			dataLocation: DataLocation.findByCode(GITWE),
			hmisEquipmentType:HmisEquipmentType.findByCode(CODE(123)),
			numberOfOpEquipment:5,
			lastUpdated:today+1)
		hmisFacilityReportTwo.save()
		then:
		HmisFacilityReport.count() == 1
		hmisFacilityReportTwo.errors.hasFieldErrors('lastUpdated') == true
		HmisFacilityReport.list()[0].numberOfOpEquipment==1

	}

}