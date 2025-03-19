package io.cloudslang.runtime.impl.python;
import io.cloudslang.dependency.api.services.DependencyService;
import io.cloudslang.runtime.api.python.PythonEvaluationResult;
import io.cloudslang.runtime.api.python.PythonExecutionResult;
import org.springframework.beans.factory.annotation.Autowired;
import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * Created by Genadi Rabinovich, genadi@hpe.com on 05/05/2016.
 */
public class PythonExecutionNotCachedEngine implements PythonExecutionEngine {
  @Autowired private DependencyService dependencyService;

  @Override public PythonExecutionResult exec(Set<String> dependencies, String script, Map<String, Serializable> vars) {
    PythonExecutor pythonExecutor = new PythonExecutor(dependencyService.getDependencies(dependencies));
    try {
      return pythonExecutor.exec(script, vars);
    }  finally {
      pythonExecutor.close();
    }
  }

  @Override public PythonEvaluationResult eval(String prepareEnvironmentScript, String script, Map<String, Serializable> vars) {
    PythonExecutor pythonExecutor = new PythonExecutor(Collections.<String>emptySet());
    try {
      return pythonExecutor.eval(prepareEnvironmentScript, script, vars);
    }  finally {
      pythonExecutor.close();
    }
  }
}