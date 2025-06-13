package com.Frontend.service;

import com.Frontend.dto.LoginRequestDTO;
import com.Frontend.dto.MfaRequestDTO;
import com.Frontend.dto.RegisterDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final HttpHeaders headers = new HttpHeaders();
    private String jwt;

    private final String BACKEND_URL = "http://localhost:8081";

    public String login(LoginRequestDTO request) {
        HttpEntity<LoginRequestDTO> entity = new HttpEntity<>(request);
        ResponseEntity<String> response = restTemplate.exchange(
                BACKEND_URL + "/api/auth/login",
                HttpMethod.POST,
                entity,
                String.class
        );

        // Guardar cookies (JSESSIONID)
        List<String> setCookie = response.getHeaders().get(HttpHeaders.SET_COOKIE);
        if (setCookie != null) {
            headers.put(HttpHeaders.COOKIE, setCookie);
        }

        return response.getBody();
    }

    public String verifyMfa(MfaRequestDTO request) {
        HttpEntity<MfaRequestDTO> entity = new HttpEntity<>(request, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    BACKEND_URL + "/api/auth/verify-mfa",
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            this.jwt = response.getBody();
            return "ok";

        } catch (HttpClientErrorException e) {
            return "fail";
        }
    }

    public Map<String, String> register(RegisterDTO dto) {
        HttpEntity<RegisterDTO> entity = new HttpEntity<>(dto);
        ResponseEntity<Map> response = restTemplate.postForEntity(
                "http://localhost:8081/api/auth/register", entity, Map.class);
        return response.getBody();
    }

    public String getJwt() {
        return jwt;
    }

}
