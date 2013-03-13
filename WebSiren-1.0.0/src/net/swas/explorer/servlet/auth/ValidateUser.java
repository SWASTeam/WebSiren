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
package net.swas.explorer.servlet.auth;

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

import net.swas.explorer.oh.handler.OntologyHandler;
import net.swas.explorer.oh.handler.UsersHandler;
import net.swas.explorer.oh.lo.OntologyLoader;

import org.openjena.atlas.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

/**
 * This servlet class is responsible for validating the entered information of user 
 * 
 */
@WebServlet("/validateUser")
public class ValidateUser extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private OntologyHandler handler = null;
	private OntologyLoader loader = null;
	private final static Logger log = LoggerFactory.getLogger(ValidateUser.class);  
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ValidateUser() {
        super();
        // TODO Auto-generated constructor stub
    }
    /**
   	 * @see Servlet#init(ServletConfig)
   	 */
   	public void init(ServletConfig config) throws ServletException {
   		super.init(config);
   		this.loader = OntologyLoader.getOntLoader(getServletContext());
   		this.handler = new UsersHandler(loader);
   	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		log.info("In validate user servlet");
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		String name = request.getParameter("userName"); 
		JsonObject json = new JsonObject();
		String status = "";
		String msg ="";
		String NS = loader.getConfiguration().getRuleEngineNameSpace() + OntologyHandler.NS_POSTFIX;
		List<String> userList = new ArrayList<String>();
		OntModel model = loader.getModel();

		//log.info(" ClassName : " + className + " : Name Space :" + NS);

		OntClass userClass = model.createClass(NS + "User");
		ExtendedIterator<Individual> userInds = (ExtendedIterator<Individual>) userClass.listInstances();
		
		while (userInds.hasNext()) {

			Individual userInd = userInds.next();
			userList.add(userInd.getLocalName());

		}
		
		for (String string : userList) {
			if (string.equals(name))
			{
				status ="1";
				msg = "User Already Exist";
			}
			else {
				status = "0";
				msg = "Unique User Name";
			}
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

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
