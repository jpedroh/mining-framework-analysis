package io.cloudslang.runtime.api.sequential;
import org.apache.commons.lang3.tuple.Pair;
import java.io.Serializable;
import java.util.Map;

public interface SequentialExecutionParametersProvider {
  Map<String, Pair<Serializable, Boolean>> getExecutionParameters();

  Object[] getCurrentContext();

  boolean getExternal();
}