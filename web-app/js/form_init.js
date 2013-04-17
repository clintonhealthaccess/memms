/**
 *  Nice input fields
 */
$(document).delegate('input[type="text"],textarea','focus',function() {
	$(this).removeClass("idle-field completed-field").addClass("focus-field");
    if (this.value == this.defaultValue && this.defaultValue==''){
    	this.value = '';
	}
    if(this.value != this.defaultValue){
    	this.select();
    }
});
$(document).delegate('input[type="text"],textarea','blur',function() {
	$(this).removeClass("focus-field").addClass("idle-field");
	if (this.value != this.defaultValue){
		this.value =$.trim(this.value);
		$(this).removeClass("focus-field").addClass("completed-field");
	}
});

/**
 * language switcher functionality on admin panes
 */
$(document).delegate('.togglable a.toggle-link', 'click', function(){
	var togglable = $(this).parents('.togglable');
	var toggle = $(this).data('toggle')
	var allId = $(this).attr('id');
	$(togglable).find('.toggle-entry').each(function(key, value){
		if(allId)
			$(this).show();
		else{
			if (toggle != $(this).data('toggle'))
				$(this).hide();
			else
			 $(this).show();
	   }
	});
	$(togglable).find('.toggle-link').each(function(key, value){
		if (toggle != $(this).data('toggle')) $(this).removeClass('no-link');
		else $(this).addClass('no-link');
	});
	return false;
});

/**
 * Show/Hide Filters
 */
$(document).ready(function() {
	$('#showhide').click(function(){
  	$('.filters-box').toggle(500);
  });
});
/**
 * Show/Hide Sub-List
 */
$(document).delegate('.toggle-list', 'click', function(e){
	e.preventDefault();
	var nextElement = ($(this).hasClass("type"))?'tr.types:first':'tr.vendors:first'
	$(this).parents('tr').nextAll(nextElement).slideToggle(500);
});