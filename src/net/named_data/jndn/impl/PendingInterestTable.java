package net.named_data.jndn.impl;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.named_data.jndn.Interest;
import net.named_data.jndn.Data;
import net.named_data.jndn.ExpressFailureReason;
import net.named_data.jndn.OnData;
import net.named_data.jndn.OnExpressFailure;
import net.named_data.jndn.OnNetworkNack;
import net.named_data.jndn.encoding.EncodingException;
import net.named_data.jndn.util.Common;
import net.named_data.jndn.util.SignedBlob;

/**
 * A PendingInterestTable is an internal class to hold a list of pending
 * interests with their callbacks.
 */
public class PendingInterestTable {
  public static class Entry {
    /**
     * Create a new Entry with the given fields. Note: You should not call this
     * directly but call PendingInterestTable.add.
     */
    public Entry(long pendingInterestId, Interest interest, OnData onData, OnExpressFailure onExpressFailure, OnNetworkNack onNetworkNack) {
      pendingInterestId_ = pendingInterestId;
      interest_ = interest;
      onData_ = onData;
      onExpressFailure_ = onExpressFailure;
      onNetworkNack_ = onNetworkNack;
    }

    /**
     * Get the pendingInterestId given to the constructor.
     * @return The pendingInterestId.
     */
    public final long getPendingInterestId() {
      return pendingInterestId_;
    }

    /**
     * Get the interest given to the constructor (from Face.expressInterest).
     * @return The interest. NOTE: You must not change the interest object - if
     * you need to change it then make a copy.
     */
    public final Interest getInterest() {
      return interest_;
    }

    /**
     * Get the OnData callback given to the constructor.
     * @return The OnData callback.
     */
    public final OnData getOnData() {
      return onData_;
    }

    /**
     * Get the OnNetworkNack callback given to the constructor.
     * @return The OnNetworkNack callback.
     */
    public final OnNetworkNack getOnNetworkNack() {
      return onNetworkNack_;
    }

    /**
     * Set the isRemoved flag which is returned by getIsRemoved().
     */
    public final void setIsRemoved() {
      isRemoved_ = true;
    }

    /**
     * Check if setIsRemoved() was called.
     * @return True if setIsRemoved() was called.
     */
    public final boolean getIsRemoved() {
      return isRemoved_;
    }

    /**
     * Call onExpressFailure_ (if defined) with ExpressFailureReason.TIMEOUT.
     * This ignores exceptions from the call to onExpressFailure_.
     */
    public final void callTimeout() {
      if (onExpressFailure_ != null) {
        try {
          onExpressFailure_.onExpressFailure(interest_, ExpressFailureReason.TIMEOUT, new Exception("Interest timeout"));
        } catch (Throwable ex) {
          logger_.log(Level.SEVERE, "Error in onTimeout", ex);
        }
      }
    }

    private final Interest interest_;

    private final long pendingInterestId_;

    /**< A unique identifier for this entry so it can be deleted */
    private final OnData onData_;

    private final OnExpressFailure onExpressFailure_;

    private final OnNetworkNack onNetworkNack_;

    private boolean isRemoved_ = false;
  }

  /**
   * Add a new entry to the pending interest table. However, if
   * removePendingInterest was already called with the pendingInterestId, don't
   * add an entry and return null.
   * @param pendingInterestId The getNextEntryId() for the pending interest ID
   * which Face got so it could return it to the caller.
   * @param interestCopy The Interest which was sent, which has already been
   * copied by expressInterest.
   * @param onData Call onData.onData when a matching data packet is
   * received.
   * @param onExpressFailure If the interest times out according to the interest
   * lifetime, this calls
   * onExpressFailure.onExpressFailure(interest, ExpressFailureReason.TIMEOUT, details)
   * where interest is the interest given to expressInterest and details is an
   * Exception with an error message. If onExpressFailure is null, this does not
   * use it.
   * @param onNetworkNack Call onNetworkNack.onNetworkNack when a network Nack
   * packet is received.
   * @return The new PendingInterestTable.Entry, or null if
   * removePendingInterest was already called with the pendingInterestId.
   */
  public synchronized final Entry add(long pendingInterestId, Interest interestCopy, OnData onData, OnExpressFailure onExpressFailure, OnNetworkNack onNetworkNack) {
    int removeRequestIndex = removeRequests_.indexOf(pendingInterestId);
    if (removeRequestIndex >= 0) {
      removeRequests_.remove(removeRequestIndex);
      return null;
    }
    Entry entry = new Entry(pendingInterestId, interestCopy, onData, onExpressFailure, onNetworkNack);
    table_.add(entry);
    return entry;
  }

  /**
   * Find all entries from the pending interest table where data conforms to
   * the entry's interest selectors, remove the entries from the table, set each
   * entry's isRemoved flag, and add to the entries list.
   * @param data The incoming Data packet to find the interest for.
   * @param entries Add matching PendingInterestTable.Entry from the pending
   * interest table.  The caller should pass in an empty ArrayList.
   */
  public synchronized final void extractEntriesForExpressedInterest(Data data, ArrayList<Entry> entries) throws EncodingException {
    for (int i = table_.size() - 1; i >= 0; --i) {
      Entry pendingInterest = table_.get(i);
      if (pendingInterest.getInterest().matchesData(data)) {
        entries.add(table_.get(i));
        table_.remove(i);
        pendingInterest.setIsRemoved();
      }
    }
  }

  /**
   * Find all entries from the pending interest table where the OnNetworkNack
   * callback is not null and the entry's interest is the same as the given
   * interest, remove the entries from the table, set each entry's isRemoved
   * flag, and add to the entries list. (We don't remove the entry if the
   * OnNetworkNack callback is null so that OnTimeout will be called later.) The
   * interests are the same if their default wire encoding is the same (which
   * has everything including the name, nonce, link object and selectors).
   * @param interest The Interest to search for (typically from a Nack packet).
   * @param entries Add matching PendingInterestTable.Entry from the pending
   * interest table. The caller should pass in an empty ArrayList.
   */
  public synchronized final void extractEntriesForNackInterest(Interest interest, ArrayList<Entry> entries) {
    SignedBlob encoding = interest.wireEncode();
    for (int i = table_.size() - 1; i >= 0; --i) {
      Entry pendingInterest = table_.get(i);
      if (pendingInterest.getOnNetworkNack() == null) {
        continue;
      }
      if (pendingInterest.getInterest().wireEncode().equals(encoding)) {
        entries.add(table_.get(i));
        table_.remove(i);
        pendingInterest.setIsRemoved();
      }
    }
  }

  /**
   * Remove the pending interest entry with the pendingInterestId from the
   * pending interest table and set its isRemoved flag. This does not affect
   * another pending interest with a different pendingInterestId, even if it has
   * the same interest name. If there is no entry with the pendingInterestId, do
   * nothing.
   * @param pendingInterestId The ID returned from expressInterest.
   */
  public synchronized final void removePendingInterest(long pendingInterestId) {
    int count = 0;
    for (int i = table_.size() - 1; i >= 0; --i) {
      if ((table_.get(i)).getPendingInterestId() == pendingInterestId) {
        ++count;
        (table_.get(i)).setIsRemoved();
        table_.remove(i);
      }
    }
    if (count == 0) {
      logger_.log(Level.WARNING, "removePendingInterest: Didn\'t find pendingInterestId {0}", pendingInterestId);
    }
    if (count == 0) {
      if (removeRequests_.indexOf(pendingInterestId) < 0) {
        removeRequests_.add(pendingInterestId);
      }
    }
  }

  /**
   * Remove the specific pendingInterest entry from the table and set its
   * isRemoved flag. However, if the pendingInterest isRemoved flag is already
   * true or the entry is not in the pending interest table then do nothing.
   * @param pendingInterest The Entry from the pending interest table.
   * @return True if the entry was removed, false if not.
   */
  public synchronized final boolean removeEntry(Entry pendingInterest) {
    if (pendingInterest.getIsRemoved()) {
      return false;
    }
    if (table_.remove(pendingInterest)) {
      pendingInterest.setIsRemoved();
      return true;
    } else {
      return false;
    }
  }

  private final ArrayList<Entry> table_ = new ArrayList<Entry>();

  private final ArrayList<Long> removeRequests_ = new ArrayList<Long>();

  private static final Logger logger_ = Logger.getLogger(PendingInterestTable.class.getName());

  private static Common dummyCommon_ = new Common();
}