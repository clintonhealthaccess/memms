<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<meta name="layout" content="main" />
		<title><g:message code="import.title.equipment.type"/></title>
		<!-- for admin forms -->
		<r:require modules="form"/>
	</head>
	<body>
		<div>
			<div class="heading1-bar">
				<h1 class="title"><g:message code="import.title.equipment.type"/></h1>
			</div>
			<div class="main">
				<g:form url="[controller:'task', action:'createTaskWithFile', params:[targetURI: targetURI]]" useToken="true" method="post" enctype="multipart/form-data">
					<div class="heading1-bar">
						<input type="hidden" name="class" value="EquipmentTypeImportTask">				
						<g:file bean="${taskWithFile}"/>
					</div>
					<br/>
					<div class="row">
						<button type="submit"><g:message code="import.button.import.label"/></button>
					</div>
				</g:form>
			</div>
		</div>
	</body>
</html>