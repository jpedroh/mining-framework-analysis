package net.java.otr4j;
import java.security.KeyPair;
import net.java.otr4j.session.FragmenterInstructions;
import net.java.otr4j.session.InstanceTag;
import net.java.otr4j.session.SessionID;

/**
 * This interface should be implemented by the host application. It is required
 * for otr4j to work properly.
 *
 * @author George Politis
 */
public interface OtrEngineHost {
  void injectMessage(SessionID sessionID, String msg) throws OtrException;

  void unreadableMessageReceived(SessionID sessionID) throws OtrException;

  void unencryptedMessageReceived(SessionID sessionID, String msg) throws OtrException;

  void showError(SessionID sessionID, String error) throws OtrException;

  void smpError(SessionID sessionID, int tlvType, boolean cheated) throws OtrException;

  void smpAborted(SessionID sessionID) throws OtrException;

  void finishedSessionMessage(SessionID sessionID, String msgText) throws OtrException;

  void requireEncryptedMessage(SessionID sessionID, String msgText) throws OtrException;

  OtrPolicy getSessionPolicy(SessionID sessionID);

  /**
	 * Get instructions for the necessary fragmentation operations.
	 *
	 * If no fragmentation is necessary, return <code>null</code> to set the default
	 * fragmentation instructions which are to use an unlimited number of
	 * messages of unlimited size each. Hence fragmentation is not necessary or
	 * applied.
	 *
	 * @param sessionID
	 *            the session ID of the session
	 * @return return fragmentation instructions or null for defaults (i.e. no
	 *         fragmentation)
	 */
  FragmenterInstructions getFragmenterInstructions(SessionID sessionID);

  KeyPair getLocalKeyPair(SessionID sessionID) throws OtrException;

  byte[] getLocalFingerprintRaw(SessionID sessionID);

  void askForSecret(SessionID sessionID, InstanceTag receiverTag, String question);

  void verify(SessionID sessionID, String fingerprint, boolean approved);

  void unverify(SessionID sessionID, String fingerprint);

  String getReplyForUnreadableMessage(SessionID sessionID);

  String getFallbackMessage(SessionID sessionID);

  void messageFromAnotherInstanceReceived(SessionID sessionID);

  void multipleInstancesDetected(SessionID sessionID);
}