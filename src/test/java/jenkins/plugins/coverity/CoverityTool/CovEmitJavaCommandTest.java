/*******************************************************************************
 * Copyright (c) 2017 Synopsys, Inc
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Synopsys, Inc - initial implementation and documentation
 *******************************************************************************/
package jenkins.plugins.coverity.CoverityTool;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import jenkins.plugins.coverity.CoverityPublisher;
import jenkins.plugins.coverity.InvocationAssistance;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;


public class CovEmitJavaCommandTest extends CommandTestBase {
    @Test
    public void addJavaWarFilesTest() throws IOException, InterruptedException {
        List<String> javaWarFiles = new ArrayList<>();
        javaWarFiles.add("webapp1.war");
        javaWarFiles.add("webapp2.war");
        InvocationAssistance invocationAssistance = new InvocationAssistance(false, StringUtils.EMPTY, false, StringUtils.EMPTY, false, false, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, false, StringUtils.EMPTY, StringUtils.EMPTY, javaWarFiles, false, false, StringUtils.EMPTY, StringUtils.EMPTY, null, false);
        CoverityPublisher publisher = new CoverityPublisher(null, invocationAssistance, false, false, false, false, false, null, null);
        ICommand covEmitJavaCommand = new CovEmitJavaCommand(build, launcher, listener, publisher, StringUtils.EMPTY, envVars, false);
        setExpectedArguments(new String[]{ "cov-emit-java", "--dir", "TestDir", "--webapp-archive", "webapp1.war", "--webapp-archive", "webapp2.war" });
        covEmitJavaCommand.runCommand();
        consoleLogger.verifyLastMessage("[Coverity] cov-emit-java command line arguments: " + actualArguments.toString());
    }

    @Test
    public void cannotExecuteTest() throws IOException, InterruptedException {
        CoverityPublisher publisher = new CoverityPublisher(
                null, null, false, false, false, false, false,
                StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY,
                null, null, null
        );

        ICommand covEmitJavaCommand = new CovEmitJavaCommand(build, launcher, listener, publisher, StringUtils.EMPTY, envVars, false);
        covEmitJavaCommand.runCommand();
        consoleLogger.verifyLastMessage("[Coverity] Skipping command because it can't be executed");
    }
}