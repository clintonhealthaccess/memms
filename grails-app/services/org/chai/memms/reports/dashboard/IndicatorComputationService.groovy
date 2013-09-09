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
package org.chai.memms.reports.dashboard
import groovy.sql.Sql
import java.util.List
import java.util.regex.Matcher
import java.util.regex.Pattern
import org.chai.location.CalculationLocation
import org.chai.location.DataLocation
import org.chai.location.Location
import org.chai.location.LocationService
import org.joda.time.DateTime
import org.chai.location.LocationLevel
import org.chai.memms.security.User
import java.sql.ResultSet
import org.chai.memms.util.Utils;
/**
 * 
 *@author Antoine Nzeyi, Donatien Masengesho, Pivot Access Ltd
 *
 */
class IndicatorComputationService {
    static transactional = true
    def sessionFactory
    def equipmentService
    def indicatorValueService
    def locationReportService
    def locationService
    def grailsApplication = new User().domainClass.grailsApplication

    static final String DATA_LOCATION_TOKEN = "@DATA_LOCATION"
    static final String USER_DEFINED_VARIABLE_REGEX = "#[0-9a-zA-Z_]+"
    static final Pattern userDefinedVariablePattern = Pattern.compile(USER_DEFINED_VARIABLE_REGEX)
    def  skipLevelCode = grailsApplication.config.location.sector.skip.level[0]

    public def computeCurrentReport() {
        def skippedLevel =  LocationLevel.findByCode(skipLevelCode)
        def memmsReports = []

        if (log.isDebugEnabled()) log.debug("computeCurrentReport start memmsReports " + memmsReports);

        // 1. GET DATES
        def reportDates = []

        // year and half year reports
        def oneYearAgo = DateTime.now().minusYears(1)
        def sixMonthsAgo = DateTime.now().minusMonths(6)
        //reportDates = [oneYearAgo, sixMonthsAgo]

        // quarterly reports
        // def fourQuartersAgo = DateTime.now().minusMonths(12) ==> same as oneYearAgo
        def threeQuartersAgo = DateTime.now().minusMonths(9)
        // def twoQuartersAgo = DateTime.now().minusMonths(6) ==> same as sixMonthsAgo
        def lastQuarter = DateTime.now().minusMonths(3)

        // monthly reports
        // def sixMonthsAgo = DateTime.now().minusMonths(6) ==> same as sixMonthsAgo
        def fiveMonthsAgo = DateTime.now().minusMonths(5)
        def fourMonthsAgo = DateTime.now().minusMonths(4)
        // def threeMonthsAgo = DateTime.now().minusMonths(3) ==> same as lastQuarter

        def twoMonthsAgo = DateTime.now().minusMonths(2)
        def lastMonth = DateTime.now().minusMonths(1)

        // today and yesterday reports
        def today = DateTime.now()
        def yesterday = null //if "yesterday" is in the same month as "today", it will be computed, but will be removed when "today" is computed
        if(today.minus(1).getMonthOfYear() == today.getMonthOfYear()){
            yesterday = today.minus(1)
            reportDates.add(yesterday)
        }

        // These are only needed for dev historic trend chart
        // reportDates.addAll([oneYearAgo, sixMonthsAgo])
        // reportDates.addAll([threeQuartersAgo, lastQuarter])
        // reportDates.addAll([fiveMonthsAgo, fourMonthsAgo, twoMonthsAgo, lastMonth])

        reportDates.add(today)
        if (log.isDebugEnabled()) log.debug("computeCurrentReport reportDates " + reportDates);

        // 1. GET LOCATIONS WITH EQUIPMENTS
        def rootLocation = locationService.getRootLocation() 
        if (log.isDebugEnabled()) log.debug("computeCurrentReport rootLocation " + rootLocation);
        def locationsWithEquipment = null
        if(rootLocation != null){
            //TODO optimize this method !
            locationsWithEquipment = rootLocation.collectTreeWithDataLocations((skippedLevel!=null)?[skippedLevel]:null,null)
            if (log.isDebugEnabled()) log.debug("computeCurrentReport locationsWithEquipment " + locationsWithEquipment);
        }
        def dataLocationsWithEquipment = equipmentService.getDataLocationsWithEquipments()
        if (log.isDebugEnabled()) log.debug("computeCurrentReport dataLocationsWithEquipment " + dataLocationsWithEquipment);
        //TODO optimize this method !
        locationsWithEquipment.addAll(dataLocationsWithEquipment)

        reportDates.each{ reportDate ->
            // 2. REMOVE PREVIOUS REPORTS IN THE SAME MONTH
            GroupIndicatorValue.executeUpdate("delete from GroupIndicatorValue where month(generatedAt) = " + reportDate.getMonthOfYear() + " and year(generatedAt) = " + reportDate.getYear())
            IndicatorValue.executeUpdate("delete from IndicatorValue where month(computedAt) = " + reportDate.getMonthOfYear() + " and year(computedAt) = " + reportDate.getYear())
            LocationReport.executeUpdate("delete from LocationReport where month(eventDate) = " + reportDate.getMonthOfYear() + " and year(eventDate) = " + reportDate.getYear())
            MemmsReport.executeUpdate("delete from MemmsReport where month(eventDate) = " + reportDate.getMonthOfYear() + " and year(eventDate) = " + reportDate.getYear())
            
            // 3. SAVE NEW MEMMS REPORT
            MemmsReport memmsReport = new MemmsReport(eventDate: reportDate.toDate()).save()

            // 4. COMPUTE MEMMS LOCATION REPORTS FOR ALL LOCATIONS WITH EQUIPMENTS
            for(def locationWithEquipment : locationsWithEquipment){
                    if (log.isDebugEnabled()) log.debug("computeCurrentReport memmsReport "+memmsReport);
                    computeLocationReport(memmsReport.eventDate, locationWithEquipment, memmsReport)
                    if (log.isDebugEnabled()) log.debug("computeCurrentReport memmsReport "+memmsReport);
            }
            memmsReports.add(memmsReport)

        }

        if (log.isDebugEnabled()) log.debug("computeCurrentReport done memmsReports " + memmsReports);
    }

     def computeLocationReport(Date currentDate, CalculationLocation location, MemmsReport memmsReport) {
        def locationReport = locationReportService.newLocationReport(memmsReport,currentDate,location)
        if (log.isDebugEnabled()) log.debug("computeLocationReport new location report for " + location);
        def indicators = Indicator.findAllByActive(true)
        for(Indicator indicator: indicators) {
            if (log.isDebugEnabled()) log.debug("computeLocationReport calculating report " + indicator.code + " for " + location.names);
            try{
				def compvalue = this.computeIndicatorForLocation(indicator, location)
				def indicatorValue = indicatorValueService.newIndicatorValue(currentDate,locationReport,indicator, compvalue)
                if(indicatorValue!=null) {
                    if (log.isDebugEnabled()) log.debug(">> map building indicatorValue =" + indicatorValue + " location = " + location);
                    Map<String,Double> map = this.groupComputeIndicatorForLocation(indicatorValue.indicator,location)
                    if (log.isDebugEnabled()) log.debug(">> built map =" + map );
                    if(map!=null) {
                        for (Map.Entry<String, Double> entry : (Set)map.entrySet()){
                            if (log.isDebugEnabled()) log.debug("computeLocationReport entry.getKey() " + entry.getKey() + " entry.getValue() " + entry.getValue());
                            DashboardInitializer.newGroupIndicatorValue(currentDate,['en':entry.getKey(),'fr':entry.getKey()],entry.getValue(),indicatorValue)
                            if (log.isDebugEnabled()) log.debug("groupIndicatorValue +++====>" + groupIndicatorValue);
                        }
                          
                    }
                }

            } catch(Exception ex) {
                ex.printStackTrace()
            }
            if (log.isDebugEnabled()) log.debug("computeLocationReport done calculating report " + indicator.code + " for " + location.names);
        }
    }

    def computeIndicatorForLocation(Indicator indicator, CalculationLocation location) {
        if (location == null) {
            return 0.0
        }
        List<DataLocation> dataLocations = new ArrayList<DataLocation>()
        if (location instanceof Location) {
            dataLocations = location.collectDataLocations(null)
        } else if (location instanceof DataLocation) {
            dataLocations.add(location)
            if(location.manages !=null) dataLocations.addAll(location?.manages)
        } else {
            return 0.0
        }
        if(dataLocations!=null && dataLocations.size() == DataLocation.count()) {
            return computeIndicatorForAllDataLocations(indicator)
        }
        return computeIndicatorForDataLocations(indicator, dataLocations)
    }

    def computeIndicatorForDataLocations(Indicator indicator, def dataLocations) {
        if(dataLocations == null)
        return 0.0
        String cond = "";
        int counter = 0
        for(DataLocation loc : dataLocations) {
            if(counter > 0) {
                cond += ", "
            }
            cond += "" + loc.id
            counter++
        }
        if(counter == 0) {
            return 0.0
        } else if(counter == 1) {
            return computeIndicatorWithDataLocationCondition(indicator, " = " + cond + " ")
        } else {
            return computeIndicatorWithDataLocationCondition(indicator, " in (" + cond + ") ")
        }
    }

    def computeIndicatorForAllDataLocations(Indicator indicator) {
        if (log.isDebugEnabled()) log.debug("computing IndicatorForAllDataLocations indicator= " + indicator.code );
        return computeIndicatorWithDataLocationCondition(indicator, " is not null ")
    }

    def computeIndicatorWithDataLocationCondition(Indicator indicator, String dataLocationCondition) {
        String mScript = indicator.queryScript
        Matcher m =  userDefinedVariablePattern.matcher(indicator.queryScript)
        while (m.find()) {
            String code = m.group()
            Double value = UserDefinedVariable.findByCode(code.replace("#", "")).currentValue
            mScript = mScript.replace(code, "" + value)
        }
        return computeScriptWithDataLocationCondition(mScript, indicator.sqlQuery, dataLocationCondition)
    }

    def computeScriptWithDataLocationCondition(String script, Boolean sql, String dataLocationCondition) {
        return computeScript(script.replace(DATA_LOCATION_TOKEN, dataLocationCondition), sql);
    }

    def computeScript(String script, Boolean sql) {
        if(sql) {
            return executeSQL(script)
        } else {
            return executeHQL(script)
        }
    }

    def executeHQL(String hql) {
        def ret = 0.0
        def res =  sessionFactory.getCurrentSession().createQuery(hql).list()[0]
        if(res != null) {
            ret = res
        }
        return ret
    }

    def executeSQL(String sql) {
        def ret = 0.0
        def res =  sessionFactory.getCurrentSession().createSQLQuery(sql).list()[0]
        if(res != null) {
            ret = res
        }
        return ret
    }


    def groupComputeIndicatorForLocation(Indicator indicator, CalculationLocation location) {
        if (log.isDebugEnabled()) log.debug("groupComputeIndicatorForLocation indicator " + indicator + ", location " + location);

        if (location == null) {
            return  null
        }
        List<DataLocation> dataLocations = new ArrayList<DataLocation>()
        if (location instanceof Location) {
			//To be checked by Aphrorwa
			//dataLocations = location.getDataLocations(LocationLevel.findAll(), null)
            dataLocations.addAll(location.collectDataLocations(null))

        } else if (location instanceof DataLocation) {
            dataLocations.add(location)
            if(location.manages != null) dataLocations.addAll(location.manages)
        } else {
            return null
        }
        if ((dataLocations != null) && (dataLocations.size() == DataLocation.count())) {
            return groupComputeIndicatorForAllDataLocations(indicator)
        }
		if (log.isDebugEnabled()) log.debug("LOCATION SIZE AS EXPECTED BY APHRO " + dataLocations.size());
        return groupComputeIndicatorForDataLocations(indicator, dataLocations)
    }

    def groupComputeIndicatorForDataLocations(Indicator indicator, def dataLocations) {
        if (log.isDebugEnabled()) log.debug("groupComputeIndicatorForDataLocations indicator " + indicator + ", dataLocations " + dataLocations);

        if(dataLocations == null)
            return null
        
        String cond = "";
        int counter = 0
        for(DataLocation loc : dataLocations) {
            if(counter > 0) {
                cond += ", "
            }
            cond += "" + loc.id
            counter++
        }
        if (log.isDebugEnabled()) log.debug("groupComputeIndicatorForDataLocations cond = " + cond);

        if(counter == 0) {
            return null
        } else if(counter == 1) {
            return groupComputeIndicatorWithDataLocationCondition(indicator, " = " + cond + " ")
        } else {
            return groupComputeIndicatorWithDataLocationCondition(indicator, " in (" + cond + ") ")
        }
    }

    def groupComputeIndicatorForAllDataLocations(Indicator indicator) {
        if (log.isDebugEnabled()) log.debug("groupComputeIndicatorForAllDataLocations indicator " + indicator);
        
        return groupComputeIndicatorWithDataLocationCondition(indicator, " is not null ")
    }

    def groupComputeIndicatorWithDataLocationCondition(Indicator indicator, String dataLocationCondition) {
        if (log.isDebugEnabled()) log.debug("groupComputeIndicatorWithDataLocationCondition indicator " + indicator + ", dataLocationCondition " + dataLocationCondition);

        String mScript = ""
        if(indicator.groupQueryScript != null) {
            mScript = indicator.groupQueryScript
            Matcher m =  userDefinedVariablePattern.matcher(indicator.groupQueryScript)
            while (m.find()) {
                String code = m.group()
                Double value = UserDefinedVariable.findByCode(code.replace("#", "")).currentValue
                mScript = mScript.replace(code, "" + value)
            }
        
            return groupComputeScriptWithDataLocationCondition(mScript, indicator.sqlQuery, dataLocationCondition)
        }
        return null
    }

    def groupComputeScriptWithDataLocationCondition(String script, Boolean sql, String dataLocationCondition) {
        if (log.isDebugEnabled()) log.debug("groupComputeScriptWithDataLocationCondition script " + script + ", dataLocationCondition " + dataLocationCondition);

        return groupComputeScript(script.replace(DATA_LOCATION_TOKEN, dataLocationCondition), sql);
    }

    def groupComputeScript(String script, Boolean sql) {
        if (log.isDebugEnabled()) log.debug("groupComputeScript script " + script + ", sql " + sql);
      
        if(sql) {
            return groupExecuteSQL(script)
        } else {
            return groupExecuteHQL(script)
        }
    }

    def groupExecuteHQL(String hql) {
        if (log.isDebugEnabled()) log.debug("groupExecuteHQL hql " + hql);

        Map<String, Double> map = new HashMap<String, Double>()
        if(hql!=null) {
            def res =  sessionFactory.getCurrentSession().createQuery(hql).list()
            if(res[0] != null) {
                for(Object arr : res) {
                    if (log.isDebugEnabled()) log.debug("groupExecuteHQL arr " + arr);
                    
                    map.put((String)arr[0], (Double)arr[1])
                }
            }
        }
        return map
    }

    def groupExecuteSQL(String sql) {
        if (log.isDebugEnabled()) log.debug("groupExecuteHQL sql " + sql);
         
        Map<String, Double> map = new HashMap<String, Double>()
        if(sql != null) {
            def res = sessionFactory.getCurrentSession().createSQLQuery(sql).list()
            if(res[0] != null) {
                for(Object arr : res) {
                    if (log.isDebugEnabled()) log.debug("groupExecuteSQL arr " + arr);
                    if(Double.parseDouble(arr[1] + ""))
                    map.put(arr[0], (Double)arr[1])
                }
            }
        }
        return map
    }
}