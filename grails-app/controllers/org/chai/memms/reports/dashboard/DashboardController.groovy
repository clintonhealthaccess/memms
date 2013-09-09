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
import org.chai.memms.AbstractController
import org.chai.memms.inventory.EquipmentType
import org.chai.memms.security.User
import org.chai.memms.util.Utils.ReportType
import org.chai.memms.util.Utils.ReportChartType

import java.lang.reflect.*
import org.apache.shiro.SecurityUtils

/**
 * @author Antoine Nzeyi, Donatien Masengesho, Pivot Access Ltd
 *
 */
class DashboardController extends AbstractController {

    def dashboardService
    def indicatorComputationService

    def index ={
        redirect(action: "view", params: params)
    }

    def view ={
        redirect(action: "dashboard", params: params)
    }

    def dashboard = {
        //indicatorComputationService.computeCurrentReport()

        LocationReport report = getUserReport()
        def reportType = getReportType()
        
        def category = IndicatorCategory.findByCode(reportType.reportType)
        def categoryItem = new CategoryItem(category, new ArrayList<IndicatorItem>())

        if(report != null) {
            Set<IndicatorValue> indicatorValues = report.indicatorValues.findAll{ it.indicator.category.equals(category) }
            if(indicatorValues != null && !indicatorValues.empty){
                indicatorValues.each { indicatorValue ->
                    def indicatorItem = new IndicatorItem(indicatorValue, null)
                    categoryItem.indicatorItems.add(indicatorItem)
                }
            }
        }

        def model = [
            reportType: reportType,
            categoryItem: categoryItem
        ]

        render(view:"/reports/reports", 
            model:model << [

                template:"/reports/dashboard/dashboard"
                // filterTemplate:"/reports/dashboard/dashboardFilter"
            ])
    }

    def dashboardChart= {
        if (log.isDebugEnabled())
            log.debug("dashboard.dashboardChart start, params:"+params)

        def reportType = getReportType()
        def reportChartType = getReportChartType()

        def category = IndicatorCategory.findByCode(reportType.reportType)
        def indicatorValue = IndicatorValue.get(params.get('indicatorValueId'))

        def indicatorItem = null
        if(indicatorValue != null){
            indicatorItem = new IndicatorItem(indicatorValue, reportChartType)
        }

        def template = null
        switch(reportChartType){
            case ReportChartType.HISTORIC:
                template = "historicTrend"
                break;
            case ReportChartType.COMPARISON:
                template = "comparisonChart"
                break;
            case ReportChartType.GEOGRAPHIC:
                template = "geographicTrend"
                break;
            case ReportChartType.INFOBY:
                template = "infoByChart"
        }

        template = "/reports/dashboard/"+template
        if (log.isDebugEnabled()) log.debug("dashboard.dashboardChart template:"+template)

        if (log.isDebugEnabled()) log.debug("dashboard.dashboardChart end, indicatorItem:"+indicatorItem)

        render(template:template,
        model:[
            indicatorItem: indicatorItem
        ])
    }
       
    def getUserReport() {
        MemmsReport memmsReport = dashboardService.getCurrentMemmsReport()
        if(memmsReport == null) return null

        User user =  User.findByUuid(SecurityUtils.subject.principal, [cache: true])
        if(user == null) return null

        return LocationReport.findByMemmsReportAndLocation(memmsReport, user.location)
    }

    def getReportType(){
        ReportType reportType = null
        if(params.get('reportType') != null && !params.get('reportType').empty)
            reportType = params.get('reportType')
        if(log.isDebugEnabled()) log.debug("abstract.reportType param:"+reportType+")")
        if(reportType == null) reportType = ReportType.PREVENTIVE
        return reportType
    }

    def getReportChartType(){
        ReportChartType reportChartType = null
        if(params.get('reportChartType') != null && !params.get('reportChartType').empty)
            reportChartType = params.get('reportChartType')
        if(log.isDebugEnabled()) log.debug("abstract.reportChartType param:"+reportChartType+")")
        if(reportChartType == null) reportChartType = ReportChartType.HISTORIC
        return reportChartType
    }
}