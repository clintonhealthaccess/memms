/**
 * 
 */
package org.chai.memms.spare.part

import org.chai.memms.Initializer;
import org.chai.memms.IntegrationTests;
import org.chai.memms.inventory.Provider.Type;
import org.chai.memms.util.Utils;
import org.chai.memms.spare.part.SparePartType;

/**
 * @author Jean Kahigiso M.
 *
 */
class SparePartTypeSpec extends IntegrationTests{
 
	def "can create and save an spare part type"(){
		setup:
		def manufactureContact = Initializer.newContact(['en':'Address Descriptions '],"Manufacture","jkl@yahoo.com","0768-889-787","Street 154","6353")
		def manufacturer = Initializer.newProvider("TEST" + CODE(1), Type.MANUFACTURER,manufactureContact)
		
		when:
		Initializer.newSparePartType(CODE(123),["en":"names spare part type one"],["en":"descriptions spare part type one"],"7654-HGT",manufacturer,new Date())
		def sparePartType = new SparePartType(code:CODE(124),names:["en":"names spare part type two"],descriptions:["en":"descriptions spare part type two"],partNumber:"7655-HGT",manufacturer:manufacturer,discontinuedDate:new Date())
		sparePartType.save(failOnError: true)
		
		then:
		SparePartType.count() == 2
		
	}
	def "can't create and save an spare part type without a code"(){
		setup:
		def manufactureContact = Initializer.newContact(['en':'Address Descriptions '],"Manufacture","jkl@yahoo.com","0768-889-787","Street 154","6353")
		def manufacturer = Initializer.newProvider("TEST" + CODE(1), Type.MANUFACTURER,manufactureContact)
		Initializer.newSparePartType(CODE(123),["en":"names spare part type one"],["en":"descriptions spare part type one"],"7654-HGT",manufacturer,new Date())
		
		when:
		def sparePartType = new SparePartType(names:["en":"names spare part type two"],descriptions:["en":"descriptions spare part type two"],partNumber:"7655-HGT",manufacturer:manufacturer,discontinuedDate:new Date())
		sparePartType.save()
		
		then:
		SparePartType.count() == 1
		sparePartType.errors.hasFieldErrors('code') == true
	}
	def "can't create and save an spare part type with a duplicate code"(){
		setup:
		def manufactureContact = Initializer.newContact(['en':'Address Descriptions '],"Manufacture","jkl@yahoo.com","0768-889-787","Street 154","6353")
		def manufacturer = Initializer.newProvider("TEST" + CODE(1), Type.MANUFACTURER,manufactureContact)
		Initializer.newSparePartType(CODE(123),["en":"names spare part type one"],["en":"descriptions spare part type one"],"7654-HGT",manufacturer,new Date())
		
		when:
		def sparePartType = new SparePartType(code:CODE(123),names:["en":"names spare part type two"],descriptions:["en":"descriptions spare part type two"],partNumber:"7655-HGT",manufacturer:manufacturer,discontinuedDate:new Date())
		sparePartType.save( )
		
		then:
		SparePartType.count() == 1
		sparePartType.errors.hasFieldErrors('code') == true
	}
		
}
