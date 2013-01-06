<%@ page import="org.chai.memms.util.Utils" %>
<%@ page import="org.chai.memms.preventive.maintenance.WorkBasedOrder.WorkIntervalType" %>
<div  class="entity-form-container togglable">
    <div class="heading1-bar">
    		<h1>
      			<g:if test="${order.id != null}">
      				  <g:message code="default.edit.label" args="[message(code:'preventive.order.label')]" />
      			</g:if>
      			<g:else>
      				  <g:message code="default.new.label" args="[message(code:'preventive.order.label')]" />
      			</g:else>
    		</h1>
		    <g:locales/>
   </div>
	 <div class="main">
    	<g:form url="[controller:'workBasedOrder', action:'save', params:[targetURI: targetURI]]" useToken="true" class="simple-list">
      <g:render template="/entity/preventiveOrder/preventiveOrderFormTop" model="[order: order]"/>
      	  <div class="form-section">
            <fieldset class="form-content">
            <h4 class="section-title">
                <span class="question-default">
                  <img src="${resource(dir:'images/icons',file:'star_small.png')}">
                </span>
                <g:message code="order.section.occurance.information.label"/>
              </h4> 
              <g:inputPlusSelect label="${message(code:'preventive.repeats.every.label')}" bean="${order}"  inputField="occurInterval" selectField="occurency" selectFieldValues="${WorkIntervalType.values()}" />
              <g:inputTimeDate name="openOn" field="openOn" date="${order.openOn?.date}" time="${order.openOn?.time}" label='preventive.starts.on.label' bean="${order}" dateClass="date-picker" timeClass="time-picker"/>
            </fieldset>
          </div>
      		<div class="buttons">
        			<button type="submit"><g:message code="default.button.save.label"/></button>
        			<a href="${createLink(uri: targetURI)}"><g:message code="default.link.cancel.label"/></a>
      		</div>  
  	  </g:form>  
    </div>
</div>
<script type="text/javascript">
  $(document).ready(function() {
    numberOnlyField();
    getDatePicker("${resource(dir:'images',file:'icon_calendar.png')}");
    getToHide()
  });
</script>

