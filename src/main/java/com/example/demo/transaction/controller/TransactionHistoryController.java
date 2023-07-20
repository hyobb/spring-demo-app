package com.example.demo.transaction.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.common.dto.ResponseDto;
import com.example.demo.transaction.dto.GetTransactionHistoriesDto;
import com.example.demo.transaction.dto.TransactionHistoryReponse;
import com.example.demo.transaction.entity.TransactionHistory;
import com.example.demo.transaction.service.TransactionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/transaction-histories")
public class TransactionHistoryController {

  private final TransactionService transactionService;

  @GetMapping
  public ResponseDto<List<TransactionHistoryReponse>> getTransactionHistories(
      @RequestParam @Valid final GetTransactionHistoriesDto getTransactionHistoriesDto) {
    List<TransactionHistory> histories = transactionService.getTransactionHistories(getTransactionHistoriesDto);
    List<TransactionHistoryReponse> responses = histories.stream().map(h -> new TransactionHistoryReponse(h))
        .collect(Collectors.toList());

    return ResponseDto.of(responses);
  }
}
