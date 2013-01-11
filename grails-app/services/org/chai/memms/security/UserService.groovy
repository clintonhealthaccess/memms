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

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils
import org.hibernate.Criteria;
import org.chai.location.CalculationLocation;
import org.chai.location.DataLocation
import org.chai.memms.corrective.maintenance.WorkOrder;
import org.chai.memms.security.User.UserType;
import org.chai.memms.util.Utils
import org.hibernate.criterion.MatchMode
import org.hibernate.criterion.Order
import org.hibernate.criterion.Projections
import org.hibernate.criterion.Restrictions

/**
 * @author Jean Kahigiso M.
 *
 */
class UserService {
	static transactional = true
	def sessionFactory;
	
	boolean canRequestEquipmentRegistration(User user){
		return user.userType == UserType.TITULAIREHC || user.userType == UserType.HOSPITALDEPARTMENT
	}
	
	boolean canViewManagedEquipments(User user){
		return user.userType == UserType.TECHNICIANDH && user.location instanceof DataLocation && (user.location as DataLocation).manages
	}
	
	List<User> searchUser(String text, Map<String, String> params) {
		if(log.isDebugEnabled()) log.debug("searchUser params=" + params)
		def criteria = User.createCriteria()
		return criteria.list(offset:params.offset,max:params.max,sort:params.sort ?:"id",order: params.order ?:"desc"){
			or{
				ilike("username","%"+text+"%")
				ilike("email","%"+text+"%")
				ilike("firstname","%"+text+"%")
				ilike("lastname","%"+text+"%")
				ilike("organisation","%"+text+"%")
				ilike("phoneNumber","%"+text+"%")				
			}
		}
	}
	
	List<User> getActiveUserByTypeAndLocation(List<UserType> userTypes, CalculationLocation location, Map<String, String> params){
		def criteria = User.createCriteria();
		return criteria.list(offset:params.offset,max:params.max,sort:params.sort ?:"id",order: params.order ?:"desc"){
			eq ("active", true)
			if(location != null)
				eq('location',location)
			if(userTypes != null && userTypes.size()!=0){
					inList ("userType", userTypes)
			}
		}
	}
	
	List<User> getNotificationWorkOrderGroup(WorkOrder workOrder,User sender,Boolean escalate){
		def users = []
		if(escalate) users.addAll(getActiveUserByTypeAndLocation([UserType.TECHNICIANMMC],null, [:]))
		else if(sender.userType != UserType.TECHNICIANDH){
				if(workOrder.equipment.dataLocation.managedBy instanceof DataLocation) users =  getActiveUserByTypeAndLocation([UserType.TECHNICIANDH],workOrder.equipment.dataLocation.managedBy, [:])
				else users =  getActiveUserByTypeAndLocation([UserType.TECHNICIANDH],workOrder.equipment.dataLocation, [:])
		}
		if(!users.contains( workOrder.addedBy )) users.add(workOrder.addedBy)
		if(users.contains( sender )) users.remove(sender)
		if(log.isDebugEnabled()) log.debug("Users in notificationWorkOrder group: " + users)
		return users
	}
	
	List<User> getNotificationEquipmentGroup(DataLocation dataLocation){
		def users;
		
		if(dataLocation.managedBy instanceof DataLocation){
			users =  getActiveUserByTypeAndLocation([UserType.TECHNICIANDH],dataLocation.managedBy, [:])
		}
		else users =  getActiveUserByTypeAndLocation([UserType.TECHNICIANDH],dataLocation, [:])
		
		if(log.isDebugEnabled()) log.debug("Users in notificationEquipment group: " + users)
		return users
	}
}
