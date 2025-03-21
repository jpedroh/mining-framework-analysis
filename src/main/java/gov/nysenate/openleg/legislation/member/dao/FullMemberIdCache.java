package gov.nysenate.openleg.legislation.member.dao;
import gov.nysenate.openleg.legislation.CacheType;
import gov.nysenate.openleg.common.util.RegexUtils;
import gov.nysenate.openleg.legislation.*;
import gov.nysenate.openleg.legislation.member.FullMember;
import gov.nysenate.openleg.notifications.model.Notification;
import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.function.Function;
import java.time.LocalDateTime;
import java.util.stream.Collectors;
import static gov.nysenate.openleg.notifications.model.NotificationType.BAD_MEMBER_NAME;

@Component final class FullMemberIdCache extends AbstractMemberCache<Integer, FullMember> {
  public FullMemberIdCache(MemberDao memberDao) {
    super(memberDao);
  }

  @Override protected CacheType cacheType() {
    return CacheType.FULL_MEMBER;
  }

  @Override public Map<Integer, FullMember> initialEntries() {
    return memberDao.getAllFullMembers().stream().collect(Collectors.toMap(FullMember::getMemberId, Function.identity()));
  }

  @Override protected FullMember getMemberFromDao(Integer memberId) {
    return memberDao.getMemberById(memberId);
  }


<<<<<<< Unknown file: This is a bug in JDime.
=======
  private void putMemberInCache(FullMember member) {
    memberCache.put(new Element(new SimpleKey(member.getMemberId()), member, true));
    String expectedShortname = RegexUtils.removeAccentedCharacters(member.getLastName()).toUpperCase();
    char firstInitial = member.getFirstName().charAt(0);
    String namePattern = "(%s)( %c.?)?".formatted(expectedShortname, firstInitial);
    String currShortname = member.getLatestSessionMember().orElse(new SessionMember()).getLbdcShortName();
    if (!currShortname.matches(namePattern)) {
      eventBus.post(new Notification(BAD_MEMBER_NAME, LocalDateTime.now(), "There is a member name mismatch.", "Member " + member.getFullName() + "\'s last name doesn\'t match their most recent session member."));
    }
  }
>>>>>>> /usr/src/app/output/nysenate/openlegislation/55f02d1256aabf0841995fe75f1beb820ed4c6bc/src/main/java/gov/nysenate/openleg/legislation/member/dao/FullMemberIdCache.java/right.java
}