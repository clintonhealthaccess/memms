<%@ page import="org.chai.memms.util.Utils" %>
<%@ page import="org.apache.shiro.SecurityUtils" %>
<%@ page import="org.chai.memms.security.User" %>
<%@ page import="org.chai.memms.preventive.maintenance.PreventiveOrder.PreventiveOrderType" %>
<table class="items">
	<thead>
		<tr>
			<th/>
			<th><g:message code="equipment.label"/></th>
			<g:sortableColumn property="${names}" title="${message(code: 'entity.names.label')}" params="[q:q,'equipment.id':equipment?.id,'dataLocation.id':dataLocation?.id]" />
			<g:sortableColumn property="type"  title="${message(code: 'entity.type.label')}" params="[q:q,'equipment.id':equipment?.id,'dataLocation.id':dataLocation?.id]" />
			<th><g:message code="entity.occurency.label"/></th>
			<th><g:message code="entity.occurency.interval.label"/></th>
			<g:sortableColumn property="status"  title="${message(code: 'entity.status.label')}" params="[q:q,'equipment.id':equipment?.id,'dataLocation.id':dataLocation?.id]" />
			<g:sortableColumn property="openOn"  title="${message(code: 'preventive.order.open.on.label')}" params="[q:q,'equipment.id':equipment?.id,'dataLocation.id':dataLocation?.id]" />
			<g:sortableColumn property="closedOn"  title="${message(code: 'preventive.order.closed.on.label')}" params="[q:q,'equipment.id':equipment?.id,'dataLocation.id':dataLocation?.id]" />
			<th><g:message code="entity.descriptions.label"/></th>
			<th><g:message code="preventive.order.messages.label"/></th>
		</tr>
	</thead>
	<tbody>
		<g:each in="${entities}" status="i" var="order">
			<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
				<td>
					<ul class="horizontal">
						<li>
							<a href="${createLinkWithTargetURI(controller:(order.type.equals(PreventiveOrderType.DURATIONBASED))?'durationBasedOrder':'workBasedOrder', action:'edit', 
							params:[id: order.id])}" class="edit-button"> <g:message code="default.link.edit.label" />
							</a>
						</li>
						<li>
							<a href="${createLinkWithTargetURI(controller:(order.type.equals(PreventiveOrderType.DURATIONBASED))?'durationBasedOrder':'workBasedOrder', action:'delete', 
							params:[id: order.id])}" onclick="return confirm('\${message(code: 'default.link.delete.confirm.message')}');" class="delete-button">
							<g:message code="default.link.delete.label" />
							</a>
						</li>	
					</ul>
				</td>
				<td>
					<a rel="${createLinkWithTargetURI(controller:'equipmentView', action:'getEquipmentClueTipsAjaxData', params:['equipment.id': order.equipment.id])}" class="clueTip">
						${order.equipment.code}
					</a>
					
				</td>
				<td>
					${order.names}
				</td>
				<td>
					${message(code: order.type?.messageCode+'.'+order.type?.name)}
				</td>
				<td>
					${message(code: order.occurency?.messageCode+'.'+order.occurency?.name)}					
				</td>
				<td>
					${order.occurInterval}					
				</td>
				<td>
					${message(code: order.status?.messageCode+'.'+order.status?.name)}
				</td>
				<td>
					${Utils.formatDateWithTime(order.openOn?.timeDate)}
				</td>
				<td>
					${Utils.formatDateWithTime(order?.closedOn)}
				</td>
				<td>
					<g:stripHtml field="${order.description}" chars="30"/>
				</td>
				<td>
					
				</td>
			</tr>
		</g:each>
	</tbody>
</table>
<g:render template="/templates/pagination" />
<script type="text/javascript">
	$(document).ready(function() {
		getDatePicker("${resource(dir:'images',file:'icon_calendar.png')}")
		showClutips()
	});
</script>
