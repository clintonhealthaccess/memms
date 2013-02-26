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
		sparePartType.save(failOnError:false)
		
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
		sparePartType.save()
		
		then:
		SparePartType.count() == 1
		sparePartType.errors.hasFieldErrors('code') == true
	}
	def "can't create and save an spare part type without a manufacturer"() {
		setup:
		def manufactureContact = Initializer.newContact(['en':'Address Descriptions '],"Manufacture","jkl@yahoo.com","0768-889-787","Street 154","6353")
		def manufacturer = Initializer.newProvider("TEST" + CODE(1), Type.MANUFACTURER,manufactureContact)
		Initializer.newSparePartType(CODE(123),["en":"names spare part type one"],["en":"descriptions spare part type one"],"7654-HGT",manufacturer,new Date())
		
		when:
		def sparePartType = new SparePartType(code:CODE(123),names:["en":"names spare part type two"],descriptions:["en":"descriptions spare part type two"],partNumber:"7655-HGT",discontinuedDate:new Date())
		sparePartType.save()
		
		then:
		SparePartType.count() == 1
		sparePartType.errors.hasFieldErrors('manufacturer') == true
	}
		
}
