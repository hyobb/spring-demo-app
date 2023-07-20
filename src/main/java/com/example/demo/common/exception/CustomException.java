package com.example.demo.common.exception;

import com.example.demo.common.constant.ErrorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CustomException extends RuntimeException {
  private final ErrorCode errorCode;
}