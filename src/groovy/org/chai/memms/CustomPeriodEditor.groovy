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


/**
 * @author Jean Kahigiso M.
 *
 */
class CustomPeriodEditor extends PropertyEditorSupport implements StructuredPropertyEditor{
	private Integer months
	private Integer years
	
	@Override
	public List getOptionalFields() {
		List optionalFilds =  new ArrayList();
		optionalFilds.add("months")
		optionalFilds.add("years")
		return optionalFilds;
	}

	@Override
	public List getRequiredFields() {
		return new ArrayList();
	}
	
	@Override
	public Object assemble(Class type, Map fieldValues) throws IllegalArgumentException {
		if(log.isDebugEnabled()) log.debug("period fieldValues to be bind "+fieldValues)
		def month = fieldValues.get("months");
		def year = fieldValues.get("years");
		try{ 
			months = Integer.parseInt(month[0]) 
			} catch (Exception nfe){
			throw new IllegalArgumentException("Months has to be a number",nfe)
		}
		try{
			years = Integer.parseInt(year[0])
		} catch (Exception nfe){
			throw new IllegalArgumentException("Years has to be a number",nfe)
		}
		return new Period(years,months);
	}
}
