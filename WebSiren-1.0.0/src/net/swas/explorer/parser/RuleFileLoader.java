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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.swas.explorer.ec.ChainRule;
import net.swas.explorer.ec.Rule;
import net.swas.explorer.ec.User;
import net.swas.explorer.ecf.Entity;
import net.swas.explorer.ecf.EntityFactory;
import net.swas.explorer.exception.SWASException;
import net.swas.explorer.oh.fields.ElementMap;
import net.swas.explorer.oh.lo.OntologyLoader;

/**
 * The RuleFileLoader class is used to translate rule file string into semantic rules.
 */
public class RuleFileLoader {

	public static ArrayList<String> parseErrorList = new ArrayList<>();
 	private final static Logger log = LoggerFactory
			.getLogger(RuleFileLoader.class);
 	

	/**
	 * Reads the rule file, pares it and return the list of rule objects.
	 * @param filePath - rule file path
	 * @param loader - ontology loader
	 * @return list of rule objects
	 * @throws IOException 
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws SWASException 
	 */

	public static ArrayList<Entity> loadAllFiles(String filePath,
			OntologyLoader loader, String fileName, String userID) throws IOException,
			InstantiationException, IllegalAccessException {
		
		String parseError = "";
		ArrayList<Entity> rulesList = new ArrayList<Entity>();	
		List<String> ruleString = new ArrayList<String>();
		Rule rule = null;
		ChainRule chainRule = null;
		User user = null; 
		ParsingErrors p = new ParsingErrors();

		try {
			rule = (Rule) EntityFactory.RULE.getObject();
			chainRule = (ChainRule) EntityFactory.CHAIN_RULE.getObject();
			user = (User) EntityFactory.USER.getObject();
			
			user.setUserName(userID);

		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}

		// ---Loading file contents into string
		String fileContentString = loadFile(filePath);

		// ---extracting rules from String
		ruleString = RuleFileParser.parseFile(fileContentString);
		boolean ruleType = false; // it is chain rule
		String chRule = "";
		String simpleRule = "";
		int size =0;
		int errorSize = 0;

		for (int i = 1; i < ruleString.size(); i++) {

			// ------Chain Rule
			if (ruleString.get(i).contains("chain")) {

				ruleType = true;
				if (chRule.equals("")) {
					chRule = ruleString.get(i);
				}

				chRule = chRule + "\n SecRule " + ruleString.get(i + 1);
				chRule = chRule.replace(ElementMap.regx_quotes.getMappedElement(), ElementMap.regx_quotes.toString());
				continue;

			}

			// -----for skipping iteration
			if (ruleType) {

				log.info("Chain Rule:" + chainRule);
				try {
					
					//ParsingErrors.errorList.add(chRule);
					chainRule = RuleParser.parseChainRule(loader, chRule,fileName);
					chainRule.setUserCreatedBy(user);
					chainRule.getRuleGroup().setUserCreatedBy(user);
					
					size = ParsingErrors.errorList.size();
					
				} catch (SWASException e) {
					e.printStackTrace();
				}
				if (size > 0 ) {
					
					parseError += "[Parse Error] Errors while parsing rule -------- :" +
							"\n\t" + ParsingErrors.errorList + "\n\n SecRule" + chRule + "\n\n";
					log.info("PARSE ERROR: " + parseError);
					errorSize += ParsingErrors.errorList.size(); 
					ParsingErrors.errorList.clear();
					
				} 
				else
				{
					rulesList.add(chainRule);
				}
				ruleType = false;
				chRule = "";
				continue;

			}

			if (!ruleType) {

				simpleRule = ruleString.get(i).replace(ElementMap.regx_quotes.getMappedElement(), ElementMap.regx_quotes.toString());
				try {

					rule = RuleParser.parseRule(loader, simpleRule, fileName);
					rule.setUserCreatedBy(user);
					rule.getRuleGroup().setUserCreatedBy(user);
					size = ParsingErrors.errorList.size();
					//p.setErrorList(ParsingErrors.errorList);
					
				} catch (SWASException e) {
					System.out.println(e.getMessage());
				}

				ruleType = false;
				if (size > 0) {
					
					parseError += "[Parse Error] Errors while parsing rule -------- :" +
							"\n\t" + ParsingErrors.errorList + "\n\t SecRule" + simpleRule + "\n\n";
					log.info("PARSE ERROR: " + parseError);
					errorSize += ParsingErrors.errorList.size(); 
					ParsingErrors.errorList.clear();
				
				} 
				else
				{
					rulesList.add(rule);
				}
			}

		}
		
		log.info("PARSE ERROR: " + parseError);
		ParsingErrors.setParseError(parseError);
		ParsingErrors.setErrorSize(errorSize);
		return rulesList;
		
	}

	// }

	/**
	 * loads the file.
	 * @param file
	 * @return fileContents in String
	 * @throws IOException
	 */
	public static String loadFile(String file) throws IOException {
		
		String fileContents = readEntireFile(file);
		return fileContents;

	}

	/**
	 * reads the file and extract it as a string
	 * @param file
	 * @return file content as string
	 * @throws IOException
	 */
	
	private static String readEntireFile(String file) throws IOException {
		
		StringBuffer stringBuffer = null;
		File readFile = null;
		BufferedReader bufferedReader = null;
		try {

			readFile = new File(file);
			FileReader fileReader = new FileReader(readFile);
			bufferedReader = new BufferedReader(fileReader);
			stringBuffer = new StringBuffer();
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				if (line.startsWith("#") || line.startsWith("SecMarker")) {
					// System.out.println(line);
				} else {
					stringBuffer.append(line);
				}
			}
			fileReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return (stringBuffer.toString());

	}

}
