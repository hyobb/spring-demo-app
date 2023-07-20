package com.example.demo.queue.service;

import java.io.IOException;

import org.springframework.amqp.core.Message;

public interface MessageSubscriber {
  void receiveTransferMessage(Message message) throws IOException, Exception;

  void receivePendingBlockMessage(Message message) throws IOException, Exception;

  void receiveMinedBlockMessage(Message message) throws IOException, Exception;

  void receiveMinedTransactionMessage(Message message) throws IOException, Exception;
}
