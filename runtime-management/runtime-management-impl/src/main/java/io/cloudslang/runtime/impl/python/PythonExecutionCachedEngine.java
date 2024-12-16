/*******************************************************************************
 * (c) Copyright 2014 Hewlett-Packard Development Company, L.P.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License v2.0 which accompany this distribution.
 *
 * The Apache License is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *******************************************************************************/
package io.cloudslang.runtime.impl.python;

import io.cloudslang.dependency.api.services.DependencyService;
import io.cloudslang.runtime.api.python.PythonEvaluationResult;
import io.cloudslang.runtime.api.python.PythonExecutionResult;
import io.cloudslang.runtime.impl.ExecutionCachedEngine;
import java.io.Serializable;
import java.util.Map;
import java.util.Set;
import org.python.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;


/**
 * Created by Genadi Rabinovich, genadi@hpe.com on 05/05/2016.
 */
public class PythonExecutionCachedEngine extends ExecutionCachedEngine<PythonExecutor> implements PythonExecutionEngine {
    @Autowired
    private DependencyService dependencyService;

    @Value("#{systemProperties['" + PythonExecutionConfigurationConsts.PYTHON_EXECUTOR_CACHE_SIZE + "'] != null ? systemProperties['" + PythonExecutionConfigurationConsts.PYTHON_EXECUTOR_CACHE_SIZE + "'] : " + PythonExecutionConfigurationConsts.PYTHON_EXECUTOR_CACHE_DEFAULT_SIZE + "}")
    private int cacheSize;

    @Override
    public PythonExecutionResult exec(Set<String> dependencies, String script, Map<String, Serializable> vars) {
        PythonExecutor executor = allocateExecutor(dependencies);
        try {
            return executor.exec(script, vars);
        } finally {
            releaseExecutor(executor);
        }
    }

    @Override
    public PythonEvaluationResult eval(String prepareEnvironmentScript, String script, Map<String, Serializable> vars) {
        PythonExecutor executor = allocateExecutor(Sets.<String>newHashSet());
        try {
            return executor.eval(prepareEnvironmentScript, script, vars);
        } finally {
            releaseExecutor(executor);
        }
    }

    @Override
    protected DependencyService getDependencyService() {
        return dependencyService;
    }

    @Override
    protected int getCacheSize() {
        return cacheSize;
    }

    @Override
    protected PythonExecutor createNewExecutor(Set<String> filePaths) {
        return new PythonExecutor(filePaths);
    }
}