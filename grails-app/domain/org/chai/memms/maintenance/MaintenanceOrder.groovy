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
 * 
 */
package org.chai.memms.maintenance

import groovy.transform.EqualsAndHashCode;

import java.util.Date;

import org.chai.memms.corrective.maintenance.CorrectiveProcess;
import org.chai.memms.inventory.Equipment;
import org.chai.memms.security.User;


/**
 * @author Jean Kahigiso M.
 * 
 */

@EqualsAndHashCode
public abstract class MaintenanceOrder {
	
	Date closedOn
	Date dateCreated
	Date lastUpdated
	
	User addedBy
	User lastModifiedBy
	String description
	
		
	static constraints = {
		
		closedOn nullable: true
		addedBy nullable: false
		
		lastModifiedBy nullable: true, validator:{ val, obj ->
			if(val!=null) return (obj.lastUpdated!=null)
		}
		description nullable:false, blank: false
		
	}

	def beforeUpdate(){
		lastUpdated = new Date();
	}
	
	static mapping = {
		table "memms_maintenance_order_abstract"
		description type:"text"
		tablePerHierarchy false
		version false
		cache true
	}
}
