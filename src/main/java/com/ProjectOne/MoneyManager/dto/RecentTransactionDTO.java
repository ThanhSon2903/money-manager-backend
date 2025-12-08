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
public class RecentTransactionDTO {
    Long id;
    Long profileId;
    String icon;
    String name;
    BigDecimal amount;
    LocalDate date;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    String type;
}
