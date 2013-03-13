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
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.swas.explorer.util.FormFieldValidator;

import org.openjena.atlas.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This servlet class is responsible for validating the entered information of group during group creation
 */
@WebServlet("/validateAddGroup")
public class ValidateAddGroup extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final static Logger log = LoggerFactory.getLogger(ValidateAddGroup.class);   
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ValidateAddGroup() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doRequest(request, response);
	}

	/**
	 * @param request
	 * @param response
	 * @throws IOException 
	 */
	private void doRequest(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		
		log.info("validate add group");
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		
		PrintWriter out = response.getWriter();
		JsonObject json = new JsonObject();
		//String configPath = "\\WEB-INF\\users";
		//InputStream ins;
	//	Properties properties = new Properties();
		
		String status = "";
		String msg = "";
		
		if (FormFieldValidator.isLogin(request.getSession()))
		{
	    	
	        String name = request.getParameter("groupTitle");  
	        String description= request.getParameter("groupDescription");  
	        
	        try {
	        	
				//ins = getServletContext().getResourceAsStream(configPath);
				//properties.load(ins);
				
			//	log.info("User Name: "
				//		+ properties.getProperty("userName")
				//		+ "Passwod:"
					//	+ properties.getProperty("password"));
				
		        if(name.length()!=0){
		        	
		        	
		    	//    HttpSession session = request.getSession();
		    	//    session.setAttribute("userName", name);
		    	//    
		        	status = "0";
					msg = "verified";
					//log.info("Authenticated Successfully for ... " + name + ":" + password );
		        }  
		        else{  
		        	
		        	status = "1";
					msg = "Please enter correct group name";
					//log.info("Authentication failed for ... " + name + ":" + password );
		        
		        }  
		        
			
	        } catch (Exception e) {
				e.printStackTrace();
			}
	        
		}
		else
		{
			status = "1";
			msg = "User Session Expired";
			json.put("status", status);
			json.put("message", msg);
		}

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
		doRequest(request, response);
	}

}
