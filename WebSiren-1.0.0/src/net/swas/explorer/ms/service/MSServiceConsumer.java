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
package net.swas.explorer.ms.service;

import javax.jms.Connection;     
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.servlet.ServletContext;

import net.swas.explorer.util.AMQConService;
import net.swas.explorer.util.MessageUtil;
import net.swas.explorer.util.SWASConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A consumer class is used for initializing message consumer for Modsecurity management.
 * The Class will used to receive all the responses coming from remote WebSirenAgent running 
 * on your modsecurity machine.   
 */
public class MSServiceConsumer {
	
	
	private final static Logger log = (Logger) LoggerFactory.getLogger(MSServiceConsumer.class);
	private Session session = null;
	private Destination destination = null;
	private Connection connection=null;
	private MessageConsumer consumer = null;
	private	AMQConService service=null;
	private String prefix="ConnToMSService";
	
	/**
	 * Initiates message consumer using the Websiren configurations.
	 * 
	 * @param context used to read system configurations from
	 *  configuration file.
	 */
	public void init(ServletContext context){
		
		log.info("init...");
		this.service=new AMQConService(prefix, context);
		this.session=service.getSession();
		this.connection=service.getConnection();
		try {
			
			this.destination = session.createTopic(service.getTopicName());
			this.consumer = session.createConsumer(destination);
			// Listen for arriving messages
			//this.listener = new MSServiceListener();
			//this.consumer.setMessageListener(listener);
			this.connection.start();
			log.info("connected");
			
		} catch (JMSException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * To disconnect consumer with Messaging Queue.
	 */
	public void doDisconnect() {
		
		try {
			this.session.close();
			this.session = null;
		} catch (JMSException e) {
		
			e.printStackTrace();
		}
	}
	
	/**
	 * To recieve messages or responses to the request sent
	 * by Websiren to remote agent.
	 * 
	 * @param context used to read message heart beat from
	 *  configuration file.
	 *  
	 * @return message received. 
	 */
	public String getReceivedMessage(ServletContext context){
		
		String  message; 
		try {
			
			int hBeat = getHeartBeat(context);
			if(hBeat != 0){
				
				Message recievedMsg = this.consumer.receive(hBeat);
				if(recievedMsg != null){
					message= MessageUtil.getMessageString(recievedMsg);
					return message;
				}
			}
			
			
		} catch (JMSException e) {
			e.printStackTrace();
		}
		
		return null;
		
	}
	
	/**
	 * @param context used to read message heart beat from
	 *  configuration file.
	 *  
	 * @return heartBeat if exists returns integer otherwise null. 
	 */
	private int getHeartBeat(ServletContext context){
		
		int beat;
		
		SWASConfig config = SWASConfig.getInstance(context);
		String str = config.getConfigMap().get("Consumer.HeartBeatTime");
		
		try{
			
			beat = Integer.parseInt(str);
			return beat;
		
		} catch(NumberFormatException | NullPointerException e){
			log.info("Parsing Consumer Heart Beat Error :" + str );
			e.printStackTrace();
		}
		
		return 0;
		
	}
	
}
