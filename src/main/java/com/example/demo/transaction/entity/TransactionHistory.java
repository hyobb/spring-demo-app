package com.example.demo.transaction.entity;

import org.hibernate.annotations.Immutable;

import com.example.demo.common.entity.BaseEntity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "transaction_histories", indexes = {
    @Index(name = "idx_transaction_id", columnList = "transaction_id")
})
@Entity
@Immutable
public class TransactionHistory extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "transaction_id")
  private Transaction transaction;

  @Column
  private String transactionHash;

  @Enumerated(EnumType.STRING)
  private TransactionStatus status;

  @Column()
  private Integer blockConfirmations;

  @Builder
  public TransactionHistory(Transaction transaction, String transactionHash, TransactionStatus status,
      Integer blockConfirmations) {
    this.transaction = transaction;
    this.transactionHash = transactionHash;
    this.status = status;
    this.blockConfirmations = blockConfirmations;
  }
}
