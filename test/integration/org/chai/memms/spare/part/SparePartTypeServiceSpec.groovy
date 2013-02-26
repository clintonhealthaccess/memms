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
import org.chai.memms.IntegrationTests
import org.chai.memms.spare.part.SparePartType;
import org.chai.memms.spare.part.SparePartTypeService;
import org.chai.memms.inventory.Provider.Type;
import java.util.Date;
/**
 * @author Alain Inema
 *
 */
class SparePartTypeServiceSpec extends IntegrationTests{
	
	def sparePartTypeService
	
	def "can search a spare part type by code"() {
		setup:
		def manufactureContact = Initializer.newContact(['en':'Address Descriptions '],"Manufacture","jkl@yahoo.com","0768-889-787","Street 154","6353")
		def manufacturer = Initializer.newProvider("TEST" + CODE(1), Type.MANUFACTURER,manufactureContact)
		Initializer.newSparePartType(CODE(123),["en":"names spare part type one"],["en":"descriptions spare part type one"],"7654-HGT",manufacturer,new Date())
		when:  
		List<SparePartType>sparePartTypes = sparePartTypeService.searchSparePartType(CODE(123),[:])
		then:
		sparePartTypes.size() == 1
		sparePartTypes[0].code.equals(CODE(123))
	
    }
	def "can search a spare part type by name"() {
		setup:
		def manufactureContact = Initializer.newContact(['en':'Address Descriptions '],"Manufacture","jkl@yahoo.com","0768-889-787","Street 154","6353")
		def manufacturer = Initializer.newProvider("TEST" + CODE(1), Type.MANUFACTURER,manufactureContact)
		Initializer.newSparePartType(CODE(123),["en":"names spare part type one"],["en":"descriptions spare part type one"],"7654-HGT",manufacturer,new Date())
		when:
		List<SparePartType>sparePartTypes = sparePartTypeService.searchSparePartType("names spare part type one",[:])
		then:
		sparePartTypes.size() == 1
		sparePartTypes[0].code.equals(CODE(123))
	}
	def "can search a spare part type by part number"() {
		setup:
		def manufactureContact = Initializer.newContact(['en':'Address Descriptions '],"Manufacture","jkl@yahoo.com","0768-889-787","Street 154","6353")
		def manufacturer = Initializer.newProvider("TEST" + CODE(1), Type.MANUFACTURER,manufactureContact)
		Initializer.newSparePartType(CODE(123),["en":"names spare part type one"],["en":"descriptions spare part type one"],"7654-HGT",manufacturer,new Date())
		when:  
		List<SparePartType>sparePartTypes = sparePartTypeService.searchSparePartType("7654-HGT",[:])
		then:
		sparePartTypes.size() == 1
		sparePartTypes[0].partNumber.equals("7654-HGT")
	}
	def "can search a spare part type by description"() {
		setup:
		def manufactureContact = Initializer.newContact(['en':'Address Descriptions '],"Manufacture","jkl@yahoo.com","0768-889-787","Street 154","6353")
		def manufacturer = Initializer.newProvider("TEST" + CODE(1), Type.MANUFACTURER,manufactureContact)
		Initializer.newSparePartType(CODE(123),["en":"names spare part type one"],["en":"descriptions spare part type one"],"7654-HGT",manufacturer,new Date())
		when:
		List<SparePartType>sparePartTypes = sparePartTypeService.searchSparePartType("descriptions spare part type one",[:])
		then:
		sparePartTypes.size() == 1
		sparePartTypes[0].code.equals(CODE(123))
	}
	
}