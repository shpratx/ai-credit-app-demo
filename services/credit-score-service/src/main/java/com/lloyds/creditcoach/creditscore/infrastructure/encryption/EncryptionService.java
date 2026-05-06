package com.lloyds.creditcoach.creditscore.infrastructure.encryption;

import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
public class EncryptionService {

    // In production: use Google Cloud KMS for AES-256-GCM encryption/decryption

    public byte[] encrypt(String plaintext) {
        // Placeholder: Base64 encode (replace with Cloud KMS in production)
        return Base64.getEncoder().encode(plaintext.getBytes(StandardCharsets.UTF_8));
    }

    public String decrypt(byte[] ciphertext) {
        // Placeholder: Base64 decode (replace with Cloud KMS in production)
        return new String(Base64.getDecoder().decode(ciphertext), StandardCharsets.UTF_8);
    }
}
