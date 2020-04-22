package uk.co.kring.android.dcs;

import java.security.*;

public class CallSign {

    public static KeyPair getKeys() throws NoSuchAlgorithmException {
        // Generate a 1024-bit
        KeyPairGenerator keyGenerator = KeyPairGenerator.getInstance("DH");
        keyGenerator.initialize(1024);
        return keyGenerator.genKeyPair();
    }

    public static byte[] getDigest(byte[] msg) throws NoSuchAlgorithmException {
        return MessageDigest.getInstance("SHA-256").digest(msg);
    }
}
