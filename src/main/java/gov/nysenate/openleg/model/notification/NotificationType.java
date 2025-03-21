package gov.nysenate.openleg.model.notification;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public enum NotificationType {

    ALL                     (null),
    EXCEPTION               (ALL),
    REQUEST_EXCEPTION       (EXCEPTION),
    PROCESS_EXCEPTION       (EXCEPTION),
    SPOTCHECK_EXCEPTION     (EXCEPTION),
    WARNING                 (ALL),
    PROCESS_WARNING         (WARNING),
    SCRAPING_EXCEPTION      (WARNING),
    LRS_OUTAGE              (WARNING),
<<<<<<< /usr/src/app/output/nysenate/openlegislation/2438250a01426fe434525552464119c5358a8384/src/main/java/gov/nysenate/openleg/model/notification/NotificationType.java/left.java
    UNVERIFIED_MEMBER       (ALL),
    SPOTCHECK               (ALL),
    SPOTCHECK_MISMATCH      (SPOTCHECK),
    SPOTCHECK_ALL_CLEAR     (SPOTCHECK),
    DAYBREAK_SPOTCHECK      (SPOTCHECK_MISMATCH),
    CALENDAR_SPOTCHECK      (SPOTCHECK_MISMATCH),
    AGENDA_SPOTCHECK        (SPOTCHECK_MISMATCH),
    BILL_TEXT_SPOTCHECK     (SPOTCHECK_MISMATCH),
    SENSITE_BILL_SPOTCHECK  (SPOTCHECK_MISMATCH),
    SENSITE_CALENDAR_SPOTCHECK (SPOTCHECK_MISMATCH),
    SENSITE_AGENDA_SPOTCHECK (SPOTCHECK_MISMATCH),
    OPENLEG_SPOTCHECK  (SPOTCHECK_MISMATCH),
||||||| /usr/src/app/output/nysenate/openlegislation/2438250a01426fe434525552464119c5358a8384/src/main/java/gov/nysenate/openleg/model/notification/NotificationType.java/base.java
    UNVERIFIED_MEMBER       (ALL),
    SPOTCHECK               (ALL),
    SPOTCHECK_MISMATCH      (SPOTCHECK),
    SPOTCHECK_ALL_CLEAR     (SPOTCHECK),
    DAYBREAK_SPOTCHECK      (SPOTCHECK_MISMATCH),
    CALENDAR_SPOTCHECK      (SPOTCHECK_MISMATCH),
    AGENDA_SPOTCHECK        (SPOTCHECK_MISMATCH),
    BILL_TEXT_SPOTCHECK     (SPOTCHECK_MISMATCH),
    SENSITE_BILL_SPOTCHECK  (SPOTCHECK_MISMATCH),
    SENSITE_CALENDAR_SPOTCHECK (SPOTCHECK_MISMATCH),
    SENSITE_AGENDA_SPOTCHECK (SPOTCHECK_MISMATCH),
=======
    UNVERIFIED_MEMBER       (WARNING),
    SPOTCHECK               (WARNING),
    DAYBREAK_SPOTCHECK      (SPOTCHECK),
    CALENDAR_SPOTCHECK      (SPOTCHECK),
    AGENDA_SPOTCHECK        (SPOTCHECK),
    BILL_TEXT_SPOTCHECK     (SPOTCHECK),
    SENSITE_BILL_SPOTCHECK  (SPOTCHECK),
    SENSITE_CALENDAR_SPOTCHECK (SPOTCHECK),
    SENSITE_AGENDA_SPOTCHECK (SPOTCHECK),
>>>>>>> /usr/src/app/output/nysenate/openlegislation/2438250a01426fe434525552464119c5358a8384/src/main/java/gov/nysenate/openleg/model/notification/NotificationType.java/right.java
    NEW_API_KEY             (ALL),
    ;

    private NotificationType parent;

    private Set<NotificationType> children = new HashSet<>();

    private static final ImmutableSet<NotificationType> ALL_NOTIFICATION_TYPES =
            ImmutableSet.copyOf(NotificationType.values());

    @SuppressWarnings("IncompleteCopyConstructor")
    NotificationType(NotificationType parent) {
        this.parent = parent;
        if (parent != null) {
            parent.children.add(this);
        }
    }

    /**
     * Returns true if this notification type encompasses the argument notification type
     * e.g. EXCEPTION covers REQUEST_EXCEPTION but not the other way around
     * @param other NotificationType
     * @return boolean
     */
    public boolean covers(NotificationType other) {
        return other != null && (this.equals(other) || covers(other.parent));
    }

    public Set<NotificationType> getChildren() {
        return children;
    }

    private Map<NotificationType, Object> getTypeHierarchy() {
        Map<NotificationType, Object> hierarchyMap = new TreeMap<>();
        children.forEach(child -> hierarchyMap.put(child, child.getTypeHierarchy()));
        return hierarchyMap;
    }

    /**
     * Returns a set of all notification types covered by the given notification type
     * @param type NotificationType
     * @return Set<NotificationType>
     */
    public static Set<NotificationType> getCoverage(NotificationType type) {
        if (type == null) return Collections.emptySet();
        Set<NotificationType> result = Sets.newHashSet(type);
        type.children.stream()
                .map(NotificationType::getCoverage)
                .forEach(result::addAll);
        return result;
    }

    public static NotificationType getValue(String string) {
        return NotificationType.valueOf(StringUtils.upperCase(string));
    }

    public static ImmutableSet<NotificationType> getAllNotificationTypes() {
        return ALL_NOTIFICATION_TYPES;
    }

    public static Map<NotificationType, Object> getHierarchy() {
        return ImmutableMap.of(ALL, ALL.getTypeHierarchy());
    }
}
