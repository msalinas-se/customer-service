package com.nttdata.bank.customer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** Entry point of the customer microservice. */
@SpringBootApplication
public class CustomerServiceApplication {

  /** Starts the customer microservice. */
  public static void main(String[] args) {
    SpringApplication.run(CustomerServiceApplication.class, args);
  }
}
