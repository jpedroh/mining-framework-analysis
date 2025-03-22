package com.spotify.apollo.meta;
import com.spotify.apollo.Request;
import com.spotify.apollo.dispatch.Endpoint;
import com.spotify.apollo.dispatch.EndpointInfo;
import com.spotify.apollo.request.OngoingRequest;
import com.spotify.apollo.route.Route;
import com.spotify.apollo.meta.model.Meta;
import com.spotify.apollo.meta.model.MetaGatherer;
import com.spotify.apollo.meta.model.MetaGatherer.CallsGatherer;
import com.spotify.apollo.meta.model.MetaGatherer.EndpointGatherer;
import com.spotify.apollo.meta.model.MetaInfoBuilder;
import com.spotify.apollo.meta.model.Model;
import com.typesafe.config.Config;
import java.util.List;
import java.util.Optional;
import static java.util.Collections.singletonList;

public class MetaInfoTracker {
  private final MetaGatherer gatherer;

  public MetaInfoTracker(Descriptor descriptor, String containerVersion, Config configNode) {
    final Model.MetaInfo metaInfo = new MetaInfoBuilder().buildVersion(descriptor.serviceName() + ' ' + descriptor.version()).containerVersion(containerVersion).build();
    gatherer = Meta.createGatherer(metaInfo, configNode);
  }

  public MetaGatherer getGatherer() {
    return gatherer;
  }

  public <E extends Endpoint> void gatherEndpoints(List<E> endpoints) {
    CallsGatherer callsGatherer = gatherer.getServiceCallsGatherer();
    for (E endpoint : endpoints) {
      EndpointInfo info = endpoint.info();
      String uri = info.getUri();
      String endpointDocString = formatDocString(info.getDocString());
      EndpointGatherer endpointGatherer = callsGatherer.uriMethodsEndpointGatherer(uri, singletonList(info.getRequestMethod()));
      endpointGatherer.setUri(uri);
      endpointGatherer.addMethod(info.getRequestMethod());
      endpointGatherer.setDocstring(endpointDocString);
    }
  }

  private static String formatDocString(Optional<Route.DocString> docString) {
    if (!docString.isPresent()) {
      return "Lacks DocString annotation.";
    }
    return docString.get().summary() + "\n\n" + docString.get().description();
  }

  private class IncomingGatherer implements IncomingCallsGatherer {
    @Override public void gatherIncomingCall(OngoingRequest ongoingRequest, Endpoint endpoint) {
      Request message = ongoingRequest.request();
      String fromService = message.service().orElse(null);
      String method = message.method();
      EndpointInfo info = endpoint.info();
      String endpointMethodName = info.getJavaMethodName();
      CallsGatherer callsGatherer = gatherer.getIncomingCallsGatherer(fromService);
      EndpointGatherer endpointGatherer = callsGatherer.namedEndpointGatherer(endpointMethodName);
      endpointGatherer.setUri(info.getUri());
      endpointGatherer.addMethod(method);
      for (String name : message.parameters().keySet()) {
        endpointGatherer.addQueryParameterName(name);
      }
    }
  }

  private final IncomingGatherer incomingGatherer = new IncomingGatherer();

  public IncomingCallsGatherer incomingCallsGatherer() {
    return incomingGatherer;
  }

  private class OutgoingGatherer implements OutgoingCallsGatherer {
    public void gatherOutgoingCall(String toService, Request request) {
      @SuppressWarnings(value = { "UnusedDeclaration" }) CallsGatherer callsGatherer = gatherer.getOutgoingCallsGatherer(toService);
    }
  }

  private OutgoingGatherer outgoingGatherer = new OutgoingGatherer();

  public OutgoingCallsGatherer outgoingCallsGatherer() {
    return outgoingGatherer;
  }
}