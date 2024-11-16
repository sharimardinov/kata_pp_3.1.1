package app.service;

import app.model.Role;
import app.model.User;
import app.repository.RoleRepository;
import app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, RoleRepository roleRepository, @Lazy BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<User> findAll() {
        return userRepository.findAll();
    }

//    @Transactional(readOnly = true)
//    public User findById(Long id) {
//        return userRepository.findById(id).orElse(null);
//    }

    @Transactional(readOnly = true)
    public User findById(Long id) {
        try {
            System.out.println("Запрос в базу данных с ID: " + id);
            return userRepository.findById(id).orElse(null);
        } catch (Exception e) {
            System.err.println("Ошибка при запросе пользователя с ID: " + id);
            e.printStackTrace();
            return null;  // Можно возвращать null или обработать ошибку
        }
    }


    @Transactional
    public User save(User user) {
        // Проверяем, существует ли уже пользователь с таким email
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Пользователь с таким email уже существует");
        }

        // Проверка на наличие ролей
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            throw new RuntimeException("Роли не могут быть пустыми");
        }

        // Кодируем пароль перед сохранением
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Сохраняем пользователя
        userRepository.save(user);

        return user;
    }


    @Transactional
    public User update(Long id, User user) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        if (emailExists(user.getEmail(), id)) {
            throw new RuntimeException("Email уже существует");
        }

        existingUser.setName(user.getName());
        existingUser.setSurname(user.getSurname());
        existingUser.setEmail(user.getEmail());

        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(user.getPassword())); // Устанавливаем закодированный пароль
        }

        existingUser.setAge(user.getAge());
        existingUser.setRoles(user.getRoles());
        userRepository.save(existingUser);
        return existingUser;
    }

    @Transactional
    public boolean delete(Long id) {
        userRepository.deleteById(id);
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username);
        if (user == null) {
            throw new UsernameNotFoundException("Пользователь не найден");
        }

        return new CustomUserDetails(user);
    }

    @Transactional
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Transactional
    public boolean emailExists(String email, Long userId) {
        User existingUser = userRepository.findByEmail(email);
        return existingUser != null && !existingUser.getId().equals(userId);
    }

}
