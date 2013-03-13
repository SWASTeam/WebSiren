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
package net.swas.explorer.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The class SWASConfig loads the sysetm configurations into
 * a map. 
 * <pre>
 * 		SWASConfig config = SWASConfig.getInstance(context);
 * </pre>
 */
public class SWASConfig {
	
	
	private final static Logger log = LoggerFactory.getLogger(SWASConfig.class);
	private static SWASConfig instance= null;
	private Properties prop =  new Properties();
	private Map<String, String> propMap = null;
	
	/**
	 * Loads the configurations from the configuration file.
	 * 
	 * @param context  used to read configurations from
	 *  configuration file if not loaded into map before.
	 */
	private SWASConfig(ServletContext context){
		
		log.info("loading MSService configuration :");
		propMap = new HashMap<String, String>();
		String configPath = "\\WEB-INF\\config.properties";
		InputStream is;
		try {
			
			is = context.getResourceAsStream(configPath);
			prop.load(is);
			for( Entry<Object, Object> entry:prop.entrySet()){
				propMap.put((String) entry.getKey(), (String) entry.getValue());
			}
			
			log.info("SWAS configurations Loaded ... ");
			
		} catch (FileNotFoundException e1) {
			
			e1.printStackTrace();
			
		} catch(IOException | NullPointerException e){
			
			e.printStackTrace();
			
		}
		
	}
	
	/**
	 * To get instance of SWAS configurations. It will initializes object if it does not exists already.
	 * 
	 * @param context  used to read configurations from
	 *  configuration file if not loaded into map before.
	 */
	public static SWASConfig getInstance(ServletContext context){
		
		if(instance == null){
			instance = new SWASConfig(context);
		}
		return instance;
		
	}
	
	/**
	 * Gets the loaded configuration map.
	 * @return configuration map.
	 */
	public Map<String, String> getConfigMap(){
		
		return propMap;
		
	}
	

}
