package com.nttdata.bank.customer.service;

import com.nttdata.bank.customer.dto.CustomerRequest;
import com.nttdata.bank.customer.dto.CustomerResponse;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

/** Business operations over bank customers. */
public interface CustomerService {

  /** Registers a new customer. */
  Single<CustomerResponse> create(CustomerRequest request);

  /** Lists every customer. */
  Flowable<CustomerResponse> findAll();

  /** Finds a customer by its identifier. */
  Single<CustomerResponse> findById(String id);

  /** Updates the data of an existing customer. */
  Single<CustomerResponse> update(String id, CustomerRequest request);

  /** Deletes a customer. */
  Completable delete(String id);
}
