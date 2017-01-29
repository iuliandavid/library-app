/**
 * 
 */
package com.library.app.common.model;

import java.util.List;

/**
 * Model class for findAll methods
 * 
 * <pre>
 * {@code
 * {
 * 	"paging": {
 * 		"totalRecords": 2
 * 			},
 * 	"entries": [
 * 			{
 * 			"id": 1,
 * 				"name": "Java"
 * 			},
 * 			{
 * 				"id": 2,
 * 				"name": "Networks"
 * 			}
 * 		]
 * }
 * }
 * </pre>
 * 
 * @author iulian
 *
 */
public class PaginatedData<T> {

	private final int numberOfRows;
	private final List<T> rows;

	public PaginatedData(final int numberOfRows, final List<T> rows) {
		this.numberOfRows = numberOfRows;
		this.rows = rows;
	}

	/**
	 * @return the numberOfRows
	 */
	public int getNumberOfRows() {
		return numberOfRows;
	}

	/**
	 * @return the rows
	 */
	public List<T> getRows() {
		return rows;
	}

	public T getRow(final int index) {
		if (index >= rows.size()) {
			return null;
		}
		return rows.get(index);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "PaginatedData [numberOfRows=" + numberOfRows + ", rows=" + rows + "]";
	}

}
