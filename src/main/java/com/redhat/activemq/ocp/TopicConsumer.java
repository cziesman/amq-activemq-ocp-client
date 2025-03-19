package com.redhat.activemq.ocp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class TopicConsumer {

    private static final Logger LOG = LoggerFactory.getLogger(TopicConsumer.class);

    @JmsListener(destination = "${topic.queue.name}", containerFactory = "topicListenerContainerFactory")
    public void processMsg(String message) {

        LOG.info("============= Received: {}", message);
    }

}
