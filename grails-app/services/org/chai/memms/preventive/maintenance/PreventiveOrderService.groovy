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
package org.chai.memms.preventive.maintenance

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.chai.location.DataLocation;
import org.chai.location.Location;
import org.chai.memms.corrective.maintenance.WorkOrder;
import org.chai.memms.inventory.Equipment;
import org.chai.memms.preventive.maintenance.DurationBasedOrder.OccurencyType;
import org.chai.memms.util.Utils;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Months;
import org.joda.time.Weeks;
import org.joda.time.Years;
import org.springframework.transaction.annotation.Transactional;
import static org.joda.time.DateTimeConstants.SUNDAY;
import static org.joda.time.DateTimeConstants.MONDAY;
import static org.joda.time.DateTimeConstants.TUESDAY;
import static org.joda.time.DateTimeConstants.WEDNESDAY;
import static org.joda.time.DateTimeConstants.THURSDAY;
import static org.joda.time.DateTimeConstants.FRIDAY;
import static org.joda.time.DateTimeConstants.SATURDAY;

/**
 * @author Jean Kahigiso M.
 *
 */
class PreventiveOrderService {
	
	def equipmentService
	def grailsApplication
	
	@Transactional(readOnly = true)
	public def findOccurrencesInRange(PreventiveOrder order, Date rangeStart, Date rangeEnd) {
		def dates = []
		Date currentDate
		if (order.isRecurring) {
			currentDate = findNextOccurrence(order, rangeStart)
			while (currentDate && currentDate < rangeEnd) {
				dates.add(currentDate)
				Date nextMinute = new DateTime(currentDate).plusMinutes(1).toDate()
				currentDate = findNextOccurrence(order, nextMinute)
			}
		}else  // One time (non-recurring) order
			if (order.openOn.timeDate >= rangeStart && order.closedOn <= rangeEnd) dates.add(order.openOn.timeDate)
		return dates
	}
	
	// For repeating order get next occurrence after the specified date
	@Transactional(readOnly = true)
	Date findNextOccurrence(PreventiveOrder order, Date afterDate) {
		Date nextOccurrence

		if (!order.isRecurring) // non-repeating order
			nextOccurrence = null
		else if (order.closedOn && afterDate > order.closedOn) // Order is already over
			nextOccurrence = null
		else if (afterDate < order.openOn.timeDate) // First occurrence
			if (order.occurency == OccurencyType.WEEKLY && !(isOnRecurringDay(order, order.openOn.timeDate))) {
				Date nextDay = new DateTime(order.openOn.timeDate).plusDays(1).toDate()
				nextOccurrence = findNextOccurrence(order, nextDay)
			}else nextOccurrence = order.openOn.timeDate
		else {
			switch (order.occurency) {
				case OccurencyType.DAILY:
					nextOccurrence = findNextDailyOccurrence(order, afterDate)
					break
				case OccurencyType.WEEKLY:
					nextOccurrence = findNextWeeklyOccurrence(order, afterDate)
					break
				case OccurencyType.MONTHLY:
					nextOccurrence = findNextMonthlyOccurrence(order, afterDate)
					break
				case OccurencyType.YEARLY:
					nextOccurrence = findNextYearlyOccurrence(order, afterDate)
					break
			}
		}
		// Next occurrence happens after closedOn date
		if (order.closedOn && order.closedOn <= nextOccurrence) nextOccurrence = null
		return nextOccurrence
	}
	
	private Date findNextDailyOccurrence(PreventiveOrder order, Date afterDate) {
		DateTime nextOccurrence = new DateTime(order.openOn.timeDate)

		Integer daysBeforeDate = Days.daysBetween(new DateTime(order.openOn.timeDate), new DateTime(afterDate)).getDays()
		Integer occurrencesBeforeDate = Math.floor(daysBeforeDate / order.occurInterval)
		nextOccurrence = nextOccurrence.plusDays((occurrencesBeforeDate + 1) * order.occurInterval)

		return nextOccurrence.toDate()
	}
	
	private Date findNextWeeklyOccurrence(PreventiveOrder order, Date afterDate) {
		Boolean occurrenceFound = false
		Integer weeksBeforeDate = Weeks.weeksBetween(new DateTime(order.openOn.timeDate), new DateTime(afterDate)).getWeeks()
		Integer weekOccurrencesBeforeDate = Math.floor(weeksBeforeDate / order.occurInterval)

		DateTime lastOccurrence = new DateTime(order.openOn.timeDate)
		lastOccurrence = lastOccurrence.plusWeeks(weekOccurrencesBeforeDate * order.occurInterval)
		lastOccurrence = lastOccurrence.withDayOfWeek(MONDAY)

		DateTime nextOccurrence
		if (Utils.isInSameWeek(lastOccurrence.toDate(), afterDate)) nextOccurrence = lastOccurrence.plusDays(1)
		else nextOccurrence = lastOccurrence.plusWeeks(order.occurInterval)

		while (!occurrenceFound) {
			if (nextOccurrence.toDate() > afterDate && isOnRecurringDay(order, nextOccurrence.toDate()))
				occurrenceFound = true
			else {
				// we're about to pass into the next week
				if (nextOccurrence.dayOfWeek() == SUNDAY) 
					nextOccurrence = nextOccurrence.withDayOfWeek(MONDAY).plusWeeks(order.occurInterval)
				else nextOccurrence = nextOccurrence.plusDays(1)
			}
		}

		return nextOccurrence.toDate()
	}
	
	private Date findNextMonthlyOccurrence(PreventiveOrder order, Date afterDate) {
		DateTime nextOccurrence = new DateTime(order.openOn.timeDate)
		Integer monthsBeforeDate = Months.monthsBetween(new DateTime(order.openOn.timeDate), new DateTime(afterDate)).getMonths()
		Integer occurrencesBeforeDate = Math.floor(monthsBeforeDate / order.occurInterval)
		nextOccurrence = nextOccurrence.plusMonths((occurrencesBeforeDate + 1) * order.occurInterval)
		return nextOccurrence.toDate()
	}
	
	private Date findNextYearlyOccurrence(PreventiveOrder order, Date afterDate) {
		DateTime nextOccurrence = new DateTime(order.openOn.timeDate)
		Integer yearsBeforeDate = Years.yearsBetween(new DateTime(order.openOn.timeDate), new DateTime(afterDate)).getYears()
		Integer occurrencesBeforeDate = Math.floor(yearsBeforeDate / order.occurInterval)
		nextOccurrence = nextOccurrence.plusYears((occurrencesBeforeDate + 1) * order.occurInterval)
		return nextOccurrence.toDate()
	}

	private boolean isOnRecurringDay(PreventiveOrder order, Date date) {
		Integer day = new DateTime(date).getDayOfWeek()
		return order.occurDaysOfWeek.find{it == day}
	}
		
}
