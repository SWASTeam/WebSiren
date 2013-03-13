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

import net.swas.explorer.ec.VariableExpression;
import net.swas.explorer.ecf.Entity;
import net.swas.explorer.ecf.EntityFactory;
import net.swas.explorer.oh.fields.ElementMap;
import net.swas.explorer.oh.lo.OntologyLoader;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

/**
 * This class is responsible for the management of variable expression individuals in ontology.
 * It uses {@link OntologyLoader} to load configurations of knowledge base.
 * 
 */
public class VarExpressionHandler extends OntologyHandler {

	
	private final static Logger log=LoggerFactory.getLogger(VarExpressionHandler.class);
	private final String varExpClassName = "VariableExpression"; 
	
	/**
	 * Constructor
	 * @param loader
	 */
	public VarExpressionHandler(OntologyLoader loader) {
		
		this.ontLoader = loader;
		this.NS = this.ontLoader.getConfiguration().getRuleEngineNameSpace() + NS_POSTFIX;
		
		//log.info("Variable Expression Name Space : " + this.NS + this.varExpClassName);
		this.ontClass = this.ontLoader.getModel().createClass(this.NS + this.varExpClassName);
		
	}
	/**
	 * @see net.swas.explorer.oh.handler.OntologyHandler#add(net.swas.explorer.ecf.Entity)
	 */
	@Override
	public Individual add(Entity entity) {
		
		//log.info( "Variable Expression's addIndividual called ..  ");
		Individual ind = null;
		if ( entity instanceof VariableExpression){
			
			VariableExpression varExp = (VariableExpression) entity;
			String key = this.getKey(varExp);
			boolean check = this.isExists(key);
			
			if ( check ) {

				ind = this.ontClass.createIndividual( this.NS + this.varExpClassName + "." + key );
				
				Individual op = null; 
				if( !varExp.getOperator().equals("") ){
					
					if( varExp.getOperator().equals(ElementMap.ampersand.toString()) ){
						op = this.ontLoader.getModel().getIndividual( this.NS + ElementMap.ampersand.toString());
					} else{
						op = this.ontLoader.getModel().getIndividual( this.NS + ElementMap.negation.toString());
					}
					
				}
				
				Individual variable = this.ontLoader.getModel().getIndividual( this.NS + varExp.getVariable());

				Property hasID= this.ontLoader.getModel().getProperty( this.NS + "hasVarExpID");
				Property hasVar= this.ontLoader.getModel().getProperty( this.NS + "hasSelectedVariable");
				Property hasOp= this.ontLoader.getModel().getProperty( this.NS + "hasUniaryOperator");
				
				ind.addLabel( key, "EN" );
				ind.addLiteral( hasID, new String(key) );
				ind.addProperty( hasVar, variable );
				
				if( op!= null ){
					ind.addProperty( hasOp, op);
				}
				
			} else {
				
				log.info( "Variable Expression individual already exist ");
				ind = this.ontLoader.getModel().getIndividual(this.NS + this.varExpClassName + "." +  key);
			
			}
		}
		
		return ind;
		
	}

	/**
	 * @see net.swas.explorer.oh.handler.OntologyHandler#remove(java.lang.String)
	 */
	@Override
	public void remove(String ID) {
		
		//log.info( "VariableExpression's removeIndividual called ..  ");
		if ( this.ontLoader.getModel().getIndividual( this.NS + this.varExpClassName + "." + ID )  != null) {
			
			Individual ind = this.ontLoader.getModel().getIndividual( this.NS + this.varExpClassName + "." + ID );
			this.ontClass.dropIndividual( ind );
			ind.remove();
			
		} else {
			
		log.info( "VariableExpression individual does not exist" );
		
		}
	
	}

	/**
	 * @see net.swas.explorer.oh.handler.OntologyHandler#get(java.lang.String)
	 */
	@Override
	public Entity get(String ID) {
		
		//log.info( "VariableExpression's getIndividual called ..  ");
		VariableExpression varExp = null;
		Individual ind = this.ontLoader.getModel().getIndividual( this.NS + this.varExpClassName + "." + ID );
		if ( ind  != null) {
			
			try {
				
				Property hasID= this.ontLoader.getModel().getProperty( this.NS + "hasVarExpID");
				Property hasVar= this.ontLoader.getModel().getProperty( this.NS + "hasSelectedVariable");
				Property hasOp= this.ontLoader.getModel().getProperty( this.NS + "hasUnaryOperator");
				
				OntResource variable =(OntResource) ind.getPropertyValue(hasVar);
				
				varExp = (VariableExpression) EntityFactory.VAR_EXPRESSION.getObject();
				varExp.setID( ind.getProperty(hasID).getString() );
				varExp.setVariable( variable.getLocalName() );
				
				if( ind.getPropertyValue(hasOp) != null ){
					OntResource operator = (OntResource) ind.getPropertyValue(hasOp);
					varExp.setOperator( operator.getLocalName() );
				} else{
					varExp.setOperator( "" );
				}
				
			} catch (InstantiationException | IllegalAccessException e) {
				
				log.info( "Could not initiate VariableExpression object ");
				e.printStackTrace();
				
			}
		}	
		return varExp;
		
	}

	/**
	 * @see net.swas.explorer.oh.handler.OntologyHandler#update(net.swas.explorer.ecf.Entity)
	 */
	@Override
	public Individual update(Entity entity) {
		
		//log.info( "VariableExpression's updateIndividual called ..  ");
		Individual ind = null;
		if ( entity instanceof VariableExpression){
			
			VariableExpression varExp = (VariableExpression) entity;
			ind = this.ontLoader.getModel().getIndividual( this.NS + this.varExpClassName + "." + varExp.getID() );
			if ( ind  != null) {
				
				Individual op = null; 
				if( !varExp.getOperator().equals("") ){
					
					if( varExp.getOperator().equals(ElementMap.ampersand.toString()) ){
						op = this.ontLoader.getModel().getIndividual( this.NS + ElementMap.ampersand.toString());
					} else{
						op = this.ontLoader.getModel().getIndividual( this.NS + ElementMap.negation.toString());
					}
					
				}
				
				Individual variable = this.ontLoader.getModel().getIndividual( this.NS + varExp.getVariable());
				
				Property hasID= this.ontLoader.getModel().getProperty( this.NS + "hasVarExpID");
				Property hasVar= this.ontLoader.getModel().getProperty( this.NS + "hasSelectedVariable");
				Property hasOp= this.ontLoader.getModel().getProperty( this.NS + "hasUnaryOperator");
				
				ind.setLabel( this.varExpClassName + "." + varExp.getID(), "EN" );
				ind.setPropertyValue( hasID, this.ontLoader.getModel().createTypedLiteral( new String(""+varExp.getID()) ));
				ind.setPropertyValue( hasVar, variable);
				
				if( op!= null ){
					ind.setPropertyValue( hasOp, op);
				}
					
			} else {
				
				log.info("VariableExpression Individual does not exist");
			}
		}
		
		return ind;
	}

	/**
	 * @see net.swas.explorer.oh.handler.OntologyHandler#getAll()
	 */
	@Override
	public List<Entity> getAll() {
		
		//log.info( "VariableExpression's getAllIndividual called ..  ");
		List<Entity> varExpList = new ArrayList<Entity>();
		
		@SuppressWarnings("unchecked")
		ExtendedIterator< Individual > indList= (ExtendedIterator<Individual>) this.ontClass.listInstances();
		
		while ( indList.hasNext() ) {

			try {
			
				Property hasID= this.ontLoader.getModel().getProperty( this.NS + "hasVarExpID");
				Property hasVar= this.ontLoader.getModel().getProperty( this.NS + "hasSelectedVariable");
				Property hasOp= this.ontLoader.getModel().getProperty( this.NS + "hasUnaryOperator");
				
				Individual ind = indList.next();
				OntResource variable =(OntResource) ind.getPropertyValue(hasVar);
				
				log.info( "ID " + ind.getLocalName() );
				VariableExpression varExp = (VariableExpression) EntityFactory.VAR_EXPRESSION.getObject();
				varExp.setID( ind.getProperty(hasID).getString() );
				varExp.setVariable( variable.getLocalName() );
				
				if( ind.getPropertyValue(hasOp) != null ){
					
					OntResource operator = (OntResource) ind.getPropertyValue(hasOp);
					varExp.setOperator( operator.getLocalName() );
				
				} else{
					
					varExp.setOperator( "" );
				
				}
			
				varExpList.add( varExp );
				
			} catch (InstantiationException | IllegalAccessException e) {
				log.info( "Could not initiate VariableExpression object ");
				e.printStackTrace();
			}
		}
		
		return varExpList;
		
	}
	
	/**
	 * This function retrieves the class name
	 * @return className
	 */
	public String getClassName(){
		return this.varExpClassName;
	}
	
	/**
	 * This function checks if variable expression individual already exist in ontology
	 * @param name
	 * @return boolean
	 */
	private boolean isExists(String name) {
		
		Individual ind = null;
		boolean check = false;
		ind = this.ontLoader.getModel().getIndividual(this.NS + this.varExpClassName + "." + name);
		if (ind == null)
			check = true;
		else
			check = false;
		return check;
		
	}
	
	/**
	 * This function is for generating key for variable expression individuals
	 * @param varExp
	 * @return key
	 */
	private String getKey(VariableExpression varExp){

		String key = "";
		if(!varExp.getOperator().equals("")){
			
			if( varExp.getOperator().equals(ElementMap.ampersand.toString()) ){
				key += ElementMap.ampersand.toString() + ".";
			} else{
				key += ElementMap.negation.toString() + ".";
			}
			
		}
		key += varExp.getVariable();
		return key;
		
	}
	
}
