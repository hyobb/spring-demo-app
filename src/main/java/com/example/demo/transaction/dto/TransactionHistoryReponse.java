package com.example.demo.transaction.dto;

import com.example.demo.transaction.entity.TransactionHistory;

import lombok.Builder;
import lombok.Getter;

@Getter
public class TransactionHistoryReponse {
  private String transactionHash;

  private String transactionStatus;

  private Integer blockConfirmations;

  @Builder
  public TransactionHistoryReponse(TransactionHistory transactionHistory) {
    this.transactionHash = transactionHistory.getTransactionHash();
    this.transactionStatus = transactionHistory.getStatus().toString();
    this.blockConfirmations = transactionHistory.getBlockConfirmations();
  }
}
