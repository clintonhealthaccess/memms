$(document).ready(function() {
	progressBar();
});

function progressBar() {
	$(".js_progress-bar").each(function(){
		var values = $(this).html().split('/');
		
		var value;
		if (values.length == 2) {
			if(values[1] == 0) value = 0;
			else value = (values[0]/values[1])*100;
		}
		else {
			value = $(this).html() * 100;
		}
		$(this).progressBar(value, {
			steps: 0,
			boxImage: progress['boxImage'],
			barImage: {
				0:  progress['barImage_0'],
				30:  progress['barImage_30'],
				70:  progress['barImage_70'],
			}
		});
	});
}