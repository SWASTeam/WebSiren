<!--
  This file is part of WebSiren.
 
  WebSiren is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.

  WebSiren is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.
 
  You should have received a copy of the GNU General Public License
  along with WebSiren.  If not, see <http://www.gnu.org/licenses/>.
 
 -->
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<title>Rule Explorer</title>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
<script type="text/javascript" src="js/jquery-1.7.2.min.js"></script>
<script type="text/javascript" src="js/swas-authenticate-1.0.0.js"></script>
<!-- the noty plugin-->
<script type="text/javascript"	src="js/noty/js/jquery.noty.js"></script>
<link rel="stylesheet" href="js/noty/css/noty_theme_blue.css" type="text/css"	media="screen" /> 
<link rel="stylesheet" href="css/login.css" type="text/css"	media="screen" />  
</head>
<body>

	<form name="login_frm" id="login_frm" action="index.jsp" method="post">
	<div id="login_box">
	<div id="login_header">
	Login
	</div>
	<div id="form_val">
	<div class="label">User Id :</div>
	<div class="control"><input type="text" name="userName" id="userName"/><span style="font-size: 10px;"></span></div>
	 
	<div class="label">Password:</div>
	<div class="control"><input type="password" name="password" id="password"/><span style="font-size: 10px;"></span></div>
	<div style="clear:both;height:0px;"></div>
	 
	<div id="errorMsg"></div>
	</div>
	<div id="login_footer">
	<label>
	<input type="submit" name="login" id="login" value="Login" class="send_button" />
	</label>
	<label>
	<a href="register.jsp">Register</a>
	</label>
	</div>
	</div>
	</form>
</body>



</html>