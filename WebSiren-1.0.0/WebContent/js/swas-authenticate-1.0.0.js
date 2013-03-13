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

/*
 * on document ready
 */
$(function () {
	console.log("app is ready ... ");
	$("#cancel").click(function(){
		window.location = "loginform.jsp";
	});
	loginForm(); 
	registerForm();
});

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
 * this function is for register form
 */
function registerForm() {
	
	$("#register_frm").submit(function() {
		var data = {
				"userName" : $('#uName').val(),
				"displayName" : $('#displayName').val(),
				"password" : $('#pwd').val(),
				"confirmPassword" : $("#cPassword").val()
			};
		var username = $('#uName').val();
		var displayname =  $('#displayName').val();
		var password = $('#pwd').val();
		var cPassword = $("#cPassword").val();
		
		if (username == ""){
			notifyAlert("Enter User Name");
		}
		else if (displayname == "")
			{
			notifyAlert("Enter Display Name");
			}
		else if (password == "")
		{
			notifyAlert("Enter Password");
		}
		else if(cPassword == "")
			{
			notifyAlert("Enter Confirm Password");
			}
		else if (password != cPassword){
			notifyAlert("password does not match");
		}
		else{		
			
			$.ajax({
				url : 'validateUser',
				data: data,
				type: 'get',
				success: function (resp, status, xhr){
					console.log("Message: " + resp);
					if(status == "error")
				          console.log("An error occurred: " + xhr.status + " - " + xhr.statusText);
					  
					if(status == "success") {
						console.log( " Status :" + resp.status + "\t Message : " + resp.message);
						if( resp.status == "0" ){
								this.timer = setTimeout(function () {
						 	$.ajax({
								url: 'register',
							 	data: data,
							 	type: 'post',
								success: function( resp, status, xhr){
										console.log("Message: " + resp);
										if(status == "error")
									          console.log("An error occurred: " + xhr.status + " - " + xhr.statusText);
										  
										if(status == "success") {
											
											console.log( " Status :" + resp.status + "\t Message : " + resp.message);
											if( resp.status == "0" ){
												window.location = "index.jsp";
											} else {
												$("#errorMsg").html(resp.message).fadeIn(1000);
											}
											
										}
									}
							 	});
						  	}, 200);
						} 
						else 
						{
							notifyAlert(resp.message);
						}
						
					}
				}
			});
		
			}
	 		return false;
		
	});
}
/*
 * this function is for login form
 */
function loginForm() {
	
	
	  $("#login_frm").submit(function(){
		  	 
		  	//remove previous class and add new "myinfo" class
			var username = $('#userName').val();
			var password = $('#password').val();
			if (username == "")
				{
				notifyAlert("Enter User Name");
				}
			else if (password ==""){
				notifyAlert("Enter Password");
			}
			else {
			var data= {
			 			"userName" : $('#userName').val(),
			 			"password" : $('#password').val()
			 	};
			$("#errorMsg").html('Validating Your Login ').fadeIn(1000);
			this.timer = setTimeout(function () {
			 	$.ajax({
				url: 'authenticate',
			 	data: data,
			 	type: 'post',
				success: function( resp, status, xhr){
						console.log("Message: " + resp);
						if(status == "error")
					          console.log("An error occurred: " + xhr.status + " - " + xhr.statusText);
						  
						if(status == "success") {
							
							console.log( " Status :" + resp.status + "\t Message : " + resp.message);
							if( resp.status == "0" ){
								window.location = "index.jsp";
							} else {
								$("#errorMsg").html(resp.message).fadeIn(1000);
							}
							
						}
					}
			 	});
		  	}, 200);
	  }
		 		return false;
		  	});
	  
}

/*
 * This function for logging out
 */
function logout(){
	
	console.log( "logout.... :" );
	$.post( "logout" ,  function( data, status, xhr ){
		
		if(status == "error"){
		
			console.log("An error occurred: " + xhr.status + " - " + xhr.statusText);
		
		} else if( status == "success" ){
			
			console.log( " Status :" + data.status + "\t Message : " + data.message);
			if( data.status == "0" ){
				window.location= "loginform.jsp"
			} else {
				console.log(data.message);
			}
			
		}
	});
	
}