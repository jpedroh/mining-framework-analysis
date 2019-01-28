package gov.nysenate.openleg.dao.spotcheck;

import com.google.common.collect.ImmutableMap;
import gov.nysenate.openleg.model.base.Version;
import gov.nysenate.openleg.model.entity.Chamber;
import gov.nysenate.openleg.model.spotcheck.agenda.AgendaAlertCheckId;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Objects;

/**
 * This converts a {@link AgendaAlertCheckId} to a Map<String,String> and vice versa.
 * The Map<String, String> is used to store hstore values for the database.
 */
@Repository
public class AgendaAlertReportDao extends AbstractSpotCheckReportDao<AgendaAlertCheckId> {

    private DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE;

    @Override
    public AgendaAlertCheckId getKeyFromMap(Map<String, String> keyMap) {
        Objects.requireNonNull(keyMap);
        return new AgendaAlertCheckId(
                Integer.valueOf(keyMap.get("year")),
                LocalDate.parse(keyMap.get("week_of"), DATE_FORMAT),
                Version.of(keyMap.get("addendum")),
                Chamber.getValue(keyMap.get("chamber")),
                keyMap.get("committee_name")
        );
    }

    @Override
    public Map<String, String> getMapFromKey(AgendaAlertCheckId alertId) {
        Objects.requireNonNull(alertId);
        return ImmutableMap.<String, String>builder()
                .put("year", String.valueOf(alertId.getYear()))
                .put("week_of", alertId.getWeekOf().format(DATE_FORMAT))
                .put("addendum", alertId.getAddendum().name())
                .put("chamber", alertId.getChamber().asSqlEnum())
                .put("committee_name", alertId.getCommitteeName())
                .build();
    }
}
