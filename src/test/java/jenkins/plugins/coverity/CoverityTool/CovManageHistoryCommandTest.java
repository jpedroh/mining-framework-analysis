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
import jenkins.plugins.coverity.*;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.mockito.Mockito;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;


public class CovManageHistoryCommandTest extends CommandTestBase {
    @Test
    public void prepareCommandTest() throws InterruptedException, IOException {
        CIMStream cimStream = new CIMStream("TestInstance", "TestProject", "TestStream", null, "TestId", null);
        List<CIMStream> cimStreamList = new ArrayList<>();
        cimStreamList.add(cimStream);
        CIMInstance cimInstance = new CIMInstance("TestInstance", "Localhost", 8080, "TestUser", "TestPassword", false, 0);
        TaOptionBlock taOptionBlock = new TaOptionBlock(StringUtils.EMPTY, false, false, false, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, false, false, false, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, true);
        InvocationAssistance invocationAssistance = new InvocationAssistance(false, StringUtils.EMPTY, false, StringUtils.EMPTY, false, false, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, false, StringUtils.EMPTY, StringUtils.EMPTY, null, false, false, StringUtils.EMPTY, StringUtils.EMPTY, null, false);
        CoverityPublisher publisher = new CoverityPublisher(cimStreamList, invocationAssistance, false, false, false, false, false, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, null, taOptionBlock, null);
        ICommand covManageHistoryCommand = new CovManageHistoryCommand(build, launcher, listener, publisher, StringUtils.EMPTY, envVars, cimStream, cimInstance, CoverityVersion.VERSION_JASPER);
        setExpectedArguments(new String[]{ "cov-manage-history", "--dir", "TestDir", "download", "--host", "Localhost", "--port", "8080", "--stream", "TestStream", "--user", "TestUser", "--merge" });
        covManageHistoryCommand.runCommand();
        assertEquals("TestPassword", envVars.get("COVERITY_PASSPHRASE"));
        consoleLogger.verifyLastMessage("[Coverity] cov-manage-history command line arguments: " + actualArguments.toString());
    }

    @Test
    public void prepareCommandTest_WithSslConfiguration_ForIndio() throws InterruptedException, IOException {
        CIMStream cimStream = new CIMStream("TestInstance", "TestProject", "TestStream", null, "TestId", null);
        List<CIMStream> cimStreamList = new ArrayList<>();
        cimStreamList.add(cimStream);
        CIMInstance cimInstance = new CIMInstance("TestInstance", "Localhost", 8080, "TestUser", "TestPassword", true, 0);
        TaOptionBlock taOptionBlock = new TaOptionBlock(StringUtils.EMPTY, false, false, false, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, false, false, false, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, true);
        InvocationAssistance invocationAssistance = new InvocationAssistance(false, StringUtils.EMPTY, false, StringUtils.EMPTY, false, false, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, false, StringUtils.EMPTY, StringUtils.EMPTY, null, false, false, StringUtils.EMPTY, StringUtils.EMPTY, null, false);
        CoverityPublisher publisher = new CoverityPublisher(cimStreamList, invocationAssistance, false, false, false, false, false, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, null, taOptionBlock, null);
        ICommand covManageHistoryCommand = new CovManageHistoryCommand(build, launcher, listener, publisher, StringUtils.EMPTY, envVars, cimStream, cimInstance, CoverityVersion.VERSION_INDIO);
        setExpectedArguments(new String[]{ "cov-manage-history", "--dir", "TestDir", "download", "--host", "Localhost", "--port", "8080", "--stream", "TestStream", "--ssl", "--user", "TestUser", "--merge" });
        covManageHistoryCommand.runCommand();
        assertEquals("TestPassword", envVars.get("COVERITY_PASSPHRASE"));
        consoleLogger.verifyLastMessage("[Coverity] cov-manage-history command line arguments: " + actualArguments.toString());
    }

    @Test
    public void prepareCommandTest_WithSslConfiguration_ForJasperOrHigher() throws InterruptedException, IOException {
        CIMStream cimStream = new CIMStream("TestInstance", "TestProject", "TestStream", null, "TestId", null);
        List<CIMStream> cimStreamList = new ArrayList<>();
        cimStreamList.add(cimStream);
        CIMInstance cimInstance = new CIMInstance("TestInstance", "Localhost", 8080, "TestUser", "TestPassword", true, 0);
        TaOptionBlock taOptionBlock = new TaOptionBlock(StringUtils.EMPTY, false, false, false, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, false, false, false, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, true);
        InvocationAssistance invocationAssistance = new InvocationAssistance(false, StringUtils.EMPTY, false, StringUtils.EMPTY, false, false, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, false, StringUtils.EMPTY, StringUtils.EMPTY, null, false, false, StringUtils.EMPTY, StringUtils.EMPTY, null, false);
        SSLConfigurations sslConfigurations = new SSLConfigurations(true, null);
        sslConfigurations.setCertFileName("TestCertFile");
        CoverityPublisher.DescriptorImpl descriptor = Mockito.mock(CoverityPublisher.DescriptorImpl.class);
        CoverityPublisher publisher = Mockito.mock(CoverityPublisher.class);
        when(publisher.getTaOptionBlock()).thenReturn(taOptionBlock);
        when(publisher.getDescriptor()).thenReturn(descriptor);
        when(publisher.getCimStreams()).thenReturn(cimStreamList);
        when(publisher.getInvocationAssistance()).thenReturn(invocationAssistance);
        when(publisher.getInvocationAssistance()).thenReturn(invocationAssistance);
        when(descriptor.getSslConfigurations()).thenReturn(sslConfigurations);
        ICommand covManageHistoryCommand = new CovManageHistoryCommand(build, launcher, listener, publisher, StringUtils.EMPTY, envVars, cimStream, cimInstance, CoverityVersion.VERSION_JASPER);
        setExpectedArguments(new String[]{ "cov-manage-history", "--dir", "TestDir", "download", "--host", "Localhost", "--port", "8080", "--stream", "TestStream", "--ssl", "--on-new-cert", "trust", "--cert", "TestCertFile", "--user", "TestUser", "--merge" });
        covManageHistoryCommand.runCommand();
        assertEquals("TestPassword", envVars.get("COVERITY_PASSPHRASE"));
        consoleLogger.verifyLastMessage("[Coverity] cov-manage-history command line arguments: " + actualArguments.toString());
    }

    @Test
    public void cannotExecuteTest() throws IOException, InterruptedException {
        CoverityPublisher publisher = new CoverityPublisher(null, null, false, false, false, false, false, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, null, null, null);
        ICommand covManageHistoryCommand = new CovManageHistoryCommand(build, launcher, listener, publisher, StringUtils.EMPTY, envVars, null, null, CoverityVersion.VERSION_INDIO);
        setExpectedArguments(new String[]{ "cov-manage-history", "--dir", "TestDir", "download", "--host", "Localhost", "--port", "8080", "--stream", "TestStream", "--ssl", "--user", "TestUser", "--merge" });
        covManageHistoryCommand.runCommand();
        consoleLogger.verifyLastMessage("[Coverity] Skipping command because it can\'t be executed");
    }
}