package com.example.demo.wallet.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransferRequest {
  @NotBlank
  private String password;

  @NotBlank
  private String toAddress;

  @NotBlank
  private String amount;
}
