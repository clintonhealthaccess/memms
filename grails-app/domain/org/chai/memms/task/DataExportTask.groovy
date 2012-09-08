package org.chai.memms.task;

import java.util.Map;
import org.chai.memms.task.Task.TaskStatus;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.chai.memms.exports.EquipmentTypeExport;
import org.chai.memms.exports.Exporter;

abstract class DataExportTask extends Task {

	Long exportId
	
	def executeTask() {
		def csvFile = null
		Task.withTransaction {
			def exporter = getExporter()
			if (exporter != null) {
				csvFile = exporter.exportData()
			}
		}
		if (csvFile != null) {
			File outputFile = new File(getFolder(), getOutputFilename())
			FileUtils.moveFile(csvFile, outputFile);
		}
	}
	
	boolean isUnique() {
		def task = exportId != null ? DataExportTask.findByExportId(exportId) : null
		return task == null || task.status == TaskStatus.COMPLETED || task.status == TaskStatus.ABORTED
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
	
	static constraints = {
		exportId(nullable: true)
	}
}
