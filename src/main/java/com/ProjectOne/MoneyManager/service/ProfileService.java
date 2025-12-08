package com.ProjectOne.MoneyManager.service;

import com.ProjectOne.MoneyManager.dto.AuthDTO;
import com.ProjectOne.MoneyManager.dto.ProfileDTO;
import com.ProjectOne.MoneyManager.entity.ProfileEntity;
import com.ProjectOne.MoneyManager.repository.ProfileRepository;
import com.ProjectOne.MoneyManager.util.JwtUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProfileService {

    @Autowired
    ProfileRepository profileRepository;

    @Autowired
    EmailService emailService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    final AuthenticationManager authenticationManager;

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    AppUserDetailsService appUserDetailsService;

    @Value("${app.activation.url}")
    String activationURl;


    //Người dùng đăng ký tài khoản
    public ProfileDTO registerProfile(ProfileDTO profileDTO){
        ProfileEntity newProfileEntity = toEntity(profileDTO);
        newProfileEntity.setActivationToken(UUID.randomUUID().toString());
        newProfileEntity.setPassword(newProfileEntity.getPassword());
        profileRepository.save(newProfileEntity);

        //Gửi email kích hoạt
        String activationLink = activationURl + "api/v1.0/activate?token=" + newProfileEntity.getActivationToken();
        String subject = "Kích hoạt tài khoản email của bạn";
        String body = "Nhấp vào liên kết sau để kích hoạt của bạn: " + activationLink;
        emailService.sendEmail(newProfileEntity.getEmail(), subject, body);

        return toDTO(newProfileEntity);
    }

    //Lưu thông tin user vào trong database
    public ProfileEntity toEntity(ProfileDTO profileDTO) {
        return ProfileEntity.builder()
                .id(profileDTO.getId())
                .fullName(profileDTO.getFullName())
                .email(profileDTO.getEmail())
                .password(passwordEncoder.encode(profileDTO.getPassword()))
                .createdAt(profileDTO.getCreatedAt())
                .updatedAt(profileDTO.getUpdatedAt())
                .build();
    }

    //Trả dữ liệu về cho client
    public ProfileDTO toDTO(ProfileEntity profileEntity){
        return ProfileDTO.builder()
                .id(profileEntity.getId())
                .fullName(profileEntity.getFullName())
                .email(profileEntity.getEmail())
                .profileImageUrl(profileEntity.getProfileImageUrl())
                .createdAt(profileEntity.getCreatedAt())
                .updatedAt(profileEntity.getUpdatedAt())
                .build();
    }

    //Kích hoạt tài khoản khi người dùng bấm vào link kích hoạt trong email
    public boolean activateProfile(String activationToken){
        return profileRepository.findByActivationToken(activationToken)
                .map(profileEntity -> {
                    profileEntity.setIsActive(true);
                    profileRepository.save(profileEntity);
                    return true;
                })
                .orElse(false);
    }

    //kiểm tra xem tài khoản có hoạt động không
    public boolean isAccountActive(String email){
        return profileRepository.findByEmail(email)
                .map(ProfileEntity::getIsActive)
                .orElse(false);
    }

    //Lấy về hồ sơ người dùng đăng nhập hiện tại
    public ProfileEntity getCurrentProfile(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();//Lấy thông tin người dùng đang đăng nhập
        return profileRepository.findByEmail(authentication.getName())//Lấy về email
                .orElseThrow(() -> new UsernameNotFoundException("Hồ sơ không tìm thấy với email: " + authentication.getName()));
    }

    //1:45:17
    public ProfileDTO getPublicProfile(String email){
        ProfileEntity profileEntity = null;
        if(email == null){
            profileEntity = getCurrentProfile();
        }
        else{
            profileEntity = profileRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("Hồ sơ không tìm thấy với email: " + email));
        }
        return ProfileDTO.builder()
                .id(profileEntity.getId())
                .fullName(profileEntity.getFullName())
                .email(profileEntity.getEmail())
                .profileImageUrl(profileEntity.getProfileImageUrl())
                .createdAt(profileEntity.getCreatedAt())
                .updatedAt(profileEntity.getUpdatedAt())
                .build();
    }

    //Nếu đã xác thực rồi thì sẽ gen ra một cái token
    public Map<String,Object> authenticateAndGenerateToken(AuthDTO authDTO){
        try{
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authDTO.getEmail(),authDTO.getPassword()));
            UserDetails userDetails = appUserDetailsService.loadUserByUsername(authDTO.getEmail());
            String token = jwtUtil.generateToken(userDetails);
            return Map.of(
                    "token",token,
                    "user",getPublicProfile(authDTO.getEmail())
            );
        }
        catch (Exception e){
            throw  new RuntimeException("Email hoặc mật khẩu không hợp lệ");
        }
    }

}
