package com.ProjectOne.MoneyManager.service;

import com.ProjectOne.MoneyManager.entity.ProfileEntity;
import com.ProjectOne.MoneyManager.repository.ProfileRepository;
import jdk.jshell.spi.ExecutionControl;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;


@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AppUserDetailsService implements UserDetailsService {

    @Autowired
    ProfileRepository profileRepository;


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        ProfileEntity profileEntity = profileRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Tên email chưa tồn tại trong hệ thống"));
        return User.builder()
                .username(profileEntity.getEmail())
                .password(profileEntity.getPassword())
                .authorities(Collections.emptyList())
                .build();
    }
}
