package org.broadleafcommerce.admin.persistence.validation;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.broadleafcommerce.core.offer.service.type.OfferType;
import org.broadleafcommerce.openadmin.dto.BasicFieldMetadata;
import org.broadleafcommerce.openadmin.dto.Entity;
import org.broadleafcommerce.openadmin.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.dto.Property;
import org.broadleafcommerce.openadmin.server.service.persistence.module.provider.RuleFieldExtractionUtility;
import org.broadleafcommerce.openadmin.server.service.persistence.validation.PropertyValidationResult;
import org.broadleafcommerce.openadmin.server.service.persistence.validation.ValidationConfigurationBasedPropertyValidator;
import org.broadleafcommerce.openadmin.web.rulebuilder.dto.DataWrapper;
import org.springframework.stereotype.Component;
import java.io.Serializable;
import java.util.Map;
import javax.annotation.Resource;

/**
 * Checks to make sure that the TargetItemCriteria is not empty if required
 * 
 * @author Jaci Eckert
 */
@Component(value = "blOfferTargetCriteriaItemValidator") public class OfferTargetItemCriteriaValidator extends ValidationConfigurationBasedPropertyValidator {
  @Resource(name = "blRuleFieldExtractionUtility") protected RuleFieldExtractionUtility ruleFieldExtractionUtility;

  @Override public PropertyValidationResult validate(Entity entity, Serializable instance, Map<String, FieldMetadata> entityFieldMetadata, Map<String, String> validationConfiguration, BasicFieldMetadata propertyMetadata, String propertyName, String value) {
    Property offerTypeProperty = entity.findProperty("type");
    Property useListForDiscountsProperty = entity.findProperty("useListForDiscounts");
    if (OfferType.ORDER_ITEM.getType().equals(offerTypeProperty.getValue()) && BooleanUtils.isNotTrue(Boolean.parseBoolean(useListForDiscountsProperty.getValue()))) {
      String targetItemCriteriaJson = entity.findProperty("targetItemCriteria").getUnHtmlEncodedValue();
      if (targetItemCriteriaJson == null) {
        targetItemCriteriaJson = entity.findProperty("targetItemCriteriaJson").getUnHtmlEncodedValue();
      }
      DataWrapper dw = ruleFieldExtractionUtility.convertJsonToDataWrapper(targetItemCriteriaJson);
      if (CollectionUtils.isEmpty(dw.getData()) && dw.getRawMvel() == null) {
        return new PropertyValidationResult(false, "requiredValidationFailure");
      }
    }
    return new PropertyValidationResult(true);
  }
}