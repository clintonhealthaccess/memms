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
import org.chai.location.Location;
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
import org.chai.memms.spare.part.SparePart.SparePartStatus;

/**
 * @author Aphrodice Rwagaju
 *
 */
class SparePartServiceSpec extends IntegrationTests{
	def sparePartService

	def "can search spare part by description"() {
		setup:
		setupLocationTree()
		
		def sparePartStatus
		def user = newOtherUser("user", "user", DataLocation.findByCode(KIVUYE))

		def manufactureContact = Initializer.newContact(['en':'Address Descriptions '],"Manufacture","jkl@yahoo.com","0768-889-787","Street 154","6353")
		def supplierContact = Initializer.newContact([:],"Supplier","jk@yahoo.com","0768-888-787","Street 1654","6353")

		def manufacturer = Initializer.newProvider(CODE(123), Type.MANUFACTURER,manufactureContact)
		def supplier = Initializer.newProvider(CODE(124), Type.SUPPLIER,supplierContact)
		def userHc = newOtherUserWithType("userHc", "userHc", DataLocation .findByCode(KIVUYE), UserType .TITULAIREHC)
		def techDH = newOtherUserWithType("techDH", "techDH", DataLocation.findByCode(BUTARO), UserType.TECHNICIANDH)
		//def techMMC = newOtherUserWithType("techMMC", "techMMC", DataLocation.findByCode(RWANDA), UserType.TECHNICIANMMC)

		def sparePartType = Initializer.newSparePartType(CODE(15810),["en":"Accelerometers"],["en":"used in memms"],"CODE Spare Part",manufacturer,Initializer.now())
		Initializer.newSparePart(SparePartPurchasedBy.BYMOH,['en':'Spare Part Descriptions one'],Initializer.getDate(22,07,2010),"","",DataLocation.findByCode(BUTARO),sparePartType,
			supplier,user,StockLocation.FACILITY,SparePartStatus.INSTOCK,23,22,0)
		def sparePartCodeToFind = Initializer.newSparePart( SparePartPurchasedBy.BYMOH,['en':'Spare Part Descriptions two'],Initializer.getDate(22,07,2010),"2900.23","RWF",DataLocation.findByCode(KIVUYE),
			sparePartType,supplier,user,StockLocation.FACILITY,SparePartStatus.INSTOCK,34,32,0)
		
		List<SparePart> spareParts

		when: "user can view all those he manages"
		spareParts = sparePartService.searchSparePart("Descriptions",techDH,null,[:])
		                                              

		then:
		spareParts.size() == 2
		spareParts[0] != spareParts[1]

		when: "user cannot see those he doesn't manage"

		spareParts = sparePartService.searchSparePart("Descriptions",userHc,null,[:])
		then:
		spareParts.size() == 1

		when: "Searching by description"

		spareParts = sparePartService.searchSparePart("one",techDH,null,[:])
		then:
		spareParts.size() == 1
		spareParts[0].getDescriptions(new Locale("en")).equals('Spare Part Descriptions one')

	}

	def "can search spare part by  description for specified type"() {
		setup:
		setupLocationTree()
		
		def sparePartStatus
		def user = newOtherUser("user", "user", DataLocation.findByCode(KIVUYE))
		def equipment01 = newEquipment("SERIAL01",DataLocation.findByCode(KIVUYE))
		def equipment02 = newEquipment("SERIAL031",DataLocation.findByCode(BUTARO))
		def manufactureContact = Initializer.newContact(['en':'Address Descriptions '],"Manufacture","jkl@yahoo.com","0768-889-787","Street 154","6353")
		def supplierContact = Initializer.newContact([:],"Supplier","jk@yahoo.com","0768-888-787","Street 1654","6353")

		def manufacturer = Initializer.newProvider(CODE(123), Type.MANUFACTURER,manufactureContact)
		def supplier = Initializer.newProvider(CODE(124), Type.SUPPLIER,supplierContact)
		def supplierTwo = Initializer.newProvider(CODE(125), Type.SUPPLIER,supplierContact)
		def userHc = newOtherUserWithType("userHc", "userHc", DataLocation .findByCode(KIVUYE), UserType .TITULAIREHC)
		def techDH = newOtherUserWithType("techDH", "techDH", DataLocation.findByCode(BUTARO), UserType.TECHNICIANDH)
		//def techMMC = newOtherUserWithType("techMMC", "techMMC", DataLocation.findByCode(RWANDA), UserType.TECHNICIANMMC)

		def sparePartType = Initializer.newSparePartType(CODE(15810),["en":"Accelerometers"],["en":"used in memms"],"CODE Spare Part",manufacturer,Initializer.now())
		def sparePartTypeTwo = Initializer.newSparePartType(CODE(15819),["en":"Accelerometers deux"],["en":"used in memms"],"CODE Spare Part",manufacturer,Initializer.now())

		Initializer.newSparePart(SparePartPurchasedBy.BYMOH,['en':'Spare Part Descriptions one'],Initializer.getDate(22,07,2010),"","",DataLocation.findByCode(BUTARO),sparePartType,
			supplier,user,StockLocation.FACILITY,SparePartStatus.INSTOCK,2,2,2)
		def sparePartCodeToFind = Initializer.newSparePart(SparePartPurchasedBy.BYMOH,['en':'Spare Part Descriptions two'],Initializer.getDate(22,07,2010),"2900.23","RWF",DataLocation.findByCode(KIVUYE),sparePartTypeTwo,
			supplierTwo,user,StockLocation.FACILITY,SparePartStatus.INSTOCK,54,53,34)
		
		List<SparePart> spareParts

		when: "user can view all those he manages"
		spareParts = sparePartService.searchSparePart("Descriptions",techDH,sparePartTypeTwo,[:])
		                                              

		then:
		spareParts.size() == 1

		when: "user cannot see those he doesn't manage"

		spareParts = sparePartService.searchSparePart("Descriptions",userHc,sparePartTypeTwo,[:])
		then:
		spareParts.size() == 1

		when: "Searching by description"

		spareParts = sparePartService.searchSparePart("two",techDH,sparePartTypeTwo,[:])
		then:
		spareParts.size() == 1
		spareParts[0].getDescriptions(new Locale("en")).equals('Spare Part Descriptions two')

		when: "Searching by spare part type name"
		spareParts = sparePartService.searchSparePart("Accelerometers",techDH,sparePartTypeTwo,[:])
		then:
		spareParts.size() == 1

	}
	def "user can only view his spare parts, if a technician at dh they can also view for the locations they manage"() {
		setup:
		setupLocationTree()
		
		def statusOfSparePart
		def sparepartType
		
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
		
		Initializer.newSparePart(SparePartPurchasedBy.BYMOH,['en':'Spare Part Descriptions one'],Initializer.getDate(22,07,2010),"","",DataLocation.findByCode(BUTARO),sparePartType,
			supplier,user,StockLocation.FACILITY,SparePartStatus.INSTOCK,98,97,9)
		def sparePartCodeToFind = Initializer.newSparePart(SparePartPurchasedBy.BYMOH,['en':'Spare Part Descriptions two'],Initializer.getDate(22,07,2010),"2900.23","RWF",DataLocation.findByCode(KIVUYE),sparePartType,
			supplier,user,StockLocation.FACILITY,SparePartStatus.INSTOCK,45,44,0)


		def sparePartsTech, sparePartsUser
		when:
		sparePartsTech = sparePartService.getSpareParts(techDh,null,[:])
		sparePartsUser = sparePartService.getSpareParts(user,null,[:])
		then:
		sparePartsTech.size() == 2
		sparePartsUser.size() == 1
	}

	def "user can only view his spare parts, if a technician at dh they can also view for the locations they manage with provided type/status"() {
		setup:
		setupLocationTree()
		
		def statusOfSparePart
		def sparepartType
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

		Initializer.newSparePart(SparePartPurchasedBy.BYMOH,['en':'Spare Part Descriptions one'],Initializer.getDate(22,07,2010),"","",DataLocation.findByCode(BUTARO),sparePartType,
			supplier,user, StockLocation.FACILITY,SparePartStatus.INSTOCK,23,21,0)
		def sparePartCodeToFind = Initializer.newSparePart(SparePartPurchasedBy.BYMOH,['en':'Spare Part Descriptions two'],Initializer.getDate(22,07,2010),"2900.23","RWF",DataLocation.findByCode(KIVUYE),sparePartType,
			supplier,user, StockLocation.FACILITY,SparePartStatus.INSTOCK,11,11,0)


		def sparePartsTechs, sparePartsUsers
		when:
		sparePartsTechs = sparePartService.getSpareParts(techDh,sparePartType,[:])
		sparePartsUsers = sparePartService.getSpareParts(user,sparePartType,[:])
		then:
		sparePartsTechs.size() == 2
		sparePartsTechs[1].receivedQuantity==21

		sparePartsUsers.size() == 1
		sparePartsUsers[0].receivedQuantity==11

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
		def user  = newUser("admin", "Admin UID")

		user.location = Location.findByCode(RWANDA)
		user.save()

		def sparePartType = Initializer.newSparePartType(CODE(15810),["en":"Accelerometers"],["en":"used in memms"],"CODE Spare Part",manufacturer,Initializer.now())
		
		def sparePartOne = Initializer.newSparePart(SparePartPurchasedBy.BYMOH,['en':'Spare Part Descriptions one'],Initializer.getDate(22,07,2010),"","",kivuyeHC,sparePartType,
			supplier,user,StockLocation.FACILITY,SparePartStatus.INSTOCK,8,8,0)
		def sparePartTwo = Initializer.newSparePart(SparePartPurchasedBy.BYFACILITY,['en':'Spare Part Descriptions two'],Initializer.getDate(22,07,2010),"2900.23","USD",butaroDH,sparePartType,
			supplier,user,StockLocation.FACILITY,SparePartStatus.INSTOCK,23,18,3)

		def spareParts
		when://Filter by defaults
		spareParts = sparePartService.filterSparePart(user,supplier,sparePartType,StockLocation.FACILITY,SparePartPurchasedBy.NONE,SparePartStatus.INSTOCK,[:])

		then://This should bring 2 records as kivuye HD is managed by butaro HD
		SparePart.count() == 2
		spareParts.size() == 2
		spareParts[0].getDescriptions(new Locale("en")).equals('Spare Part Descriptions two')
		spareParts[0].sparePartPurchasedBy == SparePartPurchasedBy.BYFACILITY
		spareParts[0].receivedQuantity==18
		spareParts[1].receivedQuantity==8
		spareParts[1].getDescriptions(new Locale("en")).equals('Spare Part Descriptions one')
		spareParts[1].sparePartPurchasedBy == SparePartPurchasedBy.BYMOH

		when://Search by SparePartPurchasedBy

		user.location = kivuyeHC
		user.save()

		spareParts = sparePartService.filterSparePart(user,supplier,sparePartType,StockLocation.FACILITY,SparePartPurchasedBy.NONE,SparePartStatus.INSTOCK,[:])		
		then:
		SparePart.count() == 2
		spareParts.size() == 1
		spareParts[0].getDescriptions(new Locale("en")).equals('Spare Part Descriptions one')
		spareParts[0].sparePartPurchasedBy == SparePartPurchasedBy.BYMOH
		spareParts[0].receivedQuantity==8
	}
	
	def "can export spare parts"(){
		setup:
		setupLocationTree()
		

		def manufactureContact = Initializer.newContact(['en':'Address Descriptions '],"Manufacture","jkl@yahoo.com","0768-889-787","Street 154","6353")
		def supplierContact = Initializer.newContact([:],"Supplier","jk@yahoo.com","0768-888-787","Street 1654","6353")
		def manufacturer = Initializer.newProvider(CODE(111), Type.MANUFACTURER,manufactureContact)
		def supplier = Initializer.newProvider(CODE(222), Type.SUPPLIER,supplierContact)
		
		def user  = newUser("user", "user", true, true)
		setupSecurityManager(user)
		
		def sparePartType = Initializer.newSparePartType(CODE(15810),["en":"Accelerometers"],["en":"used in memms"],"CODE Spare Part",manufacturer,Initializer.now())
		
		def sparePartOne = Initializer.newSparePart(SparePartPurchasedBy.BYFACILITY,['en':'Spare Part Descriptions two'],Initializer.getDate(22,07,2010),"","",DataLocation.findByCode(KIVUYE),sparePartType,
			supplier,user,StockLocation.FACILITY,SparePartStatus.INSTOCK,12,12,0)
		def sparePartTwo = Initializer.newSparePart(SparePartPurchasedBy.BYFACILITY,['en':'Spare Part Descriptions two'],Initializer.getDate(22,07,2010),"2900.23","EUR",DataLocation.findByCode(KIVUYE),sparePartType,
			supplier,user,StockLocation.FACILITY,SparePartStatus.INSTOCK,4,4,0)
		def sparePartThree = Initializer.newSparePart(SparePartPurchasedBy.BYFACILITY,['en':'Spare Part Descriptions two'],Initializer.getDate(22,07,2010),"2900.23","EUR",DataLocation.findByCode(KIVUYE),sparePartType,
			supplier,user,StockLocation.FACILITY,SparePartStatus.INSTOCK,45,45,0)
		def sparePartFour = Initializer.newSparePart(SparePartPurchasedBy.BYFACILITY,['en':'Spare Part Descriptions two'],Initializer.getDate(22,07,2010),"2900.23","EUR",DataLocation.findByCode(BUTARO),sparePartType,
			supplier,user,StockLocation.FACILITY,SparePartStatus.INSTOCK,25,23,10)
		List<SparePart> spareParts = [sparePartOne,sparePartTwo,sparePartThree]

		when:
		File csvFile = sparePartService.exporter(DataLocation.findByCode(KIVUYE),spareParts)
		
		then:
		csvFile != null
	}

}
