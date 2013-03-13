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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.swas.explorer.ec.Resource;
import net.swas.explorer.ecf.Entity;
import net.swas.explorer.ecf.EntityFactory;
import net.swas.explorer.oh.lo.OntologyLoader;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

/**
 * This class is responsible for the management of resource of application profile. It uses {@link OntologyLoader} to load 
 * configurations of knowledge base.
 * 
 */
public class ResourceHandler extends OntologyHandler {
	
	private final static Logger log = LoggerFactory.getLogger(ResourceHandler.class);
	private final String resourceClassName = "Resource";
	
	/**
	 * Constructor
	 * @param loader
	 */
	public ResourceHandler(OntologyLoader loader){
		
		this.ontLoader = loader;
		this.NS = this.ontLoader.getConfiguration().getRuleEngineNameSpace()
				+ NS_POSTFIX;
		this.ontClass = this.ontLoader.getModel().createClass(
				this.NS + this.resourceClassName);
	}

	/**
	 * @see net.swas.explorer.oh.handler.OntologyHandler#add(net.swas.explorer.ecf.Entity)
	 */
	@Override
	public Individual add(Entity entity) {

		log.info("Resource addIndividual called ..  ");
		Individual resourceInd = null;
		if (entity instanceof Resource) {
			Resource resource = (Resource) entity;
			
			boolean check = this.isExists(resource.getResource());
			
			if (check) {
				resourceInd = this.ontClass.createIndividual(this.NS + resource.getResource());
				Property hasURL = this.ontLoader.getModel().getProperty(this.NS + "hasUrl");

				resourceInd.addLiteral(hasURL , resource.getResource());
				
			} else {
				log.info("Resource Already Exists ... ");
				resourceInd = this.ontLoader.getModel().getIndividual(this.NS + resource.getResource());
			}
			try {
				
				log.info("writing into file ");
				OntologyHandler.write(ontLoader);

			} catch (IOException e) {
				e.printStackTrace();
			}
	
		}

		return resourceInd;
	}

	/**
	 * Checks if resource exist in ontology
	 * @param name
	 * @return boolean
	 */
	private boolean isExists(String name) {
		Individual resourceInd = null;
		boolean check = false;
		resourceInd =this.ontLoader.getModel().getIndividual(this.NS + name);
		if (resourceInd == null)
			check = true;
		else
			check = false;
		return check;
	}

	/**
	 * @see net.swas.explorer.oh.handler.OntologyHandler#remove(java.lang.String)
	 */
	@Override
	public void remove(String ID) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * @see net.swas.explorer.oh.handler.OntologyHandler#get(java.lang.String)
	 */
	@Override
	public Entity get(String ID) {
		log.info("Resource get Individual Called.." + ID + ".");
			Resource resource = null;
			Individual resourceInd = this.ontLoader.getModel().getIndividual(
						this.NS  + ID);

				if (resourceInd != null) {
					
					Property hasURL = this.ontLoader.getModel().getProperty(this.NS + "hasUrl");
	
					try {
						
						resource = (Resource) EntityFactory.RESOURCE.getObject();
						
						resource.setResource(resourceInd.getLocalName());
						resource.setUrl(resourceInd.getProperty(hasURL).getString());

							
						}
	  
					 catch (InstantiationException | IllegalAccessException e) {
						log.info("Could not initiate Variable object ");
						e.printStackTrace();
					}
				}
					
	
				
				return resource;
	}

	/**
	 * @see net.swas.explorer.oh.handler.OntologyHandler#update(net.swas.explorer.ecf.Entity)
	 */
	@Override
	public Individual update(Entity entity) {
		log.info("resource update Individual Called...");
				Individual resourceInd = null;
				
				return resourceInd;
			
	}

	/**
	 * @see net.swas.explorer.oh.handler.OntologyHandler#getAll()
	 */
	@Override
	public List<Entity> getAll() {
		//log.info("Rule Group getAllIndividual called ..  ");
				List<Entity> resourceList = new ArrayList<Entity>();
				Resource resource = null;

				@SuppressWarnings("unchecked")
				ExtendedIterator<Individual> indList = (ExtendedIterator<Individual>) this.ontClass
						.listInstances();

				while (indList.hasNext()) {
					try {
						

						Property hasURL = this.ontLoader.getModel().getProperty(this.NS + "hasUrl");
						
						Individual resourceInd = indList.next();
						
						resource = (Resource) EntityFactory.RESOURCE.getObject();
						
						resource.setResource(resourceInd.getLocalName());
						resource.setUrl(resourceInd.getProperty(hasURL).getString());
						resourceList.add(resource);

					} catch (InstantiationException | IllegalAccessException e) {
						log.info("Could not initiate variable object ");
						e.printStackTrace();
					}

				}

				return resourceList;
	}

}
