package com.redhat.activemq.ocp;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSException;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.core.JmsTemplate;

@Configuration
public class JmsConfig {

    private static final Logger LOG = LoggerFactory.getLogger(JmsConfig.class);

    @Value("${spring.artemis.broker-url}")
    private String jmsUrl;

    @Value("${spring.artemis.user}")
    private String user;

    @Value("${spring.artemis.password}")
    private String password;

    @Value("${spring.artemis.pool.max-connections}")
    private Integer maxConnections;

    @Value("${spring.artemis.trust-store}")
    private String trustStorePath;

    @Value("${spring.artemis.trust-store-password}")
    private String trustStorePassword;

    @Value("${spring.artemis.key-store}")
    private String keyStorePath;

    @Value("${spring.artemis.key-store-password}")
    private String keyStorePassword;

    @Bean
    public JmsTemplate queueJmsTemplate(ConnectionFactory connectionFactory) throws JMSException {

        return new JmsTemplate(connectionFactory);
    }

    @Bean
    public JmsTemplate topicJmsTemplate(ConnectionFactory connectionFactory) throws JMSException {

        JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory);
        jmsTemplate.setPubSubDomain(true); // Enable Topic mode

        return jmsTemplate;
    }

    @Bean
    public ActiveMQConnectionFactory connectionFactory() throws JMSException, NoSuchAlgorithmException, KeyManagementException {

        System.setProperty("javax.net.ssl.keyStore", keyStorePath);
        System.setProperty("javax.net.ssl.keyStorePassword", keyStorePassword);
        System.setProperty("javax.net.ssl.trustStore", trustStorePath);
        System.setProperty("javax.net.ssl.trustStorePassword", trustStorePassword);

        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
        connectionFactory.setBrokerURL(jmsUrl);
        connectionFactory.setUser(user);
        connectionFactory.setPassword(password);
        connectionFactory.setThreadPoolMaxSize(maxConnections);

        return connectionFactory;
    }

}
