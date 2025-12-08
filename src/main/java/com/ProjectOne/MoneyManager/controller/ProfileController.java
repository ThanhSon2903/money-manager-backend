package com.ProjectOne.MoneyManager.controller;

import com.ProjectOne.MoneyManager.dto.AuthDTO;
import com.ProjectOne.MoneyManager.dto.ProfileDTO;
import com.ProjectOne.MoneyManager.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ProfileController {

    @Autowired
    ProfileService profileService;

    @PostMapping("/register")
    public ResponseEntity<ProfileDTO> registerProfile(@RequestBody ProfileDTO profileDTO){
        ProfileDTO registeredProfile = profileService.registerProfile(profileDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(registeredProfile);
    }

    @GetMapping("/activate")
    public ResponseEntity<String> activateProfile(@RequestParam String token){
        boolean isActivated = profileService.activateProfile(token);
        if(isActivated) return ResponseEntity.ok("Tài khoản đã kích hoạt thành công");
        else return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy mã thông báo kích hoạt hoặc đã được sử dụng");
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String,Object>> login(@RequestBody AuthDTO authDTO){
        try{
            //Kiểm tra xem email này đã được kích hoạt hay chưa
            if(!profileService.isAccountActive(authDTO.getEmail())){
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                        "Thông báo","Tài khoản chưa được kích hoạt, Vui lòng kích hoạt tài khoản của bạn!"
                ));
            }
            Map<String,Object> response = profileService.authenticateAndGenerateToken(authDTO);
            return ResponseEntity.ok(response);
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "Thông báo",e.getMessage()
            ));
        }
    }

    @GetMapping("/test")
    public String test(){
        return "ok";
    }
}
