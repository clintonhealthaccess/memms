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
import org.chai.memms.spare.part.SparePart.SparePartStatus;
import org.chai.memms.spare.part.SparePart.SparePartStatusChange;
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

		def supplierContact = Initializer.newContact([:],"Supplier","jk@yahoo.com","0768-888-787","Street 1654","6353")
		def supplier = Initializer.newProvider(CODE(124), Type.SUPPLIER,supplierContact)

		def sparePartType = Initializer.newSparePartType(CODE(15810),["en":"testOne names"],["en":"testOne descriptions"],"CODE Spare Part",manufacturer,Initializer.now())

		
		when:
		def sparePart = new SparePart(
			descriptions:['en':'Spare Part Descriptions'],
			sparePartPurchasedBy:SparePartPurchasedBy.BYMOH,
			purchaseDate:Initializer.getDate(22,07,2010),
			purchaseCost:2344,
			currency:"RWF",	
			type:sparePartType,
			supplier:supplier,
			addedBy: User.findByUsername("systemUser"),
			stockLocation:StockLocation.MMC,
			status:SparePartStatus.INSTOCK,
			initialQuantity:20,
			inStockQuantity:10
			)
		sparePart.save(failOnError: true)
		
		then:
		SparePart.count() == 1
		sparePart.usedQuantity == 10
		sparePart.isEmptyStock == false
	}

	def "can't create and save the spare part when:"() {

		setup:
		setupLocationTree()
		setupSystemUser()
		def now = Initializer.now()
		def manufactureContact = Initializer.newContact(['en':'Address Descriptions '],"Manufacture","jkl@yahoo.com","0768-889-787","Street 154","6353")
		def manufacturer = Initializer.newProvider(CODE(111), Type.MANUFACTURER,manufactureContact)

		def supplierContact = Initializer.newContact([:],"Supplier","jk@yahoo.com","0768-888-787","Street 1654","6353")
		def supplier = Initializer.newProvider(CODE(124), Type.SUPPLIER,supplierContact)

		def sparePartType = Initializer.newSparePartType(CODE(15810),["en":"testOne names"],["en":"testOne descriptions"],"CODE Spare Part",manufacturer,Initializer.now())
		def sparePart
		
		when: //"sparePartPurchasedBy is not specified"
		sparePart = new SparePart(descriptions:['en':'Spare Part Descriptions'],purchaseDate:Initializer.getDate(22,07,2010),purchaseCost:2344,
			currency:"RWF",	type:sparePartType,supplier:supplier,addedBy: User.findByUsername("systemUser"),stockLocation:StockLocation.MMC,
			status:SparePartStatus.INSTOCK,initialQuantity:20,inStockQuantity:10
			)
		sparePart.save()
		
		then:
		SparePart.count() == 0
		sparePart.errors.hasFieldErrors('sparePartPurchasedBy') == true

		when: //"purchaseDate is in future specified"
		sparePart = new SparePart(descriptions:['en':'Spare Part Descriptions'],purchaseDate:now+1,sparePartPurchasedBy:SparePartPurchasedBy.BYMOH,purchaseCost:2344,
			currency:"RWF",	type:sparePartType,supplier:supplier,addedBy: User.findByUsername("systemUser"),stockLocation:StockLocation.MMC,
			status:SparePartStatus.INSTOCK,initialQuantity:20,inStockQuantity:10
			)
		sparePart.save()
		
		then:
		SparePart.count() == 0
		sparePart.errors.hasFieldErrors('purchaseDate') == true

		when: //"purchaseCost when currency is spacified "
		sparePart = new SparePart(descriptions:['en':'Spare Part Descriptions'],sparePartPurchasedBy:SparePartPurchasedBy.BYMOH,purchaseDate:Initializer.getDate(22,07,2010),
			currency:"RWF",type:sparePartType,supplier:supplier,addedBy: User.findByUsername("systemUser"),stockLocation:StockLocation.MMC,
			status:SparePartStatus.INSTOCK,initialQuantity:20,inStockQuantity:10
			)
		sparePart.save()
		
		then:
		SparePart.count() == 0
		sparePart.errors.hasFieldErrors('purchaseCost') == true

		when: //"currency when purchaseCost is spacified "
		sparePart = new SparePart(descriptions:['en':'Spare Part Descriptions'],sparePartPurchasedBy:SparePartPurchasedBy.BYMOH,purchaseDate:Initializer.getDate(22,07,2010),
			purchaseCost:2344,type:sparePartType,supplier:supplier,addedBy: User.findByUsername("systemUser"),stockLocation:StockLocation.MMC,
			status:SparePartStatus.INSTOCK,initialQuantity:20,inStockQuantity:10
			)
		sparePart.save()
		
		then:
		SparePart.count() == 0
		sparePart.errors.hasFieldErrors('currency') == true

		when: //"type  is not spacified "
		 sparePart = new SparePart(descriptions:['en':'Spare Part Descriptions'],sparePartPurchasedBy:SparePartPurchasedBy.BYMOH,purchaseDate:Initializer.getDate(22,07,2010),
			purchaseCost:2344,currency:"RWF",supplier:supplier,addedBy: User.findByUsername("systemUser"),stockLocation:StockLocation.MMC,
			status:SparePartStatus.INSTOCK,initialQuantity:20,inStockQuantity:10
			)
		sparePart.save()
		
		then:
		SparePart.count() == 0
		sparePart.errors.hasFieldErrors('type') == true

		when: //"addedBy  is not spacified "
		sparePart = new SparePart(descriptions:['en':'Spare Part Descriptions'],sparePartPurchasedBy:SparePartPurchasedBy.BYMOH,purchaseDate:Initializer.getDate(22,07,2010),
			purchaseCost:2344,currency:"RWF",type:sparePartType,supplier:supplier,stockLocation:StockLocation.MMC,
			status:SparePartStatus.INSTOCK,initialQuantity:20,inStockQuantity:10
			)
		sparePart.save()
		
		then:
		SparePart.count() == 0
		sparePart.errors.hasFieldErrors('addedBy') == true

		when: //"stockLocation  is not spacified "
		sparePart = new SparePart(descriptions:['en':'Spare Part Descriptions'],sparePartPurchasedBy:SparePartPurchasedBy.BYMOH,purchaseDate:Initializer.getDate(22,07,2010),
			purchaseCost:2344,currency:"RWF",type:sparePartType,supplier:supplier,addedBy: User.findByUsername("systemUser"),
			status:SparePartStatus.INSTOCK,initialQuantity:20,inStockQuantity:10
			)
		sparePart.save()
		
		then:
		SparePart.count() == 0
		sparePart.errors.hasFieldErrors('stockLocation') == true

		when: //"status  is not spacified "
		sparePart = new SparePart(descriptions:['en':'Spare Part Descriptions'],sparePartPurchasedBy:SparePartPurchasedBy.BYMOH,purchaseDate:Initializer.getDate(22,07,2010),
			purchaseCost:2344,currency:"RWF",type:sparePartType,supplier:supplier,addedBy: User.findByUsername("systemUser"),stockLocation:StockLocation.MMC,
			initialQuantity:20,inStockQuantity:10
			)
		sparePart.save()
		then:
		SparePart.count() == 0
		sparePart.errors.hasFieldErrors('status') == true

		when: //"initialQuantity  is not spacified "
		sparePart = new SparePart(descriptions:['en':'Spare Part Descriptions'],sparePartPurchasedBy:SparePartPurchasedBy.BYMOH,purchaseDate:Initializer.getDate(22,07,2010),
			purchaseCost:2344,currency:"RWF",type:sparePartType,supplier:supplier,addedBy: User.findByUsername("systemUser"),stockLocation:StockLocation.MMC,
			status:SparePartStatus.INSTOCK,inStockQuantity:10
			)
		sparePart.save()
		then:
		SparePart.count() == 0
		sparePart.errors.hasFieldErrors('initialQuantity') == true

		when: //"inStockQuantity  is negative"
		sparePart = new SparePart(descriptions:['en':'Spare Part Descriptions'],sparePartPurchasedBy:SparePartPurchasedBy.BYMOH,purchaseDate:Initializer.getDate(22,07,2010),
			purchaseCost:2344,currency:"RWF",type:sparePartType,supplier:supplier,addedBy: User.findByUsername("systemUser"),stockLocation:StockLocation.MMC,
			status:SparePartStatus.PENDINGORDER,initialQuantity:20,inStockQuantity:-9
			)
		sparePart.save()		
		then:
		SparePart.count() == 0
		sparePart.errors.hasFieldErrors('inStockQuantity') == true

		when: //"can save with supplier not spacified"
		sparePart = new SparePart(descriptions:['en':'Spare Part Descriptions'],sparePartPurchasedBy:SparePartPurchasedBy.BYMOH,purchaseDate:Initializer.getDate(22,07,2010),
			purchaseCost:2344,currency:"RWF",type:sparePartType,addedBy: User.findByUsername("systemUser"),stockLocation:StockLocation.MMC,
			status:SparePartStatus.INSTOCK,initialQuantity:20,inStockQuantity:10
			)
		sparePart.save()
		
		then:
		SparePart.count() == 1
		sparePart.errors.hasFieldErrors('supplier') == false
	}

	def "can get spare part status change"() {

		setup:
		setupLocationTree()
		setupSystemUser()
		def manufactureContact = Initializer.newContact(['en':'Address Descriptions '],"Manufacture","jkl@yahoo.com","0768-889-787","Street 154","6353")
		def manufacturer = Initializer.newProvider(CODE(111), Type.MANUFACTURER,manufactureContact)

		def supplierContact = Initializer.newContact([:],"Supplier","jk@yahoo.com","0768-888-787","Street 1654","6353")
		def supplier = Initializer.newProvider(CODE(124), Type.SUPPLIER,supplierContact)

		def sparePartType = Initializer.newSparePartType(CODE(15810),["en":"testOne names"],["en":"testOne descriptions"],"CODE Spare Part",manufacturer,Initializer.now())

		
		when:
		def sparePart = new SparePart(
			descriptions:['en':'Spare Part Descriptions'],
			sparePartPurchasedBy:SparePartPurchasedBy.BYMOH,
			purchaseDate:Initializer.getDate(22,07,2010),
			purchaseCost:2344,
			currency:"RWF",	
			type:sparePartType,
			supplier:supplier,
			addedBy: User.findByUsername("systemUser"),
			stockLocation:StockLocation.MMC,
			status:SparePartStatus.INSTOCK,
			initialQuantity:20,
			inStockQuantity:10
			)
		sparePart.save(failOnError: true)
		
		then:
		SparePart.count() == 1
		sparePart.getSparePartStatusChange().equals(SparePartStatusChange.NEWPENDINGORDER)
		sparePart.getSparePartStatusChange([SparePartStatusChange.NEWPENDINGORDER]).equals(SparePartStatusChange.NEWPENDINGORDER)
		sparePart.getSparePartStatusChange([SparePartStatusChange.PENDINGORDERARRIVED]).equals(null)

		when:
		sparePart.deliveryDate = Initializer.getDate(22,07,2011)
		sparePart.save()

		then:
		sparePart.getSparePartStatusChange().equals(SparePartStatusChange.PENDINGORDERARRIVED)
		sparePart.getSparePartStatusChange([SparePartStatusChange.NEWPENDINGORDER]).equals(null)
		sparePart.getSparePartStatusChange([SparePartStatusChange.PENDINGORDERARRIVED]).equals(SparePartStatusChange.PENDINGORDERARRIVED)
	}

}