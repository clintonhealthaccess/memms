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

import org.chai.memms.IntegrationTests;
import org.chai.memms.spare.part.SparePartController;
import org.chai.memms.Initializer;
import org.chai.memms.spare.part.SparePart.StockLocation;
import org.chai.memms.spare.part.SparePartViewController;
import org.chai.memms.spare.part.FilterCommand;
import org.chai.memms.spare.part.SparePart.SparePartPurchasedBy;
import org.chai.memms.spare.part.SparePart.SparePartStatus;
import org.chai.location.DataLocation;
import org.chai.memms.security.User;
import org.chai.memms.inventory.Provider.Type;
import org.chai.memms.spare.part.SparePart;
import org.chai.memms.inventory.Provider;
import org.chai.memms.inventory.Provider.Type;

/**
 * @author Aphrodice Rwagaju
 *
 */
class SparePartControllerSpec extends IntegrationTests{
	def sparePartController

	def "create spare part with correct required data in fields - for english input"(){

		setup:
		setupLocationTree()
		setupSystemUser()
		def manufactureContact = Initializer.newContact(['en':'Address Descriptions '],"Manufacture","jkl@yahoo.com","0768-889-787","Street 154","6353")
		def manufacturer = Initializer.newProvider(CODE(111), Type.MANUFACTURER,manufactureContact)
		def sparePartType = Initializer.newSparePartType(CODE(15810),["en":"testOne names"],["en":"testOne descriptions"],"CODE Spare Part",manufacturer,Initializer.now())
		def supplierContact = Initializer.newContact(['en':'Address Descriptions '],"Supplier","jk@yahoo.com","0768-888-787","Street 1654","6353")
		def supplier = Initializer.newProvider(CODE(124), Type.SUPPLIER,supplierContact)

		when:
		sparePartController = new SparePartController();
		sparePartController.params.receivedQuantity = 44
		sparePartController.params.purchaseCost = "32000"
		sparePartController.params.currency = "USD"
		sparePartController.params.sparePartPurchasedBy = SparePartPurchasedBy.BYMOH
		sparePartController.params.stockLocation = StockLocation.MMC
		sparePartController.params.descriptions_en = "test_english_descriptions"
		sparePartController.params.purchaseDate = Initializer.getDate(2,1,2012)
		sparePartController.params.type = sparePartType
		sparePartController.params."supplier.id" = supplier.id
		sparePartController.params.status="INSTOCK"
		sparePartController.params.orderedQuantity="130"
		sparePartController.save()

		then:
		SparePart.count() == 1;
		SparePart.findByDescriptions_en("test_english_descriptions").getDescriptions(new Locale("en")).equals("test_english_descriptions")
		SparePart.findByReceivedQuantity(44) != null
		SparePart.findByOrderedQuantity(130) != null
	}
	def "create spare part with correct required data in fields - for french input"(){

		setup:
		setupLocationTree()
		setupSystemUser()
		def manufactureContact = Initializer.newContact(['fr':'Addresse detaille '],"Fabriquant","aphro@gmail.com","0788-448-261","Avenue de la Paix","6139")
		def manufacturer = Initializer.newProvider(CODE(111), Type.MANUFACTURER,manufactureContact)
		def sparePartType = Initializer.newSparePartType(CODE(15810),["fr":"Test des noms"],["fr":"test des noms en details"],"CODE Spare Part",manufacturer,Initializer.now())
		def supplierContact = Initializer.newContact(['fr':'Description addresse '],"Fournisseur","aphro@gmail.com","0788-448-261","Avenue de la Paix","6139")
		def supplier = Initializer.newProvider(CODE(124), Type.SUPPLIER,supplierContact)
		
		when:
		sparePartController = new SparePartController();
		sparePartController.params.receivedQuantity = 55
		sparePartController.params.purchaseCost = "327670"
		sparePartController.params.currency = "USD"
		sparePartController.params.sparePartPurchasedBy = SparePartPurchasedBy.BYMOH
		sparePartController.params.stockLocation = StockLocation.FACILITY
		sparePartController.params.descriptions_fr = "Un test francais"
		sparePartController.params.purchaseDate = Initializer.getDate(2,1,2012)
		sparePartController.params.type = sparePartType
		sparePartController.params."supplier.id" = supplier.id
		sparePartController.params.dataLocation = DataLocation.list().first()
		sparePartController.params.status="INSTOCK"
		sparePartController.params.orderedQuantity="55"
		sparePartController.save()

		then:
		SparePart.count() == 1;
		SparePart.findByDescriptions_fr("Un test francais").getDescriptions(new Locale("fr")).equals("Un test francais")
		SparePart.findByReceivedQuantity(55) != null
		SparePart.findByPurchaseCost(327670) != null
		SparePart.findByOrderedQuantity(55) != null

	}
	def "create spare part with correct required data in fields - for all locale"(){
		
		setup:
		setupLocationTree()
		setupSystemUser()
		
		def supplierContact = Initializer.newContact(['en':'Address Descriptions '],"Supplier","jk@yahoo.com","0768-888-787","Street 1654","6353")
		def manufactureContact = Initializer.newContact(['en':'Address Descriptions '],"Manufacture","jkl@yahoo.com","0768-889-787","Street 154","6353")
		def manufacturer = Initializer.newProvider(CODE(111), Type.MANUFACTURER,manufactureContact)
		def supplier = Initializer.newProvider(CODE(124), Type.SUPPLIER,supplierContact)
		def sparePartType = Initializer.newSparePartType(CODE(15810),["en":"testOne names"],["en":"testOne descriptions"],"CODE Spare Part",manufacturer,Initializer.now())
				
		sparePartController = new SparePartController();
		
		when:
		sparePartController.params.purchaseCost = ""
		sparePartController.params.currency = ""
		sparePartController.params.receivedQuantity = 44
		sparePartController.params.sparePartPurchasedBy = SparePartPurchasedBy.BYMOH
		sparePartController.params.stockLocation = StockLocation.MMC
		grailsApplication.config.i18nFields.locales.each{
			sparePartController.params."descriptions_$it" = "test descriptions $it"
		}
		sparePartController.params.purchaseDate = Initializer.getDate(1,1,2012)
		sparePartController.params.type = sparePartType
		sparePartController.params."supplier.id" = supplier.id
		sparePartController.params.status="INSTOCK"
		sparePartController.params.orderedQuantity="44"
		sparePartController.save()
		
		then:
		SparePart.count() == 1;
		grailsApplication.config.i18nFields.locales.each{
			SparePart."findByDescriptions_$it"("test descriptions $it").getDescriptions(new Locale("$it")).equals("test descriptions $it")
		}
	}
	
	def "dataLocation cannot be null when the stockLocation is at FACILITY and has to be null when stockLocation is at MMC"(){
		
		setup:
		setupLocationTree()
		setupSystemUser()
		def manufactureContact = Initializer.newContact(['en':'Address Descriptions '],"Manufacture","jkl@yahoo.com","0768-889-787","Street 154","6353")
		def manufacturer = Initializer.newProvider(CODE(111), Type.MANUFACTURER,manufactureContact)
		def sparePartType = Initializer.newSparePartType(CODE(15810),["en":"testOne names"],["en":"testOne descriptions"],"CODE Spare Part",manufacturer,Initializer.now())
		def supplierContact = Initializer.newContact(['en':'Address Descriptions '],"Supplier","jk@yahoo.com","0768-888-787","Street 1654","6353")
		def supplier = Initializer.newProvider(CODE(124), Type.SUPPLIER,supplierContact)
		
		sparePartController = new SparePartController();		

		when:
		sparePartController.params.receivedQuantity = 13
		sparePartController.params.purchaseCost = "32000"
		sparePartController.params.currency = "USD"
		sparePartController.params.sparePartPurchasedBy = SparePartPurchasedBy.BYMOH
		sparePartController.params.stockLocation = StockLocation.FACILITY
		sparePartController.params.dataLocation = null
		sparePartController.params.room = ''
		sparePartController.params.shelve = ''
		sparePartController.params.descriptions_en = "test_english_descriptions"
		sparePartController.params.purchaseDate = Initializer.getDate(2,1,2012)
		sparePartController.params.type = sparePartType
		sparePartController.params."supplier.id" = supplier.id
		sparePartController.params.status="INSTOCK"
		sparePartController.params.orderedQuantity = 10
		sparePartController.save()
		
		then:
		SparePart.count() == 0;

		when:
		sparePartController.params.receivedQuantity = 43
		sparePartController.params.purchaseCost = "32000"
		sparePartController.params.currency = "USD"
		sparePartController.params.sparePartPurchasedBy = SparePartPurchasedBy.BYMOH
		sparePartController.params.stockLocation = StockLocation.MMC
		sparePartController.params.dataLocation = DataLocation.findByCode(KIVUYE)
		sparePartController.params.room = ''
		sparePartController.params.shelve = ''
		sparePartController.params.descriptions_en = "test_english_descriptions"
		sparePartController.params.purchaseDate = Initializer.getDate(2,1,2012)
		sparePartController.params.type = sparePartType
		sparePartController.params."supplier.id" = supplier.id
		sparePartController.params.status="INSTOCK"
		sparePartController.params.orderedQuantity = 43
		sparePartController.save()
		
		then:
		SparePart.count() == 0;
		
	}
	
	def "Create spare part without oredered quantity not allowed"(){
		
		setup:
		setupLocationTree()
		setupSystemUser()
		def manufactureContact = Initializer.newContact(['en':'Address Descriptions '],"Manufacture","jkl@yahoo.com","0768-889-787","Street 154","6353")
		def manufacturer = Initializer.newProvider(CODE(111), Type.MANUFACTURER,manufactureContact)
		def sparePartType = Initializer.newSparePartType(CODE(15810),["en":"testOne names"],["en":"testOne descriptions"],"CODE Spare Part",manufacturer,Initializer.now())
		def supplierContact = Initializer.newContact(['en':'Address Descriptions '],"Supplier","jk@yahoo.com","0768-888-787","Street 1654","6353")
		def supplier = Initializer.newProvider(CODE(124), Type.SUPPLIER,supplierContact)
		
		sparePartController = new SparePartController();

		when:
		sparePartController.params.receivedQuantity = 13
		sparePartController.params.purchaseCost = "32000"
		sparePartController.params.currency = "USD"
		sparePartController.params.sparePartPurchasedBy = SparePartPurchasedBy.BYMOH
		sparePartController.params.stockLocation = StockLocation.FACILITY
		sparePartController.params.dataLocation = null
		sparePartController.params.room = ''
		sparePartController.params.shelve = ''
		sparePartController.params.descriptions_en = "test_english_descriptions"
		sparePartController.params.purchaseDate = Initializer.getDate(2,1,2012)
		sparePartController.params.type = sparePartType
		sparePartController.params."supplier.id" = supplier.id
		sparePartController.params.status="INSTOCK"
		sparePartController.params.orderedQuantity = null
		sparePartController.save()
		
		then:
		SparePart.count() == 0;

		when:
		sparePartController.params.receivedQuantity = 43
		sparePartController.params.purchaseCost = "32000"
		sparePartController.params.currency = "USD"
		sparePartController.params.sparePartPurchasedBy = SparePartPurchasedBy.BYMOH
		sparePartController.params.dataLocation = DataLocation.findByCode(KIVUYE)
		sparePartController.params.room = ''
		sparePartController.params.shelve = ''
		sparePartController.params.descriptions_en = "test_english_descriptions"
		sparePartController.params.purchaseDate = Initializer.getDate(2,1,2012)
		sparePartController.params.type = sparePartType
		sparePartController.params."supplier.id" = supplier.id
		sparePartController.params.status="INSTOCK"
		sparePartController.params.orderedQuantity = 43
		sparePartController.save()
		
		then:
		SparePart.count() == 1;
		
	}
	
}
