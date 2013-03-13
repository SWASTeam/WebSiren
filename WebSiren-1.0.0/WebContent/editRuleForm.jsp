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
<%@page import="net.swas.explorer.util.RuleMappings"%>
<%@page import="net.swas.explorer.ec.Condition"%>
<%@page import="net.swas.explorer.oh.handler.RuleGroupHandler"%>
<%@page import="net.swas.explorer.ecf.Entity"%>
<%@page import="net.swas.explorer.ec.RuleGroup"%>
<%@page import="net.swas.explorer.ec.UserDefinedVariable"%>
<%@page import="net.swas.explorer.ec.CollectionExpression"%>
<%@page import="net.swas.explorer.ec.ChainRule"%>
<%@page import="net.swas.explorer.oh.handler.ChainRuleHandler"%>
<%@page import="net.swas.explorer.ec.VariableExpression"%>
<%@page import="net.swas.explorer.oh.fields.DisruptiveAction"%>
<%@page import="net.swas.explorer.oh.fields.Transformation"%>
<%@page import="net.swas.explorer.oh.handler.Fetcher"%>
<%@page import="net.swas.explorer.oh.fields.RulePhase"%>
<%@page import="net.swas.explorer.oh.fields.Severity"%>
<%@page import="net.swas.explorer.oh.handler.RuleHandler"%>
<%@page import="net.swas.explorer.ec.Rule"%>
<%@page import="net.swas.explorer.oh.handler.OntologyHandler"%>
<%@page import="net.swas.explorer.oh.lo.OntologyLoader"%>
<%@ page language="java" contentType="text/html; charset=windows-1256"
    pageEncoding="windows-1256"%>

<%!private OntologyLoader loader = null;%>
<%    
	loader = OntologyLoader.getOntLoader(getServletContext());

	RuleGroupHandler groupHandler = new RuleGroupHandler(loader);
	RuleHandler ruleHandler = new RuleHandler(loader);
	ChainRuleHandler chHandler = new ChainRuleHandler(loader);
	ChainRule chRule =null; 
	Rule rule = null;
	
	String ruleID = request.getParameter("ruleID");
	
	if(ruleID.split("\\.")[0].equals("Rule")){
		rule = (Rule) ruleHandler.get(ruleID.split("\\.")[1]);
	%>	
<div id="rule-form">
	<ul class="form-tabs">
		<li><a href="#">Meta Data</a></li>
		<li><a href="#">List Conditions</a></li>
		<li><a href="#">Action</a></li>
		<li><a href="#">Review</a></li>
	</ul>
	 
	<!-- tab "panes" -->
	<div class="form-panes">
		<form id="editRule">
			<div class="step" >
				<legend class="step-head"> Meta Data</legend>
				<fieldset>
					<p>
						<label for="ruleTitle">Title:</label>
						<input id="ruleTitle" name="ruleTitle" value="<%=rule.getRuleTitle() %>"/>
					</p>  
					<p>
						<label for="ruleType">Type:</label>
						<input type="text" id="ruleType" name="ruleType" value="SimpleRule" readonly="readonly" style="background: transparent;" >
					</p>  
					<p>
						<label for="ruleGroup">Group:</label>
						<select id="ruleGroup" name="ruleGroup">
							<option value=""> None </option>

							<% for( Entity entity: groupHandler.getAll()){ 
								if( entity instanceof RuleGroup ){
									RuleGroup rg = (RuleGroup) entity; 	
									if(rg.getName().equals(rule.getRuleGroup().getName())){%>								
									<option value="<%= rg.getName() %>" selected="selected"> <%= rg.getName() %> </option>
								<% } else { %>
									<option value="<%= rg.getName() %>"> <%= rg.getName() %> </option>
								<% }
								} 
							} %>
						</select>
						<a href="#" id="addGroupLink" title="Add Rule Group" onclick="addGroupField(this)"> 
									<img alt="add group" src="images/icons/add2.png"> </a>
					</p>  
					
                    <p>
						<label for="ruleSeverity">Severity:</label>
						<select id="ruleSeverity" name="ruleSeverity">
							<% for ( Severity val: Severity.values()) { %>
									<% if(rule.getMetaData().getSeverity().equals( val.getValue() ) ){ %>
										<option value="<%= val.getValue()%>" selected="selected" ><%= val.toString() %></option>
									<% } else { %>
											<option value="<%= val.getValue() %>"><%= val.toString() %></option>
									<% } %>
							<% } %>
						</select>
                    </p>                             
					<p>
						<label for="ruleRevision">Revision:</label>
						<input id="ruleRevision" name="ruleRevision" value="<%= rule.getMetaData().getRevision() %>"/>
					</p>
					<p>
						<label for="ruleMessage">Message:</label>
						<input id="ruleMessage" name="ruleMessage" value="<%= rule.getMetaData().getMessage() %>" />
					</p>  
					<p>
						<label for="ruleTags">Tags:</label>
						<%
							String tags = "" ;
							int size = rule.getMetaData().getTag().size();
							for( int i=0 ; i < size; i++ ) {
								if( i != size -1 ){
									tags += rule.getMetaData().getTag().get(i) + ",";
								} else {
									tags += rule.getMetaData().getTag().get(i);
								}
							}
						%>
          				<input  name="ruleTags" id="ruleTags" value="<%= tags %>" />
					</p>                        
                    <p>
						<label for="ruleDescription">Description:</label>
						<textarea name="ruleDescription" id="ruleDescription" cols="45" rows="10" > <%= rule.getComment() %> </textarea>
                    </p>      
					<p>
						<label for="rulePhase">Phase:</label>
						<select id="rulePhase" name="rulePhase">
							<% for( RulePhase val: RulePhase.values()){ %>
								<% if(rule.getPhase() == val.getIndex()){ %>
									<option value="<%= val.getIndex() %>" selected="selected"> <%= val.toString() %> </option>
								<% } else{ %>
									<option value="<%= val.getIndex() %>"> <%= val.toString() %> </option>
								<% }
							} %>
						</select>
					</p>                           
				</fieldset>
				<a href="#" id="metadata-next" class="nextTab" style="margin-left: 86%;">Next</a>
			</div>
			<div class="step" >
				<legend class="step-head"> List Conditions</legend>
				<fieldset id="condition">
					<span id = "allExpressions">
						<p class="expression">
							<input type="radio" name="expGroup" class = "expGroup" value="VariableExpression" onclick="onExpSelect(this)" /> Variable Expression
							<input type="radio" name="expGroup" class = "expGroup" value="CollectionExpression" onclick="onExpSelect(this)" /> Collection Expression
							<a href="#" class="closeExp" onclick="closeExpression(this)"> X </a> 
						</p>
					</span>
					<a href="#" class="setvar" onclick="addExpFields()"> Add More Expressions</a>
                       <p>
						<label for="conditionOperator">Condition Operator</label>
						<select id="conditionOperator">
							<option value="">None</option>
							<optgroup label="File Operators">
									<% for ( String val: Fetcher.getFileOperators(loader) ) { %>
		                          		<option id="<%= val.toString() %>" ><%= val.toString() %></option> 
		                          	<% } %>	  
                           	</optgroup>
                           	<optgroup label="Numeric Operators">
									<% for ( String val: Fetcher.getNumericOperators(loader) ) { %>
		                          		<option id="<%= val.toString() %>" ><%= val.toString() %></option> 
		                          	<% } %>	  
                           	</optgroup>
                           	<optgroup label="Regex Operators">
									<% for ( String val: Fetcher.getRegexOperators(loader) ) { %>
		                          		<option id="<%= val.toString() %>" ><%= val.toString() %></option> 
		                          	<% } %>	  
                           	</optgroup>
                           	<optgroup label="String Operators">
									<% for ( String val:  Fetcher.getStringOperators(loader) ) { %>
		                          		<option id="<%= val.toString() %>" ><%= val.toString() %></option> 
		                          	<% } %>	  
                           	</optgroup>
                           	<optgroup label="Validation Operators">
									<% for ( String val:  Fetcher.getValidationOperators(loader) ) { %>
		                          		<option id="<%= val.toString() %>" ><%= val.toString() %></option> 
		                          	<% } %>	  
                           	</optgroup>
							<% for ( String val:Fetcher.getOperators(loader) ) { %>
                          		<option id="<%= val.toString() %>" ><%= val.toString() %></option> 
                          	<% } %>	 
						</select>
                       </p>
                       <p> 
					   		<label for="conditionValue">Value:</label>
					   		<input id="conditionValue" name="conditionValue" value="" />
                       </p>   
                       <p>
	                       <label for="conditionTransformations">Condition Transformations:</label>
							<select id="conditionTransformations"  title="Basic example" multiple="multiple" name="conditionTransformation" size="5">
								<% for( String val : Fetcher.getTransformations(loader) ){%>
									<option value="<%= val %>"> <%= Transformation.valueOf(val).getDisplayName() %></option>
								<% } %>
							</select>
					   </p>
					   <span id="allSetVars">
                       </span>
                      <a href="#" class="setvar" onclick="addSetVarFields()"> Add Transaction Variable </a>
					  <span id="allConditions">
					  <% 
					  	String exp = "";
					  	for(int i=0;i<rule.getCondition().getVariable().getCollectionExpressions().size(); i++){
					  		CollectionExpression colExp = rule.getCondition().getVariable().getCollectionExpressions().get(i);
					  		exp += "unaryOperator:" + colExp.getOperator() +
					  			   "|collectionVars:" + colExp.getCollection();
					  		if(!colExp.getElement().equals("")){
					  			exp += "|elementVars:" + colExp.getElement().split("\\.")[1];
					  			
					  		}
					  		if(!(rule.getCondition().getVariable().getCollectionExpressions().size() - 1 == i)){
					  			exp += ",";
					  		}
					  		
					  	}
					  	
					  	if(rule.getCondition().getVariable().getCollectionExpressions().size() > 0 &&
					  			rule.getCondition().getVariable().getVariableExpressions().size() > 0){
					  		exp += ",";
					  	}
					  	
					  	for(int i=0;i<rule.getCondition().getVariable().getVariableExpressions().size(); i++){
					  		VariableExpression varExp = rule.getCondition().getVariable().getVariableExpressions().get(i);
					  		exp += "unaryOperator:" + varExp.getOperator() + "|" +
					  			   "standardVars:" + varExp.getVariable();
					  		if(!(rule.getCondition().getVariable().getVariableExpressions().size() - 1 == i)){
					  			exp += ",";
					  		}
					  		
					  	}
					  	
					  	String transVarName = "";
					  	String transVarValue = "";
					  	
					  	for(int i = 0; i < rule.getCondition().getUserDefinedVariables().size();i++){
					  		UserDefinedVariable udv = rule.getCondition().getUserDefinedVariables().get(i);
					  		if( i >= (rule.getCondition().getUserDefinedVariables().size()-1)){
					  			transVarName += udv.getName();
					  		} else{
					  			transVarName += udv.getName() + ",";
					  		}
					  		
					  		if( i >= (rule.getCondition().getUserDefinedVariables().size()-1)){
					  			transVarValue += udv.getValue();
					  		} else{
					  			transVarValue += udv.getValue() + ",";
					  		}
					  	}
					  	
					  	String trnsfrmtion = "";
					  	for(int i = 0; i < rule.getCondition().getVariable().getTransformation().size(); i++){
					  		String val = rule.getCondition().getVariable().getTransformation().get(i);
					  		if(rule.getCondition().getVariable().getTransformation().size()-1 == i){
					  			trnsfrmtion += val; 
					  		} else{
					  			trnsfrmtion += val + ",";
					  		}
					  	}
					  	
					  %>
						<span id="cno0" class="condition">
							<p><a href="#" class="showCondition" onClick="showCondition(0)">View Condition 1</a>
								<a href="#" class="closeCondition"  onClick="deleteCondition(0)"> X </a></p>
							<input type="hidden" name="selectedExpressions" id="cse0" value="<%=exp %>" />
							<input type="hidden" name="selectedOperators" id="co0" value="<%=rule.getCondition().getOperator() %>" /> 
							<input type="hidden" name="selectedConditionValues" id="cv0" value="<%=rule.getCondition().getValue() %>" />
							<input type="hidden" name="selectedTransformations" id="ct0" value="<%=trnsfrmtion%>" />
							<input type=hidden" name="selectedTransVarName" id="ctn<%=0 %>"  value="<%=transVarName%>" style="display: none;" />
							<input type="hidden" name="selectedTransVarValue" id="ctv<%=0 %>" value="<%=transVarValue %>" style="display: none;" />								
						</span>	
                       </span>
					   <p class="submit">
                          <button style="text-align:center;margin-left:80px;" id="saveCondButton" type="button" onClick="saveCondition()">Save Condition</button>
                      </p>                    
				</fieldset>
				<a href="#" class="prevTab" style="margin-left: 4%;">Previous</a>
				<a href="#" id="condition-next" class="nextTab" style="margin-left: 70%;">Next</a>
			</div>
			<div class="step" >
				<legend class="step-head"> Actions</legend>                      
				<fieldset>
				<h5>Disruptive Action :</h5>
                <p class="disruptiveActions" >
                     <% for ( DisruptiveAction val: DisruptiveAction.values() ) {
                     	if ( rule.getDisruptiveAction().equals(val.toString()) ) { %>
                 			<span></span><input type="radio" name="action" id="action" value="<%=val.toString()%>" checked="checked" >&nbsp;&nbsp;&nbsp;<%= val.toString() %></span></br></br>
                 		<% } else{ %>
                 			<span></span><input type="radio" name="action" id="action" value="<%=val.toString()%>" >&nbsp;&nbsp;&nbsp;<%= val.toString() %></span></br></br>
                 		<% } %>
                 	<% } %>   
                </p>  
                </fieldset>
				<a href="#" class="prevTab" style="margin-left: 4%;">Previous</a>
				<a href="#" id="action-next" class="nextTab" style="margin-left: 70%;">Next</a>
			</div>
			<div class="step" >
				<legend class="step-head"> Review</legend>
				<fieldset>  
                                             
                     <span id="review">
                    </span>
                                               
                    <input id="selectedGroup" name="selectedGroup" type="hidden" />
					<input id="selectedType" name="selectedType" type="hidden" />
	         		<input  name="selectedSeverity" id="selectedSeverity" type="hidden" />
					<input id="selectedTitle" name="selectedTitle" type="hidden" />
	         		<input  name="selectedRevision" id="selectedRevision" type="hidden" />
	         		<input  name="selectedTags" id="selectedTags" type="hidden" />
					<input id="selectedMessage" name="selectedMessage" type="hidden" />
					<input name="selectedDescription" id="selectedDescription" type="hidden" />
					<input id="selectedPhase" name="selectedPhase" type="hidden" />
					<input id="selectedAction" name="selectedAction" type="hidden" />
					<input id="ruleID" name="ruleID" type="hidden" value="<%=ruleID %>" />
					
               	</fieldset>
				<a href="#" class="prevTab" style="margin-left: 4%;">Previous</a>
               	<button style="margin-left:28%; width:120px; " id="saveRuleButton" type="submit">Save Rule</button>
			</div>
		</form>
	</div>
</div>		
		
		
<% 	} else {
		chRule = (ChainRule) chHandler .get(ruleID.split("\\.")[1]);
%>	
	<div id="rule-form">
	<ul class="form-tabs">
		<li><a href="#">Meta Data</a></li>
		<li><a href="#">List Conditions</a></li>
		<li><a href="#">Action</a></li>
		<li><a href="#">Review</a></li>
	</ul>
	 
	<!-- tab "panes" -->
	<div class="form-panes">
		<form id="editRule">
			<div class="step" >
				<legend class="step-head"> Meta Data</legend>
				<fieldset>
					<p>
						<label for="ruleTitle">Title:</label>
						<input id="ruleTitle" name="ruleTitle" value="<%=chRule.getRuleTitle() %>"/>
					</p>  
					<p>
						<label for="ruleType">Type:</label>
						<input type="text" id="ruleType" name="ruleType" value="ChainRule" readonly="readonly" style="background: transparent;">
					</p>  
					<p>
						<label for="ruleGroup">Group:</label>
						<select id="ruleGroup" name="ruleGroup">
							<option value=""> None </option>

							<% for( Entity entity: groupHandler.getAll()){ 
								if( entity instanceof RuleGroup ){
									RuleGroup rg = (RuleGroup) entity; 	
									if(rg.getName().equals(chRule.getRuleGroup().getName())){%>								
									<option value="<%= rg.getName() %>" selected="selected"> <%= rg.getName() %> </option>
								<% } else { %>
									<option value="<%= rg.getName() %>"> <%= rg.getName() %> </option>
								<% }
								} 
							} %>
						</select>
						<a href="#" id="addGroupLink" title="Add Rule Group" onclick="addGroupField(this)"> 
									<img alt="add group" src="images/icons/add2.png"> </a>
					</p>  
					
                    <p>
						<label for="ruleSeverity">Severity:</label>
						<select id="ruleSeverity" name="ruleSeverity">
							<% for ( Severity val: Severity.values()) { %>
									<% if(chRule.getMetaData().getSeverity().equals( val.getValue() ) ){ %>
										<option value="<%= val.getValue()%>" selected="selected" ><%= val.toString() %></option>
									<% } else { %>
											<option value="<%= val.getValue() %>"><%= val.toString() %></option>
									<% } %>
							<% } %>
						</select>
                    </p>                             
					<p>
						<label for="ruleRevision">Revision:</label>
						<input id="ruleRevision" name="ruleRevision" value="<%= chRule.getMetaData().getRevision() %>"/>
					</p>
					<p>
						<label for="ruleMessage">Message:</label>
						<input id="ruleMessage" name="ruleMessage" value="<%= chRule.getMetaData().getMessage() %>" />
					</p>  
					<p>
						<label for="ruleTags">Tags:</label>
						<%
							String tags = "" ;
							int size = chRule.getMetaData().getTag().size();
							for( int i=0 ; i < size; i++ ) {
								if( i != size -1 ){
									tags += chRule.getMetaData().getTag().get(i) + ",";
								} else {
									tags += chRule.getMetaData().getTag().get(i);
								}
							}
						%>
          				<input  name="ruleTags" id="ruleTags" value="<%= tags %>" />
					</p>                        
                    <p>
						<label for="ruleDescription">Description:</label>
						<textarea name="ruleDescription" id="ruleDescription" cols="45" rows="10" > <%= chRule.getComment() %> </textarea>
                    </p>      
					<p>
						<label for="rulePhase">Rule Phase:</label>
						<select id="rulePhase" name="rulePhase">
							<% for( RulePhase val: RulePhase.values()){ %>
								<% if(chRule.getPhase() == val.getIndex()){ %>
									<option value="<%= val.getIndex() %>" selected="selected"> <%= val %> </option>
								<% } else{ %>
									<option value="<%= val.getIndex() %>"> <%= val %> </option>
								<% }
							} %>
						</select>
					</p>                           
				</fieldset>
				<a href="#" id="metadata-next" class="nextTab" style="margin-left: 86%;">Next</a>
			</div>
			<div class="step" >
				<legend class="step-head">List Conditions</legend>
				<fieldset id="condition">
					<span id = "allExpressions">
						<p class="expression">
							<input type="radio" name="expGroup" class = "expGroup" value="VariableExpression" onclick="onExpSelect(this)" /> Variable Expression
							<input type="radio" name="expGroup" class = "expGroup" value="CollectionExpression" onclick="onExpSelect(this)" /> Collection Expression
							<a href="#" class="closeExp" onclick="closeExpression(this)"> X </a> 
						</p>
					</span>
					<a href="#" class="setvar" onclick="addExpFields()"> Add More Expressions</a>
                       <p>
						<label for="conditionOperator">Condition Operator</label>
						<select id="conditionOperator">
							<option value="">None</option>
							<optgroup label="File Operators">
									<% for ( String val: Fetcher.getFileOperators(loader) ) { %>
		                          			<option id="<%= val.toString() %>" ><%= val.toString() %></option>
		                          	<%	} %>	  
                           	</optgroup>
                           	<optgroup label="Numeric Operators">
									<% for ( String val: Fetcher.getNumericOperators(loader) ) { %>
		                          			<option id="<%= val.toString() %>" ><%= val.toString() %></option>
		                          	<%} %>	  	  
                           	</optgroup>
                           	<optgroup label="Regex Operators">
									<% for ( String val: Fetcher.getRegexOperators(loader) ) { %>
		                          			<option id="<%= val.toString() %>" ><%= val.toString() %></option>
		                          	<%	} %>	  
                           	</optgroup>
                           	<optgroup label="String Operators">
									<% for ( String val:  Fetcher.getStringOperators(loader) ) { %>
		                          			<option id="<%= val.toString() %>" ><%= val.toString() %></option>
		                          	<%	}  %>	    
                           	</optgroup>
                           	<optgroup label="Validation Operators">
									<% for ( String val:  Fetcher.getValidationOperators(loader) ) { %>
		                          			<option id="<%= val.toString() %>" ><%= val.toString() %></option>
		                          	<%	}  %>	    
                           	</optgroup>
							<% for ( String val:Fetcher.getOperators(loader) ) { %>
		                          <option id="<%= val.toString() %>" ><%= val.toString() %></option>
		                    <%	}  %>	  
						</select>
                       </p>
                       <p> 
					   		<label for="conditionValue">Value:</label>
					   		<input id="conditionValue" name="conditionValue"  />
                       </p>   
                       <p>
	                       <label for="conditionTransformations">Condition Transformations:</label>
							<select id="conditionTransformations"  title="Basic example" multiple="multiple" name="ruleTransformation" size="5">
								<% for( String val : Fetcher.getTransformations(loader) ){ %>
										<option value="<%= val %>" > <%= Transformation.valueOf(val).getDisplayName() %></option>
								<% 	}  %>
							</select>
					   </p>
					   <span id="allSetVars">
                       </span>
                      <a href="#" class="setvar" onclick="addSetVarFields()"> Add Transaction Variable </a>
					  <span id="allConditions">
					  <% 
					  	int count=0;
					  	for(Condition cond:chRule.getCondition() ){
					  	
						  	String exp = "";
						  	for(int i=0;i<cond.getVariable().getCollectionExpressions().size(); i++){
						  		CollectionExpression colExp = cond.getVariable().getCollectionExpressions().get(i);
						  		exp += "unaryOperator:" + colExp.getOperator() +
						  			   "|collectionVars:" + colExp.getCollection();
						  		if(!colExp.getElement().equals("")){
						  			exp += "|elementVars" +  RuleMappings.elementMapping(colExp.getElement().split("\\.")[1]);
						  		}
						  		if(!(cond.getVariable().getCollectionExpressions().size() - 1 == i)){
						  			exp += ",";
						  		}
						  		
						  	}
						  	
						  	if(cond.getVariable().getCollectionExpressions().size() > 0 &&
						  			cond.getVariable().getVariableExpressions().size() > 0){
						  		exp += ",";
						  	}
						  	
						  	for(int i=0;i<cond.getVariable().getVariableExpressions().size(); i++){
						  		VariableExpression varExp = cond.getVariable().getVariableExpressions().get(i);
						  		exp += "unaryOperator:" + varExp.getOperator() + "|" +
						  			   "standardVars:" + varExp.getVariable();
						  		if(!(cond.getVariable().getVariableExpressions().size() - 1 == i)){
						  			exp += ",";
						  		}
						  		
						  	}
						  	
						  	String transVarName = "";
						  	String transVarValue = "";
						  	
						  	for(int i = 0; i < cond.getUserDefinedVariables().size();i++){
						  		UserDefinedVariable udv = cond.getUserDefinedVariables().get(i);
						  		if( i >= (cond.getUserDefinedVariables().size()-1)){
						  			transVarName += udv.getName();
						  		} else{
						  			transVarName += udv.getName() + ",";
						  		}
						  		
						  		if( i >= (cond.getUserDefinedVariables().size()-1)){
						  			transVarValue += udv.getValue();
						  		} else{
						  			transVarValue += udv.getValue() + ",";
						  		}
						  	}
						  	
						  	String trnsfrmtion = "";
						  	for(int i = 0; i < cond.getVariable().getTransformation().size(); i++){
						  		String val = cond.getVariable().getTransformation().get(i);
						  		if(cond.getVariable().getTransformation().size()-1 == i){
						  			trnsfrmtion += val; 
						  		} else{
						  			trnsfrmtion += val + ",";
						  		}
						  	}
						  	
						  %>
							<span id="cno<%=count %>" class="condition">
								<p><a href="#" class="showCondition" onClick="showCondition(<%=count %>)">View Condition <%=count + 1 %></a>
									<a href="#" class="closeCondition"  onClick="deleteCondition(<%=count %>)"> X </a></p>
								<input type="hidden" name="selectedExpressions" id="cse<%=count %>" value="<%=exp %>" />
								<input type="hidden" name="selectedOperators" id="co<%=count %>" value="<%=cond.getOperator() %>" /> 
								<input type="hidden" name="selectedConditionValues" id="cv<%=count %>" value="<%=cond.getValue() %>" />
								<input type="hidden" name="selectedTransformations" id="ct<%=count %>" value="<%=trnsfrmtion%>" />
								<input type=hidden" name="selectedTransVarName" id="ctn<%=count %>"  value="<%=transVarName%>" style="display: none;" />
								<input type="hidden" name="selectedTransVarValue" id="ctv<%=count %>" value="<%=transVarValue %>" style="display: none;" />								
							</span>	
						<%
							count++;
					  	} %>
                       </span>
					   <p class="submit">
                          <button style="text-align:center;margin-left:80px;" id="saveCondButton" type="button" onClick="saveCondition()">Save Condition</button>
                      </p>                    
				</fieldset>
				<a href="#" class="prevTab" style="margin-left: 4%;">Previous</a>
				<a href="#" id="condition-next" class="nextTab" style="margin-left: 70%;">Next</a>
			</div>
			<div class="step" >
				<legend class="step-head"> Actions</legend>                      
				<fieldset>
				<h5>Disruptive Action :</h5>
                <p class="disruptiveActions" >
                     <% for ( DisruptiveAction val: DisruptiveAction.values() ) {
	                     	if ( chRule.getDisruptiveAction().equals(val.toString()) ) { %>
	                 			<span><input type="radio" name="action" value="<%=val.toString()%>" checked="checked" >&nbsp;&nbsp;&nbsp;<%= val.toString() %></span></br></br>
	                 		<% } else{ %>
	                 			<span><input type="radio" name="action" value="<%=val.toString()%>" >&nbsp;&nbsp;&nbsp;<%= val.toString() %></span></br></br>
	                 		<% } %>
                 	<% } %>   
                </p>  
                  </fieldset>
				<a href="#" class="prevTab" style="margin-left: 4%;">Previous</a>
				<a href="#" id="action-next" class="nextTab" style="margin-left: 70%;">Next</a>
			</div>
			<div class="step" >
				<legend class="step-head"> Review</legend>
				<fieldset>                          
                     <span id="review">
                    </span>
                                               
                    <input id="selectedGroup" name="selectedGroup" type="hidden" />
					<input id="selectedType" name="selectedType" type="hidden" />
	         		<input  name="selectedSeverity" id="selectedSeverity" type="hidden" />
					<input id="selectedTitle" name="selectedTitle" type="hidden" />
	         		<input  name="selectedRevision" id="selectedRevision" type="hidden" />
	         		<input  name="selectedTags" id="selectedTags" type="hidden" />
					<input id="selectedMessage" name="selectedMessage" type="hidden" />
					<input name="selectedDescription" id="selectedDescription" type="hidden" />
					<input id="selectedPhase" name="selectedPhase" type="hidden" />
					<input id="selectedAction" name="selectedAction" type="hidden" />
					<input id="ruleID" name="ruleID" type="hidden" value="<%=ruleID %>" />
               	</fieldset>
				<a href="#" class="prevTab" style="margin-left: 4%;">Previous</a>
				<button style="margin-left:28%; width:120px; " id="saveRuleButton" type="submit">Save Rule</button>
			</div>
		</form>
	</div>
</div>		
<%		
	}
%>



	