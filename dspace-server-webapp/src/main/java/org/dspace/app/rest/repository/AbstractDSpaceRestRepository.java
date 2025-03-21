package org.dspace.app.rest.repository;
import org.dspace.app.rest.converter.ConverterService;
import org.dspace.app.rest.utils.ContextUtil;
import org.dspace.app.rest.utils.Utils;
import org.dspace.core.Context;
import org.dspace.services.RequestService;
import org.dspace.utils.DSpace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

/**
 * This is the base class for any Rest Repository. It provides utility method to
 * access the DSpaceContext
 *
 * @author Andrea Bollini (andrea.bollini at 4science.it)
 */
public abstract class AbstractDSpaceRestRepository {
  @Autowired protected Utils utils;

  @Lazy @Autowired protected ConverterService converter;

  protected RequestService requestService = new DSpace().getRequestService();

  protected Context obtainContext() {
    return ContextUtil.obtainCurrentRequestContext();
  }

  public RequestService getRequestService() {
    return requestService;
  }
}