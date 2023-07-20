package com.example.demo.wallet.entity;

import java.math.BigDecimal;

import com.example.demo.common.entity.BaseEntity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "wallets")
@Entity
public class Wallet extends BaseEntity {
  @Id
  @Column(unique = true)
  private String address;

  @Column(nullable = false)
  private String password;

  @Column(nullable = false)
  private String fileName;

  @Setter
  @Column(nullable = false, precision = 36, scale = 18)
  private BigDecimal balance;

  @Builder
  public Wallet(String address, String password, String fileName, BigDecimal balance) {
    this.address = address;
    this.password = password;
    this.fileName = fileName;
    this.balance = balance == null ? BigDecimal.ZERO : balance;
  }
}
