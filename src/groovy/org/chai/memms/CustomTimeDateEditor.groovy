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
package org.chai.memms

import java.beans.PropertyEditorSupport;
import java.util.List;
import java.util.Map;

import org.codehaus.groovy.grails.web.binding.StructuredPropertyEditor;
import org.springframework.util.NumberUtils;
import org.springframework.util.StringUtils;
import org.chai.memms.util.Utils


/**
 * @author Jean Kahigiso M.
 *
 */
class CustomTimeDateEditor extends PropertyEditorSupport implements StructuredPropertyEditor{
	
	@Override
	public List getOptionalFields() {
		return new ArrayList();
	}

	@Override
	public List getRequiredFields() {
		List optionalFilds =  new ArrayList();
		optionalFilds.add("time")
		optionalFilds.add("date")
		return optionalFilds;
		
	}
	
	@Override
	public Object assemble(Class type, Map fieldValues) throws IllegalArgumentException {
		String time
		Date date
		
		if(log.isDebugEnabled()) log.debug("TimeDate fieldValues to be bind "+fieldValues)
		
		String timeField = (String) fieldValues.get("time");
		String dateField =  (String) fieldValues.get("date");
		
		if(timeField && !timeField.isEmpty()){
			try{ 
				Utils.parseTime(timeField)
				time = timeField.trim()
				} catch (Exception nfe){
				if(log.isDebugEnabled()) log.debug("exception while parsing timeField: TimeDate:time",nfe)
				throw new IllegalArgumentException("Time has to be of the HH:mm:ss format",nfe)
			}
		}
		
		if(dateField && !dateField.isEmpty()){
			try{
				date = Utils.parseDate(dateField)
			} catch (Exception nfe){
				if(log.isDebugEnabled()) log.debug("exception while parsing date: TimeDate:date",nfe)
				throw new IllegalArgumentException("Time has to be of the dd/MM/yyyy format",nfe)
			}
		}
		
		if(date==null || time==null || time.isEmpty()) {
			return null
		}
		if(log.isDebugEnabled()) log.debug("date and time before creating timeDate. Date: "+date+" Time: "+time)
		TimeDate timeDate = new TimeDate(date,time);
		if(log.isDebugEnabled()) log.debug("Created timeDate: "+timeDate)
		return timeDate
	}
}
