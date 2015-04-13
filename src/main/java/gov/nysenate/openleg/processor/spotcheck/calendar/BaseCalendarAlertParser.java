package gov.nysenate.openleg.processor.spotcheck.calendar;

import gov.nysenate.openleg.model.base.Version;
import gov.nysenate.openleg.model.calendar.CalendarId;
import gov.nysenate.openleg.util.DateUtils;
import org.jsoup.select.Elements;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;

public abstract class BaseCalendarAlertParser {

    protected CalendarId parseCalendarId(File file) {
        int year = Integer.valueOf(splitFileName(file)[1]);
        String calNo = parseCalNo(file);
        return new CalendarId(Integer.valueOf(calNo), year);
    }

    protected String parseCalNo(File file) {
        String calNoAndVersion = parseCalNoAndVersion(file);
        if (!isDefaultVersion(calNoAndVersion)) {
            calNoAndVersion = calNoAndVersion.substring(0, calNoAndVersion.length() - 1);
        }
        return calNoAndVersion;
    }

    protected Version parseVersion(File file) {
        String calNoAndVersion = parseCalNoAndVersion(file);
        if (!isDefaultVersion(calNoAndVersion)) {
            return Version.of(calNoAndVersion.substring(calNoAndVersion.length() - 1));
        }
        return Version.DEFAULT;
    }

    protected LocalDateTime parseReleaseDateTime(File file) {
        String dateTimeString = file.getName().split("-")[3];
        dateTimeString = dateTimeString.split("\\.")[0];
        return LocalDateTime.parse(dateTimeString, DateUtils.MINIMAL_ISO_DATE_TIME);
    }

    protected LocalDate parseCalendarDate(File file) {
        return parseReleaseDateTime(file).toLocalDate();
    }

    protected Elements deleteHeaderRow(Elements entryRows) {
        return new Elements(entryRows.subList(1, entryRows.size()));
    }

    private boolean isDefaultVersion(String calNoAndVersion) {
        return !calNoAndVersion.matches("\\d+[A-Z]");
    }

    private String parseCalNoAndVersion(File file) {
        return splitFileName(file)[2];
    }

    private String[] splitFileName(File file) {
        return file.getName().split("-");
    }
}
