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

import java.util.Date;

import org.chai.memms.Period;

import groovy.transform.EqualsAndHashCode;
import i18nfields.I18nFields

/**
 * @author Eugene Munyaneza
 *
 */

@i18nfields.I18nFields
@EqualsAndHashCode(includes="code")
class EquipmentType {

	enum Observation{
		NONE("none"),
		USEDINMEMMS("used.in.memms"),
		RETIRED("retired"),
		TOODETAILED("too.detailed"),
		NOTINSCOPE("not.in.scope")
		
		String messageCode = "equipment.type"	

		String name
		Observation(String name){ this.name=name }
		String getKey() { return name() }
	}
	
	String code
	String names
	String descriptions
	Period expectedLifeTime
	Observation observation
	
	Date dateCreated
	Date lastUpdated
	
	static embedded = ["expectedLifeTime"]
	static i18nFields = ["descriptions","names"]
	static hasMany = [equipments: Equipment]
	
    static constraints = {
		
		code nullable: false, blank: false,  unique: true
		names nullable: true, blank: true
		descriptions nullable: true, blank: true
		
		expectedLifeTime nullable: true
		
		lastUpdated nullable: false, blank: false, validator:{it <= new Date()}
		
		observation nullable: false, inLIst:[Observation.USEDINMEMMS,Observation.RETIRED,Observation.TOODETAILED,Observation.NOTINSCOPE]
    }
	
	static mapping = {
		table "memms_equipment_type"
		version false
		cache true
	}

	def beforeValidate(){
		lastUpdated = new Date()
	}
	
	String toString() {
		return "EquipmentType[Id=" + id + "code="+code+"]";
	}
}
