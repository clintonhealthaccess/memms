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
import org.chai.memms.TimeDate
import groovy.time.TimeCategory;
import org.chai.memms.util.Utils;

/**
 * @author Jean Kahigiso M.
 *
 */
class TimeDateUtilSpec extends UnitSpec {
	def now = new Date()
	def groovyTimeSetUp(){
		Integer.metaClass.mixin TimeCategory
		Date.metaClass.mixin TimeCategory
	}

	def "test if the structure works with correct data"() {
		setup:
		groovyTimeSetUp()
		when:
		def toDayDate = now.clearTime()
		TimeDate customTime = new TimeDate(toDayDate,"18:22:01")
		then:
		customTime.timeDate.hours == 18
		customTime.timeDate.minutes == 22
		customTime.timeDate.seconds == 1
		customTime.timeDate == toDayDate + 18.hours + 22.minutes + 1.seconds
		
	}

	def "test date cannot be null"() {
		setup:
		groovyTimeSetUp()
		when:
		def toDayDate = now.clearTime()
		TimeDate customTime = new TimeDate(null,"18:22:01")
		then:
		customTime.timeDate == null
	}

	def "test time cannot be null"() {
		setup:
		groovyTimeSetUp()
		when:
		def toDayDate = now.clearTime()
		TimeDate customTime = new TimeDate(toDayDate,null)
		then:
		customTime.timeDate == null
	}

	def "test time cannot be an empty string"() {
		setup:
		groovyTimeSetUp()
		when:
		def toDayDate = now.clearTime()
		TimeDate customTime = new TimeDate(toDayDate,"")
		then:
		customTime.timeDate == null
	}

	

	def "test time format (HH:MM:SS) has to be respected cannot be negative"() {
		setup:
		groovyTimeSetUp()
		def toDayDate = now.clearTime()
		when:
		TimeDate customTime = new TimeDate(toDayDate,"12:10:0122")
		then:
		customTime.timeDate == null
		when:
		TimeDate customTimeOne = new TimeDate(toDayDate,"12:-10:0")
		then:
		customTimeOne.timeDate == null
		when:
		TimeDate customTimeTwo = new TimeDate(toDayDate,"-0:10:00")
		then:
		customTimeTwo.timeDate != null
		when:
		TimeDate customTimeThree = new TimeDate(toDayDate,"00:10:-2")
		then:
		customTimeThree.timeDate == null
		when:
		TimeDate customTimeFour = new TimeDate(toDayDate,"-23:10:2")
		then:
		customTimeFour.timeDate == null
	}

	def "test time interval maximum"() {
		setup:
		groovyTimeSetUp()
		def toDayDate = now.clearTime()
		when:
		TimeDate customTime = new TimeDate(toDayDate,"23:59:59")
		then:
		customTime.timeDate != null
	}

	def "test time interval for minimum"() {
		setup:
		groovyTimeSetUp()
		def toDayDate = now.clearTime()
		when:
		TimeDate customTime = new TimeDate(toDayDate,"00:00:00")
		then:
		customTime.timeDate != null
	}

	def "test getDate() return date and getTime() return string representing time"() {
		setup:
		groovyTimeSetUp()
		def toDayDate = now.clearTime()
		when:
		TimeDate customTime = new TimeDate(toDayDate,"17:10:22")
		def formattedDate = Utils.formatDateWithTime(customTime.timeDate)
		then:
		customTime.date == formattedDate.split(" ")[0]
		customTime.time == formattedDate.split(" ")[1]
	}

	def "test time interval for hours"() {
		setup:
		groovyTimeSetUp()
		def toDayDate = now.clearTime()
		when:
		TimeDate customTime = new TimeDate(toDayDate,"00:23:52")
		then:
		customTime.timeDate != null
	}

	def "test time interval for minutes"() {
		setup:
		groovyTimeSetUp()
		def toDayDate = now.clearTime()
		when:
		TimeDate customTime = new TimeDate(toDayDate,"23:00:51")
		then:
		customTime.timeDate != null
	}
	def "test time interval seconds"() {
		setup:
		groovyTimeSetUp()
		def toDayDate = now.clearTime()
		when:
		TimeDate customTime = new TimeDate(toDayDate,"22:59:00")
		then:
		customTime.timeDate != null
	}
	
	def "test hasFormatTime()"() {
		setup:
		groovyTimeSetUp()
		def toDayDate = now.clearTime()
		when:
		TimeDate customTime = new TimeDate(toDayDate,"10:00:00")
		then:
		customTime.timeDate != null
		when:
		TimeDate customTimeOne = new TimeDate(toDayDate,"0:0:0")
		then:
		customTimeOne.timeDate !=null
		when:
		TimeDate customTimeTwo = new TimeDate(toDayDate,"0:00:00")
		then:
		customTimeTwo.timeDate !=null
		when:
		TimeDate customTimeThree = new TimeDate(toDayDate,"00:0:00")
		then:
		customTimeThree.timeDate !=null
		when:
		TimeDate customTimeFour = new TimeDate(toDayDate,"00:00:0")
		then:
		customTimeFour.timeDate !=null
		when:
		TimeDate customTimeSeven = new TimeDate(toDayDate,"01:00:00")
		then:
		customTimeSeven.timeDate != null
	}

	def "test unparsable format"(){
		setup:
		groovyTimeSetUp()
		def toDayDate = now.clearTime()
		when:
		TimeDate customTimeFive = new TimeDate(toDayDate,"00:00:")
		then:
		customTimeFive != null
		customTimeFive.timeDate == null
		when:
		TimeDate customTimeSix = new TimeDate(toDayDate,"00:0000:")
		then:
		customTimeFive != null
		customTimeSix.timeDate == null
	}

	
	
}
	
