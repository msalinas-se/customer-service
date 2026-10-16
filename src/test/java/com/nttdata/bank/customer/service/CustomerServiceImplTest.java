package com.nttdata.bank.customer.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.nttdata.bank.customer.dto.CustomerRequest;
import com.nttdata.bank.customer.exception.BusinessRuleException;
import com.nttdata.bank.customer.exception.ResourceNotFoundException;
import com.nttdata.bank.customer.mapper.CustomerMapper;
import com.nttdata.bank.customer.model.Customer;
import com.nttdata.bank.customer.model.CustomerProfile;
import com.nttdata.bank.customer.model.CustomerType;
import com.nttdata.bank.customer.model.DocumentType;
import com.nttdata.bank.customer.repository.CustomerRepository;
import com.nttdata.bank.customer.service.impl.CustomerServiceImpl;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
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

  private final CustomerRequest request = new CustomerRequest(CustomerType.PERSONAL, null,
      DocumentType.DNI, "45678912", "Ana Torres", "ana@mail.com", null, null);

  @BeforeEach
  void setUp() {
    customerService = new CustomerServiceImpl(customerRepository, new CustomerMapper());
  }

  private Customer customer() {
    return Customer.builder().id("1").type(CustomerType.PERSONAL)
        .profile(CustomerProfile.STANDARD).documentNumber("45678912").name("Ana Torres").build();
  }

  @Test
  void createCustomer() {
    when(customerRepository.existsByDocumentNumber("45678912")).thenReturn(Single.just(false));
    when(customerRepository.save(any(Customer.class)))
        .thenAnswer(invocation -> Single.just(invocation.getArgument(0)));

    customerService.create(request).test()
        .assertValue(customer -> customer.name().equals("Ana Torres")
            && customer.profile() == CustomerProfile.STANDARD);
  }

  @Test
  void createVipCustomer() {
    CustomerRequest vip = new CustomerRequest(CustomerType.PERSONAL, CustomerProfile.VIP,
        DocumentType.DNI, "45678912", "Ana Torres", null, null, null);
    when(customerRepository.existsByDocumentNumber("45678912")).thenReturn(Single.just(false));
    when(customerRepository.save(any(Customer.class)))
        .thenAnswer(invocation -> Single.just(invocation.getArgument(0)));

    customerService.create(vip).test()
        .assertValue(customer -> customer.profile() == CustomerProfile.VIP);
  }

  @Test
  void pymeOnlyForBusinessCustomers() {
    CustomerRequest pyme = new CustomerRequest(CustomerType.PERSONAL, CustomerProfile.PYME,
        DocumentType.DNI, "45678912", "Ana Torres", null, null, null);

    customerService.create(pyme).test().assertError(BusinessRuleException.class);
  }

  @Test
  void vipOnlyForPersonalCustomers() {
    CustomerRequest vip = new CustomerRequest(CustomerType.BUSINESS, CustomerProfile.VIP,
        DocumentType.RUC, "20123456789", "Acme", null, null, null);

    customerService.update("1", vip).test().assertError(BusinessRuleException.class);
  }

  @Test
  void createCustomerWithDuplicateDocument() {
    when(customerRepository.existsByDocumentNumber("45678912")).thenReturn(Single.just(true));

    customerService.create(request).test().assertError(BusinessRuleException.class);
  }

  @Test
  void findAllCustomers() {
    when(customerRepository.findAll()).thenReturn(Flowable.just(customer(), customer()));

    customerService.findAll().test().assertValueCount(2);
  }

  @Test
  void findCustomerById() {
    when(customerRepository.findById("1")).thenReturn(Maybe.just(customer()));

    customerService.findById("1").test().assertValue(customer -> customer.id().equals("1"));
  }

  @Test
  void findCustomerNotFound() {
    when(customerRepository.findById("1")).thenReturn(Maybe.empty());

    customerService.findById("1").test().assertError(ResourceNotFoundException.class);
  }

  @Test
  void updateCustomer() {
    when(customerRepository.findById("1")).thenReturn(Maybe.just(customer()));
    when(customerRepository.save(any(Customer.class)))
        .thenAnswer(invocation -> Single.just(invocation.getArgument(0)));

    customerService.update("1", request).test()
        .assertValue(customer -> customer.email().equals("ana@mail.com"));
  }

  @Test
  void deleteCustomer() {
    Customer customer = customer();
    when(customerRepository.findById("1")).thenReturn(Maybe.just(customer));
    when(customerRepository.delete(customer)).thenReturn(Completable.complete());

    customerService.delete("1").test().assertComplete();
  }
}
