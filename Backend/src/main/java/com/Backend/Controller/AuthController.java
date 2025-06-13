package com.Backend.Controller;

import com.Backend.dto.RegisterDTO;
import com.Backend.dto.UserDTO;
import com.Backend.entity.User;
import com.Backend.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.Backend.security.JwtUtil;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody RegisterDTO dto) throws Exception {
        if (userService.findByUsername(dto.getUsername()) != null) {
            return ResponseEntity.badRequest().body(Map.of("error", "El usuario ya existe"));
        }

        String secret = userService.generateNewTotpSecret();

        User user = User.builder()
                .username(dto.getUsername())
                .password(dto.getPassword())
                .email(dto.getEmail())
                .mfaEnabled(true)
                .mfaSecret(secret)
                .build();

        userService.save(user);
        String qrBase64 = userService.generateQrImage(dto.getUsername(), secret);

        return ResponseEntity.ok(Map.of(
                "secret", secret,
                "qr", qrBase64
        ));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserDTO userDTO, HttpSession session) {
        System.out.println("Recibido: " + userDTO);

        User user = userService.findByUsername(userDTO.getUsername());
        if (user == null || !user.getPassword().equals(userDTO.getPassword())) {
            return ResponseEntity.status(401).body("Credenciales inválidas");
        }

        session.setAttribute("user", user);

        if (user.isMfaEnabled()) {
            return ResponseEntity.ok("MFA_REQUIRED");
        }

        return ResponseEntity.ok("LOGIN_OK");
    }

    @PostMapping("/verify-mfa")
    public ResponseEntity<?> verifyMfa(@RequestBody UserDTO userDTO, HttpSession session) {
        User user = (User) session.getAttribute("user");

        if (user == null || !userService.verifyTotp(user.getMfaSecret(), userDTO.getTotpCode())) {
            return ResponseEntity.status(401).body("Código MFA inválido");
        }

        String jwt = jwtUtil.generateToken(user.getUsername());
        return ResponseEntity.ok().body(jwt);
    }

    @GetMapping("/api/protegido")
    public ResponseEntity<String> zonaSegura() {
        return ResponseEntity.ok("Acceso concedido con JWT 🎉");
    }
}
