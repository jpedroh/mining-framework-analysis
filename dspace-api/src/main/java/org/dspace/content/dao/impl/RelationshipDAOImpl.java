package org.dspace.content.dao.impl;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.persistence.Query;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.dspace.content.Item;
import org.dspace.content.Item_;
import org.dspace.content.Relationship;
import org.dspace.content.RelationshipType;
import org.dspace.content.RelationshipType_;
import org.dspace.content.Relationship_;
import org.dspace.content.dao.RelationshipDAO;
import org.dspace.content.dao.pojo.ItemUuidAndRelationshipId;
import org.dspace.content.factory.ContentServiceFactory;
import org.dspace.content.service.RelationshipTypeService;
import org.dspace.core.AbstractHibernateDAO;
import org.dspace.core.Context;

public class RelationshipDAOImpl extends AbstractHibernateDAO<Relationship> implements RelationshipDAO {
  @Override public List<Relationship> findByItem(Context context, Item item, boolean excludeTilted, boolean excludeNonLatest) throws SQLException {
    return findByItem(context, item, -1, -1, excludeTilted, excludeNonLatest);
  }

  @Override public List<Relationship> findByItem(Context context, Item item, Integer limit, Integer offset, boolean excludeTilted, boolean excludeNonLatest) throws SQLException {
    CriteriaBuilder criteriaBuilder = getCriteriaBuilder(context);
    CriteriaQuery<Relationship> criteriaQuery = getCriteriaQuery(criteriaBuilder, Relationship.class);
    Root<Relationship> relationshipRoot = criteriaQuery.from(Relationship.class);
    criteriaQuery.select(relationshipRoot);
    criteriaQuery.where(criteriaBuilder.or(getLeftItemPredicate(criteriaBuilder, relationshipRoot, item, excludeTilted, excludeNonLatest), getRightItemPredicate(criteriaBuilder, relationshipRoot, item, excludeTilted, excludeNonLatest)));
    return list(context, criteriaQuery, false, Relationship.class, limit, offset);
  }

  /**
     * Get the predicate for a criteria query that selects relationships by their left item.
     * @param criteriaBuilder   the criteria builder.
     * @param relationshipRoot  the relationship root.
     * @param item              the item that is being searched for.
     * @param excludeTilted     if true, exclude tilted relationships.
     * @param excludeNonLatest  if true, exclude relationships for which the opposite item is not the latest version
     *                          that is relevant.
     * @return a predicate that satisfies the given restrictions.
     */
  protected Predicate getLeftItemPredicate(CriteriaBuilder criteriaBuilder, Root<Relationship> relationshipRoot, Item item, boolean excludeTilted, boolean excludeNonLatest) {
    List<Predicate> predicates = new ArrayList<>();
    predicates.add(criteriaBuilder.equal(relationshipRoot.get(Relationship_.leftItem), item));
    if (excludeTilted) {
      predicates.add(criteriaBuilder.or(criteriaBuilder.isNull(relationshipRoot.get(Relationship_.relationshipType).get(RelationshipType_.tilted)), criteriaBuilder.notEqual(relationshipRoot.get(Relationship_.relationshipType).get(RelationshipType_.tilted), RelationshipType.Tilted.RIGHT)));
    }
    if (excludeNonLatest) {
      predicates.add(criteriaBuilder.notEqual(relationshipRoot.get(Relationship_.LATEST_VERSION_STATUS), Relationship.LatestVersionStatus.LEFT_ONLY));
    }
    return criteriaBuilder.and(predicates.toArray(new Predicate[] {  }));
  }

  /**
     * Get the predicate for a criteria query that selects relationships by their right item.
     * @param criteriaBuilder   the criteria builder.
     * @param relationshipRoot  the relationship root.
     * @param item              the item that is being searched for.
     * @param excludeTilted     if true, exclude tilted relationships.
     * @param excludeNonLatest  if true, exclude relationships for which the opposite item is not the latest version
     *                          that is relevant.
     * @return a predicate that satisfies the given restrictions.
     */
  protected Predicate getRightItemPredicate(CriteriaBuilder criteriaBuilder, Root<Relationship> relationshipRoot, Item item, boolean excludeTilted, boolean excludeNonLatest) {
    List<Predicate> predicates = new ArrayList<>();
    predicates.add(criteriaBuilder.equal(relationshipRoot.get(Relationship_.rightItem), item));
    if (excludeTilted) {
      predicates.add(criteriaBuilder.or(criteriaBuilder.isNull(relationshipRoot.get(Relationship_.relationshipType).get(RelationshipType_.tilted)), criteriaBuilder.notEqual(relationshipRoot.get(Relationship_.relationshipType).get(RelationshipType_.tilted), RelationshipType.Tilted.LEFT)));
    }
    if (excludeNonLatest) {
      predicates.add(criteriaBuilder.notEqual(relationshipRoot.get(Relationship_.LATEST_VERSION_STATUS), Relationship.LatestVersionStatus.RIGHT_ONLY));
    }
    return criteriaBuilder.and(predicates.toArray(new Predicate[] {  }));
  }

  @Override public int countByItem(Context context, Item item, boolean excludeTilted, boolean excludeNonLatest) throws SQLException {
    CriteriaBuilder criteriaBuilder = getCriteriaBuilder(context);
    CriteriaQuery criteriaQuery = getCriteriaQuery(criteriaBuilder, Relationship.class);
    Root<Relationship> relationshipRoot = criteriaQuery.from(Relationship.class);
    criteriaQuery.select(relationshipRoot);
    criteriaQuery.where(criteriaBuilder.or(getLeftItemPredicate(criteriaBuilder, relationshipRoot, item, excludeTilted, excludeNonLatest), getRightItemPredicate(criteriaBuilder, relationshipRoot, item, excludeTilted, excludeNonLatest)));
    return count(context, criteriaQuery, criteriaBuilder, relationshipRoot);
  }

  @Override public List<Relationship> findByRelationshipType(Context context, RelationshipType relationshipType) throws SQLException {
    return findByRelationshipType(context, relationshipType, -1, -1);
  }

  @Override public List<Relationship> findByRelationshipType(Context context, RelationshipType relationshipType, Integer limit, Integer offset) throws SQLException {
    CriteriaBuilder criteriaBuilder = getCriteriaBuilder(context);
    CriteriaQuery criteriaQuery = getCriteriaQuery(criteriaBuilder, Relationship.class);
    Root<Relationship> relationshipRoot = criteriaQuery.from(Relationship.class);
    criteriaQuery.select(relationshipRoot);
    criteriaQuery.where(criteriaBuilder.equal(relationshipRoot.get(Relationship_.relationshipType), relationshipType));
    return list(context, criteriaQuery, true, Relationship.class, limit, offset);
  }

  @Override public List<Relationship> findByItemAndRelationshipType(Context context, Item item, RelationshipType relationshipType, Integer limit, Integer offset, boolean excludeNonLatest) throws SQLException {
    CriteriaBuilder criteriaBuilder = getCriteriaBuilder(context);
    CriteriaQuery criteriaQuery = getCriteriaQuery(criteriaBuilder, Relationship.class);
    Root<Relationship> relationshipRoot = criteriaQuery.from(Relationship.class);
    criteriaQuery.select(relationshipRoot);
    criteriaQuery.where(criteriaBuilder.equal(relationshipRoot.get(Relationship_.relationshipType), relationshipType), criteriaBuilder.or(getLeftItemPredicate(criteriaBuilder, relationshipRoot, item, false, excludeNonLatest), getRightItemPredicate(criteriaBuilder, relationshipRoot, item, false, excludeNonLatest)));
    return list(context, criteriaQuery, true, Relationship.class, limit, offset);
  }

  @Override public List<Relationship> findByItemAndRelationshipType(Context context, Item item, RelationshipType relationshipType, boolean isLeft, Integer limit, Integer offset, boolean excludeNonLatest) throws SQLException {
    CriteriaBuilder criteriaBuilder = getCriteriaBuilder(context);
    CriteriaQuery criteriaQuery = getCriteriaQuery(criteriaBuilder, Relationship.class);
    Root<Relationship> relationshipRoot = criteriaQuery.from(Relationship.class);
    criteriaQuery.select(relationshipRoot);
    if (isLeft) {
      criteriaQuery.where(criteriaBuilder.equal(relationshipRoot.get(Relationship_.relationshipType), relationshipType), getLeftItemPredicate(criteriaBuilder, relationshipRoot, item, false, excludeNonLatest));
      criteriaQuery.orderBy(criteriaBuilder.asc(relationshipRoot.get(Relationship_.leftPlace)));
    } else {
      criteriaQuery.where(criteriaBuilder.equal(relationshipRoot.get(Relationship_.relationshipType), relationshipType), getRightItemPredicate(criteriaBuilder, relationshipRoot, item, false, excludeNonLatest));
      criteriaQuery.orderBy(criteriaBuilder.asc(relationshipRoot.get(Relationship_.rightPlace)));
    }
    return list(context, criteriaQuery, true, Relationship.class, limit, offset);
  }

  @Override public List<ItemUuidAndRelationshipId> findByLatestItemAndRelationshipType(Context context, Item latestItem, RelationshipType relationshipType, boolean isLeft) throws SQLException {
    final String relationshipIdAlias = "relationshipId";
    final String itemUuidAlias = "itemUuid";
    CriteriaBuilder criteriaBuilder = getCriteriaBuilder(context);
    CriteriaQuery<Tuple> criteriaQuery = criteriaBuilder.createTupleQuery();
    Root<Relationship> relationshipRoot = criteriaQuery.from(Relationship.class);
    ArrayList<Predicate> predicates = new ArrayList<>();
    predicates.add(criteriaBuilder.equal(relationshipRoot.get(Relationship_.relationshipType), relationshipType));
    if (isLeft) {
      predicates.add(criteriaBuilder.equal(relationshipRoot.get(Relationship_.leftItem), latestItem));
      predicates.add(criteriaBuilder.notEqual(relationshipRoot.get(Relationship_.LATEST_VERSION_STATUS), Relationship.LatestVersionStatus.RIGHT_ONLY));
      criteriaQuery.multiselect(relationshipRoot.get(Relationship_.id).alias(relationshipIdAlias), relationshipRoot.get(Relationship_.rightItem).get(Item_.id).alias(itemUuidAlias));
    } else {
      predicates.add(criteriaBuilder.equal(relationshipRoot.get(Relationship_.rightItem), latestItem));
      predicates.add(criteriaBuilder.notEqual(relationshipRoot.get(Relationship_.LATEST_VERSION_STATUS), Relationship.LatestVersionStatus.LEFT_ONLY));
      criteriaQuery.multiselect(relationshipRoot.get(Relationship_.id).alias(relationshipIdAlias), relationshipRoot.get(Relationship_.leftItem).get(Item_.id).alias(itemUuidAlias));
    }
    criteriaQuery.where(predicates.toArray(new Predicate[] {  }));
    criteriaQuery.distinct(true);
    Query query = this.getHibernateSession(context).createQuery(criteriaQuery);
    query.setHint("org.hibernate.cacheable", true);
    List<?> resultList = query.getResultList();
    return resultList.stream().map(Tuple.class::cast).map((t) -> new ItemUuidAndRelationshipId((UUID) t.get(itemUuidAlias), (Integer) t.get(relationshipIdAlias))).collect(Collectors.toList());
  }

  @Override public List<Relationship> findByTypeName(Context context, String typeName) throws SQLException {
    return this.findByTypeName(context, typeName, -1, -1);
  }

  @Override public List<Relationship> findByTypeName(Context context, String typeName, Integer limit, Integer offset) throws SQLException {
    RelationshipTypeService relationshipTypeService = ContentServiceFactory.getInstance().getRelationshipTypeService();
    List<RelationshipType> relTypes = relationshipTypeService.findByLeftwardOrRightwardTypeName(context, typeName);
    List<Integer> ids = new ArrayList<>();
    for (RelationshipType relationshipType : relTypes) {
      ids.add(relationshipType.getID());
    }
    CriteriaBuilder criteriaBuilder = getCriteriaBuilder(context);
    CriteriaQuery criteriaQuery = getCriteriaQuery(criteriaBuilder, Relationship.class);
    Root<Relationship> relationshipRoot = criteriaQuery.from(Relationship.class);
    criteriaQuery.where(relationshipRoot.get(Relationship_.relationshipType).in(ids));
    return list(context, criteriaQuery, true, Relationship.class, limit, offset);
  }

  @Override public int countByRelationshipType(Context context, RelationshipType relationshipType) throws SQLException {
    CriteriaBuilder criteriaBuilder = getCriteriaBuilder(context);
    CriteriaQuery criteriaQuery = getCriteriaQuery(criteriaBuilder, Relationship.class);
    Root<Relationship> relationshipRoot = criteriaQuery.from(Relationship.class);
    criteriaQuery.select(relationshipRoot);
    criteriaQuery.where(criteriaBuilder.equal(relationshipRoot.get(Relationship_.relationshipType), relationshipType));
    return count(context, criteriaQuery, criteriaBuilder, relationshipRoot);
  }

  @Override public int countRows(Context context) throws SQLException {
    CriteriaBuilder criteriaBuilder = getCriteriaBuilder(context);
    CriteriaQuery criteriaQuery = getCriteriaQuery(criteriaBuilder, Relationship.class);
    Root<Relationship> relationshipRoot = criteriaQuery.from(Relationship.class);
    criteriaQuery.select(relationshipRoot);
    return count(context, criteriaQuery, criteriaBuilder, relationshipRoot);
  }

  @Override public int countByItemAndRelationshipType(Context context, Item item, RelationshipType relationshipType, boolean isLeft, boolean excludeNonLatest) throws SQLException {
    CriteriaBuilder criteriaBuilder = getCriteriaBuilder(context);
    CriteriaQuery criteriaQuery = getCriteriaQuery(criteriaBuilder, Relationship.class);
    Root<Relationship> relationshipRoot = criteriaQuery.from(Relationship.class);
    criteriaQuery.select(relationshipRoot);
    if (isLeft) {
      criteriaQuery.where(criteriaBuilder.equal(relationshipRoot.get(Relationship_.relationshipType), relationshipType), getLeftItemPredicate(criteriaBuilder, relationshipRoot, item, false, excludeNonLatest));
    } else {
      criteriaQuery.where(criteriaBuilder.equal(relationshipRoot.get(Relationship_.relationshipType), relationshipType), getRightItemPredicate(criteriaBuilder, relationshipRoot, item, false, excludeNonLatest));
    }
    return count(context, criteriaQuery, criteriaBuilder, relationshipRoot);
  }

  @Override public int countByTypeName(Context context, String typeName) throws SQLException {
    RelationshipTypeService relationshipTypeService = ContentServiceFactory.getInstance().getRelationshipTypeService();
    List<RelationshipType> relTypes = relationshipTypeService.findByLeftwardOrRightwardTypeName(context, typeName);
    List<Integer> ids = new ArrayList<>();
    for (RelationshipType relationshipType : relTypes) {
      ids.add(relationshipType.getID());
    }
    CriteriaBuilder criteriaBuilder = getCriteriaBuilder(context);
    CriteriaQuery criteriaQuery = getCriteriaQuery(criteriaBuilder, Relationship.class);
    Root<Relationship> relationshipRoot = criteriaQuery.from(Relationship.class);
    criteriaQuery.where(relationshipRoot.get(Relationship_.relationshipType).in(ids));
    return count(context, criteriaQuery, criteriaBuilder, relationshipRoot);
  }

  @Override public List<Relationship> findByItemAndRelationshipTypeAndList(Context context, UUID focusUUID, RelationshipType relationshipType, List<UUID> items, boolean isLeft, int offset, int limit) throws SQLException {
    String side = isLeft ? "left_id" : "right_id";
    String otherSide = !isLeft ? "left_id" : "right_id";
    Query query = createQuery(context, "FROM " + Relationship.class.getSimpleName() + " WHERE type_id = (:typeId) " + "AND " + side + " = (:focusUUID) " + "AND " + otherSide + " in (:list) " + "ORDER BY id");
    query.setParameter("typeId", relationshipType.getID());
    query.setParameter("focusUUID", focusUUID);
    query.setParameter("list", items);
    return list(query, limit, offset);
  }

  @Override public int countByItemAndRelationshipTypeAndList(Context context, UUID focusUUID, RelationshipType relationshipType, List<UUID> items, boolean isLeft) throws SQLException {
    String side = isLeft ? "left_id" : "right_id";
    String otherSide = !isLeft ? "left_id" : "right_id";
    Query query = createQuery(context, "SELECT count(*) " + "FROM " + Relationship.class.getSimpleName() + " WHERE type_id = (:typeId) " + "AND " + side + " = (:focusUUID) " + "AND " + otherSide + " in (:list)");
    query.setParameter("typeId", relationshipType.getID());
    query.setParameter("focusUUID", focusUUID);
    query.setParameter("list", items);
    return count(query);
  }
}