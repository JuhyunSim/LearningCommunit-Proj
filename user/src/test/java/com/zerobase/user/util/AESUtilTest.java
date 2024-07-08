package com.zerobase.user.util;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.security.SecureRandom;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class AESUtilTest {

    @Autowired
    private AESUtil aesUtil;

    public static String generateRandomKey() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] key = new byte[16];
        secureRandom.nextBytes(key);
        return Base64.getEncoder().encodeToString(key);
    }

    public static void main(String[] args) {
        System.out.println(generateRandomKey());
    }

    @Test
    void encryptTest() throws Exception {
        //given
        String password = "123456";
        String encrypted = aesUtil.encrypt(password);

        //when
        String decrypted = aesUtil.decrypt(encrypted);

        //then
        assertEquals("123456", decrypted);
    }

}