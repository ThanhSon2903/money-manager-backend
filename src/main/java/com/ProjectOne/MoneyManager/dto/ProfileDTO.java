package com.ProjectOne.MoneyManager.dto;


import jakarta.persistence.Column;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProfileDTO {

    Long id;
    String fullName,password,profileImageUrl;
    String email;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
