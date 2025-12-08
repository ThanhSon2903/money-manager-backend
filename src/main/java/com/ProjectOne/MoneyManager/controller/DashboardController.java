package com.ProjectOne.MoneyManager.controller;

import com.ProjectOne.MoneyManager.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    @Autowired
    DashboardService dashboardService;

    @GetMapping
    public ResponseEntity<Map<String,Object>> getDashboardData(){
        Map<String,Object> dashBoard = dashboardService.getDashboardData();
        return ResponseEntity.ok(dashBoard);
    }
}
