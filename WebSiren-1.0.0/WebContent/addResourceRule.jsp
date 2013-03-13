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
<%@page import="net.swas.explorer.ecf.EntityFactory"%>
<%@page import="net.swas.explorer.oh.handler.ElementHandler"%>
<%@page import="org.junit.internal.matchers.IsCollectionContaining"%>
<%@page import="net.swas.explorer.ec.RuleGroup"%>
<%@page import="net.swas.explorer.ec.Element"%>
<%@page import="net.swas.explorer.ecf.Entity"%>
<%@page import="net.swas.explorer.oh.handler.RuleGroupHandler"%>
<%@page import="net.swas.explorer.oh.fields.DisruptiveAction"%>
<%@page import="net.swas.explorer.oh.fields.Transformation"%>
<%@page import="net.swas.explorer.oh.handler.Fetcher"%>
<%@page import="net.swas.explorer.oh.lo.OntologyLoader"%>
<%@page import="net.swas.explorer.oh.fields.Severity"%>
<%@page import="net.swas.explorer.oh.fields.RulePhase"%>
<%!private OntologyLoader loader = null;%>
<%

	loader = OntologyLoader.getOntLoader(getServletContext());
	RuleGroupHandler handler = new RuleGroupHandler(loader);
	String resourceHeader = request.getParameter("resourceHeader");
	System.out.println(resourceHeader);
	String[] headerList = resourceHeader.split(",");
	String resource = headerList[headerList.length - 1];
	
	/* String resourceHeader = "Content-Type"; */
%>
<div class="right-bar-head">
	<span class="title">Rule Creation</span>
</div>
<form id="addResourceRule">
<div class="right-bar-body">
	<div id="resource">
	<input id = "node" type="hidden" name="resource" value="<%=resource %>">
		<fieldset>
		<legend>Create Rule</legend>
		<p><label for="Title">Rule Title:</label> <input id="Title"
					name="Title" /></p>
				
			
			<p><label for="ruleGroup">Group:</label> 
				<select id="ruleGroup"
					name="ruleGroup">
					<option value="">None</option>

					<%
						for (Entity entity : handler.getAll()) {
							if (entity instanceof RuleGroup) {
								RuleGroup rg = (RuleGroup) entity;
					%>
					<option value="<%=rg.getName()%>">
						<%=rg.getName()%>
					</option>
					<%
						}
						}
					%>
				</select> <a href="#" id="addGroupLink" title="Add Rule Group"
					onclick="addGroupField(this)"> <img alt="add group"
					src="images/icons/add2.png">
				</a></p>
				
			<p><label for="ruleSeverity">Severity:</label> 
				<select
					id="ruleSeverity" name="ruleSeverity">
					<option value="">None</option>
					<%
						for (Severity val : Severity.values()) {
					%>
					<option value="<%=val.getValue()%>"><%=val.toString()%></option>
					<%
						}
					%>
				</select></p>
			
				
				<p>	<label for="targets">Rule Target:</label>
				<%
					String target = Fetcher.getTarget(loader, resourceHeader);
					
					%> 
					<label for="targets" id ="targetCollection"><%=target %></label>
					<input type="hidden" id ="targetCollection" name="targets" value="<%=target %>">
					</p>
			
		
			<p><label for="rulePhase">Phase:</label> <select id="rulePhase"
					name="rulePhase">
					<option value="">None</option>
					<%
						//for (RulePhase val : RulePhase.values()) {
					%>
					<option value="<%=RulePhase.requestBody.getIndex()%>">
						<%=RulePhase.requestBody%>
					</option>
					<%
						//}
					%>
				</select></p>	
			
	<p>				<label for="operator">Condition Operator</label> 
				<select id="operator" name = "operator">
					<option value="">None</option>
					<optgroup label="File Operators">
						<%
							for (String val : Fetcher.getFileOperators(loader)) {
						%>
						<option id="<%=val.toString()%>"><%=val.toString()%></option>
						<%
							}
						%>
					</optgroup>
					<optgroup label="Numeric Operators">
						<%
							for (String val : Fetcher.getNumericOperators(loader)) {
						%>
						<option id="<%=val.toString()%>"><%=val.toString()%></option>
						<%
							}
						%>
					</optgroup>
					<optgroup label="Regex Operators">
						<%
							for (String val : Fetcher.getRegexOperators(loader)) {
						%>
						<option id="<%=val.toString()%>"><%=val.toString()%></option>
						<%
							}
						%>
					</optgroup>
					<optgroup label="String Operators">
						<%
							for (String val : Fetcher.getStringOperators(loader)) {
						%>
						<option id="<%=val.toString()%>"><%=val.toString()%></option>
						<%
							}
						%>
					</optgroup>
					<optgroup label="Validation Operators">
						<%
							for (String val : Fetcher.getValidationOperators(loader)) {
						%>
						<option id="<%=val.toString()%>"><%=val.toString()%></option>
						<%
							}
						%>
					</optgroup>
					<%
						for (String val : Fetcher.getOperators(loader)) {
					%>
					<option id="<%=val.toString()%>"><%=val.toString()%></option>
					<%
						}
					%>
				</select></p>

			<p><label for="value">Value:</label> 
				<input id="value" name="value" /></p>
				
			

			<p>				<label for="disruptiveActions">Disruptive Action:</label> <select id="disruptiveActions"
					name="disruptiveActions">
					<option value="">None</option>
					 <%
					 	for (DisruptiveAction val : DisruptiveAction.values()) {
					 %>
					<option value="<%=val.toString()%>">
						<%=val%>
					</option>
					<%
						}
					%>
				</select></p>

			<p><label for="ruleMessage">Message:</label> <input id="ruleMessage"
					name="ruleMessage" /></p>
				
		


		</fieldset>
		 	<button style="margin-left:28%; width:150px;" id="saveRuleButton" type="submit">Save Rule</button>
	</div>
</div>
</form>