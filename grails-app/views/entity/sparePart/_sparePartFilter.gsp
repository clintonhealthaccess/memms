<%@ page import="org.chai.memms.spare.part.SparePartStatus.StatusOfSparePart" %>
<%@ page import="org.chai.memms.spare.part.SparePart.SparePartPurchasedBy" %>
<%@ page import="org.chai.memms.spare.part.SparePart" %>
<div class="filters main">
  <h2>
  		<g:message code="spare.part.filter.label" />
	  	<a href="#" id="showhide" class="right">
	  		<g:message code="entity.show.hide.filter.label" />
	  	</a>
  </h2>

	<g:hasErrors bean="${filterCmd}">
		<ul>
			<g:eachError var="err" bean="${filterCmd}">
				<h2><g:message error="${err}" /></h2>
			</g:eachError>
		</ul>
	</g:hasErrors>

	<g:form url="[controller:'sparePartView', action:'filter']" method="get" useToken="false" class="filters-box">
		<ul class="filters-list third">
      <li>
        <g:selectFromList name="sparePartType.id"
          label="${message(code:'spare.part.type.label')}" bean="${filterCmd}"
          field="type" optionKey="id" multiple="false"
          ajaxLink="${createLink(controller:'SparePartType', action:'getAjaxData')}"
          from="${filterCmd?.sparePartType}" value="${filterCmd?.sparePartType?.id}"
          values="${filterCmd?.sparePartType.collect{it.names + ' ['+ it.code +']'}}"/>
      </li>
      <li>
        <g:selectFromEnum name="statusOfSparePart" values="${StatusOfSparePart.values()}" field="statusOfSparePart" label="${message(code:'spare.part.status.label')}" />
      </li>
    </ul>
		<ul class="filters-list third">
      <li>
        <g:selectFromList name="supplier.id"
          label="${message(code:'provider.type.supplier')}" bean="${filterCmd}"
          field="supplier" optionKey="id" multiple="false"
          ajaxLink="${createLink(controller:'Provider', action:'getAjaxData', params:[type:'SUPPLIER'])}"
          from="${filterCmd?.supplier}" value="${filterCmd?.supplier?.id}"
          values="${filterCmd?.supplier.collect{it.contact.contactName + ' ['+ it.code +']'}}"/>
      </li>
      <li>
        <g:selectFromEnum name="sparePartPurchasedBy" values="${SparePartPurchasedBy.values()}" field="sparePartPurchasedBy" label="${message(code:'spare.part.purchaser.label')}" />
      </li>
    </ul>
		<ul class="filters-list third">
      <li>
        <label><g:message code="spare.part.same.as.manufacturer.label" /></label>
        <select name="sameAsManufacturer">
          <option value=""><g:message code="default.please.select" /></option>
          <option value="true" ${filterCmd?.sameAsManufacturer?.equals("true")? 'selected' : ''} ><g:message code="same.as.manufacturer.boolean.true" /></option>
          <option value="false" ${filterCmd?.sameAsManufacturer?.equals("false")? 'selected' : ''}><g:message code="same.as.manufacturer.boolean.false" /></option>
        </select>
      </li>
		</ul>
    <div class="clear-left">
      <input type="hidden" name="dataLocation.id" value="${dataLocation?.id}"/>
      <button type="submit"><g:message code="entity.filter.label" /></button>
      <a href="#" class="clear-form"><g:message code="default.link.clear.form.label"/></a>
    </div>
  </g:form>
</div>
<g:if test="${params?.q}">
	<h2 class="filter-results">
		<g:message code="entity.filter.message.label" args="${[message(code: 'spare.part.label'),params?.q]}" />
	</h2>
</g:if>

