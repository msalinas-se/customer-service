package com.nttdata.bank.customer.cache;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.nttdata.bank.customer.model.Customer;
import java.time.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
class CustomerCacheTest {

  @Mock
  private ReactiveRedisTemplate<String, Customer> redisTemplate;

  @Mock
  private ReactiveValueOperations<String, Customer> valueOperations;

  private CustomerCache customerCache;

  private final Customer customer = Customer.builder().id("1").name("Ana").build();

  @BeforeEach
  void setUp() {
    when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    customerCache = new CustomerCache(redisTemplate, Duration.ofMinutes(10));
  }

  @Test
  void getCachedCustomer() {
    when(valueOperations.get("customer:1")).thenReturn(Mono.just(customer));

    customerCache.get("1").test().assertValue(customer);
  }

  @Test
  void getMissingCustomer() {
    when(valueOperations.get("customer:1")).thenReturn(Mono.empty());

    customerCache.get("1").test().assertNoValues().assertComplete();
  }

  @Test
  void redisDownIsIgnored() {
    when(valueOperations.get("customer:1"))
        .thenReturn(Mono.error(new RedisConnectionFailureException("down")));

    customerCache.get("1").test().assertNoValues().assertComplete();
  }

  @Test
  void putCustomer() {
    when(valueOperations.set(eq("customer:1"), eq(customer), any(Duration.class)))
        .thenReturn(Mono.just(true));

    customerCache.put(customer).test().assertComplete();
  }

  @Test
  void evictCustomer() {
    when(valueOperations.delete("customer:1")).thenReturn(Mono.just(true));

    customerCache.evict("1").test().assertComplete();
  }
}
