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
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="net.swas.explorer.httpprofile.*"%>
<%@page import="java.sql.*"%>

<div class="left-bar-head">
	<h2>HTTP Profile</h2>
	<div id="content-menu" style="left: 94%; width: 6%;">
		<ul>
			<li><a href="resourceTree.jsp" rel="#httpProfileOverlay" 
							id="addHttpProfileOverlay"><img
					alt="add profile" src="images/icons/list1_add.png"></a></li>
		</ul>
	</div>
</div>
<%
	DOProfile profile = new DOProfile(getServletContext());
	List<String> parent = new ArrayList<String>();
	parent = profile.getRootParent();
%>
<div id="tree">
	<ul>
		<%
			for (String string : parent) {
				//System.out.println(string);
		%>
		<li id="key3" class="folder"><%=string%>
			<ul>
				<%
					profile.generateTree(string, out);
				%>
			</ul> <%
 					}
				 %>
	</ul>
</div>