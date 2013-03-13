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

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.swas.explorer.ms.service.MSServiceConsumer;
import net.swas.explorer.ms.service.MSServiceProducer;
import net.swas.explorer.ms.service.ModSecService;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The GetMSConfig is a Servlet implementation class  for processing get modsecurity configuration
 * request to remote websiren agent. It will be blocking call to the remote service which will 
 * wait for a response for 5 seconds. If it didnt get the response it acknowledge an error to the browser. 
 */
@WebServlet("/getMSConfig")
public class GetMSConfig extends HttpServlet {
	
	
	private final static Logger log = LoggerFactory.getLogger(GetMSConfig.class);
	private MSServiceProducer prod = null;
	private MSServiceConsumer cons = null;
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetMSConfig() {
    	
        super();
        
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

		JSONObject messageJson = new JSONObject();
		
		messageJson.put("action", "readMSConfig");
		this.prod.send(messageJson.toJSONString());
		
		String revMsg = this.cons.getReceivedMessage(request.getServletContext());
		log.info("Received Message :" + revMsg);
		if(revMsg != null){
			
			JSONParser parser = new JSONParser();
			JSONObject revJson = null;
			try {
				
				revJson = (JSONObject) parser.parse(revMsg);
				request.removeAttribute("msConfig");
				request.setAttribute("msConfig", revJson);
				
			} catch (ParseException e) {
				
				e.printStackTrace();
			
			}
		
		}
		
		RequestDispatcher rd = request.getRequestDispatcher("/msConfigForm.jsp");
        rd.forward(request, response);
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
