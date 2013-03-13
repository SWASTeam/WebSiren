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
package net.swas.explorer.httpprofile;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.impl.DefaultHttpRequestFactory;
import org.apache.http.io.HttpMessageParser;
import org.apache.http.io.SessionInputBuffer;
import org.apache.http.params.BasicHttpParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is responsible for generating HTTP Profile from HTTP dump file
 */
public class Profile {
	
	
	private final static Logger log = LoggerFactory.getLogger(Profile.class);
	
	/**
	 * @param fileName fileName of HTTP dump file
	 * @param context context is for capturing the knowledge base configuration.
	 * @return boolean
	 * @throws IOException
	 * @throws HttpException
	 * @throws SQLException
	 */
	public static boolean parseRequestByFile(String fileName, ServletContext context) throws IOException,
			HttpException, SQLException {
		
		
		log.info("In Parse Request by file");
		boolean check = false;
		HttpMessageParser requestParser;
		List<HttpRequest> request = new ArrayList<HttpRequest>();

		@SuppressWarnings("resource")
		BufferedReader br = new BufferedReader(new FileReader(fileName));

		List<String> requestList = new ArrayList<String>();
		String requestString = "";
		String line = "";
		line = br.readLine();
		int i = 0;
		while (line != null) {

			if (line.startsWith("GET") || line.startsWith("POST")) {
				if (i == 0) {
					i = 1;
				} else {
					requestList.add(requestString);
				}
				requestString = "";
				requestString = "    " + requestString + line + "\r\n";
			} else {
				if (line.contains(":")) {
					requestString = requestString + line + "\r\n";
				} else if (!line.equals("")) {
					requestString = requestString + "SSRG: " + line + "\r\n";
				}

			}
			line = br.readLine();
		}
		for (String string : requestList) {

			SessionInputBuffer inbuffer = new FileInputBuffer(string, 1024,
					new BasicHttpParams());
			requestParser = HttpRequestParser.createRequestParser(inbuffer,
					new DefaultHttpRequestFactory(), new BasicHttpParams());
			HttpRequest httpRequest = HttpRequestParser
					.receiveRequestHeaderByFile(requestParser);
			request.add(httpRequest);
		}
		
		ArrayList<String> urls = null;
		DOProfile profile = new DOProfile(context);
		profile.insertRequest(request);
		log.info("Rquest Inserted");

		urls = (ArrayList<String>) profile.getUrl();
		if (urls.size() > 0) {
			check = true;
			profile.insertPairs(urls);
		} else {
			check = false;
		}

		log.info("Pairs inserted");

		return check;

	}	
	
}