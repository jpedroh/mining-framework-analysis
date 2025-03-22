package org.skyscreamer.yoga.demo.controller;
import org.skyscreamer.yoga.demo.dao.GenericDao;
import java.lang.reflect.ParameterizedType;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by IntelliJ IDEA.
 * User: Carter Page
 */
public abstract class AbstractController<T extends java.lang.Object> {
  @Autowired GenericDao _genericDao;

  Class<T> _entityClass = returnedClass();

  @RequestMapping(value = "/{id}") public T get(@PathVariable long id) {
    return _genericDao.find(_entityClass, id);
  }

  @SuppressWarnings(value = { "unchecked" }) private Class<T> returnedClass() {
    ParameterizedType parameterizedType = (ParameterizedType) getClass().getGenericSuperclass();
    return (Class<T>) parameterizedType.getActualTypeArguments()[0];
  }

  @ExceptionHandler(value = ObjectNotFoundException.class) @ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "No such resource") public void notFound() {
  }
}