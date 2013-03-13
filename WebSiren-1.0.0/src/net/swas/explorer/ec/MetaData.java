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

import java.util.List;


import net.swas.explorer.ecf.Entity;

/**
 * Purpose of this class is to create MetaData type objects.
 */
public class MetaData extends Entity {

	private String message = "";
	private String revision = "";
	private String ruleID = "";
	private String severity = "";
	private List<String> tag;

	/**
	 * To get Message
	 * @return message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * To set Message
	 * @param message
	 */
	public void setMessage(String message) {
		this.message = message;
		// System.out.println(this.message);
	}

	/**
	 * To get Revision
	 * @return revision
	 */
	public String getRevision() {
		return revision;
	}

	/**
	 * To set Revision
	 * @param revision
	 */
	public void setRevision(String revision) {
		this.revision = revision;
		// System.out.println(this.revision);
	}

	/**
	 * To get Rule ID
	 * @return the ruleID
	 */
	public String getRuleID() {
		return ruleID;
	}

	/**
	 * To set Rule ID
	 * @param ruleID
	 */
	public void setRuleID(String ruleID) {
		this.ruleID = ruleID;
		// System.out.println(this.ruleID);
	}

	/**
	 * To set Severity
	 * @return severity
	 */
	public String getSeverity() {
		return severity;
	}

	/**
	 * To set Severity
	 * @param severity
	 */
	public void setSeverity(String severity) {
		this.severity = severity;
		// System.out.println(this.severity);
	}

	/**
	 * To get list of tags
	 * @return the tag
	 */
	public List<String> getTag() {
		return tag;
	}

	/**
	 * To set list of tags
	 * @param tag
	 */
	public void setTag(List<String> tag) {
		this.tag = tag;
		// System.out.println(this.tag);
	}
}
