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

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.chai.memms.spare.part.SparePart;
import org.chai.memms.spare.part.SparePartStatus.StatusOfSparePart;
import org.chai.memms.security.User;
import org.chai.memms.util.Utils;
import org.grails.datastore.mapping.query.api.Criteria;

/**
 * @author Aphrodice Rwagaju
 *
 */
class SparePartStatusService {
	static transactional = true
	
	List<SparePart> getSparePartStatusBySparePart(SparePart sparePart, Map<String,String> params){
		def criteria = SparePartStatus.createCriteria()
		return criteria.list(offset:params.offset,max:params.max,sort:params.sort ?:"dateCreated",order: params.order ?:"desc"){
			eq("sparePart",sparePart)
		}
	}
	
	SparePart createSparePartStatus(User changedBy,StatusOfSparePart value, SparePart sparePart,Date dateOfEvent, Map<String,String> reasons){
		def status = new SparePartStatus(dateOfEvent:dateOfEvent,changedBy:changedBy,statusOfSparePart:value)
		Utils.setLocaleValueInMap(status,reasons,"Reasons")
		sparePart.statusOfSparePart = value
		if(sparePart.id){
			//When updating the sparePart
			sparePart.lastModified = changedBy
		}
		sparePart.addToStatus(status)
		if(!sparePart.statusOfSparePart.equals(StatusOfSparePart.DISPOSED))
			sparePart.save(failOnError:true, flush:true)
		return sparePart
	}

}