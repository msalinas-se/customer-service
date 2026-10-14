package com.nttdata.bank.customer.exception;

/** Error body returned by the API. */
public record ErrorResponse(String code, String message) {
}
