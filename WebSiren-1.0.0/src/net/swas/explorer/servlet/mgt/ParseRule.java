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
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.swas.explorer.ec.RuleGroup;
import net.swas.explorer.ecf.EntityFactory;

import net.swas.explorer.oh.handler.OntologyHandler;
import net.swas.explorer.oh.handler.RuleHandler;
import net.swas.explorer.oh.lo.KBConfiguration;
import net.swas.explorer.oh.lo.OntologyLoader;
import net.swas.explorer.parser.ErrorLogMap;
import net.swas.explorer.parser.ModSecRuleFileLoader;
import net.swas.explorer.parser.ParsingErrors;
import net.swas.explorer.parser.RuleFileLoader;
import net.swas.explorer.util.FormFieldValidator;

import org.apache.tomcat.util.http.fileupload.FileItem;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.json.simple.JSONObject;
import org.openjena.atlas.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This servlet class is responsible for translating Mod Security rules into semantic representation in ontology
 */
@WebServlet(urlPatterns = { "/parseRule" })
public class ParseRule extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private final static Logger log = LoggerFactory.getLogger(ParseRule.class);
	private OntologyHandler handler = null;
	private OntologyLoader loader = null;
	private boolean isMultipart;
	private String filePath;
	private File file;
	private String stat;
	private String msg;
	RuleGroup policy = null;
	String fileName = null;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ParseRule() {
		super();

	}

	public void init() {
		// Get the file location where it would be stored.
		filePath = getServletContext().getRealPath("/");

	}

	@SuppressWarnings("unchecked")
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, java.io.IOException {
		
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		JSONObject json = new JSONObject();
		log.info("IN PARSE RULE SERVLET.........");
		
		if (FormFieldValidator.isLogin(request.getSession()))
		{
			
			String userID = (String) request.getSession().getAttribute("userName");
			// Check that we have a file upload request
			isMultipart = ServletFileUpload.isMultipartContent(request);
			PrintWriter out = response.getWriter();

			if (!isMultipart) {

				return;
			}

			KBConfiguration config = KBConfiguration.getInstance(getServletContext());
			DiskFileItemFactory factory = new DiskFileItemFactory();
			factory.setRepository(new File(config.getUploadFolderPath()));
			// Create a new file upload handler
			ServletFileUpload upload = new ServletFileUpload(factory);

			try {
				// Parse the request to get file items.
				List<FileItem> fileItems = upload.parseRequest(request);

				// Process the uploaded file items
				Iterator<FileItem> i = fileItems.iterator();

				while (i.hasNext()) {

					FileItem fi = (FileItem) i.next();

					if (!fi.isFormField()) {
						// Get the uploaded file parameters
						fileName = fi.getName();
						// Write the file
						if (fileName.lastIndexOf("\\") >= 0) {

							file = new File(
									filePath
											+ fileName.substring(fileName
													.lastIndexOf("\\")));

						} else {

							file = new File(
									filePath
											+ fileName.substring(fileName
													.lastIndexOf("\\") + 1));
						}
						fi.write(file);
						// boolean check = false;
						log.info("parse Rule servlet called......");
						this.loader = OntologyLoader
								.getOntLoader(getServletContext());
						this.handler = new RuleHandler(loader);
						boolean check = ModSecRuleFileLoader.startService(loader,
								(filePath + fileName), fileName, userID);

						log.info("File Name::" + fileName);

						if (check) {
							
							log.info("Rule added...");
							stat = "0";
							msg = "Translated successfully";
							
							String parseErrorID = ErrorLogMap.addLog(ParsingErrors.getParseError());
							json.put("parseErrorID", parseErrorID);
							json.put("noOfErrors", ParsingErrors.getErrorSize());
							json.put("status", stat);
							json.put("message", msg);
							
						} else {
							
							log.info("No Rule added..."); 
							stat = "1";
							msg = "Translation UN-successfully";
							String parseErrorID = ErrorLogMap.addLog(ParsingErrors.getParseError());
							json.put("parseErrorID", parseErrorID);
							json.put("noOfErrors", ParsingErrors.getErrorSize());
							json.put("status", stat);
							json.put("message", msg);
						
						}
						
						
						try {

							log.info("Sending Json : " + json.toString());
							out.print(json.toString());
						} catch (Exception ex) {
							System.out.println(ex);
						}
					}

				}
			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				out.close();
			}
		}
		else {
			stat = "2";
			msg = "User Session Expired";
			json.put("status", stat);
			json.put("message", msg);
		}

	}


	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, java.io.IOException {

		throw new ServletException("GET method used with "
				+ getClass().getName() + ": POST method required.");
	}
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	/*
	 * protected void doGet(HttpServletRequest request, HttpServletResponse
	 * response) throws ServletException, IOException {
	 * 
	 * log.info("parse Rule servlet called......"); PrintWriter out =
	 * response.getWriter(); this.loader =
	 * OntologyLoader.getOntLoader(getServletContext()); this.handler = new
	 * RuleHandler(loader);
	 * 
	 * boolean check = ModSecRuleFileLoader.startService(loader);
	 * 
	 * if (check) { log.info("Rule added..."); //
	 * out.println("<h1> Rule stored successfully ...</h1>"); } else {
	 * log.info("No Rule added..."); //
	 * out.println("<h1> Rule stored UnSuccessfully ...</h1>"); } }
	 *//**
	 * @see HttpServlet#doPost(HttpServletRequest request,
	 *      HttpServletResponse response)
	 */
	/*
	 * protected void doPost(HttpServletRequest request, HttpServletResponse
	 * response) throws ServletException, IOException {
	 * 
	 * factory = new DiskFileItemFactory(); contextRoot =
	 * getServletContext().getRealPath("/"); //String pathName =
	 * filterConfig.getServletContext().getRealPath("/");
	 * 
	 * factory.setRepository(new File(contextRoot + "WEB-INF/tmp"));
	 * 
	 * //getting temporary path of directory String temporaryPath =
	 * System.getProperty("java.io.tmpdir");
	 * 
	 * response.setContentType("text/html"); PrintWriter out =
	 * response.getWriter(); out.println(contextRoot + "<br/>"); boolean
	 * isMultipartContent = ServletFileUpload.isMultipartContent(request); if
	 * (!isMultipartContent) { out.println("You are not trying to upload<br/>");
	 * return; }
	 * 
	 * out.println("You are trying to upload<br/>");
	 * 
	 * 
	 * 
	 * ServletFileUpload upload = new ServletFileUpload(factory);
	 * 
	 * out.println(System.getProperty("java.io.tmpdir") +"<br/>"); try {
	 * List<FileItem> fields = upload.parseRequest(request); Iterator<FileItem>
	 * it = fields.iterator(); if (!it.hasNext()) { return; }
	 * 
	 * while (it.hasNext()) { FileItem fileItem = it.next(); boolean isFormField
	 * = fileItem.isFormField();
	 * 
	 * out.println(contextRoot +"<br/>"); if (isFormField) {
	 * 
	 * } else { out.println(fileItem.toString() + "<br/>");
	 * log.info("parse Rule servlet called......");
	 * 
	 * this.loader = OntologyLoader.getOntLoader(getServletContext());
	 * this.handler = new RuleHandler(loader);
	 * 
	 * boolean check = ModSecRuleFileLoader.startService(loader,
	 * fileItem.getString());
	 * 
	 * if (check) { log.info("Rule added..."); //
	 * out.println("<h1> Rule stored successfully ...</h1>"); } else {
	 * log.info("No Rule added..."); //
	 * out.println("<h1> Rule stored UnSuccessfully ...</h1>"); }
	 * 
	 * }
	 * 
	 * }
	 * 
	 * } catch (FileUploadException e) { e.printStackTrace(); } }
	 */

}
