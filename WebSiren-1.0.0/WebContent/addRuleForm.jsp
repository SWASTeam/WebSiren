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
<%@page import="net.swas.explorer.oh.handler.RuleGroupHandler"%>
<%@page import="net.swas.explorer.oh.fields.DisruptiveAction"%>
<%@page import="net.swas.explorer.oh.fields.Transformation"%>
<%@page import="net.swas.explorer.oh.handler.Fetcher"%>
<%@page import="net.swas.explorer.oh.lo.OntologyLoader"%>
<%@page import="net.swas.explorer.oh.fields.Severity"%>
<%@page import="net.swas.explorer.oh.fields.RulePhase"%>

<%!private OntologyLoader loader = null;%>
<% 
	loader =  OntologyLoader.getOntLoader( getServletContext() );
    RuleGroupHandler handler = new RuleGroupHandler( loader );
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
		<form id="addRule">
			<div class="step" >
				<legend class="step-head"> Create New Rule : <span>Define Meta Data</span></legend>
				<fieldset>
					<p>
						<label for="ruleTitle">Title:</label>
						<input id="ruleTitle" name="ruleTitle" />
					</p>  
					<p>
						<label for="ruleType">Type:</label>
						<select id="ruleType" name="ruleType">
							<option value=""> None </option>
							<option value="SimpleRule"> Simple Rule </option>
							<option value="ChainRule"> Chain Rule </option>
						</select>
					</p>  
					<p>
						<label for="ruleGroup">Group:</label>
						<select id="ruleGroup" name="ruleGroup">
							<option value=""> None </option>

							<% for( Entity entity: handler.getAll()){ 
								if( entity instanceof RuleGroup ){
									RuleGroup rg = (RuleGroup) entity; %>									
									<option value="<%= rg.getName() %>"> <%= rg.getName() %> </option>
								<% } 
							} %>
						</select>
						<a href="#" id="addGroupLink" title="Add Rule Group" onclick="addGroupField(this)"> 
									<img alt="add group" src="images/icons/add2.png"> </a>
					</p>  
                    <p>
						<label for="ruleSeverity">Severity:</label>
						<select id="ruleSeverity" name="ruleSeverity">
							<option value=""> None </option>
							<% for ( Severity val: Severity.values()) { %>
									<option value="<%=val.getValue()%>"><%= val.toString() %></option>
							<% } %>
						</select>
                    </p>                            
                                               
					<p>
						<label for="ruleRevision">Revision:</label>
						<input id="ruleRevision" name="ruleRevision" />
					</p>       
                    <p>
						<label for="ruleMessage">Message:</label>
						<input id="ruleMessage" name="ruleMessage" />
					</p>  
					
					<p>
						<label for="ruleTags">Tags:</label>
          				<input  name="ruleTags" id="ruleTags"  />
					</p> 
					<p>
						<label for="ruleDescription">Description:</label>
						<textarea name="ruleDescription" id="ruleDescription" cols="45" rows="10"></textarea>
                    </p>   
					<p>
						<label for="rulePhase">Phase:</label>
						<select id="rulePhase" name="rulePhase">
							<option value=""> None </option>
							<% for( RulePhase val: RulePhase.values()){ %>
									<option value="<%= val.getIndex() %>" > <%= val %> </option>
							<% } %>
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
					   		<label for="conditionValue">Condition Value:</label>
					   		<input id="conditionValue" name="conditionValue" />
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
					  <!-- <a href="#" class="setvar" onclick="addSetVarFields()"> Add Transaction Variable </a> -->
					  <span id="allConditions">
							
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
				<h5>Disruptive Actions :</h5>
                <p class="disruptiveActions" >
                     <% for ( DisruptiveAction val: DisruptiveAction.values() ) { %>
                 		<span><input type="radio" name="action" id="action" value="<%=val.toString()%>" >&nbsp;&nbsp;&nbsp;<%= val.toString() %></span></br></br>
                 	<% } %>   
                </p>
                </fieldset>
				<a href="#" class="prevTab" style="margin-left: 4%;">Previous</a>
				<a href="#" id="action-next" class="nextTab" style="margin-left: 70%;">Next</a>
			</div>
			<div class="step" >
				<legend class="step-head"> Rule</legend>
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
					
               	</fieldset>
				<a href="#" class="prevTab" style="margin-left: 4%;">Previous</a>
               	<button style="margin-left:28%; width:120px;" id="saveRuleButton" type="submit">Save Rule</button>
			</div>
		</form>
	</div>
</div>