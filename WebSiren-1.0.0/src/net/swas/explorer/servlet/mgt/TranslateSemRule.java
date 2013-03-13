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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.swas.explorer.ec.ChainRule;
import net.swas.explorer.ec.Rule;
import net.swas.explorer.oh.handler.ChainRuleHandler;
import net.swas.explorer.oh.handler.OntologyHandler;
import net.swas.explorer.oh.handler.RuleHandler;
import net.swas.explorer.oh.lo.OntologyLoader;
import net.swas.explorer.translator.ModSecTranslator;

/**
 * This servlet class is responsible for translating rules from semantic representation
 * to Mod Security representation
 */
@WebServlet("/translateSemRule")
public class TranslateSemRule extends HttpServlet {

	private final static Logger log = LoggerFactory
			.getLogger(TranslateSemRule.class);
	private static final long serialVersionUID = 1L;
	private OntologyLoader loader = null;
	private OntologyHandler rulehandler = null;
	private OntologyHandler chRuleHandler = null;
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public TranslateSemRule() {
		super();
	}

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {

		super.init(config);
		loader = OntologyLoader.getOntLoader(getServletContext());
		chRuleHandler = new ChainRuleHandler(loader);
		rulehandler = new RuleHandler(loader);
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
		String ruleFileString = "";

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
		
		log.info("context :" + getServletContext().getMimeType("text/plain"));

		response.setContentType("text/plain");
		response.setHeader("Content-Disposition",
				"attachment; filename=modsecRule.config");
		response.setContentLength(ruleFileString.getBytes().length);

		log.info("Rule String : \n\n \t" + ruleFileString);

		OutputStream out = response.getOutputStream();
		out.write(ruleFileString.getBytes());

		out.flush();
		out.close();

	}

}
