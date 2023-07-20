package com.example.demo.queue.dto;

import java.math.BigInteger;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SendMinedBlockMessageDto {
  @NotEmpty
  private Long blockId;

  @NotEmpty
  private BigInteger latestBlockNumber;

  @Builder
  public SendMinedBlockMessageDto(Long blockId, BigInteger latestBlockNumber) {
    this.blockId = blockId;
    this.latestBlockNumber = latestBlockNumber;
  }
}
