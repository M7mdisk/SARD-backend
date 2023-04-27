package com.example.demo.controller.Auth;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
        import org.springframework.web.bind.annotation.PostMapping;
        import org.springframework.web.bind.annotation.RequestBody;
        import org.springframework.web.bind.annotation.RequestMapping;
        import org.springframework.web.bind.annotation.RestController;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService service;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody RegisterRequest request
    ) {
        return ResponseEntity.ok(service.register(request));
    }
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request
    ) {
        try {
        AuthenticationResponse res = service.authenticate(request);
            if (res.getToken() == null)
                return  new ResponseEntity(res,HttpStatus.UNAUTHORIZED);
            return ResponseEntity.ok(res);
        }catch (NoSuchElementException e){
            System.out.println(e);
            // return an unauthorized response with the message "no such user"
            return new ResponseEntity("No such user", HttpStatus.UNAUTHORIZED);
        }
    }


}
