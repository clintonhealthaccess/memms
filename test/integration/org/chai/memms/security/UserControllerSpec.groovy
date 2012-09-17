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

import org.apache.shiro.crypto.hash.Sha256Hash
import org.chai.memms.IntegrationTests;
import org.chai.location.DataLocation;

/**
 * @author Jean Kahigiso M.
 *
 */
class UserControllerSpec extends IntegrationTests{
	
	def userController
	
	def "create user with correct required data in fields"(){
		
		setup:
		userController = new UserController();
		
		when: 
		userController.params.userType="OTHER";
		userController.params.email="exemple@exemple.com";
		userController.params.username ="exemple";
		userController.params.firstname ="first";
		userController.params.lastname ="last";
		userController.params.phoneNumber ="+250 11 111 11 11";
		userController.params.organisation ="org";
		userController.params.password = "exemple";
		userController.params.repeat = "exemple";
		userController.params.permissionString = "*";
		userController.params.confirmed = true
		userController.params.active = false
		userController.save()
		
		then:
		User.count() == 1;
		User.findByUsername("exemple").username.equals("exemple");
		User.findByUsername("exemple").passwordHash.equals(new Sha256Hash("exemple").toHex());
		
	}
	
	def "create user with incorrect repeat password"(){
		
		setup:
		userController = new UserController();
		
		when:
		userController.params.userType="OTHER";
		userController.params.email="exemple@exemple.com";
		userController.params.username ="exemple";
		userController.params.firstname ="first";
		userController.params.lastname ="last";
		userController.params.phoneNumber ="+250 11 111 11 11";
		userController.params.organisation ="org";
		userController.params.password = "exemple";
		userController.params.repeat = "exemple1";
		userController.params.permissionString = "*";
		userController.params.confirmed = true
		userController.params.active = true
		userController.save()
		
		then:
		User.count() == 0;
		User.findByUsername("exemple")==null;
		
	}
	
	def "edit user without password change"(){
		
		setup:
		userController = new UserController();
		def user = newUser("myuser1",UUID.randomUUID().toString());

		when:
		userController.params.id = user.id;
		userController.params.uuid = user.uuid;
		userController.params.email = "exemple@exemple.com";
		userController.params.username ="exemple1";
		userController.save();
		
		then:
		User.count() == 1;
		User.findByUsername("myuser1")==null;
		User.findByUsername("exemple1")!=null;
		User.findByUsername("exemple1").email.equals("exemple@exemple.com");
		User.findByUsername("exemple1").passwordHash.equals("");
				
	}
	def "edit user with password change"(){
		
		setup:
		userController = new UserController();
		def user = newUser("myuser2",UUID.randomUUID().toString());
		
		when:
		userController.params.id = user.id
		userController.params.uuid = user.uuid;
		userController.params.email="ex@exemple.com";
		userController.params.username ="exemple2";
		userController.params.password = "exemple2";
		userController.params.repeat = "exemple2";
		userController.save();
		
		then:
		User.count() == 1;
		User.findByUsername("myuser2")==null;
		User.findByUsername("exemple2")!=null;
		User.findByUsername("exemple2").email.equals("ex@exemple.com");
		User.findByUsername("exemple2").passwordHash.equals(new Sha256Hash("exemple2").toHex());
		
	}
	
	def "cannot change uuid"(){
		setup:
		userController = new UserController();
		def uuid = UUID.randomUUID().toString();
		def user = newUser("myuser1",uuid);

		when:
		userController.params.id = user.id
		userController.params.uuid = "new uuid";
		userController.save();
		
		then:
		User.count() == 1;
		User.findByUsername("myuser1")!=null;
		User.findByUsername("myuser1").uuid == uuid;
	}
	
	def "cannot change password hash"(){
		setup:
		userController = new UserController();
		def user = newUser("myuser1",UUID.randomUUID().toString());

		when:
		userController.params.id = user.id
		userController.params.passwordHash = "new hash";
		userController.save();
		
		then:
		User.count() == 1;
		User.findByUsername("myuser1")!=null;
		User.findByUsername("myuser1").passwordHash == '';
	}
}
