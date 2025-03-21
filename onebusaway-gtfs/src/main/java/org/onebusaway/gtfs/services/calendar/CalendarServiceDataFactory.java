package org.onebusaway.gtfs.services.calendar;
import org.onebusaway.gtfs.model.Agency;
import org.onebusaway.gtfs.model.AgencyAndId;
import org.onebusaway.gtfs.model.calendar.CalendarServiceData;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public interface CalendarServiceDataFactory {
  public CalendarServiceData createData();

  public CalendarServiceData updateData(Collection<Agency> allAgencies, Map<AgencyAndId, List<String>> tripAgencyIdsReferencingServiceId, Map<String, TimeZone> timeZoneMapByAgencyId);
}