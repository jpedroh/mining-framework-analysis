package com.ning.billing.recurly.model;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.joda.time.DateTime;
import com.fasterxml.jackson.annotation.JsonIgnore;

@XmlRootElement(name = "coupon") public class Coupon extends RecurlyObject {
  @XmlTransient public static final String COUPON_RESOURCE = "/coupons";

  @XmlElement(name = "name") private String name;

  @XmlElement(name = "coupon_code") private String couponCode;

  @XmlElement(name = "discount_type") private String discountType;

  @XmlElement(name = "discount_percent") private Integer discountPercent;

  public String getName() {
    return name;
  }

  public void setName(final Object name) {
    this.name = stringOrNull(name);
  }

  public String getCouponCode() {
    return couponCode;
  }

  public void setCouponCode(final Object couponCode) {
    this.couponCode = stringOrNull(couponCode);
  }

  public void setDiscountType(final Object discountType) {
    this.discountType = stringOrNull(discountType);
  }

  public String getDiscountType() {
    return discountType;
  }

  public Integer getDiscountPercent() {
    return discountPercent;
  }

  public void setDiscountPercent(final Object discountPercent) {
    this.discountPercent = integerOrNull(discountPercent);
  }

  @Override public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append("Coupon");
    sb.append("{name=\'").append(name).append('\'');
    sb.append(", couponCode=\'").append(couponCode).append('\'');
    sb.append(", discountType=\'").append(discountType).append('\'');
    sb.append(", discountPercent=\'").append(discountPercent).append('\'');
    sb.append('}');
    return sb.toString();
  }

  @Override public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    final Coupon coupon = (Coupon) o;
    if (couponCode != null ? !couponCode.equals(coupon.couponCode) : coupon.couponCode != null) {
      return false;
    }
    if (discountPercent != null ? !discountPercent.equals(coupon.discountPercent) : coupon.discountPercent != null) {
      return false;
    }
    if (discountType != null ? !discountType.equals(coupon.discountType) : coupon.discountType != null) {
      return false;
    }
    if (name != null ? !name.equals(coupon.name) : coupon.name != null) {
      return false;
    }
    return true;
  }

  @Override public int hashCode() {
    int result = name != null ? name.hashCode() : 0;
    result = 31 * result + (couponCode != null ? couponCode.hashCode() : 0);
    result = 31 * result + (discountType != null ? discountType.hashCode() : 0);
    result = 31 * result + (discountPercent != null ? discountPercent.hashCode() : 0);
    return result;
  }

  @XmlTransient private String href;

  @XmlElement(name = "redeem_by_date") private DateTime redeemByDate;

  @XmlElement(name = "applies_for_months") private Integer appliesForMonths;

  @XmlElement(name = "max_redemptions") private Integer maxRedemptions;

  @XmlElement(name = "applies_to_all_plans") private Boolean appliesToAllPlans;

  @XmlElement(name = "single_use") private Boolean singleUse;

  @XmlElement(name = "discount_in_cents") private Integer discountInCents;

  @XmlElement(name = "state") private String state;

  @JsonIgnore public String getHref() {
    return href;
  }

  public void setHref(final Object href) {
    this.href = stringOrNull(href);
  }

  public String getState() {
    return state;
  }

  public void setState(final Object state) {
    this.state = stringOrNull(state);
  }

  public DateTime getRedeemByDate() {
    return redeemByDate;
  }

  public void setRedeemByDate(final Object redeemByDate) {
    this.redeemByDate = dateTimeOrNull(redeemByDate);
  }

  public Integer getAppliesForMonths() {
    return appliesForMonths;
  }

  public void setAppliesForMonths(final Object appliesForMonths) {
    this.appliesForMonths = integerOrNull(appliesForMonths);
  }

  public Integer getMaxRedemptions() {
    return maxRedemptions;
  }

  public void setMaxRedemptions(final Object maxRedemptions) {
    this.maxRedemptions = integerOrNull(maxRedemptions);
  }

  public Boolean getSingleUse() {
    return singleUse;
  }

  public void setSingleUse(final Object singleUse) {
    this.singleUse = booleanOrNull(singleUse);
  }

  public Integer getDiscountInCents() {
    return discountInCents;
  }

  public void setDiscountInCents(final Object discountInCents) {
    this.discountInCents = integerOrNull(discountInCents);
  }

  public Boolean getAppliesToAllPlans() {
    return appliesToAllPlans;
  }

  public void setAppliesToAllPlans(final Object appliesToAllPlans) {
    this.appliesToAllPlans = booleanOrNull(appliesToAllPlans);
  }
}