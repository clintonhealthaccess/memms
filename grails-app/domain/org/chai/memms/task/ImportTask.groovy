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

package org.chai.memms.task

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.chai.memms.imports.FileImporter;
import org.chai.memms.imports.ImporterErrorManager
import org.chai.memms.util.Utils;
import org.springframework.web.multipart.MultipartFile;

abstract class ImportTask extends Task {

	def groovyPageRenderer
	
	String inputFilename
	String encoding
	Character delimiter
	
	static FileType getFileType(String filename){
		String fileExtension = filename.substring(filename.lastIndexOf(".") + 1);// Add a one to get where to cut from

		if (fileExtension.equalsIgnoreCase("csv")) return FileType.CSV;
		else if(fileExtension.equalsIgnoreCase("zip")) return FileType.ZIP;
			
		return FileType.NONE;
	}
	
	private void importFile(def importer, File file, String encoding, Character delimiter){
		switch (getFileType(inputFilename)) {
		case FileType.ZIP:
			importer.importZipFiles(new FileInputStream(file), encoding, delimiter,file, this);
			break;
		case FileType.CSV:
			importer.importCsvFile(inputFilename, new FileInputStream(file), encoding, delimiter,file, this);
			break;
		default:
			throw new IllegalStateException('file type not accepted')
			break;
		}
	}
	
	def executeTask() {
		ImporterErrorManager errorManager = new ImporterErrorManager()
		FileImporter importer = getImporter(errorManager)
		File inputFile = new File(getFolder(), inputFilename)
		if (log.isDebugEnabled()) log.debug('input file=' + inputFile + ', importer=' + importer)
		if (inputFile.exists() && importer != null) {
			Task.withTransaction {
				importFile(importer, inputFile, encoding, delimiter)
			}
			
			// put errorManager output in a file and save it
			String errorOutput = groovyPageRenderer.render template: '/task/importOutput', model: [errorManager: errorManager]
			File outputFile = new File(getFolder(), 'equipmentTypeimportlog.txt')
			outputFile.createNewFile()
			
			def fileWriter = new FileWriter(outputFile)
			IOUtils.write(errorOutput, fileWriter)
			fileWriter.flush()
			IOUtils.closeQuietly(fileWriter)
		}
		else {
			throw new IllegalStateException("task is invalid, data, period or file not found")
		}
	}
	
	String getOutputFilename() {
		return null
	}
	
	boolean isUnique() {
		return true;
	}
	
	abstract FileImporter getImporter(ImporterErrorManager errorManager);
	
	static constraints = {
		delimiter(blank:false, nullable:false)
		encoding(blank:false, nullable:false)
		inputFilename(blank:false, nullable: false, validator: {val, obj ->
			return getFileType(val) != FileType.NONE
		})
	}
}

enum FileType {
	NONE,ZIP,CSV
}