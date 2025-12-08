package com.ProjectOne.MoneyManager.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FilterDTO {
    String type;
    LocalDate startDate;
    LocalDate endDate;
    String keyWord;
    String sortField; // sort field amount,name,id,...
    String sortOrder;
}
