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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

/**
 * The class OntologyLoader loads the Jena's OntModel into memory from
 * Knowledge Base. Model will be lazy loaded into memory.
 * 
 * <pre>
 * 		OntologyLoader loader = OntologyLoader.getOntLoader(servletContext);
 * </pre>
 */

public class OntologyLoader {

	
	private static OntologyLoader ontLoader = null;
	private Logger log = LoggerFactory.getLogger(OntologyLoader.class);
	private KBConfiguration configuration = null;
	private OntModel model = null;

	/**
	 * Loads the Ontology Model from the Knowledge Base.
	 * 
	 * @param context  used to read configurations from
	 *  configuration file
	 */
	private OntologyLoader(ServletContext context) {

		File file = null;
		FileInputStream ins;

		log.info("Loading ontology model");
		this.configuration = KBConfiguration.getInstance(context);

		try {
			file = new File(configuration.getOntologyPath());
			ins = new FileInputStream(file);

			Model baseModel = ModelFactory.createDefaultModel();
			baseModel.read(ins, configuration.getRuleEngineNameSpace());
			this.model = ModelFactory.createOntologyModel(
					OntModelSpec.OWL_DL_MEM, baseModel);
			log.info("Ontology model loaded");

		} catch (FileNotFoundException e) {
			log.info("OWL File not Found");
			e.printStackTrace();
		}

	}

	/**
	 * To get instance of Ontology Loader. It will initialize object if it does not exists already.
	 * 
	 * @param context  used to read configurations from
	 *  configuration file.
	 */
	public synchronized static OntologyLoader getOntLoader(
			ServletContext context) {
		if (ontLoader == null) {
			ontLoader = new OntologyLoader(context);
		}
		return ontLoader;
	}

	/**
	 * To get Knowledge Configurations
	 */
	public KBConfiguration getConfiguration() {
		return configuration;
	}

	/**
	 * To get ontology Model
	 */
	public OntModel getModel() {
		return model;
	}

}
