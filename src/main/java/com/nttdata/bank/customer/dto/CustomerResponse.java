package com.nttdata.bank.customer.dto;

import com.nttdata.bank.customer.model.CustomerProfile;
import com.nttdata.bank.customer.model.CustomerType;
import com.nttdata.bank.customer.model.DocumentType;

/** Customer representation returned by the API. */
public record CustomerResponse(
    String id,
    CustomerType type,
    CustomerProfile profile,
    DocumentType documentType,
    String documentNumber,
    String name,
    String email,
    String phone,
    String address) {
}
