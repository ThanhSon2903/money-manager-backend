package com.ProjectOne.MoneyManager.entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProfileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String fullName,password,profileImageUrl;
    @Column(unique = true)
    String email;

    @Column(updatable = false)
    @CreationTimestamp
    LocalDateTime createdAt;

    @UpdateTimestamp
    LocalDateTime updatedAt;
    Boolean isActive;
    String activationToken;//mã kích hoạt

    @PrePersist //Annotation được gọi trước khi 1 obj được lưu vào DB
    public void prePersist(){
        if(this.isActive == null){
            isActive = false;
        }
    }
}
