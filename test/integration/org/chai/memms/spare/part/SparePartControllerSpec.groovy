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
import org.chai.memms.spare.part.SparePartStatus.StatusOfSparePart;
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
		def equipment = newEquipment("SERIAL01",DataLocation.findByCode(KIVUYE))

		sparePartController = new SparePartController();
		
		when:
		sparePartController.params.serialNumber = 'SERIAL12129'
		sparePartController.params.purchaseCost = "32000"
		sparePartController.params.currency = "USD"
		sparePartController.params.sparePartPurchasedBy = SparePartPurchasedBy.BYMOH
		sparePartController.params.stockLocation = StockLocation.MMC
		sparePartController.params.sameAsManufacturer = false
		sparePartController.params.descriptions_en = "test_english_descriptions"
		sparePartController.params.manufactureDate = Initializer.getDate(1,1,2012)
		sparePartController.params.purchaseDate = Initializer.getDate(2,1,2012)
		sparePartController.params.type = sparePartType
		sparePartController.params."warranty.startDate" = Initializer.getDate(10,1,2012)
		sparePartController.params."warranty.sameAsSupplier" = true
		sparePartController.params."warranty.descriptions_en" = "new warranty for testing"
		sparePartController.params.warrantyPeriod = "struct"
		sparePartController.params.warrantyPeriod_years = "1"
		sparePartController.params.warrantyPeriod_months = "4"
		sparePartController.params."supplier.id" = supplier.id
		sparePartController.params.expectedLifeTime = "struct"
		sparePartController.params.expectedLifeTime_years = "1"
		sparePartController.params.expectedLifeTime_months = "3"
		sparePartController.params.dataLocation = DataLocation.list().first()
		sparePartController.params.statusOfSparePart="INSTOCK"
		sparePartController.params.dateOfEvent=Initializer.now()
		sparePartController.params.usedOnEquipment=equipment
		sparePartController.save()

		then:
		SparePart.count() == 1;
		SparePart.findBySerialNumber("SERIAL12129").serialNumber.equals("SERIAL12129")
		SparePart.findBySerialNumber("SERIAL12129").expectedLifeTime.numberOfMonths == 15
		SparePart.findBySerialNumber("SERIAL12129").warrantyPeriod.numberOfMonths == 16
		SparePart.findByDescriptions_en("test_english_descriptions").getDescriptions(new Locale("en")).equals("test_english_descriptions")
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
		def equipment = newEquipment("SERIAL01",DataLocation.findByCode(KIVUYE))
				
		sparePartController = new SparePartController();
		
		when:
		sparePartController.params.serialNumber = 'SERIAL129'
		sparePartController.params.purchaseCost = ""
		sparePartController.params.currency = ""
		sparePartController.params.sparePartPurchasedBy = SparePartPurchasedBy.BYMOH
		sparePartController.params.stockLocation = StockLocation.MMC
		sparePartController.params.sameAsMAnufacturer = false
		sparePartController.params."warranty.startDate" = Initializer.getDate(1,1,2012)
		sparePartController.params."warranty.sameAsSupplier" = true
		sparePartController.params.warrantyPeriod = "struct"
		sparePartController.params.warrantyPeriod_years = "1"
		sparePartController.params.warrantyPeriod_months = "4"
		grailsApplication.config.i18nFields.locales.each{
			sparePartController.params."warranty.descriptions_$it" = "new warranty for testing $it"
		}
		
		grailsApplication.config.i18nFields.locales.each{
			sparePartController.params."descriptions_$it" = "test descriptions $it"
		}
		sparePartController.params.manufactureDate = Initializer.getDate(1,1,2012)
		sparePartController.params.purchaseDate = Initializer.getDate(1,1,2012)
		sparePartController.params.type = sparePartType
		sparePartController.params.expectedLifeTime = "struct"
		sparePartController.params.expectedLifeTime_years = "1"
		sparePartController.params.expectedLifeTime_months = "3"
		sparePartController.params."supplier.id" = supplier.id
		sparePartController.params.dataLocation = DataLocation.list().first()
		sparePartController.params.statusOfSparePart="INSTOCK"
		sparePartController.params.dateOfEvent=Initializer.now()
		sparePartController.params.usedOnEquipment=equipment
		sparePartController.save()
		
		then:
		SparePart.count() == 1;
		SparePart.findBySerialNumber("SERIAL129").serialNumber.equals("SERIAL129")
		SparePart.findBySerialNumber("SERIAL129").expectedLifeTime.numberOfMonths == 15
		SparePart.findBySerialNumber("SERIAL129").warrantyPeriod.numberOfMonths == 16
		grailsApplication.config.i18nFields.locales.each{
			SparePart."findByDescriptions_$it"("test descriptions $it").getDescriptions(new Locale("$it")).equals("test descriptions $it")
		}
	}
	
	def "Can create spare part with status OPERATIONAL when all location's parameters are null"(){
		
		setup:
		setupLocationTree()
		setupSystemUser()
		def manufactureContact = Initializer.newContact(['en':'Address Descriptions '],"Manufacture","jkl@yahoo.com","0768-889-787","Street 154","6353")
		def manufacturer = Initializer.newProvider(CODE(111), Type.MANUFACTURER,manufactureContact)
		def sparePartType = Initializer.newSparePartType(CODE(15810),["en":"testOne names"],["en":"testOne descriptions"],"CODE Spare Part",manufacturer,Initializer.now())
		def supplierContact = Initializer.newContact(['en':'Address Descriptions '],"Supplier","jk@yahoo.com","0768-888-787","Street 1654","6353")
		def supplier = Initializer.newProvider(CODE(124), Type.SUPPLIER,supplierContact)
		def equipment = newEquipment("SERIAL01",DataLocation.findByCode(KIVUYE))
		
		sparePartController = new SparePartController();
		
		when:
		sparePartController.params.serialNumber = 'SERIAL12129'
		sparePartController.params.purchaseCost = "32000"
		sparePartController.params.currency = "USD"
		sparePartController.params.sparePartPurchasedBy = SparePartPurchasedBy.BYMOH
		sparePartController.params.stockLocation = null
		sparePartController.params.dataLocation = null
		sparePartController.params.room = null
		sparePartController.params.shelve = null
		sparePartController.params.sameAsManufacturer = false
		sparePartController.params.descriptions_en = "test_english_descriptions"
		sparePartController.params.manufactureDate = Initializer.getDate(1,1,2012)
		sparePartController.params.purchaseDate = Initializer.getDate(2,1,2012)
		sparePartController.params.type = sparePartType
		sparePartController.params."warranty.startDate" = Initializer.getDate(10,1,2012)
		sparePartController.params."warranty.sameAsSupplier" = true
		sparePartController.params."warranty.descriptions_en" = "new warranty for testing"
		sparePartController.params.warrantyPeriod = "struct"
		sparePartController.params.warrantyPeriod_years = "1"
		sparePartController.params.warrantyPeriod_months = "4"
		sparePartController.params."supplier.id" = supplier.id
		sparePartController.params.expectedLifeTime = "struct"
		sparePartController.params.expectedLifeTime_years = "1"
		sparePartController.params.expectedLifeTime_months = "3"
		sparePartController.params.statusOfSparePart="OPERATIONAL"
		sparePartController.params.dateOfEvent=Initializer.now()
		sparePartController.params.usedOnEquipment=equipment
		sparePartController.save()
		
		then:
		SparePart.count() == 1;
		SparePart.findBySerialNumber("SERIAL12129").serialNumber.equals("SERIAL12129")
		SparePart.findBySerialNumber("SERIAL12129").expectedLifeTime.numberOfMonths == 15
		SparePart.findBySerialNumber("SERIAL12129").warrantyPeriod.numberOfMonths == 16
		SparePart.findByDescriptions_en("test_english_descriptions").getDescriptions(new Locale("en")).equals("test_english_descriptions")
	}
	
}
