/**
 * 
 */
package com.library.app.order.services.impl;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.library.app.common.appproperties.PropertyValue;
import com.library.app.order.services.OrderServices;

/**
 * Scheduled job to find expired orders
 * 
 * Set as {@link Singleton} since we need only 1 instance
 * 
 * @author iulian
 *
 */
@Singleton
public class OrderExpiratorJob {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Inject
	private OrderServices orderServices;

	// @Inject
	// private ApplicationProperties applicationProperties;

	@PropertyValue(name = "days-before-order-expiration")
	private Integer daysBeforeOrderExpiration;

	@Schedule(hour = "*/1", minute = "0", second = "0", persistent = false)
	public void run() {
		logger.debug("Executing order expirator job");

		orderServices.changeStatusOfExpiredOrders(daysBeforeOrderExpiration);
	}
}
