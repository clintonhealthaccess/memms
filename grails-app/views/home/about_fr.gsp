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
					<g:message code="home.section.about.description.label" default="A propos" />
				</h4>
				<p>
					MEMMS a été développé par <a
						href="http://www.clintonhealthaccess.org">Clinton Health
						Access Initiative</a> en partenariat avec le <a
						href="http://www.moh.gov.rw">Ministère de la Santé du Rwanda</a>.
				</p>
				<br/>
				<p>
					Si vous avez des questions concernant le site, <a
						href="mailto:${grailsApplication.config.site.contact.email}">contactez-nous</a>.
				</p>
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