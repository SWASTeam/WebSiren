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
 * This class is responsible for creating Rule Group type objects and classify rules in terms of groups.
 * 
 */
public class RuleGroup extends Entity {
	
	private String name = "";
	private String description = "";
	private String previousGroup = "";
	private User userCreatedBy = null;
	private String creationDate = "";
	private String editingDate = "";
	private User userEditedBy = null;
	
	
	/**
	 * To get Rule Group editing User
	 * @return the userEditedBy
	 */
	public User getUserEditedBy() {
		return userEditedBy;
	}

	/**
	 * To set Rule Group  editing User
	 * @param userEditedBy
	 */
	public void setUserEditedBy(User userEditedBy) {
		this.userEditedBy = userEditedBy;
	}

	/**
	 * To get creation date
	 * @return the creationDate
	 */
	public String getCreationDate() {
		return creationDate;
	}

	/**
	 * To set creation date
	 * @param creationDate the creationDate to set
	 */
	public void setCreationDate(String creationDate) {
		this.creationDate = creationDate;
	}

	/**
	 * To get editing date
	 * @return editingDate
	 */
	public String getEditingDate() {
		return editingDate;
	}

	/**
	 * To set editing date
	 * @param editingDate
	 */
	public void setEditingDate(String editingDate) {
		this.editingDate = editingDate;
	}

	/**
	 * To get previous group name
	 * @return previousGroup
	 */
	public String getPreviousGroup() {
		return previousGroup;
	}

	/**
	 * To set previous group name
	 * @param previousGroup
	 */
	public void setPreviousGroup(String previousGroup) {
		this.previousGroup = previousGroup;
	}

	/**
	 * To get Rule Group Creation User
	 * @return userCreatedBy
	 */
	public User getUserCreatedBy() {
		return userCreatedBy;
	}

	/**
	 * To set Rule Group Creation User
	 * @param user
	 */
	public void setUserCreatedBy(User user) {
		this.userCreatedBy = user;
	}

	/**
	 * To get group name
	 * @return name
	 */
	public String getName() {
		return name;
	}

	/**
	 * To set group name
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * To get description
	 * @return description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * To set description
	 * @param description
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
}
