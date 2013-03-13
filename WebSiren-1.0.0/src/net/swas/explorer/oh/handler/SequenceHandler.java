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

import net.swas.explorer.ec.Sequence;
import net.swas.explorer.ecf.Entity;
import net.swas.explorer.ecf.EntityFactory;
import net.swas.explorer.oh.lo.OntologyLoader;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

/**
 * This class is responsible for the generating sequence ID's for the individuals of different concepts in ontology.
 * It uses {@link OntologyLoader} to load configurations of knowledge base.
 * 
 */
public class SequenceHandler extends OntologyHandler {
	
	
	private final static Logger log = LoggerFactory
			.getLogger(SequenceHandler.class);
	private final String seqClassName = "Sequence";

	/**
	 * Contructor
	 * @param loader
	 */
	public SequenceHandler(OntologyLoader loader) {

		this.ontLoader = loader;
		this.NS = this.ontLoader.getConfiguration().getRuleEngineNameSpace()
				+ NS_POSTFIX;
		
		//log.info("Sequence Name Space : " + this.NS + this.seqClassName);

		this.ontClass = this.ontLoader.getModel().createClass(
				this.NS + this.seqClassName);

	}

	
	/**
	 * @see net.swas.explorer.oh.handler.OntologyHandler#add(net.swas.explorer.ecf.Entity)
	 */
	@Override
	public Individual add(Entity entity) {

		//log.info("Sequence's addIndividual called ..  ");
		Individual ind = null;
		if (entity instanceof Sequence) {
			Sequence seq = (Sequence) entity;

			if (this.ontLoader.getModel().getIndividual(
					this.NS + this.seqClassName + "." + seq.getID()) == null) {

				ind = this.ontClass.createIndividual(this.NS
						+ this.seqClassName + "." + seq.getID());

				Property hasConcept = this.ontLoader.getModel().getProperty(
						this.NS + "hasConcept");
				Property hasFirst = this.ontLoader.getModel().getProperty(
						this.NS + "hasFirst");
				Property hasLast = this.ontLoader.getModel().getProperty(
						this.NS + "hasLast");
				Property hasMaximum = this.ontLoader.getModel().getProperty(
						this.NS + "hasMaximum");

				ind.addLiteral(hasConcept, seq.getID());
				ind.addLiteral(hasFirst, new Integer(seq.getFirst()));
				ind.addLiteral(hasLast, new Integer(seq.getLast()));
				ind.addLiteral(hasMaximum, new Integer(seq.getMax()));

			}
		}

		return ind;
	}

	/**
	 * @see net.swas.explorer.oh.handler.OntologyHandler#remove(java.lang.String)
	 */
	@Override
	public void remove(String ID) {

		//log.info("Sequence's removeIndividual called ..  ");
		if (this.ontLoader.getModel().getIndividual(
				this.NS + this.seqClassName + "." + ID) != null) {

			Individual ind = this.ontLoader.getModel().getIndividual(
					this.NS + this.seqClassName + "." + ID);
			this.ontClass.dropIndividual(ind);
			ind.remove();

		} else {

			log.info("Sequence individual does not exist");

		}

	}

	/**
	 * @see net.swas.explorer.oh.handler.OntologyHandler#get(java.lang.String)
	 */
	@Override
	public Entity get(String ID) {

		//log.info("Sequence's getIndividual called ..  ");
		Sequence seq = null;
		Individual ind = this.ontLoader.getModel().getIndividual(
				this.NS + this.seqClassName + "." + ID);
		if (ind != null) {

			try {

				Property hasConcept = this.ontLoader.getModel().getProperty(
						this.NS + "hasConcept");
				Property hasFirst = this.ontLoader.getModel().getProperty(
						this.NS + "hasFirst");
				Property hasLast = this.ontLoader.getModel().getProperty(
						this.NS + "hasLast");
				Property hasMaximum = this.ontLoader.getModel().getProperty(
						this.NS + "hasMaximum");

				seq = (Sequence) EntityFactory.SEQUENCE.getObject();
				seq.setID(ind.getProperty(hasConcept).getString());
				seq.setFirst(ind.getProperty(hasFirst).getInt());
				seq.setLast(ind.getProperty(hasLast).getInt());
				seq.setMax(ind.getProperty(hasMaximum).getInt());

			} catch (InstantiationException | IllegalAccessException e) {

				log.info("Could not initiate Sequence object ");
				e.printStackTrace();

			}
		}
		return seq;
	}

	/**
	 * @see net.swas.explorer.oh.handler.OntologyHandler#update(net.swas.explorer.ecf.Entity)
	 */
	@Override
	public Individual update(Entity entity) {

		//log.info("Sequence's updateIndividual called ..  ");
		Individual ind = null;
		if (entity instanceof Sequence) {

			Sequence seq = (Sequence) entity;
			ind = this.ontLoader.getModel().getIndividual(
					this.NS + this.seqClassName + "." + seq.getID());
			if (ind != null) {

				Property hasConcept = this.ontLoader.getModel().getProperty(
						this.NS + "hasConcept");
				Property hasFirst = this.ontLoader.getModel().getProperty(
						this.NS + "hasFirst");
				Property hasLast = this.ontLoader.getModel().getProperty(
						this.NS + "hasLast");
				Property hasMaximum = this.ontLoader.getModel().getProperty(
						this.NS + "hasMaximum");

				ind.setPropertyValue(hasConcept, this.ontLoader.getModel()
						.createTypedLiteral(seq.getID()));
				ind.setPropertyValue(hasFirst, this.ontLoader.getModel()
						.createTypedLiteral(seq.getFirst()));
				ind.setPropertyValue(hasLast, this.ontLoader.getModel()
						.createTypedLiteral(seq.getLast()));
				ind.setPropertyValue(hasMaximum, this.ontLoader.getModel()
						.createTypedLiteral(seq.getMax()));

			} else {

				//log.info("Sequence Individual does not exist");
			}
		}

		return ind;
	}

	/**
	 * @see net.swas.explorer.oh.handler.OntologyHandler#getAll()
	 */
	@Override
	public List<Entity> getAll() {

		//log.info("Sequence's getAllIndividual called ..  ");
		List<Entity> seqList = new ArrayList<Entity>();

		@SuppressWarnings("unchecked")
		ExtendedIterator<Individual> indList = (ExtendedIterator<Individual>) this.ontClass
				.listInstances();

		while (indList.hasNext()) {

			try {

				Property hasConcept = this.ontLoader.getModel().getProperty(
						this.NS + "hasConcept");
				Property hasFirst = this.ontLoader.getModel().getProperty(
						this.NS + "hasFirst");
				Property hasLast = this.ontLoader.getModel().getProperty(
						this.NS + "hasLast");
				Property hasMaximum = this.ontLoader.getModel().getProperty(
						this.NS + "hasMaximum");

				Individual ind = indList.next();

				Sequence seq = (Sequence) EntityFactory.SEQUENCE.getObject();
				seq.setID(ind.getProperty(hasConcept).getString());
				seq.setFirst(ind.getProperty(hasFirst).getInt());
				seq.setLast(ind.getProperty(hasLast).getInt());
				seq.setMax(ind.getProperty(hasMaximum).getInt());

				seqList.add(seq);

			} catch (InstantiationException | IllegalAccessException e) {
				log.info("Could not initiate Sequence object ");
				e.printStackTrace();
			}
		}

		return seqList;

	}

	
}
