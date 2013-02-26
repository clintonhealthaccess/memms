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
import org.chai.memms.spare.part.SparePart.SparePartPurchasedBy;
import org.chai.memms.spare.part.SparePartStatus.StatusOfSparePart;
import org.chai.memms.inventory.FilterCommand;
import org.chai.memms.security.User;
import org.chai.memms.security.User.UserType


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
		
		def sparePartOne = Initializer.newSparePart(CODE(123),SparePartPurchasedBy.BYFACILITY,false,Initializer.newPeriod(32),"2900.23",['en':'SparePart Descriptions one'],Initializer.getDate(22,07,2010)
				,Initializer.getDate(10,10,2010),"USD","sparePartModel",DataLocation.findByCode(KIVUYE),sparePartType,supplier,StatusOfSparePart.INSTOCK,user,null,null)
		def sparePartTwo = Initializer.newSparePart(CODE(124),SparePartPurchasedBy.BYFACILITY,true,Initializer.newPeriod(32),"2900.23",['en':'SparePart Descriptions two'],Initializer.getDate(22,07,2010)
				,Initializer.getDate(10,10,2010),"EUR","sparePartModel",DataLocation.findByCode(BUTARO),sparePartType,supplier,StatusOfSparePart.INSTOCK,user,null,null)
		
		Initializer.newSparePart(CODE(125),SparePartPurchasedBy.BYFACILITY,true,Initializer.newPeriod(32),"2900.23",['en':'SparePart Descriptions two'],Initializer.getDate(22,07,2010)
			,Initializer.getDate(10,10,2010),"RWF","sparePartModel",DataLocation.findByCode(MUSANZE),sparePartType,supplier,StatusOfSparePart.INSTOCK,user,null,null)
		Initializer.newSparePart(CODE(126),SparePartPurchasedBy.BYFACILITY,true,Initializer.newPeriod(32),"2900.23",['en':'SparePart Descriptions two'],Initializer.getDate(22,07,2010)
			,Initializer.getDate(10,10,2010),"RWF","sparePartModel",DataLocation.findByCode(GITWE),sparePartType,supplier,StatusOfSparePart.INSTOCK,user,null,null)
	
		sparePartViewController = new SparePartViewController()
		setupSecurityManager(techDh)
		
		when:
		sparePartViewController.request.makeAjaxRequest()
		sparePartViewController.list()
		
		then:
		sparePartViewController.response.json.results[0].contains(SparePart.findBySerialNumber(CODE(123)).code)
		sparePartViewController.response.json.results[0].contains(SparePart.findBySerialNumber(CODE(124)).code)
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

		def sparePartOne = Initializer.newSparePart(CODE(123),SparePartPurchasedBy.BYFACILITY,false,Initializer.newPeriod(32),"2900.23",['en':'SparePart Descriptions one'],Initializer.getDate(22,07,2010)
				,Initializer.getDate(10,10,2010),"USD","sparePartModel",DataLocation.findByCode(KIVUYE),sparePartType,supplier,StatusOfSparePart.INSTOCK,user,null,null)
		def sparePartTwo = Initializer.newSparePart(CODE(124),SparePartPurchasedBy.BYFACILITY,true,Initializer.newPeriod(32),"2900.23",['en':'SparePart Descriptions two'],Initializer.getDate(22,07,2010)
				,Initializer.getDate(10,10,2010),"EUR","sparePartModel",DataLocation.findByCode(BUTARO),sparePartType,supplier,StatusOfSparePart.INSTOCK,user,null,null)
		
		Initializer.newSparePart(CODE(125),SparePartPurchasedBy.BYFACILITY,true,Initializer.newPeriod(32),"2900.23",['en':'SparePart Descriptions two'],Initializer.getDate(22,07,2010)
			,Initializer.getDate(10,10,2010),"RWF","sparePartModel",DataLocation.findByCode(MUSANZE),sparePartType,supplier,StatusOfSparePart.INSTOCK,user,null,null)
		Initializer.newSparePart(CODE(126),SparePartPurchasedBy.BYFACILITY,true,Initializer.newPeriod(32),"2900.23",['en':'SparePart Descriptions two'],Initializer.getDate(22,07,2010)
			,Initializer.getDate(10,10,2010),"RWF","sparePartModel",DataLocation.findByCode(GITWE),sparePartType,supplier,StatusOfSparePart.INSTOCK,user,null,null)
	
		sparePartViewController = new SparePartViewController()
		setupSecurityManager(techDh)

		when:
		sparePartViewController.params.q = SparePart.findBySerialNumber(CODE(123)).code
		sparePartViewController.request.makeAjaxRequest()
		sparePartViewController.search()
		
		then:
		sparePartViewController.response.json.results[0].contains(SparePart.findBySerialNumber(CODE(123)).code)
		!sparePartViewController.response.json.results[0].contains(SparePart.findBySerialNumber(CODE(124)).code)
		!sparePartViewController.response.json.results[0].contains(SparePart.findBySerialNumber(CODE(125)).code)
		!sparePartViewController.response.json.results[0].contains(SparePart.findBySerialNumber(CODE(126)).code)
	}
	
}
