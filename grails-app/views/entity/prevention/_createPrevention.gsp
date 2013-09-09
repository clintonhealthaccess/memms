
<%@ page import="org.chai.memms.preventive.maintenance.PreventiveOrder.PreventiveOrderType" %>
<r:require modules="tipsy"/>
<div  class="entity-form-container togglable">
    <div class="heading1-bar">
    		<h1>
      			<g:if test="${prevention.id != null}">
      				  <g:message code="default.edit.label" args="[message(code:'prevention.label')]" />
      			</g:if>
      			<g:else>
      				  <g:message code="default.new.label" args="[message(code:'prevention.label')]" />
      			</g:else>
    		</h1>
		    <g:locales/>
   </div>
	 <div class="main">
    	<g:form url="[controller:'prevention', action:'save', params:[targetURI: targetURI]]" useToken="true" class="simple-list">
      	  <div class="form-section">
            <fieldset class="form-content">
              <h4 class="section-title">
                  <span class="question-default">
                    <img src="${resource(dir:'images/icons',file:'star_small.png')}">
                  </span>
                  <g:message code="prevention.section.basic.information.label"/>
              </h4> 
              <div class="row">
                <input type="hidden" name="order.id" value="${prevention.order.id}" />
              </div>
            <g:input name="eventDate" dateClass="date-picker" label="${message(code:'equipment.status.date.of.event.label')}" bean="${prevention}" field="eventDate"/>
            <g:inputHourMinute name="timeSpend" field="timeSpend" hours="${prevention.timeSpend?.hours}" minutes="${prevention.timeSpend?.minutes}" label='prevention.work.time.label' bean="${order}"/>
            <g:inputTimeDate name="scheduledOn" field="scheduledOn" date="${(scheduledOn)?scheduledOn.date:prevention.scheduledOn?.date}" time="${(scheduledOn)?scheduledOn.time:prevention.scheduledOn?.time}" label='prevention.scheduled.on.label' bean="${prevention}" dateClass="date-picker" timeClass="time-picker"/>
            <g:i18nTextarea name="descriptions" bean="${prevention}" label="${message(code:'entity.descriptions.label')}" field="descriptions" height="150" width="300" maxHeight="150" />
            </fieldset>
            </div>
            <g:if test="${prevention.id != null}">
            <div class="form-content">
              <fieldset>
                <h4 class="section-title">
                    <span class="question-default">
                      <img src="${resource(dir:'images/icons',file:'star_small.png')}">
                    </span>
                    <g:message code="prevention.section.process.information.label"/>
                </h4> 
              <div class="row maintenance-process">
                  <div class="process process-action half">
                    <label>Possible processes</label>
                    <fieldset>
                     <ul class="draggable process-list-action">
                       <g:if test="${prevention.actions.size()==0}" >
                          <li value="_">No process available add some form equipment type</li>
                      </g:if>  
                       <g:each in="${actions}" status="i" var="action">
                          <li value="${action.id}">
                            ${action.description}
                          </li>
                       </g:each>
                     </ul>
                    </fieldset>
                  </div>
                  <div class="process process-material half">
                    <label>Used processes</label>
                    <fieldset>
                      <ul class="droppable process-list-material">
                      <g:if test="${prevention.actions.size()==0}" >
                          <li value="_">Dragge process here</li>
                      </g:if>                       
                        <g:each in="${prevention.actions}" var="action">
                          <li value="${action.id}" name="test">
                            ${action.description}
                          </li>
                        </g:each>
                      </ul>
                      <div class="added-processes" >  
                        <select name="actions[]" multiple="true" style="diplay:none">
                          <g:each in="${prevention.actions}" var="action">
                            <option value="${action.id}"></option>
                          </g:each>
                        </select>
                      </div>
                    </fieldset>
                  </div>
              </div>
              </fieldset>
            </div>
            </g:if>
             <g:if test="${prevention.id != null}">
              <input type="hidden" name="id" value="${prevention.id}" />
            </g:if>
            
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
    addPreventionProcess("${createLink(controller:'prevention',action: 'addProcess')}","${prevention.id}","");
    removePreventionProcess("${createLink(controller:'prevention',action: 'removeProcess')}");
    getDatePicker("${resource(dir:'images',file:'icon_calendar.png')}");
    
    $('.added-processes').hide();

    $(".droppable").droppable({ hoverClass:'dragging',drop: function(event, ui) {
        $(".droppable").prepend($(ui.draggable[0]).removeAttr("style"));
    }});

    $(".draggable").droppable({ hoverClass:'dragging',drop: function(event, ui) {
      var currentTag = $(ui.draggable[0])
      alert("==>: "+ui.attr("value"))
        $(".draggable").prepend(currentTag.removeAttr("style"));
    }});

    $(".draggable li").draggable({ addClasses:'.dragging',addClasses:'',containment: 'document',cursor:'pointer',helper: 'clone'});
    $(".droppable li").draggable({ addClasses:'.dragging',addClasses:'',containment: 'document',cursor:'pointer',helper: 'clone'});
    
  });
</script>

