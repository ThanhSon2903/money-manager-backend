package com.ProjectOne.MoneyManager.controller;

import com.ProjectOne.MoneyManager.dto.ExpenseDTO;
import com.ProjectOne.MoneyManager.dto.FilterDTO;
import com.ProjectOne.MoneyManager.dto.IncomeDTO;
import com.ProjectOne.MoneyManager.service.ExpenseService;
import com.ProjectOne.MoneyManager.service.IncomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/filter")
@RequiredArgsConstructor
public class FilterController {

    @Autowired
    ExpenseService expenseService;

    @Autowired
    IncomeService incomeService;

    @PostMapping
    public ResponseEntity<?> filterTransactions(@RequestBody FilterDTO filterDTO){
        LocalDate startDate = filterDTO.getStartDate() != null ? filterDTO.getStartDate() : LocalDate.MIN;
        LocalDate endDate = filterDTO.getEndDate() != null ? filterDTO.getEndDate() : LocalDate.now();
        String keyWord = filterDTO.getKeyWord() != null ? filterDTO.getKeyWord() :  "";

        //Nếu người dùng chọn sắp xếp theo trường nào (ví dụ: amount, name…) → dùng trường đó.
        String sortField = filterDTO.getSortField() != null ? filterDTO.getSortField() : "date";
        Sort.Direction direction = "desc".equals(filterDTO.getSortOrder()) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(direction,sortField);

        if("income".equals(filterDTO.getType())){
            List<IncomeDTO> incomes = incomeService.filterIncome(startDate,endDate,keyWord,sort);
            return ResponseEntity.ok(incomes);
        }
        else if("expense".equals(filterDTO.getType())){
            List<ExpenseDTO> expenses = expenseService.filterExpense(startDate,endDate,keyWord,sort);
            return ResponseEntity.ok(expenses);
        }
        else{
            return ResponseEntity.badRequest().body("Invalid type.Must be 'income' or 'expense' ");
        }
    }
}
