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
 * This class is responsible for creating user defined variable type objects.
 */
public class UserDefinedVariable extends Entity {
	
	private String name;
	private String value;
	private SpecialCollection variableOf;
	
	/**
	 * To get name of user defined variable
	 * @return name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * To set name of user defined variable
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * To get value of user defined variable
	 * @return value
	 */
	public String getValue() {
		return value;
	}
	
	/**
	 * To set value of user defined variable
	 * @param value
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * To get Special collection associated with variable
	 * @return special collection
	 */
	public SpecialCollection getVariableOf() {
		return variableOf;
	}

	/**
	 * To set Special collection associated with variable
	 * @param variableOf
	 */
	public void setVariableOf(SpecialCollection variableOf) {
		this.variableOf = variableOf;
	}
	
}
