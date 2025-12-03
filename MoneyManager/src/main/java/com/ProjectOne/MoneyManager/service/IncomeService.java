package com.ProjectOne.MoneyManager.service;

import com.ProjectOne.MoneyManager.dto.ExpenseDTO;
import com.ProjectOne.MoneyManager.dto.IncomeDTO;
import com.ProjectOne.MoneyManager.entity.CategoryEntity;
import com.ProjectOne.MoneyManager.entity.ExpenseEntity;
import com.ProjectOne.MoneyManager.entity.IncomeEntity;
import com.ProjectOne.MoneyManager.entity.ProfileEntity;
import com.ProjectOne.MoneyManager.repository.CategoryRepository;
import com.ProjectOne.MoneyManager.repository.IncomeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IncomeService {

    @Autowired
    IncomeRepository incomeRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    ProfileService profileService;

    @Autowired
    CategoryService categoryService;

    //Method Hỗ trợ
    private IncomeEntity toEntity(IncomeDTO dto, ProfileEntity profileEntity, CategoryEntity category){
        return IncomeEntity.builder()
                .name(dto.getName())
                .icon(dto.getIcon())
                .amount(dto.getAmount())
                .date(dto.getDate())
                .profileEntity(profileEntity)
                .categoryEntity(category)
                .build();
    }

    //Method Hỗ trợ
    private IncomeDTO toDTO(IncomeEntity entity){
        return IncomeDTO.builder()
                .id(entity.getId())
                .icon(entity.getIcon())
                .name(entity.getName())
                .categoryId(entity.getCategoryEntity() != null ? entity.getCategoryEntity().getId() : null)
                .categoryName(entity.getCategoryEntity() != null ? entity.getCategoryEntity().getName() : "N/A")
                .date(entity.getDate())
                .createdAt(entity.getCreateAt())
                .updatedAt(entity.getUpdateAt())
                .amount(entity.getAmount())
                .build();
    }

    //Thêm một khoản thu mới vào cơ sở dữ liệu
    public IncomeDTO addIncome(IncomeDTO dto){
        ProfileEntity profile = profileService.getCurrentProfile();
        CategoryEntity category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Danh mục không tìm thấy"));

        IncomeEntity newIncome = toEntity(dto,profile,category);
        incomeRepository.save(newIncome);
        return toDTO(newIncome);
    }

    //Lấy ra tất cả các khoản thu (Income) mà người dùng đã tạo trong một khoảng thời gian
    public List<IncomeDTO> getCurrentMonthIncomesForCurrentUser() {
        ProfileEntity profile = profileService.getCurrentProfile();
        LocalDate now = LocalDate.now();
        LocalDate startDate = now.withDayOfMonth(1);
        LocalDate endDate = now.withDayOfMonth(now.lengthOfMonth());
        List<IncomeEntity> list = incomeRepository.findByProfileEntity_IdAndDateBetween(profile.getId(),startDate,endDate);
        return list.stream().map(this::toDTO).toList();
    }

    //Xoá đi khoản thu
    public void deleteIncome(Long Id){
        ProfileEntity profile = profileService.getCurrentProfile();
        IncomeEntity entity = incomeRepository.findById(Id).orElseThrow(() -> new RuntimeException("Khoản thu không tồn tại"));
        if(!entity.getProfileEntity().getId().equals(profile.getId())){
            throw new RuntimeException("Không được phép xóa chi phí này");
        }
        incomeRepository.delete(entity);
    }

    //Lấy về 5 khoản thu mới nhất
    public List<IncomeDTO> getLatest5Incomes(){
        ProfileEntity profile = profileService.getCurrentProfile();
        List<IncomeEntity> list = incomeRepository.findTop5ByProfileEntity_IdOrderByDateDesc(profile.getId());
        return list.stream().map(this::toDTO).toList();
    }

    //Lấy về tổng tiền thu
    public BigDecimal getTotalIncome(){
        ProfileEntity profile = profileService.getCurrentProfile();
        BigDecimal total = incomeRepository.findTotalIncomeByProfileEntity_Id(profile.getId());
        return total != null ? total : BigDecimal.ZERO;
    }

    //Lọc khoản thu
    public List<IncomeDTO> filterIncome(LocalDate startDate, LocalDate endDate, String keyWord, Sort sort){
        ProfileEntity profile = profileService.getCurrentProfile();
        List<IncomeEntity> list = incomeRepository.findByProfileEntity_IdAndDateBetweenAndNameContainingIgnoreCase(
                profile.getId(),
                startDate,
                endDate,
                keyWord,
                sort
        );
        return list.stream().map(this::toDTO).toList();
    }
}
