package org.broadleafcommerce.profile.web.core.service;
import org.broadleafcommerce.common.i18n.domain.ISOCountry;
import org.broadleafcommerce.profile.core.domain.Address;
import org.broadleafcommerce.profile.core.domain.AddressImpl;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.core.domain.CustomerAddress;
import org.broadleafcommerce.profile.core.domain.CustomerAddressImpl;
import org.broadleafcommerce.profile.core.service.CustomerAddressService;
import org.broadleafcommerce.profile.dataprovider.CustomerAddressDataProvider;
import org.broadleafcommerce.test.CommonSetupBaseTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.Test;
import java.util.List;
import javax.annotation.Resource;

public class CustomerAddressTest extends CommonSetupBaseTest {
  private String userName;

  private Long userId;

  @Resource private CustomerAddressService customerAddressService;

  @Test(groups = "testCustomerAddress") @Transactional public void readCustomerAddresses() {
    Customer customer = createCustomerWithAddresses();
    List<CustomerAddress> customerAddressList = customerAddressService.readActiveCustomerAddressesByCustomerId(customer.getId());
    for (CustomerAddress ca : customerAddressList) {
      assert ca != null;
    }
  }

  @Test(groups = "testCustomerAddress") public void createNewDefaultAddress() {
    Customer customer = createCustomerWithAddresses();
    CustomerAddress ca = new CustomerAddressImpl();
    Address address = new AddressImpl();
    address.setAddressLine1("123 Main");
    address.setCity("Dallas");
    address.setPostalCode("75201");
    address.setDefault(true);
    ca.setAddress(address);
    ca.setCustomer(customer);
    ca.setAddressName("address3");
    CustomerAddress savedAddress = customerAddressService.saveCustomerAddress(ca);
    List<CustomerAddress> customerAddressList = customerAddressService.readActiveCustomerAddressesByCustomerId(customer.getId());
    for (CustomerAddress customerAddress : customerAddressList) {
      if (customerAddress.getId().equals(savedAddress.getId())) {
        assert customerAddress.getAddress().isDefault();
      } else {
        assert !customerAddress.getAddress().isDefault();
      }
    }
  }

  /**
     * This method only exists because so many other tests depend on it, but should be removed once tests are more isolated
     * @param customerAddress
     */
  @Deprecated @Test(groups = "createCustomerAddress", dataProvider = "setupCustomerAddress", dataProviderClass = CustomerAddressDataProvider.class, dependsOnGroups = { "readCustomer", "createCountry" }) @Transactional @Rollback(value = false) public void createCustomerAddress(CustomerAddress customerAddress) {
    userName = "customer1";
    Customer customer = customerService.readCustomerByUsername(userName);
    assert customerAddress.getId() == null;
    customerAddress.setCustomer(customer);
    customerAddress.getAddress().setIsoCountrySubdivision("US-KY");
    ISOCountry isoCountry = isoService.findISOCountryByAlpha2Code("US");
    customerAddress.getAddress().setIsoCountryAlpha2(isoCountry);
    customerAddress = customerAddressService.saveCustomerAddress(customerAddress);
    assert customer.equals(customerAddress.getCustomer());
    userId = customerAddress.getCustomer().getId();
  }

  /**
     * TThis method only exists because so many other tests depend on it, but should be removed once tests are more isolated
     */
  @Deprecated @Test(groups = "readCustomerAddress", dependsOnGroups = "createCustomerAddress") @Transactional public void readCustomerAddressByUserId() {
    List<CustomerAddress> customerAddressList = customerAddressService.readActiveCustomerAddressesByCustomerId(userId);
    for (CustomerAddress customerAddress : customerAddressList) {
      assert customerAddress != null;
    }
  }
}