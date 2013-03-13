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

import net.swas.explorer.ec.RuleGroup;
import net.swas.explorer.ec.User;
import net.swas.explorer.ecf.EntityFactory;
import net.swas.explorer.oh.handler.OntologyHandler;
import net.swas.explorer.oh.handler.RuleGroupHandler;
import net.swas.explorer.oh.lo.OntologyLoader;
import net.swas.explorer.translator.SemTranslator;
import net.swas.explorer.util.FormFieldValidator;

import org.openjena.atlas.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This servlet class is responsible for updation of group in ontology
 */
@WebServlet("/editRuleGroup")
public class EditRuleGroup extends HttpServlet {
	private final static long serialVersionUID = 1L;
	private final static Logger log = LoggerFactory.getLogger(AddSemRule.class);
	private OntologyHandler handler = null;
	private OntologyLoader loader = null;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public EditRuleGroup() {
        super();
        // TODO Auto-generated constructor stub
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
			log.info("Getting form parameters .. " + (String) request.getSession().getAttribute("userName"));
			// request parameters
			String userID = (String) request.getSession().getAttribute("userName");
			String groupName = request.getParameter("groupTitle");
			String groupDescription = request.getParameter("groupDescription");
			String previousGroup = request.getParameter("previousGroup");

			groupName = groupName.replace(" ", "_");
			log.info("\nPrevious Group Name: " + previousGroup + "\nGroup Name : " + groupName + "\tRule Description : " + groupDescription);

			try {
				log.info("setting Group values to beans ...");
				User user = SemTranslator.toUserBeans(userID, "", "");
				RuleGroup group = (RuleGroup) EntityFactory.GROUP.getObject();
				
				group.setPreviousGroup(previousGroup);
				group.setName(groupName);
				group.setDescription(groupDescription);
				group.setUserEditedBy(user);
				
				if (handler.update(group) != null) {
					log.info("Group stored successfully ...");
					status = "0";
					msg = "Group updated successfully";
				} else {
					status = "1";
					msg = "Un-succcesful";
				}

			} catch (InstantiationException | IllegalAccessException e) {

				e.printStackTrace();
			}
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
