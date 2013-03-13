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
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="net.swas.explorer.httpprofile.*"%>
<%@page import="java.sql.*"%>
<%
	String node = request.getParameter("node");
	DOProfile profile = new DOProfile(getServletContext());
	String url = profile.getUrlByResource(node);
	ResultSet rs = profile.getAllByUrl(url);
	rs.next();
	int id = rs.getInt("request_id");
	System.out.println("Request ID: " + id);
	String method = (rs.getString("method"));
	String value = (rs.getString("entity_data"));
%>
<div id="content">
	<div id="content-head">
		<h2>HTTP Request Resource Tree</h2>
	</div>
	<div id="content-body">
		<div id=requestDiv>
		<input id = "node" type="hidden" name="resource" value="<%=node %>">
			<div class="requestTable">
				<div class="head"><%=rs.getString("method") + " " + rs.getString("url") + " "
						+ rs.getString("http_version")%></div>
				<%
					//RequestHeader requestHeader = new RequestHeader();
					List<RequestHeader> headers = new ArrayList<RequestHeader>();
					List<String> paramList = new ArrayList<String>();
					headers = profile.getHeadersById(id);
					paramList = profile.getParametersByURL(url);
					for (RequestHeader header : headers) {
				%>
				<div class="header-list-checkboxes">
					<input type="checkbox" class="headerChkBox"
						value="<%=header.getName()%>" />
				</div>
	
				<div class='right'>
					<p><%=header.getName()%></p>
				</div>
				<div class='left'>
					<p><%=header.getValue()%></p>
				</div>
				
				<%
					}%>
					<hr>
					<% 
	
					for (String parameter : paramList) {
				%>
				
				<div class="header-list-checkboxes">
					<input type="checkbox" class="headerChkBox"
						value="<%="QUERY"+parameter%>" />
				</div>
				<div class='left'>
					<p><%=parameter%></p>
				</div>
				<%
					}
				%>
			</div>
			
		
			<input type="button" value="Generate Resource Rule" id="resourceRule" class="submitButton" onclick="generateRuleFromResource()">
			
			<div id = "resourceRules">
			</div>

		</div>

	</div>
	<div id = "resourceRules">
		</div>
</div>
<!-- Html for right most protion of the page -->
<div id="right-bar"></div>
