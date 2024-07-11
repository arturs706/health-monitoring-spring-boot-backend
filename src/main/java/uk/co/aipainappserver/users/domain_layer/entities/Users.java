package uk.co.aipainappserver.users.domain_layer.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class Users {

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

    @Enumerated(EnumType.STRING)
    @Column(name = "authmethod", nullable = false)
    private AuthMethodEnum authmethod = AuthMethodEnum.LOCAL;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime created_at;

    public Users() {
        // Default constructor
    }

    // Getters and Setters
    public UUID getUsid() {
        return usid;
    }

    public void setUsid(UUID usid) {
        this.usid = usid;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getMob_phone() {
        return mob_phone;
    }

    public void setMob_phone(String mob_phone) {
        this.mob_phone = mob_phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isEmail_ver() {
        return email_ver;
    }

    public void setEmail_ver(boolean email_ver) {
        this.email_ver = email_ver;
    }

    public String getEmail_ver_token() {
        return email_ver_token;
    }

    public void setEmail_ver_token(String email_ver_token) {
        this.email_ver_token = email_ver_token;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public AuthMethodEnum getAuthmethod() {
        return authmethod;
    }

    public void setAuthmethod(AuthMethodEnum authmethod) {
        this.authmethod = authmethod;
    }

    public LocalDateTime getCreated_at() {
        return created_at;
    }

    public void setCreated_at(LocalDateTime created_at) {
        this.created_at = created_at;
    }


}
