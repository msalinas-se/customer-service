package com.nttdata.bank.customer.service.impl;

import com.nttdata.bank.customer.cache.CustomerCache;
import com.nttdata.bank.customer.dto.CustomerRequest;
import com.nttdata.bank.customer.dto.CustomerResponse;
import com.nttdata.bank.customer.exception.BusinessRuleException;
import com.nttdata.bank.customer.exception.ResourceNotFoundException;
import com.nttdata.bank.customer.mapper.CustomerMapper;
import com.nttdata.bank.customer.model.Customer;
import com.nttdata.bank.customer.model.CustomerProfile;
import com.nttdata.bank.customer.model.CustomerType;
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
  private final CustomerCache customerCache;

  /** Registers a new customer. */
  @Override
  public Single<CustomerResponse> create(CustomerRequest request) {
    if (!isValidProfile(request)) {
      return Single.error(new BusinessRuleException("INVALID_PROFILE",
          "VIP is only for personal customers and PYME only for business customers"));
    }
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
    return customerCache.get(id)
        .switchIfEmpty(findCustomer(id)
            .flatMap(customer -> customerCache.put(customer).toSingleDefault(customer)))
        .map(customerMapper::toResponse);
  }

  /** Updates the data of an existing customer. */
  @Override
  public Single<CustomerResponse> update(String id, CustomerRequest request) {
    if (!isValidProfile(request)) {
      return Single.error(new BusinessRuleException("INVALID_PROFILE",
          "VIP is only for personal customers and PYME only for business customers"));
    }
    return findCustomer(id)
        .flatMap(customer ->
            customerRepository.save(customerMapper.updateEntity(customer, request)))
        .flatMap(customer -> customerCache.put(customer).toSingleDefault(customer))
        .doOnSuccess(customer -> log.info("Customer updated: {}", id))
        .map(customerMapper::toResponse);
  }

  /** Deletes a customer. */
  @Override
  public Completable delete(String id) {
    return findCustomer(id)
        .flatMapCompletable(customerRepository::delete)
        .andThen(customerCache.evict(id))
        .doOnComplete(() -> log.info("Customer deleted: {}", id));
  }

  private boolean isValidProfile(CustomerRequest request) {
    if (request.profile() == CustomerProfile.VIP) {
      return request.type() == CustomerType.PERSONAL;
    }
    if (request.profile() == CustomerProfile.PYME) {
      return request.type() == CustomerType.BUSINESS;
    }
    return true;
  }

  private Single<Customer> findCustomer(String id) {
    return customerRepository.findById(id)
        .switchIfEmpty(Single.error(new ResourceNotFoundException("Customer not found: " + id)));
  }
}
