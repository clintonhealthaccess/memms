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

import org.chai.memms.reports.dashboard.LocationReport;
import org.chai.memms.reports.dashboard.IndicatorCategory;
/**
 * @author Antoine Nzeyi, Donatien Masengesho, Pivot Access Ltd
 *
 */
class CategoryItem {

    String code
    Date computedAt
    String names
    Double value
    String color
    List<IndicatorItem> indicatorItems

    public CategoryItem(IndicatorCategory cat, List<IndicatorItem> items) {
        this.indicatorItems = new ArrayList<IndicatorItem>()
        this.code = cat.code
        this.names = cat.names
        Double totalValue = 0.0
        Integer counter = 0
        for(IndicatorItem i : items) {
            if(i.categoryCode.equals(this.code)) {
                if("green".equals(i.color)) {
                    totalValue += 2.0
                }else if("yellow".equals(i.color)) {
                    totalValue += 1.0
                }
                counter++
                this.indicatorItems.add(i)
            }
        }
        if(counter > 0) {
            this.value = (1.0 * totalValue) / (2.0 * counter)
            Double red = cat.redToYellowThreshold
            Double green =  cat.yellowToGreenThreshold
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
    }
}