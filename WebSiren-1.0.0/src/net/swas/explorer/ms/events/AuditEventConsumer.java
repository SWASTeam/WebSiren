package net.swas.explorer.ms.events;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.servlet.ServletContext;

import net.swas.explorer.util.AMQConService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A consumer class is used for initializing message consumer for modsecurity audit log events.
 * The Class will used to receive all the logs coming from remote WebSirenAgent running 
 * on your modsecurity machine.   
 */
public class AuditEventConsumer {
	
	private final static Logger log = (Logger) LoggerFactory.getLogger(AuditEventConsumer.class);
	private Session session = null;
	private Destination destination = null;
	private Connection connection = null;
	private AuditEventListener listener = null;
	private MessageConsumer consumer = null;
	private	AMQConService service = null;
	private String prefix="ConnToAuditEventService";
	
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
			this.listener = new AuditEventListener();
			this.consumer.setMessageListener(listener);
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
	
	
}
