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
package net.swas.explorer.ec;

import net.swas.explorer.ecf.Entity;
import net.swas.explorer.ecf.EntityFactory;
import net.swas.explorer.oh.handler.SequenceHandler;

public class Sequence extends Entity {

	
	private int first;
	private int last;
	private int max;

	/**
	 * To get first value
	 * @return first
	 */
	public int getFirst() {
		return first;
	}

	/**
	 * To set first value
	 * @param first
	 */
	public void setFirst(int first) {
		this.first = first;
	}

	/**
	 * To get last value
	 * @return last
	 */
	public int getLast() {
		return last;
	}

	/**
	 * To set last value
	 * @param last
	 */
	public void setLast(int last) {
		this.last = last;
	}

	/**
	 * To get maximum value
	 * @return maximum
	 */
	public int getMax() {
		return max;
	}

	/**
	 * To set maximum value
	 * @param max
	 */
	public void setMax(int max) {
		this.max = max;
	}

	/**
	 * To get sequence ID
	 * @param hndlr 
	 * @param concept
	 * @return ID
	 */
	public static synchronized int getID(SequenceHandler hndlr, String concept) {

		Sequence seq = null;

		try {

			seq = (Sequence) hndlr.get(concept);
			if (seq != null) {
				int lastVal = seq.getLast();
				seq.setLast(++lastVal);
				hndlr.update(seq);

			} else {

				seq = (Sequence) EntityFactory.SEQUENCE.getObject();
				seq.setID(concept);
				seq.setFirst(0);
				seq.setLast(0);
				seq.setMax(100000);
				hndlr.add(seq);

			}

		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}

		return seq.getLast();

	}

}
