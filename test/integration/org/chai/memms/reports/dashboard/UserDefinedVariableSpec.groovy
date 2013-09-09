
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
import org.chai.memms.reports.dashboard.UserDefinedVariable
import org.chai.memms.IntegrationTests
/**
 *
 * @author Kahigiso M. Jean
 */

class UserDefinedVariableSpec extends IntegrationTests{
	
    
    def "can create and save an User Defined Variable "() {
      when:
      def userDefinedVariableOne = new UserDefinedVariable(code:CODE(120),currentValue:90.0).save()
      def userDefinedVariableTwo = DashboardInitializer.newUserDefinedVariable(CODE(123),["en":"User Defined Variable test ","fr":"User Defined Variable test fr"],9.0)
      then:
      UserDefinedVariable.count() == 2
      userDefinedVariableTwo.code == UserDefinedVariable.list(sort:"id",order:"desc")[0].code
      userDefinedVariableOne.code == UserDefinedVariable.list(sort:"id",order:"desc")[1].code
    }

   def "can't create and save an User Defined Variable with duplicate code"() {
    setup:
    DashboardInitializer.newUserDefinedVariable(CODE(123),["en":"User Defined Variable test ","fr":"User Defined Variable test fr"],9.0)
    when:
    def userDefinedVariable = new UserDefinedVariable(code:CODE(123),currentValue:"90.0")
    userDefinedVariable.save()
    then:
    UserDefinedVariable.count() == 1
    userDefinedVariable.errors.hasFieldErrors('code') == true
    userDefinedVariable.errors.hasFieldErrors('names') == false
  }
  
   def "can't create and save an User Defined Variable without code"() {
    setup:
    DashboardInitializer.newUserDefinedVariable(CODE(123),["en":"User Defined Variable test ","fr":"User Defined Variable test fr"],9.0)
    when:
    def userDefinedVariable = new UserDefinedVariable(currentValue:"90.0")
    userDefinedVariable.save()
    then:
    UserDefinedVariable.count() == 1
    userDefinedVariable.errors.hasFieldErrors('code') == true
    userDefinedVariable.errors.hasFieldErrors('names') == false
  }
    
}

