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

import net.swas.explorer.ec.MetaData;
import net.swas.explorer.ec.Sequence;
import net.swas.explorer.ecf.Entity;
import net.swas.explorer.ecf.EntityFactory;
import net.swas.explorer.oh.lo.OntologyLoader;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

/**
 * This class is responsible for the management of Meta Data of Rule and Chain Rule. It uses {@link OntologyLoader} to load 
 * configurations of knowledge base.
 * 
 */
public class MetaDataHandler extends OntologyHandler {
	
	
	private final static Logger log=LoggerFactory.getLogger(MetaDataHandler.class);
	private final String mdClassName = "RuleMetaData"; 
	private SequenceHandler seqHndlr = null; // Sequence handler
	
	/**
	 * Constructor
	 * @param loader
	 */
	public MetaDataHandler( OntologyLoader loader ) {
		
		this.ontLoader = loader;
		this.NS = this.ontLoader.getConfiguration().getRuleEngineNameSpace() + NS_POSTFIX;
		
		//log.info("Meta Data Name Space : " + this.NS + this.mdClassName);
		this.ontClass = this.ontLoader.getModel().createClass(this.NS + this.mdClassName);
		this.seqHndlr = new SequenceHandler( this.ontLoader );
	}

	/**
	 * @see net.swas.explorer.oh.handler.OntologyHandler#add(net.swas.explorer.ecf.Entity)
	 */
	@Override
	public Individual add(Entity entity) {
		
		//log.info( "Meta Data's addIndividual called ..  ");
		Individual ind = null;
		if ( entity instanceof MetaData){
			
			MetaData md = (MetaData) entity;
			int ID = Sequence.getID( seqHndlr, this.mdClassName );
			//log.info("Seq ID assigned:" + ID);
			if ( this.ontLoader.getModel().getIndividual( this.NS + this.mdClassName + "." + ID ) == null) {

				ind = this.ontClass.createIndividual( this.NS + this.mdClassName + "." + ID);
				Individual severity = this.ontLoader.getModel().getIndividual(this.NS + "Severity." + md.getSeverity());
				
				Property hasID= this.ontLoader.getModel().getProperty( this.NS + "hasMetaDataID");
				Property hasMessage= this.ontLoader.getModel().getProperty( this.NS + "hasMessage");
				Property hasRevision= this.ontLoader.getModel().getProperty( this.NS + "hasRevisionNumber");
				Property hasRuleID= this.ontLoader.getModel().getProperty( this.NS + "hasRuleID");
				Property hasSeverity= this.ontLoader.getModel().getProperty( this.NS + "hasSeverity");
				Property hasTag= this.ontLoader.getModel().getProperty( this.NS + "hasTag");
				
				ind.addLabel( this.mdClassName + "." + ID, "EN" );
				ind.addLiteral( hasID, new String(""+ID) );
				ind.addLiteral( hasMessage, md.getMessage());
				ind.addLiteral( hasRevision, md.getRevision());
				
				if(!md.getRuleID().equals("") && md.getRuleID() != null){
					ind.addLiteral( hasRuleID, md.getRuleID());
				} else{
					ind.addLiteral( hasRuleID, "" + ID);
				}
				
				if(severity != null){
					ind.addProperty( hasSeverity, severity);
				}
				if (md.getTag() != null)
				{
					for (String tag : md.getTag()) {
						
						ind.addLiteral( hasTag, tag);
					}
				}
				
				
			} else {
				
				log.info( "Meta data individual already exist ");
			
			}
		}
		
		return ind;
	}

	/**
	 * @see net.swas.explorer.oh.handler.OntologyHandler#remove(java.lang.String)
	 */
	@Override
	public void remove(String ID) {
		
		log.info( "MetaData's removeIndividual called ..  ");
		if ( this.ontLoader.getModel().getIndividual( this.NS + this.mdClassName + "." + ID )  != null) {
			
			Individual ind = this.ontLoader.getModel().getIndividual( this.NS + this.mdClassName + "." + ID );
			this.ontClass.dropIndividual( ind );
			ind.remove();
			
		} else {
			
			log.info( "MetaData individual does not exist" );
			
		}
		
	}

	/**
	 * @see net.swas.explorer.oh.handler.OntologyHandler#get(java.lang.String)
	 */
	@Override
	public Entity get(String ID) {
		
		//log.info( "MetaData's getIndividual called ..  ");
		MetaData md = null;
		Individual ind = this.ontLoader.getModel().getIndividual( this.NS + this.mdClassName + "." + ID );
		if ( ind  != null) {
			
			try {
				
				Property hasID= this.ontLoader.getModel().getProperty( this.NS + "hasMetaDataID");
				Property hasMessage= this.ontLoader.getModel().getProperty( this.NS + "hasMessage");
				Property hasRevision= this.ontLoader.getModel().getProperty( this.NS + "hasRevisionNumber");
				Property hasRuleID= this.ontLoader.getModel().getProperty( this.NS + "hasRuleID");
				Property hasSeverity= this.ontLoader.getModel().getProperty( this.NS + "hasSeverity");
				Property hasTag= this.ontLoader.getModel().getProperty( this.NS + "hasTag");
				
				OntResource severity = (OntResource) ind.getPropertyValue(hasSeverity);
				
				md = (MetaData) EntityFactory.METADATA.getObject();
				md.setID( ind.getProperty(hasID).getString() );
				md.setMessage( ind.getProperty(hasMessage).getString() );
				md.setRevision( ind.getProperty(hasRevision).getString() );
				md.setRuleID( ind.getProperty(hasRuleID).getString() );
				
				if(severity != null){
					md.setSeverity( severity.getLocalName().split("\\.")[1] );
				}
				
				NodeIterator tagList = ind.listPropertyValues(hasTag);
				List<String> tags = new ArrayList<String>();
				
				while (tagList.hasNext()) {

					Literal tagInd = (Literal) tagList.next();
					tags.add(tagInd.getString());

				}	
				md.setTag(tags);
				
			} catch (InstantiationException | IllegalAccessException e) {
				
				log.info( "Could not initiate MetaData object ");
				e.printStackTrace();
				
			}
		}	
		return md;
		
	}

	/**
	 * @see net.swas.explorer.oh.handler.OntologyHandler#update(net.swas.explorer.ecf.Entity)
	 */
	@Override
	public Individual update(Entity entity) {

		//log.info( "MetaData's updateIndividual called ..  ");
		Individual ind = null;
		if ( entity instanceof MetaData){
			
			MetaData md = (MetaData) entity;
			ind = this.ontLoader.getModel().getIndividual( this.NS + this.mdClassName + "." + md.getID() );
			if ( ind  != null) {
				
				Individual severity = this.ontLoader.getModel().getIndividual(this.NS + "Severity." + md.getSeverity());
				
				Property hasID= this.ontLoader.getModel().getProperty( this.NS + "hasMetaDataID");
				Property hasMessage= this.ontLoader.getModel().getProperty( this.NS + "hasMessage");
				Property hasRevision= this.ontLoader.getModel().getProperty( this.NS + "hasRevisionNumber");
				Property hasRuleID= this.ontLoader.getModel().getProperty( this.NS + "hasRuleID");
				Property hasSeverity= this.ontLoader.getModel().getProperty( this.NS + "hasSeverity");
				Property hasTag= this.ontLoader.getModel().getProperty( this.NS + "hasTag");
				
				ind.setLabel( this.mdClassName + "." + md.getID(), "EN" );
				ind.setPropertyValue( hasID, this.ontLoader.getModel().createTypedLiteral(md.getID()) );
				ind.setPropertyValue( hasMessage, this.ontLoader.getModel().createTypedLiteral(md.getMessage()) );
				ind.setPropertyValue( hasRevision, this.ontLoader.getModel().createTypedLiteral(md.getRevision()) );
				ind.setPropertyValue( hasRuleID, this.ontLoader.getModel().createTypedLiteral(md.getRuleID()) );
				ind.setPropertyValue( hasSeverity, severity );
				
				ind.removeAll( hasTag );
				for (String tag : md.getTag()) {
					
					ind.addLiteral( hasTag, tag);
				
				}
					
			} else {
				
				log.info("MetaData Individual does not exist");
			}
		}
		
		return ind;
		
	}

	/**
	 * @see net.swas.explorer.oh.handler.OntologyHandler#getAll()
	 */
	@Override
	public List<Entity> getAll() {
		
		//log.info( "MetaData's getAllIndividual called ..  ");
		List<Entity> mdList = new ArrayList<Entity>();
		
		@SuppressWarnings("unchecked")
		ExtendedIterator< Individual > indList= (ExtendedIterator<Individual>) this.ontClass.listInstances();
		
		while ( indList.hasNext() ) {

			try {
			
				Property hasID= this.ontLoader.getModel().getProperty( this.NS + "hasMetaDataID");
				Property hasMessage= this.ontLoader.getModel().getProperty( this.NS + "hasMessage");
				Property hasRevision= this.ontLoader.getModel().getProperty( this.NS + "hasRevisionNumber");
				Property hasRuleID= this.ontLoader.getModel().getProperty( this.NS + "hasRuleID");
				Property hasSeverity= this.ontLoader.getModel().getProperty( this.NS + "hasSeverity");
				Property hasTag= this.ontLoader.getModel().getProperty( this.NS + "hasTag");
				
				Individual ind = indList.next();
				OntResource severity = (OntResource) ind.getPropertyValue(hasSeverity);
				
				log.info( "ID " + ind.getLocalName() );
				MetaData md = (MetaData) EntityFactory.METADATA.getObject();
				md.setID( ind.getProperty(hasID).getString() );
				md.setMessage( ind.getProperty(hasMessage).getString() );
				md.setRevision( ind.getProperty(hasRevision).getString() );
				md.setRuleID( ind.getProperty(hasRuleID).getString() );
				
				if(severity != null){
					md.setSeverity( severity.getLocalName().split("\\.")[1] );
				}
				
				NodeIterator tagList = ind.listPropertyValues(hasTag);
				List<String> tags = new ArrayList<String>();
				
				while (tagList.hasNext()) {

					Literal tagInd = (Literal) tagList.next();
					tags.add(tagInd.getString());

				}	
				md.setTag(tags);
			
				mdList.add( md );
			} catch (InstantiationException | IllegalAccessException e) {
				log.info( "Could not initiate MetaData object ");
				e.printStackTrace();
			}
		}
		
		return mdList;
		
	}

}
