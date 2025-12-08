package com.ProjectOne.MoneyManager.repository;

import com.ProjectOne.MoneyManager.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity,Long> {

    //Select * from tbl_categories where profile_Id = ?
    List<CategoryEntity> findByProfileEntity_Id(Long profileId);

    //Select * from tbl_categories where id = ? AND profile_Id = ?
    Optional<CategoryEntity> findByIdAndProfileEntity_Id(Long id,Long profileId);

    //Select * from tbl_categories where type = "?" AND profile_Id = ?
    List<CategoryEntity> findByTypeAndProfileEntity_Id(String type, Long profileId);

    //Select exists (Select 1 from tbl_categories where name = "?" AND profile_Id = ?)
    Boolean existsByNameAndProfileEntity_Id(String name, Long profileId);
}
