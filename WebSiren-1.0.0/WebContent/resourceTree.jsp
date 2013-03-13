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
<form id = "httpPolicyLoader" name = "form" method ="post" action = "addHttpPolicy" enctype="multipart/form-data">
<div id = "file">

<fieldset>
<legend id = "fileUpload">File Importer</legend>
<p>
<label for="fileName">Select file to upload</label>
<input id = "fileName" type="file" name="fileName" required="required">
</p>
<div style="clear:both;height:0px;"></div>
	<div id="errorMsg" style="margin-left: 10px;"></div>
</fieldset>
	
	<input type="submit" id = "submitFile" class = "submitButton" value = "Import">
</div>
</form>