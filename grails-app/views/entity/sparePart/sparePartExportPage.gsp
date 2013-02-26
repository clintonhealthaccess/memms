<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta name="layout" content="main" />
<g:set var="entityClass" value="${entityClass}" />
<title><g:message code="sparePart.summary.title" /></title>
<r:require modules="chosen,form, dropdown" />
</head>
<body>
	<div>
		<div class="heading1-bar">
			<h1><g:message code="sparePart.export.label"/></h1>
		</div>
		<g:render template="${template}" />
	</div>
	<script type="text/javascript">
			$(document).ready(function() {
				 clearFormField()
			});
	</script>
</body>
</html>