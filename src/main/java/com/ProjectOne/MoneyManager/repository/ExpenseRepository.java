package com.ProjectOne.MoneyManager.repository;

import com.ProjectOne.MoneyManager.entity.ExpenseEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<ExpenseEntity,Long> {

    /*
        select * from tbl_expenses where ProfileEntity_Id = ?1 order by date desc
        lấy ra danh sách tất cả các khoản chi tiêu dựa vào Id và sắp xếp giảm dần theo ngày
     */
    List<ExpenseEntity> findByProfileEntity_IdOrderByDateDesc(Long ProfileEntity_Id);


    /*
        select * from tbl_expenses where ProfileEntity_Id = ?1 order by date desc limit 5
        lấy ra danh sách top 5 khoản cho tiêu dựa vào Id và sắp xếp giảm dần theo ngày
     */
    List<ExpenseEntity> findTop5ByProfileEntity_IdOrderByDateDesc(Long ProfileEntity_Id);


    /*
       Lấy ra tổng chi tiêu dựa vào Id người dùng
    */
    @Query("SELECT SUM(e.amount) FROM ExpenseEntity e WHERE e.profileEntity.id = :profileId")
    BigDecimal findTotalExpenseByProfileEntity_Id(@Param("profileId")Long profileId);


    /*
    name like concat(?4,'%'),('%',?4,'%'),('%',?4)
        select * from tbl_expenses where profileEntity_Id = :profileId and date between ?startDate and ?endDate and Lower(name) like lower(%?4%)
        Lấy ra tổng chi tiêu dựa vào Id và tìm khiếm trong khoảng [startDate,endDate] và có name nằm ở bất kỳ đâu trong chuỗi
     */
    List<ExpenseEntity> findByProfileEntity_IdAndDateBetweenAndNameContainingIgnoreCase(
        Long profileId,
        LocalDate startDate,
        LocalDate endDate,
        String keyword,
        Sort sort
    );


    /*
    Tìm kiếm danh sách chi tiêu dựa vào profileId và tìm kiếm trong khoảng [startDate,endDate]
    select * from tbl_expense where profileEntity_Id = ?1 and date between ?2 and ?3
     */
    List<ExpenseEntity> findByProfileEntity_IdAndDateBetween(Long profileId, LocalDate startDate, LocalDate endDate);

    //select * from tbl_expense where profileEntity_Id = ?1 and date ?2
    List<ExpenseEntity> findByProfileEntity_IdAndDate(Long profileId,LocalDate date);
}
