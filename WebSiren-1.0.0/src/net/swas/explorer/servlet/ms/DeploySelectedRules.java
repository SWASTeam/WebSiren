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
package net.swas.explorer.servlet.ms;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.swas.explorer.ec.ChainRule;
import net.swas.explorer.ec.Rule;
import net.swas.explorer.ms.service.MSServiceConsumer;
import net.swas.explorer.ms.service.MSServiceProducer;
import net.swas.explorer.ms.service.ModSecService;
import net.swas.explorer.oh.handler.ChainRuleHandler;
import net.swas.explorer.oh.handler.RuleHandler;
import net.swas.explorer.oh.lo.OntologyLoader;
import net.swas.explorer.translator.ModSecTranslator;
import net.swas.explorer.util.FormFieldValidator;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The DeploySelectedRules is a Servlet implementation class  for processing deploy list of selected rules 
 * request to remote websiren agent. It will be blocking call to the remote service which will 
 * wait for a response for 5 seconds. If it didnt get the response it acknowledge an error to the browser. 
 */
@WebServlet("/deploySelectedRules")
public class DeploySelectedRules extends HttpServlet {
	
	
	private final static Logger log = LoggerFactory.getLogger(DeploySelectedRules.class);
	private MSServiceProducer prod = null;
	private MSServiceConsumer cons = null;
	private OntologyLoader loader = null;
	private RuleHandler rulehandler = null;
	private ChainRuleHandler chRuleHandler = null;
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DeploySelectedRules() {
        super();
    }
    

	/* (non-Javadoc)
	 * @see javax.servlet.GenericServlet#init(javax.servlet.ServletConfig)
	 */
	@Override
	public void init(ServletConfig config) throws ServletException {
		
		super.init(config);
		this.prod = ModSecService.getMSServiceProducer(getServletContext());
		this.cons = ModSecService.getMSServiceConsumer(getServletContext());
		loader = OntologyLoader.getOntLoader(getServletContext());
		rulehandler = new RuleHandler(loader);
		chRuleHandler = new ChainRuleHandler(loader);
	
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
	@SuppressWarnings("unchecked")
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		
		String[] ruleIDs = request.getParameter("ruleID").split(",");
		String ruleFileString = "";
		String status = "", msg = "";

		PrintWriter out = response.getWriter();
		JSONObject respJson = new JSONObject();
		JSONObject messageJson = new JSONObject();
		
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		
		if (FormFieldValidator.isLogin(request.getSession()))
		{
			//get the rule file string 
			for (String ruleID : ruleIDs) {

				log.info(" RuleID :  " + ruleID);
				if(ruleID.split("\\.")[0].equals("Rule")){
					
					log.info("----------------->>>>> RULE");
					Rule rule = (Rule) rulehandler.get(ruleID.split("\\.")[1]);
					ruleFileString += ModSecTranslator.getRuleString(rule);
					
				} else {

					log.info("----------------->>>>> CHAIN RULE");
					ChainRule chainRule = (ChainRule) chRuleHandler.get(ruleID.split("\\.")[1]);	
					ruleFileString += ModSecTranslator.getRuleString(chainRule);
				
				}
				
			}
			
			//produce message
			messageJson.put("action", "deployRules");
			messageJson.put("ruleString", ruleFileString);
			this.prod.send(messageJson.toJSONString());
			
			//consume message 
			String revMsg = this.cons.getReceivedMessage(request.getServletContext());
			log.info("Received Message :" + revMsg);
			if(revMsg != null){
				
				JSONParser parser = new JSONParser();
				JSONObject revJson = null;
				try {
					
					revJson = (JSONObject) parser.parse(revMsg);
					respJson = revJson;
					
				} catch (ParseException e) {
					
					status = "1";
					msg = "Unable to reach modsercurity service. Please try later";
					e.printStackTrace();
				
				}
			
			} else{
				
				status = "1";
				msg = "Unable to reach modsercurity service. Please try later";
				log.info(">>>>>>>>>   Message is not received......");
				
			}
			
			if(!status.equals("")){
				
				respJson.put("status", status);
				respJson.put("message", msg);
			
			}
			
		}
		else
		{
			status = "2";
			msg = "User Session Expired";
			respJson.put("status", status);
			respJson.put("message", msg);
		}

		try {
			log.info("Sending Json : " +respJson.toString());
			out.print(respJson.toString());
		} finally {
			out.close();
		}
		
	}

}
