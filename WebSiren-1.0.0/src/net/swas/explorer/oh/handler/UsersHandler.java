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

import net.swas.explorer.ec.User;
import net.swas.explorer.ecf.Entity;
import net.swas.explorer.ecf.EntityFactory;
import net.swas.explorer.oh.lo.OntologyLoader;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

/**
 * This class is responsible for the management of users in ontology.
 * It uses {@link OntologyLoader} to load configurations of knowledge base.
 * 
 */
/**
 * @author Sidra
 *
 */
public class UsersHandler extends OntologyHandler {
	
	
	private final static Logger log = LoggerFactory
			.getLogger(SequenceHandler.class);
	private final String userClassName = "User";
	
	
	/**
	 * Constructor
	 * @param loader
	 */
	public UsersHandler(OntologyLoader loader) {
		
		this.ontLoader = loader;
		this.NS = this.ontLoader.getConfiguration().getRuleEngineNameSpace()+ NS_POSTFIX;
		
		//log.info("User Name Space : " + this.NS + this.userClassName);
		
		this.ontClass = this.ontLoader.getModel().createClass(this.NS + this.userClassName);
	}
	
	/**
	 * @see net.swas.explorer.oh.handler.OntologyHandler#add(net.swas.explorer.ecf.Entity)
	 */
	@Override
	public Individual add(Entity entity) {
		
		//log.info("Sequence's addIndividual called ..  ");
		Individual userInd = null;
		if (entity instanceof User) {
			User user =(User) entity;
			
			boolean check = UserExists( user.getUserName());
			
			if (check)
			{
				userInd = this.ontClass.createIndividual(this.NS + user.getUserName());
				Property hasDisplayName = this.ontLoader.getModel().getProperty(this.NS + "hasDisplayName");
				
				userInd.addLiteral(hasDisplayName, user.getDisplayName());
			}
			else {
				log.info("User already exits..");
				userInd = this.ontLoader.getModel().getIndividual(this.NS + user.getUserName());
			}
			
			try {
				
				log.info("writing into file ");
				OntologyHandler.write(ontLoader);

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return userInd;
		
	}

	/**
	 * @see net.swas.explorer.oh.handler.OntologyHandler#remove(java.lang.String)
	 */
	@Override
	public void remove(String ID) {
		
		//log.info("User Remove Individual Called...");
		Individual userInd = this.ontLoader.getModel().getIndividual(this.NS + ID);
		if (userInd != null) {

			this.ontClass.dropIndividual(userInd);
			userInd.remove();
			
 			try {
				
				log.info("writing into file ");
				OntologyHandler.write(ontLoader);

			} catch (IOException e) {
				e.printStackTrace();
			}

		} else {
			log.info("User individual does not exist");
		}
	}
	
	/**
	 * This function retrieves the individual of user from ontology
	 * @param Name
	 * @return user individual
	 */
	public Individual getIndividual(String Name) {
		
		//log.info("In User get individual...");
		Individual userInd = null;
		userInd = this.ontLoader.getModel().getIndividual(this.NS + Name);
		return userInd;
	}

	/**
	 * @see net.swas.explorer.oh.handler.OntologyHandler#get(java.lang.String)
	 */
	@Override
	public Entity get(String ID) {
		
		//log.info("User get Individual Called.." + ID + ".");
		User user = null;
		Individual userInd = this.ontLoader.getModel().getIndividual(this.NS  + ID);

		if (userInd != null) {	
			Property hasDisplayName = this.ontLoader.getModel().getProperty(this.NS + "hasDisplayName");
			try {
				user = (User) EntityFactory.USER.getObject();
				user.setDisplayName(userInd.getProperty(hasDisplayName).getString());
				user.setUserName(ID);

			} catch (InstantiationException | IllegalAccessException e) {
				log.info("Could not initiate Variable object ");
				e.printStackTrace();
			}
		}
		return user;
	}

	/**
	 * @see net.swas.explorer.oh.handler.OntologyHandler#update(net.swas.explorer.ecf.Entity)
	 */
	@Override
	public Individual update(Entity entity) {
		
		//log.info("Group update Individual Called...");
		Individual userInd = null;
		if (entity instanceof User) {
			User user = (User) entity;
			
			if(user.getUserName().matches(user.getPreviousName()))
			{
				log.info("only Display name changed...");
				userInd = this.ontLoader.getModel().getIndividual(this.NS + user.getUserName());
				if (userInd != null) {

					Property hasDisplayName = this.ontLoader.getModel().getProperty(this.NS + "hasDisplayName");

					userInd.setLabel(user.getUserName(),"EN");
					userInd.setPropertyValue(hasDisplayName, this.ontLoader.getModel().createTypedLiteral(user.getDisplayName()));
					
				} else {

					log.info("User individual does not exist");
				}
			}
			else
			{
				log.info("Group name changed...");
				remove(user.getPreviousName());
				userInd = add(user);	
			}
			
		}
		return userInd;
	
	}

	/**
	 * @see net.swas.explorer.oh.handler.OntologyHandler#getAll()
	 */
	@Override
	public List<Entity> getAll() {
		
		//log.info("User getAllIndividual called ..  ");
		List<Entity> userList = new ArrayList<Entity>();
		User user = null;

		@SuppressWarnings("unchecked")
		ExtendedIterator<Individual> indList = (ExtendedIterator<Individual>) this.ontClass
				.listInstances();

		while (indList.hasNext()) {
			try {
				
				Property hasDisplayName = this.ontLoader.getModel().getProperty(this.NS + "hasDisplayName");

				Individual userInd = indList.next();

				user = (User) EntityFactory.USER.getObject();
				user.setDisplayName(userInd.getProperty(hasDisplayName).getString());
				user.setUserName(userInd.getLocalName().toString());
				
				userList.add(user);

			} catch (InstantiationException | IllegalAccessException e) {
				log.info("Could not initiate variable object ");
				e.printStackTrace();
			}

		}

		return userList;
	}
	
	
	/**
	 * This function checks if user exists in ontology
	 * @param name
	 * @return boolean
	 */
	private boolean UserExists(String name) {
		
		Individual userInd = null;
		boolean check = false;
		userInd =this.ontLoader.getModel().getIndividual(this.NS + name);
		if (userInd == null)
			check = true;
		else
			check = false;
		return check;
		
	}

}
