package com.redhat.activemq.ocp;

import org.apache.qpid.jms.JmsConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@Configuration
public class JmsConfig {

    private static final Logger LOG = LoggerFactory.getLogger(JmsConfig.class);

    @Value("${broker.scheme}")
    private String brokerScheme;

    @Value("${broker.host}")
    private String brokerHost;

    @Value("${broker.port}")
    private String brokerPort;

    @Value("${broker.username}")
    private String brokerUsername;

    @Value("${broker.password}")
    private String brokerPassword;

    @Value("${broker.maxConnections}")
    private Integer brokerMaxConnections;

    private CachingConnectionFactory cachingConnectionFactory;

    private JmsConnectionFactory jmsConnectionFactory;

    @Bean
    public JmsTemplate queueJmsTemplate() {

        return new JmsTemplate(cachingConnectionFactory());
    }

    // ✅ JMS Template for Topics
    @Bean
    public JmsTemplate topicJmsTemplate() {

        JmsTemplate jmsTemplate = new JmsTemplate(cachingConnectionFactory());
        jmsTemplate.setPubSubDomain(true); // Enable Topic mode

        return jmsTemplate;
    }

    @Bean
    public DefaultJmsListenerContainerFactory queueListenerContainerFactory() {

        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(cachingConnectionFactory());

        return factory;
    }

    // ✅ Listener Factory for Topics (Caching enabled)
    @Bean
    public DefaultJmsListenerContainerFactory topicListenerContainerFactory() {

        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(cachingConnectionFactory());
        factory.setPubSubDomain(true); // Enable Topic mode

        return factory;
    }

    private CachingConnectionFactory cachingConnectionFactory() {

        if (cachingConnectionFactory == null) {
            cachingConnectionFactory = new CachingConnectionFactory(jmsConnectionFactory());
            cachingConnectionFactory.setSessionCacheSize(brokerMaxConnections);
        }

        return cachingConnectionFactory;
    }

    private JmsConnectionFactory jmsConnectionFactory() {

        if (jmsConnectionFactory == null) {
            jmsConnectionFactory = new JmsConnectionFactory();
            jmsConnectionFactory.setRemoteURI(remoteUri());
            jmsConnectionFactory.setUsername(brokerUsername);
            jmsConnectionFactory.setPassword(brokerPassword);
        }

        return jmsConnectionFactory;
    }

    private String remoteUri() {

        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .scheme(brokerScheme)
                .host(brokerHost)
                .port(brokerPort)
                .build();

        LOG.debug(uriComponents.toUriString());

        return String.format("failover:(%s)", uriComponents.toUriString());
    }

}
