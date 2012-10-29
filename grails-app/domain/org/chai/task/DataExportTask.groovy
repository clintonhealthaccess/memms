package org.chai.task;

import java.util.Map
import org.chai.task.Task
import org.chai.task.Task.TaskStatus;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.chai.memms.exports.EquipmentTypeExport;
import org.chai.memms.task.Exporter;

abstract class DataExportTask extends Task {

	Long exportFilterId
	
	def executeTask() {
		def csvFile = null
		Task.withTransaction {
			def exporter = getExporter()
			if (exporter != null) {
				csvFile = exporter.exportData(this)
			}
		}
		
		if (csvFile != null) {
			File outputFile = new File(getFolder(), getOutputFilename())
			FileUtils.moveFile(csvFile, outputFile);
		}
	}
	
	boolean isUnique() {
		def task = DataExportTask.findByExportFilterId(exportFilterId)
		return task == null || task.status == TaskStatus.COMPLETED || task.status == TaskStatus.ABORTED
	}
	
	String getOutputFilename() {
		return getInformation()+".csv"
	}
	
	String getFormView() {
		return null
	}
	
	Map getFormModel() {
		return null
	}
	abstract Exporter getExporter()
	
	static constraints = {
		exportFilterId nullable:false
	}
}