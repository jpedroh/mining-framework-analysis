package org.dspace.content.packager;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.Logger;
import org.dspace.authorize.AuthorizeException;
import org.dspace.content.Bitstream;
import org.dspace.content.BitstreamFormat;
import org.dspace.content.Bundle;
import org.dspace.content.Collection;
import org.dspace.content.Community;
import org.dspace.content.DSpaceObject;
import org.dspace.content.InProgressSubmission;
import org.dspace.content.Item;
import org.dspace.content.WorkspaceItem;
import org.dspace.content.crosswalk.CrosswalkException;
import org.dspace.content.crosswalk.MetadataValidationException;
import org.dspace.content.factory.ContentServiceFactory;
import org.dspace.content.service.BitstreamFormatService;
import org.dspace.content.service.BitstreamService;
import org.dspace.content.service.BundleService;
import org.dspace.content.service.CollectionService;
import org.dspace.content.service.CommunityService;
import org.dspace.content.service.ItemService;
import org.dspace.content.service.WorkspaceItemService;
import org.dspace.core.Constants;
import org.dspace.core.Context;
import org.dspace.core.LogHelper;
import org.dspace.handle.factory.HandleServiceFactory;
import org.dspace.handle.service.HandleService;
import org.dspace.services.ConfigurationService;
import org.dspace.services.factory.DSpaceServicesFactory;
import org.dspace.workflow.WorkflowException;
import org.dspace.workflow.factory.WorkflowServiceFactory;
import org.jdom2.Element;

/**
 * Base class for package ingester of METS (Metadata Encoding and Transmission
 * Standard) Packages.<br>
 * See <a href="http://www.loc.gov/standards/mets/">
 * http://www.loc.gov/standards/mets/</a>.
 * <p>
 * This is a generic packager framework intended to be subclassed to create
 * ingesters for more specific METS "profiles". METS is an abstract and flexible
 * framework that can encompass many different kinds of metadata and inner
 * package structures.
 *
 * <p>
 * <b>Package Parameters:</b>
 * <ul>
 * <li><code>validate</code> -- true/false attempt to schema-validate the METS
 * manifest.</li>
 * <li><code>manifestOnly</code> -- package consists only of a manifest
 * document.</li>
 * <li><code>ignoreHandle</code> -- true/false, ignore AIP's idea of handle
 * when ingesting.</li>
 * <li><code>ignoreParent</code> -- true/false, ignore AIP's idea of parent
 * when ingesting.</li>
 * </ul>
 * <p>
 * <b>Configuration Properties:</b>
 * <ul>
 * <li><code>mets.CONFIGNAME.ingest.preserveManifest</code> - if <em>true</em>,
 * the METS manifest itself is preserved in a bitstream named
 * <code>mets.xml</code> in the <code>METADATA</code> bundle. If it is
 * <em>false</em> (the default), the manifest is discarded after ingestion.</li>
 *
 * <li><code>mets.CONFIGNAME.ingest.manifestBitstreamFormat</code> - short name
 * of the bitstream format to apply to the manifest; MUST be specified when
 * preserveManifest is true.</li>
 *
 * <li><code>mets.default.ingest.crosswalk.MD_SEC_NAME</code> = PLUGIN_NAME
 * Establishes a default crosswalk plugin for the given type of metadata in a
 * METS mdSec (e.g. "DC", "MODS"). The plugin may be either a stream or
 * XML-oriented ingestion crosswalk. Subclasses can override the default mapping
 * with their own, substituting their configurationName for "default" in the
 * configuration property key above.</li>
 *
 * <li><code>mets.CONFIGNAME.ingest.useCollectionTemplate</code> - if
 * <em>true</em>, when an item is created, use the collection template. If it is
 * <em>false</em> (the default), any existing collection template is ignored.</li>
 * </ul>
 *
 * @author Larry Stone
 * @author Tim Donohue
 * @see org.dspace.content.packager.METSManifest
 * @see AbstractPackageIngester
 * @see PackageIngester
 */
public abstract class AbstractMETSIngester extends AbstractPackageIngester {
  /**
     * log4j category
     */
  private static final Logger log = org.apache.logging.log4j.LogManager.getLogger(AbstractMETSIngester.class);

  protected final BitstreamService bitstreamService = ContentServiceFactory.getInstance().getBitstreamService();

  protected final BitstreamFormatService bitstreamFormatService = ContentServiceFactory.getInstance().getBitstreamFormatService();

  protected final BundleService bundleService = ContentServiceFactory.getInstance().getBundleService();

  protected final CommunityService communityService = ContentServiceFactory.getInstance().getCommunityService();

  protected final CollectionService collectionService = ContentServiceFactory.getInstance().getCollectionService();

  protected final ItemService itemService = ContentServiceFactory.getInstance().getItemService();

  protected final HandleService handleService = HandleServiceFactory.getInstance().getHandleService();

  protected final WorkspaceItemService workspaceItemService = ContentServiceFactory.getInstance().getWorkspaceItemService();

  protected final ConfigurationService configurationService = DSpaceServicesFactory.getInstance().getConfigurationService();

  protected static final class MdrefManager implements METSManifest.Mdref {
    private File packageFile = null;

    private final PackageParameters params;

    private MdrefManager(File packageFile, PackageParameters params) {
      super();
      this.packageFile = packageFile;
      this.params = params;
    }

    /**
         * Make the contents of an external resource mentioned in an
         * <code>mdRef</code> element available as an <code>InputStream</code>.
         * See the <code>METSManifest.MdRef</code> interface for details.
         *
         * @param mdref the METS mdRef element to locate the input for.
         * @return the input stream of its content.
         * @throws MetadataValidationException if validation error
         * @throws IOException                 if IO error
         * @see METSManifest
         */
    @Override public InputStream getInputStream(Element mdref) throws MetadataValidationException, IOException {
      String path = METSManifest.getFileName(mdref);
      if (packageFile == null) {
        throw new MetadataValidationException("Failed referencing mdRef element, because there is no package specified.");
      }
      return AbstractMETSIngester.getFileInputStream(packageFile, params, path);
    }
  }

  /**
     * Create a new DSpace object out of a METS content package. All contents
     * are dictated by the METS manifest. Package is a ZIP archive (or
     * optionally bare manifest XML document). In a Zip, all files relative to
     * top level and the manifest (as per spec) in mets.xml.
     *
     * @param context DSpace context.
     * @param parent  parent under which to create new object (may be null -- in
     *                which case ingester must determine parent from package or
     *                throw an error).
     * @param pkgFile The package file to ingest
     * @param params  Properties-style list of options (interpreted by each
     *                packager).
     * @param license may be null, which takes default license.
     * @return DSpaceObject created by ingest.
     * @throws PackageValidationException if package validation error
     *                                    if package is unacceptable or there is a fatal error turning
     *                                    it into a DSpaceObject.
     * @throws CrosswalkException         if crosswalk error
     * @throws AuthorizeException         if authorization error
     * @throws SQLException               if database error
     * @throws IOException                if IO error
     * @throws WorkflowException          if workflow error
     */
  @Override public DSpaceObject ingest(Context context, DSpaceObject parent, File pkgFile, PackageParameters params, String license) throws PackageValidationException, CrosswalkException, AuthorizeException, SQLException, IOException, WorkflowException {
    METSManifest manifest = null;
    DSpaceObject dso = null;
    try {
      log.info(LogHelper.getHeader(context, "package_parse", "Parsing package for ingest, file=" + pkgFile.getName()));
      manifest = parsePackage(context, pkgFile, params);
      if (manifest == null) {
        throw new PackageValidationException("No METS Manifest found (filename=" + METSManifest.MANIFEST_FILE + ").  Package is unacceptable!");
      }
      checkManifest(manifest);
      if (!params.restoreModeEnabled() && !params.containsKey("ignoreHandle")) {
        params.addProperty("ignoreHandle", "true");
      }
      if (parent != null && !params.containsKey("ignoreParent")) {
        params.addProperty("ignoreParent", "true");
      }
      dso = ingestObject(context, parent, manifest, pkgFile, params, license);
      if (dso != null) {
        String action = "package_ingest";
        if (params.restoreModeEnabled()) {
          action = "package_restore";
        }
        log.info(LogHelper.getHeader(context, action, "Created new Object, type=" + Constants.typeText[dso.getType()] + ", handle=" + dso.getHandle() + ", dbID=" + String.valueOf(dso.getID())));
        if (params.recursiveModeEnabled()) {
          String[] childFilePaths = manifest.getChildMetsFilePaths();
          for (int i = 0; i < childFilePaths.length; i++) {
            addPackageReference(dso, childFilePaths[i]);
          }
        }
      }
      return dso;
    } catch (SQLException se) {
      dso = null;
      throw se;
    }
  }

  /**
     * Parse a given input package, ultimately returning the METS manifest out
     * of the package. METS manifest is assumed to be a file named 'mets.xml'
     *
     * @param context DSpace Context
     * @param pkgFile package to parse
     * @param params  Ingestion parameters
     * @return parsed out METSManifest
     * @throws IOException                 if IO error
     * @throws SQLException                if database error
     * @throws AuthorizeException          if authorization error
     * @throws MetadataValidationException if metadata validation error
     */
  protected METSManifest parsePackage(Context context, File pkgFile, PackageParameters params) throws IOException, SQLException, AuthorizeException, MetadataValidationException {
    boolean validate = params.getBooleanProperty("validate", false);
    METSManifest manifest = null;
    if (params.getBooleanProperty("manifestOnly", false)) {
      manifest = METSManifest.create(new FileInputStream(pkgFile), validate, getConfigurationName());
    } else {
      try (ZipFile zip = new ZipFile(pkgFile)) {
        ZipEntry manifestEntry = zip.getEntry(METSManifest.MANIFEST_FILE);
        if (manifestEntry != null) {
          manifest = METSManifest.create(zip.getInputStream(manifestEntry), validate, getConfigurationName());
        }
      }
    }
    return manifest;
  }

  /**
     * Ingest/import a single DSpace Object, based on the associated METS
     * Manifest and the parameters passed to the METSIngester
     *
     * @param context  DSpace Context
     * @param parent   Parent DSpace Object
     * @param manifest the parsed METS Manifest
     * @param pkgFile  the full package file (which may include content files if a
     *                 zip)
     * @param params   Parameters passed to METSIngester
     * @param license  DSpace license agreement
     * @return completed result as a DSpace object
     * @throws IOException                 if IO error
     * @throws SQLException                if database error
     * @throws AuthorizeException          if authorization error
     * @throws CrosswalkException          if crosswalk error
     * @throws MetadataValidationException if metadata validation error
     * @throws WorkflowException           if workflow error
     * @throws PackageValidationException  if package validation error
     */
  protected DSpaceObject ingestObject(Context context, DSpaceObject parent, METSManifest manifest, File pkgFile, PackageParameters params, String license) throws IOException, SQLException, AuthorizeException, CrosswalkException, PackageValidationException, WorkflowException {
    int type;
    type = getObjectType(manifest);
    if (type != Constants.SITE && (parent == null || !params.getBooleanProperty("ignoreParent", false))) {
      try {
        parent = getParentObject(context, manifest);
      } catch (UnsupportedOperationException e) {
        if (params.getBooleanProperty("skipIfParentMissing", false)) {
          log.warn(LogHelper.getHeader(context, "package_ingest", "SKIPPING ingest of object \'" + manifest.getObjID() + "\' as parent DSpace Object could not be found. " + "If you are running a recursive ingest, it is likely this " + "object will be created as soon as its parent is created."));
          return null;
        } else {
          throw e;
        }
      }
    }
    String handle = null;
    if (!params.getBooleanProperty("ignoreHandle", false)) {
      handle = getObjectHandle(manifest);
    }
    UUID uuid = getObjectID(manifest);
    DSpaceObject dso;
    try {
      dso = PackageUtils.createDSpaceObject(context, parent, type, handle, uuid, params);
    } catch (SQLException sqle) {
      throw new PackageValidationException("Exception while ingesting " + pkgFile.getPath(), sqle);
    }
    if (dso == null) {
      throw new PackageValidationException("Unable to initialize object specified by package (type=\'" + type + "\', handle=\'" + handle + "\' and parent=\'" + parent.getHandle() + "\').");
    }
    MdrefManager callback = new MdrefManager(pkgFile, params);
    manifest.crosswalkObjectSourceMD(context, params, dso, callback);
    manifest.crosswalkObjectOtherAdminMD(context, params, dso, callback);
    crosswalkObjectDmd(context, dso, manifest, callback, manifest.getItemDmds(), params);
    if (type == Constants.ITEM) {
      PackageUtils.checkItemMetadata((Item) dso);
    }
    if (type == Constants.ITEM) {
      Item item = (Item) dso;
      WorkspaceItem wsi = workspaceItemService.findByItem(context, item);
      Collection collection = item.getOwningCollection();
      if (collection == null) {
        if (wsi != null) {
          collection = wsi.getCollection();
        }
      }
      if (preserveManifest()) {
        addManifestBitstream(context, item, manifest);
      }
      addBitstreams(context, item, manifest, pkgFile, params, callback);
      addLicense(context, item, license, collection, params);
      finishObject(context, dso, params);
      if (wsi != null) {
        PackageUtils.finishCreateItem(context, wsi, handle, params);
      }
    } else {
      if (type == Constants.COLLECTION || type == Constants.COMMUNITY) {
        addContainerLogo(context, dso, manifest, pkgFile, params);
        if (type == Constants.COLLECTION) {
          addTemplateItem(context, dso, manifest, pkgFile, params, callback);
        }
        finishObject(context, dso, params);
      } else {
        if (type == Constants.SITE) {
          finishObject(context, dso, params);
        } else {
          throw new PackageValidationException("Unknown DSpace Object type in package, type=" + String.valueOf(type));
        }
      }
    }
    PackageUtils.updateDSpaceObject(context, dso);
    return dso;
  }

  /**
     * Replace the contents of a single DSpace Object, based on the associated
     * METS Manifest and the parameters passed to the METSIngester.
     *
     * @param context  DSpace Context
     * @param dso      DSpace Object to replace
     * @param manifest the parsed METS Manifest
     * @param pkgFile  the full package file (which may include content files if a
     *                 zip)
     * @param params   Parameters passed to METSIngester
     * @param license  DSpace license agreement
     * @return completed result as a DSpace object
     * @throws IOException                 if IO error
     * @throws SQLException                if database error
     * @throws AuthorizeException          if authorization error
     * @throws CrosswalkException          if crosswalk error
     * @throws MetadataValidationException if metadata validation error
     * @throws PackageValidationException  if package validation error
     */
  protected DSpaceObject replaceObject(Context context, DSpaceObject dso, METSManifest manifest, File pkgFile, PackageParameters params, String license) throws IOException, SQLException, AuthorizeException, CrosswalkException, MetadataValidationException, PackageValidationException {
    int manifestType = getObjectType(manifest);
    if (manifestType != dso.getType()) {
      throw new PackageValidationException("The object type of the METS manifest (" + Constants.typeText[manifestType] + ") does not match up with the object type (" + Constants.typeText[dso.getType()] + ") of the DSpaceObject to be replaced!");
    }
    if (log.isDebugEnabled()) {
      log.debug("Object to be replaced (handle=" + dso.getHandle() + ") is " + Constants.typeText[dso.getType()] + " id=" + dso.getID());
    }
    PackageUtils.removeAllBitstreams(context, dso);
    PackageUtils.clearAllMetadata(context, dso);
    MdrefManager callback = new MdrefManager(pkgFile, params);
    manifest.crosswalkObjectSourceMD(context, params, dso, callback);
    manifest.crosswalkObjectOtherAdminMD(context, params, dso, callback);
    if (dso.getType() == Constants.ITEM) {
      Item item = (Item) dso;
      if (preserveManifest()) {
        addManifestBitstream(context, item, manifest);
      }
      addBitstreams(context, item, manifest, pkgFile, params, callback);
      Collection owningCollection = (Collection) ContentServiceFactory.getInstance().getDSpaceObjectService(dso).getParentObject(context, dso);
      if (owningCollection == null) {
        InProgressSubmission inProgressSubmission = workspaceItemService.findByItem(context, item);
        if (inProgressSubmission == null) {
          inProgressSubmission = WorkflowServiceFactory.getInstance().getWorkflowItemService().findByItem(context, item);
        }
        owningCollection = inProgressSubmission.getCollection();
      }
      addLicense(context, item, license, owningCollection, params);
    } else {
      if (dso.getType() == Constants.COLLECTION || dso.getType() == Constants.COMMUNITY) {
        addContainerLogo(context, dso, manifest, pkgFile, params);
      } else {
        if (dso.getType() == Constants.SITE) {
        }
      }
    }
    crosswalkObjectDmd(context, dso, manifest, callback, manifest.getItemDmds(), params);
    if (dso.getType() == Constants.ITEM) {
      PackageUtils.checkItemMetadata((Item) dso);
    }
    finishObject(context, dso, params);
    PackageUtils.updateDSpaceObject(context, dso);
    return dso;
  }

  /**
     * Add Bitstreams to an Item, based on the files listed in the METS Manifest
     *
     * @param context       DSpace Context
     * @param item          DSpace Item
     * @param manifest      METS Manifest
     * @param pkgFile       the full package file (which may include content files if a
     *                      zip)
     * @param params        Ingestion Parameters
     * @param mdRefCallback MdrefManager storing info about mdRefs in manifest
     * @throws SQLException                if database error
     * @throws IOException                 if IO error
     * @throws AuthorizeException          if authorization error
     * @throws MetadataValidationException if metadata validation error
     * @throws CrosswalkException          if crosswalk error
     * @throws PackageValidationException  if package validation error
     */
  protected void addBitstreams(Context context, Item item, METSManifest manifest, File pkgFile, PackageParameters params, MdrefManager mdRefCallback) throws SQLException, IOException, AuthorizeException, MetadataValidationException, CrosswalkException, PackageValidationException {
    String primaryID = null;
    Element primaryFile = manifest.getPrimaryOrLogoBitstream();
    if (primaryFile != null) {
      primaryID = primaryFile.getAttributeValue("ID");
      if (log.isDebugEnabled()) {
        log.debug("Got primary bitstream file ID=\"" + primaryID + "\"");
      }
    }
    List<Element> manifestContentFiles = manifest.getContentFiles();
    List<Element> manifestBundleFiles = manifest.getBundleFiles();
    boolean setPrimaryBitstream = false;
    BitstreamFormat unknownFormat = bitstreamFormatService.findUnknown(context);
    for (Iterator<Element> mi = manifestContentFiles.iterator(); mi.hasNext(); ) {
      Element mfile = mi.next();
      String mfileID = mfile.getAttributeValue("ID");
      if (mfileID == null) {
        throw new PackageValidationException("Invalid METS Manifest: file element without ID attribute.");
      }
      String path = METSManifest.getFileName(mfile);
      InputStream fileStream = getFileInputStream(pkgFile, params, path);
      String bundleName = METSManifest.getBundleName(mfile);
      Bundle bundle;
      List<Bundle> bns = itemService.getBundles(item, bundleName);
      if (CollectionUtils.isNotEmpty(bns)) {
        bundle = bns.get(0);
      } else {
        bundle = bundleService.create(context, item, bundleName);
      }
      Bitstream bitstream = bitstreamService.create(context, bundle, fileStream);
      bitstream.setName(context, path);
      String seqID = mfile.getAttributeValue("SEQ");
      if (seqID != null && !seqID.isEmpty()) {
        bitstream.setSequenceID(Integer.parseInt(seqID));
      }
      manifest.crosswalkBitstream(context, params, bitstream, mfileID, mdRefCallback);
      if (primaryID != null && mfileID.equals(primaryID)) {
        bundle.setPrimaryBitstreamID(bitstream);
        bundleService.update(context, bundle);
        setPrimaryBitstream = true;
      }
      finishBitstream(context, bitstream, mfile, manifest, params);
      if (bitstream.getFormat(context).equals(unknownFormat)) {
        if (log.isDebugEnabled()) {
          log.debug("Guessing format of Bitstream left un-set: " + bitstream.toString());
        }
        String mimeType = mfile.getAttributeValue("MIMETYPE");
        BitstreamFormat bf = (mimeType == null) ? null : bitstreamFormatService.findByMIMEType(context, mimeType);
        if (bf == null) {
          bf = bitstreamFormatService.guessFormat(context, bitstream);
        }
        bitstreamService.setFormat(context, bitstream, bf);
      }
      bitstreamService.update(context, bitstream);
    }
    for (Iterator<Element> mi = manifestBundleFiles.iterator(); mi.hasNext(); ) {
      Element mfile = mi.next();
      String bundleName = METSManifest.getBundleName(mfile, false);
      Bundle bundle;
      List<Bundle> bns = itemService.getBundles(item, bundleName);
      if (CollectionUtils.isNotEmpty(bns)) {
        bundle = bns.get(0);
      } else {
        bundle = bundleService.create(context, item, bundleName);
      }
      String mfileGrp = mfile.getAttributeValue("ADMID");
      if (mfileGrp != null) {
        manifest.crosswalkBundle(context, params, bundle, mfileGrp, mdRefCallback);
      } else {
        if (log.isDebugEnabled()) {
          log.debug("Ingesting bundle with no ADMID, not crosswalking bundle metadata");
        }
      }
      bundleService.update(context, bundle);
    }
    if (primaryID != null && !setPrimaryBitstream) {
      log.warn("Could not find primary bitstream file ID=\"" + primaryID + "\" in manifest file \"" + pkgFile.getAbsolutePath() + "\"");
    }
  }

  /**
     * Save/Preserve the METS Manifest as a Bitstream attached to the given
     * DSpace item.
     *
     * @param context  DSpace Context
     * @param item     DSpace Item
     * @param manifest The METS Manifest
     * @throws IOException                if IO error
     * @throws SQLException               if database error
     * @throws AuthorizeException         if authorization error
     * @throws PackageValidationException if package validation error
     */
  protected void addManifestBitstream(Context context, Item item, METSManifest manifest) throws IOException, SQLException, AuthorizeException, PackageValidationException {
    Bundle mdBundle = bundleService.create(context, item, Constants.METADATA_BUNDLE_NAME);
    Bitstream manifestBitstream = bitstreamService.create(context, mdBundle, manifest.getMetsAsStream());
    manifestBitstream.setName(context, METSManifest.MANIFEST_FILE);
    manifestBitstream.setSource(context, METSManifest.MANIFEST_FILE);
    bitstreamService.update(context, manifestBitstream);
    String fmtName = getManifestBitstreamFormat();
    if (fmtName == null) {
      throw new PackageValidationException("Configuration Error: No Manifest BitstreamFormat configured for METS ingester type=" + getConfigurationName());
    }
    BitstreamFormat manifestFormat = PackageUtils.findOrCreateBitstreamFormat(context, fmtName, "application/xml", fmtName + " package manifest");
    manifestBitstream.setFormat(context, manifestFormat);
    bitstreamService.update(context, manifestBitstream);
  }

  /**
     * Add a Logo to a Community or Collection container object based on a METS
     * Manifest.
     *
     * @param context  DSpace Context
     * @param dso      DSpace Container Object
     * @param manifest METS Manifest
     * @param pkgFile  the full package file (which may include content files if a
     *                 zip)
     * @param params   Ingestion Parameters
     * @throws SQLException                if database error
     * @throws IOException                 if IO error
     * @throws AuthorizeException          if authorization error
     * @throws MetadataValidationException if metadata validation error
     * @throws PackageValidationException  if package validation error
     */
  protected void addContainerLogo(Context context, DSpaceObject dso, METSManifest manifest, File pkgFile, PackageParameters params) throws SQLException, IOException, AuthorizeException, MetadataValidationException, PackageValidationException {
    Element logoRef = manifest.getPrimaryOrLogoBitstream();
    if (logoRef != null) {
      String logoID = logoRef.getAttributeValue("ID");
      for (Iterator<Element> mi = manifest.getContentFiles().iterator(); mi.hasNext(); ) {
        Element mfile = mi.next();
        if (logoID.equals(mfile.getAttributeValue("ID"))) {
          String path = METSManifest.getFileName(mfile);
          InputStream fileStream = getFileInputStream(pkgFile, params, path);
          if (dso.getType() == Constants.COLLECTION) {
            collectionService.setLogo(context, ((Collection) dso), fileStream);
          } else {
            communityService.setLogo(context, ((Community) dso), fileStream);
          }
          break;
        }
      }
    }
  }

  /**
     * Add a Template Item to a Collection container object based on a METS
     * Manifest.
     *
     * @param context  DSpace Context
     * @param dso      DSpace Container Object
     * @param manifest METS Manifest
     * @param pkgFile  the full package file (which may include content files if a
     *                 zip)
     * @param params   Ingestion Parameters
     * @param callback the MdrefManager (manages all external metadata files
     *                 referenced by METS <code>mdref</code> elements)
     * @throws SQLException                if database error
     * @throws IOException                 if IO error
     * @throws AuthorizeException          if authorization error
     * @throws MetadataValidationException if metadata validation error
     * @throws PackageValidationException  if package validation error
     */
  protected void addTemplateItem(Context context, DSpaceObject dso, METSManifest manifest, File pkgFile, PackageParameters params, MdrefManager callback) throws SQLException, IOException, AuthorizeException, CrosswalkException, PackageValidationException {
    if (dso.getType() != Constants.COLLECTION) {
      return;
    }
    Collection collection = (Collection) dso;
    List childObjList = manifest.getChildObjDivs();
    if (childObjList != null && !childObjList.isEmpty()) {
      Element templateItemDiv = null;
      Iterator childIterator = childObjList.iterator();
      while (childIterator.hasNext()) {
        Element childDiv = (Element) childIterator.next();
        String childType = childDiv.getAttributeValue("TYPE");
        if (childType.contains(Constants.typeText[Constants.ITEM]) && childType.endsWith(AbstractMETSDisseminator.TEMPLATE_TYPE_SUFFIX)) {
          templateItemDiv = childDiv;
          break;
        }
      }
      if (templateItemDiv != null) {
        String templateDmdIds = templateItemDiv.getAttributeValue("DMDID");
        if (templateDmdIds != null) {
          itemService.createTemplateItem(context, collection);
          Item templateItem = collection.getTemplateItem();
          Element[] templateDmds = manifest.getDmdElements(templateDmdIds);
          crosswalkObjectDmd(context, templateItem, manifest, callback, templateDmds, params);
          PackageUtils.updateDSpaceObject(context, templateItem);
        }
      }
    }
  }

  /**
     * Replace an existing DSpace object with the contents of a METS-based
     * package. All contents are dictated by the METS manifest. Package is a ZIP
     * archive (or optionally bare manifest XML document). In a Zip, all files
     * relative to top level and the manifest (as per spec) in mets.xml.
     * <P>
     * This method is similar to ingest(), except that if the object already
     * exists in DSpace, it is emptied of files and metadata. The METS-based
     * package is then used to ingest new values for these.
     *
     * @param context      DSpace Context
     * @param dsoToReplace DSpace Object to be replaced (may be null if it will be
     *                     specified in the METS manifest itself)
     * @param pkgFile      The package file to ingest
     * @param params       Parameters passed from the packager script
     * @return DSpaceObject created by ingest.
     * @throws PackageValidationException if package validation error
     *                                    if package is unacceptable or there is a fatal error turning
     *                                    it into a DSpace Object.
     * @throws IOException                if IO error
     * @throws SQLException               if database error
     * @throws AuthorizeException         if authorization error
     * @throws CrosswalkException         if crosswalk error
     * @throws WorkflowException          if workflow error
     */
  @Override public DSpaceObject replace(Context context, DSpaceObject dsoToReplace, File pkgFile, PackageParameters params) throws PackageValidationException, CrosswalkException, AuthorizeException, SQLException, IOException, WorkflowException {
    METSManifest manifest = null;
    DSpaceObject dso = null;
    try {
      log.info(LogHelper.getHeader(context, "package_parse", "Parsing package for replace, file=" + pkgFile.getName()));
      manifest = parsePackage(context, pkgFile, params);
      if (manifest == null) {
        throw new PackageValidationException("No METS Manifest found (filename=" + METSManifest.MANIFEST_FILE + ").  Package is unacceptable!");
      }
      if (dsoToReplace == null) {
        String handleURI = manifest.getObjID();
        String handle = decodeHandleURN(handleURI);
        try {
          dsoToReplace = handleService.resolveToObject(context, handle);
        } catch (IllegalStateException ie) {
        }
      }
      if (dsoToReplace == null) {
        dso = ingestObject(context, null, manifest, pkgFile, params, null);
        if (dso != null) {
          log.info(LogHelper.getHeader(context, "package_replace", "Created new Object, type=" + Constants.typeText[dso.getType()] + ", handle=" + dso.getHandle() + ", dbID=" + String.valueOf(dso.getID())));
        }
      } else {
        dso = replaceObject(context, dsoToReplace, manifest, pkgFile, params, null);
        log.info(LogHelper.getHeader(context, "package_replace", "Replaced Object, type=" + Constants.typeText[dso.getType()] + ", handle=" + dso.getHandle() + ", dbID=" + String.valueOf(dso.getID())));
      }
      if (dso != null) {
        if (params.recursiveModeEnabled()) {
          String[] childFilePaths = manifest.getChildMetsFilePaths();
          for (int i = 0; i < childFilePaths.length; i++) {
            addPackageReference(dso, childFilePaths[i]);
          }
        }
      }
      return dso;
    } catch (SQLException se) {
      dso = null;
      throw se;
    }
  }

  protected boolean preserveManifest() {
    return configurationService.getBooleanProperty("mets." + getConfigurationName() + ".ingest.preserveManifest", false);
  }

  protected String getManifestBitstreamFormat() {
    return configurationService.getProperty("mets." + getConfigurationName() + ".ingest.manifestBitstreamFormat");
  }

  protected boolean useCollectionTemplate() {
    return configurationService.getBooleanProperty("mets." + getConfigurationName() + ".ingest.useCollectionTemplate", false);
  }

  /**
     * Parse the hdl: URI/URN format into a raw Handle.
     *
     * @param value handle URI string
     * @return raw handle (with 'hdl:' prefix removed)
     */
  protected String decodeHandleURN(String value) {
    if (value != null && value.startsWith("hdl:")) {
      return value.substring(4);
    } else {
      return null;
    }
  }

  /**
     * Remove an existing DSpace Object (called during a replace)
     *
     * @param context context
     * @param dso     DSpace Object
     * @throws IOException        if IO error
     * @throws SQLException       if database error
     * @throws AuthorizeException if authorization error
     */
  protected void removeObject(Context context, DSpaceObject dso) throws AuthorizeException, SQLException, IOException {
    if (log.isDebugEnabled()) {
      log.debug("Removing object " + Constants.typeText[dso.getType()] + " id=" + dso.getID());
    }
    switch (dso.getType()) {
      case Constants.ITEM:
      Item item = (Item) dso;
      itemService.delete(context, item);
      break;
      case Constants.COLLECTION:
      Collection collection = (Collection) dso;
      collectionService.delete(context, collection);
      break;
      case Constants.COMMUNITY:
      communityService.delete(context, (Community) dso);
      break;
      default:
      break;
    }
  }

  /**
     * Determines what parent DSpace object is referenced in this METS doc.
     * <p>
     * This is a default implementation which assumes the parent will be
     * specified in a &lt;structMap LABEL="Parent"&gt;. You should override this
     * method if your METS manifest specifies the parent object in another
     * location.
     *
     * @param context  DSpace Context
     * @param manifest METS manifest
     * @return a DSpace Object which is the parent (or null, if not found)
     * @throws PackageValidationException  if package validation error
     *                                     if parent reference cannot be found in manifest
     * @throws MetadataValidationException if metadata validation error
     * @throws SQLException                if database error
     */
  public DSpaceObject getParentObject(Context context, METSManifest manifest) throws PackageValidationException, MetadataValidationException, SQLException {
    DSpaceObject parent = null;
    String parentLink = manifest.getParentOwnerLink();
    if (parentLink != null && parentLink.length() > 0) {
      parent = handleService.resolveToObject(context, parentLink);
      if (parent == null) {
        throw new UnsupportedOperationException("Could not find a parent DSpaceObject referenced as \'" + parentLink + "\' in the METS Manifest for object " + manifest.getObjID() + ". A parent DSpaceObject must be specified from either the \'packager\' command or noted in " + "the METS Manifest itself.");
      }
    } else {
      throw new UnsupportedOperationException("Could not find a parent DSpaceObject where we can ingest the packaged object " + manifest.getObjID() + ".  A parent DSpaceObject must be specified from either the \'packager\' command or noted in the " + "METS Manifest itself.");
    }
    return parent;
  }

  /**
     * Determines the handle of the DSpace object represented in this METS doc.
     * <p>
     * This is a default implementation which assumes the handle of the DSpace
     * Object can be found in the &lt;mets&gt; @OBJID attribute. You should
     * override this method if your METS manifest specifies the handle in
     * another location.
     *
     * If no handle was found then null is returned.
     *
     * @param manifest METS manifest
     * @return handle as a string (or null, if not found)
     * @throws PackageValidationException  if package validation error
     *                                     if handle cannot be found in manifest
     * @throws MetadataValidationException if validation error
     * @throws SQLException                if database error
     */
  public String getObjectHandle(METSManifest manifest) throws PackageValidationException, MetadataValidationException, SQLException {
    String handleURI = manifest.getObjID();
    String handle = decodeHandleURN(handleURI);
    return handle;
  }

  /**
     * Retrieve the inputStream for a File referenced from a specific path
     * within a METS package.
     * <p>
     * If the packager is set to 'manifest-only' (i.e. pkgFile is just a
     * manifest), we assume the file is available for download via a URL.
     * <p>
     * Otherwise, the pkgFile is a Zip, so the file should be retrieved from
     * within that Zip package.
     *
     * @param pkgFile the full package file (which may include content files if a
     *                zip)
     * @param params  Parameters passed to METSIngester
     * @param path    the File path (either path in Zip package or a URL)
     * @return the InputStream for the file
     * @throws MetadataValidationException if validation error
     * @throws IOException                 if IO error
     */
  protected static InputStream getFileInputStream(File pkgFile, PackageParameters params, String path) throws MetadataValidationException, IOException {
    if (params.getBooleanProperty("manifestOnly", false)) {
      try {
        URL fileURL = new URL(path);
        URLConnection connection = fileURL.openConnection();
        return connection.getInputStream();
      } catch (IOException io) {
        log.error("Unable to retrieve external file from URL \'" + path + "\' for manifest-only METS package.  All externally referenced files must be " + "retrievable via URLs.");
        throw io;
      }
    } else {
      ZipFile zipPackage = new ZipFile(pkgFile);
      ZipEntry manifestEntry = zipPackage.getEntry(path);
      if (manifestEntry != null) {
        return zipPackage.getInputStream(manifestEntry);
      } else {
        throw new MetadataValidationException("Manifest file references file \'" + path + "\' not included in the zip.");
      }
    }
  }

  /**
     * Returns a user help string which should describe the
     * additional valid command-line options that this packager
     * implementation will accept when using the <code>-o</code> or
     * <code>--option</code> flags with the Packager script.
     *
     * @return a string describing additional command-line options available
     * with this packager
     */
  @Override public String getParameterHelp() {
    return "* ignoreHandle=[boolean]      " + "If true, the ingester will ignore any Handle specified in the METS manifest itself, and instead create a" + " new Handle during the ingest process (this is the default when running in Submit mode, using the -s " + "flag). " + "If false, the ingester attempts to restore the Handles specified in the METS manifest (this is the " + "default when running in Restore/replace mode, using the -r flag). " + "\n\n" + "* ignoreParent=[boolean]      " + "If true, the ingester will ignore any Parent object specified in the METS manifest itself, and instead " + "ingest under a new Parent object (this is the default when running in Submit mode, using the -s flag). " + "The new Parent object must be specified via the -p flag. " + "If false, the ingester attempts to restore the object directly under its old Parent (this is the default" + " when running in Restore/replace mode, using the -r flag). " + "\n\n" + "* manifestOnly=[boolean]      " + "Specify true if the ingest package consists of just a METS manifest (mets.xml), without any content " + "files (defaults to false)." + "\n\n" + "* validate=[boolean]      " + "If true, enable XML validation of METS file using schemas in document (default is true).";
  }

  /**
     * Profile-specific tests to validate manifest. The implementation can
     * access the METS document through the <code>manifest</code> variable, an
     * instance of <code>METSManifest</code>.
     *
     * @throws MetadataValidationException if metadata validation error
     *                                     if there is a fatal problem with the METS document's
     *                                     conformance to the expected profile.
     */
  abstract void checkManifest(METSManifest manifest) throws MetadataValidationException;

  /**
     * Select the <code>dmdSec</code> element(s) to apply to the Item. The
     * implementation is responsible for choosing which (if any) of the metadata
     * sections to crosswalk to get the descriptive metadata for the item being
     * ingested. It is responsible for calling the crosswalk, using the
     * manifest's helper i.e.
     * <code>manifest.crosswalkItemDmd(context,item,dmdElement,callback);</code>
     * (The <code>callback</code> argument is a reference to itself since the
     * class also implements the <code>METSManifest.MdRef</code> interface to
     * fetch package files referenced by mdRef elements.)
     * <p>
     * Note that <code>item</code> and <code>manifest</code> are available as
     * protected fields from the superclass.
     *
     * @param context  the DSpace context
     * @param dso      DSpace Object
     * @param manifest the METSManifest
     * @param callback the MdrefManager (manages all external metadata files
     *                 referenced by METS <code>mdref</code> elements)
     * @param dmds     array of Elements, each a METS <code>dmdSec</code> that
     *                 applies to the Item as a whole.
     * @param params   Packager Parameters
     * @throws CrosswalkException         if crosswalk error
     * @throws PackageValidationException if package validation error
     * @throws IOException                if IO error
     * @throws SQLException               if database error
     * @throws AuthorizeException         if authorization error
     */
  public abstract void crosswalkObjectDmd(Context context, DSpaceObject dso, METSManifest manifest, MdrefManager callback, Element[] dmds, PackageParameters params) throws CrosswalkException, PackageValidationException, AuthorizeException, SQLException, IOException;

  /**
     * Add license(s) to Item based on contents of METS and other policies. The
     * implementation of this method controls exactly what licenses are added to
     * the new item, including the DSpace deposit license. It is given the
     * collection (which is the source of a default deposit license), an
     * optional user-supplied deposit license (in the form of a String), and the
     * METS manifest. It should invoke <code>manifest.getItemRightsMD()</code>
     * to get an array of <code>rightsMd</code> elements which might contain
     * other license information of interest, e.g. a Creative Commons license.
     * <p>
     * This framework does not add any licenses by default.
     * <p>
     * Note that crosswalking rightsMD sections can also add a deposit or CC
     * license to the object.
     *
     * @param context    the DSpace context
     * @param item       Item
     * @param collection DSpace Collection to which the item is being submitted.
     * @param license    optional user-supplied Deposit License text (may be null)
     * @param params     Packager Parameters
     * @throws PackageValidationException if package validation error
     * @throws IOException                if IO error
     * @throws SQLException               if database error
     * @throws AuthorizeException         if authorization error
     */
  public abstract void addLicense(Context context, Item item, String license, Collection collection, PackageParameters params) throws PackageValidationException, AuthorizeException, SQLException, IOException;

  /**
     * Hook for final "finishing" operations on the new Object. This method is
     * called when the new Object is otherwise complete and ready to be
     * returned. The implementation should use this opportunity to make whatever
     * final checks and modifications are necessary.
     *
     * @param context the DSpace context
     * @param dso     the DSpace Object
     * @param params  the Packager Parameters
     * @throws CrosswalkException         if crosswalk error
     * @throws PackageValidationException if package validation error
     * @throws IOException                if IO error
     * @throws SQLException               if database error
     * @throws AuthorizeException         if authorization error
     */
  public abstract void finishObject(Context context, DSpaceObject dso, PackageParameters params) throws PackageValidationException, CrosswalkException, AuthorizeException, SQLException, IOException;

  /**
     * Determines what type of DSpace object is represented in this METS doc.
     *
     * @param manifest METS manifest
     * @return one of the object types in Constants.
     * @throws PackageValidationException if package validation error
     */
  public abstract int getObjectType(METSManifest manifest) throws PackageValidationException;

  /**
     * Subclass-dependent final processing on a Bitstream; could include fixing
     * up the name, bundle, other attributes.
     *
     * @param context  context
     * @param manifest METS manifest
     * @param bs       bitstream
     * @param mfile    element
     * @param params   package params
     * @throws MetadataValidationException if validation error
     * @throws IOException                 if IO error
     * @throws SQLException                if database error
     * @throws AuthorizeException          if authorization error
     */
  public abstract void finishBitstream(Context context, Bitstream bs, Element mfile, METSManifest manifest, PackageParameters params) throws MetadataValidationException, SQLException, AuthorizeException, IOException;

  /**
     * Returns keyword that makes the configuration keys of this subclass
     * unique, e.g. if it returns NAME, the key would be:
     * "mets.NAME.ingest.preserveManifest = true"
     *
     * @return name
     */
  public abstract String getConfigurationName();

  public UUID getObjectID(METSManifest manifest) throws PackageValidationException {
    Element mets = manifest.getMets();
    String idStr = mets.getAttributeValue("ID");
    if (idStr == null || idStr.length() == 0) {
      throw new PackageValidationException("Manifest is missing the required mets@ID attribute.");
    }
    if (idStr.contains("DB-ID-")) {
      idStr = idStr.substring(idStr.lastIndexOf("DB-ID-") + 6, idStr.length());
    }
    try {
      return UUID.fromString(idStr);
    } catch (IllegalArgumentException ignored) {
    }
    return null;
  }
}