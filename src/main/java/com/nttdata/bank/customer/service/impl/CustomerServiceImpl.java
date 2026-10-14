package com.nttdata.bank.customer.service.impl;

import com.nttdata.bank.customer.dto.CustomerRequest;
import com.nttdata.bank.customer.dto.CustomerResponse;
import com.nttdata.bank.customer.exception.BusinessRuleException;
import com.nttdata.bank.customer.exception.ResourceNotFoundException;
import com.nttdata.bank.customer.mapper.CustomerMapper;
import com.nttdata.bank.customer.model.Customer;
import com.nttdata.bank.customer.repository.CustomerRepository;
import com.nttdata.bank.customer.service.CustomerService;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/** Implementation of CustomerService backed by MongoDB. */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {
  private final CustomerRepository customerRepository;
  private final CustomerMapper customerMapper;

  /** Registers a new customer. */
  @Override
  public Single<CustomerResponse> create(CustomerRequest request) {
    return customerRepository.existsByDocumentNumber(request.documentNumber())
        .flatMap(exists -> {
          if (exists) {
            return Single.error(new BusinessRuleException("DUPLICATE_DOCUMENT",
                "A customer with document " + request.documentNumber() + " already exists"));
          }
          return customerRepository.save(customerMapper.toEntity(request));
        })
        .doOnSuccess(customer -> log.info("Customer created: {}", customer.getId()))
        .map(customerMapper::toResponse);
  }

  /** Lists every customer. */
  @Override
  public Flowable<CustomerResponse> findAll() {
    return customerRepository.findAll().map(customerMapper::toResponse);
  }

  /** Finds a customer by its identifier. */
  @Override
  public Single<CustomerResponse> findById(String id) {
    return findCustomer(id).map(customerMapper::toResponse);
  }

  /** Updates the data of an existing customer. */
  @Override
  public Single<CustomerResponse> update(String id, CustomerRequest request) {
    return findCustomer(id)
        .flatMap(customer -> customerRepository.save(customerMapper.updateEntity(customer, request)))
        .doOnSuccess(customer -> log.info("Customer updated: {}", id))
        .map(customerMapper::toResponse);
  }

  /** Deletes a customer. */
  @Override
  public Completable delete(String id) {
    return findCustomer(id)
        .flatMapCompletable(customerRepository::delete)
        .doOnComplete(() -> log.info("Customer deleted: {}", id));
  }

  private Single<Customer> findCustomer(String id) {
    return customerRepository.findById(id)
        .switchIfEmpty(Single.error(new ResourceNotFoundException("Customer not found: " + id)));
  }
}
