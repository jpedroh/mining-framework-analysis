package org.dspace.curate;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.Logger;
import org.dspace.authorize.AuthorizeException;
import org.dspace.content.Bitstream;
import org.dspace.content.Bundle;
import org.dspace.content.DSpaceObject;
import org.dspace.content.Item;
import org.dspace.content.factory.ContentServiceFactory;
import org.dspace.content.service.BitstreamService;
import org.dspace.content.service.BundleService;
import org.dspace.core.Context;
import org.dspace.disseminate.factory.DisseminateServiceFactory;
import org.dspace.disseminate.service.CitationDocumentService;

/**
 * CitationPage
 *
 * This task is used to generate a cover page with citation information for text
 * documents and then to add that cover page to a PDF version of the document
 * replacing the originally uploaded document form the user's perspective.
 *
 * @author Ryan McGowan
 */
@Distributive @Mutative public class CitationPage extends AbstractCurationTask {
  /**
     * Class Logger
     */
  private static final Logger log = org.apache.logging.log4j.LogManager.getLogger(CitationPage.class);

  protected int status = Curator.CURATE_UNSET;

  protected String result = null;

  /**
     * A StringBuilder to handle result string building process.
     */
  protected StringBuilder resBuilder;

  /**
     * The name to give the bundle we add the cited pages to.
     */
  protected static final String DISPLAY_BUNDLE_NAME = "DISPLAY";

  /**
     * The name of the bundle to move source documents into after they have been
     * cited.
     */
  protected static final String PRESERVATION_BUNDLE_NAME = "PRESERVATION";

  protected BitstreamService bitstreamService = ContentServiceFactory.getInstance().getBitstreamService();

  protected BundleService bundleService = ContentServiceFactory.getInstance().getBundleService();

  /**
     * {@inheritDoc}
     *
     * @see CurationTask#perform(DSpaceObject)
     */
  @Override public int perform(DSpaceObject dso) throws IOException {
    this.resBuilder = new StringBuilder();
    this.distribute(dso);
    this.result = this.resBuilder.toString();
    this.setResult(this.result);
    this.report(this.result);
    return this.status;
  }

  /**
     * {@inheritDoc}
     *
     * @see AbstractCurationTask#performItem(Item)
     */
  @Override protected void performItem(Item item) throws SQLException {
    List<Bundle> dBundles = itemService.getBundles(item, CitationPage.DISPLAY_BUNDLE_NAME);
    Bundle dBundle = null;
    if (dBundles == null || dBundles.isEmpty()) {
      try {
        dBundle = bundleService.create(Curator.curationContext(), item, CitationPage.DISPLAY_BUNDLE_NAME);
      } catch (AuthorizeException e) {
        log.error("User not authroized to create bundle on item \"" + item.getName() + "\": " + e.getMessage());
      }
    } else {
      dBundle = dBundles.get(0);
    }
    Map<String, Bitstream> displayMap = new HashMap<>();
    for (Bitstream bs : dBundle.getBitstreams()) {
      displayMap.put(bs.getName(), bs);
    }
    List<Bundle> pBundles = itemService.getBundles(item, CitationPage.PRESERVATION_BUNDLE_NAME);
    Bundle pBundle = null;
    List<Bundle> bundles = new ArrayList<>();
    if (pBundles != null && pBundles.size() > 0) {
      pBundle = pBundles.get(0);
      bundles.addAll(itemService.getBundles(item, "ORIGINAL"));
      bundles.addAll(pBundles);
    } else {
      try {
        pBundle = bundleService.create(Curator.curationContext(), item, CitationPage.PRESERVATION_BUNDLE_NAME);
      } catch (AuthorizeException e) {
        log.error("User not authroized to create bundle on item \"" + item.getName() + "\": " + e.getMessage());
      }
      bundles = itemService.getBundles(item, "ORIGINAL");
    }
    for (Bundle bundle : bundles) {
      List<Bitstream> bitstreams = bundle.getBitstreams();
      for (Bitstream bitstream : bitstreams) {
        CitationDocumentService citationDocument = DisseminateServiceFactory.getInstance().getCitationDocumentService();
        if (citationDocument.canGenerateCitationVersion(Curator.curationContext(), bitstream)) {
          this.resBuilder.append(item.getHandle()).append(" - ").append(bitstream.getName()).append(" is citable.");
          try {
            InputStream citedInputStream = citationDocument.makeCitedDocument(Curator.curationContext(), bitstream).getLeft();
            this.addCitedPageToItem(citedInputStream, bundle, pBundle, dBundle, displayMap, item, bitstream);
          } catch (Exception e) {
            e.printStackTrace();
            StackTraceElement[] stackTrace = e.getStackTrace();
            StringBuilder stack = new StringBuilder();
            int numLines = Math.min(stackTrace.length, 12);
            for (int j = 0; j < numLines; j++) {
              stack.append("\t").append(stackTrace[j].toString()).append("\n");
            }
            if (stackTrace.length > numLines) {
              stack.append("\t. . .\n");
            }
            log.error(e.toString() + " -> \n" + stack.toString());
            this.resBuilder.append(", but there was an error generating the PDF.\n");
            this.status = Curator.CURATE_ERROR;
          }
        } else {
          this.resBuilder.append(item.getHandle()).append(" - ").append(bitstream.getName()).append(" is not citable.\n");
          this.status = Curator.CURATE_SUCCESS;
        }
      }
    }
  }

  /**
     * A helper function for {@link CitationPage#performItem(Item)}. This function takes in the
     * cited document as a File and adds it to DSpace properly.
     *
     * @param citedDoc The inputstream that is the cited document.
     * @param bundle The bundle the cited file is from.
     * @param pBundle The preservation bundle. The original document should be
     * put in here if it is not already.
     * @param dBundle The display bundle. The cited document gets put in here.
     * @param displayMap The map of bitstream names to bitstreams in the display
     *                   bundle.
     * @param item       The item containing the bundles being used.
     * @param bitstream  The original source bitstream.
     * @throws SQLException       if database error
     * @throws AuthorizeException if authorization error
     * @throws IOException        if IO error
     */
  protected void addCitedPageToItem(InputStream citedDoc, Bundle bundle, Bundle pBundle, Bundle dBundle, Map<String, Bitstream> displayMap, Item item, Bitstream bitstream) throws SQLException, AuthorizeException, IOException {
    Context context = Curator.curationContext();
    if (!bundle.getID().equals(pBundle.getID())) {
      bundleService.addBitstream(context, pBundle, bitstream);
      bundleService.removeBitstream(context, bundle, bitstream);
      List<Bitstream> bitstreams = bundle.getBitstreams();
      if (bitstreams == null || bitstreams.isEmpty()) {
        itemService.removeBundle(context, item, bundle);
      }
    }
    if (displayMap.containsKey(bitstream.getName())) {
      bundleService.removeBitstream(context, dBundle, displayMap.get(bitstream.getName()));
    }
    Bitstream citedBitstream = bitstreamService.create(context, dBundle, citedDoc);
    citedDoc.close();
    citedBitstream.setName(context, bitstream.getName());
    bitstreamService.setFormat(context, citedBitstream, bitstream.getFormat(Curator.curationContext()));
    citedBitstream.setDescription(context, bitstream.getDescription());
    this.resBuilder.append(" Added ").append(citedBitstream.getName()).append(" to the ").append(CitationPage.DISPLAY_BUNDLE_NAME).append(" bundle.\n");
    itemService.update(context, item);
    this.status = Curator.CURATE_SUCCESS;
  }
}