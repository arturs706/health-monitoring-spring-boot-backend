package uk.co.aipainappserver.users.infrastructure_layer.ro;

import uk.co.aipainappserver.users.domain_layer.entities.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface UserRORepository extends JpaRepository<Users, UUID> {

    List<Users> findAll();
    Users getById(UUID userId);
    Users findByEmailAndPasswd(String email, String passwd);

}