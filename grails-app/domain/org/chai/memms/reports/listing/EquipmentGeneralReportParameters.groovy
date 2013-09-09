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
package org.chai.memms.reports.listing

import groovy.transform.EqualsAndHashCode;

import java.util.Date;

import org.chai.memms.inventory.Department;
import org.chai.memms.inventory.EquipmentType;
import org.chai.memms.security.User;
import org.chai.location.DataLocation;
import org.chai.memms.util.Utils.ReportType;
import org.chai.memms.util.Utils.ReportSubType;

/**
 * @author Aphrodice Rwagaju
 *
 */
@EqualsAndHashCode(includes='id')
class EquipmentGeneralReportParameters {

	String reportName
	ReportType reportType
	ReportSubType reportSubType
	Date dateCreated
	Double lowerLimitCost
	Double upperLimitCost
	String currency
	Date fromDate
	Date toDate
	User savedBy
	boolean noCostSpecified
	String displayOptions
	
	static hasMany = [dataLocations:DataLocation,departments:Department,equipmentTypes:EquipmentType]
	static constraints = {
		dataLocations nullable: true, blank: true
		departments nullable: true, blank: true
		equipmentTypes nullable: true, blank: true
		lowerLimitCost nullable: true, blank: true
		upperLimitCost nullable: true, blank: true
		currency nullable: true, blank: true
		fromDate nullable: true, blank: true
		toDate nullable: true, blank: true
		savedBy nullable: false
		noCostSpecified nullable: false
		displayOptions nullable: false
	}

	static mapping = {
		table "memms_equipment_report_listing"
		version false
		dataLocations joinTable:[name:"memms_equipment_data_location_report_listing",key:"data_location_id",column:"report_listing_id"]
		departments joinTable:[name:"memms_equipment_department_report_listing",key:"department_id",column:"report_listing_id"]
		equipmentTypes joinTable:[name:"memms_equipment_type_report_listing",key:"equipment_type_id",column:"report_listing_id"]
	}
}
