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
package net.swas.explorer.ec;

import net.swas.explorer.ecf.Entity;

/**
 * This class is responsible for creating resource type object and is associated with rule and chain rule class
 *
 */
public class Resource extends Entity{

	private String resource = "";
	private String url = "";
	
	/**
	 * To get resource
	 * @return resource
	 */
	public String getResource() {
		return resource;
	}
	/**
	 * To set resource
	 * @param resource
	 */
	public void setResource(String resource) {
		this.resource = resource;
	}
	/**
	 * To get URL
	 * @return url
	 */
	public String getUrl() {
		return url;
	}
	/**
	 * To set URL
	 * @param url
	 */
	public void setUrl(String url) {
		this.url = url;
	}
	
			
}
