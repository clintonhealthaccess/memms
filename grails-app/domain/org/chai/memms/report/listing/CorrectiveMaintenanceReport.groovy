/**
 * 
 */
package org.chai.memms.report.listing

import org.chai.memms.corrective.maintenance.WorkOrderStatus;

/**
 * @author Aphrodice Rwagaju
 *
 */
class CorrectiveMaintenanceReport extends EquipmentGeneralReportParameters{

	WorkOrderStatus workOrderStatus

	static constraints = {
		workOrderStatus nullable: true, blank: true
	}

	static mapping = {
		table "memms_corrective_maintenance_report_listing"
		version false
	}
}
