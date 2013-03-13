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
package net.swas.explorer.util;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.swas.explorer.ec.CollectionExpression;
import net.swas.explorer.ec.SpecialCollection;
import net.swas.explorer.ec.UserDefinedVariable;
import net.swas.explorer.ec.VariableExpression;
import net.swas.explorer.oh.fields.ElementMap;
import net.swas.explorer.oh.fields.ModSecFields;

/**
 * The RuleMapping class has all utility functions for mapping 
 * domain objects to modsecurity rule string and vice versa.
 */
public class RuleMappings {

	
	private final static Logger log = LoggerFactory.getLogger(RuleMappings.class);
	
	/**
	 * Maps the VariableExpression to string.
	 * @param variableString
	 * @return variable expression's string representation.
	 */
	public static String standardVariableMappings(VariableExpression variableString){
		
		String var = variableString.getVariable().toString();
		String op = getMappedOperator(variableString.getOperator().toString());
		return op+var;
	}
	
	
	/**
	 * Maps the CollectionExpression to string.
	 * @param colString
	 * @return collection expression's string representation.
	 */
	public static String collectionVariableMapping(CollectionExpression colString){
		
		String op = getMappedOperator(colString.getOperator().toString());
		String colVar = colString.getCollection().toString();
		String element = "";
		if(!colString.getElement().equals(""))
		{	
			element = elementMapping(colString.getElement().split("\\.")[1]);	
		}
		
		return op + colVar + element;
	}
	
	/**
	 * Maps the Collection Element to string.
	 * @param element
	 * @return element's string representation.
	 */
	public static String elementMapping(String element){
		
		log.info("Element :" + element);
		if(element.equals(ElementMap.all_xml_elements.toString())){
			element = "/" + ElementMap.all_xml_elements.getMappedElement();
		}
		element = ":" + element;
		return element;
	}
	
	/**
	 * Maps the UserDefinedVariables to string.
	 * @param udv
	 * @return user defined variable's string representation.
	 */
	public static String userDefinedVariableMapping(UserDefinedVariable udv) {
	
		String name = udv.getName().replaceAll(ElementMap.var_open.toString(), ElementMap.var_open.getMappedElement());
		name = name.replaceAll(ElementMap.var_close.toString(), ElementMap.var_close.getMappedElement());
		name= name.replaceAll( ElementMap.fwdslash.toString(), ElementMap.fwdslash.getMappedElement());
				
		String value = udv.getValue().replaceAll(ElementMap.var_open.toString(), ElementMap.var_open.getMappedElement());
		value = value.replaceAll(ElementMap.var_close.toString(), ElementMap.var_close.getMappedElement());
		value = value.replaceAll(ElementMap.op_append.toString(), ElementMap.op_append.getMappedElement());
		value = value.replaceAll(ElementMap.op_equal.toString(), ElementMap.op_equal.getMappedElement());
		
		String userDefined = "";
		if(udv.getName().contains(".")){
			
			if(name.split("\\.")[0].equals(ModSecFields.TX.toString())){
				
				userDefined = ModSecFields.setvar.toString() + ":'" + ModSecFields.tx.toString() + "." + name.substring(3) + value + "',";
			
			}else if(name.split("\\.")[0].equals(ModSecFields.IP.toString())){
				
				userDefined = ModSecFields.setvar.toString() + ":'" + ModSecFields.ip.toString() + "." + name.substring(3)  +  value + "',";
				
			}else if(name.split("\\.")[0].equals(ModSecFields.SESSION.toString())){
				
				userDefined = ModSecFields.setvar.toString() + ":'" + ModSecFields.session.toString() + "." + name.substring(7)  + value + "',";
				
			}else if(name.split("\\.")[0].equals(ModSecFields.GLOBAL.toString())){
				
				userDefined = ModSecFields.setvar.toString() + ":'" + ModSecFields.global.toString() + "." + name.substring(6) +  value + "',";
				
			}else if(name.split("\\.")[0].equals(ModSecFields.USER.toString())){
				
				userDefined = ModSecFields.setvar.toString() + ":'" + ModSecFields.user.toString() + "." + name.substring(5)  + value + "',";
				
			}else if(name.split("\\.")[0].equals(ModSecFields.ENV.toString())){
				
				userDefined = ModSecFields.setenv.toString() + ":'"  + name.substring(4)  + value + "',";
				
			}
		
		}
		return userDefined;
	}
	
	/**
	 * Maps the Unary Operator to string.
	 * @param operator
	 * @return mapped operator
	 */
	public static String getMappedOperator(String operator) {
		
		if (operator.equals(ElementMap.ampersand.toString()))
		{
			operator = operator.replace(ElementMap.ampersand.toString(), ElementMap.ampersand.getMappedElement());
		}
		else if (operator.equals(ElementMap.negation.toString()))
		{
			operator = operator.replace(ElementMap.negation.toString(), ElementMap.negation.getMappedElement());
		}
		return operator;
	}

	/**
	 * Maps the SpecialCollection to string.
	 * @param spColList list of special collection 
	 * @return special collection's string representation.
	 */
	public static String getMappedSpCollection(Collection<SpecialCollection> spColList){
		
		String spColString = "";
		for(SpecialCollection spCol:spColList){
		
			if(spCol.getClassName().equals(ModSecFields.IPCollection.toString())){
				
				spColString += ModSecFields.initcol.toString() + ":" +
							ModSecFields.IP.toString() + "=" + spCol.getName() + ",";
			
			} else if(spCol.getClassName().equals(ModSecFields.GlobalCollection.toString())){
				
				spColString += ModSecFields.initcol.toString() + ":" +
						ModSecFields.GLOBAL.toString() + "=" + spCol.getName() + ",";
			
			} else if(spCol.getClassName().equals(ModSecFields.ResourceCollection.toString())){
				
				spColString += ModSecFields.initcol.toString() + ":" +
						ModSecFields.RESOURCE.toString() + "=" + spCol.getName() + ",";
			
			} else if(spCol.getClassName().equals(ModSecFields.SessionCollection.toString())){
				
				spColString += ModSecFields.setsid.toString() + ":" + spCol.getName() + ",";
			
			} else if(spCol.getClassName().equals(ModSecFields.UserCollection.toString())){
				
				spColString += ModSecFields.setuid.toString()+ ":" + spCol.getName() + ",";
			
			}
				
			//TODO: shifting comma to be based on the size of list
		}
		
		return spColString;
	
	}

}

