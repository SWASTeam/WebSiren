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
package net.swas.explorer.parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.swas.explorer.ec.ChainRule;
import net.swas.explorer.ec.CollectionExpression;
import net.swas.explorer.ec.Condition;
import net.swas.explorer.ec.MetaData;
import net.swas.explorer.ec.Rule;
import net.swas.explorer.ec.RuleGroup;
import net.swas.explorer.ec.SpecialCollection;
import net.swas.explorer.ec.UserDefinedVariable;
import net.swas.explorer.ec.Variable;
import net.swas.explorer.ec.VariableExpression;
import net.swas.explorer.ecf.Entity;
import net.swas.explorer.ecf.EntityFactory;
import net.swas.explorer.exception.SWASException;
import net.swas.explorer.oh.fields.DisruptiveAction;
import net.swas.explorer.oh.fields.ElementMap;
import net.swas.explorer.oh.fields.ModSecFields;
import net.swas.explorer.oh.handler.Fetcher;
import net.swas.explorer.oh.handler.UserDefinedVariableHandler;
import net.swas.explorer.oh.lo.OntologyLoader;
import net.swas.explorer.translator.SemTranslator;

/**
 * The RuleParser is used to obtain entity objects after parsing the rule string. It contains
 * method that are used to parse a specific information in a rule like metadata, condition and 
 * target.   
 */
public class RuleParser {
	
	
	private final static Logger log = LoggerFactory.getLogger(RuleParser.class);
	
	/**
	 * Parses the chain rule string and creates a domain object against it.
	 * @param loader - ontology loader
	 * @param chRule - chain rule string
	 * @param fileName - filename of rule file
	 * @return ChainRule object
	 * @throws SWASException
	 */
	public static ChainRule parseChainRule(OntologyLoader loader,String chRule, String fileName) throws SWASException{

		log.info("parse chainRule called..");
		List<Condition> conditionList = new ArrayList<Condition>();
		
		Condition condition = null;
		ChainRule chainRule = null;
		try {

			chainRule = (ChainRule) EntityFactory.CHAIN_RULE.getObject();
			List<String> cRule = new ArrayList<String>();
			cRule = RuleFileParser.parseFile(chRule);

			for (int i = 0; i < cRule.size(); i++) {
				System.out.println(cRule.get(i));
				if (i == 0) {
					 System.out.println("****************Chain Rule Header Rule*************");
					 	MetaData md = (MetaData) EntityFactory.METADATA.getObject();
						md = RuleParser.parseMetaData(cRule.get(i), loader);
						chainRule.setMetaData(md);
						log.info("Rule MetaData Parsed..");

						String disruptiveAction = "";
						try {
							disruptiveAction = RuleParser.parseAction(cRule.get(i));
						} catch (SWASException e) {
							System.out.println(e.getMessage());
						}
						chainRule.setDisruptiveAction(disruptiveAction);
						log.info("Disruptive Action Parsed..");
						
						String phaseString = RuleParser.getPhase(cRule.get(i), loader);
						if( !phaseString.equals("") ){
							
							int phase = Integer.parseInt(phaseString);
							chainRule.setPhase(phase);
							log.info("Phase Action Parsed...: " + phase);
						
						}
						RuleGroup ruleGroup = (RuleGroup) EntityFactory.GROUP.getObject();
						ruleGroup = RuleParser.parseRuleGroup(fileName);
						chainRule.setRuleGroup(ruleGroup);
						log.info("Rule Group Parsed..");
						
						condition = RuleParser.parseCondtion(loader, cRule.get(i), chainRule);
						conditionList.add(condition);
						
						chainRule.setRuleTitle("Default Mod Security Rule");
						chainRule.setComment("File Name : " + fileName +  "  \"ModSecurity Rules\"");
						System.out.println("---------------Disruptive Action--------------");
						System.out.println(disruptiveAction);
						
				} else {
					
					System.out.println("****************Chain Rule Condition*************");
					System.out.println(cRule.get(i));
					condition = RuleParser.parseCondtion(loader,cRule.get(i), chainRule);
					conditionList.add(condition);
					log.info("Child Rule Parsed..");
				
				}
			}
			chainRule.setCondition(conditionList);

		} catch (IOException | InstantiationException | IllegalAccessException e) {

			e.printStackTrace();
		}

		return chainRule;

	}
	
	
	/**
	 * Parses the chain rule string and creates a domain object against it.
	 * @param loader - ontology loader
	 * @param ruleString - rule string
	 * @param fileName - rule file name
	 * @return Rule entity object
	 * @throws SWASException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public static Rule parseRule(OntologyLoader loader, String ruleString, String fileName) throws SWASException, InstantiationException, IllegalAccessException {
		
		log.info("Parse Rule Called..");
		Rule rule = null;
		String disruptiveAction = "";
		rule = (Rule) EntityFactory.RULE.getObject();

		MetaData md = (MetaData) EntityFactory.METADATA.getObject();
		md = RuleParser.parseMetaData(ruleString, loader);
		rule.setMetaData(md);
		log.info("Rule MetaData Parsed..");
		
		disruptiveAction = parseAction(ruleString);	
		rule.setDisruptiveAction(disruptiveAction);
		log.info("Disruptive Action Parsed..");
		
		String phaseString =getPhase(ruleString, loader);
		if( !phaseString.equals("") ){
			
			int phase = Integer.parseInt(phaseString);
			rule.setPhase(phase);
			log.info("Phase Action Parsed...: " + phase);
		
		}
		
		Map<String, SpecialCollection> spColMap = RuleParser.parseSpecialCol(loader, ruleString);
		rule.setSpecialCollection(spColMap);
		
		RuleGroup ruleGroup = (RuleGroup) EntityFactory.GROUP.getObject();
		ruleGroup = RuleParser.parseRuleGroup(fileName);
		rule.setRuleGroup(ruleGroup);
		log.info("Rule Group Parsed..");

		
		rule.setRuleTitle("Default Mod Security Rule");
		rule.setComment("File Name : " + fileName +  "  \"ModSecurity Rules\"");
		
		Condition con = (Condition) EntityFactory.CONDITION.getObject();
		con = RuleParser.parseCondtion(loader, ruleString, rule);
		rule.setCondition(con);
		
		return rule;

	}

	/**
	 * Parses the action string and extracts the disruptive action if exists.
	 * @param ruleString 
	 * @return disruptive action if exists otherwise returns null.
	 * @throws SWASException 
	 */
	public static String parseAction(String ruleString) throws SWASException {
		
		log.info("Parse Disruptive Action Called..");
		String actionStringDelimiter = "\"";
		int disruptiveActionCount = 0;
		String[] actionString = null;
		actionString = ruleString.split(actionStringDelimiter);
		String[] action = actionString[3].split(",");

		String str = null;
		String disruptiveAction = "";
		for (String disAction : action) {

			for (DisruptiveAction dis : DisruptiveAction.values()) {
				
				str = dis.toString();
				if (disAction.equals(str)) {
					disruptiveAction = str;
					disruptiveActionCount++;
				}
				
			}

		}
		
		if (disruptiveAction.equals(""))
		{
			log.info("Disruptive action missing..");
			ParsingErrors.errorList.add("Disruptive Action Missing in Rule");
		}
		
		if (disruptiveActionCount>1)
		{
			log.info("More then 1 Disruptive action..");
			ParsingErrors.errorList.add("More then 1 Disruptive action..");
		}

		return disruptiveAction;
	
	}
	
	/**
	 * Parses the rule string and extracts the rule phase from it.
	 * @param ruleString 
	 * @return rule phase if exists otherwise returns null.
	 */
	public static String getPhase(String ruleString, OntologyLoader loader) {
		
		log.info("Parse phase Called..");
		String actionStringDelimiter = "\"";
		String[] actionString = null;
		actionString = ruleString.split(actionStringDelimiter);
		String[] action = actionString[3].split(",");

		String phase = "";
		for (String phaseString : action) {
			
			if (phaseString.startsWith("phase")) {
				
				boolean check = Fetcher.getPhaseList(loader, "Phase", phaseString.split(":")[1]);
				if (check)
				{
					phase = phaseString.split(":")[1];
				}
				else {
					log.info("Phase value either greater then 5 or less then 1" + phaseString.split(":")[1]);
					ParsingErrors.errorList.add("Phase value either greater then 5 or less then 1" + phaseString.split(":")[1]);
				}
			}

		}

		return phase;
		
	}
	
	/**
	 * Parses a file name and creates a rule group object.
	 * @param fileName
	 * @return rule group object.
	 */
	public static RuleGroup parseRuleGroup(String fileName) {

		log.info("In  parseRuleGroup...");
		RuleGroup ruleGroup = null;
		try {
			
			ruleGroup = (RuleGroup) EntityFactory.GROUP.getObject();
			String[] nameChunks = null;
			String[] name = null;
			nameChunks = fileName.split("\\s");
			
			String transformedName = "";
			for ( int i = 0; i < nameChunks.length; i++ ) {
				
				if( i == nameChunks.length -1 ){
					transformedName += nameChunks[i];
				} else{
					transformedName += nameChunks[i] + "_";
				}
			
			}
			
			name = transformedName.split("\\.");
			transformedName = name[0];
			transformedName = transformedName.toUpperCase();
			
			log.info("Transformed Policy Name :" + transformedName);
			
			ruleGroup.setName(transformedName);
			ruleGroup.setDescription("Default Description");
		
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		
		return ruleGroup;
	}
	
	
	/**
	 * Parses the rule string and extracts the condition object from it.
	 * @param loader - ontology loader
	 * @param ruleString
	 * @param rule - rule's entity object
	 * @return Condition object
	 * @throws SWASException
	 */
	public static Condition parseCondtion(OntologyLoader loader, String ruleString, Entity rule) throws SWASException {
		
		log.info("parse Condition called.. :" + ruleString);
		Condition condition = null;
		try {
			List<String> opValue = new ArrayList<String>();
			condition = (Condition) EntityFactory.CONDITION.getObject();
			String varStringDelimiter = "\"";
			String[] varString = null;

			varString = ruleString.split(varStringDelimiter);

			String operator = varString[1];

			Variable variable = (Variable) EntityFactory.VARIABLE.getObject();
			variable = RuleParser.parseVariable(ruleString,loader);
			condition.setVariable(variable);
			log.info("variables parsed..");

			opValue = parseOperatorAndValue(loader, operator);

			condition.setOperator(opValue.get(0));
			log.info("Operator Parsed..");

			condition.setValue(opValue.get(1));
			log.info("Value Parsed..");
			
			if (opValue.get(2) == "true")
			{
				condition.setIsNegated(true);
			}
			else
			{
				condition.setIsNegated(false);
			}
			
			condition.setUserDefinedVariables( parseUserDefinedVariables(ruleString, rule) );

		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}

		return condition;

	}

	/**
	 * Parses the operator string and extracts operator and value from it.
	 * @param operatorString 
	 * @return List of operator and a value.
	 */
	private static List<String> parseOperatorAndValue(OntologyLoader loader, String operatorString) throws SWASException{

		log.info("parse operator and value called..");
		List<String> opValue = new ArrayList<String>();
		String operator = null;
		String value = null;
		int numericValue;
		String negation = null;
		if (operatorString.trim().startsWith("!"))
		{
			negation = "true";
			operatorString = operatorString.substring(1);
		}
		else 
		{
			negation = "false";
		}
		for (String fileOp : Fetcher.getFileOperators(loader)) {

			if (operatorString.startsWith("@" + fileOp))

			{
				operator = fileOp;
				value = operatorString.substring(4);
			}

		}
		for (String numericOp : Fetcher.getNumericOperators(loader)) {

			if (operatorString.startsWith("@" + numericOp))

			{
				operator = numericOp;
				value = operatorString.substring(4);
				try{
					numericValue =Integer.parseInt(value);
				}
				catch (NumberFormatException e)
				{
					log.info("integer value with numeric operator..");
					ParsingErrors.errorList.add("integer value with numeric operator..");
				}
				

			}

		}

		for (String regexOp : Fetcher.getRegexOperators(loader)) {

			if (operatorString.startsWith("@" + regexOp))

			{
				operator = regexOp;
				value = operatorString.substring(4);
			}
		}

		for (String stringOp : Fetcher.getStringOperators(loader)) {

			if (operatorString.startsWith("@" + stringOp))

			{
				operator = stringOp;
				value = operatorString.substring(4);
			}
		}

		for (String validationOp : Fetcher.getValidationOperators(loader)) {

			if (operatorString.startsWith("@" + validationOp))

			{
				operator = validationOp;
				value = operatorString.substring(4);
			}

		}

		if (operator == null) {
			operator = "rx";
			value = operatorString;
		}

		// System.out.println();
		// System.out.println("--------Operators-------------");
		// System.out.println("Operator: " + operator);
		// System.out.println("Value :" + value);
		// System.out.println();

		opValue.add(operator);
		value.replace(ElementMap.regx_quotes.toString(), ElementMap.regx_quotes.getMappedElement());
		opValue.add(value);
		opValue.add(negation);
		return opValue;

	}
	
	
	/**
	 * Parses the rule string and extracts list of user defined variables. 
	 * @param ruleString
	 * @param entity - rule's or chain rule's entity object.
	 * @return list of user defined objects.
	 */
	private static List<UserDefinedVariable> parseUserDefinedVariables(String ruleString, Entity entity){

		String ruleDelimiter = "\"";
		List<UserDefinedVariable> udvList = new ArrayList<UserDefinedVariable>();
		UserDefinedVariable udv = null;
		
		String[] actionString = ruleString.split( ruleDelimiter );
		String[] action = null;
		
		Map<String, SpecialCollection> spColMap = new HashMap<String, SpecialCollection>();
		
		if(entity instanceof Rule){
			
			Rule rule = (Rule) entity;
			if(rule.getSpecialCollection() != null){
				spColMap.putAll(rule.getSpecialCollection());
			}
			
		} else if(entity instanceof ChainRule){
			
			ChainRule chRule = (ChainRule) entity;
			if(chRule.getSpecialCollection() != null){
				spColMap.putAll(chRule.getSpecialCollection());
			}
			
		}
		
		if(actionString.length == 4){
			action = actionString[3].split(",");  
		}
		
		if(action != null){

			for (String varString : action) {
				
				if(varString.startsWith(ModSecFields.setvar.toString())){
					
					String transVar = varString.split(":")[1].replace("'", "");
					String[] keyValue = transVar.split("=");
					if(keyValue.length > 1){
						
						if(keyValue[0].startsWith(ModSecFields.tx.toString())){
							
							udv = toUserDefinedVar(keyValue[0], keyValue[1], ModSecFields.TX.toString());
							udvList.add(udv);
						
						} else if(keyValue[0].startsWith(ModSecFields.ip.toString()) || keyValue[0].startsWith(ModSecFields.IP.toString())){
							
							if(spColMap.containsKey(ModSecFields.IPCollection.toString())){
								udv = toUserDefinedVar(keyValue[0], keyValue[1], ModSecFields.IP.toString(), spColMap.get(ModSecFields.IPCollection.toString()) );
							} else{
								udv = toUserDefinedVar(keyValue[0], keyValue[1], ModSecFields.IP.toString());
							}
							udvList.add(udv);
						
						} else if(keyValue[0].startsWith(ModSecFields.session.toString()) || keyValue[0].startsWith(ModSecFields.SESSION.toString())){
							
							if(spColMap.containsKey(ModSecFields.SessionCollection.toString())){
								udv = toUserDefinedVar(keyValue[0], keyValue[1], ModSecFields.SESSION.toString(), spColMap.get(ModSecFields.SessionCollection.toString()) );
							} else{
								udv = toUserDefinedVar(keyValue[0], keyValue[1], ModSecFields.SESSION.toString());
							}
							udvList.add(udv);
						
						} else if(keyValue[0].startsWith(ModSecFields.global.toString())){
							
							if(spColMap.containsKey(ModSecFields.GlobalCollection.toString())){
								udv = toUserDefinedVar(keyValue[0], keyValue[1], ModSecFields.GLOBAL.toString(), spColMap.get(ModSecFields.GlobalCollection.toString()) );
							} else{
								udv = toUserDefinedVar(keyValue[0], keyValue[1], ModSecFields.GLOBAL.toString());
							}
							udvList.add(toUserDefinedVar(keyValue[0], keyValue[1], ModSecFields.GLOBAL.toString()));
						
						}
						
					} else{
						
						log.info("user defined variable value is not set");
						ParsingErrors.errorList.add("user defined variable value is not set");
					
					}
					
				} else if(varString.startsWith(ModSecFields.setenv.toString())){
					
					String transVar = varString.split(":")[1].replace("'", "");
					String[] keyValue = transVar.split("=");
					if(keyValue.length > 1){
						udvList.add(toUserDefinedVar(keyValue[0], keyValue[1], ModSecFields.ENV.toString()));
					} else{
						log.info("user defined variable value is not set");
						ParsingErrors.errorList.add("user defined variable value is not set");
					}
					
				}
				
			}
			
		}
		return udvList;
		
	}
	
	/**
	 * Converts the string arguments into user defined variable entity object.
	 * @param key - string
	 * @param value - string
	 * @param type - special collection type
	 * @return user defined variable object
	 */
	private static UserDefinedVariable toUserDefinedVar(String key, String value, String type){
		
		UserDefinedVariable udv = null;
		try{
			
			key = key.replaceAll("\'", "").substring(3);
			udv= (UserDefinedVariable) EntityFactory.USER_DEFINED_VAR.getObject();
			String openRgx = ElementMap.var_open.getMappedElement().toCharArray()[0] 
					+ "\\" + ElementMap.var_open.getMappedElement().toCharArray()[1];
			
			String appendRgx = ElementMap.op_append.getMappedElement().toCharArray()[0]
					+ "\\" + ElementMap.op_append.getMappedElement().toCharArray()[1]; 
			
			key = key.replaceAll(openRgx, ElementMap.var_open.toString());
			key = key.replaceAll("\\"+ElementMap.var_close.getMappedElement(), ElementMap.var_close.toString());
			key = key.replaceAll(ElementMap.fwdslash.getMappedElement(), ElementMap.fwdslash.toString());
				
			value =  "=" + value;
			value = value.replaceAll(appendRgx, ElementMap.op_append.toString());
			value = value.replaceAll(ElementMap.op_equal.getMappedElement(), ElementMap.op_equal.toString());
			
			log.info(" Key :" + key );
			log.info(" Value :" + value );
			 
			udv.setName(  type + "." + key );
			udv.setValue( value );
			
		}catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		
		return udv;
		
	}
	
	/**
	 * Converts the string arguments into user defined variable entity object.
	 * @param key - string
	 * @param value - string
	 * @param type - special collection type
	 * @param spCol - special collection object
	 * @return
	 */
	private static UserDefinedVariable toUserDefinedVar(String key, String value, String type, SpecialCollection spCol){
		
		UserDefinedVariable udv = null;
		try{
			
			key = key.replaceAll("\'", "").substring(3);
			udv= (UserDefinedVariable) EntityFactory.USER_DEFINED_VAR.getObject();
			String openRgx = ElementMap.var_open.getMappedElement().toCharArray()[0] 
					+ "\\" + ElementMap.var_open.getMappedElement().toCharArray()[1];
			
			String appendRgx = ElementMap.op_append.getMappedElement().toCharArray()[0]
					+ "\\" + ElementMap.op_append.getMappedElement().toCharArray()[1]; 
			
			key = key.replaceAll(openRgx, ElementMap.var_open.toString());
			key = key.replaceAll("\\"+ElementMap.var_close.getMappedElement(), ElementMap.var_close.toString());
			key = key.replaceAll(ElementMap.fwdslash.getMappedElement(), ElementMap.fwdslash.toString());
				
			value =  "=" + value;
			value = value.replaceAll(appendRgx, ElementMap.op_append.toString());
			value = value.replaceAll(ElementMap.op_equal.getMappedElement(), ElementMap.op_equal.toString());
			
			log.info(" Key :" + key );
			log.info(" Value :" + value );
			 
			udv.setName(  type + "." + key );
			udv.setValue( value );
			udv.setVariableOf(spCol);
			
		}catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		
		return udv;
		
	}
	
	/**
	 * Parse the action string and extracts rule metadata from it.
	 * @param actionString 
	 * @param loader - ontology loader
	 * @return meta data object
	 * @throws SWASException
	 */
	public static MetaData parseMetaData(String actionString, OntologyLoader loader) throws SWASException{
		
		log.info("Parse MetaData Called..");
		MetaData metaData = null;
		;
		try {
			metaData = (MetaData) EntityFactory.METADATA.getObject();
			String metaDataStringDelimiter = "\"";
			String[] metaDataString = null;

			metaDataString = actionString.split(metaDataStringDelimiter);
			// System.out.println("----------Action String-------------");
			// System.out.println(metaDataString[3]);

			String[] md = metaDataString[3].split(",");
			List<String> tagList = new ArrayList<String>();
			 System.out.println("------Meta Data------");

			 int revCount = 0;
			 int msgCount = 0;
			 int idCount =0;
			 int sevCount = 0;
			 
			for (String string : md) {
				//----------Revision------------
				if (string.startsWith("rev")) {
					revCount++;
					String[] rev = string.split(":");
					rev[1] = rev[1].replace("'", "");
					System.out.println(string);
					
					if (revCount > 1)
					{
						log.info("More then 1 Revision..");
						ParsingErrors.errorList.add("More then 1 Revision..");
					}

					if (rev.length != 2 )
					{
						log.info("Revision value missing..");
						ParsingErrors.errorList.add("Revision value missing..");
					}
					else
					{
						metaData.setRevision(rev[1]);
					}
	
				}
				//----------------ID-------------------------
				if (string.startsWith("id")) {
					String[] id = string.split(":");
					id[1] = id[1].replace("'", ""); 
					System.out.println(string);
					if (idCount > 1)
					{
						log.info("More then 1 ID's..");
						ParsingErrors.errorList.add("More then 1 ID's..");
					}
					
					if (id.length != 2 )
					{
						log.info("ID value missing..");
						ParsingErrors.errorList.add("ID value missing..");
					}
					else
					{
					metaData.setRuleID(id[1]);
					idCount++;
					}
					
					
				}
				//-----------------------Message------------------
				if (string.startsWith("msg")) {
					String[] msg = string.split(":");
					
					msg[1] = msg[1].replace("'", "");
					System.out.println(string);
					msgCount++;
					
					if (msgCount > 1)
					{
						log.info("More then 1 Message..");
						ParsingErrors.errorList.add("More then 1 Message..");
					}
					
					if (msg.length != 2 )
					{
						log.info("Message value missing..");
						ParsingErrors.errorList.add("Message value missing..");
					}
					else{
					metaData.setMessage(msg[1]);
					}
					
				//---------------------tag--------------------
				}
				if (string.startsWith("tag")) {
					
					String tag = string.substring(4).trim();
					tag = tag.replace("'", "");
					System.out.println(string);
					
					if (string.split(":").length < 2 )
					{
						log.info("Tag value missing..");
						ParsingErrors.errorList.add("Tag value missing..");
					}
					tagList.add(tag);
				}
				metaData.setTag(tagList);
				
				//-----------------------------Severity--------------------
				if (string.startsWith("severity")) {
					
					String[] sev = string.split(":");
					sev[1] = sev[1].replace("'", "");
					log.info(string);
					if (sevCount > 1 )
					{
						log.info("More then 1 Severity's..");
						ParsingErrors.errorList.add("More then 1 Severity's..");
					}
					
					if (sev.length != 2 )
					{
						log.info("Severity value missing..");
						ParsingErrors.errorList.add("Severity value missing..");
					}
					
					boolean check = Fetcher.getSeverityList(loader, "Severity", sev[1]);
					 if (check)
					 {
					metaData.setSeverity(sev[1]);
					 }
					 else
					 {
						 //log.info(("Severity value more then 7 or less then 1" + sev[1]);
						ParsingErrors.errorList.add("Severity value more then 7 or less then 1" + sev[1]);
					 }
				}
			}
			

		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}

		return metaData;
	}
	
	/**
	 * Parses the rule string and extracts rule's target or list variables.
	 * @param ruleString 
	 * @param loader - ontology loader
	 * @return variable's entity object.
	 * @throws SWASException
	 */
	public static Variable parseVariable(String ruleString , OntologyLoader loader) throws SWASException{

		log.info("Parse Variable Called..");
		Variable variable = null;
		UserDefinedVariableHandler udvHndler = new UserDefinedVariableHandler(loader);
		try {
			
			variable = (Variable) EntityFactory.VARIABLE.getObject();
			List<VariableExpression> varList = new ArrayList<VariableExpression>();
			List<CollectionExpression> colList = new ArrayList<CollectionExpression>();
			List<String> transList = new ArrayList<String>();
			
			String varStringDelimiter = "\"";
			String varDelimiter = "\\|";

			String[] varString = null;
			String[] vars = null;

			varString = ruleString.split(varStringDelimiter);
			String variables = varString[0];
			String actions = "";
			vars = variables.split(varDelimiter);

			log.info("-----------Variables---------" );
			for (String var : vars) {
				
				var = normaliseVariable(var);
				log.info(" Varibale :" + var);
				if( var.startsWith(ElementMap.ampersand.getMappedElement()) || var.startsWith(ElementMap.negation.getMappedElement())) {
					
					log.info("Unary op if");
					if( var.contains(":") ){
						
						if(var.substring(1).startsWith(ModSecFields.TX.toString())){
							
							boolean setVarCheck = false;
							for(Entity entity:udvHndler.getAll()){
								
								UserDefinedVariable udv= (UserDefinedVariable) entity;
								log.info("Var form ontology :" + udv.getName().toUpperCase());
								if(udv.getName().toUpperCase().equals(ModSecFields.TX + "." +var.split(":")[1])){
									
									VariableExpression varExp = SemTranslator.toVariableExpBeans("" + var.toCharArray()[0], ModSecFields.TX + "." + var.split(":")[1].toLowerCase());
									varList.add(varExp);
									setVarCheck = true;
								
								} 
								
							}
							
							try{
								
								Integer.parseInt(var.split(":")[1]);
								VariableExpression varExp = SemTranslator.toVariableExpBeans("" + var.toCharArray()[0], ModSecFields.TX + "." + var.split(":")[1]);
								varList.add(varExp);
								setVarCheck = true;
								
							}catch (NumberFormatException e) {
								log.info("Not a number : " + var.split(":")[1]);
							}
							
							
							if(!setVarCheck){
								
								log.info("throw parsing exception for : set var does not Exist \"" + var.substring(1) );
								ParsingErrors.errorList.add("set var does not Exist \"" + var.substring(1) );

							}
							
						} else{
							
							String collection = var.substring(1).split(":")[0];
							String elements = var.substring(1).split(":")[1];
							for(String element : elements.split(varDelimiter)){
							
								if(element.trim().equals(ElementMap.all_xml_elements.getMappedElement())){
									element = ElementMap.all_xml_elements.toString();
								}
								
								CollectionExpression colExp = SemTranslator.toCollectionExpBeans("" + var.toCharArray()[0], 
										collection, element );
								
								if (!Fetcher.isCollectionIndividual(loader, collection))
								{
									log.info(collection + " : Collection does not exist..");
									ParsingErrors.errorList.add(collection + "Collection does not exist..");
								}
								colList.add(colExp);
								
							}
							
						}
						
					} else{
						
						if(Fetcher.isCollectionIndividual(loader, var.substring(1))){
							
							CollectionExpression colExp = SemTranslator.toCollectionExpBeans("" + var.toCharArray()[0], var.substring(1).split(":")[0] );
							colList.add(colExp);
							
						} else if (Fetcher.isStandardVariableIndividual(loader, var.substring(1)))
						{
							VariableExpression varExp = SemTranslator.toVariableExpBeans("" + var.toCharArray()[0], var.substring(1));
							varList.add(varExp);
							
						} else{
							log.info(var.substring(1) + "Neither Standard nor collection");
							ParsingErrors.errorList.add(var.substring(1) + "Variable does not exist..");
						}
					
					}
					
				} else{ 
					
					if( var.contains(":") ){
						
						if(var.startsWith(ModSecFields.TX.toString())){
							
							boolean setVarCheck = false;
							for(Entity entity:udvHndler.getAll()){
								
								UserDefinedVariable udv= (UserDefinedVariable) entity;
								log.info("Var form ontology :" + udv.getName().toUpperCase());
								if(udv.getName().toUpperCase().equals(ModSecFields.TX + "." +var.split(":")[1])){
									
									VariableExpression varExp = SemTranslator.toVariableExpBeans("", ModSecFields.TX + "." + var.split(":")[1].toLowerCase());
									varList.add(varExp);
									
									setVarCheck = true;	
								} 
								
							}
							
							try{
								
								Integer.parseInt(var.split(":")[1]);
								VariableExpression varExp = SemTranslator.toVariableExpBeans("", ModSecFields.TX + "." + var.split(":")[1].toLowerCase());
								varList.add(varExp);

								setVarCheck = true;
								
							}catch (NumberFormatException e) {
								log.info("Not a number : " + var.split(":")[1]);
								setVarCheck = false;
							}
							
							if(!setVarCheck){
								
								log.info("throw parsing exception for : set var does not Exist \"" + var );
								ParsingErrors.errorList.add("set var does not Exist \"" + var);
							}
							
						} else{
							
							String collection = var.split(":")[0];
							String elements = var.split(":")[1];
							
							for(String element:elements.split(varDelimiter)){
								if(element.trim().equals(ElementMap.all_xml_elements.getMappedElement())){
									element = ElementMap.all_xml_elements.toString();
								}
								
								CollectionExpression colExp = SemTranslator.toCollectionExpBeans("", collection , element);
								colList.add(colExp);
								
								if (!Fetcher.isCollectionIndividual(loader, var.split(":")[0]))
								{
									log.info(var.split(":")[0] + "Collection does not exist..");
									ParsingErrors.errorList.add(var.split(":")[0] + "Collection does not exist..");
								}
							}
							
						}
							
						
					} else{
						
						if(Fetcher.isCollectionIndividual(loader, var)){
							
							CollectionExpression colExp = SemTranslator.toCollectionExpBeans("", var);
							colList.add(colExp);
							
						} else if (Fetcher.isStandardVariableIndividual(loader, var)){
							
							VariableExpression varExp = SemTranslator.toVariableExpBeans("", var);
							varList.add(varExp);
						
						}else {
							log.info(var + "Neither Standard nor collection");
							ParsingErrors.errorList.add(var + " : Variable does not exist..");

						}
						
					}
					
				}
				
			}//---------------

			//variable.setVariables(varList);

			// -----parsing transformations
			
			if(varString.length == 4){
				actions = varString[3];
			}
			
			transList = parseTransformations(actions, loader);
			variable.setTransformation(transList);
			variable.setCollectionExpressions(colList);
			variable.setVariableExpressions(varList);
			
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}

		return variable;

	}

	/**
	 * Parses an action string and extracts list of transformations from it.
	 * @param actionString 
	 * @return List<String> - list of transfromations
	 * @throws SWASException
	 */
	private static List<String> parseTransformations(String actionString, OntologyLoader loader) throws SWASException {

		List<String> transList = new ArrayList<String>();
		String[] trans = actionString.split(",");
		for (String t : trans) {
			if (t.startsWith("t:")) {
				String[] transf = t.split(":");
				if (transf.length !=2)
				{
					log.info("Transformation value missiing");
					ParsingErrors.errorList.add("Transformation value missiing");
				}
				else
				{	
					transf[1] = transf[1].replace("'", "");
					transf[1] = transf[1].trim();
					boolean check = Fetcher.getTranformationList(loader, "Transformation", transf[1]);
					if (check)
					{
					transList.add(transf[1]);
					}
					else
					{
						log.info("Transformation Does not exists" + transf[1]);
						ParsingErrors.errorList.add("Transformation Does not exists" + transf[1]);
					}
				}
				
			}
		}
		
		return transList;

	}
	
	/**
	 * Normalizes the variable expression.
	 * @param variable like RRQUEST_HEADERS:'(User-Agent|Cache-Control)'
	 * @return normalized variable.
	 */
	public static String normaliseVariable(String variable){
		
		variable = variable.replace("'", "");
		variable = variable.replace("(", "");
		variable = variable.replace(")", "");
		variable = variable.replace("/", "");
		variable = variable.trim();
		variable = variable.replace(" ", "");
		return variable;
	}
	
	/**
	 * Parses the rule string and extracts special collection used in the rule from it.
	 * @param loader - ontology loader
	 * @param ruleString
	 * @return map of special collection
	 * @throws SWASException
	 */
	public static Map<String,SpecialCollection> parseSpecialCol(OntologyLoader loader, String ruleString) throws SWASException {
		
		log.info("parse special collection called.. :" + ruleString);
		Map<String, SpecialCollection> spColMap = new HashMap<String, SpecialCollection>();
		
		SpecialCollection spCol = null;
		String varStringDelimiter = "\"";
		String[] varString = null, actions = null;
		
		varString = ruleString.split(varStringDelimiter);
		log.info("recieved action :" + varString[3]);
		actions = varString[2].split(",");
		
		for(String action: actions){
			
			if(action.startsWith(ModSecFields.initcol.toString())){
				
				String keyValue = action.split(":")[1];
				if(keyValue.split("=")[0].trim().equals(ModSecFields.IP.toString())){
					
					spCol = SemTranslator.toSpecialColection(ModSecFields.IPCollection.toString(), keyValue.split("=")[1].trim());
					spColMap.put(ModSecFields.IPCollection.toString(), spCol);
					
				} else if(keyValue.split("=")[0].trim().equals(ModSecFields.GLOBAL.toString())){
					
					spCol = SemTranslator.toSpecialColection(ModSecFields.GlobalCollection.toString(), keyValue.split("=")[1].trim());
					spColMap.put(ModSecFields.GlobalCollection.toString(), spCol);
					
				} else if(keyValue.split("=")[0].trim().equals(ModSecFields.RESOURCE.toString())){
					
					spCol = SemTranslator.toSpecialColection(ModSecFields.ResourceCollection.toString(), keyValue.split("=")[1].trim());
					spColMap.put(ModSecFields.ResourceCollection.toString(), spCol);
					
				} else{
					
					ParsingErrors.errorList.add("No collection initialized while Init Column exists");
				}
			} else if(action.startsWith(ModSecFields.setuid.toString())){
				
				//ParsingErrors.errorList.add("Not more than 1 \"setsid\" are allowed in a rule");
				String value = action.split(":")[1];
				spCol = SemTranslator.toSpecialColection(ModSecFields.UserCollection.toString(), value.trim());
				spColMap.put(ModSecFields.UserCollection.toString(), spCol);
				
			} else if(action.startsWith(ModSecFields.setsid.toString())){
				
				//ParsingErrors.errorList.add("Not more than 1 \"setuid\" are allowed in a rule");
				String value = action.split(":")[1];
				spCol = SemTranslator.toSpecialColection(ModSecFields.SessionCollection.toString(), value.trim());
				spColMap.put(ModSecFields.SessionCollection.toString(), spCol);
				
			}
			
		}

		return spColMap;

	}
}
