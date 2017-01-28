package com.library.app.author.services;

import java.util.List;

import javax.ejb.Local;

import com.library.app.author.exception.AuthorNotFoundException;
import com.library.app.author.model.Author;
import com.library.app.common.exception.FieldNotValidException;

/**
 * The contract for the {@link Author} services
 * 
 * It will not be a remote call, that's why it'a annotated as {@link Local}
 * 
 * @author Iulian David david.iulian@gmail.com
 *
 */
@Local
public interface AuthorServices {

	Author add(Author author) throws FieldNotValidException, AuthorNotFoundException;

	void update(Author author) throws FieldNotValidException, AuthorNotFoundException;

	Author findById(long id);

	List<Author> findAll();
}
