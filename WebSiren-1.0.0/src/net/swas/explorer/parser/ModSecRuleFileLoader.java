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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.ontology.Individual;

import net.swas.explorer.ec.Rule;
import net.swas.explorer.ec.ChainRule;
import net.swas.explorer.ecf.Entity;
import net.swas.explorer.oh.handler.ChainRuleHandler;
import net.swas.explorer.oh.handler.RuleHandler;
import net.swas.explorer.oh.lo.OntologyLoader;

/**
 * The ModSecRuleFileLoader class is used to load the modsecurity file, 
 * extract modsecurity rules from it and convert them into semantic representation.
 */

public class ModSecRuleFileLoader {
	
	
	private OntologyLoader loader = null;
	private final static Logger log = LoggerFactory
			.getLogger(ModSecRuleFileLoader.class);

	
	/**
	 * Starts loading the modsecurity rule file into th ontology or Knowledge base.
	 * @param loader ontology loader 
	 * @param filePath modsecurity file path
	 * @param fileName modsecurity file name
	 * @param userID 
	 * @return true if stored successfully otherwise false.
	 */
	public static boolean startService(OntologyLoader loader, String filePath,
			String fileName, String userID)  {

		ArrayList<Entity> entity;
		boolean r = false;
		try {
			log.info("Parsing Service Start..");
			entity = (ArrayList<Entity>) RuleFileLoader.loadAllFiles(filePath,
					loader, fileName, userID);

			for (Entity en : entity) {

				if (en instanceof Rule) {
					
					Rule rule = (Rule) en;
					RuleHandler ruleHandler = new RuleHandler(loader);
					Individual ind = ruleHandler.add(rule);
					log.info("Rule object : " + ind.toString());
					if (ind != null) {
						log.info("Rule added");
						r = true;
					}
					log.info("Entity is instance of RULE....");
					
				}

				else if (en instanceof ChainRule) {
					ChainRule chainRule = (ChainRule) en;
					ChainRuleHandler chainRuleHandler = new ChainRuleHandler(
							loader);
					
					if (chainRuleHandler.add(chainRule) != null) {
					 log.info("ChainRule added"); r = true; 
					 }
					log.info("Entity is instance of CHAIN RULE....");
				} 
				else { 
					log.info("No Rule added");
					r = false;
				}

			}

		} catch (IOException | InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return r;
	}

	
	/**
	 * Stops file translation.
	 */
	public static void StopService() {
		//TODO
	}
	
}
