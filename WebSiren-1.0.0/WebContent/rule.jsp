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
<%@page import="net.swas.explorer.oh.fields.ElementMap"%>
<%@page import="net.swas.explorer.oh.fields.RulePhase"%>
<%@page import="net.swas.explorer.oh.fields.Severity"%>
<%@page import="net.swas.explorer.ec.UserDefinedVariable"%>
<%@page import="net.swas.explorer.util.RuleMappings"%>
<%@page import="net.swas.explorer.ec.Condition"%>
<%@page import="net.swas.explorer.ec.ChainRule"%>
<%@page import="net.swas.explorer.oh.handler.ChainRuleHandler"%>
<%@page import="net.swas.explorer.ec.CollectionExpression"%>
<%@page import="net.swas.explorer.ec.VariableExpression"%>
<%@page import="net.swas.explorer.ec.Rule"%>
<%@page import="net.swas.explorer.oh.handler.RuleHandler"%>
<%@page import="net.swas.explorer.oh.handler.OntologyHandler"%>
<%@page import="net.swas.explorer.oh.lo.OntologyLoader"%>

<%!private OntologyLoader loader = null;%>
<%

	loader = OntologyLoader.getOntLoader(getServletContext());

	String ruleID = request.getParameter("ruleID");
	RuleHandler ruleHandler = new RuleHandler(loader);
	ChainRuleHandler chHandler = new ChainRuleHandler(loader);

	if (ruleID.split("\\.")[0].equals("Rule")) {

		Rule rule = (Rule) ruleHandler.get(ruleID.split("\\.")[1]);
%>
<div class="right-bar-head">
	<span class="title"> <%=rule.getRuleTitle()%>
	</span>
</div>
<div class="right-bar-body">

	<ul>
		<li class="md"><p>
				<b>Rule ID </b>
			</p>
			<p><%=rule.getMetaData().getRuleID()%></p></li>
		<li class="md"><p>
				<b>Message </b>
			</p>
			<p><%=rule.getMetaData().getMessage()%></p></li>
		<li class="md"><p>
				<b>Revision </b>
			</p>
			<p><%=rule.getMetaData().getRevision()%></p></li>
		<li class="md"><p>
				<b>Phase </b>
			</p> <%
 	for (RulePhase phase : RulePhase.values()) {
 			if (phase.getIndex() == rule.getPhase()) {
 %>
			<p><%=phase.toString()%></p> <%
 	}
 		}
 %></li>
		<li class="md"><p>
				<b>Severity </b>
			</p> <%
 	for (Severity sev : Severity.values()) {
 			if (sev.getValue().equals(rule.getMetaData().getSeverity())) {
 %>
			<p><%=sev.toString()%></p> <%
 	}
 		}
 %></li>
 		<% if(rule.getMetaData().getTag().size() > 0){ %>
		<li class="md">
			<p>
				<b>Tag </b>
			</p>
			<ul>
				<%
					for (String tag : rule.getMetaData().getTag()) {
				%>
				<li>
					<%
						out.print(tag);
					%>
				</li>
				<%
					}
				%>
			</ul>
		</li>
	    <% } %>
	</ul>
</div>
<div class="right-bar-head">
	<span class="title">Disruptive Action</span>
</div>

<div class="right-bar-body">
	<ul>
		<li class="md"><p><%=rule.getDisruptiveAction()%></p></li>
	</ul>
</div>
<div class="right-bar-head">
	<span class="title">Rule Condition</span>
</div>
<div class="right-bar-body">
	<div id="accordion">
		<h2>
			<b>Condition</b>
		</h2>
		<!-- the tabs -->
		<div class="pane" style="display: block">
			<ul>
				<li class="cond">
					<p>
						<b>Variables </b>
					</p>
					<ul>
						<%
							for (VariableExpression var : rule.getCondition().getVariable()
										.getVariableExpressions()) {
						%>
						<li>
							<%
								String varString = RuleMappings.standardVariableMappings(var);
							%>
							<%=varString%>
						</li>
						<%
							}
						%>
						<%
							for (CollectionExpression var : rule.getCondition()
										.getVariable().getCollectionExpressions()) {
						%>
						<li>
							<%
								String colVariableString = RuleMappings.collectionVariableMapping(var);
							%>
							<%=colVariableString%>
						</li>
						<%
							}
						%>
					</ul>
				</li>
				<li class="cond"><p>
						<b>Operator </b>
					</p>
					<p>
						<%
							if (rule.getCondition().getIsNegated()) {
						%>
						<%="!"%>
						<%
							}
						%>
						<%=rule.getCondition().getOperator()%>
					</p></li>
				<li class="cond"><p>
						<b>Value </b>
					</p>
					<p><%=rule.getCondition().getValue()%></p></li>

				<%
					if (rule.getCondition().getVariable().getTransformation()
								.size() > 1) {
				%>
				<li class="cond"><p>
						<b>Transformations </b>
					</p>
					<p>
						<%
							for (String trans : rule.getCondition().getVariable()
											.getTransformation()) {
						%>
						<%=trans + " , "%>
						<%
							}
						%>
					</p></li>
				<%
					}
				%>
				<% if(rule.getCondition().getUserDefinedVariables() != null){ 
							if(rule.getCondition().getUserDefinedVariables().size() > 0){%>
				<li class="cond">
					<p>
						<b>UserDefined Variables </b>
					</p>
					</br>
					<p>
						<%
							for (UserDefinedVariable udv : rule.getCondition()
										.getUserDefinedVariables()) {
									String userDefined = RuleMappings.userDefinedVariableMapping(udv);
						%>
						<%=userDefined%>
						</br>
						<%
							}
						%>
					</p>
				</li>
				<%	} 
				} %>
			</ul>
		</div>

	</div>
</div>

<%
	} else {

		ChainRule ch = (ChainRule) chHandler
				.get(ruleID.split("\\.")[1]);
%>

<div class="right-bar-head">
	<span class="title"> <%=ch.getRuleTitle()%>
	</span>
</div>
<div class="right-bar-body">

	<ul>
		<li class="md"><p>
				<b>Rule ID </b>
			</p>
			<p><%=ch.getMetaData().getRuleID()%></p></li>
		<li class="md"><p>
				<b>Message </b>
			</p>
			<p><%=ch.getMetaData().getMessage()%></p></li>
		<li class="md"><p>
				<b>Revision </b>
			</p>
			<p><%=ch.getMetaData().getRevision()%></p></li>
		<li class="md"><p>
				<b>Phase </b>
			</p> <%
 	for (RulePhase phase : RulePhase.values()) {
 			if (phase.getIndex() == ch.getPhase()) {
 %>
			<p><%=phase.toString()%></p> <%
 	}
 		}
 %></li>
		<li class="md"><p>
				<b>Severity </b>
			</p> <%
 	for (Severity sev : Severity.values()) {
 			if (sev.getValue().equals(ch.getMetaData().getSeverity())) {
 %>
			<p><%=sev.toString()%></p> <%
 	}
 		}
 %></li>
 		<% if(ch.getMetaData().getTag().size() > 0){ %>
		<li class="md">
			<p>
				<b>Tag </b>
			</p>
			<ul>
				<%
					for (String tag : ch.getMetaData().getTag()) {
				%>
				<li>
					<%
						out.print(tag);
					%>
				</li>
				<%
					}
				%>
			</ul>
		</li>
		<% } %>
	</ul>
</div>
<div class="right-bar-head">
	<span class="title">Disruptive Action</span>
</div>

<div class="right-bar-body">
	<ul>
		<li class="md"><p><%=ch.getDisruptiveAction()%></p></li>
	</ul>
</div>
<div class="right-bar-head">
	<span class="title">Rule Condition</span>
</div>
<div class="right-bar-body">
	<div id="accordion">
		<!-- the tabs -->
		<%
			for (int i = 0; i < ch.getCondition().size(); i++) {

					Condition cond = ch.getCondition().get(i);
					if (i == 0) {
		%>
		<h2>
			<b>Condition <%=" : " + i%></b>
		</h2>
		<div class="pane" style="display: block">
			<%
				} else {
			%>
			<h2>
				<b>Condition <%=" : " + i%></b>
			</h2>
			<div class="pane">
				<%
					}
				%>
				<ul>
					<li class="cond">
						<p>
							<b>Variables </b>
						</p>
						<ul>
							<%
								for (VariableExpression var : cond.getVariable()
												.getVariableExpressions()) {
							%>
							<li><%=RuleMappings.standardVariableMappings(var)%></li>
							<%
								}
							%>
							<%
								for (CollectionExpression var : cond.getVariable()
												.getCollectionExpressions()) {
							%>
								<li><%= RuleMappings.collectionVariableMapping(var)%></li>
							<%
								}
							%>
						</ul>
					</li>
					<li class="cond"><p>
							<b>Operator </b>
						</p>
						<p>
							<%
								if (cond.getIsNegated()) {
							%>
							<%="!"%>
							<%
								}
							%>
							<%=cond.getOperator()%>
						</p></li>
					<li class="cond"><p>
							<b>Value </b>
						</p>
						<p><%=cond.getValue()%></p></li>

					<%
						if (cond.getVariable().getTransformation().size() > 1) {
					%>
					<li class="cond"><p>
							<b>Transformations </b>
						</p>
						<p>
							<%
								for (String trans : cond.getVariable()
													.getTransformation()) {
							%>
							<%=trans + " , "%>
							<%
								}
							%>
						</p></li>
					<%
						}
					%>
					
					<% if(cond.getUserDefinedVariables() != null){ 
							if(cond.getUserDefinedVariables().size() > 0){ %>
					<li class="cond">
					<p>
						<b>UserDefined Variables </b>
					</p>
					</br>
					<p>
						<%
							for (UserDefinedVariable udv : cond.getUserDefinedVariables()) {
									String userDefined = RuleMappings.userDefinedVariableMapping(udv);
						%>
						<%=userDefined%>
						</br>
						<%
							}
						%>
					</p>
				</li>
				<%	} 
				} %>

				</ul>
			</div>
			<%
				}
			%>
		</div>
	</div>
</div>

<%
	}
%>

