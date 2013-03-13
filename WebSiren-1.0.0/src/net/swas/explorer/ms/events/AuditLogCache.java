package net.swas.explorer.ms.events;

import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.json.simple.JSONObject;

/**
 * The AuditLogCache class is used to maintain the audit event logs cache. It maintains them in a map.
 */
public class AuditLogCache {
	
	
	private final static String auditLogName = "AuditEvents";
	private static ConcurrentMap<String, Queue<JSONObject>> logMap = new ConcurrentHashMap<String, Queue<JSONObject>>();
	
	/**
	 * Adds the list of audit events.
	 * AEL for Audit Event List
	 * @param jsonList
	 */
	public static void addAEL(Queue<JSONObject> jsonList){
        
		logMap.put(auditLogName, jsonList);
		
	}
	
	/**
	 * Gets the list of audit events.
	 * AEL for Audit Event List
	 * @return Queue of json based audit event.
	 */
	public static Queue<JSONObject> getAEL(){
		
		return logMap.get(auditLogName);
		
	}
	
	
	/**
	 * Checks whether audit evens exist or not.
	 * @return true if exists otherwise false.
	 */
	public static boolean isAELInitialized(){
		
		if(getAEL() == null)
			return false;
		else
			return true;
		
	}
	
	/**
	 * Removes all the audit events from the map.
	 * AEL for Audit Event List
	 */
	public static void flushAEL(){
		
		if(isAELInitialized()){
			getAEL().clear();
		}
		
	}
	

}
