package com.example.demo.queue.service;

import com.example.demo.queue.dto.SendMinedBlockMessageDto;
import com.example.demo.queue.dto.SendMinedTransactionMessageDto;
import com.example.demo.queue.dto.SendPendingBlockMessageDto;
import com.example.demo.queue.dto.SendTransferMessageDto;

public interface MessagePublisher {
  void sendTransferMessage(SendTransferMessageDto sendTransferMessageDto);

  void sendPendingBlockMessage(SendPendingBlockMessageDto sendPendingBlockMessageDto);

  void sendMinedBlockMessage(SendMinedBlockMessageDto sendMinedBlockMessageDto);

  void sendMinedTransactionMessage(SendMinedTransactionMessageDto sendMinedTransactionMessageDto);
}
