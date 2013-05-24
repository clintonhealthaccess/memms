function customizedlisting_init(step){

  // load wizard stuff here
  // ...

  // load step-specific wizard stuff here
  if(step == 1) step1_init();
  if(step == 2) step2_init();
  if(step == 3) step3_init();
  if(step == 4) step4_init();

}

function step1_init(){
  $('#js-next-step-1').click(function(e) {
    $('#formRemoteStep1Next').submit();
  });
}

function step2_init(){

  // load chosen
  $(".chzn-select").chosen();
  // select/deselect all dropdown
  // $('.js-select-all').click(function(e) {
  //   var options = $(this).parents('li').find('select').find('option');
  //   var select = $(this).parents('li').find('select')
  //   if($(this).is(':checked')) {
  //     options.prop('selected', true);
  //   }else{
  //     options.prop('selected', false);
  //   }
  //   select.trigger('liszt:updated');
  // });

  // load jqueryui date time picker
  $(function() {
    $( ".js-date-picker" ).datepicker({ dateFormat: "dd/mm/yy" })
  });

  $('#js-next-step-2').click(function(e) {
    $('#formRemoteStep2Next').submit();
  });
  $('#js-prev-step-2').click(function(e) {
    $('#formRemoteStep2Prev').submit();
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

function step4_init(){
  $('#js-next-step-4').click(function(e) {
    $('#formStep4Next').submit();
  });
  $('#js-prev-step-4').click(function(e) {
    $('#formRemoteStep4Prev').submit();
  });
}
