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
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.http.impl.io.AbstractSessionInputBuffer;
import org.apache.http.io.EofSensor;
import org.apache.http.params.HttpParams;


/**
 *This class is responsible for creating buffer for HTTP request and response parsing 
 * @author SWASS Team
 */
public class FileInputBuffer extends AbstractSessionInputBuffer implements EofSensor {
	
	private String request;
	
	/**
	 * @param request
	 * @param bufferSize
	 * @param params
	 * @throws IOException
	 */
	public FileInputBuffer(String request, int bufferSize, final HttpParams params) throws IOException {
		
		super();
		if (request == null) {
			
			throw new IllegalArgumentException(	"File input stream may not be null");
		
		}
		
		this.request = request;
		if (bufferSize < 0) {
			
			bufferSize = request.length();
		
		}
		
		if (bufferSize < 1024) {
			
			bufferSize = 1024;
		
		}
		
		InputStream is = IOUtils.toInputStream(this.request);
		init(is, bufferSize, params);
	
	}
	

	@Override
    protected int fillBuffer() throws IOException {
     
		int i = super.fillBuffer();
        return i;
        
    }
	
	@Override
	public boolean isDataAvailable(int timeout) throws IOException {
		return false;
	}

	@Override
	public boolean isEof() {
		return false;
	}
	

}
