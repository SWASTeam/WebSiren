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
package net.swas.explorer.oh.handler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.jsp.JspWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;


import net.swas.explorer.oh.fields.Severity;
import net.swas.explorer.ec.CollectionExpression;
import net.swas.explorer.ec.Element;
import net.swas.explorer.ec.UserDefinedVariable;
import net.swas.explorer.ec.Variable;
import net.swas.explorer.ec.VariableExpression;
import net.swas.explorer.ecf.Entity;
import net.swas.explorer.ecf.EntityFactory;
import net.swas.explorer.oh.fields.ElementMap;
import net.swas.explorer.oh.fields.ModSecFields;
import net.swas.explorer.oh.fields.RulePhase;
import net.swas.explorer.oh.lo.OntologyLoader;
import net.swas.explorer.parser.ParsingErrors;
import net.swas.explorer.parser.RuleParser;
import net.swas.explorer.translator.SemTranslator;

/**
 * The fetcher class contains all the utility methods that accomodate classes for fetching 
 * custom data from ontology. It contains methods that hides the complexity of communication 
 * with ontology and fetch data for other classes.
 */
public class Fetcher {

	private final static Logger log = LoggerFactory.getLogger(Fetcher.class);

	
	/**
	 * Get collection of individual names from ontology on the basis of class name provided to it. 
	 * @param loader - ontology loader
	 * @param className - concept name
	 * @return List of individual names.
	 */
	@SuppressWarnings("unchecked")
	private static List<String> getOntCollection(OntologyLoader loader,
			String className) {

		////log.info(" getOntCollection called...");
		String NS = loader.getConfiguration().getRuleEngineNameSpace()
				+ OntologyHandler.NS_POSTFIX;
		List<String> colList = new ArrayList<String>();
		OntModel model = loader.getModel();

		//log.info(" ClassName : " + className + " : Name Space :" + NS);

		OntClass colClass = model.createClass(NS + className);
		ExtendedIterator<Individual> colInds = (ExtendedIterator<Individual>) colClass
				.listInstances();
		
		while (colInds.hasNext()) {

			Individual colInd = colInds.next();
			colList.add(colInd.getLocalName());

		}

		return colList;

	}
	
	/**
	 * Checks whether a variable name is a modsecurity collection or not. 
	 * @param loader -ontology loader
	 * @param varName - variable name
	 * @return true if variable name is a collection otherwise returns false.
	 */
	public static boolean isCollectionIndividual(OntologyLoader loader, String varName) {

		////log.info("isCollectionIndividual get called...");
		
		boolean check = false;
		String NS = loader.getConfiguration().getRuleEngineNameSpace()
				+ OntologyHandler.NS_POSTFIX;
		OntModel model = loader.getModel();
		
		////log.info("Variable Namespace :" + NS + varName);
		try{
			Individual varInd = model.getIndividual(NS + varName);
			
			if(varInd.getOntClass().getLocalName().equals("CollectionVariable")){
				check = true;
			}
		
			ExtendedIterator<OntClass> supClasses = varInd.getOntClass().listSuperClasses();
			while(supClasses.hasNext()){
				
				OntClass supCls = (OntClass) supClasses.next();
				////log.info("Super Class : " + supCls.getLocalName());
				if(supCls.getLocalName().equals("CollectionVariable")){
					check = true;
				}
			
			}
		} catch(NullPointerException e){}
		
		return check;
	}
	
	/**
	 * Checks whether a variable name is a modsecurity's standard variable or not. 
	 * @param loader - ontology loader
	 * @param varName - variable name
	 * @return true if variable name is a standard variable otherwise returns false.
	 */
	public static boolean isStandardVariableIndividual(OntologyLoader loader, String varName){
		
		//log.info("isStandardVariableIndividual called...");
		boolean check = false;
		String NS = loader.getConfiguration().getRuleEngineNameSpace()
				+ OntologyHandler.NS_POSTFIX;
		OntModel model = loader.getModel();
		
		////log.info("Variable NameSpace : " + NS +varName);
		try{
			
			Individual varInd = model.getIndividual(NS + varName);
			ExtendedIterator<OntClass> supclasses = varInd.getOntClass().listSuperClasses();
			
			if (varInd.getOntClass().getLocalName().equals("StandardVariable")){
				check = true;
			}
			
			while (supclasses.hasNext())
			{
				OntClass supcls = (OntClass) supclasses.next();
				////log.info("Super class : " + supcls.getLocalName());
				if (supcls.getLocalName().equals("StandardVariable")){
					check = true;
				}
			}
			
		} catch(NullPointerException e){}
		
		return check;
	}

	/**
	 * Gets modesecurity request collection
	 * @param loader - ontology loader
	 * @return list of request collection
	 */
	public static List<String> getRequestCollection(OntologyLoader loader) {

		////log.info(" getRequestCollection called...");
		return getOntCollection(loader, "RequestCollection");

	}

	/**
	 * Gets modesecurity response collection
	 * @param loader - ontology loader
	 * @return list of response collection
	 */
	public static List<String> getResponseCollection(OntologyLoader loader) {

		////log.info(" getResponseCollection called...");
		return getOntCollection(loader, "ResponseCollection");

	}
	
	/**
	 * Gets modesecurity collections
	 * @param loader - ontology loader
	 * @return list of collections
	 */
	public static List<String> getCollections(OntologyLoader loader) {

		////log.info(" getOperators called...");
		return getOntCollection(loader, "CollectionVariable");

	}

	/**
	 * Gets list of parsing flags.
	 * @param loader - ontology loader
	 * @return list of parsing flags
	 */
	public static List<String> getParsingFlags(OntologyLoader loader) {

		////log.info(" getParsingFlags called...");
		return getOntCollection(loader, "ParsingFlags");

	}

	/**
	 * Gets list of standard variables.
	 * @param loader - ontology loader
	 * @return list of standard variables.
	 */
	public static List<String> getStandardVariables(OntologyLoader loader) {

		////log.info(" getStandardVariables called...");
		return getOntCollection(loader, "StandardVariable");

	}

	/**
	 * Gets list of request variables
	 * @param loader - ontology loader
	 * @return list of request variables
	 */
	public static List<String> getRequestVariables(OntologyLoader loader) {

		////log.info(" getRequestVariables called...");
		return getOntCollection(loader, "RequestVariables");

	}

	/**
	 * Gets list of response variables
	 * @param loader - ontology loader
	 * @return list of response variables
	 */
	public static List<String> getResponseVariables(OntologyLoader loader) {

		////log.info(" getResponseVariables called...");
		return getOntCollection(loader, "ResponseVariables");

	}

	/**
	 * Gets list of server variables
	 * @param loader - ontology loader
	 * @return list of server variables
	 */
	public static List<String> getServerVariables(OntologyLoader loader) {

		////log.info(" getServerVariables called...");
		return getOntCollection(loader, "ServerVariables");

	}

	/**
	 * Gets list of time variables
	 * @param loader - ontology loader
	 * @return list of time variables
	 */
	public static List<String> getTimeVariables(OntologyLoader loader) {

		////log.info(" getTimeVariables called...");
		return getOntCollection(loader, "TimeVariables");

	}

	/**
	 * Gets list of transformations.
	 * @param loader - ontology loader
	 * @return list of transformtions
	 */
	public static List<String> getTransformations(OntologyLoader loader) {

		////log.info(" getTransformations called...");
		return getOntCollection(loader, "Transformation");

	}

	/**
	 * Gets list of file operator
	 * @param loader - ontology loader
	 * @return list of file operator
	 */
	public static List<String> getFileOperators(OntologyLoader loader) {

		////log.info(" getFileOperators called...");
		return getOntCollection(loader, "FileOperator");

	}

	/**
	 * Gets list of numeric operator
	 * @param loader - ontology loader
	 * @return list of numeric operator
	 */
	public static List<String> getNumericOperators(OntologyLoader loader) {

		////log.info(" getNumericOperators called...");
		return getOntCollection(loader, "NumericOperator");

	}

	/**
	 * Gets list of regex operator
	 * @param loader - ontology loader
	 * @return list of regex operator
	 */
	public static List<String> getRegexOperators(OntologyLoader loader) {

		////log.info(" getRegexOperators called...");
		return getOntCollection(loader, "RegexOperator");

	}

	/**
	 * Gets list of String operators
	 * @param loader - ontology loader
	 * @return list of String operators
	 */
	public static List<String> getStringOperators(OntologyLoader loader) {

		////log.info(" getStringOperators called...");
		return getOntCollection(loader, "StringOperator");

	}

	/**
	 * Gets list of validation operator
	 * @param loader - ontology loader
	 * @return list of validation operator
	 */
	public static List<String> getValidationOperators(OntologyLoader loader) {

		////log.info(" getValidationOperators called...");
		return getOntCollection(loader, "ValidationOperator");

	}

	/**
	 * Gets list of operators
	 * @param loader - ontology loader
	 * @return list of operators
	 */
	public static List<String> getOperators(OntologyLoader loader) {

		////log.info(" getOperators called...");
		return getOntCollection(loader, "Operator");

	}
	
	/**
	 * Gets list of builtin variables
	 * @param loader - ontology loader
	 * @return list of builtin variables
	 */
	public static List<String> getBuiltInVariables(OntologyLoader loader) {

		////log.info(" getRequestVariables called...");
		return getOntCollection(loader, "BuiltinVariables");

	}

	/**
	 * Gets the list of classes that related to rule at any level. 
	 * @param loader
	 * @return list of ontClass object.
	 */
	public static List<OntClass> getRuleConmpositionClasses(OntologyLoader loader) {

		////log.info("getRuleSubClasses called");
		List<OntClass> clsList = new ArrayList<OntClass>();
		OntologyHandler handler = new RuleHandler(loader);
		OntModel model = loader.getModel();
		
		for (RelationalModel rModel : handler.getRelationalModels().values()) {

			if (rModel.getRange() != null) {
				
				if(rModel.getRange().equals("Rule")){
					continue;
				}
				
				boolean isClsRangeExist = true;
				//log.info("Range : " + rModel.getRange() + ":  Range URI : " + rModel.getRangeURI());
				isClsRangeExist = getCompositionClasses(loader, clsList, rModel.getRange());
				
				if(isClsRangeExist){
					clsList.add(model.createClass(rModel.getRangeURI()));
				}

			}

		}

		return clsList;

	}

	/**
	 * A Recursive method for getting ont classes from all levels of semantic rule.
	 * @param loader - ontolgy loader
	 * @param clsList - ontology class list
	 * @param concept
	 * @return true if there exists object property otherwise false.
	 */
	public static boolean getCompositionClasses(OntologyLoader loader, 
			List<OntClass> clsList, String concept) {

		boolean isClsRangeExist = true;
		//log.info("getRuleSubClasses called");
		OntModel model = loader.getModel();
		
		for(RelationalModel subModel: OntologyHandler.getRelationalModels(loader, concept).values()){
			
			if (subModel.getRange() != null) {
				
				//log.info("Range : " + subModel.getRange() + ":  Range URI : " + subModel.getRangeURI());
				//To avoid infinite loop
				if(subModel.getRange().equals("Element")){
					continue;
				}
				
				if(getCompositionClasses(loader, clsList, subModel.getRange())){
					
					OntClass cls = model.createClass(subModel.getRangeURI());
					clsList.add(cls);
					
					if(cls.getLocalName().equals("Expression")){
						
						ExtendedIterator<OntClass> subClasses = cls.listSubClasses();
						while(subClasses.hasNext()){
							
							OntClass subClass = subClasses.next();
							if(getCompositionClasses(loader, clsList, subClass.getLocalName())){
								clsList.add(subClass);
							}
							
						}
						
					}
				}
				isClsRangeExist = false;
			}
				
		}

		return isClsRangeExist;

	}
	
	/**
	 * This function is for generating recursive elements of multiple accordion of perspective based search.
	 * @param ontClass
	 * @param out Jspwriter used to write the  reponse
	 * @param rootClass - root class of the hierarchy
	 * @throws IOException
	 */
	public static void getOntClassHierarchy(OntClass ontClass, JspWriter out,
			boolean rootClass) throws IOException {
		
		@SuppressWarnings("rawtypes")
		ExtendedIterator listIndividuals = ontClass.listInstances(true);

		if (ontClass.hasSubClass() || listIndividuals.hasNext()) {

			Iterator<OntClass> subClasses = ontClass.listSubClasses(true);

			// creating URIModel of the individuals for traversing
			List<URIModel> list = new ArrayList<URIModel>();
			while (listIndividuals.hasNext()) {

				URIModel uv = new URIModel();
				OntResource resource = (OntResource) listIndividuals.next();
				Individual ind = resource.asIndividual();
				if(ind.getOntClass().getLocalName().equals("Phase")){
					
					for(RulePhase ph:RulePhase.values()){
						
						if(ph.getIndex() == Integer.parseInt(ind.getLocalName().split("\\.")[1])){
							uv.setURI(resource.getURI());
							uv.setLabel(ph.toString());
							list.add(uv);
						}
						
					}
					
				} else if(ind.getOntClass().getLocalName().equals("Severity")){
					
					for(Severity sev:Severity.values()){
						
						if(sev.getValue().equals(ind.getLocalName().split("\\.")[1])){
							uv.setURI(resource.getURI());
							uv.setLabel(sev.toString());
							list.add(uv);
						}
						
					}
					
				} else{
					uv.setURI(resource.getURI());
					uv.setLabel(resource.getLocalName());
					list.add(uv);
				}
				

			}

			// if rootClass then add the class=topnav2 to apply css
			if (rootClass) {
				out.print("<ul id=\"facetHierarchy\" class=\"topnav\" >");
			} else {
				out.print("<ul id=\"facetHierarchy\" class=\"nested\" >");
			}

			if ((list.size()) > 0) {
				for (int l = 0; l < list.size(); l++) {
					URIModel individual = list.get(l);

					// print the top level facet's individual
					out.print("<li> <a id=\"facetIndividual\" href=\"#\" type=\"action\" data-value=\""
							+ individual.getLabel()
							+ "\" >"
							+ individual.getLabel() + "</a></li>");

				}
			}

			while (subClasses.hasNext()) {

				OntClass cls = (OntClass) subClasses.next();
				out.print("<li> <a id=\"parentFacetValue\" href=\"#\" data-value=\""
						+ cls.getLocalName()
						+ "\">"
						+ cls.getLocalName()
						+ "</a>");
				getOntClassHierarchy(cls, out, false);
				out.print("</li>");

			}
			out.print("</ul>");

		}// end of top level if
	}

	/**
	 * This function is to extract concept element map from queryString coming
	 * from facet post request.
	 * @param queryString
	 * @return map of mapped values of phase and severity
	 */
	public static Map<String, List<String>> getConceptElementMap(
			String queryString) {

		Map<String, List<String>> conceptElemMap = new HashMap<String, List<String>>();
		List<String> elemList = null;

		for (String val : queryString.split(",")) {

			//log.info("Value:" + val);
			if (!val.equals("undefined")) {

				String concept = val.split("::")[0];
				String elem = val.split("::")[1];
				
				//to map phase and sevrity local names to element
				if(concept.equals("Phase")){
					
					for(RulePhase ph:RulePhase.values()){
						
						if(ph.toString().equals(elem)){
							elem = "Phase." + ph.getIndex();
						}
						
					}
					
				}
				
				if(concept.equals("Severity")){
					
					for(Severity sev:Severity.values()){
						
						if(sev.toString().equals(elem)){
							elem = "Severity." + sev.getValue();
						}
						
					}
					
				}

				if (conceptElemMap.containsKey(concept)) {

					elemList = conceptElemMap.get(concept);
					elemList.add(elem);
					conceptElemMap.put(concept, elemList);

				} else {

					elemList = new ArrayList<String>();
					elemList.add(elem);
					conceptElemMap.put(concept, elemList);
				}

			}
		}

		return conceptElemMap;

	}
	
	/**
	 * This function is to extract element List from queryString coming
	 * from facet post request.
	 * @param queryString
	 * @return list of string
	 */
	public static List<String> getFacetElementList( String queryString ) {

		List<String> elemList = new ArrayList<String>();

		for (String val : queryString.split(",")) {

			//log.info("Value:" + val);
			if (!val.equals("undefined")) {

				//String concept = val.split("::")[0];
				
				String elem = val.split("::")[1];
				elemList.add(elem);

			}
		}

		return elemList;

	}
	
	/**
	 * Checks the list of phases with phase coming as an input. 
	 * @param loader - ontology loader
	 * @param className 
	 * @param phase - input phase
	 * @return true if phase exists otherwise returns false.
	 */
	public static boolean getPhaseList(OntologyLoader loader, String className, String phase)
	{
		log.info("getPhaseList called...");
		boolean check = false;
		List<String> phaseList = getOntCollection(loader, className);
		for (String ph : phaseList) {
			if (phase.equals(ph.split("\\.")[1]))
			{
				check = true;
			}
		}
		return check;
	}
	
	/**
	 * Checks the list of severity levels with severity coming as an input. 
	 * @param loader - ontology loader
	 * @param className 
	 * @param severity - input severity
	 * @return true if severity exists otherwise returns false.
	 */
	public static boolean getSeverityList(OntologyLoader loader, String className, String severity)
	{
		log.info("getSeverityList called...");
		boolean check = false;
		List<String> phaseList = getOntCollection(loader, className);
		for (String sev : phaseList) {
			log.info("severity:"+ sev);
			if (severity.equals(sev.split("\\.")[1]))
			{
				check = true;
			}
		}
		return check;
	}
	
	/**
	 * Checks the list of transformations with transformation coming as an input. 
	 * @param loader - ontology loader
	 * @param className 
	 * @param tranformation 
	 * @return true if transformation exists otherwise returns false.
	 */
	public static boolean getTranformationList(OntologyLoader loader, String className, String tranformation)
	{
		log.info("getTransformationList called...");
		boolean check = false;
		List<String> phaseList = getOntCollection(loader, className);
		for (String trans : phaseList) {
			if (tranformation.equals(trans))
			{
				check = true;
			}
		}
		return check;
	}
	
	/**
	 * Gets the target string from request header.
	 * @param loader
	 * @param requestHeader
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @return string
	 */
	public static String getTarget(OntologyLoader loader,String requestHeader) throws InstantiationException, IllegalAccessException{
		
		log.info("getTarget called...");
		String targetString ="";
		ElementHandler element = new ElementHandler(loader);
		Element elmnt = (Element) EntityFactory.ELEMENT.getObject();
		String[] headerList = requestHeader.split(",");
		
		for (int i = 0; i < headerList.length -1; i++) {
			log.info("Header : " + headerList[i]);
			if (headerList[i].startsWith("QUERY"))
			{	
				if (targetString.contains("ARGS"))
				{
					targetString = targetString +  " | " + "ARGS:" + headerList[i].substring(5);
				}
				else if (targetString.isEmpty())
				{
					targetString = targetString + "ARGS:" + headerList[i].substring(5);
				}
				else
				{
					targetString = targetString +  " | " + "ARGS:" + headerList[i].substring(5);
				}
			}
			else if (Fetcher.isCollectionIndividual(loader, headerList[i])) {
				
				System.out.println("collection variable");

			} else if (Fetcher.isStandardVariableIndividual(loader,
					headerList[i])) {
				
				System.out.println("Standard variable");

			} else if (element.isExists(headerList[i])) {
				
				System.out.println("ELEMENT");
				Entity en = element.get(headerList[i]);
				
				if (en instanceof Element) {
					
					elmnt = (Element) en;

					if (targetString.contains(elmnt.getCollection()))
					{
						targetString  = targetString +  " | "+ elmnt.getCollection() + ":" + elmnt.getName();
					}					
					else 
					{
						targetString  =  targetString + elmnt.getCollection() + ":" + elmnt.getName();						
					}

				}
			} else {
				System.out.println("Not Part of dictionary");
			}
		}
		log.info("Target String : " + targetString);
		return targetString;
		
	}
	
/**
 * Converts an expression string to variable object 
 * @param expression -
 * @param loader
 * @return variable object.
 */
public static Variable toVariableBean(String expression, OntologyLoader loader){
	log.info("Parse Variable Called..");
	Variable variable = null;
	UserDefinedVariableHandler udvHndler = new UserDefinedVariableHandler(loader);
	try {
		
		variable = (Variable) EntityFactory.VARIABLE.getObject();
		List<VariableExpression> varList = new ArrayList<VariableExpression>();
		List<CollectionExpression> colList = new ArrayList<CollectionExpression>();
		List<String> transList = new ArrayList<String>();

		String varDelimiter = "\\|";

		String[] vars = null;

		vars = expression.split(varDelimiter);

		log.info("-----------Variables---------" );
		for (String var : vars) {
			
			var = RuleParser.normaliseVariable(var);
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
							log.info("Collection:" + colExp.getCollection());
							log.info("Element:" + colExp.getElement());
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

		//transList = parseTransformations(actions, loader);
		variable.setTransformation(transList);
		variable.setCollectionExpressions(colList);
		variable.setVariableExpressions(varList);
		
	} catch (InstantiationException | IllegalAccessException e) {
		e.printStackTrace();
	}

	return variable;

}
	
}
