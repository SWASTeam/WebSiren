/**
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
package net.swas.explorer.ecf;

/**
 * This class is parent class of all entity classes. All the entity classes need to be derived 
 * from Entity Class
 */
public abstract class Entity {

	private String ID;

	/**
	 * To get ID. If exists returns String otherwise null. 
	 * @return ID
	 */
	public String getID() {
		return ID;
	}

	/**
	 * To set entity ID
	 * @param ID
	 */
	public void setID(String ID) {
		this.ID = ID;
	}

}
