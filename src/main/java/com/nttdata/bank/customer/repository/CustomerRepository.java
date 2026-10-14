package com.nttdata.bank.customer.repository;

import com.nttdata.bank.customer.model.Customer;
import io.reactivex.rxjava3.core.Single;
import org.springframework.data.repository.reactive.RxJava3CrudRepository;

/** Reactive repository for Customer documents based on RxJava. */
public interface CustomerRepository extends RxJava3CrudRepository<Customer, String> {

  /** Checks whether a customer is registered with the given document number. */
  Single<Boolean> existsByDocumentNumber(String documentNumber);
}
