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
package org.chai.memms.inventory

import org.chai.memms.Contact;

import groovy.transform.EqualsAndHashCode;
import i18nfields.I18nFields

/**
 * @author Jean Kahigiso M.
 *
 */
@EqualsAndHashCode(includes="code")
public class Provider{
	
	enum Type{
		NONE("none"),
		BOTH("both"),
		MANUFACTURER("manufacturer"),
		SUPPLIER("supplier"),
		SERVICEPROVIDER("serviceProvider")
		
		String messageCode = "provider.type"
		final String name
		Type(String name){ this.name = name }
		String getKey() { return name() }
	}
	
	String code
	Type type
	Contact contact
	Date dateCreated
	Date lastUpdated
	
	static embedded = ["contact"]
	static mappedBy = [manufacturers: "manufacturer",suppliers: "supplier",serviceProviders: "serviceProvider"]
	static hasMany = [manufacturers: Equipment, suppliers: Equipment,serviceProviders: Equipment]
   
	static constraints ={
		code nullable: false, blank: false, unique: true
		type nullable: false, inList: [Type.BOTH,Type.MANUFACTURER,Type.SUPPLIER,Type.SERVICEPROVIDER]
		contact nullable: false
		lastUpdated nullable: true, validator:{
			if(it != null) return (it <= new Date())
		}
	}
	static mapping = {
	    version false
	    table "memms_provider"
	}
	
	@Override
	public String toString() {
		return "Provider [code=" + code + ", type=" + type + "]";
	}	
}
