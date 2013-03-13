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
package net.swas.explorer.translator;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.swas.explorer.ec.ChainRule;
import net.swas.explorer.ec.CollectionExpression;
import net.swas.explorer.ec.Condition;
import net.swas.explorer.ec.Rule;
import net.swas.explorer.ec.SpecialCollection;
import net.swas.explorer.ec.UserDefinedVariable;
import net.swas.explorer.ec.VariableExpression;
import net.swas.explorer.ecf.Entity;
import net.swas.explorer.oh.fields.ElementMap;
import net.swas.explorer.oh.fields.ModSecFields;
import net.swas.explorer.util.RuleMappings;

/**
 * This class is responsible for translating semantic rules from ontology into Mod Security representation
 *
 */
public class ModSecTranslator {
	
	
	private final static Logger log = LoggerFactory
			.getLogger(ModSecTranslator.class);
	
	/**
	 * This function is responsible for generating variable string from entity object
	 * @param entity
	 * @return list of variable string
	 */
	public static List<String> getVariableString(Entity entity) {
		
		List<String> varStrList = new ArrayList<String>();
		Rule rule = null;
		ChainRule chainRule = null;
		if (entity instanceof Rule)
		{
			rule = (Rule) entity;
			varStrList.add(getVarStringPerCondition(rule.getCondition()));
			
		} else if (entity instanceof ChainRule){
			
			chainRule = (ChainRule) entity;
			for(Condition cond: chainRule.getCondition()){
			
				varStrList.add(getVarStringPerCondition(cond));
				
			}
		}
		
		return varStrList;
	}
	
	/**
	 * This function is responsible for generating special collection string from map
	 * @param spColMap
	 * @return special collection string
	 */
	public static String getSpCollectionString(Map<String, SpecialCollection> spColMap){
		
		
		for(SpecialCollection spCol:spColMap.values()){
			
		}
		return null;
		
	}
	
	/**
	 * This function is responsible for generating variable string for each condition from {@link net.swas.explorer.ec.Condition} type object
	 * @param cond
	 * @return variable string
	 */
	public static String getVarStringPerCondition(Condition cond){
		
		String variableString = "";
		int varExpSize = cond.getVariable().getVariableExpressions().size();
		int colExpSize = cond.getVariable().getCollectionExpressions().size();
		
		for (int i = 0; i < varExpSize; i++) {
	
			VariableExpression varExp= cond.getVariable().getVariableExpressions()
					.get(i);
			
			variableString += RuleMappings.getMappedOperator(varExp.getOperator()) + varExp.getVariable();
			log.info("operator: " + varExp.getOperator());
			
			if (varExpSize > 1 && i != varExpSize - 1) {
				variableString += "|";
			}

		}
		
		if(!variableString.equals("") && colExpSize > 0){
			variableString += "|";
		}
	
		for ( int i = 0; i < colExpSize; i++ ) {   

			CollectionExpression colExp = cond.getVariable().getCollectionExpressions().get(i);
			
			variableString += RuleMappings.getMappedOperator(colExp.getOperator());
			log.info("Collection operator : " + colExp.getOperator());
			variableString += colExp.getCollection();
			
			if(!colExp.getElement().equals("")){
				
				String element = RuleMappings.elementMapping(colExp.getElement().split("\\.")[1]);
				variableString += element;
			
			}
			
			if (colExpSize > 1 && i != colExpSize - 1) {
				variableString += "|";
			}
		}
		
		return variableString;
		
	}

	/**
	 * This function is responsible for generating condition string from {@link net.swas.explorer.ecf.Entity} type object
	 * @param entity
	 * @return list of condition string
	 */
	public static List<String> getConditionString(Entity entity) {
		
		List<String> conStrList = new ArrayList<String>();
		String quotes = "\"";
		String OPERATOR_PREFIX = "@";
		String negationOperator = "!";
		
		if (entity instanceof Rule)
		{
			String conditionString = "";
			
			Rule rule = (Rule) entity;
			conditionString += quotes;
			if (rule.getCondition().getIsNegated())
			{
				conditionString += negationOperator;
			}
			conditionString += OPERATOR_PREFIX;
			conditionString += rule.getCondition().getOperator();
			conditionString += " ";
			conditionString += rule.getCondition().getValue();
			log.info("Value : " + rule.getCondition().getValue());
			conditionString += quotes;
			
			log.info("Condition String : " + conditionString);
			conStrList.add(conditionString);
			
		}
		else if (entity instanceof ChainRule)
		{
			  
			ChainRule chainRule = (ChainRule) entity;
			for(Condition cond: chainRule.getCondition()){
				
				String conditionString = "";
				
				conditionString += quotes;
				if (cond.getIsNegated())
				{
					conditionString += negationOperator;
				}
				conditionString += OPERATOR_PREFIX;
				conditionString += cond.getOperator();
				conditionString += " ";
				conditionString += cond.getValue();
				conditionString += quotes;
				
				conStrList.add(conditionString);
				
			}
		}
		return conStrList;

	}

	
	/**
	 * This function is responsible for generating action string from {@link net.swas.explorer.ecf.Entity} type object
	 * @param entity
	 * @return list of action string
	 */
	public static List<String> getActionString(Entity entity) {

		log.info("getActionString called ... " );
		List<String> actionStrList = new  ArrayList<String>();
		String quotes = "\"";
			if (entity instanceof Rule)
			{
				String actionString = "";
				Rule rule = (Rule) entity;	
				actionString += quotes;
				actionString += rule.getDisruptiveAction() + ",";
				actionString += RuleMappings.getMappedSpCollection(rule.getSpecialCollection().values());
				
				if(!rule.getMetaData().getRuleID().equals("") && rule.getMetaData().getRuleID() != null){
					
					log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>> id :" + rule.getMetaData().getRuleID().length() + rule.getMetaData().getRuleID() + "|");
					actionString += ModSecFields.id.toString() + ":\'"
							+ rule.getMetaData().getRuleID() + "\',";
				
				}
				
				actionString += "auditlog,";
				actionString += ModSecFields.rev.toString() + ":\'"
						+ rule.getMetaData().getRevision() + "\',";
				actionString += ModSecFields.phase.toString() + ":\'"
						+ rule.getPhase() + "\',";
				actionString += ModSecFields.severity.toString() + ":\'"
						+ rule.getMetaData().getSeverity() + "\',";
				
				for(String trans:rule.getCondition().getVariable().getTransformation()){
					
					actionString += ModSecFields.t.toString() + ":\'" 
								+ trans + "\'," ;
				
				}
				
				actionString += ModSecFields.msg.toString() + ":\'"
						+ rule.getMetaData().getMessage() + "\',";
			
			for( int i = 0; i < rule.getCondition().getUserDefinedVariables().size() ; i++){
				
				UserDefinedVariable udv = rule.getCondition().getUserDefinedVariables().get(i);
				log.info("UserDefinedVariables :" + udv.getName() );
		
			    actionString += RuleMappings.userDefinedVariableMapping(udv);
			}
		
			for (int i = 0; i < rule.getMetaData().getTag().size(); i++) {
	
				String tag = rule.getMetaData().getTag().get(i);
				if (i != rule.getMetaData().getTag().size() - 1) {
					actionString += ModSecFields.tag.toString() + ":\'" + tag
							+ "\',";
				} else {
					actionString += ModSecFields.tag.toString() + ":\'" + tag
							+ "\'";
				}
				
			}
			
			actionString += quotes;
			actionStrList.add(actionString);
			
		}
		
		else if ( entity instanceof ChainRule)
		{
			
			ChainRule chainRule = (ChainRule) entity;	
			
			int condSize =  chainRule.getCondition().size();
			
			for(int i = 0; i < condSize; i++){
				
				String actionString = "";
				Condition cond = chainRule.getCondition().get(i);

				actionString += quotes;
				if( i == 0 ){
					
					actionString += ModSecFields.chain.toString() + ",";
					actionString += chainRule.getDisruptiveAction() + ",";
					actionString += RuleMappings.getMappedSpCollection(chainRule.getSpecialCollection().values());
					
					if(!chainRule.getMetaData().getRuleID().equals("") && chainRule.getMetaData().getRuleID() != null){
						
						log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>> id :" + chainRule.getMetaData().getRuleID().length() + chainRule.getMetaData().getRuleID() + "|");
						actionString += ModSecFields.id.toString() + ":\'"
								+ chainRule.getMetaData().getRuleID() + "\',";
					
					}
					
					actionString += "auditlog,";
					actionString += ModSecFields.rev.toString() + ":\'"
							+ chainRule.getMetaData().getRevision() + "\',";
					actionString += ModSecFields.phase.toString() + ":\'"
							+ chainRule.getPhase() + "\',";
					actionString += ModSecFields.severity.toString() + ":\'"
							+ chainRule.getMetaData().getSeverity() + "\',";
					
					for(String trans:cond.getVariable().getTransformation()){
						
						actionString += ModSecFields.t.toString() + ":\'" 
									+ trans + "\'," ;
					
					} 
					
					actionString += ModSecFields.msg.toString() + ":\'"
							+ chainRule.getMetaData().getMessage() + "\',";
				
				for( int j = 0; j < cond.getUserDefinedVariables().size() ; j++){
					
					UserDefinedVariable udv = cond.getUserDefinedVariables().get(j);
					log.info("UserDefinedVariables :" + udv.getName() );
			
				    actionString += RuleMappings.userDefinedVariableMapping(udv);
				}
			
				for (int j = 0; j < chainRule.getMetaData().getTag().size(); j++) {
		
					String tag = chainRule.getMetaData().getTag().get(j);
					if (j != chainRule.getMetaData().getTag().size() - 1) {
						actionString += ModSecFields.tag.toString() + ":\'" + tag
								+ "\',";
					} else {
						actionString += ModSecFields.tag.toString() + ":\'" + tag
								+ "\'";
					}
					
				}
					
				} else if( i != condSize - 1 ){
					
					actionString += ModSecFields.chain.toString() + ",";
					
					if(!(cond.getVariable().getTransformation().size() < 1 ||  cond.getUserDefinedVariables().size() < 1)){
						actionString += "," ;
					}
					
				} 
				
				if( i > 0 ){
					
					for(String trans:cond.getVariable().getTransformation()){
						
						actionString += ModSecFields.t.toString() + ":\'" 
									+ trans + "\'," ;
					
					}
					
					for( int j = 0; j < cond.getUserDefinedVariables().size() ; j++){
						
						UserDefinedVariable udv = cond.getUserDefinedVariables().get(j);
						
						log.info("UserDefinedVariables :" + udv.getName() );
					    actionString += RuleMappings.userDefinedVariableMapping(udv);
					}
					
				}

				actionString += quotes;
				actionStrList.add(actionString);
			}
		}
			
		return actionStrList;

	}

	/**
	 * This function is responsible for generating rule string from {@link net.swas.explorer.ecf.Entity} type object
	 * @param entity
	 * @return rule string
	 */
	public static String getRuleString(Entity entity) {

		String ruleString = "";
		String COMMENT_PREFIX = "#";

		if(entity instanceof Rule){
			
			Rule rule = (Rule) entity;
			ruleString += COMMENT_PREFIX + rule.getComment() + "\n";
			ruleString += COMMENT_PREFIX + rule.getRuleTitle() + "\n" + "\n";
			ruleString += ModSecFields.SecRule.toString() + " ";
			ruleString += getVariableString(rule).get(0) + " ";
			ruleString += getConditionString(rule).get(0) + " ";
			ruleString += getActionString(rule).get(0);
			ruleString += "\n" + "\n";
		
		} else {
			
			ChainRule chainRule = (ChainRule) entity;
			for (int i = 0; i < chainRule.getCondition().size(); i++) {
				if (i == 0)
				{
					ruleString += COMMENT_PREFIX + chainRule.getComment() + "\n";
					ruleString += COMMENT_PREFIX + chainRule.getRuleTitle() + "\n" + "\n";
					ruleString += ModSecFields.SecRule.toString() + " ";
					ruleString += getVariableString(chainRule).get(i) + " ";
					ruleString += getConditionString(chainRule).get(i) + " ";
					ruleString += getActionString(chainRule).get(i);
					ruleString += "\n";
				}
				else
				{
					ruleString += ModSecFields.SecRule.toString() + " ";
					ruleString += getVariableString(chainRule).get(i) + " ";
					ruleString += getConditionString(chainRule).get(i) + " ";
					ruleString += getActionString(chainRule).get(i);
					ruleString += "\n" + "\n";
				}
			}
			
		}
		return ruleString;

	}

}
