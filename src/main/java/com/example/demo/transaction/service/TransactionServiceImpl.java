package com.example.demo.transaction.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.demo.ethereum.entity.Block;
import com.example.demo.queue.dto.SendMinedTransactionMessageDto;
import com.example.demo.queue.service.MessagePublisher;
import com.example.demo.transaction.dao.BlockRepository;
import com.example.demo.transaction.dao.TransactionHistoryRepository;
import com.example.demo.transaction.dao.TransactionRepository;
import com.example.demo.transaction.dto.CreateTransactionDto;
import com.example.demo.transaction.dto.GetTransactionHistoriesDto;
import com.example.demo.transaction.entity.Transaction;
import com.example.demo.transaction.entity.TransactionHistory;
import com.example.demo.transaction.entity.TransactionStatus;
import com.example.demo.wallet.dao.WalletRepository;
import com.example.demo.wallet.entity.Wallet;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TransactionServiceImpl implements TransactionService {
  private final BlockRepository blockRepository;
  private final TransactionRepository transactionRepository;
  private final TransactionHistoryRepository transactionHistoryRepository;
  private final WalletRepository walletRepository;
  private final MessagePublisher messagePublisher;

  public static final Integer MAX_CONFIRMATIONS = 12;

  @Autowired
  public TransactionServiceImpl(BlockRepository blockRepository, TransactionRepository transactionRepository,
      TransactionHistoryRepository transactionHistoryRepository,
      WalletRepository walletRepository,
      MessagePublisher messagePublisher) {
    this.blockRepository = blockRepository;
    this.transactionRepository = transactionRepository;
    this.transactionHistoryRepository = transactionHistoryRepository;
    this.walletRepository = walletRepository;
    this.messagePublisher = messagePublisher;
  }

  @Transactional
  @Override
  public Transaction findOrCreateTransaction(CreateTransactionDto createTransactionDto) {
    Transaction transaction = transactionRepository.findOneByHash(createTransactionDto.getHash());

    if (transaction != null) {
      transaction.setStatus(createTransactionDto.getStatus());
      transactionRepository.save(transaction);

      return transaction;
    }

    Transaction newTransaction = createTransactionDto.toEntity();
    TransactionHistory transactionHistory = TransactionHistory.builder()
        .transaction(newTransaction)
        .transactionHash(newTransaction.getHash())
        .status(newTransaction.getStatus())
        .blockConfirmations(newTransaction.getBlockConfirmations())
        .build();

    transactionRepository.save(newTransaction);

    return newTransaction;
  }

  @Transactional
  @Override
  public Block updateMinedBlock(Long blockId, BigInteger lastestBlockNumber) throws IOException, Exception {
    Block minedBlock = blockRepository.findById(blockId).get();
    Integer blockConfirmations = lastestBlockNumber.subtract(minedBlock.getNumber()).intValue();

    updateMinedTransactionsByEthBlock(minedBlock.getNumber(), blockConfirmations);

    return minedBlock;
  }

  @Transactional
  @Override
  public Integer updateMinedTransactionsByEthBlock(BigInteger blockNumber, Integer blockConfirmations)
      throws IOException, Exception {
    List<Transaction> minedTransactions = transactionRepository.findByBlockNumber(blockNumber);

    minedTransactions.forEach(transaction -> {
      SendMinedTransactionMessageDto dto = SendMinedTransactionMessageDto.builder()
          .transactionId(transaction.getId())
          .blockConfirmations(blockConfirmations)
          .build();

      messagePublisher.sendMinedTransactionMessage(dto);
    });

    Integer minedTransactionsSize = minedTransactions.size();

    log.info("TransactionServiceImpl.updateMinedTransactionsByEthBlock() - Mined Transactions count: {}",
        minedTransactionsSize);

    return minedTransactionsSize;
  }

  @Transactional
  @Override
  public Transaction updateMinedTransaction(Long transactionId, Integer blockConfirmations) {
    Optional<Transaction> transaction = transactionRepository.findById(transactionId);

    if (transaction.isPresent()) {
      Transaction t = transaction.get();
      TransactionStatus newStatus = MAX_CONFIRMATIONS < blockConfirmations ? TransactionStatus.MINED
          : TransactionStatus.CONFIRMED;
      t.setStatus(newStatus);

      TransactionHistory transactionHistory = TransactionHistory.builder()
          .transaction(t)
          .transactionHash(t.getHash())
          .status(t.getStatus())
          .blockConfirmations(t.getBlockConfirmations())
          .build();

      if (newStatus == TransactionStatus.CONFIRMED) {
        Wallet fromWallet = walletRepository.findByAddress(t.getFromAddress()).get();
        Wallet toWallet = walletRepository.findByAddress(t.getToAddress()).get();

        if (fromWallet != null) {
          BigDecimal newBalance = fromWallet.getBalance().subtract(t.getAmount());
          fromWallet.setBalance(newBalance);
          walletRepository.save(fromWallet);
        }

        if (toWallet != null) {
          BigDecimal newBalance = toWallet.getBalance().add(t.getAmount());
          toWallet.setBalance(newBalance);
          walletRepository.save(toWallet);
        }

      }

      transactionRepository.save(t);

      return t;
    }

    return null;
  }

  @Override
  public List<TransactionHistory> getTransactionHistories(GetTransactionHistoriesDto getTransactionHistoriesDto) {
    Sort.Direction direction = (getTransactionHistoriesDto.getStarting_after() != null
        || getTransactionHistoriesDto.getEnding_after() != null) ? Sort.Direction.ASC : Sort.Direction.DESC;
    Pageable pageable = PageRequest.of(getTransactionHistoriesDto.getPage(), getTransactionHistoriesDto.getSize(),
        Sort.by(direction, "id"));

    List<TransactionHistory> histories = transactionHistoryRepository.findAllByOrderByIdDesc(pageable);

    return histories;
  }
}
