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
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.swas.explorer.ec.RuleGroup;
import net.swas.explorer.ec.User;
import net.swas.explorer.ecf.Entity;
import net.swas.explorer.oh.handler.OntologyHandler;
import net.swas.explorer.oh.handler.RuleGroupHandler;
import net.swas.explorer.oh.lo.OntologyLoader;
import net.swas.explorer.translator.SemTranslator;
import net.swas.explorer.util.FormFieldValidator;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This servlet class is responsible for adding new rule based on specified group in ontology
 */
@WebServlet("/addRuleFormGroup")
public class AddRuleFormGroup extends HttpServlet {
	
	
	private static final long serialVersionUID = 1L;
	private final static Logger log = LoggerFactory.getLogger(AddRuleFormGroup.class);
	private OntologyHandler handler = null;
	private OntologyLoader loader = null;   
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AddRuleFormGroup() {
        super();
    }

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		this.loader = OntologyLoader.getOntLoader(getServletContext());
		this.handler = new RuleGroupHandler(loader);
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	@SuppressWarnings("unchecked")
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		log.info("Getting form parameters .. ");
		
		String name = request.getParameter("name");
		String status = "", msg = "";
		List<String> groupList = new ArrayList<String>();

		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		JSONObject json = new JSONObject();
		
		if (FormFieldValidator.isLogin(request.getSession())) {
			
			String userID = (String) request.getSession().getAttribute("userName");
			if (name.length() != 0) {

				log.info("Group Name : " + name);
				name = name.replace(" ", "_");
				User user = SemTranslator.toUserBeans(userID, "", "");

				RuleGroup group = SemTranslator.toRuleGroupBeans(name, "");
				group.setUserCreatedBy(user);
				if (handler.add(group) != null) {

					log.info("Group stored successfully ...");
					status = "0";
					msg = "Group added successfully";

					for (Entity entity : handler.getAll()) {
						RuleGroup rg = (RuleGroup) entity;
						groupList.add(rg.getName());
					}

					json.put("groups", groupList);
					json.put("selected", name);

				} else {
					status = "1";
					msg = "Un-succcesful";
				}
			} else {
				status = "1";
				msg = "please fill out blank field";
			}
		} else {
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
