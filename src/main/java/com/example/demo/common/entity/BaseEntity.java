package com.example.demo.common.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;

@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
public class BaseEntity {

  @CreatedDate
  @Column(updatable = false)
  private LocalDateTime created_at;

  @LastModifiedDate
  private LocalDateTime updated_at;
}