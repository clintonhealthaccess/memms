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
package org.chai.memms.exports;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chai.task.DataExportTask
import org.chai.task.Progress
import org.chai.memms.inventory.EquipmentType
import org.chai.memms.util.ImportExportConstant;
import org.chai.memms.util.Utils;
import org.supercsv.io.CsvListWriter;
import org.supercsv.io.ICsvListWriter;
import org.supercsv.prefs.CsvPreference;
import org.chai.memms.task.Exporter;
import org.chai.memms.reports.hmis.HmisReport;
import org.chai.task.HmisEquipmentTypeExportFilter;
import org.chai.memms.reports.hmis.HmisFacilityReport;

public class HmisEquipmentTypeExport implements Exporter {

	public File exportData(DataExportTask task) throws IOException{
		if (log.isDebugEnabled()) log.debug("exportData");
		//def hmisReportId = task.exportFilterId
		File csvFile = File.createTempFile("hmisEquipmentType.export.temp", ImportExportConstant.CSV_FILE_EXTENSION);
		FileWriter csvFileWriter = new FileWriter(csvFile);
		ICsvListWriter writer = new CsvListWriter(csvFileWriter, CsvPreference.EXCEL_PREFERENCE);
		this.writeFile(writer,task, task.exportFilterId);
		return csvFile;
		
	}
		
	private void writeFile(ICsvListWriter writer, Progress progress, Long hmisReportId) throws IOException {
		try{
			String[] csvHeaders = null;
			// headers
			if(csvHeaders == null){
				csvHeaders = this.getExportDataHeaders()
				writer.writeHeader(csvHeaders);
			}
 			def hmisEquipmentTypeExportFilter = HmisEquipmentTypeExportFilter.get(hmisReportId)
			//def hmisReport = hmisEquipmentTypeExportFilter.hmisReport
			def facilityReports = HmisFacilityReport.findAllByHmisReport(hmisEquipmentTypeExportFilter.hmisReport)
			progress.setMaximum(facilityReports.size())
			for(def facilityReport: facilityReports){
				log.debug("cod: " + facilityReport.dataLocation.code + " Hmis Equipment Type: " + facilityReport.hmisEquipmentType.getNames(new Locale("en")) + " Number of Equipment: " + facilityReport.numberOfOpEquipment)

				List<String> line = 
				[
					facilityReport.dataLocation.code,
					(facilityReport.dataLocation.getNames(new Locale("en")))?facilityReport.dataLocation.getNames(new Locale("en")):"",
					(facilityReport.dataLocation.getNames(new Locale("fr")))?facilityReport.dataLocation.getNames(new Locale("fr")):"",
					(facilityReport.hmisEquipmentType.getNames(new Locale("en")))?facilityReport.hmisEquipmentType.getNames(new Locale("en")):"",
					(facilityReport.hmisEquipmentType.getNames(new Locale("fr")))?facilityReport.hmisEquipmentType.getNames(new Locale("fr")):"",
					facilityReport.hmisEquipmentType.code,
					facilityReport.dateCreated.toString(),
					facilityReport.numberOfOpEquipment
				]
				writer.write(line)
				progress.incrementProgress()
			}
		} catch (IOException ioe){
			// TODO throw something that make sense
			throw ioe;
		} finally {
			writer.close();
		}
	}
	
	public List<String> getBasicInfo(){
		List<String> basicInfo = new ArrayList<String>();
		basicInfo.add("equipmentType.export")
		return basicInfo;
	}
	

	public List<String> getExportDataHeaders() {
		List<String> headers = new ArrayList<String>();
		headers.add(ImportExportConstant.LOCATION_CODE);
		headers.add(ImportExportConstant.LOCATION_NAME_EN);
		headers.add(ImportExportConstant.LOCATION_NAME_FR);
		headers.add(ImportExportConstant.HMIS_EQUIPMENT_TYPE_NAME_EN);
		headers.add(ImportExportConstant.HMIS_EQUIPMENT_TYPE_NAME_FR);
		headers.add(ImportExportConstant.HMIS_EQUIPMENT_TYPE_CODE);
		headers.add(ImportExportConstant.HMIS_EQUIPMENT_TYPE_GENERATED_DATE);
		headers.add(ImportExportConstant.NUMBER_OF_OPERATIONAL_EQUIPMENT);
		
		return headers;
	}
}
