package com.example.app.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 自定义密码编码器，支持多种密码格式
 * 支持的格式：
 * - scrypt:xxx... (scrypt加密)
 * - {bcrypt}xxx... (bcrypt加密，Spring Security标准格式)
 * - 默认使用bcrypt验证
 */
@Slf4j
@Component
public class MultiPasswordEncoder implements PasswordEncoder {

    private final BCryptPasswordEncoder bcryptEncoder = new BCryptPasswordEncoder();
    private final SCryptPasswordEncoder scryptEncoder = new SCryptPasswordEncoder(32768, 8, 1, 32, 64);

    @Override
    public String encode(CharSequence rawPassword) {
        return "{bcrypt}" + bcryptEncoder.encode(rawPassword);
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        if (encodedPassword == null || encodedPassword.isEmpty()) {
            return false;
        }

        // 处理 scrypt:xxx 格式
        if (encodedPassword.startsWith("scrypt:")) {
            log.debug("使用 scrypt 编码器验证密码");
            String scryptHash = encodedPassword.substring("scrypt:".length());
            return scryptEncoder.matches(rawPassword, scryptHash);
        }

        // 处理 {bcrypt}xxx 格式
        if (encodedPassword.startsWith("{bcrypt}")) {
            log.debug("使用 bcrypt 编码器验证密码");
            String bcryptHash = encodedPassword.substring("{bcrypt}".length());
            return bcryptEncoder.matches(rawPassword, bcryptHash);
        }

        // 处理纯 bcrypt 格式（无前缀）
        if (encodedPassword.startsWith("$2a$") || encodedPassword.startsWith("$2b$") || encodedPassword.startsWith("$2y$")) {
            log.debug("使用 bcrypt 编码器验证密码（无前缀）");
            return bcryptEncoder.matches(rawPassword, encodedPassword);
        }

        // 默认尝试 bcrypt
        log.debug("默认使用 bcrypt 编码器验证密码");
        return bcryptEncoder.matches(rawPassword, encodedPassword);
    }
}