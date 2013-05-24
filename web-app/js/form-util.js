/**
 * Load Calendar
 */

function loadCalendar(dataLocation){
	$("#calendar").fullCalendar({
	        events: "projection.json?dataLocation.id="+dataLocation,
	        header: { left: 'prev,next today',center: 'title',right: 'month,agendaWeek,agendaDay'}
	    });
}

/**
 * List ajax init
 */
function listGridAjaxInit(){
	$(".spinner-container").hide();
	listGridAjax();
	searchFormAjax();
	filterFormAjax();
	clearFormField();
}

/**
 * Loading list with Ajax
 */
function listGridAjax() {
    $("#list-grid").find(".paginateButtons a, th.sortable a").live('click', function(event) {
        event.preventDefault();
        $("div.spinner-container").show();
        var url = $(this).attr('href');
        $.ajax({
            type: 'GET',
            url: url,
            success: function(data) {
            	addListAjaxResponse(data)
            },
        	error : function(jqXHR, exception, errorThrown){listLoadingFailed(jqXHR, exception, errorThrown)}
        });
    });
}

/**
 * Loading search results list with Ajax
 */
function searchFormAjax(){
	 $(".heading1-bar form").submit(function(event) {
		 	event.preventDefault();
		 	$(".spinner-container").show();
		    var url = $(this).parents(".heading1-bar").find("form").attr("action");
		    var dataLocation = $(this).parents(".heading1-bar").find("input[name=dataLocation]").attr("value");
		    var equipment = $(this).parents(".heading1-bar").find("input[name=equipment]").attr("value");
		    var type = $(this).parents(".heading1-bar").find("input[name=type]").attr("value");
		    var status = $(this).parents(".heading1-bar").find("input[name=status]").attr("value");
		    var term = $(this).parents(".heading1-bar").find("input[name=q]").attr("value");
	        $.ajax({
	            type: 'GET',
	            url: url,
	            data: {"dataLocation.id":dataLocation,"equipment.id":equipment,"q":term,"type.id":type,"status":status},
	            success: function(data) {
	            	addListAjaxResponse(data)
	            },
	            error : function(jqXHR, exception, errorThrown){listLoadingFailed(jqXHR, exception, errorThrown)}
	        })
	});
}
/**
 * Loading filtered list with Ajax
 */
function filterFormAjax() {
    $("div.filters form").submit(function(event) {
	    event.preventDefault();
	    var filterBox = $(this).parents("div.filters");
	    var grid = $(filterBox).nextAll("div.list-template");
	    $(".spinner-container").show()
	    var url = $(filterBox).find("form").attr("action");
	    var data = $(filterBox).find("form").serialize();
	    $.ajax({
	        type: 'GET',
	        url: url,
	        data: data,
	        success: function(data) {
	        	addListAjaxResponse(data)
	        },
	    	error : function(jqXHR, exception, errorThrown){listLoadingFailed(jqXHR, exception, errorThrown)}
	     });
    });
}

function showClutips(){
	$('a.clueTip').cluetip({
		  arrows: true,
		  dropShadow: false,
		  hoverIntent: false,
		  sticky: true,
		  mouseOutClose: true,
		  closePosition: 'title'
		});
}
/**
 * Clear form content
 */
function clearFormField(){
	$(".clear-form").live('click', function(event){
		event.preventDefault();
		var form = $(this).parents("form");
		$(form)[0].reset();
		//For chosen plugin fields
		$(form).find(".chzn-done").val('').trigger("liszt:updated");
	});
}
/**
 * Add list html to div holder
 * @param data
 */
function addListAjaxResponse(data){
	$("div.spinner-container").hide();
    $("div.list-template").fadeOut(300, function() {
    	$(this).html(data.results[0]).show(600);
    });
}
/**
 * Handle ajax list loading error
 */
function listLoadingFailed(jqXHR, exception, errorThrown){
	$("div.spinner-container").hide();
    $("div.list-template").fadeOut(100, function() {
    	var error = "";
    if (jqXHR.status === 0) {
    	error = 'Not connect.\n Verify Network.'
      } else if (jqXHR.status == 404) {
    	  error = 'Requested page not found. [404]';
      } else if (jqXHR.status == 500) {
    	  error = 'Internal Server Error [500].';
      } else if (exception === 'parsererror') {
    	  error = 'Requested JSON parse failed.';
      } else if (exception === 'timeout') {
    	  error = 'Time out error.';
      } else if (exception === 'abort') {
    	  error = 'Ajax request aborted.';
      } else {
    	  error = 'Uncaught Error.\n' + errorThrown;
      }
    	$(this).html("<span class='ajax-error'> "+ error +"</span>").show();
    });
}

/**
 * Make an input field to accept only number
 */
function numberOnlyField(){
	$(".numbers-only").keyup(function () {
	    if (this.value != this.value.replace(/[^0-9\.]/g, '')) {
	       this.value = this.value.replace(/[^0-9\.]/g, '');
	    }
	});
}

/**
 * Date picker
 */
function getDatePicker(iconPath){
	$(function(){
		$(".date-picker").datepicker({
			changeYear: true,
			dateFormat: "dd/mm/yy",
			showOn: "both",
			buttonImage: iconPath,
			buttonImageOnly: true
		});
		$('.date-time-picker').datetimepicker({
			changeYear: true,
			dateFormat: "dd/mm/yy",
			showOn: "both",
			buttonImage: iconPath,
			buttonImageOnly: true,
			timeFormat: "HH:mm:ss",
			addSliderAccess: true,
			sliderAccessArgs: { touchonly: false }
		});
		$('.time-picker').timepicker({
			timeFormat: "HH:mm:ss",
			showSecond: true,
			addSliderAccess: true,
			sliderAccessArgs: { touchonly: false }
		});
	});
}

/**
 * Form-aside loader
 */
function getHtml(htmls,field){
	var html=""
	$.each(htmls, function (i, val) {
		html=html+" "+val.html
	});
	var attrId= "#form-aside-"+field
	$(attrId+" .form-aside-hidden").remove();
	$(attrId).append(html);
	$(".form-aside-hidden").hide();
}

/**
 * Escalate WorkOrder
 */
function escaletWorkOrder(baseUrl){
	$(".ajax-spinner").hide();
	$("tr").on("click",".escalate",function(e){
		e.preventDefault();
		$(this).nextAll(".ajax-spinner").show();
		$.ajax({
			type :'GET',dataType: 'json',
			data:{"order":event.target.id},
			url:baseUrl,
			success: function(data) {
				$(".spinner-container").show()
				addListAjaxResponse(data)
			}
		});
		$(this).ajaxError(function(){
			$(this).nextAll(".ajax-error").show();
			$(this).nextAll(".ajax-spinner").hide();
			$(this).fadeIn("slow");
		});
	})
}
/**
 * Edit donation and obsolete
 */
function updateEquipment(baseUrl){
	$(".ajax-spinner").hide();
	$(".ajax-error").hide()
	$(".list-check-box").change(function(event){
		$(event.target).hide();
	    $(event.target).prev().show();
	    $(event.target).next().hide();
	    var state= event.target.checked
		$.ajax({
			type :'GET',dataType: 'json',data:{"equipment.id":event.target.id,"field":event.target.name},url: baseUrl,
			success: function(data) {
				$(event.target).prev().fadeOut("slow");
				$(event.target).fadeIn("slow");
			}
		});
		$(event.target).ajaxError(function(){
			if(state) $(event.target).attr('checked',false);
			else $(event.target).attr('checked',true);
			$(event.target).prev().fadeOut("slow");
			$(event.target).fadeIn("slow");
			$(event.target).next().show("slow");

		});
	})
}

/**
 * Add work order maintenance process
 */
function addWorkOrderProcess(baseUrl,order,errorMsg){
	$(".ajax-spinner").hide();
	$(".ajax-error").hide()
	$('.process').on("click",".add-process-button",function(e){
		e.preventDefault();
		$(this).hide();
		$(this).nextAll(".ajax-spinner").show();
		$.ajax({
			type :'GET',
			dataType: 'json',
			data:{"order.id":order,"type":$(this).prevAll(".idle-field").attr('name'),"value":$(this).prevAll(".idle-field").attr('value')},
			url:baseUrl,
			success: function(data) {
				$(e.target).nextAll(".ajax-error").hide();
				$(e.target).nextAll(".ajax-spinner").hide();
				$(e.target).prevAll(".idle-field").val("")
				$(e.target).fadeIn("slow");
				refreshList(data.results[1],'.process-list-'+data.results[2])
			}
		});
		$(this).ajaxError(function(){
			$(this).nextAll(".ajax-error").show();
			$(this).nextAll(".ajax-spinner").hide();
			$(this).fadeIn("slow");
		});
	})
}
/**
* Add Expected Life Year Month
*/
function addExpectedLifeYearMonth(expectedLifeTime){
	if(expectedLifeTime != null){
		$("#expectedLifeTime_years").val( expectedLifeTime >= 12 ? Math.floor( expectedLifeTime/12 ) : null);
		$("#expectedLifeTime_months").val(expectedLifeTime % 12 != 0 ? expectedLifeTime % 12 : null);
	}
}
/**
 * Remove Work Order Maintenance Process
 */
function removeWorkOrderProcess(baseUrl){
	$(".ajax-spinner").hide();
	$(".ajax-error").hide();
	$('.process').on("click","a.delete-process",function(e){
		e.preventDefault();
		$(this).hide();
		$(this).nextAll(".ajax-spinner").show();
		$.ajax({
			type :'GET',dataType: 'json',data:{"process.id":$(this).attr("name")},url:baseUrl,
			success: function(data) {
				$(e.target).nextAll(".ajax-error").hide();
				$(e.target).nextAll(".ajax-spinner").hide();
				$(e.target).fadeIn("slow");
				refreshList(data.results[1],'.process-list-'+data.results[2])
			}
		});

		$(this).ajaxError(function(){
			$(this).nextAll(".ajax-error").show();
			$(this).nextAll(".ajax-spinner").hide();
			$(this).fadeIn("slow");
		});

	});
}

/**
 * Add Comment to a workOrder
 */
function addComment(baseUrl,order){
	$(".comment-section").on("click","#add-comment",function(e){
		e.preventDefault();
		$(this).hide();
		$(this).nextAll(".ajax-spinner").show();
		$.ajax({
			type :'GET',dataType: 'json',
			data:{"order.id":order,"content":$("#comment-content").val()},
			url:baseUrl,
			success: function(data) {
				$(e.target).nextAll(".ajax-error").hide();
				$(e.target).nextAll(".ajax-spinner").hide();
				$(e.target).fadeIn("slow");
				$("#comment-content").val("")
				refreshList(data.results[1],'.comment-list')
			}
		});
		$(this).ajaxError(function(){
			$(this).nextAll(".ajax-error").show();
			$(this).nextAll(".ajax-spinner").hide();
			$(this).fadeIn("slow");
		});
	})
}

/**
 * Delete a comment from workOrder
 */
function removeComment(baseUrl){
	$(".comment-section").on("click","a.delete-comment",function(e){
		e.preventDefault();
		$(this).hide();
		$(this).prevAll(".ajax-spinner").show();
		$.ajax({
			type :'GET',dataType: 'json',data:{"comment.id":$(this).attr("id")},
			url:baseUrl,
			success: function(data) {
				$(e.target).prevAll(".ajax-error").hide();
				$(e.target).prevAll(".ajax-spinner").hide();
				$(e.target).fadeIn("slow");
				refreshList(data.results[1],'.comment-list')
			}
		});
		$(this).ajaxError(function(){
			$(this).prevAll(".ajax-error").show();
			$(this).prevAll(".ajax-spinner").hide();
			$(this).fadeIn("slow");
		});
	})
}
/**
 * Replace (refresh) any list with the provided class and hide ajax-spinner
 */
function refreshList(html,cssClass){
	$(cssClass).slideUp().replaceWith(html).slideDown();
	$(".ajax-spinner").hide();
	$(".ajax-error").hide();
}

/**
 * Hide set of fields if an option is selected (purchaser==donor - supplier==warranty provider)
 * This method has to be refactored to re-use code (I (Jean) will fix it)
 */
function getToHide(parchaseCost,estimatedCost){
	//supplier==warranty provider
	if($("input[name='warranty.sameAsSupplier']").is(":checked")) $("#address").hide()
	$("input[name='warranty.sameAsSupplier']").change(function(e){
		if($(this).is(":checked")) $("#address").slideUp()
		else $("#address").slideDown()
	})
	if($("input[name='sameAsManufacturer']").is(":checked")) $("#address").hide()
	$("input[name='sameAsManufacturer']").change(function(e){
		if($(this).is(":checked")) $("#address").slideUp()
		else $("#address").slideDown()
	})

	//purchaser==donor
	if($("select[name=purchaser]").val()!="BYDONOR") $(".donor-information").hide()
	else{
		$("label[for=purchaseCost]").html(estimatedCost)
	}
	$("select[name=purchaser]").change(function(e){
		if($(this).val()=="BYDONOR"){
			$("label[for=purchaseCost]").html(estimatedCost)
			$(".donor-information").slideDown()
		}else{
			$("label[for=purchaseCost]").html(parchaseCost)
			$(".donor-information").slideUp()
		}
	})
	//purchaser==partner
	if($("select[name=sparePartPurchasedBy]").val()!="BYPARTNER") $(".partner-information").hide()
	else{
		$("label[for=sparePartPurchasedBy]").html(estimatedCost)
	}
	$("select[name=sparePartPurchasedBy]").change(function(e){
		if($(this).val()=="BYPARTNER"){
			$("label[for=purchaseCost]").html(estimatedCost)
			$(".partner-information").slideDown()
		}else{
			$("label[for=purchaseCost]").html(parchaseCost)
			$(".partner-information").slideUp()
		}
	})
	//dataLocation
	if($("select[name=stockLocation]").val()!="FACILITY") $(".facility-information").hide()
	$("select[name=stockLocation]").change(function(e){
		if($(this).val()=="FACILITY"){
			$(".facility-information").slideDown()
		}else{
			$(".facility-information").slideUp()
		}
	})
	//Spare Part Status
	if($("select[name=statusOfSparePart]").val()!="OPERATIONAL") $(".equipment-information").hide()
	$("select[name=statusOfSparePart]").change(function(e){
		if($(this).val()=="OPERATIONAL"){
			$(".equipment-information").slideDown()
			$(".form-section-to-hide").slideUp()
		}else{
			$(".equipment-information").slideUp()
			$(".form-section-to-hide").slideDown()
		}
	})
	//user
	if($("select[name=preventionResponsible]").val()!="HCTECHNICIAN") $(".prevention-responsible").hide()
	$("select[name=preventionResponsible]").change(function(e){
		if($(this).val()=="HCTECHNICIAN"){
			$(".prevention-responsible").slideDown()
		}else{
			$(".prevention-responsible").slideUp()
		}
	})

	$("select[name=occurency]").change(function(e){
		if($(this).val()=="DAYS_OF_WEEK"){
			$(".week-days").slideDown()
			$(".occur-interval").slideUp()
		}else{
			$(".week-days").slideUp()
			$(".occur-interval").slideDown()
		}
		$("select[name=occurInterval]").nextAll("label.has-helper").html($(this).find("option:selected").text());
		$("input[name=occurInterval]").nextAll("label.has-helper").html($(this).find("option:selected").text());
	})
}
/**
 * Add Prevention maintenance process
 */
function addPreventionProcess(baseUrl,prevention,errorMsg){
	$(".ajax-spinner").hide();
	$(".ajax-error").hide()
	$('.process').on("click",".add-process-button",function(e){
		e.preventDefault();
		$(this).hide();
		$(this).nextAll(".ajax-spinner").show();
		$.ajax({
			type :'GET',
			dataType: 'json',
			data:{"prevention.id":prevention,"value":$(this).prevAll(".idle-field").attr('value')},
			url:baseUrl,
			success: function(data) {
				$(e.target).nextAll(".ajax-error").hide();
				$(e.target).nextAll(".ajax-spinner").hide();
				$(e.target).prevAll(".idle-field").val("")
				$(e.target).fadeIn("slow");
				refreshList(data.results[1],'.process-list-'+data.results[2])
			}
		});
		$(this).ajaxError(function(){
			$(this).nextAll(".ajax-error").show();
			$(this).nextAll(".ajax-spinner").hide();
			$(this).fadeIn("slow");
		});
	})
}
/**
 * Remove Prevention Maintenance Process
 */
function removePreventionProcess(baseUrl){
	$(".ajax-spinner").hide();
	$(".ajax-error").hide();
	$('.process').on("click","a.delete-process",function(e){
		e.preventDefault();
		$(this).hide();
		$(this).nextAll(".ajax-spinner").show();
		$.ajax({
			type :'GET',dataType: 'json',data:{"process.id":$(this).attr("name")},url:baseUrl,
			success: function(data) {
				$(e.target).nextAll(".ajax-error").hide();
				$(e.target).nextAll(".ajax-spinner").hide();
				$(e.target).fadeIn("slow");
				refreshList(data.results[1],'.process-list-'+data.results[2])
			}
		});

		$(this).ajaxError(function(){
			$(this).nextAll(".ajax-error").show();
			$(this).nextAll(".ajax-spinner").hide();
			$(this).fadeIn("slow");
		});

	});
}

