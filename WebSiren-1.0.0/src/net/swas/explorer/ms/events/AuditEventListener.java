package net.swas.explorer.ms.events;

import java.util.concurrent.ConcurrentLinkedQueue;

import javax.jms.Message;
import javax.jms.MessageListener;


import net.swas.explorer.util.MessageUtil;
import net.swas.explorer.websocket.WSServiceInbound;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A AMQ Listener class for listening audit log events coming from websiren agent.
 * */
public class AuditEventListener implements MessageListener{
	
	
	private final static Logger log = LoggerFactory.getLogger(AuditEventListener.class);
	
	/**(non-Javadoc)
	 * @see javax.jms.MessageListener#onMessage(javax.jms.Message)
	 */
	@Override
	public void onMessage(Message msg) {
		
		String msgString = MessageUtil.getMessageString(msg);
		log.info("Message Received :" + msgString);
		onAuditEvent(msgString);
		
	}
	
	/**
	 * Converts the string message into json object and then extract events from it.
	 * @param evtStr event string
	 */
	public void onAuditEvent(String evtStr){
		
		//log.info("onAuditEvent : " + evtStr);

		try {
			
			JSONParser parser = new JSONParser();
			JSONObject revObj = (JSONObject) parser.parse(evtStr);
			
			JSONObject respObj = parseAuditEvent(revObj);
			
			if(!AuditLogCache.isAELInitialized()){
				AuditLogCache.addAEL(new ConcurrentLinkedQueue<JSONObject>());
			}
			
			if(AuditLogCache.getAEL().size() == 15){
				AuditLogCache.getAEL().poll();
			} 
			
			AuditLogCache.getAEL().add(respObj);
		
			log.info("sending data to websocket : " + respObj);
			WSServiceInbound.broadcast(respObj.toJSONString());
			
			
		} catch (ParseException e) {
			
			log.info("Unable to parse received message");
			e.printStackTrace();
		
		}
		
	}
	
	/**
	 * To parse json based audit events and extract infomation needed for GUI
	 * @param revObj
	 * @return response for GUI
	 */
	@SuppressWarnings("unchecked")
	private JSONObject parseAuditEvent( JSONObject revObj ){
		

		JSONObject respObj = new JSONObject();
		
		respObj.put( "id", revObj.get("id") );
		respObj.put( "eventType", revObj.get("eventType") );
		respObj.put( "date", revObj.get("date") );
		respObj.put( "remoteAddr", revObj.get("remoteAddr") + ":" + revObj.get("remotePort") );
		respObj.put( "serverAddr", revObj.get("serverAddr") + ":" + revObj.get("serverPort") );
		respObj.put( "resource", revObj.get("reqURI") );
		respObj.put( "ruleID", revObj.get("ruleID") );
		respObj.put( "severity", revObj.get("severity") );
		respObj.put( "message", revObj.get("message") );
		
		return respObj;
		
	}
	
}
