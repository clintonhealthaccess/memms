
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
				refreshList(data.results[1],".items")
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
 * Add maintenance process
 */
function addProcess(baseUrl,order,spinnerImgSrc,errorMsg){
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
function addExpectedLifeYearMonth(expectedLifeTime){
	if(expectedLifeTime != null){
		$("#expectedLifeTime_years").val( expectedLifeTime >= 12 ? Math.floor( expectedLifeTime/12 ) : null);
		$("#expectedLifeTime_months").val(expectedLifeTime % 12 != 0 ? expectedLifeTime % 12 : null);
	}
}
/**
 * Remove Maintenance Process
 */
function removeProcess(baseUrl){
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
 */
function getToHide(parchaseCost,estimatedCost){
	//supplier==warranty provider
	if($("input[name='warranty.sameAsSupplier']").is(":checked")) $("#address").hide()		
	$("input[name='warranty.sameAsSupplier']").change(function(e){
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
}
