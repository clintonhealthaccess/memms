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
package org.chai.memms.preventive.maintenance

/**
 * @author Jean Kahigiso M.
 *
 */
@i18nfields.I18nFields
class WorkBasedOrder extends PreventiveOrder{
	
	enum WorkIntervalType{
		
		NONE("none"),
		HOURS("hours"),
		DAY("days"),
		WEEK("weeks"),
		MOTHN("months"),
		YEAR("years")
		
		String messageCode = "preventive.interval.type"
		String name
		WorkIntervalType(String name){this.name=name}
		String getKey(){ return name() }
		
	}
	
	WorkIntervalType occurency
	Integer occurInterval = 1
	
	
	static mapping = {
		table "memms_preventive_order_work_based"
		version false
	}
	
	static constaints = {
		importFrom PreventiveOrder
	}
	
	Integer getPlannedPrevention(){
		//TODO
		return null
	}
	
	@Override
	public String toString() {
		return "WorkBasedOrder [id= "+id+" occurency=" + occurency + "]";
	}
	
	
}
