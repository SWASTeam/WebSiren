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
 *This class is responsible for creating variable expression type object
 */
public class VariableExpression extends Entity  {
	
	String operator;
	String variable;
	
	
	/**
	 * To get variable
	 * @return variable
	 */
	public String getVariable() {
		return variable;
	}
	
	/**
	 * To set variable
	 * @param variable
	 */
	public void setVariable(String variable) {
		this.variable = variable;
	}
	
	/**
	 * To get Operator
	 * @return operator
	 */
	public String getOperator() {
		return operator;
	}
	
	/**
	 * To set operator
	 * @param operator
	 */
	public void setOperator(String operator) {
		this.operator = operator;
	}
	
}
