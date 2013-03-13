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
<%@page import="net.swas.explorer.ecf.Entity"%>
<%@page import="net.swas.explorer.ec.RuleGroup"%>
<%@page import="net.swas.explorer.oh.handler.RuleGroupHandler"%>
<%@page import="net.swas.explorer.oh.handler.OntologyHandler"%>
<%@page import="net.swas.explorer.oh.lo.OntologyLoader"%>
<%!private OntologyLoader loader = null;%>	
<% 

		loader =  OntologyLoader.getOntLoader( getServletContext() );
			OntologyHandler handler = new RuleGroupHandler( loader );
	%> 
<div id="content">	
	<div id="content-head">
		<h2 style="margin-left: 2%;"><b>Rules Group List</b></h2>
		<div id="content-menu" style="left: 94%; width: 6%;">
			<ul>
				<li><a href="addRuleGroup.jsp" id="addGroup" rel = "#groupOverlay" title="Add Rule Group"> 
						<img alt="add group" src="images/icons/list1_add.png"> </a></li>
			</ul>
		</div>
	</div>
	<div id="content-body">
		<div class="pagination">
		</div>
	<%
		int i=1;
		for (Entity entity: handler.getAll()) {
			
			RuleGroup group = null;
			if( entity instanceof RuleGroup ) {
				group = (RuleGroup) entity;
		%>
		<div class="rule" id="<%= group.getName() %>" >
				<img alt="rule" src="images/groups.png" />
				<div class="rule-list-body">
					<a href="editRuleGroup.jsp?groupName=<%= group.getName() %>" class="group-title list-title" id="over-<%= i++ %>" 
						rel="#groupOverlay" title="Add Rule Group" ><%=group.getName().replace("_", " ") %></a>
					<ul>
						<% if(group.getDescription().length() > 80){ %>
							<li> Description : <span> <%= group.getDescription().substring(0, 80) + " ..." %> </span>, </li>
						<% } else{ %>
							<li> Description : <span> <%= group.getDescription() %> </span> </li>
						<% } %>
					</ul>
				</div>
				
				
	
	<%-- 			<div class="list-link" id="link-<%= group.getName() %>" title="More Details" > --%>
	<%-- 				<a href="#" onclick="loadGroup('<%= group.getName() %>')">»</a> --%>
	<!-- 			</div> -->
				
				<a href="#" title="Delete this group" class="rule-delete" onclick="delGroupRequest('<%= group.getName() %>')"><img alt="delete group" src="images/icons/delete.png"></a>
				<a href="#" title="Translate and download rules in this group" class="rule-translate" onclick="translateGroupRequest('<%= group.getName() %>')"><img alt="download group" src="images/icons/download.png"></a>
				<a href="#" title="Translate and deploy rules" class="rule-delete" onclick="deployGroupRequest('<%= group.getName() %>')"><img alt="deploy rule icon" src="images/icons/rule-deploy.png"></a>
				
		</div>
		<%	}
		}
	%> 
	<div class="pagination"></div>
	</div>
</div>
<!-- Html for right most protion of the page -->
<div id="right-bar"></div>