<%@ page import="org.chai.memms.equipment.EquipmentStatus.Status" %>
<div class="filters main">
		  <h2>Filter inventory<a href="#" class="right"><img src="${resource(dir:'images/icons',file:'icon_close_flash.png')}" alt="Section"/></a></h2>

			<g:hasErrors bean="${filterCmd}">
				<ul>
					<g:eachError var="err" bean="${filterCmd}">
						<h2><g:message error="${err}" /></h2>
					</g:eachError>
				</ul>
			</g:hasErrors>

			<g:form url="[controller:'equipment', action:'filter']" method="get" useToken="false" class="filters-box">
				<ul>
					<li><g:selectFromList name="equipmentType.id"
							label="${message(code:'equipment.type.label')}" bean="${filterCmd}"
							field="type" optionKey="${filterCmd?.equipmentType? 'id' : null}" multiple="false"
							ajaxLink="${createLink(controller:'EquipmentType', action:'getAjaxData', params:[class: 'EquipmentType'])}"
							from="${filterCmd?.equipmentType}" value="${filterCmd?.equipmentType?.id}" 
							values="${filterCmd?.equipmentType?.collect{it.names + ' ['+ it.code +']'}}"/></li>

					<li><g:selectFromList name="manufacturer.id"
							label="${message(code:'provider.manufacture.label')}" bean="${filterCmd}"
							field="manufacturer" optionKey="${filterCmd?.manufacturer? 'id' : null}" multiple="false"
							ajaxLink="${createLink(controller:'Provider', action:'getAjaxData', params:[class: 'Provider',type:'MANUFACTURE'])}"
							from="${filterCmd?.manufacturer}" value="${filterCmd?.manufacturer?.id}" 
							values="${filterCmd?.manufacturer?.collect{it.contact.contactName + ' ['+ it.code +']'}}"/></li>

					<li><g:selectFromList name="supplier.id"
							label="${message(code:'provider.supplier.label')}" bean="${filterCmd}"
							field="supplier" optionKey="${filterCmd?.supplier? 'id' : null}" multiple="false"
							ajaxLink="${createLink(controller:'Provider', action:'getAjaxData', params:[class: 'Provider',type:'SUPPLIER'])}"
							from="${filterCmd?.supplier}" value="${filterCmd?.supplier?.id}"
							values="${filterCmd?.manufacturer?.collect{it.contact.contactName + ' ['+ it.code +']'}}"
							/></li>
					<li>
					<li><label>Obsolete</label> <select name="obsolete">
							<option value="">Please select</option>
							<option value="true" ${filterCmd?.obsolete?.equals("true")? 'selected' : ''} >True</option>
							<option value="false" ${filterCmd?.obsolete?.equals("false")? 'selected' : ''}>False</option>
					</select></li>
					<li><label>Donated</label> <select name="donated">
							<option value="">Please select</option>
							<option value="true" ${filterCmd?.donated?.equals("true")? 'selected' : ''}>True</option>
							<option value="false" ${filterCmd?.donated?.equals("false")? 'selected' : ''}>False</option>
					</select></li>
					<li><g:selectFromEnum name="status" values="${Status.values()}" field="status" label="${message(code:'equipment.status.label')}" /></li>

				</ul>
				<button type="submit">Filter</button>
				<input type="hidden" name="dataLocation.id" value="${dataLocation.id}"/>
		  </g:form>
		</div>
		<h2 class="filter-results">Showing filtered list of equipment which contain search term "Scanner"</h2>
		