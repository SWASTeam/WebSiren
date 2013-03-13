<!--
  This file is part of WebSiren.
 
  WebSiren is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.

  WebSiren is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.
 
  You should have received a copy of the GNU General Public License
  along with WebSiren.  If not, see <http://www.gnu.org/licenses/>.
 
 -->
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    
<div class="left-bar-head">
	<h2>ModSecurity</h2>
</div>
<div class="left-bar-body">
	<ul class="msMenu">
		<li><a href="#" onclick="loadMSState()" >Start or Stop ModSecurity</a></li>
		<li><a href="#" onclick="loadMSConfig()" id="msConfigLink" >Configurations</a></li>
		<li><a href="#" onclick="loadMSEvents()" id="msEventsLink" >Events Monitoring</a></li>
	</ul>
</div>
