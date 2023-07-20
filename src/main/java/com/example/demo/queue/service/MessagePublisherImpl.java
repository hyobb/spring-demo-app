package com.example.demo.queue.service;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.demo.queue.dto.SendMinedBlockMessageDto;
import com.example.demo.queue.dto.SendMinedTransactionMessageDto;
import com.example.demo.queue.dto.SendPendingBlockMessageDto;
import com.example.demo.queue.dto.SendTransferMessageDto;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class MessagePublisherImpl implements MessagePublisher {
  private final RabbitTemplate rabbitTemplate;
  private final Queue pendingBlockQueue;
  private final Queue minedBlockQueue;
  private final Queue minedTransactionQueue;

  @Autowired
  public MessagePublisherImpl(RabbitTemplate rabbitTemplate, Queue pendingBlockQueue, Queue minedBlockQueue,
      Queue minedTransactionQueue) {
    this.rabbitTemplate = rabbitTemplate;
    this.pendingBlockQueue = pendingBlockQueue;
    this.minedBlockQueue = minedBlockQueue;
    this.minedTransactionQueue = minedTransactionQueue;
  }

  @Override
  public void sendTransferMessage(SendTransferMessageDto sendTransferMessageDto) {
    log.info("Transfer Message Sent: {}", sendTransferMessageDto);

    rabbitTemplate.convertAndSend("transfer-queue", sendTransferMessageDto);
  }

  @Override
  public void sendPendingBlockMessage(SendPendingBlockMessageDto sendPendingBlockMessageDto) {
    log.info("PendingBlock Message Sent: {}", sendPendingBlockMessageDto);

    rabbitTemplate.convertAndSend("pending-block-queue", sendPendingBlockMessageDto);
  }

  @Override
  public void sendMinedBlockMessage(SendMinedBlockMessageDto sendMinedBlockMessageDto) {
    log.info("MinedBlock Message Sent: {}", sendMinedBlockMessageDto);

    rabbitTemplate.convertAndSend("mined-block-queue", sendMinedBlockMessageDto);
  }

  @Override
  public void sendMinedTransactionMessage(SendMinedTransactionMessageDto sendMinedTransactionMessageDto) {
    log.info("Mined Transaction Message Sent: {}", sendMinedTransactionMessageDto);

    rabbitTemplate.convertAndSend("mined-transaction-queue", sendMinedTransactionMessageDto);
  }

}
