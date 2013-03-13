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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.swas.explorer.oh.lo.KBConfiguration;
import net.swas.explorer.util.SWASConfig;

/**
 *This class manages Data Base connection and manages prepared statements along with their execution.
 *MySQL is used at back end. 
 *<pre>
 *    DBConnection db = new DBConnection();
 *    db.connect();
 * </pre>
 * @author SWASS Team
 */
@SuppressWarnings("unused")
public class DBConnection {

	private Logger log = LoggerFactory.getLogger(DBConnection.class);
    private Connection con;
	private String driverString;
    private String userName;
    private String password;
    private String connectionString;
    
    /**
     * @param context context is for capturing the knowledge base configuration.
     */
    public DBConnection(ServletContext context) {
		super();
		
		SWASConfig config = SWASConfig.getInstance(context);
		Map<String, String> configMap = config.getConfigMap();
		this.driverString = configMap.get("JDBCDriver");
		this.connectionString =configMap.get("ConnectionString");
		this.userName = configMap.get("username");
		this.password = configMap.get("password");
	}
    
 
     /**
     * Connect to Data Base
     * @throws IOException
     */
    public void connect() throws IOException
    { 
        try  
        {   
            
            Class.forName(driverString).newInstance();
            try  
            {   
               con = DriverManager.getConnection( connectionString, userName, password);  
               log.info( "DB connection sucessful"); 
            }   
            catch( SQLException e )   
            {   
                log.info( "could not get JDBC connection: " + e );   
            }   
        }   
        catch( Exception e )   
        {   
            log.info( "could not load JDBC driver: " + e );   
        }   

    }


    /**
     * Disconnect from Data Base
     */
    public void disconnect()
    {
        try
        {
            con.close();
            con=null;
        }
        catch(Exception ex)
        {
            ex.printStackTrace(System.out);
        }
        finally
        {
            con=null;
        }
    }
  
    
    /**
     * @param query Query is String type parameter and is prepared for execution.
     * @return Prepared Statement Prepared Statement is a precompiled statement for faster execution.
     */
    public PreparedStatement prepareQuery(String query)
    {
      PreparedStatement stmt=null;
        try
        {
            stmt = con.prepareStatement(query);
           
        }
        catch(Exception ex)
        {
            ex.printStackTrace(System.out);
        }
        return stmt;
  
    }
     /**
      * Execute Query
     * @param st is prepared statement which is previously prepared in   
     * @return Result Set
     */
    public ResultSet executeQuery(PreparedStatement st) throws SQLException
    { 
        ResultSet rst;
        rst=st.executeQuery();
        return rst;
       
    }
    /**
     * Update Query
     * @param pstmt pstmt is prepared statement which is previously prepared in   
     * 
     */
    public void updateQuery(PreparedStatement pstmt) throws SQLException {
        
        pstmt.executeUpdate();
    }
            
}

