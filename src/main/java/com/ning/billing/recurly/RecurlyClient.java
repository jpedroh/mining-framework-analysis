package com.ning.billing.recurly;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;
import javax.annotation.Nullable;
import javax.xml.bind.DatatypeConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ning.billing.recurly.model.Account;
import com.ning.billing.recurly.model.Accounts;
import com.ning.billing.recurly.model.AddOn;
import com.ning.billing.recurly.model.BillingInfo;
import com.ning.billing.recurly.model.Coupon;
import com.ning.billing.recurly.model.Invoice;
import com.ning.billing.recurly.model.Invoices;
import com.ning.billing.recurly.model.Plan;
import com.ning.billing.recurly.model.Plans;
import com.ning.billing.recurly.model.RecurlyObject;
import com.ning.billing.recurly.model.Subscription;
import com.ning.billing.recurly.model.SubscriptionUpdate;
import com.ning.billing.recurly.model.Subscriptions;
import com.ning.billing.recurly.model.Transaction;
import com.ning.billing.recurly.model.Transactions;
import com.ning.http.client.AsyncCompletionHandler;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClientConfig;
import com.ning.http.client.Response;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.ning.billing.recurly.model.Coupons;

public class RecurlyClient {
  private static final Logger log = LoggerFactory.getLogger(RecurlyClient.class);

  public static final String RECURLY_DEBUG_KEY = "recurly.debug";

  public static final String RECURLY_PAGE_SIZE_KEY = "recurly.page.size";

  private static final Integer DEFAULT_PAGE_SIZE = 20;

  private static final String PER_PAGE = "per_page=";

  public static final String FETCH_RESOURCE = "/recurly_js/result";

  private static boolean debug() {
    return Boolean.getBoolean(RECURLY_DEBUG_KEY);
  }

  public static Integer getPageSize() {
    Integer pageSize;
    try {
      pageSize = new Integer(System.getProperty(RECURLY_PAGE_SIZE_KEY));
    } catch (NumberFormatException nfex) {
      pageSize = DEFAULT_PAGE_SIZE;
    }
    return pageSize;
  }

  public static String getPageSizeGetParam() {
    return PER_PAGE + getPageSize().toString();
  }

  private final XmlMapper xmlMapper = new XmlMapper();

  private final String key;

  private final String baseUrl;

  private AsyncHttpClient client;

  public RecurlyClient(final String apiKey) {
    this(apiKey, "api.recurly.com", 443, "v2");
  }

  public RecurlyClient(final String apiKey, final String host, final int port, final String version) {
    this.key = DatatypeConverter.printBase64Binary(apiKey.getBytes());
    this.baseUrl = String.format("https://%s:%d/%s", host, port, version);
    this.xmlMapper = RecurlyObject.newXmlMapper();
  }

  public synchronized void open() {
    client = createHttpClient();
  }

  public synchronized void close() {
    if (client != null) {
      client.close();
    }
  }

  public Account createAccount(final Account account) {
    return doPOST(Account.ACCOUNT_RESOURCE, account, Account.class);
  }

  public Accounts getAccounts() {
    return doGET(Accounts.ACCOUNTS_RESOURCE, Accounts.class);
  }

  public Account getAccount(final String accountCode) {
    return doGET(Account.ACCOUNT_RESOURCE + "/" + accountCode, Account.class);
  }

  public Account updateAccount(final String accountCode, final Account account) {
    return doPUT(Account.ACCOUNT_RESOURCE + "/" + accountCode, account, Account.class);
  }

  public void closeAccount(final String accountCode) {
    doDELETE(Account.ACCOUNT_RESOURCE + "/" + accountCode);
  }

  public Subscription createSubscription(final Subscription subscription) {
    return doPOST(Subscription.SUBSCRIPTION_RESOURCE, subscription, Subscription.class);
  }

  public Subscription getSubscription(final String uuid) {
    return doGET(Subscriptions.SUBSCRIPTIONS_RESOURCE + "/" + uuid, Subscription.class);
  }

  public Subscription cancelSubscription(final Subscription subscription) {
    return doPUT(Subscription.SUBSCRIPTION_RESOURCE + "/" + subscription.getUuid() + "/cancel", subscription, Subscription.class);
  }

  public Subscription reactivateSubscription(final Subscription subscription) {
    return doPUT(Subscription.SUBSCRIPTION_RESOURCE + "/" + subscription.getUuid() + "/reactivate", subscription, Subscription.class);
  }

  public Subscription updateSubscription(final String uuid, final SubscriptionUpdate subscriptionUpdate) {
    return doPUT(Subscriptions.SUBSCRIPTIONS_RESOURCE + "/" + uuid, subscriptionUpdate, Subscription.class);
  }

  public Subscriptions getAccountSubscriptions(final String accountCode) {
    return doGET(Account.ACCOUNT_RESOURCE + "/" + accountCode + Subscriptions.SUBSCRIPTIONS_RESOURCE, Subscriptions.class);
  }

  public Subscriptions getAccountSubscriptions(final String accountCode, final String status) {
    return doGET(Account.ACCOUNT_RESOURCE + "/" + accountCode + Subscriptions.SUBSCRIPTIONS_RESOURCE + "?state=" + status, Subscriptions.class);
  }

  public BillingInfo createOrUpdateBillingInfo(final BillingInfo billingInfo) {
    final String accountCode = billingInfo.getAccount().getAccountCode();
    billingInfo.setAccount(null);
    return doPUT(Account.ACCOUNT_RESOURCE + "/" + accountCode + BillingInfo.BILLING_INFO_RESOURCE, billingInfo, BillingInfo.class);
  }

  public BillingInfo getBillingInfo(final String accountCode) {
    return doGET(Account.ACCOUNT_RESOURCE + "/" + accountCode + BillingInfo.BILLING_INFO_RESOURCE, BillingInfo.class);
  }

  public void clearBillingInfo(final String accountCode) {
    doDELETE(Account.ACCOUNT_RESOURCE + "/" + accountCode + BillingInfo.BILLING_INFO_RESOURCE);
  }

  public Transactions getAccountTransactions(final String accountCode) {
    return doGET(Accounts.ACCOUNTS_RESOURCE + "/" + accountCode + Transactions.TRANSACTIONS_RESOURCE, Transactions.class);
  }

  public Transaction createTransaction(final Transaction trans) {
    return doPOST(Transactions.TRANSACTIONS_RESOURCE, trans, Transaction.class);
  }

  public Invoices getAccountInvoices(final String accountCode) {
    return doGET(Accounts.ACCOUNTS_RESOURCE + "/" + accountCode + Invoices.INVOICES_RESOURCE, Invoices.class);
  }

  public Plan createPlan(final Plan plan) {
    return doPOST(Plan.PLANS_RESOURCE, plan, Plan.class);
  }

  public Plan getPlan(final String planCode) {
    return doGET(Plan.PLANS_RESOURCE + "/" + planCode, Plan.class);
  }

  public Plans getPlans() {
    return doGET(Plans.PLANS_RESOURCE, Plans.class);
  }

  public void deletePlan(final String planCode) {
    doDELETE(Plan.PLANS_RESOURCE + "/" + planCode);
  }

  public AddOn createPlanAddOn(final String planCode, final AddOn addOn) {
    return doPOST(Plan.PLANS_RESOURCE + "/" + planCode + AddOn.ADDONS_RESOURCE, addOn, AddOn.class);
  }

  public AddOn getAddOn(final String planCode, final String addOnCode) {
    return doGET(Plan.PLANS_RESOURCE + "/" + planCode + AddOn.ADDONS_RESOURCE + "/" + addOnCode, AddOn.class);
  }

  public AddOn getAddOns(final String planCode) {
    return doGET(Plan.PLANS_RESOURCE + "/" + planCode + AddOn.ADDONS_RESOURCE, AddOn.class);
  }

  public void deleteAddOn(final String planCode, final String addOnCode) {
    doDELETE(Plan.PLANS_RESOURCE + "/" + planCode + AddOn.ADDONS_RESOURCE + "/" + addOnCode);
  }

  public Coupon createCoupon(final Coupon coupon) {
    return doPOST(Coupon.COUPON_RESOURCE, coupon, Coupon.class);
  }

  public Coupon getCoupon(final String couponCode) {
    return doGET(Coupon.COUPON_RESOURCE + "/" + couponCode, Coupon.class);
  }

  public Subscription fetchSubscription(final String recurlyToken) {
    return fetch(recurlyToken, Subscription.class);
  }

  public BillingInfo fetchBillingInfo(final String recurlyToken) {
    return fetch(recurlyToken, BillingInfo.class);
  }

  public Invoice fetchInvoice(final String recurlyToken) {
    return fetch(recurlyToken, Invoice.class);
  }

  private <T extends java.lang.Object> T fetch(final String recurlyToken, final Class<T> clazz) {
    return doGET(FETCH_RESOURCE + "/" + recurlyToken, clazz);
  }

  private <T extends java.lang.Object> T doGET(final String resource, final Class<T> clazz) {
    final StringBuffer url = new StringBuffer(baseUrl);
    url.append(resource);
    if (resource != null && !resource.contains("?")) {
      url.append("?");
    } else {
      url.append("&");
      url.append("&");
    }
    url.append(getPageSizeGetParam());
    if (debug()) {
      log.info("Msg to Recurly API [GET] :: URL : {}", url);
    }
    return callRecurlySafe(client.prepareGet(url.toString()), clazz);
  }

  private <T extends java.lang.Object> T doPOST(final String resource, final RecurlyObject payload, final Class<T> clazz) {
    final String xmlPayload;
    try {
      xmlPayload = xmlMapper.writeValueAsString(payload);
      if (debug()) {
        log.info("Msg to Recurly API [POST]:: URL : {}", baseUrl + resource);
        log.info("Payload for [POST]:: {}", xmlPayload);
      }
    } catch (IOException e) {
      log.warn("Unable to serialize {} object as XML: {}", clazz.getName(), payload.toString());
      return null;
    }
    return callRecurlySafe(client.preparePost(baseUrl + resource).setBody(xmlPayload), clazz);
  }

  private <T extends java.lang.Object> T doPUT(final String resource, final RecurlyObject payload, final Class<T> clazz) {
    final String xmlPayload;
    try {
      xmlPayload = xmlMapper.writeValueAsString(payload);
      if (debug()) {
        log.info("Msg to Recurly API [PUT]:: URL : {}", baseUrl + resource);
        log.info("Payload for [PUT]:: {}", xmlPayload);
      }
    } catch (IOException e) {
      log.warn("Unable to serialize {} object as XML: {}", clazz.getName(), payload.toString());
      return null;
    }
    return callRecurlySafe(client.preparePut(baseUrl + resource).setBody(xmlPayload), clazz);
  }

  private void doDELETE(final String resource) {
    callRecurlySafe(client.prepareDelete(baseUrl + resource), null);
  }

  private <T extends java.lang.Object> T callRecurlySafe(final AsyncHttpClient.BoundRequestBuilder builder, @Nullable final Class<T> clazz) {
    try {
      return callRecurly(builder, clazz);
    } catch (IOException e) {
      log.warn("Error while calling Recurly", e);
      return null;
    } catch (ExecutionException e) {
      log.error("Execution error", e);
      return null;
    } catch (InterruptedException e) {
      log.error("Interrupted while calling Recurly", e);
      return null;
    }
  }

  private <T extends java.lang.Object> T callRecurly(final AsyncHttpClient.BoundRequestBuilder builder, @Nullable final Class<T> clazz) throws IOException, ExecutionException, InterruptedException {
    return builder.addHeader("Authorization", "Basic " + key).addHeader("Accept", "application/xml").addHeader("Content-Type", "application/xml; charset=utf-8").execute(new AsyncCompletionHandler<T>() {
      @Override public T onCompleted(final Response response) throws Exception {
        if (response.getStatusCode() >= 300) {
          log.warn("Recurly error whilst calling: {}", response.getUri());
          log.warn("Recurly error: {}", response.getResponseBody());
          return null;
        }
        if (clazz == null) {
          return null;
        }
        final InputStream in = response.getResponseBodyAsStream();
        try {
          final String payload = convertStreamToString(in);
          if (debug()) {
            log.info("Msg from Recurly API :: {}", payload);
          }
          final T obj = xmlMapper.readValue(payload, clazz);
          return obj;
        }  finally {
          closeStream(in);
        }
      }
    }).get();
  }

  private String convertStreamToString(final java.io.InputStream is) {
    try {
      return new java.util.Scanner(is).useDelimiter("\\A").next();
    } catch (java.util.NoSuchElementException e) {
      return "";
    }
  }

  private void closeStream(final InputStream in) {
    if (in != null) {
      try {
        in.close();
      } catch (IOException e) {
        log.warn("Failed to close http-client - provided InputStream: {}", e.getLocalizedMessage());
      }
    }
  }

  private AsyncHttpClient createHttpClient() {
    final AsyncHttpClientConfig.Builder builder = new AsyncHttpClientConfig.Builder();
    builder.setMaximumConnectionsPerHost(-1);
    return new AsyncHttpClient(builder.build());
  }

  public Coupons getCoupons() {
    return doGET(Coupons.COUPONS_RESOURCE, Coupons.class);
  }
}