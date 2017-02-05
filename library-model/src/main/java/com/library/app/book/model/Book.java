/**
 * 
 */
package com.library.app.book.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.library.app.author.model.Author;
import com.library.app.category.model.Category;

/**
 * @author iulian
 *
 */
@Entity
@Table(name = "books")
public class Book implements Serializable {

	private static final long serialVersionUID = -98804459764760011L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@Size(min = 10, max = 150)
	private String title;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "category_id")
	private Category category;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "book_author", joinColumns = @JoinColumn(name = "book_id"), inverseJoinColumns = @JoinColumn(name = "author_id"), uniqueConstraints = @UniqueConstraint(columnNames = {
			"book_id", "author_id" }))
	@JoinColumn(name = "author_id")
	@OrderBy("name")
	@NotNull
	@Size(min = 1)
	private List<Author> authors;

	@Lob
	@NotNull
	@Size(min = 10)
	private String description;

	@NotNull
	private Double price;

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(final Long id) {
		this.id = id;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title
	 *            the title to set
	 */
	public void setTitle(final String title) {
		this.title = title;
	}

	/**
	 * @return the category
	 */
	public Category getCategory() {
		return category;
	}

	/**
	 * @param category
	 *            the category to set
	 */
	public void setCategory(final Category category) {
		this.category = category;
	}

	/**
	 * @return the authors
	 */
	public List<Author> getAuthors() {
		if (authors == null) {
			authors = new ArrayList<>();
		}
		return authors;
	}

	public boolean addAuthor(final Author author) {
		if (!getAuthors().contains(author)) {
			getAuthors().add(author);
			return true;
		}
		return false;
	}

	/**
	 * @param authors
	 *            the authors to set
	 */
	public void setAuthors(final List<Author> authors) {
		this.authors = authors;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(final String description) {
		this.description = description;
	}

	/**
	 * @return the price
	 */
	public Double getPrice() {
		return price;
	}

	/**
	 * @param price
	 *            the price to set
	 */
	public void setPrice(final Double price) {
		this.price = price;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Book other = (Book) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Book [id=" + id + ", title=" + title + ", price=" + price + "]";
	}

}
