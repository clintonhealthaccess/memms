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

class UserSpec  extends IntegrationTests {

	def "can create and save a user"() {
		setup:
		setupLocationTree()

		when:
		def user = new User(
				username: 'test', uuid: 'test', permissionString: '',
				passwordHash: '', email: 'test@test.com', firstname: 'test', lastname: 'test',
				phoneNumber: '123', organisation: 'test', userType: UserType.OTHER, location:getCalculationLocation(KIVUYE)
				).save(failOnError: true)

		def foundUser = User.get(user.id)
		then:
		user.count() == 1
		foundUser.username == user.username
	}

	def "can register and save a user with a registration token"() {
		when:
		def registrationTokenOne = new RegistrationToken(token:'FSJxsOsRlrxXeRbVWuBd1',used:false)
		
		def userOne = new User(
				username: 'testOne', uuid: 'testOne', permissionString: '',
				passwordHash: '', email: 'testOne@test.com', firstname: 'test', lastname: 'test',
				phoneNumber: '123', organisation: 'test', userType: UserType.OTHER, registrationToken:registrationTokenOne
				)
		userOne.save(flush: true,failOnError: true)

		def registrationTokenTwo = new RegistrationToken(token:'FSJxsOsRlrxXeRbVWuBd2',used:false)
		
		def userTwo = new User(
				username: 'testTwo', uuid: 'testTwo', permissionString: '',
				passwordHash: '', email: 'testTwo@test.com', firstname: 'test', lastname: 'test',
				phoneNumber: '123', organisation: 'test', userType: UserType.OTHER, registrationToken:registrationTokenTwo
				)
		userTwo.save(flush: true,failOnError: true)
		
		then:
		User.count() == 2
		RegistrationToken.findByToken('FSJxsOsRlrxXeRbVWuBd1') != null
		RegistrationToken.findByToken('FSJxsOsRlrxXeRbVWuBd2') != null
		
		when:
		def registrationTokenThree = new RegistrationToken(token:'FSJxsOsRlrxXeRbVWuBd3',used:false)
		
		def userThree = new User(
				username: 'testThree', uuid: 'testThree', permissionString: '',
				passwordHash: '', email: 'testThree@test.com', firstname: 'test', lastname: 'test',
				phoneNumber: '123', organisation: 'test', userType: UserType.OTHER, registrationToken:registrationTokenThree
				)
		userThree.save(flush: true,failOnError: true)
		
		userTwo.delete()
		then:
		User.count() == 2
		RegistrationToken.count() == 2
		RegistrationToken.findByToken('FSJxsOsRlrxXeRbVWuBd1') != null
		RegistrationToken.findByToken('FSJxsOsRlrxXeRbVWuBd2') == null
		RegistrationToken.findByToken('FSJxsOsRlrxXeRbVWuBd3') != null
		
	}

	def "can create and save a user and their role"() {

		when:
		def user = new User(
				username: 'test', uuid: 'test', permissionString: '',
				passwordHash: '', email: 'test@test.com', firstname: 'test', lastname: 'test',
				phoneNumber: '123', organisation: 'test', userType: UserType.OTHER
				)

		def roleOne = new Role(name:"test role one",permissionString:"new:*")
		def roleTwo = new Role(name:"test role two",permissionString:"new:*")

		user.addToRoles(roleTwo)
		user.addToRoles(roleOne)

		then:
		user.roles.size() == 2
	}

	def "can add permisions to roles"() {

		when:
		def user = new User(
				username: 'test', uuid: 'test', permissionString: '',
				passwordHash: '', email: 'test@test.com', firstname: 'test', lastname: 'test',
				phoneNumber: '123', organisation: 'test', userType: UserType.OTHER
				)

		def roleOne = new Role(name:"test role one",permissionString:"new:*;none:ugin:*")
		def roleTwo = new Role(name:"test role two",permissionString:"new:*")

		user.addToRoles(roleTwo)
		user.addToRoles(roleOne)

		then:
		roleOne.getPermissionString() == "new:*;none:ugin:*"
	}

	def "user must have a type"() {

		when:
		new User(
				username: 'test1', uuid: 'test1', permissionString: '',
				passwordHash: '', email: 'test1@test.com', firstname: 'test', lastname: 'test',
				phoneNumber: '123', organisation: 'test', userType: UserType.OTHER
				).save(failOnError: true)

		then:
		//TODO clear this later
		//log.debug("Errors : " + ValidationException.fullMessage)
		//thrown ValidationException
		User.count() == 1

		when:
		new User(
				username: 'test2', uuid: 'test2', permissionString: '',
				passwordHash: '', email: 'test2@test.com', firstname: 'test', lastname: 'test',
				phoneNumber: '123', organisation: 'test', userType: UserType.OTHER
				).save(failOnError: true)
		then:
		User.count() == 2
	}
}