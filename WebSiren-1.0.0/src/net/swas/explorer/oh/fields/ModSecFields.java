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
 * This enum defines the constants that are used in modsecurity rule language as keywords. This list of 
 * strings are used while creating a rule.
 */
public enum ModSecFields {
	SecRule, SecMarker, phase, rev, t, msg, id, tag, severity, setvar, setenv, 
	chain, tx, TX, IP, ip, SESSION, session, GLOBAL, global, ENV, env, initcol,
	setsid, setuid, resource, RESOURCE, USER, user, RULE, rule, IPCollection, 
	SessionCollection, GlobalCollection, ResourceCollection, UserCollection,
	GeoCollection, ENVCollection
}
