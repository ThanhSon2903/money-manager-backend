package com.ProjectOne.MoneyManager.service;

import com.ProjectOne.MoneyManager.dto.ExpenseDTO;
import com.ProjectOne.MoneyManager.entity.CategoryEntity;
import com.ProjectOne.MoneyManager.entity.ExpenseEntity;
import com.ProjectOne.MoneyManager.entity.ProfileEntity;
import com.ProjectOne.MoneyManager.repository.CategoryRepository;
import com.ProjectOne.MoneyManager.repository.ExpenseRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.ExcelNumberFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExpenseService {

    @Autowired
    ExpenseRepository expenseRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    CategoryService categoryService;

    @Autowired
    ProfileService profileService;

    //Method Hỗ trợ
    private ExpenseEntity toEntity(ExpenseDTO dto, ProfileEntity profileEntity, CategoryEntity category){
        return ExpenseEntity.builder()
                .name(dto.getName())
                .icon(dto.getIcon())
                .amount(dto.getAmount())
                .date(dto.getDate())
                .profileEntity(profileEntity)
                .categoryEntity(category)
                .build();
    }


    //Method Hỗ trợ
    private ExpenseDTO toDTO(ExpenseEntity entity){
        return ExpenseDTO.builder()
                .id(entity.getId())
                .icon(entity.getIcon())
                .name(entity.getName())
                .amount(entity.getAmount())
                .categoryId(entity.getCategoryEntity() != null ? entity.getCategoryEntity().getId() : null)
                .categoryName(entity.getCategoryEntity() != null ? entity.getCategoryEntity().getName() : "N/A")
                .date(entity.getDate())
                .createdAt(entity.getCreateAt())
                .updatedAt(entity.getUpdateAt())
                .build();
    }

    //Thêm một khoản chi mới vào cơ sở dữ liệu
    public ExpenseDTO addExpense(ExpenseDTO dto){
        ProfileEntity profile = profileService.getCurrentProfile();
        CategoryEntity category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Danh mục không tìm thấy"));

        ExpenseEntity newExpense = toEntity(dto,profile,category);
        expenseRepository.save(newExpense);
        return toDTO(newExpense);
    }

    //Lấy ra tất cả các khoản chi tiêu (Expense) mà người dùng đã tạo trong một 1 month
    public List<ExpenseDTO> getCurrentMonthExpensesForCurrentUser() {
        ProfileEntity profile = profileService.getCurrentProfile();
        LocalDate now = LocalDate.now();
        LocalDate startDate = now.withDayOfMonth(1);
        LocalDate endDate = now.withDayOfMonth(now.lengthOfMonth());
        List<ExpenseEntity> list = expenseRepository.findByProfileEntity_IdAndDateBetween(profile.getId(),startDate,endDate);
        return list.stream().map(this::toDTO).toList();
    }

    //Xoá đi khoản chi tiêu dựa vào Id người dùng hiện tại
    public void deleteExpense(Long Id){
        ProfileEntity profile = profileService.getCurrentProfile();
        ExpenseEntity entity = expenseRepository.findById(Id).orElseThrow(() -> new RuntimeException("Khoản chi không tồn tại"));
        if(!entity.getProfileEntity().getId().equals(profile.getId())){
            throw new RuntimeException("Không được phép xóa chi phí này");
        }
        expenseRepository.delete(entity);
    }

    //Lấy về 5 khoản chi mới nhất
    public List<ExpenseDTO> getLatest5Expenses(){
        ProfileEntity profile = profileService.getCurrentProfile();
        List<ExpenseEntity> list = expenseRepository.findTop5ByProfileEntity_IdOrderByDateDesc(profile.getId());
        return list.stream().map(this::toDTO).toList();
    }

    //Lấy về tổng tiền chi
    public BigDecimal getTotalExpenses(){
        ProfileEntity profile = profileService.getCurrentProfile();
        BigDecimal total = expenseRepository.findTotalExpenseByProfileEntity_Id(profile.getId());
        return total != null ? total : BigDecimal.ZERO;
    }

    //Lọc khoản chi
    public List<ExpenseDTO> filterExpense(LocalDate startDate, LocalDate endDate, String keyWord, Sort sort){
        ProfileEntity profile = profileService.getCurrentProfile();
        List<ExpenseEntity> list = expenseRepository.findByProfileEntity_IdAndDateBetweenAndNameContainingIgnoreCase(profile.getId(),
                startDate,
                endDate,
                keyWord,
                sort);
        return list.stream().map(this::toDTO).toList();
    }


    //Lấy danh sách các khoản chi tiêu của một user trong một ngày cụ thể.
    public List<ExpenseDTO> getExpensesForUserOnDate(Long id, LocalDate date){
        List<ExpenseEntity> list = expenseRepository.findByProfileEntity_IdAndDate(id,date);
        return list.stream().map(this::toDTO).toList();
    }


}
