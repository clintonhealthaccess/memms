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
import org.chai.memms.inventory.EquipmentType.Observation;
import org.chai.memms.inventory.Provider.Type;
import org.chai.memms.inventory.Provider;
import org.chai.memms.spare.part.SparePartType;

/**
 * @author Jean Kahigiso M.
 *
 */
class ProviderService {
	
	static transactional = true
	def languageService;
	def sessionFactory;

	public def searchProvider(Type type,String text,Map<String, String> params){
		text = text.trim();
		def dbFieldDescriptions = 'addressDescriptions_'+languageService.getCurrentLanguagePrefix();
		def criteria = Provider.createCriteria()
		return criteria.list(offset:params.offset,max:params.max,sort:params.sort ?:"id",order: params.order ?:"desc"){
			if(type!=null){
				if(type==Type.SERVICEPROVIDER){
					or{
						eq('type',type)
						eq('type',Type.SUPPLIERANDSERVICEPROVIDER)
					}
				}else if(type==Type.SUPPLIER){
					or{
						eq('type',type)
						eq('type',Type.SUPPLIERANDSERVICEPROVIDER)
					}
				}else{
					or{
						eq('type',type)
						eq('type',Type.BOTH)
					}
				}
			}
			or{

				//for(Type t: this.getEnumeMatcher(text))
					//eq("type",t)
					
				ilike("code","%"+text+"%")
				ilike("phone","%"+text+"%")
				ilike("contactName","%"+text+"%")
				ilike("email","%"+text+"%")
				ilike("street","%"+text+"%")
				ilike("poBox","%"+text+"%")
				ilike(dbFieldDescriptions,"%"+text+"%")
				//ilike("type.name","%"+text+"%")
			}
		}
	}
	
	public static List<Type> getEnumeMatcher(String text){
		List<Type> observations=[]
		if(text!=null && !text.equals(""))
			for(Type ob: Type.values()){
				if(ob.name.toLowerCase().contains(text.toLowerCase()))
					observations.add(ob)
			}
		return observations
	}
}

