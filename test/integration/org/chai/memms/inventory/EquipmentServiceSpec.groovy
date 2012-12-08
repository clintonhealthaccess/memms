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

import java.util.List;
import java.util.Map;

import org.chai.memms.Initializer;
import org.chai.memms.IntegrationTests;
import org.chai.memms.inventory.Equipment.Donor;
import org.chai.memms.inventory.Equipment.PurchasedBy;
import org.chai.memms.inventory.EquipmentStatus.Status;
import org.chai.memms.inventory.EquipmentType.Observation;
import org.chai.location.Location
import org.chai.location.DataLocation
import org.chai.memms.security.User;
import org.chai.memms.security.User.UserType
import org.chai.memms.IntegrationTests
import org.chai.location.DataLocation;
import org.chai.memms.inventory.Provider.Type;
import org.chai.memms.inventory.Equipment;

import java.io.File;

/**
 * @author Jean Kahigiso M.
 *
 */
class EquipmentServiceSpec extends IntegrationTests{

	def equipmentService
	
	def "can search equipment by serial number, description and observation"() {
		setup:
		setupLocationTree()

		def manufactureContact = Initializer.newContact(['en':'Address Descriptions '],"Manufacture","jkl@yahoo.com","0768-889-787","Street 154","6353")
		def supplierContact = Initializer.newContact([:],"Supplier","jk@yahoo.com","0768-888-787","Street 1654","6353")

		def manufacture = Initializer.newProvider(CODE(123), Type.MANUFACTURER,manufactureContact)
		def supplier = Initializer.newProvider(CODE(124), Type.SUPPLIER,supplierContact)
		def userHc = newOtherUserWithType("userHc", "userHc", DataLocation.findByCode(KIVUYE), UserType.TITULAIREHC)
		def techDH = newOtherUserWithType("techDH", "techDH", DataLocation.findByCode(BUTARO), UserType.TECHNICIANDH)
		def techMMC = newOtherUserWithType("techMMC", "techMMC", DataLocation.findByCode(RWANDA), UserType.TECHNICIANMMC)
		
		def department = Initializer.newDepartment(['en':"testName"], CODE(123),['en':"testDescription"])
		def equipmentType = Initializer.newEquipmentType(CODE(15810),["en":"Accelerometers"],["en":"used in memms"],Observation.USEDINMEMMS,Initializer.now(),Initializer.now())

		Initializer.newEquipment(CODE(123),PurchasedBy.BYDONOR,Donor.OTHERNGO,"Internews",false,Initializer.newPeriod(32),"ROOM A1","",['en':'Equipment Descriptions one'],Initializer.getDate(22,07,2010)
				,Initializer.getDate(10,10,2010),'',Initializer.now(),"equipmentModelOne",DataLocation.findByCode(BUTARO),department,equipmentType,manufacture,supplier,Status.OPERATIONAL)
		def equipmentCodeToFind = Initializer.newEquipment(CODE(124),PurchasedBy.BYMOH,null,null,false,Initializer.newPeriod(32),"ROOM A1","2900.23",['en':'Equipment Descriptions two'],Initializer.getDate(22,07,2010)
				,Initializer.getDate(10,10,2010),"RWF",Initializer.now(),"equipmentModelTwo",DataLocation.findByCode(KIVUYE),department,equipmentType,manufacture,supplier,Status.OPERATIONAL)
		def List<Equipment> equipments

		when: "user can view all those he manages"
		equipments = equipmentService.searchEquipment("Descriptions",techDH,null, [:])
		then:
		equipments.size() == 2
		equipments[0] != equipments[1]
		
		when: "user cannot see those he doesn't manage"
		equipments = equipmentService.searchEquipment("Descriptions",userHc,null, [:])
		then:
		equipments.size() == 1
		equipments[0].serialNumber.equals(CODE(124))
		
		when: "user can search by dataLocaiton only if specified"
		equipments = equipmentService.searchEquipment("Descriptions",techMMC,DataLocation.findByCode(BUTARO), [:])
		then:
		equipments.size() == 1
		equipments[0].serialNumber.equals(CODE(123))

		when: "Searching by description"

		equipments = equipmentService.searchEquipment("one",techDH,null, [:])
		then:
		equipments.size() == 1
		equipments[0].getDescriptions(new Locale("en")).equals('Equipment Descriptions one')

		when: "Searching by type name"
		equipments = equipmentService.searchEquipment("Accelerometers",techDH,null, [:])
		then:
		equipments.size() == 2
		equipments[0] != equipments[1]
		
		when: "Searching by code"
		equipments = equipmentService.searchEquipment(equipmentCodeToFind.code,techDH,null, [:])
		then:
		equipments.size() == 1
		equipments[0].code.equals(equipmentCodeToFind.code)
		
		when: "Searching by serial number"
		equipments = equipmentService.searchEquipment(CODE(124),techDH,null, [:])
		then:
		equipments.size() == 1
		equipments[0].serialNumber.equals(CODE(124))
		
		when: "Searching by model"
		equipments = equipmentService.searchEquipment("equipmentModelOne",techDH,null, [:])
		then:
		equipments.size() == 1
		equipments[0].serialNumber.equals(CODE(123))
	}

	def "user can only view his equipments, if a technician at dh they can also view for the locations they manage"() {
		setup:
		setupLocationTree()
		def user = newOtherUser("user", "user", DataLocation.findByCode(KIVUYE))
		user.userType = UserType.TITULAIREHC
		user.save(failOnError:true)
		
		def techDh = newOtherUser("techDh", "techDh", DataLocation.findByCode(BUTARO))
		techDh.userType = UserType.TECHNICIANDH
		techDh.save(failOnError:true)
		
		def manufactureContact = Initializer.newContact(['en':'Address Descriptions '],"Manufacture","jkl@yahoo.com","0768-889-787","Street 154","6353")
		def supplierContact = Initializer.newContact([:],"Supplier","jk@yahoo.com","0768-888-787","Street 1654","6353")

		def manufacture = Initializer.newProvider(CODE(123), Type.MANUFACTURER,manufactureContact)
		def supplier = Initializer.newProvider(CODE(124), Type.SUPPLIER,supplierContact)
		def department = Initializer.newDepartment(['en':"testName"], CODE(123),['en':"testDescription"])
		def equipmentType = Initializer.newEquipmentType(CODE(15810),["en":"Accelerometers"],["en":"used in memms"],Observation.USEDINMEMMS,Initializer.now(),Initializer.now())

		Initializer.newEquipment("SERIAL10",PurchasedBy.BYDONOR,Donor.MOHPARTNER,"CHAI",,false,Initializer.newPeriod(32),"ROOM A1","",['en':'Equipment Descriptions one'],Initializer.getDate(22,07,2010)
				,Initializer.getDate(10,10,2010),"",Initializer.now(),"equipmentModel",DataLocation.findByCode('Kivuye HC'),department,equipmentType,manufacture,supplier,Status.OPERATIONAL)
		Initializer.newEquipment("SERIAL11",PurchasedBy.BYFACILITY,null,null,false,Initializer.newPeriod(32),"ROOM A1","2900.23",['en':'Equipment Descriptions two'],Initializer.getDate(22,07,2010)
				,Initializer.getDate(10,10,2010),"USD",Initializer.now(),"equipmentModel",DataLocation.findByCode('Butaro DH'),department,equipmentType,manufacture,supplier,Status.OPERATIONAL)
		def equipmentsTech, equipmentsUser

		when:
		equipmentsTech = equipmentService.getMyEquipments(techDh,[:])
		equipmentsUser = equipmentService.getMyEquipments(user,[:])
		then:
		equipmentsTech.size() == 2
		equipmentsUser.size() == 1
	}

	def "filter equipments"() {
		setup:
		setupLocationTree()
		def butaroDH =  DataLocation.findByCode('Butaro DH')
		def kivuyeHC = DataLocation.findByCode('Kivuye HC')
		def manufactureContact = Initializer.newContact(['en':'Address Descriptions '],"Manufacture","jkl@yahoo.com","0768-889-787","Street 154","6353")
		def supplierContact = Initializer.newContact([:],"Supplier","jk@yahoo.com","0768-888-787","Street 1654","6353")
		def serviceProContact = Initializer.newContact([:],"Service Provider","jk@yahoo.com","0768-888-787","Street 1654","6353")
		def manufacture = Initializer.newProvider(CODE(111), Type.MANUFACTURER,manufactureContact)
		def supplier = Initializer.newProvider(CODE(222), Type.SUPPLIER,supplierContact)
		def servicePro = Initializer.newProvider(CODE(221), Type.SERVICEPROVIDER,serviceProContact)
		
		def user  = newUser("admin", "Admin UID")
		def department = Initializer.newDepartment(['en':"testName"], CODE(123),['en':"testDescription"])
		def equipmentType = Initializer.newEquipmentType(CODE(15810),["en":"Accelerometers"],["en":"used in memms"],Observation.USEDINMEMMS,Initializer.now(),Initializer.now())

		def equipmentOne = Initializer.newEquipment("SERIAL10",PurchasedBy.BYDONOR,Donor.MOHPARTNER,"Intra-health",false,Initializer.newPeriod(32),"ROOM A1","",['en':'Equipment Descriptions one'],Initializer.getDate(22,07,2010)
				,Initializer.getDate(10,10,2010),"",Initializer.now(),"equipmentModel",kivuyeHC,department,equipmentType,manufacture,supplier,Status.INSTOCK)
		def equipmentTwo = Initializer.newEquipment("SERIAL11",PurchasedBy.BYFACILITY,null,null,true,Initializer.newPeriod(32),"ROOM A1","2900.23",['en':'Equipment Descriptions two'],Initializer.getDate(22,07,2010)
				,Initializer.getDate(10,10,2010),"USD",Initializer.now(),"equipmentModel",butaroDH,department,equipmentType,manufacture,supplier,Status.OPERATIONAL)
		
		equipmentOne.serviceProvider=servicePro
		equipmentOne.serviceContractPeriod = Initializer.newPeriod(2)
		equipmentOne.serviceContractStartDate = Initializer.getDate(11,10,2010)
		equipmentTwo.serviceProvider=servicePro
		equipmentTwo.serviceContractPeriod = Initializer.newPeriod(21)
		equipmentTwo.serviceContractStartDate = Initializer.getDate(11,10,2010)
		equipmentOne.save(failOnError:true)
		equipmentTwo.save(failOnError:true)
		
		def List<Equipment> equipmentsOne, equipmentsTwo, equipmentsThree
		def equipmentStatusOneActive = Initializer.newEquipmentStatus(Initializer.now(),User.findByUsername("admin"),Status.INSTOCK,equipmentOne,[:])
		def equipmentStatusOneInActive = Initializer.newEquipmentStatus(Initializer.now(),User.findByUsername("admin"),Status.OPERATIONAL,equipmentOne,[:])
		def equipmentStatusTwo = Initializer.newEquipmentStatus(Initializer.now(),User.findByUsername("admin"),Status.DISPOSED,equipmentOne,[:])
		
		when://Search by defaults
		
		equipmentsOne = equipmentService.filterEquipment(butaroDH,supplier,manufacture,servicePro,equipmentType,PurchasedBy.NONE,Donor.NONE,'true',Status.OPERATIONAL,[:])

		then:
		Equipment.count() == 2
		Equipment.list()[0].currentStatus == Status.DISPOSED
		Equipment.list()[1].currentStatus ==Status.OPERATIONAL
		equipmentsOne.size() == 1
		equipmentsOne[0].getDescriptions(new Locale("en")).equals('Equipment Descriptions two')
		
		when://Search by currentStatus
		
		equipmentsTwo = equipmentService.filterEquipment(kivuyeHC,supplier, manufacture,servicePro,equipmentType,PurchasedBy.BYDONOR,Donor.MOHPARTNER,'false',Status.DISPOSED,[:])
		equipmentsThree = equipmentService.filterEquipment(kivuyeHC,supplier, manufacture,servicePro,equipmentType,PurchasedBy.BYDONOR,Donor.NONE,'false',Status.INSTOCK,[:])

		then:
		Equipment.count() == 2
		equipmentsTwo.size() == 1
		equipmentsThree.size() == 0
		equipmentsTwo[0].getDescriptions(new Locale("en")).equals('Equipment Descriptions one')
		equipmentsTwo[0].status.size() == 3
		
		when://Search by donor only
		equipmentsTwo = equipmentService.filterEquipment(kivuyeHC,supplier, manufacture,servicePro,equipmentType,null,Donor.MOHPARTNER,'false',Status.DISPOSED,[:])

		then:
		Equipment.count() == 2
		equipmentsTwo.size() == 1
		equipmentsTwo[0].status.size() == 3
	}

	def "can export equipments"(){
		setup:
		setupLocationTree()

		def manufactureContact = Initializer.newContact(['en':'Address Descriptions '],"Manufacture","jkl@yahoo.com","0768-889-787","Street 154","6353")
		def supplierContact = Initializer.newContact([:],"Supplier","jk@yahoo.com","0768-888-787","Street 1654","6353")
		def manufacture = Initializer.newProvider(CODE(111), Type.MANUFACTURER,manufactureContact)
		def supplier = Initializer.newProvider(CODE(222), Type.SUPPLIER,supplierContact)
		def warrantyContact = Initializer.newContact(['fr':'Warranty Address Descriptions One'],"Warranty","jk@yahoo.com","0768-888-787","Street 654","8988")
		def warranty = Initializer.newWarranty(warrantyContact,Initializer.getDate(10, 12, 2010),false,[:])
		
		def user  = newUser("user", "user", true, true)
		setupSecurityManager(user)
		def department = Initializer.newDepartment(['en':"testName"], CODE(123),['en':"testDescription"])
		def equipmentType = Initializer.newEquipmentType(CODE(15810),["en":"Accelerometers"],["en":"used in memms"],Observation.USEDINMEMMS,Initializer.now(),Initializer.now())

		def equipmentOne = Initializer.newEquipment("SERIAL10",PurchasedBy.BYFACILITY,null,null,true,Initializer.newPeriod(32),"ROOM A1","",['en':'Equipment Descriptions one'],Initializer.getDate(22,07,2010)
				,Initializer.getDate(10,10,2010),"",Initializer.now(),"equipmentModel",DataLocation.findByCode(KIVUYE),department,equipmentType,manufacture,supplier,Status.INSTOCK)
		def equipmentTwo = Initializer.newEquipment("SERIAL11",PurchasedBy.BYFACILITY,null,null,,true,Initializer.newPeriod(32),"ROOM A1","2900.23",['en':'Equipment Descriptions two'],Initializer.getDate(22,07,2010)
				,Initializer.getDate(10,10,2010),"EUR",Initializer.now(),"equipmentModel",DataLocation.findByCode(KIVUYE),department,equipmentType,manufacture,supplier,Status.INSTOCK)
		def equipmentThree = Initializer.newEquipment("SERIAL12",PurchasedBy.BYFACILITY,null,null,,true,Initializer.newPeriod(32),"ROOM A1","2900.23",['en':'Equipment Descriptions two'],Initializer.getDate(22,07,2010)
			,Initializer.getDate(10,10,2010),"EUR",Initializer.now(),"equipmentModel",DataLocation.findByCode(KIVUYE),department,equipmentType,manufacture,supplier,Status.INSTOCK)
		def equipmentFour = Initializer.newEquipment("SERIAL13",PurchasedBy.BYFACILITY,null,null,,true,Initializer.newPeriod(32),"ROOM A1","2900.23",['en':'Equipment Descriptions two'],Initializer.getDate(22,07,2010)
			,Initializer.getDate(10,10,2010),"EUR",Initializer.now(),"equipmentModel",DataLocation.findByCode(BUTARO),department,equipmentType,manufacture,supplier,Status.INSTOCK)
	
		
		def List<Equipment> equipmentsOne, equipmentsTwo, equipmentsThree
		def equipmentStatusOneActive = Initializer.newEquipmentStatus(Initializer.now(),User.findByUsername("user"),Status.INSTOCK,equipmentOne,[:])
		def equipmentStatusOneInActive = Initializer.newEquipmentStatus(Initializer.now(),User.findByUsername("user"),Status.OPERATIONAL,equipmentOne,[:])
		
		equipmentOne.warranty=warranty
		equipmentOne.warrantyPeriod = Initializer.newPeriod(22)
		equipmentOne.addToStatus(equipmentStatusOneActive).save(failOnError:true,flush: true)
		equipmentTwo.warranty=warranty
		equipmentTwo.warrantyPeriod = Initializer.newPeriod(20)
		equipmentTwo.addToStatus(equipmentStatusOneActive).save(failOnError:true,flush: true)
		equipmentThree.warranty=warranty
		equipmentThree.warrantyPeriod = Initializer.newPeriod(12)
		equipmentThree.addToStatus(equipmentStatusOneActive).save(failOnError:true,flush: true)
		List<Equipment> equipments = [equipmentOne,equipmentTwo,equipmentThree]

		when:
		File csvFile = equipmentService.exporter(DataLocation.findByCode(KIVUYE),equipments)
		
		then:
		csvFile != null
	}
	
	def "update equipment status"(){
		setup:
		
		setupLocationTree()
		def manufactureContact = Initializer.newContact(['en':'Address Descriptions '],"Manufacture","jkl@yahoo.com","0768-889-787","Street 154","6353")
		def supplierContact = Initializer.newContact([:],"Supplier","jk@yahoo.com","0768-888-787","Street 1654","6353")
		def manufacture = Initializer.newProvider(CODE(111), Type.MANUFACTURER,manufactureContact)
		def supplier = Initializer.newProvider(CODE(222), Type.SUPPLIER,supplierContact)
		def warrantyContact = Initializer.newContact(['fr':'Warranty Address Descriptions One'],"Warranty","jk@yahoo.com","0768-888-787","Street 654","8988")
		def warranty = Initializer.newWarranty(warrantyContact,Initializer.getDate(10, 12, 2010),false,[:])
		
		def user  = newUser("user", "user", true, true)
		setupSecurityManager(user)
		def department = Initializer.newDepartment(['en':"testName"], CODE(123),['en':"testDescription"])
		def equipmentType = Initializer.newEquipmentType(CODE(15810),["en":"Accelerometers"],["en":"used in memms"],Observation.USEDINMEMMS,Initializer.now(),Initializer.now())
		def equipment = Initializer.newEquipment("SERIAL10",PurchasedBy.BYFACILITY,null,null,true,Initializer.newPeriod(32),"ROOM A1","",['en':'Equipment Descriptions one'],Initializer.getDate(22,07,2010)
				,Initializer.getDate(10,10,2010),"",Initializer.now(),"equipmentModel",DataLocation.findByCode(KIVUYE),department,equipmentType,manufacture,supplier,Status.INSTOCK)
		def statusOne = Initializer.newEquipmentStatus(Initializer.now(),User.findByUsername("user"),Status.INSTOCK,equipment,[:])
		def statusTwo = new EquipmentStatus(dateOfEvent:Initializer.now(),changedBy:User.findByUsername("user"),status:Status.OPERATIONAL,statusChangeDate:Initializer.now())
		
		when:
		equipment = equipmentService.updateCurrentEquipmentStatus(Equipment.findBySerialNumber("SERIAL10"),statusTwo)

		then:
		Equipment.count() == 1
		Equipment.list()[0].currentStatus == Status.OPERATIONAL
		
	}
}