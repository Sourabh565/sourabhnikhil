package nikhai.com.Sourabh.service;

import nikhai.com.Sourabh.dto.LoginRequest;
import nikhai.com.Sourabh.dto.LoginResponse;
import nikhai.com.Sourabh.entity.User;
import nikhai.com.Sourabh.enums.Status;
import nikhai.com.Sourabh.repository.UserRepository;
import nikhai.com.Sourabh.security.JwtUtil;
import nikhai.com.Sourabh.security.UserDetailsImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }
    
    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        
        User user = userRepository.findByUsername(request.getUsername())
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        
        if (user.getStatus() != Status.ACTIVE) {
            throw new RuntimeException("User account is not active");
        }
        
        String token = jwtUtil.generateToken(user.getUsername(), user.getId(), user.getRole().name());
        
        return new LoginResponse(token, user.getUsername(), user.getRole().name(), user.getId());
    }
    
    public User createUser(String username, String password, nikhai.com.Sourabh.enums.Role role) {
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username already exists");
        }
        
        User user = new User();
        user.setUsername(username);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setRole(role);
        user.setStatus(Status.ACTIVE);
        
        return userRepository.save(user);
    }
}
