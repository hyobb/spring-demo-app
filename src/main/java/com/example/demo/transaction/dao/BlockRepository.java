package com.example.demo.transaction.dao;

import java.math.BigInteger;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.ethereum.entity.Block;
import com.example.demo.ethereum.entity.BlockStatus;

public interface BlockRepository extends JpaRepository<Block, Long> {
  Block findFirstByOrderByNumberDesc();

  List<Block> findTop12ByOrderByIdDesc();

  Block findByNumber(BigInteger blockNumber);

  List<Block> findByStatus(BlockStatus status);
}
