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
import static com.dnastack.bob.util.ParsingUtils.parseRef;
import static com.dnastack.bob.util.ParsingUtils.parseYesNoCaseInsensitive;
import static com.dnastack.bob.util.QueryUtils.denormalizePosition;
import static com.dnastack.bob.util.QueryUtils.denormalizeReference;

/**
 * WTSI beacon service.
 *
 * @author Miroslav Cupak (mirocupak@gmail.com)
 * @version 1.0
 */
@Processor @Wtsi public class WtsiBeaconProcessor extends AbstractBeaconProcessor {
  private static final long serialVersionUID = 14L;

  private static final String BASE_URL = "http://www.sanger.ac.uk/sanger/GA4GH_Beacon";

  private static final String PARAM_TEMPLATE_ASSEMBLY = "?src=all&ass=%s&chr=%s&pos=%d&all=%s";

  private static final String PARAM_TEMPLATE = "?src=all&chr=%s&pos=%d&all=%s";

  private static final Set<Reference> SUPPORTED_REFS = ImmutableSet.of(Reference.HG19);

  private String getQueryUrl(String ref, String chrom, Long pos, String allele) throws MalformedURLException {
    String params;
    if (ref == null) {
      params = String.format(PARAM_TEMPLATE, chrom, pos, allele);
    } else {
      params = String.format(PARAM_TEMPLATE_ASSEMBLY, ref, chrom, pos, allele);
    }
    return BASE_URL + params;
  }

  @Override @Asynchronous public Future<String> getQueryResponse(Beacon beacon, Query query) {
    String res = null;
    try {
      res = executeRequest(createRequest(getQueryUrl(denormalizeReference(query.getReference()), query.getChromosome().toString(), denormalizePosition(query.getPosition()), query.getAllele()), false, null));
    } catch (MalformedURLException | UnsupportedEncodingException ex) {
    }
    return new AsyncResult<>(res);
  }

  @Override @Asynchronous public Future<Boolean> parseQueryResponse(Beacon b, String response) {
    Boolean res = parseYesNoCaseInsensitive(response);
    if (res == null) {
      Boolean isRef = parseRef(response);
      if (isRef != null && isRef) {
        res = false;
      }
    }
    return new AsyncResult<>(res);
  }

  @Override public Set<Reference> getSupportedReferences() {
    return SUPPORTED_REFS;
  }
}