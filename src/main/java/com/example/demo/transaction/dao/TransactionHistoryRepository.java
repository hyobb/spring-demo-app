package com.example.demo.transaction.dao;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.transaction.entity.TransactionHistory;

public interface TransactionHistoryRepository extends JpaRepository<TransactionHistory, Long> {
  List<TransactionHistory> findAllByOrderByIdDesc(Pageable pageable);
}
