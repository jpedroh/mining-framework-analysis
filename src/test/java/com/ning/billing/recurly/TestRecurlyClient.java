package com.ning.billing.recurly;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Minutes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.ning.billing.recurly.model.Account;
import com.ning.billing.recurly.model.Accounts;
import com.ning.billing.recurly.model.AddOn;
import com.ning.billing.recurly.model.BillingInfo;
import com.ning.billing.recurly.model.Coupon;
import com.ning.billing.recurly.model.Invoices;
import com.ning.billing.recurly.model.Plan;
import com.ning.billing.recurly.model.RefundOption;
import com.ning.billing.recurly.model.Subscription;
import com.ning.billing.recurly.model.Subscriptions;
import com.ning.billing.recurly.model.Transaction;
import com.ning.billing.recurly.model.Transactions;
import static com.ning.billing.recurly.TestUtils.randomString;

public class TestRecurlyClient {
  public static final String RECURLY_PAGE_SIZE = "recurly.page.size";

  public static final String KILLBILL_PAYMENT_RECURLY_API_KEY = "killbill.payment.recurly.apiKey";

  public static final String KILLBILL_PAYMENT_RECURLY_DEFAULT_CURRENCY_KEY = "killbill.payment.recurly.currency";

  private static final Logger log = LoggerFactory.getLogger(TestRecurlyClient.class);

  private static final String CURRENCY = System.getProperty(KILLBILL_PAYMENT_RECURLY_DEFAULT_CURRENCY_KEY, "USD");

  private RecurlyClient recurlyClient;

  @BeforeMethod(groups = "integration") public void setUp() throws Exception {
    final String apiKey = System.getProperty(KILLBILL_PAYMENT_RECURLY_API_KEY);
    if (apiKey == null) {
      Assert.fail("You need to set your Recurly api key to run integration tests:" + " -Dkillbill.payment.recurly.apiKey=...");
    }
    recurlyClient = new RecurlyClient(apiKey);
    recurlyClient.open();
  }

  @AfterMethod(groups = "integration") public void tearDown() throws Exception {
    recurlyClient.close();
  }

  @Test(groups = "integration") public void testGetPageSize() throws Exception {
    System.setProperty(RECURLY_PAGE_SIZE, "");
    Assert.assertEquals(new Integer("20"), RecurlyClient.getPageSize());
    System.setProperty(RECURLY_PAGE_SIZE, "350");
    Assert.assertEquals(new Integer("350"), RecurlyClient.getPageSize());
  }

  @Test(groups = "integration") public void testGetPageSizeGetParam() throws Exception {
    System.setProperty(RECURLY_PAGE_SIZE, "");
    Assert.assertEquals("per_page=20", RecurlyClient.getPageSizeGetParam());
    System.setProperty(RECURLY_PAGE_SIZE, "350");
    Assert.assertEquals("per_page=350", RecurlyClient.getPageSizeGetParam());
  }

  @Test(groups = "integration") public void testCreateAccount() throws Exception {
    final Account accountData = TestUtils.createRandomAccount();
    final BillingInfo billingInfoData = TestUtils.createRandomBillingInfo();
    try {
      final DateTime creationDateTime = new DateTime(DateTimeZone.UTC);
      final Account account = recurlyClient.createAccount(accountData);
      Assert.assertNotNull(account);
      Assert.assertEquals(accountData.getAccountCode(), account.getAccountCode());
      Assert.assertEquals(accountData.getEmail(), account.getEmail());
      Assert.assertEquals(accountData.getFirstName(), account.getFirstName());
      Assert.assertEquals(accountData.getLastName(), account.getLastName());
      Assert.assertEquals(accountData.getUsername(), account.getUsername());
      Assert.assertEquals(accountData.getAcceptLanguage(), account.getAcceptLanguage());
      Assert.assertEquals(accountData.getCompanyName(), account.getCompanyName());
      Assert.assertEquals(Minutes.minutesBetween(account.getCreatedAt(), creationDateTime).getMinutes(), 0);
      log.info("Created account: {}", account.getAccountCode());
      final Accounts retrievedAccounts = recurlyClient.getAccounts();
      Assert.assertTrue(retrievedAccounts.size() > 0);
      final Account retrievedAccount = recurlyClient.getAccount(account.getAccountCode());
      Assert.assertEquals(retrievedAccount, account);
      billingInfoData.setAccount(account);
      final BillingInfo billingInfo = recurlyClient.createOrUpdateBillingInfo(billingInfoData);
      Assert.assertNotNull(billingInfo);
      Assert.assertEquals(billingInfoData.getFirstName(), billingInfo.getFirstName());
      Assert.assertEquals(billingInfoData.getLastName(), billingInfo.getLastName());
      Assert.assertEquals(billingInfoData.getMonth(), billingInfo.getMonth());
      Assert.assertEquals(billingInfoData.getYear(), billingInfo.getYear());
      Assert.assertEquals(billingInfo.getCardType(), "Visa");
      log.info("Added billing info: {}", billingInfo.getCardType());
      final BillingInfo retrievedBillingInfo = recurlyClient.getBillingInfo(account.getAccountCode());
      Assert.assertEquals(retrievedBillingInfo, billingInfo);
    }  finally {
      recurlyClient.clearBillingInfo(accountData.getAccountCode());
      recurlyClient.closeAccount(accountData.getAccountCode());
    }
  }

  @Test(groups = "integration") public void testCreatePlan() throws Exception {
    final Plan planData = TestUtils.createRandomPlan();
    try {
      final DateTime creationDateTime = new DateTime(DateTimeZone.UTC);
      final Plan plan = recurlyClient.createPlan(planData);
      final Plan retPlan = recurlyClient.getPlan(plan.getPlanCode());
      Assert.assertNotNull(plan);
      Assert.assertEquals(retPlan, plan);
      Assert.assertEquals(Minutes.minutesBetween(plan.getCreatedAt(), creationDateTime).getMinutes(), 0);
      Assert.assertTrue(recurlyClient.getPlans().size() > 0);
    }  finally {
      recurlyClient.deletePlan(planData.getPlanCode());
      final Plan retrievedPlan2 = recurlyClient.getPlan(planData.getPlanCode());
      if (null != retrievedPlan2) {
        Assert.fail("Failed to delete the Plan");
      }
    }
  }

  @Test(groups = "integration") public void testCreateSubscriptions() throws Exception {
    final Account accountData = TestUtils.createRandomAccount();
    final BillingInfo billingInfoData = TestUtils.createRandomBillingInfo();
    final Plan planData = TestUtils.createRandomPlan();
    try {
      final Account account = recurlyClient.createAccount(accountData);
      billingInfoData.setAccount(account);
      final BillingInfo billingInfo = recurlyClient.createOrUpdateBillingInfo(billingInfoData);
      Assert.assertNotNull(billingInfo);
      final BillingInfo retrievedBillingInfo = recurlyClient.getBillingInfo(account.getAccountCode());
      Assert.assertNotNull(retrievedBillingInfo);
      final Plan plan = recurlyClient.createPlan(planData);
      Subscription subscriptionData = new Subscription();
      subscriptionData.setPlanCode(plan.getPlanCode());
      subscriptionData.setAccount(accountData);
      subscriptionData.setCurrency(CURRENCY);
      subscriptionData.setUnitAmountInCents(1242);
      final DateTime creationDateTime = new DateTime(DateTimeZone.UTC);
      final Subscription subscription = recurlyClient.createSubscription(subscriptionData);
      Assert.assertNotNull(subscription);
      Assert.assertEquals(subscription.getCurrency(), subscriptionData.getCurrency());
      if (null == subscriptionData.getQuantity()) {
        Assert.assertEquals(subscription.getQuantity(), new Integer(1));
      } else {
        Assert.assertEquals(subscription.getQuantity(), subscriptionData.getQuantity());
      }
      Assert.assertEquals(Minutes.minutesBetween(subscription.getActivatedAt(), creationDateTime).getMinutes(), 0);
      log.info("Created subscription: {}", subscription.getUuid());
      Subscription sub1 = recurlyClient.getSubscription(subscription.getUuid());
      Assert.assertNotNull(sub1);
      Assert.assertEquals(sub1, subscription);
      Subscriptions subs = recurlyClient.getAccountSubscriptions(accountData.getAccountCode());
      boolean found = false;
      for (Subscription s : subs) {
        if (s.getUuid().equals(subscription.getUuid())) {
          found = true;
          break;
        }
      }
      if (!found) {
        Assert.fail("Could not locate the subscription in the subscriptions associated with the account");
      }
      recurlyClient.terminateSubscription(subscription.getUuid(), RefundOption.none);
      Subscription subAfterCancel = recurlyClient.getSubscription(subscription.getUuid());
      Assert.assertEquals(sub1.getUuid(), subAfterCancel.getUuid(), "It seems we did not change the correct subscription");
      Assert.assertNotNull(subAfterCancel.getCanceledAt(), "Subscription should have been canceled");
      Assert.assertNotEquals(sub1.getState(), subAfterCancel.getState(), "State should change from Active to Expired");
    }  finally {
      recurlyClient.clearBillingInfo(accountData.getAccountCode());
      recurlyClient.closeAccount(accountData.getAccountCode());
      recurlyClient.deletePlan(planData.getPlanCode());
    }
  }

  @Test(groups = "integration") public void testCreateAndQueryTransactions() throws Exception {
    final Account accountData = TestUtils.createRandomAccount();
    final BillingInfo billingInfoData = TestUtils.createRandomBillingInfo();
    final Plan planData = TestUtils.createRandomPlan();
    try {
      final Account account = recurlyClient.createAccount(accountData);
      billingInfoData.setAccount(account);
      final BillingInfo billingInfo = recurlyClient.createOrUpdateBillingInfo(billingInfoData);
      Assert.assertNotNull(billingInfo);
      final BillingInfo retrievedBillingInfo = recurlyClient.getBillingInfo(account.getAccountCode());
      Assert.assertNotNull(retrievedBillingInfo);
      final Plan plan = recurlyClient.createPlan(planData);
      Subscription subscriptionData = new Subscription();
      subscriptionData.setPlanCode(plan.getPlanCode());
      subscriptionData.setAccount(accountData);
      subscriptionData.setUnitAmountInCents(150);
      subscriptionData.setCurrency(CURRENCY);
      recurlyClient.createSubscription(subscriptionData);
      Transaction t = new Transaction();
      accountData.setBillingInfo(billingInfoData);
      t.setAccount(accountData);
      t.setAmountInCents(10);
      t.setCurrency(CURRENCY);
      Transaction createdT = recurlyClient.createTransaction(t);
      Assert.assertNotNull(createdT);
      Assert.assertEquals(createdT.getAmountInCents(), t.getAmountInCents());
      Assert.assertEquals(createdT.getCurrency(), t.getCurrency());
      log.info("Created transaction: {}", createdT.getUuid());
      Transactions trans = recurlyClient.getAccountTransactions(account.getAccountCode());
      boolean found = false;
      for (Transaction _t : trans) {
        if (_t.getUuid().equals(createdT.getUuid())) {
          found = true;
          break;
        }
      }
      if (!found) {
        Assert.fail("Failed to locate the newly created transaction");
      }
      Invoices invoices = recurlyClient.getAccountInvoices(account.getAccountCode());
      Assert.assertEquals(invoices.size(), 2, "Number of Invoices incorrect");
      Assert.assertEquals(invoices.get(0).getTotalInCents(), t.getAmountInCents(), "Amount in cents is not the same");
      Assert.assertEquals(invoices.get(1).getTotalInCents(), subscriptionData.getUnitAmountInCents(), "Amount in cents is not the same");
    }  finally {
      recurlyClient.clearBillingInfo(accountData.getAccountCode());
      recurlyClient.closeAccount(accountData.getAccountCode());
      recurlyClient.deletePlan(planData.getPlanCode());
    }
  }

  @Test(groups = "integration") public void testAddons() throws Exception {
    Plan planData = TestUtils.createRandomPlan();
    AddOn addOn = TestUtils.createRandomAddOn();
    try {
      Plan plan = recurlyClient.createPlan(planData);
      AddOn addOnRecurly = recurlyClient.createPlanAddOn(plan.getPlanCode(), addOn);
      Assert.assertNotNull(addOnRecurly);
      Assert.assertEquals(addOnRecurly.getAddOnCode(), addOn.getAddOnCode());
      Assert.assertEquals(addOnRecurly.getName(), addOn.getName());
      Assert.assertEquals(addOnRecurly.getUnitAmountInCents(), addOn.getUnitAmountInCents());
      addOnRecurly = recurlyClient.getAddOn(plan.getPlanCode(), addOn.getAddOnCode());
      Assert.assertEquals(addOnRecurly.getAddOnCode(), addOn.getAddOnCode());
      Assert.assertEquals(addOnRecurly.getName(), addOn.getName());
      Assert.assertEquals(addOnRecurly.getUnitAmountInCents(), addOn.getUnitAmountInCents());
    }  finally {
      recurlyClient.deleteAddOn(planData.getPlanCode(), addOn.getAddOnCode());
      recurlyClient.deletePlan(planData.getPlanCode());
    }
  }

  @Test(groups = "integration") public void testCreateCoupon() throws Exception {
    Coupon c = new Coupon();
    c.setName(randomString());
    c.setCouponCode(randomString());
    c.setDiscountType("percent");
    c.setDiscountPercent("10");
    final Coupon coupon = recurlyClient.createCoupon(c);
    Assert.assertNotNull(coupon);
    Assert.assertEquals(coupon.getName(), c.getName());
    Assert.assertEquals(coupon.getCouponCode(), c.getCouponCode());
    Assert.assertEquals(coupon.getDiscountType(), c.getDiscountType());
    Assert.assertEquals(coupon.getDiscountPercent(), c.getDiscountPercent());
  }
}