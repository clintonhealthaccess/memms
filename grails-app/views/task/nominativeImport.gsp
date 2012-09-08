<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<meta name="layout" content="main" />
		<title><g:message code="import.title"/></title>
		<!-- for admin forms -->
        <r:require modules="form,chosen"/>
	</head>
	<body>
		<div class="entity-form-container togglable">
			<div class="entity-form-header">
				<h3 class="title"><g:message code="import.title"/></h3>
				<div class="clear"></div>
			</div>
			<g:form url="[controller:'task', action:'createTaskWithFile']" useToken="true" method="post" enctype="multipart/form-data">
				<input type="hidden" name="class" value="NominativeImportTask">
			
				<g:selectFromList name="rawDataElementId" label="${message(code:'dataelement.label')}" bean="${task}" field="rawDataElementId" optionKey="id" multiple="false"
					ajaxLink="${createLink(controller:'data', action:'getAjaxData', params:[class:'DataElement'])}"
					from="${dataElements}" value="${task?.rawDataElementId}" values="${dataElements.collect{i18n(field:it.names)+' ['+it.code+'] ['+it.class.simpleName+']'}}" />
				
				<g:selectFromList name="periodId" label="${message(code:'period.label')}" bean="${task}" field="periodId" optionKey="id" multiple="false"
					from="${periods}" value="${task?.periodId}" optionValue="startDate" />
					
				<g:file bean="${taskWithFile}"/>
				
				<div class="row">
					<button type="submit"><g:message code="import.button.import.label"/></button>
				</div>
				
			</g:form>
		</div>
	</body>
</html>