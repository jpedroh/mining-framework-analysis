package com.premiumminds.billy.core.persistence.entities;
import java.util.Date;
import com.premiumminds.billy.core.services.entities.ShippingPoint;

public interface ShippingPointEntity extends ShippingPoint, BaseEntity {
  public void setDeliveryId(String deliveryId);

  public void setDate(Date date);

  public void setWarehouseId(String id);

  public void setLocationId(String id);

  public void setUCR(String UCR);

  public <T extends AddressEntity> void setAddress(T address);
}