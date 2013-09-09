$(document).ready(function(){

	// load listing stuff here
	// ...

	// load customized report jqueryui dialog box
	$( "#js-customize-toggle").click(function(e) {
	  	e.preventDefault();
		$( "#dialog-form" ).dialog({ modal: true });
		// $('.ui-dialog').resizable('destroy');
	});

	// load customized report jquery step 1 init
	customizedlisting_init(1);

	// load predefined/saved report list jqueryui horizontal scroll
	var items, scroller = $('#js-slider-wrapper ul');
	var width = 0;
	var item = 0;
	var scrolledWidth = 0;
	scroller.children().each(function(){
	    width += $(this).outerWidth(true);
	});
	scroller.css('width', width);
    hideRestOfElements();

	$(document).on('click', '#js-scroll-left', function(e){
	  e.preventDefault();
	  items = scroller.children();
	  if(item > 0) {
	    item -= 5;
	  }
      hideRestOfElements();
	});

	$(document).on('click', '#js-scroll-right', function(e){
	  e.preventDefault();
	  items = scroller.children();
	  if(item < (items.length - 5)){
	    item += 5;
	  }
      hideRestOfElements();
	});

    function hideRestOfElements(){
      var scrollWidth = 0;
      items = scroller.children('li');
      items.show();
      items.each(function(idx) {
        if(idx < item || idx > item + 4) {
          scrollWidth += $(items[idx]).outerWidth();
          $(items[idx]).hide();
        }
      });
    }

	// load predefined/saved report list jqueryui horizontal scroll left
	function scrollLeft(item){
	  var scrollWidth = 0
	  items.each(function(idx){
            if(scrollWidth < 650 && idx < items.length -1){
              scrollWidth += $(items[idx]).outerWidth();
            }
	  });
	  scrolledWidth += scrollWidth;
	  scroller.animate({'left' : (0 - scrolledWidth) + 'px'}, 'linear');
	}

	// load predefined/saved report list jqueryui horizontal scroll right
	function scrollRight(item){
	  var scrollWidth = 0
	  items.each(function(idx){
            if(scrollWidth < 650 && idx < items.length -1){
              scrollWidth += $(items[idx]).outerWidth();
            }
	  });
	  scrolledWidth -= scrollWidth;
	  scroller.animate({'left' : (0 - scrolledWidth) + 'px'}, 'linear');
	}
});