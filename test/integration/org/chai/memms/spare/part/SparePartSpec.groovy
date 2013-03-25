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

import org.chai.memms.Initializer;
import org.chai.memms.IntegrationTests;
import org.chai.memms.util.Utils;
import org.chai.memms.inventory.Provider;
import org.chai.memms.inventory.Provider.Type;
import org.chai.memms.spare.part.SparePart.StockLocation;
import org.chai.memms.spare.part.SparePart.SparePartPurchasedBy;
import org.chai.memms.spare.part.SparePart.StockLocation;
import org.chai.memms.spare.part.SparePartStatus.StatusOfSparePart;
import org.chai.memms.spare.part.SparePart;
import org.chai.memms.spare.part.SparePartType;
import org.chai.memms.security.User;
import org.chai.location.DataLocation;
import org.chai.memms.inventory.Equipment;


/**
 * @author Aphrodice Rwagaju
 *
 */
class SparePartSpec extends IntegrationTests {
	def "can create and save the spare part"() {

		setup:
		setupLocationTree()
		setupSystemUser()
		def manufactureContact = Initializer.newContact(['en':'Address Descriptions '],"Manufacture","jkl@yahoo.com","0768-889-787","Street 154","6353")
		def manufacturer = Initializer.newProvider(CODE(111), Type.MANUFACTURER,manufactureContact)
		
		
		def sparePartType = Initializer.newSparePartType(CODE(15810),["en":"testOne names"],["en":"testOne descriptions"],"CODE Spare Part",manufacturer,Initializer.now())
		def equipment01 = newEquipment("SERIAL01",DataLocation.findByCode(KIVUYE))
		if(log.isDebugEnabled()) log.debug("Spare Part Type Created:" + sparePartType)
		when:
		def sparePart = new SparePart(serialNumber:"test123",sparePartPurchasedBy:SparePartPurchasedBy.BYMOH,sameAsManufacturer:false,expectedLifeTime:Initializer.newPeriod(20),
				descriptions:['en':'Spare Part Descriptions'],manufactureDate:Initializer.getDate(22,07,2010),purchaseDate:Initializer.getDate(22,07,2010),model:"sparePartModel",
				type:sparePartType,statusOfSparePart:StatusOfSparePart.INSTOCK,dateCreated:Initializer.getDate(23,07,2010), addedBy: User.findByUsername("systemUser"),stockLocation:StockLocation.MMC, usedOnEquipment:equipment01)

		def supplierContact = Initializer.newContact([:],"Supplier","jk@yahoo.com","0768-888-787","Street 1654","6353")
		def supplier = Initializer.newProvider(CODE(124), Type.SUPPLIER,supplierContact)
		def contact = Initializer.newContact([:],"Contact","jk@yahoo.com","0768-888-787","Street 654","6353")
		def warranty = Initializer.newWarranty(['en':'warranty'],'warranty name','email@gmail.com',"0768-889-787","Street 154","6353",Initializer.getDate(10, 12, 2010),false,[:])
		
		sparePart.supplier=supplier
		sparePart.warranty = warranty
		sparePart.warrantyPeriod = Initializer.newPeriod(4)
		sparePart.save(failOnError: true)
		
		then:
		SparePart.count() == 1
	}
	def "can get current spare part status based on time"(){
		setup:
		setupLocationTree()
		setupSystemUser()
		def manufactureContact = Initializer.newContact(['en':'Address Descriptions '],"Manufacture","jkl@yahoo.com","0768-889-787","Street 154","6353")
		def manufacturer = Initializer.newProvider(CODE(111), Type.MANUFACTURER,manufactureContact)
		
		def sparePartType = Initializer.newSparePartType(CODE(15810),["en":"testOne names"],["en":"testOne descriptions"],"CODE Spare Part",manufacturer,Initializer.now())
		def equipment01 = newEquipment("SERIAL01",DataLocation.findByCode(KIVUYE))
		def user  = newUser("admin", "Admin UID")
		def sparePart = new SparePart(serialNumber:"test123",sparePartPurchasedBy:SparePartPurchasedBy.BYMOH,sameAsManufacturer:false,expectedLifeTime:Initializer.newPeriod(20),
				descriptions:['en':'SparePart Descriptions'],manufactureDate:Initializer.getDate(22,07,2010),purchaseDate:Initializer.getDate(22,07,2010),model:"sparePartModel",
				type:sparePartType,statusOfSparePart:StatusOfSparePart.INSTOCK,dateCreated:Initializer.getDate(23,07,2010), addedBy: User.findByUsername("systemUser"), stockLocation: StockLocation.MMC, usedOnEquipment:equipment01)
		
		def supplierContact = Initializer.newContact([:],"Supplier","jk@yahoo.com","0768-888-787","Street 1654","6353")
		def supplier = Initializer.newProvider(CODE(124), Type.SUPPLIER,supplierContact)
		def contact = Initializer.newContact([:],"Contact","jk@yahoo.com","0768-888-787","Street 654","6353")
		def warranty = Initializer.newWarranty(['en':'warranty'],'warranty name','email@gmail.com',"0768-889-787","Street 154","6353",Initializer.getDate(10, 12, 2010),false,[:])
		
		sparePart.supplier=supplier
		sparePart.warranty = warranty
		sparePart.warrantyPeriod = Initializer.newPeriod(4)
		sparePart.save(failOnError: true)
		
		when:
		def statusThree= Initializer.newSparePartStatus(Initializer.getDate(10, 12, 2010),User.findByUsername("admin"),StatusOfSparePart.INSTOCK,sparePart,[:],null)
		def statusTwo= Initializer.newSparePartStatus(Initializer.getDate(10, 12, 2011),User.findByUsername("admin"),StatusOfSparePart.OPERATIONAL,sparePart,[:],equipment01)
		def statusOne= Initializer.newSparePartStatus(Initializer.getDate(10, 07, 2012),User.findByUsername("admin"),StatusOfSparePart.DISPOSED,sparePart,[:],null)
		
		then:
		SparePart.count() == 1
		SparePart.list()[0].timeBasedStatus.statusOfSparePart==statusOne.statusOfSparePart
	}
	def "can get current spare part status"(){
		setup:
		setupLocationTree()
		setupSystemUser()
		def manufactureContact = Initializer.newContact(['en':'Address Descriptions '],"Manufacture","jkl@yahoo.com","0768-889-787","Street 154","6353")
		def manufacturer = Initializer.newProvider(CODE(111), Type.MANUFACTURER,manufactureContact)
		
		def sparePartType = Initializer.newSparePartType(CODE(15810),["en":"testOne names"],["en":"testOne descriptions"],"CODE Spare Part",manufacturer,Initializer.now())
		def equipment01 = newEquipment("SERIAL01",DataLocation.findByCode(KIVUYE))
		def user  = newUser("admin", "Admin UID")
		def sparePart = new SparePart(serialNumber:"test123",sparePartPurchasedBy:SparePartPurchasedBy.BYMOH,sameAsManufacturer:false,expectedLifeTime:Initializer.newPeriod(20),
				descriptions:['en':'SparePart Descriptions'],manufactureDate:Initializer.getDate(22,07,2010),purchaseDate:Initializer.getDate(22,07,2010),model:"sparePartModel",
				type:sparePartType,statusOfSparePart:StatusOfSparePart.INSTOCK,dateCreated:Initializer.getDate(23,07,2010), addedBy: User.findByUsername("systemUser"),stockLocation:StockLocation.MMC, usedOnEquipment:equipment01)
		
		def supplierContact = Initializer.newContact([:],"Supplier","jk@yahoo.com","0768-888-787","Street 1654","6353")
		def supplier = Initializer.newProvider(CODE(124), Type.SUPPLIER,supplierContact)
		def contact = Initializer.newContact([:],"Contact","jk@yahoo.com","0768-888-787","Street 654","6353")
		def warranty = Initializer.newWarranty(['en':'warranty'],'warranty name','email@gmail.com',"0768-889-787","Street 154","6353",Initializer.getDate(10, 12, 2010),false,[:])
		
		sparePart.supplier=supplier
		sparePart.warranty = warranty
		sparePart.warrantyPeriod = Initializer.newPeriod(4)
		sparePart.save(failOnError: true)
		
		when:
		def statusThree= Initializer.newSparePartStatus(Initializer.getDate(10, 12, 2010),User.findByUsername("admin"),StatusOfSparePart.INSTOCK,sparePart,[:],null)
		def statusTwo= Initializer.newSparePartStatus(Initializer.getDate(10, 12, 2011),User.findByUsername("admin"),StatusOfSparePart.OPERATIONAL,sparePart,[:],equipment01)
		def statusOne= Initializer.newSparePartStatus(Initializer.getDate(10, 07, 2012),User.findByUsername("admin"),StatusOfSparePart.DISPOSED,sparePart,[:],null)
		then:
		SparePart.count() == 1
		SparePart.list()[0].timeBasedStatus.statusOfSparePart==statusOne.statusOfSparePart
		
		
		when:
		def statusSix= Initializer.newSparePartStatus(Initializer.getDate(10, 12, 2010),User.findByUsername("admin"),StatusOfSparePart.INSTOCK,sparePart,[:],null)
		def statusFive= Initializer.newSparePartStatus(Initializer.getDate(10, 12, 2011),User.findByUsername("admin"),StatusOfSparePart.OPERATIONAL,sparePart,[:],equipment01)
		def statusFour= Initializer.newSparePartStatus(Initializer.getDate(10, 07, 2012),User.findByUsername("admin"),StatusOfSparePart.DISPOSED,sparePart,[:],null)
		then:
		SparePart.count() == 1
		SparePart.list()[0].timeBasedStatus.is(statusFour)
		
		when:
		def statusInStock= Initializer.newSparePartStatus(Initializer.getDate(10, 12, 2010),User.findByUsername("admin"),StatusOfSparePart.INSTOCK,sparePart,[:],null)
		def statusOperational= Initializer.newSparePartStatus(Initializer.getDate(10, 12, 2011),User.findByUsername("admin"),StatusOfSparePart.OPERATIONAL,sparePart,[:],equipment01)
		def statusDisposed= Initializer.newSparePartStatus(Initializer.getDate(10, 07, 2012),User.findByUsername("admin"),StatusOfSparePart.DISPOSED,sparePart,[:],null)
		then:
		SparePart.count() == 1
		SparePart.list()[0].timeBasedStatus.is(statusDisposed)
	}
	
	def "can't create and save a spare part with a duplicate serial number"() {
		
				setup:
				setupLocationTree()
				setupSystemUser()
				def manufactureContact = Initializer.newContact(['en':'Address Descriptions '],"Manufacture","jkl@yahoo.com","0768-889-787","Street 154","6353")
				def manufacturer = Initializer.newProvider(CODE(111), Type.MANUFACTURER,manufactureContact)
				
				def sparePartType = Initializer.newSparePartType(CODE(15810),["en":"testOne names"],["en":"testOne descriptions"],"CODE Spare Part",manufacturer,Initializer.now())
				def equipment01 = newEquipment("SERIAL01",DataLocation.findByCode(KIVUYE))
				
				def supplierContact = Initializer.newContact([:],"Supplier","jk@yahoo.com","0768-888-787","Street 1654","6353")
				
				def supplier = Initializer.newProvider(CODE(124), Type.SUPPLIER,supplierContact)
				Initializer.newSparePart("test123",SparePartPurchasedBy.BYFACILITY,false,Initializer.newPeriod(30),"16677",['en':'Spare Part Descriptions one'],Initializer.getDate(22,07,2010),Initializer.getDate(22,07,2011),"RWF",'sparePartModel',
					DataLocation.list().first(),sparePartType,supplier,StatusOfSparePart.INSTOCK,User.findByUsername("systemUser"),null, null, StockLocation.FACILITY, equipment01)
					
				when:
				def sparePart = new SparePart(serialNumber:"test123",sparePartPurchasedBy:SparePartPurchasedBy.BYMOH,sameAsManufacturer:false,expectedLifeTime:Initializer.newPeriod(20),
						descriptions:['en':'SparePart Descriptions'],manufactureDate:Initializer.getDate(22,07,2010),purchaseDate:Initializer.getDate(22,07,2010),model:"sparePartModel",
						type:sparePartType,statusOfSparePart:StatusOfSparePart.OPERATIONAL,dateCreated:Initializer.getDate(23,07,2010), addedBy: User.findByUsername("systemUser"),stockLocation:StockLocation.MMC, usedOnEquipment:equipment01)
				def contact = Initializer.newContact([:],"Contact","jk@yahoo.com","0768-888-787","Street 654","6353")
				def warranty = Initializer.newWarranty(['en':'warranty'],'warranty name','email@gmail.com',"0768-889-787","Street 154","6353",Initializer.getDate(10, 12, 2010),false,[:])
		
				sparePart.supplier=supplier
				sparePart.warranty=warranty
				sparePart.warrantyPeriod = Initializer.newPeriod(21)
		
				sparePart.save()
				then:
				SparePart.count() == 1
				sparePart.errors.hasFieldErrors('serialNumber') == true
			}
	def "can't create and save an spare part with a duplicate code"() {
		
				setup:
				setupLocationTree()
				setupSystemUser()
				def manufactureContact = Initializer.newContact(['en':'Address Descriptions '],"Manufacture","jkl@yahoo.com","0768-889-787","Street 154","6353")
				def manufacturer = Initializer.newProvider(CODE(111), Type.MANUFACTURER,manufactureContact)
				
				def sparePartType = Initializer.newSparePartType(CODE(15810),["en":"testOne names"],["en":"testOne descriptions"],"CODE Spare Part",manufacturer,Initializer.now())
				def equipment01 = newEquipment("SERIAL01",DataLocation.findByCode(KIVUYE))
				def supplierContact = Initializer.newContact([:],"Supplier","jk@yahoo.com","0768-888-787","Street 1654","6353")
				def supplier = Initializer.newProvider(CODE(124), Type.SUPPLIER,supplierContact)
				def contact = Initializer.newContact([:],"Contact","jk@yahoo.com","0768-888-787","Street 654","6353")
				def warranty = Initializer.newWarranty(['en':'warranty'],'warranty name','email@gmail.com',"0768-889-787","Street 154","6353",Initializer.getDate(10, 12, 2010),false,[:])
		
				def sparePartOne = Initializer.newSparePart("test123",SparePartPurchasedBy.BYFACILITY,false,Initializer.newPeriod(30),"16677",['en':"testDescription"],Initializer.getDate(22,07,2010), Initializer.getDate(22,07,2010),"RWF",
						"sparePartModel",DataLocation.list().first(),sparePartType,supplier,StatusOfSparePart.INSTOCK,
						User.findByUsername("systemUser"),null,null, StockLocation.FACILITY, equipment01)
					
				when:
				
				def sparePart = new SparePart(serialNumber:"test124",sparePartPurchasedBy:SparePartPurchasedBy.BYMOH,sameAsManufacturer:false,expectedLifeTime:Initializer.newPeriod(20),
					descriptions:['en':'Spare Part Descriptions'],manufactureDate:Initializer.getDate(22,07,2010),purchaseDate:Initializer.getDate(22,07,2010),model:"sparePartModel",
					type:sparePartType,code:sparePartOne.code,statusOfSparePart:StatusOfSparePart.OPERATIONAL,dateCreated:Initializer.getDate(23,07,2010), addedBy: User.findByUsername("systemUser"))
				sparePart.supplier=supplier
				sparePart.warranty=warranty
				sparePart.warrantyPeriod = Initializer.newPeriod(21)
		
				sparePart.save()
				then:
				SparePart.count() == 1
				sparePart.errors.hasFieldErrors('code') == true
			}
	def "can set current spare part status based on time"(){
		setup:
		setupLocationTree()
		setupSystemUser()
		def manufactureContact = Initializer.newContact(['en':'Address Descriptions '],"Manufacture","jkl@yahoo.com","0768-889-787","Street 154","6353")
		def manufacturer = Initializer.newProvider(CODE(111), Type.MANUFACTURER,manufactureContact)
		
		def sparePartType = Initializer.newSparePartType(CODE(15810),["en":"testOne names"],["en":"testOne descriptions"],"CODE Spare Part",manufacturer,Initializer.now())
		def equipment01 = newEquipment("SERIAL01",DataLocation.findByCode(KIVUYE))
		def user  = newUser("admin", "Admin UID")
		
		def sparePart = new SparePart(serialNumber:"test123",sparePartPurchasedBy:SparePartPurchasedBy.BYFACILITY,sameAsManufacturer:false,expectedLifeTime:Initializer.newPeriod(20),
			descriptions:['en':'SparePart Descriptions'],manufactureDate:Initializer.getDate(22,07,2010),purchaseDate:Initializer.getDate(22,07,2010),model:"sparePartModel",dataLocation:DataLocation.list().first(),
			type:sparePartType,statusOfSparePart:StatusOfSparePart.INSTOCK,dateCreated:Initializer.getDate(23,07,2010), addedBy: User.findByUsername("systemUser"),stockLocation:StockLocation.FACILITY,usedOnEquipment:equipment01)

			def supplierContact = Initializer.newContact([:],"Supplier","jk@yahoo.com","0768-888-787","Street 1654","6353")
			def supplier = Initializer.newProvider(CODE(124), Type.SUPPLIER,supplierContact)
			def contact = Initializer.newContact([:],"Contact","jk@yahoo.com","0768-888-787","Street 654","6353")
			def warranty = Initializer.newWarranty(['en':'warranty'],'warranty name','email@gmail.com',"0768-889-787","Street 154","6353",Initializer.getDate(10, 12, 2010),false,[:])
		
		sparePart.supplier=supplier
		sparePart.warranty=warranty
		sparePart.warrantyPeriod = Initializer.newPeriod(3)
		sparePart.save(failOnError: true)
		when:
		def statusThree= Initializer.newSparePartStatus(Initializer.getDate(10, 12, 2010),User.findByUsername("admin"),StatusOfSparePart.INSTOCK,sparePart,[:],null)
		def statusTwo= Initializer.newSparePartStatus(Initializer.getDate(10, 12, 2011),User.findByUsername("admin"),StatusOfSparePart.OPERATIONAL,sparePart,[:],equipment01)
		def statusOne= Initializer.newSparePartStatus(Initializer.getDate(10, 07, 2012),User.findByUsername("admin"),StatusOfSparePart.DISPOSED,sparePart,[:],null)
		then:
		SparePart.count() == 1
		SparePart.list()[0].timeBasedStatus.statusOfSparePart==statusOne.statusOfSparePart
//		equipment01.serialNumber.equals("SERIAL01")
//		equipment01.id!=null
	}
}
