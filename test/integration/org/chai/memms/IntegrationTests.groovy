package org.chai.memms

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
import grails.plugin.spock.IntegrationSpec

import javax.servlet.ServletRequest

import org.apache.shiro.SecurityUtils
import org.apache.shiro.mgt.SecurityManager
import org.apache.shiro.subject.Subject
import org.apache.shiro.util.ThreadContext
import org.apache.shiro.web.util.WebUtils
import org.chai.location.DataLocation
import org.chai.location.DataLocationType
import org.chai.location.Location
import org.chai.location.LocationLevel
import org.chai.memms.preventive.maintenance.DurationBasedOrder;
import org.chai.memms.preventive.maintenance.PreventiveOrder.PreventiveOrderType;
import org.chai.memms.preventive.maintenance.PreventiveOrder.PreventiveOrderStatus;
import org.chai.memms.preventive.maintenance.PreventiveOrder.PreventionResponsible;
import org.chai.memms.inventory.Equipment
import org.chai.memms.inventory.Equipment.Donor;
import org.chai.memms.inventory.Equipment.PurchasedBy;
import org.chai.memms.inventory.EquipmentStatus.Status;
import org.chai.memms.inventory.EquipmentType
import org.chai.memms.inventory.EquipmentType.Observation
import org.chai.memms.inventory.Provider.Type
import org.chai.memms.security.Role
import org.chai.memms.security.User
import org.chai.memms.security.User.UserType

import org.chai.memms.reports.dashboard.Indicator
import org.chai.memms.reports.dashboard.IndicatorCategory
import org.chai.memms.reports.dashboard.UserDefinedVariable
import org.chai.memms.reports.dashboard.DashboardInitializer
import org.chai.memms.reports.dashboard.IndicatorComputationService
import org.chai.memms.Initializer

abstract class IntegrationTests extends IntegrationSpec {

	def refreshValueService
	def springcacheService
	def sessionFactory
	def grailsApplication
      

	static final String CODE (def number) { return "CODE"+number }
	static final String HEALTH_CENTER_GROUP = "Health Center"
	static final String DISTRICT_HOSPITAL_GROUP = "District Hospital"

	static final String NATIONAL = "National"
	static final String PROVINCE = "Province"
	static final String DISTRICT = "District"
	static final String SECTOR = "Sector"

	static final String RWANDA = "Rwanda"
	static final String KIGALI_CITY = "Kigali City"
	static final String NORTH = "North"
	static final String SOUTH = "South"
	static final String GITARAMA = "Gitarama"
	static final String BURERA = "Burera"
	static final String BUTARO = "Butaro DH"
	static final String MUSANZE = "Musanze DH"
	static final String KIVUYE = "Kivuye HC"
	static final String GITWE ="Gitwe HC"
	static final String MUVUNA ="Muvuna HC"


	def setup() {
		// using cache.use_second_level_cache = false in test mode doesn't work so
		// we flush the cache after each test
		//springcacheService.flushAll()
		//new org.apache.shiro.grails.ShiroSecurityService()
	}
	static def setupSystemUser(){
		def user = (User.findByUsername("systemUser"))?User.findByUsername("systemUser"):newUser("systemUser","systemUser", true, true)
		user.location = DataLocation.findByCode(BUTARO)
		user.save(failOnError:true)
		setupSecurityManager(user)
	}
	static def setupLocationTree() {
		// for the test environment, the location level is set to 4
		// so we create a tree accordingly

		def hc = Initializer.newDataLocationType(['en':HEALTH_CENTER_GROUP], HEALTH_CENTER_GROUP);
		def dh = Initializer.newDataLocationType(['en':DISTRICT_HOSPITAL_GROUP], DISTRICT_HOSPITAL_GROUP);

		def country = Initializer.newLocationLevel(['en':NATIONAL], NATIONAL,1)
		def province = Initializer.newLocationLevel(['en':PROVINCE], PROVINCE,2)
		def district = Initializer.newLocationLevel(['en':DISTRICT], DISTRICT,3)
		def sector = Initializer.newLocationLevel(['en':SECTOR], SECTOR,4)

		def rwanda = Initializer.newLocation(['en':RWANDA], RWANDA,null,country)
		
		def north = Initializer.newLocation(['en':NORTH], NORTH, rwanda, province)
		def south = Initializer.newLocation(['en':SOUTH], SOUTH, rwanda, province)
		
		def burera = Initializer.newLocation(['en':BURERA], BURERA, north, district)
		def gitarama = Initializer.newLocation(['en':GITARAMA], GITARAMA, south, district)

		def butaro = Initializer.newDataLocation(['en':BUTARO], BUTARO, gitarama, dh)
		def kivuye = Initializer.newDataLocation(['en':KIVUYE], KIVUYE, gitarama, hc)
		butaro.addToManages(kivuye)
		butaro.save(failOnError:true)
		
		def musanze = Initializer.newDataLocation(['en':MUSANZE], MUSANZE, burera, dh)
		def gitwe = Initializer.newDataLocation(['en':GITWE], GITWE, burera, hc)
		def muvuna = Initializer.newDataLocation(['en':MUVUNA], MUVUNA, burera, hc)
		musanze.addToManages(muvuna)
		musanze.addToManages(gitwe)
		musanze.save(failOnError:true)
	}

	static def setupEquipment(){
		newEquipment(CODE(123),DataLocation.findByCode(KIVUYE))
	}
	
	static def newEquipment(def serialNumber, DataLocation dataLocation){
		setupSystemUser()
		def department = Initializer.newDepartment(['en':"testName"], serialNumber,['en':"testDescription"])
		def equipmentType = Initializer.newEquipmentType(serialNumber,["en":"Accelerometers"],["en":"used in memms"],Observation.USEDINMEMMS,Initializer.now(),12)

		def equipment = new Equipment(serialNumber:serialNumber,manufactureDate:Initializer.getDate(22,07,2010),purchaseDate:Initializer.getDate(22,07,2010),
				registeredOn:Initializer.getDate(23,07,2010), model:"equipmentModel", department:department,dataLocation:dataLocation,
				purchaser:PurchasedBy.BYFACILITY,obsolete:false,expectedLifeTime:Initializer.newPeriod(20),descriptions:['en':'Equipment Descriptions'], type:equipmentType,
				currentStatus:Status.OPERATIONAL,
				addedBy: User.findByUsername("systemUser")
				)
		def manufactureContact = Initializer.newContact(['en':'Address Descriptions '],"Manufacture","jkl@yahoo.com","0768-889-787","Street 154","6353")
		def supplierContact = Initializer.newContact([:],"Supplier","jk@yahoo.com","0768-888-787","Street 1654","6353")
		def servicePro = Initializer.newContact([:],"Service Provider","sp@yahoo.com","0768-888-787","Street 1654","6353")

		def manufacture = Initializer.newProvider(serialNumber + CODE(1), Type.MANUFACTURER,manufactureContact)
		def supplier = Initializer.newProvider(serialNumber + CODE(2), Type.SUPPLIER,supplierContact)
		def serviceProvider = Initializer.newProvider(serialNumber + CODE(3), Type.SERVICEPROVIDER,servicePro)

		def contact = Initializer.newContact([:],"Contact","jk@yahoo.com","0768-888-787","Street 654","6353")
		def warranty = Initializer.newWarranty(['en':'warranty'],'warranty name','email@gmail.com',"0768-889-787","Street 154","6353",Initializer.getDate(10, 12, 2010),false,[:])

		equipment.manufacturer = manufacture
		equipment.supplier = supplier

		equipment.warranty = warranty
		equipment.warrantyPeriod = Initializer.newPeriod(4)
		

		equipment.save(failOnError: true)
	}

	static def newDurationBasedOrder(def equipment, def addedBy, def firstOccurenceOn, def occurency) {
		new DurationBasedOrder(equipment: equipment, addedBy: addedBy, description: 'description',
			type: PreventiveOrderType.DURATIONBASED, status: PreventiveOrderStatus.OPEN, preventionResponsible: PreventionResponsible.SERVICEPROVIDER,
			firstOccurenceOn: firstOccurenceOn, occurency: occurency
		).save(failOnError: true)
	}

	static def getLocationLevels(def levels) {
		def result = []
		for (def level : levels) {
			result.add LocationLevel.findByCode(level)
		}
		return result;
	}

	static def getCalculationLocation(def code, def log = null) {
		def location = Location.findByCode(code)
		return location
	}

	static def getLocations(def codes) {
		def result = []
		for (String code : codes) {
			result.add(Location.findByCode(code))
		}
		return result
	}

	static def getDataLocations(def codes) {
		def result = []
		for (String code : codes) {
			result.add(DataLocation.findByCode(code))
		}
		return result
	}
	static def getDataLocationTypes(def codes){
		def result=[]
		for(String code: codes)
			result.add(DataLocationType.findByCode(code));
		return result;
	}

	static def newRole(def rolename, def permissions){
		return new Role(name: rolename, permissionString: permissions).save(failOnError: true)
	}

	static def newUser(def username, def uuid) {
		return new User(userType: UserType.OTHER, username: username, permissionString: '', passwordHash:'', active: true, confirmed: true, uuid: uuid, firstname: 'user', lastname: 'last', organisation: 'org', phoneNumber: '+250 11 111 11 11').save(failOnError: true)
	}

	static def newUser(def username, def active, def confirmed) {
		return new User(userType: UserType.OTHER, username: username, email: "$username@memms.org",permissionString:'*:*',
		passwordHash: '', active: active, confirmed: confirmed, uuid: username, firstname: 'user', lastname: 'last',
		organisation: 'org', phoneNumber: '+250 11 111 11 11', location:DataLocation.findByCode(KIVUYE)).save(failOnError: true)
	}

	static def newUser(def username, def passwordHash, def active, def confirmed) {
		return new User(userType: UserType.OTHER, username: username, email: "$username@yahoo.co.rw",permissionString:'*:*',
		passwordHash: passwordHash, active: active, confirmed: confirmed, uuid: username, firstname: 'user', lastname: 'last',
		organisation: 'org', phoneNumber: '+250 11 111 11 11',location:DataLocation.findByCode(KIVUYE)).save(failOnError: true)
	}

	static def newOtherUser(def username, def uuid, def location) {
		return new User(userType: UserType.OTHER, code: username, username: username, permissionString: '', passwordHash:'', uuid: uuid, location: location, firstname: 'other', lastname: 'last', organisation: 'org', phoneNumber: '+250 11 111 11 11', confirmed:true, active:true).save(failOnError: true)
	}
	
//	static def newOtherUser(def username, def uuid, def location, def active, def confirmed, def userType) {
//		return new User(userType: userType, code: username, username: username, permissionString: '', passwordHash:'', uuid: uuid, location: location, firstname: 'other', lastname: 'last', organisation: 'org', phoneNumber: '+250 11 111 11 11',active:active).save(failOnError: true)
//	}
	static def newOtherUserWithType(def username, def uuid, def location, def userType) {
		return new User(userType: userType, code: username, username: username, permissionString: '', passwordHash:'', uuid: uuid, location: location, firstname: 'other', lastname: 'last', organisation: 'org', phoneNumber: '+250 11 111 11 11', confirmed:true, active:true).save(failOnError: true)
	}
	static def newSystemUser(def username, def uuid, def location) {
		return new User(userType: UserType.SYSTEM, code: username, username: username, permissionString: '', passwordHash:'', uuid: uuid, location: location, firstname: 'system', lastname: 'last', organisation: 'org', phoneNumber: '+250 11 111 11 11', confirmed:true, active:true).save(failOnError: true)
	}
	static def setupSecurityManager(def user) {
		def subject = [getPrincipal: { user?.uuid }, isAuthenticated: { user==null?false:true }, login: { token -> null }] as Subject
		ThreadContext.put( ThreadContext.SECURITY_MANAGER_KEY, [ getSubject: { subject } ] as SecurityManager )
		SecurityUtils.metaClass.static.getSubject = { subject }
		WebUtils.metaClass.static.getSavedRequest = { ServletRequest request -> null }
	}

	static def adaptParamsForList() {
		def params = [max:5,offset:0]
		return params
	}
        
    
    
    // dashboard testing data
    
      static def createDashboardTestData(){
     
          def category=new IndicatorCategory(code:DashboardInitializer.MANAGEMENT_EQUIPMENT+"TESTSTRUCTURE",name_en:"MANAGEMENT EQUIPMENT",redToYellowThreshold:60,yellowToGreenThreshold:80).save()
          def validIndicator=new Indicator(category:category, code:"DEGREE_STD_EQUIPMENTESTSTRUCTURE", name_en:"Degree of equipment standardization",name_fr:"Degree of equipment standardization",description_en:"Degree of equipment standardization",description_fr:"Degree of equipment standardization", formula_en:"(max (no. of equipments of type 1 from one manufacturer)+max(no. of equipments of type 2 from one manufacturer)+....) / (Total no. of equipments at the facility except equipments with status = For disposal or Disposed))", formula_fr:"(max (no. of equipments of type 1 from one manufacturer)+max(no. of equipments of type 2 from one manufacturer)+....) / (Total no. of equipments at the facility except equipments with status = For disposal or Disposed))",unit:"%",redToYellowThreshold:0.6,yellowToGreenThreshold:0.8, historicalPeriod:Indicator.HistoricalPeriod.YEARLY, historyItems:5, queryScript:DashboardInitializer.DEGREE_STANDARDIZATION_SIMPLE_SLD10, groupName_en:"Type of Equipment", groupName_fr:"Type of Equipment", groupQueryScript:DashboardInitializer.DEGREE_STANDARDIZATION_GROUP_SLD10,sqlQuery:true, active:true).save(failOnError: true, flush:true)
          def userDefVal=new UserDefinedVariable(code:"WO_REINCIDENCE_DAYSteststr",name_en:"Work order re-incidence period(days)",name_fr:"Work order re-incidence period(days)",currentValue:365.0).save(failOnError: true, flush:true)
          
    }
}
