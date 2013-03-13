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
<%@page import="net.swas.explorer.oh.fields.ModSecConfigFields"%>
<%@page import="org.json.simple.JSONObject"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<div id="content-head">
	<h2 style="margin-left: 2%;"><b>ModSecurity Configuration</b></h2>
</div>
<%


	JSONObject json = (JSONObject) request.getAttribute("msConfig");
%>
<div id="content">
	<div id="content-body">
		<form id="modifyMSConfig">
			<div class="ms-config">
				<% for(ModSecConfigFields field: ModSecConfigFields.values()){ 
						String value = (String) json.get(field.toString()); %>
					
						<p><label> <%= field.toString() + " :" %></label>
							<% if(value != null){ %>
								<input type="text" name="<%=field.toString() %>" value="<%=value %>" />
							<% } else{ %>
								<input type="text" name="<%=field.toString() %>" />
							<% } %>
						</p>		
					
				<% }
				
				json.clear(); %>
				<input type="submit" id="submitMSConfig" name="modify" value="Save Configurations"  onclick="writeMSconfig()" />
			</div>
		</form>
	</div>
</div>
<!-- Html for right most protion of the page -->
<div id="right-bar"></div>