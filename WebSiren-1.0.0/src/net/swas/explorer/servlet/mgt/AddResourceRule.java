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
import java.sql.SQLException;
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
import net.swas.explorer.ec.Resource;
import net.swas.explorer.ec.Rule;
import net.swas.explorer.ec.RuleGroup;
import net.swas.explorer.ec.User;
import net.swas.explorer.ec.UserDefinedVariable;
import net.swas.explorer.ec.Variable;
import net.swas.explorer.ec.VariableExpression;
import net.swas.explorer.oh.handler.ChainRuleHandler;
import net.swas.explorer.oh.handler.Fetcher;
import net.swas.explorer.oh.handler.RuleHandler;
import net.swas.explorer.oh.lo.OntologyLoader;
import net.swas.explorer.translator.SemTranslator;
import net.swas.explorer.util.FormFieldValidator;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *This servlet class is responsible for adding new rule based on specified resource in ontology
 */
@WebServlet("/addResourceRule")
public class AddResourceRule extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final static Logger log = LoggerFactory.getLogger(AddSemRule.class);
	private RuleHandler rulehandler = null;
	private ChainRuleHandler chainHandler = null;
	private OntologyLoader loader = null;
    
	@Override
	public void init(ServletConfig config) throws ServletException {

		super.init(config);
		this.loader = OntologyLoader.getOntLoader(getServletContext());
		this.rulehandler = new RuleHandler(loader);
		this.chainHandler = new ChainRuleHandler(loader);
		
	}

    /**
     * @see HttpServlet#HttpServlet()
     */
    public AddResourceRule() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String status = "0";
		List<String> msg = new ArrayList<String>();
		JSONObject json = new JSONObject();
		PrintWriter out = response.getWriter();
		
		if (FormFieldValidator.isLogin(request.getSession()))
		{
			log.info("Getting form parameters .. ");
			// request parameters
			String userID = (String) request.getSession().getAttribute("userName");
			String ruleGroup = request.getParameter("ruleGroup");
			String severity = request.getParameter("ruleSeverity");
			String ruleTitle = request.getParameter("Title");
			String message = request.getParameter("ruleMessage");	
			String phase = request.getParameter("rulePhase");
			String action = request.getParameter("disruptiveActions");
			String[] operators = request.getParameterValues("operator");
			String target = request.getParameter("targets");
			String conditionValue = request.getParameter("value");
			String resource = request.getParameter("resource");
			
			log.info("User ID:" + userID + "\nRule Group:" + ruleGroup + "\nSeverity:" + severity +
					"\nRule Title:" + ruleTitle + "\nMessage:" + message + "\nPhase:" + phase +
					"\nAction:" + action + "\nTarget" + target + "\nOperator:" + operators[0] + 
					"\nCondition Value:" + conditionValue + "\nResource : " + resource);
			
			log.info("setting values to beans ...");
			Rule rule = null; 
			//ChainRule chainRule = null;
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			
			msg.addAll(FormFieldValidator.validateRule("simple", ruleGroup, ruleTitle, phase, action));
			Resource res = null;
			if(msg.size() > 0){
				status = "1";
			}
			
				User user = SemTranslator.toUserBeans(userID, userID, "");
				MetaData md = SemTranslator.toMetaDataBean( "", message, "1.0", severity, null);
				RuleGroup group = SemTranslator.toRuleGroupBeans( ruleGroup, "");
				group.setUserCreatedBy(user);
				Variable variable = Fetcher.toVariableBean(target, loader);
				Condition cond = SemTranslator.toConditionBean(false, operators[0], conditionValue , variable, null);	
				try {
					res = SemTranslator.toResourceBean(resource, getServletContext());
				} catch (InstantiationException | IllegalAccessException
						| SQLException e) {
					e.printStackTrace();
				}
				int ph = Integer.parseInt(phase);		
				rule = SemTranslator.toRuleBean(ruleTitle, md, cond, action, ph, "" , group);
				rule.setResource(res);
				rule.setUserCreatedBy(user);
				log.info("Beans Created ...");
				
				if(rule instanceof Rule){
					
					if (rulehandler.add(rule) != null) {
						
						log.info("rule stored successfully ...");
						status = "0";
						msg.add("rule added sucessfully");
						
					} else {
						
						status = "1";
						msg.add("Rule is not stored");
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
