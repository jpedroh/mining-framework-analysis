package com.mercadopago.client.cardtoken;

import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.MercadoPagoClient;
import com.mercadopago.core.MPRequestOptions;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.net.HttpMethod;
import com.mercadopago.net.MPHttpClient;
import com.mercadopago.net.MPResponse;
import com.mercadopago.resources.CardToken;
import com.mercadopago.serialization.Serializer;
import java.util.logging.Logger;
import java.util.logging.StreamHandler;
import static com.mercadopago.MercadoPagoConfig.getStreamHandler;


/** Client for retrieving the token for a card. */
public class CardTokenClient extends MercadoPagoClient {
  /**
   * Get card token.
   *
   * @param id card id
   * @param requestOptions metadata to customize the request
   * @return card token information
   */
<<<<<<< LEFT
  public CardToken get(String id, MPRequestOptions requestOptions) throws MPException {
    LOGGER.info("Sending get card token request");

=======
  public CardToken get(String id, MPRequestOptions requestOptions)
      throws MPException, MPApiException {
>>>>>>> RIGHT
    MPResponse response =
        send(String.format("/v1/card_tokens/%s", id), HttpMethod.GET, null, null, requestOptions);
    CardToken cardToken = Serializer.deserializeFromJson(CardToken.class, response.getContent());
    cardToken.setResponse(response);
    return cardToken;
  }

  /**
   * Create token associated with a card.
   *
   * @param request attributes used to perform the request
   * @param requestOptions metadata to customize the request
   * @return card token information
   * @throws MPException an error if the request fails
   */
  public CardToken create(CardTokenRequest request, MPRequestOptions requestOptions)
<<<<<<< LEFT
      throws MPException {
    LOGGER.info("Sending create card token request");

=======
      throws MPException, MPApiException {
>>>>>>> RIGHT
    MPResponse response =
        send(
            "/v1/card_tokens",
            HttpMethod.POST,
            Serializer.serializeToJson(request),
            null,
            requestOptions);
    CardToken cardToken = Serializer.deserializeFromJson(CardToken.class, response.getContent());
    cardToken.setResponse(response);
    return cardToken;
  }

  private static final Logger LOGGER = Logger.getLogger(CardTokenClient.class.getName());

  /**
   * Default constructor. Uses http client provided by MercadoPagoConfig.
   */
  public CardTokenClient() {
    this(MercadoPagoConfig.getHttpClient());
  }

  /**
   * CardTokenClient constructor.
   *
   * @param httpClient http client
   */
  public CardTokenClient(MPHttpClient httpClient) {
    super(httpClient);
    StreamHandler streamHandler = getStreamHandler();
    streamHandler.setLevel(MercadoPagoConfig.getLoggingLevel());
    LOGGER.addHandler(streamHandler);
    LOGGER.setLevel(MercadoPagoConfig.getLoggingLevel());
  }

  /**
   * Get card token.
   *
   * @param id card id
   * @return card token information
   */
  public CardToken get(String id) throws MPException, MPApiException {
    return this.get(id, null);
  }

  /**
   * Create token associated with a card.
   *
   * @param request attributes used to perform the request
   * @return card token information
   * @throws MPException an error if the request fails
   */
  public CardToken create(CardTokenRequest request) throws MPException, MPApiException {
    return this.create(request, null);
  }
}