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

/**
 * @author Sidra
 *
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.ServletContext;

import net.swas.explorer.util.SWASConfig;

/**
 *
 * @author Sidra
 */
@SuppressWarnings("unused")
public class HttpRequestReceiver {

    private static HttpRequestReceiver httpRecObj = new HttpRequestReceiver();
    private Connection con = null;
    private  Statement st;
	private String driverString;
    private String userName;
    private String password;
    private String connectionString;
    
    private HttpRequestReceiver() {
    	
    }

    public static HttpRequestReceiver getInstance() {
       
    	return httpRecObj;
    
    }

    public void createDBConnection(ServletContext context) throws Exception {
    	
    	SWASConfig config = SWASConfig.getInstance(context);
		Map<String, String> configMap = config.getConfigMap();
		this.driverString = configMap.get("JDBCDriver");
		this.connectionString =configMap.get("ConnectionString");
		this.userName = configMap.get("username");
		this.password = configMap.get("password");

    	Class.forName(driverString).newInstance();
	    con = DriverManager.getConnection(connectionString, userName , password);
	    System.out.println("connected to mysql");
        
    }


    
    public void processData(ArrayList<String> urls) throws Exception
    {
    	
    	for(int q=1;q<urls.size();q++){ 
    		
	    	st = con.createStatement();
	    	ResultSet rs = st.executeQuery("select max(id) id from pairs");
	        rs.next();
	        
	        int max=0;
	        max = rs.getInt("id");
	    	max = max + 1;
	    	 
	    	StringTokenizer ss = new StringTokenizer(urls.get(q), "/");
	        System.out.println("total tokens are= "+ ss.countTokens());
	        ss.nextToken();
	        
	        int value=0;
	        int x=0;
	        String parent="";
	        
	        while(ss.hasMoreTokens())
	        {
	        	
	        	ResultSet rss=null;
	        	String name= ss.nextToken();
	        	
	        	if(x==0){
	        		
	        		rss = st.executeQuery("select name from pairs where name= '" + name + "'");
	        		rss.next();
	        	
	        	} else{
	        		
	        		rss = st.executeQuery("select name, parentid from pairs where name= '" + name + "'");
	        		rss.next();
	           	
	        		ResultSet 	 rass = st.executeQuery("select  id from pairs where name= '" + parent + "'");
	        		rass.next();
	        		value=rass.getInt(1);
	           	 
	        	}
	        	
	        	rss = st.executeQuery("select name, parentid from pairs where name= '" + name + "'");
            	rss.next();
            	
        	 if(!rss.first()){
        		 
        		 if(x==0)
        			 value=0;
        		 else
        			 max=max+1;;
        		 
        	     st.execute("    INSERT INTO pairs VALUES     (" + max + ",    '" + name + "',    " + value + "  )  ");
        		 System.out.println("inserted is "+name);
            	 	 
        	 }
        	 
        	 parent=name;
        	 // value=max;
        	 
        	 x++;
	    }
    	
    	}

    }
    
    public String generateUrl(String Resource) throws Exception{
    	
    	String leafNode = Resource;
    	st = con.createStatement();
    	
    	Boolean my = new Boolean(true);
    	List<String> urlString = new ArrayList<String>();
    	urlString.add(leafNode);
    	
    	while(my){
    		
    		ResultSet rss = st.executeQuery("select parentid from pairs where name= '" + Resource + "'");
    		rss.next();
    		int parentid=rss.getInt("parentid");
    		
    		if(parentid==0){
    			
    			my=false; 
    			break;
    		
    		}
    		
    		rss = st.executeQuery("select name from pairs where id= " + parentid + "");
    		rss.next();
    		String parentname=rss.getString("name");
    		System.out.println("parent of "+Resource+" is " +parentid+" with "+parentname);
    		
    		urlString.add(parentname);
    		Resource=parentname;
    	
    	}
    	
    	String url = "http://";
    	for (int i = urlString.size()-1; i >= 0; i--) {
			
    		if (i == 0){
				url = url + urlString.get(i);
			} else{
				url = url + urlString.get(i) + "/";
			}
		}
    	
    	System.out.println(url);
    	return url;
    }
    
    
    public List<String> getParent() throws SQLException{
    	
    	List<String> parent = new ArrayList<String>();
    	st = con.createStatement();
    	ResultSet rss = st.executeQuery("select name from pairs where id=1");
    	System.out.println();
  
    	while (rss.next())
    	{
    		
    		parent.add(rss.getString("name"));
    		System.out.println("Parent:" + rss.getString("name"));
    	
    	}
    	
    	System.out.println("---------------------------");
    	return parent;
    }
    
    public String getParent(String Resource) throws Exception{
    	
    	st = con.createStatement();
    	ResultSet rss = st.executeQuery("select parentid from pairs where name= '" + Resource + "'");
    	rss.next();
    	
    	int parentid=rss.getInt("parentid");
    	rss = st.executeQuery("select name from pairs where id= " + parentid + "");
    	rss.next();
    	
    	String parentname=rss.getString("name");
    	System.out.println("parent of "+Resource+" with id " +parentid+" is "+parentname);
    	
    	return null;
   
    }
    
    public ArrayList<String> getChilds(String Resource) throws Exception{
    	
    	ArrayList<String> array=new ArrayList<String>();
    	st = con.createStatement();
    	
    	ResultSet rss = st.executeQuery("select id from pairs where name= '" + Resource + "'");
    	rss.next();
    	
    	int id=rss.getInt("id");
    	System.out.println("my id is= "+id);
    	rss = st.executeQuery("select name from pairs where parentid= " + id + "");
    	
    	if(rss.next()==false){return null;}
    	 
    		while (rss.next()) {
    		    
    			String name = rss.getString(1);
    		    array.add(name);
    		//    System.out.println("child of "+Resource+" is " +name); 
    		}
    		
    	return array;
    }
    
    
}