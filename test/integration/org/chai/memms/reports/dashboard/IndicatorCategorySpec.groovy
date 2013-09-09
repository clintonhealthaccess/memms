
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
import org.chai.memms.reports.dashboard.IndicatorCategory
import org.chai.memms.reports.dashboard.DashboardInitializer
import org.chai.memms.IntegrationTests

/**
 *
 * @author Kahigiso M. Jean
 */


class IndicatorCategorySpec extends  IntegrationTests{

	 def "can create and save indicator category with valid fields"() {
          when:
          def indicatorCategoryOne = DashboardInitializer.newIndicatorCategory(CODE(120),["en":"Corrective maintenance","fr":"Corrective maintenance fr"],60,80)
          def indicatorCategoryTwo = new IndicatorCategory(code:"CODE 233",redToYellowThreshold:60,yellowToGreenThreshold:80)
          indicatorCategoryTwo.save()
          then:
          IndicatorCategory.count() == 2
          indicatorCategoryTwo.code == IndicatorCategory.list(sort:"id",order:"desc")[0].code
          indicatorCategoryOne.code == IndicatorCategory.list(sort:"id",order:"desc")[1].code      
   }

   def "can't create and save indicator category without code"() {
          when:
          def indicatorCategoryOne = DashboardInitializer.newIndicatorCategory(CODE(120),["en":"Corrective maintenance","fr":"Corrective maintenance fr"],60,80)
          def indicatorCategoryTwo = new IndicatorCategory(redToYellowThreshold:60,yellowToGreenThreshold:80)
          indicatorCategoryTwo.save()
          then:
          IndicatorCategory.count() == 1
          indicatorCategoryTwo.errors.hasFieldErrors("code")
   }

    def "can't create and save indicator category duplicate code"() {
          when:
          def indicatorCategoryOne = DashboardInitializer.newIndicatorCategory(CODE(120),["en":"Corrective maintenance","fr":"Corrective maintenance fr"],60,80)
          def indicatorCategoryTwo = new IndicatorCategory(code:CODE(120),redToYellowThreshold:60,yellowToGreenThreshold:80)
          indicatorCategoryTwo.save()
          then:
          IndicatorCategory.count() == 1
          indicatorCategoryTwo.errors.hasFieldErrors("code")
   }

  def "can't create and save indicator category without redToYellowThreshold"() {
          when:
          def indicatorCategoryOne = DashboardInitializer.newIndicatorCategory(CODE(120),["en":"Corrective maintenance","fr":"Corrective maintenance fr"],60,80)
          def indicatorCategoryTwo = new IndicatorCategory(code:CODE(122),yellowToGreenThreshold:80)
          indicatorCategoryTwo.save()
          then:
          IndicatorCategory.count() == 1
          indicatorCategoryTwo.errors.hasFieldErrors("redToYellowThreshold")
   }

   def "can't create and save indicator category without yellowToGreenThreshold"() {
          when:
          def indicatorCategoryOne = DashboardInitializer.newIndicatorCategory(CODE(120),["en":"Corrective maintenance","fr":"Corrective maintenance fr"],60,80)
          def indicatorCategoryTwo = new IndicatorCategory(code:CODE(121),redToYellowThreshold:60)
          indicatorCategoryTwo.save()
          then:
          IndicatorCategory.count() == 1
          indicatorCategoryTwo.errors.hasFieldErrors("yellowToGreenThreshold")
   }

   def "can't create and save indicator category with yellowToGreenThreshold in range 0..100"() {
          when:
          def indicatorCategoryOne = DashboardInitializer.newIndicatorCategory(CODE(125),["en":"Corrective maintenance","fr":"Corrective maintenance fr"],60,80)
          def indicatorCategoryTwo = new IndicatorCategory(code:CODE(121),redToYellowThreshold:60,yellowToGreenThreshold:101)
          indicatorCategoryTwo.save()
          then:
          IndicatorCategory.count() == 1
          indicatorCategoryTwo.errors.hasFieldErrors("yellowToGreenThreshold")

          when:
          def indicatorCategoryFour = new IndicatorCategory(code:CODE(123),redToYellowThreshold:60,yellowToGreenThreshold:-2)
          indicatorCategoryFour.save()
          then:
          IndicatorCategory.count() == 1
          indicatorCategoryFour.errors.hasFieldErrors("yellowToGreenThreshold")
   }

   def "can't create and save indicator category without redToYellowThreshold"() {
          when:
          def indicatorCategoryOne = DashboardInitializer.newIndicatorCategory(CODE(128),["en":"Corrective maintenance","fr":"Corrective maintenance fr"],60,80)
          def indicatorCategoryTwo = new IndicatorCategory(code:CODE(129),yellowToGreenThreshold:80,redToYellowThreshold:101)
          indicatorCategoryTwo.save()
          then:
          IndicatorCategory.count() == 1
          indicatorCategoryTwo.errors.hasFieldErrors("redToYellowThreshold")
   
          when:
          def indicatorCategoryFour = new IndicatorCategory(code:CODE(125),yellowToGreenThreshold:80,redToYellowThreshold:-1)
          indicatorCategoryFour.save()
          then:
          IndicatorCategory.count() == 1
          indicatorCategoryFour.errors.hasFieldErrors("redToYellowThreshold")
   }    
}

