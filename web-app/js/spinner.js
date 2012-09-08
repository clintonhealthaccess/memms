$(document).ready(function(){
	$("#spinner").bind("ajaxStart", function() {
		var self = this;
		window.setTimeout(function() {
			if ($.active) $(self).show();
		}, 1500);
	}).bind("ajaxStop", function() {
		$(this).hide();
	}).bind("ajaxError", function() {
		$(this).hide();
	});
});