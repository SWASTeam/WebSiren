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
<%@page import="net.swas.explorer.ec.RuleGroup"%>
<%@page import="net.swas.explorer.oh.handler.RuleGroupHandler"%>
<%@page import="com.sun.corba.se.impl.oa.poa.Policies"%>
<%@page import="net.swas.explorer.ec.Rule"%>
<%@page import="net.swas.explorer.oh.handler.OntologyHandler"%>
<%@page import="net.swas.explorer.oh.handler.RuleGroupHandler"%>
<%@page import="net.swas.explorer.oh.lo.OntologyLoader"%>
<%@ page language="java" contentType="text/html; charset=windows-1256"
    pageEncoding="windows-1256"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>

<%!private OntologyLoader loader = null;%>
<% 
	loader = OntologyLoader.getOntLoader(getServletContext());
	OntologyHandler handler = new RuleGroupHandler(loader);
	RuleGroup group = (RuleGroup) handler.get(request.getParameter("groupName"));
%>
<form id="editRuleGroup">
		<div id="group">
			<legend id="createGroup"> Update Rule Group</legend>
			<fieldset>
				<p>
					<label for="groupTitle">Name:</label> 
					<input type= "hidden" id ="previousGroup" name="previousGroup" value =<%=group.getName()%> >
						<% try { %>
							<input id="groupTitle" name="groupTitle" value="<%= group.getName() %>" />
                    	<% } catch(Exception e) { %>
                    		<input id="groupTitle" name="groupTitle" />
                    	<% } %>
				</p>
				<p>
					<label for="groupDescription">Description:</label>
					<textarea name="groupDescription" id="groupDescription" cols="35"
						rows="5"><%= group.getDescription() %> </textarea>
				</p>
			</fieldset>
			<input type="submit" id = "submitGroup" value = "Update">
		</div>
	</form>

</body>
</html>