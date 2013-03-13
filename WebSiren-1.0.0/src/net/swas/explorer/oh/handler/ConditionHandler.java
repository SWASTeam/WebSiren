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

import net.swas.explorer.ec.Condition;
import net.swas.explorer.ec.Sequence;
import net.swas.explorer.ec.UserDefinedVariable;
import net.swas.explorer.ec.Variable;
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
 * This class is responsible for the management of rule condition. It uses {@link OntologyLoader} to load 
 * configurations of knowledge base.
 * 
 */
public class ConditionHandler extends OntologyHandler {

	
	private final static Logger log = LoggerFactory
			.getLogger(ConditionHandler.class);
	private final String condClassName = "Condition";
	private VariableHandler varHndlr = null;
	private SequenceHandler seqHndlr = null; // Sequence handler
	private UserDefinedVariableHandler udvHandler = null;

	/**
	 * Constructor
	 * @param loader
	 */
	public ConditionHandler(OntologyLoader loader) {

		this.ontLoader = loader;
		this.NS = this.ontLoader.getConfiguration().getRuleEngineNameSpace()
				+ NS_POSTFIX;
		
		log.info("Condition Name Space : " + this.NS + this.condClassName);

		this.ontClass = this.ontLoader.getModel().createClass(
				this.NS + this.condClassName);
		this.varHndlr = new VariableHandler(this.ontLoader);
		this.seqHndlr = new SequenceHandler(this.ontLoader);
		this.udvHandler = new UserDefinedVariableHandler( this.ontLoader );

	}

	/**
	 * @see net.swas.explorer.oh.handler.OntologyHandler#add(net.swas.explorer.ecf.Entity)
	 */
	@Override
	public Individual add(Entity entity) {

		//log.info("Condition's addIndividual called ..  ");
		Individual ind = null;
		if (entity instanceof Condition) {

			Condition condition = (Condition) entity;
			int ID = Sequence.getID(seqHndlr, this.condClassName);

			if (this.ontLoader.getModel().getIndividual(
					this.NS + this.condClassName + "." + ID) == null) {

				ind = this.ontClass.createIndividual(this.NS
						+ this.condClassName + "." + ID);
				Individual op = this.ontLoader.getModel().getIndividual(
						this.NS + condition.getOperator());
				Individual var = this.varHndlr.add(condition.getVariable());

				Property hasID = this.ontLoader.getModel().getProperty(
						this.NS + "hasConditionID");
				Property hasOperator = this.ontLoader.getModel().getProperty(
						this.NS + "hasOperator");
				Property hasValue = this.ontLoader.getModel().getProperty(
						this.NS + "hasValue");
				Property appliedOn = this.ontLoader.getModel().getProperty(
						this.NS + "appliedOn");
				Property setTransVar = this.ontLoader.getModel().getProperty(
								this.NS + "setTranscationVariable");
				Property isNegated = this.ontLoader.getModel().getProperty(this.NS + "isNegated");


				ind.addLabel(this.condClassName + "." + ID, "EN");
				ind.addLiteral(hasID, new String("" + ID));
				ind.addProperty(hasOperator, op);
				ind.addLiteral(hasValue, condition.getValue());
				ind.addProperty(appliedOn, var);
				ind.addLiteral(isNegated, new Boolean(condition.getIsNegated()));
				
				if(condition.getUserDefinedVariables() != null){
					for( UserDefinedVariable udv: condition.getUserDefinedVariables() ){
						
						if( this.udvHandler.isExist( udv.getName() ) != null ){
							ind.addProperty( setTransVar, this.udvHandler.isExist( udv.getName() ));
						} else {
							ind.addProperty( setTransVar, this.udvHandler.add( udv ));
						}
					}
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

		//log.info("Condition's removeIndividual called ..  ");
		Individual ind = this.ontLoader.getModel().getIndividual(
				this.NS + this.condClassName + "." + ID);
		if (ind != null) {

			Property appliedOn = this.ontLoader.getModel().getProperty(
					this.NS + "appliedOn");
			OntResource varInd = (OntResource) ind.getPropertyValue(appliedOn);
			this.varHndlr.remove(varInd.getLocalName().split("\\.")[1]);

			this.ontClass.dropIndividual(ind);
			ind.remove();
			
		} else {

			log.info("Condition individual does not exist");

		}

	}

	/**
	 * @see net.swas.explorer.oh.handler.OntologyHandler#get(java.lang.String)
	 */
	@Override
	public Entity get(String ID) {

		//log.info("Condition's getIndividual called ..  :" + ID);
		Condition condition = null;
		Individual ind = this.ontLoader.getModel().getIndividual(
				this.NS + this.condClassName + "." + ID);
		if (ind != null) {

			try {

				Property hasID = this.ontLoader.getModel().getProperty(
						this.NS + "hasConditionID");
				Property hasOperator = this.ontLoader.getModel().getProperty(
						this.NS + "hasOperator");
				Property hasValue = this.ontLoader.getModel().getProperty(
						this.NS + "hasValue");
				Property appliedOn = this.ontLoader.getModel().getProperty(
						this.NS + "appliedOn");
				Property setTransVar = this.ontLoader.getModel().getProperty(
						this.NS + "setTranscationVariable");
				Property isNegated = this.ontLoader.getModel().getProperty(
						this.NS + "isNegated");
				
				OntResource operator = (OntResource) ind
						.getPropertyValue(hasOperator);

				condition = (Condition) EntityFactory.CONDITION.getObject();
				condition.setID(ind.getProperty(hasID).getString());
				condition.setOperator(operator.getLocalName());
				condition.setValue(ind.getProperty(hasValue).getString());
				condition.setIsNegated(ind.getProperty(isNegated).getBoolean());
				
				OntResource varInd = (OntResource) ind
						.getPropertyValue(appliedOn);
				String varID = varInd.getLocalName().split("\\.")[1];

				condition.setVariable((Variable) this.varHndlr.get(varID));
				
				NodeIterator transVarList = ind
						.listPropertyValues(setTransVar);
				
				List<UserDefinedVariable> transVars = new ArrayList<UserDefinedVariable>();
				while (transVarList.hasNext()) {
					
					OntResource transVarInd = (OntResource) transVarList.next();
					transVars.add((UserDefinedVariable)udvHandler.get(transVarInd.getLocalName()));

				}
				
				condition.setUserDefinedVariables(transVars);

			} catch (InstantiationException | IllegalAccessException e) {

				log.info("Could not initiate Condition object ");
				e.printStackTrace();

			}
		}
		return condition;

	}

	/**
	 * @see net.swas.explorer.oh.handler.OntologyHandler#update(net.swas.explorer.ecf.Entity)
	 */
	@Override
	public Individual update(Entity entity) {

		//log.info("Condition's updateIndividual called ..  ");
		Individual ind = null;
		if (entity instanceof Condition) {

			Condition condition = (Condition) entity;
			ind = this.ontLoader.getModel().getIndividual(
					this.NS + this.condClassName + "." + condition.getID());
			if (ind != null) {

				Individual op = this.ontLoader.getModel().getIndividual(
						this.NS + condition.getOperator());

				Property hasID = this.ontLoader.getModel().getProperty(
						this.NS + "hasConditionID");
				Property hasOperator = this.ontLoader.getModel().getProperty(
						this.NS + "hasOperator");
				Property hasValue = this.ontLoader.getModel().getProperty(
						this.NS + "hasValue");
				Property appliedOn = this.ontLoader.getModel().getProperty(
						this.NS + "appliedOn");
				Property isNegated = this.ontLoader.getModel().getProperty(
						this.NS + "isNegated");
				Property setTransVar = this.ontLoader.getModel().getProperty(
								this.NS + "setTranscationVariable");

				ind.setLabel(this.condClassName + "." + condition.getID(), "EN");
				ind.setPropertyValue(hasID, this.ontLoader.getModel().createTypedLiteral(condition.getID()));
				ind.setPropertyValue(hasOperator, op);
				ind.setPropertyValue(hasValue, this.ontLoader.getModel()
						.createTypedLiteral(condition.getValue()));
				ind.setPropertyValue(appliedOn,
						this.varHndlr.update(condition.getVariable()));
				ind.setPropertyValue(isNegated, this.ontLoader.getModel()
						.createTypedLiteral(new Boolean(condition.getIsNegated())));
				
				// Condition have user defined variables
				NodeIterator udvList = ind
						.listPropertyValues(setTransVar);
				while (udvList.hasNext()) {

					Resource udvInd = (Resource) udvList.removeNext().asResource();
					this.udvHandler.remove(udvInd.getLocalName());

				}
				
				ind.removeAll(setTransVar);
				if(condition.getUserDefinedVariables() != null){
					for( UserDefinedVariable udv: condition.getUserDefinedVariables() ){
						
						if( this.udvHandler.isExist( udv.getName() ) != null ){
							ind.addProperty( setTransVar, this.udvHandler.isExist( udv.getName() ));
						} else {
							ind.addProperty( setTransVar, this.udvHandler.add( udv ));
						}
					}
				}

			} else {

				log.info("Condition Individual does not exist");
			}
		}

		return ind;

	}

	/**
	 * @see net.swas.explorer.oh.handler.OntologyHandler#getAll()
	 */
	@Override
	public List<Entity> getAll() {

		//log.info("Condition's getAllIndividual called ..  ");
		List<Entity> conditionList = new ArrayList<Entity>();
		Condition condition = null;

		@SuppressWarnings("unchecked")
		ExtendedIterator<Individual> indList = (ExtendedIterator<Individual>) this.ontClass
				.listInstances();

		while (indList.hasNext()) {

			try {

				Property hasID = this.ontLoader.getModel().getProperty(
						this.NS + "hasConditionID");
				Property hasOperator = this.ontLoader.getModel().getProperty(
						this.NS + "hasOperator");
				Property hasValue = this.ontLoader.getModel().getProperty(
						this.NS + "hasValue");
				Property appliedOn = this.ontLoader.getModel().getProperty(
						this.NS + "appliedOn");
				Property setTransVar = this.ontLoader.getModel().getProperty(
								this.NS + "setTranscationVariable");
				Property isNegated = this.ontLoader.getModel().getProperty(
						this.NS + "isNegated");

				Individual ind = indList.next();
				OntResource operator = (OntResource) ind
						.getPropertyValue(hasOperator);

				condition = (Condition) EntityFactory.CONDITION.getObject();
				condition.setID(ind.getProperty(hasID).getString());
				condition.setOperator(operator.getLocalName());
				condition.setValue(ind.getProperty(hasValue).getString());
				condition.setIsNegated(ind.getProperty(isNegated).getBoolean());
				
				OntResource varInd = (OntResource) ind
						.getPropertyValue(appliedOn);
				String varID = varInd.getLocalName().split("\\.")[1];

				condition.setVariable((Variable) this.varHndlr.get(varID));
				
				NodeIterator transVarList = ind
						.listPropertyValues(setTransVar);
				
				List<UserDefinedVariable> transVars = new ArrayList<UserDefinedVariable>();
				while (transVarList.hasNext()) {

					OntResource transVarInd = (OntResource) transVarList.next();
					transVars.add((UserDefinedVariable)udvHandler.get( transVarInd.getLocalName()));

				}
				
				condition.setUserDefinedVariables(transVars);

				conditionList.add(condition);

			} catch (InstantiationException | IllegalAccessException e) {
				log.info("Could not initiate Condition object ");
				e.printStackTrace();
			}
		}

		return conditionList;

	}

}
