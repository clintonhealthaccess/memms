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

package org.chai.memms.util

class ImportExportConstant {
	public final static String CSV_FILE_EXTENSION = ".csv";
	public final static String TRUE = "TRUE";
	public final static String FALSE = "FALSE";
	
	//For use in imports
	public final static Integer NUMBER_OF_LINES_TO_IMPORT = 100;
	
	public final static String SUPPLIER_CODE = "Supplier"
	public final static String SUPPLIER_DATE = "Supply date"
	public final static String SUPPLIER_CONTACT_NAME = "Supplier name"
	
	public final static String SERVICEPROVIDER_CODE = "Service Code"
	public final static String SERVICEPROVIDER_CONTACT_NAME= "Service Provider"
	public final static String SERVICEPROVIDER_DATE = "Contract Start Date"
	public final static String SERVICEPROVIDER_PERIOD = "Contract Start Number of Months"
	
	public final static String LOCATION_CODE = "Facility code"
	public final static String LOCATION_NAME_EN = "Facility name_en"
	public final static String LOCATION_NAME_FR = "Facility name_fr"
	
	public final static String DEPARTMENT_CODE = "Department"
	public final static String DEPARTMENT_NAME_EN = "Department_name_en"
	public final static String DEPARTMENT_NAME_FR = "Department_name_fr"
	
	public final static String ROOM = "Room"
	
	//Importing/exporting equipment types
	public final static String DEVICE_CODE = "device_code"
	public final static String DEVICE_NAME_EN = "device_name_en"
	public final static String DEVICE_NAME_FR = "device_name_fr"
	public final static String DEVICE_INCLUDED_IN_MEMMS = "include_in_MEMMS"
	public final static String DEVICE_DESCRIPTION_EN = "device_description_en"
	public final static String DEVICE_DESCRIPTION_FR = "device_description_fr"
	public final static String DEVICE_OBSERVATION = "observations"
	
	//Importing/exporting equipment
	public final static String EQUIPMENT_SERIAL_NUMBER = "Serial number"
	public final static String EQUIPMENT_TYPE = "Type"
	public final static String EQUIPMENT_MODEL = "Model"
	public final static String MANUFACTURER_CODE = "Manufacturer code"
	public final static String MANUFACTURER_CONTACT_NAME = "Manufacturer name"
	public final static String EQUIPMENT_MANUFACTURE_DATE = "Manufacture date"
	public final static String EQUIPMENT_PURCHASE_COST = "Cost"
	public final static String EQUIPMENT_PURCHASE_COST_CURRENCY = "Currency"
	public final static String EQUIPMENT_STATUS = "Status"
	public final static String EQUIPMENT_DONOR= "Donor" 
	public final static String EQUIPMENT_OBSOLETE = "Obsolete"
	public final static String EQUIPMENT_WARRANTY_START = "Warranty start"
	public final static String EQUIPMENT_WARRANTY_END = "Warranty period (Months)"
	public final static String EQUIPMENT_ID = "Id"
	//Importing/exporting Spare part
	public final static String SPARE_PART_TYPE = "Type"
	public final static String SPARE_PART_MANUFACTURE_DATE = "Manufacture date"
	public final static String SPARE_PART_PURCHASE_COST = "Cost"
	public final static String SPARE_PART_PURCHASE_COST_CURRENCY = "Currency"
	public final static String SPARE_PART_PURCHASED_BY = "Purchaser"
	public final static String SPARE_PART_QUANTITY = "Quantity"
	
	//Importing/exporting spare part types
	public final static String SPARE_PART_TYPE_CODE = "spare part type code"
	public final static String SPARE_PART_TYPE_NAME_EN = "spare part type name_en"
	public final static String SPARE_PART_TYPE_NAME_FR = "spare part type name_fr"
	public final static String SPARE_PART_TYPE_DESCRIPTION_EN = "spare part type description_en"
	public final static String SPARE_PART_TYPE_DESCRIPTION_FR = "spare part type description_fr"
	
}