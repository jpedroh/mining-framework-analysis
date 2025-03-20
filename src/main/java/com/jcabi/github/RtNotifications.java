package com.jcabi.github;
import com.jcabi.aspects.Immutable;
import java.util.Collections;
import org.apache.commons.lang3.NotImplementedException;

/**
 * Github Notifications API.
 *
 * @author Giang Le (lthuangiang@gmail.com)
 * @author Paul Polishchuk (ppol@ua.fm)
 * @version $Id$
 * @since 0.15
 * @see <a href="https://developer.github.com/v3/activity/notifications/">Notifications API</a>
 * @todo #913:30min Implement markAsRead(), thread(final int number) operations
 *  in RtNotifications. Don't forget about unit tests.
 */
@Immutable final class RtNotifications implements Notifications {
  @Override public Iterable<Notification> iterate() {
    return Collections.emptyList();
  }

  @Override public Notification get(final int number) {
    return new RtNotification(number);
  }

  @Override public void markAsRead() {
    throw new NotImplementedException("RtNotifications#markAsRead");
  }

  @Override public GitHubThread thread(final int number) {
    throw new NotImplementedException("RtNotifications#thread");
  }
}