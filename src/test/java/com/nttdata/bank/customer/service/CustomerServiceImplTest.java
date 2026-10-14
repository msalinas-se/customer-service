package com.nttdata.bank.customer.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.nttdata.bank.customer.dto.CustomerRequest;
import com.nttdata.bank.customer.exception.BusinessRuleException;
import com.nttdata.bank.customer.exception.ResourceNotFoundException;
import com.nttdata.bank.customer.mapper.CustomerMapper;
import com.nttdata.bank.customer.model.Customer;
import com.nttdata.bank.customer.model.CustomerType;
import com.nttdata.bank.customer.model.DocumentType;
import com.nttdata.bank.customer.repository.CustomerRepository;
import com.nttdata.bank.customer.service.impl.CustomerServiceImpl;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

  @Mock
  private CustomerRepository customerRepository;

  private CustomerServiceImpl customerService;

  private final CustomerRequest request = new CustomerRequest(CustomerType.PERSONAL,
      DocumentType.DNI, "45678912", "Ana Torres", "ana@mail.com", null, null);

  @BeforeEach
  void setUp() {
    customerService = new CustomerServiceImpl(customerRepository, new CustomerMapper());
  }

  @Test
  void createCustomer() {
    when(customerRepository.existsByDocumentNumber("45678912")).thenReturn(Single.just(false));
    when(customerRepository.save(any(Customer.class)))
        .thenAnswer(invocation -> Single.just(invocation.getArgument(0)));

    customerService.create(request).test()
        .assertValue(customer -> customer.name().equals("Ana Torres"));
  }

  @Test
  void createCustomerWithDuplicateDocument() {
    when(customerRepository.existsByDocumentNumber("45678912")).thenReturn(Single.just(true));

    customerService.create(request).test().assertError(BusinessRuleException.class);
  }

  @Test
  void findCustomerNotFound() {
    when(customerRepository.findById("1")).thenReturn(Maybe.empty());

    customerService.findById("1").test().assertError(ResourceNotFoundException.class);
  }
}
