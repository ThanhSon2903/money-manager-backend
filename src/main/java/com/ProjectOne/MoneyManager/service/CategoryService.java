package com.ProjectOne.MoneyManager.service;

import com.ProjectOne.MoneyManager.dto.CategoryDTO;
import com.ProjectOne.MoneyManager.entity.CategoryEntity;
import com.ProjectOne.MoneyManager.entity.ProfileEntity;
import com.ProjectOne.MoneyManager.repository.CategoryRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryService {

    @Autowired
    ProfileService profileService;


    @Autowired
    CategoryRepository categoryRepository;

    //Lưu category
    public CategoryDTO saveCate(CategoryDTO categoryDTO){
        ProfileEntity profileEntity = profileService.getCurrentProfile();
        if(categoryRepository.existsByNameAndProfileEntity_Id(categoryDTO.getName(), categoryDTO.getId())){
            throw new ResponseStatusException(HttpStatus.CONFLICT,"Danh mục có tên này đã tồn tại");
        }
        CategoryEntity newCategory = toEntity(categoryDTO,profileEntity);
        categoryRepository.save(newCategory);
        return toDTO(newCategory);
    }

    //Lấy danh mục cho người dùng hiện tại
    public List<CategoryDTO> getCateForCurrUser(){
        ProfileEntity profile = profileService.getCurrentProfile();
        List<CategoryEntity> categories = categoryRepository.findByProfileEntity_Id(profile.getId());
        return categories.stream().map(this::toDTO).toList();
    }

    //Lấy về danh mục của người dùng hiện tại theo loại và Id
    public List<CategoryDTO> getCategoriesByTypeForCurrentUser(String type){
        ProfileEntity profileEntity = profileService.getCurrentProfile();
        List<CategoryEntity> list = categoryRepository.findByTypeAndProfileEntity_Id(type, profileEntity.getId());
        return list.stream().map(this::toDTO).toList();
    }

    public CategoryDTO updateCategory(Long id, CategoryDTO categoryDTO){
        ProfileEntity profileEntity = profileService.getCurrentProfile();
        CategoryEntity categoryEntity = categoryRepository.findByIdAndProfileEntity_Id(id,profileEntity.getId())
                .orElseThrow(() -> new RuntimeException("Danh mục không tìm thấy hoặc không có sẵn"));
        categoryEntity.setName(categoryDTO.getName());
        categoryEntity.setType(categoryDTO.getType());
        categoryEntity.setIcon(categoryDTO.getIcon());
        categoryRepository.save(categoryEntity);
        return toDTO(categoryEntity);
    }

    CategoryEntity toEntity(CategoryDTO categoryDTO, ProfileEntity profile){
        return CategoryEntity.builder()
                .name(categoryDTO.getName())
                .icon(categoryDTO.getIcon())
                .profileEntity(profile)
                .type(categoryDTO.getType())
                .build();
    }

    CategoryDTO toDTO(CategoryEntity categoryEntity){
        return CategoryDTO.builder()
                .id(categoryEntity.getId())
                .profileId(String.valueOf(categoryEntity.getProfileEntity() != null ? categoryEntity.getProfileEntity().getId() : null))
                .name(categoryEntity.getName())
                .icon(categoryEntity.getIcon())
                .createdAt(categoryEntity.getCreatedAt())
                .updatedAt(categoryEntity.getUpdatedAt())
                .type(categoryEntity.getType())
                .build();
    }
}
