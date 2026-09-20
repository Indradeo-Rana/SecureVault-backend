package in.infosys.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

@Service
public class EncryptionService {

        private static final String ALGORITHM = "AES/GCM/NoPadding";
        private static final int IV_LENGTH = 12;
        private static final int TAG_LENGTH = 128;

        private final byte[] secretKey;
        private final SecureRandom secureRandom = new SecureRandom();

        public EncryptionService(@Value("${encryption.secret}") String secret) {
            this.secretKey = Base64.getDecoder().decode(secret);

            if (secretKey.length != 32) {
                throw new IllegalArgumentException(
                        "Encryption key must be exactly 32 bytes"
                );
            }
        }

        public String encrypt(String plainText) {

            try {
                // 1. Generate a new random IV
                byte[] iv = new byte[IV_LENGTH];
                secureRandom.nextBytes(iv);

                // 2. Create AES key
                SecretKeySpec key = new SecretKeySpec(secretKey, "AES");

                // 3. Create cipher
                Cipher cipher = Cipher.getInstance(ALGORITHM);

                // 4. Configure GCM
                GCMParameterSpec gcmSpec =
                        new GCMParameterSpec(TAG_LENGTH, iv);

                cipher.init(Cipher.ENCRYPT_MODE, key, gcmSpec);

                // 5. Encrypt password
                byte[] encryptedBytes =
                        cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

                // 6. Store IV + encrypted data together
                byte[] combined = new byte[iv.length + encryptedBytes.length];

                System.arraycopy(
                        iv,
                        0,
                        combined,
                        0,
                        iv.length
                );

                System.arraycopy(
                        encryptedBytes,
                        0,
                        combined,
                        iv.length,
                        encryptedBytes.length
                );

                // 7. Convert to Base64
                return Base64.getEncoder().encodeToString(combined);

            } catch (Exception e) {
                throw new RuntimeException("Failed to encrypt password", e);
            }
        }

        public String decrypt(String encryptedText) {

            try {
                // 1. Decode Base64
                byte[] combined =
                        Base64.getDecoder().decode(encryptedText);

                // 2. Extract IV
                byte[] iv = new byte[IV_LENGTH];

                System.arraycopy(
                        combined,
                        0,
                        iv,
                        0,
                        IV_LENGTH
                );

                // 3. Extract encrypted data
                byte[] encryptedBytes =
                        new byte[combined.length - IV_LENGTH];

                System.arraycopy(
                        combined,
                        IV_LENGTH,
                        encryptedBytes,
                        0,
                        encryptedBytes.length
                );

                // 4. Create AES key
                SecretKeySpec key =
                        new SecretKeySpec(secretKey, "AES");

                // 5. Create cipher
                Cipher cipher =
                        Cipher.getInstance(ALGORITHM);

                // 6. Configure GCM
                GCMParameterSpec gcmSpec =
                        new GCMParameterSpec(TAG_LENGTH, iv);

                cipher.init(
                        Cipher.DECRYPT_MODE,
                        key,
                        gcmSpec
                );

                // 7. Decrypt
                byte[] decryptedBytes =
                        cipher.doFinal(encryptedBytes);

                return new String(
                        decryptedBytes,
                        StandardCharsets.UTF_8
                );

            } catch (Exception e) {
                throw new RuntimeException("Failed to decrypt password", e);
            }
        }
    }