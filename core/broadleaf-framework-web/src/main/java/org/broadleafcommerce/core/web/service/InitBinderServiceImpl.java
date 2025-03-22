package org.broadleafcommerce.core.web.service;
import org.apache.commons.lang.StringUtils;
import org.broadleafcommerce.common.i18n.domain.ISOCountry;
import org.broadleafcommerce.common.i18n.service.ISOService;
import org.broadleafcommerce.profile.core.domain.Phone;
import org.broadleafcommerce.profile.core.domain.PhoneImpl;
import org.broadleafcommerce.profile.core.service.CountryService;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.ServletRequestDataBinder;
import java.beans.PropertyEditorSupport;
import javax.annotation.Resource;

/**
 * @author Chris Kittrell (ckittrell)
 */
@Service(value = "blInitBinderService") public class InitBinderServiceImpl implements InitBinderService {
  @Resource(name = "blCountryService") protected CountryService countryService;

  @Resource(name = "blISOService") protected ISOService isoService;

  @Override public void configAddressInitBinder(ServletRequestDataBinder binder) {
    binder.registerCustomEditor(ISOCountry.class, "address.isoCountryAlpha2", new PropertyEditorSupport() {
      @Override public void setAsText(String text) {
        if (StringUtils.isNotEmpty(text)) {
          ISOCountry isoCountry = isoService.findISOCountryByAlpha2Code(text);
          setValue(isoCountry);
        } else {
          setValue(null);
        }
      }
    });
    binder.registerCustomEditor(Phone.class, "address.phonePrimary", new PropertyEditorSupport() {
      @Override public void setAsText(String text) {
        Phone phone = new PhoneImpl();
        phone.setPhoneNumber(text);
        setValue(phone);
      }
    });
    binder.registerCustomEditor(Phone.class, "address.phoneSecondary", new PropertyEditorSupport() {
      @Override public void setAsText(String text) {
        Phone phone = new PhoneImpl();
        phone.setPhoneNumber(text);
        setValue(phone);
      }
    });
    binder.registerCustomEditor(Phone.class, "address.phoneFax", new PropertyEditorSupport() {
      @Override public void setAsText(String text) {
        Phone phone = new PhoneImpl();
        phone.setPhoneNumber(text);
        setValue(phone);
      }
    });
  }
}