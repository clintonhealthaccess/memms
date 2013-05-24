<%@ page import="org.chai.memms.util.Utils.ReportType" %>
<%@ page import="org.chai.memms.util.Utils.ReportSubType" %>
<div class="entity-list">

  <div class="heading1-bar">
    <h1>Detailed Reports</h1>
  </div>

  <!-- filter template -->
  <g:if test="${filterTemplate!=null}">
    <g:render template="/entity/${filterTemplate}" />
  </g:if>

  <!-- start listing template -->
  <div id ="list-grid" class="v-tabs">

    <!-- customized report template -->
    <g:render template="/reports/listing/customizedReport/createCustomizedReport" />

    <!-- report type tabs template -->
    <g:render template="/reports/listing/listingReportTypeTabs" />

    <!-- Inventory -->
    <g:if test="${reportType == ReportType.INVENTORY}">
      <div id='js-inventory' class="v-tabs-content right shown">
          <!-- equipment predefined reports template -->
          <g:render template="/reports/listing/equipmentReport/equipmentPredefinedReports" />
          <!-- TODO equipment saved reports template -->
          <div id='report-1' class="js-shown-report">
            <!-- equipment indicator summary template -->
            <g:render template ="/reports/listing/equipmentReport/equipmentIndicatorSummary" />
            <!-- listing filter summary template -->
            <g:render template ="/reports/listing/listingFilterSummary" />
            <!-- equipment report template -->
            <g:if test="${customizedReportName != null && !customizedReportName.empty}">
              <g:render template="/reports/listing/equipmentReport/customEquipmentListing" />
            </g:if>
            <g:else>
              <g:render template="/reports/listing/equipmentReport/equipmentListing" />
            </g:else>
          </div>
      </div>
    </g:if>

    <!-- Corrective -->
    <g:if test="${reportType == ReportType.CORRECTIVE}">
      <div id='js-corrective' class="v-tabs-content right shown">
        <!-- work order predefined reports template -->
          <g:render template="/reports/listing/workOrderReport/workOrderPredefinedReports" />
          <!-- TODO work order saved reports template -->
          <div id='report-1' class="js-shown-report">
            <!-- work order indicator summary template -->
            <g:render template ="/reports/listing/workOrderReport/workOrderIndicatorSummary" />
            <!-- listing filter summary template -->
            <g:render template ="/reports/listing/listingFilterSummary" />
            <!-- work order report template -->
            <g:if test="${customizedReportName != null && !customizedReportName.empty}">
              <g:render template="/reports/listing/workOrderReport/customWorkOrderListing" />
            </g:if>
            <g:else>
              <g:render template="/reports/listing/workOrderReport/workOrderListing" />
            </g:else>
          </div>
      </div>
    </g:if>

    <!-- Preventive -->
    <g:if test="${reportType == ReportType.PREVENTIVE}">
      <div id='js-preventive' class="v-tabs-content right shown">
        <!-- preventive order predefined reports template -->
          <g:render template="/reports/listing/preventiveOrderReport/preventiveOrderPredefinedReports" />
          <!-- TODO preventive order saved reports template -->
          <div id='report-1' class="js-shown-report">
            <!-- preventive order indicator summary template -->
            <g:render template ="/reports/listing/preventiveOrderReport/preventiveOrderIndicatorSummary" />
            <!-- listing filter summary template -->
            <g:render template ="/reports/listing/listingFilterSummary" />
            <!-- preventive order report template -->
            <g:if test="${customizedReportName != null && !customizedReportName.empty}">
              <g:render template="/reports/listing/preventiveOrderReport/customPreventiveOrderListing" />
            </g:if>
            <g:else>
              <g:render template="/reports/listing/preventiveOrderReport/preventiveOrderListing" />
            </g:else>
          </div>
      </div>
    </g:if>

    <!-- Spare Parts -->
    <g:if test="${reportType == ReportType.SPAREPARTS}">
      <div id='js-spare-parts' class="v-tabs-content right shown">
        <!-- spare parts predefined reports template -->
          <g:render template="/reports/listing/sparePartReport/sparePartPredefinedReports" />
          <!-- TODO spare parts saved reports template -->
          <div id='report-1' class="js-shown-report">
            <!-- spare parts indicator summary template -->
            <g:render template ="/reports/listing/sparePartReport/sparePartIndicatorSummary" />
            <!-- listing filter summary template -->
            <g:render template ="/reports/listing/listingFilterSummary" />
            <!-- spare parts report template -->
            <g:if test="${customizedReportName != null && !customizedReportName.empty}">
              <g:render template="/reports/listing/sparePartReport/customSparePartsListing" />
            </g:if>
            <g:else>
              <g:render template="/reports/listing/sparePartReport/sparePartsListing" />
            </g:else>
          </div>
      </div>
    </g:if>

  </div>
  <!-- end listing template -->
</div>
