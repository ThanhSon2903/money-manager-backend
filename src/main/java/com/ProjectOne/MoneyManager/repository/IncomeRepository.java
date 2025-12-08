package com.ProjectOne.MoneyManager.repository;

import com.ProjectOne.MoneyManager.entity.ExpenseEntity;
import com.ProjectOne.MoneyManager.entity.IncomeEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface IncomeRepository extends JpaRepository<IncomeEntity,Long> {
    /*
        select * from tbl_incomes where ProfileEntity_Id = ?1 order by date desc
        lấy ra danh sách tất cả các khoản thu nhap dựa vào Id và sắp xếp giảm dần theo ngày
     */
    List<IncomeEntity> findByProfileEntity_IdOrderByDateDesc(Long ProfileEntity_Id);


    List<IncomeEntity> findTop5ByProfileEntity_IdOrderByDateDesc(Long ProfileEntity_Id);


    /*
       Lấy ra tổng thu dựa vào id người dùng
    */
    @Query("SELECT SUM(i.amount) FROM IncomeEntity i WHERE i.profileEntity.id = :profileId")    /*
        select * from tbl_incomes where ProfileEntity_Id = ?1 order by date desc limit 5
        lấy ra danh sách top 5 khoản thu dựa vào Id và sắp xếp giảm dần theo ngày
     */

    BigDecimal findTotalIncomeByProfileEntity_Id(@Param("profileId")Long profileId);


    /*
    name like concat(?4,'%'),('%',?4,'%'),('%',?4)
        select * from tbl_incomes where profileEntity_Id = ?1 and date between ?2 and ?3 and name like %?4%
        Lấy ra tổng thu dựa vào Id và tìm khiếm trong khoảng [startDate,endDate] và có name nằm ở bất kỳ đâu trong chuỗi
     */
    List<IncomeEntity> findByProfileEntity_IdAndDateBetweenAndNameContainingIgnoreCase(
            Long profileId,
            LocalDate startDate,
            LocalDate endDate,
            String keyword,
            Sort sort
    );


    /*
    Tìm kiếm danh sách chi tiêu dựa vào profileId và tìm kiếm trong khoảng [startDate,endDate]
    select * from tbl_incomes where profileEntity_Id = ?1 and date between ?2 and ?3
     */
    List<IncomeEntity> findByProfileEntity_IdAndDateBetween(Long profileId, LocalDate startDate, LocalDate endDate);
}
