package com.nttdata.bank.customer.cache;

import com.nttdata.bank.customer.model.Customer;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;

/** Cache of customers in Redis, if Redis fails the database is used. */
@Slf4j
@Component
public class CustomerCache {
  private static final String PREFIX = "customer:";

  private final ReactiveRedisTemplate<String, Customer> redisTemplate;
  private final Duration ttl;

  /** Creates the cache with the configured time to live. */
  public CustomerCache(ReactiveRedisTemplate<String, Customer> redisTemplate,
      @Value("${cache.customer-ttl}") Duration ttl) {
    this.redisTemplate = redisTemplate;
    this.ttl = ttl;
  }

  /** Gets a customer from the cache. */
  public Maybe<Customer> get(String id) {
    return Flowable.fromPublisher(redisTemplate.opsForValue().get(PREFIX + id))
        .firstElement()
        .doOnSuccess(customer -> log.debug("Customer {} found in cache", id))
        .onErrorComplete(this::logError);
  }

  /** Saves a customer in the cache. */
  public Completable put(Customer customer) {
    return Completable.fromPublisher(
            redisTemplate.opsForValue().set(PREFIX + customer.getId(), customer, ttl))
        .onErrorComplete(this::logError);
  }

  /** Removes a customer from the cache. */
  public Completable evict(String id) {
    return Completable.fromPublisher(redisTemplate.opsForValue().delete(PREFIX + id))
        .onErrorComplete(this::logError);
  }

  private boolean logError(Throwable error) {
    log.warn("Redis is not available: {}", error.getMessage());
    return true;
  }
}
