<%@ page import="org.chai.memms.spare.part.SparePartStatus.Status" %>
<%@ page import="org.chai.memms.spare.part.SparePart.PurchasedBy" %>
<%@ page import="org.chai.memms.spare.part.SparePart.Donor" %>
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
							ajaxLink="${createLink(controller:'SparePartType', action:'getAjaxData', params:[observation:'USEDINMEMMS'])}"
							from="${filterCmd?.sparePartTypes}" value="${filterCmd?.sparePartTypes*.id}" 
							values="${filterCmd?.sparePartTypes.collect{it.names + ' ['+ it.code +']'}}"/></li>

					<li><g:selectFromList name="manufacturerids"
							label="${message(code:'provider.type.manufacturer')}" bean="${filterCmd}"
							field="manufacturer" optionKey="${filterCmd?.manufacturers? 'id' : null}" multiple="true"
							ajaxLink="${createLink(controller:'Provider', action:'getAjaxData', params:[type:'MANUFACTURER'])}"
							from="${filterCmd?.manufacturers}" value="${filterCmd?.manufacturers?.id}" 
							values="${filterCmd?.manufacturers.collect{it.contact.contactName + ' ['+ it.code +']'}}"/></li>

					<li><g:selectFromList name="supplierids"
							label="${message(code:'provider.type.supplier')}" bean="${filterCmd}"
							field="supplier" optionKey="${filterCmd?.suppliers? 'id' : null}" multiple="true"
							ajaxLink="${createLink(controller:'Provider', action:'getAjaxData', params:[type:'SUPPLIER'])}"
							from="${filterCmd?.suppliers}" value="${filterCmd?.suppliers?.id}"
							values="${filterCmd?.suppliers.collect{it.contact.contactName + ' ['+ it.code +']'}}"
							/></li>
					<li><g:selectFromList name="serviceProvidersIds"
							label="${message(code:'provider.type.serviceProvider')}" bean="${filterCmd}"
							field="serviceProviders" optionKey="${filterCmd?.suppliers? 'id' : null}" multiple="true"
							ajaxLink="${createLink(controller:'Provider', action:'getAjaxData', params:[type:'SERVICEPROVIDER'])}"
							from="${filterCmd?.serviceProviders}" value="${filterCmd?.serviceProviders?.id}"
							values="${filterCmd?.serviceProviders.collect{it.contact.contactName + ' ['+ it.code +']'}}"
							/></li>
					<li><g:selectFromList name="calculationLocationids"
							label="${message(code:'calculation.location.display.label')}" bean="${filterCmd}"
							field="calculationLocations" optionKey="id" multiple="true"
							ajaxLink="${createLink(controller:'SparePart', action:'getCalculationLocationAjaxData', params:[class: 'CalculationLocation'])}"
							from="${filterCmd?.calculationLocations}" value="${filterCmd?.calculationLocations*.id}" 
							values="${filterCmd?.calculationLocations*.names}"/></li>

					<li><g:selectFromList name="dataLocationTypeids"
							label="${message(code:'filter.datalocationtype.label')}" bean="${filterCmd}"
							field="manufacturer" optionKey="id" multiple="true"
							ajaxLink="${createLink(controller:'SparePart', action:'getAjaxData', params:[class: 'DataLocationType'])}"
							from="${dataLocationTypes}" value="${filterCmd?.dataLocationTypes*.id}" 
							values="${dataLocationTypes*.names}"/></li>
					<li>
						<label><g:message code="spare.part.obsolete.label" /></label> 
						<select name="obsolete">
								<option value=""><g:message code="default.please.select" /></option>
								<option value="true" ${filterCmd?.obsolete?.equals("true")? 'selected' : ''} ><g:message code="default.boolean.true" /></option>
								<option value="false" ${filterCmd?.obsolete?.equals("false")? 'selected' : ''}><g:message code="default.boolean.false" /></option>
						</select>
					</li>
					<li><g:selectFromEnum name="sparePartStatus" values="${Status.values()}" field="sparePartStatus" label="${message(code:'spare.part.status.label')}" /></li>
					<li><g:selectFromEnum name="purchaser" values="${PurchasedBy.values()}" field="purchaser" label="${message(code:'spare.part.purchaser.label')}" /></li>
					<li><g:selectFromEnum name="donor" values="${Donor.values()}" field="donor" label="${message(code:'spare.part.donor.label')}" /></li>
				</ul>
				<input type="hidden" name="exported" value="true"/>
				<button type="submit">Export</button>
				<a href="#" class="clear-form"><g:message code="default.link.clear.form.label"/></a>
		  </g:form>
		</div>