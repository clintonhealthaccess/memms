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
import org.chai.memms.util.Utils;
import java.util.Date;
import groovy.time.TimeCategory;

/**
 * This class expect the following date "dd/MM/yyyy HH:mm:ss";
 *
 * @author Jean Kahigiso M.
 *
 */
class TimeDate {

	Date timeDate
	
	public TimeDate(){}
	public TimeDate(Date startDate,String startTime){
		if(startDate!=null && startTime!=null && this.hasTimeFormat(startTime))
			this.buildTimeDate(startDate,startTime)
	}
	
	static constraints = {
		timeDate nullable:false
	}
	
	static mapping = {
		version false
	}


	static transients =["date","time","formatDate"]
	
	public String getDate(){
		def split = this.getFormatDate().split(" ")
		return split[0]
	}

	public String getTime(){
		def split = this.getFormatDate().split(" ")
		return split[1]
	}

	public String getFormatDate(){
		return Utils.formatDateWithTime(timeDate)
	}

	private buildTimeDate(Date sentDate,String sentTime){
		Integer.metaClass.mixin TimeCategory
		Date.metaClass.mixin TimeCategory
		Date time = sentDate.clearTime()
		sentTime.trim()
		def hourMinSecond =  sentTime.split(":")
		timeDate = time + Integer.parseInt(hourMinSecond[0]).hours + Integer.parseInt(hourMinSecond[1]).minutes + Integer.parseInt(hourMinSecond[2]).seconds
		return timeDate
	}

	private hasTimeFormat(String sentTime){
		if(sentTime.isEmpty()) return false
		if(!(sentTime.size() in 5..8)) return false

		try{ Utils.parseTime(sentTime);
		}catch(Exception ex){ return false; }
		//Avoid negative value
		def split = sentTime.split(":")
		if(!(split[0].size() in 1..2) || Integer.parseInt(split[0])<0) return false
		if(!(split[1].size() in 1..2) || Integer.parseInt(split[1])<0) return false
		if(!(split[2].size() in 1..2) || Integer.parseInt(split[2])<0) return false
		return true;
	}
	
	@Override
	public String toString() {
		return "TimeDate [timeDate= " + timeDate + "]";
	}
	
}
