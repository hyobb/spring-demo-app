package com.example.demo.transaction.dto;

import java.math.BigDecimal;
import java.math.BigInteger;

import com.example.demo.transaction.entity.Transaction;
import com.example.demo.transaction.entity.TransactionStatus;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;

@Getter
public class CreateTransactionDto {
  @NotEmpty
  private String fromAddress;

  @NotEmpty
  private String toAddress;

  @NotEmpty
  private BigDecimal amount;

  @NotEmpty
  private TransactionStatus status;

  @NotEmpty
  private String hash;

  @NotEmpty
  private BigInteger blockNumber;

  @NotEmpty
  private Integer blockConfirmations;

  @Builder
  public CreateTransactionDto(String fromAddress, String toAddress, BigDecimal amount, TransactionStatus status,
      String hash, BigInteger blockNumber, Integer blockConfirmations) {
    this.fromAddress = fromAddress;
    this.toAddress = toAddress;
    this.amount = amount;
    this.status = status;
    this.hash = hash;
    this.blockNumber = blockNumber;
    this.blockConfirmations = blockConfirmations;
  }

  public Transaction toEntity() {
    return Transaction.builder()
        .fromAddress(fromAddress)
        .toAddress(toAddress)
        .amount(amount)
        .status(status)
        .hash(hash)
        .blockNumber(blockNumber)
        .blockConfirmation(blockConfirmations)
        .build();
  }
}
