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
<div id="content">
	<div id="content-head">
		<h2 style="margin-left: 2%;"><b>Start or Stop ModSecurity</b></h2>
	</div>
	<%
	
		String status = (String) request.getAttribute("msStatus");
	%>
	<div id="content-body">
		<div class="ms-state">
			<h2> Update ModSecurity State : </h2> 
				<% if(status != null){ 
						if(status.equals("1")){ %>
							<p>ModSecurity is currently running</p>	
							<button class="ms-state-button" name="action" value="stop" onclick="msStateUpdate(this)" > Stop</button>
						<% } else{ %>
							<p>ModSecurity is currently stopped</p>	
							<button class="ms-state-button" name="action" value="start" onclick="msStateUpdate(this)" > Start</button>
						<%} 
					} else {%>
						<p>ModSecurity is currently stopped</p>	
						<button class="ms-state-button" name="action" value="start" onclick="msStateUpdate(this)" > Start</button>
				<% } %>
			<button class="ms-state-button" name="action" value="restart" onclick="msStateUpdate(this)" > Restart</button>
		</div>
	</div>
</div>

<!-- Html for right most protion of the page -->
<div id="right-bar"></div>