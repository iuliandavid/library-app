package com.library.app.order.services.impl;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.library.app.order.model.Order;

/**
 * Defines an observer for {@link Order} events
 * 
 * @author iulian
 *
 */
@ApplicationScoped
public class OrderNotificationReceiverCDI {

	private Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 * The method name is irrelevant as long as {@link Observes} annotation
	 * is set before the parameter
	 * 
	 * @param order
	 */
	public void receiveEvent(@Observes final Order order) {
		logger.debug("Order event notification received for order: {}", order);
	}
}
