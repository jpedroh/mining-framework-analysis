package demo;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.TimeZone;
import com.mambu.accounting.shared.model.GLAccountingRule;
import com.mambu.accounts.shared.model.AccountHolderType;
import com.mambu.accounts.shared.model.AccountState;
import com.mambu.accounts.shared.model.DecimalIntervalConstraints;
import com.mambu.accounts.shared.model.InterestAccountSettings;
import com.mambu.accounts.shared.model.InterestRateSource;
import com.mambu.accounts.shared.model.PredefinedFee;
import com.mambu.accounts.shared.model.PrincipalPaymentMethod;
import com.mambu.accounts.shared.model.ProductSecuritySettings;
import com.mambu.accounts.shared.model.TransactionChannel;
import com.mambu.accounts.shared.model.TransactionDetails;
import com.mambu.accountsecurity.shared.model.Guaranty;
import com.mambu.accountsecurity.shared.model.Guaranty.GuarantyType;
import com.mambu.accountsecurity.shared.model.InvestorFund;
import com.mambu.admin.shared.model.InterestProductSettings;
import com.mambu.admin.shared.model.PrincipalPaymentProductSettings;
import com.mambu.api.server.handler.loan.model.JSONLoanAccountResponse;
import com.mambu.api.server.handler.loan.model.JSONRestructureEntity;
import com.mambu.api.server.handler.loan.model.JSONTransactionRequest;
import com.mambu.api.server.handler.loan.model.RestructureDetails;
import com.mambu.apisdk.MambuAPIFactory;
import com.mambu.apisdk.exception.MambuApiException;
import com.mambu.apisdk.services.CustomFieldValueService;
import com.mambu.apisdk.services.DocumentsService;
import com.mambu.apisdk.services.LoansService;
import com.mambu.apisdk.services.OrganizationService;
import com.mambu.apisdk.services.RepaymentsService;
import com.mambu.apisdk.services.SavingsService;
import com.mambu.apisdk.util.APIData;
import com.mambu.apisdk.util.APIData.CLOSER_TYPE;
import com.mambu.apisdk.util.DateUtils;
import com.mambu.apisdk.util.MambuEntityType;
import com.mambu.clients.shared.model.Client;
import com.mambu.clients.shared.model.Group;
import com.mambu.core.shared.model.Currency;
import com.mambu.core.shared.model.CustomFieldType;
import com.mambu.core.shared.model.CustomFieldValue;
import com.mambu.core.shared.model.LoanPenaltyCalculationMethod;
import com.mambu.core.shared.model.Money;
import com.mambu.core.shared.model.RepaymentAllocationElement;
import com.mambu.core.shared.model.User;
import com.mambu.docs.shared.model.Document;
import com.mambu.loans.shared.model.AmortizationMethod;
import com.mambu.loans.shared.model.CustomPredefinedFee;
import com.mambu.loans.shared.model.DisbursementDetails;
import com.mambu.loans.shared.model.GracePeriodType;
import com.mambu.loans.shared.model.LoanAccount;
import com.mambu.loans.shared.model.LoanAccount.RepaymentPeriodUnit;
import com.mambu.loans.shared.model.LoanProduct;
import com.mambu.loans.shared.model.LoanProductType;
import com.mambu.loans.shared.model.LoanTranche;
import com.mambu.loans.shared.model.LoanTransaction;
import com.mambu.loans.shared.model.LoanTransactionType;
import com.mambu.loans.shared.model.PrincipalPaymentAccountSettings;
import com.mambu.loans.shared.model.ProductArrearsSettings;
import com.mambu.loans.shared.model.Repayment;
import com.mambu.loans.shared.model.RepaymentScheduleMethod;
import com.mambu.loans.shared.model.ScheduleDueDatesMethod;
import com.mambu.savings.shared.model.SavingsAccount;
import com.mambu.savings.shared.model.SavingsProduct;
import com.mambu.savings.shared.model.SavingsType;
import demo.DemoUtil.FeeCategory;

/**
 * Test class to show example usage of the api calls
 * 
 * @author edanilkis
 * 
 */
public class DemoTestLoanService {
  private static String NEW_LOAN_ACCOUNT_ID;

  private static Client demoClient;

  private static Group demoGroup;

  private static User demoUser;

  private static LoanProduct demoProduct;

  private static LoanAccount demoLoanAccount;

  private static LoanAccount newAccount;

  private static String methodName = null;

  private static String extraAmount = "55";

  private static String extraPercentage = "5";

  public static void main(String[] args) {
    DemoUtil.setUp();
    try {
      final String testProductId = DemoUtil.demoLaonProductId;
      final String testAccountId = null;
      demoClient = DemoUtil.getDemoClient(null);
      demoGroup = DemoUtil.getDemoGroup(null);
      demoUser = DemoUtil.getDemoUser();
      LoanProductType productTypes[];
      boolean productTypesTesting;
      if (testProductId != null && testProductId.equals(DemoUtil.allProductTypes)) {
        productTypesTesting = true;
        productTypes = LoanProductType.values();
      } else {
        productTypesTesting = false;
        LoanProduct testProduct = DemoUtil.getDemoLoanProduct(testProductId);
        productTypes = new LoanProductType[] { testProduct.getLoanProductType() };
      }
      for (LoanProductType productType : productTypes) {
        System.out.println("\n*** Product Type=" + productType + " ***");
        demoProduct = (productTypesTesting) ? DemoUtil.getDemoLoanProduct(productType) : DemoUtil.getDemoLoanProduct(testProductId);
        if (demoProduct == null) {
          continue;
        }
        System.out.println("Product Id=" + demoProduct.getId() + " Name=" + demoProduct.getName() + " ***");
        demoLoanAccount = DemoUtil.getDemoLoanAccount(testAccountId);
        if (demoLoanAccount != null) {
          System.out.println("Using Demo Loan Account=" + demoLoanAccount.getId() + "\tName=" + demoLoanAccount.getName());
        } else {
          System.out.println("WARNING: no Demo account found for ID=" + testAccountId);
        }
        try {
          testCreateJsonAccount();
          testAddAndRemoveSetllementAccounts();
          testPatchLoanAccountTerms();
          testUpdateTranchesAmount();
          testApproveLoanAccount();
          testUndoApproveLoanAccount();
          testUpdateLoanAccount();
          CLOSER_TYPE testTypes[] = { CLOSER_TYPE.REJECT, CLOSER_TYPE.WITHDRAW };
          for (CLOSER_TYPE closerType : testTypes) {
            LoanAccount closedAccount = testCloseLoanAccount(closerType);
            testUndoCloseLoanAccount(closedAccount);
          }
          testDeleteLoanAccount();
          testCreateJsonAccount();
          testRequestApprovalLoanAccount();
          testApproveLoanAccount();
          testDisburseLoanAccount();
          testChangeInterestRateForActiveRevolvingCreditLoans();
          testEditLoanAmountForActiveRevolvingCreditLoans();
          testEditPrincipalPaymentAmountForActiveRevolvingCreditLoans();
          testEditPrincipalPaymentPercentageForActiveRevolvingCreditLoans();
          testUndoDisburseLoanAccount();
          testDisburseLoanAccount();
          testGetLoanAccountTransactions();
          testLockLoanAccount();
          testUnlockLoanAccount();
          testRepayLoanAccount(true);
          LoanAccount closedAccount = testCloseLoanAccount(CLOSER_TYPE.CLOSE);
          testUndoCloseLoanAccount(closedAccount);
          testCreateJsonAccount();
          testUpdatingAccountTranches();
          testUpdatingAccountFunds();
          testUpdateLoanAccountGuarantees();
          testRequestApprovalLoanAccount();
          testApproveLoanAccount();
          testDisburseLoanAccount();
          testApplyInterestToLoanAccount();
          testApplyFeeToLoanAccount();
          testBulkReverseLoanTransactions();
          testGetLoanProductSchedule();
          testGetLoanAccount();
          testGetLoanAccountDetails();
          testGetLoanWithSettlemntAccounts();
          testUpdateDeleteCustomFields();
          testGetLoanAccountsByBranchCentreOfficerState();
          testGetLoanAccountsForClient();
          testGetLoanAccountsForGroup();
          List<LoanTransaction> transactions = testGetLoanAccountTransactions();
          testReverseLoanAccountTransactions(transactions);
          testRepayLoanAccount(false);
          testWriteOffLoanAccount();
          testCreateJsonAccount();
          testRequestApprovalLoanAccount();
          testApproveLoanAccount();
          testDisburseLoanAccount();
          testRescheduleAndRefinanceLoanAccount();
          testGetLoanProducts();
          testGetLoanProductById();
          testGetDocuments();
        } catch (MambuApiException e) {
          DemoUtil.logException(methodName, e);
          System.out.println("Product Type=" + demoProduct.getLoanProductType() + "\tID=" + demoProduct.getId() + "\tName=" + demoProduct.getName());
        }
      }
    } catch (MambuApiException e) {
      System.out.println("Exception caught in Demo Test Loan Service");
      System.out.println("Error code=" + e.getErrorCode());
      System.out.println(" Cause=" + e.getCause() + ".  Message=" + e.getMessage());
    }
  }

  public static void testChangeInterestRateForActiveRevolvingCreditLoans() throws MambuApiException {
    methodName = new Object() { }.getClass().getEnclosingMethod().getName();
    System.out.println("\nIn " + methodName);
    LoansService loanService = MambuAPIFactory.getLoanService();
    AccountState accountState = loanService.getLoanAccount(NEW_LOAN_ACCOUNT_ID).getAccountState();
    LoanProductType loanProductType = demoProduct.getLoanProductType();
    if (loanProductType.equals(LoanProductType.REVOLVING_CREDIT) && accountState.equals(AccountState.ACTIVE)) {
      JSONTransactionRequest jsonTransactionRequest = new JSONTransactionRequest();
      Calendar calendar = Calendar.getInstance();
      calendar.add(Calendar.MONTH, 1);
      jsonTransactionRequest.setDate(calendar.getTime());
      jsonTransactionRequest.setRate(new BigDecimal(10));
      jsonTransactionRequest.setNotes("Interest rate changed through API to be " + jsonTransactionRequest.getRate() + "%");
      LoanTransaction loanTransaction = loanService.postInterestRateChange(NEW_LOAN_ACCOUNT_ID, jsonTransactionRequest);
      System.out.println("The interest rate was edited for the loan account " + NEW_LOAN_ACCOUNT_ID + ". Transaction ID = " + loanTransaction.getTransactionId());
    } else {
      System.out.println("The loan account does not meet the prerequisites, therefore its interest rate will not be updated.");
    }
  }

  private static void testEditLoanAmountForActiveRevolvingCreditLoans() throws MambuApiException {
    System.out.println(methodName = "\nIn testEditLoanAmountForActiveRevolvingCreditLoans");
    LoanProductType loanProductType = demoProduct.getLoanProductType();
    LoansService loanService = MambuAPIFactory.getLoanService();
    AccountState accountState = loanService.getLoanAccount(NEW_LOAN_ACCOUNT_ID).getAccountState();
    if (loanProductType.equals(LoanProductType.REVOLVING_CREDIT) && accountState.equals(AccountState.ACTIVE)) {
      LoanAccount newLoanAccount = new LoanAccount();
      newLoanAccount.setId(NEW_LOAN_ACCOUNT_ID);
      newLoanAccount.setLoanAmount(newAccount.getLoanAmount().add(new BigDecimal(extraAmount)));
      newLoanAccount.setPeriodicPayment((BigDecimal) null);
      newLoanAccount.setRepaymentInstallments(null);
      newLoanAccount.setGracePeriod(null);
      newLoanAccount.setPrincipalRepaymentInterval(null);
      newLoanAccount.setArrearsTolerancePeriod(null);
      boolean patchResult = loanService.patchLoanAccount(newLoanAccount);
      System.out.println("The loan amount was edited for the loan account " + NEW_LOAN_ACCOUNT_ID + ". Status: " + patchResult);
    } else {
      System.out.println("The loan account does not meet the prerequisites, therefore its loan amount will not be updated.");
    }
  }

  private static void testEditPrincipalPaymentAmountForActiveRevolvingCreditLoans() throws MambuApiException {
    methodName = new Object() { }.getClass().getEnclosingMethod().getName();
    System.out.println("\nIn " + methodName);
    LoanProductType loanProductType = demoProduct.getLoanProductType();
    LoansService loanService = MambuAPIFactory.getLoanService();
    AccountState accountState = loanService.getLoanAccount(NEW_LOAN_ACCOUNT_ID).getAccountState();
    PrincipalPaymentAccountSettings paymentSettings = newAccount.getPrincipalPaymentSettings();
    if (loanProductType.equals(LoanProductType.REVOLVING_CREDIT) && PrincipalPaymentMethod.FLAT.equals(paymentSettings.getPrincipalPaymentMethod()) && (accountState.equals(AccountState.ACTIVE) || accountState.equals(AccountState.ACTIVE_IN_ARREARS))) {
      LoanAccount newLoanAccount = new LoanAccount();
      newLoanAccount.setId(NEW_LOAN_ACCOUNT_ID);
      paymentSettings.setAmount(paymentSettings.getAmount().add(new BigDecimal(extraAmount)));
      newLoanAccount.setPrincipalPaymentSettings(paymentSettings);
      newLoanAccount.setLoanAmount(newAccount.getLoanAmount().add(new BigDecimal(extraAmount)));
      newLoanAccount.setPeriodicPayment((BigDecimal) null);
      newLoanAccount.setRepaymentInstallments(null);
      newLoanAccount.setGracePeriod(null);
      newLoanAccount.setPrincipalRepaymentInterval(null);
      newLoanAccount.setArrearsTolerancePeriod(null);
      boolean patchResult = loanService.patchLoanAccount(newLoanAccount);
      System.out.println("The loan\'s principal payment amount was edited for the loan account " + NEW_LOAN_ACCOUNT_ID + ". Status: " + patchResult);
    } else {
      System.out.println("The loan account does not meet the prerequisites, therefore its principal payment amount will not be updated.");
    }
  }

  private static void testEditPrincipalPaymentPercentageForActiveRevolvingCreditLoans() throws MambuApiException {
    methodName = new Object() { }.getClass().getEnclosingMethod().getName();
    System.out.println("\nIn " + methodName);
    LoanProductType loanProductType = demoProduct.getLoanProductType();
    LoansService loanService = MambuAPIFactory.getLoanService();
    AccountState accountState = loanService.getLoanAccount(NEW_LOAN_ACCOUNT_ID).getAccountState();
    PrincipalPaymentAccountSettings paymentSettings = newAccount.getPrincipalPaymentSettings();
    if (loanProductType.equals(LoanProductType.REVOLVING_CREDIT) && PrincipalPaymentMethod.OUTSTANDING_PRINCIPAL_PERCENTAGE.equals(paymentSettings.getPrincipalPaymentMethod()) && (accountState.equals(AccountState.ACTIVE) || accountState.equals(AccountState.ACTIVE_IN_ARREARS))) {
      LoanAccount newLoanAccount = new LoanAccount();
      newLoanAccount.setId(NEW_LOAN_ACCOUNT_ID);
      paymentSettings.setPercentage(paymentSettings.getPercentage().add(new BigDecimal(extraPercentage)));
      newLoanAccount.setPrincipalPaymentSettings(paymentSettings);
      newLoanAccount.setLoanAmount((BigDecimal) null);
      newLoanAccount.setPeriodicPayment((BigDecimal) null);
      newLoanAccount.setRepaymentInstallments(null);
      newLoanAccount.setGracePeriod(null);
      newLoanAccount.setPrincipalRepaymentInterval(null);
      newLoanAccount.setArrearsTolerancePeriod(null);
      boolean patchResult = loanService.patchLoanAccount(newLoanAccount);
      System.out.println("The loan\'s principal payment percentage was edited for the loan account " + NEW_LOAN_ACCOUNT_ID + ". Status: " + patchResult);
    } else {
      System.out.println("The loan account does not meet the prerequisites, therefore its principal payment percentage will not be updated.");
    }
  }

  public static void testGetLoanAccount() throws MambuApiException {
    System.out.println(methodName = "\nIn testGetLoanAccount");
    LoansService loanService = MambuAPIFactory.getLoanService();
    String accountId = NEW_LOAN_ACCOUNT_ID;
    System.out.println("Got loan account  by ID: " + loanService.getLoanAccount(accountId).getName());
  }

  public static void testGetLoanAccountDetails() throws MambuApiException {
    System.out.println(methodName = "\nIn testGetLoanAccountDetails");
    LoansService loanService = MambuAPIFactory.getLoanService();
    String accountId = NEW_LOAN_ACCOUNT_ID;
    LoanAccount loanDeatils = loanService.getLoanAccountDetails(accountId);
    System.out.println("Got loan account by ID with details: " + loanDeatils.getName() + "\tId=" + loanDeatils.getId());
    DisbursementDetails disbDetails = loanDeatils.getDisbursementDetails();
    logDisbursementDetails(disbDetails);
    List<Guaranty> guarantees = loanDeatils.getGuarantees();
    if (guarantees == null) {
      System.out.println("Account has no guarantees defined");
      return;
    }
    System.out.println("Logging Guarantees:");
    for (Guaranty guaranty : guarantees) {
      System.out.println("Guarantor type=" + guaranty.getType() + "\tAssetName=" + guaranty.getAssetName() + "\tGurantor Key=" + guaranty.getGuarantorKey() + "\tSavinsg Key=" + guaranty.getSavingsAccountKey() + "\tAmount=" + guaranty.getAmount());
      List<CustomFieldValue> guarantyCustomValues = guaranty.getCustomFieldValues();
      DemoUtil.logCustomFieldValues(guarantyCustomValues, "Guarantor", guaranty.getEncodedKey());
    }
    List<InvestorFund> funds = loanDeatils.getFunds();
    if (funds == null) {
      System.out.println("Account has no investor funds defined");
      return;
    }
    System.out.println("Logging Investor Funds:");
    for (InvestorFund fund : funds) {
      System.out.println("Guarantor type=" + fund.getType() + "\tGurantor Key=" + fund.getGuarantorKey() + "\tSavinsg Key=" + fund.getSavingsAccountKey() + "\tAmount=" + fund.getAmount());
      List<CustomFieldValue> fundCustomValues = fund.getCustomFieldValues();
      DemoUtil.logCustomFieldValues(fundCustomValues, "Fund", fund.getEncodedKey());
    }
  }

  public static void testCreateJsonAccount() throws MambuApiException {
    System.out.println(methodName = "\nIn testCreateJsonAccount");
    LoansService loanService = MambuAPIFactory.getLoanService();
    LoanAccount account = makeLoanAccountForDemoProduct();
    account.setCustomFieldValues(null);
    List<CustomFieldValue> clientCustomInformation = DemoUtil.makeForEntityCustomFieldValues(CustomFieldType.LOAN_ACCOUNT_INFO, demoProduct.getEncodedKey());
    account.setCustomFieldValues(clientCustomInformation);
    newAccount = loanService.createLoanAccount(account);
    NEW_LOAN_ACCOUNT_ID = newAccount.getId();
    System.out.println("Loan Account created OK, ID=" + newAccount.getId() + " Name= " + newAccount.getLoanName() + " Account Holder Key=" + newAccount.getAccountHolderKey());
    logDisbursementDetails(newAccount.getDisbursementDetails());
    List<CustomFieldValue> customFieldValues = newAccount.getCustomFieldValues();
    DemoUtil.logCustomFieldValues(customFieldValues, newAccount.getLoanName(), newAccount.getId());
  }

  public static void testUpdateLoanAccount() throws MambuApiException {
    System.out.println(methodName = "\nIn testUpdateLoanAccount");
    LoansService loanService = MambuAPIFactory.getLoanService();
    LoanAccount account = loanService.getLoanAccount(NEW_LOAN_ACCOUNT_ID);
    LoanAccount updatedAccount = account;
    List<CustomFieldValue> customFields = account.getCustomFieldValues();
    List<CustomFieldValue> updatedFields = new ArrayList<CustomFieldValue>();
    if (customFields != null && customFields.size() > 0) {
      for (CustomFieldValue value : customFields) {
        value = DemoUtil.makeNewCustomFieldValue(value);
        updatedFields.add(value);
      }
    } else {
      System.out.println("Adding new custom fields to the account " + updatedAccount.getId());
      updatedFields = DemoUtil.makeForEntityCustomFieldValues(CustomFieldType.LOAN_ACCOUNT_INFO, account.getProductTypeKey(), false);
    }
    updatedAccount.setCustomFieldValues(updatedFields);
    clearInterestRateFieldsForFunderAccounts(demoProduct, updatedAccount);
    LoanAccount updatedAccountResult = loanService.updateLoanAccount(updatedAccount);
    System.out.println("Loan Update OK, ID=" + updatedAccountResult.getId() + "\tAccount Name=" + updatedAccountResult.getName());
    List<CustomFieldValue> updatedCustomFields = updatedAccountResult.getCustomFieldValues();
    if (updatedCustomFields != null) {
      System.out.println("Custom Fields for Loan Account\n");
      for (CustomFieldValue value : updatedCustomFields) {
        System.out.println("CustomFieldKey=" + value.getCustomFieldKey() + "\tValue=" + value.getValue() + "\tName=" + value.getCustomField().getName());
      }
    }
  }

  public static void testUpdateTranchesAmount() throws MambuApiException {
    System.out.println(methodName = "\nIn testUpdateTranchesAmount");
    LoansService loanService = MambuAPIFactory.getLoanService();
    LoanAccount account = loanService.getLoanAccount(NEW_LOAN_ACCOUNT_ID);
    if (account.getTranches() != null && !account.getTranches().isEmpty()) {
      List<LoanTranche> tranches = account.getTranches();
      BigDecimal firstTranchAmount = tranches.get(0).getAmount();
      tranches.get(0).setAmount(firstTranchAmount.add(new BigDecimal(extraAmount)));
      LoanAccount updatedAccount = loanService.updateLoanAccountTranches(account.getId(), tranches);
      System.out.println("Loan account tranches amount updated for account " + updatedAccount.getId());
    } else {
      System.out.println("Loan account " + account.getId() + " does not have tranches to be updated");
    }
  }

  public static void testPatchLoanAccountTerms() throws MambuApiException {
    methodName = new Object() { }.getClass().getEnclosingMethod().getName();
    System.out.println("\nIn " + methodName);
    LoansService loanService = MambuAPIFactory.getLoanService();
    LoanAccount theAccount = newAccount;
    LoanAccount account = new LoanAccount();
    account.setId(theAccount.getId());
    account.setLoanAmount(theAccount.getLoanAmount());
    if (demoProduct.getInterestRateSettings().getInterestRateSource() == InterestRateSource.FIXED_INTEREST_RATE) {
      account.setInterestRate(theAccount.getInterestRate());
    } else {
      account.setInterestSpread(theAccount.getInterestSpread());
    }
    account.setRepaymentInstallments(theAccount.getRepaymentInstallments());
    account.setRepaymentPeriodCount(theAccount.getRepaymentPeriodCount());
    account.setRepaymentPeriodUnit(theAccount.getRepaymentPeriodUnit());
    account.setDisbursementDetails(theAccount.getDisbursementDetails());
    account = updateExpectedDisbursementDateAndFirstRepaymentDate(account);
    account.setGracePeriod(theAccount.getGracePeriod());
    account.setPrincipalRepaymentInterval(theAccount.getPrincipalRepaymentInterval());
    account.setPenaltyRate(theAccount.getPenaltyRate());
    account.setPeriodicPayment(theAccount.getPeriodicPayment());
    account.setLoanAmount(theAccount.getLoanAmount().add(new BigDecimal(extraAmount)));
    account.setArrearsTolerancePeriod(theAccount.getArrearsTolerancePeriod());
    account.setPrincipalPaymentSettings(theAccount.getPrincipalPaymentSettings());
    boolean result = loanService.patchLoanAccount(account);
    System.out.println("Loan Terms Update for account. Status=" + result);
  }

  public static void testUpdatingAccountTranches() throws MambuApiException {
    System.out.println(methodName = "\nIn testUpdatingAccountTranches");
    LoanAccount theAccount = newAccount;
    String accountId = theAccount.getId();
    List<LoanTranche> allTranches = theAccount.getTranches();
    if (allTranches == null || allTranches.size() == 0) {
      System.out.println("WARNING: cannot test update tranches: loan account " + theAccount.getId() + " doesn\'t have tranches");
      return;
    }
    List<LoanTranche> nonDisbursedTranches = theAccount.getNonDisbursedTranches();
    if (nonDisbursedTranches == null || nonDisbursedTranches.size() == 0) {
      System.out.println("WARNING: cannot test update tranches: loan account " + theAccount.getId() + " doesn\'t have any non-disbursed tranches");
      return;
    }
    List<LoanTranche> disbursedTranches = theAccount.getDisbursedTranches();
    boolean hasDisbursedTranches = disbursedTranches != null && disbursedTranches.size() > 0;
    Date trancheDate = DemoUtil.getAsMidnightUTC();
    long fiveDays = 5 * 24 * 60 * 60 * 1000L;
    int i = 0;
    Date firstRepaymentDate = theAccount.getDisbursementDetails() != null ? theAccount.getDisbursementDetails().getFirstRepaymentDate() : null;
    for (LoanTranche tranche : nonDisbursedTranches) {
      trancheDate = new Date(trancheDate.getTime() + i * fiveDays);
      if (!hasDisbursedTranches && firstRepaymentDate != null && i == 0) {
        if (trancheDate.after(firstRepaymentDate)) {
          trancheDate = firstRepaymentDate;
        }
      }
      tranche.setExpectedDisbursementDate(trancheDate);
      i++;
    }
    System.out.println("\nUpdating existent tranches");
    LoansService loanService = MambuAPIFactory.getLoanService();
    LoanAccount result = loanService.updateLoanAccountTranches(accountId, nonDisbursedTranches);
    System.out.println("Loan Tranches updated for account " + accountId + " Total New Tranches=" + result.getNonDisbursedTranches().size());
    for (LoanTranche tranche : nonDisbursedTranches) {
      tranche.setEncodedKey(null);
    }
    System.out.println("\nDeleting and re-creating the same tranches");
    LoanAccount result2 = loanService.updateLoanAccountTranches(accountId, nonDisbursedTranches);
    System.out.println("Loan Tranches deleted and added for account " + accountId + " Total New Tranches=" + result2.getNonDisbursedTranches().size());
  }

  public static void testUpdatingAccountFunds() throws MambuApiException {
    System.out.println(methodName = "\nIn testUpdatingAccountFunds");
    LoanAccount theAccount = newAccount;
    String accountId = theAccount.getId();
    List<InvestorFund> funds = theAccount.getFunds();
    if (funds == null || funds.size() == 0) {
      System.out.println("WARNING: cannot test update funds: loan account " + theAccount.getId() + " doesn\'t have funds");
      return;
    }
    BigDecimal changeByAmount = new BigDecimal(50.0);
    Money loanAmount = theAccount.getLoanAmount();
    Money fundsTotal = Money.zero();
    for (InvestorFund fund : funds) {
      Money currentAmount = fund.getAmount();
      Money newFundAmount = currentAmount == null ? new Money(changeByAmount.doubleValue()) : currentAmount.add(changeByAmount);
      if (fundsTotal.add(newFundAmount).isMoreThan(loanAmount)) {
        newFundAmount = loanAmount.subtract(fundsTotal);
      }
      fund.setAmount(newFundAmount);
      fundsTotal = fundsTotal.add(newFundAmount);
    }
    System.out.println("\nUpdating existent funds");
    LoansService loanService = MambuAPIFactory.getLoanService();
    LoanAccount result = loanService.updateLoanAccountFunds(accountId, funds);
    System.out.println("Loan Funds updated for account " + accountId + " Total New Funds=" + result.getFunds().size());
    List<InvestorFund> resultFunds = result.getFunds();
    List<InvestorFund> updatedFunds = new ArrayList<>();
    for (InvestorFund fund : resultFunds) {
      InvestorFund newFund = new InvestorFund();
      newFund.setAmount(fund.getAmount());
      newFund.setGuarantorKey(fund.getGuarantorKey());
      newFund.setSavingsAccountKey(fund.getSavingsAccountKey());
      newFund.setCustomFieldValues(fund.getCustomFieldValues());
      updatedFunds.add(newFund);
    }
    System.out.println("\nDeleting and re-creating the same funds");
    LoanAccount result2 = loanService.updateLoanAccountFunds(accountId, updatedFunds);
    System.out.println("Loan Funds deleted and added for account " + accountId + " Total New Funds=" + result2.getFunds().size());
  }

  public static void testGetLoanWithSettlemntAccounts() throws MambuApiException {
    System.out.println(methodName = "\nIn testGetLoanWithSettlemntAccounts");
    String accountId = NEW_LOAN_ACCOUNT_ID;
    LoansService loanService = MambuAPIFactory.getLoanService();
    System.out.println("Getting Settlement Account for Loan " + accountId);
    JSONLoanAccountResponse loanAccountResponse = loanService.getLoanAccountWithSettlementAccounts(accountId);
    List<SavingsAccount> settlementAccounts = loanAccountResponse.getSettlementAccounts();
    int totalAccounts = settlementAccounts == null ? 0 : settlementAccounts.size();
    System.out.println("Total Settlement Accounts-" + totalAccounts + " for Account ID=" + accountId);
    if (totalAccounts == 0) {
      return;
    }
    for (SavingsAccount savings : settlementAccounts) {
      System.out.println("\tSavings ID=" + savings.getId() + "\tName=" + savings.getName() + "\tHolderKey=" + savings.getAccountHolderKey());
    }
  }

  public static void testUpdateLoanAccountGuarantees() throws MambuApiException {
    System.out.println(methodName = "\nIn testUpdateLoanAccountGuarantees");
    LoanAccount theAccount = newAccount;
    String accountId = theAccount.getId();
    List<Guaranty> guaraantees = theAccount.getGuarantees();
    if (guaraantees == null || guaraantees.size() == 0) {
      System.out.println("WARNING: cannot test update Guarantees: loan account " + theAccount.getId() + " doesn\'t have Guarantees");
      return;
    }
    for (Guaranty guaranty : guaraantees) {
      guaranty.setAmount(guaranty.getAmount().add(new BigDecimal(50.0)));
    }
    System.out.println("\nUpdating existent guarantees");
    LoansService loanService = MambuAPIFactory.getLoanService();
    LoanAccount result = loanService.updateLoanAccountGuarantees(accountId, guaraantees);
    System.out.println("Loan Guarantees updated for account " + accountId + " Total New Guarantees=" + result.getGuarantees().size());
    List<Guaranty> resultGuarantees = result.getGuarantees();
    List<Guaranty> updatedGuarantees = new ArrayList<>();
    for (Guaranty guaranty : resultGuarantees) {
      Guaranty newGuaranty = new Guaranty();
      newGuaranty.setType(guaranty.getType());
      newGuaranty.setGuarantorType(guaranty.getGuarantorType());
      newGuaranty.setAssetName(guaranty.getAssetName());
      newGuaranty.setAmount(guaranty.getAmount());
      newGuaranty.setGuarantorKey(guaranty.getGuarantorKey());
      newGuaranty.setSavingsAccountKey(guaranty.getSavingsAccountKey());
      newGuaranty.setCustomFieldValues(guaranty.getCustomFieldValues());
      updatedGuarantees.add(newGuaranty);
    }
    System.out.println("\nDeleting and re-creating the same guarantees");
    LoanAccount result2 = loanService.updateLoanAccountGuarantees(accountId, updatedGuarantees);
    System.out.println("Loan Guarantees deleted and added for account " + accountId + " Total New Guarantees=" + result2.getGuarantees().size());
  }

  public static void testDisburseLoanAccount() throws MambuApiException {
    System.out.println(methodName = "\nIn testDisburseLoanAccount");
    LoansService loanService = MambuAPIFactory.getLoanService();
    if (newAccount == null) {
      System.out.println("\nThere is no account to disburse");
    }
    ;
    LoanAccount account = newAccount;
    String accountId = account.getId();
    Date disbursementDate = DemoUtil.getAsMidnightUTC();
    Date firstRepaymentDate = makeFirstRepaymentDate(account, demoProduct, true);
    Money amount = null;
    LoanProductType productType = demoProduct.getLoanProductType();
    switch (productType) {
      case DYNAMIC_TERM_LOAN:
      case FIXED_TERM_LOAN:
      case PAYMENT_PLAN:
      amount = null;
      break;
      case TRANCHED_LOAN:
      amount = null;
      disbursementDate = null;
      List<LoanTranche> nonDisbursedTranches = account.getNonDisbursedTranches();
      if (nonDisbursedTranches == null || nonDisbursedTranches.size() == 0) {
        System.out.println("WARNING: Cannot test disburse: Loan  " + account.getId() + " has non disbursed tranches");
        return;
      }
      Date expectedTrancheDisbDate = nonDisbursedTranches.get(0).getExpectedDisbursementDate();
      Date now = new Date();
      if (expectedTrancheDisbDate.after(now)) {
        System.out.println("WARNING: cannot disburse tranche. Its ExpectedDisbursementDate=" + expectedTrancheDisbDate);
        return;
      }
      if (account.getDisbursedTranches() != null && account.getDisbursedTranches().size() > 0) {
        firstRepaymentDate = null;
      } else {
        if (firstRepaymentDate != null && firstRepaymentDate.before(expectedTrancheDisbDate)) {
          long fiveDays = 5 * 24 * 60 * 60 * 1000L;
          firstRepaymentDate = new Date(firstRepaymentDate.getTime() + fiveDays);
        }
      }
      break;
      case REVOLVING_CREDIT:
      amount = account.getLoanAmount();
      if (account.getAccountState() != AccountState.APPROVED) {
        firstRepaymentDate = null;
      }
      break;
    }
    String disbursementDateParam = DateUtils.format(disbursementDate);
    String firstRepaymentDateParam = DateUtils.format(firstRepaymentDate);
    System.out.println("Disbursement=" + disbursementDateParam + "\tFirstRepaymentDate=" + firstRepaymentDateParam);
    String notes = "Disbursed loan for testing";
    DisbursementDetails disbDetails = newAccount.getDisbursementDetails();
    if (disbDetails == null) {
      disbDetails = new DisbursementDetails();
    }
    disbDetails.setFirstRepaymentDate(firstRepaymentDate);
    if (!demoProduct.isFundingSourceEnabled()) {
      disbDetails.setExpectedDisbursementDate(disbursementDate);
    }
    List<CustomPredefinedFee> disbursementFees = DemoUtil.makeDemoPredefinedFees(demoProduct, new HashSet<>(Collections.singletonList(FeeCategory.DISBURSEMENT)));
    disbDetails.setFees(disbursementFees);
    TransactionDetails accountTransactionDetails = disbDetails.getTransactionDetails();
    if (accountTransactionDetails == null) {
      accountTransactionDetails = new TransactionDetails();
    }
    String channelKey = accountTransactionDetails.getTransactionChannelKey();
    if (channelKey == null) {
      TransactionChannel channel = DemoUtil.getDemoTransactionChannel();
      channelKey = channel != null ? channel.getEncodedKey() : null;
    }
    TransactionDetails transactionDetails = new TransactionDetails();
    transactionDetails.setTransactionChannelKey(channelKey);
    disbDetails.setTransactionDetails(transactionDetails);
    List<CustomFieldValue> transactionFields = disbDetails.getCustomFieldValues();
    if (transactionFields == null || transactionFields.size() == 0) {
      System.out.println("Creating new transaction fields for channel=" + channelKey);
      transactionFields = DemoUtil.makeForEntityCustomFieldValues(CustomFieldType.TRANSACTION_CHANNEL_INFO, channelKey, false);
    }
    try {
      LoanTransaction transaction = loanService.disburseLoanAccount(accountId, amount, disbDetails, transactionFields, notes);
      System.out.println("Disbursed OK: Transaction Id=" + transaction.getTransactionId() + " amount=" + transaction.getAmount());
      testGetCustomFieldForLoanTransaction(account, transaction);
    } catch (MambuApiException e) {
      DemoUtil.logException(methodName, e);
    }
  }

  /**
	 * Gets the custom field values from the loan transaction passed as argument to this method, and then iterates over
	 * them and call Mambu to get the details and logs them to the console.
	 * 
	 * @param account
	 *            The account (loan account) holding the transaction
	 * @param transaction
	 *            The transaction holding the custom field details
	 * @throws MambuApiException
	 */
  private static void testGetCustomFieldForLoanTransaction(LoanAccount account, LoanTransaction transaction) throws MambuApiException {
    System.out.println(methodName = "\nIn testGetCustomFieldForTransaction");
    if (account == null || transaction == null) {
      System.out.println("Warning!! Account or transaction was found null," + " testGetCustomFieldForTransaction() method couldn`t run");
      return;
    }
    CustomFieldValueService customFieldValueService = MambuAPIFactory.getCustomFieldValueService();
    for (CustomFieldValue customFieldValue : transaction.getCustomFieldValues()) {
      List<CustomFieldValue> retrievedCustomFieldValues = customFieldValueService.getCustomFieldValue(MambuEntityType.LOAN_ACCOUNT, account.getId(), MambuEntityType.LOAN_TRANSACTION, transaction.getEncodedKey(), customFieldValue.getCustomFieldId());
      DemoUtil.logCustomFieldValues(retrievedCustomFieldValues, "LoanTransaction", account.getId());
    }
  }

  public static void testUndoDisburseLoanAccount() throws MambuApiException {
    System.out.println(methodName = "\nIn testUndoDisburseLoanAccount");
    try {
      LoansService loanService = MambuAPIFactory.getLoanService();
      String accountId = NEW_LOAN_ACCOUNT_ID;
      String notes = "Undo disbursement via Demo API";
      LoanTransaction transaction = loanService.undoDisburseLoanAccount(accountId, notes);
      System.out.println("\nOK Undo Loan Disbursement for account=" + accountId + "\tTransaction Id=" + transaction.getTransactionId());
    } catch (MambuApiException e) {
      DemoUtil.logException(methodName, e);
    }
  }

  public static void testWriteOffLoanAccount() throws MambuApiException {
    System.out.println(methodName = "\nIn testWriteOffLoanAccount");
    LoansService loanService = MambuAPIFactory.getLoanService();
    String accountId = NEW_LOAN_ACCOUNT_ID;
    String notes = "Write off account via Demo API";
    LoanTransaction transaction = loanService.writeOffLoanAccount(accountId, notes);
    System.out.println("\nOK Write Off for account=" + accountId + "\tTransaction Id=" + transaction.getTransactionId());
  }

  public static List<LoanTransaction> testGetLoanAccountTransactions() throws MambuApiException {
    System.out.println(methodName = "\nIn testGetLoanAccountTransactions");
    LoansService loanService = MambuAPIFactory.getLoanService();
    String offest = "0";
    String limit = "8";
    final String accountId = NEW_LOAN_ACCOUNT_ID;
    List<LoanTransaction> loanTransactions = loanService.getLoanAccountTransactions(accountId, offest, limit);
    System.out.println("Got loan accounts transactions, total=" + loanTransactions.size() + " in a range for the Loan with the " + accountId + " id:" + " Range=" + offest + "  " + limit);
    return loanTransactions;
  }

  public static void testReverseLoanAccountTransactions(List<LoanTransaction> transactions) throws MambuApiException {
    System.out.println(methodName = "\nIn testReverseLoanAccountTransactions");
    if (transactions == null || transactions.size() == 0) {
      System.out.println("WARNING: no transactions available to test transactions reversal");
    }
    LoansService loanService = MambuAPIFactory.getLoanService();
    boolean reversalTested = false;
    for (LoanTransaction transaction : transactions) {
      if (transaction.getReversalTransactionKey() != null) {
        continue;
      }
      LoanTransactionType originalTransactionType = transaction.getType();
      switch (originalTransactionType) {
        case PENALTY_APPLIED:
        case REPAYMENT:
        case FEE:
        case INTEREST_APPLIED:
        reversalTested = true;
        try {
          String reversalNotes = "Reversed " + originalTransactionType + " by Demo API";
          String originalTransactionId = String.valueOf(transaction.getTransactionId());
          System.out.println("Reversing " + originalTransactionType + "\tID=" + originalTransactionId + "\tAmount=" + transaction.getAmount());
          LoanTransaction reversed = loanService.reverseLoanTransaction(transaction.getParentAccountKey(), originalTransactionType, originalTransactionId, reversalNotes);
          System.out.println("Reversed Transaction " + transaction.getType() + "\tReversed Amount=" + reversed.getAmount().toString() + "\tBalance =" + reversed.getBalance().toString() + "\tTransaction Type=" + reversed.getType() + "\tAccount key=" + reversed.getParentAccountKey());
        } catch (MambuApiException e) {
          DemoUtil.logException(methodName, e);
          continue;
        }
        break;
        default:
        break;
      }
    }
    if (!reversalTested) {
      System.out.println("WARNING: no transaction types supporting reversal are available to test transaction reversal");
    }
  }

  public static void testRepayLoanAccount(boolean fullRepayment) throws MambuApiException {
    System.out.println(methodName = "\nIn testRepayLoanAccount");
    LoansService loanService = MambuAPIFactory.getLoanService();
    String accountId = newAccount.getId();
    LoanAccount account = loanService.getLoanAccountDetails(accountId);
    Money repaymentAmount = fullRepayment ? account.getTotalBalanceOutstanding() : account.getDueAmount(RepaymentAllocationElement.PRINCIPAL);
    System.out.println("Repayment Amount=" + repaymentAmount);
    if (repaymentAmount == null || repaymentAmount.isNegativeOrZero()) {
      repaymentAmount = new Money(320);
    }
    Date date = null;
    String notes = "repayment notes from API";
    TransactionDetails transactionDetails = DemoUtil.makeDemoTransactionDetails();
    String channelKey = transactionDetails != null ? transactionDetails.getTransactionChannelKey() : null;
    List<CustomFieldValue> transactionCustomFields = DemoUtil.makeForEntityCustomFieldValues(CustomFieldType.TRANSACTION_CHANNEL_INFO, channelKey, false);
    LoanTransaction transaction = loanService.makeLoanRepayment(accountId, repaymentAmount, date, transactionDetails, transactionCustomFields, notes);
    System.out.println("Repaid loan account with the " + accountId + " id response=" + transaction.getTransactionId() + "   for amount=" + transaction.getAmount());
    if (!fullRepayment) {
      testReverseLoanAccountTransactions(Collections.singletonList(transaction));
    }
  }

  public static void testApplyFeeToLoanAccount() throws MambuApiException {
    System.out.println(methodName = "\nIn testApplyFeeToLoanAccount");
    String accountId = NEW_LOAN_ACCOUNT_ID;
    LoanProductType productType = demoProduct.getLoanProductType();
    LoansService loanService = MambuAPIFactory.getLoanService();
    boolean needRepaymentNumber = productType == LoanProductType.FIXED_TERM_LOAN || productType == LoanProductType.PAYMENT_PLAN;
    Integer repaymentNumber = needRepaymentNumber ? 3 : null;
    if (needRepaymentNumber) {
      RepaymentsService repayemntService = MambuAPIFactory.getRepaymentsService();
      List<Repayment> repaymnts = repayemntService.getLoanAccountRepayments(NEW_LOAN_ACCOUNT_ID, null, null);
      if (repaymnts == null || repaymnts.size() == 0) {
        System.out.println("WARNING: cannot set repayment number for Apply Fee: NO schedule is available");
        return;
      }
    }
    String notes = "Fee Notes";
    List<CustomPredefinedFee> productFees = DemoUtil.makeDemoPredefinedFees(demoProduct, new HashSet<>(Collections.singletonList(FeeCategory.MANUAL)));
    if (productFees.size() > 0) {
      for (CustomPredefinedFee predefinedFee : productFees) {
        System.out.println("Applying Predefined Fee =" + predefinedFee.getPredefinedFeeEncodedKey() + "\tAmount=" + predefinedFee.getAmount());
        List<CustomPredefinedFee> customFees = new ArrayList<>();
        customFees.add(predefinedFee);
        LoanTransaction transaction = loanService.applyFeeToLoanAccount(accountId, customFees, repaymentNumber, notes);
        System.out.println("Predefined Fee. TransactionID=" + transaction.getTransactionId() + "\tAmount=" + transaction.getAmount().toString() + "\tFees Amount=" + transaction.getFeesAmount());
        testReverseLoanAccountTransactions(Collections.singletonList(transaction));
      }
    } else {
      System.out.println("WARNING: No Predefined Fees defined for product " + demoProduct.getId());
    }
    if (demoProduct.getAllowArbitraryFees()) {
      BigDecimal amount = new BigDecimal(15);
      String repaymentNumberString = repaymentNumber != null ? repaymentNumber.toString() : null;
      System.out.println("Applying Arbitrary Fee. Amount=" + amount + "\tRepayment Number=" + repaymentNumberString);
      LoanTransaction transaction = loanService.applyFeeToLoanAccount(accountId, amount.toPlainString(), repaymentNumberString, notes);
      System.out.println("Arbitrary Fee. TransactionID=" + transaction.getTransactionId() + "\tAmount=" + transaction.getAmount().toString() + "\tFees Amount=" + transaction.getFeesAmount());
      testReverseLoanAccountTransactions(Collections.singletonList(transaction));
    } else {
      System.out.println("WARNING: Arbitrary Fees no allowed for product " + demoProduct.getId());
    }
  }

  public static void testApplyInterestToLoanAccount() throws MambuApiException {
    System.out.println(methodName = "\nIn testApplyInterestToLoanAccount");
    LoansService loanService = MambuAPIFactory.getLoanService();
    String accountId = newAccount.getId();
    System.out.println("For Loan ID=" + accountId);
    Date date = new Date();
    String notes = "Notes for applying interest to a loan";
    try {
      LoanTransaction transaction = loanService.applyInterestToLoanAccount(accountId, date, notes);
      System.out.println("Transaction. ID= " + transaction.getTransactionId().toString() + "\tTransaction Amount=" + transaction.getAmount().toString());
    } catch (MambuApiException e) {
      DemoUtil.logException(methodName, e);
    }
  }

  public static void testRescheduleAndRefinanceLoanAccount() throws MambuApiException {
    System.out.println(methodName = "\nIn testRescheduleAndRefinanceLoanAccount");
    try {
      LoansService loanService = MambuAPIFactory.getLoanService();
      LoanAccount loanAccount = loanService.getLoanAccountDetails(NEW_LOAN_ACCOUNT_ID);
      String accountId = loanAccount.getEncodedKey();
      LoanProduct product = loanService.getLoanProduct(loanAccount.getProductTypeKey());
      Date firstRepayDate = makeFirstRepaymentDate(loanAccount, product, true);
      DisbursementDetails disbursementDetails = loanAccount.getDisbursementDetails();
      if (disbursementDetails == null) {
        disbursementDetails = new DisbursementDetails();
        loanAccount.setDisbursementDetails(disbursementDetails);
      }
      disbursementDetails.setFirstRepaymentDate(firstRepayDate);
      loanAccount.setId(null);
      loanAccount.setInterestRate(loanAccount.getInterestRate());
      BigDecimal principalWriteOff = new BigDecimal(200.00f);
      BigDecimal principalBalance = loanAccount.getPrincipalBalance().getAmount();
      BigDecimal newPrincipalBalance = principalBalance.subtract(principalWriteOff);
      System.out.println("Reschedule: original Principal balance=" + principalBalance + "\tNew=" + newPrincipalBalance);
      if (newPrincipalBalance.signum() != 1) {
        newPrincipalBalance = principalBalance;
        principalWriteOff = BigDecimal.ZERO;
        System.out.println("New now=" + newPrincipalBalance);
      }
      RestructureDetails rescheduleDetails = new RestructureDetails();
      if (!product.isFundingSourceEnabled()) {
        rescheduleDetails.setPrincipalWriteOff(principalWriteOff);
        loanAccount.setLoanAmount(principalBalance.subtract(principalWriteOff));
      }
      JSONRestructureEntity rescheduleEntity = new JSONRestructureEntity();
      rescheduleEntity.setAction(APIData.RESCHEDULE);
      rescheduleEntity.setLoanAccount(loanAccount);
      rescheduleEntity.setRestructureDetails(rescheduleDetails);
      System.out.println("Rescheduling account=" + accountId);
      LoanAccount rescheduledAccount = loanService.postLoanAccountRestructureAction(accountId, rescheduleEntity);
      System.out.println("RESCHEDULED OK. New Amount=" + rescheduledAccount.getLoanAmount() + "  Notes=" + rescheduledAccount.getNotes());
      if (demoProduct.getLoanProductType() != LoanProductType.REVOLVING_CREDIT && rescheduledAccount.getFunds() == null) {
        accountId = rescheduledAccount.getEncodedKey();
        BigDecimal topUpAmount = new BigDecimal(1000.00f);
        BigDecimal loamAmount = rescheduledAccount.getPrincipalBalance().getAmount();
        rescheduledAccount.setLoanAmount(loamAmount.add(topUpAmount));
        rescheduledAccount.setId(null);
        RestructureDetails refinanceDetails = new RestructureDetails();
        refinanceDetails.setTopUpAmount(topUpAmount);
        JSONRestructureEntity refinanceEntity = new JSONRestructureEntity();
        refinanceEntity.setAction(APIData.REFINANCE);
        refinanceEntity.setLoanAccount(rescheduledAccount);
        refinanceEntity.setRestructureDetails(refinanceDetails);
        System.out.println("Refinancing account=" + accountId);
        LoanAccount refinancedAccount = loanService.postLoanAccountRestructureAction(accountId, refinanceEntity);
        System.out.println("Account Refinanced. New Amount=" + refinancedAccount.getLoanAmount() + "  Notes=" + refinancedAccount.getNotes());
      } else {
        System.out.println("WARNING: REVOLVING_CREDIT accounts cannot be refinanced");
      }
    } catch (MambuApiException e) {
      DemoUtil.logException(methodName, e);
    }
  }

  public static void testGetLoanAccountsByBranchCentreOfficerState() throws MambuApiException {
    System.out.println(methodName = "\nIn testGetLoanAccountsByBranchCentreOfficerState");
    LoansService loanService = MambuAPIFactory.getLoanService();
    String branchId = demoClient.getAssignedBranchKey();
    String centreId = demoClient.getAssignedCentreKey();
    String creditOfficerUserName = demoUser.getUsername();
    String accountState = null;
    String offset = null;
    String limit = null;
    List<LoanAccount> loanAccounts = loanService.getLoanAccountsByBranchCentreOfficerState(branchId, centreId, creditOfficerUserName, accountState, offset, limit);
    if (loanAccounts != null) {
      System.out.println("Got loan accounts for the branch, centre, officer, state, total loans=" + loanAccounts.size());
    }
    for (LoanAccount account : loanAccounts) {
      System.out.println("Account Name=" + account.getId() + "-" + account.getLoanName() + "\tBranchId=" + account.getAssignedBranchKey() + "\tCentreId=" + account.getAssignedCentreKey() + "\tCredit Officer=" + account.getAssignedUserKey());
    }
  }

  public static void testGetLoanAccountsForClient() throws MambuApiException {
    System.out.println(methodName = "\nIn testGetLoanAccountsForClient");
    LoansService loanService = MambuAPIFactory.getLoanService();
    String clientId = demoClient.getId();
    List<LoanAccount> loanAccounts = loanService.getLoanAccountsForClient(clientId);
    System.out.println("Got loan accounts for the client with the " + clientId + " id, Total=" + loanAccounts.size());
    for (LoanAccount account : loanAccounts) {
      System.out.println(account.getLoanName() + " - " + account.getId());
    }
    System.out.println();
  }

  public static void testGetLoanAccountsForGroup() throws MambuApiException {
    System.out.println(methodName = "\nIn testGetLoanAccountsForGroup");
    LoansService loanService = MambuAPIFactory.getLoanService();
    if (demoGroup == null) {
      System.out.println("WARNING: no Demo Group available");
      return;
    }
    String groupId = demoGroup.getId();
    List<LoanAccount> loanAccounts = loanService.getLoanAccountsForGroup(groupId);
    System.out.println("Got " + loanAccounts.size() + " loan accounts for the group with the " + groupId + " id, Total= " + loanAccounts.size());
    int i = 0;
    for (LoanAccount account : loanAccounts) {
      if (i > 0) {
        System.out.print("\n");
      }
      System.out.print(" Account id=" + account.getId() + "\tAccount Name=" + account.getLoanName());
      i++;
    }
    System.out.println();
  }

  public static void testRequestApprovalLoanAccount() throws MambuApiException {
    System.out.println(methodName = "\nIn testRequestApprovalLoanAccount");
    if (newAccount == null || newAccount.getAccountState() != AccountState.PARTIAL_APPLICATION) {
      System.out.println("WARNING: Need to create loan account in PARTIAL_APPLICATION state to test Request Approval");
      return;
    }
    LoansService loanService = MambuAPIFactory.getLoanService();
    String accountId = newAccount.getId();
    String requestNotes = "Requested Approval by Demo API";
    LoanAccount account = loanService.requestApprovalLoanAccount(accountId, requestNotes);
    System.out.println("Requested Approval for loan account with the " + accountId + " Loan name=" + account.getLoanName() + "  Account State=" + account.getState());
  }

  public static void testApproveLoanAccount() throws MambuApiException {
    System.out.println(methodName = "\nIn testApproveLoanAccount");
    LoansService loanService = MambuAPIFactory.getLoanService();
    LoanAccount account = loanService.approveLoanAccount(NEW_LOAN_ACCOUNT_ID, "some demo notes");
    System.out.println("Approving loan account with the " + NEW_LOAN_ACCOUNT_ID + " Loan name=" + account.getLoanName() + "  Account State=" + account.getState().toString());
  }

  /**
	 * Test Closing Loan account
	 * 
	 * @param closerType
	 *            closer type. Must not be null. Supported closer types are: REJECT, WITHDRAW and CLOSE
	 * @return updated account
	 * @throws MambuApiException
	 */
  public static LoanAccount testCloseLoanAccount(CLOSER_TYPE closerType) throws MambuApiException {
    System.out.println(methodName = "\nIn testCloseLoanAccount");
    LoansService loanService = MambuAPIFactory.getLoanService();
    if (closerType == null) {
      throw new IllegalArgumentException("Closer type must not be null");
    }
    LoanAccount account = newAccount;
    String accountId = account.getId();
    String notes = "Closed by Demo Test";
    System.out.println("Closing account with Closer Type=" + closerType + "\tId=" + accountId + "\tState=" + account.getAccountState());
    LoanAccount resultAaccount = loanService.closeLoanAccount(accountId, closerType, notes);
    System.out.println("Closed account id:" + resultAaccount.getId() + "\tNew State=" + resultAaccount.getAccountState().name() + "\tSubState=" + resultAaccount.getAccountSubState());
    return resultAaccount;
  }

  /**
	 * Test Undo Closing Loan account
	 * 
	 * @param closedAccount
	 *            closed loan account. Must not be null. Account must be closed with API supported closer types are:
	 *            REJECT, WITHDRAW and CLOSE
	 * @return updated account
	 * @throws MambuApiException
	 */
  public static LoanAccount testUndoCloseLoanAccount(LoanAccount closedAccount) throws MambuApiException {
    System.out.println(methodName = "\nIn testUndoCloseLoanAccount");
    LoansService loanService = MambuAPIFactory.getLoanService();
    if (closedAccount == null || closedAccount.getId() == null) {
      System.out.println("Account must be not null for testing undo closer");
      return null;
    }
    String notes = "Undo notes";
    System.out.println("Undo Closing account with tId=" + closedAccount.getId() + "\tState=" + closedAccount.getAccountState() + "\tSubState=" + closedAccount.getAccountSubState());
    LoanAccount resultAaccount = loanService.undoCloseLoanAccount(closedAccount, notes);
    System.out.println("Undid Closed account id:" + resultAaccount.getId() + "\tNew State=" + resultAaccount.getAccountState().name() + "\tSubState=" + resultAaccount.getAccountSubState());
    return resultAaccount;
  }

  public static void testUndoApproveLoanAccount() throws MambuApiException {
    System.out.println(methodName = "\nIn testUndoApproveLoanAccount");
    LoansService loanService = MambuAPIFactory.getLoanService();
    final String accountId = NEW_LOAN_ACCOUNT_ID;
    LoanAccount account = loanService.undoApproveLoanAccount(accountId, "some undo approve demo notes");
    System.out.println("Undo Approving loan account with the " + accountId + " Loan name " + account.getLoanName() + "  Account State=" + account.getState().toString());
  }

  public static void testLockLoanAccount() throws MambuApiException {
    System.out.println(methodName = "\nIn testLockLoanAccount");
    LoansService loanService = MambuAPIFactory.getLoanService();
    String accountId = NEW_LOAN_ACCOUNT_ID;
    List<LoanTransaction> transactions = loanService.lockLoanAccount(accountId, "some lock demo notes");
    if (transactions == null || transactions.size() == 0) {
      System.out.println("No Transactions returned in response");
      return;
    }
    for (LoanTransaction transaction : transactions) {
      System.out.println("Locked account with ID " + accountId + " Transaction  " + transaction.getTransactionId() + " Type=" + transaction.getType() + "  Balance=" + transaction.getBalance());
    }
  }

  public static void testUnlockLoanAccount() throws MambuApiException {
    System.out.println(methodName = "\nIn testUnlockLoanAccount");
    LoansService loanService = MambuAPIFactory.getLoanService();
    String accountId = NEW_LOAN_ACCOUNT_ID;
    List<LoanTransaction> transactions = loanService.unlockLoanAccount(accountId, "some unlock demo notes");
    if (transactions == null || transactions.size() == 0) {
      System.out.println("No Transactions returned in response");
      return;
    }
    for (LoanTransaction transaction : transactions) {
      System.out.println("UnLocked account with ID " + accountId + " Transaction  " + transaction.getTransactionId() + " Type=" + transaction.getType() + "  Balance=" + transaction.getBalance());
    }
  }

  public static void testDeleteLoanAccount() throws MambuApiException {
    System.out.println(methodName = "\nIn testDeleteLoanAccount");
    LoansService loanService = MambuAPIFactory.getLoanService();
    String loanAccountId = NEW_LOAN_ACCOUNT_ID;
    boolean status = loanService.deleteLoanAccount(loanAccountId);
    System.out.println("Deletion status=" + status);
  }

  public static List<LoanProduct> testGetLoanProducts() throws MambuApiException {
    System.out.println(methodName = "\nIn testGetLoanProducts");
    LoansService loanService = MambuAPIFactory.getLoanService();
    String offset = "0";
    String limit = "100";
    List<LoanProduct> products = loanService.getLoanProducts(offset, limit);
    System.out.println("Got loan products, count=" + products.size());
    if (products.size() > 0) {
      for (LoanProduct product : products) {
        System.out.println("Product=" + product.getName() + "\tId=" + product.getId() + "\tKey=" + product.getEncodedKey() + "\tProduct Type=" + product.getLoanProductType());
      }
    }
    return products;
  }

  public static void testGetLoanProductById() throws MambuApiException {
    System.out.println(methodName = "\nIn testGetLoanProductById");
    LoansService loanService = MambuAPIFactory.getLoanService();
    String productId = demoProduct.getId();
    LoanProduct product = loanService.getLoanProduct(productId);
    List<GLAccountingRule> accountingRules = product.getGlProductRules();
    int totalAccountingRules = accountingRules == null ? 0 : accountingRules.size();
    System.out.println("Product=" + product.getName() + "\tId=" + product.getId() + "\tProduct Type=" + product.getLoanProductType() + "\tAccountingRules=" + totalAccountingRules);
    logProductSecuritySettings(product.getProductSecuritySettings());
  }

  public static void testGetLoanProductSchedule() throws MambuApiException {
    System.out.println(methodName = "\nIn testGetLoanProductSchedule");
    if (demoProduct.getLoanProductType() == LoanProductType.REVOLVING_CREDIT) {
      System.out.println("WARNING: Schedule preview is not supported for REVOLVING_CREDIT products");
      return;
    }
    LoansService loanService = MambuAPIFactory.getLoanService();
    String productId = demoProduct.getId();
    LoanAccount loanAccount = makeLoanAccountForDemoProduct();
    DisbursementDetails disbursementDetails = loanAccount.getDisbursementDetails();
    Date firstRepaymentDate = disbursementDetails != null ? disbursementDetails.getFirstRepaymentDate() : null;
    if (firstRepaymentDate != null) {
      long firstRepaymentTime = firstRepaymentDate.getTime();
      firstRepaymentDate = new Date(firstRepaymentTime - TimeZone.getDefault().getOffset(firstRepaymentTime));
      disbursementDetails.setFirstRepaymentDate(firstRepaymentDate);
    }
    Date expectedDisbursementDate = disbursementDetails != null ? disbursementDetails.getExpectedDisbursementDate() : null;
    if (expectedDisbursementDate != null) {
      long expectedDisbursemenTime = expectedDisbursementDate.getTime();
      expectedDisbursementDate = new Date(expectedDisbursemenTime - TimeZone.getDefault().getOffset(expectedDisbursemenTime));
      disbursementDetails.setExpectedDisbursementDate(expectedDisbursementDate);
    }
    System.out.println("Getting schedule with adjusted firstRepaymentDate=" + firstRepaymentDate + "\texpectedDisbursementDate=" + expectedDisbursementDate);
    List<Repayment> repayments = loanService.getLoanProductSchedule(productId, loanAccount);
    int totalRepayments = (repayments == null) ? 0 : repayments.size();
    System.out.println("Total repayments=" + totalRepayments + "\tfor product ID=" + productId);
    if (totalRepayments == 0) {
      return;
    }
    Repayment firstRepayment = repayments.get(0);
    Repayment lastRepayment = repayments.get(totalRepayments - 1);
    System.out.println("First Repayment. Due Date=" + firstRepayment.getDueDate() + "\tTotal Due=" + firstRepayment.getTotalDue());
    System.out.println("Last Repayment. Due Date=" + lastRepayment.getDueDate() + "\tTotal Due=" + lastRepayment.getTotalDue());
  }

  private static final String apiTestIdPrefix = "API-";

  private static LoanAccount makeLoanAccountForDemoProduct() throws MambuApiException {
    System.out.println(methodName = "\nIn makeLoanAccountForDemoProduct");
    System.out.println("\nProduct name=" + demoProduct.getName() + " id=" + demoProduct.getId());
    if (!demoProduct.isActivated()) {
      System.out.println("*** WARNING ***: demo product is NOT Active. Product name=" + demoProduct.getName() + " id=" + demoProduct.getId());
    }
    LoanAccount loanAccount = new LoanAccount();
    LoanProductType productType = demoProduct.getLoanProductType();
    final long time = new Date().getTime();
    loanAccount.setId(apiTestIdPrefix + time);
    loanAccount.setLoanName(demoProduct.getName());
    loanAccount.setProductTypeKey(demoProduct.getEncodedKey());
    boolean isForClient = demoProduct.isForIndividuals();
    String holderKey = (isForClient) ? demoClient.getEncodedKey() : demoGroup.getEncodedKey();
    AccountHolderType holderType = (isForClient) ? AccountHolderType.CLIENT : AccountHolderType.GROUP;
    loanAccount.setAccountHolderKey(holderKey);
    loanAccount.setAccountHolderType(holderType);
    DisbursementDetails disbursementDetails = new DisbursementDetails();
    loanAccount.setDisbursementDetails(disbursementDetails);
    Money amount = DemoUtil.getValueMatchingConstraints(demoProduct.getDefaultLoanAmount(), demoProduct.getMinLoanAmount(), demoProduct.getMaxLoanAmount(), new Money(3000.00f));
    loanAccount.setLoanAmount(amount);
    Money nullMoney = null;
    loanAccount.setPeriodicPayment(nullMoney);
    if (demoProduct.getAmortizationMethod() == AmortizationMethod.BALLOON_PAYMENTS) {
      loanAccount.setPeriodicPayment(amount.multiply(new BigDecimal(0.5)));
    }
    loanAccount.setInterestRate(null);
    loanAccount.setInterestRateSource(null);
    BigDecimal interestRate = null;
    if (demoProduct.getRepaymentScheduleMethod() != RepaymentScheduleMethod.NONE) {
      InterestProductSettings intRateSettings = demoProduct.getInterestRateSettings();
      InterestRateSource rateSource = intRateSettings == null ? null : intRateSettings.getInterestRateSource();
      intRateSettings = intRateSettings != null ? intRateSettings : new InterestProductSettings();
      interestRate = DemoUtil.getValueMatchingConstraints(intRateSettings.getDefaultInterestRate(), intRateSettings.getMinInterestRate(), intRateSettings.getMaxInterestRate(), new BigDecimal(6.5f));
      if (rateSource == InterestRateSource.INDEX_INTEREST_RATE) {
        loanAccount.setInterestSpread(interestRate);
      } else {
        loanAccount.setInterestRate(interestRate);
      }
      loanAccount.setInterestRateSource(rateSource);
      clearInterestRateFieldsForFunderAccounts(demoProduct, loanAccount);
    }
    if (productType == LoanProductType.REVOLVING_CREDIT) {
      PrincipalPaymentProductSettings productPaymentSettings = demoProduct.getPrincipalPaymentSettings();
      PrincipalPaymentAccountSettings principlaAccountSettings = new PrincipalPaymentAccountSettings();
      principlaAccountSettings.setPrincipalPaymentMethod(productPaymentSettings.getPrincipalPaymentMethod());
      principlaAccountSettings.setPrincipalFloorValue(productPaymentSettings.getPrincipalFloorValue());
      principlaAccountSettings.setPrincipalCeilingValue(productPaymentSettings.getPrincipalCeilingValue());
      PrincipalPaymentMethod method = productPaymentSettings.getPrincipalPaymentMethod();
      switch (method) {
        case FLAT:
        Money principalAmount = DemoUtil.getValueMatchingConstraints(productPaymentSettings.getDefaultAmount(), productPaymentSettings.getMinAmount(), productPaymentSettings.getMaxAmount(), new Money(100));
        principlaAccountSettings.setAmount(principalAmount);
        break;
        case OUTSTANDING_PRINCIPAL_PERCENTAGE:
        BigDecimal principalPercent = DemoUtil.getValueMatchingConstraints(productPaymentSettings.getDefaultPercentage(), productPaymentSettings.getMinPercentage(), productPaymentSettings.getMaxPercentage(), new BigDecimal(2));
        principlaAccountSettings.setPercentage(principalPercent);
        break;
      }
      loanAccount.setPrincipalPaymentSettings(principlaAccountSettings);
    }
    Date now = DemoUtil.getAsMidnightUTC();
    long aDay = 24 * 60 * 60 * 1000L;
    disbursementDetails.setExpectedDisbursementDate(new Date(now.getTime() + 3 * aDay));
    if (productType == LoanProductType.TRANCHED_LOAN) {
      LoanTranche tranche = new LoanTranche(loanAccount.getLoanAmount(), now);
      disbursementDetails.setExpectedDisbursementDate(null);
      ArrayList<LoanTranche> tanches = new ArrayList<LoanTranche>();
      tranche.setIndex(null);
      tanches.add(tranche);
      loanAccount.setTranches(tanches);
    }
    ScheduleDueDatesMethod scheduleDueDatesMethod = demoProduct.getScheduleDueDatesMethod();
    loanAccount.setRepaymentPeriodCount(null);
    loanAccount.setRepaymentPeriodUnit(null);
    loanAccount.setFixedDaysOfMonth(null);
    switch (scheduleDueDatesMethod) {
      case FIXED_DAYS_OF_MONTH:
      List<Integer> fixedDays = Arrays.asList(2, 18);
      loanAccount.setFixedDaysOfMonth(new ArrayList<>(fixedDays));
      break;
      case INTERVAL:
      Integer defRepPeriodCount = demoProduct.getDefaultRepaymentPeriodCount();
      if (defRepPeriodCount == null) {
        defRepPeriodCount = 30;
      }
      loanAccount.setRepaymentPeriodCount(defRepPeriodCount);
      RepaymentPeriodUnit units = demoProduct.getRepaymentPeriodUnit();
      if (units == null) {
        units = RepaymentPeriodUnit.DAYS;
      }
      loanAccount.setRepaymentPeriodUnit(units);
      break;
    }
    Integer repaymentInsatllments = DemoUtil.getValueMatchingConstraints(demoProduct.getDefaultNumInstallments(), demoProduct.getMinNumInstallments(), demoProduct.getMaxNumInstallments(), 12);
    if (productType == LoanProductType.REVOLVING_CREDIT) {
      repaymentInsatllments = null;
    }
    loanAccount.setRepaymentInstallments(repaymentInsatllments);
    loanAccount.setPrincipalRepaymentInterval(1);
    Integer principalRepaymentInterva = demoProduct.getDefaultPrincipalRepaymentInterval();
    if (principalRepaymentInterva != null) {
      loanAccount.setPrincipalRepaymentInterval(principalRepaymentInterva);
    }
    loanAccount.setPenaltyRate(null);
    if (demoProduct.getLoanPenaltyCalculationMethod() != LoanPenaltyCalculationMethod.NONE) {
      BigDecimal penaltyRate = DemoUtil.getValueMatchingConstraints(demoProduct.getDefaultPenaltyRate(), demoProduct.getMinPenaltyRate(), demoProduct.getMaxPenaltyRate(), new BigDecimal(0.5f));
      loanAccount.setPenaltyRate(penaltyRate);
    }
    loanAccount.setGracePeriod(null);
    if (demoProduct.getGracePeriodType() != GracePeriodType.NONE) {
      Integer gracePeriod = DemoUtil.getValueMatchingConstraints(demoProduct.getDefaultGracePeriod(), demoProduct.getMinGracePeriod(), demoProduct.getMaxGracePeriod(), null);
      loanAccount.setGracePeriod(gracePeriod);
    }
    ArrayList<Guaranty> guarantees = new ArrayList<Guaranty>();
    if (demoProduct.isGuarantorsEnabled()) {
      Guaranty guarantySecurity = new Guaranty(GuarantyType.GUARANTOR);
      guarantySecurity.setAmount(loanAccount.getLoanAmount());
      guarantySecurity.setGuarantorKey(demoClient.getEncodedKey());
      guarantySecurity.setGuarantorType(demoClient.getAccountHolderType());
      guarantees.add(guarantySecurity);
    }
    if (demoProduct.isCollateralEnabled()) {
      Guaranty guarantyAsset = new Guaranty(GuarantyType.ASSET);
      guarantyAsset.setAssetName("Asset Name as a collateral");
      guarantyAsset.setAmount(loanAccount.getLoanAmount());
      guarantees.add(guarantyAsset);
    }
    loanAccount.setGuarantees(guarantees);
    List<InvestorFund> funds = new ArrayList<>();
    if (demoProduct.isFundingSourceEnabled()) {
      String savingsAccountKey = null;
      try {
        SavingsService savingsService = MambuAPIFactory.getSavingsService();
        List<SavingsAccount> clientSavings = savingsService.getSavingsAccountsForClient(demoClient.getId());
        OrganizationService organizationService = MambuAPIFactory.getOrganizationService();
        Currency baseCurrency = organizationService.getCurrency();
        if (clientSavings != null) {
          for (SavingsAccount account : clientSavings) {
            String accountCurrencyCode = account.getCurrencyCode();
            if (account.getAccountType() == SavingsType.INVESTOR_ACCOUNT && baseCurrency.getCode().equals(accountCurrencyCode) && account.getAccountState() == AccountState.ACTIVE && account.getBalance() != null && account.getBalance().isPositive()) {
              savingsAccountKey = account.getEncodedKey();
              break;
            }
          }
        }
      } catch (MambuApiException e) {
        DemoUtil.logException(methodName, e);
      }
      if (savingsAccountKey != null) {
        InvestorFund investor = new InvestorFund();
        investor.setAmount(loanAccount.getLoanAmount());
        investor.setSavingsAccountKey(savingsAccountKey);
        investor.setGuarantorKey(demoClient.getEncodedKey());
        investor.setGuarantorType(demoClient.getAccountHolderType());
        setFunderInterestCommission(demoProduct, investor);
        funds.add(investor);
        loanAccount.getDisbursementDetails().setExpectedDisbursementDate(null);
      } else {
        System.out.println("WARNING:cannot find Savings Funding account: add applicable INVESTOR_ACCOUNT accounts");
      }
      setOrganizationInterestCommission(demoProduct, loanAccount);
    }
    loanAccount.setFunds(funds);
    Date firstRepaymentDate = makeFirstRepaymentDate(loanAccount, demoProduct, false);
    disbursementDetails.setFirstRepaymentDate(firstRepaymentDate);
    TransactionDetails transactionDetails = DemoUtil.makeDemoTransactionDetails();
    if (!productType.equals(LoanProductType.TRANCHED_LOAN)) {
      disbursementDetails.setTransactionDetails(transactionDetails);
    }
    String method = transactionDetails.getTransactionChannelKey();
    disbursementDetails.setCustomFieldValues(DemoUtil.makeForEntityCustomFieldValues(CustomFieldType.TRANSACTION_CHANNEL_INFO, method, false));
    if (!productType.equals(LoanProductType.TRANCHED_LOAN)) {
      List<CustomPredefinedFee> customFees = DemoUtil.makeDemoPredefinedFees(demoProduct, new HashSet<>(Collections.singletonList(FeeCategory.DISBURSEMENT)));
      disbursementDetails.setFees(customFees);
    }
    if (productType == LoanProductType.REVOLVING_CREDIT) {
      loanAccount.setDisbursementDetails(null);
    }
    Integer arrearsTolerancePeriod = null;
    ProductArrearsSettings arrearsSettings = demoProduct.getArrearsSettings();
    if (arrearsSettings != null) {
      arrearsTolerancePeriod = DemoUtil.getValueMatchingConstraints(arrearsSettings.getDefaultTolerancePeriod(), arrearsSettings.getMinTolerancePeriod(), arrearsSettings.getMaxTolerancePeriod(), 0);
    }
    loanAccount.setArrearsTolerancePeriod(arrearsTolerancePeriod);
    loanAccount.setNotes("Created by DemoTest on " + new Date());
    return loanAccount;
  }

  public static void testGetDocuments() throws MambuApiException {
    System.out.println(methodName = "\nIn testGetDocuments");
    LoanAccount account = DemoUtil.getDemoLoanAccount();
    String accountId = account.getId();
    Integer offset = 0;
    Integer limit = 5;
    DocumentsService documentsService = MambuAPIFactory.getDocumentsService();
    List<Document> documents = documentsService.getDocuments(MambuEntityType.LOAN_ACCOUNT, accountId, offset, limit);
    System.out.println("Documents returned for a Loan Account with ID=" + accountId);
    DemoTestDocumentsService.logDocuments(documents);
  }

  public static void testUpdateDeleteCustomFields() throws MambuApiException {
    System.out.println(methodName = "\nIn testUpdateDeleteCustomFields");
    DemoEntityParams demoEntityParams = new DemoEntityParams(newAccount.getName(), newAccount.getEncodedKey(), newAccount.getId(), newAccount.getProductTypeKey());
    DemoTestCustomFieldValueService.testUpdateAddDeleteEntityCustomFields(MambuEntityType.LOAN_ACCOUNT, demoEntityParams);
  }

  public static void deleteTestAPILoanAccounts() throws MambuApiException {
    System.out.println(methodName = "\nIn deleteTestAPILoanAccounts");
    System.out.println("**  Deleting all Test Loan Accounts for Client =" + demoClient.getFullNameWithId() + " **");
    LoansService loanService = MambuAPIFactory.getLoanService();
    List<LoanAccount> accounts = loanService.getLoanAccountsForClient(demoClient.getId());
    List<LoanAccount> groupAccounts = loanService.getLoanAccountsForGroup(demoGroup.getId());
    accounts.addAll(groupAccounts);
    if (accounts.size() == 0) {
      System.out.println("Nothing to delete for client " + demoClient.getFullNameWithId() + " and Group" + demoGroup.getName() + "-" + demoGroup.getId());
      return;
    }
    for (LoanAccount account : accounts) {
      String name = account.getLoanName();
      String id = account.getId();
      if (id.startsWith(apiTestIdPrefix)) {
        AccountState state = account.getAccountState();
        if (state == AccountState.PARTIAL_APPLICATION || state == AccountState.PENDING_APPROVAL || state == AccountState.APPROVED || state == AccountState.ACTIVE || state == AccountState.ACTIVE_IN_ARREARS) {
          System.out.println("Deleting loan account " + name + " ID=" + id);
          try {
            loanService.undoDisburseLoanAccount(id, "Undo by API deletion");
          } catch (MambuApiException e) {
            System.out.println("Account " + id + " is NOT un-disbursed. Exception=" + e.getMessage());
          }
          try {
            boolean deleted = loanService.deleteLoanAccount(id);
            System.out.println("Account " + id + " DELETED. Status=" + deleted);
          } catch (MambuApiException e) {
            System.out.println("Account " + id + " is NOT deleted. Exception=" + e.getMessage());
            continue;
          }
        }
      }
    }
  }

  /**
	 * Make test first repayment date consistent with product settings. Create first repayment date considering its
	 * ScheduleDueDatesMethod and product restrictions, like a definition for a minimum allowed first repayments date
	 * offset or the fixed days repayments.
	 * 
	 * See MBU-9730 -As a Credit Officer, I want to have an offset applied to the first repayment date
	 * 
	 * @param account
	 *            loan account
	 * @param product
	 *            loan product
	 * @param isLocalMidnight
	 *            true if setting first repayment date to be local midnight. return as UTC midnight date otherwise
	 * @return first repayment date
	 */
  private static Date makeFirstRepaymentDate(LoanAccount account, LoanProduct product, boolean isLocalMidnight) {
    if (account == null || product == null) {
      return null;
    }
    DisbursementDetails disbursementDetails = account.getDisbursementDetails();
    Date disbDate = disbursementDetails != null ? disbursementDetails.getExpectedDisbursementDate() : null;
    if (disbDate == null) {
      disbDate = new Date();
    }
    long aDay = 24 * 60 * 60 * 1000L;
    Date firstRepaymentDate = new Date(disbDate.getTime() + 4 * aDay);
    ScheduleDueDatesMethod scheduleDueDatesMethod = product.getScheduleDueDatesMethod();
    System.out.println("ScheduleDueDatesMethod=" + scheduleDueDatesMethod);
    if (scheduleDueDatesMethod == null) {
      return firstRepaymentDate;
    }
    switch (scheduleDueDatesMethod) {
      case FIXED_DAYS_OF_MONTH:
      List<Integer> fixedDays = account.getFixedDaysOfMonth();
      firstRepaymentDate = makeFixedDateFirstRepayment(fixedDays, isLocalMidnight);
      return firstRepaymentDate;
      case INTERVAL:
      Integer repaymentPeriodCount = account.getRepaymentPeriodCount();
      RepaymentPeriodUnit unit = account.getRepaymentPeriodUnit();
      if (unit == null || repaymentPeriodCount == null) {
        return null;
      }
      Integer minOffsetDays = product.getMinFirstRepaymentDueDateOffset();
      Integer maxOffsetDays = product.getMaxFirstRepaymentDueDateOffset();
      System.out.println("INTERVAL schedule due dates product. Min offset=" + minOffsetDays + " RepaymentPeriodUnit=" + unit + " repaymentPeriodCount=" + repaymentPeriodCount);
      if (minOffsetDays == null && maxOffsetDays == null) {
        return firstRepaymentDate;
      }
      minOffsetDays = minOffsetDays != null ? minOffsetDays : maxOffsetDays;
      Calendar disbDateCal = Calendar.getInstance();
      int day = disbDateCal.get(Calendar.DAY_OF_MONTH);
      int year = disbDateCal.get(Calendar.YEAR);
      int month = disbDateCal.get(Calendar.MONTH);
      disbDateCal.clear();
      disbDateCal.setTimeZone(TimeZone.getTimeZone("UTC"));
      disbDateCal.set(year, month, day);
      Calendar firstRepaymDateCal = Calendar.getInstance();
      firstRepaymDateCal.clear();
      firstRepaymDateCal.setTimeZone(TimeZone.getTimeZone("UTC"));
      firstRepaymDateCal.setTime(disbDateCal.getTime());
      switch (unit) {
        case DAYS:
        firstRepaymDateCal.add(Calendar.DAY_OF_MONTH, repaymentPeriodCount);
        break;
        case MONTHS:
        firstRepaymDateCal.add(Calendar.MONTH, repaymentPeriodCount);
        break;
        case WEEKS:
        firstRepaymDateCal.add(Calendar.DAY_OF_MONTH, (repaymentPeriodCount * 7));
        break;
        case YEARS:
        firstRepaymDateCal.add(Calendar.YEAR, repaymentPeriodCount);
        break;
      }
      firstRepaymDateCal.add(Calendar.DAY_OF_MONTH, 1 + minOffsetDays);
      firstRepaymentDate = firstRepaymDateCal.getTime();
      return firstRepaymentDate;
    }
    System.out.println("FirstRepaymentDate =" + firstRepaymentDate);
    return firstRepaymentDate;
  }

  private static Date makeFixedDateFirstRepayment(List<Integer> fixedDays, boolean isLocalMidnight) {
    Date firstRepaymentDate = null;
    System.out.println("Fixed day product:" + fixedDays);
    if (fixedDays != null && fixedDays.size() > 0) {
      Calendar date = Calendar.getInstance();
      int year = date.get(Calendar.YEAR);
      int month = date.get(Calendar.MONTH);
      date.clear();
      date.setTimeZone(TimeZone.getTimeZone("UTC"));
      int fixedDay = fixedDays.get(fixedDays.size() - 1);
      date.set(year, month + 1, fixedDay);
      firstRepaymentDate = date.getTime();
    }
    if (isLocalMidnight) {
      firstRepaymentDate = new Date(firstRepaymentDate.getTime() - TimeZone.getDefault().getOffset(firstRepaymentDate.getTime()));
    }
    return firstRepaymentDate;
  }

  private static void logDisbursementDetails(DisbursementDetails disbDetails) {
    if (disbDetails == null) {
      return;
    }
    System.out.println("DisbursementDetails:" + "\tExpected DisbursementDate=" + disbDetails.getExpectedDisbursementDate() + "\tFirstRepaymentDate=" + disbDetails.getFirstRepaymentDate() + "\tDisbursementDate:" + disbDetails.getDisbursementDate() + "\tEntityName:" + disbDetails.getEntityName() + "\tEntityType:" + disbDetails.getEntityType());
    TransactionDetails transactionDetails = disbDetails.getTransactionDetails();
    String channelKey = transactionDetails != null ? transactionDetails.getTransactionChannelKey() : null;
    System.out.println("\tChannel: Key=" + channelKey);
    List<CustomFieldValue> transactionFields = disbDetails.getCustomFieldValues();
    if (transactionFields != null) {
      System.out.println("Total Transaction Fields= " + transactionFields.size());
      DemoUtil.logCustomFieldValues(transactionFields, "Channel", channelKey);
    } else {
      System.out.println("\tNull transaction custom fields");
    }
    List<CustomPredefinedFee> dibsursementFees = disbDetails.getFees();
    if (dibsursementFees != null) {
      System.out.println("\nDisbursement Fees=" + dibsursementFees.size());
      for (CustomPredefinedFee customFee : dibsursementFees) {
        System.out.println("\tAmount=" + customFee.getAmount());
        PredefinedFee fee = customFee.getFee();
        if (fee == null) {
          continue;
        }
        System.out.println("\tPredefinedFee=" + fee.getEncodedKey() + "\tAmount=" + fee.getAmount());
      }
    }
  }

  /**
	 * Log Loan product ProductSecuritySettings details
	 * 
	 * @param settings
	 *            Product Security Settings
	 */
  private static void logProductSecuritySettings(ProductSecuritySettings settings) {
    if (settings == null) {
      System.out.println("\tNULL ProductSecuritySettings");
      return;
    }
    System.out.println("\tEnabled: InvestorFunds=" + settings.isInvestorFundsEnabled() + "\tGuarantors=" + settings.isGuarantorsEnabled() + "\tCollateral=" + settings.isCollateralEnabled());
    System.out.println("\tRequired Guarantees:" + settings.getRequiredGuaranties() + "\tRequired Funds=" + settings.getRequiredInvestorFunds());
    DecimalIntervalConstraints organizationLimits = settings.getOrganizationInterestCommission();
    if (organizationLimits != null) {
      System.out.println("\tOrganization Commission:\tDefault=" + organizationLimits.getDefaultValue() + "\tMin=" + organizationLimits.getMinValue() + "\tMax=" + organizationLimits.getMaxValue());
    }
    System.out.println("\tFinder Commission Type=" + settings.getFunderInterestCommissionAllocationType());
    DecimalIntervalConstraints funderLimits = settings.getOrganizationInterestCommission();
    if (funderLimits != null) {
      System.out.println("\tFinder Commission:\tDefault=" + funderLimits.getDefaultValue() + "\tMin=" + funderLimits.getMinValue() + "\tMax=" + funderLimits.getMaxValue());
    }
  }

  /**
	 * Helper to set Organization Interest Rate Commission field for loan account according to the product settings
	 * 
	 * @param product
	 *            loan product for the loan account
	 * @param account
	 *            loan account in which to set the commission rate
	 */
  private static void setOrganizationInterestCommission(LoanProduct product, LoanAccount account) {
    if (account == null || product == null || product.getProductSecuritySettings() == null) {
      return;
    }
    ProductSecuritySettings settings = product.getProductSecuritySettings();
    DecimalIntervalConstraints organizationLimits = settings.getOrganizationInterestCommission();
    if (organizationLimits != null) {
      BigDecimal orgInterestCommission = DemoUtil.getValueMatchingConstraints(organizationLimits, new BigDecimal(0.1f));
      BigDecimal interestRate = account.getInterestRate();
      if (interestRate != null && interestRate.compareTo(orgInterestCommission) <= 0) {
        orgInterestCommission = interestRate.subtract(new BigDecimal(0.1f));
      }
      account.setInterestCommission(orgInterestCommission);
    }
  }

  /**
	 * Clear Interest Rate Fields for Loan Accounts with Funding Sources enabled and with FIXED_INTEREST_COMMISSIONS
	 * FunderInterestCommissionAllocationType
	 * 
	 * @param product
	 *            loan product for the loan account
	 * 
	 * @param account
	 *            loan account in which to clear the interest rate fields
	 */
  private static void clearInterestRateFieldsForFunderAccounts(LoanProduct product, LoanAccount account) {
    if (account == null || product == null) {
      return;
    }
    if (product.isFixedFunderInterestCommission()) {
      account.setInterestRate(null);
      account.setInterestRateSource(null);
    }
  }

  /**
	 * Helper to set Funder Interest Rate commission field for loan account according to the product settings
	 * 
	 * @param product
	 *            loan product for the loan account
	 * @param investorFund
	 *            investor Fund for which to set his individual commission rate
	 */
  private static void setFunderInterestCommission(LoanProduct product, InvestorFund investorFund) {
    if (investorFund == null || product == null || product.getProductSecuritySettings() == null) {
      return;
    }
    if (!product.isFixedFunderInterestCommission()) {
      return;
    }
    ProductSecuritySettings settings = product.getProductSecuritySettings();
    DecimalIntervalConstraints funderLimits = settings.getFunderInterestCommission();
    if (funderLimits != null) {
      BigDecimal funderInterestCommission = DemoUtil.getValueMatchingConstraints(funderLimits, new BigDecimal(0.1f));
      investorFund.setInterestCommission(funderInterestCommission);
    }
  }

  private static void testBulkReverseLoanTransactions() throws MambuApiException {
    System.out.println(methodName = "\nIn testBulkReverseLoanTransactions");
    LoanAccount loanAccount = newAccount;
    if (loanAccount == null) {
      System.out.println("WARNING: loan account couldn\'t be created for bulk reverse loan transactions test");
      return;
    }
    List<LoanTransaction> loanTransactions = makeThreeRapaymentTransactionForBulkReverseTest(loanAccount);
    if (loanTransactions.size() < 2) {
      System.out.println("WARNING:Cannot test bulk reversal: there is not enough transactions to test");
      return;
    }
    List<LoanTransaction> loanTransactionsToReverse = loanTransactions.subList(1, 2);
    testReverseLoanAccountTransactions(loanTransactionsToReverse);
  }

  /**
	 * Creates three repayment transaction for the account passed as parameter to this method.
	 * 
	 * @param loanAccount
	 *            The loan account
	 * @return A list containing the created transactions
	 * @throws MambuApiException
	 */
  private static List<LoanTransaction> makeThreeRapaymentTransactionForBulkReverseTest(LoanAccount loanAccount) throws MambuApiException {
    System.out.println(methodName = "\nIn makeThreeRapaymentTransactionForBulkReverseTest");
    List<LoanTransaction> loanTransactions = new ArrayList<>();
    LoansService loanService = MambuAPIFactory.getLoanService();
    LoanAccount account = loanService.getLoanAccountDetails(loanAccount.getId());
    Money totalBalanceOutstanding = account.getTotalBalanceOutstanding();
    if (totalBalanceOutstanding.isMoreThan(new Money(4))) {
      BigDecimal quarter = new BigDecimal(totalBalanceOutstanding.getAmount().divide(new BigDecimal("4")).doubleValue());
      Money repaymentAmount = new Money(quarter.doubleValue());
      repaymentAmount.setScale(2, RoundingMode.HALF_UP);
      Date date = null;
      for (int i = 1; i <= 3; i++) {
        String notes = "Repayment notes from API bulkreverse test transaction no." + i;
        TransactionDetails transactionDetails = DemoUtil.makeDemoTransactionDetails();
        LoanTransaction transaction = loanService.makeLoanRepayment(loanAccount.getId(), repaymentAmount, date, transactionDetails, null, notes);
        loanTransactions.add(transaction);
        System.out.println("Repaid loan account with the " + loanAccount.getId() + " id response=" + transaction.getTransactionId() + "   for amount=" + transaction.getAmount());
      }
    }
    return loanTransactions;
  }

  /**
	 * Updates expected disbursement date and first repayment date
	 * 
	 * @param loanAccount
	 *            The loan account to be updated
	 * @throws MambuApiException
	 */
  private static LoanAccount updateExpectedDisbursementDateAndFirstRepaymentDate(LoanAccount loanAccount) throws MambuApiException {
    LoanAccount updatedLoanAccount = loanAccount;
    if (!demoProduct.getLoanProductType().equals(LoanProductType.REVOLVING_CREDIT)) {
      Calendar currentCalendar = Calendar.getInstance();
      DisbursementDetails disbursementDetails = loanAccount.getDisbursementDetails();
      if (disbursementDetails != null && disbursementDetails.getExpectedDisbursementDate() != null) {
        currentCalendar.setTime(disbursementDetails.getExpectedDisbursementDate());
      }
      updatedLoanAccount.setExpectedDisbursementDate(currentCalendar.getTime());
      if (disbursementDetails != null && disbursementDetails.getFirstRepaymentDate() != null) {
        updatedLoanAccount.setFirstRepaymentDate(disbursementDetails.getFirstRepaymentDate());
      } else {
        currentCalendar.add(Calendar.DAY_OF_MONTH, 1);
        updatedLoanAccount.setFirstRepaymentDate(currentCalendar.getTime());
      }
    }
    return updatedLoanAccount;
  }

  /**
	 * Tests updating a loan in order to set a settlement account on it and then remove the link between the two. It
	 * works only with Loans having Account Linking enabled
	 * 
	 * @throws MambuApiException
	 */
  public static void testAddAndRemoveSetllementAccounts() throws MambuApiException {
    methodName = new Object() { }.getClass().getEnclosingMethod().getName();
    System.out.println(methodName = "\nIn " + methodName);
    LoanAccount loanAccountToBeUpdated = newAccount;
    LoansService loanService = MambuAPIFactory.getLoanService();
    String productTypeKey = loanAccountToBeUpdated.getProductTypeKey();
    System.out.println("Obtaining the product type of the loan account");
    LoanProduct loanProduct = loanService.getLoanProduct(productTypeKey);
    if (!loanProduct.isAccountLinkingEnabled()) {
      System.out.println("WARNING: " + methodName + " PATCH can`t be ran against a loan that doesn`t have account linking enabled");
    } else {
      String linkableSavingAccountEncodedKey = loanProduct.getLinkableSavingsProductKey();
      SavingsService savingService = MambuAPIFactory.getSavingsService();
      SavingsAccount savingsAccount = null;
      if (linkableSavingAccountEncodedKey == null) {
        savingsAccount = DemoUtil.getDemoSavingsAccount();
      } else {
        System.out.println("Obtaining the product type for the saving account that should be created");
        SavingsProduct savingsProduct = savingService.getSavingsProduct(linkableSavingAccountEncodedKey);
        System.out.println("Creating the Savings account to be used for linking...");
        savingsAccount = makeSavingsAccountForLoanWithSettlements(loanAccountToBeUpdated, savingsProduct);
        System.out.println("POSTing the newly created Savings account...");
        savingsAccount = savingService.createSavingsAccount(savingsAccount);
      }
      if (savingsAccount == null) {
        System.out.println("WARNING: The saving account couldn`t be created");
        return;
      }
      System.out.println("Adding the settlement account the loan account...");
      boolean additionSucceeded = loanService.addSettlementAccount(loanAccountToBeUpdated.getEncodedKey(), savingsAccount.getEncodedKey());
      System.out.println("The result of adding settlements is: " + additionSucceeded);
      if (additionSucceeded) {
        testDeleteSettlementAccount(savingsAccount);
      }
    }
  }

  /**
	 * Helper method, builds and returns a simple saving account for the same account holder as the Loan account passed
	 * as parameter to this method. NOTE that it need to be amended in case you want to use it for building more complex
	 * savings accounts (i.e. having custom fields and mandatory fields)
	 * 
	 * @param loanAccount
	 *            The loan account that the deposit will be created for
	 * @param savingsProduct
	 *            The saving product used for building the new saving account
	 * @return A brand new SavingAccount for the same account holder as per the loan passed as parameter to this method
	 *         call
	 */
  private static SavingsAccount makeSavingsAccountForLoanWithSettlements(LoanAccount loanAccount, SavingsProduct savingsProduct) {
    SavingsAccount savingsAccount = new SavingsAccount();
    savingsAccount.setInterestSettings(new InterestAccountSettings());
    savingsAccount.setAccountHolderKey(loanAccount.getAccountHolderKey());
    savingsAccount.setAccountHolderType(loanAccount.getAccountHolderType());
    savingsAccount.setCurrencyCode(loanAccount.getCurrencyCode());
    savingsAccount.setProductTypeKey(savingsProduct.getEncodedKey());
    savingsAccount.setAccountType(savingsProduct.getProductType());
    savingsAccount.setAccountState(AccountState.PENDING_APPROVAL);
    savingsAccount.setInterestRate(new BigDecimal(1.50));
    final long time = new Date().getTime();
    savingsAccount.setId(apiTestIdPrefix + time);
    savingsAccount.setNotes("Created by API on " + new Date());
    return savingsAccount;
  }

  /**
	 * Tests deleting the linkage between the loan account and the savings account passed as parameters in a call to
	 * this method.
	 * 
	 * @param savingsAccount
	 *            The saving account used for linkage deletion
	 * @throws MambuApiException
	 */
  public static void testDeleteSettlementAccount(SavingsAccount savingsAccount) throws MambuApiException {
    methodName = new Object() { }.getClass().getEnclosingMethod().getName();
    System.out.println(methodName = "\nIn " + methodName);
    LoanAccount loanAccountToBeUpdated = newAccount;
    LoansService loanService = MambuAPIFactory.getLoanService();
    boolean deleteResult = loanService.deleteSettlementAccount(loanAccountToBeUpdated.getEncodedKey(), savingsAccount.getEncodedKey());
    System.out.println("The delete result is: " + deleteResult);
  }
}