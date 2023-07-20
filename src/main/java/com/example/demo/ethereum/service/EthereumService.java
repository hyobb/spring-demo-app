package com.example.demo.ethereum.service;

import java.io.IOException;
import java.math.BigInteger;

public interface EthereumService {
  void pollNewBlock() throws IOException;

  void updateMinedBlocks() throws IOException;

  void updatePendingBlock(BigInteger blockNumber) throws IOException, Exception;

  public void transfer(String fromAddress, String toAddress, String amount);
}
