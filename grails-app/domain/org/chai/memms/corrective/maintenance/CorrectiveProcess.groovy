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
package org.chai.memms.corrective.maintenance

import org.chai.memms.maintenance.MaintenanceProcess;
import org.chai.memms.security.User;

/**
 * @author Jean Kahigiso M.
 *
 */
public class CorrectiveProcess extends MaintenanceProcess{
	
	enum ProcessType{
		ACTION("action"),
		MATERIAL("material"),
		String messageCode = "corrective.process.type"
		String name
		ProcessType(String name){this.name=name}
		String getKey(){ return name() }
	}
	
	ProcessType type
	static belongsTo =[workOrder: WorkOrder]
	
	static mapping = {
		table "memms_corrective_process"
		version  false
	}
	
	static constraints = {
		importFrom MaintenanceProcess
		type nullable:false, inList:[ProcessType.ACTION,ProcessType.MATERIAL]
	}

	@Override
	public String toString() {
		return "MaintenanceProcess [id=" + id + ", name=" + name + ", workOrder="+ workOrder +"]";
	}	
}
