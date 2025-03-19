package com.redhat.activemq.ocp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

//@Component
public class QueueConsumer {

    private static final Logger LOG = LoggerFactory.getLogger(QueueConsumer.class);

    @JmsListener(destination = "${queue.name}", containerFactory = "queueListenerContainerFactory")
    public void processMsg(String message) {

        LOG.info("============= Received: {}", message);
    }

}
