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
package net.swas.explorer.servlet.search;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.swas.explorer.ec.Element;
import net.swas.explorer.ecf.Entity;
import net.swas.explorer.oh.fields.ElementMap;
import net.swas.explorer.oh.handler.ElementHandler;
import net.swas.explorer.oh.handler.Fetcher;
import net.swas.explorer.oh.lo.OntologyLoader;
import net.swas.explorer.util.FormFieldValidator;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This servlet class is responsible for retrieving all elements of collection expression from ontology
 */
@WebServlet("/getElementList")
public class GetElementList extends HttpServlet {
	
	
	private static final long serialVersionUID = 1L;
	private final static Logger log = LoggerFactory.getLogger(GetElementList.class);
	private OntologyLoader loader = null;  
	private ElementHandler handler = null;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetElementList() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		
		super.init(config);
		this.loader = OntologyLoader.getOntLoader(getServletContext());
		this.handler = new ElementHandler(this.loader);
		
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String col = request.getParameter("collection");
		String status = "", listType = "";
		
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		JSONObject json = new JSONObject();
		List<String> list = new ArrayList<String>();
		if (FormFieldValidator.isLogin(request.getSession()))
		{
			log.info("Type : " + col);
			for(Entity entity:this.handler.getElementByCollection(col)){
				
				Element elmnt = (Element) entity;
				String name = elmnt.getName();
				
				if(name.equals(ElementMap.all_xml_elements.toString())){
					name = ElementMap.all_xml_elements.toString();
				}
				
				list.add(name);
			}
			status = "0";
		}
		else
		{
			status = "2";
			list.add("User Session Expired");
		}
		
		
		json.put("status", status);
		json.put("elementList", list);
		
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
