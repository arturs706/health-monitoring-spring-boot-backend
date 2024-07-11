package uk.co.aipainappserver.users.domain_layer.entities;


import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class UserDto {
    private UUID usid;
    private String fullname;
    private String email;
    private LocalDateTime created_at;
}
