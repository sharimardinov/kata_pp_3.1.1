package app.service;

import app.model.User;
import app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Transactional
    public void save(User user) {
        userRepository.save(user);
    }

    @Transactional
    public void update(Long id, User user) {
        // Логика обновления пользователя
        User existingUser = userRepository.findById(id).orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        existingUser.setName(user.getName());
        existingUser.setSurname(user.getSurname());
        existingUser.setEmail(user.getEmail());
        existingUser.setPassword(user.getPassword()); // Учтите, что пароль должен быть закодирован
        existingUser.setAge(user.getAge());
        existingUser.setRoles(user.getRoles());
        userRepository.save(existingUser);
    }

    @Transactional
    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username);
        if (user == null) {
            throw new UsernameNotFoundException("Пользователь не найден");
        }

        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                authorities
        );
    }
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public boolean isAdmin(String email) {
        User user = findByEmail(email);
        return user != null && user.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ADMIN"));
    }

}
