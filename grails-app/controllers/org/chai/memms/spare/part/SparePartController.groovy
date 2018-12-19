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

import java.util.Date;

import org.apache.shiro.SecurityUtils;
import org.chai.memms.AbstractEntityController;
import org.chai.memms.spare.part.SparePart.StockLocation;
import org.chai.memms.spare.part.SparePart.SparePartStatus;
import org.chai.memms.Contact;
import org.chai.memms.Initializer;
import org.chai.memms.spare.part.SparePart.SparePartPurchasedBy;
import org.chai.memms.security.User;
import org.chai.memms.security.User.UserType;
import org.chai.memms.util.Utils;
import org.chai.memms.spare.part.SparePart;
import org.chai.memms.inventory.Provider;
import org.chai.location.DataLocation;
import org.chai.location.CalculationLocation;
import org.chai.location.DataLocationType;
import org.chai.location.Location;
import org.chai.location.LocationLevel;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Aphrodice Rwagaju
 *
 */
class SparePartController extends AbstractEntityController{
	def sparePartStatusService


	def index = {
		redirect(controller: "sparePartView", action: "summaryPage", params: params)
	}

	def getEntity(def id) {
		return SparePart.get(id);
	}

	def createEntity() {
		return new SparePart();
	}

	def getTemplate() {
		return "/entity/sparePart/createSparePart";
	}
	
	def getLabel() {
		return "spare.part.label";
	}

	def getEntityClass() {
		return SparePart.class;
	}
	
	def bindParams(def entity) {
		def stockLocation = params['stockLocation']
		def status = params['status']
		def previousStatus = null;
		if(entity.id!=null){
			previousStatus = entity.status
		}
		
		if(status!=null) status = SparePartStatus."$status"
		if(stockLocation!=null) stockLocation = StockLocation."$stockLocation"

		if(log.isDebugEnabled()) log.debug("SparePart params: before bind "+params)

		if(entity.id==null){
			entity.addedBy = user 
			if(status.equals(SparePartStatus.INSTOCK)) 
				entity.inStockQuantity = params.int("receivedQuantity")
		}else{ 
			entity.lastModified = user
			if(status.equals(SparePartStatus.PENDINGORDER) && (previousStatus.equals(SparePartStatus.PENDINGORDER)||previousStatus.equals(SparePartStatus.INSTOCK))) {
				params["inStockQuantity"] = null
				params["receivedQuantity"] = null
			}
			if(status.equals(SparePartStatus.INSTOCK) && previousStatus.equals(SparePartStatus.PENDINGORDER)) {
				if(params["receivedQuantity"] && !params["inStockQuantity"]) params["inStockQuantity"] = params["receivedQuantity"]
			}
			if(stockLocation.equals(StockLocation.MMC)) params.dataLocation =''
		}

		entity.properties = params
		if(log.isDebugEnabled()) log.debug("SparePart params: after bind "+entity)
	}


	def getModel(def entity) {
		def suppliers = []; def types = []; def dataLocations = [];
		if (entity.supplier != null) suppliers << entity.supplier
		if (entity.type!=null) types << entity.type
		if (entity.dataLocation!=null) dataLocations << entity.dataLocation
		[
			sparePart: entity,
			suppliers: suppliers,
			types: types,
			dataLocations: dataLocations,
			currencies: grailsApplication.config.site.possible.currency,
			now:now,
			code: getLabel()
		]
	}
}

