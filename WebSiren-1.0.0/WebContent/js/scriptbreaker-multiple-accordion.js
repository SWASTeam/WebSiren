/*
 * jQuery UI Multilevel Accordion v.1
 * 
 * Copyright (c) 2011 Pieter Pareit
 *
 * http://www.scriptbreaker.com
 *
 */

//plugin definition
(function($){
    $.fn.extend({

    //pass the options variable to the function
    multiAccordion: function(options) {
        
		var defaults = {
			accordion: 'true',
			speed: 300,
			closedSign: '[+]',
			openedSign: '[-]'
		};

		// Extend our default options with those provided.
		var opts = $.extend(defaults, options);
		//Assign current element to variable, in this case is UL element
 		var $this = $(this);
 		
 		//add a mark [+] to a multilevel menu
 		$this.find("li").each(function() {
 			if($(this).find("ul").size() != 0){
 				//add the multilevel sign next to the link
 				$(this).find("a:first").append("<span>"+ opts.closedSign +"</span>");
 				
 				//avoid jumping to the top of the page when the href is an #
 				if($(this).find("a:first").attr('href') == "#"){
 		  			$(this).find("a:first").click(function(){return false;});
 		  		}
 			}
 		});

 		//open active level
 		$this.find("li.active").each(function() {
 			$(this).parents("ul").slideDown(opts.speed);
 			$(this).parents("ul").parent("li").find("span:first").html(opts.openedSign);
 		});
 		
 		/*******************added by ALI HUR for selection of facet individual and sending those selected individusals to facet query builder class******/
 		
 		$this.find('#facetIndividual').click(function(){
 			
 			$(this).toggleClass('selectedFacet');// added by ali hur -- 3/2/2012
 			id = $('a.current').attr('data-value');

 			var vals = [];
 			vals.push(id);
 			
 			$('a.selectedParentFacet').each( function() {
 				
 				child=$(this).attr('data-value');
 				parent=parent+"p";
 				vals.push(parent+"::"+child);
 				
 			});
 			
 			$('a.selectedFacet').each(function() {
 				
 				parent=$(this).parent().parent().parent().children("a").attr('data-value');
 				child=$(this).attr('data-value');
 				vals.push(parent+"::"+child); // :: represent the parent child relationship like parent::child
 			});

 			$('#profile').html('');
 			
 			var values=""; 
 			$(vals).each( function(index, value){
 				if( index == checkedRules.length - 1 ){
 					values += value;
 				} else {
 					values += value + ",";
 				}
 			});
 			
 			var data = { "value" : values, "userID" : $("#userID").val() };
 			console.log( " Data for request :" + data.value);
 			$.get( "searchByFacets", data , function( resp, status, xhr){
			
 			if(status == "error"){
		          
 				console.log("An error occurred: " + xhr.status + " - " + xhr.statusText);
 			
 			} else if(status == "success") {
 				
 				$('#main').html(resp);
 				$(function(){
 					
 					$("#rule-loading").hide();
					onListMouseOver();
					onRuleChecked();
					onAllRuleChecked();
					$(".rule-list-link").hide();
					onToolTip("#content-head ul li a[title]");
					onEditRuleOverlay();
					onListPagination("rule");
 				
 				});
 			
 			}
 			
 			});
 			
 		});
  		
  		/**************************************************this section ENDS here -- 3/2/2012***************************************************************/
 		
 		
  		$this.find("li a").click(function() {
  			if($(this).parent().find("ul").size() != 0){
  				if(opts.accordion){
  					//Do nothing when the list is open
  					if(!$(this).parent().find("ul").is(':visible')){
  						parents = $(this).parent().parents("ul");
  						visible = $this.find("ul:visible");
  						visible.each(function(visibleIndex){
  							var close = true;
  							parents.each(function(parentIndex){
  								if(parents[parentIndex] == visible[visibleIndex]){
  									close = false;
  									return false;
  								}
  							});
  							if(close){
  								if($(this).parent().find("ul") != visible[visibleIndex]){
  									$(visible[visibleIndex]).slideUp(opts.speed, function(){
  										$(this).parent("li").find("span:first").html(opts.closedSign);
  									});
  									
  								}
  							}
  						});
  					}
  				}
  				if($(this).parent().find("ul:first").is(":visible")){
  					$(this).parent().find("ul:first").slideUp(opts.speed, function(){
  						$(this).parent("li").find("span:first").delay(opts.speed).html(opts.closedSign);
  					});
  					
  					
  				}else{
  					$(this).parent().find("ul:first").slideDown(opts.speed, function(){
  						$(this).parent("li").find("span:first").delay(opts.speed).html(opts.openedSign);
  					});
  				}
  			}
  		});
    }
});
})(jQuery);