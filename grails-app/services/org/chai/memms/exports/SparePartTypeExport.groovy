/**
 * 
 */
package org.chai.memms.exports

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.chai.memms.spare.part.SparePartType
import org.chai.memms.task.Exporter;
import org.chai.task.DataExportTask;
import org.chai.task.Progress;
import org.chai.memms.util.ImportExportConstant;
import org.supercsv.io.CsvListWriter
import org.supercsv.io.ICsvListWriter
import org.supercsv.prefs.CsvPreference

/**
 * @author Aphrodice Rwagaju
 *
 */
class SparePartTypeExport implements Exporter{

	public List<String> getExportDataHeaders() {
		List<String> headers = new ArrayList<String>();

		headers.add(ImportExportConstant.SPARE_PART_TYPE_CODE)
		headers.add(ImportExportConstant.SPARE_PART_TYPE_NAME_EN)
		headers.add(ImportExportConstant.SPARE_PART_TYPE_NAME_FR)
		headers.add(ImportExportConstant.SPARE_PART_TYPE_DESCRIPTION_EN)
		headers.add(ImportExportConstant.SPARE_PART_TYPE_DESCRIPTION_FR)

		return headers;
	}
	
	public File exportData(DataExportTask task) throws IOException{
		if (log.isDebugEnabled()) log.debug("exportData");
		
		File csvFile = File.createTempFile("sparePartType.export.temp", ImportExportConstant.CSV_FILE_EXTENSION);
		FileWriter csvFileWriter = new FileWriter(csvFile);
		ICsvListWriter writer = new CsvListWriter(csvFileWriter, CsvPreference.EXCEL_PREFERENCE);
		this.writeFile(writer,task);
		return csvFile;
		
	}
	private void writeFile(ICsvListWriter writer, Progress progress) throws IOException {
		try{
			String[] csvHeaders = null;
			if(csvHeaders == null){
				csvHeaders = this.getExportDataHeaders()
				writer.writeHeader(csvHeaders);
			}
			progress.setMaximum(SparePartType.count())
			for(SparePartType sparePartType: SparePartType.list()){
				if (log.isDebugEnabled()) log.debug(" exportSparePartType values code:"+sparePartType.code+", names:([en: "+sparePartType.getNames(new Locale("en"))+"], [fr: "+sparePartType.getNames(new Locale("fr"))+"]), "
					+", descriptions:([en: "+sparePartType.getDescriptions(new Locale("en"))+"], [fr: "+sparePartType.getDescriptions(new Locale("fr"))+"])");
				List<String> line = [sparePartType.code,sparePartType.getNames(new Locale("en")),sparePartType.getNames(new Locale("fr")),
					sparePartType.getDescriptions(new Locale("en")),sparePartType.getDescriptions(new Locale("fr"))]
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
		basicInfo.add("sparePartType.export")
		return basicInfo;
	}

}
