package com.uniform_imperials.herald;

import com.uniform_imperials.herald.util.CryptoUtil;

import org.junit.Test;

import static com.uniform_imperials.herald.util.CryptoUtil.generateSalt;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Created by Sean Johnson on 4/26/2016.
 *
 * This is a set of unit tests for the crypto utils.
 */
public class CryptoUtilUnitTest {

    private static String dataString = "Data to encrypt :)";

    @Test
    public void aes256EncryptCycleTest() {
        char[] encryptPassphrase = "password.01!".toCharArray();

        byte[] salt = generateSalt(8);
        assertNotEquals(salt, null);

        CryptoUtil.EncryptedPayload p = CryptoUtil.aesEncryptData(encryptPassphrase, salt, dataString);
        assertNotEquals(null, p.getPayload());

        String decryptedData = p.decrypt(encryptPassphrase, salt);
        assertEquals(dataString, decryptedData);
    }

}
