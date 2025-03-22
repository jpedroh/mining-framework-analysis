package org.broadleafcommerce.profile.core.service;
import org.apache.commons.lang.StringUtils;
import org.broadleafcommerce.common.config.domain.ModuleConfiguration;
import org.broadleafcommerce.common.config.service.ModuleConfigurationService;
import org.broadleafcommerce.common.config.service.type.ModuleConfigurationType;
import org.broadleafcommerce.common.util.TransactionUtils;
import org.broadleafcommerce.profile.core.dao.AddressDao;
import org.broadleafcommerce.profile.core.domain.Address;
import org.broadleafcommerce.profile.core.domain.CountrySubdivision;
import org.broadleafcommerce.profile.core.domain.Phone;
import org.broadleafcommerce.profile.core.service.exception.AddressVerificationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;

@Service(value = "blAddressService") public class AddressServiceImpl implements AddressService {
  protected boolean mustValidateAddresses = false;

  @Resource(name = "blAddressDao") protected AddressDao addressDao;

  @Resource(name = "blModuleConfigurationService") protected ModuleConfigurationService moduleConfigService;

  @Resource(name = "blAddressVerificationProviders") protected List<AddressVerificationProvider> providers;

  @Resource(name = "blPhoneService") protected PhoneService phoneService;

  @Resource(name = "blCountrySubdivisionService") protected CountrySubdivisionService countrySubdivisionService;

  @Override @Transactional(value = TransactionUtils.DEFAULT_TRANSACTION_MANAGER) public Address saveAddress(Address address) {
    return addressDao.save(address);
  }

  @Override public Address readAddressById(Long addressId) {
    return addressDao.readAddressById(addressId);
  }

  @Override @Transactional(value = TransactionUtils.DEFAULT_TRANSACTION_MANAGER) public Address create() {
    return addressDao.create();
  }

  @Override @Transactional(value = TransactionUtils.DEFAULT_TRANSACTION_MANAGER) public void delete(Address address) {
    addressDao.delete(address);
  }

  @Override public List<Address> verifyAddress(Address address) throws AddressVerificationException {
    if (address.getStandardized() != null && Boolean.TRUE.equals(address.getStandardized())) {
      ArrayList<Address> out = new ArrayList<Address>();
      out.add(address);
      return out;
    }
    if (providers != null && !providers.isEmpty()) {
      List<ModuleConfiguration> moduleConfigs = moduleConfigService.findActiveConfigurationsByType(ModuleConfigurationType.ADDRESS_VERIFICATION);
      if (moduleConfigs != null && !moduleConfigs.isEmpty()) {
        ModuleConfiguration config = null;
        for (ModuleConfiguration configuration : moduleConfigs) {
          if (configuration.getIsDefault()) {
            config = configuration;
            break;
          }
        }
        if (config == null) {
          config = moduleConfigs.get(0);
        }
        for (AddressVerificationProvider provider : providers) {
          if (provider.canRespond(config)) {
            return provider.validateAddress(address, config);
          }
        }
      }
    }
    if (mustValidateAddresses) {
      throw new AddressVerificationException("No providers were configured to handle address validation");
    }
    ArrayList<Address> out = new ArrayList<Address>();
    out.add(address);
    return out;
  }

  @Override public Address copyAddress(Address orig) {
    return copyAddress(null, orig);
  }

  @Override public Address copyAddress(Address dest, Address orig) {
    if (dest == null) {
      dest = create();
    }
    if (orig != null) {
      dest.setFullName(orig.getFullName());
      dest.setFirstName(orig.getFirstName());
      dest.setLastName(orig.getLastName());
      dest.setAddressLine1(orig.getAddressLine1());
      dest.setAddressLine2(orig.getAddressLine2());
      dest.setAddressLine3(orig.getAddressLine3());
      dest.setCity(orig.getCity());
      dest.setCounty(orig.getCounty());
      dest.setIsoCountrySubdivision(orig.getIsoCountrySubdivision());
      dest.setStateProvinceRegion(orig.getStateProvinceRegion());
      dest.setPostalCode(orig.getPostalCode());
      dest.setZipFour(orig.getZipFour());
      dest.setIsoCountryAlpha2(orig.getIsoCountryAlpha2());
      dest.setCompanyName(orig.getCompanyName());
      dest.setPrimaryPhone(orig.getPrimaryPhone());
      dest.setSecondaryPhone(orig.getSecondaryPhone());
      dest.setFax(orig.getFax());
      dest.setPhonePrimary(phoneService.copyPhone(dest.getPhonePrimary(), orig.getPhonePrimary()));
      dest.setPhoneSecondary(phoneService.copyPhone(dest.getPhoneSecondary(), orig.getPhoneSecondary()));
      dest.setPhoneFax(phoneService.copyPhone(dest.getPhoneFax(), orig.getPhoneFax()));
      dest.setEmailAddress(orig.getEmailAddress());
      dest.setBusiness(orig.isBusiness());
      dest.setMailing(orig.isMailing());
      dest.setStreet(orig.isStreet());
      return dest;
    }
    return null;
  }

  @Override public void populateAddressISOCountrySub(Address address) {
    if (StringUtils.isBlank(address.getIsoCountrySubdivision()) && address.getIsoCountryAlpha2() != null && StringUtils.isNotBlank(address.getStateProvinceRegion())) {
      String friendlyStateProvRegion = address.getStateProvinceRegion();
      CountrySubdivision isoCountrySub = countrySubdivisionService.findSubdivisionByCountryAndAltAbbreviation(address.getIsoCountryAlpha2().getAlpha2(), friendlyStateProvRegion);
      if (isoCountrySub == null) {
        isoCountrySub = countrySubdivisionService.findSubdivisionByCountryAndName(address.getIsoCountryAlpha2().getAlpha2(), friendlyStateProvRegion);
      }
      if (isoCountrySub != null) {
        address.setIsoCountrySubdivision(isoCountrySub.getAbbreviation());
      }
    }
  }

  /**
     * Default is false. If set to true, the verifyAddress method will throw an exception if there are no providers to handle the request.
     * @param mustValidateAddresses
     */
  public void setMustValidateAddresses(boolean mustValidateAddresses) {
    this.mustValidateAddresses = mustValidateAddresses;
  }
}