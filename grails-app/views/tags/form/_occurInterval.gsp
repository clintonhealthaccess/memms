<%@ page import="org.chai.memms.preventive.maintenance.DurationBasedOrder.OccurencyType" %>

<div class="row occur-interval ${hasErrors(bean:bean,field:field,'errors')}" ${bean.occurency == OccurencyType.DAYS_OF_WEEK ? 'style="display:none"' : ''}>
    <label for="${name}">${label}:</label>
    <select name="${name}">
    	<g:each in="${1..range}" var="item" status="i">
    		<option value="${item}" ${!(value==item)?:'selected'}>${item}</option>
    	</g:each>
    </select>
    <label class="has-helper"></label>
    <div class="error-list"><g:renderErrors bean="${bean}" field="${field}" /></div>
</div>



