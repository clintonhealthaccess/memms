<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta name="layout" content="main" />
<title><g:message code="about.page.title" /></title>
</head>
<body>
	<div class="main">
		<div class="form-section">
			<div></div>
			<fieldset class="form-content">
				<h4 class="section-title">
					<span class="question-default"> <img
						src="${resource(dir:'images/icons',file:'star_small.png')}">
					</span>
					<g:message code="home.section.about.description.label" default="About" />
				</h4>
				<div>
					<p>
						MEMMS has been developed by the <a
							href="http://www.clintonhealthaccess.org">Clinton Health
							Access Initiative</a> in partnership with the <a
							href="http://www.moh.gov.rw">Ministry of Health of Rwanda</a>.
					</p>
				<br/>
					<p>
						If you have any questions concerning the system, please <a
							href="mailto:${grailsApplication.config.site.contact.email}">contact
							us</a>.
					</p>
				</div>
			</fieldset>
		</div>
		<br/>
		<div class="form-section">
			<div></div>
			<fieldset class="form-content">
				<h4 class="section-title">
					<span class="question-default"> <img
						src="${resource(dir:'images/icons',file:'star_small.png')}">
					</span>
					<g:message code="home.section.about.version.label" default="Version" />
				</h4>
				<p>
						<build:buildInfo />
				</p>
			</fieldset>
		</div>
	</div>
</body>
</html>