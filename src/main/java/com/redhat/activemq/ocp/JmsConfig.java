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

    @Bean
    public JmsTemplate jmsTemplate(CachingConnectionFactory connectionFactory) {

        JmsTemplate jmsTemplate = new JmsTemplate();
        jmsTemplate.setConnectionFactory(connectionFactory);

        jmsTemplate.afterPropertiesSet();

        return jmsTemplate;
    }

    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory(CachingConnectionFactory connectionFactory) {

        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setConcurrency("3-10");
        return factory;
    }

    @Bean
    public CachingConnectionFactory cachingConnectionFactory(JmsConnectionFactory connectionFactory) {

        CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory();
        cachingConnectionFactory.setSessionCacheSize(brokerMaxConnections);
        cachingConnectionFactory.setTargetConnectionFactory(connectionFactory);
        cachingConnectionFactory.afterPropertiesSet();

        return cachingConnectionFactory;
    }

    @Bean
    public JmsConnectionFactory jmsConnectionFactory() {

        JmsConnectionFactory factory = new JmsConnectionFactory();
        factory.setRemoteURI(remoteUri());
        factory.setUsername(brokerUsername);
        factory.setPassword(brokerPassword);

        return factory;
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
