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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.swas.explorer.util.UserConfiguration;

import org.openjena.atlas.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This servlet class is responsible for the authentication of user
 */
@WebServlet("/authenticate")
public class Authenticate extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final static Logger log = LoggerFactory.getLogger(Authenticate.class);
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Authenticate() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
			
			log.info("Authenticatin user");
			UserConfiguration uConfig = UserConfiguration.getInstance(getServletContext());
			String userFilePath = uConfig.getFilePath();
			
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			
			PrintWriter out = response.getWriter();
			JsonObject json = new JsonObject();
			
			Properties properties = new Properties();
			File f = new File(userFilePath);
			if (f.exists())
			{
		     FileInputStream r = new FileInputStream(userFilePath);
		     properties.load(r);
			}
			
			String status = "";
			String msg = "";
	    	 
	        String name = request.getParameter("userName");  
	        String password= request.getParameter("password");  
	        
	        MessageDigest md = null;
	        StringBuilder hash = new StringBuilder();

			try {
				MessageDigest sha = MessageDigest.getInstance("SHA-1");
				byte[] hashedBytes = sha.digest(password.getBytes());
				char[] digits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
						'a', 'b', 'c', 'd', 'e', 'f' };
				for (int idx = 0; idx < hashedBytes.length; ++idx) {
					byte b = hashedBytes[idx];
					hash.append(digits[(b & 0xf0) >> 4]);
					hash.append(digits[b & 0x0f]);
				}
			} catch (NoSuchAlgorithmException e) {
				// handle error here.
			}
		   //   String hashPassword = digest.toString();
	        try {
	        	
				//ins = getServletContext().getResourceAsStream(configPath);
				//properties.load(ins);
				
				log.info("User Name: "
						+ name
						+ "Passwod:"
						+ hash);
				//log.info("match: " + password.matches(properties.getProperty(name)));
				String matchPass = properties.getProperty(name);
				log.info("password in file: " + matchPass);
		        if( hash.toString().equals(matchPass)){
		        	
		        	
		    	    HttpSession session = request.getSession();
		    	    session.setAttribute("userName", name);
		    	   
		    	    
		        	status = "0";
					msg = "authentication succcessful";
					log.info("Authenticated Successfully for ... " + name + ":" + password );
		        }  
		        else{  
		        	
		        	status = "1";
					msg = "Please enter correct username or password";
					log.info("Authentication failed for ... " + name + ":" + password );
		        
		        }  
		        json.put("status", status);
				json.put("message", msg);
			
	        } catch (Exception e) {
				e.printStackTrace();
			}
	        
			try {
				log.info("Sending Json : " + json.toString());
				out.print(json.toString());
			} finally {
				out.close();
			}
			
	}
    public void doRequest(HttpServletRequest request, HttpServletResponse response) throws IOException{  
        
    }

}
