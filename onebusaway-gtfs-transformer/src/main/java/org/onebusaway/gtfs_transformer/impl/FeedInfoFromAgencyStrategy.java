package org.onebusaway.gtfs_transformer.impl;
import org.onebusaway.csv_entities.schema.annotations.CsvField;
import org.slf4j.Logger;
import org.onebusaway.gtfs.model.Agency;
import org.slf4j.LoggerFactory;
import org.onebusaway.gtfs.model.FeedInfo;
import org.onebusaway.gtfs.services.GtfsMutableRelationalDao;
import org.onebusaway.gtfs_transformer.services.GtfsTransformStrategy;
import org.onebusaway.gtfs_transformer.services.TransformContext;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class FeedInfoFromAgencyStrategy implements GtfsTransformStrategy {
  private static Logger _log = LoggerFactory.getLogger(FeedInfoFromAgencyStrategy.class);

  private String agencyId;

  private String feedVersion;

  @CsvField(optional = true) private String defaultLang = "en";

  @Override public String getName() {
    return this.getClass().getSimpleName();
  }

  @Override public void run(TransformContext context, GtfsMutableRelationalDao dao) {
    boolean foundAgency = false;
    for (Agency agency : dao.getAllAgencies()) {
      _log.info("comparing agency " + agency.getId() + " to " + agencyId);
      if (agency.getId().equals(agencyId)) {
        foundAgency = true;
        _log.info("creating feed info from matched agency " + agencyId);
        FeedInfo info = getFeedInfoFromAgency(dao, agency);
        if (info.getVersion() == null) {

<<<<<<< /usr/src/app/output/onebusaway/onebusaway-gtfs-modules/0dfc36d5c8bb5baea8656f9c4ea68983a8e0070d/onebusaway-gtfs-transformer/src/main/java/org/onebusaway/gtfs_transformer/impl/FeedInfoFromAgencyStrategy.java/left.java
          addCreationTime(info, context);
=======
          if (feedVersion != null) {
            info.setVersion(feedVersion);
          } else {
            addCreationTime(info, context);
          }
>>>>>>> /usr/src/app/output/onebusaway/onebusaway-gtfs-modules/0dfc36d5c8bb5baea8656f9c4ea68983a8e0070d/onebusaway-gtfs-transformer/src/main/java/org/onebusaway/gtfs_transformer/impl/FeedInfoFromAgencyStrategy.java/right.java

          dao.saveOrUpdateEntity(info);
        } else {
          _log.info("found feedVersion " + info.getVersion() + ", abandoning");
        }
      }
    }
    if (!foundAgency) {
      Agency agency = dao.getAllAgencies().iterator().next();
      FeedInfo info = getFeedInfoFromAgency(dao, agency);
      _log.info("creating feed info from unmatched agency " + agency.getId());
      addCreationTime(info, context);
      dao.saveOrUpdateEntity(info);
    }
  }

  private FeedInfo getFeedInfoFromAgency(GtfsMutableRelationalDao dao, Agency agency) {
    FeedInfo info = dao.getFeedInfoForId(agencyId);
    if (info == null) {
      info = new FeedInfo();
    }
    info.setId(agencyId);
    info.setPublisherName(agency.getName());
    info.setPublisherUrl(agency.getUrl());
    if (agency.getLang() == null || agency.getLang().isEmpty()) {
      info.setLang(defaultLang);
    } else {
      info.setLang(agency.getLang());
    }
    return info;
  }

  private void addCreationTime(FeedInfo feedInfo, TransformContext context) {
    Long creationTime = (Long) context.getReader().getContext().get("lastModifiedTime");
    SimpleDateFormat df = new SimpleDateFormat("zzz: dd-MMM-yyyy HH:mm");
    if (creationTime != null) {
      _log.info("setting version to lastModifiedTime of " + new Date(creationTime));
      feedInfo.setVersion(df.format(new Date(creationTime)));
    }
  }

  public void setAgencyId(String agencyId) {
    this.agencyId = agencyId;
  }

  public void setDefaultLang(String lang) {
    this.defaultLang = lang;
  }

  public void setFeedVersion(String feedVersion) {
    this.feedVersion = feedVersion;
  }
}