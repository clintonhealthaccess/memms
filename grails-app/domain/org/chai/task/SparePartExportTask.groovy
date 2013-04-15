/**
 * 
 */
package org.chai.task

import java.util.Map;

import org.chai.memms.exports.SparePartExport;
import org.chai.memms.task.Exporter;
import org.chai.memms.util.Utils;
import org.springframework.context.i18n.LocaleContextHolder;
import org.chai.location.CalculationLocation;
import org.chai.location.DataLocationType;
import org.chai.memms.exports.SparePartTypeExport;
import org.chai.memms.spare.part.SparePartStatus.StatusOfSparePart;

/**
 * @author Aphrodice Rwagaju
 *
 */
class SparePartExportTask extends DataExportTask{
	
	def messageSource
	String getInformation() {
		def informLabel = messageSource.getMessage('spare.part.label', new Object[0], LocaleContextHolder.getLocale())
		return informLabel +" "+'<br/>'+messageSource.getMessage('import.file.label', new Object[0], LocaleContextHolder.getLocale())
	}

	public Exporter getExporter() {
		return new SparePartExport();
	}
	
	Map getFormModel() {
		return [
			task: this
		]
	}

}
