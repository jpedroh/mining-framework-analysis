package com.mambu.apisdk.util;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.mambu.accounts.shared.model.Account;
import com.mambu.accounts.shared.model.AccountState;
import com.mambu.accounts.shared.model.PredefinedFee;
import com.mambu.accounts.shared.model.TransactionDetails;
import com.mambu.api.server.handler.documents.model.JSONDocument;
import com.mambu.api.server.handler.loan.model.JSONApplyManualFee;
import com.mambu.api.server.handler.loan.model.JSONFeeRequest;
import com.mambu.api.server.handler.loan.model.JSONLoanAccount;
import com.mambu.api.server.handler.loan.model.JSONTransactionRequest;
import com.mambu.api.server.handler.savings.model.JSONSavingsAccount;
import com.mambu.apisdk.MambuAPIFactory;
import com.mambu.clients.shared.model.ClientExpanded;
import com.mambu.clients.shared.model.GroupExpanded;
import com.mambu.core.shared.model.CustomFieldValue;
import com.mambu.core.shared.model.Money;
import com.mambu.loans.shared.model.CustomPredefinedFee;
import com.mambu.loans.shared.model.DisbursementDetails;
import com.mambu.loans.shared.model.LoanAccount;
import com.mambu.savings.shared.model.SavingsAccount;

/**
 * ServiceHelper class provides helper methods for validating and building parameters and API definitions required for
 * executing API requests common across different services. For example, there might be common operations required to
 * build transaction parameters for both LoanService and SavingsService, or for API requests which are supported by
 * multiple services (such as getting a list of entities for a Custom View, updating custom field value for an entity,
 * etc.)
 * 
 * @author mdanilkis
 * 
 */
public class ServiceHelper {
  /**
	 * Create JSONTransactionRequest for submitting JSON transaction API requests
	 * 
	 * @param amount
	 *            transaction amount
	 * @param backDate
	 *            transaction back date
	 * @param firstRepaymentDate
	 *            first repayment date
	 * @param transactionDetails
	 *            transaction details
	 * @param transactionFees
	 *            transaction fees
	 * @param customInformation
	 *            transaction custom fields
	 * @param notes
	 *            transaction notes
	 * @return JSON Transaction Request
	 */
  public static JSONTransactionRequest makeJSONTransactionRequest(Money amount, Date backDate, Date firstRepaymentDate, TransactionDetails transactionDetails, List<CustomPredefinedFee> transactionFees, List<CustomFieldValue> customInformation, String notes) {
    JSONTransactionRequest request = new JSONTransactionRequest();
    BigDecimal bigDecimalAmount = amount == null ? null : amount.getAmount();
    request.setAmount(bigDecimalAmount);
    request.setNotes(notes);
    request.setDate(backDate);
    request.setFirstRepaymentDate(firstRepaymentDate);
    request.setCustomInformation(customInformation);
    request.setTransactionDetails(transactionDetails);
    String channelKey = transactionDetails != null ? transactionDetails.getTransactionChannelKey() : null;
    request.setMethod(channelKey);
    setTransactionFees(request, transactionFees);
    return request;
  }

  /**
	 * Convenience method to create JSONTransactionRequest specifying disbursement details
	 * 
	 * @param amount
	 *            transaction amount
	 * @param disbursementDetails
	 *            disbursement details
	 * @param customInformation
	 *            transaction custom fields
	 * @param notes
	 *            transaction notes
	 * @return JSON Transaction Request
	 */
  public static JSONTransactionRequest makeJSONTransactionRequest(Money amount, DisbursementDetails disbursementDetails, List<CustomFieldValue> customInformation, String notes) {
    Date backDate = null;
    Date firstRepaymentDate = null;
    List<CustomPredefinedFee> disbursementFees = null;
    TransactionDetails transactionDetails = null;
    if (disbursementDetails != null) {
      backDate = disbursementDetails.getExpectedDisbursementDate();
      firstRepaymentDate = disbursementDetails.getFirstRepaymentDate();
      transactionDetails = disbursementDetails.getTransactionDetails();
      disbursementFees = disbursementDetails.getFees();
    }
    return makeJSONTransactionRequest(amount, backDate, firstRepaymentDate, transactionDetails, disbursementFees, customInformation, notes);
  }

  /**
	 * Create JSON Transaction request for submitting applying predefined fee and specifying repayment number
	 * 
	 * Available since Mambu 4.1. See MBU-12271, MBU-12272 (loan fees), MBU-12273 (savings fees)
	 * 
	 * @param transactionFees
	 *            transaction fees
	 * @param repaymentNumber
	 *            repayment number. Applicable only for loan transactions. Must be set to null for savings apply fee
	 *            transaction
	 * @param notes
	 *            transaction notes
	 * @return JSON Apply Manual Fee Request
	 */
  public static JSONApplyManualFee makeJSONApplyManualFeeRequest(List<CustomPredefinedFee> transactionFees, Integer repaymentNumber, String notes) {
    JSONApplyManualFee request = new JSONApplyManualFee();
    setTransactionFees(request, transactionFees);
    request.setRepayment(repaymentNumber);
    request.setNotes(notes);
    return request;
  }

  /**
	 * Helper to add custom predefined fees to a JSON Transaction Request. CustomPredefinedFees must be converted into
	 * JSONFeeRequest format when POSTing JSONTransactionRequest
	 * 
	 * @param request
	 *            JSON Transaction Request
	 * @param transactionFees
	 *            transaction fees to be added to the request
	 */
  private static void setTransactionFees(JSONTransactionRequest request, List<CustomPredefinedFee> transactionFees) {
    if (request == null) {
      return;
    }
    request.setPredefinedFeeInfo(null);
    if (transactionFees != null && transactionFees.size() > 0) {
      List<JSONFeeRequest> fees = new ArrayList<>();
      for (CustomPredefinedFee custFee : transactionFees) {
        PredefinedFee predefinedFee = custFee.getFee();
        String feeEncodedKey = predefinedFee != null ? predefinedFee.getEncodedKey() : null;
        JSONFeeRequest jsonFee = new JSONFeeRequest();
        jsonFee.setEncodedKey(feeEncodedKey);
        jsonFee.setAmount(custFee.getAmount());
        fees.add(jsonFee);
      }
      request.setPredefinedFeeInfo(fees);
    }
  }

  /**
	 * Create params map with as JSON request message for a transaction type
	 * 
	 * @param transactionType
	 *            transaction type string. Example: "DISBURSEMENT"
	 * @param transactionRequest
	 *            jSON transaction request. If null then the JSON string with only the transactionType is returned.
	 * @return params map with a JSON_OBJECT included
	 */
  public static ParamsMap makeParamsForTransactionRequest(String transactionType, JSONTransactionRequest transactionRequest) {
    JsonObject jsonObject;
    Gson gson = GsonUtils.createGson();
    if (transactionRequest == null) {
      jsonObject = new JsonObject();
      jsonObject.addProperty(APIData.TYPE, transactionType);
    } else {
      JsonElement transactionJson = gson.toJsonTree(transactionRequest, transactionRequest.getClass());
      jsonObject = transactionJson.getAsJsonObject();
      jsonObject.addProperty(APIData.TYPE, transactionType);
    }
    String jsonRequest = gson.toJson(jsonObject);
    ParamsMap paramsMap = new ParamsMap();
    paramsMap.put(APIData.JSON_OBJECT, jsonRequest);
    return paramsMap;
  }

  /***
	 * Create ParamsMap with a JSON string for the JSONDocument object
	 * 
	 * @param document
	 *            JSONDocument document containing Document object and documentContent string
	 * @return params map with the document JSON string
	 */
  public static ParamsMap makeParamsForDocumentJson(JSONDocument document) {
    JSONDocument copy = new JSONDocument();
    copy.setDocument(document.getDocument());
    copy.setDocumentContent("");
    String jsonData = makeApiJson(copy);
    String applicationKey = MambuAPIFactory.getApplicationKey();
    if (applicationKey != null && applicationKey.length() > 0) {
      jsonData = addAppkeyValueToJson(applicationKey, jsonData);
    }
    final String documentContent = document.getDocumentContent();
    StringBuffer finalJson = new StringBuffer(jsonData.length() + documentContent.length());
    finalJson.append(jsonData);
    final String contentPair = "\"documentContent\":\"\"";
    int insertPosition = finalJson.indexOf(contentPair) + contentPair.length() - 1;
    finalJson.insert(insertPosition, documentContent);
    String documentJson = finalJson.toString();
    ParamsMap paramsMap = new ParamsMap();
    paramsMap.put(APIData.JSON_OBJECT, documentJson);
    return paramsMap;
  }

  /**
	 * Get Base64 encoded content from the API message containing bas64 encoding indicator and base64 encoded content
	 * 
	 * Mambu API returns encoded files in the following format: "data:image/jpg;base64,/9j...." The encoded string is
	 * base64 encoded with CRLFs, E.g. 9j/4AAQSkZJR...\r\nnHBwgJC4nICIsIxwcKDcpL... This methods returns just the
	 * content part without the base64 indicator
	 * 
	 * @param apiResponse
	 *            api response message containing base64 indicator and base64 encoded string
	 * 
	 * @return base64 encoded message content
	 */
  public static String getContentForBase64EncodedMessage(String apiResponse) {
    final String encodingStartsAfter = APIData.BASE64_ENCODING_INDICATOR;
    if (apiResponse == null || !apiResponse.contains(encodingStartsAfter)) {
      return null;
    }
    final int dataStart = apiResponse.indexOf(encodingStartsAfter) + encodingStartsAfter.length();
    String base64EncodedString = apiResponse.substring(dataStart, apiResponse.length() - 1);
    return base64EncodedString;
  }

  /**
	 * Create ParamsMap with a map of fields for the GET loan schedule for the product API. Only fields applicable to
	 * the API are added to the params map
	 * 
	 * @param account
	 *            input loan account
	 * @param apiDefinition
	 *            api definition containing custom serializer for loan account to support generating request only with
	 *            those fields required by the GET schedule API
	 * @return params map with fields for an API request
	 */
  public static ParamsMap makeParamsForLoanSchedule(LoanAccount account, ApiDefinition apiDefinition) {
    if (account == null) {
      throw new IllegalArgumentException("Loan Account must not be null");
    }
    JsonElement object = ServiceHelper.makeApiJsonElement(account, apiDefinition);
    Type type = new TypeToken<ParamsMap>() { }.getType();
    ParamsMap params = GsonUtils.createGson(APIData.yyyyMmddFormat).fromJson(object.getAsJsonObject(), type);
    return params;
  }

  /**
	 * Generate a JSON string for an object using Mambu's default date time format ("yyyy-MM-dd'T'HH:mm:ssZ")
	 * 
	 * @param object
	 *            object
	 * @return JSON string for the object
	 */
  public static <T extends java.lang.Object> String makeApiJson(T object) {
    return GsonUtils.createGson().toJson(object, object.getClass());
  }

  /**
	 * Generate a JSON string for an object and with the specified format for date fields
	 * 
	 * @param object
	 *            object
	 * @param dateTimeFormat
	 *            date time format string. Example: "yyyy-MM-dd". If null then the default date time format is used
	 *            ("yyyy-MM-dd'T'HH:mm:ssZ")
	 * @return JSON string for the object
	 */
  public static <T extends java.lang.Object> String makeApiJson(T object, String dateTimeFormat) {
    return GsonUtils.createGson(dateTimeFormat).toJson(object, object.getClass());
  }

  /**
	 * Generate a JSON string for an object and with the specified ApiDefinition
	 * 
	 * @param object
	 *            object
	 * @param apiDefinition
	 *            API definition
	 * @return JSON string for the object
	 */
  public static <T extends java.lang.Object> String makeApiJson(T object, ApiDefinition apiDefinition) {
    return GsonUtils.createSerializerGson(apiDefinition).toJson(object, object.getClass());
  }

  /**
	 * Generate a JsonElement for an object and with the specified ApiDefinition
	 * 
	 * @param object
	 *            object
	 * @param apiDefinition
	 *            API definition
	 * @return JsonElement for an object
	 */
  public static <T extends java.lang.Object> JsonElement makeApiJsonElement(T object, ApiDefinition apiDefinition) {
    return GsonUtils.createSerializerGson(apiDefinition).toJsonTree(object, object.getClass());
  }

  /**
	 * Convenience helper to make parameters map which contains only pagination parameters: offset and limit
	 * 
	 * @param offset
	 *            pagination offset. If not null it must be an integer greater or equal to zero
	 * @param limit
	 *            pagination limit. If not null the must be an integer greater than zero
	 * @return ParamsMap
	 */
  public static ParamsMap makePaginationParams(String offset, String limit) {
    if (offset == null && limit == null) {
      return null;
    }
    if ((offset != null && Integer.parseInt(offset) < 0) || ((limit != null && Integer.parseInt(limit) < 1))) {
      throw new IllegalArgumentException("Invalid pagination parameters. Offset=" + offset + " Limit=" + limit);
    }
    ParamsMap params = new ParamsMap();
    params.addParam(APIData.OFFSET, offset);
    params.addParam(APIData.LIMIT, limit);
    return params;
  }

  /**
	 * Add appKey value to the json string.
	 * 
	 * @param appKey
	 *            app key value. Can be null
	 * @param jsonString
	 *            json string.
	 * @return json string with the appKey parameter added. If the appKey parameter is already present then the
	 *         jsonString is not modified. See MBU-3892
	 */
  public static String addAppkeyValueToJson(String appKey, String jsonString) {
    if (appKey == null || appKey.length() == 0) {
      return jsonString;
    }
    String appKeyValue = APIData.APPLICATION_KEY + "\":\"" + appKey;
    String appKeyString = "{\"" + appKeyValue + "\",";
    if (jsonString == null || jsonString.length() == 0) {
      return appKeyString.replace(',', '}');
    }
    if (jsonString.contains(appKeyValue)) {
      return jsonString;
    }
    String jsonStringToAdd = jsonString.substring(1);
    StringBuffer jsonWithAppKey = new StringBuffer(jsonStringToAdd.length() + appKeyString.length());
    jsonWithAppKey.append(appKeyString);
    jsonWithAppKey.append(jsonStringToAdd);
    return jsonWithAppKey.toString();
  }

  /**
	 * Get class corresponding to the "full details" class for a Mambu Entity. For example, ClientExpanded.class for
	 * MambuEntityType.CLIENT;
	 * 
	 * @param entityType
	 *            entity type. Must not be null
	 * @return class representing full details class for the Mambu Entity or null if no such class exists
	 */
  public static Class<?> getFullDetailsClass(MambuEntityType entityType) {
    if (entityType == null) {
      throw new IllegalArgumentException("Entity type must not be null");
    }
    switch (entityType) {
      case CLIENT:
      return ClientExpanded.class;
      case GROUP:
      return GroupExpanded.class;
      case LOAN_ACCOUNT:
      return JSONLoanAccount.class;
      case SAVINGS_ACCOUNT:
      return JSONSavingsAccount.class;
      default:
      return null;
    }
  }

  /**
	 * Helper to determine the type of the Undo Closer Transaction for a closed loan account. The transaction type
	 * needed in UNDO closer API transactions. Can be also used to determine ahead of time if the UNDO Closer
	 * transaction can be performed via API for the specified account
	 * 
	 * See MBU-13190. As of Mambu 4.2 the following UNDO closer types are supported "UNDO_REJECT", "UNDO_WITHDRAWN",
	 * "UNDO_CLOSE"
	 * 
	 * @param account
	 *            loan account. Must not be null and its state must not be null.
	 * @return UNDO close transaction type. Return null if account is not closed or if its closer type is not supported
	 *         by Mambu API
	 */
  public static String getUndoCloserTransactionType(LoanAccount account) {
    if (account == null || account.getState() == null) {
      throw new IllegalArgumentException("Account and its state must not be null");
    }
    AccountState accountState = account.getState();
    AccountState accountSubState = account.getSubState();
    switch (accountState) {
      case CLOSED:
      if (accountSubState == null) {
        return APIData.UNDO_CLOSE;
      }
      switch (accountSubState) {
        case WITHDRAWN:
        return APIData.UNDO_WITHDRAWN;
        default:
        return null;
      }
      case CLOSED_REJECTED:
      return APIData.UNDO_REJECT;
      default:
      return null;
    }
  }

  /**
	 * Helper to determine the type of the Undo Closer Transaction for a closed savings account. The transaction type
	 * needed in UNDO closer API transactions. Can be also used to determine ahead of time if the UNDO Closer
	 * transaction can be performed via API for the specified account
	 * 
	 * See MBU-13193. As of Mambu 4.2 the following UNDO closer types are supported "UNDO_REJECT", "UNDO_WITHDRAWN",
	 * "UNDO_CLOSE"
	 * 
	 * @param account
	 *            savings account. Must not be null and its state must not be null.
	 * @return UNDO close transaction type. Return null if account is not closed or if its closer type is not supported
	 *         by Mambu API
	 */
  public static String getUndoCloserTransactionType(SavingsAccount account) {
    if (account == null || account.getAccountState() == null) {
      throw new IllegalArgumentException("Account and its state must not be null");
    }
    AccountState accountState = account.getAccountState();
    switch (accountState) {
      case CLOSED:
      return APIData.UNDO_CLOSE;
      case WITHDRAWN:
      return APIData.UNDO_WITHDRAWN;
      case CLOSED_REJECTED:
      return APIData.UNDO_REJECT;
      default:
      return null;
    }
  }

  /**
	 * Gets the encodedKey or the ID from the account passed as parameter in a call to this method.
	 * 
	 * @param account
	 *            The account used to obtain the ID or the encoded key from.
	 * @return a String key representing the encodedKey or the ID or null if both are null.
	 */
  public static String getKeyForAccount(Account account) {
    if (account == null) {
      throw new IllegalArgumentException("Account must not be NULL");
    }
    String encodedKey = account.getEncodedKey();
    String accountId = account.getId();
    return encodedKey != null ? encodedKey : accountId;
  }
}