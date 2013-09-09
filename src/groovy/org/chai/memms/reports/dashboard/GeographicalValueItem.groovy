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

import org.chai.location.Location

import java.util.StringTokenizer;
import java.util.regex.Matcher
import java.util.regex.Pattern
/**
 * @author Antoine Nzeyi, Donatien Masengesho, Pivot Access Ltd
 *
 */
class GeographicalValueItem {

    String dataLocation
    Double value
    String unit
    String color
    double latitude
    double longitude

    def geoDataRow() {
        def row = [latitude,longitude,"\'"+dataLocation+"\'",value]
        if (log.isDebugEnabled()) log.debug("geoDataRow row " + row);
        return row
    }

    public GeographicalValueItem(IndicatorValue iv){
        this.value = iv.computedValue
        this.unit = iv.indicator.unit

        def location = iv.locationReport.location
        this.dataLocation = location.names

        def coordinates = location.coordinates
        if (log.isDebugEnabled()) log.debug("GeographicalValueItem coordinates " + coordinates);

        if(coordinates != null && !coordinates.empty){
            if(location instanceof Location){
                
                // calculate the centroid
                // double xMin = 0
                // double xMax = 0
                // double yMin = 0
                // double yMax = 0

                double totalLatCoordinates = 0
                double totalLngCoordinates = 0
                double totalNumberOfCoordinates = 0

                def latlonRegex = /\[(\-|\d|\.)*,(\-|\d|\.)*\]/
                def regionCoordinates = (coordinates =~ latlonRegex).collect{ it[0] } // http://naleid.com/blog/2008/05/19/dont-fear-the-regexp/
                if (log.isDebugEnabled()) log.debug("GeographicalValueItem regionCoordinates " + regionCoordinates[0]);

                // int i = 0
                regionCoordinates.each{ regionCoordinate ->
                    def coordinate = regionCoordinate.replaceAll(~/(\[|\])/,"")
                    double lng = coordinate.split(',')[0].toDouble();
                    double lat = coordinate.split(',')[1].toDouble();

                    // if(i == 0){
                    //     xMin = lat
                    //     xMax = lat
                    //     yMin = lng
                    //     yMax = lng
                    // }
                    // else{
                    //     if(lat < xMin) xMin = lat
                    //     if(lat > xMax) xMax = lat
                    //     if(lng < yMin) yMin = lng
                    //     if(lng > yMax) yMax = lng
                    // }
                    // i++

                    totalLatCoordinates += lat;
                    totalLngCoordinates += lng;
                    totalNumberOfCoordinates++;
                }

                def latCentroid = totalLatCoordinates / totalNumberOfCoordinates
                def lngCentroid = totalLngCoordinates / totalNumberOfCoordinates

                // def latCentroid = xMin + ((xMax - xMin) / 2);
                // def lngCentroid = yMin + ((yMax - yMin) / 2);

                this.latitude = latCentroid
                this.longitude = lngCentroid
            }
            else{

                def coordinate = coordinates.replaceAll(~/(\[|\])/,"")
                def lng = coordinate.split(',')[0];
                def lat = coordinate.split(',')[1];
                this.latitude = lat.toDouble();
                this.longitude = lng.toDouble();
            }
        }

        Double red = iv.indicator.redToYellowThreshold
        Double green =  iv.indicator.yellowToGreenThreshold
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
    }

    @Override
    public String toString() {
        return "GeographicalValueItem[Latitude:"+this.latitude+" Longitude:"+this.longitude+" Location:" +this.dataLocation+" Value:"+this.value+"]"
    }
}