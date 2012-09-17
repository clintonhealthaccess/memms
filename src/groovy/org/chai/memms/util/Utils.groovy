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
package org.chai.memms.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.chai.memms.security.User

/**
 * @author Jean Kahigiso M.
 * 
 */
public class Utils {
	
	
	private final static String DATE_FORMAT = "dd-MM-yyyy";
	private final static String DATE_FORMAT_TIME = "dd-MM-yyyy hh:mm:ss";
	public final static String ZIP_FILE_EXTENSION = ".zip";
	
	public final static String CODE_DELIMITER = "~";
	public final static String CODE_PATTERN = 
			CODE_DELIMITER + "[^" + CODE_DELIMITER + "]+" + CODE_DELIMITER;
	public final static String VALUE_NOT_EXPORTABLE = "VALUE_NOT_EXPORTABLE";	

	public static Set<String> split(String string, String delimiter) {
		Set<String> result = new HashSet<String>();
		if (string != null) result.addAll(Arrays.asList(StringUtils.split(string, delimiter)));
		return result;
	}

	//TODO get rid of this
	public static File getZipFile(File file, String filename) throws IOException {
		List<File> files = new ArrayList<File>();
		files.add(file);
		return getZipFile(files, filename);
	}
	
	public static File getZipFile(List<File> files, String filename) throws IOException {
		
		File zipFile = File.createTempFile(filename, ZIP_FILE_EXTENSION);

		ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(zipFile));
				
		try {
			for(File file: files){
				FileInputStream fileInputStream = new FileInputStream(file);
				ZipEntry zipEntry = new ZipEntry(file.getName());
				zipOutputStream.putNextEntry(zipEntry);
				
				IOUtils.copy(fileInputStream, zipOutputStream);
				zipOutputStream.closeEntry();
			}
		} catch (IOException e) {
			throw e;
		} finally {
			IOUtils.closeQuietly(zipOutputStream);
			IOUtils.closeQuietly(zipOutputStream);
		}
			
		return zipFile;
	}
	
	public static String unsplit(Object list, String delimiter) {
		List<String> result = new ArrayList<String>();
		
		if (list instanceof String) result.add((String) list);
		if (list instanceof Collection) result.addAll((Collection<String>)list);
		else result.addAll(Arrays.asList((String[]) list));
		
		for (String string : new ArrayList<String>(result)) {
			if (string.isEmpty()) result.remove(string);
		}
		
		return StringUtils.join(result, delimiter);
	}
		
	public static boolean matches(String text, String value) {
		if (value == null) return false;
		return value.matches("(?i).*"+text+".*");
	}

	public static String formatNumber(String format, Number value) {
		if (format == null) format = "#";
		
		DecimalFormat frmt = new DecimalFormat(format);
		return frmt.format(value.doubleValue()).toString();
	}
	
	public static String formatDate(Date date) {
		if (date == null) return null;
		return new SimpleDateFormat(DATE_FORMAT).format(date);
	}
	
	public static String formatDateWithTime(Date date) {
		if (date == null) return null;
		return new SimpleDateFormat(DATE_FORMAT_TIME).format(date);
	}
	
	public static Date parseDate(String string) throws ParseException {
		return new SimpleDateFormat(DATE_FORMAT).parse(string);
	}
	
	//TODO implement reg expressions in groovy
	public static boolean containsId(String word, Long id) {
		def pattern = ~/".*\\$"+id+"(\\D|\\z|\\s)(.|\\s)*"/
		return word.matches(pattern);
	}
	
	public static String stripHtml(String htmlString) {
		String noHtmlString;
	
		if (htmlString != null) {
			noHtmlString = htmlString.replace("&nbsp;", " ");
			noHtmlString = noHtmlString.replaceAll("<.*?>", " ");
			noHtmlString = StringEscapeUtils.unescapeHtml(noHtmlString);
			noHtmlString = noHtmlString.trim();
		}
		else noHtmlString = htmlString;
	
		return noHtmlString;
	}
	
	public static <E> List<E> removeDuplicates(List<E> list){
		Set<E> set = new LinkedHashSet<E>(list);
		list.clear();
		list.addAll(set);
		return list;
	}
	
	public static Class<?> isExportablePrimitive(Class<?> clazz){
		Class<?> exportableClazz = null;		
		if(clazz.isPrimitive() || ClassUtils.wrapperToPrimitive(clazz) != null){
			exportableClazz = clazz;
		}
		return exportableClazz;
	}
	
	/**
	 * fieldName has to start with capital letter as
	 * it is used to create setter of the object field
	 * @param object
	 * @param map
	 * @param fieldName
	 * @return
	 */
	public static def setLocaleValueInMap(def object, def map, def fieldName){
	   def methodName = 'set'+fieldName
	   //TODO replace with CONF variable if this fails
	   def grailsApplication = new User().domainClass.grailsApplication
	   grailsApplication.config.i18nFields.locales.each{ loc ->
		   if(map.get(loc) != null)
			   object."$methodName"(map.get(loc),new Locale(loc))
		   else
			   object."$methodName"("",new Locale(loc))
	   }
   }
}
