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

import org.joda.time.DateTime

/**
 * @author Jean Kahigiso M.
 *
 */
class DurationBasedOrder extends PreventiveOrder {
	
	enum OccurencyType{
		
		NONE("none"),
		DAYS_OF_WEEK("days_of_week"),
		DAILY("daily"),
		WEEKLY("weekly"),
		MONTHLY("monthly"),
		YEARLY("yearly"),
		
		String messageCode = "preventive.occurance.type"
		String name
		OccurencyType(String name){this.name = name}
		String getKey(){ return name() }
	}
	
	OccurencyType occurency
	
	static hasMany = [occurDaysOfWeek: Integer]
	
	static mapping = {
		table "memms_preventive_order_duration_based"
		version false
	}
	
	static constraints = {
		
		importFrom PreventiveOrder, excludes:["closedOn"]
		occurency nullable: false, inList:[OccurencyType.DAYS_OF_WEEK,OccurencyType.DAILY,OccurencyType.WEEKLY,OccurencyType.MONTHLY,OccurencyType.YEARLY]
		closedOn nullable: true
		occurDaysOfWeek validator :{val, obj ->
			if (obj.occurency == OccurencyType.DAYS_OF_WEEK) {
				if(log.isDebugEnabled()) log.debug("checking list size of ${val}")
				return val != null && val.size() > 0
			}
		}
		
	}

	def updateOccurDaysOfWeek(){
		if(!occurency.equals(OccurencyType.DAYS_OF_WEEK)){
			occurDaysOfWeek?.clear()
		}
	}

	def beforeUpdate(){
		updateOccurDaysOfWeek()
	}
	
	def getOccurCount() {
		return getOccurencesBetween(firstOccurenceOn.timeDate, new Date()).size()
	}
	
	/**
	 * Returns a list of all dates when this duration based order will be 
	 * occurring between the indicated period of time
	 */ 
	def getOccurencesBetween(Date start, Date end) {
		if(log.isDebugEnabled()) log.debug("getOccurencesBetween(${start}, ${end})")
		
		def dates = []
		def occurence = firstOccurenceOn.timeDate
		
		// let's loop until we reach the end of the range
		while (occurence.before(end) || occurence.equals(end)) {
			DateTime dateTime = new DateTime(occurence)
			
			// if we are after start, we add to the dates
			if (occurence.after(start) || occurence.equals(start)) {
				if (occurency != OccurencyType.DAYS_OF_WEEK || dateTime.getDayOfWeek() in occurDaysOfWeek) {
					dates << occurence
				}
			}
			
			switch(occurency) {
				case OccurencyType.DAYS_OF_WEEK:
					def sortedDaysOfWeek = occurDaysOfWeek.sort()
					if(log.isDebugEnabled()) log.debug("found days of week: ${sortedDaysOfWeek}")
					
					for (def dayOfWeek : sortedDaysOfWeek) {
						def newDateTime = dateTime.withDayOfWeek(dayOfWeek)
						if(log.isDebugEnabled()) log.debug("comparing new date ${newDateTime} with ${dateTime}")
						
						if (newDateTime.toDate().after(dateTime.toDate())) {
							if(log.isDebugEnabled()) log.debug("found new date ${newDateTime}")
							occurence = newDateTime.toDate()
							break;
						}
					}
					if (dateTime.toDate().equals(occurence)) {
						occurence = dateTime.plusWeeks(1).withDayOfWeek(sortedDaysOfWeek[0]).toDate()
					}
				break
				case OccurencyType.DAILY:
					occurence = dateTime.plusDays(occurInterval).toDate()
				break
				case OccurencyType.WEEKLY:
					occurence = dateTime.plusWeeks(occurInterval).toDate()
				break
				case OccurencyType.MONTHLY:
					occurence = dateTime.plusMonths(occurInterval).toDate()
				break
				case OccurencyType.YEARLY:
					occurence = dateTime.plusYears(occurInterval).toDate()
				break
				case OccurencyType.NONE:
					// this will break the while loop and exit
					occurence = end + 1;
				break
			}
		}

		if(log.isDebugEnabled()) log.debug("getOccurencesBetween(...)=${dates}")
		return dates
	}

	def getNextOccurence(){
		def dates = []
		Date now = new Date();
		DateTime yesterday = new DateTime(now-1) 
		Date nextOccurrence = null
		
		switch(occurency) {

			case OccurencyType.DAILY:
				def oneDayLater = yesterday.plusDays(2)
				dates = getOccurencesBetween(yesterday.toDate(),oneDayLater.toDate())
			break
			//As days_of_week fall in a week period
			case OccurencyType.DAYS_OF_WEEK:
			case OccurencyType.WEEKLY:
				def oneWeekLater = yesterday.plusWeeks(1)
				dates = getOccurencesBetween(yesterday.toDate(),oneWeekLater.toDate())
			break
			case OccurencyType.MONTHLY:
				def oneMonthLater = yesterday.plusMonths(1)
				dates = getOccurencesBetween(yesterday.toDate(),oneMonthLater.toDate())
			break
			case OccurencyType.YEARLY:
				def oneYearLater = yesterday.plusYears(1)
				dates = getOccurencesBetween(yesterday.toDate(),oneYearLater.toDate())
			break
			case OccurencyType.NONE:
			break
		}
		//Return the first date as it is next occurence date
		if(!dates.isEmpty()) nextOccurrence = dates[0]
		return nextOccurrence
	}


	@Override
	public String toString() {
		return "DurationBasedOrder [id= "+id+" occurency=" + occurency + "]";
	}	
	
}
