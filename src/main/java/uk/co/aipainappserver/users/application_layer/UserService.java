package uk.co.aipainappserver.users.application_layer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestBody;
import uk.co.aipainappserver.users.domain_layer.entities.AuthMethodEnum;
import uk.co.aipainappserver.users.domain_layer.entities.UserRoleEnum;
import uk.co.aipainappserver.users.domain_layer.entities.Users;
import org.springframework.stereotype.Service;
import uk.co.aipainappserver.users.infrastructure_layer.ro.UserRORepository;
import uk.co.aipainappserver.users.infrastructure_layer.rw.UserRWRepository;
import uk.co.aipainappserver.utils.JwtUtil;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
public class UserService implements UserDetailsService {
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

    public Users registerUser(@RequestBody Users user) {

        user.setUsid(UUID.randomUUID());
        user.setCreated_at(LocalDateTime.now());
        user.setPasswd(passwordEncoder.encode(user.getPasswd()));
        String token = jwtUtil.generateToken(user.getEmail());
        user.setEmail_ver_token(token);
        return userRWRepository.save(user);
    }

    public Users loginUser(String username, String passwd) {
        Optional<Users> userOptional = userRORepository.findByEmailAndPasswd(username, passwd);
        return userOptional.orElse(null);
    }

    public Users loadUserByUsername(String username) {
        Optional<Users> userOptional = userRORepository.findByEmail(username);
        return userOptional.orElse(null);
    }


}
