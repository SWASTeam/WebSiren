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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.swas.explorer.ec.User;
import net.swas.explorer.ecf.Entity;
import net.swas.explorer.ecf.EntityFactory;
import net.swas.explorer.oh.handler.OntologyHandler;
import net.swas.explorer.oh.handler.RuleGroupHandler;
import net.swas.explorer.oh.handler.UsersHandler;
import net.swas.explorer.oh.lo.OntologyLoader;
import net.swas.explorer.util.UserConfiguration;

import org.openjena.atlas.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This servlet class is responsible for registering new user
 */
@WebServlet("/register")
public class Register extends HttpServlet {
	
	
	private static final long serialVersionUID = 1L;
	private OntologyHandler handler = null;
	private OntologyLoader loader = null;
	private final static Logger log = LoggerFactory.getLogger(Register.class);

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Register() {
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
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		log.info("in register Servlet");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		log.info("in register Servlet");
		UserConfiguration uConfig = UserConfiguration.getInstance(getServletContext());
		String userFilePath = uConfig.getFilePath();
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		
		Properties prop = new Properties();
		MessageDigest md = null;
		byte[] digest = null;
		PrintWriter out = response.getWriter();
		JsonObject json = new JsonObject();
		
		String status = "";
		String msg = "";
		String name = request.getParameter("userName");
		String password = request.getParameter("password");
		String displayName = request.getParameter("displayName");
		String confirmPassword = request.getParameter("confirmPassword");

		log.info("User Name : " + name + " , Password : " + password
				+ " , Display Name : " + displayName + " , Confirm password : "
				+ confirmPassword);

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

		String newhash = hash.toString();
		try {
			File f = new File(userFilePath);
			if (f.exists()) {
				
				FileInputStream r = new FileInputStream(
						userFilePath);
				prop.load(r);
				
			} else
				f.createNewFile();
			prop.setProperty(name, newhash);
			prop.store(new FileOutputStream(userFilePath), null);
			log.info("user inserted in file");

			if (name.length() != 0 && password.length() != 0
					&& displayName.length() != 0
					&& confirmPassword.length() != 0
					&& password.matches(confirmPassword)) {
				User user = (User) EntityFactory.USER.getObject();
				user.setUserName(name);
				user.setDisplayName(displayName);
				user.setPreviousName(name);

				if (handler.add(user) != null) {
					log.info("User added successfully ...");
					status = "0";
					msg = "User added successfully";
				} else {
					status = "1";
					msg = "Un-succcesful";
				}
			} else {

				status = "1";
				msg = "please fill out blank field";
			}

		} catch (InstantiationException | IllegalAccessException ex) {
			ex.printStackTrace();
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
