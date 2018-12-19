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
package org.chai.memms.spare.part

import javax.persistence.Transient;

import groovy.transform.EqualsAndHashCode;
import org.chai.memms.inventory.Provider;
import org.chai.memms.inventory.EquipmentType;
import org.chai.memms.inventory.Equipment;
import org.chai.memms.spare.part.SparePart.SparePartStatus;
import org.chai.memms.spare.part.SparePart.StockLocation;
import org.chai.memms.spare.part.SparePart;
import org.chai.memms.inventory.Provider.Type


import i18nfields.I18nFields;
/**
 * @author Jean Kahigiso M.
 *
 */
@i18nfields.I18nFields
@EqualsAndHashCode(includes="code")
class SparePartType {
	
	String code
	String names
	String partNumber
	String model
	String descriptions
	Date discontinuedDate
	Date dateCreated
	Date lastUpdated
	Provider manufacturer

	static i18nFields = ["descriptions", "names"]
	static hasMany = [spareParts: SparePart,compatibleEquipmentTypes:EquipmentType,vendors:Provider]
	static mappedBy = [vendors: "sparePartTypes"]

	static constraints = {
		code nullable: false, blank:false,unique :true
		names nullable: true, blank: true
		descriptions nullable: true, blank: true
		partNumber nullable: false,blank:false
		model nullable: true,blank:true
		discontinuedDate nullable: true
		manufacturer nullable:false,blank:false, validator:{
			return it.type in [Type.BOTH, Type.MANUFACTURER]		
		}
	}

	static mapping = {
		table "memms_spare_part_type"
		version false
		cache true
	}

	@Transient
	def getInStockSpareParts(){
		def inStockSpareParts = []
		if(spareParts && spareParts.size())
			for(SparePart sparePart: spareParts)
				if(sparePart.status.equals(SparePartStatus.INSTOCK) && sparePart.inStockQuantity>0) 
					inStockSpareParts.add(sparePart)
		return inStockSpareParts
	}
	// Added by Aphrodice for testing purpose
	@Transient
	def getInStockSparePartsQuantity(){
		spareParts=getInStockSpareParts()
		def inStockSpareParts = 0
			for(SparePart sparePart: spareParts)
				inStockSpareParts= inStockSpareParts+ sparePart.inStockQuantity
		return inStockSpareParts
	}

	@Transient
	def getPendingSpareParts(){
		def pendingSpareParts = []
		if(spareParts && spareParts.size())
			for(SparePart sparePart: spareParts)
				if(sparePart.status.equals(SparePartStatus.PENDINGORDER) && sparePart.receivedQuantity>0) 
					pendingSpareParts.add(sparePart)
		return pendingSpareParts
	}

	@Transient
	def getQuantityInStockAtMMC(){
		def quantityInStockAtMMC = []
		if(spareParts && spareParts.size())
			for(SparePart sparePart: spareParts){
				if(sparePart.status.equals(SparePartStatus.INSTOCK) && sparePart.stockLocation.equals(StockLocation.MMC) && sparePart.inStockQuantity>0)
					quantityInStockAtMMC = quantityInStockAtMMC+sparePart.inStockQuantity 
			}
		return quantityInStockAtMMC
	}

	@Transient
	def getQuantityInStockAtFacility(){
		def quantityInStockAtFacility= 0
		if(spareParts && spareParts.size())
			for(SparePart sparePart: spareParts){
				if(sparePart.status.equals(SparePartStatus.INSTOCK) && sparePart.stockLocation.equals(StockLocation.FACILITY) && sparePart.inStockQuantity>0)
					quantityInStockAtFacility = quantityInStockAtFacility+sparePart.inStockQuantity
			}
		return quantityInStockAtFacility
	}

	@Transient
	def getQuantityPendingAtMMC(){
		def quantityPendingAtMMC = 0
		if(spareParts && spareParts.size())
			for(SparePart sparePart: spareParts)
				if(sparePart.status.equals(SparePartStatus.PENDINGORDER) && sparePart.stockLocation.equals(StockLocation.MMC) && sparePart.receivedQuantity>0) 
					quantityPendingAtMMC = quantityPendingAtMMC+sparePart.receivedQuantity
		return quantityPendingAtMMC
	}

	@Transient
	def getQuantityPendingAtFacility(){
		def quantityPendingAtFacility =0
		if(spareParts && spareParts.size())
			for(SparePart sparePart: spareParts)
				if(sparePart.status.equals(SparePartStatus.PENDINGORDER) && sparePart.stockLocation.equals(StockLocation.FACILITY) && sparePart.receivedQuantity>0) 
					quantityPendingAtFacility = quantityPendingAtFacility+sparePart.receivedQuantity
		return quantityPendingAtFacility
	}



	@Override
	public String toString() {
		return "SparePartType [id ="+id+" code=" + code + ", partNumber=" + partNumber + ", model=" + model +"]";
	}
}
