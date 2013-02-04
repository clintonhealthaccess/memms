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
	
	def "test occurence days of week"() {
		setup:
		def firstDayOfWeek = new DateTime(new Date()).withDayOfWeek(1).toDate()
		def daily = new DurationBasedOrder(occurency: OccurencyType.DAYS_OF_WEEK, firstOccurenceOn: new TimeDate(firstDayOfWeek-1, "00:00:00"), occurDaysOfWeek: [1, 3, 5])
			
		expect:
		daily.getOccurencesBetween(firstDayOfWeek, firstDayOfWeek + 7) == [firstDayOfWeek, firstDayOfWeek + 2, firstDayOfWeek + 4 , firstDayOfWeek + 1.weeks]
	}
	
}