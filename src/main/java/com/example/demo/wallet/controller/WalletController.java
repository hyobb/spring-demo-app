package com.example.demo.wallet.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.common.dto.ResponseDto;
import com.example.demo.wallet.dto.CreateWalletRequest;
import com.example.demo.wallet.dto.TransferRequest;
import com.example.demo.wallet.dto.WalletResponse;
import com.example.demo.wallet.entity.Wallet;
import com.example.demo.wallet.service.WalletService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/wallets")
public class WalletController {

  private final WalletService walletService;

  @GetMapping
  public ResponseDto<List<WalletResponse>> getWallets() {
    List<Wallet> wallets = walletService.getAllWallets();
    List<WalletResponse> responses = wallets.stream().map(w -> new WalletResponse(w)).collect(Collectors.toList());

    return ResponseDto.of(responses);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public ResponseDto<WalletResponse> createWallet(@RequestBody @Valid final CreateWalletRequest createWalletRequest)
      throws Exception {
    Wallet wallet = walletService.createWallet(createWalletRequest);

    return ResponseDto.of(WalletResponse.builder().wallet(wallet).build());
  }

  @GetMapping("/{address}/balance")
  public ResponseDto<String> getBalance(@PathVariable(name = "address") String address) throws Exception {
    String balance = walletService.getBalance(address).toString();

    return ResponseDto.of(balance);
  }

  @PostMapping("/{address}/transfer")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void transfer(@PathVariable(name = "address") String fromAddress,
      @RequestBody @Valid final TransferRequest transferRequest) throws Exception {
    walletService.transfer(fromAddress, transferRequest);
  }
}
