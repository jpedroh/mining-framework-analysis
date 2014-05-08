/**
* Code contributed to the Learning Layers project
* http://www.learning-layers.eu
* Development is partly funded by the FP7 Programme of the European Commission under
* Grant Agreement FP7-ICT-318209.
* Copyright (c) 2014, Graz University of Technology - KTI (Knowledge Technologies Institute).
* For a list of contributors see the AUTHORS file at the top-level directory of this distribution.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package at.kc.tugraz.ss.category.datatypes.ret;

import at.kc.tugraz.socialserver.utils.SSMethU;
import at.kc.tugraz.ss.serv.jsonld.util.SSJSONLDU;
import at.kc.tugraz.socialserver.utils.SSStrU;
import at.kc.tugraz.socialserver.utils.SSVarU;
import at.kc.tugraz.ss.category.datatypes.par.SSCategoryFrequ;
import at.kc.tugraz.ss.serv.datatypes.SSServRetI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SSCategoryUserFrequsGetRet extends SSServRetI{
  
  public List<SSCategoryFrequ> categoryFrequs = new ArrayList<SSCategoryFrequ>();
  
  public static SSCategoryUserFrequsGetRet get(final List<SSCategoryFrequ> categoryFrequs){
    return new SSCategoryUserFrequsGetRet(categoryFrequs);
  }
  
  private SSCategoryUserFrequsGetRet(final List<SSCategoryFrequ> categoryFrequs){
    
    super(SSMethU.categoryUserFrequsGet);
    
    if(categoryFrequs != null){
      this.categoryFrequs.addAll(categoryFrequs);
    }
  }
  
  @Override
  public Map<String, Object> jsonLDDesc(){
    
    final Map<String, Object> ld                      = new HashMap<String, Object>();
    final Map<String, Object> categoriesFrequsObj     = new HashMap<String, Object>();
    
    categoriesFrequsObj.put(SSJSONLDU.id,        SSVarU.sss + SSStrU.colon + SSCategoryFrequ.class.getName());
    categoriesFrequsObj.put(SSJSONLDU.container, SSJSONLDU.set);
    
    ld.put(SSVarU.categoryFrequs, categoriesFrequsObj);
    
    return ld;
  }
  
  /* getters to allow for json enconding */
  public List<SSCategoryFrequ> getTagFrequs() {
    return categoryFrequs;
  }
}
