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
<%@ page language="java" contentType="text/html; charset=windows-1256" pageEncoding="windows-1256"%>
<%@ page import="net.swas.explorer.httpprofile.*"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.util.List"%>
<%@ page import="java.sql.*"%>
<%
	String username = (String) session.getAttribute("userName");
	System.out.println("User Name in index :" + username);
	if (username != null) {
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type"
	content="text/html; charset=windows-1256">
<title>Web Siran</title>

<link rel="stylesheet" href="css/jquery.tools.css" type="text/css"
	media="screen" />
<link rel="stylesheet" type="text/css" href="css/jquery-ui.css" />
<link rel="stylesheet" href="css/swas.css" type="text/css"
	media="screen" />
<link rel="stylesheet"
	href="js/dyna-tree/src/skin-vista/ui.dynatree.css" type="text/css"
	media="screen" />
<link rel="stylesheet"
	href="js/jquery-multiselect/jquery.multiselect.css" type="text/css"
	media="screen" />
<link rel="stylesheet" href="js/noty/css/noty_theme_blue.css"
	type="text/css" media="screen" />
<link rel="shortcut icon" href="favicon.ico">
<link rel="icon" type="image/gif" href="animated_favicon1.gif">
        
<script type="text/javascript" src="js/jquery-1.7.2.min.js">
	
</script>
<script type="text/javascript" src="js/jquery-ui.min.js"></script>

<!-- JQuery tool api -->
<script type="text/javascript" src="js/jquery.tools.min.js"></script>

<!-- Jquery form api -->
<script type="text/javascript" src="js/jquery.form.js"></script>

<!-- the tag-it plugin -->
<script type="text/javascript" src="js/tag-it/js/tag-it.js"></script>

<!-- Dyna tree specific js files -->
<script type="text/javascript"
	src="js/dyna-tree/jquery/jquery.cookie.js"></script>
<script type="text/javascript" src="js/dyna-tree/src/jquery.dynatree.js"></script>

<!-- the mousewheel plugin (optional to provide mousewheel support) && the jScrollPane script-->
<script type="text/javascript" src="js/jScrollPane/jquery.mousewheel.js"></script>
<script type="text/javascript"
	src="js/jScrollPane/jquery.jscrollpane.min.js"></script>

<!-- the mousewheel plugin (optional to provide mousewheel support) && the jScrollPane script-->
<script type="text/javascript"
	src="js/jquery-multiselect/src/jquery.multiselect.filter.js"></script>
<script type="text/javascript"
	src="js/jquery-multiselect/src/jquery.multiselect.js"></script>

<!-- the noty plugin-->
<script type="text/javascript" src="js/noty/js/jquery.noty.js"></script>

<!-- ScriptBreaker's multiple accordion plugin -->
<script type="text/javascript"
	src="js/scriptbreaker-multiple-accordion.js" charset="utf-8"></script>

<!-- infusion Jquery pagination plugin -->
<script type="text/javascript" src="js/jquery.paging.js" charset="utf-8"></script>

<script type="text/javascript" src="js/swas-1.0.0.js"></script>
<script type="text/javascript" src="js/swas-authenticate-1.0.0.js"></script>

</head>
<body>

	<input type="hidden" id="userID" value="<%=username%>">
	<div id="container">
		<!-- Header block -->
		<!-- for bar -->
		<div id="top-bar">
			<div id="top-menu">
				<ul>
					<li>Welcome <%=username%></li>
					<li id="top-menu-ms-state">ModSecurity : Off</li>
					<li><a href="#" onclick="logout()"> Logout </a></li>
				</ul>
			</div>
		</div>
		<div id="menu-bar">
			<div id="logo">
				<img alt="logo" src="images/websiran.png" width="100" height="50">
			</div>

			<div id="menu-bar-options">
				<ul>

					<li><a href="#" rel="#manage" id="manageNavigation"><img
							alt="manage icon" src="images/icons/setting.png" title="Manage"
							class="menu_class"></a>

						<ul class="the_menu">
							<li><a href="#" onclick="loadRuleList();loadFacets();">Rules</a></li>
							<li><a href="#" onclick="loadGroupList()">Groups</a></li>
							<li><a href="#" onclick="loadMSMenu()">ModSecurity</a></li>
							<%
								DOProfile profile = new DOProfile(getServletContext());
									List<String> parent = new ArrayList<String>();
									parent = profile.getRootParent();
									if (parent.size() == 0) {
							%>
							<li><a href="resourceTree.jsp" rel="#httpProfileOverlay" 
							id="addHttpProfileOverlay">Http Profile</a></li>
							<%
								} else {
							%>
							<li><a href="#" onclick="loadHTTPProfile()">Http Profile </a></li>
							<%
								}
							%>
							
						</ul></li>

					<li><a href="addRuleForm.jsp" class="mainOverlay"
						rel="#overlay" id="ruleOverlay"><img alt="add icon"
							src="images/icons/plus.png" title="Create New Rule"></a></li>
					<li><a href="fileLoad.jsp" rel="#fileOverlay"
						id="ruleFileOverlay"><img alt="translate icon"
							src="images/icons/translate.png"
							title="Translates a Mod Security Rule to Semantic Rule"></a></li>
					<li><a href="https://github.com/wasg-nust/siran/issues"
						target="_blank" rel="reportBug" id="reportBug"><img
							alt="report Bug icon" src="images/icons/bugs.png"
							title="Report bugs"></a></li>
				</ul>
			</div>

			<div id="searching">
				<div id="menu-bar-search">
					<input type="text" id="search" name="search"
						onkeyup="if (event.keyCode == 13) $('#searchRule').click()">
				</div>
				<div id="menu-bar-button">
					<a href="#" id="searchRule" onClick="loadByKeywords()"><img
						alt="search" src="images/icons/search3.png" /></a>
				</div>
			</div>
		</div>
		<div id="columns">
			<!-- a bar for rule groups placed on the top - left of the screen -->
			<div id="left-bar">
				<!-- a bar for facets placed on the bottom - left screen of the screen-->
				<div class="left-bar-head">
					<h2>Search By Perspective</h2>
				</div>
				<div id="facet-loading" class="load"></div>
				<div class="left-bar-body">
					<div id="facets"></div>
				</div>
			</div>
			
			<div id="main">
			
				<!-- A block for list of rules to be placed in it .  -->
				<div id="content">
					<div id="content-head"></div>
					<div id="rule-loading" class="load"></div>
					<div id="content-body"></div>
				</div>
	
				<!-- Html for right most protion of the page -->
				<div id="right-bar"></div>
				
			</div>
		</div>
	</div>

	<!-- Html for footer of the page -->
	<jsp:include page="footer.jsp"></jsp:include>

	<!-- external page is given in the href attribute (as it should be) -->
	<div class="apple_overlay" id="overlay">
		<div class="contentWrap"></div>
	</div>
	<div class="file_overlay" id="fileOverlay">
		<div class="fileUpload"></div>
	</div>
	<div class="group_overlay" id="groupOverlay">
		<div class="addRuleGroup"></div>
	</div>
	 <div class="httpProfile_overlay" id="httpProfileOverlay">
		<div class="profileResourceTree"></div>
	</div>
	<div class="generalization_overlay" id="generalizationOverlay">
		<div class="generalizationDiv"></div>
	</div>
</body>
</html>
<%
	} else {
		response.sendRedirect("loginform.jsp");
	}
%>
