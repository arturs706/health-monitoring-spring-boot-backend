package uk.co.aipainappserver.users.infrastructure_layer.rw;

import uk.co.aipainappserver.users.domain_layer.entities.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface UserRWRepository extends JpaRepository<Users, UUID> {

    Users save(Users user);
    Users findByEmail(String email);

}