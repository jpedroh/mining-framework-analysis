package org.broadleafcommerce.common.web.validator;
import org.broadleafcommerce.common.util.BLCSystemProperty;
import org.broadleafcommerce.common.web.form.BroadleafFormType;
import org.broadleafcommerce.profile.core.domain.Address;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import javax.annotation.Resource;

/**
 * Generic Address Validator not specific to a particular Country.
 * Some modules may provide custom validation which can be enabled by setting
 * the {@link org.broadleafcommerce.common.config.domain.SystemProperty} "validator.custom.enabled"
 *
 * If a custom validation is not performed, a generic set of address validation is used.
 * You may configure this validator to only validate a Full Name rather than a separate first and last name,
 * by setting the {@link org.broadleafcommerce.common.config.domain.SystemProperty} "validator.address.fullNameOnly"
 *
 * @author Elbert Bautista (elbertbautista)
 */
public class BroadleafCommonAddressValidator {
  @Resource(name = "blBroadleafCommonAddressValidatorExtensionManager") protected BroadleafCommonAddressValidatorExtensionManager validatorExtensionManager;

  public boolean isValidateFullNameOnly() {
    return BLCSystemProperty.resolveBooleanSystemProperty("validator.address.fullNameOnly");
  }

  public boolean isCustomValidationEnabled() {
    return BLCSystemProperty.resolveBooleanSystemProperty("validator.custom.enabled");
  }

  public void validate(BroadleafFormType formType, Address address, Errors errors) {
    if (isCustomValidationEnabled()) {
      validatorExtensionManager.getProxy().validate(formType, address, errors);
      return;
    }
    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "address.addressLine1", "addressLine1.required");
    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "address.city", "city.required");
    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "address.stateProvinceRegion", "state.required");
    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "address.postalCode", "postalCode.required");
    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "address.phonePrimary.phoneNumber", "phonePrimary.required");
    if (isValidateFullNameOnly()) {
      ValidationUtils.rejectIfEmptyOrWhitespace(errors, "address.fullName", "fullName.required");
    } else {
      ValidationUtils.rejectIfEmptyOrWhitespace(errors, "address.firstName", "firstName.required");
      ValidationUtils.rejectIfEmptyOrWhitespace(errors, "address.lastName", "lastName.required");
    }
    if (address.getIsoCountryAlpha2() == null) {
      errors.rejectValue("address.isoCountryAlpha2", "country.required", null, null);
    }
  }
}