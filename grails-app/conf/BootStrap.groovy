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

import org.chai.memms.location.CalculationLocation;
import org.chai.memms.security.Role;
import org.chai.memms.security.User;
import org.chai.memms.security.UserType;

import java.util.Date;

import grails.util.GrailsUtil;

import org.apache.commons.lang.time.DateUtils;
import org.apache.shiro.crypto.hash.Sha256Hash;

import org.chai.memms.Initializer;
import org.omg.CORBA.INITIALIZE;

class BootStrap {

    def init = { servletContext ->
		
		switch (GrailsUtil.environment) {
			case "development":
			Initializer.createDummyStructure();
			Initializer.createUsers();
			Initializer.createInventoryStructure()
			break;
			case "production":
			if (!Role.count()) {
				def adminRole = new Role(name: "admin")
				adminRole.addToPermissions("*:*")
				adminRole.save(failOnError: true)
				
				def clercRole = new Role(name: "clerc")
				clercRole.addToPermissions("home:*")
				clercRole.addToPermissions("menu:home")
				clercRole.addToPermissions("menu:inventory")
				clercRole.addToPermissions("menu:correctivemaintenance")
				clercRole.addToPermissions("menu:preventivemaintenance")
				clercRole.addToPermissions("menu:reports")
				clercRole.addToPermissions("equipment:*")
				clercRole.save(failOnError: true)
			}
			if (!User.count()) {
				def userAdmin = new User(userType: UserType.ADMIN,code:"admin", location: CalculationLocation.findByCode(0), username: "admin",
					firstname: "First Name", lastname: "Last Name", email:'admin@memms.org', passwordHash: new Sha256Hash("admin").toHex(), active: true,
					confirmed: true, uuid:'admin', defaultLanguage:'en', phoneNumber: '+250 78 111 11 11', organisation:'org')
				userAdmin.addToPermissions("*:*")				
				userAdmin.save(failOnError: true)
				
				def clerc = new User(userType: UserType.OTHER,code:"clerc", location: CalculationLocation.findByCode(327), username: "clerc",
					firstname: "memms", lastname: "memms", email:'clerk@memms.org', passwordHash: new Sha256Hash("clerc").toHex(), active: true,
					confirmed: true, uuid:'clerc', defaultLanguage:'en', phoneNumber: '+250 72 111 11 11', organisation:'org')
				clerc.addToPermissions("menu:home")
				clerc.addToPermissions("menu:inventory")
				clerc.addToPermissions("menu:correctivemaintenance")
				clerc.addToPermissions("menu:preventivemaintenance")
				clerc.addToPermissions("menu:reports")
				clerc.addToPermissions("equipment:*")
				clerc.addToPermissions("home:*")
				clerc.save(failOnError: true)
				
			}
			break;
		}
    }
    def destroy = {
    }
}
