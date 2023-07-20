package com.example.demo.queue.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SendTransferMessageDto {
  @NotBlank
  private String fromAddress;

  @NotBlank
  private String toAddress;

  @NotBlank
  private String amount;

  @Builder
  public SendTransferMessageDto(String fromAddress, String toAddress, String amount) {
    this.fromAddress = fromAddress;
    this.toAddress = toAddress;
    this.amount = amount;
  }
}
