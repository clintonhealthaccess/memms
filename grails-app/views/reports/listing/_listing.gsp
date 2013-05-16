<%@ page import="org.chai.memms.util.Utils.ReportType" %>
<%@ page import="org.chai.memms.util.Utils.ReportSubType" %>
<div class="entity-list">
  <!-- List Top Header Template goes here -->
  <div class="heading1-bar">
    <h1>Detailed Reports</h1>
  </div>
  <!-- End of template -->

  <!-- Filter template if any goes here -->
  <g:if test="${filterTemplate!=null}">
    <g:render template="/entity/${filterTemplate}" />
  </g:if>
  <!-- End of template -->

  <!-- List Template goes here -->
  <div id ="list-grid" class="v-tabs">

    <!-- Create Customized Report Template goes here -->
    <g:render template="/reports/listing/customizedReport/createCustomizedReport" />

    <ul id='js-top-tabs' class="v-tabs-nav left">
      <li><a class="active" id="#js-inventory">Inventory</a></li>
      <li><a id="#js-corrective">Corrective Maintenance</a></li>
      <li><a id="#js-preventive">Preventive Maintenance</a></li>
      <li><a id="#js-spare-parts">Spare Parts</a></li>
    </ul>

    <div id='js-inventory' class="v-tabs-content right shown">
      <ul class="v-tabs-subnav">
      	<li>
      	<a class="active" href="${createLinkWithTargetURI(controller: 'listing', action:'generalEquipmentsListing')}" id="report-1">
			<g:message code="default.all.equipments.label" />
		</a></li>
		<li>
      	<a href="${createLinkWithTargetURI(controller: 'listing', action:'obsoleteEquipments')}" id="report-2">
			<g:message code="default.obsolete.label" />
		</a></li>
		<li>
      	<a href="${createLinkWithTargetURI(controller: 'listing', action:'disposedEquipments')}" id="report-3">
			<g:message code="default.disposed.label" />
		</a></li>
		<li>
		<a href="${createLinkWithTargetURI(controller: 'listing', action:'underMaintenanceEquipments')}" id="report-4">
			<g:message code="default.under.maintenance.label" />
		</a></li>
		<li>
		<a href="${createLinkWithTargetURI(controller: 'listing', action:'inStockEquipments')}" id="report-5">
			<g:message code="default.in.stock.label" />
		</a></li>
		<li>
		<a href="${createLinkWithTargetURI(controller: 'listing', action:'underWarrantyEquipments')}" id="report-6">
			<g:message code="default.under.waranty.label" />
		</a></li>
      </ul>

        <div id='report-1' class="js-shown-report">
          <div class="v-tabs-summary">
            <h2>Summary</h2>
            <hr />
            <ul>
              <li><a href="#">32.020<span>Some label</span></a></li>
              <li><a href="#">12.345<span>Some longer label</span></a></li>
              <li><a href="#">75.501<span>Some very long label</span></a></li>
              <li><a href="#">12.944<span>Some label</span></a></li>
              <li><a href="#">22.300<span>Some long label</span></a></li>
              <li><a href="#">11.478<span>Some very long label</span></a></li>
            </ul>
          </div>
         
          <div class="v-tabs-criteria">
            <ul class="left">
              <li>
                <span>Report type:</span>
                <a href="#">${message(code:'reports.type.'+reportType?.reportType)}</a>
              </li>
              <li>
                <span>Report Subtype:</span>
                <a href="#">${message(code:'reports.subType.'+reportSubType?.reportSubType)}</a>
              </li>
              %{-- TODO get rid of this
              <li>
                <span>Ordering:</span>
                <a href="#">by Location</a>
              </li> --}%
              <li>
                <span>Filters:</span>
                <a href="#">There are <a href="#" id='js-filters-toggle' class="tooltip">133</a> filters applied</a>
              </li>
            </ul>
          </div>

          <div class="filters main">
            <form class="filters-box" method="get" action="/memms/equipmentView/filter" style="display: none;">
              <a href="#" class='filters-close' id='js-filters-close'>
                <img src="${resource(dir:'images',file:'icon_close.png')}" />
              </a>
              <ul class="applied-filters-list">
                <li>
                  <h3>Filters category</h3>
                  <ul class="applied-filters">
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                  </ul>
                </li>
                <li>
                  <h3>Filters category</h3>
                  <ul class="applied-filters">
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                  </ul>
                </li>
                <li>
                  <h3>Filters category</h3>
                  <ul class="applied-filters">
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                  </ul>
                </li>
                <li>
                  <h3>Filters category</h3>
                  <ul class="applied-filters">
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                  </ul>
                </li>
                <li>
                  <h3>Filters category</h3>
                  <ul class="applied-filters">
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                  </ul>
                </li>
                <li>
                  <h3>Filters category</h3>
                  <ul class="applied-filters">
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                  </ul>
                </li>

              </ul>
            </form>
          </div>
			<g:render template="/reports/listing/equipmentListing" />
		</div>
        <div id='report-2'>
         	<g:render template="/reports/listing/equipmentListing" />
        </div>
        <div id='report-3'>
          	<g:render template="/reports/listing/equipmentListing" />
        </div>
        <div id='report-4'>
          	<g:render template="/reports/listing/equipmentListing" />
        </div>
        
        <div id='report-5'>
          	<g:render template="/reports/listing/equipmentListing" />
        </div>
        <div id='report-6'>
         	<g:render template="/reports/listing/equipmentListing" />
        </div>   
      </div> 
      
      <!-- end of Inventory -->

      <div id="js-corrective" class="v-tabs-content right">
      <ul class="v-tabs-subnav">
      	<li>
      	<a class="active" href="${createLinkWithTargetURI(controller: 'listingReport', action:'lastMonthWorkOrders')}" id="report-1">
			<g:message code="default.work.order.last.month.label" />
		</a></li>
		<li>
      	<a href="${createLinkWithTargetURI(controller: 'listingReport', action:'workOrdersEscalatedToMMC')}" id="report-2">
			<g:message code="default.work.order.escalated.to.mmc.label" />
		</a></li>
      </ul>

        <div id='report-1' class="js-shown-report">
          <div class="v-tabs-summary">
            <h2>Summary</h2>
            <hr />
            <ul>
              <li><a href="#">32.020<span>Some label</span></a></li>
              <li><a href="#">12.345<span>Some longer label</span></a></li>
              <li><a href="#">75.501<span>Some very long label</span></a></li>
              <li><a href="#">12.944<span>Some label</span></a></li>
              <li><a href="#">22.300<span>Some long label</span></a></li>
              <li><a href="#">11.478<span>Some very long label</span></a></li>
            </ul>
          </div>

          <div class="v-tabs-criteria">
            <ul class="left">
              <li>
                <span>Report type:</span>
                <a href="#">Lorem Ipsum 1234</a>
              </li>
              <li>
                <span>Report subtype:</span>
                <a href="#">Dolor Sit Amet 1234</a>
              </li>
              <li>
                <span>Ordering:</span>
                <a href="#">by Location</a>
              </li>
              <li>
                <span>Filters:</span>
                <a href="#">There are <a href="#" id='js-filters-toggle' class="tooltip" original-title="click to view them all">133</a> filters applied</a>
              </li>
            </ul>
          </div>

          <div class="filters main">
            <form class="filters-box" method="get" action="/memms/equipmentView/filter" style="display: none;">
              <a href="#" class='filters-close' id='js-filters-close'>
                <img src="${resource(dir:'images',file:'icon_close.png')}" />
              </a>
              <ul class="applied-filters-list">
                <li>
                  <h3>Filters category</h3>
                  <ul class="applied-filters">
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                  </ul>
                </li>
                <li>
                  <h3>Filters category</h3>
                  <ul class="applied-filters">
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                  </ul>
                </li>
                <li>
                  <h3>Filters category</h3>
                  <ul class="applied-filters">
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                  </ul>
                </li>
                <li>
                  <h3>Filters category</h3>
                  <ul class="applied-filters">
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                  </ul>
                </li>
                <li>
                  <h3>Filters category</h3>
                  <ul class="applied-filters">
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                  </ul>
                </li>
                <li>
                  <h3>Filters category</h3>
                  <ul class="applied-filters">
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                  </ul>
                </li>
              </ul>
            </form>
          </div>
          	<g:render template="/reports/listing/equipmentListing" />
 			<%--
 		 		<g:render template="/reports/listing/workOrderListing" />
       		--%>  
        </div>
        <div id='report-2'>
         	<g:render template="/reports/listing/equipmentListing" />
          	<%--
         		<g:render template="/reports/listing/workOrderListing" />
       		--%> 
        </div>
        
      </div> <!-- end of Corrective Maintenance -->
      
      <!-- start of Preventive Maintenance -->
      <div id="js-preventive" class="v-tabs-content right">
       <ul class="v-tabs-subnav">
      	<li>
      	<a class="active" href="${createLinkWithTargetURI(controller: 'listingReport', action:'equipmentsWithPreventionPlan')}" id="report-1">
			<g:message code="default.equipments.with.maintenance.plan.label" />
		</a></li>
		<li>
      	<a href="${createLinkWithTargetURI(controller: 'listingReport', action:'preventionsDelayed')}" id="report-2">
			<g:message code="default.work.order.delayed.label" />
		</a></li>
      </ul>

        <div id='report-1' class="js-shown-report">
          <div class="v-tabs-summary">
            <h2>Summary</h2>
            <hr />
            <ul>
              <li><a href="#">32.020<span>Some label</span></a></li>
              <li><a href="#">12.345<span>Some longer label</span></a></li>
              <li><a href="#">75.501<span>Some very long label</span></a></li>
              <li><a href="#">12.944<span>Some label</span></a></li>
              <li><a href="#">22.300<span>Some long label</span></a></li>
              <li><a href="#">11.478<span>Some very long label</span></a></li>
            </ul>
          </div>

          <div class="v-tabs-criteria">
            <ul class="left">
              <li>
                <span>Report type:</span>
                <a href="#">Lorem Ipsum 1234</a>
              </li>
              <li>
                <span>Report subtype:</span>
                <a href="#">Dolor Sit Amet 1234</a>
              </li>
              <li>
                <span>Ordering:</span>
                <a href="#">by Location</a>
              </li>
              <li>
                <span>Filters:</span>
                <a href="#">There are <a href="#" id='js-filters-toggle' class="tooltip" original-title="click to view them all">133</a> filters applied</a>
              </li>
            </ul>
          </div>

          <div class="filters main">
            <form class="filters-box" method="get" action="/memms/equipmentView/filter" style="display: none;">
              <a href="#" class='filters-close' id='js-filters-close'>
                <img src="${resource(dir:'images',file:'icon_close.png')}" />
              </a>
              <ul class="applied-filters-list">
                <li>
                  <h3>Filters category</h3>
                  <ul class="applied-filters">
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                  </ul>
                </li>
                <li>
                  <h3>Filters category</h3>
                  <ul class="applied-filters">
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                  </ul>
                </li>
                <li>
                  <h3>Filters category</h3>
                  <ul class="applied-filters">
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                  </ul>
                </li>
                <li>
                  <h3>Filters category</h3>
                  <ul class="applied-filters">
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                  </ul>
                </li>
                <li>
                  <h3>Filters category</h3>
                  <ul class="applied-filters">
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                  </ul>
                </li>
                <li>
                  <h3>Filters category</h3>
                  <ul class="applied-filters">
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                  </ul>
                </li>
              </ul>
            </form>
          </div>
          	<g:render template="/reports/listing/equipmentListing" />
 			<%--
 		 		<g:render template="/reports/listing/preventiveOrderListing" />
       		--%>  
        </div>
        <div id='report-2'>
         	<g:render template="/reports/listing/equipmentListing" />
          	<%--
         		<g:render template="/reports/listing/preventiveOrderListing" />
       		--%> 
        </div>
      </div> <!-- end of Preventive Maintenance -->

      <div id="js-spare-parts" class="v-tabs-content right">
        <p>
          Spare Parts
        </p>
      </div> <!-- end of Spare parts -->
  </div>
  <!-- End of template -->
</div>
