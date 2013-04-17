$(document).ready(function(){
    $('.v-tabs-fold-toggle').click(function(event) {
      var toggle = $(this);
      var li = toggle.parent().parent();
      if(li.hasClass('selected')) {
        li.children('.v-tabs-fold-container').children().hide();
        li.removeClass('selected');
      } else {
        li.children('.v-tabs-fold-container').children().show();
        li.addClass('selected');
        li.find('div#comparison').hide();
        li.find('div#geo_trend').hide();
        li.find('div#info_facility').hide();
      }
      li.children('.v-tabs-fold-container').toggle(500);
    });
});
