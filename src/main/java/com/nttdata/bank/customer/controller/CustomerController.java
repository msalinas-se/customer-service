package com.nttdata.bank.customer.controller;

import com.nttdata.bank.customer.dto.CustomerRequest;
import com.nttdata.bank.customer.dto.CustomerResponse;
import com.nttdata.bank.customer.service.CustomerService;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** REST endpoints for customer management. */
@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
public class CustomerController {
  private final CustomerService customerService;

  /** Creates a customer. */
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Single<CustomerResponse> create(@Valid @RequestBody CustomerRequest request) {
    return customerService.create(request);
  }

  /** Lists all customers. */
  @GetMapping
  public Flowable<CustomerResponse> findAll() {
    return customerService.findAll();
  }

  /** Gets a customer by identifier. */
  @GetMapping("/{id}")
  public Single<CustomerResponse> findById(@PathVariable String id) {
    return customerService.findById(id);
  }

  /** Updates a customer. */
  @PutMapping("/{id}")
  public Single<CustomerResponse> update(@PathVariable String id,
      @Valid @RequestBody CustomerRequest request) {
    return customerService.update(id, request);
  }

  /** Deletes a customer. */
  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public Completable delete(@PathVariable String id) {
    return customerService.delete(id);
  }
}
