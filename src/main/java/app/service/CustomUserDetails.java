package app.service;
import app.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.stream.Collectors;

public class CustomUserDetails implements UserDetails {
    private final String email;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(User user) {
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    @Transactional
    public String getPassword() {
        return password;
    }

    @Override
    @Transactional
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Можно изменить по необходимости
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Можно изменить по необходимости
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Можно изменить по необходимости
    }

    @Override
    public boolean isEnabled() {
        return true; // Можно изменить по необходимости
    }
}
