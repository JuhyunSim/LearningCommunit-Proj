package com.zerobase.user.util;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

class JwtUtilTest {

    public static void main(String[] args) {
        String token = "eyJhbGciOiJIUzI1NiJ9.eyJhdXRob3JpdGllcyI6WyJST0xFX1VTRVIiXSwic3ViIjoidGVzdFVzZXIiLCJpYXQiOjE3MjA1MTUzMDYsImV4cCI6MTcyMDUxNjIwNn0.DD-XiP3GN4b_dd1jWa7-VNf-l77bsecjEVtr8iVCbZc";
        try {
            String[] parts = token.split("\\.");
            if (parts.length == 3) {
                Base64.Decoder decoder = Base64.getUrlDecoder();
                String header = new String(decoder.decode(parts[0]), StandardCharsets.UTF_8);
                String payload = new String(decoder.decode(parts[1]), StandardCharsets.UTF_8);
                String signature = parts[2];

                System.out.println("Header: " + header);
                System.out.println("Payload: " + payload);
                System.out.println("signature: " + signature);
            } else {
                System.out.println("Invalid JWT token format.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}