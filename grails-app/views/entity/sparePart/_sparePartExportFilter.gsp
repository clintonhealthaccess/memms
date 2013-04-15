<%@ page import="org.chai.memms.spare.part.SparePartStatus.StatusOfSparePart" %>
<%@ page import="org.chai.memms.spare.part.SparePart.SparePartPurchasedBy" %>
<div class="filters main">
		  <h2><g:message code="spare.part.filter.label" /><a href="#" id="showhide" class="right"><g:message code="entity.show.hide.filter.label" /></a></h2>

			<g:hasErrors bean="${filterCmd}">
				<ul>
					<g:eachError var="err" bean="${filterCmd}">
						<h2><g:message error="${err}" /></h2>
					</g:eachError>
				</ul>
			</g:hasErrors>

			<g:form url="[controller:'sparePartView', action:'generalExport']" method="get" useToken="false" class="filters-box">
				<ul class="filters-list">
					<li><g:selectFromList name="sparePartTypeids"
							label="${message(code:'spare.part.type.label')}" bean="${filterCmd}" 
							field="type" optionKey="id" multiple="true"
    						ajaxLink="${createLink(controller:'sparePartType', action:'getAjaxData', params: [type:'TYPE'])}"
    						from="${filterCmd?.sparePartTypes}" value="${filterCmd?.sparePartTypes*.id}" 
    						values="${filterCmd?.sparePartTypes.collect{it.names + ' ['+ it.code +']'}}"
    						/></li>
								
					<li><g:selectFromList name="supplierids"
							label="${message(code:'provider.type.supplier')}" bean="${filterCmd}"
							field="supplier" optionKey="${filterCmd?.suppliers? 'id' : null}" multiple="true"
							ajaxLink="${createLink(controller:'Provider', action:'getAjaxData', params:[type:'SUPPLIER'])}"
							from="${filterCmd?.suppliers}" value="${filterCmd?.suppliers?.id}"
							values="${filterCmd?.suppliers.collect{it.contact.contactName + ' ['+ it.code +']'}}"
							/></li>

					<li><g:selectFromList name="dataLocationids"
							label="${message(code:'filter.datalocation.label')}" bean="${filterCmd}"
							field="dataLocation" optionKey="id" multiple="true"
							ajaxLink="${createLink(controller:'dataLocation', action:'getAjaxData', params:[class: 'dataLocation'])}"
							from="${dataLocations}" value="${filterCmd?.dataLocations*.id}" 
							values="${dataLocations*.names}"/></li>
					<li>
						<label><g:message code="spare.part.same.as.manufacturer.label" /></label> 
						<select name="sameAsManufacturer">
								<option value=""><g:message code="default.please.select" /></option>
								<option value="true" ${filterCmd?.sameAsManufacturer?.equals("true")? 'selected' : ''} ><g:message code="default.boolean.true" /></option>
								<option value="false" ${filterCmd?.sameAsManufacturer?.equals("false")? 'selected' : ''}><g:message code="default.boolean.false" /></option>
						</select>
					</li>
					<li><g:selectFromEnum name="statusOfSparePart" values="${StatusOfSparePart.values()}" field="statusOfSparePart" label="${message(code:'spare.part.status.label')}" /></li>
					<li><g:selectFromEnum name="sparePartPurchasedBy" values="${SparePartPurchasedBy.values()}" field="sparePartPurchasedBy" label="${message(code:'spare.part.purchaser.label')}" /></li>
				</ul>
				<input type="hidden" name="exported" value="true"/>
				<button type="submit">Export</button>
				<a href="#" class="clear-form"><g:message code="default.link.clear.form.label"/></a>
		  </g:form>
		</div>