/**
 * options for cluetip plugin
 */		
var cluetipOptions = {
	ajaxProcess : function(data) {
		return data.html;
	},
	ajaxSettings : {
		dataType : 'json'
	},
	cluetipClass : 'jtip',
	hoverIntent : false,
	dropShadow : false,
	width : '400px',
	clickThrough : true,
	hoverIntent: {
		sensitivity:  1,
		interval:     500,
		timeout:      0
	}
};

$(document).ready(function(){
	/**
	 * cluetip
	 **/
	$('a.cluetip').cluetip(cluetipOptions);
});