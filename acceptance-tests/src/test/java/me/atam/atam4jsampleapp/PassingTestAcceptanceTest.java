package me.atam.atam4jsampleapp;

import javax.ws.rs.core.Response;
import me.atam.atam4j.dummytests.PassingTestWithNoCategory;
import me.atam.atam4jdomain.IndividualTestResult;
import me.atam.atam4jdomain.TestsRunResult;
import me.atam.atam4jsampleapp.testsupport.AcceptanceTest;
import me.atam.atam4jsampleapp.testsupport.Atam4jApplicationStarter;
import org.junit.Test;
import static me.atam.atam4jsampleapp.testsupport.AcceptanceTestTimeouts.TEN_SECONDS_IN_MILLIS;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


public class PassingTestAcceptanceTest extends AcceptanceTest {
    @Test
    public void givenPassingTest_whenTestsEndpointCalledBeforeTestRun_thenTooEarlyMessageReceived() {
        //given
        dropwizardTestSupportAppConfig = Atam4jApplicationStarter.startApplicationWith(TEN_SECONDS_IN_MILLIS, PassingTestWithNoCategory.class);
        //when
        Response testRunResultFromServer = getTestRunResultFromServer(getTestsURI());
        //then
        assertThat(testRunResultFromServer.getStatus(), is(Response.Status.OK.getStatusCode()));
        assertThat(testRunResultFromServer.readEntity(TestsRunResult.class).getStatus(), is(TestsRunResult.Status.TOO_EARLY));
    }

    @Test
    public void givenSampleApplicationStartedWithPassingTest_whenCriticalHealthCheckCalledBeforeTestRun_thenTooEarlyMessageReceived(){

        dropwizardTestSupportAppConfig = Atam4jApplicationStarter
                .startApplicationWith(TEN_SECONDS_IN_MILLIS, PassingTest.class);

        Response testRunResultFromServer = getCriticalTestRunResultFromServer();
        assertThat(testRunResultFromServer.getStatus(), is(Response.Status.OK.getStatusCode()));
        assertThat(
                testRunResultFromServer.readEntity(TestsRunResult.class).getStatus(),
                is(TestsRunResult.Status.TOO_EARLY)
        );
    }

    @Test
    public void givenPassingTest_whenTestsEndpointCalledAfterTestRun_thenOKMessageReceived() {
        //given
        dropwizardTestSupportAppConfig = Atam4jApplicationStarter.startApplicationWith(0, PassingTestWithNoCategory.class);
        //when
        Response response = getResponseFromTestsEndpointOnceTestsRunHasCompleted();
        TestsRunResult testRunResult = response.readEntity(TestsRunResult.class);
        //then
        assertThat(response.getStatus(), is(Response.Status.OK.getStatusCode()));
        assertThat(testRunResult.getTests().size(), is(1));
        assertThat(testRunResult.getTests(), hasItem(new IndividualTestResult(PassingTestWithNoCategory.class.getName(), "testThatPasses", true)));
    }
}