package org.dspace.app.rest.submit.factory.impl;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.collections.CollectionUtils;
import org.dspace.app.rest.model.AccessConditionDTO;
import org.dspace.app.rest.model.patch.LateObjectEvaluator;
import org.dspace.authorize.ResourcePolicy;
import org.dspace.authorize.service.ResourcePolicyService;
import org.dspace.content.Bitstream;
import org.dspace.content.Bundle;
import org.dspace.content.InProgressSubmission;
import org.dspace.content.Item;
import org.dspace.content.service.ItemService;
import org.dspace.core.Constants;
import org.dspace.core.Context;
import org.dspace.submit.model.UploadConfiguration;
import org.dspace.submit.model.UploadConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Submission "add" operation to add resource policies in the Bitstream
 *
 * @author Luigi Andrea Pascarelli (luigiandrea.pascarelli at 4science.it)
 */
public class BitstreamResourcePolicyAddPatchOperation extends AddPatchOperation<AccessConditionDTO> {
  @Autowired ItemService itemService;

  @Autowired private ResourcePolicyService resourcePolicyService;

  @Autowired UploadConfigurationService uploadConfigurationService;

  @Override void add(Context context, HttpServletRequest currentRequest, InProgressSubmission source, String path, Object value) throws Exception {
    String[] splitAbsPath = getAbsolutePath(path).split("/");
    String[] splitPath = path.split("/");
    Item item = source.getItem();
    List<Bundle> bundle = itemService.getBundles(item, Constants.CONTENT_BUNDLE_NAME);
    ;
    UploadConfiguration uploadConfig = uploadConfigurationService.getMap().get(splitPath[2]);
    for (Bundle bb : bundle) {
      int idx = 0;
      for (Bitstream bitstream : bb.getBitstreams()) {
        if (idx == Integer.parseInt(splitAbsPath[1])) {
          List<AccessConditionDTO> newAccessConditions = new ArrayList<AccessConditionDTO>();
          if (splitAbsPath.length == 3) {
            resourcePolicyService.removePolicies(context, bitstream, ResourcePolicy.TYPE_CUSTOM);
            newAccessConditions = evaluateArrayObject((LateObjectEvaluator) value);
          } else {
            if (splitAbsPath.length == 4) {
              newAccessConditions.add(evaluateSingleObject((LateObjectEvaluator) value));
            }
          }
          if (CollectionUtils.isNotEmpty(newAccessConditions)) {
            BitstreamResourcePolicyUtils.findApplyResourcePolicy(context, uploadConfig, bitstream, newAccessConditions);
          }
        }
        idx++;
      }
    }
  }

  @Override protected Class<AccessConditionDTO[]> getArrayClassForEvaluation() {
    return AccessConditionDTO[].class;
  }

  @Override protected Class<AccessConditionDTO> getClassForEvaluation() {
    return AccessConditionDTO.class;
  }
}