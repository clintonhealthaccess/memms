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

package org.chai.memms.imports

import org.chai.memms.inventory.EquipmentType
import org.chai.memms.inventory.EquipmentTypeService;
import org.chai.memms.imports.ImporterErrorManager
import org.chai.memms.imports.ImporterError
import org.chai.task.ImportTask
import org.chai.memms.util.ImportExportConstant;
import org.chai.memms.util.Utils
import org.hibernate.SessionFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.supercsv.io.ICsvMapReader

class EquipmentTypeImporter extends FileImporter {
	
	def sessionFactory
	def equipmentTypeService
	private ImporterErrorManager manager

	public EquipmentTypeImporter(SessionFactory sessionFactory,EquipmentTypeService equipmentTypeService,ImporterErrorManager manager) {
		this.equipmentTypeService = equipmentTypeService
		this.manager = manager
		this.sessionFactory = sessionFactory
	}

	private boolean importDataOne(String fileName,ICsvMapReader reader,Integer numberOfLinesToImport,  String[] headers,  Map<String,Integer> positions,ImportTask task) throws IOException{

		Map<String, String> rowValues = reader.read(headers);

		int importedLines = 0;
		while (rowValues != null && importedLines < numberOfLinesToImport) {
			if (log.isInfoEnabled()) log.info("starting import of line with values: "+rowValues);

			Integer lineNumber=  reader.getLineNumber();
			
			String newDeviceCode = rowValues.get(ImportExportConstant.DEVICE_CODE)
			String newDeviceNameEn = rowValues.get(ImportExportConstant.DEVICE_NAME_EN)?:"none"
			String newDeviceNameFr = rowValues.get(ImportExportConstant.DEVICE_NAME_FR)?:"none"
			//This is boolean
			String newDeviceIncludedInMemms = rowValues.get(ImportExportConstant.DEVICE_INCLUDED_IN_MEMMS)
			String newDescriptionEn = rowValues.get(ImportExportConstant.DEVICE_DESCRIPTION_EN)?:"none"
			String newDescriptionFr = rowValues.get(ImportExportConstant.DEVICE_DESCRIPTION_FR)?:"none"
			//This is an enum
			String newDeviceObservation = rowValues.get(ImportExportConstant.DEVICE_OBSERVATION)
			
			if (log.isInfoEnabled()) log.info("Importing values code:$newDeviceCode, names:([en: $newDeviceNameEn], [fr: $newDeviceNameFr]), included in memms: $newDeviceIncludedInMemms, descriptions:([en: $newDescriptionEn], [fr: $newDescriptionFr]), Observation: $newDeviceObservation");
			//Exists so update
			def equipmentType = EquipmentType.findByCode(newDeviceCode)
			if(equipmentType != null){
				equipmentType.lastModifiedOn = new Date()
				Utils.setLocaleValueInMap(equipmentType,["en":newDeviceNameEn,"fr":newDeviceNameFr],"Names")
				Utils.setLocaleValueInMap(equipmentType,["en":newDescriptionEn,"fr":newDescriptionFr],"Descriptions")
				if(equipmentType.save(flush: true) == null){
					manager.numberOfUnsavedRows++
					manager.getErrors().add(new ImporterError(fileName,lineNumber, Arrays.asList(headers).toString(),"import.error.message.not.saved"))
				}else{
				manager.numberOfSavedRows++
				}
			}else{
				equipmentType = new EquipmentType(code:newDeviceCode,addedOn:new Date(),lastModifiedOn:new Date())
				Utils.setLocaleValueInMap(equipmentType,["en":newDeviceNameEn,"fr":newDeviceNameFr],"Names")
				equipmentType.observation = equipmentTypeService.importToObservation( newDeviceObservation )
				Utils.setLocaleValueInMap(equipmentType,["en":newDescriptionEn,"fr":newDescriptionFr],"Descriptions")
				if(equipmentType.save(flush: true) == null){
					manager.numberOfUnsavedRows++
					manager.getErrors().add(new ImporterError(fileName,lineNumber, Arrays.asList(headers).toString(),"import.error.message.not.saved"))
				}else{
				manager.numberOfSavedRows++
				}
			}
			if (log.isInfoEnabled()) log.info("finished importing line");

			// we increment the number of imported lines to stop the while loop when it reaches numberOfLinesToImport
			importedLines++;

			task.incrementProgress()
			
			// read new line
			if (importedLines < numberOfLinesToImport) rowValues = reader.read(headers);
		}
		return rowValues == null;
	}



	public boolean importData(final String fileName, final ICsvMapReader csvMapReader, File file, ImportTask task) throws IOException {
		if (log.isInfoEnabled()) log.info("importData( fileName "+fileName+" reader "+csvMapReader+")");
		manager.setCurrentFileName(fileName);
		final String[] headers = csvMapReader.getCSVHeader(true);

		final Map<String,Integer> positions = new HashMap<String,Integer>();

		boolean readEntirely = false;
		if(!Arrays.asList(headers).contains(ImportExportConstant.DEVICE_CODE) || !Arrays.asList(headers).contains(ImportExportConstant.DEVICE_NAME_EN) ||
		!Arrays.asList(headers).contains(ImportExportConstant.DEVICE_NAME_FR) || !Arrays.asList(headers).contains(ImportExportConstant.DEVICE_INCLUDED_IN_MEMMS) ||
		!Arrays.asList(headers).contains(ImportExportConstant.DEVICE_DESCRIPTION_EN) || !Arrays.asList(headers).contains(ImportExportConstant.DEVICE_DESCRIPTION_FR)||
		!Arrays.asList(headers).contains(ImportExportConstant.DEVICE_OBSERVATION))
			manager.getErrors().add(new ImporterError(fileName,csvMapReader.getLineNumber(), Arrays.asList(headers).toString(),"import.error.message.unknowm.header"));
		else{
			//TODO find a better way to count the number of lines to be imported to be used by the progress bar
			def lines = 0
			file.eachLine {
				lines++
			}
			//Subtract two for the header and since the count starts from zero
			task.setMaximum(lines -= 2)
			if (log.isInfoEnabled())log.info("number of lines in file = " + lines)
			while (!readEntirely) {
				EquipmentType.withNewTransaction {
					try {
						readEntirely = importData(fileName, csvMapReader, ImportExportConstant.NUMBER_OF_LINES_TO_IMPORT, headers, positions,task);
					} catch (IOException e) {
						readEntirely = true;
					}
				}
				sessionFactory.getCurrentSession().clear();
			}
			
			if (log.isInfoEnabled()) log.info("Finished all imports, total lines saved=" + manager.numberOfSavedRows +", total lines not saved=" + manager.numberOfUnsavedRows  +", errors=" + manager.errors );
		}
	}
}
