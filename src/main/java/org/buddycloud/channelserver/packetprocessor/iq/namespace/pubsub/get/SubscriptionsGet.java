package org.buddycloud.channelserver.packetprocessor.iq.namespace.pubsub.get;

import java.util.concurrent.BlockingQueue;

import org.buddycloud.channelserver.Configuration;
import org.buddycloud.channelserver.channel.ChannelManager;
import org.buddycloud.channelserver.db.exception.NodeStoreException;
import org.buddycloud.channelserver.packetprocessor.iq.namespace.pubsub.JabberPubsub;
import org.buddycloud.channelserver.packetprocessor.iq.namespace.pubsub.PubSubElementProcessorAbstract;
import org.buddycloud.channelserver.pubsub.model.NodeMembership;
import org.buddycloud.channelserver.pubsub.subscription.Subscriptions;
import org.buddycloud.channelserver.utils.XMLConstants;
import org.buddycloud.channelserver.utils.node.item.payload.Buddycloud;
import org.dom4j.Element;
import org.xmpp.packet.IQ;
import org.xmpp.packet.JID;
import org.xmpp.packet.Packet;
import org.xmpp.resultsetmanagement.ResultSet;

public class SubscriptionsGet extends PubSubElementProcessorAbstract {

    private final BlockingQueue<Packet> outQueue;
    private ChannelManager channelManager;
    
    private IQ result;
    private String node;
    private JID actorJid;
    private IQ requestIq;


    public SubscriptionsGet(BlockingQueue<Packet> outQueue,
            ChannelManager channelManager) {
        this.outQueue = outQueue;
        this.channelManager = channelManager;
    }

    public void setChannelManager(ChannelManager dataStore) {
        channelManager = dataStore;
    }


    @Override
    public void process(Element elm, JID actorJID, IQ reqIQ, Element rsm)
            throws Exception {
        result = IQ.createResultIQ(reqIQ);
        actorJid = actorJID;
        requestIq = reqIQ;
        
        Element pubsub = result.setChildElement(XMLConstants.PUBSUB_ELEM,
                JabberPubsub.NAMESPACE_URI);
        Element subscriptions = pubsub.addElement(XMLConstants.SUBSCRIPTIONS_ELEM);

        node = reqIQ.getChildElement().element(XMLConstants.SUBSCRIPTIONS_ELEM).attributeValue(XMLConstants.NODE_ATTR);

        if (null == actorJid) {
            actorJid = reqIQ.getFrom();
        }
        
        boolean isProcessedLocally = true;

        if (node == null) {
            isProcessedLocally = getUserMemberships(subscriptions);
        } else {
<<<<<<< /usr/src/app/output/buddycloud/buddycloud-server-java/e746268967301a644cf186725205f1d823a45a83/src/main/java/org/buddycloud/channelserver/packetprocessor/iq/namespace/pubsub/get/SubscriptionsGet.java/left.java
            if (!channelManager.isLocalNode(node)) {
||||||| /usr/src/app/output/buddycloud/buddycloud-server-java/e746268967301a644cf186725205f1d823a45a83/src/main/java/org/buddycloud/channelserver/packetprocessor/iq/namespace/pubsub/get/SubscriptionsGet.java/base.java
            if (false == channelManager.isLocalNode(node)) {
=======
            if (false == Configuration.getInstance().isLocalNode(node)) {
>>>>>>> /usr/src/app/output/buddycloud/buddycloud-server-java/e746268967301a644cf186725205f1d823a45a83/src/main/java/org/buddycloud/channelserver/packetprocessor/iq/namespace/pubsub/get/SubscriptionsGet.java/right.java
                result.getElement().addAttribute(XMLConstants.REMOTE_SERVER_DISCOVER_ATTR, Boolean.FALSE.toString());
            }
            isProcessedLocally = getNodeMemberships(subscriptions);
        }
        if (!isProcessedLocally) {
            return;
        }
        
        outQueue.put(result);
    }

    /**
     * Don't include any subscriptions other than Subscriptions.subscriptions, unless the user is
     * one of owner, moderator or the user.
     * 
     * @param subscriptions
     * @return
     * @throws NodeStoreException
     * @throws InterruptedException
     */
    private boolean getNodeMemberships(Element subscriptions)
            throws NodeStoreException, InterruptedException {
<<<<<<< /usr/src/app/output/buddycloud/buddycloud-server-java/e746268967301a644cf186725205f1d823a45a83/src/main/java/org/buddycloud/channelserver/packetprocessor/iq/namespace/pubsub/get/SubscriptionsGet.java/left.java
||||||| /usr/src/app/output/buddycloud/buddycloud-server-java/e746268967301a644cf186725205f1d823a45a83/src/main/java/org/buddycloud/channelserver/packetprocessor/iq/namespace/pubsub/get/SubscriptionsGet.java/base.java
        if (false == channelManager.isLocalNode(node) && (false == channelManager.isCachedNode(node))) {
            makeRemoteRequest(new JID(node.split("/")[2]).getDomain());
            return false;
        }
        ResultSet<NodeMembership> cur;
        cur = channelManager.getNodeMemberships(node);
=======
        if (false == Configuration.getInstance().isLocalNode(node) 
            && (false == channelManager.isCachedNode(node))
        ) {
            makeRemoteRequest(new JID(node.split("/")[2]).getDomain());
            return false;
        }
        ResultSet<NodeMembership> cur;
        cur = channelManager.getNodeMemberships(node);
>>>>>>> /usr/src/app/output/buddycloud/buddycloud-server-java/e746268967301a644cf186725205f1d823a45a83/src/main/java/org/buddycloud/channelserver/packetprocessor/iq/namespace/pubsub/get/SubscriptionsGet.java/right.java

        ResultSet<NodeMembership> cur = channelManager.getNodeMemberships(node);

<<<<<<< /usr/src/app/output/buddycloud/buddycloud-server-java/e746268967301a644cf186725205f1d823a45a83/src/main/java/org/buddycloud/channelserver/packetprocessor/iq/namespace/pubsub/get/SubscriptionsGet.java/left.java
        if (channelManager.isLocalNode(node)) {

            subscriptions.addAttribute(XMLConstants.NODE_ATTR, node);

            for (NodeMembership ns : cur) {
                if (!isUserPriviledged(ns.getUser()) && !ns.getSubscription().equals(Subscriptions.subscribed)) {
||||||| /usr/src/app/output/buddycloud/buddycloud-server-java/e746268967301a644cf186725205f1d823a45a83/src/main/java/org/buddycloud/channelserver/packetprocessor/iq/namespace/pubsub/get/SubscriptionsGet.java/base.java
        if ((null != requestIq.getElement().element("pubsub").element("set")) && (0 == cur.size()) && (false == channelManager.isLocalNode(node))) {
            makeRemoteRequest(new JID(node.split("/")[2]).getDomain());
            return false;
        }
        boolean isOwnerModerator = isOwnerModerator();
        for (NodeMembership ns : cur) {
            if (false == actorJid.toBareJID().equals(ns.getUser())) {
                if ((false == isOwnerModerator) && ns.getAffiliation().in(Affiliations.outcast, Affiliations.none)) {
=======
        if ((null != requestIq.getElement().element("pubsub").element("set"))
                && (0 == cur.size())
                && (false == Configuration.getInstance().isLocalNode(node))) {
            makeRemoteRequest(new JID(node.split("/")[2]).getDomain());
            return false;
        }
        boolean isOwnerModerator = isOwnerModerator();
        for (NodeMembership ns : cur) {
            if (false == actorJid.toBareJID().equals(ns.getUser())) {
                if ((false == isOwnerModerator) && ns.getAffiliation().in(Affiliations.outcast, Affiliations.none)) {
>>>>>>> /usr/src/app/output/buddycloud/buddycloud-server-java/e746268967301a644cf186725205f1d823a45a83/src/main/java/org/buddycloud/channelserver/packetprocessor/iq/namespace/pubsub/get/SubscriptionsGet.java/right.java
                    continue;
                }

                Element subscription = subscriptions.addElement(XMLConstants.SUBSCRIPTION_ELEM);
                subscription.addAttribute(XMLConstants.NODE_ATTR, ns.getNodeId())
                        .addAttribute(XMLConstants.SUBSCRIPTION_ELEM, ns.getSubscription().toString())
                        .addAttribute(XMLConstants.JID_ATTR, ns.getUser().toBareJID());

                // Only add the invited by attribute if the user has the necessary priviledges
                if (null != ns.getInvitedBy() && isUserPriviledged(ns.getUser())) {
                    subscription.addAttribute(XMLConstants.INVITED_BY_ELEM, ns.getInvitedBy().toBareJID());
                }

            }
<<<<<<< /usr/src/app/output/buddycloud/buddycloud-server-java/e746268967301a644cf186725205f1d823a45a83/src/main/java/org/buddycloud/channelserver/packetprocessor/iq/namespace/pubsub/get/SubscriptionsGet.java/left.java
        } else {
            // TODO(garethf) this looks like makeRemoteRequest
            if (!channelManager.isCachedNode(node) || (null != requestIq.getElement().element(XMLConstants.PUBSUB_ELEM).element(XMLConstants.SET_ELEM))
                    && !cur.isEmpty()) {
                makeRemoteRequest(new JID(node.split("/")[2]).getDomain());
||||||| /usr/src/app/output/buddycloud/buddycloud-server-java/e746268967301a644cf186725205f1d823a45a83/src/main/java/org/buddycloud/channelserver/packetprocessor/iq/namespace/pubsub/get/SubscriptionsGet.java/base.java
            Element subscription = subscriptions.addElement("subscription");
            subscription.addAttribute("node", ns.getNodeId()).addAttribute("subscription", ns.getSubscription().toString())
                    .addAttribute("jid", ns.getUser().toBareJID());
            if (null != ns.getInvitedBy()) {
                subscription.addAttribute("invited-by", ns.getInvitedBy().toBareJID());
=======
            Element subscription = subscriptions
                    .addElement("subscription");
            subscription
                    .addAttribute("node", ns.getNodeId())
                    .addAttribute("subscription",
                            ns.getSubscription().toString())
                    .addAttribute("jid", ns.getUser().toBareJID());
            if (null != ns.getInvitedBy()) {
                subscription.addAttribute("invited-by", ns.getInvitedBy().toBareJID());
>>>>>>> /usr/src/app/output/buddycloud/buddycloud-server-java/e746268967301a644cf186725205f1d823a45a83/src/main/java/org/buddycloud/channelserver/packetprocessor/iq/namespace/pubsub/get/SubscriptionsGet.java/right.java
            }
            return false;
        }

        return true;
    }

    /**
     * Don't include any subscriptions other than Subscriptions.subscriptions, unless the user is
     * one of owner, moderator or the user.
     * 
     * @param ns
     * 
     * @return
     * @throws NodeStoreException
     */
    private boolean isUserPriviledged(JID jid) throws NodeStoreException {
        boolean isUser = actorJid.toBareJID().equals(jid.toBareJID());

        return (isUser || isOwnerModerator()) ? true : false;
    }

    private boolean getUserMemberships(Element subscriptions) throws NodeStoreException, InterruptedException {
        // let's get all subscriptions.
        ResultSet<NodeMembership> cur;
        cur = channelManager.getUserMemberships(actorJid);

        if ((null != requestIq.getElement().element(XMLConstants.PUBSUB_ELEM).element("set")) && (!cur.isEmpty())
                && (!Configuration.getInstance().isLocalJID(actorJid))) {
            makeRemoteRequest(actorJid.getDomain());
            return false;
        }
        for (NodeMembership ns : cur) {
            Element subscription = subscriptions.addElement(XMLConstants.SUBSCRIPTION_ELEM);
            subscription.addAttribute(XMLConstants.NODE_ATTR, ns.getNodeId())
                    .addAttribute(XMLConstants.SUBSCRIPTION_ELEM, ns.getSubscription().toString())
                    .addAttribute(XMLConstants.JID_ATTR, ns.getUser().toBareJID());
            if (null != ns.getInvitedBy() && isOwnerModerator()) {
                subscription.addAttribute(XMLConstants.INVITED_BY_ELEM, ns.getInvitedBy().toBareJID());
            }
        }
        return true;
    }

    private boolean isOwnerModerator() throws NodeStoreException {
        return channelManager.getNodeMembership(node, actorJid).getAffiliation().canAuthorize();
    }

    private void makeRemoteRequest(String to) throws InterruptedException {
        IQ forwarder = requestIq.createCopy();
        forwarder.setTo(to);
        if (null == forwarder.getElement().element(XMLConstants.PUBSUB_ELEM).element(XMLConstants.ACTOR_ELEM)) {
            Element actor = forwarder.getElement().element(XMLConstants.PUBSUB_ELEM)
                .addElement(XMLConstants.ACTOR_ELEM, Buddycloud.NS);
            actor.addText(requestIq.getFrom().toBareJID());
        }
        outQueue.put(forwarder);
    }
    
    @Override
    public boolean accept(Element elm) {
        return XMLConstants.SUBSCRIPTIONS_ELEM.equals(elm.getName());
    }
}
