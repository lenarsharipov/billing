package com.lenarsharipov.billing.common.configs;

import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.lenarsharipov.billing.common.constants.CommonConstants.EXCHANGE_NAME;

@Configuration
public class AMQPConfig {

    @Bean
    public Exchange subscriptionEventsExchange() {
        return ExchangeBuilder.topicExchange(EXCHANGE_NAME)
                .durable(true)
                .build();
    }
}
