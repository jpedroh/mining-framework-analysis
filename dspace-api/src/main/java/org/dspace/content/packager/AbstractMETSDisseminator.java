package org.dspace.content.packager;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import edu.harvard.hul.ois.mets.AmdSec;
import edu.harvard.hul.ois.mets.BinData;
import edu.harvard.hul.ois.mets.Checksumtype;
import edu.harvard.hul.ois.mets.DigiprovMD;
import edu.harvard.hul.ois.mets.Div;
import edu.harvard.hul.ois.mets.DmdSec;
import edu.harvard.hul.ois.mets.FLocat;
import edu.harvard.hul.ois.mets.FileGrp;
import edu.harvard.hul.ois.mets.FileSec;
import edu.harvard.hul.ois.mets.Fptr;
import edu.harvard.hul.ois.mets.Loctype;
import edu.harvard.hul.ois.mets.MdRef;
import edu.harvard.hul.ois.mets.MdWrap;
import edu.harvard.hul.ois.mets.Mdtype;
import edu.harvard.hul.ois.mets.Mets;
import edu.harvard.hul.ois.mets.MetsHdr;
import edu.harvard.hul.ois.mets.Mptr;
import edu.harvard.hul.ois.mets.RightsMD;
import edu.harvard.hul.ois.mets.SourceMD;
import edu.harvard.hul.ois.mets.StructMap;
import edu.harvard.hul.ois.mets.TechMD;
import edu.harvard.hul.ois.mets.XmlData;
import edu.harvard.hul.ois.mets.helper.Base64;
import edu.harvard.hul.ois.mets.helper.MdSec;
import edu.harvard.hul.ois.mets.helper.MetsElement;
import edu.harvard.hul.ois.mets.helper.MetsException;
import edu.harvard.hul.ois.mets.helper.MetsValidator;
import edu.harvard.hul.ois.mets.helper.MetsWriter;
import edu.harvard.hul.ois.mets.helper.PreformedXML;
import org.apache.logging.log4j.Logger;
import org.dspace.app.util.Util;
import org.dspace.authorize.AuthorizeException;
import org.dspace.authorize.factory.AuthorizeServiceFactory;
import org.dspace.authorize.service.AuthorizeService;
import org.dspace.content.Bitstream;
import org.dspace.content.Bundle;
import org.dspace.content.Collection;
import org.dspace.content.Community;
import org.dspace.content.DSpaceObject;
import org.dspace.content.Item;
import org.dspace.content.crosswalk.AbstractPackagerWrappingCrosswalk;
import org.dspace.content.crosswalk.CrosswalkException;
import org.dspace.content.crosswalk.CrosswalkObjectNotSupported;
import org.dspace.content.crosswalk.DisseminationCrosswalk;
import org.dspace.content.crosswalk.StreamDisseminationCrosswalk;
import org.dspace.content.factory.ContentServiceFactory;
import org.dspace.content.service.BitstreamService;
import org.dspace.content.service.SiteService;
import org.dspace.core.Constants;
import org.dspace.core.Context;
import org.dspace.core.LogHelper;
import org.dspace.core.Utils;
import org.dspace.core.factory.CoreServiceFactory;
import org.dspace.core.service.PluginService;
import org.dspace.license.factory.LicenseServiceFactory;
import org.dspace.license.service.CreativeCommonsService;
import org.dspace.services.ConfigurationService;
import org.dspace.services.factory.DSpaceServicesFactory;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

/**
 * Base class for disseminator of
 * METS (Metadata Encoding and Transmission Standard) Package.<br>
 * See <a href="http://www.loc.gov/standards/mets/">http://www.loc.gov/standards/mets/</a>
 * <p>
 * This is a generic packager framework intended to be subclassed to create
 * packagers for more specific METS "profiles".   METS is an
 * abstract and flexible framework that can encompass many
 * different kinds of metadata and inner package structures.
 * <p>
 * <b>Package Parameters:</b><br>
 * <ul>
 * <li><code>manifestOnly</code> -- if true, generate a standalone XML
 * document of the METS manifest instead of a complete package.  Any
 * other metadata (such as licenses) will be encoded inline.
 * Default is <code>false</code>.</li>
 *
 * <li><code>unauthorized</code> -- this determines what is done when the
 * packager encounters a Bundle or Bitstream it is not authorized to
 * read.  By default, it just quits with an AuthorizeException.
 * If this option is present, it must be one of the following values:
 * <ul>
 * <li><code>skip</code> -- simply exclude unreadable content from package.</li>
 * <li><code>zero</code> -- include unreadable bitstreams as 0-length files;
 * unreadable Bundles will still cause authorize errors.</li></ul></li>
 * </ul>
 *
 * @author Larry Stone
 * @author Robert Tansley
 * @author Tim Donohue
 */
public abstract class AbstractMETSDisseminator extends AbstractPackageDisseminator {
  /**
     * log4j category
     */
  private static final Logger log = org.apache.logging.log4j.LogManager.getLogger(AbstractMETSDisseminator.class);

  protected static XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());

  protected final AuthorizeService authorizeService = AuthorizeServiceFactory.getInstance().getAuthorizeService();

  protected final BitstreamService bitstreamService = ContentServiceFactory.getInstance().getBitstreamService();

  protected final SiteService siteService = ContentServiceFactory.getInstance().getSiteService();

  protected final CreativeCommonsService creativeCommonsService = LicenseServiceFactory.getInstance().getCreativeCommonsService();

  protected final ConfigurationService configurationService = DSpaceServicesFactory.getInstance().getConfigurationService();

  protected int idCounter = 1;

  /**
     * Default date/time (in milliseconds since epoch) to set for Zip Entries
     * for DSpace Objects which don't have a Last Modified date.  If we don't
     * set our own date/time, then it will default to current system date/time.
     * This is less than ideal, as it causes the md5 checksum of Zip file to
     * change whenever Zip is regenerated (even if compressed files are unchanged)
     * 1036368000 seconds * 1000 = Nov 4, 2002 GMT (the date DSpace 1.0 was released)
     */
  protected static final long DEFAULT_MODIFIED_DATE = 1036368000L * 1000;

  /**
     * Suffix for Template objects (e.g. Item Templates)
     */
  protected static final String TEMPLATE_TYPE_SUFFIX = " Template";

  protected static class MdStreamCache {
    protected Map<MdRef, InputStream> extraFiles = new HashMap<>();

    public void addStream(MdRef key, InputStream md) {
      extraFiles.put(key, md);
    }

    public Map<MdRef, InputStream> getMap() {
      return extraFiles;
    }

    public void close() throws IOException {
      for (InputStream is : extraFiles.values()) {
        is.close();
      }
    }
  }

  /**
     * Make a new unique ID symbol with specified prefix.
     *
     * @param prefix the prefix of the identifier, constrained to XML ID schema
     * @return a new string identifier unique in this session (instance).
     */
  protected synchronized String gensym(String prefix) {
    return prefix + "_" + String.valueOf(idCounter++);
  }

  /**
     * Resets the unique ID counter used by gensym() method to
     * determine the @ID values of METS tags.
     */
  protected synchronized void resetCounter() {
    idCounter = 1;
  }

  @Override public String getMIMEType(PackageParameters params) {
    return (params != null && (params.getBooleanProperty("manifestOnly", false))) ? "text/xml" : "application/zip";
  }

  /**
     * Export the object (Item, Collection, or Community) as a
     * "package" on the indicated OutputStream.  Package is any serialized
     * representation of the item, at the discretion of the implementing
     * class.  It does not have to include content bitstreams.
     * <p>
     * Use the <code>params</code> parameter list to adjust the way the
     * package is made, e.g. including a "<code>metadataOnly</code>"
     * parameter might make the package a bare manifest in XML
     * instead of a Zip file including manifest and contents.
     * <p>
     * Throws an exception of the chosen object is not acceptable or there is
     * a failure creating the package.
     *
     * @param context DSpace context.
     * @param dso     DSpace object (item, collection, etc)
     * @param params  Properties-style list of options specific to this packager
     * @param pkgFile File where export package should be written
     * @throws PackageValidationException if package cannot be created or there
     *                                    is a fatal error in creating it.
     * @throws CrosswalkException         if crosswalk error
     * @throws AuthorizeException         if authorization error
     * @throws SQLException               if database error
     * @throws IOException                if IO error
     */
  @Override public void disseminate(Context context, DSpaceObject dso, PackageParameters params, File pkgFile) throws PackageValidationException, CrosswalkException, AuthorizeException, SQLException, IOException {
    resetCounter();
    FileOutputStream outStream = null;
    try {
      if (!pkgFile.exists()) {
        PackageUtils.createFile(pkgFile);
      }
      outStream = new FileOutputStream(pkgFile);
      if (params != null && params.getBooleanProperty("manifestOnly", false)) {
        Mets manifest = makeManifest(context, dso, params, null);
        if (params.getBooleanProperty("validate", true)) {
          manifest.validate(new MetsValidator());
        }
        manifest.write(new MetsWriter(outStream));
      } else {
        writeZipPackage(context, dso, params, outStream);
      }
      log.info(LogHelper.getHeader(context, "package_disseminate", "Disseminated package file=" + pkgFile.getName() + " for Object, type=" + Constants.typeText[dso.getType()] + ", handle=" + dso.getHandle() + ", dbID=" + String.valueOf(dso.getID())));
    } catch (MetsException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
      String errorMsg = "Error exporting METS for DSpace Object, type=" + Constants.typeText[dso.getType()] + ", handle=" + dso.getHandle() + ", dbID=" + String.valueOf(dso.getID());
      log.error(errorMsg, e);
      throw new PackageValidationException(errorMsg, e);
    } finally {
      if (outStream != null) {
        outStream.close();
      }
    }
  }

  /**
     * Make a Zipped up METS package for the given DSpace Object
     *
     * @param context DSpace Context
     * @param dso     The DSpace Object
     * @param params  Parameters to the Packager script
     * @param pkg     Package output stream
     * @throws PackageValidationException if package validation error
     * @throws CrosswalkException         if crosswalk error
     * @throws AuthorizeException         if authorization error
     * @throws MetsException              if METS error
     * @throws SQLException               if database error
     * @throws IOException                if IO error
     * @throws NoSuchMethodException      passed through.
     * @throws InstantiationException     passed through.
     * @throws IllegalAccessException     passed through.
     * @throws InvocationTargetException  passed through.
     */
  protected void writeZipPackage(Context context, DSpaceObject dso, PackageParameters params, OutputStream pkg) throws PackageValidationException, CrosswalkException, MetsException, AuthorizeException, SQLException, IOException, NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
    long lmTime = 0;
    if (dso.getType() == Constants.ITEM) {
      lmTime = ((Item) dso).getLastModified().getTime();
    }
    MdStreamCache extraStreams = new MdStreamCache();
    ZipOutputStream zip = new ZipOutputStream(pkg);
    zip.setComment("METS archive created by DSpace " + Util.getSourceVersion());
    Mets manifest = makeManifest(context, dso, params, extraStreams);
    for (Map.Entry<MdRef, InputStream> ment : extraStreams.getMap().entrySet()) {
      MdRef ref = ment.getKey();
      linkLicenseRefsToBitstreams(context, params, dso, ref);
      if (ref.getXlinkHref() == null || ref.getXlinkHref().isEmpty()) {
        InputStream is = ment.getValue();
        String fname = gensym("metadata");
        ref.setXlinkHref(fname);
        if (log.isDebugEnabled()) {
          log.debug("Writing EXTRA stream to Zip: " + fname);
        }
        ZipEntry ze = new ZipEntry(fname);
        if (lmTime != 0) {
          ze.setTime(lmTime);
        } else {
          ze.setTime(DEFAULT_MODIFIED_DATE);
        }
        zip.putNextEntry(ze);
        Utils.copy(is, zip);
        zip.closeEntry();
        is.close();
      }
    }
    ZipEntry me = new ZipEntry(METSManifest.MANIFEST_FILE);
    if (lmTime != 0) {
      me.setTime(lmTime);
    } else {
      me.setTime(DEFAULT_MODIFIED_DATE);
    }
    zip.putNextEntry(me);
    if (params.getBooleanProperty("validate", true)) {
      manifest.validate(new MetsValidator());
    }
    manifest.write(new MetsWriter(zip));
    zip.closeEntry();
    addBitstreamsToZip(context, dso, params, zip);
    zip.close();
  }

  /**
     * Add Bitstreams associated with a given DSpace Object into an
     * existing ZipOutputStream
     *
     * @param context DSpace Context
     * @param dso     The DSpace Object
     * @param params  Parameters to the Packager script
     * @param zip     Zip output
     * @throws PackageValidationException if validation error
     * @throws AuthorizeException         if authorization error
     * @throws SQLException               if database error
     * @throws IOException                if IO error
     */
  protected void addBitstreamsToZip(Context context, DSpaceObject dso, PackageParameters params, ZipOutputStream zip) throws PackageValidationException, AuthorizeException, SQLException, IOException {
    String unauth = (params == null) ? null : params.getProperty("unauthorized");
    if (dso.getType() == Constants.ITEM) {
      Item item = (Item) dso;
      long lmTime = ((Item) dso).getLastModified().getTime();
      List<Bundle> bundles = item.getBundles();
      for (Bundle bundle : bundles) {
        if (includeBundle(bundle)) {
          if (!authorizeService.authorizeActionBoolean(context, bundle, Constants.READ)) {
            if (unauth != null && (unauth.equalsIgnoreCase("skip"))) {
              log.warn("Skipping Bundle[\"" + bundle.getName() + "\"] because you are not authorized to read it.");
              continue;
            } else {
              throw new AuthorizeException("Not authorized to read Bundle named \"" + bundle.getName() + "\"");
            }
          }
          List<Bitstream> bitstreams = bundle.getBitstreams();
          for (Bitstream bitstream : bitstreams) {
            boolean auth = authorizeService.authorizeActionBoolean(context, bitstream, Constants.READ);
            if (auth || (unauth != null && unauth.equalsIgnoreCase("zero"))) {
              String zname = makeBitstreamURL(context, bitstream, params);
              ZipEntry ze = new ZipEntry(zname);
              if (log.isDebugEnabled()) {
                log.debug(new StringBuilder().append("Writing CONTENT stream of bitstream(").append(bitstream.getID()).append(") to Zip: ").append(zname).append(", size=").append(bitstream.getSizeBytes()).toString());
              }
              if (lmTime != 0) {
                ze.setTime(lmTime);
              } else {
                ze.setTime(DEFAULT_MODIFIED_DATE);
              }
              ze.setSize(auth ? bitstream.getSizeBytes() : 0);
              zip.putNextEntry(ze);
              if (auth) {
                InputStream input = bitstreamService.retrieve(context, bitstream);
                Utils.copy(input, zip);
                input.close();
              } else {
                log.warn("Adding zero-length file for Bitstream, uuid=" + String.valueOf(bitstream.getID()) + ", not authorized for READ.");
              }
              zip.closeEntry();
            } else {
              if (unauth != null && unauth.equalsIgnoreCase("skip")) {
                log.warn("Skipping Bitstream, uuid=" + String.valueOf(bitstream.getID()) + ", not authorized for READ.");
              } else {
                throw new AuthorizeException("Not authorized to read Bitstream, uuid=" + String.valueOf(bitstream.getID()));
              }
            }
          }
        }
      }
    } else {
      if (dso.getType() == Constants.COLLECTION || dso.getType() == Constants.COMMUNITY) {
        Bitstream logoBs = dso.getType() == Constants.COLLECTION ? ((Collection) dso).getLogo() : ((Community) dso).getLogo();
        if (logoBs != null) {
          String zname = makeBitstreamURL(context, logoBs, params);
          ZipEntry ze = new ZipEntry(zname);
          if (log.isDebugEnabled()) {
            log.debug("Writing CONTENT stream of bitstream({}) to Zip: {}, size={}", String.valueOf(logoBs.getID()), zname, String.valueOf(logoBs.getSizeBytes()));
          }
          ze.setSize(logoBs.getSizeBytes());
          ze.setTime(DEFAULT_MODIFIED_DATE);
          zip.putNextEntry(ze);
          Utils.copy(bitstreamService.retrieve(context, logoBs), zip);
          zip.closeEntry();
        }
      }
    }
  }

  protected void setMdType(MdWrap mdWrap, String mdtype) {
    try {
      mdWrap.setMDTYPE(Mdtype.parse(mdtype));
    } catch (MetsException e) {
      mdWrap.setMDTYPE(Mdtype.OTHER);
      mdWrap.setOTHERMDTYPE(mdtype);
    }
  }

  protected void setMdType(MdRef mdRef, String mdtype) {
    try {
      mdRef.setMDTYPE(Mdtype.parse(mdtype));
    } catch (MetsException e) {
      mdRef.setMDTYPE(Mdtype.OTHER);
      mdRef.setOTHERMDTYPE(mdtype);
    }
  }

  /**
     * Create an element wrapped around a metadata reference (either mdWrap
     * or mdRef); i.e.dmdSec, techMd, sourceMd, etc.  Checks for
     * XML-DOM oriented crosswalk first, then if not found looks for
     * stream crosswalk of the same name.
     *
     * @param context      DSpace Context
     * @param dso          DSpace Object we are generating METS manifest for
     * @param mdSecClass   class of mdSec (TechMD, RightsMD, DigiProvMD, etc)
     * @param typeSpec     Type of metadata going into this mdSec (e.g. MODS, DC, PREMIS, etc)
     * @param params       the PackageParameters
     * @param extraStreams list of extra files which need to be added to final dissemination package
     * @return mdSec element or null if crosswalk returns empty results.
     * @throws SQLException               if database error
     * @throws PackageValidationException if package validation error
     * @throws CrosswalkException         if crosswalk error
     * @throws IOException                if IO error
     * @throws AuthorizeException         if authorization error
     * @throws NoSuchMethodException      if mdSecClass cannot be instantiated.
     * @throws InstantiationException     if mdSecClass cannot be instantiated.
     * @throws IllegalAccessException     if mdSecClass cannot be instantiated.
     * @throws InvocationTargetException  if mdSecClass cannot be instantiated.
     */
  protected MdSec makeMdSec(Context context, DSpaceObject dso, Class mdSecClass, String typeSpec, PackageParameters params, MdStreamCache extraStreams) throws SQLException, PackageValidationException, CrosswalkException, IOException, AuthorizeException, NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
    try {
      MdSec mdSec = (MdSec) mdSecClass.getDeclaredConstructor().newInstance();
      mdSec.setID(gensym(mdSec.getLocalName()));
      String parts[] = typeSpec.split(":", 2);
      String xwalkName;
      String metsName;
      if (parts.length > 1) {
        metsName = parts[0];
        xwalkName = parts[1];
      } else {
        metsName = typeSpec;
        xwalkName = typeSpec;
      }
      PluginService pluginService = CoreServiceFactory.getInstance().getPluginService();
      boolean xwalkFound = pluginService.hasNamedPlugin(DisseminationCrosswalk.class, xwalkName);
      if (xwalkFound) {
        DisseminationCrosswalk xwalk = (DisseminationCrosswalk) pluginService.getNamedPlugin(DisseminationCrosswalk.class, xwalkName);
        if (xwalk.canDisseminate(dso)) {
          if (xwalk instanceof AbstractPackagerWrappingCrosswalk) {
            AbstractPackagerWrappingCrosswalk wrapper = (AbstractPackagerWrappingCrosswalk) xwalk;
            wrapper.setPackagingParameters(params);
          }
          MdWrap mdWrap = new MdWrap();
          setMdType(mdWrap, metsName);
          XmlData xmlData = new XmlData();
          if (crosswalkToMetsElement(context, xwalk, dso, xmlData) != null) {
            mdWrap.getContent().add(xmlData);
            mdSec.getContent().add(mdWrap);
            return mdSec;
          } else {
            return null;
          }
        } else {
          return null;
        }
      } else {
        StreamDisseminationCrosswalk sxwalk = (StreamDisseminationCrosswalk) pluginService.getNamedPlugin(StreamDisseminationCrosswalk.class, xwalkName);
        if (sxwalk != null) {
          if (sxwalk.canDisseminate(context, dso)) {
            if (sxwalk instanceof AbstractPackagerWrappingCrosswalk) {
              AbstractPackagerWrappingCrosswalk wrapper = (AbstractPackagerWrappingCrosswalk) sxwalk;
              wrapper.setPackagingParameters(params);
            }
            ByteArrayOutputStream disseminateOutput = new ByteArrayOutputStream();
            sxwalk.disseminate(context, dso, disseminateOutput);
            ByteArrayInputStream crosswalkedStream = new ByteArrayInputStream(disseminateOutput.toByteArray());
            if (extraStreams != null) {
              MdRef mdRef = new MdRef();
              extraStreams.addStream(mdRef, crosswalkedStream);
              mdRef.setMIMETYPE(sxwalk.getMIMEType());
              setMdType(mdRef, metsName);
              mdRef.setLOCTYPE(Loctype.URL);
              mdSec.getContent().add(mdRef);
            } else {
              MdWrap mdWrap = new MdWrap();
              mdWrap.setMIMETYPE(sxwalk.getMIMEType());
              setMdType(mdWrap, metsName);
              BinData binData = new BinData();
              Base64 base64 = new Base64(crosswalkedStream);
              binData.getContent().add(base64);
              mdWrap.getContent().add(binData);
              mdSec.getContent().add(mdWrap);
            }
            return mdSec;
          } else {
            return null;
          }
        } else {
          throw new PackageValidationException("Cannot find " + xwalkName + " crosswalk plugin, either DisseminationCrosswalk or " + "StreamDisseminationCrosswalk");
        }
      }
    } catch (InstantiationException | IllegalAccessException e) {
      throw new PackageValidationException("Error instantiating Mdsec object: " + e.toString(), e);
    }
  }

  protected void addToAmdSec(AmdSec fAmdSec, String[] mdTypes, Class mdSecClass, Context context, DSpaceObject dso, PackageParameters params, MdStreamCache extraStreams) throws SQLException, PackageValidationException, CrosswalkException, IOException, AuthorizeException, NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
    for (int i = 0; i < mdTypes.length; ++i) {
      MdSec md = makeMdSec(context, dso, mdSecClass, mdTypes[i], params, extraStreams);
      if (md != null) {
        fAmdSec.getContent().add(md);
      }
    }
  }

  protected String addAmdSec(Context context, DSpaceObject dso, PackageParameters params, Mets mets, MdStreamCache extraStreams) throws SQLException, PackageValidationException, CrosswalkException, IOException, AuthorizeException, NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, IllegalArgumentException, InvocationTargetException {
    String techMdTypes[] = getTechMdTypes(context, dso, params);
    String rightsMdTypes[] = getRightsMdTypes(context, dso, params);
    String sourceMdTypes[] = getSourceMdTypes(context, dso, params);
    String digiprovMdTypes[] = getDigiprovMdTypes(context, dso, params);
    if ((techMdTypes.length + sourceMdTypes.length + digiprovMdTypes.length + rightsMdTypes.length) > 0) {
      String result = gensym("amd");
      AmdSec fAmdSec = new AmdSec();
      fAmdSec.setID(result);
      addToAmdSec(fAmdSec, techMdTypes, TechMD.class, context, dso, params, extraStreams);
      addToAmdSec(fAmdSec, rightsMdTypes, RightsMD.class, context, dso, params, extraStreams);
      addToAmdSec(fAmdSec, sourceMdTypes, SourceMD.class, context, dso, params, extraStreams);
      addToAmdSec(fAmdSec, digiprovMdTypes, DigiprovMD.class, context, dso, params, extraStreams);
      mets.getContent().add(fAmdSec);
      return result;
    } else {
      return null;
    }
  }

  protected String makePersistentID(DSpaceObject dso) {
    String handle = dso.getHandle();
    if (handle == null) {
      return "DSpace_DB_" + Constants.typeText[dso.getType()] + "_" + String.valueOf(dso.getID());
    } else {
      return getHandleURN(handle);
    }
  }

  /**
     * Write out a METS manifest.  Mostly lifted from Rob Tansley's METS exporter.
     *
     * @param context      context
     * @param dso          DSpaceObject
     * @param params       packaging parameters
     * @param extraStreams streams
     * @return METS manifest
     * @throws MetsException              if mets error
     * @throws PackageValidationException if validation error
     * @throws CrosswalkException         if crosswalk error
     * @throws AuthorizeException         if authorization error
     * @throws SQLException               if database error
     * @throws IOException                if IO error
     * @throws NoSuchMethodException      if DmdSec cannot be instantiated.
     * @throws InstantiationException     if DmdSec cannot be instantiated.
     * @throws IllegalAccessException     if DmdSec cannot be instantiated.
     * @throws InvocationTargetException  if DmdSec cannot be instantiated.
     */
  protected Mets makeManifest(Context context, DSpaceObject dso, PackageParameters params, MdStreamCache extraStreams) throws MetsException, PackageValidationException, CrosswalkException, AuthorizeException, SQLException, IOException, NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
    Mets mets = new Mets();
    String identifier = "DB-ID-" + dso.getID();
    mets.setID("DSpace_" + Constants.typeText[dso.getType()] + "_" + identifier);
    mets.setOBJID(makePersistentID(dso));
    mets.setTYPE(getObjectTypeString(dso));
    mets.setPROFILE(getProfile());
    MetsHdr metsHdr = makeMetsHdr(context, dso, params);
    if (metsHdr != null) {
      mets.getContent().add(metsHdr);
    }
    String dmdTypes[] = getDmdTypes(context, dso, params);
    String dmdId[] = new String[dmdTypes.length];
    for (int i = 0; i < dmdTypes.length; ++i) {
      MdSec dmdSec = makeMdSec(context, dso, DmdSec.class, dmdTypes[i], params, extraStreams);
      if (dmdSec != null) {
        mets.getContent().add(dmdSec);
        dmdId[i] = dmdSec.getID();
      }
    }
    String objectAMDID = addAmdSec(context, dso, params, mets, extraStreams);
    StructMap structMap = new StructMap();
    structMap.setID(gensym("struct"));
    structMap.setTYPE("LOGICAL");
    structMap.setLABEL("DSpace Object");
    Div div0 = new Div();
    div0.setID(gensym("div"));
    div0.setTYPE("DSpace Object Contents");
    structMap.getContent().add(div0);
    FileSec fileSec = null;
    if (dso.getType() == Constants.ITEM) {
      String bitstreamIDstart = "bitstream_";
      Item item = (Item) dso;
      String unauth = (params == null) ? null : params.getProperty("unauthorized");
      fileSec = new FileSec();
      List<Bundle> bundles = item.getBundles();
      for (Bundle bundle : bundles) {
        if (!includeBundle(bundle)) {
          continue;
        }
        if (!authorizeService.authorizeActionBoolean(context, bundle, Constants.READ)) {
          if (unauth != null && (unauth.equalsIgnoreCase("skip"))) {
            continue;
          } else {
            throw new AuthorizeException("Not authorized to read Bundle named \"" + bundle.getName() + "\"");
          }
        }
        List<Bitstream> bitstreams = bundle.getBitstreams();
        FileGrp fileGrp = new FileGrp();
        String bName = bundle.getName();
        if ((bName != null) && !bName.equals("")) {
          fileGrp.setUSE(bundleToFileGrp(bName));
        }
        String techBundID = addAmdSec(context, bundle, params, mets, extraStreams);
        if (techBundID != null) {
          fileGrp.setADMID(techBundID);
        }
        Bitstream primaryBitstream = null;
        boolean isContentBundle = false;
        if ((bName != null) && bName.equals("ORIGINAL")) {
          isContentBundle = true;
          primaryBitstream = bundle.getPrimaryBitstream();
        }
        for (Bitstream bitstream : bitstreams) {
          boolean auth = authorizeService.authorizeActionBoolean(context, bitstream, Constants.READ);
          if (!auth) {
            if (unauth != null && unauth.equalsIgnoreCase("skip")) {
              continue;
            } else {
              if (!(unauth != null && unauth.equalsIgnoreCase("zero"))) {
                throw new AuthorizeException("Not authorized to read Bitstream, uuid=" + String.valueOf(bitstream.getID()));
              }
            }
          }
          String uuid = String.valueOf(bitstream.getID());
          String fileID = bitstreamIDstart + uuid;
          edu.harvard.hul.ois.mets.File file = new edu.harvard.hul.ois.mets.File();
          file.setID(fileID);
          file.setSEQ(bitstream.getSequenceID());
          fileGrp.getContent().add(file);
          if (bitstream.equals(primaryBitstream)) {
            Fptr fptr = new Fptr();
            fptr.setFILEID(fileID);
            div0.getContent().add(0, fptr);
          }
          if (isContentBundle) {
            div0.getContent().add(makeFileDiv(fileID, getObjectTypeString(bitstream)));
          }
          String groupID = "GROUP_" + bitstreamIDstart + uuid;
          if ((bundle.getName() != null) && (bundle.getName().equals("THUMBNAIL") || bundle.getName().startsWith("TEXT"))) {
            Bitstream original = findOriginalBitstream(item, bitstream);
            if (original != null) {
              groupID = "GROUP_" + bitstreamIDstart + String.valueOf(original.getID());
            }
          }
          file.setGROUPID(groupID);
          file.setMIMETYPE(bitstream.getFormat(context).getMIMEType());
          file.setSIZE(auth ? bitstream.getSizeBytes() : 0);
          String csType = bitstream.getChecksumAlgorithm();
          String cs = bitstream.getChecksum();
          if (auth && cs != null && csType != null) {
            try {
              file.setCHECKSUMTYPE(Checksumtype.parse(csType));
              file.setCHECKSUM(cs);
            } catch (MetsException e) {
              log.warn("Cannot set bitstream checksum type=" + csType + " in METS.");
            }
          }
          FLocat flocat = new FLocat();
          flocat.setLOCTYPE(Loctype.URL);
          flocat.setXlinkHref(makeBitstreamURL(context, bitstream, params));
          file.getContent().add(flocat);
          String techID = addAmdSec(context, bitstream, params, mets, extraStreams);
          if (techID != null) {
            file.setADMID(techID);
          }
        }
        fileSec.getContent().add(fileGrp);
      }
    } else {
      if (dso.getType() == Constants.COLLECTION) {
        Collection collection = (Collection) dso;
        Iterator<Item> ii = itemService.findByCollection(context, collection);
        while (ii.hasNext()) {
          Item item = ii.next();
          Div childDiv = makeChildDiv(getObjectTypeString(item), item, params);
          if (childDiv != null) {
            div0.getContent().add(childDiv);
          }
        }
        Item templateItem = collection.getTemplateItem();
        if (templateItem != null) {
          String templateDmdId[] = new String[dmdTypes.length];
          int dmdIndex = dmdTypes.length + 1;
          for (int i = 0; i < dmdTypes.length; ++i) {
            MdSec templateDmdSec = makeMdSec(context, templateItem, DmdSec.class, dmdTypes[i], params, extraStreams);
            if (templateDmdSec != null) {
              mets.getContent().add(dmdIndex, templateDmdSec);
              dmdIndex++;
              templateDmdId[i] = templateDmdSec.getID();
            }
          }
          Div templateItemDiv = new Div();
          templateItemDiv.setID(gensym("div"));
          templateItemDiv.setTYPE(getObjectTypeString(templateItem) + TEMPLATE_TYPE_SUFFIX);
          StringBuilder templateDmdIds = new StringBuilder();
          for (String currdmdId : templateDmdId) {
            templateDmdIds.append(" ").append(currdmdId);
          }
          templateItemDiv.setDMDID(templateDmdIds.substring(1));
          div0.getContent().add(0, templateItemDiv);
        }
        Bitstream logoBs = collection.getLogo();
        if (logoBs != null) {
          fileSec = new FileSec();
          addLogoBitstream(context, logoBs, fileSec, div0, params);
        }
      } else {
        if (dso.getType() == Constants.COMMUNITY) {
          List<Community> subcomms = ((Community) dso).getSubcommunities();
          for (Community subcomm : subcomms) {
            Div childDiv = makeChildDiv(getObjectTypeString(subcomm), subcomm, params);
            if (childDiv != null) {
              div0.getContent().add(childDiv);
            }
          }
          List<Collection> colls = ((Community) dso).getCollections();
          for (Collection coll : colls) {
            Div childDiv = makeChildDiv(getObjectTypeString(coll), coll, params);
            if (childDiv != null) {
              div0.getContent().add(childDiv);
            }
          }
          Bitstream logoBs = ((Community) dso).getLogo();
          if (logoBs != null) {
            fileSec = new FileSec();
            addLogoBitstream(context, logoBs, fileSec, div0, params);
          }
        } else {
          if (dso.getType() == Constants.SITE) {
            List<Community> comms = communityService.findAllTop(context);
            for (Community comm : comms) {
              Div childDiv = makeChildDiv(getObjectTypeString(comm), comm, params);
              if (childDiv != null) {
                div0.getContent().add(childDiv);
              }
            }
          }
        }
      }
    }
    if (fileSec != null && fileSec.getContent() != null && !fileSec.getContent().isEmpty()) {
      mets.getContent().add(fileSec);
    }
    mets.getContent().add(structMap);
    StringBuilder dmdIds = new StringBuilder();
    for (String currdmdId : dmdId) {
      dmdIds.append(" ").append(currdmdId);
    }
    div0.setDMDID(dmdIds.substring(1));
    if (objectAMDID != null) {
      div0.setADMID(objectAMDID);
    }
    addStructMap(context, dso, params, mets);
    return mets;
  }

  protected void addLogoBitstream(Context context, Bitstream logoBs, FileSec fileSec, Div div0, PackageParameters params) throws SQLException {
    edu.harvard.hul.ois.mets.File file = new edu.harvard.hul.ois.mets.File();
    String fileID = gensym("logo");
    file.setID(fileID);
    file.setMIMETYPE(logoBs.getFormat(context).getMIMEType());
    file.setSIZE(logoBs.getSizeBytes());
    String csType = logoBs.getChecksumAlgorithm();
    String cs = logoBs.getChecksum();
    if (cs != null && csType != null) {
      try {
        file.setCHECKSUMTYPE(Checksumtype.parse(csType));
        file.setCHECKSUM(cs);
      } catch (MetsException e) {
        log.warn("Cannot set bitstream checksum type=" + csType + " in METS.");
      }
    }
    FLocat flocat = new FLocat();
    flocat.setLOCTYPE(Loctype.URL);
    flocat.setXlinkHref(makeBitstreamURL(context, logoBs, params));
    file.getContent().add(flocat);
    FileGrp fileGrp = new FileGrp();
    fileGrp.setUSE("LOGO");
    fileGrp.getContent().add(file);
    fileSec.getContent().add(fileGrp);
    Fptr fptr = new Fptr();
    fptr.setFILEID(fileID);
    div0.getContent().add(0, fptr);
  }

  protected Div makeFileDiv(String fileID, String type) {
    Div div = new Div();
    div.setID(gensym("div"));
    div.setTYPE(type);
    Fptr fptr = new Fptr();
    fptr.setFILEID(fileID);
    div.getContent().add(fptr);
    return div;
  }

  /**
     * Create a {@code <div>} element with {@code <mptr>} which references a child
     * object via its handle (and via a local file name, when recursively disseminating
     * all child objects).
     *
     * @param type   - type attr value for the {@code <div>}
     * @param dso    - object for which to create the div
     * @param params package params
     * @return a new {@code Div} with {@code dso} as child.
     */
  protected Div makeChildDiv(String type, DSpaceObject dso, PackageParameters params) {
    String handle = dso.getHandle();
    Div div = new Div();
    div.setID(gensym("div"));
    div.setTYPE(type);
    if (handle == null || handle.length() == 0) {
      log.warn("METS Disseminator is skipping " + type + " without handle: " + dso.toString());
    } else {
      Mptr mptr = new Mptr();
      mptr.setID(gensym("mptr"));
      mptr.setLOCTYPE(Loctype.HANDLE);
      mptr.setXlinkHref(handle);
      div.getContent().add(mptr);
    }
    String childFileExtension = (params.getBooleanProperty("manifestOnly", false)) ? "xml" : "zip";
    Mptr mptr2 = new Mptr();
    mptr2.setID(gensym("mptr"));
    mptr2.setLOCTYPE(Loctype.URL);
    mptr2.setXlinkHref(PackageUtils.getPackageName(dso, childFileExtension));
    div.getContent().add(mptr2);
    return div;
  }

  protected String getHandleURN(String handle) {
    if (handle.startsWith("hdl:")) {
      return handle;
    }
    return "hdl:" + handle;
  }

  /**
     * For a bitstream that's a thumbnail or extracted text, find the
     * corresponding bitstream it was derived from, in the ORIGINAL bundle.
     *
     * @param item    the item we're dealing with
     * @param derived the derived bitstream
     * @return the corresponding original bitstream (or null)
     * @throws SQLException if database error
     */
  protected Bitstream findOriginalBitstream(Item item, Bitstream derived) throws SQLException {
    List<Bundle> bundles = item.getBundles();
    String originalFilename = derived.getName().substring(0, derived.getName().length() - 4);
    for (Bundle bundle : bundles) {
      if ((bundle.getName() != null) && bundle.getName().equals("ORIGINAL")) {
        List<Bitstream> bitstreams = bundle.getBitstreams();
        for (Bitstream bitstream : bitstreams) {
          if (bitstream.getName().equals(originalFilename)) {
            return bitstream;
          }
        }
      }
    }
    return null;
  }

  protected MetsElement crosswalkToMetsElement(Context context, DisseminationCrosswalk xwalk, DSpaceObject dso, MetsElement me) throws CrosswalkException, IOException, SQLException, AuthorizeException {
    try {
      String raw = xwalk.getSchemaLocation();
      String sloc[] = raw == null ? null : raw.split("\\s+");
      Namespace ns[] = xwalk.getNamespaces();
      for (int i = 0; i < ns.length; ++i) {
        String uri = ns[i].getURI();
        if (sloc != null && sloc.length > 1 && uri.equals(sloc[0])) {
          me.setSchema(ns[i].getPrefix(), uri, sloc[1]);
        } else {
          me.setSchema(ns[i].getPrefix(), uri);
        }
      }
      PreformedXML pXML = null;
      if (xwalk.preferList()) {
        List<Element> res = xwalk.disseminateList(context, dso);
        if (!(res == null || res.isEmpty())) {
          pXML = new PreformedXML(outputter.outputString(res));
        }
      } else {
        Element res = xwalk.disseminateElement(context, dso);
        if (res != null) {
          pXML = new PreformedXML(outputter.outputString(res));
        }
      }
      if (pXML != null) {
        me.getContent().add(pXML);
        return me;
      }
      return null;
    } catch (CrosswalkObjectNotSupported e) {
      if (log.isDebugEnabled()) {
        log.debug("Skipping MDsec because of CrosswalkObjectNotSupported: dso=" + dso.toString() + ", xwalk=" + xwalk.getClass().getName());
      }
      return null;
    }
  }

  /**
     * Cleanup our license file reference links, as Deposit Licenses and CC Licenses can be
     * added two ways (and we only want to add them to zip package *once*):
     * (1) Added as a normal Bitstream (assuming LICENSE and CC_LICENSE bundles will be included in pkg)
     * (2) Added via a 'rightsMD' crosswalk (as they are rights information/metadata on an Item)
     * <p>
     * So, if they are being added by *both*, then we want to just link the rightsMD {@code <mdRef>} entry so
     * that it points to the Bitstream location.  This implementation is a bit 'hackish', but it's
     * the best we can do, as the Harvard METS API doesn't allow us to go back and crawl an entire
     * METS file to look for these inconsistencies/duplications.
     *
     * @param context current DSpace Context
     * @param params  current Packager Parameters
     * @param dso     current DSpace Object
     * @param mdRef   the rightsMD {@code <mdRef>} element
     * @throws SQLException       if database error
     * @throws IOException        if IO error
     * @throws AuthorizeException if authorization error
     */
  protected void linkLicenseRefsToBitstreams(Context context, PackageParameters params, DSpaceObject dso, MdRef mdRef) throws SQLException, IOException, AuthorizeException {
    if (mdRef.getMDTYPE() != null && mdRef.getMDTYPE() == Mdtype.OTHER && mdRef.getOTHERMDTYPE() != null && mdRef.getOTHERMDTYPE().equals("DSpaceDepositLicense")) {
      Item i = (Item) dso;
      List<Bundle> license = itemService.getBundles(i, Constants.LICENSE_BUNDLE_NAME);
      if (license != null && license.size() > 0 && includeBundle(license.get(0))) {
        Bitstream licenseBs = PackageUtils.findDepositLicense(context, (Item) dso);
        mdRef.setXlinkHref(makeBitstreamURL(context, licenseBs, params));
      }
    } else {
      if (mdRef.getMDTYPE() != null && mdRef.getMDTYPE() == Mdtype.OTHER && mdRef.getOTHERMDTYPE() != null && mdRef.getOTHERMDTYPE().equals("CreativeCommonsText")) {
        Item i = (Item) dso;
        List<Bundle> license = itemService.getBundles(i, CreativeCommonsService.CC_BUNDLE_NAME);
        if (license != null && license.size() > 0 && includeBundle(license.get(0))) {
          Bitstream ccText = creativeCommonsService.getLicenseTextBitstream(i);
          mdRef.setXlinkHref(makeBitstreamURL(context, ccText, params));
        }
      } else {
        if (mdRef.getMDTYPE() != null && mdRef.getMDTYPE() == Mdtype.OTHER && mdRef.getOTHERMDTYPE() != null && mdRef.getOTHERMDTYPE().equals("CreativeCommonsRDF")) {
          Item i = (Item) dso;
          List<Bundle> license = itemService.getBundles(i, CreativeCommonsService.CC_BUNDLE_NAME);
          if (license != null && license.size() > 0 && includeBundle(license.get(0))) {
            Bitstream ccRdf = creativeCommonsService.getLicenseRdfBitstream(i);
            mdRef.setXlinkHref(makeBitstreamURL(context, ccRdf, params));
          }
        }
      }
    }
  }

  /**
     * Build a string which will be used as the "Type" of this object in
     * the METS manifest.
     * <P>
     * Default format is "DSpace [Type-as-string]".
     *
     * @param dso DSpaceObject to create type-string for
     * @return a string which will represent this object Type in METS
     * @see org.dspace.core.Constants
     */
  public String getObjectTypeString(DSpaceObject dso) {
    return "DSpace " + Constants.typeText[dso.getType()];
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
    return "* manifestOnly=[boolean]      " + "If true, only export the METS manifest (mets.xml) and don\'t export content files (defaults to false)." + "\n\n" + "* unauthorized=[value]      " + "If \'skip\', skip over any files which the user doesn\'t have authorization to read. " + "If \'zero\', create a zero-length file for any files the user doesn\'t have authorization to read. " + "By default, an AuthorizationException will be thrown for any files the user cannot read.";
  }

  /**
     * Get the URL by which the METS manifest refers to a Bitstream
     * member within the same package.  In other words, this is generally
     * a relative path link to where the Bitstream file is within the Zipped
     * up package.
     * <p>
     * For a manifest-only METS, this is a reference to an HTTP URL where
     * the bitstream should be able to be downloaded from.
     *
     * @param context   context
     * @param bitstream the Bitstream
     * @param params    Packager Parameters
     * @return String in URL format naming path to bitstream.
     * @throws SQLException if database error
     */
  public String makeBitstreamURL(Context context, Bitstream bitstream, PackageParameters params) throws SQLException {
    if (params != null && (params.getBooleanProperty("manifestOnly", false))) {
      try {
        String handle = null;
        List<Bundle> bn = bitstream.getBundles();
        if (bn.size() > 0) {
          List<Item> bi = bn.get(0).getItems();
          if (bi.size() > 0) {
            handle = bi.get(0).getHandle();
          }
        }
        return configurationService.getProperty("dspace.ui.url") + "/bitstreams/" + String.valueOf(bitstream.getID()) + "/download";
      } catch (SQLException e) {
        log.error("Database problem", e);
      }
      return bitstream.getName();
    } else {
      String base = "bitstream_" + String.valueOf(bitstream.getID());
      List<String> ext = bitstream.getFormat(context).getExtensions();
      return (ext.size() > 0) ? base + "." + ext.get(0) : base;
    }
  }

  /**
     * Create metsHdr element - separate so subclasses can override.
     *
     * @param context context
     * @param dso     DSpaceObject
     * @param params  packaging params
     * @return Mets header
     * @throws SQLException if database error
     */
  public abstract MetsHdr makeMetsHdr(Context context, DSpaceObject dso, PackageParameters params) throws SQLException;

  /**
     * Returns name of METS profile to which this package conforms, e.g.
     * "DSpace METS DIP Profile 1.0"
     *
     * @return string name of profile.
     */
  public abstract String getProfile();

  /**
     * Returns fileGrp's USE attribute value corresponding to a DSpace bundle name.
     *
     * @param bname name of DSpace bundle.
     * @return string name of fileGrp
     */
  public abstract String bundleToFileGrp(String bname);

  /**
     * Get the types of Item-wide DMD to include in package.
     * Each element of the returned array is a String, which
     * MAY be just a simple name, naming both the Crosswalk Plugin and
     * the METS "MDTYPE", <em>or</em> a colon-separated pair consisting of
     * the METS name followed by a colon and the Crosswalk Plugin name.
     * E.g. the type string <code>"DC:qualifiedDublinCore"</code> tells it to
     * create a METS section with <code>MDTYPE="DC"</code> and use the plugin
     * named "qualifiedDublinCore" to obtain the data.
     *
     * @param context context
     * @param dso     DSpaceObject
     * @param params  the PackageParameters passed to the disseminator.
     * @return array of metadata type strings, never null.
     * @throws IOException        if IO error
     * @throws SQLException       if database error
     * @throws AuthorizeException if authorization error
     */
  public abstract String[] getDmdTypes(Context context, DSpaceObject dso, PackageParameters params) throws SQLException, IOException, AuthorizeException;

  /**
     * Get the type string of the technical metadata to create for each
     * object and each Bitstream in an Item.  The type string may be a
     * simple name or colon-separated compound as specified for
     * <code>getDmdTypes()</code> above.
     *
     * @param context context
     * @param dso     DSpaceObject
     * @param params  the PackageParameters passed to the disseminator.
     * @return array of metadata type strings, never null.
     * @throws IOException        if IO error
     * @throws SQLException       if database error
     * @throws AuthorizeException if authorization error
     */
  public abstract String[] getTechMdTypes(Context context, DSpaceObject dso, PackageParameters params) throws SQLException, IOException, AuthorizeException;

  /**
     * Get the type string of the source metadata to create for each
     * object and each Bitstream in an Item.  The type string may be a
     * simple name or colon-separated compound as specified for
     * <code>getDmdTypes()</code> above.
     *
     * @param context context
     * @param dso     DSpaceObject
     * @param params  the PackageParameters passed to the disseminator.
     * @return array of metadata type strings, never null.
     * @throws IOException        if IO error
     * @throws SQLException       if database error
     * @throws AuthorizeException if authorization error
     */
  public abstract String[] getSourceMdTypes(Context context, DSpaceObject dso, PackageParameters params) throws SQLException, IOException, AuthorizeException;

  /**
     * Get the type string of the "digiprov" (digital provenance)
     * metadata to create for each object and each Bitstream in an Item.
     * The type string may be a simple name or colon-separated compound
     * as specified for <code>getDmdTypes()</code> above.
     *
     * @param context context
     * @param dso     DSpaceObject
     * @param params  the PackageParameters passed to the disseminator.
     * @return array of metadata type strings, never null.
     * @throws IOException        if IO error
     * @throws SQLException       if database error
     * @throws AuthorizeException if authorization error
     */
  public abstract String[] getDigiprovMdTypes(Context context, DSpaceObject dso, PackageParameters params) throws SQLException, IOException, AuthorizeException;

  /**
     * Get the type string of the "rights" (permission and/or license)
     * metadata to create for each object and each Bitstream in an Item.
     * The type string may be a simple name or colon-separated compound
     * as specified for <code>getDmdTypes()</code> above.
     *
     * @param context context
     * @param dso     DSpaceObject
     * @param params  the PackageParameters passed to the disseminator.
     * @return array of metadata type strings, never null.
     * @throws IOException        if IO error
     * @throws SQLException       if database error
     * @throws AuthorizeException if authorization error
     */
  public abstract String[] getRightsMdTypes(Context context, DSpaceObject dso, PackageParameters params) throws SQLException, IOException, AuthorizeException;

  /**
     * Add any additional <code>structMap</code> elements to the
     * METS document, as required by this subclass.  A simple default
     * structure map which fulfills the minimal DSpace METS DIP/SIP
     * requirements is already present, so this does not need to do anything.
     *
     * @param context context
     * @param dso     DSpaceObject
     * @param mets    the METS document to which to add structMaps
     * @param params  the PackageParameters passed to the disseminator.
     * @throws IOException        if IO error
     * @throws SQLException       if database error
     * @throws AuthorizeException if authorization error
     * @throws MetsException      if METS error
     */
  public abstract void addStructMap(Context context, DSpaceObject dso, PackageParameters params, Mets mets) throws SQLException, IOException, AuthorizeException, MetsException;

  /**
     * @param bundle bundle
     * @return true when this bundle should be included as "content"
     * in the package.. e.g. DSpace SIP does not include metadata bundles.
     */
  public abstract boolean includeBundle(Bundle bundle);
}