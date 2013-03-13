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
import java.util.Map;

import net.swas.explorer.ecf.Entity;

/**
 * This class utilizes information form Rule and Condition class. Main purpose of
 * this class is to create ChainRule type objects.
 */
public class ChainRule extends Entity {

	
	private List<Condition> condition;
	private String comment = "";
	private String disruptiveAction = "";
	private MetaData metaData;
	private String ruleTitle = "";
	private int phase = 0;
	private RuleGroup ruleGroup;
	private User userCreatedBy;
	private User userEditedBy;
	private String creationDate = "";
	private String editingDate = "";
	private Map<String, SpecialCollection> specialCollection;
	private ChainRule extension;
	
	/**
	 * To get Chain Rule Creation User
	 * @return userCreatedBy
	 */
	public User getUserCreatedBy() {
		return userCreatedBy;
	}

	/**
	 * To set Chain Rule Creation User
	 * @param userCreatedBy 
	 */
	public void setUserCreatedBy(User userCreatedBy) {
		this.userCreatedBy = userCreatedBy;
	}

	/**
	 * To get Chain Rule editing User
	 * @return the userEditedBy
	 */
	public User getUserEditedBy() {
		return userEditedBy;
	}

	/**
	 * To set Chain Rule editing User
	 * @param userEditedBy
	 */
	public void setUserEditedBy(User userEditedBy) {
		this.userEditedBy = userEditedBy;
	}

	/**
	 * To get version of Chain Rule
	 * @return the extension
	 */
	public ChainRule getExtension() {
		return extension;
	}

	/**
	 * To set version of chain rule
	 * @param extension
	 */
	public void setExtension(ChainRule extension) {
		this.extension = extension;
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
	 * To get list of condition
	 * @return the condition list
	 */
	public List<Condition> getCondition() {

		return condition;
	}

	/**
	 * To set list of condition
	 * @param condition
	 */
	public void setCondition(List<Condition> condition) {
		this.condition = condition;
	}

	
	/**
	 * To get comment
	 * @return comment
	 */
	public String getComment() {
		return comment;
	}

	/** 
	 * To set comment
	 * @param comment
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}

	/**
	 * To get disruptive action
	 * @return disruptiveAction
	 */
	public String getDisruptiveAction() {
		return disruptiveAction;
	}

	/**
	 * To get disruptive action
	 * @param disruptiveAction 
	 **/
	public void setDisruptiveAction(String disruptiveAction) {
		this.disruptiveAction = disruptiveAction;
	}

	/**
	 * To get Meta Data 
	 * @return metaData
	 */
	public MetaData getMetaData() {
		return metaData;
	}

	/**
	 * To set Meta Data
	 * @param metaData 
	 */
	public void setMetaData(MetaData metaData) {
		this.metaData = metaData;
	}

	/**
	 * To set Rule Title
	 * @return ruleTitle
	 */
	public String getRuleTitle() {
		return ruleTitle;
	}

	/**
	 * To set Rule Title
	 * @param ruleTitle 
	 */
	public void setRuleTitle(String ruleTitle) {
		this.ruleTitle = ruleTitle;
	}

	/**
	 * To get Phase
	 * @return phase
	 */
	public int getPhase() {
		return phase;
	}

	/**
	 * To set Phase
	 * @param phase
	 */
	public void setPhase(int phase) {
		this.phase = phase;
	}

	/**
	 * To get Rule group
	 * @return ruleGroup
	 */
	public RuleGroup getRuleGroup() {
		return ruleGroup;
	}

	/**
	 * To set Rule Group
	 * @param ruleGroup
	 */
	public void setRuleGroup(RuleGroup ruleGroup) {
		this.ruleGroup = ruleGroup;
	}

	
	/**
	 * To get Special Collection Map
	 * @return specialCollection
	 */
	public Map<String, SpecialCollection> getSpecialCollection() {
		return specialCollection;
	}

	/**
	 * To set Special Collection Map
	 * @param specialCollection
	 */
	public void setSpecialCollection(Map<String, SpecialCollection> specialCollection) {
		this.specialCollection = specialCollection;
	}
	

}
