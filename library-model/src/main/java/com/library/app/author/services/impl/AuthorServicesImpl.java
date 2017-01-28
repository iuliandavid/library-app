/**
 * 
 */
package com.library.app.author.services.impl;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.ejb.Stateful;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import com.library.app.author.exception.AuthorNotFoundException;
import com.library.app.author.model.Author;
import com.library.app.author.repository.AuthorRepository;
import com.library.app.author.services.AuthorServices;
import com.library.app.common.exception.FieldNotValidException;

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
	public Author add(final Author author) throws FieldNotValidException, AuthorNotFoundException {
		validateAuthor(author);
		return authorRepository.add(author);
	}

	/**
	 * @param category
	 */
	private void validateAuthor(final Author author) {
		final Set<ConstraintViolation<Author>> errors = validator.validate(author);
		final Iterator<ConstraintViolation<Author>> itErrors = errors.iterator();
		if (itErrors.hasNext()) {
			final ConstraintViolation<Author> violation = itErrors.next();
			throw new FieldNotValidException(violation.getPropertyPath().toString(), violation.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.library.app.author.services.AuthorServices#update(com.library.app.author.model.Author)
	 */
	@Override
	public void update(final Author author) throws FieldNotValidException, AuthorNotFoundException {
		validateAuthor(author);

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

}
