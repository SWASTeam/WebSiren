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
<%@page import="net.swas.explorer.oh.lo.OntologyLoader"%>
<%@page import="net.swas.explorer.oh.handler.Fetcher"%>
<%@page import="com.hp.hpl.jena.ontology.OntClass"%>

<%! private OntologyLoader loader = null; %>


<div class="left-bar-head">
	<h2>Search By Perspective</h2>
	<a href="#" id="uncheckFacets"> Uncheck All </a>
</div>
<div id="facet-loading" class="load"></div>
<div class="left-bar-body">
	<div id="facets">
	
<%  
	loader =  OntologyLoader.getOntLoader( getServletContext() );
%>  

		<ul id="facetHierarchy" class="topnav" >
			<%
				for ( OntClass cls: Fetcher.getRuleConmpositionClasses(this.loader) ) {
					
		// 			if(cls.getLocalName().equals("Phase") ||
		// 					cls.getLocalName().equals("RuleGroup") ||
		// 					cls.getLocalName().equals("DisruptiveAction") ||
		// 					cls.getLocalName().equals("Severity")){
						
					if(cls.getLocalName().equals("UserDefined") ||
							cls.getLocalName().equals("LogAction") ||
							cls.getLocalName().equals("UnaryOperator") ||
							cls.getLocalName().equals("SpecialCollection")){
						continue;
					}
					out.print("<li> <a id=\"parentFacetValue\" href=\"#\" data-value=\""+ cls.getLocalName() +"\">"+ cls.getLocalName() +"</a>");
					Fetcher.getOntClassHierarchy(cls, out, false);
					out.print("</li>");
		//			}
				}
			%>
		</ul>
	</div>
</div>