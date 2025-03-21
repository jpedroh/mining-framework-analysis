package gov.nysenate.openleg.processors.bill;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import gov.nysenate.openleg.legislation.PublishStatus;
import gov.nysenate.openleg.legislation.SessionYear;
import gov.nysenate.openleg.legislation.bill.*;
import gov.nysenate.openleg.legislation.committee.Chamber;
import gov.nysenate.openleg.processors.AbstractLegDataProcessor;
import gov.nysenate.openleg.processors.ParseError;
import gov.nysenate.openleg.processors.bill.xml.XmlBillActionAnalyzer;
import gov.nysenate.openleg.updates.bill.BillFieldUpdateEvent;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Node;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static gov.nysenate.openleg.legislation.bill.BillTextFormat.PLAIN;

/**
 * The AbstractBillProcessor serves as a base class for actual bill processor implementations to provide unified
 * helper methods to address some of the quirks that are present when processing bill data.
 */
public abstract class AbstractBillProcessor extends AbstractLegDataProcessor {
  /** RULES Sponsors are formatted as RULES COM followed by the name of the sponsor that requested passage. */
  protected static final Pattern rulesSponsorPattern = Pattern.compile("^RULES (?:COM)? *\\(?([A-Z-_\']+(?: [A-Z]+)?)\\)?", Pattern.CASE_INSENSITIVE);

  /** The expected format for SameAs [5] block data. Same as Uni A 372, S 210 */
  protected static final Pattern sameAsPattern = Pattern.compile(
<<<<<<< /usr/src/app/output/nysenate/openlegislation/55f02d1256aabf0841995fe75f1beb820ed4c6bc/src/main/java/gov/nysenate/openleg/processors/bill/AbstractBillProcessor.java/left.java
  "Same as( Uni\\.)? (([A-Z] ?\\d{1,5}-?[A-Z]?(, *)?(?: / )?)+)"
=======
  "Same as( Uni\\.)? (([A-Z] ?[0-9]{1,5}-?[A-Z]?(, *)?(?: / )?)+)"
>>>>>>> /usr/src/app/output/nysenate/openlegislation/55f02d1256aabf0841995fe75f1beb820ed4c6bc/src/main/java/gov/nysenate/openleg/processors/bill/AbstractBillProcessor.java/right.java
  );

  /** The format for program info lines. */
  protected static final Pattern programInfoPattern = Pattern.compile("(\\d+)\\s+(.+)");

  /**
     * Handles parsing a Session member out of a sobi or xml file
     */
  protected void handlePrimaryMemberParsing(Bill baseBill, String sponsorLine, SessionYear sessionYear) {
    if (sponsorLine.trim().isEmpty()) {
      return;
    }
    Chamber chamber = baseBill.getBillType().getChamber();
    BillSponsor billSponsor = new BillSponsor();
    sponsorLine = sponsorLine.replace("(MS)", "").toUpperCase().trim();
    if (sponsorLine.startsWith("RULES")) {
      billSponsor.setRules(true);
      Matcher sposorMatch = rulesSponsorPattern.matcher(sponsorLine);
      if (sposorMatch.matches() && !"RULES COM".equals(sponsorLine)) {
        sponsorLine = sposorMatch.group(1);
        billSponsor.setMember(getMemberFromShortName(sponsorLine, sessionYear, chamber));
      } else {
        billSponsor.setMember(null);
      }
    } else {
      if (sponsorLine.startsWith("BUDGET")) {
        billSponsor.setBudget(true);
        billSponsor.setMember(null);
      } else {
        if (sponsorLine.startsWith("REDISTRICTING")) {
          billSponsor.setRedistricting(true);
          billSponsor.setMember(null);
        } else {
          if (sponsorLine.contains(",")) {
            List<String> sponsors = Lists.newArrayList(Splitter.on(",").omitEmptyStrings().trimResults().splitToList(sponsorLine));
            if (!sponsors.isEmpty()) {
              sponsorLine = sponsors.remove(0);
              for (String sponsor : sponsors) {
                baseBill.getAdditionalSponsors().add(getMemberFromShortName(sponsor, sessionYear, chamber));
              }
            }
          }
          billSponsor.setMember(getMemberFromShortName(sponsorLine, sessionYear, chamber));
        }
      }
    }
    baseBill.setSponsor(billSponsor);
  }


<<<<<<< /usr/src/app/output/nysenate/openlegislation/55f02d1256aabf0841995fe75f1beb820ed4c6bc/src/main/java/gov/nysenate/openleg/processors/bill/AbstractBillProcessor.java/left.java
  /**
     * Checks that the base bill's default amendment is published. If it isn't it will be set to published using
     * the source file's published date.
     * @param baseBill Bill
     * @param fragment LegDataFragment
     * @param source String - Indicates the origin of this publishing request, e.g. bill info line.
     */
  protected void ensureBaseBillIsPublished(Bill baseBill, LegDataFragment fragment, String source) {
    Optional<PublishStatus> pubStatus = baseBill.getPublishStatus(Version.ORIGINAL);
    if (pubStatus.isEmpty() || !pubStatus.get().isPublished()) {
      baseBill.updatePublishStatus(Version.ORIGINAL, new PublishStatus(true, fragment.getPublishedDateTime(), false, source));
      setModifiedDateTime(baseBill, fragment);
    }
  }
=======
>>>>>>> Unknown file: This is a bug in JDime.


  /**
     * Sets the law section to the specified amendment version.
     * @param baseBill Bill
     * @param specificVersion Version
     * @param lawSection String
     * @param fragment LegDataFragment
     */
  protected void setLawSection(Bill baseBill, Version specificVersion, String lawSection, LegDataFragment fragment) {
    if (lawSection == null) {
      lawSection = "";
    }
    baseBill.getAmendment(specificVersion).setLawSection(lawSection.trim());
    setModifiedDateTime(baseBill, fragment);
  }

  /**
     * Sets the title to the base bill.
     * @param baseBill Bill
     * @param title String
     * @param fragment LegDataFragment
     */
  protected void setTitle(Bill baseBill, String title, LegDataFragment fragment) {
    if (title == null) {
      title = "";
    }
    baseBill.setTitle(title.replace("\n", " ").trim());
    setModifiedDateTime(baseBill, fragment);
  }

  /**
     * Sets the summary to the base bill.
     * @param baseBill Bill
     * @param summary String
     * @param fragment SoboFragment
     */
  protected void setSummary(Bill baseBill, String summary, LegDataFragment fragment) {
    if (summary == null) {
      summary = "";
    }
    baseBill.setSummary(summary.replace("\n", " ").trim());
    setModifiedDateTime(baseBill, fragment);
  }

  /**
     * Applies information to bill events; replaces existing information in full.
     * Events are uniquely identified by text/date/sequenceNo/bill.
     *
     * Also parses bill events to apply several other bits of meta data to bills (see examples)
     *
     * Examples
     * --------------------------------------------------------------------
     * Same as             | 406/11/14 SUBSTITUTED BY A9504
     * --------------------------------------------------------------------
     * Stricken            | 403/10/14 RECOMMIT, ENACTING CLAUSE STRICKEN
     * --------------------------------------------------------------------
     * Current committee   | 406/21/13 COMMITTED TO RULES
     * --------------------------------------------------------------------
     *
     * There are currently no checks for the action list starting over again which
     * could lead back to back action blocks for a bill to produce a double long list.
     *
     * Bill events cannot be deleted, only replaced.
     *
     * @see BillActionParser
     * @throws ParseError
     */
  protected void parseActions(String data, Bill bill, BillAmendment specifiedAmendment, LegDataFragment fragment, Node xmlActions) throws ParseError {
    List<BillAction> billActions;
    if (xmlActions != null) {
      try {
        billActions = BillActionParser.parseActionsListXML(specifiedAmendment.getBillId(), xmlActions);
      } catch (ParseError e) {
        billActions = BillActionParser.parseActionsList(specifiedAmendment.getBillId(), data);
      }
    } else {
      billActions = BillActionParser.parseActionsList(specifiedAmendment.getBillId(), data);
    }
    bill.setActions(billActions);
    Optional<PublishStatus> defaultPubStatus = bill.getPublishStatus(Version.ORIGINAL);
    XmlBillActionAnalyzer analyzer = new XmlBillActionAnalyzer(specifiedAmendment.getBillId(), billActions, defaultPubStatus);
    analyzer.analyze();
    addAnyMissingAmendments(bill, billActions);
    final Version initialAV = bill.getActiveVersion();
    bill.setActiveVersion(analyzer.getActiveVersion());
    if (initialAV != bill.getActiveVersion()) {
      BillAmendment initialActiveAmend = bill.getAmendment(initialAV);
      BillAmendment newActiveAmend = bill.getActiveAmendment();
      newActiveAmend.setCoSponsors(initialActiveAmend.getCoSponsors());
      newActiveAmend.setMultiSponsors(initialActiveAmend.getMultiSponsors());
    }
    bill.setSubstitutedBy(analyzer.getSubstitutedBy());
    bill.setStatus(analyzer.getBillStatus());
    bill.setMilestones(analyzer.getMilestones());
    bill.setPastCommittees(analyzer.getPastCommittees());
    bill.setPublishStatuses(analyzer.getPublishStatusMap());
    bill.getAmendPublishStatusMap().keySet().forEach((version) -> getOrCreateBaseBill(bill.getBaseBillId().withVersion(version), fragment));
    analyzer.getSameAsMap().forEach((k, v) -> {
      if (bill.hasAmendment(k)) {
        bill.getAmendment(k).setSameAs(Sets.newHashSet(v));
      }
    });
    specifiedAmendment.setStricken(analyzer.isStricken());
  }


<<<<<<< /usr/src/app/output/nysenate/openlegislation/55f02d1256aabf0841995fe75f1beb820ed4c6bc/src/main/java/gov/nysenate/openleg/processors/bill/AbstractBillProcessor.java/left.java
  /**
     * For the specified amendment parse the same as data to obtain a list of same as bill id references.
     * If the uni bill flag is detected, the bill's uni bill status will be set to true and a uni bill
     * sync will be triggered.
     * @param baseBill Bill
     * @param version Version
     * @param sameAsData String
     * @param fragment LegDataFragment
     * @throws ParseError
     */
  protected void processSameAs(Bill baseBill, Version version, String sameAsData, LegDataFragment fragment) throws ParseError {
    Matcher sameAsMatcher = sameAsPattern.matcher(sameAsData);
    BillAmendment billAmendment = baseBill.getAmendment(version);
    if (sameAsMatcher.find()) {
      String matches = sameAsMatcher.group(2).replaceAll(" / ", ", ");
      List<String> sameAsMatches = new ArrayList<>(Arrays.asList(matches.split(", ")));
      billAmendment.getSameAs().addAll(sameAsMatches.stream().map((sameAs) -> new BillId(sameAs.replace("-", "").replace(" ", ""), baseBill.getSession())).toList());
      if (sameAsMatcher.group(1) != null && !sameAsMatcher.group(1).isEmpty()) {
        billAmendment.setUniBill(true);
        syncUniBillText(billAmendment, fragment);
      }
      setModifiedDateTime(baseBill, fragment);
    } else {
      throw new ParseError("sameAsPattern not matched: " + sameAsData);
    }
  }
=======
>>>>>>> Unknown file: This is a bug in JDime.


  /**
     * Sets the modified datetime to the base bill. This modified datetime is not guaranteed to reflect an actual
     * change to the bill or it's amendments since we receive duplicate data from LBDC frequently.
     * @param baseBill Bill
     * @param fragment SoboFragment
     */
  protected void setModifiedDateTime(Bill baseBill, LegDataFragment fragment) {
    baseBill.setModifiedDateTime(fragment.getPublishedDateTime());
  }

  /**
     * Uni-bills share text with their counterpart house. Ensure that the full text of bill amendments that
     * have a uni-bill designator are kept in sync.
     */
  protected void syncUniBillText(BillAmendment billAmendment, LegDataFragment legDataFragment) {
    billAmendment.getSameAs().forEach((uniBillId) -> {
      Bill uniBill = getOrCreateBaseBill(uniBillId, legDataFragment);
      BillAmendment uniBillAmend = uniBill.getAmendment(uniBillId.getVersion());
      BaseBillId updatedBillId = null;
      if (billAmendment.getBillType().getChamber().equals(Chamber.SENATE)) {
        copyBillTexts(billAmendment, uniBillAmend);
        updatedBillId = uniBillAmend.getBaseBillId();
      } else {
        if (StringUtils.isNotBlank(uniBillAmend.getFullText(PLAIN))) {
          copyBillTexts(uniBillAmend, billAmendment);
          updatedBillId = billAmendment.getBaseBillId();
        }
      }
      if (updatedBillId != null) {
        eventBus.post(new BillFieldUpdateEvent(updatedBillId, BillUpdateField.FULLTEXT));
      }
    });
  }

  /**
     * After the BillActionAnalyzer updates the actions with the proper amendment version, the baseBill must be updated
     * with those changes
     * @param baseBill
     * @param billActions
     */
  protected void addAnyMissingAmendments(Bill baseBill, List<BillAction> billActions) {
    for (BillAction action : billActions) {
      Version actionVersion = action.getBillId().getVersion();
      if (!baseBill.hasAmendment(actionVersion)) {
        BillAmendment baseAmendment = new BillAmendment(baseBill.getBaseBillId(), actionVersion);
        baseBill.addAmendment(baseAmendment);
      }
    }
  }

  private void copyBillTexts(BillAmendment sourceAmend, BillAmendment destAmend) {
    destAmend.setBillText(sourceAmend.getBillText());
  }
}