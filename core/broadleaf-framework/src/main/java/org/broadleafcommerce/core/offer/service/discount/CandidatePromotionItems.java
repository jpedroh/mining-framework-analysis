package org.broadleafcommerce.core.offer.service.discount;
import org.broadleafcommerce.core.offer.domain.OfferItemCriteria;
import org.broadleafcommerce.core.offer.domain.OfferPriceData;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableOrderItem;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 
 * @author jfischer
 *
 */
public class CandidatePromotionItems {
  protected HashMap<OfferItemCriteria, List<PromotableOrderItem>> candidateQualifiersMap = new HashMap<OfferItemCriteria, List<PromotableOrderItem>>();

  protected HashMap<OfferItemCriteria, List<PromotableOrderItem>> candidateTargetsMap = new HashMap<OfferItemCriteria, List<PromotableOrderItem>>();

  protected HashMap<OfferPriceData, List<PromotableOrderItem>> candidateFixedTargetsMap = new HashMap<>();

  protected boolean isMatchedQualifier = false;

  protected boolean isMatchedTarget = false;

  public void addQualifier(OfferItemCriteria criteria, PromotableOrderItem item) {
    List<PromotableOrderItem> itemList = candidateQualifiersMap.get(criteria);
    if (itemList == null) {
      itemList = new ArrayList<PromotableOrderItem>();
      candidateQualifiersMap.put(criteria, itemList);
    }
    itemList.add(item);
  }

  public void addTarget(OfferItemCriteria criteria, PromotableOrderItem item) {
    List<PromotableOrderItem> itemList = candidateTargetsMap.get(criteria);
    if (itemList == null) {
      itemList = new ArrayList<PromotableOrderItem>();
      candidateTargetsMap.put(criteria, itemList);
    }
    itemList.add(item);
  }

  public void addFixedTarget(OfferPriceData offerPriceData, PromotableOrderItem item) {
    List<PromotableOrderItem> itemList = candidateFixedTargetsMap.get(offerPriceData);
    if (itemList == null) {
      itemList = new ArrayList<PromotableOrderItem>();
      candidateFixedTargetsMap.put(offerPriceData, itemList);
    }
    itemList.add(item);
  }

  public boolean isMatchedQualifier() {
    return isMatchedQualifier;
  }

  public void setMatchedQualifier(boolean isMatchedCandidate) {
    this.isMatchedQualifier = isMatchedCandidate;
  }

  public HashMap<OfferItemCriteria, List<PromotableOrderItem>> getCandidateQualifiersMap() {
    return candidateQualifiersMap;
  }

  public HashMap<OfferItemCriteria, List<PromotableOrderItem>> getCandidateTargetsMap() {
    return candidateTargetsMap;
  }

  public HashMap<OfferPriceData, List<PromotableOrderItem>> getCandidateFixedTargetsMap() {
    return candidateFixedTargetsMap;
  }

  public boolean isMatchedTarget() {
    return isMatchedTarget;
  }

  public void setMatchedTarget(boolean isMatchedCandidate) {
    this.isMatchedTarget = isMatchedCandidate;
  }

  public Set<PromotableOrderItem> getAllCandidateTargets() {
    Set<PromotableOrderItem> promotableOrderItemSet = new HashSet<PromotableOrderItem>();
    for (List<PromotableOrderItem> orderItems : getCandidateTargetsMap().values()) {
      promotableOrderItemSet.addAll(orderItems);
    }
    for (List<PromotableOrderItem> orderItems : getCandidateFixedTargetsMap().values()) {
      promotableOrderItemSet.addAll(orderItems);
    }
    return promotableOrderItemSet;
  }
}