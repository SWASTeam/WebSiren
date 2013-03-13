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

import java.io.IOException;

import org.apache.http.HttpException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseFactory;
import org.apache.http.io.HttpMessageParser;
import org.apache.http.io.SessionInputBuffer;
import org.apache.http.params.HttpParams;

/**
 *This class is responsible for creating HTTPResponseParser instance.
 * @author SWASS Team
 */
public class HttpResponseParser {

	 /**
	  * Creates HttpMessageParser type object for HTTP Request parsing
	 * @param buffer created from {@link FileInputBuffer}
	 * @param responseFactory
	 * @param params
	 * @return HttpMessageParser
	 */
	 public static HttpMessageParser createResponseParser(
	            final SessionInputBuffer buffer,
	            final HttpResponseFactory responseFactory,
	            final HttpParams params) {
		 
	        return new DefaultHttpResponseParser(buffer, null, responseFactory, params);
	 }
	 
	 /**
	   * Responsible for parsing HTTP Response
	 * @param responseParser
	 * @return HTTP response 
	 * @throws IOException
	 * @throws HttpException
	 */
	 public static HttpResponse receiveResponseHeaderByFile(HttpMessageParser responseParser) 
	    		throws IOException, HttpException{
		  	
		 HttpResponse response = (HttpResponse) responseParser.parse();
	     return response;
	    	
	  }

}
