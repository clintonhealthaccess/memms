/**
 * 
 */
package org.chai.memms.report.listing

import org.chai.memms.inventory.EquipmentStatus.Status;

/**
 * @author Aphrodice Rwagaju
 *
 */
class EquipmentReport extends EquipmentGeneralReportParameters {

	String obsolete
	Status equipmentStatus

	static constraints = {
		obsolete nullable: true, blank: true
		equipmentStatus nullable: true
	}

	static mapping = {
		table "memms_equipment_report_listing"
		version false
	}
}
