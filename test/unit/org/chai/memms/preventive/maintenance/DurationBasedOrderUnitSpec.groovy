package org.chai.memms.preventive.maintenance

import grails.plugin.spock.UnitSpec

import org.chai.memms.TimeDate
import org.chai.memms.preventive.maintenance.DurationBasedOrder
import org.chai.memms.preventive.maintenance.DurationBasedOrder.OccurencyType

import org.joda.time.DateTime

class DurationBasedOrderUnitSpec extends UnitSpec {
	
	def "test occurences between"() {
		setup:
		def now = new Date()
		
		def daily = new DurationBasedOrder(occurency: OccurencyType.DAILY, firstOccurenceOn: new TimeDate(now,"00:00:00"))
		def weekly = new DurationBasedOrder(occurency: OccurencyType.WEEKLY, firstOccurenceOn: new TimeDate(now,"00:00:00"), occurInterval: 1)
		def monthly = new DurationBasedOrder(occurency: OccurencyType.MONTHLY, firstOccurenceOn: new TimeDate(now,"00:00:00"), occurInterval: 1)
		def yearly = new DurationBasedOrder(occurency: OccurencyType.YEARLY, firstOccurenceOn: new TimeDate(now,"00:00:00"), occurInterval: 1)
		
		expect:
		daily.getOccurencesBetween(now - 1, now + 1) == [now, now + 1]
		weekly.getOccurencesBetween(now - 1, now + 1) == [now]
		monthly.getOccurencesBetween(now - 1, now + 1) == [now]
		yearly.getOccurencesBetween(now - 1, now + 1) == [now]
		
		daily.getOccurencesBetween(now - 1, now + 2) == [now, now + 1, now + 2]
		weekly.getOccurencesBetween(now - 1, now + 2) == [now]
		monthly.getOccurencesBetween(now - 1, now + 2) == [now]
		yearly.getOccurencesBetween(now - 1, now + 2) == [now]

		daily.getOccurencesBetween(now - 1, now + 7) == [now, now + 1, now + 2, now + 3, now + 4, now + 5, now + 6, now + 7]
		weekly.getOccurencesBetween(now - 1, now + 7) == [now, now + 7]
		monthly.getOccurencesBetween(now - 1, now + 7) == [now]
		yearly.getOccurencesBetween(now - 1, now + 7) == [now]

		monthly.getOccurencesBetween(now - 1, now + 1.months + 1) == [now, now + 1.month]
		yearly.getOccurencesBetween(now - 1, now + 8) == [now]
		
		yearly.getOccurencesBetween(now - 1, now + 1.years + 1) == [now, now + 1.year]
		
		daily.getOccurencesBetween(now + 1, now + 2) == [now + 1, now + 2]
	}


	def "test next occurence"() {
		setup:
		def now = new Date()
		def nowJoda = new DateTime(now)
		def firstDayOfWeek = new DateTime(now).withDayOfWeek(1).withMillisOfSecond(0)
		def days_of_week = new DurationBasedOrder(occurency: OccurencyType.DAYS_OF_WEEK, firstOccurenceOn: new TimeDate(now - 1, nowJoda.toLocalTime().toString("HH:mm:ss")), occurDaysOfWeek: [1, 3, 5])
		def daily = new DurationBasedOrder(occurency: OccurencyType.DAILY, firstOccurenceOn: new TimeDate(now - 1,nowJoda.toLocalTime().toString("HH:mm:ss")))
		def weekly = new DurationBasedOrder(occurency: OccurencyType.WEEKLY, firstOccurenceOn: new TimeDate(now - 7,nowJoda.toLocalTime().toString("HH:mm:ss")), occurInterval: 1)
		def monthly = new DurationBasedOrder(occurency: OccurencyType.MONTHLY, firstOccurenceOn: new TimeDate(new DateTime(now).minusMonths(1).toDate(),nowJoda.toLocalTime().toString("HH:mm:ss")), occurInterval: 1)
		def yearly = new DurationBasedOrder(occurency: OccurencyType.YEARLY, firstOccurenceOn: new TimeDate(new DateTime(now).minusYears(1).toDate(),nowJoda.toLocalTime().toString("HH:mm:ss")), occurInterval: 1)
		
		expect:
		daily.getNextOccurence() == new DateTime(daily.firstOccurenceOn.timeDate).plusDays(1).toDate() 
		weekly.getNextOccurence() == new DateTime(weekly.firstOccurenceOn.timeDate).plusWeeks(1).toDate()
		monthly.getNextOccurence() == new DateTime(monthly.firstOccurenceOn.timeDate).plusMonths(1).toDate()
		yearly.getNextOccurence() == new DateTime(yearly.firstOccurenceOn.timeDate).plusYears(1).toDate()
		//TODO Find a better way to test this
		new DateTime(days_of_week.getNextOccurence()).getDayOfWeek() in [1,3,5]		
	}
	
	def "test occurence days of week"() {
		setup:
		def firstDayOfWeek = new DateTime(new Date()).withDayOfWeek(1).withMillisOfSecond(0)
		def daily = new DurationBasedOrder(occurency: OccurencyType.DAYS_OF_WEEK, firstOccurenceOn: new TimeDate(firstDayOfWeek.toDate()-1, firstDayOfWeek.toLocalTime().toString("HH:mm:ss")), occurDaysOfWeek: [1, 3, 5])
			
		expect:
		daily.getOccurencesBetween(firstDayOfWeek.toDate(), firstDayOfWeek.toDate() + 8) == [firstDayOfWeek.toDate(), firstDayOfWeek.toDate() + 2, firstDayOfWeek.toDate() + 4 , firstDayOfWeek.toDate() + 1.weeks]
	} 	
	
}