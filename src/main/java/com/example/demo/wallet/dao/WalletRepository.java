package com.example.demo.wallet.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.wallet.entity.Wallet;

import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
  Optional<Wallet> findByAddress(String address);
}
