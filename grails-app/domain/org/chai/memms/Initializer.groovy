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
import org.chai.memms.equipment.EquipmentModel;
import org.chai.memms.equipment.EquipmentStatus;
import org.chai.memms.equipment.EquipmentType
import org.chai.memms.equipment.EquipmentType.Observation;
import org.chai.memms.equipment.Provider
import org.chai.memms.equipment.EquipmentStatus.Status;
import org.chai.memms.equipment.Provider.Type;
import org.chai.memms.location.CalculationLocation;
import org.chai.memms.location.DataLocation;
import org.chai.memms.location.DataLocationType;
import org.chai.memms.location.Location;
import org.chai.memms.location.LocationLevel;
import org.chai.memms.security.Role
import org.chai.memms.security.User
import org.chai.memms.security.UserType
//import org.codehaus.groovy.grails.commons.ConfigurationHolder as CONF

public class Initializer {
		
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
	static final String WEST = "West"
	static final String EAST = "East"
	static final String BURERA = "Burera"
	static final String BURERA1 = "Burera1"
	static final String BURERA2 = "Burera2"
	static final String BUTARO = "Butaro DH"
	static final String KIVUYE = "Kivuye HC"
	static final String BUNGWE = "Bungwe HC"
	
	static def createUsers() {
		if(!User.count()){
			def adminRole = new Role(name: "Admin")
			adminRole.addToPermissions("*:*")
			adminRole.save(failOnError: true, flush:true)
			
			def dataClerkRole = new Role(name: "Clerk")
			dataClerkRole.addToPermissions("equipment:*")
			dataClerkRole.addToPermissions("home:*")
			dataClerkRole.save(failOnError: true, flush:true)

			def userAdmin = new User(userType: UserType.ADMIN,code:"admin", location: CalculationLocation.findByCode(RWANDA), username: "admin", 
				firstname: "memms", lastname: "memms", email:'memms@memms.org', passwordHash: new Sha256Hash("admin").toHex(), active: true, 
				confirmed: true, uuid:'admin', defaultLanguage:'en', phoneNumber: '+250 11 111 11 11', organisation:'org')

			userAdmin.addToRoles(adminRole)
			userAdmin.save(failOnError: true, flush:true)
			
			def userClerk= new User(userType: UserType.OTHER,code:"user", location: CalculationLocation.findByCode(KIVUYE), username: "user", 
				firstname: "user", lastname: "user", email:'user@memms.org', passwordHash: new Sha256Hash("user").toHex(), active: true, 
				confirmed: true, uuid:'user', defaultLanguage:'en', phoneNumber: '+250 11 111 11 11', organisation:'org')
			userClerk.addToRoles(dataClerkRole)
			userClerk.addToPermissions("equipment:*")
			userClerk.addToPermissions("home:*")
			userClerk.save(failOnError: true, flush:true)
			
			def userClerk1= new User(userType: UserType.OTHER,code:"user1", location: CalculationLocation.findByCode(BURERA), username: "user1",
				firstname: "user", lastname: "user", email:'user1@memms.org', passwordHash: new Sha256Hash("user1").toHex(), active: true,
				confirmed: true, uuid:'user1', defaultLanguage:'en', phoneNumber: '+250 11 111 11 11', organisation:'org')
			userClerk1.addToRoles(dataClerkRole)
			userClerk1.addToPermissions("equipment:index")
			userClerk1.addToPermissions("equipment:summaryPage")
			userClerk1.addToPermissions("equipment:list")
			userClerk1.addToPermissions("home:*")
			userClerk1.save(failOnError: true, flush:true)
		}
	}
	
	static def createDummyStructure() {
		
		if (!Location.count()) {
			//Add Location types
			def hc = newDataLocationType(['en':HEALTH_CENTER_GROUP], HEALTH_CENTER_GROUP);
			def dh = newDataLocationType(['en':DISTRICT_HOSPITAL_GROUP], DISTRICT_HOSPITAL_GROUP);
			//Add Location Level
			def country = newLocationLevel(['en':NATIONAL], NATIONAL)
			def province = newLocationLevel(['en':PROVINCE], PROVINCE)
			def district = newLocationLevel(['en':DISTRICT], DISTRICT)
			def sector = newLocationLevel(['en':SECTOR], SECTOR)
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
			
			//Add DataLocation
			def butaro = newDataLocation(['en':BUTARO],BUTARO,burera,dh)
			def kivuye = newDataLocation(['en':KIVUYE],KIVUYE,burera2,hc)
			def bungwe = newDataLocation(['en':BUNGWE],BUNGWE,burera1,hc)
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
		
		if(!EquipmentModel.count()){
			//Add Equipment Model
			def modelOne = newEquipmentModel(['en':'Model One'],'MODEL1',['en':'Model One'])
			def modelTwo= newEquipmentModel(['en':'Model Two'],'MODEL2',['en':'Model Two'])
			def modelThree = newEquipmentModel(['en':'MODEL3'],'MODEL3',['en':'Model Three'])
		}
		
		if(!EquipmentType.count()){
			//Add equipment types as defined in ecri
			def typeOne = newEquipmentType("15810", ["en":"Accelerometers","fr":"Accelerometers"],["en":"used in memms"],Observation.USEDINMEMMS,now(),now())
			def typeTwo = newEquipmentType("15819", ["en":"X-Ray Film Cutter"],["en":"used in memms"],Observation.USEDINMEMMS,now(),now())
			def typeThree = newEquipmentType("15966", ["en":"Video Systems"],["en":"used in memms"],Observation.USEDINMEMMS,now(),now())
			def typeFour = newEquipmentType("10035", ["en":"Adhesives, Aerosol"],["en":"not used in memms"],Observation.RETIRED,now(),now())
			def typeFive = newEquipmentType("20760", ["en":"Pancreatic Drainage Tubes"],["en":"not used in memms"],Observation.RETIRED,now(),now())
			def typeSix = newEquipmentType("20729", ["en":"PCR Test Tubes"],["en":"not used in memms"],Observation.RETIRED,now(),now())
			def typeSeven = newEquipmentType("10026", ["en":"Adhesive Strips"],["en":"used in memms"],Observation.USEDINMEMMS,now(),now())
			def typeEight = newEquipmentType("10124", ["en":"Anesthesia Kits"],["en":"used in memms"],Observation.USEDINMEMMS,now(),now())
			def typeNine = newEquipmentType("10155", ["en":"Anklets"],["en":"used in memms"],Observation.USEDINMEMMS,now(),now())
			def typeTen = newEquipmentType("10426", ["en":"Blood Donor Sets"],["en":"used in memms"],Observation.USEDINMEMMS,now(),now())
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
			
			
			def manufactureOne = newProvider("ONE",Type.MANUFACTURE,contactOne)
			def manufactureTwo = newProvider("TWO",Type.MANUFACTURE,sontactTwo)
			def manufactureThree = newProvider("THREE",Type.MANUFACTURE,contactThree)
			def manufactureFour = newProvider("FOUR",Type.MANUFACTURE,contactFour)
			
			
			def supplierOne = newProvider("FIVE",Type.SUPPLIER,contactFive)
			def supplierTwo = newProvider("SIX",Type.SUPPLIER,contactSix)
			def supplierThree = newProvider("SEVEN",Type.SUPPLIER,contactSeven)
			
			def both = newProvider("EIGHT",Type.BOTH,contactEight)
		}
		
		
		if(!Equipment.count()){
			def equipmentOne = newEquipment("SERIAL01",true,false,24,"Room A1","2900.23",['en':'Equipment Descriptions'],
				getDate(22,07,2010),getDate(10,10,2010),now(),
				'MODEL1',
				DataLocation.findByCode(BUTARO),
				Department.findByCode('SURGERY'),
				EquipmentType.findByCode("15819"),
				Provider.findByCode("ONE"),
				Provider.findByCode("FIVE")
				)
						
			def warrantyContactOne = newContact(['fr':'Warranty Address Descriptions One'],"Warranty","jk@yahoo.com","0768-888-787","Street 654","8988")
			def warrantyOne = newWarranty(warrantyContactOne,getDate(10, 12, 2010),getDate(12, 12, 2012),[:])
			def statusOne= newEquipmentStatus(now(),User.findByUsername("admin"),Status.INSTOCK,equipmentOne,true)
			
			equipmentOne.warranty=warrantyOne
			equipmentOne.addToStatus(statusOne)
			equipmentOne.save(failOnError:true,flush: true)

			def equipmentTwo = newEquipment("SERIAL11",true,false,12,"Room A34","34900.23",['en':'Equipment Descriptions two'],
				getDate(12,01,2009),getDate(10,10,2009),now(),
				'MODEL2',
				DataLocation.findByCode(KIVUYE),
				Department.findByCode('PEDIATRY'),
				EquipmentType.findByCode("15810"),
				Provider.findByCode("TWO"),
				Provider.findByCode("FIVE")
				)
			
			def warrantyTwo = newWarranty(['en':'warranty one'],'warranty name1','email1@gmail.com',"0768-111-787","Street 154","898",getDate(10, 12, 2010),getDate(12, 12, 2012),[:])
			def statusTwo= newEquipmentStatus(now(),User.findByUsername("admin"),Status.OPERATIONAL,equipmentTwo,true)
			equipmentTwo.warranty=warrantyTwo
			equipmentTwo.addToStatus(statusTwo)
			equipmentTwo.save(failOnError:true,flush: true)
			
			def equipmentThree = newEquipment("SERIAL12",false,true,34,"Room A1","98700.23",['en':'Equipment Descriptions three'],
				getDate(14,8,2008),getDate(10,01,2009),now(),
				'MODEL3',
				DataLocation.findByCode(BUNGWE),
				Department.findByCode('EMERGENCY'),
				EquipmentType.findByCode("15966"),
				Provider.findByCode("TWO"),
				Provider.findByCode("FIVE")
				)
			
			def warrantyThree = newWarranty(['en':'warranty two'],'warranty name2','email2@gmail.com',"0768-222-787","Street 154","88",getDate(10, 12, 2010),getDate(12, 12, 2012),[:])
			def statusThree= newEquipmentStatus(now(),User.findByUsername("admin"),Status.OPERATIONAL,equipmentThree,true)
			equipmentThree.warranty=warrantyTwo
			equipmentThree.addToStatus(statusThree)
			equipmentThree.save(failOnError:true)
			
			def equipmentFour = newEquipment("SERIAL13",true,false,12,"Room A1","78900.23",['en':'Equipment Descriptions four'],
				getDate(18,2,2011),getDate(10,10,2011),now(),
				'MODEL2',
				DataLocation.findByCode(KIVUYE),
				Department.findByCode('CARDIOLOGY'),
				EquipmentType.findByCode("15819"),
				Provider.findByCode("THREE"),
				Provider.findByCode("SEVEN")
				)
			
			def warrantyFour = newWarranty(['en':'warranty two'],'warranty name2','email2@gmail.com',"0768-222-787","Street 154","888",getDate(10, 12, 2010),getDate(12, 12, 2012),[:])
			def statusFour = newEquipmentStatus(now(),User.findByUsername("admin"),Status.OPERATIONAL,equipmentFour,false)
			def statusFourOne = newEquipmentStatus(now(),User.findByUsername("admin"),Status.UNDERMAINTENANCE,equipmentFour,true)
			equipmentFour.warranty=warrantyFour
			equipmentFour.addToStatus(statusFour)
			equipmentFour.addToStatus(statusFourOne)
			equipmentFour.save(failOnError:true,flush: true)
			
			def equipmentFive = newEquipment("SERIAL14",true,true,34,"Room A1","28723",['en':'Equipment Descriptions five'],
				getDate(11,8,2008),getDate(11,10,2009),now(),
				'MODEL1',
				DataLocation.findByCode(BUNGWE),
				Department.findByCode('CONSULTATION'),
				EquipmentType.findByCode("10026"),
				Provider.findByCode("FOUR"),
				Provider.findByCode("SIX")
				)
			
			def warrantyFive = newWarranty(['en':'warranty Five'],'warranty name3','email3@gmail.com',"0768-333-787","Street 154","988",getDate(10, 12, 2010),getDate(12, 12, 2012),[:])
			def statusFive= newEquipmentStatus(now(),User.findByUsername("admin"),Status.INSTOCK,equipmentFive,false)
			def statusFiveOne = newEquipmentStatus(now(),User.findByUsername("admin"),Status.OPERATIONAL,equipmentFive,false)
			def statusFiveTwo = newEquipmentStatus(now(),User.findByUsername("admin"),Status.UNDERMAINTENANCE,equipmentFive,true)
			equipmentFive.warranty=warrantyFour
			equipmentFive.addToStatus(statusFive)
			equipmentFive.addToStatus(statusFiveOne)
			equipmentFive.addToStatus(statusFiveTwo)
			equipmentFive.save(failOnError:true)
			
			def equipmentSix = newEquipment("SERIAL15",false,true,4,"Room A1","290540.23",['en':'Equipment Descriptions six'],
				getDate(1,7,2000),getDate(12,7,2001),now(),
				'MODEL3',
				DataLocation.findByCode(BUTARO),
				Department.findByCode('ANAESTHETICS'),
				EquipmentType.findByCode("15819"),
				Provider.findByCode("TWO"),
				Provider.findByCode("SIX")
				)
			
			def warrantySix = newWarranty(['en':'warranty four'],'warranty name4','email4@gmail.com',"0768-444-787","Street 154","8988",getDate(10, 12, 2010),getDate(12, 12, 2012),[:])
			def statusSix= newEquipmentStatus(now(),User.findByUsername("admin"),Status.INSTOCK,equipmentSix,false)
			def statusSixOne = newEquipmentStatus(now(),User.findByUsername("admin"),Status.OPERATIONAL,equipmentSix,false)
			def statusSixTwo = newEquipmentStatus(now(),User.findByUsername("admin"),Status.UNDERMAINTENANCE,equipmentSix,true)
			equipmentSix.warranty=warrantySix
			equipmentSix.addToStatus(statusSix)
			equipmentSix.addToStatus(statusSixOne)
			equipmentSix.addToStatus(statusSixTwo)
			equipmentSix.save(failOnError:true,flush: true)
			
			def equipmentSeven = newEquipment("SERIAL07",false,true,4,"Room A1","290540.23",['en':'Equipment Descriptions seven'],
				getDate(1,7,2000),getDate(12,7,2001),now(),
				'MODEL3',
				DataLocation.findByCode(BUTARO),
				Department.findByCode('ANAESTHETICS'),
				EquipmentType.findByCode("15819"),
				Provider.findByCode("TWO"),
				Provider.findByCode("SIX")
				)
			
			def warrantySeven = newWarranty(['en':'warranty seven'],'warranty name7','email7@gmail.com',"0768-777-787","Street 174","8988",getDate(1, 12, 2010),getDate(12, 12, 2012),[:])
			def statusSeven= newEquipmentStatus(now(),User.findByUsername("admin"),Status.INSTOCK,equipmentSeven,false)
			def statusSevenOne = newEquipmentStatus(now(),User.findByUsername("admin"),Status.OPERATIONAL,equipmentSeven,false)
			def statusSevenTwo = newEquipmentStatus(now(),User.findByUsername("admin"),Status.UNDERMAINTENANCE,equipmentSeven,true)
			equipmentSeven.warranty=warrantySeven
			equipmentSeven.addToStatus(statusSeven)
			equipmentSeven.addToStatus(statusSevenOne)
			equipmentSeven.addToStatus(statusSevenTwo)
			equipmentSeven.save(failOnError:true,flush: true)
			
			def equipmentEight = newEquipment("SERIAL08",false,true,4,"Room A3","290540.23",['en':'Equipment Descriptions eight'],
				getDate(1,7,2000),getDate(12,7,2001),now(),
				'MODEL8',
				DataLocation.findByCode(BUTARO),
				Department.findByCode('ANAESTHETICS'),
				EquipmentType.findByCode("15819"),
				Provider.findByCode("EIGHT"),
				Provider.findByCode("EIGHT")
				)
			
			def warrantyEight = newWarranty(['en':'warranty four'],'warranty name4','email4@gmail.com',"0768-444-787","Street 154","8988",getDate(10, 12, 2010),getDate(12, 12, 2012),[:])
			def statusEight= newEquipmentStatus(now(),User.findByUsername("admin"),Status.INSTOCK,equipmentEight,false)
			def statusEightOne = newEquipmentStatus(now(),User.findByUsername("admin"),Status.OPERATIONAL,equipmentEight,false)
			def statusEightTwo = newEquipmentStatus(now(),User.findByUsername("admin"),Status.UNDERMAINTENANCE,equipmentEight,true)
			equipmentEight.warranty=warrantyEight
			equipmentEight.addToStatus(statusEight)
			equipmentEight.addToStatus(statusEightOne)
			equipmentEight.addToStatus(statusEightTwo)
			equipmentEight.save(failOnError:true,flush: true)
			
			def equipmentNine = newEquipment("SERIAL09",false,true,4,"Room 9A1","290540.23",['en':'Equipment Descriptions Nine'],
				getDate(1,7,2000),getDate(12,7,2001),now(),
				'MODEL3',
				DataLocation.findByCode(BUTARO),
				Department.findByCode('ANAESTHETICS'),
				EquipmentType.findByCode("15819"),
				Provider.findByCode("TWO"),
				Provider.findByCode("SIX")
				)
			
			def warrantyNine = newWarranty(['en':'warranty Nine'],'warranty name9','email94@gmail.com',"0768-999-787","Street 954","8989",getDate(10, 12, 2010),getDate(12, 12, 2012),[:])
			def statusNine= newEquipmentStatus(now(),User.findByUsername("admin"),Status.INSTOCK,equipmentNine,false)
			def statusNineOne = newEquipmentStatus(now(),User.findByUsername("admin"),Status.OPERATIONAL,equipmentNine,false)
			def statusNineTwo = newEquipmentStatus(now(),User.findByUsername("admin"),Status.UNDERMAINTENANCE,equipmentNine,true)
			equipmentNine.warranty=warrantyNine
			equipmentNine.addToStatus(statusNine)
			equipmentNine.addToStatus(statusNineOne)
			equipmentNine.addToStatus(statusNineTwo)
			equipmentNine.save(failOnError:true,flush: true)
			
			def equipmentTen = newEquipment("SERIAL10",false,true,4,"Room 10A1","290540.23",['en':'Equipment Descriptions Ten'],
				getDate(1,7,2000),getDate(12,7,2001),now(),
				'MODELTen3',
				DataLocation.findByCode(BUTARO),
				Department.findByCode('ANAESTHETICS'),
				EquipmentType.findByCode("15819"),
				Provider.findByCode("TWO"),
				Provider.findByCode("SIX")
				)
			
			def warrantyTen = newWarranty(['en':'warranty Ten'],'warranty name10','email410@gmail.com',"0768-100-787","Street 154","8988",getDate(10, 12, 2010),getDate(12, 12, 2012),[:])
			def statusTen= newEquipmentStatus(now(),User.findByUsername("admin"),Status.INSTOCK,equipmentTen,false)
			def statusTenOne = newEquipmentStatus(now(),User.findByUsername("admin"),Status.OPERATIONAL,equipmentTen,false)
			def statusTenTwo = newEquipmentStatus(now(),User.findByUsername("admin"),Status.UNDERMAINTENANCE,equipmentTen,true)
			equipmentTen.warranty=warrantyTen
			equipmentTen.addToStatus(statusTen)
			equipmentTen.addToStatus(statusTenOne)
			equipmentTen.addToStatus(statusTenTwo)
			equipmentTen.save(failOnError:true,flush: true)

		}
	}
	
	
	
	//Models definition
	public static def newEquipment(def serialNumber,def donation,def obsolete,def expectedLifeTime,def room,def purchaseCost,def descriptions,def manufactureDate, def purchaseDate,def registeredOn,def model,def dataLocation,def department, def type,def manufacture,def supplier){
		def equipment = new Equipment(serialNumber:serialNumber,donation:donation,obsolete:obsolete,room:room,expectedLifeTime:expectedLifeTime,purchaseCost:purchaseCost,manufactureDate:manufactureDate,purchaseDate:purchaseDate,registeredOn:registeredOn,model:model,dataLocation:dataLocation,department:department,type:type,manufacture:manufacture,supplier:supplier);
		setLocaleValueInMap(equipment,descriptions,"Descriptions")
		return equipment.save(failOnError: true,flush: true)
	}

	public static def newEquipmentStatus(def dateOfEvent,def changedBy,def value, def equipment,def current){
		def statusChangeDate = now()
		return new EquipmentStatus(dateOfEvent:dateOfEvent,changedBy:changedBy,status:value,equipment:equipment,current:current,statusChangeDate:statusChangeDate).save(failOnError: true,flush: true)
	}

	public static def newContact(def addressDescriptions,def contactName,def email, def phone, def street, def poBox){
		def contact = new Contact(contactName:contactName,email:email,phone:phone,street:street,poBox:poBox)
		setLocaleValueInMap(contact,addressDescriptions,"AddressDescriptions")
		return contact;
	}
	
	public static def newProvider(def code, def type, def contact){
		return new Provider(code:code,type:type,contact:contact).save(failOnError: true,flush: true)
	}
	
	public static def newProvider(def code, def type, def addressDescriptions, def contactName,def email, def phone, def street, def poBox){
		def contact = newContact(addressDescriptions,contactName,email,phone,street,poBox)
		return newProvider(code,type,contact)
	}
	
	public static def newWarranty(def contact, def startDate,def endDate,def descriptions){
		def warranty = new Warranty(contact:contact,startDate:startDate,endDate:endDate)
		setLocaleValueInMap(warranty,descriptions,"Descriptions")
		return warranty
	}
	
	public static def newWarranty(def addressDescriptions,def contactName,def email,def phone,def street,def poBox,def startDate,def endDate,def descriptions){
		def contact = newContact(addressDescriptions,contactName,email,phone,street,poBox)
		return newWarranty(contact, startDate,endDate,descriptions)
	}
	
	public static def newEquipmentModel(def names,def code,def descriptions){
		def model = new EquipmentModel(code:code)
		setLocaleValueInMap(model,names,"Names")
		setLocaleValueInMap(model,descriptions,"Descriptions")
		return model.save(failOnError: true,flush: true)
	}
	
	public static def newEquipmentType(def code, def names,def descriptions, def observation, def addedOn, def lastModifiedOn){
		def type = new EquipmentType(code:code,observation:observation,addedOn:addedOn,lastModifiedOn:lastModifiedOn)
		setLocaleValueInMap(type,names,"Names")
		setLocaleValueInMap(type,descriptions,"Descriptions")
		return type.save(failOnError: true,flush: true)
	}
	
	public static def newDepartment(def names,def code, def descriptions){
		def department = new Department(code:code)
		setLocaleValueInMap(department,names,"Names") 
		setLocaleValueInMap(department,descriptions,"Descriptions")
		return department.save(failOnError: true,flush: true)
	}
	
	public static def newDataLocationType(def names, def code) {
		def dataLocationType = new DataLocationType(code: code)
		setLocaleValueInMap(dataLocationType,names,"Names")
		return dataLocationType.save(failOnError: true,flush: true)
	}
	
	public static def newLocationLevel(def names, def code) {
		def locationLevel = new LocationLevel(code: code)
		setLocaleValueInMap(locationLevel,names,"Names")
		return locationLevel.save(failOnError: true,flush: true)
	}
	
	public static def newLocation(def names, def code, def parent, def level) {
		def location = new Location(code: code, parent: parent, level: level)
		setLocaleValueInMap(location,names,"Names")
		location.save(failOnError: true)
		level.addToLocations(location)
		level.save(failOnError: true,flush: true)
		if (parent != null) {
			parent.addToChildren(location)
			parent.save(failOnError: true,flush: true)
		}
		return location
	}
	
	public static def newDataLocation(def names, def code, def location, def type) {
		def dataLocation = new DataLocation(code: code, location: location, type: type)
		setLocaleValueInMap(dataLocation,names,"Names")
		dataLocation.save(failOnError: true,flush: true)
		if (location != null) {
			location.addToDataLocations(dataLocation)
			location.save(failOnError: true,flush: true)
		}
		if (type != null) {
			type.addToDataLocations(dataLocation)
			type.save(failOnError: true,flush: true)
	   }
		return dataLocation
	}
	
	static mapping ={
		table "memms_initializer_data"
		version false
	}
	
	/**
	* fieldName has to start with capital letter as
	* it is used to create setter of the object field
	* @param object
	* @param map
	* @param fieldName
	* @return
	*/
   public static def setLocaleValueInMap(def object, def map, def fieldName){
	   def methodName = 'set'+fieldName
	   //TODO replace with CONF variable if this fails
	   def grailsApplication = new Initializer().domainClass.grailsApplication
	   grailsApplication.config.i18nFields.locales.each{ loc ->
		   if(map.get(loc) != null)
			   object."$methodName"(map.get(loc),new Locale(loc))
		   else
			   object."$methodName"("",new Locale(loc))
	   }
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
