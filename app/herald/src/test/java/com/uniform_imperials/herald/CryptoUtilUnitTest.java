package com.uniform_imperials.herald;

import org.junit.Test;

import static com.uniform_imperials.herald.util.CryptoUtil.aes256DecryptData;
import static com.uniform_imperials.herald.util.CryptoUtil.aes256EncryptData;
import static com.uniform_imperials.herald.util.CryptoUtil.generateSalt;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Created by Sean Johnson on 4/26/2016.
 */
public class CryptoUtilUnitTest {

    private static String dataString = "Data to encrypt :)";

    @Test
    public void aes256EncryptCycleTest() {
        char[] encryptPassphrase = new String("password.01!").toCharArray();

        byte[] salt = generateSalt(8);
        assertNotEquals(salt, null);

        byte[] encryptedData = aes256EncryptData(encryptPassphrase, salt, dataString);
        assertNotEquals(encryptedData, null);

        String decryptedData = aes256DecryptData(encryptPassphrase, salt, encryptedData);
        assertEquals(decryptedData, dataString);
    }

}
