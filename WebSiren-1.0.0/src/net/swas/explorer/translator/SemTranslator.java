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

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;

import net.swas.explorer.ec.ChainRule;
import net.swas.explorer.ec.CollectionExpression;
import net.swas.explorer.ec.Condition;
import net.swas.explorer.ec.MetaData;
import net.swas.explorer.ec.Resource;
import net.swas.explorer.ec.Rule;
import net.swas.explorer.ec.RuleGroup;
import net.swas.explorer.ec.SpecialCollection;
import net.swas.explorer.ec.User;
import net.swas.explorer.ec.UserDefinedVariable;
import net.swas.explorer.ec.Variable;
import net.swas.explorer.ec.VariableExpression;
import net.swas.explorer.ecf.EntityFactory;
import net.swas.explorer.httpprofile.DOProfile;
import net.swas.explorer.oh.fields.ElementMap;
import net.swas.explorer.oh.fields.ModSecFields;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is responsible for translating Mod Security rules into semantic representation.
 *
 */
public class SemTranslator {
	
	private final static Logger log = LoggerFactory
			.getLogger(SemTranslator.class);
	
	/**
	 * This function is responsible for creating {@link net.swas.explorer.ec.Rule} bean
	 * @param title
	 * @param md
	 * @param cond
	 * @param disruptiveAction
	 * @param phase
	 * @param group
	 * @return Rule type object
	 */
	public static Rule toRuleBean( String title, MetaData md, Condition cond, 
			String disruptiveAction, int phase, String comment, RuleGroup group){
		
		log.info( "Rule Components :  Title : " + title + "\n" 
				+ "| Severity : " + md.getSeverity() + "| Message : " + md.getMessage() + "\n" 
				+ "| Revision : " + md.getRevision() + "| Phase : " + phase + "\n" 
				+ "| Operator : " + cond.getOperator() + "| Severity : " + cond.getValue() + "\n" 
				+ "| Action : " + disruptiveAction + "| Group : " + group.getName()); 
		
		Rule rule = null;
		try {
			
			rule = (Rule) EntityFactory.RULE.getObject();
			rule.setRuleTitle(title);
			rule.setMetaData(md);
			rule.setCondition(cond);
			rule.setDisruptiveAction(disruptiveAction);
			rule.setPhase(phase);
			rule.setComment(comment);
			rule.setRuleGroup(group);
			
			
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return rule;
		
	}
	
	/**
	 * This function is responsible for creating {@link net.swas.explorer.ec.ChainRule} bean
	 * @param title
	 * @param md
	 * @param conds
	 * @param disruptiveAction
	 * @param phase
	 * @param comment
	 * @param group
	 * @return chain rule type object
	 */
	public static ChainRule toChainRuleBean( String title, MetaData md, List<Condition> conds, 
			String disruptiveAction, int phase, String comment, RuleGroup group){
		

		log.info( "Rule Components :  Title : " + title + "\n" 
				+ "| Severity : " + md.getSeverity() + "| Message : " + md.getMessage() + "\n" 
				+ "| Revision : " + md.getRevision() + "| Phase : " + phase + "\n" 
				+ "| Operator : " + conds.get(0).getOperator() + "| Value : " + conds.get(0).getValue() + "\n" 
				+ "| Action : " + disruptiveAction + "| Group : " + group.getName()); 
		
		ChainRule chain = null;
		try {
			
			chain = (ChainRule) EntityFactory.CHAIN_RULE.getObject();
			chain.setRuleTitle(title);
			chain.setMetaData(md);
			chain.setCondition(conds);
			chain.setDisruptiveAction(disruptiveAction);
			chain.setPhase(phase);
			chain.setComment(comment);
			chain.setRuleGroup(group);
			
			
		} catch (InstantiationException | IllegalAccessException e){
			e.printStackTrace();
		}
		return chain;
		
	}
	
	/**
	 * This function is responsible for creating {@link net.swas.explorer.ec.MetaData} bean
	 * @param ruleID
	 * @param message
	 * @param revision
	 * @param severity
	 * @param tags
	 * @return meta data type object
	 */
	public static MetaData toMetaDataBean( String ruleID, String message, 
			String revision, String severity, List<String> tags ){
		
		log.info( "Meta Data : Severity : " + severity + "| Message : " + message + "\n" 
				+ "| Revision : " + revision + "| RuleID : " + ruleID ); 
		
		MetaData md = null;
		try {
			
			md = (MetaData) EntityFactory.METADATA.getObject();
			md.setRuleID(ruleID);
			md.setMessage(message);
			md.setRevision(revision);
			md.setSeverity(severity);
			md.setTag(tags);
			
			
		} catch (InstantiationException | IllegalAccessException e){
			e.printStackTrace();
		}
		return md;
		
	}

	/**
	 * This function is responsible for creating {@link net.swas.explorer.ec.Condition} bean
	 * @param isNegated
	 * @param operator
	 * @param value
	 * @param var
	 * @param udvList
	 * @return condition type object
	 */
	public static Condition toConditionBean( boolean isNegated, String operator, 
			String value, Variable var, List<UserDefinedVariable> udvList ){
		
		log.info( "Condition : Negation : " + isNegated + "| operator : " + operator + "\n" 
				+ "| Value : " + value + "| Variable : " + var.toString() ); 
		
		Condition cond = null;
		try {
			
			cond = (Condition) EntityFactory.CONDITION.getObject();
			cond.setIsNegated(isNegated);
			cond.setOperator(operator);
			cond.setValue(value);
			cond.setVariable(var);
			cond.setUserDefinedVariables(udvList);
			
			
		} catch (InstantiationException | IllegalAccessException e){
			e.printStackTrace();
		}
		return cond;
		
	}
	
	/**
	 * This function is responsible for creating {@link net.swas.explorer.ec.Variable} bean
	 * @param transfromations
	 * @param varExps
	 * @param colExps
	 * @return variable type object
	 */
	public static Variable toVariableBean( List<String> transfromations, 
			List<VariableExpression> varExps, List<CollectionExpression> colExps ){
		
		Variable var = null;
		try {
			
			var = (Variable) EntityFactory.VARIABLE.getObject();
			var.setTransformation(transfromations);
			var.setVariableExpressions(varExps);
			var.setCollectionExpressions(colExps);
			
			
		} catch (InstantiationException | IllegalAccessException e){
			e.printStackTrace();
		}
		return var;
		
	}
	
	/**
	 * This function is responsible for creating {@link net.swas.explorer.ec.VariableExpression} bean
	 * @param expression
	 * @return list of variable expression type objects
	 */
	public static List<VariableExpression> toVariableExpBeans(String expression){
		
		log.info( "Variable Expressions : Expression String : " + expression ); 
		List<VariableExpression> varExpList = new ArrayList<VariableExpression>();
		
		try {
			for(String expStr: expression.split(",")){
				
				String[] tokens = expStr.split("\\|"); 
				if(tokens[1].split(":")[0].equals("standardVars")){
					
					VariableExpression varExp = (VariableExpression) EntityFactory.VAR_EXPRESSION.getObject();
					
					boolean check = true; // for checking whether operator exist and matches
					if(tokens[0].split(":").length > 1){
						
						if( ElementMap.ampersand.toString().equals(tokens[0].split(":")[1]) ||
								ElementMap.negation.toString().equals(tokens[0].split(":")[1])){
							
							varExp.setOperator(tokens[0].split(":")[1]);
							check = false;
						}
						
					} 
					
					if(check){
						
						varExp.setOperator("");
						
					}
					
					varExp.setVariable(tokens[1].split(":")[1]);
					varExpList.add(varExp);
					
				}
				
			}
			
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		
		return varExpList;
		
	}
	

	/**
	 * This function is responsible for creating {@link net.swas.explorer.ec.VariableExpression} bean
	 * @param unaryOperator
	 * @param variable
	 * @return variable expression type objects
	 */
	public static VariableExpression toVariableExpBeans(String unaryOperator, String variable){
		
		log.info( "Variable Expressions : Unary Operator : " + unaryOperator + " | Variable :" + variable  ); 
		VariableExpression varExp = null;
		try {
	
			varExp = (VariableExpression) EntityFactory.VAR_EXPRESSION.getObject();
			varExp.setOperator(unaryOperator);
			varExp.setVariable(variable);

		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		
		return varExp;
		
	}
	
	/**
	 * This function is responsible for creating {@link net.swas.explorer.ec.CollectionExpression} bean
	 * @param unaryOperator
	 * @param collection
	 * @param element
	 * @return collection expression type object
	 */
	public static CollectionExpression toCollectionExpBeans(String unaryOperator, String collection, String element){
		
		log.info( "Collection Expressions : Unary Operator : " + unaryOperator + " | Collection:" + collection + 
				" | element : " + element);
		
		CollectionExpression colExp = null;
		try {
	
			colExp = (CollectionExpression) EntityFactory.COL_EXPRESSION.getObject();
			colExp.setOperator(unaryOperator);
			colExp.setCollection(collection);
			colExp.setElement(element);

		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		
		return colExp;
		
	}
	
	/**
	 * This function is responsible for creating {@link net.swas.explorer.ec.CollectionExpression} bean
	 * @param unaryOperator
	 * @param collection
	 * @return collection expression type objects
	 */
	public static CollectionExpression toCollectionExpBeans(String unaryOperator, String collection){
		
		log.info( "Collection Expressions : Unary Operator : " + unaryOperator + " | Collection:" + collection );
		
		CollectionExpression colExp = null;
		try {
	
			colExp = (CollectionExpression) EntityFactory.COL_EXPRESSION.getObject();
			colExp.setOperator(unaryOperator);
			colExp.setCollection(collection);
			colExp.setElement("");

		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		
		return colExp;
		
	}
	/**This function is responsible for creating {@link net.swas.explorer.ec.CollectionExpression} bean
	 * @param expression
	 * @return list of collection expression type objects
	 */
	public static List<CollectionExpression> toCollectionExpBeans(String expression){
		
		log.info( "Collection Expressions : Expression String : " + expression ); 
		List<CollectionExpression> colExpList = new ArrayList<CollectionExpression>();
		
		try{
			for(String expStr: expression.split(",")){
				
				String[] tokens = expStr.split("\\|"); 
				if(tokens[1].split(":")[0].equals("collectionVars")){
					
					CollectionExpression colExp = (CollectionExpression) EntityFactory.COL_EXPRESSION.getObject();
					
					boolean check = true; // for checking whether operator exist and matches
					if(tokens[0].split(":").length > 1){
						
						if( ElementMap.ampersand.toString().equals(tokens[0].split(":")[1]) ||
								ElementMap.negation.toString().equals(tokens[0].split(":")[1])){
							
							colExp.setOperator(tokens[0].split(":")[1]);
							check = false;
						}
						
					}	
					
					if(check){
						
						colExp.setOperator("");
						
					}
					
					colExp.setCollection(tokens[1].split(":")[1]);
					
					if(tokens.length == 3){
						
						if(tokens[2].split(":")[1].equals("all")){
							colExp.setElement("");
						} else {
							colExp.setElement(tokens[2].split(":")[1]);
						}
					} else {
						
						colExp.setElement("");
					
					}
					
					colExpList.add(colExp);
					
				}
			}
		
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
			
		return colExpList;
		
	}
	
	/**
	 * This function is responsible for creating {@link net.swas.explorer.ec.UserDefinedVariable} bean
	 * @param names
	 * @param values
	 * @return list of user defined variable type object
	 */
	public static List<UserDefinedVariable> toUserDefVariablesBeans(String[] names, String[] values){
		
		log.info( "User Defined Variable : Names : " + names.toString()); 
		List<UserDefinedVariable> udvList = new ArrayList<UserDefinedVariable>();
		UserDefinedVariable udv = null;
		
		try{
			
			for( int i=0 ; i < names.length; i++ ){
				
				if(!names[i].trim().equals("")){
					
					udv = (UserDefinedVariable) EntityFactory.USER_DEFINED_VAR.getObject();
					if(!names[i].startsWith("TX")){
						udv.setName( ModSecFields.TX.toString() + "." + names[i]);
					} else{
						udv.setName(names[i]);
					}
					String transValue = ElementMap.op_equal.toString() + ElementMap.var_open.toString()
							+ values[i] + ElementMap.var_close.toString();
					udv.setValue(transValue);
					udvList.add(udv);
				
				}
				
			}
		
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
			
		return udvList;
		
	}
	/**
	 * This function is responsible for creating {@link net.swas.explorer.ec.UserDefinedVariable} bean
	 * @param name
	 * @param value
	 * @return list of user defined variable type objects
	 */
	public static List<UserDefinedVariable> toUserDefVariablesBeans(String name, String value){
		
		log.info( "User Defined Variable : Names : " + name.toString()); 
		List<UserDefinedVariable> udvList = new ArrayList<UserDefinedVariable>();
		UserDefinedVariable udv = null;
		
		try{
			
			udv = (UserDefinedVariable) EntityFactory.USER_DEFINED_VAR.getObject();
			if(!name.startsWith("TX")){
				udv.setName( ModSecFields.TX.toString() + "." + name);
			} else{
				udv.setName(name);
			}
			String transValue = ElementMap.op_equal.toString() + ElementMap.var_open.toString()
					+ value + ElementMap.var_close.toString();
			udv.setValue(transValue);
			udvList.add(udv);
		
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
			
		return udvList;
		
	}
	/**
	 * This function is responsible for creating {@link net.swas.explorer.ec.RuleGroup} bean
	 * @param name
	 * @param despcription
	 * @return rule group type object
	 */
	public static RuleGroup toRuleGroupBeans(String name, String despcription){
		
		log.info( "Rule Group : Group : " + name + " | Description : " + despcription); 
		RuleGroup group = null;
		try {
			
			group = (RuleGroup) EntityFactory.GROUP.getObject();
			group.setName(name);
			group.setDescription(despcription);
			
		} catch (InstantiationException | IllegalAccessException e){
			e.printStackTrace();
		}
		return group;
		
	}
	
	/**
	 * This function is responsible for creating {@link net.swas.explorer.ec.User} bean
	 * @param userName
	 * @param displayName
	 * @param preName
	 * @return user type object
	 */
	public static User toUserBeans(String userName, String displayName, String preName){
		
		log.info( "User : UserName : " + userName + " | DisplayName : " + displayName ); 
		User user = null;
		try {
			
			user = (User) EntityFactory.USER.getObject();
			user.setUserName(userName);
			user.setDisplayName(displayName);
			user.setPreviousName(preName);
			
		} catch (InstantiationException | IllegalAccessException e){
			e.printStackTrace();
		}
		return user;
		
	}
	
	/**
	 * This function is responsible for creating {@link net.swas.explorer.ec.SpecialCollection} bean
	 * @param className
	 * @param value
	 * @return special collection type object
	 */
	public static SpecialCollection toSpecialColection( String className, String value ){
		
		log.info( "Special Collection : Type : " + className + " | Value : " + value ); 
		SpecialCollection spCol = null;
		try {
			
			spCol = (SpecialCollection) EntityFactory.SPECIAL_COL.getObject();
			spCol.setClassName(className);
			spCol.setName(value);
			
		} catch (InstantiationException | IllegalAccessException e){
			e.printStackTrace();
		}
		return spCol;
		
	}
	
	/**
	 * This function is responsible for creating {@link net.swas.explorer.ec.Resource} bean
	 * @param resource
	 * @param context
	 * @return resource type object
	 * @throws IOException
	 * @throws SQLException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public static Resource toResourceBean(String resource, ServletContext context) throws IOException, SQLException, InstantiationException, IllegalAccessException
	{
		Resource res = (Resource) EntityFactory.RESOURCE.getObject();
		DOProfile profile = new DOProfile(context);
		String url = profile.getUrlByResource(resource);
		res.setUrl(url);
		res.setResource(resource);
		return res;
	}
	
	
}
