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
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.swas.explorer.ec.ChainRule;
import net.swas.explorer.ec.Condition;
import net.swas.explorer.ec.MetaData;
import net.swas.explorer.ec.SpecialCollection;
import net.swas.explorer.ec.User;
import net.swas.explorer.ec.Rule;
import net.swas.explorer.ec.RuleGroup;
import net.swas.explorer.ec.Sequence;

import net.swas.explorer.ecf.Entity;
import net.swas.explorer.ecf.EntityFactory;
import net.swas.explorer.oh.lo.OntologyLoader;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

/**
 * This class is responsible for the management of rule. It uses {@link OntologyLoader} to load 
 * configurations of knowledge base.
 * 
 */
public class RuleHandler extends OntologyHandler {

	private final static Logger log = LoggerFactory.getLogger(RuleHandler.class);
	private final String ruleClassName = "SimpleRule";
	private final String parentClassName = "Rule";
	private ConditionHandler conditionHndlr = null; // condition handler
	private MetaDataHandler mdHndlr = null; // Meta data handler
	private SequenceHandler seqHndlr = null; // Sequence handler
	private RuleGroupHandler groupHndlr = null; // policy handler
	private UsersHandler usrHndlr = null;//user defined variable handler
	private SpecialColHandler spColHndlr = null; //special collection handler
	private ResourceHandler resourceHandlr = null;
	Calendar currentDate = Calendar.getInstance();
	SimpleDateFormat formatter =  new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

	/**
	 * Constructor
	 * @param loader
	 */
	public RuleHandler(OntologyLoader loader) {

		this.ontLoader = loader;
		this.NS = this.ontLoader.getConfiguration().getRuleEngineNameSpace()
				+ NS_POSTFIX;
		
		//log.info("Rule Name Space : " + this.NS + this.ruleClassName);

		this.ontClass = this.ontLoader.getModel().createClass(
				this.NS + this.ruleClassName);
		this.conditionHndlr = new ConditionHandler(this.ontLoader);
		this.mdHndlr = new MetaDataHandler(this.ontLoader);
		this.seqHndlr = new SequenceHandler(this.ontLoader);
		this.groupHndlr = new RuleGroupHandler(this.ontLoader);
		this.usrHndlr = new UsersHandler(this.ontLoader);
		this.spColHndlr = new SpecialColHandler(this.ontLoader);
		this.resourceHandlr = new ResourceHandler( this.ontLoader);

	}

	/**
	 * @see net.swas.explorer.oh.handler.OntologyHandler#add(net.swas.explorer.ecf.Entity)
	 */
	@SuppressWarnings("unused")
	@Override
	public Individual add(Entity entity) {
		

		RuleGroup group = null;
		User user = null;

		try {
			group = (RuleGroup) EntityFactory.GROUP.getObject();
			user = (User) EntityFactory.USER.getObject();
		} catch (InstantiationException | IllegalAccessException e1) {

			e1.printStackTrace();
		}
		
		log.info("Rule's addIndividual called ..  ");
		Individual ind = null;
		if (entity instanceof Rule) {

			Rule rule = (Rule) entity;
			int ID = Sequence.getID(seqHndlr, this.parentClassName);
			log.info("Sequence ID for Rule: " + ID + " : Action :" + rule.getDisruptiveAction() );
			if (this.ontLoader.getModel().getIndividual(this.NS + this.parentClassName + "." + ID) == null) {
				
				log.info("in IF statement");
				ind = this.ontClass.createIndividual(this.NS+ this.parentClassName + "." + ID);
				Individual disruptiveAction = this.ontLoader.getModel().getIndividual(this.NS + rule.getDisruptiveAction());
				Individual phase = null;
				if (rule.getPhase() != 0)
				{
					phase = this.ontLoader.getModel().getIndividual(this.NS + "Phase." + rule.getPhase());
				}
				
				Property hasID = this.ontLoader.getModel().getProperty(this.NS + "hasSemRuleID");
				Property hasCondition = this.ontLoader.getModel().getProperty(this.NS + "hasCondition");
				Property hasDisruptiveAction = this.ontLoader.getModel().getProperty(this.NS + "hasDisruptiveAction");
				Property hasMetaData = this.ontLoader.getModel().getProperty(this.NS + "hasMetaData");
				Property hasPhase= this.ontLoader.getModel().getProperty( this.NS + "executedInPhase");
				Property hasGroup = this.ontLoader.getModel().getProperty(this.NS + "hasGroup");
				Property createdBy = this.ontLoader.getModel().getProperty(this.NS + "createdBy");
				Property createdAt = this.ontLoader.getModel().getProperty(this.NS + "createdAt");
				Property initializeCollection = this.ontLoader.getModel().getProperty(this.NS + "initializeCollection"); //Property For Special Collections
				Property belongsTo = this.ontLoader.getModel().getProperty(this.NS + "belongsTo");

				ind.addLabel(rule.getRuleTitle(), "EN");
				ind.addComment(rule.getComment(), "EN");
				ind.addLiteral(hasID, new String("" + ID));

				ind.addLiteral(createdAt, new String(formatter.format(currentDate.getTime())));

				Individual mtInd = mdHndlr.add(rule.getMetaData());
				ind.addProperty(hasMetaData, mtInd);
				ind.addProperty(hasCondition,conditionHndlr.add(rule.getCondition()));
				ind.addProperty(hasDisruptiveAction, disruptiveAction);
				
				//adding resource information for rule
				if (rule.getResource() != null)
				{
					boolean checkResource = resourceExists(rule.getResource().getResource());
					if (!checkResource){
						ind.addProperty(belongsTo, resourceHandlr.add(rule.getResource()));
					} else{
						ind.addProperty(belongsTo, this.ontLoader.getModel().getIndividual(this.NS + rule.getResource().getResource()));
					}
				}

				//adding phase
				if (phase != null)
				{
					ind.addProperty( hasPhase, phase);
				}
				
				//adding special collections
				if(rule.getSpecialCollection() != null){
					for( SpecialCollection spCol: rule.getSpecialCollection().values() ){
						ind.addProperty( initializeCollection, this.spColHndlr.add( spCol ));
					}
				}
				
				//adding creation details
				log.info("UserCreatedBY:" + rule.getUserCreatedBy().getUserName());
				ind.addProperty(createdBy, this.ontLoader.getModel().getIndividual(this.NS + rule.getUserCreatedBy().getUserName()));
				
				//Creation Date of rule
				if (rule.getCreationDate() != ""){
					ind.addLiteral(createdAt, rule.getCreationDate());
				}
				else{
					ind.addLiteral(createdAt, new String(formatter.format(currentDate.getTime())));
				}
				
				//adding group details
				boolean check = groupExists(rule.getRuleGroup().getName());
				if (check){
					ind.addProperty(hasGroup, groupHndlr.add(rule.getRuleGroup()));
				} else{
					ind.addProperty(hasGroup,this.ontLoader.getModel().getIndividual(this.NS + rule.getRuleGroup().getName()));
				}
				
				//writing 
				try {
					
					log.info("writing into file ");
					OntologyHandler.write(ontLoader);

				} catch (IOException e) {
					e.printStackTrace();
				}

			} else {
				log.info("Rule Individual already exist");
			}
		}

		return ind;

	}

	
	/**
	 * @see net.swas.explorer.oh.handler.OntologyHandler#remove(java.lang.String)
	 */
	@Override
	public void remove(String ID) {

		log.info("Rule's removeIndividual called ..  ");
		Individual ind = this.ontLoader.getModel().getIndividual(
				this.NS + this.parentClassName + "." + ID);

		if (ind != null) {

			Property hasCondition = this.ontLoader.getModel().getProperty(this.NS + "hasCondition");
			Property hasMetaData = this.ontLoader.getModel().getProperty(this.NS + "hasMetaData");
			Property initializeCollection = this.ontLoader.getModel().getProperty(this.NS + "initializeCollection"); //Property For Special Collections
			//Property createdBy = this.ontLoader.getModel().getProperty(this.NS + "createdBy");
			
			OntResource condition = (OntResource) ind.getPropertyValue(hasCondition);
			OntResource metaData = (OntResource) ind.getPropertyValue(hasMetaData);
			//OntResource user = (OntResource) ind.getPropertyValue(createdBy);
			
			this.conditionHndlr.remove(condition.getLocalName().split("\\.")[1]);
			this.mdHndlr.remove(metaData.getLocalName().split("\\.")[1]);
			
			NodeIterator spColList = ind.listPropertyValues(initializeCollection);
			while (spColList.hasNext()) {

				Resource spCol = (Resource) spColList.removeNext().asResource();
				this.spColHndlr.remove(spCol.getLocalName());
				
			}
			
			this.ontClass.dropIndividual(ind);

			try {
				log.info("writing into file ");
				OntologyHandler.write(ontLoader);

			} catch (IOException e) {
				e.printStackTrace();
			}

		} else {

			log.info("Condition individual does not exist");

		}
	}

	/**
	 * @see net.swas.explorer.oh.handler.OntologyHandler#get(java.lang.String)
	 */
	@SuppressWarnings("unused")
	@Override
	public Entity get(String ID) {

		//log.info("Rule's getIndividual called ..  ");
		Rule rule = null;
		User user = null;
		Individual ind = this.ontLoader.getModel().getIndividual(
				this.NS + this.parentClassName + "." + ID);
		if (ind != null) {

			try {

				Property hasID = this.ontLoader.getModel().getProperty(this.NS + "hasSemRuleID");
				Property hasCondition = this.ontLoader.getModel().getProperty(this.NS + "hasCondition");
				Property hasDisruptiveAction = this.ontLoader.getModel().getProperty(this.NS + "hasDisruptiveAction");
				Property hasPhase= this.ontLoader.getModel().getProperty( this.NS + "executedInPhase");
				Property hasMetaData = this.ontLoader.getModel().getProperty(this.NS + "hasMetaData");
				Property initializeCollection = this.ontLoader.getModel().getProperty(this.NS + "initializeCollection"); //Property For Special Collections
				Property hasGroup = this.ontLoader.getModel().getProperty(this.NS + "hasGroup");
				Property createdBy = this.ontLoader.getModel().getProperty(this.NS + "createdBy");
				Property createdAt = this.ontLoader.getModel().getProperty(this.NS + "createdAt");
				Property editedAt = this.ontLoader.getModel().getProperty(this.NS + "editedAt");
				Property editedBy = this.ontLoader.getModel().getProperty(this.NS + "editedBy");
				Property belongsTo = this.ontLoader.getModel().getProperty(this.NS + "belongsTo");

				OntResource condition = (OntResource) ind.getPropertyValue(hasCondition);
				OntResource metaData = (OntResource) ind.getPropertyValue(hasMetaData);
				OntResource disruptiveAction = (OntResource) ind.getPropertyValue(hasDisruptiveAction);
				OntResource phase = (OntResource) ind.getPropertyValue(hasPhase);
				OntResource group = (OntResource) ind.getPropertyValue(hasGroup);
				OntResource user1 = (OntResource) ind.getPropertyValue(createdBy);
				OntResource editUser = (OntResource) ind.getPropertyValue(editedBy);
				OntResource resource = (OntResource) ind.getPropertyValue(belongsTo);

				rule = (Rule) EntityFactory.RULE.getObject();
				user = (User) EntityFactory.USER.getObject();

				rule.setID(ind.getProperty(hasID).getString());
				rule.setCreationDate(ind.getProperty(createdAt).getString());
				
				rule.setRuleTitle(ind.getLabel("EN"));
				rule.setComment(ind.getComment("EN"));
				rule.setCondition((Condition) (this.conditionHndlr.get(condition.getLocalName().split("\\.")[1])));
				rule.setDisruptiveAction(disruptiveAction.getLocalName());
				if (phase != null)
				{
					rule.setPhase( Integer.parseInt(phase.getLocalName().split("\\.")[1]) );
				}
				
				rule.setMetaData((MetaData) this.mdHndlr.get(metaData.getLocalName().split("\\.")[1]));
				rule.setRuleGroup((RuleGroup) this.groupHndlr.get(group.getLocalName()));
				rule.setUserCreatedBy((User)this.usrHndlr.get(user1.getLocalName()));
				if (resource != null)
				{
					rule.setResource((net.swas.explorer.ec.Resource) this.resourceHandlr.get(resource.getLocalName()));
				}
				
				if(ind.getProperty(editedAt) != null){
					rule.setEditingDate(ind.getProperty(editedAt).getString());
				}
				
				if(editUser != null){
					rule.setUserEditedBy((User)this.usrHndlr.get(editUser.getLocalName()));
				}
				
				//getting special collection individuals
				NodeIterator spColList = ind.listPropertyValues(initializeCollection);
				
				Map<String, SpecialCollection> spCols = new HashMap<String, SpecialCollection>();
				while (spColList.hasNext()) {
					
					OntResource spColInd = (OntResource) spColList.next();
					spCols.put(spColInd.asIndividual().getOntClass().getLocalName(),
								(SpecialCollection)spColHndlr.get(spColInd.getLocalName()));

				}
				rule.setSpecialCollection(spCols);

			} catch (InstantiationException | IllegalAccessException e) {

				log.info("Could not initiate Rule object ");
				e.printStackTrace();

			}
		}
		return rule;

	}

	
	/**
	 * @see net.swas.explorer.oh.handler.OntologyHandler#update(net.swas.explorer.ecf.Entity)
	 */
	@SuppressWarnings("unused")
	@Override
	public Individual update(Entity entity) {

		//log.info("Rule's updateIndividual called ..  ");
		Individual ind = null;
		Individual ruleEditedInd = null;
		if (entity instanceof Rule) {

			Rule rule = (Rule) entity;
			User user = null;
			ind = this.ontLoader.getModel().getIndividual(this.NS + this.parentClassName + "." + rule.getID());
			if (ind != null) {
				try {
					
					user = (User) EntityFactory.USER.getObject();
					Property createdBy = this.ontLoader.getModel().getProperty(this.NS + "createdBy");
					Property createdAt = this.ontLoader.getModel().getProperty(this.NS + "createdAt");
					Property belongsTo = this.ontLoader.getModel().getProperty(this.NS + "belongsTo");

					OntResource cUser = (OntResource) ind.getPropertyValue(createdBy);
					OntResource resource = (OntResource) ind.getPropertyValue(belongsTo);
					
					rule.setUserCreatedBy((User)this.usrHndlr.get(cUser.getLocalName()));
					rule.setEditingDate(new String(formatter.format(currentDate.getTime())));
					rule.setCreationDate(ind.getProperty(createdAt).getString());
					if(resource != null){
						rule.setResource((net.swas.explorer.ec.Resource) this.resourceHandlr.get(resource.getLocalName()));
					}

					int ID = Sequence.getID(seqHndlr, this.parentClassName);
					log.info("Sequence ID for Rule: " + ID + " : Action :" + rule.getDisruptiveAction() );
					if (this.ontLoader.getModel().getIndividual(this.NS + this.parentClassName + "." + ID) == null) {
						
						log.info("in IF statement");
						ruleEditedInd  = this.ontClass.createIndividual(this.NS+ this.parentClassName + "." + ID);
						Individual disruptiveAction = this.ontLoader.getModel().getIndividual(this.NS + rule.getDisruptiveAction());
						Individual phase = null;
						if (rule.getPhase() != 0)
						{
							phase = this.ontLoader.getModel().getIndividual(this.NS + "Phase." + rule.getPhase());
						}
						
						Property hasID = this.ontLoader.getModel().getProperty(this.NS + "hasSemRuleID");
						Property hasCondition = this.ontLoader.getModel().getProperty(this.NS + "hasCondition");
						Property hasDisruptiveAction = this.ontLoader.getModel().getProperty(this.NS + "hasDisruptiveAction");
						Property hasMetaData = this.ontLoader.getModel().getProperty(this.NS + "hasMetaData");
						Property hasPhase= this.ontLoader.getModel().getProperty( this.NS + "executedInPhase");
						Property hasGroup = this.ontLoader.getModel().getProperty(this.NS + "hasGroup");
						Property extension = this.ontLoader.getModel().getProperty(this.NS + "extensionOf");
						Property initializeCollection = this.ontLoader.getModel().getProperty(this.NS + "initializeCollection"); //Property For Special Collections
						
						
						ruleEditedInd .addLabel(rule.getRuleTitle(), "EN");
						ruleEditedInd .addComment(rule.getComment(), "EN");
						ruleEditedInd .addLiteral(hasID, new String("" + ID));
						
						if (rule.getCreationDate() != "")
						{
							ruleEditedInd .addLiteral(createdAt, rule.getCreationDate());
						}
						else
							ruleEditedInd .addLiteral(createdAt, new String(formatter.format(currentDate.getTime())));

						Individual mtInd = mdHndlr.add(rule.getMetaData());
						ruleEditedInd .addProperty(hasMetaData, mtInd);
						ruleEditedInd .addProperty(hasCondition,conditionHndlr.add(rule.getCondition()));
						ruleEditedInd .addProperty(hasDisruptiveAction, disruptiveAction);
						if (phase != null)
						{
							ruleEditedInd .addProperty( hasPhase, phase);
						}
						
						ruleEditedInd .addProperty(createdBy, this.ontLoader.getModel().getIndividual(this.NS + rule.getUserCreatedBy().getUserName()));
						
						//adding resource information for rule
						if(rule.getResource() != null){
							if (resourceExists(rule.getResource().getResource())){
							
								ruleEditedInd.addProperty(belongsTo, this.ontLoader.getModel().getIndividual(this.NS + rule.getResource().getResource()));
								
							}
						}
						
						boolean check = groupExists(rule.getRuleGroup().getName());
						if (check)
							ruleEditedInd .addProperty(hasGroup, groupHndlr.add(rule.getRuleGroup()));
						else
							ruleEditedInd .addProperty(hasGroup,this.ontLoader.getModel().getIndividual(this.NS + rule.getRuleGroup().getName()));
						
						if (rule.getUserEditedBy() != null)
						{
							Property editedBy = this.ontLoader.getModel().getProperty(this.NS + "editedBy");
							if (this.ontLoader.getModel().getIndividual(this.NS + rule.getUserEditedBy().getUserName()) != null)
							{
								ruleEditedInd .addProperty(editedBy, this.ontLoader.getModel().getIndividual(this.NS + rule.getUserEditedBy().getUserName()));
							}
							else
								log.info("user does not exist..");
						}
						
						if (rule.getEditingDate() != "")
						{
							Property editedAt = this.ontLoader.getModel().getProperty(this.NS + "editedAt");
							ruleEditedInd.addLiteral(editedAt, rule.getEditingDate());
						}
						
						
						 ruleEditedInd.addProperty(extension, ind);
						
						//adding special collections
						if(rule.getSpecialCollection() != null){
							for( SpecialCollection spCol: rule.getSpecialCollection().values() ){
								 ruleEditedInd.addProperty( initializeCollection, this.spColHndlr.add( spCol ));
							}
						}
						
						try {
							
							log.info("writing into file ");
							OntologyHandler.write(ontLoader);

						} catch (IOException e) {
							e.printStackTrace();
						}

					}//----- end if new rule individual 
					else {
						log.info("Rule Individual already exist");
					}
					
				} catch (InstantiationException | IllegalAccessException e) {
					e.printStackTrace();
				}

			}//---end if rule individual does not exists. 
			
			else {

				log.info("Rule Individual does not exist");
			}
		}

		return ind;

	}

	/**
	 * @see net.swas.explorer.oh.handler.OntologyHandler#getAll()
	 */
	@Override
	public List<Entity> getAll() {

		//log.info("Rule's getAllIndividual called ..  ");
		List<Entity> ruleList = new ArrayList<Entity>();
		Rule rule = null;
		User user = null;
		User creating_user =null;
		User editing_user = null;

		@SuppressWarnings("unchecked")
		ExtendedIterator<Individual> indList = (ExtendedIterator<Individual>) this.ontClass
				.listInstances();
		while (indList.hasNext()) {

			try {

				Individual ind = indList.next();
				Property hasID = this.ontLoader.getModel().getProperty(	this.NS + "hasSemRuleID");
				Property hasCondition = this.ontLoader.getModel().getProperty(this.NS + "hasCondition");
				Property hasDisruptiveAction = this.ontLoader.getModel().getProperty(this.NS + "hasDisruptiveAction");
				Property hasPhase= this.ontLoader.getModel().getProperty( this.NS + "executedInPhase");
				Property hasMetaData = this.ontLoader.getModel().getProperty(this.NS + "hasMetaData");
				Property initializeCollection = this.ontLoader.getModel().getProperty(this.NS + "initializeCollection"); //Property For Special Collections
				Property hasGroup = this.ontLoader.getModel().getProperty(this.NS + "hasGroup");
				Property createdBy = this.ontLoader.getModel().getProperty(this.NS + "createdBy");
				Property createdAt = this.ontLoader.getModel().getProperty(this.NS + "createdAt");
				Property editedBy = this.ontLoader.getModel().getProperty(this.NS + "editedBy");
				Property editedAt = this.ontLoader.getModel().getProperty(this.NS + "editedAt");
				Property belongsTo = this.ontLoader.getModel().getProperty(this.NS + "belongsTo");
				
				OntResource condition = (OntResource) ind.getPropertyValue(hasCondition);
				OntResource metaData = (OntResource) ind.getPropertyValue(hasMetaData);
				OntResource disruptiveAction = (OntResource) ind.getPropertyValue(hasDisruptiveAction);
				OntResource phase = (OntResource) ind.getPropertyValue(hasPhase);
				OntResource policy = (OntResource) ind.getPropertyValue(hasGroup);
				OntResource user1 = (OntResource) ind.getPropertyValue(createdBy);
				OntResource editUser = (OntResource) ind.getPropertyValue(editedBy);
				OntResource resource = (OntResource) ind.getPropertyValue(belongsTo);

				rule = (Rule) EntityFactory.RULE.getObject();
				user = (User) EntityFactory.USER.getObject();

				rule.setID(ind.getProperty(hasID).getString());
				rule.setCreationDate(ind.getProperty(createdAt).getString());
				rule.setRuleTitle(ind.getLabel("EN"));
				rule.setComment(ind.getComment("EN"));
				
				//log.info("condition local name : " + condition.getLocalName());
				rule.setCondition((Condition) (this.conditionHndlr.get(condition.getLocalName().split("\\.")[1])));
				rule.setDisruptiveAction(disruptiveAction.getLocalName());
				
				if (phase != null)
				{
					rule.setPhase( Integer.parseInt(phase.getLocalName().split("\\.")[1]) );
				}
				
				
				rule.setMetaData((MetaData) this.mdHndlr.get(metaData.getLocalName().split("\\.")[1]));
				rule.setRuleGroup((RuleGroup) this.groupHndlr.get(policy.getLocalName()));
				rule.setUserCreatedBy((User)this.usrHndlr.get(user1.getLocalName()));
				if (resource != null)
				{
					rule.setResource((net.swas.explorer.ec.Resource) this.resourceHandlr.get(resource.getLocalName()));
				}
				

				//----rule editor---------
				if(ind.getProperty(editedAt) != null){
					rule.setEditingDate(ind.getProperty(editedAt).getString());
				}
				if(editUser != null){
					rule.setUserEditedBy((User)this.usrHndlr.get(editUser.getLocalName()));
				}
				
				//getting special collection individuals
				NodeIterator spColList = ind.listPropertyValues(initializeCollection);
				
				Map<String, SpecialCollection> spCols = new HashMap<String, SpecialCollection>();
				while (spColList.hasNext()) {
					
					OntResource spColInd = (OntResource) spColList.next();
					spCols.put(spColInd.asIndividual().getOntClass().getLocalName(),
								(SpecialCollection)spColHndlr.get(spColInd.getLocalName()));

				}
				rule.setSpecialCollection(spCols);
				ruleList.add(rule);

			} catch (InstantiationException | IllegalAccessException e) {
				log.info("Could not initiate Rule object ");
				e.printStackTrace();
			}
		}

		return ruleList;

	}

	/**
	 * To get class name
	 * @return className
	 */
	public String getClassName(){
		return this.ruleClassName;
	}
	
	/**
	 * This function is for getting list of rules by user who created 
	 * those rules.
	 * 
	 * @param user
	 * @return entity
	 */
	public List<Entity> getRuleByUser( String user ) {

		log.info("Rule's getRuleByUser called ..  ");
		List<Entity> ruleList = new ArrayList<Entity>();
		ChainRuleHandler chHandler = new ChainRuleHandler(this.ontLoader);
		Rule rule = null;
		ChainRule chRule = null;
		
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
		
		query += "SELECT ?rule " + "WHERE{ ";
		query += "?rule ruleEngine:createdBy ruleEngine:" + user + ". }";
		
		log.info("query : " + query);
		QueryExecution qexec = QueryExecutionFactory.create(query,
				Syntax.syntaxSPARQL_11, this.ontLoader.getModel());
		try {
			
			ResultSet rs = qexec.execSelect();
			while (rs.hasNext()) {
				
				QuerySolution qs = rs.next();
				//log.info("Individuals : " + qs.getResource("rule").getLocalName());
				Individual ind = this.ontLoader.getModel().getIndividual(qs.getResource("rule").getURI());
			
				if( ind != null){
					
					if(ind.getOntClass().getLocalName().equals(this.getClassName())){
						
						rule = (Rule) this.get(ind.getLocalName()
								.split("\\.")[1]);
						ruleList.add(rule);
					
					} else if(ind.getOntClass().getLocalName().equals(chHandler.getClassName())){
						
						chRule = (ChainRule) chHandler.get(ind.getLocalName()
								.split("\\.")[1]);
						ruleList.add(chRule);
					
					}
					
				}
				
			}
		
		} finally {
			qexec.close();
		}
		
		return ruleList;
	
	}
	
	/**
	 * This function retrieves list of rules associated with particular group
	 * @param groupName
	 * @param user
	 * @return list of rules and chain rules
	 */
	public List<Entity> getRuleByGroup( String groupName, String user ) {

		log.info("Rule's getRuleByGroup called ..  ");
		List<Entity> ruleList = new ArrayList<Entity>();
		ChainRuleHandler chHandler = new ChainRuleHandler(this.ontLoader);
		Rule rule = null;
		ChainRule chRule = null;
		
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
		
		query += "SELECT ?rule " + "WHERE{ " +
				" ?rule ruleEngine:hasGroup ruleEngine:" + groupName + ". } ";
		
		log.info("query : " + query);
		QueryExecution qexec = QueryExecutionFactory.create(query,
				Syntax.syntaxSPARQL_11, this.ontLoader.getModel());
		try {
			
			ResultSet rs = qexec.execSelect();
			while (rs.hasNext()) {
				
				QuerySolution qs = rs.next();
				//log.info("Individuals : " + qs.getResource("rule").getLocalName());
				Individual ind = this.ontLoader.getModel().getIndividual(qs.getResource("rule").getURI());
			
				if( ind != null){
					
					if(ind.getOntClass().getLocalName().equals(this.getClassName())){
						
						rule = (Rule) this.get(ind.getLocalName()
								.split("\\.")[1]);
						ruleList.add(rule);
					
					} else{
						
						chRule = (ChainRule) chHandler.get(ind.getLocalName()
								.split("\\.")[1]);
						ruleList.add(chRule);
					
					}
					
				}
				
			}
		
		} finally {
			qexec.close();
		}
		
		return ruleList;
	
	}
	
	/**
	 * This function retrieves list of rules associated with particular resource of application profile
	 * @param resourceName
	 * @param user
	 * @return list of rules and chain rules
	 */
	public List<Entity> getRuleByResource( String resourceName, String user) {

		log.info("Rule's getRuleByResource called ..  ");
		List<Entity> ruleList = new ArrayList<Entity>();
		ChainRuleHandler chHandler = new ChainRuleHandler(this.ontLoader);
		Rule rule = null;
		ChainRule chRule = null;
		
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
		
		query += "SELECT ?rule " + "WHERE{ " +
				" ?rule ruleEngine:belongsTo ruleEngine:" + resourceName + ". } ";
		
		log.info("query : " + query);
		QueryExecution qexec = QueryExecutionFactory.create(query,
				Syntax.syntaxSPARQL_11, this.ontLoader.getModel());
		try {
			
			ResultSet rs = qexec.execSelect();
			while (rs.hasNext()) {
				
				QuerySolution qs = rs.next();
				log.info("Individuals : " + qs.getResource("rule").getLocalName());
				Individual ind = this.ontLoader.getModel().getIndividual(qs.getResource("rule").getURI());
			
				if( ind != null){
					
					if(ind.getOntClass().getLocalName().equals(this.getClassName())){
						
						rule = (Rule) this.get(ind.getLocalName()
								.split("\\.")[1]);
						ruleList.add(rule);
					
					} else{
						
						chRule = (ChainRule) chHandler.get(ind.getLocalName()
								.split("\\.")[1]);
						ruleList.add(chRule);
					
					}
					
				}
				
			}
		
		} finally {
			qexec.close();
		}
		
		return ruleList;
	
	}
	
	/**
	 * This function rRetrieves list of rules associated with particular group
	 * @param groupName
	 * @return list of rules and chain rules
	 */
	public List<Entity> getRuleByGroup( String groupName) {

		log.info("Rule's getRuleByGroup called ..  ");
		List<Entity> ruleList = new ArrayList<Entity>();
		ChainRuleHandler chHandler = new ChainRuleHandler(this.ontLoader);
		Rule rule = null;
		ChainRule chRule = null;
		
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
		
		query += "SELECT ?rule " + "WHERE{ " +
				" ?rule ruleEngine:hasGroup ruleEngine:" + groupName + ". } ";
		
		log.info("query : " + query);
		QueryExecution qexec = QueryExecutionFactory.create(query,
				Syntax.syntaxSPARQL_11, this.ontLoader.getModel());
		try {
			
			ResultSet rs = qexec.execSelect();
			while (rs.hasNext()) {
				
				QuerySolution qs = rs.next();
				//log.info("Individuals : " + qs.getResource("rule").getLocalName());
				Individual ind = this.ontLoader.getModel().getIndividual(qs.getResource("rule").getURI());
			
				if( ind != null){
					
					if(ind.getOntClass().getLocalName().equals(this.getClassName())){
						
						rule = (Rule) this.get(ind.getLocalName()
								.split("\\.")[1]);
						ruleList.add(rule);
					
					} else{
						
						chRule = (ChainRule) chHandler.get(ind.getLocalName()
								.split("\\.")[1]);
						ruleList.add(chRule);
					
					}
					
				}
				
			}
		
		} finally {
			qexec.close();
		}
		
		return ruleList;
	
	}
	
	/**
	 * This function is for getting list of rules by keywords
	 * 
	 * @param keywordsString
	 * @return map of keyword and rules
	 */
	public Map<String, Entity> getRuleByKeywords( String keywordsString, String user ) {

		log.info("Rule's getRuleByKeywords called ..  :" + keywordsString);
		Map<String, Entity> ruleList = new HashMap<String, Entity>();
		ChainRuleHandler chHandler = new ChainRuleHandler(this.ontLoader);
		Rule rule = null;
		ChainRule chRule = null;
		if(!keywordsString.trim().equals("")){
			
			String prefixes = "";
			String query = "";
			prefixes += "PREFIX rdf: <"
					+ this.ontLoader.getConfiguration().getRdfNameSpace()
					+ NS_POSTFIX + "> ";
			prefixes += "PREFIX rdfs: <"
					+ this.ontLoader.getConfiguration().getRdfsNameSpace()
					+ NS_POSTFIX + "> ";
			prefixes += "PREFIX owl: <"
					+ this.ontLoader.getConfiguration().getOwlNameSpace()
					+ NS_POSTFIX + "> ";
			prefixes += "PREFIX ruleEngine: <"
					+ this.ontLoader.getConfiguration().getRuleEngineNameSpace()
					+ NS_POSTFIX + "> ";
					
			
			StringTokenizer stringTokens = new StringTokenizer(keywordsString);
			
			query += prefixes + "SELECT * WHERE{ ";
			
			while (stringTokens.hasMoreTokens()) {
				
				String token = stringTokens.nextToken();
				log.info(token);
				
//				query += "{ ?c1 rdf:type owl:Class . ?i rdf:type ?c1 . FILTER regex(str(?i), \"" + token + "\", \"i\") . ?c2 ?p1 ?i . }";
				query += "{{ ?c1 rdf:type owl:Class . ?i rdf:type ?c1 . FILTER regex(str(?i), \"" + token + "\", \"i\") . ?c2 ?p1 ?i . }";
				query += " UNION { ?c1 rdf:type owl:Class . ?i rdf:type ?c1 . ?i ?p ?v . FILTER regex(str(?v), \"" + token + "\", \"i\") . ?c2 ?p1 ?i . }}";
				
				if(stringTokens.hasMoreTokens()){
					query += " UNION ";
				}
				
			}
			query += " } ";
		
			log.info("query new : " + query);
			
			QueryExecution qexec = QueryExecutionFactory.create(query,
					Syntax.syntaxSPARQL_11, this.ontLoader.getModel());
			try {
				ResultSet rs = qexec.execSelect();
				while (rs.hasNext()) {
					
					QuerySolution qs = rs.next();
					Individual ind = this.ontLoader.getModel().getIndividual(qs.getResource("c2").getURI());
					
					if(ind != null) {
						
						if(ind.getOntClass().getLocalName().equals(this.getClassName())) {
							rule = (Rule) this.get(ind.getLocalName().split("\\.")[1]);
							ruleList.put(rule.getID(), rule);
						}
						
						else if(ind.getOntClass().getLocalName().equals(chHandler.getClassName())) {
							chRule = (ChainRule) chHandler.get(ind.getLocalName().split("\\.")[1]);
							ruleList.put(chRule.getID(), chRule);
						}
						
						else {
							recursiveQuery(ind, ruleList);
						}				
					}
				}
			} finally {
				qexec.close();
			}
			
		}
		
		return ruleList;	
	
	}
	
	/**
	 * This function is for getting list of rules by multilevel facets
	 * 
	 * @param facets
	 * @param user
	 * @return list of rules and chain rules
	 */
	public List<Entity> getRuleByFacets( Map<String, List<String>> facets, String user) {

		log.info("Rule's getRuleByFacets called ..  ");
		Map<String, Entity> ruleList = new HashMap<String, Entity>();
		
		if(facets.size() > 0){
			ChainRuleHandler chHandler = new ChainRuleHandler(this.ontLoader);
			Rule rule = null;
			ChainRule chRule = null;
			
			String prefixes = "";
			String query = "";
			prefixes += "PREFIX rdf: <"
					+ this.ontLoader.getConfiguration().getRdfNameSpace()
					+ NS_POSTFIX + "> ";
			prefixes += "PREFIX rdfs: <"
					+ this.ontLoader.getConfiguration().getRdfsNameSpace()
					+ NS_POSTFIX + "> ";
			prefixes += "PREFIX owl: <"
					+ this.ontLoader.getConfiguration().getOwlNameSpace()
					+ NS_POSTFIX + "> ";
			prefixes += "PREFIX ruleEngine: <"
					+ this.ontLoader.getConfiguration().getRuleEngineNameSpace()
					+ NS_POSTFIX + "> ";
			query += prefixes + "SELECT * WHERE{ ";
			
			int i = 0;
			for (String key : facets.keySet()) {
				
				if (facets.size() > 1) {
					query += " { ";
				}
	
				List<String> elemList = facets.get(key);
				for (int j = 0; j < elemList.size(); j++) {
					
					String token = elemList.get(j);
					
					log.info("token :" + token);
					query += "{ ?c1 rdf:type owl:Class . ?i rdf:type ?c1 . FILTER regex(str(?i), \"" + token + "\", \"i\") . ?c2 ?p1 ?i . }";
					
					if (j != elemList.size() - 1) {
						query += " UNION ";
					}
					
				}
	
				if (facets.size() > 1) {
					query += " } ";
				}
	
				if (i != facets.size() - 1) {
					query += " . ";
				}
				i++;
		
			}
			
			query += " } ";
		
			log.info("query : " + query);
			
			QueryExecution qexec = QueryExecutionFactory.create(query,
					Syntax.syntaxSPARQL_11, this.ontLoader.getModel());
			try {
				ResultSet rs = qexec.execSelect();
				while (rs.hasNext()) {
					
					QuerySolution qs = rs.next();
					Individual ind = this.ontLoader.getModel().getIndividual(qs.getResource("c2").getURI());
					
					if(ind != null) {
						
						if(ind.getOntClass().getLocalName().equals(this.getClassName())) {
							rule = (Rule) this.get(ind.getLocalName().split("\\.")[1]);
							ruleList.put(rule.getID(), rule);
						}
						
						else if(ind.getOntClass().getLocalName().equals(chHandler.getClassName())) {
							chRule = (ChainRule) chHandler.get(ind.getLocalName().split("\\.")[1]);
							ruleList.put(chRule.getID(), chRule);
						}
						
						else {
							recursiveQuery(ind, ruleList);
						}		
						
					}
				}
			} finally {
				qexec.close();
			}
		}
		return new ArrayList<Entity>(ruleList.values());	
		
	}
	
	
	/**
	 * This function is for getting rules that are identical except the 
	 * condition value is not same because of different encoding schemes  
	 * @param trnsfrmtn
	 * @param value
	 * @param rule 
	 * @return map of transformations and rules
	 */
	public Map<String, Entity> getRuleByTrnsfrmtn(Rule rule, String trnsfrmtn, String value) {

		log.info("Rule's getRuleByTrnsfrmtn called ..  ");
		Map<String, Entity> ruleList =  new HashMap<String, Entity>();
		
		Rule revRule = null;
		
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
		
		query += "SELECT ?rule " + "WHERE{ " ;
		
		for(Entity entity : rule.getCondition().getVariable().getCollectionExpressions()){
			query += " ?rule ruleEngine:haveExpressions ruleEngine:CollectionExpression." + entity.getID() + " . " ;		
		}
	
		for(Entity entity : rule.getCondition().getVariable().getVariableExpressions()){
			query += " ?rule ruleEngine:haveExpressions ruleEngine:VariableExpression." + entity.getID() + " . " ;
		}
		
		query += " ?rule ruleEngine:haveTransformation ruleEngine:" + trnsfrmtn  + " . " ;
		query += " } ";
		
		log.info("query : " + query);
		QueryExecution qexec = QueryExecutionFactory.create(query,
				Syntax.syntaxSPARQL_11, this.ontLoader.getModel());
		try {
			
			ResultSet rs = qexec.execSelect();
			while (rs.hasNext()) {
				
				QuerySolution qs = rs.next();
				//log.info("Individuals : " + qs.getResource("rule").getLocalName());
				Individual ind = this.ontLoader.getModel().getIndividual(qs.getResource("rule").getURI());
			
				if( ind != null){
					
					log.info("OntClass : " + ind.getOntClass().getLocalName());
					if(ind.getOntClass().getLocalName().equals(this.getClassName())){
						
						rule = (Rule) this.get(ind.getLocalName().split("\\.")[1]);
						ruleList.put(rule.getID(), rule);
						
					} else{
						
						recursiveTrnsfrmtnQuery(ind, ruleList, value);
					
					}
					
				}
				
			}
		
		} finally {
			qexec.close();
		}
		
		return ruleList;	
	
	}
	
	/**
	 * A helper function used by getRulesByKeywords
	 * 
	 * @param Individual, List
	 */
	private void recursiveQuery(Individual sub, Map<String, Entity> ruleList) {

		ChainRuleHandler chHandler = new ChainRuleHandler(this.ontLoader);
		Rule rule = null;
		ChainRule chRule = null;
		
		OntClass simpleRuleClass = this.ontLoader.getModel().getOntClass(this.ontLoader.getConfiguration().getRuleEngineNameSpace() + NS_POSTFIX + "SimpleRule");
		OntClass chainRuleClass = this.ontLoader.getModel().getOntClass(this.ontLoader.getConfiguration().getRuleEngineNameSpace() + NS_POSTFIX + "ChainRule");
		
		String prefixes = "";
		
		prefixes += "PREFIX rdf: <"
				+ this.ontLoader.getConfiguration().getRdfNameSpace()
				+ NS_POSTFIX + "> ";
		prefixes += "PREFIX rdfs: <"
				+ this.ontLoader.getConfiguration().getRdfsNameSpace()
				+ NS_POSTFIX + "> ";
		prefixes += "PREFIX owl: <"
				+ this.ontLoader.getConfiguration().getOwlNameSpace()
				+ NS_POSTFIX + "> ";
		prefixes += "PREFIX ruleEngine: <"
				+ this.ontLoader.getConfiguration().getRuleEngineNameSpace()
				+ NS_POSTFIX + "> ";
		
			String query = "";				
			query += prefixes + "SELECT * WHERE { ?s ?p <" + sub + "> }";
			log.info("query : " + query);							
			
			QueryExecution qexe = QueryExecutionFactory.create(query,
					Syntax.syntaxSPARQL_11, this.ontLoader.getModel());
			ResultSet rs = qexe.execSelect();
			
			while (rs.hasNext()) {
				
				QuerySolution qs = rs.next();
				sub = this.ontLoader.getModel().getIndividual(qs.getResource("s").getURI());
				
				if(sub.getOntClass().equals(simpleRuleClass)) {
					rule = (Rule) this.get(sub.getLocalName().split("\\.")[1]);
					ruleList.put(rule.getID(), rule);
				}
				
				else if(sub.getOntClass().equals(chainRuleClass)) {
					chRule = (ChainRule) chHandler.get(sub.getLocalName().split("\\.")[1]);
					ruleList.put(chRule.getID(), chRule);
				}
				
				else {
					recursiveQuery(sub, ruleList);
				}				
			}
	}
	
	/**
	 * A helper function used by getRulesByTrnsfrmtn
	 * 
	 * @param Individual, List
	 */
	private void recursiveTrnsfrmtnQuery(Individual sub, Map<String, Entity> ruleList, String condValue) {

		log.info("Rule's recursiveTrnsfrmtnQuery ..  ");
		Rule rule = null;
		
		String prefixes = "";
		prefixes += "PREFIX rdf: <"
				+ this.ontLoader.getConfiguration().getRdfNameSpace()
				+ NS_POSTFIX + "> ";
		prefixes += "PREFIX rdfs: <"
				+ this.ontLoader.getConfiguration().getRdfsNameSpace()
				+ NS_POSTFIX + "> ";
		prefixes += "PREFIX owl: <"
				+ this.ontLoader.getConfiguration().getOwlNameSpace()
				+ NS_POSTFIX + "> ";
		prefixes += "PREFIX ruleEngine: <"
				+ this.ontLoader.getConfiguration().getRuleEngineNameSpace()
				+ NS_POSTFIX + "> ";
		
		OntClass simpleRuleClass = this.ontLoader.getModel().getOntClass(this.ontLoader.getConfiguration().getRuleEngineNameSpace() + NS_POSTFIX + "SimpleRule");
		
		log.info("Rule variable:" + sub.getOntClass().getLocalName());	
		if(sub.getOntClass().getLocalName().equals("RuleVariable")){
			
			String query = "";				
			query += prefixes + "SELECT * WHERE { " + 
					"?cond ruleEngine:appliedOn	ruleEngine:" + sub.getLocalName()  + " . " +
					"?cond ruleEngine:hasValue ?value ." +
					"FILTER (str(?value) = \""+ condValue +"\") . }";
			
			log.info("query : " + query);							
			
			QueryExecution qexe = QueryExecutionFactory.create(query,
					Syntax.syntaxSPARQL_11, this.ontLoader.getModel());
			ResultSet rs = qexe.execSelect();
			
			while (rs.hasNext()) {
				
				QuerySolution qs = rs.next();
				sub = this.ontLoader.getModel().getIndividual(qs.getResource("cond").getURI());
				recursiveTrnsfrmtnQuery(sub, ruleList, null);
			}
			
		} else{
			
			String query = "";				
			query += prefixes + "SELECT * WHERE { ?s ?p <" + sub + "> }";
			log.info("query : " + query);							
			
			QueryExecution qexe = QueryExecutionFactory.create(query,
					Syntax.syntaxSPARQL_11, this.ontLoader.getModel());
			ResultSet rs = qexe.execSelect();
			
			while (rs.hasNext()) {
				
				QuerySolution qs = rs.next();
				sub = this.ontLoader.getModel().getIndividual(qs.getResource("s").getURI());
				
				if(sub.getOntClass().equals(simpleRuleClass)) {
					rule = (Rule) this.get(sub.getLocalName().split("\\.")[1]);
					ruleList.put(rule.getID(), rule);
				} else{
					recursiveTrnsfrmtnQuery(sub, ruleList, null);
				}				
			}
			
		}
	}
	
	/**
	 * This function checks if group exists in ontology
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
	 * This function checks if resource exists in ontology
	 * @param name
	 * @return
	 */
	private boolean resourceExists(String name) {
		Individual resourceInd = null;
		boolean check = false;
		resourceInd = this.ontLoader.getModel().getIndividual(this.NS + name);
		if (resourceInd == null)
			check = false;
		else
			check = true;
		return check;
	}
	
}
