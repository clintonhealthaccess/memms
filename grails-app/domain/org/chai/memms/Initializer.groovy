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
package org.chai.memms

import java.util.Date;
import org.apache.shiro.crypto.hash.Sha256Hash
import org.chai.memms.Contact;
import org.chai.memms.equipment.Department;
import org.chai.memms.equipment.Equipment;
import org.chai.memms.equipment.Equipment.Donor;
import org.chai.memms.equipment.EquipmentStatus;
import org.chai.memms.equipment.EquipmentType
import org.chai.memms.equipment.EquipmentType.Observation;
import org.chai.memms.equipment.Provider
import org.chai.memms.equipment.Equipment.PurchasedBy;
import org.chai.memms.equipment.EquipmentStatus.Status;
import org.chai.memms.equipment.Provider.Type;
import org.chai.location.CalculationLocation;
import org.chai.location.DataLocation;
import org.chai.location.DataLocationType;
import org.chai.location.Location;
import org.chai.location.LocationLevel;
import org.chai.memms.maintenance.Comment;
import org.chai.memms.maintenance.MaintenanceProcess;
import org.chai.memms.maintenance.NewEquipmentNotification
import org.chai.memms.maintenance.WorkOrderNotification
import org.chai.memms.maintenance.MaintenanceProcess.ProcessType;
import org.chai.memms.maintenance.Notification;
import org.chai.memms.maintenance.WorkOrder;
import org.chai.memms.maintenance.WorkOrder.Criticality;
import org.chai.memms.maintenance.WorkOrder.FailureReason;
import org.chai.memms.maintenance.WorkOrderStatus;
import org.chai.memms.maintenance.WorkOrderStatus.OrderStatus;
import org.chai.memms.security.Role
import org.chai.memms.security.User
import org.chai.memms.security.User.UserType
import org.chai.memms.util.Utils;

public class Initializer {
		
	static final String HEALTH_CENTER_GROUP = "Health Center"
	static final String DISTRICT_HOSPITAL_GROUP = "District Hospital"
	static final String MILITARY_HOSPITAL_GROUP = "Military Hospital"
	
	static final String NATIONAL = "National"
	static final String PROVINCE = "Province"
	static final String DISTRICT = "District"
	static final String SECTOR = "Sector"
	
	static final String RWANDA = "Rwanda"
	static final String KIGALI_CITY = "Kigali City"
	static final String NORTH = "North"
	static final String SOUTH = "South"
	static final String WEST = "West"
	static final String EAST = "East"
	static final String BURERA = "Burera"
	static final String BURERA1 = "Burera1"
	static final String BURERA2 = "Burera2"
	static final String GITARAMA = "Gitarama"
	static final String KIBUYE = "Kibuye"
	
	
	static final String BUTARO = "Butaro DH"
	static final String KIVUYE = "Kivuye HC"
	static final String BUNGWE = "Bungwe HC"
	
	static final String GAHINI = "Gahini DH"
	static final String NYANGE = "Nyange DH"
	static final String GITWE = "Gitwe HC"
	static final String MUSANGE = "Musange HC"
	
	static def createUsers() {
		if(!User.count()){
			//Default roles
			def defaultAdminRole = new Role(name: "Admin")
			defaultAdminRole.addToPermissions("*")
			defaultAdminRole.save(failOnError: true)
			
			def defaultDataClerkRole = new Role(name: "Data Clerk")
			defaultDataClerkRole.addToPermissions("home:*;menu:home,inventory,correctivemaintenance;account:*;equipmentType:getAjaxData;provider:getAjaxData;department:getAjaxData")
			defaultDataClerkRole.addToPermissions("equipment:filter,export,summaryPage,index,list,save,create,updateObsolete,edit")
			defaultDataClerkRole.addToPermissions("workOrder:*")
			defaultDataClerkRole.addToPermissions("notification:*")
			defaultDataClerkRole.addToPermissions("equipmentStatus:list,save,delete,edit,create")
			defaultDataClerkRole.save(failOnError: true, flush:true)
			
			def defaultTechnicianFacilityRole = new Role(name: "Technician Facility")
			defaultTechnicianFacilityRole.addToPermissions(defaultDataClerkRole.permissionString)
			defaultTechnicianFacilityRole.addToPermissions("menu:preventivemaintenance,admin;equipment:*")
			defaultTechnicianFacilityRole.addToPermissions("department:*;equipmentType:*;provider:*;equipment:*")
			defaultTechnicianFacilityRole.save(failOnError: true, flush:true)
			
			def defaultTechnicianMoHRole = new Role(name: "Technician MoH")
			defaultTechnicianMoHRole.addToPermissions(defaultDataClerkRole.permissionString)
			defaultTechnicianMoHRole.addToPermissions("menu:preventivemaintenance,admin,advanced;equipment:*")
			defaultTechnicianMoHRole.addToPermissions("department:*;equipmentType:*;provider:*;equipment:*")
			defaultTechnicianMoHRole.save(failOnError: true, flush:true)
			//End default roles
			
			def defaultClercRole = new Role(name: "Default Clerk")
			defaultClercRole.addToPermissions("equipment:")
			defaultClercRole.addToPermissions("equipmentStatus:*")
			defaultClercRole.addToPermissions("home:*")
			defaultClercRole.addToPermissions("menu:home")
			defaultClercRole.addToPermissions("menu:inventory,correctivemaintenance")
			defaultClercRole.addToPermissions("provider:getAjaxData")
			defaultClercRole.addToPermissions("equipmentType:getAjaxData")
			defaultClercRole.addToPermissions("department:getAjaxData")
			defaultClercRole.addToPermissions("account:editAccount")
			defaultClercRole.addToPermissions("account:saveAccount")
			defaultClercRole.addToPermissions("auth:newPassword")
			defaultClercRole.addToPermissions("auth:saveAccount")
			defaultClercRole.addToPermissions("auth:setPassword")
			defaultClercRole.addToPermissions("auth:retrievePassword")
			defaultClercRole.save(failOnError: true)
			
			def faultyClercRole = new Role(name: "Faulty Clerc Role")
			faultyClercRole.addToPermissions("home:*;menu:home;menu:inventory;account:*;equipment:summaryPage;equipment:index;equipment:list;equipment:save;equipment:create")
			faultyClercRole.addToPermissions("equipment:filter;equipment:export;equipmentType:getAjaxData;provider:getAjaxData;department:getAjaxData;equipmentStatus:create")
			faultyClercRole.addToPermissions("equipmentStatus:list;equipmentStatus:save;equipmentStatus:delete;equipmentStatus:edit;equipment:updateObsolete")
			faultyClercRole.save(failOnError: true)
			
			
			

			//Defining User
			//Default users
			def admin = new User(userType: UserType.ADMIN, location: CalculationLocation.findByCode(RWANDA), username: "admin", 
				firstname: "admin", lastname: "admin", email:'memms@memms.org', passwordHash: new Sha256Hash("admin").toHex(), active: true, 
				confirmed: true, uuid:'admin', defaultLanguage:'en', phoneNumber: '+250 11 111 11 11', organisation:'org')
			admin.addToRoles(defaultAdminRole)
			admin.save(failOnError: true)
			
			def userClerk= new User(userType: UserType.DATACLERK, location: CalculationLocation.findByCode(KIVUYE), username: "user", 
				firstname: "Data", lastname: "Clerk", email:'user@memms.org', passwordHash: new Sha256Hash("user").toHex(), active: true, 
				confirmed: true, uuid:'user', defaultLanguage:'en', phoneNumber: '+250 11 111 11 11', organisation:'org')
			userClerk.addToRoles(defaultDataClerkRole)
			userClerk.save(failOnError: true, flush:true)
			
			def userTechnicianFacility= new User(userType: UserType.TECHNICIANFACILITY, location: CalculationLocation.findByCode(KIVUYE), username: "techf",
				firstname: "technician", lastname: "facility", email:'techf@memms.org', passwordHash: new Sha256Hash("techf").toHex(), active: true,
				confirmed: true, uuid:'techf', defaultLanguage:'en', phoneNumber: '+250 11 111 11 11', organisation:'org')
			userTechnicianFacility.addToRoles(defaultTechnicianFacilityRole)
			userTechnicianFacility.save(failOnError: true, flush:true)
			
			def userTechnicianMoH= new User(userType: UserType.TECHNICIANMOH, location: CalculationLocation.findByCode(RWANDA), username: "techMoH",
				firstname: "technician", lastname: "MoH", email:'techMoH@memms.org', passwordHash: new Sha256Hash("techMoH").toHex(), active: true,
				confirmed: true, uuid:'techMoH', defaultLanguage:'en', phoneNumber: '+250 11 111 11 11', organisation:'org')
			userTechnicianMoH.addToRoles(defaultTechnicianMoHRole)
			userTechnicianMoH.save(failOnError: true, flush:true)
			//End default users
			
			//User with default clerk role
			def userClerkOne= new User(userType: UserType.OTHER,location: CalculationLocation.findByCode(KIVUYE), username: "userOne", 
				firstname: "user", lastname: "user", email:'user@memms.com', passwordHash: new Sha256Hash("user").toHex(), active: true, 
				confirmed: true, uuid:'userOne', defaultLanguage:'en', phoneNumber: '+250 11 111 11 11', organisation:'org')
			userClerkOne.addToRoles(defaultClercRole)
			userClerkOne.save(failOnError: true)
			
			//User with faulty clerk role
			def userClerkTwo= new User(userType: UserType.OTHER, location: CalculationLocation.findByCode(BURERA), username: "user1",
				firstname: "user1", lastname: "user1", email:'user1@memms.org', passwordHash: new Sha256Hash("user1").toHex(), active: true,
				confirmed: true, uuid:'user1', defaultLanguage:'en', phoneNumber: '+250 11 111 11 11', organisation:'org')
			userClerkTwo.addToRoles(faultyClercRole)
			userClerkTwo.save(failOnError: true)
			
			//User with permission assigned directly
			def userClerkThree= new User(userType: UserType.OTHER, location: CalculationLocation.findByCode(BURERA), username: "user2",
				firstname: "user2", lastname: "user2", email:'user2@memms.org', passwordHash: new Sha256Hash("user2").toHex(), active: true,
				confirmed: true, uuid:'user2', defaultLanguage:'en', phoneNumber: '+250 11 111 11 11', organisation:'org')
			userClerkThree.addToPermissions("equipment:")
			userClerkThree.addToPermissions("equipmentStatus:*")
			userClerkThree.addToPermissions("home:*")
			userClerkThree.addToPermissions("menu:home")
			userClerkThree.addToPermissions("menu:inventory")
			userClerkThree.addToPermissions("provider:getAjaxData")
			userClerkThree.addToPermissions("equipmentType:getAjaxData")
			userClerkThree.addToPermissions("department:getAjaxData")
			userClerkThree.addToPermissions("account:editAccount")
			userClerkThree.addToPermissions("account:saveAccount")
			userClerkThree.addToPermissions("auth:newPassword")
			userClerkThree.addToPermissions("auth:saveAccount")
			userClerkThree.addToPermissions("auth:setPassword")
			userClerkThree.addToPermissions("auth:retrievePassword")
			userClerkThree.save(failOnError: true)
		}
	}
	
	static def createDummyStructure() {
		
		if (!Location.count()) {
			//Add Location types
			def hc = newDataLocationType(['en':HEALTH_CENTER_GROUP], HEALTH_CENTER_GROUP);
			def dh = newDataLocationType(['en':DISTRICT_HOSPITAL_GROUP], DISTRICT_HOSPITAL_GROUP);
			//Add Location Level
			def country = newLocationLevel(['en':NATIONAL], NATIONAL,1)
			def province = newLocationLevel(['en':PROVINCE], PROVINCE,2)
			def district = newLocationLevel(['en':DISTRICT], DISTRICT,3)
			def sector = newLocationLevel(['en':SECTOR], SECTOR,4)
			//Add Location
			def rwanda = newLocation(['en':RWANDA], RWANDA,null,country)
			def kigali = newLocation(['en':KIGALI_CITY],KIGALI_CITY,rwanda,province)
			def north = newLocation(['en':NORTH], NORTH, rwanda, province)
			def south = newLocation(['en':SOUTH],SOUTH,rwanda,province)
			def west = newLocation(['en':WEST], WEST,rwanda,province)
			def east = newLocation(['en':EAST], EAST,rwanda,province)
			def burera = newLocation(['en':BURERA], BURERA, north, district)
			def burera1 = newLocation(['en':BURERA1], BURERA1, south, district)
			def burera2 = newLocation(['en':BURERA2], BURERA2, burera1, sector)
			def gitarama = newLocation(['en':GITARAMA], GITARAMA, west, district)
			def kibuye = newLocation(['en':KIBUYE], KIBUYE, west, district)
			
			//Add DataLocation
			def butaro = newDataLocation(['en':BUTARO],BUTARO,burera,dh)
			def kivuye = newDataLocation(['en':KIVUYE],KIVUYE,burera2,hc)
			def bungwe = newDataLocation(['en':BUNGWE],BUNGWE,burera1,hc)
			def gitwe = newDataLocation(['en':GAHINI],GAHINI,gitarama,dh)
			def musange = newDataLocation(['en':MUSANGE],MUSANGE,gitarama,hc)
			def nyange = newDataLocation(['en':NYANGE],NYANGE,kibuye,dh)
			def gahini = newDataLocation(['en':GITWE],GITWE,kibuye,hc)
		}		
	}
	static def createInventoryStructure(){
		if(!Department.count()){
			//Add Department
			def surgery = newDepartment(['en':'Surgery'],'SURGERY',['en':'Surgery Dep'])
			def pediatry = newDepartment(['en':'Pediatry'],'PEDIATRY',[:])
			def emeregency = newDepartment(['en':'Emeregency'],'EMERGENCY',['en':'Emeregency Dep'])
			def consultation = newDepartment(['en':'Consultation'],'CONSULTATION',['fr':'Consultation Dep'])
			def anaesthetics = newDepartment(['en':'Anaesthetics'],'ANAESTHETICS',['en':'Anaesthetics Dep'])
			def cardiology = newDepartment(['en':'Cardiology'],'CARDIOLOGY',['en':'Cardiology Dep'])
			def gynaecology = newDepartment(['en':'Gynaecology'],'GYNAECOLOGY',['en':'Anaesthetics Dep'])
		}
			
		if(!EquipmentType.count()){
			//Add equipment types as defined in ecri
			def typeOne = newEquipmentType("15810", ["en":"Accelerometers","fr":"Accelerometers"],["en":"used in memms"],Observation.USEDINMEMMS,now(),now(),25)
			def typeTwo = newEquipmentType("15819", ["en":"X-Ray Film Cutter"],["en":"used in memms"],Observation.USEDINMEMMS,now(),now(),13)
			def typeThree = newEquipmentType("15966", ["en":"Video Systems"],["en":"used in memms"],Observation.USEDINMEMMS,now(),now(),12)
			def typeFour = newEquipmentType("10035", ["en":"Adhesives, Aerosol"],["en":"not used in memms"],Observation.RETIRED,now(),now(),34)
			def typeFive = newEquipmentType("20760", ["en":"Pancreatic Drainage Tubes"],["en":"not used in memms"],Observation.RETIRED,now(),now(),54)
			def typeSix = newEquipmentType("20729", ["en":"PCR Test Tubes"],["en":"not used in memms"],Observation.RETIRED,now(),now(),67)
			def typeSeven = newEquipmentType("10026", ["en":"Adhesive Strips"],["en":"used in memms"],Observation.USEDINMEMMS,now(),now(),12)
			def typeEight = newEquipmentType("10124", ["en":"Anesthesia Kits"],["en":"used in memms"],Observation.USEDINMEMMS,now(),now(),7)
			def typeNine = newEquipmentType("10155", ["en":"Anklets"],["en":"used in memms"],Observation.USEDINMEMMS,now(),now(),60)
			def typeTen = newEquipmentType("10426", ["en":"Blood Donor Sets"],["en":"used in memms"],Observation.USEDINMEMMS,now(),now(),34)
		}
		
		if(!Provider.count()){
			def contactOne = newContact(['fr':'Manufacture Address Descriptions One'],"Manufacture Nokia","jkl@yahoo.com","0768-889-787","Street 154","8988")
			def sontactTwo = newContact(['en':'Supplier Address Descriptions Two'],"Manufacture Siemens","jk@yahoo.com","0768-888-787","Street 1654","8988")
			def contactThree = newContact(['en':'Address Descriptions Three'],"Manufacture HP","jkl2@yahoo.com","0768-888-787","Street 151","8988")
			def contactFour = newContact(['en':'Address Descriptions Four'],"Manufacture DELL","jkl3@yahoo.com","0768-132-787","Street 152","8988")
			def contactFive = newContact(['en':'Address Descriptions Five'],"Supplier Company 1","jkl4@yahoo.com","0768-657-787","Street 153","8988")
			def contactSix = newContact(['en':'Address Descriptions Six'],"Supplier Company 2","jkl5@yahoo.com","0768-342-787","Street 155","8988")
			def contactSeven = newContact(['en':'Address Descriptions Seven'],"Supplier Company 3","jkl6@yahoo.com","0768-123-787","Street 156","8988")
			def contactEight = newContact(['en':'Address Descriptions Eight'],"Manufacture and Supplier Ericson","jkl6@yahoo.com","0768-123-787","Street 156","8988")
			
			
			def manufactureOne = newProvider("ONE",Type.MANUFACTURER,contactOne)
			def manufactureTwo = newProvider("TWO",Type.MANUFACTURER,sontactTwo)
			def manufactureThree = newProvider("THREE",Type.MANUFACTURER,contactThree)
			def manufactureFour = newProvider("FOUR",Type.MANUFACTURER,contactFour)
			
			
			def supplierOne = newProvider("FIVE",Type.SUPPLIER,contactFive)
			def supplierTwo = newProvider("SIX",Type.SUPPLIER,contactSix)
			def supplierThree = newProvider("SEVEN",Type.SUPPLIER,contactSeven)
			
			def both = newProvider("EIGHT",Type.BOTH,contactEight)
		}
		
		
		if(!Equipment.count()){
			def equipmentOne = newEquipment("SERIAL01",PurchasedBy.BYDONOR,Donor.MOHPARTNER,"CHAI",false,24,"Room A1","",['en':'Equipment Descriptions'],
				getDate(22,07,2010),getDate(10,10,2010),"",now(),
				'MODEL1',
				DataLocation.findByCode(KIVUYE),
				Department.findByCode('SURGERY'),
				EquipmentType.findByCode("15810"),
				Provider.findByCode("ONE"),
				Provider.findByCode("FIVE")
				)
						
			def warrantyContactOne = newContact(['fr':'Warranty Address Descriptions One'],"Warranty","jk@yahoo.com","0768-888-787","Street 654","8988")
			def warrantyOne = newWarranty(warrantyContactOne,getDate(10, 12, 2010),22,false,[:])
			def statusOne= newEquipmentStatus(now(),User.findByUsername("admin"),Status.INSTOCK,equipmentOne,true,[:])
			
			equipmentOne.warranty=warrantyOne
			equipmentOne.addToStatus(statusOne)
			equipmentOne.save(failOnError:true)

			def equipmentTwo = newEquipment("SERIAL11",PurchasedBy.BYFACILITY,null,null,false,12,"Room A34","34900",['en':'Equipment Descriptions two'],
				getDate(12,01,2009),getDate(10,10,2009),"USD",now(),
				'MODEL2',
				DataLocation.findByCode(KIVUYE),
				Department.findByCode('PEDIATRY'),
				EquipmentType.findByCode("15819"),
				Provider.findByCode("TWO"),
				Provider.findByCode("FIVE")
				)
			
			def warrantyTwo = newWarranty(['en':'warranty one'],'warranty name1','email1@gmail.com',"0768-111-787","Street 154","898",getDate(10, 12, 2010),14,false,[:])
			def statusTwo= newEquipmentStatus(now(),User.findByUsername("admin"),Status.OPERATIONAL,equipmentTwo,true,[:])
			equipmentTwo.warranty=warrantyTwo
			equipmentTwo.addToStatus(statusTwo)
			equipmentTwo.save(failOnError:true)
			
			def equipmentThree = newEquipment("SERIAL12",PurchasedBy.BYFACILITY,null,null,true,34,"Room A1","98700",['en':'Equipment Descriptions three'],
				getDate(14,8,2008),getDate(10,01,2009),"EUR",now(),
				'MODEL3',
				DataLocation.findByCode(BUNGWE),
				Department.findByCode('EMERGENCY'),
				EquipmentType.findByCode("15966"),
				Provider.findByCode("TWO"),
				Provider.findByCode("FIVE")
				)
			
			def warrantyThree = newWarranty(['en':'warranty two'],'warranty name2','email2@gmail.com',"0768-222-787","Street 154","88",getDate(10, 12, 2010),12,false,[:])
			def statusThree= newEquipmentStatus(now(),User.findByUsername("admin"),Status.OPERATIONAL,equipmentThree,true,[:])
			equipmentThree.warranty=warrantyTwo
			equipmentThree.addToStatus(statusThree)
			equipmentThree.save(failOnError:true)
			
			def equipmentFour = newEquipment("SERIAL13",PurchasedBy.BYDONOR,Donor.MOHPARTNER,"Voxiva",false,12,"Room A1","",['en':'Equipment Descriptions four'],
				getDate(18,2,2011),getDate(10,10,2011),"",now(),
				'MODEL2',
				DataLocation.findByCode(KIVUYE),
				Department.findByCode('CARDIOLOGY'),
				EquipmentType.findByCode("10035"),
				Provider.findByCode("THREE"),
				Provider.findByCode("SEVEN")
				)
			
			def warrantyFour = newWarranty(['en':'warranty two'],'warranty name2','email2@gmail.com',"0768-222-787","Street 154","888",getDate(10, 12, 2010),24,false,[:])
			def statusFour = newEquipmentStatus(now(),User.findByUsername("admin"),Status.OPERATIONAL,equipmentFour,false,[:])
			def statusFourOne = newEquipmentStatus(now(),User.findByUsername("admin"),Status.UNDERMAINTENANCE,equipmentFour,true,[:])
			equipmentFour.warranty=warrantyFour
			equipmentFour.addToStatus(statusFour)
			equipmentFour.addToStatus(statusFourOne)
			equipmentFour.save(failOnError:true)
			
			def equipmentFive = newEquipment("SERIAL14",PurchasedBy.BYDONOR,Donor.MOHPARTNER,"Voxiva",true,34,"Room A1","",['en':'Equipment Descriptions five'],
				getDate(11,8,2008),getDate(11,10,2009),"",now(),
				'MODEL1',
				DataLocation.findByCode(BUNGWE),
				Department.findByCode('CONSULTATION'),
				EquipmentType.findByCode("20760"),
				Provider.findByCode("FOUR"),
				Provider.findByCode("SIX")
				)
			
			def warrantyFive = newWarranty(['en':'warranty Five'],'warranty name3','email3@gmail.com',"0768-333-787","Street 154","988",getDate(10, 12, 2010),8,false,[:])
			def statusFive= newEquipmentStatus(now(),User.findByUsername("admin"),Status.INSTOCK,equipmentFive,false,[:])
			def statusFiveOne = newEquipmentStatus(now(),User.findByUsername("admin"),Status.OPERATIONAL,equipmentFive,false,[:])
			def statusFiveTwo = newEquipmentStatus(now(),User.findByUsername("admin"),Status.UNDERMAINTENANCE,equipmentFive,true,[:])
			equipmentFive.warranty=warrantyFour
			equipmentFive.addToStatus(statusFive)
			equipmentFive.addToStatus(statusFiveOne)
			equipmentFive.addToStatus(statusFiveTwo)
			equipmentFive.save(failOnError:true)
			
			def equipmentSix = newEquipment("SERIAL15",PurchasedBy.BYFACILITY,null,null,true,4,"Room A1","290540",['en':'Equipment Descriptions six'],
				getDate(1,7,2000),getDate(12,7,2001),"RWF",now(),
				'MODEL3',
				DataLocation.findByCode(BUTARO),
				Department.findByCode('ANAESTHETICS'),
				EquipmentType.findByCode("20729"),
				Provider.findByCode("TWO"),
				Provider.findByCode("SIX")
				)
			
			def warrantySix = newWarranty(['en':'warranty four'],'warranty name4','email4@gmail.com',"0768-444-787","Street 154","8988",getDate(10, 12, 2010),48,false,[:])
			def statusSix= newEquipmentStatus(now(),User.findByUsername("admin"),Status.INSTOCK,equipmentSix,false,[:])
			def statusSixOne = newEquipmentStatus(now(),User.findByUsername("admin"),Status.OPERATIONAL,equipmentSix,false,[:])
			def statusSixTwo = newEquipmentStatus(now(),User.findByUsername("admin"),Status.UNDERMAINTENANCE,equipmentSix,true,[:])
			equipmentSix.warranty=warrantySix
			equipmentSix.addToStatus(statusSix)
			equipmentSix.addToStatus(statusSixOne)
			equipmentSix.addToStatus(statusSixTwo)
			equipmentSix.save(failOnError:true)
			
			def equipmentSeven = newEquipment("SERIAL07",PurchasedBy.BYFACILITY,null,null,true,4,"Room A1","290540",['en':'Equipment Descriptions seven'],
				getDate(1,7,2000),getDate(12,7,2001),"USD",now(),
				'MODEL3',
				DataLocation.findByCode(BUTARO),
				Department.findByCode('ANAESTHETICS'),
				EquipmentType.findByCode("10026"),
				Provider.findByCode("TWO"),
				Provider.findByCode("SIX")
				)
			
			def warrantySeven = newWarranty(['en':'warranty seven'],'warranty name7','email7@gmail.com',"0768-777-787","Street 174","8988",getDate(1, 12, 2010),4,false,[:])
			def statusSeven= newEquipmentStatus(now(),User.findByUsername("admin"),Status.INSTOCK,equipmentSeven,false,[:])
			def statusSevenOne = newEquipmentStatus(now(),User.findByUsername("admin"),Status.OPERATIONAL,equipmentSeven,false,[:])
			def statusSevenTwo = newEquipmentStatus(now(),User.findByUsername("admin"),Status.UNDERMAINTENANCE,equipmentSeven,true,[:])
			equipmentSeven.warranty=warrantySeven
			equipmentSeven.addToStatus(statusSeven)
			equipmentSeven.addToStatus(statusSevenOne)
			equipmentSeven.addToStatus(statusSevenTwo)
			equipmentSeven.save(failOnError:true)
			
			def equipmentEight = newEquipment("SERIAL08",PurchasedBy.BYFACILITY,null,null,true,4,"Room A3","290540",['en':'Equipment Descriptions eight'],
				getDate(1,7,2000),getDate(12,7,2001),"EUR",now(),
				'MODEL8',
				DataLocation.findByCode(BUTARO),
				Department.findByCode('ANAESTHETICS'),
				EquipmentType.findByCode("10155"),
				Provider.findByCode("EIGHT"),
				Provider.findByCode("EIGHT")
				)
			
			def warrantyEight = newWarranty(['en':'warranty four'],'warranty name4','email4@gmail.com',"0768-444-787","Street 154","8988",getDate(10, 12, 2010),17,false,[:])
			def statusEight= newEquipmentStatus(now(),User.findByUsername("admin"),Status.INSTOCK,equipmentEight,false,[:])
			def statusEightOne = newEquipmentStatus(now(),User.findByUsername("admin"),Status.OPERATIONAL,equipmentEight,false,[:])
			def statusEightTwo = newEquipmentStatus(now(),User.findByUsername("admin"),Status.UNDERMAINTENANCE,equipmentEight,true,[:])
			equipmentEight.warranty=warrantyEight
			equipmentEight.addToStatus(statusEight)
			equipmentEight.addToStatus(statusEightOne)
			equipmentEight.addToStatus(statusEightTwo)
			equipmentEight.save(failOnError:true)
			
			def equipmentNine = newEquipment("SERIAL09",PurchasedBy.BYFACILITY,null,null,true,4,"Room 9A1","290540",['en':'Equipment Descriptions Nine'],
				getDate(1,7,2000),getDate(12,7,2001),"RWF",now(),
				'MODEL3',
				DataLocation.findByCode(BUTARO),
				Department.findByCode('ANAESTHETICS'),
				EquipmentType.findByCode("10124"),
				Provider.findByCode("TWO"),
				Provider.findByCode("SIX")
				)
			
			def warrantyNine = newWarranty(['en':'warranty Nine'],'warranty name9','email94@gmail.com',"0768-999-787","Street 954","8989",getDate(10, 12, 2010),9,false,[:])
			def statusNine= newEquipmentStatus(now(),User.findByUsername("admin"),Status.INSTOCK,equipmentNine,false,[:])
			def statusNineOne = newEquipmentStatus(now(),User.findByUsername("admin"),Status.OPERATIONAL,equipmentNine,false,[:])
			def statusNineTwo = newEquipmentStatus(now(),User.findByUsername("admin"),Status.UNDERMAINTENANCE,equipmentNine,true,[:])
			equipmentNine.warranty=warrantyNine
			equipmentNine.addToStatus(statusNine)
			equipmentNine.addToStatus(statusNineOne)
			equipmentNine.addToStatus(statusNineTwo)
			equipmentNine.save(failOnError:true)
			
			def equipmentTen = newEquipment("SERIAL10",PurchasedBy.BYFACILITY,null,null,true,4,"Room 10A1","290540",['en':'Equipment Descriptions Ten'],
				getDate(1,7,2000),getDate(12,7,2001),"RWF",now(),
				'MODELTen3',
				DataLocation.findByCode(BUTARO),
				Department.findByCode('ANAESTHETICS'),
				EquipmentType.findByCode("10426"),
				Provider.findByCode("TWO"),
				Provider.findByCode("SIX")
				)
			
			def warrantyTen = newWarranty(['en':'warranty Ten'],'warranty name10','email410@gmail.com',"0768-100-787","Street 154","8988",getDate(10, 12, 2010),28,false,[:])
			def statusTen= newEquipmentStatus(now(),User.findByUsername("admin"),Status.INSTOCK,equipmentTen,false,[:])
			def statusTenOne = newEquipmentStatus(now(),User.findByUsername("admin"),Status.OPERATIONAL,equipmentTen,false,[:])
			def statusTenTwo = newEquipmentStatus(now(),User.findByUsername("admin"),Status.UNDERMAINTENANCE,equipmentTen,true,[:])
			equipmentTen.warranty=warrantyTen
			equipmentTen.addToStatus(statusTen)
			equipmentTen.addToStatus(statusTenOne)
			equipmentTen.addToStatus(statusTenTwo)
			equipmentTen.save(failOnError:true)

		}
	}
	
	static def createCorrectiveMaintenanceStructure(){
		
		def admin = User.findByUsername("admin")
		def user = User.findByUsername("user")
		def techFac = User.findByUsername("techf")
		def techMoH = User.findByUsername("techMoH")
		
		def equipment01 =Equipment.findBySerialNumber("SERIAL01")
		def equipment09 =Equipment.findBySerialNumber("SERIAL09")
		def equipment11 =Equipment.findBySerialNumber("SERIAL11")
		
		def workOrderOne =  newWorkOrder(equipment01,"First order",Criticality.NORMAL,user,now()-1,FailureReason.NOTSPECIFIED,OrderStatus.OPENATFOSA)
		def statusOne =  newWorkOrderStatus(workOrderOne,OrderStatus.OPENATFOSA,now()-1,user,false)
		def statusTwo =  newWorkOrderStatus(workOrderOne,OrderStatus.OPENATMMC,now(),user,true)
		def notifationOne = newWorkOrderNotification(workOrderOne, user, techFac,now(), "notifationOne")
		def notifationTwo = newWorkOrderNotification(workOrderOne, techFac, user,now(), "I am currentlly working on this, but needs further review. Am making this long to see how it fits when reading it.")
		def notifationThree = newWorkOrderNotification(workOrderOne, techFac, techMoH,now(), "notifationThree")
		workOrderOne.addToStatus(statusOne)
		workOrderOne.addToStatus(statusTwo)
		workOrderOne.save(failOnError:true)
		
		def workOrderTwo =  newWorkOrder(equipment01,"Second order",Criticality.LOW,admin,now(),FailureReason.NOTSPECIFIED,OrderStatus.OPENATFOSA)
		def statusThree =  newWorkOrderStatus(workOrderTwo,OrderStatus.OPENATFOSA,now()-1,admin,false)
		
		def workOrderFive =  newWorkOrder(equipment01,"Closed order",Criticality.HIGH,user,now()-3,FailureReason.MISUSE,OrderStatus.OPENATFOSA)
		
		def statusFour =  newWorkOrderStatus(workOrderFive,OrderStatus.OPENATFOSA,now()-3,user,false)
		def statusFive =  newWorkOrderStatus(workOrderFive,OrderStatus.OPENATMMC,now()-2,user,true)		
		def statusSix =  newWorkOrderStatus(workOrderFive,OrderStatus.OPENATFOSA,now()-1,user,false)
		def statusSeven =  newWorkOrderStatus(workOrderFive,OrderStatus.CLOSEDFIXED,now(),user,false)
		workOrderFive.closedOn = now()
		workOrderTwo.save(failOnError:true)
		def notifationFour = newWorkOrderNotification(workOrderOne, user, techFac,now(), "Solve this for me")
		def notifationFive = newWorkOrderNotification(workOrderOne, techFac, user,now(), "More information needed")
		workOrderFive.addToStatus(statusFour)
		workOrderFive.addToStatus(statusFive)
		workOrderFive.addToStatus(statusSix)
		workOrderFive.addToStatus(statusSeven)
		workOrderFive.save(failOnError:true)
		
		
		
		equipment01.addToWorkOrders(workOrderOne)
		equipment01.addToWorkOrders(workOrderTwo)
		equipment01.addToWorkOrders(workOrderFive)
		
		equipment01.save(failOnError:true)
		
		def processOne = newMaintenanceProcess(workOrderOne,ProcessType.ACTION,"cleaning material", now(), admin)
		def processTwo = newMaintenanceProcess(workOrderOne,ProcessType.ACTION,"open material", now(), admin)
		def processThree = newMaintenanceProcess(workOrderOne,ProcessType.MATERIAL,"material piece one", now(), admin)
		def processFour = newMaintenanceProcess(workOrderOne,ProcessType.MATERIAL,"material piece two", now(), admin)
		
		workOrderOne.addToProcesses(processOne)
		workOrderOne.addToProcesses(processTwo)
		workOrderOne.addToProcesses(processThree)
		workOrderOne.addToProcesses(processFour)
		
		def commentOne = newComment(workOrderOne, admin, now(), "comment one")
		def commentTwo = newComment(workOrderOne, admin, now()-1, "comment one")
		def commentThree = newComment(workOrderOne, admin, now()-2, "comment one")
		
		workOrderOne.addToComments(commentOne)
		workOrderOne.addToComments(commentTwo)
		workOrderOne.addToComments(commentThree)
		
		workOrderOne.save(failOnError:true)
		
		def workOrderThree =  newWorkOrder(equipment09,"Third order",Criticality.NORMAL,user,now(),FailureReason.NOTSPECIFIED,OrderStatus.OPENATFOSA)
		def statusEight =  newWorkOrderStatus(workOrderThree,OrderStatus.OPENATFOSA,now(),admin,false)
		equipment09.addToWorkOrders(workOrderThree)
		equipment09.save(failOnError:true)
		
		def workOrderFour =  newWorkOrder(equipment11,"Fourth order",Criticality.HIGH,admin,now(),FailureReason.NOTSPECIFIED,OrderStatus.OPENATFOSA)
		def statusNine =  newWorkOrderStatus(workOrderFour,OrderStatus.OPENATFOSA,now(),admin,false)
		equipment11.addToWorkOrders(workOrderFour)
		equipment11.save(failOnError:true)	
	}
	
	//Models definition
	//Corrective Maintenance
	public static def newWorkOrder(def equipment, def description, def criticality, def addedBy, def openOn,def failureReason,def currentStatus){
		return new WorkOrder(equipment:equipment,description: description,criticality:criticality,addedBy:addedBy,openOn: openOn,currentStatus:currentStatus,failureReason:failureReason).save(failOnError:true)
	}
	public static def newWorkOrder(def equipment, def description, def criticality,def addedBy, def openOn, def closedOn, def failureReason,def currentStatus){
		return new WorkOrder(equipment:equipment, description:description, criticality:criticality,addedBy:addedBy, openOn: openOn, closedOn:closedOn, currentStatus:currentStatus,failureReason:failureReason).save(failOnError:true)
	}
	public static newWorkOrderNotification(def workOrder, def sender, def receiver,def writtenOn, def content){
		return new WorkOrderNotification(workOrder: workOrder, sender: sender, receiver: receiver, writtenOn: writtenOn, content: content).save(failOnError: true)
	}
	
	public static newNewEquipmentNotification(def dataLocation, def department, def sender, def receiver,def writtenOn, def content){
		return new NewEquipmentNotification(dataLocation:dataLocation, department:department, sender: sender, receiver: receiver, writtenOn: writtenOn, content: content).save(failOnError: true)
	}
	
	public static newComment(def workOrder, def writtenBy, def writtenOn, def content){
		return new Comment(workOrder: workOrder, writtenBy: writtenBy, writtenOn: writtenOn, content: content ).save(failOnError: true)
	}
	public static newMaintenanceProcess(def workOrder,def type, def name, def addedOn, def addedBy){
		return new MaintenanceProcess(workOrder: workOrder,type: type,name: name, addedOn: addedOn, addedBy: addedBy ).save(failOnError: true)
	}
	public static newWorkOrderStatus(def workOrder,def status,def changeOn,def changedBy,def escalation){
		def stat = new WorkOrderStatus(workOrder:workOrder,status:status,changeOn:changeOn,changedBy:changedBy,escalation:escalation)
		workOrder.currentStatus= status
		workOrder.addToStatus(stat)
		return stat;
	}
	
	//Inventory
	public static def newEquipment(def serialNumber,def purchaser,def donor,def donorName,def obsolete,def expectedLifeTime,def room,def purchaseCost,def descriptions,def manufactureDate, def purchaseDate,def currency,def registeredOn,def model,def dataLocation,def department, def type,def manufacture,def supplier){
		def equipment = new Equipment(serialNumber:serialNumber,purchaser:purchaser,donor:donor,donorName:donorName,obsolete:obsolete,room:room,purchaseCost:purchaseCost,currency:currency,manufactureDate:manufactureDate,purchaseDate:purchaseDate,registeredOn:registeredOn,model:model,dataLocation:dataLocation,expectedLifeTime:expectedLifeTime,department:department,type:type,manufacturer:manufacture,supplier:supplier);
		Utils.setLocaleValueInMap(equipment,descriptions,"Descriptions")
		equipment.genarateAndSetEquipmentCode()
		return equipment.save(failOnError: true)
	}

	public static def newEquipmentStatus(def dateOfEvent,def changedBy,def value, def equipment,def current,def reasons){
		def statusChangeDate = now()
		def status = new EquipmentStatus(dateOfEvent:dateOfEvent,changedBy:changedBy,status:value,equipment:equipment,current:current,statusChangeDate:statusChangeDate)
		Utils.setLocaleValueInMap(status,reasons,"Reasons")
		return status.save(failOnError: true)
	}

	public static def newContact(def addressDescriptions,def contactName,def email, def phone, def street, def poBox){
		def contact = new Contact(contactName:contactName,email:email,phone:phone,street:street,poBox:poBox)
		Utils.setLocaleValueInMap(contact,addressDescriptions,"AddressDescriptions")
		return contact;
	}
	
	public static def newProvider(def code, def type, def contact){
		return new Provider(code:code,type:type,contact:contact).save(failOnError: true)
	}
	
	public static def newProvider(def code, def type, def addressDescriptions, def contactName,def email, def phone, def street, def poBox){
		def contact = newContact(addressDescriptions,contactName,email,phone,street,poBox)
		return newProvider(code,type,contact)
	}
	
	public static def newWarranty(def contact, def startDate,def numberOfMonth,def sameAsSupplier,def descriptions){
		def warranty = new Warranty(contact:contact,startDate:startDate,numberOfMonth:numberOfMonth,sameAsSupplier:sameAsSupplier)
		Utils.setLocaleValueInMap(warranty,descriptions,"Descriptions")
		return warranty
	}
	
	public static def newWarranty(def addressDescriptions,def contactName,def email,def phone,def street,def poBox,def startDate,def numberOfMonth,def sameAsSupplier,def descriptions){
		def contact = newContact(addressDescriptions,contactName,email,phone,street,poBox)
		return newWarranty(contact, startDate,numberOfMonth,sameAsSupplier,descriptions)
	}
		
	public static def newEquipmentType(def code, def names,def descriptions, def observation, def addedOn, def lastModifiedOn,def expectedLifeTime = 12){
		def type = new EquipmentType(code:code,observation:observation,addedOn:addedOn,lastModifiedOn:lastModifiedOn,expectedLifeTime:expectedLifeTime)
		Utils.setLocaleValueInMap(type,names,"Names")
		Utils.setLocaleValueInMap(type,descriptions,"Descriptions")
		return type.save(failOnError: true)
	}
	
	public static def newDepartment(def names,def code, def descriptions){
		def department = new Department(code:code)
		Utils.setLocaleValueInMap(department,names,"Names") 
		Utils.setLocaleValueInMap(department,descriptions,"Descriptions")
		return department.save(failOnError: true)
	}
	
	//Location
	public static def newDataLocationType(def names, def code) {
		def dataLocationType = new DataLocationType(code: code)
		Utils.setLocaleValueInMap(dataLocationType,names,"Names")
		return dataLocationType.save(failOnError: true)
	}
	
	public static def newLocationLevel(def names, def code, def order) {
		def locationLevel = new LocationLevel(code: code,order:order)
		Utils.setLocaleValueInMap(locationLevel,names,"Names")
		return locationLevel.save(failOnError: true)
	}
	
	public static def newLocation(def names, def code, def parent, def level) {
		def location = new Location(code: code, parent: parent, level: level)
		Utils.setLocaleValueInMap(location,names,"Names")
		location.save(failOnError: true)
		level.addToLocations(location)
		level.save(failOnError: true)
		if (parent != null) {
			parent.addToChildren(location)
			parent.save(failOnError: true)
		}
		return location
	}
	
	public static def newDataLocation(def names, def code, def location, def type) {
		def dataLocation = new DataLocation(code: code, location: location, type: type)
		Utils.setLocaleValueInMap(dataLocation,names,"Names")
		dataLocation.save(failOnError: true)
		if (location != null) {
			location.addToDataLocations(dataLocation)
			location.save(failOnError: true)
		}
		if (type != null) {
			type.addToDataLocations(dataLocation)
			type.save(failOnError: true)
	   }
		return dataLocation
	}
	
	static mapping ={
		table "memms_initializer_data"
		version false
	}
   public static def now(){
	   return new Date()
   }
   public static Date getDate( int day, int month, int year) {
	   final Calendar calendar = Calendar.getInstance();

	   calendar.clear();
	   calendar.set( Calendar.YEAR, year );
	   calendar.set( Calendar.MONTH, month - 1 );
	   calendar.set( Calendar.DAY_OF_MONTH, day );

	   return calendar.getTime();
   }
}
