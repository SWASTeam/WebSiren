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
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Add Rule Group</title>
</head>
<body>
	<form id="newGroup">
		<div id="group">
			<legend id="createGroup"> Add Rule Group</legend>
			<fieldset>
				<p>
					<label for="groupTitle">Name:</label> <input
						id="groupTitle" name="groupTitle"/>
				</p>
				<p>
					<label for="groupDescription">Description:</label>
					<textarea name="groupDescription" id="groupDescription" cols="35"
						rows="5"></textarea>
				</p>
			</fieldset>
			<input type="submit" id = "submitGroup" value = "Add">
		</div>
	</form>


</body>
</html>