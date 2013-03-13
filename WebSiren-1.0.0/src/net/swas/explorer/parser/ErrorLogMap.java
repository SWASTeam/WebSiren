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
package net.swas.explorer.parser;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The ErrorLogMap class is used for storing error logs with a unique ID generated at the time
 * of parsing modsecurity rules. It also get removed its been viewed by the user.
 */
public class ErrorLogMap {
	
	
	private final static Logger log = LoggerFactory.getLogger(ErrorLogMap.class);
	private static ConcurrentMap<String, String> logMap = new ConcurrentHashMap<String, String>();
	
	/**
	 * Adds the error logs to the map.
	 * @param parsingError string of errors.
	 * @return unique id.
	 */
	public static String addLog(String parsingError){
		
		UUID uuid = UUID.randomUUID();
        String randomUUIDString = uuid.toString();
        
		logMap.put(randomUUIDString, parsingError);
		
		return randomUUIDString;
		
	}
	
	/**
	 * To get logs from map
	 * @param ID unique id set at the time of adding logs.
	 * @return error logs - If exists return string otherwise null.
	 */
	public static String getLog(String ID){
		
		return logMap.get(ID);
		
	}
	
	/**
	 * Removes the logs from map.
	 * @param ID unique id set at the time of adding logs.
	 */
	public static void remove(String ID){
		
		logMap.remove(ID);
		
	}

}
