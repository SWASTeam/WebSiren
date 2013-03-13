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
package net.swas.explorer.servlet.ms;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.swas.explorer.ms.service.MSServiceConsumer;
import net.swas.explorer.ms.service.MSServiceProducer;
import net.swas.explorer.ms.service.ModSecService;
import net.swas.explorer.util.FormFieldValidator;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The GetMSStatus is a Servlet implementation class  for processing get modsecurity status request 
 * to remote websiren agent. It will be blocking call to the remote service which will 
 * wait for a response for 5 seconds. If it didnt get the response it acknowledge an error to the browser. 
 */
@WebServlet("/getMSStatus")
public class GetMSStatus extends HttpServlet {
	
	
	private final static Logger log = LoggerFactory.getLogger(GetMSStatus.class);
	private MSServiceProducer prod = null;
	private MSServiceConsumer cons = null;
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetMSStatus() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		
		super.init(config);
		this.prod = ModSecService.getMSServiceProducer(getServletContext());
		this.cons = ModSecService.getMSServiceConsumer(getServletContext());
	
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	@SuppressWarnings("unchecked")
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String status = "", msg = "";

		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		JSONObject messageJson = new JSONObject();
		JSONObject respJson = new JSONObject();
		
		messageJson.put("action", "status");
		this.prod.send(messageJson.toJSONString());
		
		String revMsg = this.cons.getReceivedMessage(request.getServletContext());
		
		if (FormFieldValidator.isLogin(request.getSession()))
		{
			log.info("Received Message :" + revMsg);
			if(revMsg != null){
				
				JSONParser parser = new JSONParser();
				JSONObject revJson = null;
				try {
					
					revJson = (JSONObject) parser.parse(revMsg);
					respJson = revJson;
					
				} catch (ParseException e) {
					
					status = "1";
					msg = "Unable to reach modsercurity service. Please try later";
					e.printStackTrace();
				
				}
			
			} else{
				
				status = "1";
				msg = "Unable to reach modsercurity service. Please try later";
				log.info("Message is not received......");
				
			}
			
			if(!status.equals("")){
				
				respJson.put("status", status);
				respJson.put("message", msg);
			
			}
		}
		else
		{
			status = "2";
			msg = "User Session Expired";
			respJson.put("status", status);
			respJson.put("message", msg);
		}

		
		try {
			log.info("Sending Json : " +respJson.toString());
			out.print(respJson.toString());
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
