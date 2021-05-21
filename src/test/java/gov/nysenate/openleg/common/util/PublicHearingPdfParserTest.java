package gov.nysenate.openleg.common.util;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.common.dao.SortOrder;
import gov.nysenate.openleg.config.annotation.SillyTest;
import gov.nysenate.openleg.legislation.transcripts.hearing.PublicHearing;
import gov.nysenate.openleg.legislation.transcripts.hearing.dao.PublicHearingDataService;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

// TODO: Make into unit test. Stop extending.
@Category(SillyTest.class)
public class PublicHearingPdfParserTest extends BaseTests {
    @Autowired
    private PublicHearingDataService hearings;

    @Test
    public void testHearings() {
        var ids = hearings.getPublicHearingIds(SortOrder.ASC, LimitOffset.ALL);
        int badCharCount = 0;
        // File name to count.
        for (var id : ids) {
            PublicHearing hearing = hearings.getPublicHearing(id);
            List<List<String>> pages = PublicHearingTextUtils.getPages(hearing.getText());
            for (int pageNum = 1; pageNum <= pages.size(); pageNum++) {
                var page = pages.get(pageNum - 1);
                for (int lineNum = 1; lineNum <= page.size(); lineNum++) {
                    var line = page.get(lineNum - 1);
                    var codePoints = line.codePoints().toArray();
                    for (int codePoint : codePoints) {
                        if (!printable(codePoint)) {
                            System.err.println("Bad char in " + hearing.getId().getFileName() +
                                    " at page " + pageNum + " at line " + lineNum);
                            System.out.println(++badCharCount);
                        }
                    }
                }
            }
        }
    }

    static Set<Integer> alphaNum = new HashSet<>(), punctuation = new HashSet<>(), accentChars = new HashSet<>();
    static {
        for (int i = 'a'; i <= 'z'; i++)
            alphaNum.add(i);
        for (int i = 'A'; i <= 'Z'; i++)
            alphaNum.add(i);
        for (int i = '0'; i <= '9'; i++)
            alphaNum.add(i);
        String[] puncArray = {" ", ".", ",", "!", "?", "\n", "\t", ":", "-", "'", "(", ")", "\"", ";", "$", "/", "¦",
                "*", "&", "[", "]", "`", "{", "}", "#", "^", "=", "<", ">", "|", "_", "+", "\\", "½", "¡", "@", "«",
                "~", "%", "\f", "\r"};
        for (String s : puncArray)
            punctuation.add(s.codePointAt(0));
        String[] accentArray = {"ã", "à", "á", "Á", "è", "é", "É", "Í", "í", "ï", "î", "ó", "ò", "õ", "ô", "Ú", "ú", "ç", "Ñ", "ñ", "ý"};
        for (String s : accentArray)
            accentChars.add(s.codePointAt(0));
    }

    private static boolean printable(int codepoint) {
        return alphaNum.contains(codepoint) || accentChars.contains(codepoint) ||
                punctuation.contains(codepoint);
    }
}
