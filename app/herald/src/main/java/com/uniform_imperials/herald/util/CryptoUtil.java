package com.uniform_imperials.herald.util;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.KeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by Sean Johnson on 4/25/2016.
 *
 * Utilities for cryptography.
 *
 * NOTE: Most (if not all) of this code was written around 4 AM on submission day. It will
 * eventually get refactored and be less abhorrent.
 */
public class CryptoUtil {

    public static class EncryptedPayload implements Serializable {
        /**
         * Encrypted payload; array of bytes.
         */
        private byte[] payload;

        /**
         * Initialization vector used to create the above payload.
         */
        private byte[] iv;

        public EncryptedPayload(byte[] ct, byte[] iv) {
            this.payload = ct;  // cipher text
            this.iv = iv;       // initialization vector
        }

        public byte[] getPayload() {
            return this.payload;
        }

        public byte[] getIv() {
            return this.iv;
        }

        public String decrypt(String password, byte[] salt) {
            if (password == null || salt == null) {
                return null;
            }
            char[] pw = password.toCharArray();

            return this.decrypt(pw, salt);
        }

        public String decrypt(char[] password, byte[] salt) {
            if (password == null || salt == null) {
                return null;
            }

            SecretKey key = getSecretKeySpec(password, salt);
            if (key == null) {
                return null;
            }

            return this.decrypt(key);
        }

        public String decrypt(SecretKey key) {
            if (key == null) {
                return null;
            }

            return aesDecryptData(key, this.getPayload(), this.getIv());
        }
    }

    public static final int DERIVATION_ITERATION_COUNT = 65536;
    public static final int KEY_SIZE = 128;

    public static byte[] generateSalt(int size) {
        SecureRandom sr = new SecureRandom();
        byte[] salt = new byte[size];
        sr.nextBytes(salt);

        return salt;
    }

    public static SecretKey getSecretKeySpec(char[] password, byte[] salt) {
        SecretKeyFactory skFactory;
        try {
            skFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        } catch (NoSuchAlgorithmException exc) {
            return null;
        }

        KeySpec spec = new PBEKeySpec(password, salt, DERIVATION_ITERATION_COUNT, KEY_SIZE);
        SecretKey tmp;
        try {
            tmp = skFactory.generateSecret(spec);
        } catch (InvalidKeySpecException exc) {
            return null;
        }

        return new SecretKeySpec(tmp.getEncoded(), "AES");
    }

    public static byte[] aesGenerateIV(char[] password, byte[] salt) {
        return aesGenerateIV(getSecretKeySpec(password, salt));
    }

    public static byte[] aesGenerateIV(SecretKey secret) {
        Cipher cipher;
        try {
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        } catch (NoSuchAlgorithmException exc) {
            // Shit what do?
            return null;
        } catch (NoSuchPaddingException exc) {
            // Shit what do....?
            return null;
        }

        try {
            cipher.init(Cipher.ENCRYPT_MODE, secret);
        } catch (InvalidKeyException exc) {
            // What the hell do we do if the key is invalid?
            return null;
        }

        AlgorithmParameters params = cipher.getParameters();

        byte[] iv;
        try {
            iv = params.getParameterSpec(IvParameterSpec.class).getIV();
        } catch (InvalidParameterSpecException exc) {
            // SHIIIIIIT
            return null;
        }

        return iv;
    }

    public static EncryptedPayload aesEncryptData(char[] password, byte[] salt, String data) {
        return aesEncryptData(getSecretKeySpec(password, salt), data);
    }

    public static EncryptedPayload aesEncryptData(SecretKey secret, String data) {
        Cipher cipher;
        try {
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        } catch (NoSuchAlgorithmException exc) {
            // Shit what do?
            exc.printStackTrace();
            return null;
        } catch (NoSuchPaddingException exc) {
            // Shit what do....?
            exc.printStackTrace();
            return null;
        }

        try {
            cipher.init(Cipher.ENCRYPT_MODE, secret);
        } catch (InvalidKeyException exc) {
            // What the hell do we do if the key is invalid?
            exc.printStackTrace();
            return null;
        }

        AlgorithmParameters params = cipher.getParameters();

        byte[] iv;
        try {
            iv = params.getParameterSpec(IvParameterSpec.class).getIV();
        } catch (InvalidParameterSpecException exc) {
            // SHIIIIIIT
            exc.printStackTrace();
            return null;
        }

        byte[] ciphertext;
        try {
            ciphertext = cipher.doFinal(data.getBytes(Charset.forName("UTF-8")));
        } catch (IllegalBlockSizeException exc) {
            // SHIIIIIIIIT BLOCK SIZES NOOO
            exc.printStackTrace();
            return null;
        } catch (BadPaddingException exc) {
            // HOW DOES THIS HAPPEN
            exc.printStackTrace();
            return null;
        }

        return new EncryptedPayload(ciphertext, iv);
    }

    public static String aesDecryptData(char[] password, byte[] salt, byte[] data, byte[] iv) {
        return aesDecryptData(getSecretKeySpec(password, salt), data, iv);
    }

    public static String aesDecryptData(SecretKey secret, byte[] data, byte[] iv) {
        Cipher cipher;
        try {
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        } catch (NoSuchAlgorithmException exc) {
            return null;
        } catch (NoSuchPaddingException exc) {
            return null;
        }

        try {
            cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(iv));
        } catch (InvalidKeyException exc) {
            return null;
        } catch (InvalidAlgorithmParameterException exc) {
            return null;
        }

        String plaintext;
        try {
            plaintext = new String(cipher.doFinal(data), "UTF-8");
        } catch (IllegalBlockSizeException exc) {
            return null;
        } catch (BadPaddingException exc) {
            return null;
        } catch (UnsupportedEncodingException exc) {
            return null;
        }

        return plaintext;
    }
}
