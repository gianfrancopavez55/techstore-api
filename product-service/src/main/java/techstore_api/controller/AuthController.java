package techstore_api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import techstore_api.dto.LoginRequest;
import techstore_api.dto.LoginResponse;
import techstore_api.security.JwtService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtService jwtService;

    public AuthController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody LoginRequest request) {

        if (
                request.getUsername().equals("admin")
                &&
                request.getPassword().equals("1234")
        ) {

            String token =
                    jwtService.generateToken(
                            request.getUsername());

            return ResponseEntity.ok(
                    new LoginResponse(token)
            );
        }

        return ResponseEntity
                .badRequest()
                .body("Credenciales incorrectas");
    }
}