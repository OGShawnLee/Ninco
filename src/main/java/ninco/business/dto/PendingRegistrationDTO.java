package ninco.business.dto;

import ninco.business.enumeration.Role;
import java.time.LocalDateTime;

public class PendingRegistrationDTO {
    private int id;
    private final String email;
    private final String pin;
    private final LocalDateTime expiresAt;
    private LocalDateTime createdAt;
    private final String password;
    private final Role role;

    public PendingRegistrationDTO(int id, String email, String pin, LocalDateTime expiresAt, LocalDateTime createdAt, String password, Role role) {
        this.id = id;
        this.email = email;
        this.pin = pin;
        this.expiresAt = expiresAt;
        this.createdAt = createdAt;
        this.password = password;
        this.role = role;
    }

    public PendingRegistrationDTO(String email, String pin, LocalDateTime expiresAt, String hashedPassword, Role role) {
        this.email = email;
        this.pin = pin;
        this.expiresAt = expiresAt;
        this.password = hashedPassword;
        this.role = role;
    }

    public int getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPin() {
        return pin;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getPassword() {
        return password;
    }

    public Role getRole() {
        return role;
    }
}