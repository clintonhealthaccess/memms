function customizedlisting_init(){

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

  // load chosen
  $(".chzn-select").chosen();

  // load jqueryui time picker
  $(function() {
    $( ".js-date-picker" ).datepicker();
  });

  step1_init();
  step2_init();
  step3_init();

}

function step1_init(){
  $('#js-next-step-1').click(function(e) {
    $('#formRemoteStep1Next').submit();
  });
}

function step2_init(){
  $('#js-next-step-2').click(function(e) {
    $('#formRemoteStep2Next').submit();
  });
  $('#js-prev-step-2').click(function(e) {
    $('#formRemoteStep2Prev').submit();
  });

  /* select/deselect all dropdown */

  $('.js-select-all').click(function(e) {
    // var options = $(this).parents('li').find('select').find('option');
    // var select = $(this).parents('li').find('select')
    // if($(this).is(':checked')) {
    //   options.prop('selected', true);
    // }else{
    //   options.prop('selected', false);
    // }
    // select.trigger('liszt:updated');
    
  });
}

function step3_init(){
  $('#js-next-step-3').click(function(e) {
    $('#formRemoteStep3Next').submit();
  });
  $('#js-prev-step-3').click(function(e) {
    $('#formRemoteStep3Prev').submit();
  });
}