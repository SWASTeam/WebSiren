/**
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
package net.swas.explorer.ecf;

/**
 * The EnityFactory can be used to obtain one or more Entity or its  derived classes objects. You just need to 
 * register the derived class name as a constant of factory enumeration.
 */
public enum EntityFactory {

	/**
	 * list of entity classes
	 */
	RULE(0, "Rule"), CHAIN_RULE(1, "ChainRule"), CONDITION(2, "Condition"), METADATA( 
	3, "MetaData"), VARIABLE(4, "Variable"), SEQUENCE(5, "Sequence"),GROUP(
	6, "RuleGroup"), USER_DEFINED_VAR(7, "UserDefinedVariable"), 
	VAR_EXPRESSION(8, "VariableExpression"), COL_EXPRESSION(9, "CollectionExpression"),
	ELEMENT(10,"Element"), USER(11, "User"), SPECIAL_COL(12, "SpecialCollection"), RESOURCE (13, "Resource"),
	HTTP_TRANSACTION(14, "HTTPTransaction"), HTTP_RESPONSE(15, "HTTPResponse"), HTTP_REQUEST(16, "HTTPRequest"),
	REQUEST_BODY(17, "RequestBody"), RESPONSE_BODY(18, "ResponseBody"), ENTITY_HEADER(19, "EntityHeader"), 
	GENERAL_HEADER(20, "GeneralHeader"), REQUEST_HEADER(21, "RequestHeader"), RESPONSE_HEADER(22, "ResponseHeader"),
	START_LINE(23, "StartLine"), STATUS_LINE(24, "StatusLine");

	private final static String packageName = "net.swas.explorer.ec."; // package name
																// of entity
																// class package
	private int index = 0; // a unique index for every constant
	private Class<Entity> cl = null;

	/**
	 * to initiate entity factory enumeration contant's properties
	 */
	@SuppressWarnings("unchecked")
	private EntityFactory(int index, String className) {

		this.index = index;
		try {
			this.cl = (Class<Entity>) Class.forName(packageName + className);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
	}

	/**
	 * to get instance of required enity class
	 */
	public Object getObject() throws InstantiationException,
			IllegalAccessException {

		Entity obj = this.cl.newInstance();
		return obj;
	}

	/**
	 * to get constant's index
	 */
	public int getIndex() {

		return this.index;

	}

	/**
	 * to get class name associated with the entity constant
	 */
	public String getClassName() {

		return this.cl.getSimpleName();

	}
	
	/**
	 * to get entity classes package name
	 */
	public static String getPackageName(){
		
		return packageName;
		
	}

}
