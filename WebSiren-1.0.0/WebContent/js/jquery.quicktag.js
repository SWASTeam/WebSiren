/*************************************************
**  jQuery Quicktag, Version 1.0
**  Copyright Milos Sutanovac, licensed MIT
**  http://twitter.com/mixn
**************************************************/
(function($){

	$.fn.quickTag = function(options){

		// Options
		options	= $.extend({

			limitation	: 	30,
			triggerKey	: 	13,
			allowedTags : 	5,
			focus		: 	false,
			coloring	: 	false,
			colors 		: 	['yellow', 'orange', 'red'],
			fade		: 	false,
			isForm 		: 	undefined,
			img 		: 	undefined,
			counter		: 	undefined,
			tagList		: 	$('#taglist'),
			notice		: 	undefined,
			tagClass	: 	'tag',
			closeClass	: 	'close'
								
		}, options);		

		// Vars
		var	that 		= 	$(this),
			val			=	undefined,
			valLength	=	0,
			mainColor	=	undefined,
			colorSpots 	=	undefined;
			
		// Allow chaining, something like $('input').hide().quickTag().show() works now			
		return that.each(function(){
			
			// Built everything inside an object to secure overwriting
			var quickTag = {

				init		: 	function(){

					// Hide notice
					$(options.notice).hide();
					
					// Cache counters color if counter is enabled
					if($(options.counter)) mainColor = $(options.counter).css('color');

					// Check if counter is enabled
					if(options.counter && options.limitation){

						// Attach necessary keyup event
						this.keyUp();

						// Set maxlength for the inputfield
						$(that).attr('maxlength', options.limitation);

						// Set starting value for the counter div
						$(options.counter).text(options.limitation);
					}

					// Check if image replacement is true, if yes, overwrite the value with value as src attribute + img tag
					if(options.img) options.img = '<img src="' + options.img + '" alt="Close this tag" />';

					// Check if everything's inside a form, if yes, call secureSubmit function
					options.isForm && this.secureSubmit();
					
					if(options.coloring) options.colors.reverse();
					
					// Wenn the counter and the coloringoption are set - generate colorSpots and store the array inside a global var
					if(options.counter && options.coloring) colorSpots = this.setColoring();

					// Call all necessary functions
					this.keyDown(); this.deleteTag();
				},

				keyDown		: 	function(){
					
					// keyDown Event, Event as a parameter
					$(that).keydown(function(e){

						// If the pressed key is the triggerKey - add it as a Tag & reset inputLength
						if(e.keyCode === options.triggerKey){

							// triggerKey pressed
							if($(options.tagList).children().length < options.allowedTags){


								if($(that).val() != ""){
									
									// Add Tag
									quickTag.addTag();

									// Reset counter value
									$(options.counter).text(options.limitation);

									// Reset input value
									$(that).val('');
								}
								
								// If the triggerKey is fired inside a form, don't submit the form
								if(options.isForm) return false;
							}else{

								// display the Error
								quickTag.displayErr();

								// disable event if the key isn't the triggerKey
								return false;
							}
						}else if(options.counter && valLength >= options.limitation && e.keyCode !== options.triggerKey && e.keyCode !== 8){

							// >= limitation && pressed key != triggerKey && pressed key != Backspace
							return false;
						}
					});
				},

				keyUp		: 	function(){

					// keyUp Event, Event as a parameter
					$(that).keyup(function(e){
						
						// Cache the inputs length when key is released
						valLength = $(this).val().length;

						// Set new/current value of the counter
						if(e.keyCode !== options.triggerKey && options.counter) $(options.counter).text(options.limitation - valLength);
						
						// Check if coloring is true / see if css needs to be applied
						if(colorSpots){

							// Necessary, local vars
							var	colorNow 	= undefined,
								lastEntry 	= colorSpots.length - 1,
								rest 		= options.limitation - valLength;

							// Loop backwards? I can haz!
							for(var i = lastEntry; i >= 0; i--){

								if(rest <= colorSpots[i]) colorNow = options.colors[i];
							}

							// See if rest is bigger than last entry inside the array, if yes, overwrite color with mainColor
							if(rest > colorSpots[lastEntry]) colorNow = mainColor;
							
							// Apply color
							$(options.counter).css('color', colorNow);
						}
					});
				},

				addTag		: 	function(){

					// Cache inputs value
					val = $(that).val();

					// Check if tag needs to be wrapped inside an li or div
				   	if($(options.tagList).is('ul')){

						// li - (options.img || 'x') checks whether an image-replacement is defined, if not use an 'x'
				   		$(options.tagList).append('<li class="' + options.tagClass + '"><a class="' + options.closeClass + '">' + (options.img || 'x') + '</a>' + ' ' + val + '</li>');
				   	}else if($(options.tagList).is('div')){

						// div - (options.img || 'x') checks whether an image-replacement is defined, if not use an 'x'
						$(options.tagList).append('<div class="' + options.tagClass + '"><a class="' + options.closeClass + '">' + (options.img || 'x') + '</a>' + ' ' + val + '</div>');
				   	}
				},

				deleteTag	: 	function(){

					// This needs to be a live event since tags are generated on the fly
					$('.' + options.closeClass).live('click', function(){

						// Remove tag & fade/hide notice if visible
						$(this).parent().remove();

						// Fade/hide notice if visible
						($(options.notice).is(':visible') && options.fade) ? $(options.notice).stop(true, true).fadeOut(options.fade) : $(options.notice).hide();
						
						// If focus is true, refocus the inputfield
						options.focus && $(that).focus();

						// return false / don't jump to the top of the site
						return false;
					});
				},

				displayErr	: 	function(){

					// Check if fade is enabled - if not, just show
					(options.fade) ? $(options.notice).stop(true, true).fadeIn(options.fade) : $(options.notice).show();
				},

				secureSubmit: 	function(){

					// Check if form has a submit button
					if($(options.isForm + ':has(input=[type=submit])')){

						// If it has one, cache it and set Eventhandler aka. submit *only* on click
						$(options.isForm)
							.find('input[type=submit]')
								.click(function(){ return true; });
					}
				},
				
				setColoring	: 	function(){
					
					// Local vars - colorableArea is the half of the limitation
					var colorableArea 	= 	Math.round(options.limitation),
						colorSpots		=	[];
					
					// Push the half of every half into an array so the coloring's dynamic
					for(var i = 0, z = 1; i < options.colors.length; i++){
						
						// Overwrite the var with its own value / 2
						colorableArea	=	Math.round(colorableArea / 2);
						
						// Check if the number is a 'nice' number (like 10, 15, 20), if not ...
						while(colorableArea % 5 != 0){
							
							// ... make it one
							colorableArea += z;
						}
						
						// Push rounded value into an array
					 	colorSpots.unshift(colorableArea);
					}
					
					// return the array
					return colorSpots;
				}
			};

			// Call init-function - this literally starts everything
			quickTag.init();
		});
	}
})(jQuery);