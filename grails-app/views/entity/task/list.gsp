<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<meta name="layout" content="main" />
	<g:set var="entityName" value="${message(code: code)}" />
	<title><g:message code="default.list.label" args="[entityName]" /></title>
	
	<!-- for admin forms -->
	<r:require modules="richeditor,datepicker,list,progressbar"/>
	
</head>
<body>
	<div class="heading1-bar">
		<span class="right">
			<a href="${createLink(controller: 'task', action: 'purge')}"><g:message code="task.delete.completed"/></a>
		</span>
	</div>

	<g:render template="/templates/genericList" model="[entityName: entityName, template: '/entity/'+template]"/>
	
	<r:script>
		${render(template:'/templates/progressImages')}
	
		$(document).ready(function() {
			
			window.setInterval(function(){
				var data = '';
				$('.js_task-entry').each(function(i, element) {
					if ($(element).find('.js_task-status').html() != 'COMPLETED') data += '&ids='+$(element).data('id');
				});
				
				if (data != '') {
	  				$.ajax('${createLink(controller: 'task', action: 'progress')}', {
						data: data,
						success: function(data, textStatus, jqXHR) {
							$(data.tasks).each(function(i, element) {
								var entry = $('#js_task-'+element.id);
								$(entry).find('.js_progress-bar').progressBar(element.progress * 100,{
									steps: 0,
									boxImage: progress['boxImage'],
									barImage: {
										0:  progress['barImage_0'],
										30:  progress['barImage_30'],
										70:  progress['barImage_70'],
									}
								});
								$(entry).find('.js_task-status').html(element.status);
								if (element.status == 'IN_PROGRESS') $(entry).find('.js_progress-bar').show();
							});
						}
					});
				}
			}, 10000);
		});
	</r:script>
</body>
</html>