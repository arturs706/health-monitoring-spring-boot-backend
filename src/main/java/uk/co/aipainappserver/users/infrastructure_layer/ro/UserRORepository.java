package uk.co.aipainappserver.users.infrastructure_layer.ro;

import uk.co.aipainappserver.users.domain_layer.entities.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface UserRORepository extends JpaRepository<Users, UUID> {
    Optional<Users> findByMobPhone(String mobPhone);
    Optional<Users> findByEmail(String email);
    Optional<Users> findById(UUID userId); // Assuming 'getById' is meant to find by ID
    Optional<Users> findByEmailAndPasswd(String email, String passwd);
}