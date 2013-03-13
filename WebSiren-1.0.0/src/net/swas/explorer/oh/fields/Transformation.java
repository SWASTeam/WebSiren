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
 * This enum defines the constants that maps the modsecurity transformations with the meaningfull strings.
 */
public enum Transformation {

	base64Decode("Base 64 Decode"), base64Encode("Base 64 Encode"), compressWhiteSpace(
			"Compress Whitespace"), escapeSeqDecode("Escape Sequence Decode"), hexDecode(
			"Hex Decode"), hexEncode("Hex Encode"), htmlEntityDecode(
			"HTML Entity Decode"), lowercase("Lower Case"), md5(
			"Message Digest 5"), normalisePath("Normalise Path"), normalisePathWin(
			"Normalise Path Win"), replaceComments("Replace Comments"), replaceNulls(
			"Replace Nulls"), sha1("SHA 1"), urlDecode("URL Decode"), urlDecodeUni(
			"URL Decode Uni"), trimLeft("Trim Left"), trim("Trim"), removeWhitespace(
			"Remove White Space"),parityZero7bit("Parity Zero7bit"), parityOdd7bit(
			"Parity Odd7bit"), parityEven7bit("Parity Even7bit"), none("none"), jsDecode(
			"Js Decode"), cssDecode("CSS Decode"), urlEncode("URL Encode"), cssEncode("CSS Encode"),
			removeNulls("Remove Nulls");

	private String displayName = ""; // for a display name of each constant

	
	/**
	 * Specifies the name to be displayed on GUI.
	 * @param displayName
	 */
	private Transformation(String displayName) {
		this.displayName = displayName;
	}

	/**
	 * To get DisplayName set against the transformation.
	 * @return displayName
	 */
	public String getDisplayName() {
		return this.displayName;
	}

}
