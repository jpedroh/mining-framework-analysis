package xdi2.messaging.target.interceptor.impl.authentication.secrettoken;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.Literal;
import xdi2.core.constants.XDIAuthenticationConstants;
import xdi2.core.exceptions.Xdi2RuntimeException;
import xdi2.core.xri3.XDI3Segment;
import xdi2.messaging.Message;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.impl.graph.GraphMessagingTarget;

/**
 * A SecretTokenAuthenticator that can authenticate a secret token against
 * digested secret tokens which are stored in a dedicated graph.
 */
public class GraphSecretTokenAuthenticator extends DigestSecretTokenAuthenticator {

	private Graph secretTokenGraph;

	public GraphSecretTokenAuthenticator(String globalHash, Graph secretTokenGraph) {

		super(globalHash);

		this.secretTokenGraph = secretTokenGraph;
	}

	public GraphSecretTokenAuthenticator() {

		super();
	}

	@Override
	public SecretTokenAuthenticator instanceFor(xdi2.messaging.target.Prototype.PrototypingContext prototypingContext) throws Xdi2MessagingException {

		// create new secret token authenticator

		GraphSecretTokenAuthenticator authenticator = new GraphSecretTokenAuthenticator();

		// set the global hash

		authenticator.setGlobalSalt(this.getGlobalSalt());

		// set the secret token graph

		if (this.getSecretTokenGraph() == null) {

			if (prototypingContext.getMessagingTarget() instanceof GraphMessagingTarget) {

				authenticator.setSecretTokenGraph(((GraphMessagingTarget) prototypingContext.getMessagingTarget()).getGraph());
			} else {

				throw new Xdi2RuntimeException("No secret token graph.");
			}
		} else {

			authenticator.setSecretTokenGraph(this.getSecretTokenGraph());
		}

		// done

		return authenticator;
	}

	@Override
	public boolean authenticate(Message message, String secretToken) {

		XDI3Segment fromAddress = message.getFromAddress();
		if (fromAddress == null) return false;

		// look for local salt and digest secret token in the graph

		ContextNode fromAddressContextNode = this.getSecretTokenGraph().getDeepContextNode(fromAddress);
		Literal localSaltAndDigestSecretTokenLiteral = fromAddressContextNode == null ? null : fromAddressContextNode.getDeepLiteral(XDIAuthenticationConstants.XRI_S_DIGEST_SECRET_TOKEN);
		if (localSaltAndDigestSecretTokenLiteral == null) return false;

		String localSaltAndDigestSecretToken = localSaltAndDigestSecretTokenLiteral.getLiteralData();

		// authenticate

		return super.authenticate(localSaltAndDigestSecretToken, secretToken);
	}

	public Graph getSecretTokenGraph() {

		return this.secretTokenGraph;
	}

	public void setSecretTokenGraph(Graph secretTokenGraph) {

		this.secretTokenGraph = secretTokenGraph;
	}
}