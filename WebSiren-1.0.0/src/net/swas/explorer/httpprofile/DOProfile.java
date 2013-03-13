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
package net.swas.explorer.httpprofile;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.ServletContext;
import javax.servlet.jsp.JspWriter;

import org.apache.http.Header;
import org.apache.http.HttpRequest;

import com.mysql.jdbc.PreparedStatement;


/**
 *This class manages business logic of application profile. MySQL is used at back end. 
 *<pre>
 *    DOProfile profile = new DOProfile();
 *    profile.getUrl();
 * </pre>
 * @author SWASS Team
 */
public class DOProfile {

	private DBConnection cdb = null;
	
	/**
	 * @param context context is for capturing the knowledge base configuration.
	 * @throws IOException
	 */
	public DOProfile(ServletContext context) throws IOException {
		
		cdb = new DBConnection(context);
		cdb.connect();
	
	}
	
	
	/**
	 * Retrieves all URL's from data base.
	 * @return List of URL's
	 * @throws SQLException
	 */
	public List<String> getUrl() throws SQLException
	{
		
		ArrayList<String> urls=new ArrayList<String>();
		
		String sql_qry = "select url from http_request ";
		PreparedStatement pstmt = (PreparedStatement) cdb.prepareQuery(sql_qry);
		ResultSet rs = cdb.executeQuery(pstmt);

		while(rs.next()){
             urls.add(rs.getString("url"));
             
		}  
		return urls;
		
	}
	
	/**
	 * Retrieves http_request from data base based on URL
	 * @param url
	 * @return ResultSet result set contains all possible http_request retrieved from data base 
	 * @throws SQLException 
	 */
	public ResultSet getAllByUrl(String url) throws SQLException
	{
		String sql_qry = "select * from http_request where url=?";
		PreparedStatement pstmt = (PreparedStatement) cdb.prepareQuery(sql_qry);
		pstmt.setString(1, url);
		ResultSet rs = cdb.executeQuery(pstmt);

		return rs;
	}
	
	/**
	 * Retrieves list of headers associated with particular http_request
	 * @param id request id  of http_request
	 * @return list of headers
	 * @throws SQLException 
	 */
	public List<RequestHeader> getHeadersById(int id) throws SQLException
	{
		
		ArrayList<RequestHeader> headers=new ArrayList<RequestHeader>();
		String sql_qry = "select * from headers where request_id=?";
		PreparedStatement pstmt = (PreparedStatement) cdb.prepareQuery(sql_qry);
		pstmt.setLong(1, id);
		ResultSet rs = cdb.executeQuery(pstmt);
		
		while(rs.next()){
			
			RequestHeader requestHeader = new RequestHeader();
			requestHeader.setName(rs.getString("name"));
			requestHeader.setValue(rs.getString("value"));
			headers.add(requestHeader);
		
		}
		
		return headers;
			
	}
	

	/**
	 * This function generates tree based on specified parent.
	 * @param parent parent node of application profile
	 * @param out out is JspWriter object, for writing on JSP page
	 * @throws Exception
	 */
	public void generateTree(String parent, JspWriter out) throws Exception
    
    {
    	String sql_qry = "select count(*) as total from pairs";
		PreparedStatement pstmt = (PreparedStatement) cdb.prepareQuery(sql_qry);
		ResultSet rs = cdb.executeQuery(pstmt);
		rs.next();
		
    	ArrayList<String> child = this.getChilds(parent, out);
		for (String string : child) { 
			
			if (this.getChilds(string, out).size() == 0)
			{
				
				out.print("<li><a href=\"resourceDescription.jsp?node=" +string+"\">"+string+"</a>"+string+"</li>\n");
				System.out.println("<li><a href=\"resourceDescription.jsp?node=" +string+"\">"+string+"</a>"+string+"</li>\n");
			
			} else{

    			out.print("<li id=\"key3\" class=\"folder\">"+string+"\n");
    			out.print("<ul>\n");
    			generateTree(string, out);
    			out.print("</ul>\n");
			
			}	
			
		} 

    }
	
	/**
	* @param parent parent node of application profile
	 * @param out out is JspWriter object, for writing on JSP page
	 * @return list of child nodes in application profile
	 * @throws Exception
	 */
	public ArrayList<String> getChilds(String parent, JspWriter out) throws Exception
	    
	    {
	    	ArrayList<String> child=new ArrayList<String>();
	    	
	    	String sql_qry = "select id from pairs where name=?";
			PreparedStatement pstmt = (PreparedStatement) cdb.prepareQuery(sql_qry);
			pstmt.setString(1, parent);
			ResultSet rs = cdb.executeQuery(pstmt);
	    	rs.next();
	    	
	    	int id=rs.getInt("id");
	    	//System.out.println("my id is= "+id);
	    	
	    	String sqlqry = "select name from pairs where parentid=?";
			PreparedStatement stmt = (PreparedStatement) cdb.prepareQuery(sqlqry);
			stmt.setLong(1, id);
			rs = cdb.executeQuery(stmt);
	    	
	    	 while (rs.next()) {
	    		    String name = rs.getString(1);
	    		    child.add(name);
	    		 // System.out.println("child of "+parent+" is " +name);    	 
	    	 } 
	    	return child;
	    }
	    
	
	 /**
	  * Retrieves all parent/root nodes of application profile
	 * @return List of root nodes
	 * @throws SQLException
	 */
	public List<String> getRootParent() throws SQLException{
	    	List<String> parent = new ArrayList<String>();
	    	
	    	String sql_qry = "select name from pairs where parentid=0 ";
			PreparedStatement pstmt = (PreparedStatement) cdb.prepareQuery(sql_qry);
			ResultSet rs = cdb.executeQuery(pstmt);

			while(rs.next()){
				parent.add(rs.getString("name"));
				System.out.println("Parent:" + rs.getString("name"));
	    }  

	    	return parent;
	    }
	
/**
 * Retrieves all root/parent nodes for specific resource
 * @param resource resource is leaf node of application profile
 * @return list of root/parent nodes based on any particular resource of application profile
 * @throws Exception
 */
	public List<String> getParentByResource(String resource) throws Exception
    {
		List<String> parent = new ArrayList<String>();
		parent.add(resource);
		String sql_qry = "select parentid from pairs where name=?";
		PreparedStatement pstmt = (PreparedStatement) cdb.prepareQuery(sql_qry);
		pstmt.setString(1, resource);
		ResultSet rs = cdb.executeQuery(pstmt);
		rs.next();
		
    	int parentid=rs.getInt("parentid");
    	String sqlqry = "select name from pairs where id=?";
		PreparedStatement stmt = (PreparedStatement) cdb.prepareQuery(sqlqry);
		stmt.setLong(1,parentid);
		rs = cdb.executeQuery(stmt);
		rs.next();
   
    	String parentname=rs.getString("name");
    	System.out.println("parent of "+resource+" with id " +parentid+" is "+parentname);
    	
    	
    	return null;
    }
	
	/**
	 * Retrieves URL of specific resource
	 * @param resource resource is leaf node of application profile
	 * @return URL
	 * @throws SQLException 
	 */
	public String getUrlByResource(String resource) throws SQLException{
		
		String leafNode = resource;
    	Boolean check = new Boolean(true);
    	List<String> urlString = new ArrayList<String>();
    	urlString.add(leafNode);
    	
    	while(check){
    		
    		String sql_qry = "select parentid from pairs where name=?";
			PreparedStatement stmt = (PreparedStatement) cdb
					.prepareQuery(sql_qry);
			stmt.setString(1, resource);
			ResultSet rs = cdb.executeQuery(stmt);
    		rs.next();
    		int parentid=rs.getInt("parentid");
 
    		if(parentid==0)
    		{
    			check=false; 
    			break;
    		}
    		String sqlqry = "select name from pairs where id=?";
			PreparedStatement pstmt = (PreparedStatement) cdb
					.prepareQuery(sqlqry);
			pstmt.setLong(1, parentid);
			rs = cdb.executeQuery(pstmt);
    		rs.next();
    		
    		String parentname=rs.getString("name");
    		System.out.println("parent of "+resource+" is " +parentid+" with "+parentname);
    		urlString.add(parentname);
    		resource=parentname;
    	}
    	String url = "http://";
    	for (int i = urlString.size()-1; i >= 0; i--) {
			if (i == 0)
			{
				url = url + urlString.get(i);
			}
			else
			{
				url = url + urlString.get(i) + "/";
			}
		}
    	System.out.println(url);
    	return url;
	}

	/**
	 * This function is responsible for inserting parent and leaf nodes
	 * @param urls
	 * @throws SQLException
	 */
	public void insertPairs(ArrayList<String> urls) throws SQLException
	{
		int max = 0;
		for (String string : urls) {
			if (max == 0) {
				max = getMaxIdPairs(max);
			} else {
				max = max + 1;
			}
	System.out.println("url size : " + urls.size());	
	System.out.println("url : " + string);
	StringTokenizer urlTokenizer = new StringTokenizer(string, "/");
    System.out.println("total tokens are= "+ urlTokenizer.countTokens());
    urlTokenizer.nextToken();
    int value=0;
    int x=0;
    String parent="";
    while(urlTokenizer.hasMoreTokens())
    { 
    	ResultSet rs=null;
    	String name= urlTokenizer.nextToken();
    	System.out.println("Name :" + name);
			if (x == 0) {
				String sql_qry = "select name from pairs where name= ?";
				PreparedStatement stmt = (PreparedStatement) cdb
						.prepareQuery(sql_qry);
				stmt.setString(1, name);
				rs = cdb.executeQuery(stmt);
				rs.next();
			} else {
				String sql_qry = "select name, parentid from pairs where name=?";
				PreparedStatement stmt = (PreparedStatement) cdb
						.prepareQuery(sql_qry);
				stmt.setString(1, name);
				rs = cdb.executeQuery(stmt);
				rs.next();

				String sqlQry = "select  id from pairs where name=?";
				PreparedStatement pstmt = (PreparedStatement) cdb
						.prepareQuery(sqlQry);
				pstmt.setString(1, parent);
				ResultSet rss = cdb.executeQuery(pstmt);
				rss.next();
				value = rss.getInt(1);
			}
			
			String sql_qry = "select name, parentid from pairs where name=?";
			PreparedStatement stmt = (PreparedStatement) cdb
					.prepareQuery(sql_qry);
			stmt.setString(1, name);
			rs = cdb.executeQuery(stmt);
        	rs.next();
    	 if(!rs.first())
    	 {
    		 if(x==0)
    			 value=0;
    		 else
    			 max=max+1;
    		 
    		 String sqlInsertPairQry = "INSERT INTO pairs (id, name, parentid) VALUES (?,?,?)";
    		 PreparedStatement pstmt = (PreparedStatement) cdb
						.prepareQuery(sqlInsertPairQry);
    		pstmt.setLong(1, max);
 			pstmt.setString(2, name);
 			pstmt.setLong(3, value);
 			cdb.updateQuery(pstmt);
    		System.out.println("inserted is "+name);
        	 	 
    	 }
    	 else {
    		 
    	 }
    	 parent=name;
    	// value=max;
    x++;
    }
		}

	}

	/**
	 * Inserts HTTP request in data base
	 * @param list of HTTP Request
	 * @throws SQLException
	 */
	public void insertRequest(List<HttpRequest> list) throws SQLException {
		System.out.println("Inserting Records..... Please Wait");

		int max = 0;
		for (int a = 0; a < list.size(); a++) {

			if (max == 0) {
				max = getMaxId(max);
			} else {
				max = max + 1;
			}
			String value="";
			HttpRequest re = list.get(a);
			RequestLine requestLine = new RequestLine();
			requestLine = getRequestLine(re);

			String sql_qry = "INSERT INTO http_request (request_id, url, http_version, method, entity_data) VALUES (?,?,?,?,?)";
			PreparedStatement stmt = (PreparedStatement) cdb
					.prepareQuery(sql_qry);
			stmt.setLong(1, max);
			stmt.setString(2, requestLine.getUrl());
			stmt.setString(3, requestLine.getHttpVersion());
			stmt.setString(4, requestLine.getMethod());
			if (requestLine.getMethod().equals("GET")) {
				value = requestLine.getQueryString();
				stmt.setString(5, requestLine.getQueryString());
			} else {
				value = requestLine.getRequestBody().getValue();
				stmt.setString(5, requestLine.getRequestBody().getValue());
			}

			cdb.updateQuery(stmt);
			insertHeaders(re, max);
			System.out.println("Value:" + value);
			insertParameters(requestLine.getUrl(), value);

		}
	}

	/**
	 * Insert HTTP request parameters 
	 * @param value value is query string or post body
	 * @param url 
	 * @throws SQLException 
	 * 
	 */
	private void insertParameters(String url, String value) throws SQLException {
		List<String> paramList = new ArrayList<String>();
		if (value == "")
		{
			System.out.println("value is null");
		}
		else
		{
		paramList = extractParameters(value);
		
		for (String param : paramList) {
			
			String sql_qry = "select parameter from request_parameters where parameter=?";
			PreparedStatement pstmt = (PreparedStatement) cdb.prepareQuery(sql_qry);
			pstmt.setString(1, param);
			ResultSet rs = cdb.executeQuery(pstmt);
			if (!rs.next())
			{
				System.out.println("!rs.next()");
				String sqlInsertPairQry = "INSERT INTO request_parameters (url, parameter) VALUES (?,?)";
	    		PreparedStatement stmt = (PreparedStatement) cdb.prepareQuery(sqlInsertPairQry);
	 			stmt.setString(1, url);
	 			stmt.setString(2, param);
	 			cdb.updateQuery(stmt);
	    		System.out.println("inserted is \""+param + "\" against URL \"" + url + "\"");
			}
			else 
			{
				System.out.println("Already inserted parameter \""+param + "\" against URL \"" + url + "\"");
			}

		}
		}
	}
	
	/**
	 * Retrieves HTTP request parameters based on URL
	 * @param url
	 * @return list of parameters
	 * @throws SQLException
	 */
	public List<String> getParametersByURL(String url) throws SQLException {
		
		ArrayList<String> params =new ArrayList<String>();
		String sql_qry = "select parameter from request_parameters where url=?";
		PreparedStatement pstmt = (PreparedStatement) cdb.prepareQuery(sql_qry);
		pstmt.setString(1, url);
		ResultSet rs = cdb.executeQuery(pstmt);
		while(rs.next()){
			
			params.add(rs.getString("parameter"));
    }  
		return params;
	}
	/**
	 * Extract HTTP request parameters from query string or post body
	 * @param value
	 * @return list of parameters
	 */
	private List<String> extractParameters(String value) {
		List<String> paramList = new ArrayList<String>();
		if (value == null)
		{
			System.out.println("value is null");
		}
		else
		{
			String[] param = value.split("&");
			for (int i = 0; i < param.length; i++) {
				paramList.add(param[i].split("=")[0]);
			}
			for (String string : param) {
				System.out.println("Param:" + string);
			}
		}
		
		return paramList;
	}


	/**
	 * Inserts HTTP request headers of particular request
	 * @param request HTTP request
	 * @throws SQLException
	 */
	private void insertHeaders(HttpRequest re, int max) throws SQLException {

		List<RequestHeader> requestHeaderList = new ArrayList<RequestHeader>();
		requestHeaderList = getRequestHeaders(re);
		for (RequestHeader requestHeader : requestHeaderList) {
			String sql_qry = "INSERT INTO headers (request_id, name, value, header_category) VALUES     (?,?,?,?) ";
			PreparedStatement stmt = (PreparedStatement) cdb
					.prepareQuery(sql_qry);
			stmt.setLong(1, max);
			stmt.setString(2, requestHeader.getName());
			stmt.setString(3, requestHeader.getValue());
			stmt.setString(4, null);
			cdb.updateQuery(stmt);
		}

	}

	/**
	 * Retrieves HTTP request headers of particular HTTP request
	 * @param HTTP Request
	 * @return list of headers
	 */
	private List<RequestHeader> getRequestHeaders(HttpRequest re) {

		List<RequestHeader> requestHeaderList = new ArrayList<RequestHeader>();
		
		Header[] headers = re.getAllHeaders();
		int length = headers.length;
		if (re.getLastHeader("SSRG") != null) {
			length = headers.length - 1;

		}
		for (int i = 0; i < length; i++) {
			RequestHeader requestHeader = new RequestHeader();
			requestHeader.setName(headers[i].getName());
			requestHeader.setValue(headers[i].getValue());
			requestHeaderList.add(requestHeader);
		}

		return requestHeaderList;
	}

	/**
	 * Retrieves HTTP request line of particular HTTP request
	 * @param HTTP Request
	 * @return Request Line
	 */
	private RequestLine getRequestLine(HttpRequest re) {
		RequestBody requestBody = new RequestBody();
		RequestLine requestLine = new RequestLine();
		String uri = "";
		String data = "";
		requestLine.setMethod(re.getRequestLine().getMethod().toString());
		requestLine.setHttpVersion(re.getRequestLine().getProtocolVersion()
				.toString());

		if (re.getRequestLine().toString().contains("?")) {
			String u = re.getRequestLine().getUri().toString();
			StringTokenizer ss = new StringTokenizer(u, "?");
			uri = ss.nextToken();
			requestLine.setUrl(uri);
			data = ss.nextToken();
			requestLine.setQueryString(data);
			requestLine.setRequestBody(null);
		}else if (re.getLastHeader("SSRG") != null) {
			data = re.getLastHeader("SSRG").getValue();
			requestLine.setUrl(re.getRequestLine().getUri().toString());
			requestLine.setQueryString("");
			requestBody.setValue(data);
			requestLine.setRequestBody(requestBody);
		}
		else
		{
			requestLine.setUrl(re.getRequestLine().getUri().toString());
		}

		return requestLine;
	}

	/**
	 * Retrieves the maximum request id from data base
	 * @param Max ID
	 * @return ID
	 * @throws SQLException
	 */
	private int getMaxId(int max) throws SQLException {

		String sql_qry = "select max(request_id) id from http_request";
		PreparedStatement pstmt = (PreparedStatement) cdb.prepareQuery(sql_qry);
		ResultSet rs = cdb.executeQuery(pstmt);
		rs.next();
		max = rs.getInt("id");

		return max+1;
	}
	
	/**
	 * Retrieves the maximum request ID from data base
	 * @param Max Pair ID
	 * @return ID
	 * @throws SQLException
	 */
	private int getMaxIdPairs(int max) throws SQLException {

		String sql_qry = "select max(request_id) id from http_request";
		PreparedStatement pstmt = (PreparedStatement) cdb.prepareQuery(sql_qry);
		ResultSet rs = cdb.executeQuery(pstmt);
		rs.next();
		max = rs.getInt("id");

		return max+1;
	}
}
