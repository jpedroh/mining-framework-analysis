package org.skyscreamer.yoga.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.skyscreamer.yoga.selector.Selector;


/**
 * Created by IntelliJ IDEA. User: corby Date: 4/21/11 Time: 3:07 PM
 */
public class MapResultMapper {
  ResultTraverser _resultTraverser = new ResultTraverser();

  public void setResultTraverser(ResultTraverser resultTraverser)
  {
     this._resultTraverser = resultTraverser;
  }

  public List<Map<String, Object>> mapOutput(Iterable<?> instances, Selector fieldSelector)
  {
     List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
     for (Object instance : instances)
     {
        result.add(mapOutput(instance, fieldSelector));
     }
     return result;
  }

  @SuppressWarnings("unchecked")
  public Map<String, Object> mapOutput(Object instance, Selector fieldSelector) {
    MapHierarchicalModel model;
    if (instance instanceof Map) {
      model = new MapHierarchicalModel(((Map<String, Object>) (instance)));
    } else {
      model = new MapHierarchicalModel();
      _resultTraverser.traverse(instance, fieldSelector, model, null);
    }
    return model.getObjectTree();
  }
}