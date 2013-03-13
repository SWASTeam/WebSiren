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
package net.swas.explorer.websocket;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;


import org.apache.catalina.websocket.StreamInbound;
import org.apache.catalina.websocket.WebSocketServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This WSService class is created for initializing websocket server. This websocket server will then listen
 * and send message to GUI. The web socket connection is used by audit event service to push all the 
 * audit logs coming from modsecurity to the GUI. 
 * 
 * WS stands for WebSockets in class name 
 */
@WebServlet(urlPatterns="/websocket/wsService")
public class WSService extends WebSocketServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final static Logger log = (Logger) LoggerFactory.getLogger(WSService.class);
	
	
	/** (non-Javadoc)
	 * @see org.apache.catalina.websocket.WebSocketServlet#verifyOrigin(java.lang.String)
	 */
	@Override
    protected boolean verifyOrigin(String origin) {
        
		log.info("Origin:" + origin);
        return true;
    
	}

	
	/** (non-Javadoc)
	 * @see org.apache.catalina.websocket.WebSocketServlet#createWebSocketInbound(java.lang.String, javax.servlet.http.HttpServletRequest)
	 */
	@Override
	protected StreamInbound createWebSocketInbound(String subProtocol,
			HttpServletRequest request) {
		
		String connectionID = request.getSession().getId();
		return new WSServiceInbound(connectionID);
	}

}
