package de.uniluebeck.itm.spitfire.nCoap.application;
import de.uniluebeck.itm.spitfire.nCoap.message.CoapRequest;
import de.uniluebeck.itm.spitfire.nCoap.message.CoapResponse;
import de.uniluebeck.itm.spitfire.nCoap.message.MessageDoesNotAllowPayloadException;
import de.uniluebeck.itm.spitfire.nCoap.message.header.Code;
import de.uniluebeck.itm.spitfire.nCoap.message.options.InvalidOptionException;
import de.uniluebeck.itm.spitfire.nCoap.message.options.OptionRegistry;
import de.uniluebeck.itm.spitfire.nCoap.message.options.ToManyOptionsException;
import org.apache.log4j.Logger;
import java.nio.charset.Charset;

/**
* @author Oliver Kleine
*/
public class SimpleCoapServerApplication extends CoapServerApplication {
  private static Logger log = Logger.getLogger(SimpleCoapServerApplication.class.getName());

  /**
     * This method makes the server to wait for 5 seconds before it returns a static response
     * (Code 205, Content: "This is a response!")
     *
     * @param coapRequest The incoming {@link CoapRequest} object
     * @return
     */
  @Override public CoapResponse receiveCoapRequest(CoapRequest coapRequest) {
    log.debug("[SimpleCoapServerApplication] Received request for " + coapRequest.getTargetUri());
    try {

<<<<<<< /usr/src/app/output/okleine/ncoap/805f73110b467c848fed1ff26471fff425614f01/src/main/java/de/uniluebeck/itm/spitfire/nCoap/application/SimpleCoapServerApplication.java/left.java
      Thread.sleep(5000)
=======
      coapResponse.setContentType(OptionRegistry.MediaType.APP_LINK_FORMAT)
>>>>>>> /usr/src/app/output/okleine/ncoap/805f73110b467c848fed1ff26471fff425614f01/src/main/java/de/uniluebeck/itm/spitfire/nCoap/application/SimpleCoapServerApplication.java/right.java
      ;
    } catch (InvalidOptionException e) {
      log.fatal("[" + this.getClass().getName() + "] " + e.getClass().getName(), e);
    } catch (ToManyOptionsException e) {
      log.fatal("[" + this.getClass().getName() + "] " + e.getClass().getName(), e);
    }
    try {
      coapResponse.setPayload((new String("</simple>").getBytes(Charset.forName("UTF-8"))));
    } catch (MessageDoesNotAllowPayloadException e) {
      log.fatal("[SimpleCoapServerApplication] Error while setting payload for response.");
    }
    return coapResponse;
  }

  public static void main(String[] args) {
    SimpleCoapServerApplication serverApplication = new SimpleCoapServerApplication();
  }
}