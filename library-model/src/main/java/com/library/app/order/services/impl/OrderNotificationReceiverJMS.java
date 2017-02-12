/**
 * 
 */
package com.library.app.order.services.impl;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.library.app.order.model.Order;

/**
 * @author iulian
 *
 */
@MessageDriven(name = "OrderNotificationReceiverJMS", activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "java:/jms/queue/Orders"),
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge")

})
public class OrderNotificationReceiverJMS implements MessageListener {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public void onMessage(final Message message) {
		logger.debug("JMS Listener received a message");
		try {
			logger.debug("Message arrived {}", message.getBody(Order.class));
		} catch (final JMSException e) {
			logger.error("error while deserializing the Order message ", e);
		}

	}

}
