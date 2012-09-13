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

import org.chai.memms.Initializer;
import org.chai.memms.IntegrationTests;
import org.chai.memms.equipment.EquipmentStatus.Status;
import org.chai.memms.equipment.EquipmentType.Observation;
import org.chai.location.DataLocation;
import org.chai.memms.security.User;
import org.chai.memms.IntegrationTests
import org.chai.location.DataLocation;
import org.chai.memms.equipment.Provider.Type;
/**
 * @author Jean Kahigiso M.
 *
 */
class EquipmentSpec extends IntegrationTests{

	def "can create and save an equipment"() {

		setup:
		setupLocationTree()
		def department = Initializer.newDepartment(['en':"testName"], CODE(123),['en':"testDescription"])
		def equipmentModel = Initializer.newEquipmentModel(['en':"testName"], CODE(123),['en':"testDescription"])
		def equipmentType = Initializer.newEquipmentType(CODE(15810),["en":"Accelerometers"],["en":"used in memms"],Observation.USEDINMEMMS,Initializer.now(),Initializer.now())
		when:
		def equipment = new Equipment(serialNumber:"test123",manufactureDate:Initializer.getDate(22,07,2010),
			purchaseDate:Initializer.getDate(22,07,2010),registeredOn:Initializer.getDate(23,07,2010), model:"equipmentModel", department:department,
			 dataLocation:DataLocation.list().first(),donation:true,obsolete:false,expectedLifeTime:20,
			 descriptions:['en':'Equipment Descriptions'], type:equipmentType)

		def manufactureContact = Initializer.newContact(['en':'Address Descriptions '],"Manufacture","jkl@yahoo.com","0768-889-787","Street 154","6353")
		def supplierContact = Initializer.newContact([:],"Supplier","jk@yahoo.com","0768-888-787","Street 1654","6353")
		
		def manufacture = Initializer.newProvider(CODE(123), Type.MANUFACTURER,manufactureContact)
		def supplier = Initializer.newProvider(CODE(124), Type.SUPPLIER,supplierContact)
		
		def contact = Initializer.newContact([:],"Contact","jk@yahoo.com","0768-888-787","Street 654","6353")
		def warranty = Initializer.newWarranty(['en':'warranty'],'warranty name','email@gmail.com',"0768-889-787","Street 154","6353",Initializer.getDate(10, 12, 2010),4,false,[:])

		equipment.manufacturer=manufacture
		equipment.supplier=supplier
		
		equipment.warranty=warranty

		equipment.save(failOnError: true)
		then:
		Equipment.count() == 1
	}

	def "can't create and save an equipment without a serial number"() {

		setup:
		setupLocationTree()
		def department = Initializer.newDepartment(['en':"testName"], CODE(123),['en':"testDescription"])
		def equipmentModel = Initializer.newEquipmentModel(['en':"testName"], CODE(123),['en':"testDescription"])
		def equipmentType = Initializer.newEquipmentType(CODE(15810), ["en":"Accelerometers"],["en":"used in memms"],Observation.USEDINMEMMS,Initializer.now(),Initializer.now())
		when:
		def equipment = new Equipment(purchaseCost:"1,200",manufactureDate:Initializer.getDate(22,07,2010),purchaseDate:Initializer.getDate(22,07,2010),
				registeredOn:Initializer.getDate(23,07,2010), model:"equipmentModel", department:department,donation:false,obsolete:false,expectedLifeTime:20,
				dataLocation:DataLocation.list().first(),descriptions:['en':'Equipment Descriptions'], type:equipmentType)

		def manufactureContact = Initializer.newContact(['en':'Address Descriptions '],"Manufacture","jkl@yahoo.com","0768-889-787","Street 154","6353")
		def supplierContact = Initializer.newContact([:],"Supplier","jk@yahoo.com","0768-888-787","Street 1654","6353")
		
		def manufacture = Initializer.newProvider(CODE(123), Type.MANUFACTURER,manufactureContact)
		def supplier = Initializer.newProvider(CODE(124), Type.SUPPLIER,supplierContact)
		def contact = Initializer.newContact([:],"Contact","jk@yahoo.com","0768-888-787","Street 654","6353")
		def warranty = Initializer.newWarranty(['en':'warranty'],'warranty name','email@gmail.com',"0768-889-787","Street 154","6353",Initializer.getDate(10, 12, 2010),12,false,[:])

		equipment.manufacturer=manufacture
		equipment.supplier=supplier
		equipment.warranty=warranty

		equipment.save()
		then:
		Equipment.count() == 0
		equipment.errors.hasFieldErrors('serialNumber') == true
	}

	def "can't create and save an equipment with a purchase cost and donation both have value"() {

		setup:
		setupLocationTree()
		def department = Initializer.newDepartment(['en':"testName"], CODE(123),['en':"testDescription"])
		def equipmentModel = Initializer.newEquipmentModel(['en':"testName"], CODE(123),['en':"testDescription"])
		def equipmentType = Initializer.newEquipmentType(CODE(15810), ["en":"Accelerometers"],["en":"used in memms"],Observation.USEDINMEMMS,Initializer.now(),Initializer.now())
		when:
		def equipment = new Equipment(purchaseCost:"200455.8",serialNumber:"test123",manufactureDate:Initializer.getDate(22,07,2010),purchaseDate:Initializer.getDate(22,07,2010),
				registeredOn:Initializer.getDate(23,07,2010), model:"equipmentModel", department:department,donation:true,obsolete:false,expectedLifeTime:20,
				dataLocation:DataLocation.list().first(),descriptions:['en':'Equipment Descriptions'],type:equipmentType)

		def manufactureContact = Initializer.newContact(['en':'Address Descriptions '],"Manufacture","jkl@yahoo.com","0768-889-787","Street 154","6353")
		def supplierContact = Initializer.newContact([:],"Supplier","jk@yahoo.com","0768-888-787","Street 1654","6353")
		
		def manufacture = Initializer.newProvider(CODE(123), Type.MANUFACTURER,manufactureContact)
		def supplier = Initializer.newProvider(CODE(124), Type.SUPPLIER,supplierContact)
		def contact = Initializer.newContact([:],"Contact","jk@yahoo.com","0768-888-787","Street 654","6353")
		def warranty = Initializer.newWarranty(['en':'warranty'],'warranty name','email@gmail.com',"0768-889-787","Street 154","6353",Initializer.getDate(10, 12, 2010),11,false,[:])

		equipment.manufacturer=manufacture
		equipment.supplier=supplier
		equipment.warranty=warranty

		equipment.save()
		then:
		Equipment.count() == 0
		equipment.errors.hasFieldErrors('purchaseCost') == true
	}

	def "can't create and save an equipment with a duplicate serial number"() {

		setup:
		setupLocationTree()
		def manufactureContact = Initializer.newContact(['en':'Address Descriptions '],"Manufacture","jkl@yahoo.com","0768-889-787","Street 154","6353")
		def supplierContact = Initializer.newContact([:],"Supplier","jk@yahoo.com","0768-888-787","Street 1654","6353")
		def department = Initializer.newDepartment(['en':"testName"], CODE(123),['en':"testDescription"])
		def equipmentModel = Initializer.newEquipmentModel(['en':"testName"], CODE(124),['en':"testDescription"])
		def equipmentType = Initializer.newEquipmentType(CODE(15810), ["en":"Accelerometers"],["en":"used in memms"],Observation.USEDINMEMMS,Initializer.now(),Initializer.now())
		def manufacture = Initializer.newProvider(CODE(123), Type.MANUFACTURER,manufactureContact)
		def supplier = Initializer.newProvider(CODE(124), Type.SUPPLIER,supplierContact)
		Initializer.newEquipment("test123",false,false,30,"ROOM A1","16677",['en':"testDescription"],Initializer.getDate(22,07,2010), Initializer.getDate(22,07,2010),"RWF",
				Initializer.getDate(22,07,2010),"equipmentModel",DataLocation.list().first(),department, equipmentType,manufacture,supplier)
			
		when:
		def equipment = new Equipment(serialNumber:"test123",purchaseCost:"1,200",currency:"USD",manufactureDate:Initializer.getDate(22,07,2010),purchaseDate:Initializer.getDate(22,07,2010),
				registeredOn:Initializer.getDate(23,07,2010), model:"equipmentModel", department:department, donation:false,obsolete:false,expectedLifeTime:20,
				dataLocation:DataLocation.list().first(),descriptions:['en':'Equipment Descriptions'],type:equipmentType)

		def contact = Initializer.newContact([:],"Contact","jk@yahoo.com","0768-888-787","Street 654","6353")
		def warranty = Initializer.newWarranty(['en':'warranty'],'warranty name','email@gmail.com',"0768-889-787","Street 154","6353",Initializer.getDate(10, 12, 2010),21,false,[:])

		equipment.manufacturer=manufacture
		equipment.supplier=supplier
		equipment.warranty=warranty

		equipment.save()
		then:
		Equipment.count() == 1
		equipment.errors.hasFieldErrors('serialNumber') == true
	}

	def "manufacture date must be after today"() {

		setup:
		setupLocationTree()
		def department = Initializer.newDepartment(['en':"testName"], CODE(123),['en':"testDescription"])
		def equipmentModel = Initializer.newEquipmentModel(['en':"testName"], CODE(123),['en':"testDescription"])
		def equipmentType = Initializer.newEquipmentType("15810", ["en":"Accelerometers"],["en":"used in memms"],Observation.USEDINMEMMS,Initializer.now(),Initializer.now())
		when:
		def equipment = new Equipment(serialNumber:"test123", purchaseCost:"1,200",currency:"EURO",manufactureDate:Initializer.now().next(),purchaseDate:Initializer.getDate(22,07,2010),
				registeredOn:Initializer.getDate(23,07,2010), model:"equipmentModel", department:department, donation:false,obsolete:false,expectedLifeTime:20,
				dataLocation:DataLocation.list().first(),descriptions:['en':'Equipment Descriptions'],type:equipmentType)

		def manufactureContact = Initializer.newContact(['en':'Address Descriptions '],"Manufacture","jkl@yahoo.com","0768-889-787","Street 154","6353")
		def supplierContact = Initializer.newContact([:],"Supplier","jk@yahoo.com","0768-888-787","Street 1654","6353")
		
		def manufacture = Initializer.newProvider(CODE(123), Type.MANUFACTURER,manufactureContact)
		def supplier = Initializer.newProvider(CODE(124), Type.SUPPLIER,supplierContact)
		def contact = Initializer.newContact([:],"Contact","jk@yahoo.com","0768-888-787","Street 654","6353")
		def warranty = Initializer.newWarranty(['en':'warranty'],'warranty name','email@gmail.com',"0768-889-787","Street 154","6353",Initializer.getDate(10, 12, 2010),12,false,[:])

		equipment.manufacturer=manufacture
		equipment.supplier=supplier
		equipment.warranty=warranty

		equipment.save()
		then:
		Equipment.count() == 0
		equipment.errors.hasFieldErrors('manufactureDate') == true
	}

	def "purchase date can't be after today"() {

		setup:
		setupLocationTree()
		def department = Initializer.newDepartment(['en':"testName"], CODE(123),['en':"testDescription"])
		def equipmentModel = Initializer.newEquipmentModel(['en':"testName"], CODE(123),['en':"testDescription"])
		def equipmentType = Initializer.newEquipmentType("15810", ["en":"Accelerometers"],["en":"used in memms"],Observation.USEDINMEMMS,Initializer.now(),Initializer.now())
		when:
		def equipment = new Equipment(serialNumber:"test123", purchaseCost:"1,200",currency:"USD",manufactureDate:Initializer.getDate(22,07,2010),purchaseDate:Initializer.now().next(),
				registeredOn:Initializer.getDate(22,07,2010), model:"equipmentModel", department:department, donation:false,obsolete:false,expectedLifeTime:20,
				dataLocation:DataLocation.list().first(),descriptions:['en':'Equipment Descriptions'],type:equipmentType)

		def manufactureContact = Initializer.newContact(['en':'Address Descriptions '],"Manufacture","jkl@yahoo.com","0768-889-787","Street 154","6353")
		def supplierContact = Initializer.newContact([:],"Supplier","jk@yahoo.com","0768-888-787","Street 1654","6353")
		
		def manufacture = Initializer.newProvider(CODE(123), Type.MANUFACTURER,manufactureContact)
		def supplier = Initializer.newProvider(CODE(124), Type.SUPPLIER,supplierContact)
		def contact = Initializer.newContact([:],"Contact","jk@yahoo.com","0768-888-787","Street 654","6353")
		def warranty = Initializer.newWarranty(['en':'warranty'],'warranty name','email@gmail.com',"0768-889-787","Street 154","6353",Initializer.getDate(10, 12, 2010),22,false,[:])

		equipment.manufacturer=manufacture
		equipment.supplier=supplier
		equipment.warranty=warranty

		equipment.save()
		then:
		Equipment.count() == 0
		equipment.errors.hasFieldErrors('purchaseDate') == true
	}
	
	def "can get current status based on time"(){
		setup:
		setupLocationTree()
		def department = Initializer.newDepartment(['en':"testName"], CODE(123),['en':"testDescription"])
		def equipmentModel = Initializer.newEquipmentModel(['en':"testName"], CODE(123),['en':"testDescription"])
		def equipmentType = Initializer.newEquipmentType(CODE(15810),["en":"Accelerometers"],["en":"used in memms"],Observation.USEDINMEMMS,Initializer.now(),Initializer.now())
		def user  = newUser("admin", "Admin UID")
		def equipment = new Equipment(serialNumber:"test123",manufactureDate:Initializer.getDate(22,07,2010),
			purchaseDate:Initializer.getDate(22,07,2010),registeredOn:Initializer.getDate(23,07,2010), model:"equipmentModel", department:department,
			 dataLocation:DataLocation.list().first(),donation:true,obsolete:false,expectedLifeTime:20,
			 descriptions:['en':'Equipment Descriptions'], type:equipmentType)

		def manufactureContact = Initializer.newContact(['en':'Address Descriptions '],"Manufacture","jkl@yahoo.com","0768-889-787","Street 154","6353")
		def supplierContact = Initializer.newContact([:],"Supplier","jk@yahoo.com","0768-888-787","Street 1654","6353")
		
		def manufacture = Initializer.newProvider(CODE(123), Type.MANUFACTURER,manufactureContact)
		def supplier = Initializer.newProvider(CODE(124), Type.SUPPLIER,supplierContact)
		
		def contact = Initializer.newContact([:],"Contact","jk@yahoo.com","0768-888-787","Street 654","6353")
		def warranty = Initializer.newWarranty(['en':'warranty'],'warranty name','email@gmail.com',"0768-889-787","Street 154","6353",Initializer.getDate(10, 12, 2010),22,false,[:])

		equipment.manufacturer=manufacture
		equipment.supplier=supplier
		equipment.warranty=warranty
		equipment.save(failOnError: true)
		
		when:
		def statusThree= Initializer.newEquipmentStatus(Initializer.getDate(10, 12, 2010),User.findByUsername("admin"),Status.INSTOCK,equipment,false)
		def statusTwo= Initializer.newEquipmentStatus(Initializer.getDate(10, 12, 2011),User.findByUsername("admin"),Status.OPERATIONAL,equipment,false)
		def statusOne= Initializer.newEquipmentStatus(Initializer.getDate(10, 07, 2012),User.findByUsername("admin"),Status.UNDERMAINTENANCE,equipment,true)
		equipment.status =[statusThree,statusTwo,statusOne]
		equipment.save(failOnError: true)
		then:
		Equipment.count() == 1
		Equipment.list()[0].getCurrentStatusBasedOnTime().is(statusOne)
	}
	
	def "can set current status based on time"(){
		setup:
		setupLocationTree()
		def department = Initializer.newDepartment(['en':"testName"], CODE(123),['en':"testDescription"])
		def equipmentModel = Initializer.newEquipmentModel(['en':"testName"], CODE(123),['en':"testDescription"])
		def equipmentType = Initializer.newEquipmentType(CODE(15810),["en":"Accelerometers"],["en":"used in memms"],Observation.USEDINMEMMS,Initializer.now(),Initializer.now())
		def user  = newUser("admin", "Admin UID")
		def equipment = new Equipment(serialNumber:"test123", purchaseCost:"1,200",currency:"RWF",manufactureDate:Initializer.getDate(22,07,2010),
			purchaseDate:Initializer.getDate(22,07,2010),registeredOn:Initializer.getDate(23,07,2010), model:"equipmentModel", department:department,
			 dataLocation:DataLocation.list().first(),donation:false,obsolete:false,expectedLifeTime:20,
			 descriptions:['en':'Equipment Descriptions'], type:equipmentType)

		def manufactureContact = Initializer.newContact(['en':'Address Descriptions '],"Manufacture","jkl@yahoo.com","0768-889-787","Street 154","6353")
		def supplierContact = Initializer.newContact([:],"Supplier","jk@yahoo.com","0768-888-787","Street 1654","6353")
		
		def manufacture = Initializer.newProvider(CODE(123), Type.MANUFACTURER,manufactureContact)
		def supplier = Initializer.newProvider(CODE(124), Type.SUPPLIER,supplierContact)
		
		def contact = Initializer.newContact([:],"Contact","jk@yahoo.com","0768-888-787","Street 654","6353")
		def warranty = Initializer.newWarranty(['en':'warranty'],'warranty name','email@gmail.com',"0768-889-787","Street 154","6353",Initializer.getDate(10, 12, 2010),3,false,[:])

		equipment.manufacturer=manufacture
		equipment.supplier=supplier
		equipment.warranty=warranty
		equipment.save(failOnError: true)
		when:
		def statusThree= Initializer.newEquipmentStatus(Initializer.getDate(10, 12, 2010),User.findByUsername("admin"),Status.INSTOCK,equipment,false)
		def statusTwo= Initializer.newEquipmentStatus(Initializer.getDate(10, 12, 2011),User.findByUsername("admin"),Status.OPERATIONAL,equipment,true)
		def statusOne= Initializer.newEquipmentStatus(Initializer.getDate(10, 07, 2012),User.findByUsername("admin"),Status.UNDERMAINTENANCE,equipment,false)
		equipment.status =[statusThree,statusTwo,statusOne]
		equipment.save(failOnError: true)
		Equipment.list()[0].setCurrentStatus()
		then:
		Equipment.count() == 1
		Equipment.list()[0].getCurrentStatusBasedOnTime().is(statusOne)
		statusTwo.current == false
	}
	
	def "can get current status"(){
		setup:
		setupLocationTree()
		def department = Initializer.newDepartment(['en':"testName"], CODE(123),['en':"testDescription"])
		def equipmentModel = Initializer.newEquipmentModel(['en':"testName"], CODE(123),['en':"testDescription"])
		def equipmentType = Initializer.newEquipmentType(CODE(15810),["en":"Accelerometers"],["en":"used in memms"],Observation.USEDINMEMMS,Initializer.now(),Initializer.now())
		def user  = newUser("admin", "Admin UID")
		def equipment = new Equipment(serialNumber:"test123", purchaseCost:"1,200",currency:"RWF",manufactureDate:Initializer.getDate(22,07,2010),
			purchaseDate:Initializer.getDate(22,07,2010),registeredOn:Initializer.getDate(23,07,2010), model:"equipmentModel", department:department,
			 dataLocation:DataLocation.list().first(),donation:false,obsolete:false,expectedLifeTime:20,
			 descriptions:['en':'Equipment Descriptions'], type:equipmentType)
		def manufactureContact = Initializer.newContact(['en':'Address Descriptions '],"Manufacture","jkl@yahoo.com","0768-889-787","Street 154","6353")
		def supplierContact = Initializer.newContact([:],"Supplier","jk@yahoo.com","0768-888-787","Street 1654","6353")
		def manufacture = Initializer.newProvider(CODE(123), Type.MANUFACTURER,manufactureContact)
		def supplier = Initializer.newProvider(CODE(124), Type.SUPPLIER,supplierContact)
		def contact = Initializer.newContact([:],"Contact","jk@yahoo.com","0768-888-787","Street 654","6353")
		def warranty = Initializer.newWarranty(['en':'warranty'],'warranty name','email@gmail.com',"0768-889-787","Street 154","6353",Initializer.getDate(10, 12, 2010),5,false,[:])
		equipment.manufacturer=manufacture
		equipment.supplier=supplier
		equipment.warranty=warranty
		equipment.save(failOnError: true)
		
		
		when:
		def statusThree= Initializer.newEquipmentStatus(Initializer.getDate(10, 12, 2010),User.findByUsername("admin"),Status.INSTOCK,equipment,false)
		def statusTwo= Initializer.newEquipmentStatus(Initializer.getDate(10, 12, 2011),User.findByUsername("admin"),Status.OPERATIONAL,equipment,false)
		def statusOne= Initializer.newEquipmentStatus(Initializer.getDate(10, 07, 2012),User.findByUsername("admin"),Status.UNDERMAINTENANCE,equipment,false)
		equipment.status =[statusThree,statusTwo,statusOne]
		equipment.save(failOnError: true)
		then:
		Equipment.count() == 1
		Equipment.list()[0].getCurrentState().is(statusOne)
		statusTwo.current == false
		
		
		when:
		equipment.status=[]
		equipment.save(failOnError: true)
		def statusSix= Initializer.newEquipmentStatus(Initializer.getDate(10, 12, 2010),User.findByUsername("admin"),Status.INSTOCK,equipment,false)
		def statusFive= Initializer.newEquipmentStatus(Initializer.getDate(10, 12, 2011),User.findByUsername("admin"),Status.OPERATIONAL,equipment,false)
		def statusFour= Initializer.newEquipmentStatus(Initializer.getDate(10, 07, 2012),User.findByUsername("admin"),Status.UNDERMAINTENANCE,equipment,true)
		equipment.status =[statusSix,statusFive,statusFour]
		equipment.save(failOnError: true)
		then:
		Equipment.count() == 1
		Equipment.list()[0].getCurrentState().is(statusFour)
		statusFive.current == false
		
		when:
		equipment.status=[]
		equipment.save(failOnError: true)
		def statusN= Initializer.newEquipmentStatus(Initializer.getDate(10, 12, 2010),User.findByUsername("admin"),Status.INSTOCK,equipment,false)
		def statusH= Initializer.newEquipmentStatus(Initializer.getDate(10, 12, 2011),User.findByUsername("admin"),Status.OPERATIONAL,equipment,true)
		def statusS= Initializer.newEquipmentStatus(Initializer.getDate(10, 07, 2012),User.findByUsername("admin"),Status.UNDERMAINTENANCE,equipment,false)
		equipment.status =[statusN,statusH,statusS]
		equipment.save(failOnError: true)
		then:
		Equipment.count() == 1
		Equipment.list()[0].getCurrentState().is(statusS)
		statusH.current == false
	}
	
}
