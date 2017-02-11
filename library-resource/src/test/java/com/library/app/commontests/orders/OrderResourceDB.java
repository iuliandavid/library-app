package com.library.app.commontests.orders;

import static com.library.app.commontests.order.OrderForTestsRepository.*;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.library.app.common.utils.DateUtils;
import com.library.app.order.model.Order;
import com.library.app.order.model.Order.OrderStatus;
import com.library.app.order.services.OrderServices;

/**
 * Helper endpoint for appending all test orders
 * 
 * <pre>
 * POST ALL
 * </pre>
 * 
 * @author Iulian David david.iulian@gmail.com
 *
 */
@Stateless
@Path("/DB/orders")
@Produces(MediaType.APPLICATION_JSON)
public class OrderResourceDB {

	@PersistenceContext
	private EntityManager em;

	@Inject
	private OrderServices orderServices;

	@POST
	public void addAll() {
		final Order order1 = normalizeDependencies(orderReserved(), em);
		order1.setCreatedAt(DateUtils.getAsDateTime("2015-01-04T10:10:34Z"));
		orderServices.add(order1);

		final Order order2 = normalizeDependencies(orderReserved(), em);
		order2.setCreatedAt(DateUtils.getAsDateTime("2015-01-05T10:10:34Z"));
		orderServices.add(order2);
		orderServices.updateStatus(order2.getId(), OrderStatus.CANCELLED);
	}
}
