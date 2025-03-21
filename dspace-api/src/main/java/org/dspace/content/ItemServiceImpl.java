package org.dspace.content;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.dspace.app.util.AuthorizeUtil;
import org.dspace.authorize.AuthorizeConfiguration;
import org.dspace.authorize.AuthorizeException;
import org.dspace.authorize.ResourcePolicy;
import org.dspace.authorize.service.AuthorizeService;
import org.dspace.authorize.service.ResourcePolicyService;
import org.dspace.content.authority.Choices;
import org.dspace.content.dao.ItemDAO;
import org.dspace.content.factory.ContentServiceFactory;
import org.dspace.content.service.BitstreamFormatService;
import org.dspace.content.service.BitstreamService;
import org.dspace.content.service.BundleService;
import org.dspace.content.service.CollectionService;
import org.dspace.content.service.CommunityService;
import org.dspace.content.service.EntityTypeService;
import org.dspace.content.service.InstallItemService;
import org.dspace.content.service.ItemService;
import org.dspace.content.service.MetadataSchemaService;
import org.dspace.content.service.RelationshipService;
import org.dspace.content.service.WorkspaceItemService;
import org.dspace.content.virtual.VirtualMetadataPopulator;
import org.dspace.core.Constants;
import org.dspace.core.Context;
import org.dspace.core.LogHelper;
import org.dspace.eperson.EPerson;
import org.dspace.eperson.Group;
import org.dspace.event.Event;
import org.dspace.harvest.HarvestedItem;
import org.dspace.harvest.service.HarvestedItemService;
import org.dspace.identifier.IdentifierException;
import org.dspace.identifier.service.IdentifierService;
import org.dspace.orcid.OrcidHistory;
import org.dspace.orcid.OrcidQueue;
import org.dspace.orcid.OrcidToken;
import org.dspace.orcid.model.OrcidEntityType;
import org.dspace.orcid.service.OrcidHistoryService;
import org.dspace.orcid.service.OrcidQueueService;
import org.dspace.orcid.service.OrcidSynchronizationService;
import org.dspace.orcid.service.OrcidTokenService;
import org.dspace.profile.service.ResearcherProfileService;
import org.dspace.services.ConfigurationService;
import org.dspace.versioning.service.VersioningService;
import org.dspace.workflow.WorkflowItemService;
import org.dspace.workflow.factory.WorkflowServiceFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Service implementation for the Item object.
 * This class is responsible for all business logic calls for the Item object and is autowired by spring.
 * This class should never be accessed directly.
 *
 * @author kevinvandevelde at atmire.com
 */
public class ItemServiceImpl extends DSpaceObjectServiceImpl<Item> implements ItemService {
  /**
     * log4j category
     */
  private static final Logger log = org.apache.logging.log4j.LogManager.getLogger(Item.class);

  @Autowired(required = true) protected ItemDAO itemDAO;

  @Autowired(required = true) protected CommunityService communityService;

  @Autowired(required = true) protected AuthorizeService authorizeService;

  @Autowired(required = true) protected BundleService bundleService;

  @Autowired(required = true) protected BitstreamFormatService bitstreamFormatService;

  @Autowired(required = true) protected MetadataSchemaService metadataSchemaService;

  @Autowired(required = true) protected BitstreamService bitstreamService;

  @Autowired(required = true) protected InstallItemService installItemService;

  @Autowired(required = true) protected ResourcePolicyService resourcePolicyService;

  @Autowired(required = true) protected CollectionService collectionService;

  @Autowired(required = true) protected IdentifierService identifierService;

  @Autowired(required = true) protected VersioningService versioningService;

  @Autowired(required = true) protected HarvestedItemService harvestedItemService;

  @Autowired(required = true) protected ConfigurationService configurationService;

  @Autowired(required = true) protected WorkspaceItemService workspaceItemService;

  @Autowired(required = true) protected WorkflowItemService workflowItemService;

  @Autowired(required = true) protected RelationshipService relationshipService;

  @Autowired(required = true) protected VirtualMetadataPopulator virtualMetadataPopulator;

  @Autowired(required = true) private RelationshipMetadataService relationshipMetadataService;

  @Autowired(required = true) private EntityTypeService entityTypeService;

  @Autowired private OrcidTokenService orcidTokenService;

  @Autowired(required = true) private OrcidHistoryService orcidHistoryService;

  @Autowired(required = true) private OrcidQueueService orcidQueueService;

  @Autowired(required = true) private OrcidSynchronizationService orcidSynchronizationService;

  @Autowired(required = true) private ResearcherProfileService researcherProfileService;

  protected ItemServiceImpl() {
    super();
  }

  @Override public Thumbnail getThumbnail(Context context, Item item, boolean requireOriginal) throws SQLException {
    Bitstream thumbBitstream;
    List<Bundle> originalBundles = getBundles(item, "ORIGINAL");
    Bitstream primaryBitstream = null;
    if (CollectionUtils.isNotEmpty(originalBundles)) {
      primaryBitstream = originalBundles.get(0).getPrimaryBitstream();
    }
    if (primaryBitstream != null) {
      if (primaryBitstream.getFormat(context).getMIMEType().equals("text/html")) {
        return null;
      }
      thumbBitstream = bitstreamService.getBitstreamByName(item, "THUMBNAIL", primaryBitstream.getName() + ".jpg");
    } else {
      if (requireOriginal) {
        primaryBitstream = bitstreamService.getFirstBitstream(item, "ORIGINAL");
      }
      thumbBitstream = bitstreamService.getFirstBitstream(item, "THUMBNAIL");
    }
    if (thumbBitstream != null) {
      return new Thumbnail(thumbBitstream, primaryBitstream);
    }
    return null;
  }

  @Override public Item find(Context context, UUID id) throws SQLException {
    Item item = itemDAO.findByID(context, Item.class, id);
    if (item == null) {
      if (log.isDebugEnabled()) {
        log.debug(LogHelper.getHeader(context, "find_item", "not_found,item_id=" + id));
      }
      return null;
    }
    if (log.isDebugEnabled()) {
      log.debug(LogHelper.getHeader(context, "find_item", "item_id=" + id));
    }
    return item;
  }

  @Override public Item create(Context context, WorkspaceItem workspaceItem) throws SQLException, AuthorizeException {
    return create(context, workspaceItem, null);
  }

  @Override public Item create(Context context, WorkspaceItem workspaceItem, UUID uuid) throws SQLException, AuthorizeException {
    Collection collection = workspaceItem.getCollection();
    authorizeService.authorizeAction(context, collection, Constants.ADD);
    if (workspaceItem.getItem() != null) {
      throw new IllegalArgumentException("Attempting to create an item for a workspace item that already contains an item");
    }
    Item item = null;
    if (uuid != null) {
      item = createItem(context, uuid);
    } else {
      item = createItem(context);
    }
    workspaceItem.setItem(item);
    log.info(LogHelper.getHeader(context, "create_item", "item_id=" + item.getID()));
    return item;
  }

  @Override public Item createTemplateItem(Context context, Collection collection) throws SQLException, AuthorizeException {
    if (collection == null || collection.getTemplateItem() != null) {
      throw new IllegalArgumentException("Collection is null or already contains template item.");
    }
    AuthorizeUtil.authorizeManageTemplateItem(context, collection);
    if (collection.getTemplateItem() == null) {
      Item template = createItem(context);
      collection.setTemplateItem(template);
      template.setTemplateItemOf(collection);
      log.info(LogHelper.getHeader(context, "create_template_item", "collection_id=" + collection.getID() + ",template_item_id=" + template.getID()));
      return template;
    } else {
      return collection.getTemplateItem();
    }
  }

  @Override public Iterator<Item> findAll(Context context) throws SQLException {
    return itemDAO.findAll(context, true);
  }

  @Override public Iterator<Item> findAll(Context context, Integer limit, Integer offset) throws SQLException {
    return itemDAO.findAll(context, true, limit, offset);
  }

  @Override public Iterator<Item> findAllUnfiltered(Context context) throws SQLException {
    return itemDAO.findAll(context, true, true);
  }

  public Iterator<Item> findAllRegularItems(Context context) throws SQLException {
    return itemDAO.findAllRegularItems(context);
  }



  @Override public Iterator<Item> findBySubmitter(Context context, EPerson eperson) throws SQLException {
    return itemDAO.findBySubmitter(context, eperson);
  }

  @Override public Iterator<Item> findBySubmitter(Context context, EPerson eperson, boolean retrieveAllItems) throws SQLException {
    return itemDAO.findBySubmitter(context, eperson, retrieveAllItems);
  }

  @Override public Iterator<Item> findBySubmitterDateSorted(Context context, EPerson eperson, Integer limit) throws SQLException {
    MetadataField metadataField = metadataFieldService.findByElement(context, MetadataSchemaEnum.DC.getName(), "date", "accessioned");
    if (metadataField == null) {
      throw new IllegalArgumentException("Required metadata field \'" + MetadataSchemaEnum.DC.getName() + ".date.accessioned\' doesn\'t exist!");
    }
    return itemDAO.findBySubmitter(context, eperson, metadataField, limit);
  }

  @Override public Iterator<Item> findByCollection(Context context, Collection collection) throws SQLException {
    return findByCollection(context, collection, null, null);
  }

  @Override public Iterator<Item> findByCollection(Context context, Collection collection, Integer limit, Integer offset) throws SQLException {
    return itemDAO.findArchivedByCollection(context, collection, limit, offset);
  }

  @Override public Iterator<Item> findByCollectionMapping(Context context, Collection collection, Integer limit, Integer offset) throws SQLException {
    return itemDAO.findArchivedByCollectionExcludingOwning(context, collection, limit, offset);
  }

  @Override public int countByCollectionMapping(Context context, Collection collection) throws SQLException {
    return itemDAO.countArchivedByCollectionExcludingOwning(context, collection);
  }

  @Override public Iterator<Item> findAllByCollection(Context context, Collection collection) throws SQLException {
    return itemDAO.findAllByCollection(context, collection);
  }

  @Override public Iterator<Item> findAllByCollection(Context context, Collection collection, Integer limit, Integer offset) throws SQLException {
    return itemDAO.findAllByCollection(context, collection, limit, offset);
  }

  @Override public Iterator<Item> findInArchiveOrWithdrawnDiscoverableModifiedSince(Context context, Date since) throws SQLException {
    return itemDAO.findAll(context, true, true, true, since);
  }

  @Override public Iterator<Item> findInArchiveOrWithdrawnNonDiscoverableModifiedSince(Context context, Date since) throws SQLException {
    return itemDAO.findAll(context, true, true, false, since);
  }

  @Override public void updateLastModified(Context context, Item item) throws SQLException, AuthorizeException {
    item.setLastModified(new Date());
    update(context, item);
    context.addEvent(new Event(Event.MODIFY, Constants.ITEM, item.getID(), null, getIdentifiers(context, item)));
  }

  @Override public boolean isIn(Item item, Collection collection) throws SQLException {
    List<Collection> collections = item.getCollections();
    return collections != null && collections.contains(collection);
  }

  @Override public List<Community> getCommunities(Context context, Item item) throws SQLException {
    List<Community> result = new ArrayList<>();
    List<Collection> collections = item.getCollections();
    for (Collection collection : collections) {
      result.addAll(communityService.getAllParents(context, collection));
    }
    return result;
  }

  @Override public List<Bundle> getBundles(Item item, String name) throws SQLException {
    List<Bundle> matchingBundles = new ArrayList<>();
    List<Bundle> bunds = item.getBundles();
    for (Bundle bund : bunds) {
      if (name.equals(bund.getName())) {
        matchingBundles.add(bund);
      }
    }
    return matchingBundles;
  }

  @Override public void addBundle(Context context, Item item, Bundle bundle) throws SQLException, AuthorizeException {
    authorizeService.authorizeAction(context, item, Constants.ADD);
    log.info(LogHelper.getHeader(context, "add_bundle", "item_id=" + item.getID() + ",bundle_id=" + bundle.getID()));
    if (item.getBundles().contains(bundle)) {
      return;
    }
    authorizeService.inheritPolicies(context, item, bundle);
    item.addBundle(bundle);
    bundle.addItem(item);
    context.addEvent(new Event(Event.ADD, Constants.ITEM, item.getID(), Constants.BUNDLE, bundle.getID(), bundle.getName(), getIdentifiers(context, item)));
  }

  @Override public void removeBundle(Context context, Item item, Bundle bundle) throws SQLException, AuthorizeException, IOException {
    authorizeService.authorizeAction(context, item, Constants.REMOVE);
    log.info(LogHelper.getHeader(context, "remove_bundle", "item_id=" + item.getID() + ",bundle_id=" + bundle.getID()));
    context.addEvent(new Event(Event.REMOVE, Constants.ITEM, item.getID(), Constants.BUNDLE, bundle.getID(), bundle.getName(), getIdentifiers(context, item)));
    bundleService.delete(context, bundle);
  }

  @Override public Bitstream createSingleBitstream(Context context, InputStream is, Item item, String name) throws AuthorizeException, IOException, SQLException {
    Bundle bnd = bundleService.create(context, item, name);
    Bitstream bitstream = bitstreamService.create(context, bnd, is);
    addBundle(context, item, bnd);
    return bitstream;
  }

  @Override public Bitstream createSingleBitstream(Context context, InputStream is, Item item) throws AuthorizeException, IOException, SQLException {
    return createSingleBitstream(context, is, item, "ORIGINAL");
  }

  @Override public List<Bitstream> getNonInternalBitstreams(Context context, Item item) throws SQLException {
    List<Bitstream> bitstreamList = new ArrayList<>();
    List<Bundle> bunds = item.getBundles();
    for (Bundle bund : bunds) {
      List<Bitstream> bitstreams = bund.getBitstreams();
      for (Bitstream bitstream : bitstreams) {
        if (!bitstream.getFormat(context).isInternal()) {
          bitstreamList.add(bitstream);
        }
      }
    }
    return bitstreamList;
  }

  protected Item createItem(Context context, UUID uuid) throws SQLException, AuthorizeException {
    Item item;
    if (uuid != null) {
      item = itemDAO.create(context, new Item(uuid));
    } else {
      item = itemDAO.create(context, new Item());
    }
    item.setDiscoverable(true);
    context.turnOffAuthorisationSystem();
    update(context, item);
    context.restoreAuthSystemState();
    context.addEvent(new Event(Event.CREATE, Constants.ITEM, item.getID(), null, getIdentifiers(context, item)));
    log.info(LogHelper.getHeader(context, "create_item", "item_id=" + item.getID()));
    return item;
  }

  protected Item createItem(Context context) throws SQLException, AuthorizeException {
    Item item = itemDAO.create(context, new Item());
    item.setDiscoverable(true);
    context.turnOffAuthorisationSystem();
    update(context, item);
    context.restoreAuthSystemState();
    context.addEvent(new Event(Event.CREATE, Constants.ITEM, item.getID(), null, getIdentifiers(context, item)));
    log.info(LogHelper.getHeader(context, "create_item", "item_id=" + item.getID()));
    return item;
  }

  @Override public void removeDSpaceLicense(Context context, Item item) throws SQLException, AuthorizeException, IOException {
    List<Bundle> bunds = getBundles(item, "LICENSE");
    for (Bundle bund : bunds) {
      removeBundle(context, item, bund);
    }
  }

  @Override public void removeLicenses(Context context, Item item) throws SQLException, AuthorizeException, IOException {
    BitstreamFormat bf = bitstreamFormatService.findByShortDescription(context, "License");
    int licensetype = bf.getID();
    List<Bundle> bunds = item.getBundles();
    for (Bundle bund : bunds) {
      boolean removethisbundle = false;
      List<Bitstream> bits = bund.getBitstreams();
      for (Bitstream bit : bits) {
        BitstreamFormat bft = bit.getFormat(context);
        if (bft.getID() == licensetype) {
          removethisbundle = true;
        }
      }
      if (removethisbundle) {
        removeBundle(context, item, bund);
      }
    }
  }

  @Override public void update(Context context, Item item) throws SQLException, AuthorizeException {
    if (!canEdit(context, item)) {
      authorizeService.authorizeAction(context, item, Constants.WRITE);
    }
    log.info(LogHelper.getHeader(context, "update_item", "item_id=" + item.getID()));
    super.update(context, item);
    int sequence = 0;
    List<Bundle> bunds = item.getBundles();
    for (Bundle bund : bunds) {
      List<Bitstream> streams = bund.getBitstreams();
      for (Bitstream bitstream : streams) {
        if (bitstream.getSequenceID() > sequence) {
          sequence = bitstream.getSequenceID();
        }
      }
    }
    sequence++;
    for (Bundle bund : bunds) {
      List<Bitstream> streams = bund.getBitstreams();
      for (Bitstream stream : streams) {
        if (stream.getSequenceID() < 0) {
          stream.setSequenceID(sequence);
          sequence++;
          bitstreamService.update(context, stream);
        }
      }
    }
    if (item.isMetadataModified() || item.isModified()) {
      item.setLastModified(new Date());
      itemDAO.save(context, item);
      if (item.isMetadataModified()) {
        context.addEvent(new Event(Event.MODIFY_METADATA, item.getType(), item.getID(), item.getDetails(), getIdentifiers(context, item)));
      }
      context.addEvent(new Event(Event.MODIFY, Constants.ITEM, item.getID(), null, getIdentifiers(context, item)));
      item.clearModified();
      item.clearDetails();
    }
  }

  @Override public void withdraw(Context context, Item item) throws SQLException, AuthorizeException {
    AuthorizeUtil.authorizeWithdrawItem(context, item);
    String timestamp = DCDate.getCurrent().toString();
    EPerson e = context.getCurrentUser();
    StringBuilder prov = new StringBuilder();
    prov.append("Item withdrawn by ").append(e.getFullName()).append(" (").append(e.getEmail()).append(") on ").append(timestamp).append("\n").append("Item was in collections:\n");
    List<Collection> colls = item.getCollections();
    for (Collection coll : colls) {
      prov.append(coll.getName()).append(" (ID: ").append(coll.getID()).append(")\n");
    }
    item.setWithdrawn(true);
    item.setArchived(false);
    prov.append(installItemService.getBitstreamProvenanceMessage(context, item));
    addMetadata(context, item, MetadataSchemaEnum.DC.getName(), "description", "provenance", "en", prov.toString());
    update(context, item);
    context.addEvent(new Event(Event.MODIFY, Constants.ITEM, item.getID(), "WITHDRAW", getIdentifiers(context, item)));
    authorizeService.switchPoliciesAction(context, item, Constants.READ, Constants.WITHDRAWN_READ);
    for (Bundle bnd : item.getBundles()) {
      authorizeService.switchPoliciesAction(context, bnd, Constants.READ, Constants.WITHDRAWN_READ);
      for (Bitstream bs : bnd.getBitstreams()) {
        authorizeService.switchPoliciesAction(context, bs, Constants.READ, Constants.WITHDRAWN_READ);
      }
    }
    log.info(LogHelper.getHeader(context, "withdraw_item", "user=" + e.getEmail() + ",item_id=" + item.getID()));
  }

  @Override public void reinstate(Context context, Item item) throws SQLException, AuthorizeException {
    AuthorizeUtil.authorizeReinstateItem(context, item);
    String timestamp = DCDate.getCurrent().toString();
    List<Collection> colls = item.getCollections();
    EPerson e = context.getCurrentUser();
    StringBuilder prov = new StringBuilder();
    prov.append("Item reinstated by ").append(e.getFullName()).append(" (").append(e.getEmail()).append(") on ").append(timestamp).append("\n").append("Item was in collections:\n");
    for (Collection coll : colls) {
      prov.append(coll.getName()).append(" (ID: ").append(coll.getID()).append(")\n");
    }
    item.setWithdrawn(false);
    item.setArchived(true);
    prov.append(installItemService.getBitstreamProvenanceMessage(context, item));
    addMetadata(context, item, MetadataSchemaEnum.DC.getName(), "description", "provenance", "en", prov.toString());
    update(context, item);
    context.addEvent(new Event(Event.MODIFY, Constants.ITEM, item.getID(), "REINSTATE", getIdentifiers(context, item)));
    for (Bundle bnd : item.getBundles()) {
      authorizeService.switchPoliciesAction(context, bnd, Constants.WITHDRAWN_READ, Constants.READ);
      for (Bitstream bs : bnd.getBitstreams()) {
        authorizeService.switchPoliciesAction(context, bs, Constants.WITHDRAWN_READ, Constants.READ);
      }
    }
    if (authorizeService.getPoliciesActionFilter(context, item, Constants.WITHDRAWN_READ).size() != 0) {
      authorizeService.switchPoliciesAction(context, item, Constants.WITHDRAWN_READ, Constants.READ);
    } else {
      if (colls.size() > 0) {
        adjustItemPolicies(context, item, item.getOwningCollection());
      }
    }
    log.info(LogHelper.getHeader(context, "reinstate_item", "user=" + e.getEmail() + ",item_id=" + item.getID()));
  }

  @Override public void delete(Context context, Item item) throws SQLException, AuthorizeException, IOException {
    authorizeService.authorizeAction(context, item, Constants.DELETE);
    rawDelete(context, item);
  }

  @Override public int getSupportsTypeConstant() {
    return Constants.ITEM;
  }

  protected void rawDelete(Context context, Item item) throws AuthorizeException, SQLException, IOException {
    authorizeService.authorizeAction(context, item, Constants.REMOVE);
    context.addEvent(new Event(Event.DELETE, Constants.ITEM, item.getID(), item.getHandle(), getIdentifiers(context, item)));
    log.info(LogHelper.getHeader(context, "delete_item", "item_id=" + item.getID()));
    for (Relationship relationship : relationshipService.findByItem(context, item, -1, -1, false, false)) {
      relationshipService.forceDelete(context, relationship, false, false);
    }
    removeAllBundles(context, item);
    handleService.unbindHandle(context, item);
    removeVersion(context, item);
    removeOrcidSynchronizationStuff(context, item);
    HarvestedItem hi = harvestedItemService.find(context, item);
    if (hi != null) {
      harvestedItemService.delete(context, hi);
    }
    OrcidToken orcidToken = orcidTokenService.findByProfileItem(context, item);
    if (orcidToken != null) {
      orcidToken.setProfileItem(null);
    }
    item.clearCollections();
    item.setOwningCollection(null);
    itemDAO.delete(context, item);
  }

  @Override public void removeAllBundles(Context context, Item item) throws AuthorizeException, SQLException, IOException {
    Iterator<Bundle> bundles = item.getBundles().iterator();
    while (bundles.hasNext()) {
      Bundle bundle = bundles.next();
      bundles.remove();
      deleteBundle(context, item, bundle);
    }
  }

  protected void deleteBundle(Context context, Item item, Bundle b) throws AuthorizeException, SQLException, IOException {
    authorizeService.authorizeAction(context, item, Constants.REMOVE);
    bundleService.delete(context, b);
    log.info(LogHelper.getHeader(context, "remove_bundle", "item_id=" + item.getID() + ",bundle_id=" + b.getID()));
    context.addEvent(new Event(Event.REMOVE, Constants.ITEM, item.getID(), Constants.BUNDLE, b.getID(), b.getName()));
  }

  protected void removeVersion(Context context, Item item) throws AuthorizeException, SQLException {
    if (versioningService.getVersion(context, item) != null) {
      versioningService.removeVersion(context, item);
    } else {
      try {
        identifierService.delete(context, item);
      } catch (IdentifierException e) {
        throw new RuntimeException(e);
      }
    }
  }

  @Override public boolean isOwningCollection(Item item, Collection collection) {
    Collection owningCollection = item.getOwningCollection();
    return owningCollection != null && collection.getID().equals(owningCollection.getID());
  }

  @Override public void replaceAllItemPolicies(Context context, Item item, List<ResourcePolicy> newpolicies) throws SQLException, AuthorizeException {
    authorizeService.removeAllPolicies(context, item);
    authorizeService.addPolicies(context, newpolicies, item);
  }

  @Override public void replaceAllBitstreamPolicies(Context context, Item item, List<ResourcePolicy> newpolicies) throws SQLException, AuthorizeException {
    List<Bundle> bunds = item.getBundles();
    for (Bundle mybundle : bunds) {
      bundleService.replaceAllBitstreamPolicies(context, mybundle, newpolicies);
    }
  }

  @Override public void removeGroupPolicies(Context context, Item item, Group group) throws SQLException, AuthorizeException {
    authorizeService.removeGroupPolicies(context, item, group);
    List<Bundle> bunds = item.getBundles();
    for (Bundle mybundle : bunds) {
      List<Bitstream> bs = mybundle.getBitstreams();
      for (Bitstream bitstream : bs) {
        authorizeService.removeGroupPolicies(context, bitstream, group);
      }
      authorizeService.removeGroupPolicies(context, mybundle, group);
    }
  }

  @Override public void inheritCollectionDefaultPolicies(Context context, Item item, Collection collection) throws SQLException, AuthorizeException {
    adjustItemPolicies(context, item, collection);
    adjustBundleBitstreamPolicies(context, item, collection);
    log.debug(LogHelper.getHeader(context, "item_inheritCollectionDefaultPolicies", "item_id=" + item.getID()));
  }

  @Override public void adjustBundleBitstreamPolicies(Context context, Item item, Collection collection) throws SQLException, AuthorizeException {
    List<ResourcePolicy> defaultCollectionPolicies = authorizeService.getPoliciesActionFilter(context, collection, Constants.DEFAULT_BITSTREAM_READ);
    List<ResourcePolicy> defaultItemPolicies = authorizeService.findPoliciesByDSOAndType(context, item, ResourcePolicy.TYPE_CUSTOM);
    if (defaultCollectionPolicies.size() < 1) {
      throw new SQLException("Collection " + collection.getID() + " (" + collection.getHandle() + ")" + " has no default bitstream READ policies");
    }
    List<Bundle> bunds = item.getBundles();
    for (Bundle mybundle : bunds) {
      authorizeService.removeAllPoliciesByDSOAndType(context, mybundle, ResourcePolicy.TYPE_SUBMISSION);
      authorizeService.removeAllPoliciesByDSOAndType(context, mybundle, ResourcePolicy.TYPE_WORKFLOW);
      addCustomPoliciesNotInPlace(context, mybundle, defaultItemPolicies);
      addDefaultPoliciesNotInPlace(context, mybundle, defaultCollectionPolicies);
      for (Bitstream bitstream : mybundle.getBitstreams()) {
        authorizeService.removeAllPoliciesByDSOAndType(context, bitstream, ResourcePolicy.TYPE_SUBMISSION);
        authorizeService.removeAllPoliciesByDSOAndType(context, bitstream, ResourcePolicy.TYPE_WORKFLOW);
        addCustomPoliciesNotInPlace(context, bitstream, defaultItemPolicies);
        addDefaultPoliciesNotInPlace(context, bitstream, defaultCollectionPolicies);
      }
    }
  }

  @Override public void adjustItemPolicies(Context context, Item item, Collection collection) throws SQLException, AuthorizeException {
    List<ResourcePolicy> defaultCollectionPolicies = authorizeService.getPoliciesActionFilter(context, collection, Constants.DEFAULT_ITEM_READ);
    if (defaultCollectionPolicies.size() < 1) {
      throw new SQLException("Collection " + collection.getID() + " (" + collection.getHandle() + ")" + " has no default item READ policies");
    }
    try {
      context.turnOffAuthorisationSystem();
      authorizeService.removeAllPoliciesByDSOAndType(context, item, ResourcePolicy.TYPE_SUBMISSION);
      authorizeService.removeAllPoliciesByDSOAndType(context, item, ResourcePolicy.TYPE_WORKFLOW);
      addDefaultPoliciesNotInPlace(context, item, defaultCollectionPolicies);
    }  finally {
      context.restoreAuthSystemState();
    }
  }

  @Override public void move(Context context, Item item, Collection from, Collection to) throws SQLException, AuthorizeException, IOException {
    if (from.equals(to)) {
      return;
    }
    this.move(context, item, from, to, false);
  }

  @Override public void move(Context context, Item item, Collection from, Collection to, boolean inheritDefaultPolicies) throws SQLException, AuthorizeException, IOException {
    if (!canEdit(context, item)) {
      authorizeService.authorizeAction(context, item, Constants.WRITE);
    }
    collectionService.addItem(context, to, item);
    collectionService.removeItem(context, from, item);
    if (isOwningCollection(item, from)) {
      log.info(LogHelper.getHeader(context, "move_item", "item_id=" + item.getID() + ", from " + "collection_id=" + from.getID() + " to " + "collection_id=" + to.getID()));
      item.setOwningCollection(to);
      if (inheritDefaultPolicies) {
        log.info(LogHelper.getHeader(context, "move_item", "Updating item with inherited policies"));
        inheritCollectionDefaultPolicies(context, item, to);
      }
      context.turnOffAuthorisationSystem();
      update(context, item);
      context.restoreAuthSystemState();
    } else {
      context.addEvent(new Event(Event.MODIFY, Constants.ITEM, item.getID(), null, getIdentifiers(context, item)));
    }
  }

  @Override public boolean hasUploadedFiles(Item item) throws SQLException {
    List<Bundle> bundles = getBundles(item, "ORIGINAL");
    for (Bundle bundle : bundles) {
      if (CollectionUtils.isNotEmpty(bundle.getBitstreams())) {
        return true;
      }
    }
    return false;
  }

  @Override public List<Collection> getCollectionsNotLinked(Context context, Item item) throws SQLException {
    List<Collection> allCollections = collectionService.findAll(context);
    List<Collection> linkedCollections = item.getCollections();
    List<Collection> notLinkedCollections = new ArrayList<>(allCollections.size() - linkedCollections.size());
    if ((allCollections.size() - linkedCollections.size()) == 0) {
      return notLinkedCollections;
    }
    for (Collection collection : allCollections) {
      boolean alreadyLinked = false;
      for (Collection linkedCommunity : linkedCollections) {
        if (collection.getID().equals(linkedCommunity.getID())) {
          alreadyLinked = true;
          break;
        }
      }
      if (!alreadyLinked) {
        notLinkedCollections.add(collection);
      }
    }
    return notLinkedCollections;
  }

  @Override public boolean canEdit(Context context, Item item) throws SQLException {
    if (authorizeService.authorizeActionBoolean(context, item, Constants.WRITE)) {
      return true;
    }
    if (item.getOwningCollection() == null) {
      if (!isInProgressSubmission(context, item)) {
        return true;
      } else {
        return false;
      }
    }
    return collectionService.canEditBoolean(context, item.getOwningCollection(), false);
  }

  /**
     * Check if the item is an inprogress submission
     *
     * @param context The relevant DSpace Context.
     * @param item    item to check
     * @return <code>true</code> if the item is an inprogress submission, i.e. a WorkspaceItem or WorkflowItem
     * @throws SQLException An exception that provides information on a database access error or other errors.
     */
  public boolean isInProgressSubmission(Context context, Item item) throws SQLException {
    return workspaceItemService.findByItem(context, item) != null || workflowItemService.findByItem(context, item) != null;
  }

  /**
     * Add the default policies, which have not been already added to the given DSpace object
     *
     * @param context                   The relevant DSpace Context.
     * @param dso                       The DSpace Object to add policies to
     * @param defaultCollectionPolicies list of policies
     * @throws SQLException       An exception that provides information on a database access error or other errors.
     * @throws AuthorizeException Exception indicating the current user of the context does not have permission
     *                            to perform a particular action.
     */
  protected void addDefaultPoliciesNotInPlace(Context context, DSpaceObject dso, List<ResourcePolicy> defaultCollectionPolicies) throws SQLException, AuthorizeException {
    boolean appendMode = configurationService.getBooleanProperty("core.authorization.installitem.inheritance-read.append-mode", false);
    for (ResourcePolicy defaultPolicy : defaultCollectionPolicies) {
      if (!authorizeService.isAnIdenticalPolicyAlreadyInPlace(context, dso, defaultPolicy.getGroup(), Constants.READ, defaultPolicy.getID()) && ((!appendMode && this.isNotAlreadyACustomRPOfThisTypeOnDSO(context, dso)) || (appendMode && this.shouldBeAppended(context, dso, defaultPolicy)))) {
        ResourcePolicy newPolicy = resourcePolicyService.clone(context, defaultPolicy);
        newPolicy.setdSpaceObject(dso);
        newPolicy.setAction(Constants.READ);
        newPolicy.setRpType(ResourcePolicy.TYPE_INHERITED);
        resourcePolicyService.update(context, newPolicy);
      }
    }
  }

  private void addCustomPoliciesNotInPlace(Context context, DSpaceObject dso, List<ResourcePolicy> customPolicies) throws SQLException, AuthorizeException {
    boolean customPoliciesAlreadyInPlace = authorizeService.findPoliciesByDSOAndType(context, dso, ResourcePolicy.TYPE_CUSTOM).size() > 0;
    if (!customPoliciesAlreadyInPlace) {
      authorizeService.addPolicies(context, customPolicies, dso);
    }
  }

  /**
     * Check whether or not there is already an RP on the given dso, which has actionId={@link Constants.READ} and
     * resourceTypeId={@link ResourcePolicy.TYPE_CUSTOM}
     *
     * @param context DSpace context
     * @param dso     DSpace object to check for custom read RP
     * @return True if there is no RP on the item with custom read RP, otherwise false
     * @throws SQLException If something goes wrong retrieving the RP on the DSO
     */
  private boolean isNotAlreadyACustomRPOfThisTypeOnDSO(Context context, DSpaceObject dso) throws SQLException {
    List<ResourcePolicy> readRPs = resourcePolicyService.find(context, dso, Constants.READ);
    for (ResourcePolicy readRP : readRPs) {
      if (readRP.getRpType() != null && readRP.getRpType().equals(ResourcePolicy.TYPE_CUSTOM)) {
        return false;
      }
    }
    return true;
  }

  /**
     * Check if the provided default policy should be appended or not to the final
     * item. If an item has at least one custom READ policy any anonymous READ
     * policy with empty start/end date should be skipped
     * 
     * @param context       DSpace context
     * @param dso           DSpace object to check for custom read RP
     * @param defaultPolicy The policy to check
     * @return
     * @throws SQLException If something goes wrong retrieving the RP on the DSO
     */
  private boolean shouldBeAppended(Context context, DSpaceObject dso, ResourcePolicy defaultPolicy) throws SQLException {
    boolean hasCustomPolicy = resourcePolicyService.find(context, dso, Constants.READ).stream().filter((rp) -> (Objects.nonNull(rp.getRpType()) && Objects.equals(rp.getRpType(), ResourcePolicy.TYPE_CUSTOM))).findFirst().isPresent();
    boolean isAnonimousGroup = Objects.nonNull(defaultPolicy.getGroup()) && StringUtils.equals(defaultPolicy.getGroup().getName(), Group.ANONYMOUS);
    boolean datesAreNull = Objects.isNull(defaultPolicy.getStartDate()) && Objects.isNull(defaultPolicy.getEndDate());
    return !(hasCustomPolicy && isAnonimousGroup && datesAreNull);
  }

  /**
     * Returns an iterator of Items possessing the passed metadata field, or only
     * those matching the passed value, if value is not Item.ANY
     *
     * @param context   DSpace context object
     * @param schema    metadata field schema
     * @param element   metadata field element
     * @param qualifier metadata field qualifier
     * @param value     field value or Item.ANY to match any value
     * @return an iterator over the items matching that authority value
     * @throws SQLException       if database error
     *                            An exception that provides information on a database access error or other errors.
     * @throws AuthorizeException if authorization error
     *                            Exception indicating the current user of the context does not have permission
     *                            to perform a particular action.
     */
  @Override public Iterator<Item> findArchivedByMetadataField(Context context, String schema, String element, String qualifier, String value) throws SQLException, AuthorizeException {
    MetadataSchema mds = metadataSchemaService.find(context, schema);
    if (mds == null) {
      throw new IllegalArgumentException("No such metadata schema: " + schema);
    }
    MetadataField mdf = metadataFieldService.findByElement(context, mds, element, qualifier);
    if (mdf == null) {
      throw new IllegalArgumentException("No such metadata field: schema=" + schema + ", element=" + element + ", qualifier=" + qualifier);
    }
    if (Item.ANY.equals(value)) {
      return itemDAO.findByMetadataField(context, mdf, null, true);
    } else {
      return itemDAO.findByMetadataField(context, mdf, value, true);
    }
  }

  @Override public Iterator<Item> findArchivedByMetadataField(Context context, String metadataField, String value) throws SQLException, AuthorizeException {
    String[] mdValueByField = getMDValueByField(metadataField);
    return findArchivedByMetadataField(context, mdValueByField[0], mdValueByField[1], mdValueByField[2], value);
  }

  /**
     * Returns an iterator of Items possessing the passed metadata field, or only
     * those matching the passed value, if value is not Item.ANY
     *
     * @param context   DSpace context object
     * @param schema    metadata field schema
     * @param element   metadata field element
     * @param qualifier metadata field qualifier
     * @param value     field value or Item.ANY to match any value
     * @return an iterator over the items matching that authority value
     * @throws SQLException       if database error
     *                            An exception that provides information on a database access error or other errors.
     * @throws AuthorizeException if authorization error
     *                            Exception indicating the current user of the context does not have permission
     *                            to perform a particular action.
     * @throws IOException        if IO error
     *                            A general class of exceptions produced by failed or interrupted I/O operations.
     */
  @Override public Iterator<Item> findByMetadataField(Context context, String schema, String element, String qualifier, String value) throws SQLException, AuthorizeException, IOException {
    MetadataSchema mds = metadataSchemaService.find(context, schema);
    if (mds == null) {
      throw new IllegalArgumentException("No such metadata schema: " + schema);
    }
    MetadataField mdf = metadataFieldService.findByElement(context, mds, element, qualifier);
    if (mdf == null) {
      throw new IllegalArgumentException("No such metadata field: schema=" + schema + ", element=" + element + ", qualifier=" + qualifier);
    }
    if (Item.ANY.equals(value)) {
      return itemDAO.findByMetadataField(context, mdf, null, true);
    } else {
      return itemDAO.findByMetadataField(context, mdf, value, true);
    }
  }

  @Override public Iterator<Item> findByMetadataQuery(Context context, List<List<MetadataField>> listFieldList, List<String> query_op, List<String> query_val, List<UUID> collectionUuids, String regexClause, int offset, int limit) throws SQLException, AuthorizeException, IOException {
    return itemDAO.findByMetadataQuery(context, listFieldList, query_op, query_val, collectionUuids, regexClause, offset, limit);
  }

  @Override public DSpaceObject getAdminObject(Context context, Item item, int action) throws SQLException {
    DSpaceObject adminObject = null;
    Collection collection = (Collection) getParentObject(context, item);
    Community community = null;
    if (collection != null) {
      if (CollectionUtils.isNotEmpty(collection.getCommunities())) {
        community = collection.getCommunities().get(0);
      }
    }
    switch (action) {
      case Constants.ADD:
      if (AuthorizeConfiguration.canItemAdminPerformBitstreamCreation()) {
        adminObject = item;
      } else {
        if (AuthorizeConfiguration.canCollectionAdminPerformBitstreamCreation()) {
          adminObject = collection;
        } else {
          if (AuthorizeConfiguration.canCommunityAdminPerformBitstreamCreation()) {
            adminObject = community;
          }
        }
      }
      break;
      case Constants.REMOVE:
      if (AuthorizeConfiguration.canItemAdminPerformBitstreamDeletion()) {
        adminObject = item;
      } else {
        if (AuthorizeConfiguration.canCollectionAdminPerformBitstreamDeletion()) {
          adminObject = collection;
        } else {
          if (AuthorizeConfiguration.canCommunityAdminPerformBitstreamDeletion()) {
            adminObject = community;
          }
        }
      }
      break;
      case Constants.DELETE:
      adminObject = item;
      break;
      case Constants.WRITE:
      if (item.getOwningCollection() == null) {
        if (AuthorizeConfiguration.canCollectionAdminManageTemplateItem()) {
          adminObject = collection;
        } else {
          if (AuthorizeConfiguration.canCommunityAdminManageCollectionTemplateItem()) {
            adminObject = community;
          }
        }
      } else {
        adminObject = item;
      }
      break;
      default:
      adminObject = item;
      break;
    }
    return adminObject;
  }

  @Override public DSpaceObject getParentObject(Context context, Item item) throws SQLException {
    Collection ownCollection = item.getOwningCollection();
    if (ownCollection != null) {
      return ownCollection;
    } else {
      InProgressSubmission inprogress = ContentServiceFactory.getInstance().getWorkspaceItemService().findByItem(context, item);
      if (inprogress == null) {
        inprogress = WorkflowServiceFactory.getInstance().getWorkflowItemService().findByItem(context, item);
      }
      if (inprogress != null) {
        return inprogress.getCollection();
      }
      return item.getTemplateItemOf();
    }
  }

  @Override public Iterator<Item> findByAuthorityValue(Context context, String schema, String element, String qualifier, String value) throws SQLException, AuthorizeException {
    MetadataSchema mds = metadataSchemaService.find(context, schema);
    if (mds == null) {
      throw new IllegalArgumentException("No such metadata schema: " + schema);
    }
    MetadataField mdf = metadataFieldService.findByElement(context, mds, element, qualifier);
    if (mdf == null) {
      throw new IllegalArgumentException("No such metadata field: schema=" + schema + ", element=" + element + ", qualifier=" + qualifier);
    }
    return itemDAO.findByAuthorityValue(context, mdf, value, true);
  }

  @Override public Iterator<Item> findByMetadataFieldAuthority(Context context, String mdString, String authority) throws SQLException, AuthorizeException {
    String[] elements = getElementsFilled(mdString);
    String schema = elements[0];
    String element = elements[1];
    String qualifier = elements[2];
    MetadataSchema mds = metadataSchemaService.find(context, schema);
    if (mds == null) {
      throw new IllegalArgumentException("No such metadata schema: " + schema);
    }
    MetadataField mdf = metadataFieldService.findByElement(context, mds, element, qualifier);
    if (mdf == null) {
      throw new IllegalArgumentException("No such metadata field: schema=" + schema + ", element=" + element + ", qualifier=" + qualifier);
    }
    return findByAuthorityValue(context, mds.getName(), mdf.getElement(), mdf.getQualifier(), authority);
  }

  @Override public boolean isItemListedForUser(Context context, Item item) {
    try {
      if (authorizeService.isAdmin(context)) {
        return true;
      }
      if (authorizeService.authorizeActionBoolean(context, item, org.dspace.core.Constants.READ)) {
        if (item.isDiscoverable()) {
          return true;
        }
      }
      log.debug("item(" + item.getID() + ") " + item.getName() + " is unlisted.");
      return false;
    } catch (SQLException e) {
      log.error(e.getMessage());
      return false;
    }
  }

  @Override public int countItems(Context context, Collection collection) throws SQLException {
    return itemDAO.countItems(context, collection, true, false);
  }

  @Override public int countAllItems(Context context, Collection collection) throws SQLException {
    return itemDAO.countItems(context, collection, true, false) + itemDAO.countItems(context, collection, false, true);
  }

  @Override public int countItems(Context context, Community community) throws SQLException {
    List<Collection> collections = communityService.getAllCollections(context, community);
    return itemDAO.countItems(context, collections, true, false);
  }

  @Override public int countAllItems(Context context, Community community) throws SQLException {
    List<Collection> collections = communityService.getAllCollections(context, community);
    return itemDAO.countItems(context, collections, true, false) + itemDAO.countItems(context, collections, false, true);
  }

  @Override protected void getAuthoritiesAndConfidences(String fieldKey, Collection collection, List<String> values, List<String> authorities, List<Integer> confidences, int i) {
    Choices c = choiceAuthorityService.getBestMatch(fieldKey, values.get(i), collection, null);
    authorities.add(c.values.length > 0 && c.values[0] != null ? c.values[0].authority : null);
    confidences.add(c.confidence);
  }

  @Override public Item findByIdOrLegacyId(Context context, String id) throws SQLException {
    if (StringUtils.isNumeric(id)) {
      return findByLegacyId(context, Integer.parseInt(id));
    } else {
      return find(context, UUID.fromString(id));
    }
  }

  @Override public Item findByLegacyId(Context context, int id) throws SQLException {
    return itemDAO.findByLegacyId(context, id, Item.class);
  }

  @Override public Iterator<Item> findByLastModifiedSince(Context context, Date last) throws SQLException {
    return itemDAO.findByLastModifiedSince(context, last);
  }

  @Override public int countTotal(Context context) throws SQLException {
    return itemDAO.countRows(context);
  }

  @Override public int countNotArchivedItems(Context context) throws SQLException {
    return itemDAO.countItems(context, false, false);
  }

  @Override public int countArchivedItems(Context context) throws SQLException {
    return itemDAO.countItems(context, true, false);
  }

  @Override public int countWithdrawnItems(Context context) throws SQLException {
    return itemDAO.countItems(context, false, true);
  }

  @Override public boolean canCreateNewVersion(Context context, Item item) throws SQLException {
    if (authorizeService.isAdmin(context, item)) {
      return true;
    }
    if (context.getCurrentUser() != null && context.getCurrentUser().equals(item.getSubmitter())) {
      return configurationService.getPropertyAsType("versioning.submitterCanCreateNewVersion", false);
    }
    return false;
  }

  /**
     * This method will return a list of MetadataValue objects that contains all the regular
     * metadata of the item passed along in the parameters as well as all the virtual metadata
     * which will be generated and processed together with the {@link VirtualMetadataPopulator}
     * by processing the item's relationships
     * @param item         the Item to be processed
     * @param schema       the schema for the metadata field. <em>Must</em> match
     *                     the <code>name</code> of an existing metadata schema.
     * @param element      the element name. <code>DSpaceObject.ANY</code> matches any
     *                     element. <code>null</code> doesn't really make sense as all
     *                     metadata must have an element.
     * @param qualifier    the qualifier. <code>null</code> means unqualified, and
     *                     <code>DSpaceObject.ANY</code> means any qualifier (including
     *                     unqualified.)
     * @param lang         the ISO639 language code, optionally followed by an underscore
     *                     and the ISO3166 country code. <code>null</code> means only
     *                     values with no language are returned, and
     *                     <code>DSpaceObject.ANY</code> means values with any country code or
     *                     no country code are returned.
     * @return
     */
  @Override public List<MetadataValue> getMetadata(Item item, String schema, String element, String qualifier, String lang) {
    return this.getMetadata(item, schema, element, qualifier, lang, true);
  }

  @Override public List<MetadataValue> getMetadata(Item item, String schema, String element, String qualifier, String lang, boolean enableVirtualMetadata) {
    if (!enableVirtualMetadata) {
      log.debug("Called getMetadata for " + item.getID() + " without enableVirtualMetadata");
      return super.getMetadata(item, schema, element, qualifier, lang);
    }
    if (item.isModifiedMetadataCache()) {
      log.debug("Called getMetadata for " + item.getID() + " with invalid cache");
      List<MetadataValue> dbMetadataValues = item.getMetadata();
      List<MetadataValue> fullMetadataValueList = new LinkedList<>();
      fullMetadataValueList.addAll(relationshipMetadataService.getRelationshipMetadata(item, true));
      fullMetadataValueList.addAll(dbMetadataValues);
      item.setCachedMetadata(sortMetadataValueList(fullMetadataValueList));
    }
    log.debug("Called getMetadata for " + item.getID() + " based on cache");
    List<MetadataValue> values = new ArrayList<>();
    for (MetadataValue dcv : item.getCachedMetadata()) {
      if (match(schema, element, qualifier, lang, dcv)) {
        values.add(dcv);
      }
    }
    return values;
  }

  /**
     * Supports moving metadata by adding the metadata value or updating the place of the relationship
     */
  @Override protected void moveSingleMetadataValue(Context context, Item dso, int place, MetadataValue rr) {
    if (rr instanceof RelationshipMetadataValue) {
      try {
        Relationship rs = relationshipService.find(context, ((RelationshipMetadataValue) rr).getRelationshipId());
        if (rs.getLeftItem() == dso) {
          rs.setLeftPlace(place);
        } else {
          rs.setRightPlace(place);
        }
        relationshipService.update(context, rs);
      } catch (Exception e) {
        log.error("An error occurred while moving " + rr.getAuthority() + " for item " + dso.getID(), e);
      }
    } else {
      rr.setPlace(place);
    }
  }

  /**
     * This method will sort the List of MetadataValue objects based on the MetadataSchema, MetadataField Element,
     * MetadataField Qualifier and MetadataField Place in that order.
     * @param listToReturn  The list to be sorted
     * @return The list sorted on those criteria
     */
  private List<MetadataValue> sortMetadataValueList(List<MetadataValue> listToReturn) {
    Comparator<MetadataValue> comparator = Comparator.comparing((metadataValue) -> metadataValue.getMetadataField().getMetadataSchema().getName(), Comparator.nullsFirst(Comparator.naturalOrder()));
    comparator = comparator.thenComparing((metadataValue) -> metadataValue.getMetadataField().getElement(), Comparator.nullsFirst(Comparator.naturalOrder()));
    comparator = comparator.thenComparing((metadataValue) -> metadataValue.getMetadataField().getQualifier(), Comparator.nullsFirst(Comparator.naturalOrder()));
    comparator = comparator.thenComparing((metadataValue) -> metadataValue.getPlace(), Comparator.nullsFirst(Comparator.naturalOrder()));
    Stream<MetadataValue> metadataValueStream = listToReturn.stream().sorted(comparator);
    listToReturn = metadataValueStream.collect(Collectors.toList());
    return listToReturn;
  }

  @Override public MetadataValue addMetadata(Context context, Item dso, String schema, String element, String qualifier, String lang, String value, String authority, int confidence, int place) throws SQLException {
    MetadataField metadataField = metadataFieldService.findByElement(context, schema, element, qualifier);
    if (metadataField == null) {
      throw new SQLException("bad_dublin_core schema=" + schema + "." + element + "." + qualifier + ". Metadata field does not " + "exist!");
    }
    final Supplier<Integer> placeSupplier = () -> place;
    return addMetadata(context, dso, metadataField, lang, Arrays.asList(value), Arrays.asList(authority), Arrays.asList(confidence), placeSupplier).stream().findFirst().orElse(null);
  }

  @Override public String getEntityTypeLabel(Item item) {
    List<MetadataValue> mdvs = getMetadata(item, "dspace", "entity", "type", Item.ANY, false);
    if (mdvs.isEmpty()) {
      return null;
    }
    if (mdvs.size() > 1) {
      log.warn("Item with uuid {}, handle {} has {} entity types ({}), expected 1 entity type", item.getID(), item.getHandle(), mdvs.size(), mdvs.stream().map(MetadataValue::getValue).collect(Collectors.toList()));
    }
    String entityType = mdvs.get(0).getValue();
    if (StringUtils.isBlank(entityType)) {
      return null;
    }
    return entityType;
  }

  @Override public EntityType getEntityType(Context context, Item item) throws SQLException {
    String entityTypeString = getEntityTypeLabel(item);
    if (StringUtils.isBlank(entityTypeString)) {
      return null;
    }
    return entityTypeService.findByEntityType(context, entityTypeString);
  }

  private void removeOrcidSynchronizationStuff(Context context, Item item) throws SQLException, AuthorizeException {
    if (isNotProfileOrOrcidEntity(item)) {
      return;
    }
    context.turnOffAuthorisationSystem();
    try {
      createOrcidQueueRecordsToDeleteOnOrcid(context, item);
      deleteOrcidHistoryRecords(context, item);
      deleteOrcidQueueRecords(context, item);
    }  finally {
      context.restoreAuthSystemState();
    }
  }

  private boolean isNotProfileOrOrcidEntity(Item item) {
    String entityType = getEntityTypeLabel(item);
    return !OrcidEntityType.isValidEntityType(entityType) && !researcherProfileService.getProfileType().equals(entityType);
  }

  private void createOrcidQueueRecordsToDeleteOnOrcid(Context context, Item entity) throws SQLException {
    String entityType = getEntityTypeLabel(entity);
    if (entityType == null || researcherProfileService.getProfileType().equals(entityType)) {
      return;
    }
    Map<Item, String> profileAndPutCodeMap = orcidHistoryService.findLastPutCodes(context, entity);
    for (Item profile : profileAndPutCodeMap.keySet()) {
      if (orcidSynchronizationService.isSynchronizationAllowed(profile, entity)) {
        String putCode = profileAndPutCodeMap.get(profile);
        String title = getMetadataFirstValue(entity, "dc", "title", null, Item.ANY);
        orcidQueueService.createEntityDeletionRecord(context, profile, title, entityType, putCode);
      }
    }
  }

  private void deleteOrcidHistoryRecords(Context context, Item item) throws SQLException {
    List<OrcidHistory> historyRecords = orcidHistoryService.findByProfileItemOrEntity(context, item);
    for (OrcidHistory historyRecord : historyRecords) {
      if (historyRecord.getProfileItem().equals(item)) {
        orcidHistoryService.delete(context, historyRecord);
      } else {
        historyRecord.setEntity(null);
        orcidHistoryService.update(context, historyRecord);
      }
    }
  }

  private void deleteOrcidQueueRecords(Context context, Item item) throws SQLException {
    List<OrcidQueue> orcidQueueRecords = orcidQueueService.findByProfileItemOrEntity(context, item);
    for (OrcidQueue orcidQueueRecord : orcidQueueRecords) {
      orcidQueueService.delete(context, orcidQueueRecord);
    }
  }
}