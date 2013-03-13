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

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.HashMap;
import java.util.Map;

import org.apache.catalina.websocket.MessageInbound;
import org.apache.catalina.websocket.WsOutbound;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The WSServiceInbound is used to listen messages coming from GUI and produce messages to GUI.
 * It also handles all the events related to websocket connection like {@link #onOpen(WsOutbound)}
 * and {@link #onClose(int)}.
 * WS stands for WebSockets in class name 
 */
public class WSServiceInbound extends MessageInbound {

	
	private final static Logger log = (Logger) LoggerFactory.getLogger(WSServiceInbound.class);
	private static Map<String, WsOutbound> connMap = new HashMap<String, WsOutbound>();
 	private String connectionID;
	
 	
	/**
	 * Constructor which soecifies connection ID.
	 * @param connectionID
	 */
	public WSServiceInbound(String connectionID){
		
		this.connectionID = connectionID;
		
	}
	
	/** 
	 * @see org.apache.catalina.websocket.StreamInbound#onOpen(org.apache.catalina.websocket.WsOutbound)
	 */
	@Override
    protected void onOpen(WsOutbound outbound) {
        
		log.info("Connection Open: " + outbound.toString());
        connMap.put(this.connectionID, outbound);
		
    }

    /**
     * @see org.apache.catalina.websocket.StreamInbound#onClose(int)
     */
    @Override
    protected void onClose(int status) {
    	
        log.info("Conection Close : " + status);
        connMap.remove(this.connectionID);
        
    }
    

    /**
     * @see org.apache.catalina.websocket.MessageInbound#onBinaryMessage(java.nio.ByteBuffer)
     */
    @Override
    protected void onBinaryMessage(ByteBuffer byteBuffer) throws IOException {
        
    	log.warn("system does not support binary messages");
    
    }

    /**
     * @see org.apache.catalina.websocket.MessageInbound#onTextMessage(java.nio.CharBuffer)
     */
    @Override
    protected void onTextMessage(CharBuffer charBuffer) throws IOException {
        
    	//String message = charBuffer.toString();
    
    }

    
    /**
     * broadcasts messages  to all the clients.
     * @param message need to be broadcasted.
     */
    public static void broadcast(String message){
    	
    	for(String connID: connMap.keySet()){
    		
    		WsOutbound out = connMap.get(connID); 
    		try {
				
    			out.writeTextMessage(CharBuffer.wrap(message));
			
    		} catch (IOException e) {
    			
    			log.info("unable to send data via websocket to  :" + connID);
				//e.printStackTrace();
			}
    		
    	}
    	
    }
    
}
