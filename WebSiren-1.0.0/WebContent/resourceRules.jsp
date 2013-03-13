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
<%@page import="net.swas.explorer.ecf.*"%>
<%@page import="net.swas.explorer.ec.*"%>
<%@page import="net.swas.explorer.oh.fields.*"%>
<%

List<Entity> ruleList = (List<Entity>) request.getAttribute("ruleList"); 
String resource = "";
String rules = "";

%>
<div id="content-head">
	<h2>Resource Rules</h2>
	<div id="content-menu">
		<ul>
		<li><a href="#" id="translateResourceSemRule"
				title="Translate Semantic Rules into ModSecurity Rules"
				onclick="translateResourceSemRuleRequest()"> <img alt="translate icon"
					src="images/icons/translate1.png"></a></li>
			
			<li><a href="#" id="deploySemRule"
				title="Deploy selected rules in ModSecurity"
				onclick="deployResourceRulesRequest()"> <img alt="deploy icon"
					src="images/icons/rule-deploy.png"></a></li>
		</ul>
	</div>
</div>
<div id="content-body">
<div id="rules">

		<%
		System.out.println("in resource rules");
		
		int i=1;
		for (Entity entity: ruleList) {
			
			if( entity instanceof Rule ) {
				
				Rule rule = (Rule) entity;
				resource = rule.getResource().getResource();
				rules = rules + rule.getID()  +",";
				
		%>
		
				<div  id="<%=rule.getID() %>"  class="resourceRule">
					<div class="rule-list-body">
						<a href="editRuleForm.jsp?ruleID=<%= "Rule." + rule.getID() %>" class="rule-title list-title" id = "over-<%= i++ %>"rel="#overlay">
							<% 
								System.out.println("Resource : " + rule.getResource().getResource());
								String title = "";
								if(!rule.getRuleTitle().equals("Default Mod Security Rule")){
									title = rule.getRuleTitle();
								} else if(!rule.getMetaData().getMessage().equals("")){
									title = rule.getMetaData().getMessage();
								} else{
									title = rule.getRuleTitle();
								}
								
								if(title.length() > 65){
									title = title.substring(0,64) + " ...";
								} 
							%>
							<%=title %>
						</a>

					</div>
					
					<div class = "ruleInfo">
						<ul>
							<li> Phase : <span>
								<% for(RulePhase phase: RulePhase.values()){
								if(phase.getIndex() == rule.getPhase()){ %>
									<%=phase.toString()%>
								<% }
								} %>
							</span> </li>
						</ul>
					</div>
					<a href="#" title="Rule Detail" class = "resourceRule-delete" onclick="loadRule('<%= "Rule." + rule.getID() %>')"><img alt="delete rule" src="images/navigate-right-icon.png"></a>
					<a href="#" title="Delete Rule" class="resourceRule-delete" onclick="delResourceRules('<%= "Rule."+rule.getID() %>', '<%= resource %>')"><img alt="delete rule" src="images/delete-icon.png"></a>
					
					</div>
				</div>
			
		<%
			} else if(entity instanceof ChainRule){
				ChainRule chRule = (ChainRule) entity;
				%>
				<div class="rule" id="<%=chRule.getID() %>">
					<img alt="rule" src="images/rule_logo.png" />
					<div class="rule-list-body">
						<a href="editRuleForm.jsp?ruleID=<%="ChainRule." + chRule.getID() %>" class="rule-title list-title" id = "over-<%= i++ %>"rel="#overlay">
						<% 
							String title = "";
							if(!chRule.getRuleTitle().equals("Default Mod Security Rule")){
								title = chRule.getRuleTitle();
							} else if(!chRule.getMetaData().getMessage().equals("")){
								title = chRule.getMetaData().getMessage();
							} else{
								title = chRule.getRuleTitle();
							}
							
							if(title.length() > 65){
								title = title.substring(0,64) + " ...";
							} 
						%>
						<%=title %>
						</a>
						<ul>
							<li> Group : <span> <%= chRule.getRuleGroup().getName() %> </span>, </li>
							<li> Phase : <span>
								<% for(RulePhase phase: RulePhase.values()){
								if(phase.getIndex() == chRule.getPhase()){ %>
									<%=phase.toString()%>
								<% }
								} %>
							</span>, </li>
							<li> Type: <span> Chain Rule </span> </li>
						</ul>
					</div>
					<div class="list-link rule-list-link" id="link-<%=chRule.getID() %>" title="More Details" >
						<a href="#" onclick="loadRule('<%= "ChainRule." + chRule.getID() %>')">»</a>
					</div>
				</div>
			<% 	}
			}
		System.out.println("rules :" + rules);
		%>
	<input type="hidden" id = "resource" value="<%=resource%>">
	<input type="hidden" id = "rules" value = "<%=rules%>">
	</div> 
	</div>