package com.dnastack.bob.processor;
import com.dnastack.bob.persistence.entity.Beacon;
import com.dnastack.bob.persistence.entity.Query;
import com.dnastack.bob.persistence.enumerated.Reference;
import com.google.common.collect.ImmutableSet;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;
import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import static com.dnastack.bob.util.HttpUtils.createRequest;
import static com.dnastack.bob.util.HttpUtils.executeRequest;
import static com.dnastack.bob.util.ParsingUtils.parseContainsStringCaseInsensitive;
import static com.dnastack.bob.util.QueryUtils.denormalizeAllele;
import static com.dnastack.bob.util.QueryUtils.denormalizeChromosome;

/**
 * AMPLab beacon service.
 *
 * @author Miroslav Cupak (mirocupak@gmail.com)
 * @version 1.0
 */
@Processor @AmpLab public class AmpLabBeaconProcessor extends AbstractBeaconProcessor {
  private static final long serialVersionUID = 10L;

  private static final String BASE_URL = "http://beacon.eecs.berkeley.edu/beacon.php";

  private static final String CHROM_TEMPLATE = "chr%s";

  private static final Set<Reference> SUPPORTED_REFS = ImmutableSet.of(Reference.HG18, Reference.HG19, Reference.HG38);

  private List<NameValuePair> getQueryData(String ref, String chrom, Long pos, String allele) {
    List<NameValuePair> nvs = new ArrayList<>();
    nvs.add(new BasicNameValuePair("population", "1000genomes"));
    nvs.add(new BasicNameValuePair("genome", ref));
    nvs.add(new BasicNameValuePair("chr", chrom));
    nvs.add(new BasicNameValuePair("coord", pos.toString()));
    nvs.add(new BasicNameValuePair("allele", allele));
    return nvs;
  }

  @Override @Asynchronous public Future<String> getQueryResponse(Beacon beacon, Query query) {
    String res = null;
    try {
      res = executeRequest(createRequest(BASE_URL, true, getQueryData(query.getReference().toString(), denormalizeChromosome(CHROM_TEMPLATE, query.getChromosome()), query.getPosition(), denormalizeAllele(query.getAllele()))));
    } catch (UnsupportedEncodingException ex) {
    }
    return new AsyncResult<>(res);
  }

  @Override @Asynchronous public Future<Boolean> parseQueryResponse(Beacon b, String response) {
    Boolean res = parseContainsStringCaseInsensitive(response, "beacon found", "beacon cannot find");
    return new AsyncResult<>(res);
  }

  @Override public Set<Reference> getSupportedReferences() {
    return SUPPORTED_REFS;
  }
}