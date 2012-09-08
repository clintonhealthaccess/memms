/**
 * explanation functionality
 */
function explanationClick(element, prefix, onSuccess){
	if ($(element).find('a').size() == 0) return;
	
	if ($(element).parents('tr').hasClass('me-open')) {
		toClose = true
	}
	else {
		toClose = false
	}
	$('.me-open').removeClass('me-open');
	
	if (toClose) {
		slideUp(element, function(){});
	}
	else {
		if ($('#explanation-'+prefix).hasClass('loaded')) {
			slideUpExplanation(element, function(){slideDown(prefix);});
		}
		else {
			$.ajax(
			{
				type:'GET', url: $(element).find('a').attr('href'),
				success: function(data) {
					slideUpExplanation(element, function() {
						addData(prefix, data);
						onSuccess(prefix);
					});
				}
			});
		}
		$(element).parents('tr').addClass('me-open');
	}
}

function slideUpExplanation(element, callback) {
	if ($(element).parents('table').first().find('.explanation-cell.visible').length == 0) {
		callback();
	}
	else {
		slideUp(element, callback);
	}
}

function slideUp(element, callback) {
	$(element).parents('table').first().find('.explanation-cell.visible').slideUp("slow", function() {callback();});
	$(element).parents('table').first().find('.explanation-cell.visible').removeClass('visible');
}

function slideDown(prefix) {
	$('#explanation-'+prefix).addClass('visible');
	$('#explanation-'+prefix).slideDown('slow');
}

function addData(prefix, data) {
	$('#explanation-'+prefix).html(data);
	$('#explanation-'+prefix).addClass('loaded');
	
	slideDown(prefix);
}
// END of explanation functionality

/**
 * data element explanations
 */
$(document).delegate('.data-element', 'mouseenter mouseleave', function() {
	var data = $(this).data('id');
	
	$(this).parents('.info').find('.data-'+data).toggleClass('highlighted');
	$(this).toggleClass('highlighted');
});

$(document).delegate('.data-element', 'click', function(){
	if (!$(this).hasClass('selected')) {
		$(this).parents('.info').find('.data-element').removeClass('selected');
		$(this).parents('.info').find('.data').removeClass('selected');
	}
	var data = $(this).data('id');
	$(this).parents('.info').find('.data-'+data).toggleClass('selected');
	$(this).toggleClass('selected');
});