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
import org.chai.memms.inventory.Provider;
import org.chai.memms.inventory.Provider.Type;
import org.chai.memms.security.User.UserType;
import org.chai.memms.spare.part.SparePart;
import org.chai.memms.spare.part.SparePart.SparePartPurchasedBy;
import org.chai.memms.spare.part.SparePart.StockLocation;
import org.chai.memms.spare.part.SparePartViewController;
import org.chai.memms.inventory.FilterCommand;
import org.chai.memms.security.User;
import org.chai.memms.security.User.UserType
import org.chai.memms.spare.part.SparePart.SparePartStatus;



/**
 * @author Aphrodice Rwagaju
 *
 */
class SparePartViewControllerSpec extends IntegrationTests{
	def sparePartViewController
	
	def "can list spare parts using ajax"(){
		setup:
		setupLocationTree()
		def user = newOtherUser("user", "user", DataLocation.findByCode(KIVUYE))
		user.userType = UserType.TITULAIREHC
		user.save(failOnError:true)
		
		def techDh = newOtherUser("techDh", "techDh", DataLocation.findByCode(BUTARO))
		techDh.userType = UserType.TECHNICIANDH
		techDh.save(failOnError:true)
		def supplierContact = Initializer.newContact([:],"Supplier","jk@yahoo.com","0768-888-787","Street 1654","6353")
		def supplier = Initializer.newProvider(CODE(222), Type.SUPPLIER,supplierContact)
		def manufactureContact = Initializer.newContact(['en':'Address Descriptions '],"Manufacture","jkl@yahoo.com","0768-889-787","Street 154","6353")
		def manufacturer = Initializer.newProvider(CODE(111), Type.MANUFACTURER,manufactureContact)
		def sparePartType = Initializer.newSparePartType(CODE(15810),["en":"testOne names"],["en":"testOne descriptions"],"CODE Spare Part",manufacturer,Initializer.now())

		def sparePartOne = Initializer.newSparePart(SparePartPurchasedBy.BYFACILITY,['en':'SparePart Descriptions one'],Initializer.getDate(22,07,2010),"2900.23","USD",
			DataLocation.findByCode(KIVUYE),sparePartType,supplier,user,StockLocation.FACILITY,SparePartStatus.INSTOCK,32,0)

		def sparePartTwo = Initializer.newSparePart(SparePartPurchasedBy.BYFACILITY,['en':'SparePart Descriptions two'],Initializer.getDate(22,07,2010),"2900.23","EUR",
			DataLocation.findByCode(BUTARO),sparePartType,supplier,user,StockLocation.FACILITY,SparePartStatus.INSTOCK,12,0)
		
		Initializer.newSparePart(SparePartPurchasedBy.BYFACILITY,['en':'SparePart Descriptions two'],Initializer.getDate(22,07,2010),"2900.23","RWF",
			DataLocation.findByCode(MUSANZE),sparePartType,supplier,user,StockLocation.FACILITY,SparePartStatus.INSTOCK,22,0)
		Initializer.newSparePart(SparePartPurchasedBy.BYFACILITY,['en':'SparePart Descriptions two'],Initializer.getDate(22,07,2010),"2900.23","RWF",
			DataLocation.findByCode(GITWE),sparePartType,supplier,user,StockLocation.FACILITY,SparePartStatus.INSTOCK,33,3)
	
		sparePartViewController = new SparePartViewController()
		setupSecurityManager(techDh)
		
		when:
		sparePartViewController.request.makeAjaxRequest()
		sparePartViewController.list()
		
		then:
		SparePart.list().size()==4
		sparePartViewController.response.json.results[0].contains("32")
		sparePartViewController.response.json.results[0].contains("33")
		sparePartViewController.response.json.results[0].contains("12")
		sparePartViewController.response.json.results[0].contains("22")
	}

	def "can list spare parts using ajax with specified type"(){
		setup:
		setupLocationTree()
		def user = newOtherUser("user", "user", DataLocation.findByCode(KIVUYE))
		user.userType = UserType.TITULAIREHC
		user.save(failOnError:true)
		
		def admin = newOtherUserWithType("admin", "admin", DataLocation.findByCode(RWANDA),UserType.ADMIN)

		def supplierContact = Initializer.newContact([:],"Supplier","jk@yahoo.com","0768-888-787","Street 1654","6353")
		def supplier = Initializer.newProvider(CODE(222), Type.SUPPLIER,supplierContact)
		def manufactureContact = Initializer.newContact(['en':'Address Descriptions '],"Manufacture","jkl@yahoo.com","0768-889-787","Street 154","6353")
		def manufacturer = Initializer.newProvider(CODE(111), Type.MANUFACTURER,manufactureContact)
		def sparePartType = Initializer.newSparePartType(CODE(15810),["en":"testOne names"],["en":"testOne descriptions"],"CODE Spare Part",manufacturer,Initializer.now())
		def sparePartTypeTwo = Initializer.newSparePartType(CODE(15819),["en":"testOne names"],["en":"testOne descriptions"],"CODE Spare Part",manufacturer,Initializer.now())
		
		def sparePartOne = Initializer.newSparePart(SparePartPurchasedBy.BYFACILITY,['en':'SparePart Descriptions one'],Initializer.getDate(22,07,2010),"2900.23",,"USD",
			DataLocation.findByCode(KIVUYE),sparePartType,supplier,user,StockLocation.FACILITY,SparePartStatus.INSTOCK,33,1)

		def sparePartTwo = Initializer.newSparePart(SparePartPurchasedBy.BYFACILITY,['en':'SparePart Descriptions two'],Initializer.getDate(22,07,2010),"2900.23","EUR",
			DataLocation.findByCode(BUTARO),sparePartTypeTwo,supplier,user,StockLocation.FACILITY,SparePartStatus.INSTOCK,323,2)
		
		Initializer.newSparePart(SparePartPurchasedBy.BYFACILITY,['en':'SparePart Descriptions two'],Initializer.getDate(22,07,2010),"2900.23","RWF",
			DataLocation.findByCode(MUSANZE),sparePartType,supplier,user,StockLocation.FACILITY,SparePartStatus.INSTOCK,34,3)

		Initializer.newSparePart(SparePartPurchasedBy.BYFACILITY,['en':'SparePart Descriptions two'],Initializer.getDate(22,07,2010),"2900.23","RWF",
			DataLocation.findByCode(GITWE),sparePartType,supplier,user,StockLocation.FACILITY,SparePartStatus.INSTOCK,17,0)
	
		sparePartViewController = new SparePartViewController()
		setupSecurityManager(admin)
		
		when:
		sparePartViewController.params.'type.id' = sparePartType.id
		sparePartViewController.request.makeAjaxRequest()
		sparePartViewController.list()
		
		then:
		SparePart.list().size()==4
		sparePartViewController.response.json.results[0].contains("33")
		sparePartViewController.response.json.results[0].contains("34")
		sparePartViewController.response.json.results[0].contains("17")
		!sparePartViewController.response.json.results[0].contains("323")

	}

	//TODO throwing a json parsing exception
	
	def "can search spare parts "(){
		setup:
		setupLocationTree()
		def user = newOtherUserWithType("sender", "sender", DataLocation.findByCode(KIVUYE),UserType.TITULAIREHC)
		
		def techDh = newOtherUserWithType("techDh", "techDh", DataLocation.findByCode(BUTARO),UserType.TECHNICIANDH)
		
		def supplierContact = Initializer.newContact([:],"Supplier","jk@yahoo.com","0768-888-787","Street 1654","6353")
		def supplier = Initializer.newProvider(CODE(222), Type.SUPPLIER,supplierContact)
		def manufactureContact = Initializer.newContact(['en':'Address Descriptions '],"Manufacture","jkl@yahoo.com","0768-889-787","Street 154","6353")
		def manufacturer = Initializer.newProvider(CODE(111), Type.MANUFACTURER,manufactureContact)
		
		def sparePartType = Initializer.newSparePartType(CODE(15810),["en":"testOne names"],["en":"testOne descriptions"],"CODE Spare Part",manufacturer,Initializer.now())

		def sparePartOne = Initializer.newSparePart(SparePartPurchasedBy.BYFACILITY,['en':'SparePart Descriptions searched item'],Initializer.getDate(22,07,2010),"2900.23","USD",
			DataLocation.findByCode(KIVUYE),sparePartType,supplier,user,StockLocation.FACILITY,SparePartStatus.INSTOCK,33,9)
		def sparePartTwo = Initializer.newSparePart(SparePartPurchasedBy.BYFACILITY,['en':'SparePart Descriptions two'],Initializer.getDate(22,07,2010),"2900.23","EUR",
			DataLocation.findByCode(BUTARO),sparePartType,supplier,user,StockLocation.FACILITY,SparePartStatus.INSTOCK,55,7)
		
		Initializer.newSparePart(SparePartPurchasedBy.BYFACILITY,['en':'SparePart Descriptions two'],Initializer.getDate(22,07,2010),"2900.23","RWF",
			DataLocation.findByCode(MUSANZE),sparePartType,supplier,user,StockLocation.FACILITY,SparePartStatus.INSTOCK,67,0)
		Initializer.newSparePart(SparePartPurchasedBy.BYFACILITY,['en':'SparePart Descriptions two'],Initializer.getDate(22,07,2010),"2900.23","RWF",
			DataLocation.findByCode(GITWE),sparePartType,supplier,user,StockLocation.FACILITY,SparePartStatus.INSTOCK,659,0)
	
		sparePartViewController = new SparePartViewController()
		setupSecurityManager(techDh)

		when:
		sparePartViewController.params.q = "searched item"
		sparePartViewController.request.makeAjaxRequest()
		sparePartViewController.search()
		
		then:
		sparePartViewController.response.json.results[0].contains("33")
		!sparePartViewController.response.json.results[0].contains("55")
		!sparePartViewController.response.json.results[0].contains("67")
		!sparePartViewController.response.json.results[0].contains("659")
	}

	def "can search spare parts with type"(){
		setup:
		setupLocationTree()
		def user = newOtherUserWithType("sender", "sender", DataLocation.findByCode(KIVUYE),UserType.TITULAIREHC)
		
		def techDh = newOtherUserWithType("techDh", "techDh", DataLocation.findByCode(BUTARO),UserType.TECHNICIANDH)
		
		def supplierContact = Initializer.newContact([:],"Supplier","jk@yahoo.com","0768-888-787","Street 1654","6353")
		def supplier = Initializer.newProvider(CODE(222), Type.SUPPLIER,supplierContact)
		def manufactureContact = Initializer.newContact(['en':'Address Descriptions '],"Manufacture","jkl@yahoo.com","0768-889-787","Street 154","6353")
		def manufacturer = Initializer.newProvider(CODE(111), Type.MANUFACTURER,manufactureContact)
		
		def sparePartType = Initializer.newSparePartType(CODE(15810),["en":"testOne names"],["en":"testOne descriptions"],"CODE Spare Part",manufacturer,Initializer.now())

		def sparePartOne = Initializer.newSparePart(SparePartPurchasedBy.BYFACILITY,['en':'SparePart Descriptions one search term'],Initializer.getDate(22,07,2010),"2900.23","USD",
			DataLocation.findByCode(KIVUYE),sparePartType,supplier,user,StockLocation.FACILITY,SparePartStatus.INSTOCK,45,0)
		def sparePartTwo = Initializer.newSparePart(SparePartPurchasedBy.BYFACILITY,['en':'SparePart Descriptions two'],Initializer.getDate(22,07,2010),"2900.23","EUR",
			DataLocation.findByCode(BUTARO),sparePartType,supplier,user,StockLocation.FACILITY,SparePartStatus.INSTOCK,33,3)
		
		def sparePartThree = Initializer.newSparePart(SparePartPurchasedBy.BYFACILITY,['en':'SparePart Descriptions two'],Initializer.getDate(22,07,2010),"2900.23","RWF",
			DataLocation.findByCode(MUSANZE),sparePartType,supplier,user,StockLocation.FACILITY,SparePartStatus.INSTOCK,2233,0)
		def sparePartFour = Initializer.newSparePart(SparePartPurchasedBy.BYFACILITY,['en':'SparePart Descriptions two'],Initializer.getDate(22,07,2010),"2900.23","RWF",
			DataLocation.findByCode(GITWE),sparePartType,supplier,user,StockLocation.FACILITY,SparePartStatus.INSTOCK,29432,0)
	
		sparePartViewController = new SparePartViewController()
		setupSecurityManager(techDh)

		when:
		sparePartViewController.params.'type.id' = sparePartType.id
		sparePartViewController.params.q = "search term"
		sparePartViewController.request.makeAjaxRequest()
		sparePartViewController.search()
		
		then:
		SparePart.list().size()==4
		sparePartViewController.response.json.results[0].contains(sparePartOne.id+"")
		!sparePartViewController.response.json.results[0].contains(sparePartTwo.id+"")
		!sparePartViewController.response.json.results[0].contains(sparePartThree.id+"")
		!sparePartViewController.response.json.results[0].contains(sparePartFour.id+"")

	}
}
