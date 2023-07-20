package com.example.demo.ethereum.entity;

import java.math.BigInteger;

import com.example.demo.common.entity.BaseEntity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "blocks")
@Entity
public class Block extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private BigInteger number;

  @Column(nullable = false)
  private String hash;

  @Enumerated(EnumType.STRING)
  private BlockStatus status;

  @Builder
  public Block(BigInteger number, String hash, BlockStatus status) {
    this.number = number;
    this.hash = hash;
    this.status = status;
  }
}
