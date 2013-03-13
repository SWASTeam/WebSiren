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
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.swas.explorer.ec.RuleGroup;
import net.swas.explorer.httpprofile.Profile;
import net.swas.explorer.oh.lo.KBConfiguration;
import net.swas.explorer.util.FormFieldValidator;

import org.apache.tomcat.util.http.fileupload.FileItem;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This servlet class is responsible for adding new HTTP Profile in ontology
 */
@WebServlet("/addHttpPolicy")
public class AddHttpPolicy extends HttpServlet {
	
	
	private static final long serialVersionUID = 1L;
	private final static Logger log = LoggerFactory.getLogger(AddHttpPolicy.class);
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
    public AddHttpPolicy() {
        super();
        // TODO Auto-generated constructor stub
    }
    public void init() {
		// Get the file location where it would be stored.
		filePath = getServletContext().getRealPath("/");

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
	@SuppressWarnings("unchecked")
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String userID = request.getParameter("userID");
		
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		JSONObject json = new JSONObject();
		log.info("IN DATA SET PARSING SERVLET.........");
		
		if (FormFieldValidator.isLogin(request.getSession()))
		{
			log.info("session valid.........");
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
						boolean check = true;
						log.info("parse data set servlet called......");
						log.info("File Name::" + filePath +  fileName);
						check =  Profile.parseRequestByFile(filePath + fileName, getServletContext());


						if (check) {
							
							log.info("dataSet added...");
							stat = "0";
							msg = "Translated successfully";
							
							json.put("status", stat);
							json.put("message", msg);
							
						} else {
							
							log.info("No dataSet added..."); 
							stat = "1";
							msg = "Translation UN-successfully";
				
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

}
