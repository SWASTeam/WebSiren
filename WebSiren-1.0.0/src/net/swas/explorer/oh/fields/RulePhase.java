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
 * This enum defines the constants that maps the modsecurity phases with the meaningfull strings.
 */
public enum RulePhase {

	requestHeaders(1), requestBody(2), responseHeaders(3), responseBody(4), logging(5);

	private int index;

	/**
	 * Specifies the phase index.
	 * @param index an integer value for phase index
	 */
	private RulePhase(int index) {

		this.index = index;

	}

	/**
	 * To get phase index.
	 * @return index
	 */
	public int getIndex() {

		return this.index;

	}

}
