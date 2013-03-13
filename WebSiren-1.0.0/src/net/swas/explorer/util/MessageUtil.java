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

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

/**
 * This class has utility functions for amq messages. 
 */

public class MessageUtil {

	/**
	 * Converts the message into string.
	 * @param msg
	 * @return string message
	 */
	public static String getMessageString(Message msg){
		if (msg instanceof TextMessage) {
			TextMessage textMessage = (TextMessage) msg;
			try {
				return textMessage.getText();
			} catch (JMSException e) {
				e.printStackTrace();
			}

		}
		return  msg.toString();
	}
	
}
