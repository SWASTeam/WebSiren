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

public class RelationalModel {

	private String domain;
	private String propertURI;
	private String property;
	private String range;
	private String rangeURI;

	/**
	 * To get domain
	 * @return the domain
	 */
	public String getDomain() {
		return domain;
	}

	/**
	 * To set domain
	 * @param domain
	 */
	public void setDomain(String domain) {
		this.domain = domain;
	}

	/**
	 * To get property URI
	 * @return the propertURI
	 */
	public String getPropertURI() {
		return propertURI;
	}

	/**
	 * To set property URI
	 * @param propertURI
	 */
	public void setPropertURI(String propertURI) {
		this.propertURI = propertURI;
	}

	/**
	 * To get property
	 * @return the property
	 */
	public String getProperty() {
		return property;
	}

	/**
	 * To set property
	 * @param property
	 * 
	 * */
	public void setProperty(String property) {
		this.property = property;
	}

	/**
	 * To get range
	 * @return the range
	 */
	public String getRange() {
		return range;
	}

	/**
	 * To set range
	 * @param range
	 */
	public void setRange(String range) {
		this.range = range;
	}

	/**
	 * To get range URi
	 * @return the rangeURI
	 */
	public String getRangeURI() {
		return rangeURI;
	}

	/**
	 * To set range URI
	 * @param rangeURI
	 * */
	public void setRangeURI(String rangeURI) {
		this.rangeURI = rangeURI;
	}

}
