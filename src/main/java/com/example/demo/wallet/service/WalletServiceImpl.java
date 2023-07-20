package com.example.demo.wallet.service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;

import com.example.demo.common.constant.ErrorCode;
import com.example.demo.common.exception.CustomException;
import com.example.demo.queue.dto.SendTransferMessageDto;
import com.example.demo.queue.service.MessagePublisher;
import com.example.demo.transaction.dao.TransactionRepository;
import com.example.demo.transaction.entity.Transaction;
import com.example.demo.transaction.entity.TransactionStatus;
import com.example.demo.wallet.dao.WalletRepository;
import com.example.demo.wallet.dto.CreateWalletRequest;
import com.example.demo.wallet.dto.TransferRequest;
import com.example.demo.wallet.entity.Wallet;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class WalletServiceImpl implements WalletService {
  private final WalletRepository walletRepository;
  private final TransactionRepository transactionRepository;
  private final MessagePublisher messagePublisher;
  private final Web3j web3j;
  private final PasswordEncoder passwordEncoder;

  @Autowired
  public WalletServiceImpl(WalletRepository walletRepository,
      TransactionRepository transactionRepository,
      MessagePublisher messagePublisher,
      Web3j web3j,
      PasswordEncoder passwordEncoder) {
    this.walletRepository = walletRepository;
    this.transactionRepository = transactionRepository;
    this.messagePublisher = messagePublisher;
    this.web3j = web3j;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public List<Wallet> getAllWallets() {
    return walletRepository.findAll();
  }

  @Override
  public Wallet createWallet(CreateWalletRequest createWalletRequest) throws Exception {
    String password = createWalletRequest.getPassword();
    String walletFileName = WalletUtils.generateNewWalletFile(password, null);
    Credentials credentials = WalletUtils.loadCredentials(password, walletFileName);
    String encodedPassword = passwordEncoder.encode(password);

    Wallet wallet = Wallet.builder().address(credentials.getAddress()).password(encodedPassword)
        .fileName(walletFileName).build();

    return walletRepository.save(wallet);
  }

  @Override
  public BigDecimal getBalance(String address) throws Exception {
    Optional<Wallet> wallet = walletRepository.findByAddress(address);

    if (wallet.isPresent()) {
      return wallet.get().getBalance();
    } else {
      throw new CustomException(ErrorCode.WALLET_NOT_FOUND);
    }
  }

  @Override
  public void transfer(String fromAddress, TransferRequest transferRequest) throws Exception {
    Wallet wallet = walletRepository.findByAddress(fromAddress).get();

    if (wallet == null) {
      throw new CustomException(ErrorCode.WALLET_NOT_FOUND);
    }

    validateTransfer(wallet, transferRequest);

    SendTransferMessageDto dto = SendTransferMessageDto.builder().fromAddress(wallet.getAddress())
        .toAddress(transferRequest.getToAddress()).amount(transferRequest.toString()).build();
    messagePublisher.sendTransferMessage(dto);
  }

  private void validateTransfer(Wallet wallet, TransferRequest transferRequest) throws Exception {
    if (!passwordEncoder.matches(transferRequest.getPassword(), wallet.getPassword())) {
      throw new CustomException(ErrorCode.WALLET_PASSWORD_INVALID);
    }

    List<Transaction> pendingTransactions = transactionRepository.findByFromAddressAndStatus(wallet.getAddress(),
        TransactionStatus.PENDING);
    BigDecimal sumOfPendingAmount = pendingTransactions.stream().map(t -> t.getAmount()).reduce(new BigDecimal(0),
        (acc, amount) -> acc.add(amount));
    BigDecimal availableBalance = wallet.getBalance().subtract(sumOfPendingAmount);
    BigDecimal amountOut = new BigDecimal(new BigInteger(transferRequest.getAmount(), 18));

    if (availableBalance.compareTo(amountOut) < 0) {
      throw new CustomException(ErrorCode.WALLET_INSUFFICIENT_BALANCE);
    }
  }
}
