package com.nttdata.bank.customer.mapper;

import com.nttdata.bank.customer.dto.CustomerRequest;
import com.nttdata.bank.customer.dto.CustomerResponse;
import com.nttdata.bank.customer.model.Customer;
import com.nttdata.bank.customer.model.CustomerProfile;
import org.springframework.stereotype.Component;

/** Converts between customer DTOs and documents. */
@Component
public class CustomerMapper {

  /** Builds a new customer document from a request. */
  public Customer toEntity(CustomerRequest request) {
    Customer customer = new Customer();
    updateEntity(customer, request);
    return customer;
  }

  /** Copies the request fields into an existing customer. */
  public Customer updateEntity(Customer customer, CustomerRequest request) {
    customer.setType(request.type());
    customer.setProfile(request.profile() == null ? CustomerProfile.STANDARD : request.profile());
    customer.setDocumentType(request.documentType());
    customer.setDocumentNumber(request.documentNumber());
    customer.setName(request.name());
    customer.setEmail(request.email());
    customer.setPhone(request.phone());
    customer.setAddress(request.address());
    return customer;
  }

  /** Builds the API representation of a customer. */
  public CustomerResponse toResponse(Customer customer) {
    return new CustomerResponse(customer.getId(), customer.getType(), customer.getProfile(),
        customer.getDocumentType(), customer.getDocumentNumber(), customer.getName(),
        customer.getEmail(), customer.getPhone(), customer.getAddress());
  }
}
