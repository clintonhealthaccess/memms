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

package org.chai.memms.equipment

import java.util.Map;

import org.chai.memms.Initializer;
import org.chai.memms.IntegrationTests;
import org.chai.memms.equipment.EquipmentStatus.Status;
import org.chai.memms.equipment.EquipmentType.Observation;
import org.chai.memms.location.DataLocation;
import org.chai.memms.security.User;
import org.chai.memms.IntegrationTests
import org.chai.memms.location.DataLocation;
import org.chai.memms.equipment.Provider.Type;

class EquipmentServiceSpec extends IntegrationTests{

	def equipmentService
	def "can search equipment by serial number, description and observation"() {
		setup:
		setupLocationTree()

		def manufactureContact = Initializer.newContact(['en':'Address Descriptions '],"Manufacture","jkl@yahoo.com","0768-889-787","Street 154","6353")
		def supplierContact = Initializer.newContact([:],"Supplier","jk@yahoo.com","0768-888-787","Street 1654","6353")

		def manufacture = Initializer.newProvider(CODE(123), Type.MANUFACTURE,manufactureContact)
		def supplier = Initializer.newProvider(CODE(124), Type.SUPPLIER,supplierContact)
		def user  = newUser("admin", "Admin UID")
		def department = Initializer.newDepartment(['en':"testName"], CODE(123),['en':"testDescription"])
		def equipmentModel = Initializer.newEquipmentModel(['en':"testName"], CODE(123),['en':"testDescription"])
		def equipmentType = Initializer.newEquipmentType(CODE(15810),["en":"Accelerometers"],["en":"used in memms"],Observation.USEDINMEMMS,Initializer.now(),Initializer.now())

		Initializer.newEquipment("SERIAL10",true,false,32,"ROOM A1","2900.23",['en':'Equipment Descriptions one'],Initializer.getDate(22,07,2010)
				,Initializer.getDate(10,10,2010),new Date(),"equipmentModel",DataLocation.list().first(),department,equipmentType,manufacture,supplier)
		Initializer.newEquipment("SERIAL11",true,false,32,"ROOM A1","2900.23",['en':'Equipment Descriptions two'],Initializer.getDate(22,07,2010)
				,Initializer.getDate(10,10,2010),new Date(),"equipmentModel",DataLocation.list().first(),department,equipmentType,manufacture,supplier)
		def List<Equipment> equipments

		when://Searching by serial number

		equipments = equipmentService.searchEquipment("SERIAL11",DataLocation.list().first(), [:])
		then:
		equipments.size() == 1
		equipments[0].serialNumber.equals("SERIAL11")

		when://Searching by observation

		equipments = equipmentService.searchEquipment("one",DataLocation.list().first(), [:])
		then:
		equipments.size() == 1
		equipments[0].getDescriptions(new Locale("en")).equals('Equipment Descriptions one')

		when://Searching by description

		equipments = equipmentService.searchEquipment("Two",DataLocation.list().first(), [:])
		then:
		equipments.size() == 1
		equipments[0].getDescriptions(new Locale("en")).equals('Equipment Descriptions two')
	}

	def "get equipment by datalocation"() {
		setup:
		setupLocationTree()

		def manufactureContact = Initializer.newContact(['en':'Address Descriptions '],"Manufacture","jkl@yahoo.com","0768-889-787","Street 154","6353")
		def supplierContact = Initializer.newContact([:],"Supplier","jk@yahoo.com","0768-888-787","Street 1654","6353")

		def manufacture = Initializer.newProvider(CODE(123), Type.MANUFACTURE,manufactureContact)
		def supplier = Initializer.newProvider(CODE(124), Type.SUPPLIER,supplierContact)
		def user  = newUser("admin", "Admin UID")
		def department = Initializer.newDepartment(['en':"testName"], CODE(123),['en':"testDescription"])
		def equipmentModel = Initializer.newEquipmentModel(['en':"testName"], CODE(123),['en':"testDescription"])
		def equipmentType = Initializer.newEquipmentType(CODE(15810),["en":"Accelerometers"],["en":"used in memms"],Observation.USEDINMEMMS,Initializer.now(),Initializer.now())

		Initializer.newEquipment("SERIAL10",true,false,32,"ROOM A1","2900.23",['en':'Equipment Descriptions one'],Initializer.getDate(22,07,2010)
				,Initializer.getDate(10,10,2010),new Date(),"equipmentModel",DataLocation.findByCode('Kivuye HC'),department,equipmentType,manufacture,supplier)
		Initializer.newEquipment("SERIAL11",true,false,32,"ROOM A1","2900.23",['en':'Equipment Descriptions two'],Initializer.getDate(22,07,2010)
				,Initializer.getDate(10,10,2010),new Date(),"equipmentModel",DataLocation.findByCode('Butaro DH'),department,equipmentType,manufacture,supplier)
		def List<Equipment> equipments

		when:
		equipments = equipmentService.getEquipmentsByDataLocation(DataLocation.findByCode('Kivuye HC'), [:])

		then:
		equipments.size() == 1
		equipments[0].getDescriptions(new Locale("en")).equals('Equipment Descriptions one')
	}

	def "filter equipments"() {
		setup:
		setupLocationTree()

		def manufactureContact = Initializer.newContact(['en':'Address Descriptions '],"Manufacture","jkl@yahoo.com","0768-889-787","Street 154","6353")
		def supplierContact = Initializer.newContact([:],"Supplier","jk@yahoo.com","0768-888-787","Street 1654","6353")
		def manufacture = Initializer.newProvider(CODE(111), Type.MANUFACTURE,manufactureContact)
		def supplier = Initializer.newProvider(CODE(222), Type.SUPPLIER,supplierContact)
		
		def user  = newUser("admin", "Admin UID")
		def department = Initializer.newDepartment(['en':"testName"], CODE(123),['en':"testDescription"])
		def equipmentModel = Initializer.newEquipmentModel(['en':"testName"], CODE(123),['en':"testDescription"])
		def equipmentType = Initializer.newEquipmentType(CODE(15810),["en":"Accelerometers"],["en":"used in memms"],Observation.USEDINMEMMS,Initializer.now(),Initializer.now())

		def equipmentOne = Initializer.newEquipment("SERIAL10",true,false,32,"ROOM A1","2900.23",['en':'Equipment Descriptions one'],Initializer.getDate(22,07,2010)
				,Initializer.getDate(10,10,2010),new Date(),"equipmentModel",DataLocation.findByCode('Kivuye HC'),department,equipmentType,manufacture,supplier)
		def equipmentTwo = Initializer.newEquipment("SERIAL11",false,true,32,"ROOM A1","2900.23",['en':'Equipment Descriptions two'],Initializer.getDate(22,07,2010)
				,Initializer.getDate(10,10,2010),new Date(),"equipmentModel",DataLocation.findByCode('Butaro DH'),department,equipmentType,manufacture,supplier)
		
		
		def List<Equipment> equipmentsOne, equipmentsTwo, equipmentsThree
		def equipmentStatusOneActive = Initializer.newEquipmentStatus(Initializer.now(),User.findByUsername("admin"),Status.INSTOCK,equipmentOne,false)
		def equipmentStatusOneInActive = Initializer.newEquipmentStatus(Initializer.now(),User.findByUsername("admin"),Status.OPERATIONAL,equipmentOne,true)
		def equipmentStatusTwo = Initializer.newEquipmentStatus(Initializer.now(),User.findByUsername("admin"),Status.DISPOSED,equipmentOne,true)
		
		equipmentOne.addToStatus(equipmentStatusOneInActive).save(failOnError:true,flush: true)
		equipmentOne.addToStatus(equipmentStatusOneActive).save(failOnError:true,flush: true)
		equipmentTwo.addToStatus(equipmentStatusTwo).save(failOnError:true,flush: true)
		
		when://Search by defaults
		
		equipmentsOne = equipmentService.filterEquipment(DataLocation.findByCode('Butaro DH'),
			supplier, manufacture,equipmentType,'false','true',Status.DISPOSED,adaptParamsForList())

		then:
		Equipment.count() == 2
		equipmentsOne.size() == 1
		equipmentsOne[0].getDescriptions(new Locale("en")).equals('Equipment Descriptions two')
		
		when://Search by active status
		
		equipmentsTwo = equipmentService.filterEquipment(DataLocation.findByCode('Kivuye HC'),
			supplier, manufacture,equipmentType,'true','false',Status.OPERATIONAL,adaptParamsForList())
		equipmentsThree = equipmentService.filterEquipment(DataLocation.findByCode('Kivuye HC'),
			supplier, manufacture,equipmentType,'true','false',Status.INSTOCK,adaptParamsForList())

		then:
		Equipment.count() == 2
		equipmentsTwo.size() == 1
		equipmentsThree.size() == 0
		equipmentsTwo[0].getDescriptions(new Locale("en")).equals('Equipment Descriptions one')
		equipmentsTwo[0].status.size() == 2
	}
}