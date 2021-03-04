package gov.nysenate.openleg.api.legislation.law.view;

import gov.nysenate.openleg.config.annotation.UnitTest;
import gov.nysenate.openleg.legislation.law.LawChapterCode;
import gov.nysenate.openleg.legislation.law.LawDocInfo;
import gov.nysenate.openleg.legislation.law.LawDocument;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import testing_utils.LawTestUtils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static gov.nysenate.openleg.api.legislation.law.view.LawPdfUtil.BOLD_MARKER;
import static gov.nysenate.openleg.api.legislation.law.view.LawPdfUtil.getLines;
import static gov.nysenate.openleg.legislation.law.LawChapterCode.*;
import static gov.nysenate.openleg.legislation.law.LawDocumentType.CHAPTER;
import static gov.nysenate.openleg.legislation.law.LawDocumentType.SECTION;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Category(UnitTest.class)
public class LawPdfUtilTest {
    @Test
    public void dummyGetBlocksTest() {
        LawDocInfo info = LawTestUtils.getLawDocInfo(MHA, "-ROOT", CHAPTER);
        LawDocument doc = new LawDocument(info, "");
        doc.setDummy(true);
        assertTrue(getLines(doc).isEmpty());
    }

    @Test
    public void sectionGetBlocksTest() {
        LawDocInfo info = LawTestUtils.getLawDocInfo(CCO, "1", SECTION);
        LawDocument doc = new LawDocument(info, "1. The\ntitle. The text.");
        doc.setTitle("The title.");

        List<String> lines = getLines(doc);
        assertEquals(4, lines.size());
        String newlineToSpace = lines.get(0) + " " + lines.get(1);
        String toMatch = BOLD_MARKER + ".*" + doc.getTitle() + BOLD_MARKER + ".*";
        assertTrue(newlineToSpace.matches(toMatch));
        assertEquals("", lines.get(2));
        assertEquals("", lines.get(3));
    }

    @Test
    public void paragraphGetBlocksTest() {
        String text = "Before paragraph.\n  1. Paragraph.";
        LawDocInfo info = LawTestUtils.getLawDocInfo(LAB, "2", SECTION);
        LawDocument doc = new LawDocument(info, text);

        List<String> lines = LawPdfUtil.getLines(doc);
        StringBuilder withNewlines = new StringBuilder();
        for (int i = 0; i < lines.size(); i++) {
            withNewlines.append(lines.get(i));
            if (i != lines.size() - 1)
                withNewlines.append("\n");
        }
        // An extra line should've been added before the paragraph.
        assertEquals(text.replaceFirst("\n", "\n\n"), withNewlines.toString().trim());
    }

    @Test
    public void consolidatedBoldTest() {
        String expected = "Before." + BOLD_MARKER + "  CHAPTER III-B OF THE CONSOLIDATED LAWS" + BOLD_MARKER + " After.";
        boldChapterTestHelper(expected, ABC, "3-B");
    }

    @Test
    public void AHHHHHtest() {
        Pattern p = Pattern.compile("\\s{2,}CHAPTER [\\w-]+( OF THE CONSOLIDATED LAWS)?");
        String s = """
                CHAPTER 712
AN ACT relating to cooperative corporations, constituting chapter
seventy-seven of the consolidated laws
Became a law April 11, 1951, with the approval of the Governor.  Passed
by a majority vote, three-fifths being present.
  The People of the State of New York, represented in Senate and
Assembly, do enact as follows:
                   CHAPTER 77 OF THE CONSOLIDATED LAWS
                      COOPERATIVE CORPORATIONS LAW
Article 1.   Short title; policy; definitions. (§§ 1-5.)
                              2.   Formation and dissolution of cooperative corporations;
                              """;
        Matcher m = p.matcher(s);
        while (m.find())
            System.out.println(m.group());
    }

    /**
     * Puts some common code together that tests chapters.
     * @param code of the chapter.
     * @param docTypeId for the chapter.
     */
    private static void boldChapterTestHelper(String expected, LawChapterCode code, String docTypeId) {
        String text = expected.replaceAll(BOLD_MARKER, "");

        LawDocInfo info = LawTestUtils.getLawDocInfo(code, "-CH" + docTypeId, CHAPTER);
        LawDocument doc = new LawDocument(info, text);
        doc.setDocTypeId(docTypeId);

        List<String> lines = getLines(doc);
        StringBuilder actual = new StringBuilder();
        for (int i = 0; i < lines.size(); i++) {
            actual.append(lines.get(i));
            if (i != lines.size() - 1)
                actual.append("\n");
        }
        assertEquals(expected, actual.toString().trim());
    }
}
