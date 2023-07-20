package com.example.demo.queue.dto;

import java.math.BigInteger;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SendPendingBlockMessageDto {
  @NotEmpty
  private BigInteger blockNumber;

  @Builder
  public SendPendingBlockMessageDto(BigInteger blockNumber) {
    this.blockNumber = blockNumber;
  }
}
