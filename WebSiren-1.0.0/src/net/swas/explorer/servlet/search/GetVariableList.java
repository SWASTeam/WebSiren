
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
 */package net.swas.explorer.servlet.search;

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
import net.swas.explorer.ec.UserDefinedVariable;
import net.swas.explorer.ecf.Entity;
import net.swas.explorer.oh.fields.ElementMap;
import net.swas.explorer.oh.handler.ElementHandler;
import net.swas.explorer.oh.handler.Fetcher;
import net.swas.explorer.oh.handler.UserDefinedVariableHandler;
import net.swas.explorer.oh.lo.OntologyLoader;
import net.swas.explorer.util.FormFieldValidator;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This servlet class is responsible for retrieving all variables from ontology
 */
@WebServlet("/getVariableList")
public class GetVariableList extends HttpServlet {
	
	
	private static final long serialVersionUID = 1L;
	private final static Logger log = LoggerFactory.getLogger(GetVariableList.class);
	private OntologyLoader loader = null;  
	private ElementHandler handler = null;
	private UserDefinedVariableHandler udvHandler = null;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetVariableList() {
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
		this.udvHandler = new UserDefinedVariableHandler(loader);
		
		
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	@SuppressWarnings("unchecked")
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String type = request.getParameter("type");
		String col = null;
		String status = "", listType = "";
		
		Map< String, List<String>> map = new HashMap< String, List<String>>();
		List<String> udvList = new ArrayList<String>();

		if(request.getParameter("collection") != null){
			col = request.getParameter("collection");
		}
		
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		JSONObject json = new JSONObject();
		log.info("Type : " + type);
		
		if (FormFieldValidator.isLogin(request.getSession()))
		{
			for(Entity entity: udvHandler.getAll()){
				
				UserDefinedVariable udv = (UserDefinedVariable) entity;
				if(!(udv.getName().contains(ElementMap.var_open.toString()) || 
						udv.getName().contains(ElementMap.var_close.toString()) ||
					    udv.getName().contains(ElementMap.op_equal.toString()))){
					udvList.add(udv.getName());
				}
			}
			
			if(type.equals("VariableExpression")){
				
				map.put("StandardVariables", Fetcher.getStandardVariables(loader) );
				map.put("ParsingFlags", Fetcher.getParsingFlags(loader) );
				map.put("RequestVariables", Fetcher.getRequestVariables(loader) );
				map.put("ResponseVariables", Fetcher.getResponseVariables(loader) );
				map.put("ServerVariables", Fetcher.getServerVariables(loader) );
				map.put("TimeVariables", Fetcher.getTimeVariables(loader) );
				map.put("BuiltinVariables", Fetcher.getBuiltInVariables(loader));
				map.put("UserDefinedVariables", udvList);
				listType = "StandardVariables"; 
				status = "0";
				
			} else {
				
				map.put("RequestCollections", Fetcher.getRequestCollection(loader));
				map.put("ResponseCollections", Fetcher.getResponseCollection(loader));
				map.put("Collection", Fetcher.getCollections(loader));
				listType = "CollectionVariables";
				status = "0";
				
				List<String> list = new ArrayList<String>();
				if(col != null){
					
					for(Entity entity:this.handler.getElementByCollection(col)){
						
						Element elmnt = (Element) entity;
						list.add(elmnt.getName());
					}
					
					map.put("Elements", list);
				
				}
				
			}
			
			json.put("status", status);
			json.put("varList", map);
			json.put("type", listType );
		}
		else
		{	
			List<String> errorlist = new ArrayList<String>();
			status = "2";
			String msg = "User Session Expired";
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
		// TODO Auto-generated method stub
	}

}
