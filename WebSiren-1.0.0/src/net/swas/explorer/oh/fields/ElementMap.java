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
package net.swas.explorer.oh.fields;

/**
 * This enum defines the constants that are used for mapping characters in 
 * modsecurity with an alternate string representation.
 */
public enum ElementMap {
	
	var_open("%{"),
	var_close("}"),
	op_equal("="),
	op_append("=+"),
	fwdslash("/"),
	ampersand("&"),
	negation("!"),
	all_xml_elements("*"),
	regx_quotes("\\\""),
	action_colon(":");
	
	private String mappedElement;
	
	/**
	 * Specifies the character which is needed to be mapped
	 * @param mappedElement 
	 */
	private ElementMap(String mappedElement) {
		this.mappedElement = mappedElement;
	}
	
	
	/**
	 * To get the mapped charaters
	 * @return mappedElement
	 */
	public String getMappedElement(){
		return this.mappedElement;
	}
	
}
