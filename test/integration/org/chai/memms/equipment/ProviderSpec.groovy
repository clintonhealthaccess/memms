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

package org.chai.memms.equipment

import org.chai.memms.Initializer;
import org.chai.memms.IntegrationTests;
import org.chai.memms.equipment.Provider.Type;


class ProviderSpec extends IntegrationTests{
	
	def "can create and save all three types of providers"() {

		setup:
		def manufactureContact = Initializer.newContact(['en':'Manufacture Address Descriptions One'],"Manufacture Nokia","jkl@yahoo.com","0768-889-787","Street 154","8988")
		def supplierContact = Initializer.newContact(['en':'Supplier Address Descriptions Two'],"Manufacture Siemens","jk2@yahoo.com","0768-432-787","Street 112654","89388")
		def othersContact = Initializer.newContact(['en':'Unknown Address Descriptions Two'],"Unknown Other","jk1@yahoo.com","0768-111-787","Street 12","89288")
		
		when:
		def manufacturer = new Provider(code:CODE(123), type: Type.MANUFACTURER, contact:manufactureContact)
		manufacturer.save(failOnError: true)
		def supplier = new Provider(code:CODE(124), type: Type.SUPPLIER, contact:supplierContact)
		supplier.save(failOnError: true)
		def both = new Provider(code:CODE(125), type: Type.BOTH, contact:othersContact)
		both.save()
		
		then:
		Provider.count() == 3
	}
	
	
	def "can't create providers without specifing type"() {
		
		setup:
		def manufactureContact = Initializer.newContact(['en':'Manufacture Address Descriptions One'],"Manufacture Nokia","jkl@yahoo.com","0768-889-787","Street 154","8988")
		when:
		def manufacturer = new Provider(code:CODE(123), contact:manufactureContact)
		manufacturer.save()
		
		then:
		Provider.count() == 0
		manufacturer.errors.hasFieldErrors('type') == true
				
	}
	
	def "can't create provider without specifying  address"() {		
		when:
		def supplier = new Provider(code:CODE(124), type: Type.SUPPLIER)
		supplier.save()
		
		then:
		Provider.count() == 0
		supplier.errors.hasFieldErrors('contact') == true
		
	}
}