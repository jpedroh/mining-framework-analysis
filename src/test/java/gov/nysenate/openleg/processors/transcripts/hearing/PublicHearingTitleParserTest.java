package gov.nysenate.openleg.processors.transcripts.hearing;

import gov.nysenate.openleg.common.util.PublicHearingTextUtils;
import gov.nysenate.openleg.config.annotation.UnitTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import static org.junit.Assert.assertEquals;

@Category(UnitTest.class)
public class PublicHearingTitleParserTest {
    @Test
    public void basicTitleParses() throws URISyntaxException, IOException {
        testTitle("03-12-14 Roundtable on the Compassionate Care Act_Savino_FINAL.txt",
                "ROUNDTABLE DISCUSSION ON THE COMPASSIONATE CARE ACT");
    }

    @Test
    public void forumTownHallTitleParses() throws URISyntaxException, IOException {
        testTitle("02-09-12 ChildCareHearing_Final.txt",
                "FORUM/TOWN HALL: HUMAN SERVICES FORUM ON THE CURRENT AND FUTURE " +
                          "ISSUES AND CONCERNS OF HUMAN SERVICES ADMINISTRATORS, ADVOCATES AND CLIENTS");
    }

    @Test
    public void newYorkStateForumTownHallTitleParses() throws URISyntaxException, IOException {
        testTitle("05-18-11 ValeskyAgingCommitteeRoundtableFINAL.txt",
                "NEW YORK STATE FORUM/TOWN HALL ROUNDTABLE ON THE SAGE COMMISSION'S " +
                          "PROPOSAL TO MERGE THE NYS OFFICE FOR THE AGING WITH THE DEPARTMENT OF HEALTH");
    }

    @Test
    public void senateHearingTitleParses() throws URISyntaxException, IOException {
        testTitle("05-23-11 NY Senate Flanagan Education Hearing FINAL_AMENDED COVER SHEET ONLY.txt",
                "A NEW YORK STATE SENATE HEARING " +
                          "DUE PROCESS TEACHER DISCIPLINE WITHOUT DELAY: " +
                          "REFORMING SECTION 3020-A OF THE EDUCATION LAW " +
                          "TO MEET THE NEEDS OF THE 21st CENTURY");
    }

    @Test
    public void titleFoundIfCommitteeMissing() throws URISyntaxException, IOException {
        testTitle("01-03-13 HurricaneSandy_NYS TaskForce Roundtable_Final.txt",
                "ROUNDTABLE DISCUSSION HELD BY THE NEW YORK STATE SENATE " +
                          "BIPARTISAN TASK FORCE FOR \"HURRICANE SANDY\" RECOVERY");
    }

    @Test
    public void conferenceTitleParses() throws URISyntaxException, IOException {
        testTitle("02-28-13 2013 RevenueConsensusConference_Final.txt",
                "NEW YORK STATE 2013 ECONOMIC AND REVENUE CONSENSUS FORECASTING CONFERENCE");
    }

    @Test
    public void onHeroinEpidemicParses() throws URISyntaxException, IOException {
        testTitle("02-23-2016 NYS Task Force Heroin_Penn Yan Final.txt",
                "TO EXAMINE THE ISSUES FACING COMMUNITIES IN THE WAKE OF INCREASED HEROIN AND OPIOID ABUSE");
    }

    @Test
    public void jointLegislativeHearingParses() throws IOException, URISyntaxException {
        testTitle("1-30-20 Human Services Transcript.txt",
                "JOINT LEGISLATIVE HEARING In the Matter of the 2020-2021 EXECUTIVE BUDGET ON HUMAN SERVICES");
    }

    private void testTitle(String filename, String expected) throws URISyntaxException, IOException{
        List<List<String>> pages = PublicHearingTestHelper.getPagesFromFileName(filename);
        String actual = PublicHearingTextUtils.parseTitle(pages.get(0));
        assertEquals(expected, actual);
    }
}