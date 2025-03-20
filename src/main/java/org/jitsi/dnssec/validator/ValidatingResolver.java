package org.jitsi.dnssec.validator;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jitsi.dnssec.SMessage;
import org.jitsi.dnssec.SRRset;
import org.jitsi.dnssec.SecurityStatus;
import org.jitsi.dnssec.R;
import org.jitsi.dnssec.validator.ValUtils.NsecProvesNodataResponse;
import org.xbill.DNS.CNAMERecord;
import org.xbill.DNS.DClass;
import org.xbill.DNS.DNAMERecord;
import org.xbill.DNS.ExtendedFlags;
import org.xbill.DNS.Flags;
import org.xbill.DNS.Header;
import org.xbill.DNS.Master;
import org.xbill.DNS.Message;
import org.xbill.DNS.NSECRecord;
import org.xbill.DNS.Name;
import org.xbill.DNS.NameTooLongException;
import org.xbill.DNS.Rcode;
import org.xbill.DNS.Record;
import org.xbill.DNS.Resolver;
import org.xbill.DNS.ResolverListener;
import org.xbill.DNS.Section;
import org.xbill.DNS.TSIG;
import org.xbill.DNS.TXTRecord;
import org.xbill.DNS.Type;

/**
 * This resolver validates responses with DNSSEC.
 */
public class ValidatingResolver implements Resolver {
  /**
     * The QCLASS being used for the injection of the reason why the validator
     * came to the returned result.
     */
  public static final int VALIDATION_REASON_QCLASS = 65280;

  private static final Logger logger = LoggerFactory.getLogger(ValidatingResolver.class);

  /**
     * This is the TTL to use when a trust anchor priming query failed to
     * validate.
     */
  private static final long DEFAULT_TA_BAD_KEY_TTL = 60;

  /**
     * This is a cache of validated, but expirable DNSKEY rrsets.
     */
  private KeyCache keyCache;

  /**
     * A data structure holding all trust anchors. Trust anchors must be
     * "primed" into the cache before being used to validate.
     */
  private TrustAnchorStore trustAnchors;

  /**
     * The local validation utilities.
     */
  private ValUtils valUtils;

  /**
     * The local NSEC3 validation utilities.
     */
  private NSEC3ValUtils n3valUtils;

  /**
     * The resolver that performs the actual DNS lookups.
     */
  private Resolver headResolver;

  /**
     * Creates a new instance of this class.
     * 
     * @param headResolver The resolver to which queries for DS, DNSKEY and
     *            referring CNAME records are sent.
     */
  public ValidatingResolver(Resolver headResolver) {
    this.headResolver = headResolver;
    headResolver.setEDNS(0, 0, ExtendedFlags.DO, null);
    headResolver.setIgnoreTruncation(false);
    this.keyCache = new KeyCache();
    this.valUtils = new ValUtils();
    this.n3valUtils = new NSEC3ValUtils();
    this.trustAnchors = new TrustAnchorStore();
  }

  /**
     * Initialize the module. The only recognized configuration value is
     * <tt>org.jitsi.dnssec.trust_anchor_file</tt>.
     * 
     * @param config The configuration data for this module.
     * @throws IOException When the file specified in the config does not exist
     *             or cannot be read.
     */
  public void init(Properties config) throws IOException {
    this.keyCache.init(config);
    this.n3valUtils.init(config);
    this.valUtils.init(config);
    String s = config.getProperty("org.jitsi.dnssec.trust_anchor_file");
    if (s != null) {
      logger.debug("reading trust anchor file file: " + s);
      this.loadTrustAnchors(new FileInputStream(s));
    }
  }

  /**
     * Load the trust anchor file into the trust anchor store. The trust anchors
     * are currently stored in a zone file format list of DNSKEY or DS records.
     * 
     * @param data The trust anchor data.
     * @throws IOException when the trust anchor data could not be read.
     */
  @SuppressWarnings(value = { "unchecked" }) public void loadTrustAnchors(InputStream data) throws IOException {
    Master master = new Master(data, Name.root, 0);
    List<Record> records = new ArrayList<Record>();
    Record mr;
    while ((mr = master.nextRecord()) != null) {
      records.add(mr);
    }
    Collections.sort(records);
    SRRset currentRrset = new SRRset();
    for (Record r : records) {
      if (r.getType() != Type.DNSKEY && r.getType() != Type.DS) {
        continue;
      }
      if (currentRrset.size() == 0) {
        currentRrset.addRR(r);
        continue;
      }
      if (currentRrset.getName().equals(r.getName()) && currentRrset.getType() == r.getType() && currentRrset.getDClass() == r.getDClass()) {
        currentRrset.addRR(r);
        continue;
      }
      this.trustAnchors.store(currentRrset);
      currentRrset = new SRRset();
      currentRrset.addRR(r);
    }
    if (currentRrset.size() > 0) {
      this.trustAnchors.store(currentRrset);
    }
  }

  /**
     * Gets the store with the loaded trust anchors.
     * 
     * @return The store with the loaded trust anchors.
     */
  public TrustAnchorStore getTrustAnchors() {
    return this.trustAnchors;
  }

  /**
     * For messages that are not referrals, if the chase reply contains an
     * unsigned NS record in the authority section it could have been inserted
     * by a (BIND) forwarder that thinks the zone is insecure, and that has an
     * NS record without signatures in cache. Remove the NS record since the
     * reply does not hinge on that record (in the authority section), but do
     * not remove it if it removes the last record from the answer+authority
     * sections.
     *
     * @param response: the chased reply, we have a key for this contents, so we
     *            should have signatures for these rrsets and not having
     *            signatures means it will be bogus.
     */
  private void removeSpuriousAuthority(SMessage response) {
    if (response.getSectionRRsets(Section.ANSWER).size() == 0 && response.getSectionRRsets(Section.AUTHORITY).size() == 1) {
      return;
    }
    Iterator<SRRset> authRrsetIterator = response.getSectionRRsets(Section.AUTHORITY).iterator();
    while (authRrsetIterator.hasNext()) {
      SRRset rrset = authRrsetIterator.next();
      if (rrset.getType() == Type.NS) {
        if (!rrset.sigs().hasNext()) {
          logger.trace("Removing spurious unsigned NS record (likely inserted by forwarder) {}/{}/{}", rrset.getName(), Type.string(rrset.getType()), DClass.string(rrset.getDClass()));
          authRrsetIterator.remove();
        }
      }
    }
  }

  /**
     * Given a "postive" response -- a response that contains an answer to the
     * question, and no CNAME chain, validate this response. This generally
     * consists of verifying the answer RRset and the authority RRsets.
     * 
     * Given an "ANY" response -- a response that contains an answer to a
     * qtype==ANY question, with answers. This consists of simply verifying all
     * present answer/auth RRsets, with no checking that all types are present.
     * 
     * NOTE: it may be possible to get parent-side delegation point records
     * here, which won't all be signed. Right now, this routine relies on the
     * upstream iterative resolver to not return these responses -- instead
     * treating them as referrals.
     * 
     * NOTE: RFC 4035 is silent on this issue, so this may change upon
     * clarification.
     * 
     * @param request The request that generated this response.
     * @param response The response to validate.
     */
  private void validatePositiveResponse(Message request, SMessage response) {
    int qtype = request.getQuestion().getType();
    Map<Name, Name> wcs = new HashMap<Name, Name>(1);
    List<SRRset> nsec3s = new ArrayList<SRRset>(0);
    List<SRRset> nsecs = new ArrayList<SRRset>(0);
    if (!this.validateAnswerAndGetWildcards(response, qtype, wcs)) {
      return;
    }
    SRRset keyRrset;
    int[] sections;
    if (request.getQuestion().getType() == Type.ANY) {
      sections = new int[] { Section.ANSWER, Section.AUTHORITY };
    } else {
      sections = new int[] { Section.AUTHORITY };
    }
    for (int section : sections) {
      for (SRRset set : response.getSectionRRsets(section)) {
        KeyEntry ke = this.prepareFindKey(set);
        if (!this.processKeyValidate(response, set.getSignerName(), ke)) {
          return;
        }
        keyRrset = ke.getRRset();
        SecurityStatus status = this.valUtils.verifySRRset(set, keyRrset);
        if (status != SecurityStatus.SECURE) {
          response.setBogus(R.get("failed.authority.positive", set));
          return;
        }
        if (wcs.size() > 0) {
          if (set.getType() == Type.NSEC) {
            nsecs.add(set);
          } else {
            if (set.getType() == Type.NSEC3) {
              nsec3s.add(set);
            }
          }
        }
      }
    }
    if (wcs.size() > 0) {
      for (Map.Entry<Name, Name> wc : wcs.entrySet()) {
        boolean wcNsecOk = false;
        for (SRRset set : nsecs) {
          NSECRecord nsec = (NSECRecord) set.first();
          if (ValUtils.nsecProvesNameError(nsec, wc.getKey(), set.getSignerName())) {
            try {
              Name nsecWc = ValUtils.nsecWildcard(wc.getKey(), nsec);
              if (wc.getValue().equals(nsecWc)) {
                wcNsecOk = true;
                break;
              }
            } catch (NameTooLongException e) {
              throw new RuntimeException(R.get("failed.positive.wildcardgeneration"));
            }
          }
        }
        if (!wcNsecOk && nsec3s.size() > 0) {
          if (this.n3valUtils.allNSEC3sIgnoreable(nsec3s, this.keyCache)) {
            response.setStatus(SecurityStatus.INSECURE, R.get("failed.nsec3_ignored"));
            return;
          }
          SecurityStatus status = this.n3valUtils.proveWildcard(nsec3s, wc.getKey(), nsec3s.get(0).getSignerName(), wc.getValue());
          if (status == SecurityStatus.INSECURE) {
            response.setStatus(status);
            return;
          } else {
            if (status == SecurityStatus.SECURE) {
              wcNsecOk = true;
            }
          }
        }
        if (!wcNsecOk) {
          response.setBogus(R.get("failed.positive.wildcard_too_broad"));
          return;
        }
      }
    }
    response.setStatus(SecurityStatus.SECURE);
  }

  private boolean validateAnswerAndGetWildcards(SMessage response, int qtype, Map<Name, Name> wcs) {
    DNAMERecord dname = null;
    for (SRRset set : response.getSectionRRsets(Section.ANSWER)) {
      if (set.getType() == Type.CNAME && dname != null) {
        if (set.size() > 1) {
          response.setBogus(R.get("failed.synthesize.multiple"));
          return false;
        }
        CNAMERecord cname = (CNAMERecord) set.first();
        try {
          Name expected = Name.concatenate(cname.getName().relativize(dname.getName()), dname.getTarget());
          if (!expected.equals(cname.getTarget())) {
            response.setBogus(R.get("failed.synthesize.nomatch", cname.getTarget(), expected));
            return false;
          }
        } catch (NameTooLongException e) {
          response.setBogus(R.get("failed.synthesize.toolong"));
          return false;
        }
        set.setSecurityStatus(SecurityStatus.SECURE);
        dname = null;
        continue;
      }
      KeyEntry ke = this.prepareFindKey(set);
      if (!this.processKeyValidate(response, set.getSignerName(), ke)) {
        return false;
      }
      SecurityStatus status = this.valUtils.verifySRRset(set, ke.getRRset());
      if (status != SecurityStatus.SECURE) {
        response.setBogus(R.get("failed.answer.positive", set));
        return false;
      }
      Name wc = null;
      try {
        wc = ValUtils.rrsetWildcard(set);
      } catch (RuntimeException ex) {
        response.setBogus(R.get(ex.getMessage(), set.getName()));
        return false;
      }
      if (wc != null) {
        if (set.getType() == Type.DNAME) {
          response.setBogus(R.get("failed.dname.wildcard", set.getName()));
          return false;
        }
        wcs.put(set.getName(), wc);
      }
      if (qtype != Type.DNAME && set.getType() == Type.DNAME) {
        dname = (DNAMERecord) set.first();
      }
    }
    return true;
  }

  /**
     * Validate a NOERROR/NODATA signed response -- a response that has a
     * NOERROR Rcode but no ANSWER section RRsets. This consists of verifying
     * the authority section rrsets and making certain that the authority
     * section NSEC/NSEC3s proves that the qname does exist and the qtype
     * doesn't.
     * 
     * Note that by the time this method is called, the process of finding the
     * trusted DNSKEY rrset that signs this response must already have been
     * completed.
     * 
     * @param request The request that generated this response.
     * @param response The response to validate.
     */
  private void validateNodataResponse(Message request, SMessage response) {
    Name qname = request.getQuestion().getName();
    int qtype = request.getQuestion().getType();
    for (SRRset set : response.getSectionRRsets(Section.ANSWER)) {
      if (set.getSecurityStatus() != SecurityStatus.SECURE) {
        response.setBogus(R.get("failed.answer.cname_nodata", set.getName()));
        return;
      }
      if (set.getType() == Type.CNAME) {
        qname = ((CNAMERecord) set.first()).getTarget();
      }
    }
    boolean hasValidNSEC = false;
    Name ce = null;
    NsecProvesNodataResponse ndp = new NsecProvesNodataResponse();
    List<SRRset> nsec3s = new ArrayList<SRRset>(0);
    Name nsec3Signer = null;
    for (SRRset set : response.getSectionRRsets(Section.AUTHORITY)) {
      KeyEntry ke = this.prepareFindKey(set);
      if (!this.processKeyValidate(response, set.getSignerName(), ke)) {
        return;
      }
      SecurityStatus status = this.valUtils.verifySRRset(set, ke.getRRset());
      if (status != SecurityStatus.SECURE) {
        response.setBogus(R.get("failed.authority.nodata", set));
        return;
      }
      if (set.getType() == Type.NSEC) {
        NSECRecord nsec = (NSECRecord) set.first();
        ndp = ValUtils.nsecProvesNodata(nsec, qname, qtype);
        if (ndp.result) {
          hasValidNSEC = true;
        }
        if (ValUtils.nsecProvesNameError(nsec, qname, set.getSignerName())) {
          ce = ValUtils.closestEncloser(qname, nsec);
        }
      }
      if (set.getType() == Type.NSEC3) {
        nsec3s.add(set);
        nsec3Signer = set.getSignerName();
      }
    }
    if (ndp.wc != null && (ce == null || (!ce.equals(ndp.wc) && !qname.equals(ce)))) {
      hasValidNSEC = false;
    }
    this.n3valUtils.stripUnknownAlgNSEC3s(nsec3s);
    if (!hasValidNSEC && nsec3s.size() > 0) {
      if (this.n3valUtils.allNSEC3sIgnoreable(nsec3s, this.keyCache)) {
        response.setStatus(SecurityStatus.BOGUS, R.get("failed.nsec3_ignored"));
        return;
      }
      SecurityStatus status = this.n3valUtils.proveNodata(nsec3s, qname, qtype, nsec3Signer);
      if (status == SecurityStatus.INSECURE) {
        response.setStatus(SecurityStatus.INSECURE);
        return;
      }
      hasValidNSEC = status == SecurityStatus.SECURE;
    }
    if (!hasValidNSEC) {
      response.setBogus(R.get("failed.nodata"));
      logger.trace("Failed NODATA for " + qname);
      return;
    }
    logger.trace("sucessfully validated NODATA response.");
    response.setStatus(SecurityStatus.SECURE);
  }

  /**
     * Validate a NAMEERROR signed response -- a response that has a NXDOMAIN
     * Rcode. This consists of verifying the authority section rrsets and making
     * certain that the authority section NSEC proves that the qname doesn't
     * exist and the covering wildcard also doesn't exist..
     * 
     * Note that by the time this method is called, the process of finding the
     * trusted DNSKEY rrset that signs this response must already have been
     * completed.
     * 
     * @param request The request to be proved to not exist.
     * @param response The response to validate.
     */
  private void validateNameErrorResponse(Message request, SMessage response) {
    Name qname = request.getQuestion().getName();
    for (SRRset set : response.getSectionRRsets(Section.ANSWER)) {
      if (set.getSecurityStatus() != SecurityStatus.SECURE) {
        response.setBogus(R.get("failed.nxdomain.cname_nxdomain", set));
        return;
      }
      if (set.getType() == Type.CNAME) {
        qname = ((CNAMERecord) set.first()).getTarget();
      }
    }
    boolean hasValidNSEC = false;
    boolean hasValidWCNSEC = false;
    List<SRRset> nsec3s = new ArrayList<SRRset>(0);
    Name nsec3Signer = null;
    SRRset keyRrset;
    for (SRRset set : response.getSectionRRsets(Section.AUTHORITY)) {
      KeyEntry ke = this.prepareFindKey(set);
      if (!this.processKeyValidate(response, set.getSignerName(), ke)) {
        return;
      }
      keyRrset = ke.getRRset();
      SecurityStatus status = this.valUtils.verifySRRset(set, keyRrset);
      if (status != SecurityStatus.SECURE) {
        response.setBogus(R.get("failed.nxdomain.authority", set));
        return;
      }
      if (set.getType() == Type.NSEC) {
        NSECRecord nsec = (NSECRecord) set.first();
        if (ValUtils.nsecProvesNameError(nsec, qname, set.getSignerName())) {
          hasValidNSEC = true;
        }
        if (ValUtils.nsecProvesNoWC(nsec, qname, set.getSignerName())) {
          hasValidWCNSEC = true;
        }
      }
      if (set.getType() == Type.NSEC3) {
        nsec3s.add(set);
        nsec3Signer = set.getSignerName();
      }
    }
    this.n3valUtils.stripUnknownAlgNSEC3s(nsec3s);
    if ((!hasValidNSEC || !hasValidWCNSEC) && nsec3s.size() > 0) {
      logger.debug("Validating nxdomain: using NSEC3 records");
      if (this.n3valUtils.allNSEC3sIgnoreable(nsec3s, this.keyCache)) {
        response.setStatus(SecurityStatus.INSECURE, R.get("failed.nsec3_ignored"));
        return;
      }
      SecurityStatus status = this.n3valUtils.proveNameError(nsec3s, qname, nsec3Signer);
      if (status != SecurityStatus.SECURE) {
        if (status == SecurityStatus.INSECURE) {
          response.setStatus(status, R.get("failed.nxdomain.nsec3_insecure"));
        } else {
          response.setStatus(status, R.get("failed.nxdomain.nsec3_bogus"));
        }
        return;
      }
      hasValidNSEC = true;
      hasValidWCNSEC = true;
    }
    if (!hasValidNSEC) {
      response.setBogus(R.get("failed.nxdomain.exists", response.getQuestion().getName()));
      return;
    }
    if (!hasValidWCNSEC) {
      response.setBogus(R.get("failed.nxdomain.haswildcard"));
      return;
    }
    logger.trace("successfully validated NAME ERROR response.");
    response.setStatus(SecurityStatus.SECURE);
  }

  private SMessage sendRequest(Message request) {
    Record q = request.getQuestion();
    logger.trace("sending request: <" + q.getName() + "/" + Type.string(q.getType()) + "/" + DClass.string(q.getDClass()) + ">");
    Message localRequest = (Message) request.clone();
    localRequest.getHeader().setFlag(Flags.CD);
    try {
      Message resp = this.headResolver.send(localRequest);
      return new SMessage(resp);
    } catch (SocketTimeoutException e) {
      logger.error("Query timed out, returning fail", e);
      return ValidatingResolver.errorMessage(localRequest, Rcode.SERVFAIL);
    } catch (UnknownHostException e) {
      logger.error("failed to send query", e);
      return ValidatingResolver.errorMessage(localRequest, Rcode.SERVFAIL);
    } catch (IOException e) {
      logger.error("failed to send query", e);
      return ValidatingResolver.errorMessage(localRequest, Rcode.SERVFAIL);
    }
  }

  private KeyEntry prepareFindKey(SRRset rrset) {
    FindKeyState state = new FindKeyState();
    state.signerName = rrset.getSignerName();
    state.qclass = rrset.getDClass();
    if (state.signerName == null) {
      state.signerName = rrset.getName();
    }
    SRRset trustAnchorRRset = this.trustAnchors.find(state.signerName, rrset.getDClass());
    if (trustAnchorRRset == null) {
      return KeyEntry.newNullKeyEntry(rrset.getSignerName(), rrset.getDClass(), DEFAULT_TA_BAD_KEY_TTL);
    }
    state.keyEntry = this.keyCache.find(state.signerName, rrset.getDClass());
    if (state.keyEntry == null || (!state.keyEntry.getName().equals(state.signerName) && state.keyEntry.isGood())) {
      state.dsRRset = trustAnchorRRset;
      state.keyEntry = null;
      state.currentDSKeyName = new Name(trustAnchorRRset.getName(), 1);
      this.processFindKey(state);
    }
    return state.keyEntry;
  }

  /**
     * Process the FINDKEY state. Generally this just calculates the next name
     * to query and either issues a DS or a DNSKEY query. It will check to see
     * if the correct key has already been reached, in which case it will
     * advance the event to the next state.
     * 
     * @param state The state associated with the current key finding phase.
     */
  private void processFindKey(FindKeyState state) {
    int qclass = state.qclass;
    Name targetKeyName = state.signerName;
    Name currentKeyName = Name.empty;
    if (state.keyEntry != null) {
      currentKeyName = state.keyEntry.getName();
    }
    if (state.currentDSKeyName != null) {
      currentKeyName = state.currentDSKeyName;
      state.currentDSKeyName = null;
    }
    if (currentKeyName.equals(targetKeyName)) {
      return;
    }
    if (state.emptyDSName != null) {
      currentKeyName = state.emptyDSName;
    }
    int targetLabels = targetKeyName.labels();
    int currentLabels = currentKeyName.labels();
    int l = targetLabels - currentLabels - 1;
    if (l < 0) {
      return;
    }
    Name nextKeyName = new Name(targetKeyName, l);
    logger.trace("findKey: targetKeyName = " + targetKeyName + ", currentKeyName = " + currentKeyName + ", nextKeyName = " + nextKeyName);
    if (state.dsRRset == null || !state.dsRRset.getName().equals(nextKeyName)) {
      Message dsRequest = Message.newQuery(Record.newRecord(nextKeyName, Type.DS, qclass));
      SMessage dsResponse = this.sendRequest(dsRequest);
      this.processDSResponse(dsRequest, dsResponse, state);
      return;
    }
    Message dnskeyRequest = Message.newQuery(Record.newRecord(state.dsRRset.getName(), Type.DNSKEY, qclass));
    SMessage dnskeyResponse = this.sendRequest(dnskeyRequest);
    this.processDNSKEYResponse(dnskeyRequest, dnskeyResponse, state);
  }

  /**
     * Given a DS response, the DS request, and the current key rrset, validate
     * the DS response, returning a KeyEntry.
     * 
     * @param response The DS response.
     * @param request The DS request.
     * @param keyRrset The current DNSKEY rrset from the forEvent state.
     * 
     * @return A KeyEntry, bad if the DS response fails to validate, null if the
     *         DS response indicated an end to secure space, good if the DS
     *         validated. It returns null if the DS response indicated that the
     *         request wasn't a delegation point.
     */
  private KeyEntry dsResponseToKE(SMessage response, Message request, SRRset keyRrset) {
    Name qname = request.getQuestion().getName();
    int qclass = request.getQuestion().getDClass();
    SecurityStatus status;
    ResponseClassification subtype = ValUtils.classifyResponse(request, response);
    KeyEntry bogusKE = KeyEntry.newBadKeyEntry(qname, qclass, DEFAULT_TA_BAD_KEY_TTL);
    switch (subtype) {
      case POSITIVE:
      SRRset dsRrset = response.findAnswerRRset(qname, Type.DS, qclass);
      status = this.valUtils.verifySRRset(dsRrset, keyRrset);
      if (status != SecurityStatus.SECURE) {
        bogusKE.setBadReason(R.get("failed.ds"));
        return bogusKE;
      }
      if (!ValUtils.atLeastOneSupportedAlgorithm(dsRrset)) {
        KeyEntry nullKey = KeyEntry.newNullKeyEntry(qname, qclass, dsRrset.getTTL());
        nullKey.setBadReason(R.get("insecure.ds.noalgorithms", qname));
        return nullKey;
      }
      logger.trace("DS rrset was good.");
      return KeyEntry.newKeyEntry(dsRrset);
      case CNAME:
      SRRset cnameRrset = response.findAnswerRRset(qname, Type.CNAME, qclass);
      status = this.valUtils.verifySRRset(cnameRrset, keyRrset);
      if (status == SecurityStatus.SECURE) {
        return null;
      }
      bogusKE.setBadReason(R.get("failed.ds.cname"));
      return bogusKE;
      case NODATA:
      case NAMEERROR:
      return this.dsReponseToKeForNodata(response, request, keyRrset);
      default:
      bogusKE.setBadReason(R.get("failed.ds.notype", subtype));
      return bogusKE;
    }
  }

  /**
     * Given a DS response, the DS request, and the current key rrset, validate
     * the DS response for the NODATA case, returning a KeyEntry.
     * 
     * @param response The DS response.
     * @param request The DS request.
     * @param keyRrset The current DNSKEY rrset from the forEvent state.
     * 
     * @return A KeyEntry, bad if the DS response fails to validate, null if the
     *         DS response indicated an end to secure space, good if the DS
     *         validated. It returns null if the DS response indicated that the
     *         request wasn't a delegation point.
     */
  private KeyEntry dsReponseToKeForNodata(SMessage response, Message request, SRRset keyRrset) {
    Name qname = request.getQuestion().getName();
    int qclass = request.getQuestion().getDClass();
    KeyEntry bogusKE = KeyEntry.newBadKeyEntry(qname, qclass, DEFAULT_TA_BAD_KEY_TTL);
    if (!this.valUtils.hasSignedNsecs(response)) {
      bogusKE.setBadReason(R.get("failed.ds.nonsec", qname));
      return bogusKE;
    }
    JustifiedSecStatus status = this.valUtils.nsecProvesNodataDsReply(request, response, keyRrset);
    switch (status.status) {
      case SECURE:
      KeyEntry nullKey = KeyEntry.newNullKeyEntry(qname, qclass, DEFAULT_TA_BAD_KEY_TTL);
      nullKey.setBadReason(R.get("insecure.ds.nsec"));
      return nullKey;
      case INSECURE:
      return null;
      case BOGUS:
      bogusKE.setBadReason(status.reason);
      return bogusKE;
      default:
      break;
    }
    SRRset[] nsec3Rrsets = response.getSectionRRsets(Section.AUTHORITY, Type.NSEC3);
    List<SRRset> nsec3s = new ArrayList<SRRset>(0);
    Name nsec3Signer = null;
    long nsec3TTL = -1;
    if (nsec3Rrsets.length > 0) {
      for (SRRset nsec3set : nsec3Rrsets) {
        SecurityStatus sstatus = this.valUtils.verifySRRset(nsec3set, keyRrset);
        if (sstatus != SecurityStatus.SECURE) {
          logger.debug("skipping bad nsec3");
          continue;
        }
        nsec3Signer = nsec3set.getSignerName();
        if (nsec3TTL < 0 || nsec3set.getTTL() < nsec3TTL) {
          nsec3TTL = nsec3set.getTTL();
        }
        nsec3s.add(nsec3set);
      }
      switch (this.n3valUtils.proveNoDS(nsec3s, qname, nsec3Signer)) {
        case INSECURE:
        case SECURE:
        KeyEntry nullKey = KeyEntry.newNullKeyEntry(qname, qclass, nsec3TTL);
        nullKey.setBadReason(R.get("insecure.ds.nsec3"));
        return nullKey;
        case INDETERMINATE:
        logger.debug("nsec3s for the referral proved no delegation.");
        return null;
        case BOGUS:
        bogusKE.setBadReason(R.get("failed.ds.nsec3"));
        return bogusKE;
        default:
        bogusKE.setBadReason(R.get("unknown.ds.nsec3"));
        return bogusKE;
      }
    }
    bogusKE.setBadReason(R.get("failed.ds.unknown"));
    return bogusKE;
  }

  /**
     * This handles the responses to locally generated DS queries.
     * 
     * @param request The request for which the response is processed.
     * @param response The response to process.
     * @param state The state associated with the current key finding phase.
     */
  private void processDSResponse(Message request, SMessage response, FindKeyState state) {
    Name qname = request.getQuestion().getName();
    state.emptyDSName = null;
    state.dsRRset = null;
    KeyEntry dsKE = this.dsResponseToKE(response, request, state.keyEntry.getRRset());
    if (dsKE == null) {
      state.emptyDSName = qname;
    } else {
      if (dsKE.isGood()) {
        state.dsRRset = dsKE.getRRset();
        state.currentDSKeyName = new Name(dsKE.getRRset().getName(), 1);
      } else {
        state.keyEntry = dsKE;
        if (dsKE.isNull()) {
          this.keyCache.store(dsKE);
        }
        return;
      }
    }
    this.processFindKey(state);
  }

  private void processDNSKEYResponse(Message request, SMessage response, FindKeyState state) {
    Name qname = request.getQuestion().getName();
    int qclass = request.getQuestion().getDClass();
    SRRset dnskeyRrset = response.findAnswerRRset(qname, Type.DNSKEY, qclass);
    if (dnskeyRrset == null) {
      state.keyEntry = KeyEntry.newBadKeyEntry(qname, qclass, DEFAULT_TA_BAD_KEY_TTL);
      state.keyEntry.setBadReason(R.get("dnskey.no_rrset", qname));
      return;
    }
    state.keyEntry = this.valUtils.verifyNewDNSKEYs(dnskeyRrset, state.dsRRset, DEFAULT_TA_BAD_KEY_TTL);
    if (!state.keyEntry.isGood()) {
      return;
    }
    this.keyCache.store(state.keyEntry);
    this.processFindKey(state);
  }

  private boolean processKeyValidate(SMessage response, Name signerName, KeyEntry keyEntry) {
    if (signerName == null) {
      logger.debug("processKeyValidate: no signerName.");
      if (keyEntry.isNull()) {
        String reason = keyEntry.getBadReason();
        if (reason == null) {
          reason = R.get("validate.insecure_unsigned");
        }
        response.setStatus(SecurityStatus.INSECURE, reason);
        return false;
      }
      if (keyEntry.isGood()) {
        response.setStatus(SecurityStatus.BOGUS, R.get("validate.bogus.missingsig"));
        return false;
      }
      response.setStatus(SecurityStatus.BOGUS, R.get("validate.bogus", keyEntry.getBadReason()));
      return false;
    }
    if (keyEntry.isBad()) {
      response.setStatus(SecurityStatus.BOGUS, R.get("validate.bogus.badkey", keyEntry.getName(), keyEntry.getBadReason()));
      return false;
    }
    if (keyEntry.isNull()) {
      String reason = keyEntry.getBadReason();
      if (reason == null) {
        reason = R.get("validate.insecure");
      }
      response.setStatus(SecurityStatus.INSECURE, reason);
      return false;
    }
    return true;
  }

  private SMessage processValidate(Message request, SMessage response) {
    ResponseClassification subtype = ValUtils.classifyResponse(request, response);
    if (subtype != ResponseClassification.REFERRAL) {
      this.removeSpuriousAuthority(response);
    }
    switch (subtype) {
      case POSITIVE:
      case CNAME:
      case ANY:
      logger.trace("Validating a positive response");
      this.validatePositiveResponse(request, response);
      break;
      case NODATA:
      logger.trace("Validating a nodata response");
      this.validateNodataResponse(request, response);
      break;
      case CNAME_NODATA:
      logger.trace("Validating a CNAME_NODATA response");
      this.validatePositiveResponse(request, response);
      if (response.getStatus() != SecurityStatus.INSECURE) {
        response.setStatus(SecurityStatus.UNCHECKED);
        this.validateNodataResponse(request, response);
      }
      break;
      case NAMEERROR:
      logger.trace("Validating a nxdomain response");
      this.validateNameErrorResponse(request, response);
      break;
      case CNAME_NAMEERROR:
      logger.trace("Validating a cname_nxdomain response");
      this.validatePositiveResponse(request, response);
      if (response.getStatus() != SecurityStatus.INSECURE) {
        response.setStatus(SecurityStatus.UNCHECKED);
        this.validateNameErrorResponse(request, response);
      }
      break;
      default:
      response.setStatus(SecurityStatus.BOGUS, R.get("validate.response.unknown", subtype));
    }
    return this.processFinishedState(request, response);
  }

  /**
     * Apply any final massaging to a response before returning up the pipeline.
     * Primarily this means setting the AD bit or not and possibly stripping
     * DNSSEC data.
     */
  private SMessage processFinishedState(Message request, SMessage response) {
    SecurityStatus status = response.getStatus();
    String reason = response.getBogusReason();
    switch (status) {
      case BOGUS:
      int code = response.getHeader().getRcode();
      if (code == Rcode.NOERROR || code == Rcode.NXDOMAIN || code == Rcode.YXDOMAIN) {
        code = Rcode.SERVFAIL;
      }
      response = ValidatingResolver.errorMessage(request, code);
      break;
      case SECURE:
      response.getHeader().setFlag(Flags.AD);
      break;
      case UNCHECKED:
      case INSECURE:
      break;
      default:
      throw new RuntimeException("unexpected security status");
    }
    response.setStatus(status, reason);
    return response;
  }

  /**
     * Forwards the data to the head resolver passed at construction time.
     * 
     * @param port The IP destination port for the queries sent.
     * @see org.xbill.DNS.Resolver#setPort(int)
     */
  public void setPort(int port) {
    this.headResolver.setPort(port);
  }

  /**
     * Forwards the data to the head resolver passed at construction time.
     * 
     * @param flag <code>true</code> to enable TCP, <code>false</code> to
     *            disable it.
     * @see org.xbill.DNS.Resolver#setTCP(boolean)
     */
  public void setTCP(boolean flag) {
    this.headResolver.setTCP(flag);
  }

  /**
     * This is a no-op, truncation is never ignored.
     * 
     * @param flag unused
     */
  public void setIgnoreTruncation(boolean flag) {
  }

  /**
     * This is a no-op, EDNS is always set to level 0.
     * 
     * @param level unused
     */
  public void setEDNS(int level) {
  }

  /**
     * The method is forwarded to the resolver, but always ensure that the level
     * is 0 and the flags contains DO.
     * 
     * @param level unused, always set to 0.
     * @param payloadSize The maximum DNS packet size that this host is capable
     *            of receiving over UDP. If 0 is specified, the default (1280)
     *            is used.
     * @param flags EDNS extended flags to be set in the OPT record,
     *            {@link ExtendedFlags#DO} is always appended.
     * @param options EDNS options to be set in the OPT record, specified as a
     *            List of OPTRecord.Option elements.
     * @see org.xbill.DNS.Resolver#setEDNS(int, int, int, java.util.List)
     */
  public void setEDNS(int level, int payloadSize, int flags, @SuppressWarnings(value = { "rawtypes" }) List options) {
    this.headResolver.setEDNS(0, payloadSize, flags | ExtendedFlags.DO, options);
  }

  /**
     * Forwards the data to the head resolver passed at construction time.
     * 
     * @param key The key.
     * @see org.xbill.DNS.Resolver#setTSIGKey(org.xbill.DNS.TSIG)
     */
  public void setTSIGKey(TSIG key) {
    this.headResolver.setTSIGKey(key);
  }

  /**
     * Sets the amount of time to wait for a response before giving up. This
     * applies only to the head resolver, the time for an actual query to the
     * validating resolver IS higher.
     * 
     * @param secs The number of seconds to wait.
     * @param msecs The number of milliseconds to wait.
     */
  public void setTimeout(int secs, int msecs) {
    this.headResolver.setTimeout(secs, msecs);
  }

  /**
     * Sets the amount of time to wait for a response before giving up. This
     * applies only to the head resolver, the time for an actual query to the
     * validating resolver IS higher.
     * 
     * @param secs The number of seconds to wait.
     */
  public void setTimeout(int secs) {
    this.headResolver.setTimeout(secs);
  }

  /**
     * Sends a message and validates the response with DNSSEC before returning
     * it.
     * 
     * @param query The query to send.
     * @return The validated response message.
     * @throws IOException An error occurred while sending or receiving.
     */
  public Message send(Message query) throws IOException {
    SMessage response = this.sendRequest(query);
    response.getHeader().unsetFlag(Flags.AD);
    if (query.getHeader().getFlag(Flags.CD)) {
      return response.getMessage();
    }
    Message rrsigResponse = response.getMessage();
    if (query.getQuestion().getType() == Type.RRSIG && rrsigResponse.getHeader().getRcode() == Rcode.NOERROR && rrsigResponse.getSectionRRsets(Section.ANSWER).length > 0) {
      rrsigResponse.getHeader().unsetFlag(Flags.AD);
      return rrsigResponse;
    }
    final SMessage validated = this.processValidate(query, response);
    Message m = validated.getMessage();
    String reason = validated.getBogusReason();
    if (reason != null) {
      final int maxTxtRecordStringLength = 255;
      String[] parts = new String[reason.length() / maxTxtRecordStringLength + 1];
      for (int i = 0; i < parts.length; i++) {
        int length = Math.min((i + 1) * maxTxtRecordStringLength, reason.length());
        parts[i] = reason.substring(i * maxTxtRecordStringLength, length);
      }
      m.addRecord(new TXTRecord(Name.root, VALIDATION_REASON_QCLASS, 0, Arrays.asList(parts)), Section.ADDITIONAL);
    }
    return m;
  }

  /**
     * Not implemented.
     * 
     * @param query The query to send
     * @param listener The object containing the callbacks.
     * @return An identifier, which is also a parameter in the callback
     * @throws UnsupportedOperationException Always
     */
  public Object sendAsync(Message query, ResolverListener listener) {
    throw new UnsupportedOperationException("Not implemented");
  }

  /**
     * Creates a response message with the given return code.
     * 
     * @param request The request for which the response belongs.
     * @param rcode The response code, @see Rcode
     * @return The response message for <code>request</code>.
     */
  private static SMessage errorMessage(Message request, int rcode) {
    SMessage m = new SMessage(request.getHeader().getID(), request.getQuestion());
    Header h = m.getHeader();
    h.setRcode(rcode);
    h.setFlag(Flags.QR);
    return m;
  }
}