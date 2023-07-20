package com.example.demo.transaction.service;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

import com.example.demo.ethereum.entity.Block;
import com.example.demo.transaction.dto.CreateTransactionDto;
import com.example.demo.transaction.dto.GetTransactionHistoriesDto;
import com.example.demo.transaction.entity.Transaction;
import com.example.demo.transaction.entity.TransactionHistory;

public interface TransactionService {
  Transaction findOrCreateTransaction(CreateTransactionDto createTransactionDto);

  Block updateMinedBlock(Long id, BigInteger lastestBlockNumber) throws IOException, Exception;

  Integer updateMinedTransactionsByEthBlock(BigInteger blockNumber, Integer blockConfirmations)
      throws IOException, Exception;

  Transaction updateMinedTransaction(Long transactionId, Integer blockConfirmations);

  List<TransactionHistory> getTransactionHistories(GetTransactionHistoriesDto getTransactionHistoriesDto);
}
