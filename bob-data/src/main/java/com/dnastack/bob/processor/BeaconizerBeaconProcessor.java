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
import org.apache.http.client.methods.HttpRequestBase;
import static com.dnastack.bob.util.HttpUtils.createRequest;
import static com.dnastack.bob.util.HttpUtils.executeRequest;
import static com.dnastack.bob.util.ParsingUtils.parseBooleanFromJson;

/**
 * Beaconizer beacon service.
 *
 * @author Miroslav Cupak (mirocupak@gmail.com)
 * @version 1.0
 */
public abstract class BeaconizerBeaconProcessor extends AbstractBeaconProcessor {
  private static final long serialVersionUID = 112L;

  private static final Set<Reference> SUPPORTED_REFS = ImmutableSet.of(Reference.HG19);

  protected abstract String getParamTemplate();

  protected abstract String getBaseUrl();

  private String getQueryUrl(String beacon, String chrom, Long pos, String allele) throws MalformedURLException {
    String params = String.format(getParamTemplate(), beacon, chrom, pos, allele);
    return getBaseUrl() + params;
  }

  @Override @Asynchronous public Future<String> getQueryResponse(Beacon beacon, Query query) {
    String res = null;
    try {
      HttpRequestBase request = createRequest(getQueryUrl(beacon.getId(), query.getChromosome().toString(), query.getPosition(), query.getAllele()), false, null);
      request.setHeader("Accept", "application/json");
      res = executeRequest(request);
    } catch (MalformedURLException | UnsupportedEncodingException ex) {
    }
    return new AsyncResult<>(res);
  }

  @Override @Asynchronous public Future<Boolean> parseQueryResponse(Beacon b, String response) {
    Boolean res = parseBooleanFromJson(response, "exists");
    return new AsyncResult<>(res);
  }

  @Override public Set<Reference> getSupportedReferences() {
    return SUPPORTED_REFS;
  }
}