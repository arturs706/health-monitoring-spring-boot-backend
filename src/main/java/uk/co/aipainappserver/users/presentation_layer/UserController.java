package uk.co.aipainappserver.users.presentation_layer;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.userdetails.UserDetails;
import uk.co.aipainappserver.config.JWTService;
import uk.co.aipainappserver.users.application_layer.UserService;
import uk.co.aipainappserver.users.domain_layer.entities.AuthenticationResponse;
import uk.co.aipainappserver.users.domain_layer.entities.LoginRequest;
import uk.co.aipainappserver.users.domain_layer.entities.Users;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.co.aipainappserver.utils.CookieUtil;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.security.access.prepost.PreAuthorize;



@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;
    private final JWTService jwtService;

    public UserController(UserService userService, JWTService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }
    @GetMapping("/manager")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<String> managerEndpoint() {
        return ResponseEntity.ok("This is a manager endpoint");
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> adminEndpoint() {
        return ResponseEntity.ok("This is an admin endpoint");
    }


    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    public ResponseEntity<List<Users>> getAllUsers() {
        try {
            List<Users> users = userService.getAllUsers();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @GetMapping("/{user_id}")
    public ResponseEntity<Users> getUserById(@PathVariable UUID user_id) {
        try {
            Users user = userService.getUserById(user_id);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PostMapping()
    public ResponseEntity<Object> registerUser(@RequestBody Users user) {
        try {
            AuthenticationResponse registeredUser = userService.registerUser(user);
            return ResponseEntity.ok().body(registeredUser);
        } catch (RuntimeException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        try {
            AuthenticationResponse authResponse = userService.authenticateUser(loginRequest, response);
            return ResponseEntity.ok(authResponse);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("refresh_token")) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }

        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Refresh token is missing");
        }

        try {
            String userEmail = jwtService.extractEmailFromToken(refreshToken);
            UserDetails userDetails = userService.loadUserByUsername(userEmail);
            if (jwtService.validateToken(refreshToken, userDetails)) {
                String newAccessToken = jwtService.generateToken(userDetails);
                String newRefreshToken = jwtService.generateRefreshToken(userDetails);
                CookieUtil.addCookie(response, "access_token", newAccessToken, 24 * 60 * 60); // 24 hours
                CookieUtil.addCookie(response, "refresh_token", newRefreshToken, 7 * 24 * 60 * 60); // 7 days
                return ResponseEntity.ok(new AuthenticationResponse(newAccessToken, newRefreshToken));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error processing refresh token");
        }
    }
}