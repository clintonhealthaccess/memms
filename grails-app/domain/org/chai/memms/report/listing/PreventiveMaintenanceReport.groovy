/**
 * 
 */
package org.chai.memms.report.listing

import org.chai.memms.preventive.maintenance.PreventiveOrder.PreventionResponsible;
import org.chai.memms.preventive.maintenance.PreventiveOrder.PreventiveOrderStatus;

/**
 * @author Aphrodice Rwagaju
 *
 */
class PreventiveMaintenanceReport extends EquipmentGeneralReportParameters{

	PreventiveOrderStatus preventiveOrderStatus
	PreventionResponsible  preventionResponsible

	static constraints = {
		preventiveOrderStatus nullable: true, blank: true
		preventionResponsible nullable: true, blank: true
	}

	static mapping = {
		table "memms_preventive_maintenance_report_listing"
		version false
	}
}
