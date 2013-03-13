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
package net.swas.explorer.oh.lo;

import java.util.Map;

import javax.servlet.ServletContext;

import net.swas.explorer.util.SWASConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The class KBConfiguration loads the knowledge base or ontology 
 * configuration. The singleton design pattern is applied to load
 * configurations only one time. It loads the path, owl namespace,
 * rdf namespace and rdfs namespace from the project configurations'
 * map.
 * 
 * <pre>
 * 		KBConfiguration configuration = KBConfiguration.getInstance(context);
 * </pre>
 */

public class KBConfiguration {
	
	
	private Logger log = LoggerFactory.getLogger(KBConfiguration.class);
	private static KBConfiguration instance = null;
	private String ontologyPath;
	private String owlNameSpace;
	private String rdfNameSpace;
	private String rdfsNameSpace;
	private String ruleEngineNameSpace;
	private String uploadFolderPath;

	/**
	 * Loads the configurations from the configuration map.
	 * 
	 * @param context  used to read configurations from
	 *  configuration file if not loaded into map before.
	 */
	private KBConfiguration(ServletContext context) {

		SWASConfig config = SWASConfig.getInstance(context);
		Map<String, String> configMap = config.getConfigMap();
		
		log.info("Loading Knowledge Base Configurations");
		log.info("Ontology Path: "
				+ configMap.get("KB.ontolgyPath")
				+ ", OWL Name Space: "
				+ configMap.get("KB.owlNameSpace")
				+ ", RDF Name Space: "
				+ configMap.get("KB.rdfNameSpace")
				+ ",\n RDFS Name Space: "
				+ configMap.get("KB.rdfsNameSpace")
				+ ", Rule Engine Name Space: "
				+ configMap.get("KB.ruleEngineNameSpace")
				+ ", Http Ontology Name Space: "
				+ configMap.get("KB.httpOntNameSpace")
				+ ", Event Engine Name Space: "
				+ configMap.get("KB.eventEngineNameSpace"));

		this.ontologyPath = configMap.get("KB.ontolgyPath");
		this.owlNameSpace = configMap.get("KB.owlNameSpace");
		this.rdfNameSpace = configMap.get("KB.rdfNameSpace");
		this.rdfsNameSpace = configMap.get("KB.rdfsNameSpace");
		this.ruleEngineNameSpace = configMap.get("KB.ruleEngineNameSpace");
		this.uploadFolderPath = configMap.get("FileUpload.folderPath");
		log.info("Knowledge Base Configurations Loaded");
		
	}

	/**
	 * To get instance of KB configurations. It will initializes object if it does not exists already.
	 * 
	 * @param context  used to read configurations from
	 *  configuration file if not loaded into map before.
	 */
	public static synchronized KBConfiguration getInstance(ServletContext context) {

		if (instance == null) {
			instance = new KBConfiguration(context);
		}
		return instance;
	}
	
	/**
	 * To get Ontology file path.
	 */
	public String getOntologyPath() {
		return ontologyPath;
	}

	/**
	 * To get OWL namespace.
	 * 
	 * @return if exists returns string otherwise null.
	 */
	public String getOwlNameSpace() {
		return owlNameSpace;
	}

	/**
	 * To get RDF namespace 
	 * 
	 * @return if exists returns string otherwise null.
	 */
	public String getRdfNameSpace() {
		return rdfNameSpace;
	}

	/**
	 * To get RDFS namespace 
	 * 
	 * @return if exists returns string otherwise null.
	 */
	public String getRdfsNameSpace() {
		return rdfsNameSpace;
	}

	/**
	 * To get Rule Engine Ontology namespace 
	 * 
	 * @return if exists returns string otherwise null.
	 */
	public String getRuleEngineNameSpace() {
		return ruleEngineNameSpace;
	}

	/**
	 * To get modsecurity rule file folder path for upload 
	 * 
	 * @return if exists returns string otherwise null.
	 */
	public String getUploadFolderPath() {
		return uploadFolderPath;
	}
	
	

}
