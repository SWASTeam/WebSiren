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

/**
 * This class is responsible for creating Element type object. It helps Collection Variable class
 *
 */
public class Element extends Entity {

	String name;
	String collection;

	/**
	 * To get Collection
	 * @return the collection
	 */
	public String getCollection() {
		return collection;
	}

	/**
	 * To set Collection
	 * @param collection the collection to set
	 */
	public void setCollection(String collection) {
		this.collection = collection;
	}

	/**
	 * To get collection Name
	 * @return name
	 */
	public String getName() {
		return name;
	}

	/**
	 * To set collection Name
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	
}
