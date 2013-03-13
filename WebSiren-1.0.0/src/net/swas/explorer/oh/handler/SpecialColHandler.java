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

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.swas.explorer.ec.Sequence;
import net.swas.explorer.ec.SpecialCollection;
import net.swas.explorer.ecf.Entity;
import net.swas.explorer.ecf.EntityFactory;
import net.swas.explorer.oh.lo.OntologyLoader;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.rdf.model.Property;

/**
 * This class is responsible for the management of special collections. It uses {@link OntologyLoader} to load 
 * configurations of knowledge base.
 * 
 */
public class SpecialColHandler extends OntologyHandler{
	

	private final static Logger log=LoggerFactory.getLogger(VarExpressionHandler.class);
	private String spColClassName = ""; 
	private SequenceHandler seqHndlr = null; // Sequence handler
	
	/**
	 * Constructor
	 * @param loader
	 */
	public SpecialColHandler(OntologyLoader loader) {
		
		this.ontLoader = loader;
		this.NS = this.ontLoader.getConfiguration().getRuleEngineNameSpace() + NS_POSTFIX;
		this.seqHndlr = new SequenceHandler( this.ontLoader );
		
	}
	
	/**
	 * @see net.swas.explorer.oh.handler.OntologyHandler#add(net.swas.explorer.ecf.Entity)
	 */
	@Override
	public Individual add(Entity entity) {
		
		//log.info( "Special Collection's addIndividual called ..  ");
		Individual ind = null;
		if ( entity instanceof SpecialCollection){
			
			SpecialCollection spCol = (SpecialCollection) entity;
			this.ontClass = this.getIntializedClass(spCol.getClassName());
			
			int ID = Sequence.getID( seqHndlr, this.spColClassName );
			if ( this.ontLoader.getModel().getIndividual( this.NS + this.spColClassName + "." + ID ) == null) {

				ind = this.ontClass.createIndividual( this.NS + this.spColClassName + "." + ID);

				Property initializedWith= this.ontLoader.getModel().getProperty( this.NS + "initializedWith");
				
				ind.addLabel( this.spColClassName + "." + ID, "EN" );
				ind.addLiteral( initializedWith , spCol.getName() );
			
			} else {
				
				log.info( "Variable Expression individual already exist ");
			
			}
		}
		
		return ind;
		
	}

	/**
	 * @see net.swas.explorer.oh.handler.OntologyHandler#remove(java.lang.String)
	 * @param ID in format  ClassName.ID i.e IP.1 
	 */
	@Override
	public void remove(String ID ) {

		//log.info( "VariableExpression's removeIndividual called ..  ");
		if ( this.ontLoader.getModel().getIndividual( this.NS + ID )  != null) {
			
			Individual ind = this.ontLoader.getModel().getIndividual( this.NS + ID );
			this.ontClass.dropIndividual( ind );
			ind.remove();
			
		} else {
			
		log.info( "Collection individual does not exist" );
		
		}
		
	}

	/**
	 * @see net.swas.explorer.oh.handler.OntologyHandler#get(java.lang.String)
	 */
	@Override
	public Entity get(String ID) {
		
		//log.info( "VariableExpression's getIndividual called ..  ");
		SpecialCollection spCol = null;
		Individual ind = this.ontLoader.getModel().getIndividual( this.NS + ID );
		if ( ind  != null) {
			
			try {
				
				Property initializedWith= this.ontLoader.getModel().getProperty( this.NS + "initializedWith");
				
				spCol = (SpecialCollection) EntityFactory.SPECIAL_COL.getObject();
				spCol.setID( ID.split("\\.")[1] );
				spCol.setClassName( ID.split("\\.")[0] );
				spCol.setName( ind.getProperty(initializedWith).getString() );
				
			} catch (InstantiationException | IllegalAccessException e) {
				
				log.info( "Could not initiate Special Collection object ");
				e.printStackTrace();
				
			}
		}	
		return spCol;
		
	}

	/**
	 * @see net.swas.explorer.oh.handler.OntologyHandler#update(net.swas.explorer.ecf.Entity)
	 */
	@Override
	public Individual update(Entity entity) {
		
		//log.info( "VariableExpression's updateIndividual called ..  ");
		Individual ind = null;
		if ( entity instanceof SpecialCollection){
			
			SpecialCollection spCol = (SpecialCollection) entity;
			ind = this.ontLoader.getModel().getIndividual( this.NS + spCol.getClassName() + "." + spCol.getID() );
			if ( ind  != null) {
				
				Property initializedWith= this.ontLoader.getModel().getProperty( this.NS + "initializedWith");
				
				ind.setLabel( spCol.getClassName() + "." + spCol.getID(), "EN" );
				ind.setPropertyValue(initializedWith, this.ontLoader.getModel().createTypedLiteral(spCol.getName()));
					
			} else {
				
				log.info("SpecialCollection Individual does not exist");
			}
		}
		
		return ind;
		
	}

	/**
	 * @see net.swas.explorer.oh.handler.OntologyHandler#getAll()
	 */
	@Override
	public List<Entity> getAll() {
		
		//TODO:
		return null;
	}
	
	/**
	 * This function is to retrieve the initialized class
	 * @param spClassName
	 * @return ontClass
	 */
	private OntClass getIntializedClass(String spClassName){
		
		//log.info("Variable Expression Name Space : " + this.NS + this.spColClassName);
		this.spColClassName = spClassName; 
		return this.ontLoader.getModel().createClass(this.NS + this.spColClassName);
	
	}
	

}
