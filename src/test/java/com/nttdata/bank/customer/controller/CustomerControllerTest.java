package com.nttdata.bank.customer.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.nttdata.bank.customer.dto.CustomerRequest;
import com.nttdata.bank.customer.dto.CustomerResponse;
import com.nttdata.bank.customer.exception.BusinessRuleException;
import com.nttdata.bank.customer.exception.ResourceNotFoundException;
import com.nttdata.bank.customer.model.CustomerProfile;
import com.nttdata.bank.customer.model.CustomerType;
import com.nttdata.bank.customer.model.DocumentType;
import com.nttdata.bank.customer.service.CustomerService;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;

@WebFluxTest(CustomerController.class)
class CustomerControllerTest {

  @Autowired
  private WebTestClient webTestClient;

  @MockBean
  private CustomerService customerService;

  private final CustomerResponse response = new CustomerResponse("1", CustomerType.PERSONAL,
      CustomerProfile.STANDARD, DocumentType.DNI, "45678912", "Ana Torres", null, null, null);

  private final Map<String, Object> body = Map.of("type", "PERSONAL", "documentType", "DNI",
      "documentNumber", "45678912", "name", "Ana Torres");

  @Test
  void create() {
    when(customerService.create(any(CustomerRequest.class))).thenReturn(Single.just(response));

    webTestClient.post().uri("/customers").bodyValue(body).exchange()
        .expectStatus().isCreated();
  }

  @Test
  void createWithInvalidBody() {
    webTestClient.post().uri("/customers").bodyValue(Map.of("type", "PERSONAL")).exchange()
        .expectStatus().isBadRequest();
  }

  @Test
  void createWithBusinessError() {
    when(customerService.create(any(CustomerRequest.class)))
        .thenReturn(Single.error(new BusinessRuleException("DUPLICATE_DOCUMENT", "duplicated")));

    webTestClient.post().uri("/customers").bodyValue(body).exchange()
        .expectStatus().isBadRequest()
        .expectBody().jsonPath("$.code").isEqualTo("DUPLICATE_DOCUMENT");
  }

  @Test
  void findAll() {
    when(customerService.findAll()).thenReturn(Flowable.just(response));

    webTestClient.get().uri("/customers").exchange().expectStatus().isOk();
  }

  @Test
  void findById() {
    when(customerService.findById("1")).thenReturn(Single.just(response));

    webTestClient.get().uri("/customers/1").exchange().expectStatus().isOk();
  }

  @Test
  void findByIdNotFound() {
    when(customerService.findById("2"))
        .thenReturn(Single.error(new ResourceNotFoundException("Customer not found: 2")));

    webTestClient.get().uri("/customers/2").exchange().expectStatus().isNotFound();
  }

  @Test
  void update() {
    when(customerService.update(eq("1"), any(CustomerRequest.class)))
        .thenReturn(Single.just(response));

    webTestClient.put().uri("/customers/1").bodyValue(body).exchange().expectStatus().isOk();
  }

  @Test
  void delete() {
    when(customerService.delete("1")).thenReturn(Completable.complete());

    webTestClient.delete().uri("/customers/1").exchange().expectStatus().isNoContent();
  }

  @Test
  void unexpectedError() {
    when(customerService.findAll()).thenReturn(Flowable.error(new IllegalStateException("boom")));

    webTestClient.get().uri("/customers").exchange().expectStatus().is5xxServerError();
  }
}
