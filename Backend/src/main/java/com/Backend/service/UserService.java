package com.Backend.service;

import com.Backend.entity.User;
import com.google.zxing.WriterException;

import java.io.IOException;

public interface UserService {

    User findByUsername(String username);
    boolean verifyTotp(String secret, String code);
    User save(User user);
    String generateNewTotpSecret();
    String generateQrImage(String username, String secret) throws WriterException, IOException;

}
