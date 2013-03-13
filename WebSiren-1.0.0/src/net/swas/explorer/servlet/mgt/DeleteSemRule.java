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
import org.openjena.atlas.logging.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.swas.explorer.oh.handler.ChainRuleHandler;
import net.swas.explorer.oh.handler.OntologyHandler;
import net.swas.explorer.oh.handler.RuleHandler;
import net.swas.explorer.oh.lo.OntologyLoader;
import net.swas.explorer.util.FormFieldValidator;

/**
 * This servlet class is responsible for removing semantic rules from ontology
 */
@WebServlet("/deleteSemRule")
public class DeleteSemRule extends HttpServlet {

	
	private final static Logger log = LoggerFactory.getLogger(DeleteSemRule.class);
	private static final long serialVersionUID = 1L;
	private OntologyLoader loader = null;
	private RuleHandler ruleHandler = null;
	private ChainRuleHandler chainHandler = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.GenericServlet#init(javax.servlet.ServletConfig)
	 */
	@Override
	public void init(ServletConfig config) throws ServletException {

		super.init(config);
		loader = OntologyLoader.getOntLoader(getServletContext());
		ruleHandler = new RuleHandler(loader);
		chainHandler = new ChainRuleHandler(loader);
		
	}

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public DeleteSemRule() {
		super();
		// TODO Auto-generated constructor stub
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
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
		String[] ruleIDs = request.getParameter("ruleID").split(",");
		String status = "", msg = "";

		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		JsonObject json = new JsonObject();

		if (FormFieldValidator.isLogin(request.getSession()))
		{
			for (String ruleID : ruleIDs) {

				log.info(" RuleID :  " + ruleID);
				if(ruleID.split("\\.")[0].equals(ruleHandler.getClassName())){

					ruleHandler.remove(ruleID.split("\\.")[1]);
					
				} else {

					chainHandler.remove(ruleID.split("\\.")[1]);
					
				}

			}

			status = "0";
			msg = "removed successfully";
		}
		else
		{
			status = "2";
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
