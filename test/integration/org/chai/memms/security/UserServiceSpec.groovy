/**
* Copyright (c) 2011, Clinton Health Access Initiative.
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
package org.chai.memms.security

import org.chai.memms.IntegrationTests;
import org.chai.location.DataLocation;

/**
 * @author Jean Kahigiso M.
 *
 */
class UserServiceSpec extends IntegrationTests{
  def userService;
  
  def "search user test"(){
	  setup:
	  setupLocationTree()
	  def dataLocation = DataLocation.findByCode(KIVUYE);
	  def userOne = newUser("userOne",UUID.randomUUID().toString());
	  def userTwo = newUser("userTwo",UUID.randomUUID().toString());
	  def personUserOne = newOtherUser("personUserOne",UUID.randomUUID().toString(),dataLocation);
	  def personUserTwo = newOtherUser("personUserTwo",UUID.randomUUID().toString(),dataLocation);
	  def systemUserOne = newSystemUser("systemUserOne",UUID.randomUUID().toString(),dataLocation);
	  def systemUserTwo = newSystemUser("systemUserTwo",UUID.randomUUID().toString(),dataLocation);
	  
	  
	  
	  when:
	  def users = userService.searchUser("user",[:]);
	  def sortUsersByFirstname = userService.searchUser("one",["sort":"firstname"]);
	  def sortUsersByUsername = userService.searchUser("two",["sort":"username"]);
	  then:
	  //test default sorting
	  users.equals([systemUserTwo,systemUserOne,personUserTwo,personUserOne,userTwo,userOne])
	  sortUsersByFirstname.equals([userOne,personUserOne,systemUserOne])
	  //test if the sorting params is taken in consideration
	  sortUsersByUsername.equals([userTwo,systemUserTwo,personUserTwo])
	  
  }
  
  def "count searched user test"(){
	  setup:
	  setupLocationTree()
	  def dataLocation = DataLocation.findByCode(KIVUYE);
	  def userOne = newUser("userOne",UUID.randomUUID().toString());
	  def userTwo = newUser("userTwo",UUID.randomUUID().toString());
	  def systemUserOne = newSystemUser("systemUserOne",UUID.randomUUID().toString(),dataLocation);
	  def systemUserTwo = newSystemUser("systemUserTwo",UUID.randomUUID().toString(),dataLocation);
	  def personUserOne = newOtherUser("personUserOne",UUID.randomUUID().toString(),dataLocation);
	  def personUserTwo = newOtherUser("personUserTwo",UUID.randomUUID().toString(),dataLocation);
	  
	  when:
	  def users = userService.searchUser("user",[:]);
	  def sortUsersByFirstname = userService.searchUser("one",["sort":"firstname"]);
	  then:
	  users.size()==6
	  sortUsersByFirstname.size()==3
	 
	  
  }
	
}
