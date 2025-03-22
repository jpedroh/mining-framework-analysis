package org.openapitools.api;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;
import org.apache.camel.model.rest.RestBindingMode;

@Component public class RestConfiguration extends RouteBuilder {
  @Override public void configure() throws Exception {
    restConfiguration().component("servlet").bindingMode(RestBindingMode.auto).dataFormatProperty("json.out.disableFeatures", "WRITE_DATES_AS_TIMESTAMPS").clientRequestValidation(true);
  }
}