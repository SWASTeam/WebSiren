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

import java.util.List;

import net.swas.explorer.ecf.Entity;

/**
 * This class utilizes information from variable class. Main purpose of this class
 * is to create condition type objects.
 */
public class Condition extends Entity {


	private boolean isNegated = false;
	private String operator = "";
	private String value = "";
	private Variable variable;
	private List<UserDefinedVariable> userDefinedVariables;
	
	
	/**
	 * To get Operator isNegated
	 * @return the operator
	 */
	public boolean getIsNegated() {
		return isNegated;
	}

	/**
	 * To set operator isNegated
	 * @param isNegated
	 */
	public void setIsNegated(boolean isNegated) {
		this.isNegated = isNegated;
	}

	/**
	 * To get Operator
	 * @return the operator
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

	/**
	 * To get Value
	 * @return value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * To set value
	 * @param value
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * To get Variable
	 * @return variable
	 */
	public Variable getVariable() {

		return this.variable;
	}

	/**
	 * To set Variable
	 * @param variable
	 */
	public void setVariable(Variable variable) {
		this.variable = variable;
	}
	

	/**
	 * To get list of User Defined Variables
	 * @return list of User Defined Variables
	 */
	public List<UserDefinedVariable> getUserDefinedVariables() {
		return userDefinedVariables;
	}

	/**
	 * To set list of User Defined Variables
	 * @param userDefinedVariables
	 */
	public void setUserDefinedVariables(
			List<UserDefinedVariable> userDefinedVariables) {
		this.userDefinedVariables = userDefinedVariables;
	}
	

}
