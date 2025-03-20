package com.jcabi.github.mock;
import com.jcabi.aspects.Immutable;
import com.jcabi.github.GitHubThread;
import com.jcabi.github.Notification;
import com.jcabi.github.Notifications;
import org.apache.commons.lang3.NotImplementedException;

/**
 * Mock for Github Notifications.
 *
 * @author Giang Le (lthuangiang@gmail.com)
 * @author Paul Polishchuk (ppol@ua.fm)
 * @version $Id$
 * @since 0.15
 * @see <a href="https://developer.github.com/v3/activity/notifications/">Notifications API</a>
 * @todo #920 Implement iterate() and get() operations in MkNotifications.
 *  Don't forget about unit tests.
 * @todo #913:30min Implement markAsRead() and thread() operations in
 *  MkNotifications. Don't forget about unit tests.
 */
@Immutable final class MkNotifications implements Notifications {
  @Override public Iterable<Notification> iterate() {
    throw new NotImplementedException("MkNotifications#iterate");
  }

  @Override public Notification get(final int number) {
    throw new NotImplementedException("MkNotifications#get");
  }

  @Override public void markAsRead() {
    throw new NotImplementedException("MkNotifications#markAsRead");
  }

  @Override public GitHubThread thread(final int number) {
    throw new NotImplementedException("MkNotifications#thread");
  }
}