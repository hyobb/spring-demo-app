package com.example.demo.wallet.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;

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

class WalletServiceImplTest {

  @Mock
  private WalletRepository walletRepository;

  @Mock
  private TransactionRepository transactionRepository;

  @Mock
  private MessagePublisher messagePublisher;

  @Mock
  private Web3j web3j;

  @Mock
  private PasswordEncoder passwordEncoder;

  @InjectMocks
  private WalletServiceImpl walletService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testCreateWallet() throws Exception {
    String password = "testPassword";
    String address = "0x1234567890abcdef";
    String walletFileName = "testWallet.json";
    Credentials credentials = mock(Credentials.class);

    CreateWalletRequest createWalletRequest = new CreateWalletRequest();
    createWalletRequest.setPassword(password);

    when(WalletUtils.generateNewWalletFile(password, null)).thenReturn(walletFileName);
    when(WalletUtils.loadCredentials(password, walletFileName)).thenReturn(credentials);
    when(credentials.getAddress()).thenReturn(address);
    when(passwordEncoder.encode(password)).thenReturn("encodedPassword");

    Wallet createdWallet = walletService.createWallet(createWalletRequest);

    assertNotNull(createdWallet);
    assertEquals(address, createdWallet.getAddress());
    assertEquals("encodedPassword", createdWallet.getPassword());
    assertEquals(walletFileName, createdWallet.getFileName());

    verify(walletRepository).save(any(Wallet.class));
  }

  @Test
  void testTransfer_Successful() throws Exception {
    String fromAddress = "0x1234567890abcdef";
    String toAddress = "0x9876543210fedcba";
    String password = "testPassword";
    String amount = "1000000000000000000"; // 1 Ether

    TransferRequest transferRequest = new TransferRequest();
    transferRequest.setToAddress(toAddress);
    transferRequest.setPassword(password);
    transferRequest.setAmount(amount);

    Wallet wallet = Wallet.builder().address(fromAddress).password("encodedPassword")
        .balance(new BigDecimal("2000000000000000000")).build();

    List<Transaction> pendingTransactions = new ArrayList<>();
    BigDecimal sumOfPendingAmount = new BigDecimal("500000000000000000"); // 0.5 Ether

    when(walletRepository.findByAddress(fromAddress)).thenReturn(Optional.of(wallet));
    when(passwordEncoder.matches(password, "encodedPassword")).thenReturn(true);
    when(transactionRepository.findByFromAddressAndStatus(fromAddress, TransactionStatus.PENDING))
        .thenReturn(pendingTransactions);

    walletService.transfer(fromAddress, transferRequest);

    SendTransferMessageDto expectedDto = SendTransferMessageDto.builder()
        .fromAddress(fromAddress)
        .toAddress(toAddress)
        .amount(amount)
        .build();
    verify(messagePublisher).sendTransferMessage(expectedDto);
  }

  @Test
  void testTransfer_InvalidPassword() throws Exception {
    String fromAddress = "0x1234567890abcdef";
    String password = "testPassword";
    String amount = "1000000000000000000"; // 1 Ether

    TransferRequest transferRequest = new TransferRequest();
    transferRequest.setPassword(password);
    transferRequest.setAmount(amount);

    Wallet wallet = Wallet.builder().address(fromAddress).password("encodedPassword")
        .balance(new BigDecimal("2000000000000000000")).build();

    when(walletRepository.findByAddress(fromAddress)).thenReturn(Optional.of(wallet));
    when(passwordEncoder.matches(password, "encodedPassword")).thenReturn(false);

    assertThrows(CustomException.class, () -> walletService.transfer(fromAddress, transferRequest));
  }
}
