

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
import org.chai.memms.IntegrationTests
import org.chai.memms.Initializer
import org.chai.location.DataLocation
/**
 *
 * @author Kahigiso M. Jean
 *
 */
class IndicatorValueSpec  extends IntegrationTests{
	
	  def "can create and save Indicator Value with valid fields"() {
          setup:
          setupLocationTree()
          setupSystemUser()
          def now = Initializer.now()
          def memmsReport = DashboardInitializer.newMemmsReport(now)         
          def locationReport = DashboardInitializer.newLocationReport(memmsReport,now,DataLocation.findByCode(BUTARO))
          def category = DashboardInitializer.newIndicatorCategory(
              DashboardInitializer.PREVENTIVE_MAINTENANCE,
              ["en":"preventive maintenance en","fr":"preventive maintenance fr"],
              60,80
            )
          def indicator = DashboardInitializer.newIndicator(
            category,
            "SHARE_OPE_EQUIPMENT", 
            ["en":"Share of operational equipment","fr":"Share of operational equipment fr"],
            ["en":"Share of operational equipment","fr":"Share of operational equipment fr"], 
            [
            "en":"(total number equipment with STATUS=Operational) / by (total number equipment with STATUS={Operational Partially operational Under maintenance}) fr", 
            "fr":"(total number equipment with STATUS=Operational) / by (total number equipment with STATUS={Operational Partially operational Under maintenance}) fr"],
            "%", 0.8, 0.9, Indicator.HistoricalPeriod.QUARTERLY, 8, DashboardInitializer.SHARE_OPERATIONAL_SIMPLE_SLD7, 
            ["en":"Type of Equipment","fr":"Type of Equipment fr"], 
            DashboardInitializer.SHARE_OPERATIONAL_GROUP_SLD7,
            false, true
            )         
          when:
          def indicatorValue = new IndicatorValue(computedAt: now, locationReport: locationReport, indicator: indicator, computedValue:30.0)
          indicatorValue.save()
          then:
          IndicatorValue.count() == 1    
    }

    def "can't create and save Indicator Value without required fiels"() {
          setup:
          setupLocationTree()
          setupSystemUser()
          def now = Initializer.now()
          def memmsReport = DashboardInitializer.newMemmsReport(now)         
          def locationReport = DashboardInitializer.newLocationReport(memmsReport,now,DataLocation.findByCode(BUTARO))
          def category = DashboardInitializer.newIndicatorCategory(
              DashboardInitializer.PREVENTIVE_MAINTENANCE,
              ["en":"preventive maintenance en","fr":"preventive maintenance fr"],
              60,80
            )
          def indicator = DashboardInitializer.newIndicator(
            category,
            "SHARE_OPE_EQUIPMENT", 
            ["en":"Share of operational equipment","fr":"Share of operational equipment fr"],
            ["en":"Share of operational equipment","fr":"Share of operational equipment fr"], 
            [
            "en":"(total number equipment with STATUS=Operational) / by (total number equipment with STATUS={Operational Partially operational Under maintenance}) fr", 
            "fr":"(total number equipment with STATUS=Operational) / by (total number equipment with STATUS={Operational Partially operational Under maintenance}) fr"],
            "%", 0.8, 0.9, Indicator.HistoricalPeriod.QUARTERLY, 8, DashboardInitializer.SHARE_OPERATIONAL_SIMPLE_SLD7, 
            ["en":"Type of Equipment","fr":"Type of Equipment fr"], 
            DashboardInitializer.SHARE_OPERATIONAL_GROUP_SLD7,
            false, true
            )   
          def indicatorValue      
          when:"computedAt"
          indicatorValue = new IndicatorValue(locationReport: locationReport, indicator: indicator, computedValue:30.0)
          indicatorValue.save()
          then:
          IndicatorValue.count() == 0
          indicatorValue.errors.hasFieldErrors('computedAt') == true

          when:"computedValue"
          indicatorValue = new IndicatorValue(computedAt: now, locationReport: locationReport, indicator: indicator)
          indicatorValue.save()
          then:
          IndicatorValue.count() == 0
          indicatorValue.errors.hasFieldErrors('computedValue') == true

          when:"locationReport"
          indicatorValue = new IndicatorValue(computedAt: now, indicator: indicator, computedValue:30.0)
          indicatorValue.save()
          then:
          IndicatorValue.count() == 0
          indicatorValue.errors.hasFieldErrors('locationReport') == true

          when: "indicator"
          indicatorValue = new IndicatorValue(computedAt: now, locationReport: locationReport, computedValue:30.0)
          indicatorValue.save()
          then:
          IndicatorValue.count() == 0
          indicatorValue.errors.hasFieldErrors('indicator') == true
    }
    
}

