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

import org.chai.location.CalculationLocation
import org.chai.location.DataLocationType
import org.chai.location.DataLocation
import org.chai.location.Location
import org.chai.location.LocationLevel
import org.chai.memms.reports.dashboard.LocationReport;
import org.chai.memms.reports.dashboard.Indicator;
import org.chai.memms.reports.dashboard.IndicatorValue;
import org.chai.memms.reports.dashboard.MemmsReport;
import org.chai.memms.reports.dashboard.GroupIndicatorValue;
import org.chai.memms.util.Utils;
import org.chai.memms.util.Utils.ReportChartType;

import java.util.*
import org.chai.memms.security.User
import org.apache.shiro.SecurityUtils
import java.util.Collections;
import org.joda.time.format.DateTimeFormat
/**
 * @author Antoine Nzeyi, Donatien Masengesho, Pivot Access Ltd
 *
 */
class IndicatorItem {

    IndicatorComputationService indicatorComputationService
    String indicatorValueId
    String categoryCode
    Date computedAt
    String code
    String names
    String groupNames
    String formulas
    Double value
    String unit
    String color
    String locationNames

    List<HistoricalValueItem> historicalValueItems
    List<ComparisonValueItem> highestComparisonValueItems
    List<ComparisonValueItem> higherComparisonValueItems
    List<ComparisonValueItem> lowerComparisonValueItems
    List<ComparisonValueItem> lowestComparisonValueItems
    List<GeographicalValueItem> geographicalValueItems
    Map<String, Double> valuesPerGroup
    Integer totalHistoryItems

    public IndicatorItem(IndicatorValue indicatorValue, ReportChartType reportChartType) {
        this.indicatorValueId = indicatorValue.id
        this.categoryCode = indicatorValue.indicator.category.code
        this.computedAt = indicatorValue.computedAt
        this.code = indicatorValue.indicator.code
        this.names = indicatorValue.indicator.names
        this.formulas = indicatorValue.indicator.formulas
        this.value = indicatorValue.computedValue
        this.unit = indicatorValue.indicator.unit
        this.groupNames = indicatorValue.indicator.groupNames
        this.totalHistoryItems = indicatorValue.indicator.historyItems
        this.locationNames = indicatorValue.locationReport.location.names

        Double red = indicatorValue.indicator.redToYellowThreshold
        Double green =  indicatorValue.indicator.yellowToGreenThreshold
        if(red < green) {
            if(this.value < red) {
                this.color = "red"
            } else if(this.value < green) {
                this.color = "yellow"
            } else {
                this.color = "green"
            }
        } else {
            if(this.value > red) {
                this.color = "red"
            } else if(this.value > green) {
                this.color = "yellow"
            } else {
                this.color = "green"
            }
        }

        // add historical value items
        if(reportChartType == null || reportChartType == ReportChartType.HISTORIC){
            this.historicalValueItems = new ArrayList<HistoricalValueItem>()
            this.historicalValueItems.addAll(getHistoricValueItems(indicatorValue))
            if (log.isDebugEnabled()) {
                log.debug("IndicatorItem getHistoricalValueItems historical value items=" + historicalValueItems + ", size="+historicalValueItems?.size());
            }
        }

        // add geographical value items
        if(reportChartType == ReportChartType.GEOGRAPHIC){
            this.geographicalValueItems = new ArrayList<GeographicalValueItem>()
            this.geographicalValueItems.addAll(getGeographicalValueItems(indicatorValue))
            if (log.isDebugEnabled()) {
                log.debug("IndicatorItem getGeographicalValueItems geographical value items=" + geographicalValueItems + ", size="+geographicalValueItems?.size());
            }
        }

        // add comparison value items
        if(reportChartType == ReportChartType.COMPARISON){
            this.highestComparisonValueItems= new ArrayList<ComparisonValueItem>()
            this.higherComparisonValueItems= new ArrayList<ComparisonValueItem>()
            this.lowerComparisonValueItems= new ArrayList<ComparisonValueItem>()
            this.lowestComparisonValueItems= new ArrayList<ComparisonValueItem>()
            getComparisonValueItems(indicatorValue)
            // TODO combine lists
            if (log.isDebugEnabled()) {
                log.debug("IndicatorItem getComparisonValueItems comparison value items=" + highestComparisonValueItems + ", size="+highestComparisonValueItems?.size());
                log.debug("IndicatorItem getComparisonValueItems comparison value items=" + higherComparisonValueItems + ", size="+higherComparisonValueItems?.size());
                log.debug("IndicatorItem getComparisonValueItems comparison value items=" + lowerComparisonValueItems + ", size="+lowerComparisonValueItems?.size());
                log.debug("IndicatorItem getComparisonValueItems comparison value items=" + lowestComparisonValueItems + ", size="+lowestComparisonValueItems?.size());
            }
        }

        // add info by x value items
        if(reportChartType == ReportChartType.INFOBY){
            this.valuesPerGroup=new HashMap<String,Double>()
            for(GroupIndicatorValue grV:indicatorValue.groupIndicatorValues)
                this.valuesPerGroup.put(grV.names,grV.value)
            if (log.isDebugEnabled()) {
                log.debug("IndicatorItem getInfoByXValueItems info by x value items=" + valuesPerGroup + ", size="+valuesPerGroup?.size());
            }
        }
    }

    /**
     *Get historical values for this indicators = for different location reports
     */
    public def getHistoricValueItems(IndicatorValue indicatorValue) {
        def historicalValueItems = new ArrayList<HistoricalValueItem>();

        if(indicatorValue != null) {
            if (log.isDebugEnabled()) log.debug("getHistoricalValueItems period " + indicatorValue.indicator.historicalPeriod);

            def locationReports = null
            if(indicatorValue.indicator.historicalPeriod.equals(Indicator.HistoricalPeriod.MONTHLY)){
                locationReports = LocationReport.findAll("from LocationReport as locationReport  where locationReport.location.id='"+indicatorValue.locationReport.location.id+"' order by locationReport.eventDate desc limit "+indicatorValue.indicator.historyItems+"")
            } 
            else if(indicatorValue.indicator.historicalPeriod.equals(Indicator.HistoricalPeriod.QUARTERLY)){
                locationReports = LocationReport.findAll("from LocationReport as locationReport where month(locationReport.eventDate) in (3,6,9,12) and locationReport.location.id='"+indicatorValue.locationReport.location.id+"' order by locationReport.eventDate desc limit "+indicatorValue.indicator.historyItems+"")
            } 
            else if(indicatorValue.indicator.historicalPeriod.equals(Indicator.HistoricalPeriod.YEARLY)) {
                locationReports = LocationReport.findAll("from LocationReport as locationReport where month(locationReport.eventDate) = 12 and locationReport.location.id='"+indicatorValue.locationReport.location.id+"' order by locationReport.eventDate desc limit "+indicatorValue.indicator.historyItems+"")
            }

            // start TODO AR create indicator value service method
            def indicatorValues = IndicatorValue.findAllByLocationReportInListAndIndicator(locationReports,indicatorValue.indicator)
            indicatorValues.sort{ a,b -> (a.computedAt <=> b.computedAt) }
            // end TODO

            indicatorValues.each{ indV ->
                def historicalValueItem = new HistoricalValueItem(indV)
                historicalValueItems.add(historicalValueItem)
            }
            if (log.isDebugEnabled()) log.debug("getHistoricalValueItems historical value items =" + historicalValueItems + ", size="+historicalValueItems.size());
            return historicalValueItems
        }
        return null
    }

    public def historicalTrendData() {
        def ret = []

        def i = 0
        def fmt = DateTimeFormat.forPattern("MMM d, y")
        for(HistoricalValueItem h: historicalValueItems) {
            ret[i] = ["\""+fmt.print(h.computedAt.time)+"\"",h.value]
            i++
        }
        if(i == 0) {
            ret = null
        }

        if (log.isDebugEnabled()) log.debug("historicalTrendData ret=" + ret);
        return ret
    }

    public def historicalTrendVAxisFormat() {
        if (log.isDebugEnabled()) log.debug("historicalTrendVAxisFormat unit=" + unit);

        if(unit == '%') return '0%';
        return '###,##0 ' + unit;
    }

    public def historicalTrendMaxValue() {
        if (log.isDebugEnabled()) log.debug("historicalTrendMaxValue unit=" + unit);

        if(unit == '%') return ', maxValue: 1';
        return '';
    }

    public def historicalTrendLineCount() {
        if (log.isDebugEnabled()) log.debug("historicalTrendLineCount unit=" + unit);

        if(unit == '%') return ', count: 5';
        return ', count: -1';
    }

    public void getComparisonValueItems(IndicatorValue currentValue){
        List<IndicatorValue> invVs=new ArrayList<IndicatorValue>()
        if(currentValue!=null) {
            def  similarLocations=getSimilarLocations(currentValue)
            def locationReports=LocationReport.findAllByMemmsReportAndLocationInList(currentValue.locationReport.memmsReport,similarLocations)
            invVs.addAll(IndicatorValue.findAllByLocationReportInListAndIndicator(locationReports,currentValue.indicator))
            sortComparisonValues(currentValue,invVs)
        }
    }

    public void sortComparisonValues(IndicatorValue currentValue,List<IndicatorValue> invVs){
        Collections.sort(invVs);
        if(invVs.size() > 0) {
            List<IndicatorValue> listOfHighest=new ArrayList<IndicatorValue>()
            List<IndicatorValue> listOfLowest=new ArrayList<IndicatorValue>()
            Double red = currentValue.indicator.redToYellowThreshold
            Double green =  currentValue.indicator.yellowToGreenThreshold
            if(red < green) {
                for(IndicatorValue indicatorValue:invVs){
                    if(indicatorValue.computedValue <= currentValue.computedValue && indicatorValue.id!=currentValue.id){
                        listOfLowest.add(indicatorValue)
                    } else if(indicatorValue.computedValue >= currentValue.computedValue && indicatorValue.id!=currentValue.id){
                        listOfHighest.add(indicatorValue)
                    } else if(indicatorValue.id == currentValue.id) {
                        for(def i = 1 ; i <= 2 ; i++) {
                            if(invVs.indexOf(indicatorValue)+i<invVs.size())
                            this.higherComparisonValueItems.add(new ComparisonValueItem(invVs.get(invVs.indexOf(indicatorValue)+i)))
                            if(invVs.indexOf(indicatorValue)-i>=0)
                            this.lowerComparisonValueItems.add(new ComparisonValueItem(invVs.get(invVs.indexOf(indicatorValue)-i)))
                        }
                    }
                }
            } else {
                for(IndicatorValue indicatorValue:invVs){
                    if(indicatorValue.computedValue<=currentValue.computedValue  && indicatorValue.id!=currentValue.id){
                        listOfHighest.add(indicatorValue)
                    }else if(indicatorValue.computedValue >= currentValue.computedValue  && indicatorValue.id!=currentValue.id){
                        listOfLowest.add(indicatorValue)
                    }else if(indicatorValue.id == currentValue.id){
                        for(def i=1 ; i<=2 ; i++){
                            if(invVs.indexOf(indicatorValue)+i<invVs.size())
                            this.lowerComparisonValueItems.add(new ComparisonValueItem(invVs.get(invVs.indexOf(indicatorValue)+i)))
                            if(invVs.indexOf(indicatorValue)-i>=0)
                            this.higherComparisonValueItems.add(new ComparisonValueItem(invVs.get(invVs.indexOf(indicatorValue)-i)))
                        }
                    }
                }
            }
            def lowestCounter=0
            def highestcounter=0
            for(IndicatorValue indicatorValue: listOfLowest.reverse()){
                if(lowestCounter < 3)
                this.lowestComparisonValueItems.add(new ComparisonValueItem(indicatorValue))
                lowestCounter++
            }
            for(IndicatorValue indicatorValue:listOfHighest.reverse()){
                if(highestcounter < 3)
                this.highestComparisonValueItems.add(new ComparisonValueItem(indicatorValue))
                highestcounter++
            }
        }
        this.higherComparisonValueItems.reverse()
    }

    public def getLocationsIdWithUsers(){
        List<Long> locations=new ArrayList<Long>();
        for(User user:User.findAll()){
            if(user.location!=null)
            locations.add(user.location.id)
        }
        return locations
    }

    /**
     *Gets geographical values for the current report
     **/
    public def getGeographicalValueItems(IndicatorValue indicatorValue){
        def geographicalValueItems = new ArrayList<GeographicalValueItem>();

        if(indicatorValue!=null){
            def geographicalValueItem = new GeographicalValueItem(indicatorValue)
            geographicalValueItems.add(geographicalValueItem)

            def dataLocations = indicatorValue.locationReport.location.collectDataLocations(null)
            def memmsReport = indicatorValue.locationReport.memmsReport
            def locationReports=LocationReport.findAllByMemmsReportAndLocationInList(memmsReport,dataLocations)
            if (log.isDebugEnabled()) log.debug("getGeographicalValueItems location report items=" + locationReports + ", size="+locationReports.size());
            def indicatorValues = IndicatorValue.findAllByLocationReportInListAndIndicator(locationReports,indicatorValue.indicator)
            indicatorValues.each{ indV ->
                geographicalValueItem = new GeographicalValueItem(indV)
                geographicalValueItems.add(geographicalValueItem)
            }
            if (log.isDebugEnabled()) log.debug("getGeographicalValueItems geographical value items=" + geographicalValueItems + ", size="+geographicalValueItems.size());
            return geographicalValueItems
        }
        return null
    }

    /**
     *Gets  facilities or locations similar to the curent facility/location from this indicator value
     */
    public def getSimilarLocations(IndicatorValue indicatorValue){
        def  similarLocations=[]
        def locationIds=getLocationsIdWithUsers()
        if(indicatorValue!=null){
            // if(indicatorValue.locationReport.location instanceof DataLocation){
            //     similarLocations=DataLocation.findAllByTypeAndIdInList(indicatorValue.locationReport.location.type,locationIds)
            // } else if(indicatorValue.locationReport.location instanceof Location){
            //     similarLocations=Location.findAllByLevelAndIdInList(indicatorValue.locationReport.location.level,locationIds)
            // }
            def location = indicatorValue.locationReport.location
            if (log.isDebugEnabled()) log.debug("getSimilarLocations location="+location);
            // similarLocations = location.collectTreeWithDataLocations(null,null)
            similarLocations.addAll(location.collectDataLocations(null))
        }
        if (log.isDebugEnabled()) log.debug("getSimilarLocations similarLocations size="+similarLocations?.size());
        return similarLocations
    }

    public geoData() {
        def ret = []

        if(this.geographicalValueItems == null || this.geographicalValueItems.empty){
            this.geographicalValueItems.addAll(getGeographicalValueItems(indicatorValue))
        }

        ret.add(["\'Lat\'", "\'Long\'", "\'Location\'", "\'"+names+"\'"])
        geographicalValueItems.eachWithIndex{ geoValueItem, i ->
            if((geoValueItem.latitude != null) && (geoValueItem.latitude != 0.0) && 
                    (geoValueItem.longitude != null) && (geoValueItem.longitude != 0.0))
                ret.add(geoValueItem.geoDataRow())
        }

        if (log.isDebugEnabled()) log.debug("geoData ret=" + ret);
        return ret
    }

    public geoValues() {

        if(this.geographicalValueItems == null || this.geographicalValueItems.empty){
            this.geographicalValueItems.addAll(getGeographicalValueItems(indicatorValue))
        }

        Map<Double,String> map = new TreeMap<Double,String>()
        geographicalValueItems.each{ geoValueItem ->
            map.put(geoValueItem.value,geoValueItem.color)
        }

        def ret = []
        map.keySet().eachWithIndex{ val, i ->
            ret[i++] = val
        }

        if (log.isDebugEnabled()) log.debug("geoValues ret=" + ret);
        return ret
    }

    public def geoValuesFormat() {
        if (log.isDebugEnabled()) log.debug("geoValuesFormat unit=" + unit);

        if(unit == '%') return '0%';
        return '###,##0 ' + unit;
    }

    public geoColors() {
        Map<Double,String> map = new TreeMap<Double,String>()

        if(this.geographicalValueItems == null || this.geographicalValueItems.empty){
            this.geographicalValueItems.addAll(getGeographicalValueItems(indicatorValue))
        }

        geographicalValueItems.each{ geoValueItem ->
            map.put(geoValueItem.value,geoValueItem.color)
        }

        def ret = []
        map.keySet().eachWithIndex{ val, i ->
            ret[i++] = "\'"+map.get(val)+"\'"
        }

        if (log.isDebugEnabled()) log.debug("geoColors ret=" + ret);
        return ret
    }

        @Override
    public String toString() {
        return "IndicatorItem[@" + computedAt?.getTime() + ", code = " + code + "]"
    }
}