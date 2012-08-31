package org.chai.memms.location;

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

import org.apache.commons.lang.StringUtils
import org.chai.memms.location.CalculationLocation;
import org.chai.memms.location.DataLocation;
import org.chai.memms.location.DataLocationType;
import org.chai.memms.location.Location;
import org.chai.memms.location.LocationLevel;
import org.chai.memms.util.Utils;
import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode
import org.hibernate.criterion.Order
import org.hibernate.criterion.Projections
import org.hibernate.criterion.Restrictions

/**
* @author Jean Kahigiso M.
*
*/
public class LocationService {
	
	static transactional = true
	
	def languageService;
	def sessionFactory;	
	
    Location getRootLocation() {
    	return Location.findByParent(null, [cache: true])
    }

	List<LocationLevel> listLevels() {
		return LocationLevel.list([cache: true])
	}
	
	List<DataLocationType> listTypes() {
		return DataLocationType.list([cache: true])
	}
	
	LocationLevel findLocationLevelByCode(String code) {
		return LocationLevel.findByCode(code, [cache: true])
	}
	
    DataLocationType findDataLocationTypeByCode(String code) {
    	return DataLocationType.findByCode(code, [cache: true])
    }
	
	List<LocationLevel> listLevels(Set<LocationLevel> skipLevels) {
		def levels = listLevels();
		if(skipLevels != null) levels.removeAll(skipLevels);		
		return levels;
	}
	
	
	List<DataLocation> getDataLocationsOfType(Set<CalculationLocation> locations,Set<DataLocationType> types){
		if (log.isDebugEnabled()) log.debug("List<DataLocation> getDataLocations(Set<CalculationLocation> "+locations+"Set<DataLocationType>"+types+")");
		def dataLocations= new ArrayList<DataLocation>()
		
		for(CalculationLocation location: locations){
			if(location instanceof DataLocation)
				if(!dataLocations.contains(location))
					dataLocations.add(location)
			if(location instanceof Location)
				for(DataLocation dataLocation: location.collectDataLocations(null,null))
					if(!dataLocations.contains(dataLocation))
						dataLocations.add(dataLocation)
		}
		
		if(types!=null && !types.isEmpty()){
			for(DataLocation dataLocation : new ArrayList(dataLocations))
				if(!types.contains(dataLocation.type))
					dataLocations.remove(dataLocation);
		}
		return dataLocations;
	}

	Long getNumberOfDataLocationsForType(DataLocationType type){
		def dataLocations = DataLocation.createCriteria().list(){eq("type",type)}
		return dataLocations.totalCount;
	}
	
	public <T extends CalculationLocation> List<T> searchLocation(Class<T> clazz, String text, Map<String, String> params) {
		def dbFieldName = 'names_'+languageService.getCurrentLanguagePrefix();
		def criteria = clazz.createCriteria()
		return criteria.list(offset:params.offset,max:params.max,sort:params.sort ?:dbFieldName,order: params.order ?:"asc"){
			createAlias("level","l")
			or{ 
				if(clazz instanceof Location )
					ilike("l."+dbFieldName,"%"+text+"%")
				ilike("code","%"+text+"%")
				ilike(dbFieldName,"%"+text+"%")
			}
		}
	}
	
	public <T extends CalculationLocation> T getCalculationLocation(Long id, Class<T> clazz) {
		return (T)sessionFactory.getCurrentSession().get(clazz, id);
	}
	
	public <T extends CalculationLocation> T findCalculationLocationByCode(String code, Class<T> clazz) {
		return (T) sessionFactory.getCurrentSession().createCriteria(clazz)
				.add(Restrictions.eq("code", code)).uniqueResult();
	}
	
	// TODO property of level?
	LocationLevel getLevelBefore(LocationLevel level, Set<LocationLevel> skipLevels) {
		def levels = listLevels();
		def intLevel = levels.indexOf(level);
		if(skipLevels != null){
			def intSkipLevels = new ArrayList<Integer>();
			for(def skipLevel : skipLevels)
				intSkipLevels.add(levels.indexOf(skipLevel));
			while(intLevel-1 >= 0 && intSkipLevels.contains(intLevel-1)){
				intLevel--;
			}
		}
		if (intLevel-1 >= 0) 
			return levels.get(intLevel-1);
		else return null;		
	}
	
	// TODO property of level?	
	LocationLevel getLevelAfter(LocationLevel level, Set<LocationLevel> skipLevels) {
		def levels = listLevels();
		def intLevel = levels.indexOf(level);
		if(skipLevels != null){
			def intSkipLevels = new ArrayList<Integer>();
			for(def skipLevel : skipLevels)
				intSkipLevels.add(levels.indexOf(skipLevel));
			while(intLevel+1 < levels.size() && intSkipLevels.contains(intLevel+1)){
				intLevel++;
			}
		}
		if (intLevel+1 < levels.size())
			return levels.get(intLevel+1);
		else return null;		
	}
	
	// TODO move to location
	Location getParentOfLevel(CalculationLocation location, LocationLevel level) {
		def tmp = location.parent;
		while (tmp != null) {
			if (tmp.level.equals(level)) return tmp;
			tmp = tmp.parent;
		}
		return null;
	}
	
	// TODO move to Location
	List<Location> getChildrenOfLevel(Location location, LocationLevel level) {
		def result = new ArrayList<Location>();
		collectChildrenOfLevel(location, level, result);
		return result;
	}

	private void collectChildrenOfLevel(Location location, LocationLevel level, List<Location> locations) {
		if (location.getLevel().equals(level)) locations.add(location);
		else {
			for (def child : location.getChildren()) {
				collectChildrenOfLevel(child, level, locations);
			}
		}
	}
	
}
