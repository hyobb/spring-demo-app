package com.example.demo.ethereum.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthBlock.TransactionResult;
import org.web3j.utils.Convert;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Transfer;

import com.example.demo.common.constant.ErrorCode;
import com.example.demo.common.exception.CustomException;
import com.example.demo.ethereum.entity.Block;
import com.example.demo.ethereum.entity.BlockStatus;
import com.example.demo.queue.dto.SendMinedBlockMessageDto;
import com.example.demo.queue.dto.SendPendingBlockMessageDto;
import com.example.demo.queue.dto.SendTransferMessageDto;
import com.example.demo.queue.service.MessagePublisher;
import com.example.demo.transaction.dao.BlockRepository;
import com.example.demo.transaction.dto.CreateTransactionDto;
import com.example.demo.transaction.entity.TransactionStatus;
import com.example.demo.transaction.service.TransactionService;
import com.example.demo.wallet.dao.WalletRepository;
import com.example.demo.wallet.entity.Wallet;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class EthereumServiceImpl implements EthereumService {
  private final TransactionService transactionService;
  private final BlockRepository blockRepository;
  private final WalletRepository walletRepository;
  private final MessagePublisher messagePublisher;
  private final Web3j web3j;
  private final EntityManager entityManager;

  @Autowired
  public EthereumServiceImpl(TransactionService transactionService,
      BlockRepository blockRepository,
      WalletRepository walletRepository,
      MessagePublisher messagePublisher,
      Web3j web3j,
      EntityManager entityManager) {
    this.transactionService = transactionService;
    this.blockRepository = blockRepository;
    this.walletRepository = walletRepository;
    this.messagePublisher = messagePublisher;
    this.web3j = web3j;
    this.entityManager = entityManager;
  }

  @Transactional
  @Scheduled(fixedDelay = 10_000)
  @Override
  public void pollNewBlock() throws IOException {
    Request<?, EthBlock> ethBlock;
    Block lastBlock = blockRepository.findFirstByOrderByNumberDesc();

    if (lastBlock == null) {
      ethBlock = web3j.ethGetBlockByNumber(DefaultBlockParameterName.LATEST, true);
    } else {
      BigInteger nextBlockNumber = lastBlock.getNumber().add(new BigInteger("1"));
      ethBlock = web3j
          .ethGetBlockByNumber(DefaultBlockParameter.valueOf(nextBlockNumber), true);
    }
    EthBlock ethBlockResponse = ethBlock.send();
    EthBlock.Block ethBlockBlock = ethBlockResponse.getBlock();

    if (ethBlockBlock == null) {
      throw new CustomException(ErrorCode.ETHEREUM_BLOCK_NOT_FOUND);
    }

    Block newBlock = blockRepository
        .save(new Block(ethBlockBlock.getNumber(), ethBlockBlock.getHash(), BlockStatus.PENDING));

    messagePublisher.sendPendingBlockMessage(new SendPendingBlockMessageDto(ethBlockBlock.getNumber()));
  }

  @Transactional
  @Scheduled(fixedDelay = 10_000)
  @Override
  public void updateMinedBlocks() throws IOException {
    BigInteger lastestBlockNumber = blockRepository.findFirstByOrderByNumberDesc().getNumber();
    List<Block> minedBlocks = blockRepository.findByStatus(BlockStatus.MINED);

    minedBlocks.forEach(block -> {
      SendMinedBlockMessageDto dto = SendMinedBlockMessageDto.builder()
          .blockId(block.getId()).latestBlockNumber(lastestBlockNumber).build();

      messagePublisher
          .sendMinedBlockMessage(dto);
    });

    log.info("EthereumServiceImpl.updateMinedBlock() - Mined Blocks count: {}", minedBlocks.size());
  }

  @Transactional
  @Override
  public void updatePendingBlock(BigInteger pendingBlockNumber) throws Exception {
    log.info("getTransactionsByEthBlock: {}", pendingBlockNumber);

    Block pendingBlock = entityManager.find(Block.class, pendingBlockNumber);

    if (pendingBlock == null) {
      throw new CustomException(ErrorCode.BLOCK_NOT_FOUND);
    }

    pendingBlock.setStatus(BlockStatus.MINED);

    Request<?, EthBlock> ethBlock = web3j
        .ethGetBlockByNumber(DefaultBlockParameter.valueOf(pendingBlockNumber), true);

    createOrUpdateTransactionsFromEthBlock(ethBlock.send().getBlock());
    entityManager.flush();
  }

  @Transactional
  @Override
  public void transfer(String fromAddress, String toAddress, String amount) {
    Wallet wallet = walletRepository.findByAddress(fromAddress).get();

    try {
      Credentials credentials = WalletUtils.loadCredentials(wallet.getPassword(), wallet.getFileName());
      TransactionReceipt transactionReceipt = Transfer
          .sendFunds(web3j, credentials, toAddress, new BigDecimal(amount), Convert.Unit.ETHER)
          .send();

      String transactionHash = transactionReceipt.getTransactionHash();
      log.info("Transfer sended. Transaction Hash: " + transactionHash);
    } catch (Exception e) {
      log.error("Transfer Error", e);

      SendTransferMessageDto dto = SendTransferMessageDto.builder().fromAddress(wallet.getAddress())
          .toAddress(toAddress).amount(amount).build();
      messagePublisher.sendTransferMessage(dto);

      log.info("Tranfer retried.");
    }
  }

  @Transactional
  private void createOrUpdateTransactionsFromEthBlock(
      EthBlock.Block ethBlock) throws Exception {
    if (ethBlock == null) {
      throw new CustomException(ErrorCode.ETHEREUM_BLOCK_NOT_FOUND);
    }

    List<TransactionResult> targetTransactions = ethBlock.getTransactions().stream()
        .filter(t -> {
          Transaction transaction = (Transaction) t.get();
          String from = transaction.getFrom();
          String to = transaction.getTo();
          Optional<Wallet> wallet = walletRepository.findByAddress(to);

          return (from != null && to != null && wallet.isPresent());
        }).collect(Collectors.toList());

    targetTransactions.forEach(t -> {
      Transaction transaction = (Transaction) t.get();

      CreateTransactionDto dto = CreateTransactionDto.builder()
          .fromAddress(transaction.getFrom())
          .toAddress(transaction.getTo())
          .amount(new BigDecimal(transaction.getValue(), 18))
          .status(TransactionStatus.MINED)
          .hash(transaction.getHash())
          .blockNumber(transaction.getBlockNumber())
          .blockConfirmations(0)
          .build();

      transactionService.findOrCreateTransaction(dto);
    });

    log.info("Created or Updated Transacions count: {}", targetTransactions.size());
  }
}
