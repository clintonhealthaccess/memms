package org.chai.memms.util

import java.util.Date;

import org.chai.memms.Initializer;
import org.chai.memms.IntegrationTests

class UtilSpec extends IntegrationTests{
	
	def "get minimum date"() {
		setup:
		Date now = new Date()
		Date cleanedMinimumDate
		when:
		cleanedMinimumDate = Utils.getMinDateFromDateTime(now)
		then:
		cleanedMinimumDate <= now
	}
	
	def "get maximum date"() {
		setup:
		def newDate = Initializer.getDate(1, 1, 2010)
		Date cleanedMaximumDate
		when:
		cleanedMaximumDate = Utils.getMaxDateFromDateTime(newDate)
		then:
		cleanedMaximumDate > newDate
	}
	
	def "test isInSameWeek method return true"(){
		when:
		def firstDate =  Utils.getDate( 8, 11, 2012)
		def secondDate =  Utils.getDate( 9, 11, 2012)
		Boolean result = Utils.isInSameWeek(firstDate,secondDate)
		then:
		result == true
	}
	
	def "test isInSameWeek method return false"(){
		when:
		def firstDate =  Utils.getDate( 2, 11, 2012)
		def secondDate =  Utils.getDate( 9, 11, 2012)
		Boolean result = Utils.isInSameWeek(firstDate,secondDate)
		then:
		result == false
	}
	
	def "test isOnSameDay method return true"(){
		when:
		def firstDate =  Utils.getDate( 8, 11, 2012)
		def secondDate =  Utils.getDate( 8, 11, 2012)
		secondDate = Utils.getMaxDateFromDateTime(secondDate)
		Boolean result = Utils.isOnSameDay(firstDate,secondDate)
		then:
		result == true
	}
	
	def "test isOnSameDay method return false"(){
		when:
		def firstDate =  Utils.getDate( 8, 11, 2012)
		def secondDate =  Utils.getDate( 9, 11, 2012)
		secondDate = Utils.getMaxDateFromDateTime(secondDate)
		Boolean result = Utils.isOnSameDay(firstDate,secondDate)
		then:
		result == false
	}
}
