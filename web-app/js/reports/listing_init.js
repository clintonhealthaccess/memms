$(document).ready(function(){

  // load listing stuff here
  // ...

  // load jqueryui dialog box
  $( "#js-customize-toggle").click(function(e) {
      e.preventDefault();
    $( "#dialog-form" ).dialog({ modal: true });
    $('.ui-dialog').resizable('destroy');
  });

});

// load customized listing init
$(function(){
  customizedlisting_init(1);
});

