package com.example.demo.wallet.dto;

import com.example.demo.wallet.entity.Wallet;

import lombok.Builder;
import lombok.Getter;

@Getter
public class WalletResponse {
  private String address;

  private String balance;

  @Builder
  public WalletResponse(Wallet wallet) {
    this.address = wallet.getAddress();
    this.balance = wallet.getBalance().toString();
  }
}
