package com.example.demo.wallet.service;

import java.math.BigDecimal;
import java.util.List;

import com.example.demo.wallet.dto.CreateWalletRequest;
import com.example.demo.wallet.dto.TransferRequest;
import com.example.demo.wallet.entity.Wallet;

public interface WalletService {
  List<Wallet> getAllWallets();

  Wallet createWallet(CreateWalletRequest createWalletRequest) throws Exception;

  BigDecimal getBalance(String address) throws Exception;

  void transfer(String toAddress, TransferRequest TransferRequest) throws Exception;
}
