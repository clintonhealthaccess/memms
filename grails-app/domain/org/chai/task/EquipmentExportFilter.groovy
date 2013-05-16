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

package org.chai.task

import groovy.transform.EqualsAndHashCode;

import java.util.Map;
import java.util.Set;

import org.chai.memms.inventory.Equipment;
import org.chai.memms.inventory.Equipment.PurchasedBy;
import org.chai.memms.inventory.EquipmentType;
import org.chai.memms.inventory.Provider;
import org.chai.memms.inventory.Equipment.Donor;
import org.chai.memms.inventory.EquipmentStatus.Status;
import org.chai.memms.exports.EquipmentExport;
import org.chai.memms.exports.EquipmentTypeExport;
import org.chai.memms.task.Exporter;
import org.chai.location.CalculationLocation;
import org.chai.location.DataLocation;
import org.chai.location.DataLocationType;
import org.chai.memms.util.Utils;

/**
 * @author Jean Kahigiso M.
 *
 */
@EqualsAndHashCode(includes='id')
class EquipmentExportFilter extends ExportFilter{
	Status equipmentStatus
	Donor donor
	PurchasedBy purchaser
	String obsolete
	
	static hasMany = [equipmentTypes:EquipmentType,manufacturers:Provider,suppliers:Provider,serviceProviders:Provider]
	static constraints = {
		equipmentTypes nullable: true, blank: true
		manufacturers nullable: true, blank: true
		suppliers nullable: true, blank: true
		serviceProviders nullable: true, blank: true
		equipmentStatus nullable: true
		donor nullable: true, blank: true
		obsolete nullable: true, blank: true
		purchaser nullable: true, blank: true
	}
	static mapping = {
		table "memms_equipment_export_filter"
		version false
		equipmentTypes joinTable:[name:"memms_equipment_equipment_type_export_filter",key:"type_id",column:"equipment_export_filter_id"]
		manufacturers joinTable:[name:"memms_equipment_manufacturer_export_filter",key:"manufacturer_id",column:"equipment_export_filter_id"]
		suppliers joinTable:[name:"memms_equipment_supplier_export_filter",key:"supplier_id",column:"equipment_export_filter_id"]
		serviceProviders joinTable:[name:"memms_equipment_service_provider_export_filter",key:"service_provider_id",column:"equipment_export_filter_id"]
	}
}