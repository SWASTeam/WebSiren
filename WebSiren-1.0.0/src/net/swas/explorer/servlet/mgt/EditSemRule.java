/*
 * This file is part of WebSiren.
 *
 *  WebSiren is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.

 *  WebSiren is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with WebSiren.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.swas.explorer.servlet.mgt;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.swas.explorer.ec.ChainRule;
import net.swas.explorer.ec.CollectionExpression;
import net.swas.explorer.ec.Condition;
import net.swas.explorer.ec.MetaData;
import net.swas.explorer.ec.Rule;
import net.swas.explorer.ec.RuleGroup;
import net.swas.explorer.ec.User;
import net.swas.explorer.ec.UserDefinedVariable;
import net.swas.explorer.ec.Variable;
import net.swas.explorer.ec.VariableExpression;
import net.swas.explorer.oh.handler.ChainRuleHandler;
import net.swas.explorer.oh.handler.RuleHandler;
import net.swas.explorer.oh.lo.OntologyLoader;
import net.swas.explorer.translator.SemTranslator;
import net.swas.explorer.util.FormFieldValidator;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This servlet class is responsible for updation of semantic rule in ontology
 */
@WebServlet(description = "This servlet is for handling edit rule request", urlPatterns = { "/editSemRule" })
public class EditSemRule extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private final static Logger log = LoggerFactory
			.getLogger(EditSemRule.class);
	private RuleHandler rulehandler = null;
	private ChainRuleHandler chainHandler = null;
	private OntologyLoader loader = null;


	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public EditSemRule() {
		super();
	}

	@Override
	public void init(ServletConfig config) throws ServletException {

		super.init(config);
		this.loader = OntologyLoader.getOntLoader(getServletContext());
		this.rulehandler = new RuleHandler(loader);
		this.chainHandler = new ChainRuleHandler(loader);

	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@SuppressWarnings("unchecked")
	protected void doPost(HttpServletRequest request,
		HttpServletResponse response) throws ServletException, IOException {
		
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		JSONObject json = new JSONObject();
		String status = "0";
		List<String> msg = new ArrayList<String>();
		
		if (FormFieldValidator.isLogin(request.getSession()))
		{
			log.info("Getting form parameters .. ");
			// request parameters
			String userID = request.getParameter("userID");
			String ruleID = request.getParameter("ruleID");
			String ruleGroup = request.getParameter("selectedGroup");
			String ruleType = request.getParameter("selectedType");
			String severity = request.getParameter("selectedSeverity");
			String ruleTitle = request.getParameter("selectedTitle");
			String revision = request.getParameter("selectedRevision");
			String tags = request.getParameter("selectedTags");
			String message = request.getParameter("selectedMessage");
			
			String description = request.getParameter("selectedDescription");
			String phase = request.getParameter("selectedPhase");
			String action = request.getParameter("selectedAction");
			
			String[] transformations = request
					.getParameterValues("selectedTransformations");
			String[] transVarNames = request
					.getParameterValues("selectedTransVarName");
			String[] transVarValues = request
					.getParameterValues("selectedTransVarValue");
			
			String[] conditionValues = request
					.getParameterValues("selectedConditionValues");
			String[] operators = request.getParameterValues("selectedOperators");
			String[] expressions = request.getParameterValues("selectedExpressions");
			
			
			Rule rule = null;
			ChainRule chainRule = null;

			log.info("ruleID :" + ruleGroup + "\t  Severity:" + severity
					+ "\t Title :" + ruleTitle + "\t revision :" + revision
					+ "\n\t  tags:" + tags + "\t action :" + action
					+ "\t\n transformations  :" + transformations + "\t  phase :"
					+ phase + "\t  description :" + description);

			
			log.info("setting values to beans ...");
			
			msg.addAll(FormFieldValidator.validateRule(ruleType, ruleGroup, ruleTitle, phase, action));
			if(msg.size() > 0){
				status = "1";
			}
			
			
			if(status.equals("0")){
				
				String[] tagList = tags.split(",");
				User user = SemTranslator.toUserBeans(userID, "", "");
				MetaData md = SemTranslator.toMetaDataBean( "", message, revision, severity, Arrays.asList(tagList));
				RuleGroup group = SemTranslator.toRuleGroupBeans( ruleGroup, "");
				
				if(ruleType.equals("SimpleRule")){
					
					String[] transList = transformations[0].split(",");
					List<VariableExpression> varExpList = SemTranslator.toVariableExpBeans(expressions[0]);
					List<CollectionExpression> colExpList = SemTranslator.toCollectionExpBeans(expressions[0]);
					List<UserDefinedVariable> udvList  = new ArrayList<UserDefinedVariable>();
					
					Variable var = SemTranslator.toVariableBean(FormFieldValidator.normaliseTransformtions(transList), varExpList, colExpList);
			
					if(transVarNames[0].contains(",")){
						udvList = SemTranslator.toUserDefVariablesBeans(transVarNames[0].split(","), transVarValues[0].split(","));
					} else{
						udvList = SemTranslator.toUserDefVariablesBeans(transVarNames[0], transVarValues[0]);
					}
					Condition cond = SemTranslator.toConditionBean(false, operators[0], conditionValues[0], var, udvList );	
					
					int ph = Integer.parseInt(phase);		
					rule = SemTranslator.toRuleBean(ruleTitle, md, cond, action, ph, description , group);
					rule.setUserEditedBy(user);
					Rule preRule = (Rule) this.rulehandler.get(ruleID.split("\\.")[1]);
					rule.setID(preRule.getID());
					rule.getMetaData().setID(preRule.getMetaData().getID());
					rule.getCondition().setID(preRule.getCondition().getID());
					rule.getCondition().getVariable().setID(preRule.getCondition().getVariable().getID());
					
					
				} else {
					
					List< Condition > conditions = new ArrayList<Condition>();
					for(int i = 0 ; i < expressions.length; i++){
						
						String[] transList = transformations[i].split(",");
						List<VariableExpression> varExpList = SemTranslator.toVariableExpBeans(expressions[i]);
						List<CollectionExpression> colExpList = SemTranslator.toCollectionExpBeans(expressions[i]);
						List<UserDefinedVariable> udvList = new ArrayList<UserDefinedVariable>();
						
						Variable var = SemTranslator.toVariableBean(FormFieldValidator.normaliseTransformtions(transList), varExpList, colExpList);
				
				
						if(transVarNames[0].contains(",")){
							udvList = SemTranslator.toUserDefVariablesBeans(transVarNames[0].split(","), transVarValues[0].split(","));
						} else{
							udvList = SemTranslator.toUserDefVariablesBeans(transVarNames[0], transVarValues[0]);
						}
						
				
						Condition cond = SemTranslator.toConditionBean(false, operators[i], conditionValues[i], var, udvList );		
						conditions.add(cond);
						
					}
				
					int ph = Integer.parseInt(phase);		
					chainRule = SemTranslator.toChainRuleBean(ruleTitle, md, conditions, action, ph, description, group);
					chainRule.setUserEditedBy(user);
					ChainRule preRule = (ChainRule) this.chainHandler.get(ruleID.split("\\.")[1]);
					chainRule.setID(preRule.getID());
					chainRule.getMetaData().setID(preRule.getMetaData().getID());
					
				}
						
				if(rule instanceof Rule){
					
					if (rulehandler.update(rule) != null) {
						
						log.info("rule stored successfully ...");
						status = "0";
						msg.add("rule added sucessfully");
						
					} else {
						
						status = "1";
						msg.add("Rule is not stored");
					
					}
					
				} else{
					
					if (chainHandler.update(chainRule) != null) {
						
						log.info("rule stored successfully ...");
						status = "0";
						msg.add("rule added sucessfully");
						
					} else {
						
						status = "1";
						msg.add("Rule is not stored");
					
					}
				}
			}
		}
		else
		{
			status = "2";
			msg.add("User Session Expired");
		}

		json.put("status", status);
		json.put("message", msg);

		try {
			log.info("Sending Json : " + json.toString());
			out.print(json.toString());
		} finally {
			out.close();
		}

	}

}
