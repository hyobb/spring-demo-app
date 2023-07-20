package com.example.demo.transaction.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.transaction.entity.Transaction;
import com.example.demo.transaction.entity.TransactionStatus;

import java.util.List;
import java.math.BigInteger;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
  List<Transaction> findByBlockNumber(BigInteger blockNumber);

  List<Transaction> findByFromAddressAndStatus(String fromAddress, TransactionStatus status);

  Transaction findOneByHash(String hash);
}
