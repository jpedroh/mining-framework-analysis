package de.uniluebeck.itm.spitfire.nCoap.message;
import de.uniluebeck.itm.spitfire.nCoap.message.header.Code;
import de.uniluebeck.itm.spitfire.nCoap.message.header.Header;
import de.uniluebeck.itm.spitfire.nCoap.message.header.InvalidHeaderException;
import de.uniluebeck.itm.spitfire.nCoap.message.header.MsgType;
import de.uniluebeck.itm.spitfire.nCoap.message.options.*;
import org.jboss.netty.buffer.ChannelBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;

/**
 * @author Oliver Kleine
 */
public class CoapResponse extends CoapMessage {
  private static Logger log = LoggerFactory.getLogger(CoapMessage.class.getName());

  public CoapResponse(Code code) {
    super(code);
  }

  public CoapResponse(MsgType msgType, Code code) {
    super(msgType, code);
  }

  public CoapResponse(MsgType msgType, Code code, int messageID) throws ToManyOptionsException, InvalidHeaderException {
    this(msgType, code);
    setMessageID(messageID);
  }

  public CoapResponse(CoapResponse coapResponse) throws InvalidHeaderException {
    super(coapResponse);
  }

  public CoapResponse(Header header, OptionList optionList, ChannelBuffer payload) {
    super(header, optionList, payload);
  }

  /**
     * Adds the max age option for the messages content. This is to indicate the maximum time a response may be
     * cached until it is considered not fresh. This causes an eventually already contained
     * max age option to be removed from the list even in case of an exception.
     *
     * @param maxAge The maximum allowed time to cache the response in seconds
     * @throws de.uniluebeck.itm.spitfire.nCoap.message.options.InvalidOptionException if the given max age is out of a valid range
     * @throws de.uniluebeck.itm.spitfire.nCoap.message.options.ToManyOptionsException if adding this option would exceed the maximum number of allowed options per
     * message
     * @return <code>true</code> if content type was succesfully set, <code>false</code> if max age option is not
     * meaningful with the message code and thus silently ignored or if max age is equal to
     * OptionRegistry.MAX_AGE_DEFAULT
     */
  public boolean setMaxAge(int maxAge) throws InvalidOptionException, ToManyOptionsException {
    optionList.removeAllOptions(OptionRegistry.OptionName.MAX_AGE);
    if (maxAge == OptionRegistry.MAX_AGE_DEFAULT) {
      return false;
    }
    try {
      Option option = Option.createUintOption(OptionRegistry.OptionName.MAX_AGE, maxAge);
      optionList.addOption(header.getCode(), OptionRegistry.OptionName.MAX_AGE, option);
      return true;
    } catch (InvalidOptionException e) {
      log.debug("Elective option (" + OptionRegistry.OptionName.MAX_AGE + ") could not be added.", e);
      optionList.removeAllOptions(OptionRegistry.OptionName.MAX_AGE);
      return false;
    } catch (ToManyOptionsException e) {
      log.debug("Elective option (" + OptionRegistry.OptionName.MAX_AGE + ") could not be added.", e);
      optionList.removeAllOptions(OptionRegistry.OptionName.MAX_AGE);
      return false;
    }
  }

  /**
     * Adds all necessary location URI related options to the list. This causes eventually already contained
     * location URI related options to be removed from the list even in case of an exception.
     *
     * @param locationURI The location URI of the newly created resource. The parts scheme, host, and port are
     * ignored anyway and thus may not be included in the URI object
     * @throws InvalidOptionException if at least one of the options to be added is not valid
     * @throws ToManyOptionsException if adding all location URI related options would exceed the maximum number of
     * options per message.
     */
  public void setLocationURI(URI locationURI) throws InvalidOptionException, ToManyOptionsException {
    optionList.removeLocationURI();
    try {
      Collection<Option> locationUriOptions = Option.createLocationUriOptions(locationURI);
      for (Option option : locationUriOptions) {
        OptionRegistry.OptionName optionName = OptionRegistry.getOptionName(option.getOptionNumber());
        optionList.addOption(header.getCode(), optionName, option);
      }
    } catch (ToManyOptionsException e) {
      optionList.removeLocationURI();
      log.debug("Critical option for location URI could not be added.", e);
      throw e;
    } catch (InvalidOptionException e) {
      optionList.removeLocationURI();
      log.debug("Critical option for location URI could not be added.", e);
      throw e;
    }
  }

  /**
     * Returns the URI reconstructed from the location URI related options contained in the message
     * @return the URI reconstructed from the location URI related options contained in the message or null if there
     * are no location URI related options
     * @throws java.net.URISyntaxException if the URI to be reconstructed from options is invalid
     */
  public URI getLocationURI() throws URISyntaxException {
    Collection<Option> options = optionList.getOption(OptionRegistry.OptionName.LOCATION_PATH);
    if (options.isEmpty()) {
      return null;
    }
    String result = "";
    for (Option option : options) {
      result += ("/" + ((StringOption) option).getDecodedValue());
    }
    options = optionList.getOption((OptionRegistry.OptionName.LOCATION_QUERY));
    if (!options.isEmpty()) {
      result += "?";
    }
    for (Option option : options) {
      result += ("/" + ((StringOption) option).getDecodedValue());
    }
    if (result.equals("")) {
      return null;
    }
    return new URI(result);
  }

  /**
     * Set the observe option. This causes eventually already contained observe options to be removed from
     * the list even in case of an exception.
     *
     * @param Sets the sequence number for the observe option
     *
     * @throws ToManyOptionsException if adding an observe options would exceed the maximum number of
     * options per message.
     */
  public void setObserveOptionResponse(long sequenceNumber) throws ToManyOptionsException {
    optionList.removeAllOptions(OptionRegistry.OptionName.OBSERVE_RESPONSE);
    try {
      Option option = Option.createUintOption(OptionRegistry.OptionName.OBSERVE_RESPONSE, sequenceNumber);
      optionList.addOption(header.getCode(), OptionRegistry.OptionName.OBSERVE_RESPONSE, option);
    } catch (InvalidOptionException e) {
      optionList.removeAllOptions(OptionRegistry.OptionName.OBSERVE_RESPONSE);
      log.error("This should never happen!", e);
    } catch (ToManyOptionsException e) {
      optionList.removeAllOptions(OptionRegistry.OptionName.OBSERVE_RESPONSE);
      log.debug("Critical option (" + OptionRegistry.OptionName.OBSERVE_RESPONSE + ") could not be added.", e);
      throw e;
    }
  }
}