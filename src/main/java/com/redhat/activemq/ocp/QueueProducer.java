package com.redhat.activemq.ocp;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
public class QueueProducer {

    private static final Logger LOG = LoggerFactory.getLogger(QueueProducer.class);

    private final int cores = Runtime.getRuntime().availableProcessors();

    private final ExecutorService executorService = Executors.newFixedThreadPool(cores);

    @Autowired
    @Qualifier("queueJmsTemplate")
    public JmsTemplate jmsTemplate;

    @Value("${queue.name}")
    private String destinationName;

    public void sendMessage(String payload, int numberOfMessages) {

        LOG.info("cores: {}", cores);
        LOG.info("numberOfMessages: {}", numberOfMessages);

        int messagesPerExecutor = numberOfMessages / cores;
        if (messagesPerExecutor > 0) {
            for (int i = 0; i < cores; i++) {
                executorService.execute(new MessageSender(messagesPerExecutor, payload));
            }
        }

        int remainder = numberOfMessages % cores;
        if (remainder > 0) {
            executorService.execute(new MessageSender(remainder, payload));
        }

        LOG.info("Finished sending all messages to {}", destinationName);
    }

    public class MessageSender implements Runnable {

        final int messagesPerExecutor;

        final String payload;

        public MessageSender(int messagesPerExecutor, String payload) {

            this.messagesPerExecutor = messagesPerExecutor;
            this.payload = payload;
        }

        @Override
        public void run() {

            try {
                for (int i = 0; i < messagesPerExecutor; i++) {

                    jmsTemplate.send(destinationName, session -> session.createTextMessage(payload));
//                    jmsTemplate.convertAndSend(destinationName, payload);
                }
            } catch (Throwable t) {
                LOG.error(t.getMessage(), t);
            }
        }

    }

}
