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
package net.swas.explorer.oh.handler;

import java.io.File;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.swas.explorer.ecf.Entity;
import net.swas.explorer.oh.handler.RelationalModel;
import net.swas.explorer.oh.handler.URIModel;
import net.swas.explorer.oh.lo.OntologyLoader;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

/**
 * This class is responsible for management (add, update, remove, get, getAll) of ontology layer. 
 *
 */
public abstract class OntologyHandler {

	protected OntologyLoader ontLoader;
	protected String NS; // owl name space
	protected OntClass ontClass; // ontology class
	protected Map<String, RelationalModel> relations; // a map containing meta
														// data of an ontology
														// class
	private URIModel URI;

	public final static String NS_POSTFIX = "#"; // name space postfix constant
	private final static Logger log = LoggerFactory
			.getLogger(OntologyHandler.class); // a logger for ontology handler
	

	/**
	 * To add an individual to a specified ontology class
	 * 
	 * @param entity an object of a Subclass of Entity class
	 * @return Individual added to the Knowledge base
	 */
	public abstract Individual add(Entity entity);
	/**
	 * To remove an individual from a specified ontology class
	 * 
	 * @param ID String value containing id of an individual to be removed.
	 */
	public abstract void remove(String ID);

	/**
	 * To retrieve an individual against the specified ID
	 * 
	 * @param ID String value containing id of an individual to be retrieved.
	 * @return Entity An Object of a class whose super class is Entity.
	 */
	public abstract Entity get(String ID);

	/**
	 * To update an individual to a specified ontology class
	 * 
	 * @param entity an object of a Subclass of Entity class
	 * @return Individual updated to the Knowledge base
	 */
	public abstract Individual update(Entity entity);

	/**
	 * to get all the individual of an ontology class
	 * 
	 * @return List of Entity Objects
	 */
	public abstract List<Entity> getAll();

	/**
	 * To get map containing all the metadata of an ontology class.
	 * 
	 * @return Map contain Relational Model as value and property name as key for retrieving its value.
	 */
	public Map<String, RelationalModel> getRelationalModels() {

		this.relations = new HashMap<String, RelationalModel>();
		ExtendedIterator<OntProperty> properties = this.ontClass
				.listDeclaredProperties();
		while (properties.hasNext()) {

			OntProperty property = properties.next();
			RelationalModel rm = new RelationalModel();

			log.info(this.ontClass.getLocalName() + " class Property : "+ property.getLocalName());
			
			rm.setDomain(property.getDomain().getLocalName());
			rm.setProperty(property.getLocalName());
			rm.setPropertURI(property.getURI());

			if (property.isDatatypeProperty() == false) {

				rm.setRange(property.getRange().getLocalName());
				rm.setRangeURI(property.getRange().getURI());

			}
			this.relations.put(property.getLocalName(), rm);
		}
		return this.relations;

	}
	
	/**
	 * To get map containing all the metadata of an ontology class 
	 * sent by user in an argument
	 *
	 * @return Map contain Relational Model as value and property name as key for retrieving its value.
	 */
	public static Map<String, RelationalModel> getRelationalModels(OntologyLoader loader, String concept) {

		log.info("getRelationalModels for : " + concept);
		String NS = loader.getConfiguration().getRuleEngineNameSpace()
				+ NS_POSTFIX;
		

		OntClass ontClass = loader.getModel().createClass(
				NS + concept);
		
		Map<String, RelationalModel> relations = new HashMap<String, RelationalModel>();
		ExtendedIterator<OntProperty> properties = ontClass
				.listDeclaredProperties();
		while (properties.hasNext()) {

			OntProperty property = properties.next();
			RelationalModel rm = new RelationalModel();

			//log.info(ontClass.getLocalName() + " class Property : " + property.getLocalName());
			
			rm.setDomain(property.getDomain().getLocalName());
			rm.setProperty(property.getLocalName());
			rm.setPropertURI(property.getURI());

			if (property.isDatatypeProperty() == false) {

				rm.setRange(property.getRange().getLocalName());
				rm.setRangeURI(property.getRange().getURI());

			}
			relations.put(property.getLocalName(), rm);
		}
		return relations;

	}

	/**
	 * To write changes being made to knowledge base to file
	 * 
	 * @throws IOException
	 */

	public static synchronized void write(OntologyLoader loader)
			throws IOException {

		FileOutputStream out = null;
		try {

			out = new FileOutputStream(new File(loader.getConfiguration()
					.getOntologyPath()));
			loader.getModel().write(out);

		} finally {
			out.close();
		}
	}

}
