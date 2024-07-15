package uk.co.aipainappserver.users.application_layer;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import uk.co.aipainappserver.config.JWTService;
import uk.co.aipainappserver.users.domain_layer.entities.*;
import uk.co.aipainappserver.users.infrastructure_layer.ro.UserRORepository;
import uk.co.aipainappserver.users.infrastructure_layer.rw.UserRWRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.aipainappserver.utils.CookieUtil;


@Service
public class UserService {
    private final UserRORepository userRORepository;
    private final UserRWRepository userRWRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public UserService(UserRORepository userRORepository, UserRWRepository userRWRepository, PasswordEncoder passwordEncoder, JWTService jwtService, AuthenticationManager authenticationManager) {
        this.userRORepository = userRORepository;
        this.userRWRepository = userRWRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }


    public List<Users> getAllUsers() {
        return userRORepository.findAll();
    }

    public Users getUserById(UUID userId) {
        return userRORepository.getById(userId);
    }

    public AuthenticationResponse registerUser(@RequestBody Users user_register) {
        if (userRORepository.findByMobPhone(user_register.getMobPhone()).isPresent()) {
            throw new RuntimeException("Mobile phone already registered");
        }

        if (userRORepository.findByEmail(user_register.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }
        var token = jwtService.generateToken(user_register);
        var refreshToken = jwtService.generateRefreshToken(user_register);
        var encodedPassword = passwordEncoder.encode(user_register.getPasswd());
        var user = Users.builder()
                .usid(UUID.randomUUID())
                .fullname(user_register.getFullname())
                .dob(user_register.getDob())
                .gender(user_register.getGender())
                .mobPhone(user_register.getMobPhone())
                .email(user_register.getEmail())
                .email_ver(false)
                .email_ver_token(token)
                .passwd(encodedPassword)
                .authmethod(AuthMethodEnum.LOCAL)
                .userrole(UserRoleEnum.USER)
                .created_at(LocalDateTime.now())
                .build();
        userRWRepository.save(user);
        return AuthenticationResponse
                .builder()
                .accessToken(token)
                .refreshToken(refreshToken)
                .build();
    }

    public AuthenticationResponse authenticateUser(LoginRequest request, HttpServletResponse response) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPasswd()
                    )
            );
        } catch (AuthenticationException e) {
            throw new RuntimeException("Invalid email or password");
        }
        var user = userRORepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    return new RuntimeException("User not found");
                });

        String jwtToken;
        String refreshToken;
        try {
            jwtToken = jwtService.generateToken(user);
            refreshToken = jwtService.generateRefreshToken(user);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate authentication tokens");
        }
        // Save tokens in cookies
        CookieUtil.addCookie(response, "access_token", jwtToken, 24 * 60 * 60); // 24 hours
        CookieUtil.addCookie(response, "refresh_token", refreshToken, 7 * 24 * 60 * 60); // 7 days

        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }


    public Users loadUserByUsername(String email) {
        return userRORepository.findByEmail(email)
                .orElseThrow(() -> {
                    return new RuntimeException("User not found");
                });
    }
}


