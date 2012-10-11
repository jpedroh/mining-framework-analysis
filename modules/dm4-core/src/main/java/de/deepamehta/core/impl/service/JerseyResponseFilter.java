package de.deepamehta.core.impl.service;

import de.deepamehta.core.Association;
import de.deepamehta.core.AssociationType;
import de.deepamehta.core.Topic;
import de.deepamehta.core.TopicType;
import de.deepamehta.core.service.Directives;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseFilter;

import javax.ws.rs.WebApplicationException;

import java.lang.reflect.Type;
import java.lang.reflect.ParameterizedType;
import java.util.Set;
import java.util.logging.Logger;



class JerseyResponseFilter implements ContainerResponseFilter {

    // ---------------------------------------------------------------------------------------------- Instance Variables

    private EmbeddedService dms;

    private Logger logger = Logger.getLogger(getClass().getName());

    // ---------------------------------------------------------------------------------------------------- Constructors

    JerseyResponseFilter(EmbeddedService dms) {
        this.dms = dms;
    }

    // -------------------------------------------------------------------------------------------------- Public Methods

    @Override
    public ContainerResponse filter(ContainerRequest request, ContainerResponse response) {
        try {
            Object entity = response.getEntity();
            if (entity != null) {
                if (entity instanceof TopicType) {          // Note: type matches both, must take precedence over topic
                    firePreSend((TopicType) entity);
                } else if (entity instanceof AssociationType) {
                    firePreSend((AssociationType) entity);  // Note: type matches both, must take precedence over topic
                } else if (entity instanceof Topic) {
                    firePreSend((Topic) entity);
                } else if (entity instanceof Association) {
                    firePreSend((Association) entity);
                } else if (entity instanceof Directives) {
                    firePreSend((Directives) entity);
                } else if (isIterable(response, TopicType.class)) {
                    firePreSendTopicTypes((Iterable<TopicType>) entity);
                } else if (isIterable(response, Topic.class)) {
                    firePreSendTopics((Iterable<Topic>) entity);
                }
            }
            return response;
        } catch (Exception e) {
            throw new WebApplicationException(new RuntimeException("Jersey response filtering failed", e));
        }
    }

    // ------------------------------------------------------------------------------------------------- Private Methods

    private void firePreSend(Topic topic) {
        dms.fireEvent(CoreEvent.PRE_SEND_TOPIC, topic, null);                   // clientState=null
    }

    private void firePreSend(Association assoc) {
        dms.fireEvent(CoreEvent.PRE_SEND_ASSOCIATION, assoc, null);             // clientState=null
    }

    private void firePreSend(TopicType topicType) {
        dms.fireEvent(CoreEvent.PRE_SEND_TOPIC_TYPE, topicType, null);          // clientState=null
    }

    private void firePreSend(AssociationType assocType) {
        dms.fireEvent(CoreEvent.PRE_SEND_ASSOCIATION_TYPE, assocType, null);    // clientState=null
    }

    private void firePreSend(Directives directives) {
        for (Directives.Entry entry : directives) {
            switch (entry.dir) {
            case UPDATE_TOPIC:
                firePreSend((Topic) entry.arg);
                break;
            case UPDATE_ASSOCIATION:
                firePreSend((Association) entry.arg);
                break;
            case UPDATE_TOPIC_TYPE:
                firePreSend((TopicType) entry.arg);
                break;
            case UPDATE_ASSOCIATION_TYPE:
                firePreSend((AssociationType) entry.arg);
                break;
            }
        }
    }

    private void firePreSendTopics(Iterable<Topic> topics) {
        for (Topic topic : topics) {
            firePreSend(topic);
        }
    }

    private void firePreSendTopicTypes(Iterable<TopicType> topicTypes) {
        for (TopicType topicType : topicTypes) {
            firePreSend(topicType);
        }
    }

    // ---

    private boolean isIterable(ContainerResponse response, Class elementType) {
        Type genericType = response.getEntityType();
        if (genericType instanceof ParameterizedType) {
            Type[] typeArgs = ((ParameterizedType) genericType).getActualTypeArguments();
            Class<?> type = response.getEntity().getClass();
            if (typeArgs.length == 1 && Iterable.class.isAssignableFrom(type) &&
                                           elementType.isAssignableFrom((Class) typeArgs[0])) {
                return true;
            }
        }
        return false;
    }
}
