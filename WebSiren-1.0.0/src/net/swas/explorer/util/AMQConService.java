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
package net.swas.explorer.util;

import java.util.Map;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Session;
import javax.servlet.ServletContext;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *  A class for reading amq configurations from config file 
 *  and initializes connection with AMQ Broker accordingly.
 */
public class AMQConService {
	
	private final static Logger log = LoggerFactory.getLogger(AMQConService.class);
	private ActiveMQConnectionFactory connectionFactory = null;
	private Connection connection = null;
	private Session session = null;
	private String url=null;
	private String topic=null;
    
    /**
     * Constructor reads the configurations and initializes connection with AMQ Broker. 
     * @param prefix for reading desired amq configurations
     * @param context used to read system configurations from
	 *  configuration file
     */
    public AMQConService(String prefix, ServletContext context){
    	
    	log.info("loading AMQ configuration :");
    	SWASConfig config = SWASConfig.getInstance(context);
		Map<String, String> configMap = config.getConfigMap();

		this.url=configMap.get("Connection");
		this.topic=configMap.get(prefix+".Topic");
		log.info("Host :"+ this.url+":"+this.topic);
		this.connectionFactory=new ActiveMQConnectionFactory(url);
		try {
			
			this.connection=connectionFactory.createConnection();
			this.session=this.connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		
		} catch (JMSException e) {
			
			e.printStackTrace();
		
		}
    }
    
    /**
     * To get AMQ connection
     * @return connection
     */
    public Connection getConnection(){
    	return this.connection;
    }
    
    /**
     * To get AMQ session object
     * @return session
     */
    public Session getSession(){
    	return this.session;
    }
    
    /**
     * to get topic name which is read from configurations
     * @return topicName
     */
    public String getTopicName(){
    	return this.topic;
    }
    
}
