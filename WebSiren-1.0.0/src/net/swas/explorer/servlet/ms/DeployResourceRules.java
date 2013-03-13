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
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.swas.explorer.ec.ChainRule;
import net.swas.explorer.ec.Rule;
import net.swas.explorer.ecf.Entity;
import net.swas.explorer.httpprofile.DOProfile;
import net.swas.explorer.ms.service.MSServiceConsumer;
import net.swas.explorer.ms.service.MSServiceProducer;
import net.swas.explorer.ms.service.ModSecService;
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
 * The DeployResourceRules is a Servlet implementation class  for processing deploy resource rule
 * request to remote websiren agent.
 */
@WebServlet("/deployResourceRules")
public class DeployResourceRules extends HttpServlet {
	
	
	private static final long serialVersionUID = 1L;
	private final static Logger log = LoggerFactory.getLogger(DeployResourceRules.class);
	private RuleHandler handler = null;
	private OntologyLoader loader = null;  
	private MSServiceProducer prod = null;
	private MSServiceConsumer cons = null;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DeployResourceRules() {
        super();
        // TODO Auto-generated constructor stub
    }
    public void init(ServletConfig config) throws ServletException {

		super.init(config);
		this.prod = ModSecService.getMSServiceProducer(getServletContext());
		this.cons = ModSecService.getMSServiceConsumer(getServletContext());
		this.loader = OntologyLoader.getOntLoader(getServletContext());
		this.handler = new RuleHandler(loader);
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
		
		String ruleFileString =""; 
		String resource ="";
		String fullResource = "";
		String userID = request.getParameter( "userID");
		String resourceName = request.getParameter( "resource");
		String status = "", msg = "";

		PrintWriter out = response.getWriter();
		JSONObject respJson = new JSONObject();
		JSONObject messageJson = new JSONObject();
		
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		
		if (FormFieldValidator.isLogin(request.getSession()))
		{
			List<Entity> ruleList = handler.getRuleByResource(resourceName, userID);
			DOProfile profile = new DOProfile(getServletContext());
			try {
				resource = profile.getUrlByResource(resourceName);
			} catch (SQLException e) {
				
				e.printStackTrace();
			}
			String[] resources = resource.split("/");
			for (int i = 3; i < resources.length; i++) {
			
					fullResource +=  "/" + resources[i];
				
			}
			
			log.info("Rule List size : " + ruleList.size());
			ruleFileString = "<location "+ fullResource + ">\n";
			for(Entity entity:ruleList){
				log.info("----------------->>>>> RULE");
				Rule rule = (Rule) entity;
				ruleFileString += ModSecTranslator.getRuleString(rule);
			}
			ruleFileString += "</location>";
		
			
			log.info("Rule String :\n" + ruleFileString);
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
