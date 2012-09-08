<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<meta name="layout" content="main" />
		<title><g:message code="import.form.label" default="Data Import" /></title>
		<!-- for admin forms -->
        <r:require modules="form"/>
	</head>
	<body>
		<div class="entity-form-container togglable">
			<div class="entity-form-header">
				<h3 class="title"><g:message code="import.title"/></h3>
				<div class="clear"></div>
			</div>
			<g:form url="[controller:'task', action:'createTaskWithFile']" useToken="true" method="post" enctype="multipart/form-data">
				<input type="hidden" name="entityClass" value="${params.entityClass}" />
				<input type="hidden" name="class" value="EntityImportTask" />
				
				<g:file bean="${taskWithFile}"/>
				
				<div class="row">
					<button type="submit"><g:message code="import.button.import.label"/></button>
				</div>
			</g:form>
		</div>

	</body>
</html>