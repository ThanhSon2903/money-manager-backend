package com.ProjectOne.MoneyManager.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtUtil {
    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 24 * 7; // 7 ngày
    private final SecretKey key;

    public JwtUtil() {
        String secretString = "843567893696976453275974432697R634976R738467TR678T34865R6834R8763T478378637664538745673865783678548735687R3";
        byte[] keyBytes = secretString.getBytes(StandardCharsets.UTF_8);
        this.key = new SecretKeySpec(keyBytes, "HmacSHA256");
    }

    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key)
                .compact();
    }

    //Lấy ra tên người dùng được lưu trong token
    public String extractUserName(String token){
        return extractClaims(token,Claims::getSubject);
    }

    //Function tổng quát để trích xuất bất kỳ thông tin nào (claim) từ token.
    private <T> T extractClaims(String token, Function<Claims,T> claimsTFunction){
        return claimsTFunction.apply(
                Jwts.parserBuilder()// tạo một bộ giải mã token.
                        .setSigningKey(key) //dùng khóa bí mật (key) để xác thực token
                        .build()
                        .parseClaimsJws(token)
                        .getBody()//nơi chứa dữ liệu người dùng
        );
    }

    //Kiểm tra xem token còn hạn không
    public boolean isTokenExpir(String token){
        return extractClaims(token,Claims::getExpiration).before(new Date());
    }
    public boolean isValidToken(String token,UserDetails userDetails){
        final String userName = extractUserName(token);
        return (userName.equals(userDetails.getUsername()) && !isTokenExpir(token));
    }
}