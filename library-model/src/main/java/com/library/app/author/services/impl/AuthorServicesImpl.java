/**
 * 
 */
package com.library.app.author.services.impl;

import static com.library.app.common.utils.ValidationUtils.*;

import java.util.List;

import javax.ejb.Stateful;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.Validator;

import com.library.app.author.exception.AuthorNotFoundException;
import com.library.app.author.model.Author;
import com.library.app.author.model.filter.AuthorFilter;
import com.library.app.author.repository.AuthorRepository;
import com.library.app.author.services.AuthorServices;
import com.library.app.common.model.PaginatedData;

/**
 * Since there is a pool of {@link Stateless} ejbs is more useful and scalable to use the {@link Stateless} instead of
 * {@link Stateful} EJB
 * 
 * @author Iulian David david.iulian@gmail.com
 *
 */
@Stateless
public class AuthorServicesImpl implements AuthorServices {

	/** Will be used by container's own Validator implementation **/
	@Inject
	public Validator validator;

	@Inject
	public AuthorRepository authorRepository;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.library.app.author.services.AuthorServices#add(com.library.app.author.model.Author)
	 */
	@Override
	public Author add(final Author author) {
		validateEntityFields(validator, author);
		return authorRepository.add(author);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.library.app.author.services.AuthorServices#update(com.library.app.author.model.Author)
	 */
	@Override
	public void update(final Author author) {
		validateEntityFields(validator, author);

		if (!authorRepository.existsById(author.getId())) {
			throw new AuthorNotFoundException();
		}

		authorRepository.update(author);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.library.app.author.services.AuthorServices#findById(long)
	 */
	@Override
	public Author findById(final long id) {
		final Author authorFound = authorRepository.findById(id);
		if (authorFound == null) {
			throw new AuthorNotFoundException();
		}
		return authorFound;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.library.app.author.services.AuthorServices#findAll()
	 */
	@Override
	public List<Author> findAll() {
		return authorRepository.findAll("name");
	}

	@Override
	public PaginatedData<Author> findByFilter(final AuthorFilter filter) {
		return authorRepository.findByFilter(filter);
	}

}
