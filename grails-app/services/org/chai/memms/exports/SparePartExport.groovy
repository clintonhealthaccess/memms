/**
 * 
 */
package org.chai.memms.exports

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.io.FileWriter;

import org.chai.memms.spare.part.SparePart
import org.chai.memms.spare.part.SparePart.SparePartPurchasedBy;
import org.chai.memms.spare.part.SparePartStatus.StatusOfSparePart;
import org.chai.memms.task.Exporter;
import org.chai.task.DataExportTask;
import org.chai.task.Progress
import org.chai.task.SparePartExportFilter
import org.chai.memms.util.ImportExportConstant
import org.supercsv.io.CsvListWriter
import org.supercsv.io.ICsvListWriter
import org.supercsv.prefs.CsvPreference

/**
 * @author Aphrodice Rwagaju
 *
 */
class SparePartExport implements Exporter{
	
	public List<String> getExportDataHeaders() {
		List<String> headers = new ArrayList<String>();

		headers.add(ImportExportConstant.SPARE_PART_SERIAL_NUMBER)
		headers.add(ImportExportConstant.SPARE_PART_TYPE_CODE)
		headers.add(ImportExportConstant.SPARE_PART_TYPE_NAME_EN)
		headers.add(ImportExportConstant.SPARE_PART_TYPE_NAME_FR)
		headers.add(ImportExportConstant.SPARE_PART_STATUS)
		headers.add(ImportExportConstant.SPARE_PART_PURCHASED_BY)
		headers.add(ImportExportConstant.LOCATION_CODE)
		headers.add(ImportExportConstant.LOCATION_NAME_EN)
		headers.add(ImportExportConstant.MANUFACTURER_CONTACT_NAME)
		headers.add(ImportExportConstant.SPARE_PART_MANUFACTURE_DATE)
		headers.add(ImportExportConstant.SUPPLIER_CODE)
		headers.add(ImportExportConstant.SUPPLIER_CONTACT_NAME)
		headers.add(ImportExportConstant.SUPPLIER_DATE)
		headers.add(ImportExportConstant.SPARE_PART_PURCHASE_COST)
		headers.add(ImportExportConstant.SPARE_PART_PURCHASE_COST_CURRENCY)
		headers.add(ImportExportConstant.SPARE_PART_SAME_AS_MANUFACTURER)
		headers.add(ImportExportConstant.SPARE_PART_WARRANTY_START)
		headers.add(ImportExportConstant.SPARE_PART_WARRANTY_PERIOD)

		return headers;
	}
	
	private void writeFile(ICsvListWriter writer, Progress progress, Long sparePartExportFilterId) throws IOException {
		try{
			String[] csvHeaders = null;
			if(csvHeaders == null){
				csvHeaders = this.getExportDataHeaders()
				writer.writeHeader(csvHeaders);
			}
			def sparePartExportFilter = SparePartExportFilter.get(sparePartExportFilterId)
			List<SparePart> spareParts = []
			if(sparePartExportFilter != null){
				spareParts = filterSparePart(sparePartExportFilter)
			}
			progress.setMaximum(spareParts.size())
			
			for(SparePart sparePart: spareParts){
				if (log.isDebugEnabled()) log.debug("exporting spare part = " + sparePart)
				List<String> line = [sparePart.serialNumber,sparePart.type.code,sparePart.type?.getNames(new Locale("en")),sparePart.type?.getNames(new Locale("fr")),
					sparePart.getTimeBasedStatus()?.statusOfSparePart,
					sparePart.dataLocation?.code,sparePart.dataLocation?.getNames(new Locale("en")),sparePart.dataLocation?.getNames(new Locale("fr")),
					sparePart.room,sparePart.manufactureDate,sparePart.supplier?.code,sparePart.supplier?.contact?.contactName,
					sparePart.purchaseDate,sparePart.purchaseCost?:"n/a",sparePart.currency?:"n/a",sparePart.sameAsManufacturer,sparePart.warranty?.startDate,sparePart.warranty?.period?.numberOfMonths]
				log.debug("exporting line=" + line)
				writer.write(line)
				progress.incrementProgress()
			}
		} catch (IOException ioe){
			throw ioe;
		} finally {
			writer.close();
		}
	}
	
	public List<SparePart> filterSparePart(SparePartExportFilter sparePartExportFilter){
		
		def criteria = SparePart.createCriteria();
		return criteria.list(sort:"id",order:"desc"){
			if(sparePartExportFilter.dataLocations != null && sparePartExportFilter.dataLocations.size() > 0)
				('dataLocation' in sparePartExportFilter.dataLocations)
			if(sparePartExportFilter.suppliers != null && sparePartExportFilter.suppliers.size() > 0)
				("supplier" in sparePartExportFilter.suppliers)
			if(sparePartExportFilter.sparePartTypes != null && sparePartExportFilter.sparePartTypes.size() > 0)
				("type" in sparePartExportFilter.sparePartTypes)
			if(!sparePartExportFilter.sparePartPurchasedBy.equals(SparePartPurchasedBy.NONE))
				("sparePartPurchasedBy" in sparePartExportFilter.sparePartPurchasedBy)
			if(sparePartExportFilter.sameAsManufacturer)
				eq ("sameAsManufacturer", (sparePartExportFilter.sameAsManufacturer.equals('true'))?true:false)
			if(!sparePartExportFilter.statusOfSparePart.equals(StatusOfSparePart.NONE)){
				createAlias("statusOfSparePart","t")
				and{
					eq ("t.statusOfSparePart", sparePartExportFilter.statusOfSparePart)
				//	eq ("t.current", true)
				}
			}
				
		}
	}

	public File exportData(DataExportTask task) throws IOException {
		if (log.isDebugEnabled()) log.debug("exporting data");
		
		File csvFile = File.createTempFile("sparePart.export.temp", ImportExportConstant.CSV_FILE_EXTENSION);
		FileWriter csvFileWriter = new FileWriter(csvFile);
		ICsvListWriter writer = new CsvListWriter(csvFileWriter, CsvPreference.EXCEL_PREFERENCE);
		this.writeFile(writer,task,task.exportFilterId);
		return csvFile;
	}
	
	public List<String> getBasicInfo(){
		List<String> basicInfo = new ArrayList<String>();
		basicInfo.add("sparePart.export")
		return basicInfo;
	}

}
