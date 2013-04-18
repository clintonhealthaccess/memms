<%@ page import="org.chai.memms.util.Utils" %>
<%@ page import="org.apache.shiro.SecurityUtils" %>
<%@ page import="org.chai.memms.security.User" %>
<%@ page import="org.chai.memms.corrective.maintenance.WorkOrderStatus.OrderStatus" %>
<table class="items">
	<thead>
		<tr>
			<th/>
			<th><g:message code="equipment.label"/></th>
			<g:sortableColumn property="currentStatus" title="${message(code: 'entity.status.label')}" params="[q:q,'equipment.id':equipment?.id,'dataLocation.id':dataLocation?.id]" />
			<g:sortableColumn property="criticality"  title="${message(code: 'work.order.criticality.label')}" params="[q:q,'equipment.id':equipment?.id,'dataLocation.id':dataLocation?.id]" />
			<g:sortableColumn property="openOn"  title="${message(code: 'order.open.on.label')}" params="[q:q,'equipment.id':equipment?.id,'dataLocation.id':dataLocation?.id]" />
			<g:sortableColumn property="closedOn"  title="${message(code: 'order.closed.on.label')}" params="[q:q,'equipment.id':equipment?.id,'dataLocation.id':dataLocation?.id]" />
			<th><g:message code="work.order.description.label"/></th>
				<th><g:message code="work.order.status.escalation.label"/></th>
			<th><g:message code="entity.messages.label"/></th>
		</tr>
	</thead>
	<tbody>
		<g:each in="${entities}" status="i" var="order">
			<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
				<td>
					<ul class="horizontal">
						<li>
							<shiro:hasPermission permission="workOrder:edit">
								<a href="${createLinkWithTargetURI(controller:'workOrder', action:'edit', params:[id: order.id])}" class="edit-button">
									<g:message code="default.link.edit.label" />
								</a>
							</shiro:hasPermission>
						</li>
						<li>
							<shiro:hasPermission permission="workOrder:delete">
								<a href="${createLinkWithTargetURI(controller:'workOrder', action:'delete', params:[id: order.id])}" onclick="return confirm('\${message(code: 'default.link.delete.confirm.message')}');" class="delete-button"><g:message code="default.link.delete.label" />
								</a>
							</shiro:hasPermission>
						</li>
						
					</ul>
				</td>
				<td>
					<a rel="${createLinkWithTargetURI(controller:'equipmentView', action:'getEquipmentClueTipsAjaxData', params:['equipment.id': order.equipment.id])}" class="clueTip">
						${order.equipment.code}
					</a>
					
				</td>
				<td>
					${message(code: order.currentStatus?.messageCode+'.'+order.currentStatus?.name)}
				</td>
				<td>
					${order.criticality}
				</td>
				<td>
					${Utils.formatDateWithTime(order.openOn)}
				</td>
				<td>
					${Utils.formatDateWithTime(order.closedOn)}
				</td>
				<td>
					<g:stripHtml field="${order.description}" chars="30"/>
				</td>
					<td>
						<g:if test="${order.currentStatus==OrderStatus.OPENATFOSA}">
			  				<button class="escalate next medium gray" id="${order.id}"><g:message code="work.order.escalate.issue.link.label"/></button>
			  				<img src="${resource(dir:'images',file:'spinner.gif')}" class="ajax-spinner"/>
			  			</g:if>
					</td>
				<td>
					<a href="${createLinkWithTargetURI(controller:'notificationWorkOrder', action:'list', params:[id: order.id, read:false])}">${order.getUnReadNotificationsForUser(User.findByUuid(SecurityUtils.subject.principal, [cache: true])).size()}</a>
				</td>
			</tr>
		</g:each>
	</tbody>
</table>
<g:render template="/templates/pagination" />
<script type="text/javascript">
	$(document).ready(function() {
		escaletWorkOrder("${createLink(controller:'workOrderView',action: 'escalate')}")
		getDatePicker("${resource(dir:'images',file:'icon_calendar.png')}")
		showClutips()
	});
</script>
