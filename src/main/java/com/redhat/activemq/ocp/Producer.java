package com.redhat.activemq.ocp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
public class Producer {

    private static final Logger LOG = LoggerFactory.getLogger(Producer.class);

    @Value("${producer.name}")
    private String destinationName;

    @Autowired
    public JmsTemplate jmsTemplate;

    public void sendMessage(String payload) {

        try {
            LOG.info("============= Sending: " + payload + " to destination: " + destinationName);
            this.jmsTemplate.convertAndSend(destinationName, payload);
        } catch (Throwable t) {
            LOG.error(t.getMessage(), t);
        }
    }

}
