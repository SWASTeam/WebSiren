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

import org.apache.http.ConnectionClosedException;
import org.apache.http.HttpException;
import org.apache.http.HttpMessage;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponseFactory;
import org.apache.http.ParseException;
import org.apache.http.StatusLine;
import org.apache.http.impl.io.AbstractMessageParser;
import org.apache.http.io.SessionInputBuffer;
import org.apache.http.message.LineParser;
import org.apache.http.message.ParserCursor;
import org.apache.http.params.HttpParams;
import org.apache.http.util.CharArrayBuffer;

public class DefaultHttpResponseParser extends AbstractMessageParser {

	private final HttpResponseFactory responseFactory;
	private final CharArrayBuffer lineBuf;
	
    /**
     * Creates an instance of this class.
     *
     * @param buffer the session input buffer.
     * @param parser the line parser.
     * @param responseFactory the factory to use to create http response.
     * @param params HTTP parameters.
     */
	public DefaultHttpResponseParser(SessionInputBuffer buffer,
			LineParser parser, HttpResponseFactory responseFactory, HttpParams params) 
	{
		
		super(buffer, parser, params);
		if (responseFactory == null) {
           
			throw new IllegalArgumentException("Response factory may not be null");
        
		}
        
		this.responseFactory = responseFactory;
        this.lineBuf = new CharArrayBuffer(128);
	}

	@Override
	protected HttpMessage parseHead(SessionInputBuffer sessionBuffer)
			throws IOException, HttpException, ParseException {
		
		this.lineBuf.clear();
        int i = sessionBuffer.readLine(this.lineBuf);
        
        if (i == -1) {
        	
            throw new ConnectionClosedException("Client closed connection");
        
        }
        
        ParserCursor cursor = new ParserCursor(0, this.lineBuf.length());
        StatusLine statusLine = this.lineParser.parseStatusLine(this.lineBuf, cursor);
        return this.responseFactory.newHttpResponse(statusLine, null);
        
	}

	
}
