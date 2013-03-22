<div class="heading1-bar">
	<h1>
		<g:message code="preventive.order.calendar.label"/>
 	</h1>
</div>
<div class="filters main projection">
	  <a class="next medium" href="${createLink(uri: targetURI)}">
	    <g:message code="default.link.back.label"/>
	  </a>
	  <p class="question-help">
      <g:message code="preventive.order.mapper.tip.label"/>
    </p>
</div>
<div id="calendar" class="main">
	<a href="${createLink(uri: targetURI)}"></a>
</div>
<script type="text/javascript">
	$(document).ready(function() {
		loadCalendar(${dataLocation.id})
	});
</script>

