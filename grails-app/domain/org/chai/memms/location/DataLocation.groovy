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
package org.chai.memms.location;

import org.chai.memms.util.Utils;
/**
* @author Jean Kahigiso M.
*
*/

public class DataLocation extends CalculationLocation{

	static belongsTo = [type: DataLocationType,location: Location]

	List<DataLocation> getDataLocations() {
		def result = new ArrayList<DataLocation>();
		result.add(this);
		return result;
	}

	List<Location> getChildren() {
		return new ArrayList<Location>();
	}
	
	List<DataLocation> getDataLocations(Set<LocationLevel> skipLevels, Set<DataLocationType> types) {
		def result = new ArrayList<DataLocation>();
		if (types == null || types.contains(type)) result.add(this);
		return result;
	}

	List<Location> getChildren(Set<LocationLevel> skipLevels) {
		return getChildren();
	}
	
	boolean collectsData() {
		return true;
	}

	
	String toString() {
		return "DataLocation[Id=" + id + ", Code=" + code + "]";
	}
	
	static mapping = {
		table "chai_location_data_location"
		location column: "location_id"
		version false
		
	}
	
	static constraints = {
		importFrom CalculationLocation
		type (nullable: false)
		location (nullable: false)
	}
}
