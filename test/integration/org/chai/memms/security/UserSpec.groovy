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
import org.chai.memms.security.User
import grails.validation.ValidationException

import org.chai.location.CalculationLocation;
import org.chai.location.DataLocation
import org.chai.location.DataLocationType
import org.chai.location.Location
import org.chai.location.LocationLevel

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
	
	def "can delete a user who has roles - roles used somewhere else"() {
		setup:
		setupLocationTree()
		
		def roleOne = newRole("RoleOne", "*:*")
		def roleTwo = newRole("roleTwo", "*:*")
		def roleThree = newRole("roleThree", "*:*")

		def userOne = newUser('userone', 'userone', true, true)
		def userTwo = newUser('usertwo', 'userone', true, true)

		userOne.addToRoles(roleOne)
		userOne.addToRoles(roleTwo)
		userOne.save(failOnError: true)
		userTwo.addToRoles(roleTwo)
		userTwo.addToRoles(roleThree)
		userTwo.save(failOnError: true)
		when:
		userOne.delete()
		then:
		User.count() == 1
		Role.count() == 3
	}
	
	def "can delete a user who has roles - roles not used somewhere else"() {
		setup:
		setupLocationTree()
		
		def roleOne = newRole("RoleOne", "*:*")
		def roleTwo = newRole("roleTwo", "*:*")

		def userOne = newUser('userone', 'userone', true, true)

		userOne.addToRoles(roleOne)
		userOne.addToRoles(roleTwo)
		userOne.save(failOnError: true)
		when:
		userOne.delete()
		then:
		User.count() == 0
	}

	def "user must have a type"() {

		when:
		new User(
				username: 'test1', uuid: 'test1', permissionString: '',
				passwordHash: '', email: 'test1@test.com', firstname: 'test', lastname: 'test',
				phoneNumber: '123', organisation: 'test'
				).save(failOnError: true)

		then:
		thrown ValidationException

		when:
		new User(
				username: 'test2', uuid: 'test2', permissionString: '',
				passwordHash: '', email: 'test2@test.com', firstname: 'test', lastname: 'test',
				phoneNumber: '123', organisation: 'test', userType: UserType.OTHER
				).save(failOnError: true)
		then:
		User.count() == 1
	}
	
	def "find if a user has access to a given calculationlocation"() {
		setup:
		setupLocationTree()
		
		def rwanda = Location.findByCode(RWANDA)
		
		def gitaramaDistrict = Location.findByCode(GITARAMA)
		def butaroDh = DataLocation.findByCode(BUTARO)
		def kivuyeHc = DataLocation.findByCode(KIVUYE)
		
		def bureraDistrict = Location.findByCode(BURERA)
		def musanzeDh = DataLocation.findByCode(MUSANZE)
		def gitweHc = DataLocation.findByCode(GITWE)
		
		when:
		def titulaireKivuye = newOtherUserWithType("titulaireKivuye", "titulaireKivuye", DataLocation.findByCode(KIVUYE),UserType.TITULAIREHC)

		def departmentButaro = newOtherUserWithType("departmentButaro", "departmentButaro", DataLocation.findByCode(BUTARO),UserType.HOSPITALDEPARTMENT)

		def techDhButaro = newOtherUserWithType("techDhButaro", "techDhButaro", DataLocation.findByCode(BUTARO),UserType.TECHNICIANDH)
		
		def ditrictGitarama = newOtherUserWithType("ditrictGitarama", "ditrictGitarama", Location.findByCode(GITARAMA),UserType.ADMIN)
		
		def techMMC = newOtherUserWithType("techMMC", "techMMC", Location.findByCode(RWANDA), UserType.TECHNICIANMMC )
		
		def admin = newOtherUserWithType("admin", "admin", Location.findByCode(RWANDA),UserType.ADMIN)
		
		then:
		titulaireKivuye.canAccessCalculationLocation(kivuyeHc)
		!titulaireKivuye.canAccessCalculationLocation(gitweHc)
		!titulaireKivuye.canAccessCalculationLocation(musanzeDh)
		!titulaireKivuye.canAccessCalculationLocation(bureraDistrict)
		!titulaireKivuye.canAccessCalculationLocation(butaroDh)
		!titulaireKivuye.canAccessCalculationLocation(gitaramaDistrict)
		!titulaireKivuye.canAccessCalculationLocation(rwanda)
		
		departmentButaro.canAccessCalculationLocation(butaroDh)
		!departmentButaro.canAccessCalculationLocation(gitweHc)
		!departmentButaro.canAccessCalculationLocation(musanzeDh)
		!departmentButaro.canAccessCalculationLocation(bureraDistrict)
		departmentButaro.canAccessCalculationLocation(kivuyeHc)
		!departmentButaro.canAccessCalculationLocation(gitaramaDistrict)
		!departmentButaro.canAccessCalculationLocation(rwanda)
		
		techDhButaro.canAccessCalculationLocation(butaroDh)
		!techDhButaro.canAccessCalculationLocation(gitweHc)
		!techDhButaro.canAccessCalculationLocation(musanzeDh)
		!techDhButaro.canAccessCalculationLocation(bureraDistrict)
		techDhButaro.canAccessCalculationLocation(kivuyeHc)
		!techDhButaro.canAccessCalculationLocation(gitaramaDistrict)
		!techDhButaro.canAccessCalculationLocation(rwanda)
		
		ditrictGitarama.canAccessCalculationLocation(butaroDh)
		!ditrictGitarama.canAccessCalculationLocation(gitweHc)
		!ditrictGitarama.canAccessCalculationLocation(musanzeDh)
		!ditrictGitarama.canAccessCalculationLocation(bureraDistrict)
		ditrictGitarama.canAccessCalculationLocation(kivuyeHc)
		ditrictGitarama.canAccessCalculationLocation(gitaramaDistrict)
		!ditrictGitarama.canAccessCalculationLocation(rwanda)
		
		techMMC.canAccessCalculationLocation(butaroDh)
		techMMC.canAccessCalculationLocation(gitweHc)
		techMMC.canAccessCalculationLocation(musanzeDh)
		techMMC.canAccessCalculationLocation(bureraDistrict)
		techMMC.canAccessCalculationLocation(kivuyeHc)
		techMMC.canAccessCalculationLocation(gitaramaDistrict)
		techMMC.canAccessCalculationLocation(rwanda)
		
		admin.canAccessCalculationLocation(butaroDh)
		admin.canAccessCalculationLocation(gitweHc)
		admin.canAccessCalculationLocation(musanzeDh)
		admin.canAccessCalculationLocation(bureraDistrict)
		admin.canAccessCalculationLocation(kivuyeHc)
		admin.canAccessCalculationLocation(gitaramaDistrict)
		admin.canAccessCalculationLocation(rwanda)
	}
}

