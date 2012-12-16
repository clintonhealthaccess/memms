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


import grails.plugin.spock.UnitSpec;
/**
 * @author Jean Kahigiso M.
 *
 */
class PeriodUtilSpec extends UnitSpec {

	def "test getYears and getMonths"() {
		when:
		Period period = new Period(3,8)
		then:
		period.years == 3
		period.months == 8
		period.numberOfMonths == 44
		
	}
	
	def "test numberOfMonths can be null"() {
		when:
		Period period = new Period(null,null)
		then:
		period.years == null
		period.months == null
		period.numberOfMonths == null
		
	}
	
	def "test can set months only"() {
		when:
		Period period = new Period(null,25)
		then:
		period.years == 2
		period.months == 1
		period.numberOfMonths == 25
		
	}
	
	def "test can set years only"() {
		when:
		Period period = new Period(5,null)
		then:
		period.years == 5
		period.months == null
		period.numberOfMonths == 60
		
	}
	def "test can set months only when less than a year"() {
		when:
		Period period = new Period(null,3)
		then:
		period.years == null
		period.months == 3
		period.numberOfMonths == 3
		
	}
	def "test can set months and years to 0"() {
		when:
		Period period = new Period(0,0)
		then:
		period.years == null
		period.months == null
		period.numberOfMonths == 0
		
	}
	
	def "test can set months to 0"() {
		when:
		Period period = new Period(2,0)
		then:
		period.years == 2
		period.months == null
		period.numberOfMonths == 24
		
	}
	
	def "test can set years to 0 and months < a year"() {
		when:
		Period period = new Period(0,9)
		then:
		period.years == null
		period.months == 9
		period.numberOfMonths == 9
		
	}
	
	def "test can set years to 0 and months > a year"() {
		when:
		Period period = new Period(0,13)
		then:
		period.years == 1
		period.months == 1
		period.numberOfMonths == 13
		
	}
	
}
	
