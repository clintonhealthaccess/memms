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
/**
 * @author Jean Kahigiso M.
 *
 */

import grails.plugin.spock.UnitSpec;
import org.chai.memms.TimeSpend

class TimeSpendUtilSpec extends UnitSpec {

	def "test getHours and getMinutes"() {
		when:
		TimeSpend timeSpend = new TimeSpend(3,8)
		then:
		timeSpend.hours == 3
		timeSpend.minutes == 8
		timeSpend.numberOfMinutes == 188
		
	}
	
	def "test numberOfMinutes can be null"() {
		when:
		TimeSpend timeSpend = new TimeSpend(null,null)
		then:
		timeSpend.hours == null
		timeSpend.minutes == null
		timeSpend.numberOfMinutes == null
	}
	
	def "test can set minutes only"() {
		when:
		TimeSpend timeSpend = new TimeSpend(null,70)
		then:
		timeSpend.hours == 1
		timeSpend.minutes == 10
		timeSpend.numberOfMinutes == 70
	}
	
	def "test can set hours only"() {
		
		when:
		TimeSpend timeSpend = new TimeSpend(5,null)
		then:
		timeSpend.hours == 5
		timeSpend.minutes == null
		timeSpend.numberOfMinutes == 300
		
	}
	def "test can set minutes only when less than a hours"() {
		
		when:
		TimeSpend timeSpend = new TimeSpend(null,35)
		then:
		timeSpend.hours == null
		timeSpend.minutes == 35
		timeSpend.numberOfMinutes == 35		
	}
	
	def "test can set minutes and hours as 0"() {
		
		when:
		TimeSpend timeSpend = new TimeSpend(0,0)
		then:
		timeSpend.hours == null
		timeSpend.minutes == null
		timeSpend.numberOfMinutes == 0
	}
	
	def "test can set minutes  as 0"() {
		
		when:
		TimeSpend timeSpend = new TimeSpend(3,0)
		then:
		timeSpend.hours == 3
		timeSpend.minutes == null
		timeSpend.numberOfMinutes == 180
	}
	
	def "test can set hours  as 0 minutes > an hours"() {
		
		when:
		TimeSpend timeSpend = new TimeSpend(0,61)
		then:
		timeSpend.hours == 1
		timeSpend.minutes == 1
		timeSpend.numberOfMinutes == 61
	}
	
	def "test can set hours as 0 minutes < an hours"() {
		
		when:
		TimeSpend timeSpend = new TimeSpend(0,50)
		then:
		timeSpend.hours == null
		timeSpend.minutes == 50
		timeSpend.numberOfMinutes == 50
	}
	
}
	
