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

import org.chai.memms.spare.part.SparePart.SparePartPurchasedBy
import org.chai.memms.spare.part.SparePart.SparePartStatus;
import org.chai.memms.spare.part.SparePartType;
import org.chai.memms.inventory.Provider;
import groovy.transform.EqualsAndHashCode;
import java.util.Map;
import java.util.Set;
import org.chai.memms.task.Exporter;
import org.chai.location.DataLocation;
import org.chai.memms.util.Utils;

/**
 * @author Aphrodice Rwagaju
 *
 */
@EqualsAndHashCode(includes='id')
class SparePartExportFilter extends ExportFilter{
	SparePartPurchasedBy sparePartPurchasedBy
	SparePartStatus sparePartStatus

	//static hasMany = [sparePartTypes:SparePartType,suppliers:Provider,dataLocations:DataLocation]
	
	static constraints = {
		sparePartTypes nullable: true, blank: true
		suppliers nullable: true, blank: true
	//	dataLocations nullable: true, blank: true
		sparePartPurchasedBy nullable: true, blank: true
		sparePartStatus nullable: true
	}
	
	static hasMany = [sparePartTypes:SparePartType,manufacturers:Provider,suppliers:Provider]
	
	static mapping = {
		table "memms_spare_part_export_filter"
		version false
		sparePartTypes joinTable:[name:"memms_spare_part_spare_part_type_export_filter",key:"type_id",column:"spare_part_export_filter_id"]
		suppliers joinTable:[name:"memms_spare_part_supplier_export_filter",key:"supplier_id",column:"spare_part_export_filter_id"]
	//	dataLocations joinTable:[name:"memms_spare_part_data_location_export_filter",key:"data_location_id",column:"spare_part_export_filter_id"]
		}

}
