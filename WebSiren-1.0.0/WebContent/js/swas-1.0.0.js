/*
 * This file is part of WebSiren.
 *
 *  WebSiren is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.

 *  WebSiren is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with WebSiren.  If not, see <http://www.gnu.org/licenses/>.
 */

//this global is used to store previously mouseover rule div ID
var preHoverID = "";
//a global variable for storing checked rules values
var checkedRules = [];
//a global variable for storing checked headers values
var checkedHeaders = [];
// a global varible for counting condition being initiated by user 
var condNo = 0;
//a global varible for storing previously selected group ID
var preGroupSelID = "";
//a global varible for storing previously selected group ID
var conditions = [];

var parseErrorID ="";
/*
 * on document ready
 */
$(function() {
	console.log("app is ready ... ");
	$("#rule-loading").hide();
	$("#facet-loading").hide();	
	
	loadFacets();
	loadRuleList();
	onToolTip("#menu-bar-options ul li a[title]");
	onAddRuleOverlay();
	onFileUploadOverlay();
	if($("#addHttpProfileOverlay").length > 0){
		httpProfileOverlay();
	}
	//for j scroll pane specific
	$('.jspDrag').hide();
	$('.jspScrollable').mouseenter(function() {
		$(this).find('.jspDrag').stop(true, true).fadeIn('slow');
	});
	$('.jspScrollable').mouseleave(function() {
		$(this).find('.jspDrag').stop(true, true).fadeOut('slow');
	});
	
	/* sliding menu start */
    $('img.menu_class').click(function () {
    	$('ul.the_menu').slideToggle('medium');
        });
	getMSState();
	/* sliding menu end */
	
});


/*
 *  loads a single rule into the right bar.
 */
function loadRule(ruleID) {

	var data = "ruleID=" + ruleID;
	$('#right-bar').load(
			'rule.jsp?' + data,
			function(resp, status, xhr) {

				if (status == "error")
					console.log("An error occurred: " + xhr.status + " - "
							+ xhr.statusText);

				if (status == "success") {

					$(function() {

						$("#accordion").accordion();
						// setup ul.tabs to work as tabs for each div directly under div.panes
						$("ul.css-tabs").tabs("div.css-panes > div");
						/*$("ul.css-tabs").tabs();*/
					});
				}

			});
}

/*
 *  loads a group into the right bar.
 */
function loadGroup(groupName) {

	var data = "groupName=" + groupName;
	$('#right-bar').load(
			'group.jsp?' + data,
			function(resp, status, xhr) {

				if (status == "error")
					console.log("An error occurred: " + xhr.status + " - "
							+ xhr.statusText);

			});
}


/*
 *  loads a list of rule into the content body.
 */
function loadRuleList() {

	$("#rule-loading").show();
	$('#main').html("");
	var data = "?userID=" + $("#userID").val();
	$('#main').load(
			"getAllRules" + data,
			function(resp, status, xhr) {

				if (status == "error") {

					console.log("An error occurred: " + xhr.status + " - "
							+ xhr.statusText);

				} else if (status == "success") {
					console.log("rule loaded sucessfully..");
					$(function() {
						$("#rule-loading").hide();
						onListMouseOver();
						onRuleChecked();
						onAllRuleChecked();
						checkGeneralizationOverlay();
						$(".list-link").hide();
						onEditRuleOverlay();
						onListPagination("rule");
					});

				}

			});
}

/*
 *  This function loads facets into UI
 */
function loadFacets() {

	$("#facet-loading").fadeIn("slow");
	$('#left-bar').load(
			"facets.jsp",
			function(resp, status, xhr) {

				if (status == "error") {

					console.log("An error occurred: " + xhr.status + " - "
							+ xhr.statusText);

				} else if (status == "success") {

					$(function() {
						$("#facet-loading").hide();
						onMultipleAccordion();
						onUncheckAllFacets();
					});

				}

			});
}

/*
 *  This function loads rule policies into UI
 */
function loadGroupList() {

	$('#main').load(
			"ruleGroupList.jsp",
			function(resp, status, xhr) {

				if (status == "error") {

					console.log("An error occurred: " + xhr.status + " - "
							+ xhr.statusText);

				} else if (status == "success") {

					console.log("Group List Successfully loaded");
					$(function() {
						$('#right-bar').html("");
						addGroupOverlay();
						onToolTip("#content-menu ul li a[title]");
						onListPagination("rule");
						$(".list-link").hide();
						onListMouseOver();
						onGroupChecked();
						editGroupOverlay();
					});

				}

			});
}

/*
 *  This function loads rule policies into UI according to the keywords
 */
function loadByKeywords() {
	
	var data = "keywords=" + $("input:text").val() + "&userID=" + $("#userID").val();
	
	console.log(data);
	
	$('#main').load(
			"searchByKeywords?", data, function(data, status, xhr) {

				if (status == "error") {

					console.log("An error occurred: " + xhr.status + " - "
							+ xhr.statusText);

				} else if (status == "success") {

					console.log("rule loaded sucessfully..");
					$(function() {
						$('#right-bar').html("");
						$("#rule-loading").hide();
						onListMouseOver();
						onRuleChecked();
						onAllRuleChecked();
						$(".rule-list-link").hide();
						onToolTip("#content-head ul li a[title]");
						onEditRuleOverlay();
						checkGeneralizationOverlay();
						onListPagination("rule");
					});

				}

			}); 

}

/*
 *  Complementary function of Key Pressed search for loadByKeywords function
 */
$("#search").keyup(function(event){
    if(event.keyCode == 13){
        $("#searchRule").click();
    }
});

/*
 *  loads a list of rule who implement the same group into the content body.
 *  @param groupName
 */
function getRuleByGroup(groupName) {

	$("#rule-loading").show();
	var userID = $("#userID").val();
	var data = "?groupName=" + groupName + "&userID=" + userID ;
	$("#main").load(
			"searchByGroup" + data,
			function(resp, status, xhr) {

				if (status == "error") {

					console.log("An error occurred: " + xhr.status + " - "
							+ xhr.statusText);
					notifyAlert("Server not responding");
					$("#rule-loading").hide();
					$(".rule-list-link").hide();

				} else if (status == "success") {

					$(function() {
						$('#right-bar').html("");
						$("#rule-loading").hide();
						onListMouseOver();
						onRuleChecked();
						onAllRuleChecked();
						$(".rule-list-link").hide();
						onEditRuleOverlay();
						checkGeneralizationOverlay();
						onListPagination("rule");
					});

				}

			});
}

/*
 * this function will be called whenever an event of mouseover
 * is triggered for a particular rule
 */
function onListMouseOver() {

	$('.rule').mouseover(function() {

		var ruleDiv = $(this);
		console.log("Mouse Over a rule :" + ruleDiv.attr('id'));

		if (preHoverID != "") {
			$("#link-" + preHoverID).hide();
		}
		preHoverID = ruleDiv.attr('id');
		$("#link-" + preHoverID).show();
		onToolTip("a#link-" + preHoverID + "[title]");
	});

}

/*
 * this function will check or check all the rules 
 */
function onAllRuleChecked() {

	console.log("onAllRuleChecked ..");

	$('#allChkBox').click(function() {

		var checked = $(this);
		checkedRules = [];

		if (checked.is(':checked')) {
			$('.ruleChkBox').each(function(index, value) {
				
				console.log("rule ids checked:" + $(this).parent().parent().css("display"));
				if(	$(this).parent().parent().css("display").trim() == "block" ){
					
					console.log("rule ids checked" + $(this).parent().parent().css("display").trim() );
					$(this).attr("checked", true);
					$(this).parent().parent().css("background", "#F0F0F0");
					checkedRules.push($(this).val());
				
				}
				

			});

		} else {

			$('.ruleChkBox').each(function(index, value) {

				console.log("rule ids checked: " + value);
				if(	$(this).parent().parent().css("display").trim() == "block" ){
					
					console.log("rule ids unchecked" + $(this).parent().parent().css("display").trim() );
					$(this).attr("checked", false);
					checkedRules = jQuery.grep(checkedRules, function(value) {
						return value != $(this).val();
					});
					$(this).parent().parent().css("background", "transparent");
				}
			});

		}

	});

}
/*
 * this function will handle events of rule checkboxes 
 */
function onRuleChecked() {

	$('.ruleChkBox').click(function() {

		var checked = $(this);
		if (checked.is(':checked')) {
			checkedRules.push(checked.val());
			$("#" + checked.val()).css("background", "#F0F0F0");

		} else {

			checkedRules = jQuery.grep(checkedRules, function(value) {
				return value != checked.val();
			});
			$("#" + checked.val()).css("background", "transparent");

		}

		$(checkedRules).each(function(index, value) {
			console.log("rule ids checked: " + value);
		});

	});

}
/*
 * this function will handle events of group radio buttons 
 */
function onGroupChecked() {
	
	$(".groupRadioButton").click(function() {

		var checked = $(this);
		console.log("radio button clicked : " + checked.val());
		$(".groupRadioButton").each(function(index, value) {
			$(this).parent().parent().css("background", "transparent");
		});
		if (checked.is(':checked')) {
			groupName = checked.val();
			$("#" + checked.val()).css("background", "#F0F0F0");

		}
		
	});
}
/*
 * this function will handle events of group radio buttons 
 */
function onUncheckAllFacets() {
	
	$("#uncheckFacets").click(function() {

		console.log("uncheckfacets called clicked : " );
		
		$("a.selectedFacet").each(function(index, value) {
			$(this).removeClass("selectedFacet");
		});
		
	});
}
/*
 * this function will handle events of http request headers checkboxes 
 */
function onHeaderChecked() {

	$('.headerChkBox').click(function() {
		
		console.log("in header checked");
		

		var checked = $(this);
		if (checked.is(':checked')) {
			checkedHeaders.push(checked.val());
			$("#" + checked.val()).css("background", "#F0F0F0");

		} else {

			checkedHeaders = jQuery.grep(checkedHeaders, function(value) {
				return value != checked.val();
			});
			$("#" + checked.val()).css("background", "transparent");

		}

		/*$(checkedHeaders).each(function(index, value) {
			

		});*/
		
	});

}

function generateRuleFromResource(){
	console.log("in generateRuleFromResource()");
/*	if (checkedRules.length < 1) {

		notifyAlert("select atleast 1 rule first");
		return;

	}*/
	var resource = $("input#node").val();
	
	var values = "";
	$(checkedHeaders).each(function(index, value) {
		
		if (index == checkedRules.length - 1) {
			values += value;
		} else {
			values += value + ",";
		}

	});
	values = values + resource;
	console.log("Values =" + values);
	$('#right-bar').load(
			'addResourceRule.jsp?resourceHeader=' + values,
			function(resp, status, xhr) {

				if (status == "error")
					console.log("An error occurred: " + xhr.status + " - "
							+ xhr.statusText);

				if (status == "success") {
					$('.headerChkBox').each(function(index, value) {

						console.log("headers checked: " + value);
						$(this).attr("checked", false);
						$(this).parent().parent().css("background", "transparent");

					});
				checkedHeaders =[];
				addRuleFromResource();
					
				}

			});
	
}

function addRuleFromResource(){
	$("#addResourceRule").submit(
			function() {
				console.log("Submit Called ... ");
				var resource = $("input#node").val();
				var formData = $(this).formSerialize();
				formData += "&userID=" + $("#userID").val();
				console.log("&resource=" + $("input#node").val());
				formData += "&resource=" + $("input#node").val();
				
				$.post("addResourceRule", formData, function(data, status, xhr) {
					if (status == "error") {

						console.log("An error occurred: " + xhr.status + " - "
								+ xhr.statusText);

					} else if (status == "success") {

						console.log("Form successfully posted :" + data);
						if (data.status == "0") {

							noty({
								layout : 'center',
								theme : 'noty_theme_blue',
								type : 'success',
								timeout : '2000',
								closeButton : 'true',
								text : 'Rule Stored Successfully'
							});
							console.log("Resource : " + resource);
							resourceRuleList(resource);
//							$('#overlay a.close').trigger('click');
//							loadRuleList();
//							loadFacets();
//							onListPagination("rule");
							condNo = 0;

						} 
						else if (data.status == "2") 
						{
							window.location("loginform.jsp");
						}
						else {

							var msg = "";
							$(data.message).each(function(index, value){
								msg += value + "</br>";
							});
							
							noty({
								layout : 'center',
								theme : 'noty_theme_blue',
								type : 'error',
								timeout : '4000',
								text : msg
							});

						}
					}
				});

				return false;
			});

}
/*
 *  This function will send delete resource rule request to the server
 *  and receives response in json form
 */
function delResourceRules(ruleid, resource) {
	
	//var resource = $("input#node").val();
	console.log("delResourceRulesRequest");
	console.log("Rule ID : " + ruleid);
	$('#right-bar').html("");

	
	data = {
			"ruleID" : ruleid + ","
		};
	//consol.log("data:"+ data);
	$.post("deleteSemRule", data, function(data, status, xhr) {

		if (status == "error") {

			console.log("An error occurred: " + xhr.status + " - "
					+ xhr.statusText);

		} else if (status == "success") {
			console.log(" Status :" + data.status + "\t Message : "
					+ data.message);

			if (data.status == "0") {

				noty({
					layout : 'top',
					theme : 'noty_theme_blue',
					type : 'success',
					timeout : '2000',
					text : 'Rules Removed Successfully'
				});

				console.log("Resource : " + resource);
				resourceRuleList(resource);

			} 
			else if (data.status == "2")
			{
				window.location("loginform.jsp");
			}
			else
			{
				notifyAlert(data.message);
				}
			
		}
	});

}

/*
 *  This function will send delete rule request to the server
 *  and receives response in json form
 */
function delRulesRequest() {

	console.log("delRulesRequest .... :");
	$('#right-bar').html("");
	if (checkedRules.length < 1) {

		notifyAlert("select atleast 1 rule first");
		return;

	}

	var values = "", data;
	$(checkedRules).each(function(index, value) {
		if (index == checkedRules.length - 1) {
			values += value;
		} else {
			values += value + ",";
		}
	});
	data = {
		"ruleID" : values
	};

	$.post("deleteSemRule", data, function(data, status, xhr) {

		if (status == "error") {

			console.log("An error occurred: " + xhr.status + " - "
					+ xhr.statusText);

		} else if (status == "success") {
			console.log(" Status :" + data.status + "\t Message : "
					+ data.message);

			if (data.status == "0") {

				noty({
					layout : 'top',
					theme : 'noty_theme_blue',
					type : 'success',
					timeout : '2000',
					text : 'Rules Removed Successfully'
				});

				loadRuleList();
				onListPagination("rule");
				loadFacets();
				$('.ruleChkBox').each(function(index, value) {

					console.log("rule ids checked: " + value);
					$(this).attr("checked", false);
					$(this).parent().parent().css("background", "transparent");

				});
				$('#allChkBox').attr("checked", false);
				checkedRules = [];

			} 
			else if (data.status == "2")
			{
				window.location.replace("loginform.jsp");
			}
			else
			{
				notifyAlert(data.message);
				}
			
		}
	});

}

/*
 * 
 */
function translateResourceSemRuleRequest(){

		var values = "", data;
		var resource = $("#resource").val();
		console.log("Resource :" + resource);
		data = "resource=" + resource;
		$.download('translateResourceSemRule', data);
		
}

/*
 * This function is for translating semantic rule to modsecurity rule
 */
function translateSemRuleRequest() {

	console.log("translateSemRuleRequest called .... :");
	if (checkedRules.length < 1) {

		noty({ 
			layout : 'center',
			theme : 'noty_theme_blue',
			type : 'alert',
			timeout : '2000',
			text : 'select atleast 1 rule first'
		});
		return;
	}

	var values = "", data;
	$(checkedRules).each(function(index, value) {
		if (index == checkedRules.length - 1) {
			values += value;
		} else {
			values += value + ",";
		}
	});

	data = "ruleID=" + values;
	$.download('translateSemRule', data);
	
	$('.ruleChkBox').each(function(index, value) {

		console.log("rule ids checked: " + value);
		$(this).attr("checked", false);
		$(this).parent().parent().css("background", "transparent");

	});
	$('#allChkBox').attr("checked", false);
	checkedRules = [];

}


/*
 * This function is for translating group of semantic rules to modsecurity rule
 */

function translateGroupRequest(group) {

	console.log("translateGroupRequest called .... :");

	var data = "groupName=" + group;
	$.download('translateRuleGroup', data);
}

/*
 * This function is for translating and
 * deploying group of semantic rules to modsecurity firewall
 */
function deployGroupRequest(group){
	
	console.log("deployRuleGroup called ...  : " + group);
	var data = { "groupName": group }; 
	$.post("deployRuleGroup", data, function(data, status, xhr) {

		if (status == "error") {

			console.log("An error occurred: " + xhr.status + " - "
					+ xhr.statusText);

		} else if (status == "success") {
			console.log(" Status :" + data.status + "\t Message : "
					+ data.message);
			

			if (data.status == "0"){
				notifyAlert(data.message);
			}
			else if (data.status == "2")
				{
					window.location("loginform.jsp");
				}
			else{
				notifyAlert(data.message);
			}
			
		}
	});
	
}
/*
 * This function is for translating and
 * deploying resource rules to modsecurity firewall
 */
function deployResourceRulesRequest(){
	var values = "", data;
	var resource = $("#resource").val();
	console.log("Resource :" + resource);
	data = "resource=" + resource;
	
	$.post("deployResourceRules", data, function(data, status, xhr) {

		if (status == "error") {

			console.log("An error occurred: " + xhr.status + " - "
					+ xhr.statusText);

		} else if (status == "success") {
			console.log(" Status :" + data.status + "\t Message : "
					+ data.message);
			

			if (data.status == "0"){
				
				notifyAlert(data.message);
				
			} 
			else if (data.status == "2") 
			{
				window.location("loginform.jsp");
			}
			else{
				notifyAlert(data.message);
			}
			
		}
	});
}
/*
 * This function is for translating and
 * deploying selected semantic rules to modsecurity firewall
 */
function deploySelectedRulesRequest(){
	
	console.log("deploySelectedRulesRequest called ...  : ");
	if (checkedRules.length < 1) {

		noty({
			layout : 'center',
			theme : 'noty_theme_blue',
			type : 'alert',
			timeout : '2000',
			text : 'select atleast 1 rule first'
		});
		return;
	}

	var values = "";
	$(checkedRules).each(function(index, value) {
		if (index == checkedRules.length - 1) {
			values += value;
		} else {
			values += value + ",";
		}
	});

	var data = { "ruleID" : values }; 
	$.post("deploySelectedRules", data, function(data, status, xhr) {

		if (status == "error") {

			console.log("An error occurred: " + xhr.status + " - "
					+ xhr.statusText);

		} else if (status == "success") {
			console.log(" Status :" + data.status + "\t Message : "
					+ data.message);
			

			if (data.status == "0"){
				
				notifyAlert(data.message);
				$('.ruleChkBox').each(function(index, value) {

					console.log("rule ids checked: " + value);
					$(this).attr("checked", false);
					$(this).parent().parent().css("background", "transparent");

				});
				$('#allChkBox').attr("checked", false);
				checkedRules = [];
				
			} 
			else if (data.status == "2") 
			{
				window.location("loginform.jsp");
			}
			else{
				notifyAlert(data.message);
			}
			
		}
	});
	
}
/*
 * this function is for rule translation
 */
function translateModSecRules() {
	console.log("translate Mod Sec Rule Called .... :");
	
	$("#fileloader").attr("action", "parseRule?userID=" + $("#userID").val());
	$('#fileloader')
			.ajaxForm(
					{

						uploadProgress : function(event, position, total,
								percentComplete) {
							$("#errorMsg").html('Translating.....').fadeIn(1000);
							console.log(" Upload progress");
						},

						complete : function(xhr, status, data) {

							var json = $.parseJSON(xhr.responseText);
							console.log("Json object  : " + json.status + " : "
									+ json.message);
							if (status == "error") {
								console.log("An error occurred: " + xhr.status
										+ " - " + xhr.statusText);
							} else if (status == "success") {
								
								$("#progressBar").hide();
								if (json.status == "0") {
									
									$("#errorMsg").html('Translation Successful').fadeIn(1000);
									console.log("success block");
									noty({
										layout : 'center',
										theme : 'noty_theme_blue',
										type : 'success',
										timeout : '2000',
										text : 'Rules translated Successfully'
									});
									
									if(json.noOfErrors != "0"){
										
										parseErrorID = json.parseErrorID;
										$("#errorMsg").html("errors("+json.noOfErrors+") while parsing the rule file. please click on view logs to check logs").fadeIn(1000);
										$("#file").append("<input type=\"button\" id = \"viewLog\" class = \"submitButton\" value = \"View Log\">");
										viewErrorLog();
										
									} else{

										$('#fileOverlay a.close').trigger('click');
										
									}
									$('#right-bar').html("");
									loadRuleList();
									loadFacets();
									onListPagination("rule");

								} else if (json.status == "2"){
									
									window.location("loginform.jsp");
									
								}
								else
									{
									notifyAlert("modsecurity rule translation un-successfull");
									}

							}
						
						}
					});
	
	return false;
	
}

/*
 * This function is for 
 */
function viewErrorLog() {
	
	$("#viewLog").click(function() {
		console.log("Error String" + parseErrorID);
		if(parseErrorID!=""){
			
			data = "parseErrorID=" + parseErrorID;
			$.download('parseError', data);
			$('#fileOverlay a.close').trigger('click');
		
		} else{
			console.log("internal error");
		}
		
	});

}

/*
 * this function is for deleting rule group
 */
function delGroupRequest(groupName) {

	console.log("delRuleGroup called..:" + groupName);

	data = { "groupName" : groupName };
	$.post("deleteRuleGroup", data, function(data, status, xhr) {

		if (status == "error") {

			console.log("An error occurred: " + xhr.status + " - "
					+ xhr.statusText);

		} else if (status == "success") {

			console.log(" Status :" + data.status + "\t Message : "
					+ data.message);
			if (data.status == "0") {

				noty({
					layout : 'top',
					theme : 'noty_theme_blue',
					type : 'success',
					timeout : '2000',
					text : 'Rules Removed Successfully'
				});
				loadGroupList();
				loadFacets();
				onListPagination("rule");

			}
			else if (data.status == "2")
				{
				window.location("loginform.jsp");
				}
			else{
				notifyAlert(data.message);
			}
		}
	});

}
/*
 * this function is for edit group overlay 
 */
function editGroupOverlay() {

	console.log("editGroupOverlay called..");
	$(".group-title").overlay({

		mask : 'black',
		onBeforeLoad : function() {

			var file = this.getOverlay().find(".addRuleGroup");
			file.load(this.getTrigger().attr("href"));

		},
		onLoad : function() {
			$(function() {
				
				editGroup();
			});
		},

		closeOnClick : false

	});
}

/* 
 * this function is for addGroup overlay
 */

function addGroupOverlay() {
	
	console.log("addGroupOverlay called..");
	$("#addGroup").overlay({
		mask : 'black',
		onBeforeLoad : function() {
			
			console.log("before addGroupOverlay triggered");
			var file = this.getOverlay().find(".addRuleGroup");
			// load the page specified in the trigger
			file.load(this.getTrigger().attr("href"));

		},
		onLoad : function() {
			$(function() {
				addGroup();
			});
		},

		closeOnClick : false

	});
}

function httpProfileOverlay(){
	
	$("#addHttpProfileOverlay").overlay({
		mask : 'black',
		onBeforeLoad : function() {
			
			console.log("httpprofile overlay");
			var file = this.getOverlay().find(".profileResourceTree");
			file.load(this.getTrigger().attr("href"));

		},
		onLoad : function() {

			$(function() {
				
				translateHttpPolicy();
				
			});

		},

		closeOnClick : false
	});
	
}


/*
 * this function is for checking generalization
 */
function checkGeneralizationOverlay() {

	console.log("checkGeneralizationOverlay called..");

	$("#checkGeneralization").overlay({

		mask : 'black',
		onBeforeLoad : function() {

			console.log("Selected Rule length :" + checkedRules.length);
			if(checkedRules.length > 1){
				
				notifyAlert("select only one rule for checking generalization");
				$('#fileOverlay a.close').trigger('click');
				
			}  else if(checkedRules.length < 1){
				
				notifyAlert("select atleast one rule for checking generalization");
				$('#fileOverlay a.close').trigger('click');
				
			} else{
				
				var ruleID = checkedRules[0];
				var file = this.getOverlay().find(".generalizationDiv");
				var URL = this.getTrigger().attr("href") + "?ruleID=" + ruleID;
				console.log("URL :" + URL);
				file.load(URL);
				
			}

		},
		onLoad : function() {
			$(function() {
				console.log("overlay loaded...");
			});
		},

		closeOnClick : false

	});
	
}

function translateHttpPolicy(){
console.log("translate dataSet Called .... :");
	
	$("#httpPolicyLoader").attr("action", "addHttpPolicy?userID=" + $("#userID").val());
	$('#httpPolicyLoader')
			.ajaxForm(
					{

						uploadProgress : function(event, position, total,
								percentComplete) {
							$("#errorMsg").html('Translating.....').fadeIn(1000);
							console.log(" Upload progress");
						},

						complete : function(xhr, status, data) {

							var json = $.parseJSON(xhr.responseText);
							console.log("Json object  : " + json.status + " : "
									+ json.message);
							if (status == "error") {
								console.log("An error occurred: " + xhr.status
										+ " - " + xhr.statusText);
							} else if (status == "success") {
								
								$("#progressBar").hide();
								if (json.status == "0") {
									
									$("#errorMsg").html('Translation Successful').fadeIn(1000);
									console.log("success block");
									noty({
										layout : 'center',
										theme : 'noty_theme_blue',
										type : 'success',
										timeout : '2000',
										text : 'Http Policy translated Successfully'
									});
									
									$('#fileOverlay a.close').trigger('click');
									$('#right-bar').html("");
									loadHTTPProfile();

								} else if (json.status == "2"){
									
									window.location("loginform.jsp");
									
								}
								else
									{
									notifyAlert("Http Policy translation unsuccesful");
									}

							}
						
						}
					});
	
	return false;
}


/*
 * this function is for jscrollpane on different elements of the page
 */
function onScrollPane(element) {
	$(element).jScrollPane({
		horizontalGutter : 5,
		verticalGutter : 5,
		'showArrows' : false
	});
}
/*
 * This function is for jquery tools tooltips of page elements 
 * @param elementpath for example "#somediv img[title]"
 */
function onToolTip(elementPath) {

	$(elementPath).tooltip({
		effect : 'fade',
		fadeOutSpeed : 100,
		predelay : 400,
		position : "bottom right",
		offset : [ -50, -80 ]
	});
}

/*
 * This function is for Add Rule Overlay
 */
function onAddRuleOverlay() {

	$("#ruleOverlay").overlay(
			{

				mask : 'black',
				onClose: function(){
		            condNo = 0;
		            loadFacets();
		        },
				onBeforeLoad : function() {
					console.log("On rule creation overlay"
							+ this.getTrigger().attr("href"));
					var wrap = this.getOverlay().find(".contentWrap");
					// load the page specified in the trigger
					wrap.load(this.getTrigger().attr("href"));
				},

				onLoad : function() {
					console.log("onLoad called...");
					$(function() {
						console.log("form is ready...");
						onFormTabs();
					});
				}

			});

}
/*
 * This function is for edit rule overlay 
 */
function onEditRuleOverlay() {

	console.log(" onEditRuleOverlay called... ");
	$(".rule-title").overlay(
			{

				mask : 'black',
				onClose: function(){
		            condNo = 0;
		            loadFacets();
		        },
				onBeforeLoad : function() {
					console.log("On rule creation overlay"
							+ this.getTrigger().attr("rel"));
					var wrap = this.getOverlay().find(".contentWrap");
					// load the page specified in the trigger
					wrap.load(this.getTrigger().attr("href"));
				},

				onLoad : function() {
					console.log("onLoad called...");
					$(function() {
						console.log("form is ready...");
						onFormTabs();
						$("#allConditions").show();
						if($(".condition").length > 0){
							condNo = $(".condition").length;
						}
					});
				}

			});

}

/*
 * an overlay for file upload 
 */
function onFileUploadOverlay() {

	// if the function argument is given to overlay,
	// it is assumed to be the onBeforeLoad event listener    
	$("#ruleFileOverlay").overlay({
		mask : 'black',
		onBeforeLoad : function() {

			var file = this.getOverlay().find(".fileUpload");
			file.load(this.getTrigger().attr("href"));

		},
		onLoad : function() {

			$(function() {
				
				translateModSecRules();
			});

		},

		closeOnClick : false

	});
}
/*
 * Below function will provide the scriptbreaker multiple accordion 
 */
function onMultipleAccordion() {

	$(".topnav").multiAccordion({
		accordion : true,
		speed : 500,
		closedSign : '[+]',
		openedSign : '[-]'
	});

}

/*
 * This function for adding new group
 */
function addGroup() {
	console.log("add group called");

	$("#newGroup").submit(
			function() {

				console.log("group submit called");

				var formData = $(this).formSerialize();
				formData += "&userID=" + $("#userID").val();
				var title = $("#groupTitle");
				var description = $("#groupDescription");

				if (title.val() == "") {
					notifyAlert("Enter group name");
				} 
				else if (description.val() == "") {
					notifyAlert("Enter description");
				} 
				else {
					$.post("addRuleGroup", formData,
							function(data, status, xhr) {
								if (status == "error") {

									console.log("An error occurred: "
											+ xhr.status + " - "
											+ xhr.statusText);

								} else if (status == "success") {
									
									if (data.status == "0") {

										notifyAlert('Group Stored Successfully');
										$('#groupOverlay a.close').trigger('click');
										
										loadFacets();
										loadGroupList();
										onListPagination("rule");

									} else if (data.status == "2")
										{
										window.location("loginform.jsp");
										}
									else {

										notifyAlert(data.message);

									}
								}
							});
				}

				return false;

			});

}
/*
 * this function is for adding rule group in 
 * rule form
 */
function addRuleFormGroup(){
	
	$(function(){
		
		console.log("addRuleFormGroup called");
		var group = $("#ruleGroup").val();
		var userID = $("#userID").val();
		var formData={ "name" : group, "userID" : userID };
		
		if( group == "" ){
			notifyAlert("group name is required");
			return;
		}
		
		$.post("addRuleFormGroup", formData, function(data, status, xhr) {
				if (status == "error") {
					console.log("An error occurred: " + xhr.status + " - "+ xhr.statusText);
				} else if (status == "success") {
					
					if (data.status == "0") {
						
						var options = "<label for=\"ruleGroup\">Group:</label>" +
							 "<select id=\"ruleGroup\" name=\"ruleGroup\"> " + 
							 "<option value=\"\"> None </option>";

						$(data.groups).each(function(index, value) {
							if(data.selected == value){
								options += "<option value=\"" + value + "\" selected>" + value + "</option>";
							} else{
								options += "<option value=\"" + value + "\">" + value + "</option>";
							}
						});
						
						options += "</select>" +
							"<a href=\"#\" id=\"addGroupLink\" class=\"ruleFormGroupLink\" title=\"Add Rule Group\" onclick=\"addGroupField(this)\" >" +
							"<img alt=\"add group\" src=\"images/icons/add2.png\"> </a>";
						
						$("#ruleGroup").parent().html(options);

					} else if (data.status == "2")
					{
						window.location("loginform.jsp");
					}
					else {

						notifyAlert(data.message);

					}
				}
		});
		
	});
	
}
/*
 * this funtion is for edit group
 */
function editGroup() {

	console.log("group edit called");
	$("#editRuleGroup").submit(
			function() {
				console.log("group update called");
				var formData = $(this).formSerialize();
				formData += "&userID=" + $("#userID").val();
				console.log("group form data : " + formData);
				var title = $("#groupTitle");
				var description = $("#groupDescription");

				if (title.val() == "") {
					notifyAlert("Enter group name");
				} 
				else if (description.val() == "") {
					notifyAlert("Enter description");
				} 
				else {
				
				$.post("editRuleGroup", formData, function(data, status, xhr) {
					if (status == "error") {

						console.log("An error occurred: " + xhr.status + " - "
								+ xhr.statusText);

					} else if (status == "success") {

						console.log("Form successfully posted :" + data);
						if (data.status == "0") {

							notifyAlert('Group Stored Successfully');
							$('#groupOverlay a.close').trigger('click');
							
							loadGroupList();

						} 
						else if(data.status == "2")
						{
							window.location("loginform.jsp");
						}
						else {

							notifyAlert(data.message);
						}
					}
				});
				}
				return false;

			});

}
/*
 * This function is for adding group field to add or edit
 * rule form
 */
function addGroupField(link){
	
	console.log("addGroupField called: " + $(link));
	
	var options = "<label for=\"ruleGroup\">Group:</label>" +
			"<input id=\"ruleGroup\" name=\"ruleGroup\" /> " +
			"<a href=\"#\" id=\"addGroupLink\" class=\"ruleFormGroupLink\" title=\"Add Rule Group\" onclick=\"addRuleFormGroup()\"> Save</a>";
	$(link).parent().html(options);
	
	
}
/*
 * This function is for sliding forms tabs
 */
function onFormTabs() {

	console.log("onFormTabs called");
	$("ul.form-tabs").tabs("div.step", {
		history : true,
		event : "none",
		onBeforeClick : function(event, index) {
			console.log("Tab event called" + index + " : " + event);
		}

	});

	var api = $("ul.form-tabs").data("tabs");
	// "next tab" button
	$("a.nextTab").click(function() {

		console.log(" Next clicked : " + $(this).attr("id"));

		if ($(this).attr("id") == "metadata-next") {

			console.log("after metadata-next");
			if (validateMDFields()) {
				api.next();
			}

		} else if ($(this).attr("id") == "action-next") {

			console.log("after action-next :" + $("input[name='action']:checked").val());
			if(!$("input[name='action']:checked").val()){
				notifyAlert("select a disruptive action first!");
			} else{
				review();
				api.next();	
			}

		} else if($(this).attr("id") == "condition-next"){
			
			if($("#ruleType").val() == "ChainRule" && $(".condition").length < 2){
				notifyAlert("chain rule must have more than 1 condition");
			} else if($("#ruleType").val() == "SimpleRule" && $(".condition").length > 1){
				notifyAlert("simple rule must have less than 1 condition");
			} else{
				api.next();
			}
			
		} else {
			api.next();
		}

	});

	// "previous tab" button
	$("a.prevTab").click(function() {
		console.log(" Previous clicked : " + api);
		api.prev();
	});

	$('#ruleTags').tagit();// Show test data
	$("#conditionTransformations").multiselect();
	$("#allConditions").hide();

	$("#action").dynatree({
		classNames : {
			container : "action-container",
			checkbox : "dynatree-radio"
		},
		selectMode : 3,
		onSelect : function(select, node) {
			var selKeys = $.map(node.tree.getSelectedNodes(), function(node) {
				return node.data.key;
			});
			$("#selectedAction").val(selKeys.join(","));
			var selRootNodes = node.tree.getSelectedNodes(true);
			var selRootKeys = $.map(selRootNodes, function(node) {
				return node.data.key;
			});
		}
	});

	/*
	 * for submiting add rule form
	 */
	$("#addRule").submit(
			function() {
				console.log("Submit Called ... ");
				var formData = $(this).formSerialize();
				formData += "&userID=" + $("#userID").val();
				
				$.post("addSemRule", formData, function(data, status, xhr) {
					if (status == "error") {

						console.log("An error occurred: " + xhr.status + " - "
								+ xhr.statusText);

					} else if (status == "success") {

						console.log("Form successfully posted :" + data);
						if (data.status == "0") {

							noty({
								layout : 'center',
								theme : 'noty_theme_blue',
								type : 'success',
								timeout : '2000',
								closeButton : 'true',
								text : 'Rule Stored Successfully'
							});

							$('#overlay a.close').trigger('click');
							loadRuleList();
							loadFacets();
							onListPagination("rule");
							condNo = 0;

						} 
						else if (data.status == "2") 
						{
							window.location("loginform.jsp");
						}
						else {

							var msg = "";
							$(data.message).each(function(index, value){
								msg += value + "</br>";
							});
							
							noty({
								layout : 'center',
								theme : 'noty_theme_blue',
								type : 'error',
								timeout : '4000',
								text : msg
							});

						}
					}
				});

				return false;
			});

	/*
	 * for submiting edit rule form
	 */
	$("#editRule").submit(
			function() {
				console.log("Submit Called ... ");
				var formData = $(this).formSerialize();
				formData += "&userID=" + $("#userID").val();
				
				$.post("editSemRule", formData, function(data, status, xhr) {
					if (status == "error") {

						console.log("An error occurred: " + xhr.status + " - "
								+ xhr.statusText);

					} else if (status == "success") {

						console.log("Form successfully posted :" + data);
						if (data.status == "0") {

							noty({
								layout : 'center',
								theme : 'noty_theme_blue',
								type : 'success',
								timeout : '2000',
								closeButton : 'true',
								text : 'Rule Stored Successfully'
							});

							$('#overlay a.close').trigger('click');
							loadRuleList();
							loadFacets();
							onListPagination("rule");
							condNo = 0;

						} 
						else if (data.status == "2") 
						{
							window.location("loginform.jsp");
						}
						else {

							var msg = "";
							$(data.message).each(function(index, value){
								msg += value + "</br></br>";
							});
							
							noty({
								layout : 'center',
								theme : 'noty_theme_blue',
								type : 'error',
								timeout : '4000',
								text : msg
							});

						}
					}
				});

				return false;
			});

}
/*
 * This function will set the conditions fields
 */
function saveCondition(editCondNo) {

	console.log("saveCondition called :" + editCondNo);
	if (!validateCondFields()) {
		return;
	}

	var expression = "";
	$(".expression").each(function(index, item) {  
		
		var children=$(item).children("select");
		$(children).each(function( index2, value) {
			
			if( index2 == ($(children).length - 1) ){
				expression += $(value).attr("name")+":"+$(value).val();
			} else{
				expression += $(value).attr("name")+":"+$(value).val() + "|";	
			}

		});

		if (!(index == ($(".expression").length - 1))) {
			expression += ",";
		}

	});


	var transVarName = "";
	$("input[name=transVarName]").each(function(index, item) {
		if (index == ($("input[name=transVarName]").length - 1)) {
			transVarName += $(item).val();
		} else {
			transVarName += $(item).val() + ",";
		}
	});
	var transVarValue = "";
	$("input[name=transVarValue]").each(function(index, item) {
		if (index == ($("input[name=transVarValue]").length - 1)) {
			transVarValue += $(item).val();
		} else {
			transVarValue += $(item).val() + ",";
		}

	});
	
	
	if(editCondNo != undefined){
		
		$("#cse"+editCondNo).val(expression);
		$("#co"+editCondNo).val($("#conditionOperator").val());
		$("#cv"+editCondNo).val($("#conditionValue").val());
		$("#ct"+editCondNo).val($("#conditionTransformations").val());
		if(transVarName != ""){
			$("#ctn"+editCondNo).val(transVarName);
			$("#ctv"+editCondNo).val(transVarValue);
		}
		
	} else{
		
		if(condNo == 1 && $("#ruleType").val() == "SimpleRule"){
			notifyAlert("simple rule cannot have more than 1 condition");
		} else{
			var options = "<span id=\"cno"+ condNo+ "\" class=\"condition\">" +
							"<p><a href=\"#\"  class=\"showCondition\" onClick=\"showCondition("+condNo+")\">View Condition "+ (condNo+1) +"</a>" +
								"<a href=\"#\" class=\"closeCondition\"  onClick=\"deleteCondition("+condNo+")\" > X </a></p>" +
							"<input type=\"hidden\" name=\"selectedExpressions\" id=\"cse" + condNo +"\" value=\""+expression+"\" />" +
							"<input type=\"hidden\" name=\"selectedOperators\" id=\"co" + condNo +"\" value=\""+$("#conditionOperator").val()+"\" style=\"display :none;\" />" +
							"<input type=\"hidden\" name=\"selectedConditionValues\" id=\"cv" + condNo +"\" value=\"" + $("#conditionValue").val() + "\" style=\"display :none;\" />" +
							"<input type=\"hidden\" name=\"selectedTransformations\" id=\"ct" + condNo +"\" value=\"" + $("#conditionTransformations").val() + "\" style=\"display :none;\" />"+
							"<input type=\"hidden\" name=\"selectedTransVarName\" id=\"ctn" + condNo +"\" value=\"" + transVarName + "\" style=\"display :none;\" />" +
							"<input type=\"hidden\" name=\"selectedTransVarValue\" id=\"ctv" + condNo +"\" value=\"" + transVarValue + "\" style=\"display :none;\" />"+								
							"</span>";	
			
			console.log("Options : " + options);
			$("#allConditions").append(options);
			$("#allConditions").show();
			condNo++;
		}
	}
	
	var data = "<p class=\"expression\">"+
					"<input type=\"radio\" name=\"expGroup\" class = \"expGroup\" value=\"VariableExpression\" onclick=\"onExpSelect(this)\" /> Variable Expression " +
					"<input type=\"radio\" name=\"expGroup\" class = \"expGroup\" value=\"CollectionExpression\" onclick=\"onExpSelect(this)\" /> Collection Expression " +
					"<a href=\"#\" class=\"closeExp\" onclick=\"closeExpression(this)\"> X </a> " + 
				"</p>";
	
	$("#allExpressions").html(data);
	$("#conditionOperator").val("");
	$("#conditionValue").val("");
	$("#conditionTransformations").val("");
	$("#conditionTransformations").multiselect("uncheckAll");
	$("#allSetVars").html("");
	$("#saveCondButton").attr("onclick","saveCondition()");

	$("input[name=selectedExpressions]").each(function(index, item) {
		console.log(" Selected Expressions : " + $(item).val());
	});

}

function review() {
	
	var options = "";
	
	options += "<label style=\"margin-left: 5px;\" class=\"reviewPane\">  Meta-data : </label> </br>";
	options += "<label style=\"margin-left: 15px;\" class=\"reviewPane\"> Title : </label>  " + $("#ruleTitle").val() + "</br>";
	options += "<label style=\"margin-left: 15px;\" class=\"reviewPane\"> Type : </label>  "+ $("#ruleType").val() +" , ";
	options += "<label style=\"margin-left: 15px;\" class=\"reviewPane\"> Group : </label>  "+ $("#ruleGroup").val() + " </br>";
	options += "<label style=\"margin-left: 15px;\" class=\"reviewPane\"> Severity : </label>  "+ $("#ruleSeverity").val() +",";
	options += "<label style=\"margin-left: 15px;\" class=\"reviewPane\"> Revision : </label>  "+ $("#ruleRevision").val() +" ,</br>";
	options += "<label style=\"margin-left: 15px;\" class=\"reviewPane\"> Tags : </label>  "+ $("#ruleTags").val() +"</br>";
	options += "<label style=\"margin-left: 15px;\" class=\"reviewPane\"> Message : </label>  "+ $("#ruleMessage").val() +"</br>";
	options += "<label style=\"margin-left: 15px;\" class=\"reviewPane\"> Description : </label>  "+ $("#ruleDescription").val() +"</br>";
	options += "<label style=\"margin-left: 15px;\" class=\"reviewPane\"> Phase : </label>  "+ $("#rulePhase").val() +"</br>";
	
	options += "<label style=\"margin-left: 5px;\" class=\"reviewPane\">  Conditions :</label> </br>";
	var count = 0; 
	$("#allConditions > span").each(function(index, item){
		
		options += "<label style=\"margin-left: 5px;\" class=\"reviewPane\">  Condition "+ count +":</label> </br>";
		var exps = $("#cse" + count).val().split(",");
		var target = "";
		
		$(exps).each(function(index, exp){
			
			if(exp.split("|")[0].split(":")[1] == "negation"){
				target += "!";
			} else if(exp.split("|")[0].split(":")[1] == "ampersand"){
				target += "&";
			}
			
			if(exp.split("|").length == 3){
				target += exp.split("|")[1].split(":")[1];
				target += ":" + exp.split("|")[2].split(":")[1];
			} else{
				target += exp.split("|")[1].split(":")[1];
			}
			
			if(!(index == exps.length - 1)){
				target += "|";
			}
			
			
		});
		
		options += "<label style=\"margin-left: 15px;\" class=\"reviewPane\"> Target : </label>  "+ target +"</br>";
		options += "<label style=\"margin-left: 15px;\" class=\"reviewPane\"> Operator : </label>  "+ $("#co" + count).val() +"</br>";
		options += "<label style=\"margin-left: 15px;\" class=\"reviewPane\"> Value : </label>  "+ $("#cv" + count).val() +"</br>";
		options += "<label style=\"margin-left: 15px;\" class=\"reviewPane\"> Transformations : </label>  "+ $("#ct" + count).val() +"</br>";
		options += "<label style=\"margin-left: 15px;\" class=\"reviewPane\"> Transaction Variables : </label> </br>";
		
		var transVarName =[]; 
		if($("#ctn" + count).val() != undefined || $("#ctn" + count).val() == ""){	
			transVarName = $("#ctn" + count).val().split(",");
		}
		
		var transVarValue =[];
		if($("#ctv" + count).val() != undefined || $("#ctv" + count).val() == ""){	
			transVarValue = $("#ctv" + count).val().split(",");
		}
		
		$(transVarName).each(function(index, exp){
			options += "<label style=\"margin-left: 25px;\" class=\"reviewPane\"> Name :</label>  "+ transVarName[index] +",";
			options += "<label style=\"margin-left: 25px;\" class=\"reviewPane\"> Value :</label>  "+ transVarValue[index] +"</br>";
		});
		
		count ++;
	});
	
	options += "<label style=\"margin-left: 5px;\" class=\"reviewPane\">  Disruptive Action :</label> </br>";
	options += "<label style=\"margin-left: 15px;\" class=\"reviewPane\"> Action : </label>  "+ $("input[name='action']:checked").val();
	
	$("#review").html(options);
	
	$("#selectedGroup").val($("#ruleGroup").val());
	$("#selectedType").val($("#ruleType").val());
	$("#selectedSeverity").val($("#ruleSeverity").val());
	$("#selectedTitle").val($("#ruleTitle").val());
	$("#selectedRevision").val($("#ruleRevision").val());
	$("#selectedTags").val($("#ruleTags").val());
	$("#selectedMessage").val($("#ruleMessage").val());
	$("#selectedDescription").val($("#ruleDescription").val());
	$("#selectedPhase").val($("#rulePhase").val());
	$("#selectedConditionValue").val($("#conditionValue").val());
	$("#selectedAction").val($("input[name='action']:checked").val());
}

/*
 * This function for showing the previously stored condition on 
 * UI form
 */
function showCondition(shCondNo) {

	console.log("showCondition called :" + shCondNo);
	
	$("#conditionOperator").val("");
	$("#conditionValue").val("");
	$("#conditionTransformations").val("");
	$("#conditionTransformations").multiselect("uncheckAll");
	$("#allSetVars").html("");
	$("#allExpressions").html("");
	
	var exps = $("#cse" + shCondNo).val().split(",");
	
	var i;
	for(i=0; i<exps.length; i++){
		
		var exp = exps[i];
		console.log("Expressions   :"+ exp);
		var query = {};
		if( exp.split("|")[1].split(":")[0] == "collectionVars" ){
			query = { "type" : "CollectionExpression",
					  "collection" : exp.split("|")[1].split(":")[1]};
		} else{
			query = { "type" : "VariableExpression" };
		}
		
		
		var options = "<p class = \"expression\" >"
			+ "<label>Unary Operator: </label>"
			+ "<select name=\"unaryOperator\" class=\"unary\"> "
			+ "<option value=\"none\"> None </option>";
		
		if( exp.split("|")[0].split(":")[1] == "negation" ){

			options += "<option value=\"negation\" selected > ! </option>";
			options +="<option value=\"ampersand\"> & </option>";
			
		} else if( exp.split("|")[0].split(":")[1] == "ampersand" ){

			options += "<option value=\"negation\"> ! </option>";
			options +="<option value=\"ampersand\" selected > & </option>";
		
		} else {
			
			options += "<option value=\"negation\"> ! </option>";
			options +="<option value=\"ampersand\"> & </option>";
			
		} 
		
		options += "</select>";
		
		 $.ajax({
		       type: "GET",
		       async: false,
		       url: "getVariableList",
		       data: query,
		       success: function(data, status){
		    	   if (data.status == "0") {

						console.log("Successfully recieved list of variables");
						if (data.type == "StandardVariables") {
							options +=  onSelectedVarExp(data.varList, exp.split("|")[1].split(":")[1] );
						} else {
							
							if(exp.split("|").length == 3){
								options +=  onSelectedColExp(data.varList, exp.split("|")[1].split(":")[1], exp.split("|")[2].split(":")[1] );
							} else{
								options +=  onSelectedColExp(data.varList, exp.split("|")[1].split(":")[1]);
							}

						}

						options += "<a href=\"#\" class=\"closeExp\" onclick=\"closeExpression(this)\"> X </a>" +
								"</p>";
						$("#allExpressions").append(options);
						
						$("#conditionOperator").val($("#co" + shCondNo).val());
						$("#conditionValue").val($("#cv" + shCondNo).val());
						var trans = $("#ct" + shCondNo).val().split(",");
						
						var transStr ="";				
						var count = 0;
						$(trans).each(function(index, item){
							 $("#conditionTransformations").multiselect("widget").find(":checkbox[value='"+item+"']").attr("checked","checked");
							 $("#conditionTransformations option[value='" + item + "']").attr("selected", 1);
							 count++;
						});
						$("#conditionTransformations").multiselect("update");
						 
//						var transVarName= $("#ctn" + shCondNo).val().split(",");
//						var transVarValue= $("#ctv" + shCondNo).val().split(",");
//
//						options = "";
//						$(transVarName).each(function(index, exp){
//							if(transVarName[index] != "" || transVarValue[index] != "" ){
//							options += " <p> \n"
//									+ " <label for=\"transVarName\">Variable Name :</label> \n"
//									+ " <input name=\"transVarName\" value = \""+transVarName[index]+"\"/> \n" + " </p> \n" + " <p> \n"
//									+ " <label for=\"transVarValue\">Variable Value:</label> \n"
//									+ " <input name=\"transVarValue\" value = \""+transVarValue[index]+"\"/ /> \n" + " </p> \n";
//							}
//						});
//						$("#allSetVars").html(options);
						$("#saveCondButton").attr("onClick","saveCondition("+ shCondNo +")");

					} 
		    	   else if (data.status == "2") 
					{
						window.location("loginform.jsp");
					}
					else {

						notifyAlert(data.message);

					}
		       },
		       
		       error: function (xhr, desc, err) {
				    console.log(xhr);
				    console.log("Desc: " + desc + "\nErr:" + err);
		       }
		 
		 });
	}

}

/*
 * this function is for delete condition from UI form
 */
function deleteCondition(rmCondNo) {
	
	console.log("deleteCondition ..... :" + rmCondNo);
	$(function(){
	var i =0; 
	
	if ($("#cno" + rmCondNo).length > 0) {
		$("#cno" + rmCondNo).remove();
	}
	
//	for(i = rmCondNo + 1 ; i < condNo; i++ ){
//		
//		$("#cno" + i).find(".showCondition").attr("onClick", "showCondition("+(i-1)+")");
//		$("#cno" + i).find(".showCondition").html("View Condition " + i );
//		$("#cno" + i).find(".closeCondition").attr("onClick", "deleteCondition("+(i-1)+")");
//		$("#cno" + i).attr("id", "#cno" + (i - 1));
//		
//	
//	}
//	
//	if( condNo > -1 ){
//		condNo--; 
//	}
//	console.log("Modified Condition No ..... :" + condNo);
	
	});
	
}
/*
 * to add user defined variable fields for the condition
 */
function addSetVarFields() {

	console.log("addSetVarFields called... ");
	var options = "";
	options += " <p> \n"
			+ " <label for=\"transVarName\">Variable Name :</label> \n"
			+ " <input name=\"transVarName\" /> \n" + " </p> \n" + " <p> \n"
			+ " <label for=\"transVarValue\">Variable Value:</label> \n"
			+ " <input name=\"transVarValue\" /> \n" + " </p> \n";
	$("#allSetVars").append(options);

}
/*
 * to add expression fields for the rule target
 */
function addExpFields() {

	console.log("addExpFields called... ");
	var options = "";
	options += " <p class=\"expression\"> "
			+ "<input type=\"radio\" name=\"expGroup\" value=\"VariableExpression\"  onclick=\"onExpSelect(this)\"/> Variable Expression "
			+ "<input type=\"radio\" name=\"expGroup\" value=\"CollectionExpression\" onclick=\"onExpSelect(this)\" /> Collection Expression "
			+ "<a href=\"#\" class=\"closeExp\" onclick=\"closeExpression(this)\"> X </a>"
			+ "</p>";
	$("#allExpressions").append(options);

}
/*
 * This function is for deleting the expression block
 */
function closeExpression(child) {

	console.log("closeExpression called......: " + child);
	$(child).parent().remove();

}
/*
 * This function is for handling expression radio button clicks
 */

function onExpSelect(object) {

	console.log("Radio Button clicked ...... : " + object);
	var options = "<label>Unary Operator: </label>"
			+ "<select name=\"unaryOperator\" class=\"unary\"> "
			+ "<option value=\"none\"> None </option>"
			+ "<option value=\"negation\"> ! </option>"
			+ "<option value=\"ampersand\"> & </option>" + "</select>";
	var radioButton = $(object);
	var query = {
		"type" : radioButton.val()
	};

	$.get("getVariableList", query, function(data, status, xhr) {

		if (status == "error") {

			console.log("An error occurred: " + xhr.status
					+ " - " + xhr.statusText);

		} else if (status == "success") {

			if (data.status == "0") {

				console
						.log("Successfully recieved list of variables");
				if (data.type == "StandardVariables") {

					options += onVariableExpression(data.varList);

				} else {

					options += onCollectionExpression(data.varList);

				}

				options += "<a href=\"#\" class=\"closeExp\" onclick=\"closeExpression(this)\"> X </a>";
				console.log("Parent of exp:"
						+ radioButton.parent().attr("class"));
				radioButton.parent().html(options);

			}
			else if (data.status == "2") 
			{
				window.location("loginform.jsp");
			}
			else {

				notifyAlert(data.message);

			}
		}

	});

}
/*
 * This function is for parsing response for variable expressions
 */
function onVariableExpression(json) {

	var options = "<label>Standar Variables: </label>"
			+ "<select name=\"standardVars\">"
			+ "<option value= \"\"> none </option> ";

	$(json.StandardVariables).each(function(index, value) {
		options += "<option value=\"" + value + "\">" + value + "</option>";
	});

	options += "<optgroup label=\"Parsing Flags\">";
	$(json.ParsingFlags).each(function(index, value) {
		options += "<option value=\"" + value + "\">" + value + "</option>";
	});

	options += "</optgroup>" + "<optgroup label=\"Request  Variables\">";

	$(json.RequestVariables).each(function(index, value) {
		options += "<option value=\"" + value + "\">" + value + "</option>";
	});

	options += "</optgroup>" + "<optgroup label=\"Response Variables\">";

	$(json.ResponseVariables).each(function(index, value) {
		options += "<option value=\"" + value + "\">" + value + "</option>";
	});

	options += "</optgroup>" + "<optgroup label=\"Server Variables\">";

	$(json.ServerVariables).each(function(index, value) {
		options += "<option value=\"" + value + "\">" + value + "</option>";
	});

	options += "</optgroup>" + "<optgroup label=\"Request Variables\">";

	$(json.TimeVariables).each(function(index, value) {
		options += "<option value=\"" + value + "\">" + value + "</option>";
	});
	
	options += "</optgroup>" + "<optgroup label=\"Builtin Variables\">";
	
	$(json.BuiltinVariables).each(function(index, value) {
		options += "<option value=\"" + value + "\">" + value + "</option>";
	});
	
	options += "</optgroup>" + "<optgroup label=\"User Defined Variables\">";
	
	$(json.UserDefinedVariables).each(function(index, value) {
		options += "<option value=\"" + value + "\">" + value + "</option>";
	});

	options += "</optgroup> </select>";

	return options;

}

/*
 * This function is for parsing response for Collection expressions
 */
function onCollectionExpression(json) {

	console.log("onCollectionExpression ......");
	var options = "<label>Collections: </label>"
			+ "<select name=\"collectionVars\" onchange=\"getColElements(this)\"> "
			+ "<option value= \"\"> none </option> ";

	$(json.Collection).each(function(index, value) {
		options += "<option value=\"" + value + "\">" + value + "</option>";
	});
	
	options += "<optgroup label=\"RequestCollections\">";

	$(json.RequestCollections).each(function(index, value) {
		options += "<option value=\"" + value + "\">" + value + "</option>";
	});

	options += "</optgroup>" + "<optgroup label=\"ResponseCollections\">";

	$(json.ResponseCollections).each(function(index, value) {
		options += "<option value=\"" + value + "\">" + value + "</option>";
	});
	
	options += "</optgroup> </select>" +
			"<label>Elements: </label>" +
			"<select name=\"elementVars\"> " +
				"<option value=\"all\"> All </option>" +
			"</select> ";

	return options;

}

/*
 * This function is for parsing response for variable expressions
 */
function onSelectedVarExp(json, selectedVar) {

	console.log("onSelectedVarExp ......");
	var options = "<label>Standar Variables: </label>"
			+ "<select name=\"standardVars\">"
			+ "<option value= \"\"> none </option> ";

	$(json.StandardVariables).each(function(index, value) {
		
		if(value == selectedVar){
			options += "<option value=\"" + value + "\" selected >" + value + "</option>";
		} else{
			options += "<option value=\"" + value + "\">" + value + "</option>";
		}
		
	});

	options += "<optgroup label=\"ParsingFlags\">";
	$(json.ParsingFlags).each(function(index, value) {
		
		if(value == selectedVar){
			options += "<option value=\"" + value + "\" selected >" + value + "</option>";
		} else{
			options += "<option value=\"" + value + "\">" + value + "</option>";
		}
		
	});

	options += "</optgroup>" + "<optgroup label=\"RequestVariables\">";

	$(json.RequestVariables).each(function(index, value) {
		
		if(value == selectedVar){
			options += "<option value=\"" + value + "\" selected >" + value + "</option>";
		} else{
			options += "<option value=\"" + value + "\">" + value + "</option>";
		}
		
	});

	options += "</optgroup>" + "<optgroup label=\"ResponseVariables\">";

	$(json.ResponseVariables).each(function(index, value) {
		
		if(value == selectedVar){
			options += "<option value=\"" + value + "\" selected >" + value + "</option>";
		} else{
			options += "<option value=\"" + value + "\">" + value + "</option>";
		}
		
	});

	options += "</optgroup>" + "<optgroup label=\"ServerVariables\">";

	$(json.ServerVariables).each(function(index, value) {
		
		if(value == selectedVar){
			options += "<option value=\"" + value + "\" selected >" + value + "</option>";
		} else{
			options += "<option value=\"" + value + "\">" + value + "</option>";
		}
		
	});

	options += "</optgroup>" + "<optgroup label=\"RequestVariables\">";

	$(json.TimeVariables).each(function(index, value) {
	
		if(value == selectedVar){
			options += "<option value=\"" + value + "\" selected >" + value + "</option>";
		} else{
			options += "<option value=\"" + value + "\">" + value + "</option>";
		}
	
	});
	
	options += "</optgroup>" + "<optgroup label=\"Builtin Variables\">";
	
	$(json.BuiltinVariables).each(function(index, value) {
		
		if(value == selectedVar){
			options += "<option value=\"" + value + "\" selected >" + value + "</option>";
		} else{
			options += "<option value=\"" + value + "\">" + value + "</option>";
		}
		
	});
	
	options += "</optgroup>" + "<optgroup label=\"User Defined Variables\">";
	
	$(json.UserDefinedVariables).each(function(index, value) {
		
		if(value == selectedVar){
			options += "<option value=\"" + value + "\" selected >" + value + "</option>";
		} else{
			options += "<option value=\"" + value + "\">" + value + "</option>";
		}
		
	});

	options += "</optgroup> </select>";

	return options;

}

/*
 * This function is for parsing response for Collection expressions
 */
function onSelectedColExp(json, selectedCol, selectedElmnt) {

	console.log("onSelectedColExp ......:" + selectedCol + ":" + selectedElmnt);
	var options = "<label>Collections: </label>"
			+ "<select name=\"collectionVars\" onchange=\"getColElements(this)\"> "
			+ "<option value= \"\"> none </option> ";

	$(json.Collection).each(function(index, value) {
		
		if(value == selectedCol){
			options += "<option value=\"" + value + "\" selected >" + value + "</option>";
		} else{
			options += "<option value=\"" + value + "\">" + value + "</option>";
		}
		
	});

	options += "<optgroup label=\"RequestCollections\">";

	$(json.RequestCollections).each(function(index, value) {
		
		if(value == selectedCol){
			options += "<option value=\"" + value + "\" selected >" + value + "</option>";
		} else{
			options += "<option value=\"" + value + "\">" + value + "</option>";
		}
		
	});

	options += "</optgroup>" + "<optgroup label=\"ResponseCollections\">";

	$(json.ResponseCollections).each(function(index, value) {
		
		if(value == selectedCol){
			options += "<option value=\"" + value + "\" selected >" + value + "</option>";
		} else{
			options += "<option value=\"" + value + "\">" + value + "</option>";
		}
		
	});
	
	options += "</optgroup> </select>" +
					"<label>Elements: </label>" +
					"<select name=\"elementVars\"> " +
					"<option value=\"all\"> All </option>";
	
	
	$(json.Elements).each(function(index, value) {
		
		if(value == selectedElmnt){
			options += "<option value=\"" + value + "\" selected >" + value + "</option>";
		} else{
			options += "<option value=\"" + value + "\">" + value + "</option>";
		}
		
	});

	options += "</select> ";	
	console.log("Options :" + options);
	return options;
	
}

/*
 * This function for getting elements against a collection
 */
function getColElements(collection){
	
	console.log("Collection Name : " + collection );
	var col = $(collection);
	var elements = col.next().next(); 
	
	var options = "<option value=\"all\"> All </option> ";
	
	var query = {
			"collection" : col.val()
	};
	
	$.get("getElementList", query, function(data, status, xhr){
		
		if(status=="error"){
			
			console.log("An error occurred: " + xhr.status + " - " + xhr.statusText);
		
		} else if(status=="success"){
			
			if( data.status == "0" ){
				
				console.log("Successfully recieved list of variables");
					
				$(data.elementList).each(function(index, value) {
					options += "<option value=\""+value+"\">"+value+"</option>";
				});

		    	console.log("Parent of exp:" + col.parent().attr("class") +":"+ col.next().next().attr("name"));
		    	elements.html(options);
			
			} 
			else if (data.status == "2" ) 
			{
				window.location("loginform.jsp");
			} 
			else {
	
				notifyAlert(data.message);
			
			}
		}
		
	});
}
/*
 * This function is for notifying alert messages
 */
function notifyAlert(message) {

	noty({
		layout : 'center',
		theme : 'noty_theme_blue',
		type : 'error',
		timeout : '2000',
		text : message
	});

}
/*
 *  This function is for validating rule form's 
 *  meta data fields
 */
function validateMDFields() {

	console.log("validateMDFields ........ ");
	var ruleTitle = $("#ruleTitle");
	var ruleGroup = $("#ruleGroup");
	var ruleType = $("#ruleType");
	var rulePhase = $("#rulePhase");
	var ruleRevision = $("#ruleRevision");
	var ruleDescription = $("#ruleDescription");
	var ruleMessage = $("#ruleMessage");
	var specialCharPattern = /^[a-zA-Z0-9\s\-\_]+$/;
	var NumRegex = /^((\d+.)+\d)$/; 
		
	if (ruleTitle.val() == "") {	
		notifyAlert("rule title is required");
		ruleTitle.focus();
		return false;
	} else if (specialCharPattern.test(ruleTitle.val())) {
		console.log("correct input");

	} else {

		notifyAlert("invalid rule Title");
		ruleTitle.focus();
		return false;
	}    
	
	if( ruleType.val() == "" ){
		notifyAlert("rule type is required");
		ruleType.focus();
		return false;
	}

	if( ruleGroup.val() == "" ){
		notifyAlert("rule group is required");
		ruleGroup.focus();
		return false;
	}
	
	if( rulePhase.val() == "" ){
		notifyAlert("rule phase is required");
		rulePhase.focus();
		return false;
	}
	
	if (NumRegex.test(ruleRevision.val())) {
		console.log("correct input revision");
	} else {

		notifyAlert("invalid input revision");
		return false;
	}  
	
	if (ruleDescription.val() == "")
		{
		
		}
	
	else if (specialCharPattern.test(ruleDescription.val())) {
		console.log("Correct input");
	} else {
		
		notifyAlert("invalid input description");
		return false;
	}     
		
	if (ruleMessage.val() == "")
		{
		
		}
	else if (specialCharPattern.test(ruleMessage.val())) {
		console.log("Correct input");
	} else {
		notifyAlert("invalid input message");
		return false;
	}
//	
   
	
	return true;

}

/*
 *  This function is for validating rule form's 
 *  meta data fields
 */
function validateCondFields() {

	console.log("validateCondFields ........ ");
	var value = $("#conditionValue");
	var operator = $("#conditionOperator");
	
	if($(".ruleType").val() == "SimpleRule"){
		
		if($(".expression").length > 1){
			notifyAlert("Simple Rule can have only one condition");
			return false;
		}
		
	}
	
	if( $(".expression").length < 1 ){
		notifyAlert("Adding atleast one expression is required for a condition");
		return false;
	}
	
	$(".expression").each(function(index, item) {  
		
		var children=$(item).children("select");
		$(children).each(function( index2, value) {
			
			if( $(value).attr("name") == "standardVars" ){
				
				if($(value).val() == ""){
					notifyAlert("selecting a Standard Variable is required");
					return false;
				}
				
			} else if( $(value).attr("name") == "collectionVars" ){		
			
				if($(value).val() == ""){
					notifyAlert("selecting a Collection Variable is required");
					return false;
				}
			
			}
			
		});
		
    });
	if( operator.val() == "" ){
		notifyAlert("selecting an operator is required");
		return false;
	}

	if (value.val() == "") {
		notifyAlert("condition value is required");
		value.focus();
		return false;
	}

	$("input[name=transVarName]").each(function(index, item) {
		if ($(item).val() == "") {
			notifyAlert(" Transaction variable name is required");
			return false;
		}
	});

	$("input[name=transVarValue]").each(function(index, item) {
		if ($(item).val() == "") {
			notifyAlert(" Transaction variable value is required");
			return false;
		}

	});

	return true;

}

/*
 * This function is for applying the pagination on 
 * rule list
 */
function onListPagination(divClass){
	
	console.log("onListPagination ..... " + $("div."+divClass).length);
	$(function(){
	
		var prev = {start: 0, stop: 0};
	    cont = $("div."+divClass);
		$(".pagination").paging(cont.length, {
			format: '[<>] ',
			perpage: 9,
			lapping: 1,
			page: 1, // we await hashchange() event
			onSelect: function (page) {
		
				var data = this.slice;
		
				cont.slice(prev[0], prev[1]).css('display', 'none');
				cont.slice(data[0], data[1]).fadeIn("slow");
		
				prev = data;
		
				return true; // locate!
			},
			onFormat: function(type) {

				switch (type) {

				case 'block':

					if (!this.active)
						return '<span class="disabled">' + this.value + '</span>';
					else if (this.value != this.page)
						return '<em><a href="#' + this.value + '">' + this.value + '</a></em>';
					return '<span class="current">' + this.value + '</span>';

				case 'next':

					if (this.active)
						return '<a href="#' + this.value + '" class="next">></a>';
					return '<span class="disabled">></span>';

				case 'prev':

					if (this.active)
						return '<a href="#' + this.value + '" class="prev"><</a>';
					return '<span class="disabled"><</span>';

				case 'first':

					if (this.active)
						return '<a href="#' + this.value + '" class="first">|<</a>';
					return '<span class="disabled">|<</span>';

				case 'last':

					if (this.active)
						return '<a href="#' + this.value + '" class="last">>|</a>';
					return '<span class="disabled">>|</span>';

				case "leap":

					if (this.active)
						return "   ";
					return "";

				case 'fill':

					if (this.active)
						return "...";
					return "";
				}
			}
		});
		
	});
}
/*
 *  This function loads Http profile Menu
 */
function loadHTTPProfile() { 

	$('#left-bar').load("httpProfile.jsp",function(resp, status, xhr) {

		if (status == "error") {

			console.log("An error occurred: " + xhr.status + " - "
					+ xhr.statusText);

		} else if (status == "success") {
			console.log("Httpprofile Menu Successfully loaded");
		
			$(function() {
				
				$('#main').html("<div id=\"content\"><div id=\"content-head\"><h2>HTTP Request Resource Tree</h2></div></div><div id=\"right-bar\"></div>");
				resourceTree();	
				httpProfileOverlay();
			
			});
		
		}

	});
}

/*
 * to make resource tree and its event handling
 */
function resourceTree(){
	$("#tree").dynatree({
        onActivate: function(node) {
        	if (!node.data.isFolder)
        	{ 
        	 var resource = node.data.title;
        	 console.log(resource);
            //alert("You activated " + node.data.title);
        	 if( node.data.href ){
 	
        		
        		 $('#main').load(node.data.href,function(resp, status, xhr) {
      
     				if (status == "error") {

     					console.log("An error occurred: " + xhr.status + " - "
     							+ xhr.statusText);

     				} else if (status == "success") {

     					resourceRuleList(resource);
     					onHeaderChecked();
     
     				}

     			});        		
        		
        	    }
        	 console.log(node.data.href);
            }
        	
        }
    });
	//var resource = node;
}

function resourceRuleList(resource)
{			
			console.log("in resourceRuleList : Resource :" + resource);
			var userID = $("#userID").val();
			var data = "?resource=" + resource + "&userID=" + userID ;
			$("#resourceRules").load(
					"getByResource" + data,
					function(resp, status, xhr) {

						if (status == "error") {

							console.log("An error occurred: " + xhr.status + " - "
									+ xhr.statusText);
							notifyAlert("Server not responding");
							$("#rule-loading").hide();
							$(".rule-list-link").hide();

						} else if (status == "success") {

							$(function() {
								$("#rule-loading").hide();
								onListMouseOver();
								onRuleChecked();
								onAllRuleChecked();
								$(".rule-list-link").hide();
								onEditRuleOverlay();
								
								
							});

						}

					});
		
	}
/*
 * ----------------
 * ModSecurity
 * ----------------
 */


//A Global Variable for polling setinterval function
var pollTimer;


/*
 *  This function loads rule policies into UI
 */
function loadModSecMenu() {

	$('#left-bar').load("modSecMenu.jsp",function(resp, status, xhr) {

				if (status == "error") {

					console.log("An error occurred: " + xhr.status + " - "
							+ xhr.statusText);

				} else if (status == "success") {

					console.log("Modsecurity Menu Successfully loaded");
					$(function() {
						$('#main').html("<div id=\"content\"><div id=\"content-head\"><h2>Help Configuring ModSecurity</h2></div></div><div id=\"right-bar\" ></div>");
					});

				}

			});
}

/*
 *  This function loads modsecurity Menu
 */

function loadMSMenu() {

	$('#left-bar').load("msMenu.jsp",function(resp, status, xhr) {

				if (status == "error") {

					console.log("An error occurred: " + xhr.status + " - "
							+ xhr.statusText);

				} else if (status == "success") {

					console.log("Modsecurity Menu Successfully loaded");
					$(function() {
						$('#main').html("<div id=\"content\"><div id=\"content-head\"><h2>Help Configuring ModSecurity</h2></div></div><div id=\"right-bar\" ></div>");
					});

				}

			});
}

/*
 *  This function loads the mode security state  page
 */
function loadMSState() {

	$('#main').load("msStateUpdate.jsp",function(resp, status, xhr) {

		if (status == "error") {

			console.log("An error occurred: " + xhr.status + " - "
					+ xhr.statusText);

		} else if (status == "success") {

		}

	});
}



/*
 *  This function loads Mod Security Current State
 */
function loadMSState() {

	$('#main').load("checkMSStatus",function(resp, status, xhr) {

		if (status == "error") {

			console.log("An error occurred: " + xhr.status + " - "
					+ xhr.statusText);

		} else if (status == "success") {

		}

	});
}

/*
 *  This function loads Mod Security Current State
 */
function getMSState() {

	$.get("getMSStatus",function(data, status, xhr) {

		if (status == "error") {

			console.log("An error occurred: " + xhr.status + " - "
					+ xhr.statusText);

		} else if (status == "success") {
			
			if (data.status == "0") {
				
				if(data.msStatus == "1"){
					$("#top-menu-ms-state").html("Modsecurity: On");
				} else if (data.msStatus == "1"){
					$("#top-menu-ms-state").html("Modsecurity: Off");
				} else{
					$("#top-menu-ms-state").html("Modsecurity: Off");
					
				}
				
				
			} else if (data.msStatus == "2")
				{
				window.location("loginform.jsp");
				}

		}

	});
}

/*
 *  This function loads Mod Security configuration to UI
 */
function loadMSConfig() {

	$("#msConfigLink").attr("disabled", "true");
	$('#main').load("getMSConfig",function(resp, status, xhr) {

		$("#msConfigLink").removeAttr("disabled");
		if (status == "error") {

			console.log("An error occurred: " + xhr.status + " - "
					+ xhr.statusText);

		} else if (status == "success") {

		}

	});
}


/*
 *  This function loads Mod Security configuration to UI
 */
function loadMSEvents() {

	$('#main').load("eventsGrid.jsp",function(resp, status, xhr) {

		if (status == "error") {

			console.log("An error occurred: " + xhr.status + " - "
					+ xhr.statusText);

		} else if (status == "success") {
			onAuditLog();
		}

	});
}

/*
 * This function is for send modsecurity state change request
 */
function msStateUpdate(button){
	
	console.log("msStateUpdate called ...  : " + $(button).attr("name"));
	var jqButObj = $(button);
	var data = { "action": $(button).val() }; 
	$.post("msStateUpdate", data, function(data, status, xhr) {

		if (status == "error") {

			console.log("An error occurred: " + xhr.status + " - "
					+ xhr.statusText);

		} else if (status == "success") {
			console.log(" Status :" + data.status + "\t Message : "
					+ data.message);
			

			if (data.status == "0") {
				
				if(data.action == "stop"){
					
					jqButObj.html("Start");
					jqButObj.val("start");
					jqButObj.parent().find("p").html("ModSecurity is currently stopped");
					notifyAlert(data.message);
				
				} else if (data.action == "start"){
				
					jqButObj.html("Stop");
					jqButObj.val("stop");
					jqButObj.parent().find("p").html("ModSecurity is currently running");
					notifyAlert(data.message);
					
				} else if (data.action == "restart"){
				
					notifyAlert(data.message);
					
				}
				
				
			} 
			else if (data.status == "2")
			{
				window.location("loginform.jsp");
			}
			else{
				notifyAlert(data.message);
			}
			
		}
	});
}

/*
 * This function is for sending request for writing the modsecurity 
 * configurations
 */
function writeMSconfig(){
	
	$("#modifyMSConfig").submit(
		function() {
			
			console.log("Submit Called ... ");
			$("#submitMSConfig").attr("disabled", "true");
			var formData = $(this).formSerialize();
			
			$.post("writeMSConfig", formData, function(data, status, xhr) {
				
				$("#submitMSConfig").removeAttr("disabled");
				if (status == "error") {

					console.log("An error occurred: " + xhr.status + " - "
							+ xhr.statusText);

				} else if (status == "success") {

					if (data.status == "0") {

						notifyAlert(data.message);

					} else if (data.status == "2")
					{
						window.location("loginform.jsp");
					} 
					else {

						notifyAlert(data.message);

					}
				}
			});

			return false;
		});
}
/*
 * This function is for getting audit logs 
 */
function onAuditLog(){
	
	var check = true;
	getAuditLog();
	
	// object for websocket 
    var socket;
    var host; 
    
    if (window.location.protocol == 'http:') {
        host = 'ws://' + window.location.host + '/WebSiren-1.0.0/websocket/wsService';
    } else {
        host = 'wss://' + window.location.host + '/WebSiren-1.0.0/websocket/wsService';
    }
    
    if ('WebSocket' in window) {
        
    	console.log("Websocket is supported by this browser :");
    	socket = new WebSocket(host);
    
    } else if ('MozWebSocket' in window) {
        
    	console.log("Websocket is supported by this browser :");
    	socket = new MozWebSocket(host);
    
    } else {
    	
        Console.log('WebSocket is not supported by this browser.');
        pollTimer = setInterval(function(){
        	
    		getAuditLog();
    		
    	}, 15000);
        
    }
    
    socket.onopen = function () {
    	
        console.log('Info: WebSocket connection opened.');
        
    };

    socket.onclose = function () {
    	
        console.log('Info: WebSocket closed.');
    
    };

    socket.onmessage = function (message) {
        
    	var json =$.parseJSON(message.data);
    	console.log("Info: Websocket onmessage : " + json);
    	onAuditEvent(json);
    	
    
    };
    
    $("a").click(function(){
		
		if(check){
			
			if(pollTimer){
				
				clearInterval(pollTimer);
				check = false;
			
			} else if(socket){
				
				console.log("Info: closing Websocket connection");
				socket.close();
			
			}
			
		}
		
	});
	
}

/*
 * This function is for sending request for auditlogs snapshot
 */
function getAuditLog(){
	
	console.log("getAuditLog called ... ");
	$.getJSON("getAuditLog", function(data, status, xhr) {

		if (status == "error") {

			console.log("An error occurred: " + xhr.status + " - "
					+ xhr.statusText);

		} else if (status == "success") {
			 
			console.log(" Status :" + data );
			

			if (data.status == undefined) {
				
				var options = "";
				
				$(data).each(function(index, value) {
					
					index = data.length-1 - index;
					value= data[index];
					options += "<tr> "+
								"<td>"+ value.id +"</td> "+
								"<td>"+ value.eventType +"</td> "+
								"<td>";
								
								var date=value.date.split("|");
								$(date).each(function(ind, val) {
								
								options += val + "</br>";
								
								});
								
								options += "</td> "+
								"<td>"+ value.remoteAddr +"</td> "+
								"<td>"+ value.serverAddr +"</td> "+
								"<td>"+ value.resource +"</td> "+
								"<td>"+ value.ruleID +"</td> " +
								"<td>"+ value.severity +"</td> "+
								"<td>"+ value.message +"</td> "+
							"</tr>";
					$("#eventBody").html(options);
				});
			} 
	
		}
	});
	
}

function onAuditEvent(data){
	
	console.log("onAuditEvent:" + data);
	var options = "<tr> "+
		"<td>"+ data.id +"</td> "+
		"<td>"+ data.eventType +"</td> "+
		"<td>";
	
		var date=data.date.split("|");
		$(date).each(function(ind, val) {
		
		options += val + "</br>";
		
		});
		
		options += "</td> "+
		"<td>"+ data.remoteAddr +"</td> "+
		"<td>"+ data.serverAddr +"</td> "+
		"<td>"+ data.resource +"</td> "+
		"<td>"+ data.ruleID +"</td> " +
		"<td>"+ data.severity +"</td> "+
		"<td>"+ data.message +"</td> "+
	"</tr>";
	
	console.log("Log list size : " + $("#eventBody tr").length);
	if($("#eventBody tr").length >= 15){
		
		$("#eventBody tr").slice(14, 1);
			
	}
		$("#eventBody").prepend(options);
	
}

/*
 * --------------------------------------------------------------------
 * jQuery-Plugin - $.download - allows for simple get/post requests for files
 * by Scott Jehl, scott@filamentgroup.com
 * http://www.filamentgroup.com
 * reference article: http://www.filamentgroup.com/lab/jquery_plugin_for_requesting_ajax_like_file_downloads/
 * Copyright (c) 2008 Filament Group, Inc
 * Dual licensed under the MIT (filamentgroup.com/examples/mit-license.txt) and GPL (filamentgroup.com/examples/gpl-license.txt) licenses.
 * --------------------------------------------------------------------
 */

$.download = function(url, data, method) {
	//url and data options required
	console.log("Parse Errors in : " + data);
	if (url && data) {
		//data can be string of parameters or array/object
		data = typeof data == 'string' ? data : jQuery.param(data);
		//split params into form inputs
		var inputs = '';
		jQuery.each(data.split('&'), function() {
			var pair = this.split('=');
			inputs += '<input type="hidden" name="' + pair[0] + '" value="'
					+ pair[1] + '" />';
		});
		//send request
		jQuery(
				'<form action="' + url + '" method="' + (method || 'post')
						+ '">' + inputs + '</form>').appendTo('body').submit()
				.remove();
	}
	;
}
