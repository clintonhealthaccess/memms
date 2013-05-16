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
package org.chai.memms.exports

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chai.memms.spare.part.SparePart;
import org.chai.memms.spare.part.SparePartType;
import org.chai.memms.spare.part.SparePart.SparePartPurchasedBy;
import org.chai.memms.spare.part.SparePartStatus.StatusOfSparePart;
import org.chai.memms.task.Exporter;
import org.chai.task.DataExportTask;
import org.chai.task.Task;
import org.chai.task.Progress;
import org.chai.task.SparePartExportFilter;
import org.chai.memms.util.ImportExportConstant;
import org.supercsv.io.CsvListWriter
import org.supercsv.io.ICsvListWriter
import org.supercsv.prefs.CsvPreference
import org.chai.memms.util.Utils;

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
		headers.add(ImportExportConstant.LOCATION_NAME_FR)
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
					sparePart.statusOfSparePart,sparePart.sparePartPurchasedBy,sparePart.dataLocation?.code,sparePart.dataLocation?.getNames(new Locale("en")),
					sparePart.dataLocation?.getNames(new Locale("fr")),sparePart.type?.manufacturer?.contact?.contactName,sparePart.manufactureDate,sparePart.supplier?.code,
					sparePart.supplier?.contact?.contactName,sparePart.purchaseDate,sparePart.purchaseCost?:"n/a",sparePart.currency?:"n/a",sparePart.sameAsManufacturer,
					sparePart.warranty?.startDate,sparePart.warrantyPeriod?.numberOfMonths]
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
				eq ("sparePartPurchasedBy", sparePartExportFilter.sparePartPurchasedBy)
			if(sparePartExportFilter.sameAsManufacturer)
				eq ("sameAsManufacturer", (sparePartExportFilter.sameAsManufacturer.equals('true'))?true:false)
			if(!sparePartExportFilter.statusOfSparePart.equals(StatusOfSparePart.NONE)){
				eq ("statusOfSparePart", sparePartExportFilter.statusOfSparePart)
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
