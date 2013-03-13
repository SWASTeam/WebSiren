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

import net.swas.explorer.ec.SpecialCollection;
import net.swas.explorer.ec.UserDefinedVariable;
import net.swas.explorer.ecf.Entity;
import net.swas.explorer.ecf.EntityFactory;
import net.swas.explorer.oh.lo.OntologyLoader;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

/**
 * This class is responsible for the management of User Defined Variable in ontology.
 * It uses {@link OntologyLoader} to load configurations of knowledge base.
 * 
 */
public class UserDefinedVariableHandler extends OntologyHandler {

	
	private final static Logger log = LoggerFactory.getLogger(UserDefinedVariableHandler.class);
	private final String udvClassName = "UserDefined";
	private SpecialColHandler spColHandler = null;
	
	/**
	 * Constructor	
	 * @param loader
	 */
	public UserDefinedVariableHandler( OntologyLoader loader ) {
		
		this.ontLoader = loader;
		this.NS = this.ontLoader.getConfiguration().getRuleEngineNameSpace()
				+ NS_POSTFIX;
		
		//log.info("UserDefined Name Space : " + this.NS + this.udvClassName);
		
		this.ontClass = this.ontLoader.getModel().createClass(this.NS + this.udvClassName);
		this.spColHandler = new SpecialColHandler(ontLoader);
		
	}
	
	/**
	 * @see net.swas.explorer.oh.handler.OntologyHandler#add(net.swas.explorer.ecf.Entity)
	 */
	@Override
	public Individual add(Entity entity) {
		
		log.info("UserDefinedVariable's addIndividual called ..  ");
		Individual ind = null;
		if (entity instanceof UserDefinedVariable) {
			
			UserDefinedVariable udv = (UserDefinedVariable) entity;
			if (this.ontLoader.getModel().getIndividual(this.NS + udv.getName()) == null) {

				ind = this.ontClass.createIndividual(this.NS + udv.getName());
				
				Property hasValue = this.ontLoader.getModel().getProperty(this.NS + "hasValue");
				Property isVariableOf = this.ontLoader.getModel().getProperty(this.NS + "isVariableOf");
				
				ind.addLiteral(hasValue, udv.getValue());
				if(udv.getVariableOf() != null){
					Individual specialCol = this.ontLoader.getModel().getIndividual( this.NS + udv.getVariableOf().getClassName() + "." + udv.getVariableOf().getID());
					ind.addProperty(isVariableOf, specialCol);
				}
			}
			
		}

		return ind;
		
	}

	/**
	 * @see net.swas.explorer.oh.handler.OntologyHandler#remove(java.lang.String)
	 */
	@Override
	public void remove(String ID) {
		
		log.info("UserDefinedVariable's removeIndividual called ..  ");
		if (this.ontLoader.getModel().getIndividual(this.NS + ID) != null) {

			Individual ind = this.ontLoader.getModel().getIndividual(this.NS + ID);
			this.ontClass.dropIndividual(ind);
			ind.remove();
			
		} else {

			log.info("UserDefinedVariable individual does not exist");

		}

	}

	/**
	 * @see net.swas.explorer.oh.handler.OntologyHandler#get(java.lang.String)
	 */
	@Override
	public Entity get(String ID) {
		
		//log.info("UserDefinedVariable's getIndividual called ..  ");
		UserDefinedVariable udv = null;
		Individual ind = this.ontLoader.getModel().getIndividual(this.NS + ID);
		if (ind != null) {

			try {

				Property hasValue = this.ontLoader.getModel().getProperty(this.NS + "hasValue");
				Property isVariableOf = this.ontLoader.getModel().getProperty(this.NS + "isVariableOf");
				
				OntResource varResource =(OntResource) ind.getPropertyValue(isVariableOf);
				
				udv = (UserDefinedVariable) EntityFactory.USER_DEFINED_VAR.getObject();
				udv.setName(ID);
				udv.setValue(ind.getProperty(hasValue).getString());
				
				if( varResource != null){
					udv.setVariableOf((SpecialCollection) spColHandler.get(varResource.getLocalName()));
				}

			} catch (InstantiationException | IllegalAccessException e) {

				log.info("Could not initiate UserDefinedVariable object ");
				e.printStackTrace();

			}
		}
		return udv;
	}

	/**
	 * @see net.swas.explorer.oh.handler.OntologyHandler#update(net.swas.explorer.ecf.Entity)
	 */
	@Override
	public Individual update(Entity entity) {

		log.info("UserDefinedVariable's updateIndividual called ..  ");
		Individual ind = null;
		if (entity instanceof UserDefinedVariable) {

			UserDefinedVariable udv = (UserDefinedVariable) entity;
			ind = this.ontLoader.getModel().getIndividual(this.NS + udv.getName());
			if (ind != null) {

				Property hasValue = this.ontLoader.getModel().getProperty(this.NS + "hasValue");
				Property isVariableOf = this.ontLoader.getModel().getProperty(this.NS + "isVariableOf");

				ind.setPropertyValue(hasValue, this.ontLoader.getModel().createTypedLiteral(udv.getValue()));
				
				if(udv.getVariableOf() != null){
					Individual specialCol = this.ontLoader.getModel().getIndividual( this.NS + udv.getVariableOf().getClassName() + "." + udv.getVariableOf().getID());
					ind.setPropertyValue(isVariableOf, specialCol);
				}
				
			} else {

				log.info("UserDefinedVariable Individual does not exist");
			}
		}

		return ind;
		
	}

	/**
	 * @see net.swas.explorer.oh.handler.OntologyHandler#getAll()
	 */
	@Override
	public List<Entity> getAll() {
		
		//log.info("UserDefinedVariable's getAllIndividual called ..  ");
		List<Entity> udvList = new ArrayList<Entity>();

		@SuppressWarnings("unchecked")
		ExtendedIterator<Individual> indList = (ExtendedIterator<Individual>) this.ontClass.listInstances();

		while (indList.hasNext()) {

			try {

				Property hasValue = this.ontLoader.getModel().getProperty(this.NS + "hasValue");
				Property isVariableOf = this.ontLoader.getModel().getProperty(this.NS + "isVariableOf");

				Individual ind = indList.next();

				UserDefinedVariable udv = (UserDefinedVariable) EntityFactory.USER_DEFINED_VAR.getObject();
				udv.setName(ind.getLocalName());
				udv.setValue(ind.getProperty(hasValue).getString());
				
				OntResource varResource =(OntResource) ind.getPropertyValue(isVariableOf);
				
				if( varResource != null){
					udv.setVariableOf((SpecialCollection) spColHandler.get(varResource.getLocalName()));
				}
				
				udvList.add(udv);

			} catch (InstantiationException | IllegalAccessException e) {
				log.info("Could not initiate UserDefinedVariable object ");
				e.printStackTrace();
			}
		}

		return udvList;
		
	}
	
	/**
	 * This function checks if user defined variable individual already exists in ontology.
	 * @param ID
	 * @return UDV individual
	 */
	public Individual isExist( String ID ){
		
		Individual ind = this.ontLoader.getModel().getIndividual(this.NS + ID);
		return ind;
		
	}

}
