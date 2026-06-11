
package com.example.app.service;

import com.example.app.dto.request.LoginRequest;
import com.example.app.dto.request.RegisterRequest;
import com.example.app.dto.response.AuthResponse;
import com.example.app.dto.response.UserResponse;
import com.example.app.dto.response.UserSimpleResponse;
import com.example.app.entity.User;
import com.example.app.exception.BusinessException;
import com.example.app.repository.UserRepository;
import com.example.app.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RedisService redisService;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException("用户名已存在");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("邮箱已存在");
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .phone(request.getPhone())
                .role("user")
                .build();

        user = userRepository.save(user);
        
        String token = jwtUtil.generateToken(user.getUsername());
        redisService.setWithExpire("token:" + user.getUsername(), token, 86400);

        return AuthResponse.builder()
                .accessToken(token)
                .user(toUserResponse(user))
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BusinessException("用户名或密码错误"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        String token = jwtUtil.generateToken(user.getUsername());
        redisService.setWithExpire("token:" + user.getUsername(), token, 86400);

        return AuthResponse.builder()
                .accessToken(token)
                .user(toUserResponse(user))
                .build();
    }

    public void logout(String username) {
        redisService.delete("token:" + username);
    }

    public UserResponse getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("用户不存在"));
        return toUserResponse(user);
    }

    public UserSimpleResponse getUserSimple(Long userId) {
        return userRepository.findById(userId)
                .map(user -> UserSimpleResponse.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .avatarUrl(cleanImageUrl(user.getAvatarUrl()))
                        .build())
                .orElse(null);
    }

    private UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .phone(user.getPhone())
                .avatar(cleanImageUrl(user.getAvatarUrl()))
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .build();
    }
    
    private String cleanImageUrl(String url) {
        if (url == null) {
            return null;
        }
        return url.replace("http://localhost:5000", "")
                  .replace("http://localhost:8080", "")
                  .replace("https://localhost:5000", "")
                  .replace("https://localhost:8080", "");
    }
}
