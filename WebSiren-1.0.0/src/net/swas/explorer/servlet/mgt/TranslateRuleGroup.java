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
import java.util.List;

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
import net.swas.explorer.ecf.Entity;
import net.swas.explorer.oh.handler.ChainRuleHandler;
import net.swas.explorer.oh.handler.OntologyHandler;
import net.swas.explorer.oh.handler.RuleHandler;
import net.swas.explorer.oh.lo.OntologyLoader;
import net.swas.explorer.translator.ModSecTranslator;

/**
 * This servlet class is responsible for translating group specific rules from semantic representation
 * to Mod Security representation
 */
@WebServlet("/translateRuleGroup")
public class TranslateRuleGroup extends HttpServlet {

	private final static Logger log = LoggerFactory
			.getLogger(TranslateRuleGroup.class);
	private static final long serialVersionUID = 1L;
	private OntologyLoader loader = null;
	private RuleHandler rulehandler = null;
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public TranslateRuleGroup() {
		super();
	}

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {

		super.init(config);
		loader = OntologyLoader.getOntLoader(getServletContext());
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

		String groupName = request.getParameter("groupName");
		log.info(" Group Name :  " + groupName);
		
		String ruleFileString = "";
		
		List<Entity> ruleList = rulehandler.getRuleByGroup(groupName);
				
		for(Entity entity:ruleList){
			log.info( "Entity : " + entity.toString() );
			
			Rule simpleRule = null;
			ChainRule chainRule = null;
			if( entity instanceof Rule ) {
				simpleRule = (Rule) entity;
				ruleFileString += ModSecTranslator.getRuleString(simpleRule);
			} else if ( entity instanceof ChainRule ) {
				chainRule = (ChainRule) entity;
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
