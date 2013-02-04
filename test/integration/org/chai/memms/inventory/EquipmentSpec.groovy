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
import org.chai.memms.inventory.Equipment.Donor;
import org.chai.memms.inventory.Equipment.PurchasedBy;
import org.chai.memms.inventory.EquipmentStatus.Status;
import org.chai.memms.inventory.EquipmentType.Observation;
import org.chai.location.DataLocation;
import org.chai.memms.security.User;
import org.chai.memms.IntegrationTests
import org.chai.location.DataLocation;
import org.chai.memms.inventory.Provider.Type;
import org.chai.memms.inventory.Equipment;
/**
 * @author Jean Kahigiso M.
 *
 */
class EquipmentSpec extends IntegrationTests{

	def "can create and save an equipment"() {

		setup:
		setupLocationTree()
		setupSystemUser()
		def department = Initializer.newDepartment(['en':"testName"], CODE(123),['en':"testDescription"])
		def equipmentType = Initializer.newEquipmentType(CODE(15810),["en":"Accelerometers"],["en":"used in memms"],Observation.USEDINMEMMS,Initializer.now(),34)
		when:
		def equipment = new Equipment(serialNumber:"test123",manufactureDate:Initializer.getDate(22,07,2010),
			purchaseDate:Initializer.getDate(22,07,2010),dateCreated:Initializer.getDate(23,07,2010), model:"equipmentModel", department:department,
			 dataLocation:DataLocation.list().first(),purchaser:PurchasedBy.BYDONOR,donor:Donor.MOHPARTNER,donorName:"CHAI",obsolete:false,expectedLifeTime:Initializer.newPeriod(20),
			 descriptions:['en':'Equipment Descriptions'], type:equipmentType,currentStatus:Status.OPERATIONAL,
			 addedBy: User.findByUsername("systemUser"))

		def manufactureContact = Initializer.newContact(['en':'Address Descriptions '],"Manufacture","jkl@yahoo.com","0768-889-787","Street 154","6353")
		def supplierContact = Initializer.newContact([:],"Supplier","jk@yahoo.com","0768-888-787","Street 1654","6353")
		
		def manufacture = Initializer.newProvider(CODE(123), Type.MANUFACTURER,manufactureContact)
		def supplier = Initializer.newProvider(CODE(124), Type.SUPPLIER,supplierContact)
		
		def contact = Initializer.newContact([:],"Contact","jk@yahoo.com","0768-888-787","Street 654","6353")
		def warranty = Initializer.newWarranty(['en':'warranty'],'warranty name','email@gmail.com',"0768-889-787","Street 154","6353",Initializer.getDate(10, 12, 2010),false,[:])

		equipment.manufacturer=manufacture
		equipment.supplier=supplier
		
		equipment.warranty = warranty
		equipment.warrantyPeriod = Initializer.newPeriod(4)

		equipment.save(failOnError: true)
		then:
		Equipment.count() == 1
	}


	// def "can't create and save an equipment without needed fields -  serialNumber, expectedLifeTime, code"() {

	// 	setup:
	// 	setupLocationTree()
	// 	setupSystemUser()
	// 	def department = Initializer.newDepartment(['en':"testName"], CODE(123),['en':"testDescription"])
	// 	def equipmentType = Initializer.newEquipmentType(CODE(15810), ["en":"Accelerometers"],["en":"used in memms"],Observation.USEDINMEMMS,Initializer.now(),27)
	// 	when://SerialNumber
	// 	def equipment = new Equipment(purchaseCost:"1,200",manufactureDate:Initializer.getDate(22,07,2010),purchaseDate:Initializer.getDate(22,07,2010),
	// 			dateCreated:Initializer.getDate(23,07,2010), model:"equipmentModel", department:department,purchaser:PurchasedBy.BYFACILITY,obsolete:false,
	// 			dataLocation:DataLocation.list().first(),descriptions:['en':'Equipment Descriptions'], type:equipmentType,currentStatus:Status.OPERATIONAL,
	// 			addedBy: User.findByUsername("systemUser"))

	// 	def manufactureContact = Initializer.newContact(['en':'Address Descriptions '],"Manufacture","jkl@yahoo.com","0768-889-787","Street 154","6353")
	// 	def supplierContact = Initializer.newContact([:],"Supplier","jk@yahoo.com","0768-888-787","Street 1654","6353")
		
	// 	def manufacture = Initializer.newProvider(CODE(123), Type.MANUFACTURER,manufactureContact)
	// 	def supplier = Initializer.newProvider(CODE(124), Type.SUPPLIER,supplierContact)
	// 	def contact = Initializer.newContact([:],"Contact","jk@yahoo.com","0768-888-787","Street 654","6353")
	// 	def warranty = Initializer.newWarranty(['en':'warranty'],'warranty name','email@gmail.com',"0768-889-787","Street 154","6353",Initializer.getDate(10, 12, 2010),false,[:])

	// 	equipment.manufacturer=manufacture
	// 	equipment.supplier=supplier
	// 	equipment.warranty=warranty
	// 	equipment.warrantyPeriod = Initializer.newPeriod(12)

	// 	equipment.save()
	// 	then:
	// 	Equipment.count() == 0
		
	// 	equipment.errors.hasFieldErrors('serialNumber') == true
	// 	equipment.errors.hasFieldErrors('expectedLifeTime') == true
	// 	equipment.errors.hasFieldErrors('currency') == true
		
	// 	equipment.errors.fieldErrorCount== 3
	// }


	def "can't create and save an equipment with a purchaser is donor not specifying the donor and his name"() {

		setup:
		setupLocationTree()
		setupSystemUser()
		def department = Initializer.newDepartment(['en':"testName"], CODE(123),['en':"testDescription"])
		def equipmentType = Initializer.newEquipmentType(CODE(15810), ["en":"Accelerometers"],["en":"used in memms"],Observation.USEDINMEMMS,Initializer.now(),13)
		when:
		def equipment = new Equipment(purchaseCost:"200455.8",serialNumber:"test123",manufactureDate:Initializer.getDate(22,07,2010),purchaseDate:Initializer.getDate(22,07,2010),
				dateCreated:Initializer.getDate(23,07,2010), model:"equipmentModel", department:department,purchaser:PurchasedBy.BYDONOR,obsolete:false,expectedLifeTime:Initializer.newPeriod(20),
				dataLocation:DataLocation.list().first(),descriptions:['en':'Equipment Descriptions'],type:equipmentType,currentStatus:Status.OPERATIONAL,
				addedBy: User.findByUsername("systemUser"))

		def manufactureContact = Initializer.newContact(['en':'Address Descriptions '],"Manufacture","jkl@yahoo.com","0768-889-787","Street 154","6353")
		def supplierContact = Initializer.newContact([:],"Supplier","jk@yahoo.com","0768-888-787","Street 1654","6353")
		
		def manufacture = Initializer.newProvider(CODE(123), Type.MANUFACTURER,manufactureContact)
		def supplier = Initializer.newProvider(CODE(124), Type.SUPPLIER,supplierContact)
		def contact = Initializer.newContact([:],"Contact","jk@yahoo.com","0768-888-787","Street 654","6353")
		def warranty = Initializer.newWarranty(['en':'warranty'],'warranty name','email@gmail.com',"0768-889-787","Street 154","6353",Initializer.getDate(10, 12, 2010),false,[:])

		equipment.manufacturer=manufacture
		equipment.supplier=supplier
		equipment.warranty=warranty
		equipment.warrantyPeriod = Initializer.newPeriod(11)

		equipment.save()
		then:
		Equipment.count() == 0
		equipment.errors.hasFieldErrors('donor') == true
		equipment.errors.hasFieldErrors('donorName') == true
	}

	def "can't create and save an equipment with a duplicate serial number"() {

		setup:
		setupLocationTree()
		setupSystemUser()
		def manufactureContact = Initializer.newContact(['en':'Address Descriptions '],"Manufacture","jkl@yahoo.com","0768-889-787","Street 154","6353")
		def supplierContact = Initializer.newContact([:],"Supplier","jk@yahoo.com","0768-888-787","Street 1654","6353")
		def department = Initializer.newDepartment(['en':"testName"], CODE(123),['en':"testDescription"])
		def equipmentType = Initializer.newEquipmentType(CODE(15810), ["en":"Accelerometers"],["en":"used in memms"],Observation.USEDINMEMMS,Initializer.now(),12)
		def manufacture = Initializer.newProvider(CODE(123), Type.MANUFACTURER,manufactureContact)
		def supplier = Initializer.newProvider(CODE(124), Type.SUPPLIER,supplierContact)
		Initializer.newEquipment("test123",PurchasedBy.BYFACILITY,null,null,false,Initializer.newPeriod(30),"ROOM A1","16677",['en':"testDescription"],Initializer.getDate(22,07,2010), Initializer.getDate(22,07,2010),"RWF",
				"equipmentModel",DataLocation.list().first(),department, equipmentType,manufacture,supplier,Status.OPERATIONAL,
				User.findByUsername("systemUser"),null,null)
			
		when:
		def equipment = new Equipment(serialNumber:"test123",purchaseCost:"1,200",currency:"USD",manufactureDate:Initializer.getDate(22,07,2010),purchaseDate:Initializer.getDate(22,07,2010),
				dateCreated:Initializer.getDate(23,07,2010), model:"equipmentModel", department:department,purchaser:PurchasedBy.BYMOH,obsolete:false,expectedLifeTime:Initializer.newPeriod(20),
				dataLocation:DataLocation.list().first(),descriptions:['en':'Equipment Descriptions'],type:equipmentType,currentStatus:Status.OPERATIONAL,
				addedBy: User.findByUsername("systemUser"))

		def contact = Initializer.newContact([:],"Contact","jk@yahoo.com","0768-888-787","Street 654","6353")
		def warranty = Initializer.newWarranty(['en':'warranty'],'warranty name','email@gmail.com',"0768-889-787","Street 154","6353",Initializer.getDate(10, 12, 2010),false,[:])

		equipment.manufacturer=manufacture
		equipment.supplier=supplier
		equipment.warranty=warranty
		equipment.warrantyPeriod = Initializer.newPeriod(21)

		equipment.save()
		then:
		Equipment.count() == 1
		equipment.errors.hasFieldErrors('serialNumber') == true
	}
	
	def "can't create and save an equipment with a duplicate code"() {
		
				setup:
				setupLocationTree()
				setupSystemUser()
				def manufactureContact = Initializer.newContact(['en':'Address Descriptions '],"Manufacture","jkl@yahoo.com","0768-889-787","Street 154","6353")
				def supplierContact = Initializer.newContact([:],"Supplier","jk@yahoo.com","0768-888-787","Street 1654","6353")
				def department = Initializer.newDepartment(['en':"testName"], CODE(123),['en':"testDescription"])
				def equipmentType = Initializer.newEquipmentType(CODE(15810), ["en":"Accelerometers"],["en":"used in memms"],Observation.USEDINMEMMS,Initializer.now(),12)
				def manufacture = Initializer.newProvider(CODE(123), Type.MANUFACTURER,manufactureContact)
				def supplier = Initializer.newProvider(CODE(124), Type.SUPPLIER,supplierContact)
				def equipmentOne = Initializer.newEquipment("test123",PurchasedBy.BYFACILITY,null,null,false,Initializer.newPeriod(30),"ROOM A1","16677",['en':"testDescription"],Initializer.getDate(22,07,2010), Initializer.getDate(22,07,2010),"RWF",
						"equipmentModel",DataLocation.list().first(),department, equipmentType,manufacture,supplier,Status.OPERATIONAL,
						User.findByUsername("systemUser"),null,null)
					
				when:
				def equipment = new Equipment(serialNumber:"test123",purchaseCost:"1,200",currency:"USD",manufactureDate:Initializer.getDate(22,07,2010),purchaseDate:Initializer.getDate(22,07,2010),
						dateCreated:Initializer.getDate(23,07,2010), model:"equipmentModel", department:department,purchaser:PurchasedBy.BYMOH,obsolete:false,expectedLifeTime:Initializer.newPeriod(20),
						dataLocation:DataLocation.list().first(),descriptions:['en':'Equipment Descriptions'],type:equipmentType,code:equipmentOne.code,currentStatus:Status.OPERATIONAL,
						addedBy: User.findByUsername("systemUser"))
		
				def contact = Initializer.newContact([:],"Contact","jk@yahoo.com","0768-888-787","Street 654","6353")
				def warranty = Initializer.newWarranty(['en':'warranty'],'warranty name','email@gmail.com',"0768-889-787","Street 154","6353",Initializer.getDate(10, 12, 2010),false,[:])
		
				equipment.manufacturer=manufacture
				equipment.supplier=supplier
				equipment.warranty=warranty
				equipment.warrantyPeriod = Initializer.newPeriod(21)
		
				equipment.save()
				then:
				Equipment.count() == 1
				equipment.errors.hasFieldErrors('code') == true
			}

	def "manufacture date must be before today"() {

		setup:
		setupLocationTree()
		setupSystemUser()
		def department = Initializer.newDepartment(['en':"testName"], CODE(123),['en':"testDescription"])
		def equipmentType = Initializer.newEquipmentType("15810", ["en":"Accelerometers"],["en":"used in memms"],Observation.USEDINMEMMS,Initializer.now(),9)
		when:
		def equipment = new Equipment(serialNumber:"test123", purchaseCost:"1,200",currency:"EURO",manufactureDate:Initializer.now().next(),purchaseDate:Initializer.getDate(22,07,2010),
				dateCreated:Initializer.getDate(23,07,2010), model:"equipmentModel", department:department,purchaser:PurchasedBy.BYFACILITY,obsolete:false,expectedLifeTime:Initializer.newPeriod(20),
				dataLocation:DataLocation.list().first(),descriptions:['en':'Equipment Descriptions'],type:equipmentType,currentStatus:Status.OPERATIONAL,
				addedBy: User.findByUsername("systemUser"))

		def manufactureContact = Initializer.newContact(['en':'Address Descriptions '],"Manufacture","jkl@yahoo.com","0768-889-787","Street 154","6353")
		def supplierContact = Initializer.newContact([:],"Supplier","jk@yahoo.com","0768-888-787","Street 1654","6353")
		
		def manufacture = Initializer.newProvider(CODE(123), Type.MANUFACTURER,manufactureContact)
		def supplier = Initializer.newProvider(CODE(124), Type.SUPPLIER,supplierContact)
		def contact = Initializer.newContact([:],"Contact","jk@yahoo.com","0768-888-787","Street 654","6353")
		def warranty = Initializer.newWarranty(['en':'warranty'],'warranty name','email@gmail.com',"0768-889-787","Street 154","6353",Initializer.getDate(10, 12, 2010),false,[:])

		equipment.manufacturer=manufacture
		equipment.supplier=supplier
		equipment.warranty=warranty
		equipment.warrantyPeriod = Initializer.newPeriod(12)

		equipment.save()
		then:
		Equipment.count() == 0
		equipment.errors.hasFieldErrors('manufactureDate') == true
	}

	// def "purchase date can't be after today"() {

	// setup:
	// setupLocationTree()
	// setupSystemUser()
	// def department = Initializer.newDepartment(['en':"testName"], CODE(123),['en':"testDescription"])
	// def equipmentType = Initializer.newEquipmentType("15810", ["en":"Accelerometers"],["en":"used in memms"],Observation.USEDINMEMMS,Initializer.now(),56)
	// when:
	// def equipment = new Equipment(serialNumber:"test123", purchaseCost:"1,200",currency:"USD",manufactureDate:Initializer.getDate(22,07,2010),purchaseDate:Initializer.now().next(),
	// 		dateCreated:Initializer.getDate(22,07,2010), model:"equipmentModel", department:department,purchaser:PurchasedBy.BYMOH,obsolete:false,expectedLifeTime:Initializer.newPeriod(20),
	// 		dataLocation:DataLocation.list().first(),descriptions:['en':'Equipment Descriptions'],type:equipmentType,currentStatus:Status.OPERATIONAL,
	// 		addedBy: User.findByUsername("systemUser"))

	// 	def manufactureContact = Initializer.newContact(['en':'Address Descriptions '],"Manufacture","jkl@yahoo.com","0768-889-787","Street 154","6353")
	// 	def supplierContact = Initializer.newContact([:],"Supplier","jk@yahoo.com","0768-888-787","Street 1654","6353")
		
	// 	def manufacture = Initializer.newProvider(CODE(123), Type.MANUFACTURER,manufactureContact)
	// 	def supplier = Initializer.newProvider(CODE(124), Type.SUPPLIER,supplierContact)
	// 	def contact = Initializer.newContact([:],"Contact","jk@yahoo.com","0768-888-787","Street 654","6353")
	// 	def warranty = Initializer.newWarranty(['en':'warranty'],'warranty name','email@gmail.com',"0768-889-787","Street 154","6353",Initializer.getDate(10, 12, 2010),false,[:])

	// 	equipment.manufacturer=manufacture
	// 	equipment.supplier=supplier
	// 	equipment.warranty=warranty
	// 	equipment.warrantyPeriod = Initializer.newPeriod(22)

	// 	equipment.save()
	// 	then:
	// 	Equipment.count() == 0
	// 	equipment.errors.hasFieldErrors('purchaseDate') == true
	// }
	
	def "can get current status based on time"(){
		setup:
		setupLocationTree()
		setupSystemUser()
		def department = Initializer.newDepartment(['en':"testName"], CODE(123),['en':"testDescription"])
		def equipmentType = Initializer.newEquipmentType(CODE(15810),["en":"Accelerometers"],["en":"used in memms"],Observation.USEDINMEMMS,Initializer.now(),10)
		def user  = newUser("admin", "Admin UID")
		def equipment = new Equipment(serialNumber:"test123",manufactureDate:Initializer.getDate(22,07,2010),
			purchaseDate:Initializer.getDate(22,07,2010),dateCreated:Initializer.getDate(23,07,2010), model:"equipmentModel", department:department,
			 dataLocation:DataLocation.list().first(),purchaser:PurchasedBy.BYDONOR,donor:Donor.OTHERS,donorName:"Personel",obsolete:false,expectedLifeTime:Initializer.newPeriod(20),
			 descriptions:['en':'Equipment Descriptions'], type:equipmentType,currentStatus:Status.OPERATIONAL,
			 addedBy: User.findByUsername("systemUser"))

		def manufactureContact = Initializer.newContact(['en':'Address Descriptions '],"Manufacture","jkl@yahoo.com","0768-889-787","Street 154","6353")
		def supplierContact = Initializer.newContact([:],"Supplier","jk@yahoo.com","0768-888-787","Street 1654","6353")
		
		def manufacture = Initializer.newProvider(CODE(123), Type.MANUFACTURER,manufactureContact)
		def supplier = Initializer.newProvider(CODE(124), Type.SUPPLIER,supplierContact)
		
		def contact = Initializer.newContact([:],"Contact","jk@yahoo.com","0768-888-787","Street 654","6353")
		def warranty = Initializer.newWarranty(['en':'warranty'],'warranty name','email@gmail.com',"0768-889-787","Street 154","6353",Initializer.getDate(10, 12, 2010),false,[:])

		equipment.manufacturer=manufacture
		equipment.supplier=supplier
		equipment.warranty=warranty
		equipment.warrantyPeriod = Initializer.newPeriod(22)
		equipment.save(failOnError: true)
		
		when:
		def statusThree= Initializer.newEquipmentStatus(Initializer.getDate(10, 12, 2010),User.findByUsername("admin"),Status.INSTOCK,equipment,[:])
		def statusTwo= Initializer.newEquipmentStatus(Initializer.getDate(10, 12, 2011),User.findByUsername("admin"),Status.OPERATIONAL,equipment,[:])
		def statusOne= Initializer.newEquipmentStatus(Initializer.getDate(10, 07, 2012),User.findByUsername("admin"),Status.UNDERMAINTENANCE,equipment,[:])
		then:
		Equipment.count() == 1
		Equipment.list()[0].timeBasedStatus.status==statusOne.status
	}
	
	def "can set current status based on time"(){
		setup:
		setupLocationTree()
		setupSystemUser()
		def department = Initializer.newDepartment(['en':"testName"], CODE(123),['en':"testDescription"])
		def equipmentType = Initializer.newEquipmentType(CODE(15810),["en":"Accelerometers"],["en":"used in memms"],Observation.USEDINMEMMS,Initializer.now(),2)
		def user  = newUser("admin", "Admin UID")
		def equipment = new Equipment(serialNumber:"test123", purchaseCost:"1,200",currency:"RWF",manufactureDate:Initializer.getDate(22,07,2010),
			purchaseDate:Initializer.getDate(22,07,2010),dateCreated:Initializer.getDate(23,07,2010), model:"equipmentModel", department:department,
			 dataLocation:DataLocation.list().first(),purchaser:PurchasedBy.BYFACILITY,obsolete:false,expectedLifeTime:Initializer.newPeriod(20),
			 descriptions:['en':'Equipment Descriptions'], type:equipmentType,currentStatus:Status.OPERATIONAL,
			 addedBy: User.findByUsername("systemUser"))

		def manufactureContact = Initializer.newContact(['en':'Address Descriptions '],"Manufacture","jkl@yahoo.com","0768-889-787","Street 154","6353")
		def supplierContact = Initializer.newContact([:],"Supplier","jk@yahoo.com","0768-888-787","Street 1654","6353")
		
		def manufacture = Initializer.newProvider(CODE(123), Type.MANUFACTURER,manufactureContact)
		def supplier = Initializer.newProvider(CODE(124), Type.SUPPLIER,supplierContact)
		
		def contact = Initializer.newContact([:],"Contact","jk@yahoo.com","0768-888-787","Street 654","6353")
		def warranty = Initializer.newWarranty(['en':'warranty'],'warranty name','email@gmail.com',"0768-889-787","Street 154","6353",Initializer.getDate(10, 12, 2010),false,[:])

		equipment.manufacturer=manufacture
		equipment.supplier=supplier
		equipment.warranty=warranty
		equipment.warrantyPeriod = Initializer.newPeriod(3)
		equipment.save(failOnError: true)
		when:
		def statusThree= Initializer.newEquipmentStatus(Initializer.getDate(10, 12, 2010),User.findByUsername("admin"),Status.INSTOCK,equipment,[:])
		def statusTwo= Initializer.newEquipmentStatus(Initializer.getDate(10, 12, 2011),User.findByUsername("admin"),Status.OPERATIONAL,equipment,[:])
		def statusOne= Initializer.newEquipmentStatus(Initializer.getDate(10, 07, 2012),User.findByUsername("admin"),Status.UNDERMAINTENANCE,equipment,[:])
		then:
		Equipment.count() == 1
		Equipment.list()[0].timeBasedStatus.status==statusOne.status
	}
	
	def "can get current status"(){
		setup:
		setupLocationTree()
		setupSystemUser()
		def department = Initializer.newDepartment(['en':"testName"], CODE(123),['en':"testDescription"])
		def equipmentType = Initializer.newEquipmentType(CODE(15810),["en":"Accelerometers"],["en":"used in memms"],Observation.USEDINMEMMS,Initializer.now(),45)
		def user  = newUser("admin", "Admin UID")
		def equipment = new Equipment(serialNumber:"test123", purchaseCost:"1,200",currency:"RWF",manufactureDate:Initializer.getDate(22,07,2010),
			purchaseDate:Initializer.getDate(22,07,2010),dateCreated:Initializer.getDate(23,07,2010), model:"equipmentModel", department:department,
			 dataLocation:DataLocation.list().first(),purchaser:PurchasedBy.BYMOH,obsolete:false,expectedLifeTime:Initializer.newPeriod(20),
			 descriptions:['en':'Equipment Descriptions'], type:equipmentType,currentStatus:Status.OPERATIONAL,
			 addedBy: User.findByUsername("systemUser"))
		def manufactureContact = Initializer.newContact(['en':'Address Descriptions '],"Manufacture","jkl@yahoo.com","0768-889-787","Street 154","6353")
		def supplierContact = Initializer.newContact([:],"Supplier","jk@yahoo.com","0768-888-787","Street 1654","6353")
		def manufacture = Initializer.newProvider(CODE(123), Type.MANUFACTURER,manufactureContact)
		def supplier = Initializer.newProvider(CODE(124), Type.SUPPLIER,supplierContact)
		def contact = Initializer.newContact([:],"Contact","jk@yahoo.com","0768-888-787","Street 654","6353")
		def warranty = Initializer.newWarranty(['en':'warranty'],'warranty name','email@gmail.com',"0768-889-787","Street 154","6353",Initializer.getDate(10, 12, 2010),false,[:])
		equipment.manufacturer=manufacture
		equipment.supplier=supplier
		equipment.warranty=warranty
		equipment.warrantyPeriod = Initializer.newPeriod(5)
		equipment.save(failOnError: true)
		
		when:
		def statusThree= Initializer.newEquipmentStatus(Initializer.getDate(10, 12, 2010),User.findByUsername("admin"),Status.INSTOCK,equipment,[:])
		def statusTwo= Initializer.newEquipmentStatus(Initializer.getDate(10, 12, 2011),User.findByUsername("admin"),Status.OPERATIONAL,equipment,[:])
		def statusOne= Initializer.newEquipmentStatus(Initializer.getDate(10, 07, 2012),User.findByUsername("admin"),Status.UNDERMAINTENANCE,equipment,[:])
		then:
		Equipment.count() == 1
		Equipment.list()[0].timeBasedStatus.status == statusOne.status
		
		
		when:
		equipment.status=[]
		equipment.save(failOnError: true)
		def statusSix= Initializer.newEquipmentStatus(Initializer.getDate(10, 12, 2010),User.findByUsername("admin"),Status.INSTOCK,equipment,[:])
		def statusFive= Initializer.newEquipmentStatus(Initializer.getDate(10, 12, 2011),User.findByUsername("admin"),Status.OPERATIONAL,equipment,[:])
		def statusFour= Initializer.newEquipmentStatus(Initializer.getDate(10, 07, 2012),User.findByUsername("admin"),Status.UNDERMAINTENANCE,equipment,[:])
		then:
		Equipment.count() == 1
		Equipment.list()[0].timeBasedStatus.is(statusFour)
		
		when:
		equipment.status=[]
		equipment.save(failOnError: true)
		def statusN= Initializer.newEquipmentStatus(Initializer.getDate(10, 12, 2010),User.findByUsername("admin"),Status.INSTOCK,equipment,[:])
		def statusH= Initializer.newEquipmentStatus(Initializer.getDate(10, 12, 2011),User.findByUsername("admin"),Status.OPERATIONAL,equipment,[:])
		def statusS= Initializer.newEquipmentStatus(Initializer.getDate(10, 07, 2012),User.findByUsername("admin"),Status.UNDERMAINTENANCE,equipment,[:])
		then:
		Equipment.count() == 1
		Equipment.list()[0].timeBasedStatus.is(statusS)
	}
	
	def "test serviceProvider exist serviceContractPeriod and serviceContractStartDate must exist"() {
		
				setup:
				setupLocationTree()
				setupSystemUser()
				def department = Initializer.newDepartment(['en':"testName"], CODE(123),['en':"testDescription"])
				def equipmentType = Initializer.newEquipmentType("15810", ["en":"Accelerometers"],["en":"used in memms"],Observation.USEDINMEMMS,Initializer.now(),9)
				
				when:
				def equipment = new Equipment(serialNumber:"test123", purchaseCost:"1,200",currency:"EURO",
					manufactureDate:Initializer.now(),purchaseDate:Initializer.getDate(22,07,2010),
					dateCreated:Initializer.getDate(23,07,2010), model:"equipmentModel", department:department,
					purchaser:PurchasedBy.BYFACILITY,obsolete:false,expectedLifeTime:Initializer.newPeriod(20),
					dataLocation:DataLocation.list().first(),descriptions:['en':'Equipment Descriptions'],type:equipmentType,
					currentStatus:Status.OPERATIONAL,addedBy: User.findByUsername("systemUser")
				)
				def serviceProContact = Initializer.newContact([:],"Supplier","jk@yahoo.com","0768-888-787","Street 1654","6353")
				def manufactureContact = Initializer.newContact(['en':'Address Descriptions '],"Manufacture","jkl@yahoo.com","0768-889-787","Street 154","6353")
				def supplierContact = Initializer.newContact([:],"Supplier","jk@yahoo.com","0768-888-787","Street 1654","6353")
				def manufacture = Initializer.newProvider(CODE(123), Type.MANUFACTURER,manufactureContact)
				def supplier = Initializer.newProvider(CODE(124), Type.SUPPLIER,supplierContact)
				def servicePro = Initializer.newProvider(CODE(125), Type.SERVICEPROVIDER,serviceProContact)
				def contact = Initializer.newContact([:],"Contact","jk@yahoo.com","0768-888-787","Street 654","6353")
				def warranty = Initializer.newWarranty(['en':'warranty'],'warranty name','email@gmail.com',"0768-889-787","Street 154","6353",Initializer.getDate(10, 12, 2010),false,[:])
		
				equipment.manufacturer=manufacture
				equipment.supplier=supplier
				equipment.warranty=warranty
				equipment.warrantyPeriod = Initializer.newPeriod(12)
				equipment.serviceProvider=servicePro
		
				equipment.save()
				then:
				Equipment.count() == 0
				equipment.errors.hasFieldErrors('serviceContractPeriod') == true
				equipment.errors.hasFieldErrors('serviceContractStartDate') == true
				
		}
	    def "test serviceContractPeriod  exist serviceProvider and serviceContractStartDate must exist"() {
		
				setup:
				setupLocationTree()
				setupSystemUser()
				def department = Initializer.newDepartment(['en':"testName"], CODE(123),['en':"testDescription"])
				def equipmentType = Initializer.newEquipmentType("15810", ["en":"Accelerometers"],["en":"used in memms"],Observation.USEDINMEMMS,Initializer.now(),9)
				
				when:
				def equipment = new Equipment(serialNumber:"test123", purchaseCost:"1,200",currency:"EURO",
					manufactureDate:Initializer.now(),purchaseDate:Initializer.getDate(22,07,2010),
					dateCreated:Initializer.getDate(23,07,2010), model:"equipmentModel", department:department,
					purchaser:PurchasedBy.BYFACILITY,obsolete:false,expectedLifeTime:Initializer.newPeriod(20),
					dataLocation:DataLocation.list().first(),descriptions:['en':'Equipment Descriptions'],type:equipmentType,
					currentStatus:Status.OPERATIONAL,addedBy: User.findByUsername("systemUser")
				)
				def manufactureContact = Initializer.newContact(['en':'Address Descriptions '],"Manufacture","jkl@yahoo.com","0768-889-787","Street 154","6353")
				def supplierContact = Initializer.newContact([:],"Supplier","jk@yahoo.com","0768-888-787","Street 1654","6353")
				def manufacture = Initializer.newProvider(CODE(123), Type.MANUFACTURER,manufactureContact)
				def supplier = Initializer.newProvider(CODE(124), Type.SUPPLIER,supplierContact)
				def contact = Initializer.newContact([:],"Contact","jk@yahoo.com","0768-888-787","Street 654","6353")
				def warranty = Initializer.newWarranty(['en':'warranty'],'warranty name','email@gmail.com',"0768-889-787","Street 154","6353",Initializer.getDate(10, 12, 2010),false,[:])
		
				equipment.manufacturer=manufacture
				equipment.supplier=supplier
				equipment.warranty=warranty
				equipment.warrantyPeriod = Initializer.newPeriod(12)
				equipment.serviceContractPeriod=Initializer.newPeriod(6)
		
				equipment.save()
				then:
				Equipment.count() == 0
				equipment.errors.hasFieldErrors('serviceProvider') == true
				equipment.errors.hasFieldErrors('serviceContractStartDate') == true
				
			}
			def "test serviceContractStartDate exist serviceProvider and serviceContractPeriod must exist"() {
			
					setup:
					setupLocationTree()
					setupSystemUser()
					def department = Initializer.newDepartment(['en':"testName"], CODE(123),['en':"testDescription"])
					def equipmentType = Initializer.newEquipmentType("15810", ["en":"Accelerometers"],["en":"used in memms"],Observation.USEDINMEMMS,Initializer.now(),9)
					
					when:
					def equipment = new Equipment(serialNumber:"test123", purchaseCost:"1,200",currency:"EURO",
						manufactureDate:Initializer.now(),purchaseDate:Initializer.getDate(22,07,2010),
						dateCreated:Initializer.getDate(23,07,2010), model:"equipmentModel", department:department,
						purchaser:PurchasedBy.BYFACILITY,obsolete:false,expectedLifeTime:Initializer.newPeriod(20),
						dataLocation:DataLocation.list().first(),descriptions:['en':'Equipment Descriptions'],type:equipmentType,
						currentStatus:Status.OPERATIONAL,addedBy: User.findByUsername("systemUser")
					)
					def manufactureContact = Initializer.newContact(['en':'Address Descriptions '],"Manufacture","jkl@yahoo.com","0768-889-787","Street 154","6353")
					def supplierContact = Initializer.newContact([:],"Supplier","jk@yahoo.com","0768-888-787","Street 1654","6353")
					def manufacture = Initializer.newProvider(CODE(123), Type.MANUFACTURER,manufactureContact)
					def supplier = Initializer.newProvider(CODE(124), Type.SUPPLIER,supplierContact)
					def contact = Initializer.newContact([:],"Contact","jk@yahoo.com","0768-888-787","Street 654","6353")
					def warranty = Initializer.newWarranty(['en':'warranty'],'warranty name','email@gmail.com',"0768-889-787","Street 154","6353",Initializer.getDate(10, 12, 2010),false,[:])
			
					equipment.manufacturer=manufacture
					equipment.supplier=supplier
					equipment.warranty=warranty
					equipment.warrantyPeriod = Initializer.newPeriod(12)
					equipment.serviceContractStartDate=Initializer.getDate(22,07,2010)
			
					equipment.save()
					then:
					Equipment.count() == 0
					equipment.errors.hasFieldErrors('serviceProvider') == true
					equipment.errors.hasFieldErrors('serviceContractPeriod') == true
					
			}
			def "test serviceContractStartDate has to come after purchase date"() {
				
					setup:
					setupLocationTree()
					setupSystemUser()
					def department = Initializer.newDepartment(['en':"testName"], CODE(123),['en':"testDescription"])
					def equipmentType = Initializer.newEquipmentType("15810", ["en":"Accelerometers"],["en":"used in memms"],Observation.USEDINMEMMS,Initializer.now(),9)
					
					when:
					def equipment = new Equipment(serialNumber:"test123", purchaseCost:"1,200",currency:"EURO",
						manufactureDate:Initializer.now(),purchaseDate:Initializer.getDate(22,07,2010),
						dateCreated:Initializer.getDate(23,07,2010), model:"equipmentModel", department:department,
						purchaser:PurchasedBy.BYFACILITY,obsolete:false,expectedLifeTime:Initializer.newPeriod(20),
						dataLocation:DataLocation.list().first(),descriptions:['en':'Equipment Descriptions'],type:equipmentType,
						currentStatus:Status.OPERATIONAL,addedBy: User.findByUsername("systemUser")
					)
					def serviceProContact = Initializer.newContact([:],"Supplier","jk@yahoo.com","0768-888-787","Street 1654","6353")
					def manufactureContact = Initializer.newContact(['en':'Address Descriptions '],"Manufacture","jkl@yahoo.com","0768-889-787","Street 154","6353")
					def supplierContact = Initializer.newContact([:],"Supplier","jk@yahoo.com","0768-888-787","Street 1654","6353")
					def manufacture = Initializer.newProvider(CODE(123), Type.MANUFACTURER,manufactureContact)
					def supplier = Initializer.newProvider(CODE(124), Type.SUPPLIER,supplierContact)
					def servicePro = Initializer.newProvider(CODE(125), Type.SERVICEPROVIDER,serviceProContact)
					def contact = Initializer.newContact([:],"Contact","jk@yahoo.com","0768-888-787","Street 654","6353")
					def warranty = Initializer.newWarranty(['en':'warranty'],'warranty name','email@gmail.com',"0768-889-787","Street 154","6353",Initializer.getDate(10, 12, 2010),false,[:])
			
					equipment.manufacturer=manufacture
					equipment.supplier=supplier
					equipment.warranty=warranty
					equipment.warrantyPeriod = Initializer.newPeriod(12)
					equipment.serviceProvider=servicePro
					equipment.serviceContractPeriod=Initializer.newPeriod(6)
					equipment.serviceContractStartDate=Initializer.getDate(22,07,2010)-1
			
					equipment.save()
					then:
					Equipment.count() == 0
					equipment.errors.hasFieldErrors('serviceContractStartDate') == true
			
						
				}
	
}
