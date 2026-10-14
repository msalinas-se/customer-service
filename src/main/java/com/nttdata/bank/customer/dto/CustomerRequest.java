package com.nttdata.bank.customer.dto;

import com.nttdata.bank.customer.model.CustomerType;
import com.nttdata.bank.customer.model.DocumentType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** Payload used to create or update a customer. */
public record CustomerRequest(
    @NotNull CustomerType type,
    @NotNull DocumentType documentType,
    @NotBlank String documentNumber,
    @NotBlank String name,
    @Email String email,
    String phone,
    String address) {
}
