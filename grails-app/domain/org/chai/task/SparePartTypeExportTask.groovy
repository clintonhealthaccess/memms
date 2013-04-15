/**
 * 
 */
package org.chai.task;

import org.chai.memms.exports.SparePartTypeExport
import org.chai.memms.task.Exporter;
import org.chai.memms.util.Utils;

/**
 * @author Aphrodice Rwagaju
 *
 */
class SparePartTypeExportTask extends DataExportTask{

	
def messageSource
	
	String getInformation() {
		def informLabel = messageSource.getMessage('spare.part.type.label', new Object[0], LocaleContextHolder.getLocale())
		return informLabel +" "+'<br/>'+messageSource.getMessage('import.file.label', new Object[0], LocaleContextHolder.getLocale())
	}
	
	Exporter getExporter() {
		return new SparePartTypeExport()
	}
	
	
	Map getFormModel() {
		return [
			task: this
		]
	}

}
