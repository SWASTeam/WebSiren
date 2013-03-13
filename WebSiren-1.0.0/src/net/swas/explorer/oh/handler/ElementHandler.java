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

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.swas.explorer.ec.Element;
import net.swas.explorer.ecf.Entity;
import net.swas.explorer.ecf.EntityFactory;
import net.swas.explorer.oh.lo.OntologyLoader;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

/**
 * This class is responsible for the management of elements of collection expressions. It uses {@link OntologyLoader} to load 
 * configurations of knowledge base.
 * 
 */
public class ElementHandler extends OntologyHandler {

	private final static Logger log = LoggerFactory
			.getLogger(ElementHandler.class);
	private final String elmntClassName = "Element";
	
	public ElementHandler( OntologyLoader loader ){
		
		this.ontLoader = loader;
		this.NS = this.ontLoader.getConfiguration().getRuleEngineNameSpace()
				+ NS_POSTFIX;
		
		//log.info("Element Name Space : " + this.NS + this.elmntClassName);

		this.ontClass = this.ontLoader.getModel().createClass(
				this.NS + this.elmntClassName);
		
	}
	
	/**
	 * @see
	 * net.swas.explorer.oh.handler.OntologyHandler#add(net.swas.explorer.ecf.Entity)
	 */
	@Override
	public Individual add(Entity entity) {

		//log.info("Element's addIndividual called ..  ");
		Individual ind = null;
		if (entity instanceof Element) {
			
			Element elmnt = (Element) entity;
			if (!this.isExists(elmnt.getName())) {
				
				Individual col = this.ontLoader.getModel().getIndividual(
						this.NS + elmnt.getCollection());
				
				ind = this.ontClass.createIndividual(this.NS
						+ this.elmntClassName + "." + elmnt.getName());
				
				Property isElementOf = this.ontLoader.getModel().getProperty(
						this.NS + "isElementOf");
				ind.addProperty(isElementOf, col);
				
			} else{
				
				ind = this.getIndividual(elmnt.getName());
			}
			
		}

		return ind;
	}

	/**
	 * @see net.swas.explorer.oh.handler.OntologyHandler#remove(java.lang.String)
	 */
	@Override
	public void remove(String ID) {

		//log.info("Element's removeIndividual called ..  ");
		if (isExists(ID)) {

			Individual ind = getIndividual(ID);
			this.ontClass.dropIndividual(ind);

		} else {

			log.info("Element individual does not exist");

		}

	}

	/**
	 * @see net.swas.explorer.oh.handler.OntologyHandler#get(java.lang.String)
	 */
	@Override
	public Entity get(String ID) {

		log.info("Element's getIndividual called ..  ");
		Element elmnt = null;
		Individual ind = null;
		if (isExists(ID)) {
			log.info("Element Exist.. ");
			try {
				
				Property isElementOf = this.ontLoader.getModel().getProperty(
						this.NS + "isElementOf");
				
				
				ind = this.ontLoader.getModel().getIndividual( this.NS + elmntClassName + "." + ID);
				
				OntResource collection = (OntResource) ind
						.getPropertyValue(isElementOf);
				
				elmnt = (Element) EntityFactory.ELEMENT.getObject();
				elmnt.setName(ind.getLocalName().split("\\.")[1]);
				elmnt.setCollection(collection.getLocalName());
				
			} catch (InstantiationException | IllegalAccessException e) {

				log.info("Could not initiate Element object ");
				e.printStackTrace();

			}
		}
		log.info("Element Name:" + elmnt.getName());
		log.info("Element Collection:" + elmnt.getCollection());
		return elmnt;
	}

	/**
	 * @see net.swas.explorer.oh.handler.OntologyHandler#update(net.swas.explorer.ecf.Entity)
	 */
	@Override
	public Individual update(Entity entity) {

		log.info("Element's updateIndividual called ..  ");
		return null;
	}

	/**
	 * @see net.swas.explorer.oh.handler.OntologyHandler#getAll()
	 */
	@Override
	public List<Entity> getAll() {

		//log.info("Element's getAllIndividual called ..  ");
		List<Entity> elmntList = new ArrayList<Entity>();

		@SuppressWarnings("unchecked")
		ExtendedIterator<Individual> indList = (ExtendedIterator<Individual>) this.ontClass
				.listInstances();

		while (indList.hasNext()) {

			try {
				Individual ind = indList.next();
				
				Property isElementOf = this.ontLoader.getModel().getProperty(
						this.NS + "isElementOf");
				
				OntResource collection = (OntResource) ind
						.getPropertyValue(isElementOf);
				
				Element elmnt = (Element) EntityFactory.ELEMENT.getObject();
				elmnt.setName(ind.getLocalName().split("\\.")[1]);
				elmnt.setCollection(collection.getLocalName());

			} catch (InstantiationException | IllegalAccessException e) {
				log.info("Could not initiate Element object ");
				e.printStackTrace();
			}
		}

		return elmntList;

	}
	
	/**
	 * This function is for getting list of elements against the collection they are part of.
	 * @param collection
	 * @return list of Entity
	 */
	public List<Entity> getElementByCollection( String collection ) {

		log.info("Element's getElementByCollection called ..  ");
		List<Entity> elementList = new ArrayList<Entity>();
		Element elmnt = null;
		
		String query = "";
		query += "PREFIX rdf: <"
				+ this.ontLoader.getConfiguration().getRdfNameSpace()
				+ NS_POSTFIX + ">";
		query += "PREFIX rdfs: <"
				+ this.ontLoader.getConfiguration().getRdfsNameSpace()
				+ NS_POSTFIX + ">";
		query += "PREFIX owl: <"
				+ this.ontLoader.getConfiguration().getOwlNameSpace()
				+ NS_POSTFIX + ">";
		query += "PREFIX ruleEngine: <"
				+ this.ontLoader.getConfiguration().getRuleEngineNameSpace()
				+ NS_POSTFIX + ">";
		
		query += "SELECT ?element " + "WHERE{ ";
		query += "?element ruleEngine:isElementOf ruleEngine:" + collection + ". }";
		
		log.info("query : " + query);
		QueryExecution qexec = QueryExecutionFactory.create(query,
				Syntax.syntaxSPARQL_11, this.ontLoader.getModel());
		try {
			
			ResultSet rs = qexec.execSelect();
			while (rs.hasNext()) {
				
				QuerySolution qs = rs.next();
				log.info("Individuals : " + qs.getResource("element").getLocalName());
			
				elmnt = (Element) this.get(qs.getResource("element").getLocalName()
						.split("\\.")[1]);
				elementList.add(elmnt);
				
			}
		
		} finally {
			qexec.close();
		}
		
		return elementList;
	
	}
	
	/**
	 * checks if element exist in ontology
	 * @param ID
	 * @return boolean
	 */
	public boolean isExists(String ID) {
		
		boolean check = false;
		if (this.ontLoader.getModel().getIndividual(this.NS + this.elmntClassName + "." + ID) != null)
			check = true;
		return check;
	}
	
	public String getClassName(){
		return elmntClassName;
	}
	
	/**
	 * Retrieves Individual of element from ontology 
	 * @param name
	 * @return element individual
	 */
	private Individual getIndividual(String ID) {
		
		return this.ontLoader.getModel().getIndividual(this.NS + this.elmntClassName + "."+ ID);
	
	}
	
}
