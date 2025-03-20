package is.ru.ggp.singleagent.lists;
import is.ru.ggp.singleagent.common.ValueNode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class OpenList implements IOpenList {
  private HashMap<String, ValueNode> stateIdHashSet;

  private ArrayList<ValueNode> sortedValueNodeList;

  public OpenList() {
    this.sortedValueNodeList = new ArrayList<ValueNode>();
    this.stateIdHashSet = new HashMap<String, ValueNode>();
  }

  public void clear() {
    this.stateIdHashSet.clear();
    this.sortedValueNodeList.clear();
  }

  public boolean isEmpty() {
    return this.sortedValueNodeList.isEmpty();
  }

  @SuppressWarnings(value = { "unchecked" }) public void add(ValueNode node) {
    this.stateIdHashSet.put(node.getStateId(), node);
    this.sortedValueNodeList.add(node);
    Collections.sort(this.sortedValueNodeList);
  }

  public ValueNode get(String stringId) {
    if (this.stateIdHashSet.containsKey(stringId)) {
      return this.stateIdHashSet.get(stringId);
    }
    return null;
  }

  @Override public void reload() {
    Collections.sort(this.sortedValueNodeList);
  }

  public boolean contains(ValueNode node) {
    return this.stateIdHashSet.containsKey(node.getStateId());
  }

  public ValueNode getMostProminentGameNode() {
    ValueNode returnNode = sortedValueNodeList.get(0);
    sortedValueNodeList.remove(0);
    this.stateIdHashSet.remove(returnNode.getStateId());
    return returnNode;
  }
}