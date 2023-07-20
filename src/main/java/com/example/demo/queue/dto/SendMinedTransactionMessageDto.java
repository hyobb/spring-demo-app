package com.example.demo.queue.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SendMinedTransactionMessageDto {
  @NotEmpty
  private Long transactionId;

  @NotEmpty
  private Integer blockConfirmations;

  @Builder
  public SendMinedTransactionMessageDto(Long transactionId, Integer blockConfirmations) {
    this.transactionId = transactionId;
    this.blockConfirmations = blockConfirmations;
  }
}
