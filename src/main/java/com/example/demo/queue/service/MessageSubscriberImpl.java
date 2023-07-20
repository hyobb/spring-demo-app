package com.example.demo.queue.service;

import java.io.IOException;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.demo.ethereum.service.EthereumService;
import com.example.demo.queue.dto.SendMinedBlockMessageDto;
import com.example.demo.queue.dto.SendMinedTransactionMessageDto;
import com.example.demo.queue.dto.SendPendingBlockMessageDto;
import com.example.demo.queue.dto.SendTransferMessageDto;
import com.example.demo.transaction.service.TransactionService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class MessageSubscriberImpl implements MessageSubscriber {
  private final EthereumService ethereumService;
  private final TransactionService transactionService;
  private final MessageConverter messageConverter;

  @Autowired
  public MessageSubscriberImpl(EthereumService ethereumService, TransactionService transactionService,
      MessageConverter messageConverter) {
    this.ethereumService = ethereumService;
    this.transactionService = transactionService;
    this.messageConverter = messageConverter;
  }

  @RabbitListener(queues = "transfer-queue")
  @Override
  public void receiveTransferMessage(Message message) throws IOException, Exception {
    SendTransferMessageDto sendTransferMessageDto = (SendTransferMessageDto) messageConverter
        .fromMessage(message);

    log.info("Received Transfer Message: {}", sendTransferMessageDto.toString());

    ethereumService.transfer(sendTransferMessageDto.getFromAddress(), sendTransferMessageDto.getToAddress(),
        sendTransferMessageDto.getAmount());
    ;
  }

  @RabbitListener(queues = "pending-block-queue")
  @Override
  public void receivePendingBlockMessage(Message message) throws IOException, Exception {
    SendPendingBlockMessageDto sendPendingBlockMessageDto = (SendPendingBlockMessageDto) messageConverter
        .fromMessage(message);

    log.info("Received PendingBlock Message: {}", sendPendingBlockMessageDto.toString());

    ethereumService.updatePendingBlock(sendPendingBlockMessageDto.getBlockNumber());
  }

  @RabbitListener(queues = "mined-block-queue")
  @Override
  public void receiveMinedBlockMessage(Message message) throws IOException, Exception {
    SendMinedBlockMessageDto sendMinedBlockMessageDto = (SendMinedBlockMessageDto) messageConverter
        .fromMessage(message);

    log.info("Received PendingBlock Message: {}", sendMinedBlockMessageDto.toString());

    transactionService.updateMinedBlock(sendMinedBlockMessageDto.getBlockId(),
        sendMinedBlockMessageDto.getLatestBlockNumber());
  }

  @RabbitListener(queues = "mined-transaction-queue")
  @Override
  public void receiveMinedTransactionMessage(Message message) throws IOException, Exception {
    SendMinedTransactionMessageDto sendMinedTransactionMessageDto = (SendMinedTransactionMessageDto) messageConverter
        .fromMessage(message);

    log.info("Received PendingBlock Message: {}", sendMinedTransactionMessageDto.toString());

    transactionService.updateMinedTransaction(sendMinedTransactionMessageDto.getTransactionId(),
        sendMinedTransactionMessageDto.getBlockConfirmations());
  }
}
