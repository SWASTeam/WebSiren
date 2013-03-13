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
 * This class is responsible for creating User type objects.
 */
public class User extends Entity {
	
	private String userName = "";
	private String displayName = "";
	private String previousName = "";


	/**
	 * To get previous name
	 * @return previousName
	 */
	public String getPreviousName() {
		return previousName;
	}
	/**
	 * To set previous name
	 * @param previousName
	 */
	public void setPreviousName(String previousName) {
		this.previousName = previousName;
	}
	
	/**
	 * To get user name
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}
	
	/**
	 * To set user name
	 * @param userName 
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	/**
	 * To get display name
	 * @return the displayName
	 */
	public String getDisplayName() {
		return displayName;
	}
	
	/**
	 * To set display name
	 * @param displayName 
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
}
