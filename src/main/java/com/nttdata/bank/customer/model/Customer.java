package com.nttdata.bank.customer.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/** Bank customer stored in the customers collection. */
@Document("customers")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Customer {
  @Id
  private String id;
  private CustomerType type;
  private DocumentType documentType;
  private String documentNumber;
  private String name;
  private String email;
  private String phone;
  private String address;
}
