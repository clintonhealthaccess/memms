// load jqueryui dialog box
$(document).ready(function(){

  /* customized reports form steps */

  $( "#js-customize-toggle").click(function(e) {
      e.preventDefault();
    $( "#dialog-form" ).dialog({ modal: true });
    $('.ui-dialog').resizable('destroy');
  });

  var step_counter = 1;
  $('#js-step-2, #js-step-3, #js-step-4').hide();

  $('#js-next-step-1, #js-next-step-2, #js-next-step-3, #js-next-step-4').click(function(e) {
    e.preventDefault();
    if(step_counter < 4) {
      $('#js-step-' + step_counter).hide();
      $('#js-step-' + (step_counter + 1)).slideToggle();
      step_counter++;
    }
  });

  $('#js-prev-step-1, #js-prev-step-2, #js-prev-step-3, #js-prev-step-4').click(function(e) {
    e.preventDefault();
    if(step_counter > 1) {
      $('#js-step-' + step_counter).hide();
      $('#js-step-' + (step_counter - 1)).slideToggle();
      step_counter--;
    }
  });

  /* select/deselect all dropdown */

  $('.js-select-all').click(function(e) {
    var options = $(this).parents('li').find('select').find('option');
    var select = $(this).parents('li').find('select')
    if($(this).is(':checked')) {
      options.prop('selected', true);
    }else{
      options.prop('selected', false);
    }
    select.trigger('liszt:updated');
  });

  /* subnavigation */

  $('.v-tabs-subnav a').click(function(e) {
    e.preventDefault();
    $(this).parents('ul').find('.active').removeClass('active');
    $(this).addClass('active');
    var id = $(this).attr('id');
    $(this).parents('div').children('.js-shown-report').hide().removeClass('js-shown-report');
    $(this).parents('div').children('div#' + id).show().addClass('js-shown-report');
  });

  /* filters show/hide */

  $('#js-filters-toggle').click(function(e) {
    e.preventDefault();
    $('.filters-box').slideToggle();
  });

  $('#js-filters-close').click(function(e) {
    e.preventDefault();
    $('.filters-box').slideToggle();
  });

  /* top tabs navigation */
  $('#js-top-tabs a').click(function(e){
    e.preventDefault();
    $(this).parents('ul').find('.active').removeClass('active');
    $(this).addClass('active');
    var id = $(this).attr('id');
    $('div.shown').hide().removeClass('shown');
    $('div#' + id).show().addClass('shown');
  });

});

// load jqueryui time picker
$(function() {
  $( ".js-date-picker" ).datepicker();
});

// load chosen
$(".chzn-select").chosen();

$(function(){
  customizedlisting_init();
});

