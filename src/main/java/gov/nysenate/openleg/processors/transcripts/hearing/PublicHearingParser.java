package gov.nysenate.openleg.processors.transcripts.hearing;

import gov.nysenate.openleg.legislation.transcripts.hearing.PublicHearing;
import gov.nysenate.openleg.legislation.transcripts.hearing.PublicHearingFile;
import gov.nysenate.openleg.legislation.transcripts.hearing.dao.PublicHearingDataService;
import org.apache.commons.io.Charsets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.time.LocalDateTime;

@Service
public class PublicHearingParser {
    @Autowired
    private PublicHearingDataService hearingDataService;

    private final static Charset CP_1252 = Charsets.toCharset("CP1252");

    /**
     * Parses a {@link PublicHearingFile}, extracting a
     * {@link PublicHearing PublicHearing}.
     * @param publicHearingFile
     * @throws IOException
     */
    public void process(PublicHearingFile publicHearingFile) throws IOException {
        String fullText = Files.readString(publicHearingFile.getFile().toPath(), CP_1252);
        PublicHearing hearing = PublicHearingTextUtils.getHearingFromText(publicHearingFile.getFileName(), fullText);

        LocalDateTime now = LocalDateTime.now();
        hearing.setModifiedDateTime(now);
        hearing.setPublishedDateTime(now);

        hearingDataService.savePublicHearing(hearing, publicHearingFile, true);
    }
}
