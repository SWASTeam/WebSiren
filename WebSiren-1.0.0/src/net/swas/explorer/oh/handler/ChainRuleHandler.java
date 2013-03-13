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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.swas.explorer.ec.ChainRule;
import net.swas.explorer.ec.Condition;
import net.swas.explorer.ec.MetaData;
import net.swas.explorer.ec.SpecialCollection;
import net.swas.explorer.ec.User;
import net.swas.explorer.ec.RuleGroup;
import net.swas.explorer.ec.Sequence;
import net.swas.explorer.ecf.Entity;
import net.swas.explorer.ecf.EntityFactory;
import net.swas.explorer.oh.lo.OntologyLoader;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;


/**
 * This class is responsible for the management of chain rule. It uses {@link OntologyLoader} to load 
 * configurations of knowledge base.
 * 
 */
public class ChainRuleHandler extends OntologyHandler {

	private final static Logger log = LoggerFactory
			.getLogger(ChainRuleHandler.class);
	private final String chainRuleClassName = "ChainRule";
	private final String parentClassName = "Rule";
	private ConditionHandler conditionHndlr = null; // condition handler
	private MetaDataHandler mdHndlr = null; // Meta data handler
	private SequenceHandler seqHndlr = null; // Sequence handler
	private RuleGroupHandler groupHndlr = null; // policy handler
	private UsersHandler usrHndlr = null;
	private SpecialColHandler spColHndlr = null; //special collection handler
	Calendar currentDate = Calendar.getInstance();
	SimpleDateFormat formatter =  new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

	/**
	 * @param loader 
	 */
	public ChainRuleHandler(OntologyLoader loader) {

		this.ontLoader = loader;
		this.NS = this.ontLoader.getConfiguration().getRuleEngineNameSpace()
				+ NS_POSTFIX;

		this.ontClass = this.ontLoader.getModel().createClass(this.NS + this.chainRuleClassName);
		this.conditionHndlr = new ConditionHandler(this.ontLoader);
		this.mdHndlr = new MetaDataHandler(this.ontLoader);
		this.seqHndlr = new SequenceHandler(this.ontLoader);
		this.groupHndlr = new RuleGroupHandler(this.ontLoader);
		this.usrHndlr = new UsersHandler(this.ontLoader);
		this.spColHndlr = new SpecialColHandler(this.ontLoader);
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.swas.explorer.oh.handler.OntologyHandler#add(net.swas.explorer.ecf.Entity)
	 */
	@Override
	public Individual add(Entity entity) {

		//log.info("ChainRule's addIndividual called ..  ");
		Individual chainRuleInd = null;
		if (entity instanceof ChainRule) {

			ChainRule chainRule = (ChainRule) entity;
			int ID = Sequence.getID(seqHndlr, this.parentClassName);

			if (!isExists(""+ID)) {

				chainRuleInd = this.ontClass.createIndividual(this.NS + this.parentClassName + "." + ID);
				
				log.info("Disruptive Action :" + chainRule.getDisruptiveAction());
				Individual disruptiveAction = this.ontLoader.getModel().getIndividual(this.NS + chainRule.getDisruptiveAction());
				Individual phase = null;
				if (chainRule.getPhase() != 0)
				{
					phase = this.ontLoader.getModel().getIndividual(this.NS + "Phase." + chainRule.getPhase());
				}
				
				Property hasID = this.ontLoader.getModel().getProperty(this.NS + "hasSemRuleID");
				Property hasCondition = this.ontLoader.getModel().getProperty(this.NS + "hasCondition");
				Property hasDisruptiveAction = this.ontLoader.getModel().getProperty(this.NS + "hasDisruptiveAction");
				Property hasMetaData = this.ontLoader.getModel().getProperty(this.NS + "hasMetaData");
				Property hasPhase= this.ontLoader.getModel().getProperty( this.NS + "executedInPhase");
				Property initializeCollection = this.ontLoader.getModel().getProperty(this.NS + "initializeCollection"); //Property For Special Collections
				Property hasGroup = this.ontLoader.getModel().getProperty(this.NS + "hasGroup");
				Property createdBy = this.ontLoader.getModel().getProperty(this.NS + "createdBy");
				Property createdAt = this.ontLoader.getModel().getProperty(this.NS + "createdAt");
				Property editedAt = this.ontLoader.getModel().getProperty(this.NS + "editedAt");
				Property editedBy = this.ontLoader.getModel().getProperty(this.NS + "editedBy");

				chainRuleInd.addLabel(chainRule.getRuleTitle(), "EN");
				chainRuleInd.addComment(chainRule.getComment(), "EN");
				chainRuleInd.addLiteral(hasID, new String("" + ID));

				chainRuleInd.addLiteral(createdAt, new String(formatter.format(currentDate.getTime())));
				chainRuleInd.addProperty(hasMetaData, mdHndlr.add(chainRule.getMetaData()));
				chainRuleInd.addProperty(hasDisruptiveAction, disruptiveAction);
				
				if (phase != null)
				{
					chainRuleInd.addProperty( hasPhase, phase);
					
				}
				

				chainRuleInd.addProperty(createdBy, this.ontLoader.getModel().getIndividual(this.NS + chainRule.getUserCreatedBy().getUserName()));

				//adding group
				boolean check = groupExists(chainRule.getRuleGroup().getName());
				
				if (check){
					
					chainRuleInd.addProperty(hasGroup, groupHndlr.add(chainRule.getRuleGroup()));
				
				} else {
					
					Individual policyInd = null;
					policyInd = this.ontLoader.getModel().getIndividual(this.NS + chainRule.getRuleGroup().getName());
					chainRuleInd.addProperty(hasGroup, policyInd);
				
				}
				
				for (Condition condition : chainRule.getCondition()) {
					
					chainRuleInd.addProperty(hasCondition,conditionHndlr.add(condition));
				
				}
				
				//adding special collections
				if(chainRule.getSpecialCollection() != null){
					for( SpecialCollection spCol: chainRule.getSpecialCollection().values() ){
						chainRuleInd.addProperty( initializeCollection, this.spColHndlr.add( spCol ));
					}
				}

				log.info("writing into file ");

				try {

					OntologyHandler.write(ontLoader);

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			else {
				log.info("ChainRule's Individual already Exist ..  ");
			}
		}

		return chainRuleInd;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.swas.explorer.oh.handler.OntologyHandler#remove(java.lang.String)
	 */
	@Override
	public void remove(String ID) {
		
		//log.info("ChainRule's removeIndividual called ..  ");
		if (isExists(ID)) {
			
			Individual chainRuleInd = this.getIndividual(ID);

			Property hasCondition = this.ontLoader.getModel().getProperty(this.NS + "hasCondition");
			Property hasMetaData = this.ontLoader.getModel().getProperty(this.NS + "hasMetaData");
			Property initializeCollection = this.ontLoader.getModel().getProperty(this.NS + "initializeCollection"); //Property For Special Collections
			
			OntResource metaData = (OntResource) chainRuleInd.getPropertyValue(hasMetaData);

			NodeIterator conditionList = chainRuleInd.listPropertyValues(hasCondition);
			while (conditionList.hasNext()) {

				Resource condition = (Resource) conditionList.removeNext().asResource();
				this.conditionHndlr.remove(condition.getLocalName().split("\\.")[1]);
			}
			
			NodeIterator spColList = chainRuleInd.listPropertyValues(initializeCollection);
			while (spColList.hasNext()) {

				Resource spCol = (Resource) spColList.removeNext().asResource();
				this.spColHndlr.remove(spCol.getLocalName());
				
			}
			
			this.mdHndlr.remove(metaData.getLocalName().split("\\.")[1]);
			this.ontClass.dropIndividual(chainRuleInd);
			chainRuleInd.remove();

		} else {

			log.info("chain rule  individual does not exist");

		}

		try {

			OntologyHandler.write(ontLoader);

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.swas.explorer.oh.handler.OntologyHandler#get(java.lang.String)
	 */
	@SuppressWarnings("unused")
	@Override
	public Entity get(String ID) {

		//log.info("ChainRule's getIndividual called ..  ");
		ChainRule chainRule = null;
		User user = null;
		Individual chainRuleInd = null;
		if (isExists(ID)) {
			
			chainRuleInd = this.getIndividual(ID);
			try {
				
				Property hasID = this.ontLoader.getModel().getProperty(this.NS + "hasSemRuleID");
				Property hasCondition = this.ontLoader.getModel().getProperty(this.NS + "hasCondition");
				Property hasDisruptiveAction = this.ontLoader.getModel().getProperty(this.NS + "hasDisruptiveAction");
				Property hasPhase= this.ontLoader.getModel().getProperty( this.NS + "executedInPhase");
				Property hasMetaData = this.ontLoader.getModel().getProperty(this.NS + "hasMetaData");
				Property hasGroup = this.ontLoader.getModel().getProperty(this.NS + "hasGroup");
				Property initializeCollection = this.ontLoader.getModel().getProperty(this.NS + "initializeCollection"); //Property For Special Collections
				Property createdBy = this.ontLoader.getModel().getProperty(this.NS + "createdBy");
				Property createdAt = this.ontLoader.getModel().getProperty(this.NS + "createdAt");
				Property editedAt = this.ontLoader.getModel().getProperty(this.NS + "editedAt");
				Property editedBy = this.ontLoader.getModel().getProperty(this.NS + "editedBy");

				
				OntResource condition = (OntResource) chainRuleInd.getPropertyValue(hasCondition);
				OntResource metaData = (OntResource) chainRuleInd.getPropertyValue(hasMetaData);
				OntResource disruptiveAction = (OntResource) chainRuleInd.getPropertyValue(hasDisruptiveAction);
				OntResource phase = (OntResource) chainRuleInd.getPropertyValue(hasPhase);
				OntResource group = (OntResource) chainRuleInd.getPropertyValue(hasGroup);
				OntResource user1 = (OntResource) chainRuleInd.getPropertyValue(createdBy);
				OntResource editUser = (OntResource) chainRuleInd.getPropertyValue(editedBy);
				
				chainRule = (ChainRule) EntityFactory.CHAIN_RULE.getObject();
				user = (User) EntityFactory.USER.getObject();
				
				chainRule.setID(chainRuleInd.getProperty(hasID).getString());
				chainRule.setCreationDate(chainRuleInd.getProperty(createdAt).getString());
				chainRule.setUserCreatedBy((User)this.usrHndlr.get(user1.getLocalName()));
				if(chainRuleInd.getProperty(editedAt) != null){
					chainRule.setEditingDate(chainRuleInd.getProperty(editedAt).getString());
				}
				chainRule.setRuleTitle(chainRuleInd.getLabel("EN"));
				chainRule.setComment(chainRuleInd.getComment("EN"));
				chainRule.setMetaData((MetaData) this.mdHndlr.get(metaData.getLocalName().split("\\.")[1]));
				chainRule.setDisruptiveAction(disruptiveAction.getLocalName());
				
				if (phase != null)
				{
					chainRule.setPhase( Integer.parseInt(phase.getLocalName().split("\\.")[1]) );
				}
				
				chainRule.setRuleGroup((RuleGroup) this.groupHndlr.get(group.getLocalName()));	
				
				//----rule editor---------
				if(editUser != null){
					chainRule.setUserEditedBy((User)this.usrHndlr.get(editUser.getLocalName()));
				}

				NodeIterator conditionList = chainRuleInd.listPropertyValues(hasCondition);
				List<Condition> childRules = new ArrayList<Condition>();

				while (conditionList.hasNext()) {
					 
					condition = (OntResource) conditionList.next();
					childRules.add((Condition) this.conditionHndlr.get(condition.getLocalName().split("\\.")[1]));
				}
				
				chainRule.setCondition(childRules);
				
				//getting special collection individuals
				NodeIterator spColList = chainRuleInd.listPropertyValues(initializeCollection);
				
				Map<String, SpecialCollection> spCols = new HashMap<String, SpecialCollection>();
				while (spColList.hasNext()) {
					
					OntResource spColInd = (OntResource) spColList.next();
					spCols.put(spColInd.asIndividual().getOntClass().getLocalName(),
								(SpecialCollection)spColHndlr.get(spColInd.getLocalName()));

				}
				chainRule.setSpecialCollection(spCols);

			} catch (InstantiationException | IllegalAccessException e) {

				log.info("Could not initiate ChainRule object ");
				e.printStackTrace();

			}
		}
		return chainRule;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.swas.explorer.oh.handler.OntologyHandler#update(net.swas.explorer.ecf.Entity)
	 */
	@SuppressWarnings("unused")
	@Override
	public Individual update(Entity entity) {

		//log.info("ChainRule's updateIndividual called ..  ");
		Individual chainRuleInd = null;
		Individual chainRuleEditedInd = null;
		if (entity instanceof ChainRule) {
			
			User user = null;
			ChainRule chainRule = (ChainRule) entity;
			if (isExists(chainRule.getID())) {
				try {
					chainRuleInd = this.ontLoader.getModel().getIndividual(this.NS + this.parentClassName + "." + chainRule.getID());
					user = (User) EntityFactory.USER.getObject();
					Property createdBy = this.ontLoader.getModel().getProperty(this.NS + "createdBy");
					Property createdAt = this.ontLoader.getModel().getProperty(this.NS + "createdAt");
					Property extension = this.ontLoader.getModel().getProperty(this.NS + "extensionOf");

					OntResource cUser = (OntResource) chainRuleInd.getPropertyValue(createdBy);

					chainRule.setUserCreatedBy((User)this.usrHndlr.get(cUser.getLocalName()));
					chainRule.setEditingDate(new String(formatter.format(currentDate.getTime())));
					chainRule.setCreationDate(chainRuleInd.getProperty(createdAt).getString());


					int ID = Sequence.getID(seqHndlr, this.parentClassName);

					if (!isExists(""+ID)) {

						chainRuleEditedInd = this.ontClass.createIndividual(this.NS + this.parentClassName + "." + ID);

						log.info("Disruptive Action :" + chainRule.getDisruptiveAction());
						Individual disruptiveAction = this.ontLoader.getModel().getIndividual(this.NS + chainRule.getDisruptiveAction());
						Individual phase = null;
						if (chainRule.getPhase() != 0)
						{
							phase = this.ontLoader.getModel().getIndividual(this.NS + "Phase." + chainRule.getPhase());
						}

						Property hasID = this.ontLoader.getModel().getProperty(this.NS + "hasSemRuleID");
						Property hasCondition = this.ontLoader.getModel().getProperty(this.NS + "hasCondition");
						Property hasDisruptiveAction = this.ontLoader.getModel().getProperty(this.NS + "hasDisruptiveAction");
						Property hasMetaData = this.ontLoader.getModel().getProperty(this.NS + "hasMetaData");
						Property hasPhase= this.ontLoader.getModel().getProperty( this.NS + "executedInPhase");
						Property initializeCollection = this.ontLoader.getModel().getProperty(this.NS + "initializeCollection"); //Property For Special Collections
						Property hasGroup = this.ontLoader.getModel().getProperty(this.NS + "hasGroup");

						chainRuleEditedInd.addLabel(chainRule.getRuleTitle(), "EN");
						chainRuleEditedInd.addComment(chainRule.getComment(), "EN");
						chainRuleEditedInd.addLiteral(hasID, new String("" + ID));

						if (chainRule.getCreationDate() != "")
						{
							chainRuleEditedInd.addLiteral(createdAt, chainRule.getCreationDate());
						}
						else
						{
							chainRuleEditedInd.addLiteral(createdAt, new String(formatter.format(currentDate.getTime())));
						}

						if (chainRule.getEditingDate() != "")
						{
							Property editedAt = this.ontLoader.getModel().getProperty(this.NS + "editedAt");
							chainRuleEditedInd.addProperty(editedAt, chainRule.getEditingDate());
						}
						chainRuleEditedInd.addProperty(hasMetaData, mdHndlr.add(chainRule.getMetaData()));
						chainRuleEditedInd.addProperty(hasDisruptiveAction, disruptiveAction);
						if (phase != null)
						{
							chainRuleEditedInd.addProperty( hasPhase, phase);

						}

		
						chainRuleEditedInd.addProperty(createdBy, this.ontLoader.getModel().getIndividual(this.NS + chainRule.getUserCreatedBy().getUserName()));
						if (chainRule.getUserEditedBy() != null)
						{
							Property editedBy = this.ontLoader.getModel().getProperty(this.NS + "editedBy");
							if (this.ontLoader.getModel().getIndividual(this.NS + chainRule.getUserEditedBy().getUserName()) != null)
							{
								chainRuleEditedInd.addProperty(editedBy, this.ontLoader.getModel().getIndividual(this.NS +chainRule.getUserEditedBy().getUserName()));
							}
							else
								log.info("user does not exist..");
						}
						
						//adding special collections
						if(chainRule.getSpecialCollection() != null){
							for( SpecialCollection spCol: chainRule.getSpecialCollection().values() ){
								chainRuleEditedInd.addProperty( initializeCollection, this.spColHndlr.add( spCol ));
							}
						}

						boolean check = groupExists(chainRule.getRuleGroup().getName());
						if (check)
						{
							chainRuleEditedInd.addProperty(hasGroup, groupHndlr.add(chainRule.getRuleGroup()));
						}
						else
						{
							Individual groupInd = null;
							groupInd = this.ontLoader.getModel().getIndividual(this.NS + chainRule.getRuleGroup().getName());
							chainRuleEditedInd.addProperty(hasGroup, groupInd);
						}

						for (Condition condition : chainRule.getCondition()) 
						{
							chainRuleEditedInd.addProperty(hasCondition,conditionHndlr.add(condition));
						}
						
						chainRuleEditedInd.addProperty(extension, chainRuleInd);
						log.info("writing into file ");

						try {

							OntologyHandler.write(ontLoader);

						} catch (IOException e) {
							e.printStackTrace();
						}
					} //---- end if new individual of chain rule

					else {
						log.info("ChainRule's Individual already Exist ..  ");
					}


				} 
				catch (InstantiationException | IllegalAccessException e) {
					e.printStackTrace();
				}
			} // ---- end if is chain rule individual exists.
			else {

				log.info("ChainRule Individual does not exist");
			}
		}
		return chainRuleInd;
	}

	/** (non-Javadoc)
	 * @see net.swas.explorer.oh.handler.OntologyHandler#getAll()
	 */
	@Override
	public List<Entity> getAll() {

		//log.info("ChainRule's getAllIndividual called ..  ");

		List<Entity> chainRuleList = new ArrayList<Entity>();
		ChainRule chainRule = null;
		User user = null;
		User creating_user =null;
		User editing_user = null;

		@SuppressWarnings("unchecked")
		ExtendedIterator<Individual> chainRuleIndList = (ExtendedIterator<Individual>) this.ontClass.listInstances();

		while (chainRuleIndList.hasNext()) {

			try {

				Individual chainRuleInd = chainRuleIndList.next();
				Property hasID = this.ontLoader.getModel().getProperty(	this.NS + "hasSemRuleID");
				Property hasCondition = this.ontLoader.getModel().getProperty(this.NS + "hasCondition");
				Property hasDisruptiveAction = this.ontLoader.getModel().getProperty(this.NS + "hasDisruptiveAction");
				Property hasPhase= this.ontLoader.getModel().getProperty( this.NS + "executedInPhase");
				Property hasMetaData = this.ontLoader.getModel().getProperty(this.NS + "hasMetaData");
				Property hasGroup = this.ontLoader.getModel().getProperty(this.NS + "hasGroup");
				Property initializeCollection = this.ontLoader.getModel().getProperty(this.NS + "initializeCollection"); //Property For Special Collections
				Property createdBy = this.ontLoader.getModel().getProperty(this.NS + "createdBy");
				Property createdAt = this.ontLoader.getModel().getProperty(this.NS + "createdAt");
				Property editedBy = this.ontLoader.getModel().getProperty(this.NS + "editedBy");
				Property editedAt = this.ontLoader.getModel().getProperty(this.NS + "editedAt");

				OntResource condition = (OntResource) chainRuleInd.getPropertyValue(hasCondition);
				OntResource metaData = (OntResource) chainRuleInd.getPropertyValue(hasMetaData);
				OntResource disruptiveAction = (OntResource) chainRuleInd.getPropertyValue(hasDisruptiveAction);
				OntResource phase = (OntResource) chainRuleInd.getPropertyValue(hasPhase);
				OntResource policy = (OntResource) chainRuleInd.getPropertyValue(hasGroup);
				OntResource user1 = (OntResource) chainRuleInd.getPropertyValue(createdBy);
				OntResource editUser = (OntResource) chainRuleInd.getPropertyValue(editedBy);
				
				chainRule = (ChainRule) EntityFactory.CHAIN_RULE.getObject();
				user = (User) EntityFactory.USER.getObject();
				creating_user = (User) EntityFactory.USER.getObject();
				editing_user = (User) EntityFactory.USER.getObject();
				
				chainRule.setID(chainRuleInd.getProperty(hasID).getString());
				chainRule.setCreationDate(chainRuleInd.getProperty(createdAt).getString());
				chainRule.setUserCreatedBy((User)this.usrHndlr.get(user1.getLocalName()));
				if(chainRuleInd.getProperty(editedAt) != null){
					chainRule.setEditingDate(chainRuleInd.getProperty(editedAt).getString());
				}
				chainRule.setRuleTitle(chainRuleInd.getLabel("EN"));
				chainRule.setComment(chainRuleInd.getComment("EN"));
				
				chainRule.setDisruptiveAction(disruptiveAction.getLocalName());
				
				if (phase != null)
				{
					chainRule.setPhase( Integer.parseInt(phase.getLocalName().split("\\.")[1]) );
				}
				
				chainRule.setMetaData((MetaData) this.mdHndlr.get(metaData.getLocalName().split("\\.")[1]));
				chainRule.setRuleGroup((RuleGroup) this.groupHndlr.get(policy.getLocalName()));
				user.setDisplayName(creating_user.getDisplayName());
				
				//----rule editor---------
				if(editUser != null){
					chainRule.setUserEditedBy((User)this.usrHndlr.get(editUser.getLocalName()));
				}
				
				NodeIterator conditionList = chainRuleInd.listPropertyValues(hasCondition);
				List<Condition> crCondition = new ArrayList<Condition>();

				while (conditionList.hasNext()) {
					 
					condition = (OntResource) conditionList.next();
					crCondition.add((Condition) this.conditionHndlr.get(condition.getLocalName().split("\\.")[1]));
					log.info("condition local name : " + condition.getLocalName());
				}
				chainRule.setCondition(crCondition);
				
				//getting special collection individuals
				NodeIterator spColList = chainRuleInd.listPropertyValues(initializeCollection);
				
				Map<String, SpecialCollection> spCols = new HashMap<String, SpecialCollection>();
				while (spColList.hasNext()) {
					
					OntResource spColInd = (OntResource) spColList.next();
					spCols.put(spColInd.asIndividual().getOntClass().getLocalName(),
								(SpecialCollection)spColHndlr.get(spColInd.getLocalName()));

				}
				chainRule.setSpecialCollection(spCols);
				
				chainRuleList.add(chainRule);

			} catch (InstantiationException | IllegalAccessException e) {
				log.info("Could not initiate ChainRule object ");
				e.printStackTrace();
			}
		}

		return chainRuleList;
	}
	
	/**
	 * Checks if group already exists in ontology
	 * @param name
	 * @return boolean
	 */
	private boolean groupExists(String name) {
		Individual policyInd = null;
		boolean check = false;
		policyInd = this.ontLoader.getModel().getIndividual(this.NS + name);
		if (policyInd == null)
			check = true;
		else
			check = false;
		return check;
	}
	
	/**
	 * Checks if chain rule individual already exists in ontology
	 * @param ID
	 * @return boolean
	 */
	public boolean isExists(String ID) {
		
		boolean check = false;
		if (this.ontLoader.getModel().getIndividual(this.NS + this.parentClassName + "." + ID) != null)
			check = true;
		return check;
	
	}
	
	/**
	 * Retrieves chain rule individual from ontology
	 * @param name
	 * @return Individual of chain rule
	 */
	private Individual getIndividual(String ID) {
		
		return this.ontLoader.getModel().getIndividual(this.NS + this.parentClassName + "." + ID);
	
	}
	
	/**
	 * Retrieve class name
	 * @return className
	 */
	public String getClassName(){
		return this.chainRuleClassName;
	}
	

}
