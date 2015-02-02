package gov.nysenate.openleg.service.spotcheck;

import com.google.common.eventbus.EventBus;
import gov.nysenate.openleg.dao.daybreak.DaybreakDao;
import gov.nysenate.openleg.config.Environment;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.notification.Notification;
import gov.nysenate.openleg.model.notification.NotificationType;
import gov.nysenate.openleg.model.spotcheck.ReferenceDataNotFoundEx;
import gov.nysenate.openleg.model.spotcheck.SpotCheckReport;
import gov.nysenate.openleg.processor.daybreak.DaybreakProcessService;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class DaybreakSpotcheckRunService implements SpotcheckRunService {

    private static final Logger logger = LoggerFactory.getLogger(DaybreakSpotcheckRunService.class);

    @Autowired
    CheckMailService checkMailService;

    @Autowired
    DaybreakDao daybreakDao;

    @Autowired
    DaybreakProcessService daybreakProcessService;

    @Autowired
    DaybreakCheckReportService spotCheckReportService;

    @Autowired
    Environment env;

    @Autowired
    EventBus eventBus;

    /**
     * Schedules the run spotcheck method to be run according to the cron supplied in the properties file
     */
    @Scheduled(cron = "${scheduler.spotcheck.cron}")
    public void scheduledSpotcheck() {
        if (env.isSpotcheckScheduled()) {
            SpotCheckReport<BaseBillId> spotCheckReport = runSpotcheck();
            if (spotCheckReport != null) {
                spotcheckCompleteNotification(spotCheckReport);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SpotCheckReport<BaseBillId> runSpotcheck() {
        // If checkmail finds and saves daybreaks, parse/store reference data and run a report
        if (checkMailService.checkMail()) {
            daybreakProcessService.collateDaybreakReports();
            daybreakProcessService.processPendingFragments();
            return generateReport();
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SpotCheckReport<BaseBillId> generateReport() {
        logger.info("looking for unchecked daybreak references...");
        try {
            LocalDate reportDate = daybreakDao.getCurrentReportDate();
            if (!daybreakDao.isChecked(reportDate)) {
                logger.info("found unchecked daybreak refs from {}", reportDate);
                try {
                    SpotCheckReport<BaseBillId> daybreakReport = spotCheckReportService.generateReport(
                            LocalDateTime.now().minusWeeks(1), LocalDateTime.now());
                    spotCheckReportService.saveReport(daybreakReport);
                    logger.info("generated daybreak spotcheck {}", daybreakReport.getReportId());
                    return daybreakReport;
                } catch (ReferenceDataNotFoundEx referenceDataNotFoundEx) {
                    logger.error("Report not found! \n{}", ExceptionUtils.getStackTrace(referenceDataNotFoundEx));
                } catch (DataAccessException ex) {
                    logger.error("{}", ex);
                }
            } else {
                logger.info("no unchecked daybreak reports found");
            }
        } catch (DataAccessException ex) {
            logger.info("no daybreak reports found");
        }
        return null;
    }

    /** --- Internal Methods --- */

    /**
     * Generates and sends a notification for a new daybreak spotcheck report
     * @param daybreakReport SpotCheckReport<BaseBillId>
     */
    private void spotcheckCompleteNotification(SpotCheckReport<BaseBillId> daybreakReport) {
        String summary = "New spotcheck report: " + daybreakReport.getReportDateTime();
        StringBuilder messageBuilder = new StringBuilder();

        messageBuilder.append(summary)
                .append("\n\n");

        messageBuilder.append(env.getUrl())
                .append("/admin/report/daybreak/")
                .append(daybreakReport.getReportDateTime())
                .append("\n\n");

        messageBuilder.append("Total open errors: ").append(daybreakReport.getOpenMismatchCount()).append("\n");

        daybreakReport.getMismatchStatusTypeCounts().forEach((status, typeCounts) -> {
            long totalTypeCounts = typeCounts.values().stream().reduce(0L, (a, b) -> a + b);
            messageBuilder.append(status).append(": ").append(totalTypeCounts).append("\n");
            typeCounts.forEach((type, count) ->
                    messageBuilder.append("\t").append(type).append(": ").append(count).append("\n"));
        });

        Notification notification = new Notification(NotificationType.SPOTCHECK, daybreakReport.getReportDateTime(),
                                                     summary, messageBuilder.toString());

        eventBus.post(notification);
    }
}
