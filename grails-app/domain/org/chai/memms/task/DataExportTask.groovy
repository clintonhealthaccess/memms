package org.chai.memms.task;

import java.util.Map;
import org.chai.memms.task.Task.TaskStatus;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.chai.memms.exports.EquipmentTypeExport;
import org.chai.memms.exports.Exporter;

abstract class DataExportTask extends Task {

	def executeTask() {
		def csvFile = null
		Task.withTransaction {
			if (log.isDebugEnabled()) log.debug('getting exporter')
			def exporter = getExporter()
			if (exporter != null) {
				if (log.isDebugEnabled()) log.debug('exporting data')
				csvFile = exporter.exportData(this)
				if (log.isDebugEnabled()) log.debug('done exporting')
			}
		}
		
		if (csvFile != null) {
			File outputFile = new File(getFolder(), getOutputFilename())
			FileUtils.moveFile(csvFile, outputFile);
		}
	}
	
	boolean isUnique() {
		return true;
	}
	
	String getOutputFilename() {
		return 'equipmentTypeExportOutput.csv'
	}
	
	String getInformation() {
		return null
	}

	String getFormView() {
		return null
	}
	
	Map getFormModel() {
		return null
	}
	abstract Exporter getExporter()
}