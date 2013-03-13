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

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openjena.atlas.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.swas.explorer.oh.handler.OntologyHandler;
import net.swas.explorer.oh.handler.RuleGroupHandler;
import net.swas.explorer.oh.handler.RuleHandler;
import net.swas.explorer.oh.lo.OntologyLoader;
import net.swas.explorer.util.FormFieldValidator;

/**
 * This servlet class is responsible for removing all rules based on specified group from ontology
 */
@WebServlet("/deleteRuleGroup")
public class DeleteRuleGroup extends HttpServlet {
	
	
	private static final long serialVersionUID = 1L;
	private final static Logger log = LoggerFactory.getLogger(DeleteRuleGroup.class);
	private OntologyLoader loader = null;
	private OntologyHandler handler = null;
	private RuleHandler ruleHandler = null;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DeleteRuleGroup() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		
		super.init(config);
		loader = OntologyLoader.getOntLoader(getServletContext());
		handler = new RuleGroupHandler(loader);
		ruleHandler = new RuleHandler(loader);
		
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
	
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		JsonObject json = new JsonObject();
		String status = "", msg = "";
		
		if (FormFieldValidator.isLogin(request.getSession()))
		{
			String groupName = request.getParameter("groupName");

			log.info(" RuleID :  " + groupName);
			
			if(groupName.equals("") || groupName == null) {
				
				status = "1";
				msg = "select a group first!";
				
			} else if(ruleHandler.getRuleByGroup(groupName, "").size() > 0){
				
				status="1";
				msg = "group already have rules into delete those rules first";
				
			}
			
			if(status.equals("")){
				handler.remove(groupName);
				status = "0";
				msg = "removed successfully";
			}
		}
		else
		{
			status="2";
			msg = "User Session Expired";
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

