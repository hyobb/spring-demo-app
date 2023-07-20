package com.example.demo.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
  @Bean
  public MessageConverter messageConverter() {
    return new Jackson2JsonMessageConverter();
  }

  @Bean
  TopicExchange exchange() {
    return new TopicExchange("exchange");
  }

  @Bean
  Binding bindingTransferQueue(Queue transferQueue, TopicExchange exchange) {
    return BindingBuilder.bind(transferQueue).to(exchange).with("transfer");
  }

  @Bean
  Binding bindingPendingBlockQueue(Queue pendingBlockQueue, TopicExchange exchange) {
    return BindingBuilder.bind(pendingBlockQueue).to(exchange).with("pending-block");
  }

  @Bean
  Binding bindingMinedBlockQueue(Queue minedBlockQueue, TopicExchange exchange) {
    return BindingBuilder.bind(minedBlockQueue).to(exchange).with("mined-block");
  }

  @Bean
  Binding bindingMinedTransactionQueue(Queue minedTransactionQueue, TopicExchange exchange) {
    return BindingBuilder.bind(minedTransactionQueue).to(exchange).with("mined-transaction");
  }

  @Bean
  public Queue transferQueue() {
    return new Queue("transfer-queue", true);
  }

  @Bean
  public Queue pendingBlockQueue() {
    return new Queue("pending-block-queue", true);
  }

  @Bean
  public Queue minedBlockQueue() {
    return new Queue("mined-block-queue", true);
  }

  @Bean
  public Queue minedTransactionQueue() {
    return new Queue("mined-transaction-queue", true);

  }
}
