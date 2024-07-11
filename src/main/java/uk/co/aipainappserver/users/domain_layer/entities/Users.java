package uk.co.aipainappserver.users.domain_layer.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.type.SqlTypes;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.hibernate.annotations.JdbcTypeCode;


@Setter
@Getter
@Entity
@NoArgsConstructor

public class Users implements UserDetails, CredentialsContainer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "usid", updatable = false, nullable = false)
    private UUID usid;

    @Column(name = "fullname", nullable = false)
    private String fullname;

    @Column(name = "dob")
    private String dob;

    @Column(name = "gender")
    private String gender;

    @Column(name = "mob_phone", unique = true)
    private String mob_phone;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "email_ver", nullable = false)
    private boolean email_ver = false;

    @Column(name = "email_ver_token")
    private String email_ver_token;

    @Column(name = "passwd")
    private String passwd;

    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "authmethod", nullable = false)
    private AuthMethodEnum authmethod = AuthMethodEnum.LOCAL;

    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "userrole", nullable = false)
    private UserRoleEnum userrole = UserRoleEnum.USER;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime created_at;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(() -> userrole.name());
    }

    @Override
    public String getPassword() {
        return passwd;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isEnabled() {
        return email_ver;
    }

    @Override
    public void eraseCredentials() {
        passwd = null;
    }
}
