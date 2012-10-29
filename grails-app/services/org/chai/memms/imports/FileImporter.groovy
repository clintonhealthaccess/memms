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

package org.chai.memms.imports;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chai.task.ImportTask
import org.supercsv.exception.SuperCSVException;
import org.supercsv.io.CsvMapReader;
import org.supercsv.io.ICsvMapReader;
import org.supercsv.prefs.CsvPreference;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

public abstract class FileImporter{

	protected static final Log log = LogFactory.getLog(FileImporter.class);

	public FileImporter() {
		super();
	}
	
	public void importCsvFile(String fileName, InputStream inputStream, String encoding, Character delimiter,File file,ImportTask task) throws IOException {
		importData(fileName, getReaderInCorrectEncodingWithDelimiter(inputStream, encoding, delimiter),file,task);	
	}

	public void importZipFiles(InputStream inputStream, String encoding, Character delimiter,File file,ImportTask task) throws IOException {
		ZipInputStream zipInputStream = null;
		zipInputStream = new ZipInputStream(inputStream);
		ZipEntry zipEntry;
		while((zipEntry = zipInputStream.getNextEntry()) != null) {
			if (!zipEntry.isDirectory()) importData(zipEntry.getName(), getReaderInCorrectEncodingWithDelimiter(zipInputStream, encoding, delimiter),file,task);
			if (log.isDebugEnabled()) log.debug("zipEntryName " +zipEntry.getName());
		}
	}

	protected Map<String, String> readRow(String filename, ICsvMapReader csvMapReader, String[] headers, ImporterErrorManager manager) throws IOException {
		try {
			return csvMapReader.read(headers);
		} catch (SuperCSVException e) {
			manager.incrementNumberOfUnsavedRows();
			manager.getErrors().add(new ImporterError(filename, csvMapReader.getLineNumber(), "", "import.error.message.headers.mismatch"));
			return new HashMap<String, String>();
		}
	}
	
	private ICsvMapReader getReaderInCorrectEncodingWithDelimiter(InputStream inputStream, String encoding, Character delimiter) {
		Charset charset = null;
		try {
			if (encoding != null && !encoding.isEmpty()) charset = Charset.forName(encoding);
		} catch (Exception e) {
			if (log.isWarnEnabled()) log.warn("encoding not recognized: "+encoding);
		}
		
		if (charset == null) {
			charset = Charset.defaultCharset();
		}
		int delimiterChar = CsvPreference.EXCEL_PREFERENCE.getDelimiterChar();
		if (delimiter != null) delimiterChar = delimiter;
		CsvPreference preference = new CsvPreference('"'.getChars()[0], delimiterChar, CsvPreference.EXCEL_PREFERENCE.getEndOfLineSymbols());
		return new CsvMapReader(new InputStreamReader(inputStream, charset), preference);
	}
	
	/**
	 * Imports one file.
	 * 
	 * @param fileName
	 * @param reader
	 * @throws IOException
	 */
	public abstract boolean importData(String fileName, ICsvMapReader reader,File file,ImportTask task) throws IOException;

}