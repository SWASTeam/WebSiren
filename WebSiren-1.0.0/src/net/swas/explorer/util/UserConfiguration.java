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

import java.util.Map;

import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The class UserConfiguration loads the user configurations into
 * the object's properties. 
 * <pre>
 * 		UserConfiguration config = UserConfigurations.getInstance(context);
 * </pre>
 */
public class UserConfiguration {
	
	private Logger log = LoggerFactory.getLogger(UserConfiguration.class);
	private static UserConfiguration instance = null;
	private String filePath = null;

	/**
	 * Loads the user configurations from the SWAS configuration map.
	 * 
	 * @param context  used to read configurations from
	 *  configuration file if not loaded into map before.
	 */
	private UserConfiguration(ServletContext context) {

		SWASConfig config = SWASConfig.getInstance(context);
		Map<String, String> configMap = config.getConfigMap();
		
		log.info("User File Path: "+ configMap.get("User.filePath"));
		this.filePath = configMap.get("User.filePath");
		log.info("User Configurations Loaded");

	}

	/**
	 * To get instance of User configurations. It will initializes object if it does not exists already.
	 * 
	 * @param context  used to read configurations from
	 *  configuration file if not loaded into map before.
	 */
	public static synchronized UserConfiguration getInstance(
			ServletContext context) {

		if (instance == null) {
			instance = new UserConfiguration(context);
		}
		return instance;
	}


	/**
	 * Gets the file path of user information
	 * @return filePath
	 */
	public String getFilePath() {
		return filePath;
	}

}
