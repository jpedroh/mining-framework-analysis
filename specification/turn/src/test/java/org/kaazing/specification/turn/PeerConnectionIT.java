package org.kaazing.specification.turn;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.rules.RuleChain.outerRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.DisableOnDebug;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;
import org.kaazing.k3po.junit.annotation.Specification;
import org.kaazing.k3po.junit.rules.K3poRule;

/**
 * Test to validate behavior as specified in <a href="https://tools.ietf.org/html/rfc5766">RFC 5766: TURN</a> through TCP.
 */
public class PeerConnectionIT {
  private final K3poRule k3po = new K3poRule().setScriptRoot("org/kaazing/specification/turn/peer.connection");

  private final TestRule timeout = new DisableOnDebug(new Timeout(5, SECONDS));

  @Rule public final TestRule chain = outerRule(k3po).around(timeout);

  /**
     * See <a href="https://tools.ietf.org/html/rfc5766">RFC 5766: Turn Protocol</a>.
     */
  @Test @Specification(value = { "correct.turn.protocol/request", "correct.turn.protocol/response" }) public void shouldSucceedWithCorrectTURNProcess() throws Exception {
    k3po.finish();
  }

  /**
     * See <a href="https://tools.ietf.org/html/rfc5766">RFC 5766: Turn Protocol</a>.
     */
  @Test @Specification(value = { "no.peer.address.with.permissions.responds.400/request", "no.peer.address.with.permissions.responds.400/response" }) public void shouldRespond400ToNoPeerAddressInPermissionRequest() throws Exception {
    k3po.finish();
  }

  /**
     * See <a href="https://tools.ietf.org/html/rfc5766">RFC 5766: Turn Protocol</a>.
     */
  @Test @Specification(value = { "no.channel.number.with.binding.request.responds.400/request", "no.channel.number.with.binding.request.responds.400/response" }) public void shouldRespond400ToNoChannelNumberInBindingRequest() throws Exception {
    k3po.finish();
  }

  /**
     * See <a href="https://tools.ietf.org/html/rfc5766">RFC 5766: Turn Protocol</a>.
     */
  @Test @Specification(value = { "no.peer.address.with.binding.request.responds.400/request", "no.peer.address.with.binding.request.responds.400/response" }) public void shouldRespond400ToNoPeerAddressInBindingRequest() throws Exception {
    k3po.finish();
  }

  /**
     * See <a href="https://tools.ietf.org/html/rfc5766">RFC 5766: Turn Protocol</a>.
     */
  @Test @Specification(value = { "invalid.channel.number.responds.400/request", "invalid.channel.number.responds.400/response" }) public void shouldRespond400ToInvalidChannelNumber() throws Exception {
    k3po.finish();
  }

  /**
     * See <a href="https://tools.ietf.org/html/rfc5766">RFC 5766: Turn Protocol</a>.
     */
  @Test @Specification(value = { "correct.turn.protocol.with.sent.data/request", "correct.turn.protocol.with.sent.data/response" }) public void shouldSuccessfullySendData() throws Exception {
    k3po.finish();
  }

  /**
     * See <a href="https://tools.ietf.org/html/rfc5766">RFC 5766: Turn Protocol</a>.
     */
  @Test @Specification(value = { "correct.turn.protocol.with.sent.data.message/request", "correct.turn.protocol.with.sent.data.message/response" }) public void shouldSuccessfullySendDataMessage() throws Exception {
    k3po.finish();
  }

  /**
     * See <a href="https://tools.ietf.org/html/rfc5766">RFC 5766: Turn Protocol</a>.
     */
  @Test @Specification(value = { "turn.protocol.with.invalid.requested.transport.attribute/request", "turn.protocol.with.invalid.requested.transport.attribute/response" }) public void shouldFailSendingData() throws Exception {
    k3po.finish();
  }

  /**
     * See <a href="https://tools.ietf.org/html/rfc5766">RFC 5766: Turn Protocol</a>.
     */
  @Test @Specification(value = { "incorrect.channel.data.message.short.length/request", "incorrect.channel.data.message.short.length/response" }) public void shouldFailSendingDataMessageTooShortLength() throws Exception {
    k3po.finish();
  }

  /**
     * See <a href="https://tools.ietf.org/html/rfc5766">RFC 5766: Turn Protocol</a>.
     */
  @Test @Specification(value = { "incorrect.channel.data.message.wrong.channel.number/request", "incorrect.channel.data.message.wrong.channel.number/response" }) public void shouldFailSendingDataMessageWrongChannelNumber() throws Exception {
    k3po.finish();
  }

  /**
     * See <a href="https://tools.ietf.org/html/rfc5766">RFC 5766: Turn Protocol</a>.
     */
  @Test @Specification(value = { "channel.data.message.without.create.permissions/request", "channel.data.message.without.create.permissions/response" }) public void shouldFailSendingDataMessageWithoutPermissions() throws Exception {
    k3po.finish();
  }

  /**
     * See <a href="https://tools.ietf.org/html/rfc5766">RFC 5766: Turn Protocol</a>.
     */
  @Test @Specification(value = { "send.indication.message.without.create.permissions/request", "send.indication.message.without.create.permissions/response" }) public void shouldFailSendingIndicationMessageWithoutPermissions() throws Exception {
    k3po.finish();
  }

  /**
     * See <a href="https://tools.ietf.org/html/rfc5766">RFC 5766: Turn Protocol</a>.
     */
  @Test @Specification(value = { "correct.turn.protocol.with.both.types.of.message/request", "correct.turn.protocol.with.both.types.of.message/response" }) public void shouldSucceedSendingIndicationAndChannelDataMessages() throws Exception {
    k3po.finish();
  }

  /**
     * See <a href="https://tools.ietf.org/html/rfc5766">RFC 5766: Turn Protocol</a>.
     */
  @Test @Specification(value = { "incorrect.channel.data.message.reserved.channel.number/request", "incorrect.channel.data.message.reserved.channel.number/response" }) public void shouldFailSendingDataMessageReservedChannelNumbber() throws Exception {
    k3po.finish();
  }

  /**
     * See <a href="https://tools.ietf.org/html/rfc5766">RFC 5766: Turn Protocol</a>.
     */
  @Test @Specification(value = { "send.indication.with.invalid.xor.peer.address/request", "send.indication.with.invalid.xor.peer.address/response" }) public void shouldFailSendingIndicationWithInvalidPeerAddress() throws Exception {
    k3po.finish();
  }

  /**
     * See <a href="https://tools.ietf.org/html/rfc5766">RFC 5766: Turn Protocol</a>.
     */
  @Test @Specification(value = { "send.indication.without.allocation/request", "send.indication.without.allocation/response" }) public void shouldFailSendingIndicationWithoutAllocation() throws Exception {
    k3po.finish();
  }

  /**
     * See <a href="https://tools.ietf.org/html/rfc5766">RFC 5766: Turn Protocol</a>.
     */
  @Test @Specification(value = { "send.indication.without.channel.bind/request", "send.indication.without.channel.bind/response" }) public void shouldFailSendingIndicationWithoutChannelBind() throws Exception {
    k3po.finish();
  }
}