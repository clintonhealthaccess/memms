$(document).ready(function(){
  $('.v-tabs-nested-nav').children('li').children('a').click(function(e) {
    var tabs = $(this).parents('div').children('.v-tabs-nested-nav').children('li').children('a');
    var clicked_tab = $(this);
    var parent_div = $(this).parents('div');
    e.preventDefault();
    tabs.removeClass('active');
    clicked_tab.addClass('active');
    var id = $(this).attr('id');
    parent_div.children('.toggled_tab').hide().removeClass('toggled_tab');
    parent_div.children('div#' + id).addClass('toggled_tab');
    parent_div.children('div#' + id).show();
  });


  $('#top_tabs').children('li').children('a').click(function(e) {
    e.preventDefault();
    var clicked_tab = $(this);
    var tabs = $('#top_tabs');
    tabs.find('.active').removeClass('active');
    clicked_tab.addClass('active');

    var id = $(this).attr('id');
    var parent_div = clicked_tab.parents('div');

    $('div#corrective').hide();
    $('div#preventive').hide();
    $('div#equipment').hide();
    $('div#spare_parts').hide();
    $('div#monitoring').hide();

    $('div#' + id).show();
  });
});
