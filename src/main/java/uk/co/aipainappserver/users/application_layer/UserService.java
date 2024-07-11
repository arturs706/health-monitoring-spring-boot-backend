package uk.co.aipainappserver.users.application_layer;
import org.springframework.security.crypto.password.PasswordEncoder;
import uk.co.aipainappserver.users.domain_layer.entities.Users;
import org.springframework.stereotype.Service;
import uk.co.aipainappserver.users.infrastructure_layer.ro.UserRORepository;
import uk.co.aipainappserver.users.infrastructure_layer.rw.UserRWRepository;
import uk.co.aipainappserver.utils.JwtUtil;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Service
public class UserService {
    private final UserRORepository userRORepository;
    private final UserRWRepository userRWRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserService(UserRORepository userRORepository, UserRWRepository userRWRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRORepository = userRORepository;
        this.userRWRepository = userRWRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public List<Users> getAllUsers() {
        return userRORepository.findAll();
    }

    public Users getUserById(UUID userId) {
        return userRORepository.getById(userId);
    }

    public Users registerUser(Users user) {
        user.setUsid(UUID.randomUUID());
        user.setCreated_at(LocalDateTime.now());
        user.setPasswd(passwordEncoder.encode(user.getPasswd()));
        String token = jwtUtil.generateToken(user.getEmail());
        user.setEmail_ver_token(token);
        return userRWRepository.save(user);
    }

    public Users loginUser(String username, String passwd) {
        return userRORepository.findByEmailAndPasswd(username, passwd);
    }

}
