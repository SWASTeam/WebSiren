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

import net.swas.explorer.ec.CollectionExpression;
import net.swas.explorer.ec.Sequence;
import net.swas.explorer.ec.Variable;
import net.swas.explorer.ec.VariableExpression;
import net.swas.explorer.ecf.Entity;
import net.swas.explorer.ecf.EntityFactory;
import net.swas.explorer.oh.lo.OntologyLoader;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

/**
 * This class is responsible for the management of variables/target individuals in ontology.
 * It uses {@link OntologyLoader} to load configurations of knowledge base.
 * 
 */
public class VariableHandler extends OntologyHandler {

	
	private final static Logger log = LoggerFactory
			.getLogger(VariableHandler.class);
	private final String varClassName = "RuleVariable";
	private VarExpressionHandler varExpHndlr = null;
	private ColExpressionHandler colExpHndlr = null;
	private SequenceHandler seqHndlr = null; // Sequence handler

	/**
	 * @param loader
	 *            object loads ontModel using ontologyLoader object loads owl
	 *            name space using ontologyLoader object loads class
	 *            RuleVariable from ontology if exists otherwise create
	 *            RuleVariable class in ontology
	 */
	public VariableHandler(OntologyLoader loader) {

		this.ontLoader = loader;
		this.NS = this.ontLoader.getConfiguration().getRuleEngineNameSpace()
				+ NS_POSTFIX;
		
		this.ontClass = this.ontLoader.getModel().createClass(
				this.NS + this.varClassName);
		this.varExpHndlr = new VarExpressionHandler(this.ontLoader);
		this.colExpHndlr = new ColExpressionHandler(this.ontLoader);
		this.seqHndlr = new SequenceHandler(this.ontLoader);

	}

	/**
	 * @see net.swas.explorer.oh.handler.OntologyHandler#add(net.swas.explorer.ecf.Entity)
	 */
	@Override
	public Individual add(Entity entity) {

		//log.info("Variable's addIndividual called ..  ");

		Individual varInd = null;
		if (entity instanceof Variable) {
			Variable var = (Variable) entity;
			int ID = Sequence.getID(seqHndlr, this.varClassName);

			if (this.ontLoader.getModel().getIndividual(
					this.NS + this.varClassName + "." + ID) == null) {
				varInd = this.ontClass.createIndividual(this.NS
						+ this.varClassName + "." + ID);

				Property hasID = this.ontLoader.getModel().getProperty(
						this.NS + "hasVariableID");
				Property hasTransformation = this.ontLoader.getModel()
						.getProperty(this.NS + "haveTransformation");
				Property haveExps = this.ontLoader.getModel().getProperty(
								this.NS + "haveExpressions");

				varInd.addLabel(this.varClassName + "." + ID, "EN");
				varInd.addLiteral(hasID, new String("" + ID));
				
				if(var.getTransformation() != null){
					
					for (String varTrans : var.getTransformation()) {
						
						varTrans = varTrans.trim();
						Individual trans = this.ontLoader.getModel().getIndividual( this.NS + varTrans);
						log.info( "Transformation: " + varTrans );
						varInd.addProperty(hasTransformation, trans);
					
					}
					
				}	
				
				for (VariableExpression varExp: var.getVariableExpressions()) {

					//log.info("Variable Expression : " +varExp.getOperator()+varExp.getVariable());
					varInd.addProperty(haveExps, varExpHndlr.add(varExp));
				}
				
				for (CollectionExpression colExp: var.getCollectionExpressions()) {
					
					//log.info("Collection Expression : " +colExp.getOperator()+colExp.getCollection()+":"+colExp.getElement());
					varInd.addProperty(haveExps, colExpHndlr.add(colExp));
				}
				
			}
		}

		return varInd;
	}

	/**
	 * @see net.swas.explorer.oh.handler.OntologyHandler#remove(java.lang.String)
	 */
	@Override
	public void remove(String ID) {

		//log.info("Variable Remove Individual Called...");
		Individual varInd = this.ontLoader.getModel().getIndividual(
				this.NS + this.varClassName + "." + ID);
		if (varInd != null) {
			
//			Property haveExpressions = this.ontLoader.getModel().getProperty(this.NS + "haveExpressions");
//			
//			NodeIterator expressions = varInd.listPropertyValues(haveExpressions);
//			while (expressions.hasNext()) {
//
//				Resource expInd = (Resource) expressions.removeNext().asResource();
//				
//				if(expInd.getLocalName().split("\\.")[0].equals(varExpHndlr.getClassName())){
//					
//					this.varExpHndlr.remove(expInd.getLocalName().split("\\.")[1]);
//				
//				} else{
//					
//					this.colExpHndlr.remove(expInd.getLocalName().split("\\.")[1]);
//				
//				}
//			}
//			
//			this.ontClass.dropIndividual(varInd);
			
			varInd.remove();
			
		} else {

			log.info("Variable individual does not exist");

		}

	}

	/**
	 * @see net.swas.explorer.oh.handler.OntologyHandler#get(java.lang.String)
	 */
	@Override
	public Entity get(String ID) {

		//log.info("Variable get Individual Called.." + ID + ".");
		Variable ruleVariable = null;
		Individual varInd = this.ontLoader.getModel().getIndividual(
				this.NS + this.varClassName + "." + ID);

		if (varInd != null) {

			Property hasID = this.ontLoader.getModel().getProperty(
					this.NS + "hasVariableID");
			Property hasTransformation = this.ontLoader.getModel().getProperty(
					this.NS + "haveTransformation");
			Property haveExpressions = this.ontLoader.getModel().getProperty(
					this.NS + "haveExpressions");

			try {
				ruleVariable = (Variable) EntityFactory.VARIABLE.getObject();

				ruleVariable.setID(varInd.getProperty(hasID).getString());

				/*
				 * Setting transformations of Rule variable
				 */
				NodeIterator transformationList = varInd
						.listPropertyValues(hasTransformation);
				List<String> transformations = new ArrayList<String>();

				while (transformationList.hasNext()) {

					Resource transInd = (Resource) transformationList.next().asResource();
					transformations.add(transInd.getLocalName());

				}

				ruleVariable.setTransformation(transformations);

				/*
				 * setting variables of Rule Variable
				 */
				NodeIterator variableList = varInd
						.listPropertyValues(haveExpressions);
				List<VariableExpression>  varExpList  = new ArrayList<VariableExpression>();
				List<CollectionExpression>  colExpList  = new ArrayList<CollectionExpression>();

				while (variableList.hasNext()) {

					Resource expInd = (Resource) variableList.next().asResource();
					//log.info("Expressions :" + expInd.getLocalName());
					if(expInd.getLocalName().split("\\.")[0].equals(varExpHndlr.getClassName())){
						
						varExpList.add((VariableExpression) varExpHndlr.get(expInd.getLocalName().substring(varExpHndlr.getClassName().length() + 1)));
					
					} else{
						
						colExpList.add((CollectionExpression) colExpHndlr.get( expInd.getLocalName().substring(colExpHndlr.getClassName().length() + 1) ));
					
					}

				}

				ruleVariable.setVariableExpressions(varExpList);
				ruleVariable.setCollectionExpressions(colExpList);

			} catch (InstantiationException | IllegalAccessException e) {
				log.info("Could not initiate Variable object ");
				e.printStackTrace();
			}

		}
		return ruleVariable;
	}

	/**
	 * @see net.swas.explorer.oh.handler.OntologyHandler#update(net.swas.explorer.ecf.Entity)
	 */
	@Override
	public Individual update(Entity entity) {

		//log.info("Variable update Individual Called...");
		Individual varInd = null;
		if (entity instanceof Variable) {
			Variable variable = (Variable) entity;
			varInd = this.ontLoader.getModel().getIndividual(
					this.NS + this.varClassName + "." + variable.getID());
			if (varInd != null) {

				Property hasID = this.ontLoader.getModel().getProperty(
						this.NS + "hasVariableID");
				Property hasTransformation = this.ontLoader.getModel()
						.getProperty(this.NS + "haveTransformation");
				Property haveExps = this.ontLoader.getModel().getProperty(
						this.NS + "haveExpressions");

				varInd.setLabel(this.varClassName + "." + variable.getID(),
						"EN");
				varInd.setPropertyValue(hasID, this.ontLoader.getModel()
						.createTypedLiteral(variable.getID()));

				// Rule variable has Transformations
				varInd.removeAll(hasTransformation);
				
				if(variable.getTransformation() != null){
					for (String varTrans : variable.getTransformation()) {
	
						Individual trans = this.ontLoader.getModel().getIndividual( this.NS + varTrans);
						varInd.addProperty(hasTransformation, trans);
						
					}
				}
				// Rule VAriables have Expressions
				NodeIterator variableList = varInd
						.listPropertyValues(haveExps);
				
				while (variableList.hasNext()) {

					Resource expInd = (Resource) variableList.removeNext().asResource();
					if(this.varExpHndlr.getClassName().equals(expInd.getLocalName().split("\\.")[0])){
						this.varExpHndlr.remove(expInd.getLocalName().split("\\.")[1]);
					} else{
						this.colExpHndlr.remove(expInd.getLocalName().split("\\.")[1]);
					}

				}
				varInd.removeAll(haveExps);
				
				for (VariableExpression varExp: variable.getVariableExpressions()) {

					varInd.addProperty(haveExps, varExpHndlr.add(varExp));
				}
				
				for (CollectionExpression colExp: variable.getCollectionExpressions()) {

					varInd.addProperty(haveExps, colExpHndlr.add(colExp));
				}

			} else {

				log.info("Variable Individual does not exist");
			}
		}

		return varInd;
	}

	/**
	 * @see net.swas.explorer.oh.handler.OntologyHandler#getAll()
	 */
	@Override
	public List<Entity> getAll() {

		//log.info("Variable's getAllIndividual called ..  ");

		List<String> transformations = new ArrayList<String>();
		List<Entity> ruleVariableList = new ArrayList<Entity>();
		Variable ruleVariable = null;

		@SuppressWarnings("unchecked")
		ExtendedIterator<Individual> indList = (ExtendedIterator<Individual>) this.ontClass
				.listInstances();

		while (indList.hasNext()) {
			try {
				Property hasID = this.ontLoader.getModel().getProperty(
						this.NS + "hasVariableID");
				Property hasTransformation = this.ontLoader.getModel()
						.getProperty(this.NS + "haveTransformation");
				Property haveExpressions = this.ontLoader.getModel().getProperty(
						this.NS + "haveExpressions");

				Individual varInd = indList.next();

				ruleVariable = (Variable) EntityFactory.VARIABLE.getObject();

				ruleVariable.setID(varInd.getProperty(hasID).getString());

				/*
				 * Setting transformations of Rule variable
				 */
				NodeIterator transformationList = varInd
						.listPropertyValues(hasTransformation);

				while (transformationList.hasNext()) {

					Resource transInd = (Resource) transformationList.next().asResource();
					transformations.add(transInd.getLocalName());

				}

				ruleVariable.setTransformation(transformations);

				/*
				 * setting variables of Rule Variable
				 */
				NodeIterator variableList = varInd
						.listPropertyValues(haveExpressions);
				List<VariableExpression>  varExpList  = new ArrayList<VariableExpression>();
				List<CollectionExpression>  colExpList  = new ArrayList<CollectionExpression>();

				while (variableList.hasNext()) {

					Resource expInd = (Resource) variableList.next().asResource();
					if(expInd.getLocalName().split("\\.")[0].equals(varExpHndlr.getClassName())){
						
						varExpList.add((VariableExpression) varExpHndlr.get(expInd.getLocalName().split("\\.")[1]));
					
					} else{
						
						colExpList.add((CollectionExpression) colExpHndlr.get( expInd.getLocalName().split("\\.")[1] ));
					
					}

				}

				ruleVariable.setVariableExpressions(varExpList);
				ruleVariable.setCollectionExpressions(colExpList);

			} catch (InstantiationException | IllegalAccessException e) {
				log.info("Could not initiate variable object ");
				e.printStackTrace();
			}

		}

		return ruleVariableList;
	}

}
