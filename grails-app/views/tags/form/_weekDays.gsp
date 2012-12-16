<%@ page import="org.chai.memms.preventive.maintenance.DurationBasedOrder.OccurencyType" %>

<div  class="row week-days ${hasErrors(bean:bean,field:field,'errors')}" ${bean.occurency != OccurencyType.WEEKLY ? 'style="display:none"' : ''}>
        <label for="${name}">${label}:</label>
        <div class="week-days-options">
            <g:daysOfWeek name="occurDaysOfWeek" selectedDays="${bean?.occurDaysOfWeek}" />
        </div>
        <div class="error-list"><g:renderErrors bean="${bean}" field="${field}" /></div>
</div>