package org.broadleafcommerce.core.util.service;
import java.util.List;
import java.util.Map;

/**
 * Service capable of deleting old or defunct entities from the persistence layer (e.g. Carts and anonymous Customers)
 *
 * @author Jeff Fischer
 */
public interface ResourcePurgeService {
  /**
     * Execute a purge of carts from the persistence layer based on the configuration parameters. The default
     * implementation is capable of looking at any combination of name, status and creation date. Take a look
     * at {@link org.broadleafcommerce.core.order.service.OrderService#findCarts(String[],
     * org.broadleafcommerce.core.order.service.type.OrderStatus[], java.util.Date, Boolean, int, int)}
     * for more info on the default behavior.
     *
     * @param config Map of params used to drive the selection of carts to purge
     */
  void purgeCarts(Map<String, String> config);

  void notifyCarts(Map<String, String> config);

  void purgeCustomers(final Map<String, String> config);

  /**
     * Generates delete statement and executes them
     * Originally was designed to purge orders and its dependencies
     * @param rootType - entity type to start from to find all dependent entities
     * @param rootTypeIdValue - id value to use, can be any string like '?' that you will want to replace with concrete id, or id itself
     * @param depends - a map representing dependent tables that can't be navigated from a root type,
     *               where key is a table name to depend on, value is structure representing a depending table name,
     *                join column name(FK column that is in depending table) and id field name in table to depend on.
     *
     */
  void purgeOrderHistory(Class<?> rootType, String rootTypeIdValue, Map<String, List<DeleteStatementGeneratorImpl.PathElement>> depends, final Map<String, Integer> config);
}