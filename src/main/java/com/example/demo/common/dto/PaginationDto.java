package com.example.demo.common.dto;

import jakarta.validation.constraints.Max;
import lombok.Getter;

@Getter
public class PaginationDto {
  private Integer page;

  @Max(100)
  private Integer size;
}
