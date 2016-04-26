package com.uniform_imperials.herald.util;

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
 */
public class CryptoUtil {

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

        SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");
        return secret;
    }

    public static byte[] aes256GenerateIV(char[] password, byte[] salt) {
        return aes256GenerateIV(getSecretKeySpec(password, salt));
    }

    public static byte[] aes256GenerateIV(SecretKey secret) {
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

    public static byte[] aes256EncryptData(char[] password, byte[] salt, String data) {
        return aes256EncryptData(getSecretKeySpec(password, salt), data);
    }

    public static byte[] aes256EncryptData(SecretKey secret, String data) {
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

        return ciphertext;
    }

    public static String aes256DecryptData(char[] password, byte[] salt, byte[] data) {
        return aes256DecryptData(getSecretKeySpec(password, salt), data);
    }

    public static String aes256DecryptData(SecretKey secret, byte[] data) {
        Cipher cipher;
        try {
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        } catch (NoSuchAlgorithmException exc) {
            return null;
        } catch (NoSuchPaddingException exc) {
            return null;
        }

        byte[] iv = aes256GenerateIV(secret);
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
