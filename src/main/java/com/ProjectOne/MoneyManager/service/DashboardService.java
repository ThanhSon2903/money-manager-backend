package com.ProjectOne.MoneyManager.service;


import com.ProjectOne.MoneyManager.dto.ExpenseDTO;
import com.ProjectOne.MoneyManager.dto.IncomeDTO;
import com.ProjectOne.MoneyManager.dto.RecentTransactionDTO;
import com.ProjectOne.MoneyManager.entity.ProfileEntity;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Stream.concat;
import static org.apache.coyote.http11.Constants.a;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DashboardService {

    @Autowired
    IncomeService incomeService;

    @Autowired
    ExpenseService expenseService;

    @Autowired
    ProfileService profileService;

    public Map<String,Object> getDashboardData(){
        ProfileEntity profile = profileService.getCurrentProfile();
        Map<String, Object> returnVal = new LinkedHashMap<>();
        List<IncomeDTO> latestIncomes = incomeService.getLatest5Incomes();
        List<ExpenseDTO> latestExpenses = expenseService.getLatest5Expenses();

        /*
        Concat ở hàng dưới để nối 2 stream -> 1 stream duy nhất
        Stream: Thao tác với Collection
            Các method có trong interface Stream
            + Filter
            + Mapping
            + Distinct
            + Sorted
            + Sorted(Comparator<T> comparator)
            + forEach
         */
        List<RecentTransactionDTO> recentTransactions = concat(latestIncomes.stream().map(income ->
                    RecentTransactionDTO.builder()
                        .id(income.getId())
                        .profileId(profile.getId())
                        .icon(income.getIcon())
                        .name(income.getName())
                        .amount(income.getAmount())
                        .date(income.getDate())
                        .createdAt(income.getCreatedAt())
                        .updatedAt(income.getUpdatedAt())
                        .type("income")
                        .build()),
                latestExpenses.stream().map(expense ->
                    RecentTransactionDTO.builder()
                            .id(expense.getId())
                            .profileId(profile.getId())
                            .icon(expense.getIcon())
                            .name(expense.getName())
                            .amount(expense.getAmount())
                            .date(expense.getDate())
                            .createdAt(expense.getCreatedAt())
                            .updatedAt(expense.getUpdatedAt())
                            .type("expense")
                            .build()))
                .sorted((a,b) -> {
                    int cmp = b.getDate().compareTo(a.getDate());
                    if(cmp == 0 && a.getCreatedAt() != null && b.getCreatedAt() != null) {
                        return b.getCreatedAt().compareTo(a.getCreatedAt());
                    }
                    return cmp;
                }).collect(Collectors.toList());
            returnVal.put("totalBalance",incomeService.getTotalIncome()// Số dư hiện tại
                    .subtract(expenseService.getTotalExpenses()));
            returnVal.put("totalIncome",incomeService.getTotalIncome());
            returnVal.put("totalExpense",expenseService.getTotalExpenses());
            returnVal.put("recent5Expenses",expenseService.getLatest5Expenses());
            returnVal.put("recent5Incomes",incomeService.getLatest5Incomes());
            returnVal.put("recentTransactions",recentTransactions);
            return returnVal;
    }
}



