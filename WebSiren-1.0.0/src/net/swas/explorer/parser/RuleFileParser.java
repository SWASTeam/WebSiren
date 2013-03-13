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
import java.util.Arrays;
import java.util.List;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is used to parse the rule file content.
 */
public class RuleFileParser {

	private final static Logger log = LoggerFactory.getLogger(RuleFileParser.class);

	/**
	 * parses the rule file contents and extracts the list of rules from the file content
	 * @param fileContent as string
	 * @return list of rules as string
	 * @throws IOException
	 */
	public static List<String> parseFile(String fileContent) throws IOException {

		log.info("parse file called..");

		List<String> ruleStrings = new ArrayList<String>();
		String ruleDelimiter = "SecRule";

		String[] results = fileContent.split(ruleDelimiter);
		ruleStrings = Arrays.asList(results);

		log.info("File parsed based on SecRule Delimiter..");
		return ruleStrings;
	}

}
