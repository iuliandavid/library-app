/**
 * 
 */
package com.library.app.order.repository;

import java.util.HashMap;
import java.util.Map;

import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.library.app.common.model.PaginatedData;
import com.library.app.common.repository.GenericRepository;
import com.library.app.order.model.Order;
import com.library.app.order.model.filter.OrderFilter;

/**
 * @author iulian
 *
 */
@Stateful
public class OrderRepository extends GenericRepository<Order> {

	@PersistenceContext
	EntityManager em;

	@Override
	protected Class<Order> getPersistentClass() {
		return Order.class;
	}

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	public PaginatedData<Order> findByFilter(final OrderFilter orderFilter) {
		final StringBuilder clause = new StringBuilder("Where e.id is not null");
		final Map<String, Object> queryParameters = new HashMap<>();

		if (orderFilter.getCustomerId() != null) {
			clause.append(" AND e.customer.id = :customerId");
			queryParameters.put("customerId", orderFilter.getCustomerId());
		}
		if (orderFilter.getStatus() != null) {
			clause.append(" AND e.currentStatus = :currentStatus");
			queryParameters.put("currentStatus", orderFilter.getStatus());
		}
		if (orderFilter.getStartDate() != null) {
			clause.append(" AND e.createdAt >= :startDate");
			queryParameters.put("startDate", orderFilter.getStartDate());
		}
		if (orderFilter.getEndDate() != null) {
			clause.append(" AND e.createdAt <= :endDate");
			queryParameters.put("endDate", orderFilter.getEndDate());
		}
		return findByParameters(clause.toString(), orderFilter.getPaginationData(), queryParameters, "createdAt DESC");
	}
}
