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

import java.util.ArrayList;
import java.util.List;

/**
 * This class stores and maintains list of parsing errors occurred 
 * while parsing modsecurity rules
 */
public class ParsingErrors {

	
	public static List<String> errorList = new ArrayList<>();
	private static String parseError = "";
	private static int errorSize = 0;
	
	/**
	 * Gets the parse error.
	 * @return the parseError.
	 */
	public static String getParseError() {
		return parseError;
	}

	/**
	 * Specifies the parse error.
	 * @param parseError the parseError to set
	 */
	public static void setParseError(String parseError) {
		ParsingErrors.parseError = parseError;
	}

	
	/**
	 * Gets the size of error
	 * the errorSize
	 */
	public static int getErrorSize() {
		return errorSize;
	}
	

	/**
	 * Specifies the error size.
	 * @param errorSize the errorSize to set
	 */
	public static void setErrorSize(int errorSize) {
		ParsingErrors.errorSize = errorSize;
	}

	/**
	 * Gets the list of parsing errors.
	 * @return the errorList
	 */
	public List<String> getErrorList() {
		return errorList;
	}

	/**
	 * Specifies the list of parsing errors.
	 * @param errorList the errorList to set
	 */
	public void setErrorList(List<String> errorList) {
		ParsingErrors.errorList = errorList;
	}
	
	
}
