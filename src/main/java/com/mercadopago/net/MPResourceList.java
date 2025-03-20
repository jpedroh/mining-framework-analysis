package com.mercadopago.net;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * List of resources returned by an API.
 *
 * @param <T> Type of resource being returned
 */
@Getter @Setter public class MPResourceList<T extends java.lang.Object> extends MPResource {
  /** List of results. */
  private List<T> results;
}