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
import org.chai.memms.Period;
import org.chai.memms.Warranty;
import org.chai.memms.inventory.Department;
import org.chai.memms.inventory.Equipment;
import org.chai.memms.inventory.NotificationEquipment;
import org.chai.memms.inventory.Equipment.Donor;
import org.chai.memms.inventory.EquipmentStatus;
import org.chai.memms.inventory.EquipmentType;
import org.chai.memms.inventory.PreventiveAction;
import org.chai.memms.inventory.EquipmentType.Observation;
import org.chai.memms.inventory.Provider;
import org.chai.memms.inventory.Equipment.PurchasedBy;
import org.chai.memms.inventory.Provider.Type;
import org.chai.location.CalculationLocation;
import org.chai.location.DataLocation;
import org.chai.location.DataLocationType;
import org.chai.location.Location;
import org.chai.location.LocationLevel;
import org.chai.memms.corrective.maintenance.Comment;
import org.chai.memms.corrective.maintenance.CorrectiveProcess;
import org.chai.memms.corrective.maintenance.CorrectiveProcess.ProcessType;
import org.chai.memms.corrective.maintenance.NotificationWorkOrder
import org.chai.memms.corrective.maintenance.WorkOrder;
import org.chai.memms.corrective.maintenance.WorkOrder.Criticality;
import org.chai.memms.corrective.maintenance.WorkOrder.FailureReason;
import org.chai.memms.corrective.maintenance.WorkOrderStatus;
import org.chai.memms.corrective.maintenance.WorkOrderStatus.OrderStatus;
import org.chai.memms.preventive.maintenance.DurationBasedOrder;
import org.chai.memms.preventive.maintenance.Prevention;
import org.chai.memms.preventive.maintenance.PreventiveOrder.PreventionResponsible;
import org.chai.memms.preventive.maintenance.PreventiveOrder.PreventiveOrderStatus;
import org.chai.memms.preventive.maintenance.WorkBasedOrder
import org.chai.memms.preventive.maintenance.WorkBasedOrder.WorkIntervalType
import org.chai.memms.preventive.maintenance.DurationBasedOrder.OccurencyType;
import org.chai.memms.preventive.maintenance.PreventiveOrder.PreventiveOrderType;
import org.chai.memms.preventive.maintenance.PreventiveProcess;
import org.chai.memms.preventive.maintenance.PreventiveOrder
import org.chai.memms.security.Role
import org.chai.memms.security.User
import org.chai.memms.security.User.UserType
import org.chai.memms.spare.part.SparePart;
import org.chai.memms.spare.part.SparePartType;
import org.chai.memms.spare.part.SparePart.SparePartPurchasedBy;
import org.chai.memms.spare.part.SparePart.SparePartStatus;
import org.chai.memms.inventory.EquipmentStatus.Status;
import org.chai.memms.spare.part.SparePart.StockLocation;

import org.chai.memms.util.Utils;
import org.chai.memms.TimeDate;
import org.chai.memms.TimeSpend

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
	static final String HUYE = "Huye"
	static final String NYARUGURU = "Nyaruguru"
	static final String GITARAMA = "Gitarama"
	static final String KIBUYE = "Kibuye"
	
	
	static final String BUTARO = "Butaro DH"
	static final String GAHINI = "Gahini DH"
	static final String NYANGE = "Nyange DH"
	static final String NYANZA = "Nyanza DH"
	
	static final String KIVUYE = "Kivuye HC"
	static final String BUNGWE = "Bungwe HC"
	static final String GITWE = "Gitwe HC"
	static final String MUSANGE = "Musange HC"
	static final String KIREHE = "Kirehe HC"
	static final String KAYONZA = "Kayonza HC"
	static final String RUHINDO = "Ruhindo HC"
	
	public static createUsers() {
		if(!User.count()){
			//Default roles
			def defaultAdminRole = new Role(name: "Admin")
			defaultAdminRole.addToPermissions("*")
			defaultAdminRole.save(failOnError: true)
			
			def defaultSystemRole = new Role(name: "System")
			defaultSystemRole.addToPermissions("*")
			defaultSystemRole.save(failOnError: true, flush:true)
			
			def defaultTechnicianDHRole = new Role(name: "Technician DH")
			defaultTechnicianDHRole.addToPermissions("equipment:*")
			defaultTechnicianDHRole.addToPermissions("equipmentView:*")
			defaultTechnicianDHRole.addToPermissions("equipmentStatus:*")
			defaultTechnicianDHRole.addToPermissions("sparePart:*")
			defaultTechnicianDHRole.addToPermissions("sparePartView:*")
			defaultTechnicianDHRole.addToPermissions("notificationEquipment:*")
			defaultTechnicianDHRole.addToPermissions("workOrder:*")
			defaultTechnicianDHRole.addToPermissions("workOrderView:*")
			defaultTechnicianDHRole.addToPermissions("preventiveOrder:*")
			defaultTechnicianDHRole.addToPermissions("preventiveOrderView:*")
			defaultTechnicianDHRole.addToPermissions("notificationWorkOrder:*")
			defaultTechnicianDHRole.addToPermissions("preventiveOrderView:*")
			defaultTechnicianDHRole.addToPermissions("home:*")
			defaultTechnicianDHRole.addToPermissions("menu:home,inventory,maintenance,correctivemaintenance,preventivemaintenance,sparePart,reports,dashboard")
			defaultTechnicianDHRole.addToPermissions("provider:getAjaxData")
			defaultTechnicianDHRole.addToPermissions("equipmentType:getAjaxData")
			defaultTechnicianDHRole.addToPermissions("sparePartType:getAjaxData")
			defaultTechnicianDHRole.addToPermissions("department:getAjaxData")
			defaultTechnicianDHRole.addToPermissions("account:editAccount")
			defaultTechnicianDHRole.addToPermissions("account:saveAccount")
			defaultTechnicianDHRole.addToPermissions("auth:newPassword")
			defaultTechnicianDHRole.addToPermissions("auth:saveAccount")
			defaultTechnicianDHRole.addToPermissions("auth:setPassword")
			defaultTechnicianDHRole.addToPermissions("auth:retrievePassword")
			defaultTechnicianDHRole.addToPermissions("menu:reports")
			defaultTechnicianDHRole.addToPermissions("menu:dashboard")
			defaultTechnicianDHRole.addToPermissions("menu:listing")
			defaultTechnicianDHRole.addToPermissions("dashboard:*")
			defaultTechnicianDHRole.addToPermissions("listing:*")
			defaultTechnicianDHRole.save(failOnError: true, flush:true)
			
			def defaultTechnicianMMCRole = new Role(name: "Technician MMC")
			defaultTechnicianMMCRole.addToPermissions("*")
			defaultTechnicianMMCRole.save(failOnError: true, flush:true)
			
			def defaultTitulaireHCRole = new Role(name: "Titulaire HC")
			defaultTitulaireHCRole.addToPermissions("equipment:list")
			defaultTitulaireHCRole.addToPermissions("equipmentView:list")
			defaultTitulaireHCRole.addToPermissions("equipmentStatus:list")
			defaultTitulaireHCRole.addToPermissions("notificationEquipment:list")
			defaultTitulaireHCRole.addToPermissions("workOrder:list")
			defaultTitulaireHCRole.addToPermissions("workOrderView:list")
			defaultTitulaireHCRole.addToPermissions("preventiveOrder:list")
			defaultTitulaireHCRole.addToPermissions("preventiveOrderView:list")
			defaultTitulaireHCRole.addToPermissions("notificationWorkOrder:list")
			defaultTitulaireHCRole.addToPermissions("preventiveOrderView:list")
			defaultTitulaireHCRole.addToPermissions("home:*")
			defaultTitulaireHCRole.addToPermissions("menu:home,inventory,maintenance,correctivemaintenance,preventivemaintenance")
			defaultTitulaireHCRole.addToPermissions("account:editAccount")
			defaultTitulaireHCRole.addToPermissions("account:saveAccount")
			defaultTitulaireHCRole.addToPermissions("auth:newPassword")
			defaultTitulaireHCRole.addToPermissions("auth:saveAccount")
			defaultTitulaireHCRole.addToPermissions("auth:setPassword")
			defaultTitulaireHCRole.addToPermissions("auth:retrievePassword")
			defaultTitulaireHCRole.save(failOnError: true, flush:true)
			
			def defaultHospitalDepartmentRole = new Role(name: "Hospital Department")
			defaultHospitalDepartmentRole.addToPermissions("*")
			defaultHospitalDepartmentRole.save(failOnError: true, flush:true)
			
			
			//End default roles
			
			def defaultClercRole = new Role(name: "Default Clerk")
			defaultClercRole.addToPermissions("equipment:")
			defaultClercRole.addToPermissions("equipmentStatus:*")
			defaultClercRole.addToPermissions("home:*")
			defaultClercRole.addToPermissions("menu:home")
			defaultClercRole.addToPermissions("menu:inventory,maintenance,correctivemaintenance,preventivemaintenance")
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
			def admin = new User(userType: UserType.ADMIN, location: Location.findByCode(RWANDA), username: "admin", 
				firstname: "admin", lastname: "admin", email:'memms@memms.org', passwordHash: new Sha256Hash("admin").toHex(), active: true, 
				confirmed: true, uuid:'admin', defaultLanguage:'en', phoneNumber: '+250 11 111 11 11', organisation:'org')
			admin.addToRoles(defaultAdminRole)
			admin.save(failOnError: true)
			
			def techDH= new User(userType: UserType.TECHNICIANDH, location: DataLocation.findByCode(NYANZA), username: "techDH", 
				firstname: "Technician", lastname: "DH", email:'techDH@memms.org', passwordHash: new Sha256Hash("techDH").toHex(), active: true, 
				confirmed: true, uuid:'techDH', defaultLanguage:'en', phoneNumber: '+250 11 111 11 11', organisation:'org')
			techDH.addToRoles(defaultTechnicianDHRole)
			techDH.save(failOnError: true, flush:true)
			
			def techMMC= new User(userType: UserType.TECHNICIANMMC, location: Location.findByCode(RWANDA), username: "techMMC",
				firstname: "Technician", lastname: "MMC", email:'techMMC@memms.org', passwordHash: new Sha256Hash("techMMC").toHex(), active: true,
				confirmed: true, uuid:'techMMC', defaultLanguage:'en', phoneNumber: '+250 11 111 11 11', organisation:'org')
			techMMC.addToRoles(defaultTechnicianMMCRole)
			techMMC.save(failOnError: true, flush:true)
			
			def titulaireHC= new User(userType: UserType.TITULAIREHC, location: DataLocation.findByCode(KIVUYE), username: "titulaireHC",
				firstname: "Titulaire", lastname: "HC", email:'titulaireHC@memms.org', passwordHash: new Sha256Hash("titulaireHC").toHex(), active: true,
				confirmed: true, uuid:'techMoH', defaultLanguage:'en', phoneNumber: '+250 11 111 11 11', organisation:'org')
			titulaireHC.addToRoles(defaultTitulaireHCRole)
			titulaireHC.save(failOnError: true, flush:true)
			
			def hospitalDepartment= new User(userType: UserType.HOSPITALDEPARTMENT, location: DataLocation.findByCode(NYANZA), username: "hospitalDepartment",
				firstname: "Hospital", lastname: "Department", email:'hospitalDepartment@memms.org', passwordHash: new Sha256Hash("hospitalDepartment").toHex(), active: true,
				confirmed: true, uuid:'hospitalDepartment', defaultLanguage:'en', phoneNumber: '+250 11 111 11 11', organisation:'org')
			hospitalDepartment.addToRoles(defaultHospitalDepartmentRole)
			hospitalDepartment.save(failOnError: true, flush:true)
			//End default users
			
			//User with default clerk role
			def userClerkOne= new User(userType: UserType.OTHER,location: DataLocation.findByCode(KIVUYE), username: "userOne", 
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
	
	public static createDummyStructure() {
		
		if (!Location.count()) {

			//Add Location types           
			def hc = newDataLocationType(['en':HEALTH_CENTER_GROUP], HEALTH_CENTER_GROUP);
			def dh = newDataLocationType(['en':DISTRICT_HOSPITAL_GROUP], DISTRICT_HOSPITAL_GROUP);

            //Add Location Level
			def country = newLocationLevel(['en':NATIONAL], NATIONAL,1)
			def province = newLocationLevel(['en':PROVINCE], PROVINCE,2)
			def district = newLocationLevel(['en':DISTRICT], DISTRICT,3)
			def sector = newLocationLevel(['en':SECTOR], SECTOR,4)

			def rwandaCoordinates = '[[[29.9811,-1.9094],[29.9916,-1.9052],[29.9811,-1.891],[29.9767,-1.8768],[29.9783,-1.8697],[29.9883,-1.8633],'+
									'[29.9919,-1.8528],[29.9905,-1.8452],[29.9991,-1.8387],[30.0146,-1.8335],[30.0209,-1.8499],[30.0254,-1.8533],'+
									'[30.0286,-1.8626],[30.048,-1.8479],[30.0591,-1.8287],[30.0692,-1.8243],[30.0796,-1.8235],[30.096,-1.8164],'+
									'[30.0981,-1.8106],[30.1132,-1.8183],[30.1183,-1.8167],[30.1274,-1.8064],[30.132,-1.7769],[30.1418,-1.778],'+
									'[30.1476,-1.7908],[30.1612,-1.7874],[30.1813,-1.8028],[30.2055,-1.8302],[30.2167,-1.8383],[30.2349,-1.8432],'+
									'[30.2471,-1.8432],[30.2469,-1.8541],[30.2631,-1.86],[30.2572,-1.8766],[30.2572,-1.8773],[30.2573,-1.8926],'+
									'[30.2723,-1.8974],[30.2702,-1.907],[30.276,-1.9239],[30.2758,-1.9337],[30.2633,-1.9506],[30.2628,-1.9505],'+
									'[30.2586,-1.9497],[30.2578,-1.9496],[30.2534,-1.9488],[30.2335,-1.9583],[30.2316,-1.9734],[30.236,-1.9805],'+
									'[30.2332,-1.9986],[30.2294,-2.0064],[30.2282,-2.0382],[30.224,-2.0522],[30.2149,-2.0657],[30.2141,-2.0724],'+
									'[30.2089,-2.0742],[30.209,-2.0756],[30.2035,-2.0712],[30.2053,-2.0645],[30.1998,-2.0608],[30.1991,-2.0599],'+
									'[30.1898,-2.0538],[30.1886,-2.0405],[30.1777,-2.0338],[30.1714,-2.0263],[30.167,-2.028],[30.1659,-2.0283],'+
									'[30.1573,-2.0288],[30.1468,-2.0383],[30.1413,-2.036],[30.1334,-2.0441],[30.1333,-2.0429],[30.1256,-2.0481],'+
									'[30.1241,-2.0474],[30.1249,-2.0531],[30.1167,-2.0524],[30.1132,-2.0574],[30.1029,-2.0539],[30.095,-2.062],'+
									'[30.0896,-2.0571],[30.0805,-2.0611],[30.0801,-2.0607],[30.0731,-2.0617],[30.0626,-2.0678],[30.0534,-2.0624],'+
									'[30.0561,-2.0534],[30.0455,-2.052],[30.0445,-2.0564],[30.044,-2.0567],[30.0385,-2.0592],[30.0383,-2.0597],'+
									'[30.0382,-2.0659],[30.0286,-2.0622],[30.0197,-2.0704],[30.0184,-2.0714],[30.0178,-2.0721],[30.0112,-2.0698],'+
									'[30.0104,-2.0707],[30.0017,-2.0633],[29.9984,-2.0665],[29.998,-2.0661],[29.9931,-2.0654],[29.9851,-2.053],'+
									'[29.9855,-2.0527],[29.9832,-2.0403],[29.9875,-2.0411],[29.9965,-2.0278],[30.0067,-2.0226],[30.0058,-2.0221],'+
									'[30.0042,-2.0196],[30.0144,-2.0111],[30.0099,-2.0103],[29.9945,-1.995],[29.9927,-1.9856],[29.9997,-1.9772],'+
									'[29.999,-1.9764],[29.9986,-1.9751],[29.9984,-1.9744],[29.9978,-1.968],[30.0025,-1.9642],[30.0012,-1.9537],'+
									'[30.0004,-1.9544],[29.9875,-1.9316],[29.9871,-1.9308],[29.9876,-1.9284],[29.9879,-1.9282],[29.9873,-1.9217],'+
									'[29.9868,-1.921],[29.9811,-1.9094]]]'

			//Add Location
			def rwanda = newLocation(['en':RWANDA], RWANDA,null,country,rwandaCoordinates)
			
			def kigali = newLocation(['en':KIGALI_CITY],KIGALI_CITY,rwanda,province,'')
			def north = newLocation(['en':NORTH], NORTH, rwanda, province,'')
			def south = newLocation(['en':SOUTH],SOUTH,rwanda,province,'')
			def west = newLocation(['en':WEST], WEST,rwanda,province,'')
			def east = newLocation(['en':EAST], EAST,rwanda,province,'')
			
			def burera = newLocation(['en':BURERA], BURERA, north, district,'')
			def huye = newLocation(['en':HUYE], HUYE, south, district,'')
			def nyaruguru = newLocation(['en':NYARUGURU], NYARUGURU, huye, sector,'')
			def gitarama = newLocation(['en':GITARAMA], GITARAMA, west, district,'')
			def kibuye = newLocation(['en':KIBUYE], KIBUYE, west, district,'')
			
			//Add DataLocation
			def butaro = newDataLocation(['en':BUTARO],BUTARO,burera,dh, '[29.836585,-1.408931]')
			def ruhindo = newDataLocation(['en':RUHINDO],RUHINDO,burera,hc, '')
			butaro.addToManages(ruhindo)
			butaro.save(failOnError:true)
			
			def gahini  = newDataLocation(['en':GAHINI],GAHINI,gitarama,dh, '[30.48322,-1.84073]')
			def musange = newDataLocation(['en':MUSANGE],MUSANGE,gitarama,hc, '[29.78134,-2.07579]')
			gahini.addToManages(musange)
			gahini.save(failOnError:true)
			
			def nyanza = newDataLocation(['en':NYANZA],NYANZA,huye,dh, '[29.73036,-2.59908]')
			def kivuye = newDataLocation(['en':KIVUYE],KIVUYE,nyaruguru,hc, '[29.93128,-1.49218]')
			def bungwe = newDataLocation(['en':BUNGWE],BUNGWE,huye,hc, '[29.97292,-1.503]')
			[kivuye,bungwe].each{it -> nyanza.addToManages(it)}
			nyanza.save(failOnError:true)
			
			def nyange = newDataLocation(['en':NYANGE],NYANGE,kibuye,dh, '')
			def gitwe = newDataLocation(['en':GITWE],GITWE,kibuye,hc, '[29.68998,-2.24103]')
			def kirehe = newDataLocation(['en':KIREHE],KIREHE,kibuye,hc, '[30.65359,-2.26663]')
			def kayonza = newDataLocation(['en':KAYONZA],KAYONZA,kibuye,hc, '')
			[gitwe,kirehe,kayonza].each{it -> nyange.addToManages(it)}
			nyange.save(failOnError:true)
		}

       
	}
	
	public static createInventoryStructure(){
		
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
			def typeOne = newEquipmentType("15810", ["en":"Inclinometers","fr":"Inclinometers"],["en":"used in memms"],Observation.USEDINMEMMS,now(),25)
			def typeTwo = newEquipmentType("15819", ["en":"Infusion Pumps, Insulin"],["en":"used in memms"],Observation.USEDINMEMMS,now(),23)
			def typeThree = newEquipmentType("15966", ["en":"Cryotherapy Systems"],["en":"used in memms"],Observation.USEDINMEMMS,now(),12)
			def typeFour = newEquipmentType("10035", ["en":"Sponge Counters, Bar-Code Identification"],["en":"not used in memms"],Observation.RETIRED,now(),34)
			def typeFive = newEquipmentType("20760", ["en":"Pancreatic Drainage Tubes"],["en":"not used in memms"],Observation.RETIRED,now(),54)
			def typeSix = newEquipmentType("20729", ["en":"PCR Test Tubes"],["en":"not used in memms"],Observation.RETIRED,now(),67)
			def typeSeven = newEquipmentType("10026", ["en":"Adhesive Strips"],["en":"used in memms"],Observation.USEDINMEMMS,now(),12)
			def typeEight = newEquipmentType("10124", ["en":"AnesthInfusion Pumps, Insulinesia Kits"],["en":"used in memms"],Observation.USEDINMEMMS,now(),7)
			def typeNine = newEquipmentType("10155", ["en":"Anklets"],["en":"used in memms"],Observation.USEDINMEMMS,now(),60)
			def typeTen = newEquipmentType("10426", ["en":"Blood Donor Sets"],["en":"used in memms"],Observation.USEDINMEMMS,now(),34)

			def typeEleven = newEquipmentType("158101", ["en":"Accelerometers","fr":"Accelerometers"],["en":"used in memms"],Observation.USEDINMEMMS,now(),25)
			def typeTwelve = newEquipmentType("158191", ["en":"X-Ray Film Cutter"],["en":"used in memms"],Observation.USEDINMEMMS,now(),13)
			def typeThirteen = newEquipmentType("159661", ["en":"Video Systems"],["en":"used in memms"],Observation.USEDINMEMMS,now(),12)
			def typeFourteen = newEquipmentType("100351", ["en":"Adhesives, Aerosol"],["en":"not used in memms"],Observation.RETIRED,now(),34)
		}

		if(!PreventiveAction.count()){
			def actionOne = newPreventiveAction(EquipmentType.findByCode(100351),"action one")
			def actionTwo = newPreventiveAction(EquipmentType.findByCode(100351),"action two")
			def actionThree = newPreventiveAction(EquipmentType.findByCode(100351),"action three")
			def actionFour = newPreventiveAction(EquipmentType.findByCode(158101),"action four")
			def actionFive = newPreventiveAction(EquipmentType.findByCode(158101),"action five")
			def actionSix = newPreventiveAction(EquipmentType.findByCode(20729),"action six")
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
			def contactNine = newContact(['en':'Address Descriptions Nine'],"Service Provider One","jkl6@yahoo.com","0768-123-787","Street 156","8988")
			def contactTen = newContact(['en':'Address Descriptions Ten'],"Service Provider Two","jkl6@yahoo.com","0768-123-787","Street 156","8988")
			
			
			def manufactureOne = newProvider("ONE",Type.MANUFACTURER,contactOne)
			def manufactureTwo = newProvider("TWO",Type.MANUFACTURER,sontactTwo)
			def manufactureThree = newProvider("THREE",Type.MANUFACTURER,contactThree)
			def manufactureFour = newProvider("FOUR",Type.MANUFACTURER,contactFour)
			
			
			def supplierOne = newProvider("FIVE",Type.SUPPLIER,contactFive)
			def supplierTwo = newProvider("SIX",Type.SUPPLIER,contactSix)
			def supplierThree = newProvider("SEVEN",Type.SUPPLIER,contactSeven)
			
			def both = newProvider("EIGHT",Type.BOTH,contactEight)
			
			def serviceProOne = newProvider("Nine",Type.SERVICEPROVIDER,contactNine)
			def serviceProTwo = newProvider("Ten",Type.SERVICEPROVIDER,contactTen)
		}

		if(!NotificationEquipment.count()){
			newNotificationEquipment(User.findByUsername("titulaireHC"), User.findByUsername("techDH"), now(), "I have a new equipment in my health center, am not sure of it't type but has a serial number of 9084320oidbfd. Could you come register it please? thanks", false, User.findByUsername("titulaireHC").location, null)
			newNotificationEquipment(User.findByUsername("hospitalDepartment"), User.findByUsername("techDH"), now(), "I have a new equipment in my department, am not sure of it't type but has a serial number of 9084320oidbfd. Could you come register it please? thanks", false, User.findByUsername("hospitalDepartment").location, null)
		}
		
		if(!Equipment.count()){
			def equipmentOne = newEquipment("SERIAL01",PurchasedBy.BYDONOR,Donor.MOHPARTNER,"CHAI",false,newPeriod(24),"Room A1","",['en':'Equipment Descriptions'],
				getDate(22,07,2010),getDate(10,10,2010),"",
				'MODEL1',
				DataLocation.findByCode(NYANZA),
				Department.findByCode('SURGERY'),
				EquipmentType.findByCode("15810"),
				Provider.findByCode("ONE"),
				Provider.findByCode("FIVE"),
				Status.INSTOCK,
				User.findByUsername("admin"),
				null,
				null
				)
						
			def warrantyContactOne = newContact(['fr':'Warranty Address Descriptions One'],"Warranty","jk@yahoo.com","0768-888-787","Street 654","8988")
			def warrantyOne = newWarranty(warrantyContactOne,getDate(10, 12, 2010),false,[:])
			def statusOne= newEquipmentStatus(now(),User.findByUsername("admin"),Status.INSTOCK,equipmentOne,[:])
			
			equipmentOne.warranty = warrantyOne
			equipmentOne.warrantyPeriod = newPeriod(22)
			equipmentOne.save(failOnError:true)

			def equipmentTwo = newEquipment("SERIAL02",PurchasedBy.BYFACILITY,null,null,false,newPeriod(12),"Room A34","34900",['en':'Equipment Descriptions two'],
				getDate(12,01,2009),getDate(10,10,2009),"USD",
				'MODEL2',
				DataLocation.findByCode(NYANZA),
				Department.findByCode('PEDIATRY'),
				EquipmentType.findByCode("15819"),
				Provider.findByCode("TWO"),
				Provider.findByCode("FIVE"),
				Status.OPERATIONAL,
				User.findByUsername("admin"),
				null,
				null
				)
			
			def warrantyTwo = newWarranty(['en':'warranty one'],'warranty name1','email1@gmail.com',"0768-111-787","Street 154","898",getDate(10, 12, 2010),false,[:])
			def statusTwo= newEquipmentStatus(now(),User.findByUsername("admin"),Status.OPERATIONAL,equipmentTwo,[:])
			equipmentTwo.warranty=warrantyTwo
			equipmentTwo.warrantyPeriod = newPeriod(14)
			equipmentTwo.save(failOnError:true)
			
			def equipmentThree = newEquipment("SERIAL03",PurchasedBy.BYFACILITY,null,null,true,newPeriod(34),"Room A1","98700",['en':'Equipment Descriptions three'],
				getDate(14,8,2008),getDate(10,01,2009),"EUR",
				'MODEL3',
				DataLocation.findByCode(KIVUYE),
				Department.findByCode('EMERGENCY'),
				EquipmentType.findByCode("15966"),
				Provider.findByCode("TWO"),
				Provider.findByCode("FIVE"),
				Status.OPERATIONAL,
				User.findByUsername("admin"),
				null,
				null
				)
			
			def warrantyThree = newWarranty(['en':'warranty two'],'warranty name2','email2@gmail.com',"0768-222-787","Street 154","88",getDate(10, 12, 2010),false,[:])
			def statusThree= newEquipmentStatus(now(),User.findByUsername("admin"),Status.OPERATIONAL,equipmentThree,[:])
			equipmentThree.warranty=warrantyTwo
			equipmentThree.warrantyPeriod = newPeriod(12)
			equipmentThree.save(failOnError:true)
			
			def equipmentFour = newEquipment("SERIAL04",PurchasedBy.BYDONOR,Donor.MOHPARTNER,"Voxiva",false,newPeriod(12),"Room A1","",['en':'Equipment Descriptions four'],
				getDate(18,2,2011),getDate(10,10,2011),"",
				'MODEL2',
				DataLocation.findByCode(KIVUYE),
				Department.findByCode('CARDIOLOGY'),
				EquipmentType.findByCode("10035"),
				Provider.findByCode("THREE"),
				Provider.findByCode("SEVEN"),
				Status.UNDERMAINTENANCE,
				User.findByUsername("admin"),
				null,
				null
				)
			
			def warrantyFour = newWarranty(['en':'warranty two'],'warranty name2','email2@gmail.com',"0768-222-787","Street 154","888",getDate(10, 12, 2011),false,[:])
			def statusFour = newEquipmentStatus(now(),User.findByUsername("admin"),Status.OPERATIONAL,equipmentFour,[:])
			def statusFourOne = newEquipmentStatus(now(),User.findByUsername("admin"),Status.UNDERMAINTENANCE,equipmentFour,[:])
			equipmentFour.warranty=warrantyFour
			equipmentFour.warrantyPeriod = newPeriod(24)
			equipmentFour.save(failOnError:true)
			
			def equipmentFive = newEquipment("SERIAL05",PurchasedBy.BYDONOR,Donor.MOHPARTNER,"Voxiva",true,newPeriod(34),"Room A1","",['en':'Equipment Descriptions five'],
				getDate(11,8,2008),getDate(11,10,2009),"",
				'MODEL1',
				DataLocation.findByCode(BUNGWE),
				Department.findByCode('CONSULTATION'),
				EquipmentType.findByCode("20760"),
				Provider.findByCode("FOUR"),
				Provider.findByCode("SIX"),
				Status.UNDERMAINTENANCE,
				User.findByUsername("admin"),
				null,
				null
				)
			
			def warrantyFive = newWarranty(['en':'warranty Five'],'warranty name3','email3@gmail.com',"0768-333-787","Street 154","988",getDate(10, 12, 2010),false,[:])
			def statusFive= newEquipmentStatus(now(),User.findByUsername("admin"),Status.INSTOCK,equipmentFive,[:])
			def statusFiveOne = newEquipmentStatus(now(),User.findByUsername("admin"),Status.OPERATIONAL,equipmentFive,[:])
			def statusFiveTwo = newEquipmentStatus(now(),User.findByUsername("admin"),Status.UNDERMAINTENANCE,equipmentFive,[:])
			equipmentFive.warranty=warrantyFour
			equipmentFive.warrantyPeriod = newPeriod(8)
			equipmentFive.save(failOnError:true)
			
			def equipmentSix = newEquipment("SERIAL06",PurchasedBy.BYFACILITY,null,null,true,newPeriod(4),"Room A1","290540",['en':'Equipment Descriptions six'],
				getDate(1,7,2000),getDate(12,7,2001),"RWF",
				'MODEL3',
				DataLocation.findByCode(BUTARO),
				Department.findByCode('ANAESTHETICS'),
				EquipmentType.findByCode("20729"),
				Provider.findByCode("TWO"),
				Provider.findByCode("SIX"),
				Status.UNDERMAINTENANCE,
				User.findByUsername("admin"),
				null,
				null
				)
			
			def warrantySix = newWarranty(['en':'warranty six'],'warranty name6','email4@gmail.com',"0768-444-787","Street 154","8988",getDate(10, 12, 2010),false,[:])
			def statusSix= newEquipmentStatus(now(),User.findByUsername("admin"),Status.INSTOCK,equipmentSix,[:])
			def statusSixOne = newEquipmentStatus(now(),User.findByUsername("admin"),Status.OPERATIONAL,equipmentSix,[:])
			def statusSixTwo = newEquipmentStatus(now(),User.findByUsername("admin"),Status.UNDERMAINTENANCE,equipmentSix,[:])
			equipmentSix.warranty=warrantySix
			equipmentSix.warrantyPeriod = newPeriod(48)
			equipmentSix.save(failOnError:true)
			
			def equipmentSeven = newEquipment("SERIAL07",PurchasedBy.BYFACILITY,null,null,true,newPeriod(4),"Room A1","290540",['en':'Equipment Descriptions seven'],
				getDate(1,7,2000),getDate(12,7,2001),"USD",
				'MODEL3',
				DataLocation.findByCode(RUHINDO),
				Department.findByCode('ANAESTHETICS'),
				EquipmentType.findByCode("10026"),
				Provider.findByCode("TWO"),
				Provider.findByCode("SIX"),
				Status.UNDERMAINTENANCE,
				User.findByUsername("admin"),
				null,
				null
				)
			
			def warrantySeven = newWarranty(['en':'warranty seven'],'warranty name7','email7@gmail.com',"0768-777-787","Street 174","8988",getDate(1, 12, 2010),false,[:])
			def statusSeven= newEquipmentStatus(now(),User.findByUsername("admin"),Status.INSTOCK,equipmentSeven,[:])
			def statusSevenOne = newEquipmentStatus(now(),User.findByUsername("admin"),Status.OPERATIONAL,equipmentSeven,[:])
			def statusSevenTwo = newEquipmentStatus(now(),User.findByUsername("admin"),Status.UNDERMAINTENANCE,equipmentSeven,[:])
			equipmentSeven.warranty=warrantySeven
			equipmentSeven.warrantyPeriod = newPeriod(4)
			equipmentSeven.save(failOnError:true)
			
			def equipmentEight = newEquipment("SERIAL08",PurchasedBy.BYFACILITY,null,null,true,newPeriod(24),"Room A3","290540",['en':'Equipment Descriptions eight'],
				getDate(1,7,2000),getDate(12,7,2001),"EUR",
				'MODEL8',
				DataLocation.findByCode(RUHINDO),
				Department.findByCode('ANAESTHETICS'),
				EquipmentType.findByCode("10155"),
				Provider.findByCode("EIGHT"),
				Provider.findByCode("EIGHT"),
				Status.UNDERMAINTENANCE,
				User.findByUsername("admin"),
				null,
				null
				)
			
			def warrantyEight = newWarranty(['en':'warranty four'],'warranty name4','email4@gmail.com',"0768-444-787","Street 154","8988",getDate(10, 12, 2010),false,[:])
			def statusEight= newEquipmentStatus(now(),User.findByUsername("admin"),Status.INSTOCK,equipmentEight,[:])
			def statusEightOne = newEquipmentStatus(now(),User.findByUsername("admin"),Status.OPERATIONAL,equipmentEight,[:])
			def statusEightTwo = newEquipmentStatus(now(),User.findByUsername("admin"),Status.UNDERMAINTENANCE,equipmentEight,[:])
			equipmentEight.warranty=warrantyEight
			equipmentEight.warrantyPeriod = newPeriod(17)
			equipmentEight.save(failOnError:true)
			
			def equipmentNine = newEquipment("SERIAL09",PurchasedBy.BYFACILITY,null,null,true,newPeriod(4),"Room 9A1","290540",['en':'Equipment Descriptions Nine'],
				getDate(1,7,2000),getDate(12,7,2001),"RWF",
				'MODEL3',
				DataLocation.findByCode(NYANGE),
				Department.findByCode('ANAESTHETICS'),
				EquipmentType.findByCode("10124"),
				Provider.findByCode("TWO"),
				Provider.findByCode("SIX"),
				Status.UNDERMAINTENANCE,
				User.findByUsername("admin"),
				null,
				null
				)
			
			def warrantyNine = newWarranty(['en':'warranty Nine'],'warranty name9','email94@gmail.com',"0768-999-787","Street 954","8989",getDate(10, 12, 2010),false,[:])
			def statusNine= newEquipmentStatus(now(),User.findByUsername("admin"),Status.INSTOCK,equipmentNine,[:])
			def statusNineOne = newEquipmentStatus(now(),User.findByUsername("admin"),Status.OPERATIONAL,equipmentNine,[:])
			def statusNineTwo = newEquipmentStatus(now(),User.findByUsername("admin"),Status.UNDERMAINTENANCE,equipmentNine,[:])
			equipmentNine.warranty=warrantyNine
			equipmentNine.warrantyPeriod = newPeriod(9)
			equipmentNine.save(failOnError:true)
			
			def equipmentTen = newEquipment("SERIAL10",PurchasedBy.BYFACILITY,null,null,true,newPeriod(4),"Room 10A1","290540",['en':'Equipment Descriptions Ten'],
				getDate(1,7,2000),getDate(12,7,2001),"RWF",
				'MODELTen3',
				DataLocation.findByCode(KIREHE),
				Department.findByCode('ANAESTHETICS'),
				EquipmentType.findByCode("10426"),
				Provider.findByCode("TWO"),
				Provider.findByCode("SIX"),
				Status.UNDERMAINTENANCE,
				User.findByUsername("admin"),
				null,
				null
				)
			
			def warrantyTen = newWarranty(['en':'warranty Ten'],'warranty name10','email410@gmail.com',"0768-100-787","Street 154","8988",getDate(10, 12, 2010),false,[:])
			def statusTen= newEquipmentStatus(now(),User.findByUsername("admin"),Status.INSTOCK,equipmentTen,[:])
			def statusTenOne = newEquipmentStatus(now(),User.findByUsername("admin"),Status.OPERATIONAL,equipmentTen,[:])
			def statusTenTwo = newEquipmentStatus(now(),User.findByUsername("admin"),Status.UNDERMAINTENANCE,equipmentTen,[:])
			equipmentTen.warranty=warrantyTen
			equipmentTen.warrantyPeriod = newPeriod(28)
			equipmentTen.save(failOnError:true)
			
			def equipmentEleven = newEquipment("SERIAL11",PurchasedBy.BYFACILITY,null,null,true,newPeriod(7),"Room 10A1","200540",['en':'Equipment Descriptions Ten'],
				getDate(7,4,2013),getDate(12,4,2013),"RWF",
				'MODELTen3',
				DataLocation.findByCode(KIREHE),
				Department.findByCode('ANAESTHETICS'),
				EquipmentType.findByCode("10426"),
				Provider.findByCode("TWO"),
				Provider.findByCode("SIX"),
				Status.UNDERMAINTENANCE,
				User.findByUsername("admin"),
				null,
				null
				)
			
			def warrantyEleven = newWarranty(['en':'warranty eleven'],'warranty name11','email410@gmail.com',"0768-100-787","Street 154","8988",getDate(13, 5, 2013),false,[:])
			def statusEleven= newEquipmentStatus(now(),User.findByUsername("admin"),Status.INSTOCK,equipmentEleven,[:])
			def statusElevenOne = newEquipmentStatus(now(),User.findByUsername("admin"),Status.OPERATIONAL,equipmentEleven,[:])
			def statusElevenTwo = newEquipmentStatus(now(),User.findByUsername("admin"),Status.UNDERMAINTENANCE,equipmentEleven,[:])
			equipmentEleven.warranty=warrantyEleven
			equipmentEleven.warrantyPeriod = newPeriod(28)
			equipmentEleven.save(failOnError:true)
		
		}
	}
	
	public static createCorrectiveMaintenanceStructure(){

		if(!WorkOrder.list()){
			def admin = User.findByUsername("admin")
			def titulaireHC = User.findByUsername("titulaireHC")
			def department = User.findByUsername("hospitalDepartment")
			def techDH = User.findByUsername("techDH")
			def techMMC = User.findByUsername("techMMC")
			
			def equipment01 =Equipment.findBySerialNumber("SERIAL01")
			def equipment09 =Equipment.findBySerialNumber("SERIAL09")
			def equipment10 =Equipment.findBySerialNumber("SERIAL10")
			
			def workOrderOne =  newWorkOrder(equipment01,"First order",Criticality.NORMAL,department,now()-1,FailureReason.NOTSPECIFIED,OrderStatus.OPENATFOSA)
			
			def statusOne =  newWorkOrderStatus(workOrderOne,OrderStatus.OPENATFOSA,department,false)
			def statusTwo =  newWorkOrderStatus(workOrderOne,OrderStatus.OPENATMMC,techDH,true)
			def notifationOne = newWorkOrderNotification(workOrderOne, department, techDH, "notifationOne")
			def notifationTwo = newWorkOrderNotification(workOrderOne, techDH, department,"I am currentlly working on this, but needs further review. Am making this long to see how it fits when reading it.")
			def notifationThree = newWorkOrderNotification(workOrderOne, techDH, techMMC, "notifationThree")
			workOrderOne.save(failOnError:true)
			
			def workOrderTwo =  newWorkOrder(equipment01,"Second order",Criticality.LOW,admin,now(),FailureReason.NOTSPECIFIED,OrderStatus.OPENATFOSA)
			def statusThree =  newWorkOrderStatus(workOrderTwo,OrderStatus.OPENATFOSA,admin,false)
			
			def workOrderFive =  newWorkOrder(equipment01,"Closed order",Criticality.HIGH,department,now()-3,FailureReason.MISUSE,OrderStatus.OPENATFOSA)
			
			def statusFour =  newWorkOrderStatus(workOrderFive,OrderStatus.OPENATFOSA,department,false)
			def statusFive =  newWorkOrderStatus(workOrderFive,OrderStatus.OPENATMMC,department,true)		
			def statusSix =  newWorkOrderStatus(workOrderFive,OrderStatus.OPENATFOSA,department,false)
			def statusSeven =  newWorkOrderStatus(workOrderFive,OrderStatus.CLOSEDFIXED,department,false)
			
			def notifationFour = newWorkOrderNotification(workOrderOne, department, techDH,"Solve this for me")
			def notifationFive = newWorkOrderNotification(workOrderOne, techDH, department,"More information needed")
			workOrderFive.save(failOnError:true)
			
			equipment01.addToWorkOrders(workOrderOne)
			equipment01.addToWorkOrders(workOrderTwo)
			equipment01.addToWorkOrders(workOrderFive)
			
			equipment01.save(failOnError:true)
			
			def processOne = newCorrectiveProcess(workOrderOne,ProcessType.ACTION,"cleaning material", admin)
			def processTwo = newCorrectiveProcess(workOrderOne,ProcessType.ACTION,"open material", admin)
			def processThree = newCorrectiveProcess(workOrderOne,ProcessType.MATERIAL,"material piece one", admin)
			def processFour = newCorrectiveProcess(workOrderOne,ProcessType.MATERIAL,"material piece two", admin)
			
			workOrderOne.addToProcesses(processOne)
			workOrderOne.addToProcesses(processTwo)
			workOrderOne.addToProcesses(processThree)
			workOrderOne.addToProcesses(processFour)
			
			def commentOne = newComment(workOrderOne, admin, "comment one")
			def commentTwo = newComment(workOrderOne, admin,  "comment two")
			def commentThree = newComment(workOrderOne, admin,  "comment three")
			
			workOrderOne.addToComments(commentOne)
			workOrderOne.addToComments(commentTwo)
			workOrderOne.addToComments(commentThree)
			
			workOrderOne.save(failOnError:true)
			
			def workOrderThree =  newWorkOrder(equipment09,"Third order",Criticality.NORMAL,titulaireHC,now(),FailureReason.NOTSPECIFIED,OrderStatus.OPENATFOSA)
			def statusEight =  newWorkOrderStatus(workOrderThree,OrderStatus.OPENATFOSA,admin,false)
			equipment09.addToWorkOrders(workOrderThree)
			equipment09.save(failOnError:true)
			
			def workOrderFour =  newWorkOrder(equipment10,"Fourth order",Criticality.HIGH,admin,now(),FailureReason.NOTSPECIFIED,OrderStatus.OPENATFOSA)
			def statusNine =  newWorkOrderStatus(workOrderFour,OrderStatus.OPENATFOSA,admin,false)
			equipment10.addToWorkOrders(workOrderFour)
			equipment10.save(failOnError:true)	
		}
	}
	
	public static createPreventiveMaintenanceStructure(){
		//TODO the users and what they can
		if(!PreventiveOrder.count()){
			def admin = User.findByUsername("admin")
			def titulaireHC = User.findByUsername("titulaireHC") //hospitalDepartment can do the same job too
			def department = User.findByUsername("hospitalDepartment")
			def techDH = User.findByUsername("techDH")
			def techMMC = User.findByUsername("techMMC")
			
			def equipment01 = Equipment.findBySerialNumber("SERIAL01")
			def equipment09 = Equipment.findBySerialNumber("SERIAL09")
			def equipment10 = Equipment.findBySerialNumber("SERIAL10")
			
			def dOrderOne = newDurationBasedOrder(equipment01,admin,PreventiveOrderStatus.OPEN,PreventionResponsible.HCTITULAIRE,null,['en':'First Duration Order'],"First Duration Order",now()+1,null,OccurencyType.DAYS_OF_WEEK,1,[1,3,5])
			
			def dOrderTwo = newDurationBasedOrder(equipment09,admin,PreventiveOrderStatus.OPEN,PreventionResponsible.HCTECHNICIAN,techDH,['en':'Secod Duration Order'],"Second Duration Order",now()+1,null,OccurencyType.MONTHLY,2,[])
			
			def dOrderThree = newDurationBasedOrder(equipment10,admin,PreventiveOrderStatus.OPEN,PreventionResponsible.SERVICEPROVIDER,null,['en':'Three Duration Order'],"Second Duration Order",now()+1,null,OccurencyType.DAILY,2,[])
			
			def dOrderFour = newDurationBasedOrder(equipment09,admin,PreventiveOrderStatus.OPEN,PreventionResponsible.HCTITULAIRE,null,['en':'Four Duration Order'],"Second Duration Order",now()+1,null,OccurencyType.YEARLY,2,[])

			def dOrderFive = newWorkBasedOrder(equipment10,admin,PreventiveOrderStatus.OPEN,PreventionResponsible.HCTITULAIRE,null,['en':'First Work Based Five'],"First Work Based Order",now()+1,null,WorkIntervalType.HOURS,22)

			def dOrderSix = newWorkBasedOrder(equipment09,admin,PreventiveOrderStatus.OPEN,PreventionResponsible.HCTECHNICIAN,techDH,['en':'Second Work Based Six'],"Second Work Based Order",now()+1,null,WorkIntervalType.WEEK,2)

			def preventionOne = newPrevention(dOrderOne,admin,Utils.addHoursToDate(now(),1),now(),67,['en':'First Duration Order'], [])
			def preventionTwo = newPrevention(dOrderTwo,admin,Utils.addHoursToDate(now(),1),now(),30,['en':'First Duration Order'], [])
			//def processOne = newPreventionProcess(now(), "process one",admin, preventionOne)
			//def processTwo = newPreventionProcess(now(), "process two",admin, preventionTwo)
		}
		
	}

	public static createSparePartStructure(){
		
		def equipment01 =Equipment.findBySerialNumber("SERIAL01")
		def equipment09 =Equipment.findBySerialNumber("SERIAL09")
		def equipment10 =Equipment.findBySerialNumber("SERIAL10")
			
		if(!SparePartType.count()){

			//Add spare part types as defined in ecri
			def spPtTypOne = newSparePartType("4323", ['en':'Eleventh spare part type','fr':' Onzieme piece de rechange'],['en':'Eleventh spare part type description','fr':' description de la onzieme piece de rechange'],"4323-XYZ",Provider.findByCode("ONE"),now())
			def spPtTypTwo = newSparePartType("4324", ['en':'Twelfth spare part type','fr':' Douzieme piece de rechange'],['en':'Twelfth spare part type description','fr':' description de la douzieme piece de rechange'],"4324-ABC",Provider.findByCode("TWO"),now())
			def spPtTypThree = newSparePartType("4325", ['en':'Thirteenth spare part type','fr':' Tresieme piece de rechange'],['en':'Thirteenth spare part type description','fr':' description de la tresieme piece de rechange'],"4325-IJK",Provider.findByCode("THREE"),now())
   
			def sparePartTypeOne = newSparePartType("15810", ['en':'first spare part type','fr':'premier sp'], ['en':'first spare part type','fr':'premier type de piece de rechange'],"CODE Spare Part type 1",Provider.findByCode("ONE"),now())
			def sparePartTypeTwo = newSparePartType("15819", ['en':'Second spare part type','fr':'Second sp'], ['en':'first spare part type','fr':'premier type de piece de rechange'],"CODE Spare Part type 2",Provider.findByCode("TWO"),now())
			def sparePartTypeThree = newSparePartType("15966", ['en':'Third spare part type','fr':'Troisieme sp'], ['en':'first spare part type','fr':'premier type de piece de rechange'],"CODE Spare Part type 3",Provider.findByCode("THREE"),now())
			def sparePartTypeFour = newSparePartType("10035", ['en':'Forth spare part type','fr':'Quatrieme sp'], ['en':'first spare part type','fr':'premier type de piece de rechange'],"CODE Spare Part type 4",Provider.findByCode("FOUR"),now())
			def sparePartTypeFive = newSparePartType("20760", ['en':'Fifth spare part type','fr':'Cinquieme sp'], ['en':'first spare part type','fr':'premier type de piece de rechange'],"CODE Spare Part type 5",Provider.findByCode("ONE"),now())
			def sparePartTypeSix = newSparePartType("20729", ['en':'Sixth spare part type','fr':'Sixieme sp'], ['en':'first spare part type','fr':'premier type de piece de rechange'],"CODE Spare Part type 6",Provider.findByCode("ONE"),now())
			def sparePartTypeSeven = newSparePartType("10026", ['en':'Seventh spare part type','fr':'premier sp'], ['en':'Septieme spare part type','fr':'premier type de piece de rechange'],"CODE Spare Part type 7",Provider.findByCode("THREE"),now())
			def sparePartTypeEight = newSparePartType("10124", ['en':'Eighth spare part type','fr':'Huitieme sp'], ['en':'first spare part type','fr':'premier type de piece de rechange'],"CODE Spare Part type 8",Provider.findByCode("EIGHT"),now())
			def sparePartTypeNine = newSparePartType("10155", ['en':'Ninth spare part type','fr':'Neuvieme sp'], ['en':'first spare part type','fr':'premier type de piece de rechange'],"CODE Spare Part type 9",Provider.findByCode("THREE"),now())
			def sparePartTypeTen = newSparePartType("10426", ['en':'Tenth spare part type','fr':'Dixieme sp'], ['en':'first spare part type','fr':'premier type de piece de rechange'],"CODE Spare Part type 10",Provider.findByCode("FOUR"),now())
			
			
			def contactFive = newContact(['en':'Address Descriptions Five'],"Supplier Company 1","jkl4@yahoo.com","0768-657-787","Street 153","8988")
			def contactSix = newContact(['en':'Address Descriptions Six'],"Supplier Company 2","jkl5@yahoo.com","0768-342-787","Street 155","8988")
			def contactSeven = newContact(['en':'Address Descriptions Seven'],"Supplier Company 3","jkl6@yahoo.com","0768-123-787","Street 156","8988")
			def contactEight = newContact(['en':'Address Descriptions Eight'],"Manufacture and Supplier Ericson","jkl6@yahoo.com","0768-123-787","Street 156","8988")
			
			
			def supplierOne = newProvider("SUPPLIERONE",Type.SUPPLIER,contactFive)
			def supplierTwo = newProvider("SUPPLIERTWO",Type.SUPPLIER,contactSix)
			def supplierThree = newProvider("SUPPLIERTHREE",Type.SUPPLIER,contactSeven)
			def both = newProvider("SUPPLIERBOTH",Type.BOTH,contactEight)

		
			sparePartTypeEight.addToCompatibleEquipmentTypes(EquipmentType.findByCode(158101))
			sparePartTypeEight.addToCompatibleEquipmentTypes(EquipmentType.findByCode(100351))
			sparePartTypeEight.addToVendors(supplierThree)
			sparePartTypeEight.addToVendors(both)
			sparePartTypeEight.save(failOnError:true)

			sparePartTypeOne.addToCompatibleEquipmentTypes(EquipmentType.findByCode(158101))
			sparePartTypeOne.addToCompatibleEquipmentTypes(EquipmentType.findByCode(158191))
			sparePartTypeOne.addToVendors(supplierOne)
			sparePartTypeOne.addToVendors(supplierTwo)
			sparePartTypeOne.save(failOnError:true)

			sparePartTypeTwo.addToCompatibleEquipmentTypes(EquipmentType.findByCode(158101))
			sparePartTypeTwo.addToCompatibleEquipmentTypes(EquipmentType.findByCode(158191))
			sparePartTypeTwo.save(failOnError:true)
			

			sparePartTypeThree.addToCompatibleEquipmentTypes(EquipmentType.findByCode(100351))
			sparePartTypeThree.addToCompatibleEquipmentTypes(EquipmentType.findByCode(158101))
			sparePartTypeThree.save(failOnError:true)

			sparePartTypeSeven.addToVendors(supplierThree)
			sparePartTypeSeven.addToVendors(both)
			sparePartTypeSeven.save(failOnError:true)
			
		}
		
		if(!SparePart.count()){
			def sparePartOne = newSparePart(
				SparePartPurchasedBy.BYMOH,
				['en':'Spare Part Descriptions one'],
				getDate(22,07,2010),
				"",
				"",
				null,
				SparePartType.findByCode("15810"),
				Provider.findByCode("FIVE"),
				User.findByUsername("admin"),
				StockLocation.MMC,
				SparePartStatus.PENDINGORDER,
				9,0
				)

			def sparePartOneT = newSparePart(
				SparePartPurchasedBy.BYMOH,
				['en':'Spare Part Descriptions two'],
				getDate(22,07,2010),
				"",
				"",
				null,
				SparePartType.findByCode("15810"),
				Provider.findByCode("FIVE"),
				User.findByUsername("admin"),
				StockLocation.MMC,
				SparePartStatus.INSTOCK,
				89,80
				)

			def sparePartTwo = newSparePart(
				SparePartPurchasedBy.BYFACILITY,
				['en':'Spare Part Descriptions three'],
				getDate(12,01,2009),
				"3333",
				"USD",
				DataLocation.findByCode(NYANZA),
				SparePartType.findByCode("15819"),
				Provider.findByCode("FIVE"),
				User.findByUsername("admin"),
				StockLocation.FACILITY,
				SparePartStatus.PENDINGORDER,
				8,0
				)
			
			def sparePartThree = newSparePart(
				SparePartPurchasedBy.BYFACILITY,
				['en':'Spare Part Descriptions four'],
				getDate(14,8,2008),
				"98700",
				"EUR",
				DataLocation.findByCode(KIVUYE),
				SparePartType.findByCode("15966"),
				Provider.findByCode("FIVE"),
				User.findByUsername("admin"),
				StockLocation.FACILITY,
				SparePartStatus.INSTOCK,
				92,70
				)
			
			def sparePartFour = newSparePart(
				SparePartPurchasedBy.BYMOH,
				['en':'Spare Part Descriptions five'],
				getDate(18,2,2011),
				"",
				"",
				null,
				SparePartType.findByCode("10035"),
				Provider.findByCode("SEVEN"),
				User.findByUsername("admin"),
				StockLocation.MMC,
				SparePartStatus.INSTOCK,
				100,1
				)	
			
			def sparePartFive = newSparePart(
				SparePartPurchasedBy.BYMOH,
				['en':'Spare Part Descriptions six'],
				getDate(11,8,2008),
				"",
				"",
				null,
				SparePartType.findByCode("20760"),
				Provider.findByCode("SIX"),
				User.findByUsername("admin"),
				StockLocation.MMC,
				SparePartStatus.INSTOCK,
				999,882
				)
			def sparePartSix = newSparePart(
				SparePartPurchasedBy.BYMOH,
				['en':'Spare Part Descriptions seven'],
				getDate(11,8,2008),
				"",
				"",
				null,
				SparePartType.findByCode("20760"),
				Provider.findByCode("SIX"),
				User.findByUsername("admin"),
				StockLocation.MMC,
				SparePartStatus.INSTOCK,
				343,0
				)
			def sparePartSeve = newSparePart(
				SparePartPurchasedBy.BYMOH,
				['en':'Spare Part Descriptions eight'],
				getDate(11,8,2008),
				"",
				"",
				null,
				SparePartType.findByCode("20760"),
				Provider.findByCode("SIX"),
				User.findByUsername("admin"),
				StockLocation.MMC,
				SparePartStatus.PENDINGORDER,
				234,0
				)
			def sparePartEight = newSparePart(
				SparePartPurchasedBy.BYFACILITY,
				['en':'Spare Part Descriptions Eight'],
				getDate(14,8,2008),
				"98700",
				"EUR",
				DataLocation.findByCode(KIVUYE),
				SparePartType.findByCode("15966"),
				Provider.findByCode("FIVE"),
				User.findByUsername("admin"),
				StockLocation.FACILITY,
				SparePartStatus.INSTOCK,
				122,87
				)
		}
	}
	
	//Models definition
	//Spare Part type
	public static newSparePartType(def code, def names, def descriptions,def partNumber,def manufacturer, def discontinuedDate){
		def type = new SparePartType(code:code,partNumber:partNumber,manufacturer:manufacturer,discontinuedDate:discontinuedDate)
		Utils.setLocaleValueInMap(type,names,"Names")
		Utils.setLocaleValueInMap(type,descriptions,"Descriptions")
		return type.save(failOnError: false, flush:true)
	}

	// Spare part
	public static newSparePart(def sparePartPurchasedBy,def descriptions,def purchaseDate,def purchaseCost,def currency,def dataLocation,def type,def supplier,def addedBy,def stockLocation,def status,def initialQuantity,def inStockQuantity){
		def sparePart = new SparePart(
			sparePartPurchasedBy:sparePartPurchasedBy,
			purchaseCost:purchaseCost,
			purchaseDate:purchaseDate,
			currency:currency,
			dataLocation:dataLocation,
			type:type,
			supplier:supplier,
			addedBy:addedBy,
			stockLocation:stockLocation,
			status:status,
			initialQuantity:initialQuantity,
			inStockQuantity:inStockQuantity
			);
		Utils.setLocaleValueInMap(sparePart,descriptions,"Descriptions")
		return sparePart.save(failOnError: true,flush:true)
	}

	//Preventive Maintenance
	public static newDurationBasedOrder(def equipment,def addedBy,def status,def preventionResponsible,def technicianInCharge,def names,def description,def firstOccurenceOn,def closedOn,def occurency,def occurInterval,def occurDaysOfWeek){
		def timeDate =  newTimeDate(firstOccurenceOn)
		def order  = new DurationBasedOrder(
			equipment: equipment,
			addedBy: addedBy,
			type: PreventiveOrderType.DURATIONBASED,
			status: status,
			description: description,
			preventionResponsible: preventionResponsible,
			technicianInCharge: technicianInCharge,
			firstOccurenceOn: timeDate,
			closedOn: closedOn,
			occurency: occurency,
			occurInterval: occurInterval,
			occurDaysOfWeek: occurDaysOfWeek
			)
		Utils.setLocaleValueInMap(order,names,"Names") 
		equipment.addToPreventiveOrders(order)
		equipment.save(failOnError:true)
		return order.save(failOnError: true)
	}
	public static newWorkBasedOrder(def equipment,def addedBy,def status,def preventionResponsible,def technicianInCharge,def names,def description,def firstOccurenceOn,def closedOn,def occurency,def occurInterval){
		def timeDate =  newTimeDate(firstOccurenceOn)
		def order  = new WorkBasedOrder(
			equipment: equipment,
			addedBy: addedBy,
			type: PreventiveOrderType.WORKBASED,
			status: status,
			description: description,
			preventionResponsible: preventionResponsible,
			technicianInCharge: technicianInCharge,
			occurInterval: occurInterval,
			occurency:occurency,
			firstOccurenceOn: timeDate,
			closedOn: closedOn
			)
		Utils.setLocaleValueInMap(order,names,"Names") 
		
		equipment.addToPreventiveOrders(order)
		equipment.save(failOnError:true)
		return order.save(failOnError: true)
	}
	//Prevention
	public static newPrevention(def order,def addedBy,def scheduledOn,def eventDate,def timeSpend,def descriptions, def processes){
		def timeD =  newTimeDate(scheduledOn)
		def timeS = newTimeSpend(timeSpend)
		def prevention = new Prevention(addedBy:addedBy,scheduledOn:timeD,eventDate:eventDate,timeSpend:timeS,processes:processes)
		Utils.setLocaleValueInMap(prevention,descriptions,"Descriptions")
		order.addToPreventions(prevention)
		return order.save(failOnError:true)
		//return prevention.save(failOnError:true)
	}
	//PreventiveProcess
	public static newPreventionProcess(def dateCreated, def name,def addedBy, def prevention){
		def process = new PreventiveProcess(dateCreated:dateCreated,name:name,addedBy:addedBy)
		prevention.addToProcesses(process)
		return prevention.save(failOnError:true)
		//return process.save(failOnError:true)
	}
	public static newTimeDate(def date,def time){
		return new TimeDate(date,time)
	}
	public static newTimeDate(def timeDate){
		return new TimeDate(timeDate:timeDate)
	}
	public static newTimeSpend(def hours,def minutes){
		return new TimeSpend(hours,minutes)
	}
	public static newTimeSpend(def numberOfMinutes){
		return new TimeSpend(numberOfMinutes:numberOfMinutes)
	}
	//Corrective Maintenance
	public static newWorkOrder(def equipment, def description, def criticality, def addedBy, def openOn,def failureReason,def currentStatus){
		return new WorkOrder(equipment:equipment,description: description,criticality:criticality,addedBy:addedBy,openOn: openOn,currentStatus:currentStatus,failureReason:failureReason).save(failOnError:true)
	}
	public static newWorkOrder(def equipment, def description, def criticality,def addedBy, def openOn, def closedOn, def failureReason,def currentStatus){
		return new WorkOrder(equipment:equipment, description:description, criticality:criticality,addedBy:addedBy, openOn: openOn, closedOn:closedOn, currentStatus:currentStatus,failureReason:failureReason).save(failOnError:true)
	}
	public static newWorkOrderNotification(def workOrder, def sender, def receiver, def content){
		return new NotificationWorkOrder(workOrder: workOrder, sender: sender, receiver: receiver, content: content).save(failOnError: true)
	}
	
	public static newNewEquipmentNotification(def dataLocation, def department, def sender, def receiver,def content){
		return new NotificationEquipment(dataLocation:dataLocation, department:department, sender: sender, receiver: receiver,content: content).save(failOnError: true)
	}
	
	public static newComment(def workOrder, def writtenBy, def content){
		return new Comment(workOrder: workOrder, writtenBy: writtenBy, content: content ).save(failOnError: true)
	}
	public static newCorrectiveProcess(def workOrder,def type, def name,def addedBy){
		return new CorrectiveProcess(workOrder: workOrder,type: type,name: name,addedBy: addedBy ).save(failOnError: true)
	}
	public static newNotificationEquipment(def sender, def receiver, def writtenOn, def content, def read, def dataLocation, def department){
		return new NotificationEquipment(sender:sender, receiver:receiver, writtenOn:writtenOn, content:content, read:read, dataLocation:dataLocation, department:department).save(failOnError: true)
	}
	
	public static newWorkOrderStatus(def workOrder,def status,def changedBy,def escalation){
		def equipment = workOrder.equipment
		def stat = new WorkOrderStatus(workOrder:workOrder,previousStatus:workOrder?.getTimeBasedStatus()?.status, status:status,changedBy:changedBy,escalation:escalation)
		
		//Create First Work Order
		if(status == OrderStatus.OPENATFOSA){
			if(!equipment.currentStatus.equals(Status.UNDERMAINTENANCE)){			
				newEquipmentStatus(now(), changedBy, Status.UNDERMAINTENANCE, equipment, [:])
			}
		}
		//Change Equipment Status When closing workorder
		if(status == OrderStatus.CLOSEDFIXED){
			 workOrder.closedOn = now()
			 newEquipmentStatus(now(), changedBy, Status.OPERATIONAL, equipment, [:])
		}
		if(status == OrderStatus.CLOSEDFORDISPOSAL){
			workOrder.closedOn = now()
			newEquipmentStatus(now(), changedBy, Status.FORDISPOSAL, equipment, [:])
		}		
		workOrder.currentStatus= status
		workOrder.addToStatus(stat)
		workOrder.save(failOnError:true,flush:true)
		return stat;
	}
	
	//Inventory
	public static newEquipment(def serialNumber,def purchaser,def donor,def donorName,def obsolete,def expectedLifeTime,def room,def purchaseCost,def descriptions,def manufactureDate, def purchaseDate,def currency,def model,def dataLocation,def department, def type,def manufacture,def supplier,def currentStatus,def addedBy,def lastModifiedBy,def lastModifiedOn){
		def equipment = new Equipment(
			serialNumber:serialNumber,
			purchaser:purchaser,
			donor:donor,
			donorName:donorName,
			obsolete:obsolete,
			room:room,
			purchaseCost:purchaseCost,
			currency:currency,
			manufactureDate:manufactureDate,
			purchaseDate:purchaseDate,
			model:model,
			dataLocation:dataLocation,
			expectedLifeTime:expectedLifeTime,
			department:department,
			type:type,
			manufacturer:manufacture,
			supplier:supplier,
			currentStatus:currentStatus,
			addedBy:addedBy,
			lastModifiedBy:lastModifiedBy
			);
		Utils.setLocaleValueInMap(equipment,descriptions,"Descriptions")
		return equipment.save(failOnError: true,flush:true)
	}

	public static newEquipmentStatus(def dateOfEvent,def changedBy,def value, def equipment,def reasons){
		def status = new EquipmentStatus(dateOfEvent:dateOfEvent,changedBy:changedBy,previousStatus:equipment?.getTimeBasedStatus()?.status,status:value)
		Utils.setLocaleValueInMap(status,reasons,"Reasons")
		equipment.addToStatus(status) 
		equipment.save(failOnError:true,flush:true)
		equipment.currentStatus = value
		equipment.lastModifiedBy = changedBy
		equipment.save(failOnError:true,flush:true)
		return status
	}

	public static newContact(def addressDescriptions,def contactName,def email, def phone, def street, def poBox){
		def contact = new Contact(contactName:contactName,email:email,phone:phone,street:street,poBox:poBox)
		Utils.setLocaleValueInMap(contact,addressDescriptions,"AddressDescriptions")
		return contact;
	}
	
	public static newProvider(def code, def type, def contact){
		return new Provider(code:code,type:type,contact:contact).save(failOnError: true)
	}
	
	public static newProvider(def code, def type, def addressDescriptions, def contactName,def email, def phone, def street, def poBox){
		def contact = newContact(addressDescriptions,contactName,email,phone,street,poBox)
		return newProvider(code,type,contact)
	}
	
	public static newWarranty(def contact, def startDate,def sameAsSupplier,def descriptions){
		def warranty = new Warranty(contact:contact,startDate:startDate,sameAsSupplier:sameAsSupplier)
		Utils.setLocaleValueInMap(warranty,descriptions,"Descriptions")
		return warranty
	}
	
	public static newWarranty(def addressDescriptions,def contactName,def email,def phone,def street,def poBox,def startDate,def sameAsSupplier,def descriptions){
		def contact = newContact(addressDescriptions,contactName,email,phone,street,poBox)
		return newWarranty(contact, startDate,sameAsSupplier,descriptions)
	}
	//@Deprecated have to remove the lastModifiedOn params	
	public static newEquipmentType(def code, def names,def descriptions, def observation, def lastModifiedOn,def expectedLifeTime = 12){
		def type = new EquipmentType(code:code,observation:observation,expectedLifeTime:newPeriod(expectedLifeTime))
		Utils.setLocaleValueInMap(type,names,"Names")
		Utils.setLocaleValueInMap(type,descriptions,"Descriptions")
		return type.save(failOnError: true)
	}

	public static newPreventiveAction(def type, def description){
		def action  = new PreventiveAction(description:description)
		type.addToPreventiveActions(action)
		return type.save(failOnError: true)
	}

	public static newDepartment(def names,def code, def descriptions){
		def department = new Department(code:code)
		Utils.setLocaleValueInMap(department,names,"Names") 
		Utils.setLocaleValueInMap(department,descriptions,"Descriptions")
		return department.save(failOnError: true)
	}
	
	public static newPeriod(def numberOfMonths){
		return new Period(numberOfMonths:numberOfMonths)
	}
	
	public static newPeriod(def years,def months){
		return new Period(years,months)
	}
	
	//Location
	public static newDataLocationType(def names, def code) {
		def dataLocationType = new DataLocationType(code: code, defaultSelected: true)
		Utils.setLocaleValueInMap(dataLocationType,names,"Names")
		return dataLocationType.save(failOnError: true)
	}
	
	public static newLocationLevel(def names, def code, def order) {
		def locationLevel = new LocationLevel(code: code, order:order)
		Utils.setLocaleValueInMap(locationLevel,names,"Names")
		return locationLevel.save(failOnError: true)
	}
	
	public static newLocation(def names, def code, def parent, def level, def coordinates) {
		def location = new Location(code: code, parent: parent, level: level, coordinates: coordinates)
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
	
	public static newDataLocation(def names, def code, def location, def type, def coordinates) {
		def dataLocation = new DataLocation(code: code, location: location, type: type, coordinates: coordinates)
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
   public static now(){
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
