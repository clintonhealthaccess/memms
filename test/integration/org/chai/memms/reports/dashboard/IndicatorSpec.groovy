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
import org.chai.memms.reports.dashboard.Indicator
import org.chai.memms.reports.dashboard.DashboardInitializer
import org.chai.memms.IntegrationTests

/**
 *
 * @author Kahigiso M. Jean
 */

class IndicatorSpec extends IntegrationTests{
  
	   def "can create and save indicator with all valid fields"() {
          setup:
          def userDefinedVariableTwo = DashboardInitializer.newUserDefinedVariable(CODE(123),["en":"User Defined Variable test ","fr":"User Defined Variable test fr"],9.0)
          def category=new IndicatorCategory(code:DashboardInitializer.CORRECTIVE_MAINTENANCE+"test",names:['en':"Corrective maintenance",'fr':"Corrective maintenance"],redToYellowThreshold:60,yellowToGreenThreshold:80).save()

          when:
          def validIndicatorOne = DashboardInitializer.newIndicator(
            category, 
            "SHARE_OPE_EQUIPMENTtes",
            ['en':"Share of operational equipment",'fr':"Share of operational equipment"], 
            ['en':"Share of operational equipment",'fr':"Share of operational equipment"], 
            [
              'en':"(total number equipment with STATUS=Operational) / by (total number equipment with STATUS={Operational Partially operational Under maintenance})", 
              'fr':"(total number equipment with STATUS=Operational) / by (total number equipment with STATUS={Operational Partially operational Under maintenance})"
            ],
            "%", 0.8, 0.9, Indicator.HistoricalPeriod.QUARTERLY, 8, DashboardInitializer.SHARE_OPERATIONAL_SIMPLE_SLD7, 
            ['en':"Type of Equipment", 'fr':"Type of Equipment"], DashboardInitializer.SHARE_OPERATIONAL_GROUP_SLD7, false, true
            )
          def validIndicatorTwo = new Indicator(
            category:category, 
            code:"SHARE_OPE_EQUIPMENT Test",
            unit:"%",redToYellowThreshold:0.8,yellowToGreenThreshold:0.9, historicalPeriod:Indicator.HistoricalPeriod.QUARTERLY, 
            historyItems:8, queryScript:DashboardInitializer.SHARE_OPERATIONAL_SIMPLE_SLD7, 
            sqlQuery:false, active:true
            ).save()
          then:
          Indicator.count() == 2
      } 

      def "can't create and save indicator with invalid code"() {
          setup:
          def userDefinedVariableTwo = DashboardInitializer.newUserDefinedVariable(CODE(123),["en":"User Defined Variable test ","fr":"User Defined Variable test fr"],9.0)
          def category=new IndicatorCategory(code:DashboardInitializer.CORRECTIVE_MAINTENANCE+"test",names:['en':"Corrective maintenance",'fr':"Corrective maintenance"],redToYellowThreshold:60,yellowToGreenThreshold:80).save()
          def validIndicatorOne = DashboardInitializer.newIndicator(
            category, 
            "SHARE_OPE_EQUIPMENT",
            ['en':"Share of operational equipment",'fr':"Share of operational equipment"], 
            ['en':"Share of operational equipment",'fr':"Share of operational equipment"], 
            [
              'en':"(total number equipment with STATUS=Operational) / by (total number equipment with STATUS={Operational Partially operational Under maintenance})", 
              'fr':"(total number equipment with STATUS=Operational) / by (total number equipment with STATUS={Operational Partially operational Under maintenance})"
            ],
            "%", 0.8, 0.9, Indicator.HistoricalPeriod.QUARTERLY, 8, DashboardInitializer.SHARE_OPERATIONAL_SIMPLE_SLD7, 
            ['en':"Type of Equipment", 'fr':"Type of Equipment"], DashboardInitializer.SHARE_OPERATIONAL_GROUP_SLD7, false, true
            )
          when:
          def validIndicatorTwo = new Indicator(
            category:category, 
            code:"SHARE_OPE_EQUIPMENT",
            unit:"%",redToYellowThreshold:0.8,yellowToGreenThreshold:0.9, historicalPeriod:Indicator.HistoricalPeriod.QUARTERLY, 
            historyItems:8, queryScript:DashboardInitializer.SHARE_OPERATIONAL_SIMPLE_SLD7, 
            sqlQuery:false, active:true
            )
          validIndicatorTwo.save()
          then:
          Indicator.count() == 1
          validIndicatorTwo.errors.hasFieldErrors('code') == true
      }
      def "can't create and save indicator with invalid unit"() {
          setup:
          def userDefinedVariableTwo = DashboardInitializer.newUserDefinedVariable(CODE(123),["en":"User Defined Variable test ","fr":"User Defined Variable test fr"],9.0)
          def category=new IndicatorCategory(code:DashboardInitializer.CORRECTIVE_MAINTENANCE+"test",names:['en':"Corrective maintenance",'fr':"Corrective maintenance"],redToYellowThreshold:60,yellowToGreenThreshold:80).save()
          def validIndicatorOne = DashboardInitializer.newIndicator(
            category, 
            "SHARE_OPE_EQUIPMENT",
            ['en':"Share of operational equipment",'fr':"Share of operational equipment"], 
            ['en':"Share of operational equipment",'fr':"Share of operational equipment"], 
            [
              'en':"(total number equipment with STATUS=Operational) / by (total number equipment with STATUS={Operational Partially operational Under maintenance})", 
              'fr':"(total number equipment with STATUS=Operational) / by (total number equipment with STATUS={Operational Partially operational Under maintenance})"
            ],
            "%", 0.8, 0.9, Indicator.HistoricalPeriod.QUARTERLY, 8, DashboardInitializer.SHARE_OPERATIONAL_SIMPLE_SLD7, 
            ['en':"Type of Equipment", 'fr':"Type of Equipment"], DashboardInitializer.SHARE_OPERATIONAL_GROUP_SLD7, false, true
            )
          when:
          def validIndicatorThree = new Indicator(
            category:category, 
            code:"SHARE_OPE_EQUIPMENT test",
            redToYellowThreshold:0.8,yellowToGreenThreshold:0.9, historicalPeriod:Indicator.HistoricalPeriod.QUARTERLY, 
            historyItems:8, queryScript:DashboardInitializer.SHARE_OPERATIONAL_SIMPLE_SLD7, 
            sqlQuery:false, active:true
            )
          validIndicatorThree.save()
          then:
          Indicator.count() == 1
          validIndicatorThree.errors.hasFieldErrors('unit') == true
        }
        def "can't create and save indicator with invalid redToYellowThreshold"() {
          setup:
          def userDefinedVariableTwo = DashboardInitializer.newUserDefinedVariable(CODE(123),["en":"User Defined Variable test ","fr":"User Defined Variable test fr"],9.0)
          def category=new IndicatorCategory(code:DashboardInitializer.CORRECTIVE_MAINTENANCE+"test",names:['en':"Corrective maintenance",'fr':"Corrective maintenance"],redToYellowThreshold:60,yellowToGreenThreshold:80).save()
          def validIndicatorOne = DashboardInitializer.newIndicator(
            category, 
            "SHARE_OPE_EQUIPMENT",
            ['en':"Share of operational equipment",'fr':"Share of operational equipment"], 
            ['en':"Share of operational equipment",'fr':"Share of operational equipment"], 
            [
              'en':"(total number equipment with STATUS=Operational) / by (total number equipment with STATUS={Operational Partially operational Under maintenance})", 
              'fr':"(total number equipment with STATUS=Operational) / by (total number equipment with STATUS={Operational Partially operational Under maintenance})"
            ],
            "%", 0.8, 0.9, Indicator.HistoricalPeriod.QUARTERLY, 8, DashboardInitializer.SHARE_OPERATIONAL_SIMPLE_SLD7, 
            ['en':"Type of Equipment", 'fr':"Type of Equipment"], DashboardInitializer.SHARE_OPERATIONAL_GROUP_SLD7, false, true
            )
          when:
          def validIndicatorFour = new Indicator(
            category:category, 
            code:"SHARE_OPE_EQUIPMENT test",
            unit:"%",yellowToGreenThreshold:0.9, historicalPeriod:Indicator.HistoricalPeriod.QUARTERLY, 
            historyItems:8, queryScript:DashboardInitializer.SHARE_OPERATIONAL_SIMPLE_SLD7, 
            sqlQuery:false, active:true
            ) 
          validIndicatorFour.save()
          then:
          Indicator.count() == 1
          validIndicatorFour.errors.hasFieldErrors('redToYellowThreshold') == true
        }
        def "can't create and save indicator with invalid yellowToGreenThreshold"() {
          setup:
          def userDefinedVariableTwo = DashboardInitializer.newUserDefinedVariable(CODE(123),["en":"User Defined Variable test ","fr":"User Defined Variable test fr"],9.0)
          def category=new IndicatorCategory(code:DashboardInitializer.CORRECTIVE_MAINTENANCE+"test",names:['en':"Corrective maintenance",'fr':"Corrective maintenance"],redToYellowThreshold:60,yellowToGreenThreshold:80).save()
          def validIndicatorOne = DashboardInitializer.newIndicator(
            category, 
            "SHARE_OPE_EQUIPMENT",
            ['en':"Share of operational equipment",'fr':"Share of operational equipment"], 
            ['en':"Share of operational equipment",'fr':"Share of operational equipment"], 
            [
              'en':"(total number equipment with STATUS=Operational) / by (total number equipment with STATUS={Operational Partially operational Under maintenance})", 
              'fr':"(total number equipment with STATUS=Operational) / by (total number equipment with STATUS={Operational Partially operational Under maintenance})"
            ],
            "%", 0.8, 0.9, Indicator.HistoricalPeriod.QUARTERLY, 8, DashboardInitializer.SHARE_OPERATIONAL_SIMPLE_SLD7, 
            ['en':"Type of Equipment", 'fr':"Type of Equipment"], DashboardInitializer.SHARE_OPERATIONAL_GROUP_SLD7, false, true
            )
          when: 
          def validIndicatorFive = new Indicator(
            category:category, 
            code:"SHARE_OPE_EQUIPMENT test",
            unit:"%",redToYellowThreshold:0.8, historicalPeriod:Indicator.HistoricalPeriod.QUARTERLY, 
            historyItems:8, queryScript:DashboardInitializer.SHARE_OPERATIONAL_SIMPLE_SLD7, 
            sqlQuery:false, active:true
            ) 
          validIndicatorFive.save()
          then:
          Indicator.count() == 1
          validIndicatorFive.errors.hasFieldErrors('yellowToGreenThreshold') == true
        }

        def "can't create and save indicator with invalid queryScript"() {
          setup:
          def userDefinedVariableTwo = DashboardInitializer.newUserDefinedVariable(CODE(123),["en":"User Defined Variable test ","fr":"User Defined Variable test fr"],9.0)
          def category=new IndicatorCategory(code:DashboardInitializer.CORRECTIVE_MAINTENANCE+"test",names:['en':"Corrective maintenance",'fr':"Corrective maintenance"],redToYellowThreshold:60,yellowToGreenThreshold:80).save()
          def validIndicatorOne = DashboardInitializer.newIndicator(
            category, 
            "SHARE_OPE_EQUIPMENT",
            ['en':"Share of operational equipment",'fr':"Share of operational equipment"], 
            ['en':"Share of operational equipment",'fr':"Share of operational equipment"], 
            [
              'en':"(total number equipment with STATUS=Operational) / by (total number equipment with STATUS={Operational Partially operational Under maintenance})", 
              'fr':"(total number equipment with STATUS=Operational) / by (total number equipment with STATUS={Operational Partially operational Under maintenance})"
            ],
            "%", 0.8, 0.9, Indicator.HistoricalPeriod.QUARTERLY, 8, DashboardInitializer.SHARE_OPERATIONAL_SIMPLE_SLD7, 
            ['en':"Type of Equipment", 'fr':"Type of Equipment"], DashboardInitializer.SHARE_OPERATIONAL_GROUP_SLD7, false, true
            )
          when:
          def validIndicatorSix = new Indicator(
            category:category, 
            code:"SHARE_OPE_EQUIPMENT test",
            unit:"%",redToYellowThreshold:0.8,yellowToGreenThreshold:0.9, historicalPeriod:Indicator.HistoricalPeriod.QUARTERLY, 
            historyItems:8, sqlQuery:false, active:true
            )
          validIndicatorSix.save()
          then:
          Indicator.count() == 1
          validIndicatorSix.errors.hasFieldErrors('queryScript') == true
        }

        def "can't create and save indicator with invalid category, sqlQuery and active"() {
          setup:
          def userDefinedVariableTwo = DashboardInitializer.newUserDefinedVariable(CODE(123),["en":"User Defined Variable test ","fr":"User Defined Variable test fr"],9.0)
          def category=new IndicatorCategory(code:DashboardInitializer.CORRECTIVE_MAINTENANCE+"test",names:['en':"Corrective maintenance",'fr':"Corrective maintenance"],redToYellowThreshold:60,yellowToGreenThreshold:80).save()
          def validIndicatorOne = DashboardInitializer.newIndicator(
            category, 
            "SHARE_OPE_EQUIPMENT",
            ['en':"Share of operational equipment",'fr':"Share of operational equipment"], 
            ['en':"Share of operational equipment",'fr':"Share of operational equipment"], 
            [
              'en':"(total number equipment with STATUS=Operational) / by (total number equipment with STATUS={Operational Partially operational Under maintenance})", 
              'fr':"(total number equipment with STATUS=Operational) / by (total number equipment with STATUS={Operational Partially operational Under maintenance})"
            ],
            "%", 0.8, 0.9, Indicator.HistoricalPeriod.QUARTERLY, 8, DashboardInitializer.SHARE_OPERATIONAL_SIMPLE_SLD7, 
            ['en':"Type of Equipment", 'fr':"Type of Equipment"], DashboardInitializer.SHARE_OPERATIONAL_GROUP_SLD7, false, true
            )
          when:
          def validIndicatorSeven = new Indicator(
            code:"SHARE_OPE_EQUIPMENT test",
            unit:"%",redToYellowThreshold:0.8,yellowToGreenThreshold:0.9, historicalPeriod:Indicator.HistoricalPeriod.QUARTERLY, 
            historyItems:8, queryScript:DashboardInitializer.SHARE_OPERATIONAL_SIMPLE_SLD7
            ) 
          validIndicatorSeven.save()
          then:
          Indicator.count() == 1
          validIndicatorSeven.errors.hasFieldErrors('category') == true
          validIndicatorSeven.errors.hasFieldErrors('sqlQuery') == true
          validIndicatorSeven.errors.hasFieldErrors('active') == true
        }
}

