package com.example.demo.common.constant;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum ErrorCode {
  WALLET_NOT_FOUND(HttpStatus.NOT_FOUND, "WALLET_001", "Wallet Not found"),
  WALLET_PASSWORD_INVALID(HttpStatus.UNAUTHORIZED, "WALLET_002", "Wallet Password Invalid"),
  WALLET_INSUFFICIENT_BALANCE(HttpStatus.BAD_REQUEST, "WALLET_003", "Insufficient Wallet Balance"),

  BLOCK_NOT_FOUND(HttpStatus.NOT_FOUND, "BLOCK_001", "Block Not Found"),

  ETHEREUM_BLOCK_NOT_FOUND(HttpStatus.NOT_FOUND, "ETHEREUM_001", "Ethereum Block Not Found"),

  INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON_001", "Internal Server Error");

  private final HttpStatus httpStatus;
  private final String code;
  private final String message;

  ErrorCode(HttpStatus httpStatus, String code, String message) {
    this.httpStatus = httpStatus;
    this.code = code;
    this.message = message;
  }

  public HttpStatus getHttpStatus() {
    return httpStatus;
  }
}