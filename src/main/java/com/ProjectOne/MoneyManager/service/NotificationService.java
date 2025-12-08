package com.ProjectOne.MoneyManager.service;


import com.ProjectOne.MoneyManager.dto.ExpenseDTO;
import com.ProjectOne.MoneyManager.entity.ProfileEntity;
import com.ProjectOne.MoneyManager.repository.ProfileRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.analysis.function.Exp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationService {

    @Autowired
    ProfileRepository profileRepository;

    @Autowired
    EmailService emailService;

    @Autowired
    ExpenseService expenseService;

    @Value("${money.manager.frontend.url}")
    String frontEndUrl;

    //Gửi thông báo tổng chi thu hàng ngày
    @Scheduled(cron = "0 0 11 * * *", zone = "Asia/Ho_Chi_Minh")
    public void sendDailyIncomeExpenseReminder() {
        log.info("Job started: Send Daily Income Expense Reminder");
        List<ProfileEntity> profiles = profileRepository.findAll();
        for (ProfileEntity profile : profiles) {
//            log.info(profile.getEmail());
            String body = "Hi " + profile.getFullName() + ", <br><br>"
                    + "This is a friendly reminder to add your incomes and expenses for today in Money Manager. <br><br>"
                    + "<a href=\"" + frontEndUrl + "\" style=\"display:inline-block;padding:10px 20px;background-color:#4CAF50;color:#fff;text-decoration:none;border-radius:5px;font-weight:bold;\">Go to Money Manager</a>"
                    + "<br><br>Best regards,<br>Money Manager Team";
            emailService.sendEmail(profile.getEmail(),"Daily reminder: Add your income and expenses",body);
            log.info("Job completed: Send daily income expense reminder");
        }
    }

    //gửi bảng tóm tắt chi tiêu hàng ngày
    @Scheduled(cron = "0 57 11 * * *", zone = "Asia/Ho_Chi_Minh")
    public void sendDailyExpenseSummary() {

        log.info("Job started: Send Daily Expense Summary");

        try {
            List<ProfileEntity> profiles = profileRepository.findAll();
            for (ProfileEntity profile : profiles) {
                log.info("Processing: " + profile.getEmail());
                try {
                    List<ExpenseDTO> todayExpenses =
                            expenseService.getExpensesForUserOnDate(profile.getId(), LocalDate.now());

                    if (!todayExpenses.isEmpty()) {
                        StringBuilder table = new StringBuilder();
                        table.append("<table style='border-collapse:collapse;width:100%;'>");
                        table.append("<tr style='background-color:#f2f2f2;'>"
                                + "<th style='border:1px solid #ddd;padding:8px;'>#</th>"
                                + "<th style='border:1px solid #ddd;padding:8px;'>Name</th>"
                                + "<th style='border:1px solid #ddd;padding:8px;'>Amount</th>"
                                + "<th style='border:1px solid #ddd;padding:8px;'>Category</th>"
                                + "</tr>");

                        int i = 1;
                        for (ExpenseDTO expenseDTO : todayExpenses) {
                            table.append("<tr>");
                            table.append("<td style='border:1px solid #ddd;padding:8px;'>").append(i++).append("</td>");
                            table.append("<td style='border:1px solid #ddd;padding:8px;'>").append(expenseDTO.getName()).append("</td>");
                            table.append("<td style='border:1px solid #ddd;padding:8px;'>").append(expenseDTO.getAmount()).append("</td>");
                            table.append("<td style='border:1px solid #ddd;padding:8px;'>")
                                    .append(expenseDTO.getCategoryId() != null ? expenseDTO.getCategoryName() : "N/A")
                                    .append("</td>");
                            table.append("</tr>");
                        }
                        table.append("</table>");

                        String body = "Hello " + profile.getFullName()
                                + " ,<br/><br/>Here is a summary of your expenses for today:<br/><br/>"
                                + table
                                + "<br/><br/>Best regards,<br/>To Money Manager Team";

                        emailService.sendEmail(
                                profile.getEmail(),
                                "Your daily Expense summary",
                                body
                        );

                    } else {
                        log.info("No expenses today for: " + profile.getEmail());
                    }

                } catch (Exception ex) {
                    log.error("Error while processing profile id=" + profile.getId(), ex);
                }
            }

        } catch (Exception e) {
            log.error("Unexpected error in scheduler", e);
        }

        log.info("Job completed: Send daily expense summary");
    }

}
