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
import java.util.List;
import java.util.Map;

import org.chai.memms.inventory.EquipmentType.Observation
import org.chai.memms.inventory.Provider.Type;
import org.chai.memms.inventory.EquipmentType;
import org.chai.memms.util.Utils;
/**
 * @author Jean Kahigiso M.
 *
 */
class EquipmentTypeService {

	static transactional = true
	def languageService;
		
	public def searchEquipmentType(String text,Observation observation, Map<String, String> params) {
		def dbFieldName = 'names_'+languageService.getCurrentLanguagePrefix();
		def dbFieldDescritpion = 'descriptions_'+languageService.getCurrentLanguagePrefix();
		def criteria = EquipmentType.createCriteria()
		return criteria.list(offset:params.offset,max:params.max,sort:params.sort ?:"id",order: params.order ?:"desc"){
			if(observation!=null)
				eq("observation",observation)
			or
			{
				if(observation==null){
					for(Observation obs: this.getEnumeMatcher(text))
						eq("observation",obs)
				}
				ilike("code","%"+text+"%")
				ilike(dbFieldName,"%"+text+"%")
				ilike(dbFieldDescritpion,"%"+text+"%")
			}
			
		}
	}
	
	public static List<Observation> getEnumeMatcher(String text){
		List<Observation> observations=[]
		if(text!=null && !text.equals(""))
			for(Observation ob: Observation.values()){
				if(ob.name.toLowerCase().contains(text.toLowerCase()))
					observations.add(ob)
			}
		return observations
	}
	
	def importToBoolean = {
		if(it.compareToIgnoreCase("no")) return false
		else if(it.compareToIgnoreCase("yes")) return true
		else return null
	}
	
	def importToObservation = {
		if (it == null) return Observation.USEDINMEMMS
		else if(it?.compareToIgnoreCase("Retired concept")) return Observation.RETIRED
		else if(it?.compareToIgnoreCase("Too detailed")) return Observation.TOODETAILED
		else if(it?.compareToIgnoreCase("Outside scope")) return Observation.RETIRED
		else if(!(it?.trim())) return Observation.USEDINMEMMS
		else return Observation.USEDINMEMMS
	}
}
