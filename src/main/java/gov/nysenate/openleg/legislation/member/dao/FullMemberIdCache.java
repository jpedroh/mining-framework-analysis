package gov.nysenate.openleg.legislation.member.dao;

import gov.nysenate.openleg.legislation.CacheType;
import gov.nysenate.openleg.legislation.member.FullMember;
import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.function.Function;
import gov.nysenate.openleg.common.util.RegexUtils;
import gov.nysenate.openleg.legislation.*;
import gov.nysenate.openleg.legislation.committee.MemberNotFoundEx;
import gov.nysenate.openleg.notifications.model.Notification;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

import static gov.nysenate.openleg.notifications.model.NotificationType.BAD_MEMBER_NAME;

@Component
final class FullMemberIdCache extends AbstractMemberCache<Integer, FullMember> {

    public FullMemberIdCache(MemberDao memberDao) {
        super(memberDao);
    }

    @Override
    protected CacheType cacheType() {
        return CacheType.FULL_MEMBER;
    }

    @Override
    public Map<Integer, FullMember> initialEntries() {
        return memberDao.getAllFullMembers().stream()
                .collect(Collectors.toMap(FullMember::getMemberId, Function.identity()));
    }

    @Override
    protected FullMember getMemberFromDao(Integer memberId) {
        return memberDao.getMemberById(memberId);
    }

    /**
     * {@inheritDoc}
     */

    /**
     * {@inheritDoc}
     */

    /* --- Internal Methods --- */

    private void putMemberInCache(FullMember member) {
        memberCache.put(new Element(new SimpleKey(member.getMemberId()), member, true));
        // Tests for consistency between the person's last name, and their most recent shortname
        // (which is in all caps and has accents removed).
        String expectedShortname = RegexUtils.removeAccentedCharacters(member.getLastName())
                .toUpperCase();
        char firstInitial = member.getFirstName().charAt(0);
        // The shortname may have the first and middle initial appended to it.
        String namePattern = "(%s)( %c.?)?".formatted(expectedShortname, firstInitial);
        String currShortname = member.getLatestSessionMember()
                .orElse(new SessionMember()).getLbdcShortName();
        if (!currShortname.matches(namePattern)) {
            eventBus.post(new Notification(BAD_MEMBER_NAME, LocalDateTime.now(),
                    "There is a member name mismatch.",
                    "Member " + member.getFullName() + "'s last name doesn't match their most recent session member."));
        }
    }
}
