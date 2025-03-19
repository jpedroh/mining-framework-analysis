package com.dnastack.bob.processor;
import com.dnastack.bob.persistence.entity.Beacon;
import com.dnastack.bob.persistence.entity.Query;
import com.dnastack.bob.persistence.enumerated.Reference;
import com.google.common.collect.ImmutableSet;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.Set;
import java.util.concurrent.Future;
import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import static com.dnastack.bob.util.HttpUtils.createRequest;
import static com.dnastack.bob.util.HttpUtils.executeRequest;
import static com.dnastack.bob.util.ParsingUtils.parseBooleanFromJson;
import static com.dnastack.bob.util.QueryUtils.denormalizeChromosome;
import static com.dnastack.bob.util.QueryUtils.denormalizePosition;

/**
 * Cafe Variome beacon service.
 *
 * @author Miroslav Cupak (mirocupak@gmail.com)
 * @version 1.0
 */
@Processor @CafeVariome public class CafeVariomeBeaconProcessor extends AbstractBeaconProcessor {
  private static final long serialVersionUID = 12L;

  private static final String BASE_URL = "http://beacon.cafevariome.org/query";

  private static final String PARAM_TEMPLATE = "?chrom=%s&pos=%d&allele=%s";

  private static final Set<Reference> SUPPORTED_REFS = ImmutableSet.of(Reference.HG19);

  private static final String BEACON_PREFIX = "cafe-";

  private static final String RESPONSE_FIELD = "response";

  private String getQueryUrl(String chrom, Long pos, String allele) throws MalformedURLException {
    String params = String.format(PARAM_TEMPLATE, chrom, pos, allele);
    return BASE_URL + params;
  }

  private String getJsonFieldName(Beacon b) {
    String res = null;
    if (b.getId().startsWith(BEACON_PREFIX)) {
      res = b.getId().substring(BEACON_PREFIX.length()) + "_" + RESPONSE_FIELD;
    } else {
      res = b.getId() + "_" + RESPONSE_FIELD;
    }
    return res;
  }

  @Override @Asynchronous public Future<String> getQueryResponse(Beacon beacon, Query query) {
    String res = null;
    try {
      res = executeRequest(createRequest(getQueryUrl(denormalizeChromosome("chr%s", query.getChromosome()).toLowerCase(), denormalizePosition(query.getPosition()), query.getAllele()), false, null));
    } catch (MalformedURLException | UnsupportedEncodingException ex) {
    }
    return new AsyncResult<>(res);
  }

  @Override @Asynchronous public Future<Boolean> parseQueryResponse(Beacon b, String response) {
    Boolean res = parseBooleanFromJson(response, RESPONSE_FIELD, getJsonFieldName(b));
    return new AsyncResult<>(res);
  }

  @Override public Set<Reference> getSupportedReferences() {
    return SUPPORTED_REFS;
  }
}