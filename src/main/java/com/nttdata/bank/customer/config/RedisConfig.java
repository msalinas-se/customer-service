package com.nttdata.bank.customer.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nttdata.bank.customer.model.Customer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/** Redis configuration used to cache the customers. */
@Configuration
public class RedisConfig {

  /** Template that stores customers as JSON. */
  @Bean
  public ReactiveRedisTemplate<String, Customer> customerRedisTemplate(
      ReactiveRedisConnectionFactory connectionFactory, ObjectMapper objectMapper) {
    Jackson2JsonRedisSerializer<Customer> serializer =
        new Jackson2JsonRedisSerializer<>(objectMapper, Customer.class);
    RedisSerializationContext<String, Customer> context = RedisSerializationContext
        .<String, Customer>newSerializationContext(new StringRedisSerializer())
        .value(serializer)
        .build();
    return new ReactiveRedisTemplate<>(connectionFactory, context);
  }
}
