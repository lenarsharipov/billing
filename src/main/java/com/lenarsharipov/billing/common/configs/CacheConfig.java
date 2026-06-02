package com.lenarsharipov.billing.common.configs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

import static com.lenarsharipov.billing.common.constants.CommonConstants.*;

@Configuration
@EnableRedisRepositories(basePackages = "com.lenarsharipov.billing.usercache.repository")
public class CacheConfig {

    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory connectionFactory) {
        return new StringRedisTemplate(connectionFactory);
    }

    @Bean
    public ObjectMapper cacheObjectMapper() {
        var mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }

    @Bean
    public TopicExchange subscriptionExchange() {
        return new TopicExchange(EXCHANGE_NAME, true, false);
    }

    @Bean
    public Queue invoiceQueue() {
        return new Queue(INVOICE_QUEUE, true);
    }

    @Bean
    public Queue subDeactivatedQueue() {
        return new Queue(DEACTIVATED_QUEUE, true);
    }

    @Bean
    public Binding bindingInvoice(
            Queue invoiceQueue,
            TopicExchange subscriptionExchange
    ) {
        return BindingBuilder.bind(invoiceQueue)
                .to(subscriptionExchange)
                .with(ROUTING_INVOICE_CREATED);
    }

    @Bean
    public Binding bindingDeactivation(
            Queue subDeactivatedQueue,
            TopicExchange subscriptionExchange
    ) {
        return BindingBuilder.bind(subDeactivatedQueue)
                .to(subscriptionExchange)
                .with(ROUTING_SUB_DEACTIVATED);
    }
}
