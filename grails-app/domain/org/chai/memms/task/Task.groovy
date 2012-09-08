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

import java.io.File

import org.apache.commons.io.FileUtils
import org.chai.memms.security.User


abstract class Task implements Progress {

	enum TaskStatus{NEW, COMPLETED, IN_PROGRESS, ABORTED}
	
	def grailsApplication
	
	User user
	TaskStatus status
	Date added = new Date()
	
	Date started
	Date finished
	
	Integer numberOfTries = 0
	Boolean sentToQueue = false
	
	// progress
	Long max = 0;
	Long current = null;
	Boolean aborted = false;
	
	abstract def executeTask()
	
	abstract boolean isUnique()
	abstract String getFormView()
	abstract Map getFormModel()
	abstract String getOutputFilename()
	abstract String getInformation()
	
	File getFolder() {
		def folder = new File(grailsApplication.config.task.temp.folder + File.separator + this.getId())
		if (!folder.exists()) folder.mkdirs()
		return folder
	}
	
	def cleanTask() {
		File folder = getFolder()
		if (folder != null && folder.exists()) FileUtils.deleteDirectory(folder)
	}
	
	void incrementProgress(Long increment = null) {
		if (log.isDebugEnabled()) log.debug('incrementProgress, max: '+ max +', current: '+current)
		if (aborted) throw new TaskAbortedException()
		
		if (current != null) {
			Task.withNewTransaction {
				if (increment == null) current++
				else current += increment
				this.save(flush: true)
			}
		}
	}
	
	void setMaximum(Long max) {
		Task.withNewTransaction {
			this.max = max;
			this.current = 0;
			this.save(flush: true)
		}
	}
	
	void abort() {
		Task.withNewTransaction {
			aborted = true
			this.save(flush: true)
		}
	}
	
	boolean isAborted() {
		return aborted
	}
	
	Double retrievePercentage() {
		if (current == null || max == 0) return null
		return current.doubleValue()/max.doubleValue()
	}
	
	static mapping = {
		table "memms_tasks"
		version false
	}
	
	static constraints = {
		user(nullable: false)
		status(nullable: false)
		max(nullable: false)
		current(nullable: true)
		
		started(nullable: true)
		finished(nullable: true)
	}
	
}

