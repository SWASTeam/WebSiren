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
<%@page import="net.swas.explorer.oh.fields.RulePhase"%>
<%@page import="net.swas.explorer.ec.ChainRule"%>
<%@page import="java.util.List"%>
<%@page import="net.swas.explorer.ecf.Entity"%>
<%@page import="net.swas.explorer.ec.Rule"%>
<div id="content">
<div id="content-head">
		<div id="content-head-checkbox">
			<input type="checkbox" id="allChkBox" />
		</div>
		<h2>
			<b>All</b> / Rules List
		</h2>
		<div id="content-menu">
			<ul>
				<li><a href="#" id="deleteSemRule" title="Delete Rules"
					onclick="delRulesRequest()"><img alt="delete icon"
						src="images/icons/trash1.gif"></a></li>
	
				<li><a href="#" id="translateSemRule"
					title="Translate Semantic Rules into ModSecurity Rules"
					onclick="translateSemRuleRequest()"> <img alt="translate icon"
						src="images/icons/translate1.png"></a></li>
				
				<li><a href="#" id="deploySemRule"
					title="Deploy selected rules in ModSecurity"
					onclick="deploySelectedRulesRequest()"> <img alt="deploy icon"
						src="images/icons/rule-deploy.png"></a></li>
						
				<!--  <li><a href="checkGeneralization" id="checkGeneralization"
					title="Check generlatization based on String" rel="#generalizationOverlay"> <img alt="generalization icon"
						src="images/icons/generalization.png"></a></li> -->
			</ul>
		</div>
	</div>
	<div id="content-body">
		<div class="pagination"></div>
		
		<div id="rules">
	
			<%
				List<Entity> ruleList = (List<Entity>) request.getAttribute("ruleList");
			%>
			<%
			int i=1;
			for (Entity entity: ruleList) {
				
				if( entity instanceof Rule ) {
					Rule rule = (Rule) entity;
			%>
					<div class="rule" id="<%=rule.getID() %>" >
						<div class="rule-list-checkboxes">
							<input type="checkbox" class="ruleChkBox" value="<%= "Rule."+rule.getID() %>" />
						</div>
						<img alt="rule" src="images/rule_logo.png" />
						<div class="rule-list-body">
							<a href="editRuleForm.jsp?ruleID=<%= "Rule." + rule.getID() %>" class="rule-title list-title" id = "over-<%= i++ %>" rel="#overlay">
								<% 
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
							<ul>
								<li> Group : <span> <%= rule.getRuleGroup().getName() %> </span>, </li>
								<li> Phase : <span>
									<% for(RulePhase phase: RulePhase.values()){
									if(phase.getIndex() == rule.getPhase()){ %>
										<%=phase.toString()%>
									<% }
									} %>
								</span>, </li>
								<li> Type : <span> Rule </span> </li>
							</ul>
						</div>
						<div class="list-link rule-list-link" id="link-<%=rule.getID() %>" title="More Details" >
							<a href="#" onclick="loadRule('<%= "Rule." + rule.getID() %>')">»</a>
						</div>
					</div>
			<%
				} else if(entity instanceof ChainRule){
					ChainRule chRule = (ChainRule) entity;
					%>
					<div class="rule" id="<%=chRule.getID() %>">
						<div class="rule-list-checkboxes">
							<input type="checkbox" class="ruleChkBox" value="<%= "ChainRule."+chRule.getID() %>" />
						</div>
						<img alt="rule" src="images/rule_logo.png" />
						<div class="rule-list-body">
							<a href="editRuleForm.jsp?ruleID=<%="ChainRule." + chRule.getID() %>" class="rule-title list-title" id = "over-<%= i++ %>" rel="#overlay">
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
			%>
	
		</div>
	
		<div class="pagination"></div>
	</div>
</div>
<!-- Html for right most protion of the page -->
<div id="right-bar"></div>