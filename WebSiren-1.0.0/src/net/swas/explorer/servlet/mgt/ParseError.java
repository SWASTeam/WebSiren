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
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.swas.explorer.parser.ErrorLogMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This servlet class is responsible for identifying the errors during translation from Mod Sec representation to 
 * semantic representation
 */
@WebServlet("/parseError")
public class ParseError extends HttpServlet {
	
	
	private static final long serialVersionUID = 1L;
	private final static Logger log = LoggerFactory.getLogger(ParseError.class);   
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ParseError() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//TODO
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		// request parameters
		log.info("Parse Error Request Received");
		String errorID = request.getParameter("parseErrorID");
		
		log.info("Rule Error String : " + errorID);
		String errors = ErrorLogMap.getLog(errorID.trim());
		
		response.setContentType("text/plain");
		response.setHeader("Content-Disposition",
				"attachment; filename=modsecRuleParseError.config");
		response.setContentLength(errors.getBytes().length);

		
		OutputStream out = response.getOutputStream();
		out.write(errors.getBytes());

		ErrorLogMap.remove(errors.trim());
		out.flush();
		out.close();
		
	}

}
