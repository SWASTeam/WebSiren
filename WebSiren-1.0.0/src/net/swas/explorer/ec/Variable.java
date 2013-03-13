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
 * This class is responsible for creating variable type objects.
 */
public class Variable extends Entity {

	
	private List<String> transformation;
	private List<VariableExpression> variableExpressions;
	private List<CollectionExpression> collectionExpressions;  
	

	/**
	 * To get list of collection expression
	 * @return collection expression 
	 */
	public List<CollectionExpression> getCollectionExpressions() {
		return collectionExpressions;
	}

	/**
	 * To set list of collection expression
	 * @param collectionExpressions
	 */
	public void setCollectionExpressions(
			List<CollectionExpression> collectionExpressions) {
		this.collectionExpressions = collectionExpressions;
	}
	

	/**
	 * To get list of variable expressions
	 * @return variable expression
	 */
	public List<VariableExpression> getVariableExpressions() {
		return variableExpressions;
	}

	/**
	 * To set list of variable expressions
	 * @param expressions
	 */
	public void setVariableExpressions(List<VariableExpression> expressions) {
		this.variableExpressions = expressions;
	}

	/**
	 * To get list of transformations
	 * @return transformations
	 */
	public List<String> getTransformation() {
		return transformation;
	}

	/**
	 * To set list of transformations
	 * @param tranformation
	 */
	public void setTransformation(List<String> tranformation) {
		this.transformation = tranformation;
	}

	

}
