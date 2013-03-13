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

import javax.servlet.ServletContext;


import net.swas.explorer.ms.events.AuditEventConsumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The ModSecService class is used to initialize all consumers and producers
 * used for communication with remote agent running on modsecurity end. It also 
 * lazy-loads( loads if needed ) the producers and consumers
 */
public class ModSecService {
	
	
	private final static Logger log = LoggerFactory.getLogger(ModSecService.class);
	
	private static MSServiceProducer mssProd = null; //for producing modsecurity messages
	private static MSServiceConsumer mssCons = null; //for consuming modsecurity messages
	private static AuditEventConsumer audEvtCons = null; //for consuming audit event messages
	private static ServletContext servletContext = null;//getting servlet context in listener classes
	
	/**
	 * Starts the Consumers and Producers
	 * @param context used to read system configurations from
	 *  configuration file.
	 */
	public static void startMSService(ServletContext context){
		
		servletContext = context;
		
		log.info("Starting modsec message producers");
		mssProd = new MSServiceProducer();
		mssProd.init(context);
		
		log.info("Starting modsec message consumers");
		mssCons = new MSServiceConsumer();
		mssCons.init(context);
	
		log.info("Starting audit message consumers");
		audEvtCons = new AuditEventConsumer();
		audEvtCons.init(context);
		
	}
	
	/**
	 * To get MSServiceProducer. Initializes producer if needed. 
	 * 
	 * @param context used to read system configurations from
	 *  configuration file.
	 *  
	 * @return MSServiceProducer Object
	 */
	public static MSServiceProducer getMSServiceProducer(ServletContext context){
		
		if(mssProd == null){
			
			mssProd = new MSServiceProducer();
			mssProd.init(context);
		
		}
		return mssProd; 
		
	}
	
	/**
	 * To get MSServiceConsumer. Initializes consumer if needed. 
	 * 
	 * @param context used to read system configurations from
	 *  configuration file.
	 *  
	 * @return MSServiceConsumer Object
	 */
	public static MSServiceConsumer getMSServiceConsumer(ServletContext context){
		
		if(mssCons == null){
			
			mssCons = new MSServiceConsumer();
			mssCons.init(context);
		
		}
		return mssCons; 
		
	}
	
	/**
	 * To get AuditEventConsumer. Initializes consumer if needed. 
	 * 
	 * @param context used to read system configurations from
	 *  configuration file.
	 *  
	 * @return AuditEventConsumer Object
	 */
	public static AuditEventConsumer getAuditEventConsumer(ServletContext context){
		
		if(audEvtCons == null){
			
			audEvtCons = new AuditEventConsumer();
			audEvtCons.init(context);
		
		}
		return audEvtCons; 
		
	}
	
	/**
	 * To get servlet conext of the application
	 * @return Servlet Context
	 */
	public static ServletContext getServletContext(){
		return servletContext;
	}
 	 
}
