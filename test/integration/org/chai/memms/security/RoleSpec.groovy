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

package org.chai.memms.security


import org.apache.commons.el.parser.Token;
import org.chai.memms.IntegrationTests
import org.chai.memms.security.User.UserType;
import org.chai.memms.security.User;

class RoleSpec  extends IntegrationTests {

	def "cannot add a role without permission -  no users"() {
		setup:
		def role = new Role(name:'testRole')
		when:"no permissions string defined"
		role.save()
		then:
		Role.count() == 0
		when:"with a permissions string defined"
		role.permissionString = "new:*"
		role.save()
		then:
		Role.count() == 1
	}
	
	def "can add a role with users"() {
		setup:
		def user = newUser('one','one')
		def role = new Role(name:'testRole',permissionString:"new:*")
		when:
		role.addToUsers(user)
		role.save(failOnError: true)
		then:
		Role.count() == 1
		Role.list()[0].users.size() == 1
	}
	
	def "can manipulate permissions strings on a role"() {
		setup:
		def role = new Role(name:'testRole')
		
		when:
		role.permissionString = "new:*;none:ugin:*"
		role.save(failOnError: true)
		
		then:
		Role.list()[0].permissionString == "new:*;none:ugin:*"
		Role.list()[0].permissions.size() == 2
		
		when:
		role.addToPermissions("another:*")
		role.save(failOnError: true)
		then:
		Role.list()[0].permissions.size() == 3
		
		when:
		role.setPermissions(["here:*","two:*"])
		role.save(failOnError: true)
		then:
		Role.list()[0].permissions.size() == 2
	}

	def "can delete a role which has users - users have other roles"() {
		setup:
		def roleOne = newRole("RoleOne", "*:*")
		def roleTwo = newRole("roleTwo", "*:*")

		def userOne
		def userTwo
		def userThree
		
		User.withNewTransaction{
			userOne = newUser('userone', 'userone', false, true)
			userTwo = newUser('usertwo', 'userone', false, true)
			userThree = newUser('userthree', 'userone', false, true)
		}

		roleOne.addToUsers(userOne)
		roleOne.addToUsers(userTwo)
		roleOne.save(failOnError: true)
		roleTwo.addToUsers(userTwo)
		roleTwo.addToUsers(userThree)
		roleTwo.save(failOnError: true)
		
		when:
		roleOne.delete()
		then:
		Role.count() == 1
		User.count() == 3
		
		cleanup:
		User.withNewTransaction{
			User.executeUpdate("delete User")
		}
	}
	
	def "can delete a role which has users - users don't have other roles"() {
		setup:
		
		def roleOne = newRole("RoleOne", "*:*")
		
		def userOne
		def userTwo
		
		User.withNewTransaction{
			userOne = newUser('userone', 'userone', false, true)
			userTwo = newUser('usertwo', 'userone', false, true)
		}

		roleOne.addToUsers(userOne)
		roleOne.addToUsers(userTwo)
		roleOne.save(failOnError: true)
		
		when:
		roleOne.delete()
		then:
		Role.count() == 0
		User.count() == 2
		cleanup:
		User.withNewTransaction{
			User.executeUpdate("delete User")
		}
	}
}