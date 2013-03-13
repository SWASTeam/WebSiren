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
import net.swas.explorer.ec.Element;
import net.swas.explorer.ecf.Entity;
import net.swas.explorer.ecf.EntityFactory;
import net.swas.explorer.oh.fields.ElementMap;
import net.swas.explorer.oh.lo.OntologyLoader;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

/**
 * This class is responsible for the management of collection expressions. It uses {@link OntologyLoader} to load 
 * configurations of knowledge base.
 * 
 */
public class ColExpressionHandler extends OntologyHandler {

	private final static Logger log=LoggerFactory.getLogger(VarExpressionHandler.class);
	private final String colExpClassName = "CollectionExpression"; 
	private ElementHandler elmntHandler = null;
	
	/**
	 * Constructor
	 * @param loader
	 */
	public ColExpressionHandler(OntologyLoader loader) {
		
		this.ontLoader = loader;
		this.NS = this.ontLoader.getConfiguration().getRuleEngineNameSpace() + NS_POSTFIX;
		
		//log.info("Collection Expression Name Space : " + this.NS + this.colExpClassName);
		
		this.ontClass = this.ontLoader.getModel().createClass(this.NS + this.colExpClassName);
		this.elmntHandler = new ElementHandler(this.ontLoader);
		
	}
	/** (non-Javadoc)
	 * @see net.swas.explorer.oh.handler.OntologyHandler#add(net.swas.explorer.ecf.Entity)
	 */
	@Override
	public Individual add(Entity entity) {
		
		//log.info( "Collection Expression's addIndividual called ..  ");
		Individual ind = null;
		if ( entity instanceof CollectionExpression){
			
			CollectionExpression colExp = (CollectionExpression) entity;
			String key = this.getKey(colExp);
			boolean check = this.isExists(key);
			if ( !check ) {

				ind = this.ontClass.createIndividual( this.NS + this.colExpClassName + "." +  key);
				
				Individual op = null; 
				if( !colExp.getOperator().equals("") ){
					
					if( colExp.getOperator().equals(ElementMap.ampersand.toString()) ){
						op = this.ontLoader.getModel().getIndividual( this.NS + ElementMap.ampersand.toString());
					} else{
						op = this.ontLoader.getModel().getIndividual( this.NS + ElementMap.negation.toString());
					}
					
				}
				
				Individual collection = this.ontLoader.getModel().getIndividual( this.NS + colExp.getCollection());
				Individual element = null;
				
				if( !colExp.getElement().equals("") ){
					
					if(elmntHandler.isExists(colExp.getElement())){
						element = this.ontLoader.getModel().getIndividual( this.NS + 
								elmntHandler.getClassName()  + "." + colExp.getElement());
					} else{
						try{
							
							Element elmnt = (Element) EntityFactory.ELEMENT.getObject();
							elmnt.setName(colExp.getElement());
							elmnt.setCollection(colExp.getCollection());
							element = elmntHandler.add(elmnt);
						
						} catch(Exception e){
							e.printStackTrace();
						}
					}
				}
				
				Property hasID= this.ontLoader.getModel().getProperty( this.NS + "hasColExpID");
				Property hasCol= this.ontLoader.getModel().getProperty( this.NS + "hasSelectedCollection");
				Property hasElmnt= this.ontLoader.getModel().getProperty( this.NS + "hasSelectedElement");
				Property hasOp= this.ontLoader.getModel().getProperty( this.NS + "hasUnaryOperator");
				
				ind.addLabel( key, "EN" );
				ind.addLiteral( hasID, new String(""+key) );
				ind.addProperty( hasCol, collection);
				
				if( element!= null ){
					ind.addProperty( hasElmnt, element);
				}
			
				if( op!= null ){
					ind.addProperty( hasOp, op);
				}
				
			} else {
				
				log.info( "Collection Expression individual already exist ");
				ind = this.ontLoader.getModel().getIndividual(this.NS + this.colExpClassName + "." + key);
			
			}
		}
		
		return ind;
		
	}

	/** (non-Javadoc)
	 * @see net.swas.explorer.oh.handler.OntologyHandler#remove(java.lang.String)
	 */
	@Override
	public void remove(String ID) {
		
		//log.info( "CollectionExpression's removeIndividual called ..  ");
		if ( this.ontLoader.getModel().getIndividual( this.NS + this.colExpClassName + "." + ID )  != null) {
			
			Individual ind = this.ontLoader.getModel().getIndividual( this.NS + this.colExpClassName + "." + ID );
			this.ontClass.dropIndividual( ind );
			ind.remove();
			
		} else {
			
			log.info( "CollectionExpression individual does not exist" );
			
		}
	
	}

	/** (non-Javadoc)
	 * @see net.swas.explorer.oh.handler.OntologyHandler#get(java.lang.String)
	 */
	@Override
	public Entity get(String ID) {
		
		//log.info( "CollectionExpression's getIndividual called ..  ");
		CollectionExpression colExp = null;
		Individual ind = this.ontLoader.getModel().getIndividual( this.NS + this.colExpClassName + "." + ID );
		if ( ind  != null) {
			
			try {
				
				Property hasID= this.ontLoader.getModel().getProperty( this.NS + "hasColExpID");
				Property hasCol= this.ontLoader.getModel().getProperty( this.NS + "hasSelectedCollection");
				Property hasElmnt= this.ontLoader.getModel().getProperty( this.NS + "hasSelectedElement");
				Property hasOp= this.ontLoader.getModel().getProperty( this.NS + "hasUnaryOperator");
				
				OntResource collection =(OntResource) ind.getPropertyValue(hasCol);
				
				colExp = (CollectionExpression) EntityFactory.COL_EXPRESSION.getObject();
				colExp.setID( ind.getProperty(hasID).getString() );
				colExp.setCollection( collection.getLocalName() );
				
				
				if( ind.getPropertyValue(hasElmnt) != null ){
					OntResource element = (OntResource) ind.getPropertyValue(hasElmnt);
					colExp.setElement( element.getLocalName() );
				} else{
					colExp.setElement( "" );
				}
				
				if( ind.getPropertyValue(hasOp) != null ){
					OntResource operator = (OntResource) ind.getPropertyValue(hasOp);
					colExp.setOperator( operator.getLocalName() );
				} else{
					colExp.setOperator( "" );
				}
				
			} catch (InstantiationException | IllegalAccessException e) {
				
				log.info( "Could not initiate CollectionExpression object ");
				e.printStackTrace();
				
			}
		}	
		return colExp;
		
	}

	/** (non-Javadoc)
	 * @see net.swas.explorer.oh.handler.OntologyHandler#update(net.swas.explorer.ecf.Entity)
	 */
	@Override
	public Individual update(Entity entity) {
		
		//log.info( "CollectionExpression's updateIndividual called ..  ");
		Individual ind = null;
		if ( entity instanceof CollectionExpression){
			
			CollectionExpression colExp = (CollectionExpression) entity;
			ind = this.ontLoader.getModel().getIndividual( this.NS + this.colExpClassName + "." + colExp.getID() );
			if ( ind  != null) {
				
				Individual op = null; 
				if( !colExp.getOperator().equals("") ){
					
					if( colExp.getOperator().equals(ElementMap.ampersand.toString()) ){
						op = this.ontLoader.getModel().getIndividual( this.NS + ElementMap.ampersand.toString());
					} else{
						op = this.ontLoader.getModel().getIndividual( this.NS + ElementMap.negation.toString());
					}
					
				}
				
				Individual collection = this.ontLoader.getModel().getIndividual( this.NS + colExp.getCollection());
				Individual element = null;
				
				if( !colExp.getElement().equals("") ){
					
					if(elmntHandler.isExists(colExp.getElement())){
						element = this.ontLoader.getModel().getIndividual( this.NS  +
								elmntHandler.getClassName() + "." + colExp.getElement());
					} else{
						try{
							
							Element elmnt = (Element) EntityFactory.ELEMENT.getObject();
							elmnt.setName(colExp.getElement());
							elmnt.setCollection(colExp.getCollection());
							element = elmntHandler.add(elmnt);
						
						} catch(Exception e){
							e.printStackTrace();
						}
					}
					
				}
				
				Property hasID= this.ontLoader.getModel().getProperty( this.NS + "hasColExpID");
				Property hasCol= this.ontLoader.getModel().getProperty( this.NS + "hasSelectedCollection");
				Property hasElmnt= this.ontLoader.getModel().getProperty( this.NS + "hasSelectedElement");
				Property hasOp= this.ontLoader.getModel().getProperty( this.NS + "hasUnaryOperator");
				
				ind.setLabel( this.colExpClassName + "." + colExp.getID(), "EN" );
				ind.setPropertyValue( hasID, this.ontLoader.getModel().createTypedLiteral(colExp.getID()) );
				ind.setPropertyValue( hasCol, collection );
				ind.setPropertyValue( hasElmnt, element );
				
				if( element!= null ){
					ind.addProperty( hasElmnt, element);
				}
				
				if( op!= null ){
					ind.addProperty( hasOp, op);
				}
					
			} else {
				
				log.info("CollectionExpression Individual does not exist");
			}
		}
		
		return ind;
	}

	/** (non-Javadoc)
	 * @see net.swas.explorer.oh.handler.OntologyHandler#getAll()
	 */
	@Override
	public List<Entity> getAll() {
		
		//log.info( "CollectionExpression's getAllIndividual called ..  ");
		List<Entity> colExpList = new ArrayList<Entity>();
		
		@SuppressWarnings("unchecked")
		ExtendedIterator< Individual > indList= (ExtendedIterator<Individual>) this.ontClass.listInstances();
		
		while ( indList.hasNext() ) {

			try {
			
				Property hasID= this.ontLoader.getModel().getProperty( this.NS + "hasColExpID");
				Property hasCol= this.ontLoader.getModel().getProperty( this.NS + "hasSelectedCollection");
				Property hasElmnt= this.ontLoader.getModel().getProperty( this.NS + "hasSelectedElement");
				Property hasOp= this.ontLoader.getModel().getProperty( this.NS + "hasUnaryOperator");
				
			
				Individual ind = indList.next();
				log.info( "ID " + ind.getLocalName() );
				
				OntResource collection =(OntResource) ind.getPropertyValue(hasCol);
				
				CollectionExpression colExp = (CollectionExpression) EntityFactory.COL_EXPRESSION.getObject();
				colExp.setID( ind.getProperty(hasID).getString() );
				
				colExp.setCollection( collection.getLocalName() );
				
				if( ind.getPropertyValue(hasElmnt) != null ){
					OntResource element = (OntResource) ind.getPropertyValue(hasElmnt);
					colExp.setElement( element.getLocalName() );
				} else{
					colExp.setElement( "" );
				}
				
				if( ind.getPropertyValue(hasOp) != null ){
					OntResource operator = (OntResource) ind.getPropertyValue(hasOp);
					colExp.setOperator( operator.getLocalName() );
				}else{
					colExp.setOperator( "" );
				}
			
				colExpList.add( colExp );
				
			} catch (InstantiationException | IllegalAccessException e) {
				log.info( "Could not initiate CollectionExpression object ");
				e.printStackTrace();
			}
		}
		
		return colExpList;
		
	}
	
	/**
	 * Retrieve class name
	 * @return className
	 */
	public String getClassName(){
		return this.colExpClassName;
	}
	
	/**
	 * Checks if collection expression individual 
	 * already exists in ontology
	 * @param name
	 * @return
	 */
	private boolean isExists(String name) {
		
		Individual colExpInd = null;
		boolean check = false;
		colExpInd =this.ontLoader.getModel().getIndividual(this.NS +  this.colExpClassName + "." + name);
		if (colExpInd == null)
			check = false;
		else
			check = true;
		return check;
		
	}
	
	/**
	 * This function is for generating key for collection expression individuals
	 * @param colExp
	 * @return key
	 */
	private String getKey(CollectionExpression colExp){

		log.info(">>>>>>>>>>>> befor col : " + colExp.getCollection() +"|" + colExp.getElement());
		String key = "";
		if(!colExp.getOperator().equals("")){
			
			if( colExp.getOperator().equals(ElementMap.ampersand.toString()) ){
				key += ElementMap.ampersand.toString() + ".";
			} else{
				key += ElementMap.negation.toString() + ".";
			}
			
			
		}
		
		key += colExp.getCollection();
		if(!colExp.getElement().equals("") && colExp.getElement() != null){
			key += "." + colExp.getElement();
		}
		
		log.info(">>>>>>>>>>>>>>> key for Col Exp:" + key);
		return key;
		
	}
}
