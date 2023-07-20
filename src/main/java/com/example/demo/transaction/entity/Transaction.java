package com.example.demo.transaction.entity;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import com.example.demo.common.entity.BaseEntity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "transactions", indexes = {
    @Index(name = "idx_from_address", columnList = "fromAddress"),
    @Index(name = "idx_to_address", columnList = "toAddress"),
    @Index(name = "idx_hash", columnList = "hash"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_block_number_status", columnList = "blockNumber, status")
})
@Entity
public class Transaction extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String fromAddress;

  @Column(nullable = false)
  private String toAddress;

  @Column(nullable = false, precision = 36, scale = 18)
  private BigDecimal amount;

  @Setter
  @Enumerated(EnumType.STRING)
  private TransactionStatus status;

  @Column(nullable = false, unique = true)
  private String hash;

  @Setter
  @Column(nullable = false)
  private BigInteger blockNumber;

  @Column()
  private Integer blockConfirmations;

  @OneToMany(mappedBy = "transaction", cascade = CascadeType.ALL)
  private List<TransactionHistory> transactionHistory;

  @Builder
  public Transaction(String fromAddress, String toAddress, BigDecimal amount, TransactionStatus status, String hash,
      BigInteger blockNumber, Integer blockConfirmation) {
    this.fromAddress = fromAddress;
    this.toAddress = toAddress;
    this.amount = amount;
    this.status = status;
    this.hash = hash;
    this.blockNumber = blockNumber;
    this.blockConfirmations = blockConfirmation;
  }
}
