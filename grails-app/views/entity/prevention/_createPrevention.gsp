
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
                <input type="hidden" class="order-id" name="order.id" value="${prevention.order.id}" />
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
                    <label><g:message code="prevention.possible.processes.label"/></label>
                    <fieldset>
                     <ul class="draggable process-list-action"> 
                       <g:each in="${actions}" status="i" var="action">
                          <li data-id="${action.id}">
                            ${action.description}
                          </li>
                       </g:each>
                     </ul>
                    </fieldset>
                  </div>
                  <div class="process process-material half">
                    <label><g:message code="prevention.used.processes.label"/></label>
                    <fieldset>
                      <ul class="droppable process-list-material">
                        <g:each in="${prevention.actions}" var="action">
                          <li data-id="${action.id}">
                            ${action.description}
                          </li>
                        </g:each>
                      </ul>
                        <select id="action-list" name="actions" multiple="true" style="display:none;"  >
                          <g:each in="${prevention.actions}" var="action">
                            <option value="${action.id}" selected></option>
                          </g:each>
                        </select>
                    </fieldset>
                  </div>
              </div>
              </fieldset>
            </div>
              <div class="form-section">
                <fieldset>
                  <h4 class="section-title">
                    <span class="question-default">
                      <img src="${resource(dir:'images/icons',file:'star_small.png')}">
                    </span>
                    <g:message code="work.order.section.spare.part.information.label"/>
                  </h4>
                  <div class="row maintenance-process">
                         <div class="process process-action half">
                            <label>Used spare parts</label>
                            <fieldset>
                            <g:render template="/templates/usedSparePartList" model="['order':prevention.order,'usedSpareParts':prevention.usedSpareParts,'label':'work.order.performed.action.label']" />
                            <br/> 
                  <label>Spare Parts</label>
                  <select class="spare-parts" name="spareParts" >
                    <option value="_" class="first-option">Select used compatible</option>
                            <g:each in="${compatibleSpareParts}" var="sparePartType">
                              <option value="${sparePartType.key.id}">${sparePartType.key.names} - [${sparePartType.value}]</option>
                            </g:each>
                        </select>
                        <div class="spare-part-quanty">
                            <input type="text" name="spare-part-number"  class="idle-field numbers-only" value="" />
                      <a href="#" class="add-spare-part next medium"><g:message code="default.add.label"  args="${['']}"/></a>
                      <img src="${resource(dir:'images',file:'spinner.gif')}" class="ajax-spinner"/>
                      <span class="ajax-error alt"><g:message code="entity.error.updating.try.again"/></span>
                    </div>  
                           </fieldset>
                          </div>
                      </div>
                 </fieldset>
              </div>
            </g:if>
             <g:if test="${prevention.id != null}">
              <input type="hidden" class="prevention-id" name="id" value="${prevention.id}" />
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
    addSpareParts("${createLink(controller:'prevention',action: 'addSpareParts')}");
    hideSparePartQuantityField();
    
    $(".droppable").droppable({ hoverClass:'dragging',drop: function(event, ui) {  
       $("#action-list option[value="+$(ui.draggable[0]).attr("data-id")+"]").remove();
       $("#action-list").append('<option value='+$(ui.draggable[0]).attr("data-id")+' selected >'+$(ui.draggable[0]).text()+'</option>');
       $(".droppable").prepend($(ui.draggable[0]).removeAttr("style"));
    }});

    $(".draggable").droppable({ hoverClass:'dragging',drop: function(event, ui) {
      $("#action-list option[value="+$(ui.draggable[0]).attr("data-id")+"]").remove();
      $(".draggable").prepend($(ui.draggable[0]).removeAttr("style"));
    }});

    $(".draggable li").draggable({ addClasses:'.dragging',addClasses:'',containment: 'document',cursor:'pointer',helper: 'clone'});
    $(".droppable li").draggable({ addClasses:'.dragging',addClasses:'',containment: 'document',cursor:'pointer',helper: 'clone'});
    
  });
</script>

