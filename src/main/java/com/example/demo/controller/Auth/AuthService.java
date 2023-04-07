package com.example.demo.controller.Auth;

import com.example.demo.config.JwtService;
import com.example.demo.config.SecurityConfig;
import com.example.demo.model.Token;
import com.example.demo.model.User;
import com.example.demo.model.repository.TokenRepository;
import com.example.demo.model.repository.UserRepository;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.authentication.AuthenticationManager;
        import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
        import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository repository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Autowired
    private MongoTemplate mongoTemplate;

    public AuthenticationResponse register(RegisterRequest request) {
        var user = User.builder()
                .firstName(request.getFirstname())
                .lastName(request.getLastname())
                .email(request.getEmail())
                .isActive(true)
                .password(passwordEncoder.encode(request.getPassword()))
                .type(User.Type.ROLE_DEV)
                .build();
        var savedUser = repository.save(user);
        var jwtToken = jwtService.generateToken(user);
        saveUserToken(savedUser, jwtToken);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        var user = repository.findByEmail(request.getEmail())
                .orElseThrow();
        try {

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

    }catch (AuthenticationException e){
            if (user.getFailedAttempts() >= SecurityConfig.MAX_FAILED_ATTEMPTS){
                user.setActive(false);
            }else {
                user.setFailedAttempts(user.getFailedAttempts()+1);
            }
            repository.save(user);
            return AuthenticationResponse.builder().build();
        }


        var jwtToken = jwtService.generateToken(user);
        if (!user.isActive())
            jwtToken = null;
        else {
            user.setFailedAttempts(0);
            repository.save(user);
            revokeAllUserTokens(user);
            saveUserToken(user, jwtToken);
        }
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    private void revokeAllUserTokens(User user) {
        Query query = new Query();
        query.addCriteria(Criteria.where("user").is(user));
        var validUserTokens = mongoTemplate.find(query,Token.class);
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }
}
