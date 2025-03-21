package org.buddycloud.channelserver.packetprocessor.iq.namespace.pubsub.get;

import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;
import org.buddycloud.channelserver.Configuration;
import org.buddycloud.channelserver.channel.ChannelManager;
import org.buddycloud.channelserver.db.exception.NodeStoreException;
import org.buddycloud.channelserver.packetprocessor.iq.namespace.pubsub.JabberPubsub;
import org.buddycloud.channelserver.packetprocessor.iq.namespace.pubsub.PubSubElementProcessorAbstract;
import org.buddycloud.channelserver.pubsub.model.NodeMembership;
import org.buddycloud.channelserver.utils.XMLConstants;
import org.buddycloud.channelserver.utils.node.item.payload.Buddycloud;
import org.dom4j.Element;
import org.xmpp.packet.IQ;
import org.xmpp.packet.JID;
import org.xmpp.packet.Packet;
import org.xmpp.resultsetmanagement.ResultSet;

public class AffiliationsGet extends PubSubElementProcessorAbstract {

    private final BlockingQueue<Packet> outQueue;
    private final ChannelManager channelManager;

    private IQ requestIq;
    private String node;
    private JID actorJid;
    private IQ result;
    private String firstItem;
<<<<<<< /usr/src/app/output/buddycloud/buddycloud-server-java/e746268967301a644cf186725205f1d823a45a83/src/main/java/org/buddycloud/channelserver/packetprocessor/iq/namespace/pubsub/get/AffiliationsGet.java/left.java
    private static final Logger LOGGER = Logger.getLogger(AffiliationsGet.class);
||||||| /usr/src/app/output/buddycloud/buddycloud-server-java/e746268967301a644cf186725205f1d823a45a83/src/main/java/org/buddycloud/channelserver/packetprocessor/iq/namespace/pubsub/get/AffiliationsGet.java/base.java
    private static final Logger logger = Logger.getLogger(AffiliationsGet.class);
=======
>>>>>>> /usr/src/app/output/buddycloud/buddycloud-server-java/e746268967301a644cf186725205f1d823a45a83/src/main/java/org/buddycloud/channelserver/packetprocessor/iq/namespace/pubsub/get/AffiliationsGet.java/right.java

    private static final Logger logger = Logger
            .getLogger(AffiliationsGet.class);

    public AffiliationsGet(BlockingQueue<Packet> outQueue,
            ChannelManager channelManager) {
        this.outQueue = outQueue;
        this.channelManager = channelManager;
    }

    @Override
    public void process(Element elm, JID actorJID, IQ reqIQ, Element rsm)
            throws Exception {
        result = IQ.createResultIQ(reqIQ);
        requestIq = reqIQ;
        actorJid = actorJID;
<<<<<<< /usr/src/app/output/buddycloud/buddycloud-server-java/e746268967301a644cf186725205f1d823a45a83/src/main/java/org/buddycloud/channelserver/packetprocessor/iq/namespace/pubsub/get/AffiliationsGet.java/left.java
        node = elm.attributeValue(XMLConstants.NODE_ATTR);
        if (!channelManager.isLocalJID(requestIq.getFrom())) {
            result.getElement().addAttribute(XMLConstants.REMOTE_SERVER_DISCOVER_ATTR, Boolean.FALSE.toString());
||||||| /usr/src/app/output/buddycloud/buddycloud-server-java/e746268967301a644cf186725205f1d823a45a83/src/main/java/org/buddycloud/channelserver/packetprocessor/iq/namespace/pubsub/get/AffiliationsGet.java/base.java
        node = elm.attributeValue("node");
        if (false == channelManager.isLocalJID(requestIq.getFrom())) {
            result.getElement().addAttribute("remote-server-discover", "false");
=======
        node = elm.attributeValue("node");

        if (false == Configuration.getInstance().isLocalJID(requestIq.getFrom())) {
            result.getElement().addAttribute("remote-server-discover", "false");
>>>>>>> /usr/src/app/output/buddycloud/buddycloud-server-java/e746268967301a644cf186725205f1d823a45a83/src/main/java/org/buddycloud/channelserver/packetprocessor/iq/namespace/pubsub/get/AffiliationsGet.java/right.java
        }
        String namespace = JabberPubsub.NS_PUBSUB_OWNER;
        if (node == null) {
            namespace = JabberPubsub.NAMESPACE_URI;
        }

<<<<<<< /usr/src/app/output/buddycloud/buddycloud-server-java/e746268967301a644cf186725205f1d823a45a83/src/main/java/org/buddycloud/channelserver/packetprocessor/iq/namespace/pubsub/get/AffiliationsGet.java/left.java
        Element pubsub = result.setChildElement(XMLConstants.PUBSUB_ELEM, namespace);
        Element affiliations = pubsub.addElement(XMLConstants.AFFILIATION_ELEM);
||||||| /usr/src/app/output/buddycloud/buddycloud-server-java/e746268967301a644cf186725205f1d823a45a83/src/main/java/org/buddycloud/channelserver/packetprocessor/iq/namespace/pubsub/get/AffiliationsGet.java/base.java
        Element pubsub = result.setChildElement(PubSubGet.ELEMENT_NAME, namespace);
        Element affiliations = pubsub.addElement("affiliations");
=======
        Element pubsub = result.setChildElement(PubSubGet.ELEMENT_NAME,
                namespace);
        Element affiliations = pubsub.addElement("affiliations");
>>>>>>> /usr/src/app/output/buddycloud/buddycloud-server-java/e746268967301a644cf186725205f1d823a45a83/src/main/java/org/buddycloud/channelserver/packetprocessor/iq/namespace/pubsub/get/AffiliationsGet.java/right.java

        if (actorJid == null) {
            actorJid = requestIq.getFrom();
        }

        boolean isProcessedLocally = true;
        if (node == null) {
            isProcessedLocally = getUserMemberships(affiliations);
        } else {
            isProcessedLocally = getNodeAffiliations(affiliations);
        }
        if (!isProcessedLocally) {
            return;
        }
            
        outQueue.put(result);
    }

<<<<<<< /usr/src/app/output/buddycloud/buddycloud-server-java/e746268967301a644cf186725205f1d823a45a83/src/main/java/org/buddycloud/channelserver/packetprocessor/iq/namespace/pubsub/get/AffiliationsGet.java/left.java
    private boolean getNodeAffiliations(Element affiliations) throws NodeStoreException, InterruptedException {
        if (!channelManager.isLocalNode(node) && (!channelManager.isCachedNode(node))) {
||||||| /usr/src/app/output/buddycloud/buddycloud-server-java/e746268967301a644cf186725205f1d823a45a83/src/main/java/org/buddycloud/channelserver/packetprocessor/iq/namespace/pubsub/get/AffiliationsGet.java/base.java
    private boolean getNodeAffiliations(Element affiliations) throws NodeStoreException, InterruptedException {
        if (false == channelManager.isLocalNode(node) && (false == channelManager.isCachedNode(node))) {
=======
    private boolean getNodeAffiliations(Element affiliations)
            throws NodeStoreException, InterruptedException {
        if (false == Configuration.getInstance().isLocalNode(node)
                && (false == channelManager.isCachedNode(node))) {
>>>>>>> /usr/src/app/output/buddycloud/buddycloud-server-java/e746268967301a644cf186725205f1d823a45a83/src/main/java/org/buddycloud/channelserver/packetprocessor/iq/namespace/pubsub/get/AffiliationsGet.java/right.java
            makeRemoteRequest(node.split("/")[2]);
            return false;
        }
        ResultSet<NodeMembership> nodeMemberships;
        nodeMemberships = channelManager.getNodeMemberships(node);
<<<<<<< /usr/src/app/output/buddycloud/buddycloud-server-java/e746268967301a644cf186725205f1d823a45a83/src/main/java/org/buddycloud/channelserver/packetprocessor/iq/namespace/pubsub/get/AffiliationsGet.java/left.java

        if ((!nodeMemberships.isEmpty()) && (!channelManager.isLocalNode(node))) {
||||||| /usr/src/app/output/buddycloud/buddycloud-server-java/e746268967301a644cf186725205f1d823a45a83/src/main/java/org/buddycloud/channelserver/packetprocessor/iq/namespace/pubsub/get/AffiliationsGet.java/base.java

        if ((0 == nodeMemberships.size()) && (false == channelManager.isLocalNode(node))) {
=======
        
        if ((0 == nodeMemberships.size())
            && (false == Configuration.getInstance().isLocalNode(node))) {
>>>>>>> /usr/src/app/output/buddycloud/buddycloud-server-java/e746268967301a644cf186725205f1d823a45a83/src/main/java/org/buddycloud/channelserver/packetprocessor/iq/namespace/pubsub/get/AffiliationsGet.java/right.java
            makeRemoteRequest(node.split("/")[2]);
            return false;
        }
<<<<<<< /usr/src/app/output/buddycloud/buddycloud-server-java/e746268967301a644cf186725205f1d823a45a83/src/main/java/org/buddycloud/channelserver/packetprocessor/iq/namespace/pubsub/get/AffiliationsGet.java/left.java
||||||| /usr/src/app/output/buddycloud/buddycloud-server-java/e746268967301a644cf186725205f1d823a45a83/src/main/java/org/buddycloud/channelserver/packetprocessor/iq/namespace/pubsub/get/AffiliationsGet.java/base.java

        boolean isOwnerModerator = isOwnerModerator();

=======
        
        boolean isOwnerModerator = isOwnerModerator();
        
>>>>>>> /usr/src/app/output/buddycloud/buddycloud-server-java/e746268967301a644cf186725205f1d823a45a83/src/main/java/org/buddycloud/channelserver/packetprocessor/iq/namespace/pubsub/get/AffiliationsGet.java/right.java
        for (NodeMembership nodeMembership : nodeMemberships) {

            if (actorJid.toBareJID().equals(nodeMembership.getUser().toBareJID())) {
                if (null == firstItem) {
                    firstItem = nodeMembership.getUser().toString();
                }
<<<<<<< /usr/src/app/output/buddycloud/buddycloud-server-java/e746268967301a644cf186725205f1d823a45a83/src/main/java/org/buddycloud/channelserver/packetprocessor/iq/namespace/pubsub/get/AffiliationsGet.java/left.java

                affiliations.addElement(XMLConstants.AFFILIATION_ELEM).addAttribute(XMLConstants.NODE_ATTR, nodeMembership.getNodeId())
                        .addAttribute(XMLConstants.AFFILIATION_ELEM, nodeMembership.getAffiliation().toString())
                        .addAttribute(XMLConstants.JID_ATTR, nodeMembership.getUser().toString());
||||||| /usr/src/app/output/buddycloud/buddycloud-server-java/e746268967301a644cf186725205f1d823a45a83/src/main/java/org/buddycloud/channelserver/packetprocessor/iq/namespace/pubsub/get/AffiliationsGet.java/base.java
                if ((false == isOwnerModerator) && !nodeMembership.getSubscription().equals(Subscriptions.subscribed)) {
                    continue;
                }
            }
            logger.trace("Adding affiliation for " + nodeMembership.getUser() + " affiliation " + nodeMembership.getAffiliation());

            if (null == firstItem) {
                firstItem = nodeMembership.getUser().toString();
=======
                if ((false == isOwnerModerator) && !nodeMembership.getSubscription().equals(Subscriptions.subscribed)) {
                    continue;
                }
            }
            logger.trace("Adding affiliation for " + nodeMembership.getUser()
                    + " affiliation " + nodeMembership.getAffiliation());
            
            if (null == firstItem) {
                firstItem = nodeMembership.getUser().toString();
>>>>>>> /usr/src/app/output/buddycloud/buddycloud-server-java/e746268967301a644cf186725205f1d823a45a83/src/main/java/org/buddycloud/channelserver/packetprocessor/iq/namespace/pubsub/get/AffiliationsGet.java/right.java
            }
<<<<<<< /usr/src/app/output/buddycloud/buddycloud-server-java/e746268967301a644cf186725205f1d823a45a83/src/main/java/org/buddycloud/channelserver/packetprocessor/iq/namespace/pubsub/get/AffiliationsGet.java/left.java
||||||| /usr/src/app/output/buddycloud/buddycloud-server-java/e746268967301a644cf186725205f1d823a45a83/src/main/java/org/buddycloud/channelserver/packetprocessor/iq/namespace/pubsub/get/AffiliationsGet.java/base.java
            nodeMembership.getUser().toString();

            affiliations.addElement("affiliation").addAttribute("node", nodeMembership.getNodeId())
                    .addAttribute("affiliation", nodeMembership.getAffiliation().toString()).addAttribute("jid", nodeMembership.getUser().toString());
=======
            
            affiliations
                    .addElement("affiliation")
                    .addAttribute("node", nodeMembership.getNodeId())
                    .addAttribute("affiliation",
                            nodeMembership.getAffiliation().toString())
                    .addAttribute("jid", nodeMembership.getUser().toString());
>>>>>>> /usr/src/app/output/buddycloud/buddycloud-server-java/e746268967301a644cf186725205f1d823a45a83/src/main/java/org/buddycloud/channelserver/packetprocessor/iq/namespace/pubsub/get/AffiliationsGet.java/right.java
        }

        return true;
    }
    
    private boolean isOwnerModerator() throws NodeStoreException {
        return channelManager.getNodeMembership(node,
                actorJid).getAffiliation().canAuthorize();
    }

<<<<<<< /usr/src/app/output/buddycloud/buddycloud-server-java/e746268967301a644cf186725205f1d823a45a83/src/main/java/org/buddycloud/channelserver/packetprocessor/iq/namespace/pubsub/get/AffiliationsGet.java/left.java
    private boolean getUserMemberships(Element affiliations) throws NodeStoreException, InterruptedException {

        if (!channelManager.isLocalJID(actorJid) && (!channelManager.isCachedJID(requestIq.getFrom()))) {
||||||| /usr/src/app/output/buddycloud/buddycloud-server-java/e746268967301a644cf186725205f1d823a45a83/src/main/java/org/buddycloud/channelserver/packetprocessor/iq/namespace/pubsub/get/AffiliationsGet.java/base.java
    private boolean getUserMemberships(Element affiliations) throws NodeStoreException, InterruptedException {

        if (false == channelManager.isLocalJID(actorJid) && (false == channelManager.isCachedJID(requestIq.getFrom()))) {
=======
    private boolean getUserMemberships(Element affiliations)
            throws NodeStoreException, InterruptedException {
        
        if (false == Configuration.getInstance().isLocalJID(actorJid)
                && (false == channelManager.isCachedJID(requestIq.getFrom()))) {
>>>>>>> /usr/src/app/output/buddycloud/buddycloud-server-java/e746268967301a644cf186725205f1d823a45a83/src/main/java/org/buddycloud/channelserver/packetprocessor/iq/namespace/pubsub/get/AffiliationsGet.java/right.java
            makeRemoteRequest(actorJid.getDomain());
            return false;
        }
<<<<<<< /usr/src/app/output/buddycloud/buddycloud-server-java/e746268967301a644cf186725205f1d823a45a83/src/main/java/org/buddycloud/channelserver/packetprocessor/iq/namespace/pubsub/get/AffiliationsGet.java/left.java

        ResultSet<NodeMembership> memberships = channelManager.getUserMemberships(actorJid);
||||||| /usr/src/app/output/buddycloud/buddycloud-server-java/e746268967301a644cf186725205f1d823a45a83/src/main/java/org/buddycloud/channelserver/packetprocessor/iq/namespace/pubsub/get/AffiliationsGet.java/base.java

        ResultSet<NodeMembership> memberships;
        memberships = channelManager.getUserMemberships(actorJid);
        boolean isOwnerModerator = isOwnerModerator();

=======
        
        ResultSet<NodeMembership> memberships;
        memberships = channelManager.getUserMemberships(actorJid);
        boolean isOwnerModerator = isOwnerModerator();
        
>>>>>>> /usr/src/app/output/buddycloud/buddycloud-server-java/e746268967301a644cf186725205f1d823a45a83/src/main/java/org/buddycloud/channelserver/packetprocessor/iq/namespace/pubsub/get/AffiliationsGet.java/right.java
        for (NodeMembership membership : memberships) {

<<<<<<< /usr/src/app/output/buddycloud/buddycloud-server-java/e746268967301a644cf186725205f1d823a45a83/src/main/java/org/buddycloud/channelserver/packetprocessor/iq/namespace/pubsub/get/AffiliationsGet.java/left.java
            if (actorJid.toBareJID().equals(membership.getUser().toBareJID())) {
                LOGGER.trace("Adding affiliation for " + membership.getUser() + " affiliation " + membership.getAffiliation() + " (no node provided)");

                if (null == firstItem) {
                    firstItem = membership.getNodeId();
                }

                affiliations.addElement(XMLConstants.AFFILIATION_ELEM).addAttribute(XMLConstants.NODE_ATTR, membership.getNodeId())
                        .addAttribute(XMLConstants.AFFILIATION_ELEM, membership.getAffiliation().toString())
                        .addAttribute(XMLConstants.JID_ATTR, membership.getUser().toBareJID());
            }

||||||| /usr/src/app/output/buddycloud/buddycloud-server-java/e746268967301a644cf186725205f1d823a45a83/src/main/java/org/buddycloud/channelserver/packetprocessor/iq/namespace/pubsub/get/AffiliationsGet.java/base.java
            if (false == actorJid.toBareJID().equals(membership.getUser())) {
                if ((false == isOwnerModerator) && membership.getAffiliation().in(Affiliations.outcast, Affiliations.none)) {
                    continue;
                }
                if ((false == isOwnerModerator) && !membership.getSubscription().equals(Subscriptions.subscribed)) {
                    continue;
                }
            }
            logger.trace("Adding affiliation for " + membership.getUser() + " affiliation " + membership.getAffiliation() + " (no node provided)");

            if (null == firstItem) {
                firstItem = membership.getNodeId();
            }
            membership.getNodeId();

            affiliations.addElement("affiliation").addAttribute("node", membership.getNodeId())
                    .addAttribute("affiliation", membership.getAffiliation().toString()).addAttribute("jid", membership.getUser().toBareJID());
=======
            if (false == actorJid.toBareJID().equals(membership.getUser())) {
                if ((false == isOwnerModerator) && membership.getAffiliation().in(Affiliations.outcast, Affiliations.none)) {
                    continue;
                }
                if ((false == isOwnerModerator) && !membership.getSubscription().equals(Subscriptions.subscribed)) {
                    continue;
                }
            }
            logger.trace("Adding affiliation for " + membership.getUser()
                    + " affiliation " + membership.getAffiliation()
                    + " (no node provided)");
            
            if (null == firstItem) {
                firstItem = membership.getNodeId();
            }
            
            affiliations
                    .addElement("affiliation")
                    .addAttribute("node", membership.getNodeId())
                    .addAttribute("affiliation",
                            membership.getAffiliation().toString())
                    .addAttribute("jid", membership.getUser().toBareJID());
>>>>>>> /usr/src/app/output/buddycloud/buddycloud-server-java/e746268967301a644cf186725205f1d823a45a83/src/main/java/org/buddycloud/channelserver/packetprocessor/iq/namespace/pubsub/get/AffiliationsGet.java/right.java
        }
        return true;
    }

    private void makeRemoteRequest(String node) throws InterruptedException {
        LOGGER.info("Going federated for <affiliations />");
        requestIq.setTo(new JID(node).getDomain());
        if (null == requestIq.getElement().element("pubsub").element("actor")) {
            Element actor = requestIq.getElement().element("pubsub")
                .addElement("actor", Buddycloud.NS);
            actor.addText(requestIq.getFrom().toBareJID());
        }
        outQueue.put(requestIq);
    }

    @Override
    public boolean accept(Element elm) {
        return XMLConstants.AFFILIATION_ELEM.equals(elm.getName());
    }
}
