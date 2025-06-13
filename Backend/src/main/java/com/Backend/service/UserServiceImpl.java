package com.Backend.service;

import com.Backend.entity.User;
import com.Backend.repository.UserRepository;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;
    private final GoogleAuthenticator gAuth = new GoogleAuthenticator();

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public boolean verifyTotp(String secret, String code) {
        return gAuth.authorize(secret, Integer.parseInt(code));
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public String generateNewTotpSecret() {
        return gAuth.createCredentials().getKey();
    }

    @Override
    public String generateQrImage(String username, String secret) throws WriterException, IOException {
        String otpAuthURL = "otpauth://totp/MG-TI-Banco:" + username + "?secret=" + secret + "&issuer=MG-TI-Banco";

        BitMatrix matrix = new MultiFormatWriter().encode(otpAuthURL, BarcodeFormat.QR_CODE, 200, 200);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(matrix, "PNG", out);
        return Base64.getEncoder().encodeToString(out.toByteArray());
    }
}
