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
<%@page import="net.swas.explorer.oh.handler.OntologyHandler"%>
<%@page import="net.swas.explorer.oh.lo.OntologyLoader"%>
<%@page import="net.swas.explorer.oh.handler.RuleGroupHandler"%>

<%!private OntologyLoader loader = null;%>
<% 

	loader = OntologyLoader.getOntLoader(getServletContext());

	String groupName = request.getParameter("groupName");
	RuleGroupHandler grpHandler = new RuleGroupHandler(loader);
	
	RuleGroup  group = (RuleGroup) grpHandler.get(groupName);

	%>
	<div class="right-bar-head">
			<span class="title">Group Name</span>
		</div>
		
		<div class="right-bar-body">
			<ul>
				<li class="md"><p><b>Group Name :</b><%=group.getName()%></p></li></ul>
		</div>	

		<div class="right-bar-body">
			<ul>
				<li class="md"><p><b>Group Description :</b><%=group.getDescription()%></p></li></ul>
		</div>	
		
		<div class="right-bar-body">
			<ul>
				<li class="md"><p><b>User :</b><%=group.getUserCreatedBy().getUserName()%></p></li></ul>
		</div>	