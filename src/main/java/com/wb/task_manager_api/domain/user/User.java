package com.wb.task_manager_api.domain.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "tb_user")
@Getter
@Setter
@NoArgsConstructor
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Email será nosso login (único)
    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    // Mapeamento do Enum Role. Armazenaremos como String.
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    // Construtor para facilitar a criação de novos usuários
    public User(String email, String password, UserRole role) {
        this.email = email;
        this.password = password;
        this.role = role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if(this.role == UserRole.ADMIN) {
            // Se for ADMIN, ele tem a role de ADMIN e de USER
            return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"), new SimpleGrantedAuthority("ROLE_USER"));
        } else {
            // Se for USER, ele só tem a role de USER
            return List.of(new SimpleGrantedAuthority("ROLE_USER"));
        }
    }

    // No nosso caso, o username é o email
    @Override
    public String getUsername() {
        return email;
    }

    // Métodos para controle de conta (manteremos como true por simplicidade)
    @Override
    public boolean isAccountNonExpired() { return true; }
    @Override
    public boolean isAccountNonLocked() { return true; }
    @Override
    public boolean isCredentialsNonExpired() { return true; }
    @Override
    public boolean isEnabled() { return true; }
}
