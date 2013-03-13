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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.swas.explorer.ms.events.AuditLogCache;
import net.swas.explorer.util.FormFieldValidator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.openjena.atlas.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.sparql.pfunction.library.listIndex;

/**
 * The GetAuditLog is a Servlet implementation class  for processing get audit logs request and sends back
 * the list of audit logs in a cache. 
 */
@WebServlet("/getAuditLog")
public class GetAuditLog extends HttpServlet {
	
	private final static Logger log = LoggerFactory.getLogger(GetAuditLog.class);
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetAuditLog() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	@SuppressWarnings("unchecked")
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		
		try {
			
			JSONObject respJson = new JSONObject();
			
			if(AuditLogCache.isAELInitialized()){
				
				if(AuditLogCache.getAEL().size() > 0){
				
					Queue<JSONObject> list = AuditLogCache.getAEL();
					
					out.print(list.toString());
					log.info("GetAudit Already have : " + list.toString());
					//AuditLogCache.flushAEL();
				
				} else{
					
					respJson.put("status", "1");
					log.info("Sending Json : " +respJson.toString());
					out.print(respJson.toString());
				
				}
				
			} else{
				
				respJson.put("status", "1");
				log.info("Sending Json : " +respJson.toString());
				out.print(respJson.toString());
			}
			
	
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
