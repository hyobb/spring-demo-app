package com.example.demo.common.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.demo.common.constant.ErrorCode;
import com.example.demo.common.dto.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(CustomException.class)
  protected ResponseEntity<ErrorResponse> handleBusinessException(CustomException e) {
    ErrorCode errorCode = e.getErrorCode();

    ErrorResponse response = new ErrorResponse(errorCode.getCode(), errorCode.getMessage());
    return new ResponseEntity<>(response, errorCode.getHttpStatus());
  }

  @ExceptionHandler(Exception.class)
  protected ResponseEntity<ErrorResponse> handleException(Exception e) {
    ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;

    ErrorResponse response = new ErrorResponse(errorCode.getCode(), errorCode.getMessage());
    return new ResponseEntity<>(response, errorCode.getHttpStatus());
  }

}
