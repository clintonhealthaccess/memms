$(document).ready(function(){

	// load dashboard stuff here
	// ...

	// filters show/hide
	$('#js-filters-toggle').click(function(e) {
		e.preventDefault();
		$('.filters-box').slideToggle();
	});
	$('#js-filters-close').click(function(e) {
		e.preventDefault();
		$('.filters-box').slideToggle();
	});

	// if indicator clicked, default to historic trend chart
	$('a.v-tabs-fold-toggle').click(function(event) {
		var selectedIndicatorToggle = $(this);
		var indicatorCode = $(selectedIndicatorToggle).data('indicator-code');
		var selectedIndicatorRow = $('li.v-tabs-row[data-indicator-code="'+indicatorCode+'"]');

		var chartTabs = $(selectedIndicatorRow).children('div').children('ul.v-tabs-nested-nav').children('li');
		var charts = $(selectedIndicatorRow).children('div.v-tabs-fold-container');

		if(selectedIndicatorRow.hasClass('selected')) {
		  //collapse indicator
		  selectedIndicatorRow.removeClass('selected');
		  chartTabs.children('a').removeClass('active');
		  $(charts).children('div.v-chart').hide();
		} 
		else {
		  //expand indicator
		  selectedIndicatorRow.addClass('selected');
		  var selectedChartTab = $(chartTabs).children('a').first();
		  $(chartTabs).children('a').removeClass('active');
		  $(selectedChartTab).addClass('active');
		  var selectedChart = $(charts).children('div.v-chart').first();
		  $(charts).children('div.v-chart').hide();
		  $(selectedChart).show();
		}
		selectedIndicatorRow.children('.v-tabs-fold-container').toggle(500);
	});

	// if indicator chart tab clicked, switch to selected chart
	$('ul.v-tabs-nested-nav').children('li').children('a').click(function(e) {
		var selectedChartTab = $(this);
		var indicatorCode = selectedChartTab.data('indicator-code');
		var selectedIndicatorRow = $('li.v-tabs-row[data-indicator-code="'+indicatorCode+'"]');

		var chartTabs = $(selectedIndicatorRow).children('div').children('ul.v-tabs-nested-nav').children('li');
		var charts = $(selectedIndicatorRow).children('div.v-tabs-fold-container');
		var chartType = selectedChartTab.data('chart-type');

		$(chartTabs).children('a').removeClass('active');
		selectedChartTab.addClass('active');
		var selectedChart = $(charts).children('div.v-chart[data-chart-type="'+chartType+'"]');
		$(charts).children('div.v-chart').hide();
		$(selectedChart).show();
	});

	$('#checkbox-green').change(function() {
		if(this.checked) {
		    $('.v-tabs-row-green').show();
		} else {
		    $('.v-tabs-row-green').hide();
		}
	});

	$('#checkbox-yellow').change(function() {
		if(this.checked) {
		    $('.v-tabs-row-yellow').show();
		} else {
		    $('.v-tabs-row-yellow').hide();
		}
	});

	$('#checkbox-red').change(function() {
		if(this.checked) {
		    $('.v-tabs-row-red').show();
		} else {
		    $('.v-tabs-row-red').hide();
		}
	});

});