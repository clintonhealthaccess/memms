function customizedlisting_init(){

  /* replaced with g:formRemote for "next" */
  /* replaced with g:remoteLink for "previous" */

  /* customized reports form steps */

  // var step_counter = 1;
  // $('#js-step-2, #js-step-3, #js-step-4').hide();

  // $('#js-next-step-1, #js-next-step-2, #js-next-step-3, #js-next-step-4').click(function(e) {
  //   e.preventDefault();
  //   if(step_counter < 4) {
  //     $('#js-step-' + step_counter).hide();
  //     $('#js-step-' + (step_counter + 1)).slideToggle();
  //     step_counter++;
  //   }
  // });

  // $('#js-prev-step-1, #js-prev-step-2, #js-prev-step-3, #js-prev-step-4').click(function(e) {
  //   e.preventDefault();
  //   if(step_counter > 1) {
  //     $('#js-step-' + step_counter).hide();
  //     $('#js-step-' + (step_counter - 1)).slideToggle();
  //     step_counter--;
  //   }
  // });

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

  // load jqueryui time picker
  $(function() {
    $( ".js-date-picker" ).datepicker();
  });

  // load chosen
  $(".chzn-select").chosen();

  step1_init();
  step2_init();
  step3_init();
  step4_init();

}

function step1_init(){
  $('#js-next-step-1').click(function(e) {
    $('#formRemoteStep1').submit();
  });
}

function step2_init(){
  $('#js-next-step-2').click(function(e) {
    $('#formRemoteStep2').submit();
  });
}

function step3_init(){
  $('#js-next-step-3').click(function(e) {
    $('#formRemoteStep3').submit();
  });
}

function step4_init(){
  $('#js-next-step-4').click(function(e) {
    $('#formRemoteStep4').submit();
  });
}