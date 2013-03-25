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
package org.chai.memms.spare.part

import org.chai.location.DataLocation;
import org.chai.memms.Initializer;
import org.chai.memms.IntegrationTests;
import org.chai.memms.inventory.Equipment;
import org.chai.memms.inventory.Provider;
import org.chai.memms.inventory.Provider.Type;
import org.chai.memms.security.User;
import org.chai.memms.security.User.UserType;
import org.chai.memms.spare.part.SparePart;
import org.chai.memms.spare.part.SparePart.StockLocation;
import org.chai.memms.spare.part.SparePartType;
import org.chai.memms.spare.part.SparePart.SparePartPurchasedBy;
import org.chai.memms.spare.part.SparePartStatus;
import org.chai.memms.spare.part.SparePartStatus.StatusOfSparePart;

/**
 * @author Aphrodice Rwagaju
 *
 */
class SparePartServiceSpec extends IntegrationTests{
	def sparePartService
	def "can search spare part by serial number, model, and description"() {
		setup:
		setupLocationTree()
		def user = newOtherUser("user", "user", DataLocation.findByCode(KIVUYE))

		def manufactureContact = Initializer.newContact(['en':'Address Descriptions '],"Manufacture","jkl@yahoo.com","0768-889-787","Street 154","6353")
		def supplierContact = Initializer.newContact([:],"Supplier","jk@yahoo.com","0768-888-787","Street 1654","6353")

		def manufacturer = Initializer.newProvider(CODE(123), Type.MANUFACTURER,manufactureContact)
		def supplier = Initializer.newProvider(CODE(124), Type.SUPPLIER,supplierContact)
		def userHc = newOtherUserWithType("userHc", "userHc", DataLocation .findByCode(KIVUYE), UserType .TITULAIREHC)
		def techDH = newOtherUserWithType("techDH", "techDH", DataLocation.findByCode(BUTARO), UserType.TECHNICIANDH)
		def techMMC = newOtherUserWithType("techMMC", "techMMC", DataLocation.findByCode(RWANDA), UserType.TECHNICIANMMC)

		def sparePartType = Initializer.newSparePartType(CODE(15810),["en":"Accelerometers"],["en":"used in memms"],"CODE Spare Part",manufacturer,Initializer.now())
		
		def equipment01 = newEquipment("SERIAL01",DataLocation.findByCode(KIVUYE))
		def equipment09 = newEquipment("SERIAL09",DataLocation.findByCode(KIVUYE))
		def equipment10 = newEquipment("SERIAL10",DataLocation.findByCode(KIVUYE))
		Initializer.newSparePart(CODE(123),SparePartPurchasedBy.BYMOH,false,Initializer.newPeriod(32),"",['en':'Spare Part Descriptions one'],Initializer.getDate(22,07,2010),Initializer.getDate(22,07,2011),"",'MODEL1',
				DataLocation.findByCode(BUTARO),sparePartType,supplier,StatusOfSparePart.INSTOCK,user,null,null,StockLocation.FACILITY, null)
		def sparePartCodeToFind = Initializer.newSparePart(CODE(124),SparePartPurchasedBy.BYMOH,false,Initializer.newPeriod(32),"2900.23",['en':'Spare Part Descriptions two'],Initializer.getDate(22,07,2010), Initializer.getDate(22,07,2011),"RWF",'MODEL2',
				DataLocation.findByCode(KIVUYE),sparePartType,supplier,StatusOfSparePart.INSTOCK,user,null,null,StockLocation.FACILITY, null)
		
		def List<SparePart> spareParts

		when: "user can view all those he manages"
		spareParts = sparePartService.searchSparePart("Descriptions",techDH,[:])

		then:
		spareParts.size() == 2
		spareParts[0] != spareParts[1]

		when: "user cannot see those he doesn't manage"

		spareParts = sparePartService.searchSparePart("Descriptions",userHc,[:])
		then:
		spareParts.size() == 1
		spareParts[0].serialNumber.equals(CODE(124))

		when: "Searching by description"

		spareParts = sparePartService.searchSparePart("one",techDH, [:])
		then:
		spareParts.size() == 1
		spareParts[0].getDescriptions(new Locale("en")).equals('Spare Part Descriptions one')

		when: "Searching by spare part name"
		spareParts = sparePartService.searchSparePart("Accelerometers",techDH, [:])
		then:
		spareParts.size() == 2
		spareParts[0] != spareParts[1]

		when: "Searching by code"
		spareParts = sparePartService.searchSparePart(sparePartCodeToFind.code+" ",techDH, [:])
		then:
		spareParts.size() == 1
		spareParts[0].code.equals(sparePartCodeToFind.code)

		when: "Searching by serial number"
		spareParts = sparePartService.searchSparePart(CODE(124),techDH, [:])
		then:
		spareParts.size() == 1
		spareParts[0].serialNumber.equals(CODE(124))

		when: "Searching by model"
		spareParts = sparePartService.searchSparePart("MODEL1",techDH, [:])
		then:
		spareParts.size() == 1
		spareParts[0].serialNumber.equals(CODE(123))
	}
	def "user can only view his spare parts, if a technician at dh they can also view for the locations they manage"() {
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

		def manufacturer = Initializer.newProvider(CODE(123), Type.MANUFACTURER,manufactureContact)
		def supplier = Initializer.newProvider(CODE(124), Type.SUPPLIER,supplierContact)
		def sparePartType = Initializer.newSparePartType(CODE(15810),["en":"Accelerometers"],["en":"used in memms"],"CODE Spare Part",manufacturer,Initializer.now())
		
		def equipment01 = newEquipment("SERIAL01",DataLocation.findByCode(KIVUYE))
		def equipment09 = newEquipment("SERIAL09",DataLocation.findByCode(KIVUYE))
		def equipment10 = newEquipment("SERIAL10",DataLocation.findByCode(KIVUYE))

		Initializer.newSparePart(CODE(123),SparePartPurchasedBy.BYMOH,false,Initializer.newPeriod(32),"",['en':'Spare Part Descriptions one'],Initializer.getDate(22,07,2010),
				Initializer.getDate(22,07,2011),"",'MODEL1',
				DataLocation.findByCode(BUTARO),sparePartType,supplier,StatusOfSparePart.INSTOCK,user,null,null, StockLocation.FACILITY, null)
		
		def sparePartCodeToFind = Initializer.newSparePart(CODE(124),SparePartPurchasedBy.BYMOH,false,Initializer.newPeriod(32),"2900.23",['en':'Spare Part Descriptions two'],Initializer.getDate(22,07,2010), Initializer.getDate(22,07,2011),"RWF",'MODEL2',
				DataLocation.findByCode(KIVUYE),sparePartType,supplier,StatusOfSparePart.INSTOCK,user,null,null, StockLocation.FACILITY,null)


		def sparePartsTech, sparePartsUser
		when:
		sparePartsTech = sparePartService.getSparePartsByUser(techDh,[:])
		sparePartsUser = sparePartService.getSparePartsByUser(user,[:])
		then:
		sparePartsTech.size() == 2
		sparePartsUser.size() == 1
	}
	
	def "filter spare parts"() {
		setup:
		setupLocationTree()

		def butaroDH =  DataLocation.findByCode('Butaro DH')
		def kivuyeHC = DataLocation.findByCode('Kivuye HC')
		def manufactureContact = Initializer.newContact(['en':'Address Descriptions '],"Manufacture","jkl@yahoo.com","0768-889-787","Street 154","6353")
		def supplierContact = Initializer.newContact([:],"Supplier","jk@yahoo.com","0768-888-787","Street 1654","6353")
		def manufacturer = Initializer.newProvider(CODE(111), Type.MANUFACTURER,manufactureContact)
		def supplier = Initializer.newProvider(CODE(222), Type.SUPPLIER,supplierContact)
		def equipment01 = newEquipment("SERIAL01",DataLocation.findByCode(KIVUYE))

		def user  = newUser("admin", "Admin UID")
		def sparePartType = Initializer.newSparePartType(CODE(15810),["en":"Accelerometers"],["en":"used in memms"],"CODE Spare Part",manufacturer,Initializer.now())
		def sparePartOne = Initializer.newSparePart("SERIAL10",SparePartPurchasedBy.BYMOH,false,Initializer.newPeriod(32),"",['en':'Spare Part Descriptions one'],Initializer.getDate(22,07,2010)
				,Initializer.getDate(10,10,2010),"","sparePartModel",
				kivuyeHC,sparePartType,supplier,StatusOfSparePart.INSTOCK
				,user,null,null, StockLocation.FACILITY,null)
	   
		def sparePartTwo = Initializer.newSparePart("SERIAL11",SparePartPurchasedBy.BYFACILITY,true,Initializer.newPeriod(32),"2900.23",['en':'Spare Part Descriptions two'],Initializer.getDate(22,07,2010)
				,Initializer.getDate(10,10,2010),"USD","sparePartModel",
				butaroDH,sparePartType,supplier,StatusOfSparePart.OPERATIONAL,
				,user,null,null, StockLocation.FACILITY,equipment01)

		sparePartOne.save(failOnError:true)
		sparePartTwo.save(failOnError:true)

		def List<SparePart> sparePartsOne, sparePartsTwo, sparePartsThree
		def sparePartStatusOneActive = Initializer.newSparePartStatus(Initializer.now(),user,StatusOfSparePart.INSTOCK,sparePartOne,[:], null)
		def sparePartStatusOneInActive = Initializer.newSparePartStatus(Initializer.now(),user,StatusOfSparePart.OPERATIONAL,sparePartOne,[:],equipment01)
		def sparePartStatusTwo = Initializer.newSparePartStatus(Initializer.now(),user,StatusOfSparePart.DISPOSED,sparePartOne,[:],equipment01)
		
		when://Search by defaults

		sparePartsOne = sparePartService.filterSparePart(null,butaroDH,supplier,sparePartType,SparePartPurchasedBy.NONE,'true',StatusOfSparePart.OPERATIONAL,[:])

		then:
		SparePart.count() == 2
		SparePart.list()[0].statusOfSparePart == StatusOfSparePart.DISPOSED
		SparePart.list()[1].statusOfSparePart ==StatusOfSparePart.OPERATIONAL
		sparePartsOne.size() == 1
		sparePartsOne[0].getDescriptions(new Locale("en")).equals('Spare Part Descriptions two')
		sparePartsOne[0].statusOfSparePart == StatusOfSparePart.OPERATIONAL
		sparePartsOne[0].sparePartPurchasedBy == SparePartPurchasedBy.BYFACILITY
		sparePartsOne[0].serialNumber.equals("SERIAL11")

		when://Search by statusOfSparePart

		sparePartsTwo = sparePartService.filterSparePart(null,kivuyeHC,supplier, sparePartType,SparePartPurchasedBy.BYMOH,'false',StatusOfSparePart.DISPOSED,[:])
		sparePartsThree = sparePartService.filterSparePart(null,kivuyeHC,supplier, sparePartType,SparePartPurchasedBy.BYMOH,'false',StatusOfSparePart.INSTOCK,[:])

		then:
		SparePart.count() == 2
		sparePartsTwo.size() == 1
		sparePartsThree.size() == 0
		sparePartsTwo[0].getDescriptions(new Locale("en")).equals('Spare Part Descriptions one')
		sparePartsTwo[0].status.size() == 3
	}
	
	def "can export spare parts"(){
		setup:
		setupLocationTree()
		

		def manufactureContact = Initializer.newContact(['en':'Address Descriptions '],"Manufacture","jkl@yahoo.com","0768-889-787","Street 154","6353")
		def supplierContact = Initializer.newContact([:],"Supplier","jk@yahoo.com","0768-888-787","Street 1654","6353")
		def manufacturer = Initializer.newProvider(CODE(111), Type.MANUFACTURER,manufactureContact)
		def supplier = Initializer.newProvider(CODE(222), Type.SUPPLIER,supplierContact)
		def warrantyContact = Initializer.newContact(['fr':'Warranty Address Descriptions One'],"Warranty","jk@yahoo.com","0768-888-787","Street 654","8988")
		def warranty = Initializer.newWarranty(warrantyContact,Initializer.getDate(10, 12, 2010),false,[:])
		
		def user  = newUser("user", "user", true, true)
		setupSecurityManager(user)
		
		def sparePartType = Initializer.newSparePartType(CODE(15810),["en":"Accelerometers"],["en":"used in memms"],"CODE Spare Part",manufacturer,Initializer.now())
		
		def equipment01 = newEquipment("SERIAL01",DataLocation.findByCode(KIVUYE))
		def equipment09 = newEquipment("SERIAL09",DataLocation.findByCode(KIVUYE))
		def equipment10 = newEquipment("SERIAL10",DataLocation.findByCode(KIVUYE))
		
		if(log.isDebugEnabled()) log.debug("Spare Part Type Created in CAN EXPORT SPARE PARTS:" + sparePartType)
		if(log.isDebugEnabled()) log.debug("Provider: Manufacturer Created in CAN EXPORT SPARE PARTS:" + manufacturer)
		def sparePartOne = Initializer.newSparePart("SERIAL10",SparePartPurchasedBy.BYFACILITY,true,Initializer.newPeriod(32),"",['en':'Spare Part Descriptions two'],Initializer.getDate(22,07,2010)
				,Initializer.getDate(10,10,2010),"","sparePartModel",
				DataLocation.findByCode(KIVUYE),sparePartType,supplier,StatusOfSparePart.INSTOCK,
				user,null,null,StockLocation.FACILITY, equipment01)
			
		//equipment01 has to be reviewed wherever it has been used from this section
		def sparePartTwo = Initializer.newSparePart("SERIAL11",SparePartPurchasedBy.BYFACILITY,true,Initializer.newPeriod(32),"2900.23",['en':'Spare Part Descriptions two'],Initializer.getDate(22,07,2010)
				,Initializer.getDate(10,10,2010),"EUR","sparePartModel",
				DataLocation.findByCode(KIVUYE),sparePartType,supplier,StatusOfSparePart.INSTOCK
				,user,null,null,StockLocation.FACILITY, equipment01)
		
		def sparePartThree = Initializer.newSparePart("SERIAL12",SparePartPurchasedBy.BYFACILITY,true,Initializer.newPeriod(32),"2900.23",['en':'Spare Part Descriptions two'],Initializer.getDate(22,07,2010)
			,Initializer.getDate(10,10,2010),"EUR","sparePartModel",
			DataLocation.findByCode(KIVUYE),sparePartType,supplier,StatusOfSparePart.INSTOCK,
			user,null,null,StockLocation.FACILITY, equipment01)
		
		def sparePartFour = Initializer.newSparePart("SERIAL13",SparePartPurchasedBy.BYFACILITY,true,Initializer.newPeriod(32),"2900.23",['en':'Spare Part Descriptions two'],Initializer.getDate(22,07,2010)
			,Initializer.getDate(10,10,2010),"EUR","sparePartModel",
			DataLocation.findByCode(BUTARO),sparePartType,supplier,StatusOfSparePart.INSTOCK
			,user,null,null,StockLocation.FACILITY, equipment01)
	
		def List<SparePart> sparePartsOne, sparePartsTwo, sparePartsThree
		def sparePartStatusOneActive = Initializer.newSparePartStatus(Initializer.now(),User.findByUsername("user"),StatusOfSparePart.INSTOCK,sparePartOne,[:],null)
		def sparePartStatusOneInActive = Initializer.newSparePartStatus(Initializer.now(),User.findByUsername("user"),StatusOfSparePart.OPERATIONAL,sparePartOne,[:],equipment01)
		
		sparePartOne.warranty=warranty
		sparePartOne.warrantyPeriod = Initializer.newPeriod(22)
		sparePartOne.addToStatus(sparePartStatusOneActive).save(failOnError:true,flush: true)
		sparePartTwo.warranty=warranty
		sparePartTwo.warrantyPeriod = Initializer.newPeriod(20)
		sparePartTwo.addToStatus(sparePartStatusOneActive).save(failOnError:true,flush: true)
		sparePartThree.warranty=warranty
		sparePartThree.warrantyPeriod = Initializer.newPeriod(12)
		sparePartThree.addToStatus(sparePartStatusOneActive).save(failOnError:true,flush: true)
		List<SparePart> spareParts = [sparePartOne,sparePartTwo,sparePartThree]

		when:
		File csvFile = sparePartService.exporter(DataLocation.findByCode(KIVUYE),spareParts)
		
		then:
		csvFile != null
	}
	
	def "update spare part status"(){
		setup:
		
		setupLocationTree()
		
		def manufactureContact = Initializer.newContact(['en':'Address Descriptions '],"Manufacture","jkl@yahoo.com","0768-889-787","Street 154","6353")
		def supplierContact = Initializer.newContact([:],"Supplier","jk@yahoo.com","0768-888-787","Street 1654","6353")
		def manufacturer = Initializer.newProvider(CODE(111), Type.MANUFACTURER,manufactureContact)
		def supplier = Initializer.newProvider(CODE(222), Type.SUPPLIER,supplierContact)
		def warrantyContact = Initializer.newContact(['fr':'Warranty Address Descriptions One'],"Warranty","jk@yahoo.com","0768-888-787","Street 654","8988")
		def warranty = Initializer.newWarranty(warrantyContact,Initializer.getDate(10, 12, 2010),false,[:])
		
		def user  = newUser("user", "user", true, true)
		setupSecurityManager(user)
		
		def sparePartType = Initializer.newSparePartType(CODE(15810),["en":"Accelerometers"],["en":"used in memms"],"CODE Spare Part",manufacturer,Initializer.now())
		
		def equipment01 = newEquipment("SERIAL01",DataLocation.findByCode(KIVUYE))
		def equipment09 = newEquipment("SERIAL09",DataLocation.findByCode(KIVUYE))
		def equipment10 = newEquipment("SERIAL10",DataLocation.findByCode(KIVUYE))
		
		def sparePart = Initializer.newSparePart("SERIAL10",SparePartPurchasedBy.BYFACILITY,true,Initializer.newPeriod(32),"",['en':'Spare Part Descriptions two'],Initializer.getDate(22,07,2010)
				,Initializer.getDate(10,10,2010),"","sparePartModel",
				DataLocation.findByCode(KIVUYE),sparePartType,supplier,StatusOfSparePart.INSTOCK,
				User.findByUsername("user"),null,null,StockLocation.FACILITY, null)
		
		def statusOne = Initializer.newSparePartStatus(Initializer.now(),User.findByUsername("user"),StatusOfSparePart.INSTOCK,sparePart,[:],null)
		def statusTwo = Initializer.newSparePartStatus(Initializer.now(),User.findByUsername("user"),StatusOfSparePart.OPERATIONAL,sparePart, [:],equipment01)
		
		when:
		sparePart = sparePartService.updateCurrentSparePartStatus(SparePart.findBySerialNumber("SERIAL10"),statusTwo,user,equipment01)

		then:
		SparePart.count() == 1
		SparePart.list()[0].statusOfSparePart == StatusOfSparePart.OPERATIONAL
		
	}

	def "test search SparePart"(){

	}
}
