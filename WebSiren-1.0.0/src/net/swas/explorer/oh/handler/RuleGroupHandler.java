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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

import net.swas.explorer.ec.RuleGroup;
import net.swas.explorer.ec.User;
import net.swas.explorer.ecf.Entity;
import net.swas.explorer.ecf.EntityFactory;
import net.swas.explorer.oh.lo.OntologyLoader;

/**
 * This class is responsible for the management of Rule Group. It uses {@link OntologyLoader} to load 
 * configurations of knowledge base.
 * 
 */
public class RuleGroupHandler extends OntologyHandler {

	
	private final static Logger log = LoggerFactory.getLogger(RuleGroupHandler.class);
	private final String varClassName = "RuleGroup";
	private UsersHandler usrHndlr = null;
	private Calendar currentDate = Calendar.getInstance();
	private SimpleDateFormat formatter =  new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

	/**
	 * Constructor
	 * @param loader
	 */
	public RuleGroupHandler(OntologyLoader loader) {

		this.ontLoader = loader;
		this.NS = this.ontLoader.getConfiguration().getRuleEngineNameSpace()
				+ NS_POSTFIX;

		//log.info("Group Name Space : " + this.NS + this.varClassName);

		this.ontClass = this.ontLoader.getModel().createClass(
				this.NS + this.varClassName);
		this.usrHndlr = new UsersHandler(this.ontLoader);

	}

	/**
	 * @see net.swas.explorer.oh.handler.OntologyHandler#add(net.swas.explorer.ecf.Entity)
	 */
	@Override
	public Individual add(Entity entity) {
		
		//log.info("Rule Group addIndividual called ..  ");
		Individual groupInd = null;
		if (entity instanceof RuleGroup) {
			RuleGroup group = (RuleGroup) entity;
			
			boolean check = this.isExists(group.getName());
			
			if (check) {
				groupInd = this.ontClass.createIndividual(this.NS + group.getName());
				
				Property hasGroupName = this.ontLoader.getModel().getProperty(this.NS + "hasGroupName");
				Property hasGroupSpecification = this.ontLoader.getModel().getProperty(this.NS + "hasGroupSpecification");
				Property createdBy = this.ontLoader.getModel().getProperty(this.NS + "createdBy");
				Property createdAt = this.ontLoader.getModel().getProperty(this.NS + "createdAt");
				
				groupInd.addLiteral(hasGroupName, group.getName());
				groupInd.addLiteral(hasGroupSpecification, group.getDescription());
				groupInd.addLiteral(createdAt, new String(formatter.format(currentDate.getTime())));
				groupInd.addProperty(createdBy, this.ontLoader.getModel().getIndividual(this.NS + group.getUserCreatedBy().getUserName()));
				
			} else {
				log.info("Group Already Exists ... ");
				groupInd = this.ontLoader.getModel().getIndividual(this.NS + group.getName());
			}
			try {
				
				log.info("writing into file ");
				OntologyHandler.write(ontLoader);

			} catch (IOException e) {
				e.printStackTrace();
			}
	
		}

		return groupInd;
	}

	
	/**
	 * @see net.swas.explorer.oh.handler.OntologyHandler#remove(java.lang.String)
	 */
	@Override
	public void remove(String ID) {
		
		//log.info("Group Remove Individual Called...");
		Individual groupInd = this.ontLoader.getModel().getIndividual(
				this.NS + ID);
		if (groupInd != null) {

			this.ontClass.dropIndividual(groupInd);
			groupInd.remove();
			
 			try {
				
				log.info("writing into file ");
				OntologyHandler.write(ontLoader);

			} catch (IOException e) {
				e.printStackTrace();
			}

		} else {

			log.info("Group individual does not exist");

		}

	}

	/**
	 * @see net.swas.explorer.oh.handler.OntologyHandler#get(java.lang.String)
	 */
	@Override
	public Entity get(String ID) {
		
		//log.info("Rule Group get Individual Called.." + ID + ".");
		RuleGroup ruleGroup = null;
		User user = null;
		Individual groupInd = this.ontLoader.getModel().getIndividual(
				this.NS  + ID);

		if (groupInd != null) {
			
			Property hasGroupName = this.ontLoader.getModel().getProperty(this.NS + "hasGroupName");
			Property hasGroupSpecification = this.ontLoader.getModel().getProperty(this.NS + "hasGroupSpecification");
			Property createdBy = this.ontLoader.getModel().getProperty(this.NS + "createdBy");
			Property createdAt = this.ontLoader.getModel().getProperty(this.NS + "createdAt");
			Property editedAt = this.ontLoader.getModel().getProperty(this.NS + "editedAt");
			Property editedBy = this.ontLoader.getModel().getProperty(this.NS + "editedBy");
			
			OntResource user1 = (OntResource) groupInd.getPropertyValue(createdBy);
			OntResource editUser = (OntResource) groupInd.getPropertyValue(editedBy);

			try {
				
				ruleGroup = (RuleGroup) EntityFactory.GROUP.getObject();
				user = (User) EntityFactory.USER.getObject();
				
				ruleGroup.setName(groupInd.getProperty(hasGroupName).getString());
				ruleGroup.setCreationDate(groupInd.getProperty(createdAt).getString());
				//ruleGroup.setUserCreatedBy((User)this.usrHndlr.get(user1.getLocalName()));
				ruleGroup.setDescription(groupInd.getProperty(hasGroupSpecification).getString());
				if(groupInd.getProperty(editedAt) != null){
					ruleGroup.setEditingDate(groupInd.getProperty(editedAt).getString());
					
				}
				//----rule editor---------
				if(editUser != null){
					ruleGroup.setUserEditedBy((User)this.usrHndlr.get(editUser.getLocalName()));
				}
				

			} catch (InstantiationException | IllegalAccessException e) {
				log.info("Could not initiate Variable object ");
				e.printStackTrace();
			}
			

		}
		return ruleGroup;
	}

	
	/**
	 * @see net.swas.explorer.oh.handler.OntologyHandler#update(net.swas.explorer.ecf.Entity)
	 */
	@Override
	public Individual update(Entity entity) {
		
		RuleGroup group;
		Individual groupInd= null;
		//log.info("Group update Individual Called...");
		
		if (entity instanceof RuleGroup) {
			
			group = (RuleGroup) entity;
			 groupInd = this.ontLoader.getModel().getIndividual(this.NS + group.getPreviousGroup());
			 log.info("group: "+ group.getPreviousGroup());
			 
			Property createdBy = this.ontLoader.getModel().getProperty(this.NS + "createdBy");
			Property createdAt = this.ontLoader.getModel().getProperty(this.NS + "createdAt");
			OntResource cUser = (OntResource) groupInd.getPropertyValue(createdBy);
			
			group.setUserCreatedBy((User)this.usrHndlr.get(cUser.getLocalName()));
			group.setCreationDate(groupInd.getProperty(createdAt).getString());
			if (group.getName().matches(group.getPreviousGroup()))
			{
				log.info("only Description changed...");
				
				
				Property hasGroupName = this.ontLoader.getModel().getProperty(this.NS + "hasGroupName");
				Property hasGroupSpecification = this.ontLoader.getModel().getProperty(this.NS + "hasGroupSpecification");
				Property editedBy = this.ontLoader.getModel().getProperty(this.NS + "editedBy");
				Property editedAt = this.ontLoader.getModel().getProperty(this.NS + "editedAt");
				
				
				
				Individual user = this.ontLoader.getModel().getIndividual(this.NS + group.getUserEditedBy().getUserName());
				if (groupInd != null) {
					
					groupInd.setLabel(group.getName(),"EN");
					groupInd.addLiteral(editedAt, new String(formatter.format(currentDate.getTime())));
					groupInd.setPropertyValue(hasGroupName, this.ontLoader.getModel().createTypedLiteral(group.getName()));
					groupInd.setPropertyValue(hasGroupSpecification, this.ontLoader.getModel().createTypedLiteral(group.getDescription()));
					groupInd.setPropertyValue(editedBy, user);
					groupInd.setPropertyValue(createdBy, this.ontLoader.getModel().getIndividual(this.NS + group.getUserCreatedBy().getUserName()));
					groupInd.addLiteral(createdAt, group.getCreationDate());
					
				} else {

					log.info("Group Individual does not exist");
				}
			}
			else
			{
				log.info("Group name changed...");
				remove(group.getPreviousGroup());
				groupInd = add(group);
				
			}
			
		}
		return groupInd;
	
	}

	/**
	 * @see net.swas.explorer.oh.handler.OntologyHandler#getAll()
	 */
	@Override
	public List<Entity> getAll() {
		
		//log.info("Rule Group getAllIndividual called ..  ");
		List<Entity> ruleGroupList = new ArrayList<Entity>();
		RuleGroup ruleGroup = null;
		User user = null;
		User creating_user =null;
		User editing_user = null;

		@SuppressWarnings("unchecked")
		ExtendedIterator<Individual> indList = (ExtendedIterator<Individual>) this.ontClass
				.listInstances();

		while (indList.hasNext()) {
			try {
				
				Property hasGroupName = this.ontLoader.getModel().getProperty(this.NS + "hasGroupName");
				Property hasGroupSpecification = this.ontLoader.getModel().getProperty(this.NS + "hasGroupSpecification");
				Property createdBy = this.ontLoader.getModel().getProperty(this.NS + "createdBy");
				Property createdAt = this.ontLoader.getModel().getProperty(this.NS + "createdAt");
				Property editedBy = this.ontLoader.getModel().getProperty(this.NS + "editedBy");
				Property editedAt = this.ontLoader.getModel().getProperty(this.NS + "editedAt");
				
				
				Individual groupInd = indList.next();
				OntResource user1 = (OntResource) groupInd.getPropertyValue(createdBy);
				OntResource editUser = (OntResource) groupInd.getPropertyValue(editedBy);

				ruleGroup = (RuleGroup) EntityFactory.GROUP.getObject();
				user = (User) EntityFactory.USER.getObject();
				
				ruleGroup.setName(groupInd.getProperty(hasGroupName).getString());
				ruleGroup.setCreationDate(groupInd.getProperty(createdAt).getString());
				
				//need to be fixde TODO
				if(user1 != null){
					ruleGroup.setUserCreatedBy((User)this.usrHndlr.get(user1.getLocalName()));
				}
				
				ruleGroup.setDescription(groupInd.getProperty(hasGroupSpecification).getString());
				
				if(groupInd.getProperty(editedAt) != null){
					ruleGroup.setEditingDate(groupInd.getProperty(editedAt).getString());
					
				}
				//----rule editor---------
				if(editUser != null){
					ruleGroup.setUserEditedBy((User)this.usrHndlr.get(editUser.getLocalName()));
				}
				ruleGroupList.add(ruleGroup);

			} catch (InstantiationException | IllegalAccessException e) {
				log.info("Could not initiate variable object ");
				e.printStackTrace();
			}

		}

		return ruleGroupList;
	}

	/**
	 * Checks if group already exists in ontology 
	 * @param name
	 * @return boolean
	 */
	private boolean isExists(String name) {
		
		Individual groupInd = null;
		boolean check = false;
		groupInd =this.ontLoader.getModel().getIndividual(this.NS + name);
		if (groupInd == null)
			check = true;
		else
			check = false;
		return check;
	}
	

	/**
	 * Retrieves group individual from ontology
	 * @param Name
	 * @return group individual
	 */
	public Individual getIndividual(String Name) {
	
		//log.info("In Group get individual...");
		Individual GroupInd = null;
		GroupInd = this.ontLoader.getModel().getIndividual(this.NS + Name);
		return GroupInd;
	}
}
