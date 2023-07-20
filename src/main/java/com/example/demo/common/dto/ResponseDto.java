package com.example.demo.common.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor
public class ResponseDto<T> {

  private final T data;

  public static <T> ResponseDto<T> of(T data) {
    return new ResponseDto<>(data);
  }
}