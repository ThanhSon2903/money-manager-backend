package com.ProjectOne.MoneyManager.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExpenseDTO {
    Long id;
    String name;
    String icon;
    String categoryName;
    Long categoryId;
    BigDecimal amount;
    LocalDate date;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
