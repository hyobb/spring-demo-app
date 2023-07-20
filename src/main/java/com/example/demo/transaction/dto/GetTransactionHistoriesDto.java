package com.example.demo.transaction.dto;

import com.example.demo.common.dto.PaginationDto;

import lombok.Getter;

@Getter
public class GetTransactionHistoriesDto extends PaginationDto {
  private Long starting_after;
  private Long ending_after;
}
