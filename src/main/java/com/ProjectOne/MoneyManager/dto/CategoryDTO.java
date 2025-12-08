package com.ProjectOne.MoneyManager.dto;


import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryDTO {
    Long id;
    String profileId;
    String name;
    String icon;
    String type;
    LocalDateTime createdAt,updatedAt;
}
