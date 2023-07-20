package com.example.demo.wallet.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateWalletRequest {
  @NotBlank
  private String password;
}
