package uk.co.kring.android.dcs;

import android.util.Base64;

import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class CallSign {

    public static KeyPair getKeys() throws NoSuchAlgorithmException {
        // Generate a 1024-bit
        KeyPairGenerator keyGenerator = KeyPairGenerator.getInstance("DSA");
        keyGenerator.initialize(1024);
        return keyGenerator.genKeyPair();
    }

    public static byte[] getDigest(byte[] msg) throws NoSuchAlgorithmException {
        return MessageDigest.getInstance("SHA-256").digest(msg);
    }

    public static PublicKey pubKey(byte[] input) throws NoSuchAlgorithmException,
            InvalidKeySpecException {
        byte[] publicBytes = Base64.decode(input, Base64.NO_WRAP);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("DSA");
        return keyFactory.generatePublic(keySpec);
    }

    public static PrivateKey priKey(byte[] input) throws NoSuchAlgorithmException,
            InvalidKeySpecException {
        byte[] privateBytes = Base64.decode(input, Base64.NO_WRAP);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("DSA");
        return keyFactory.generatePrivate(keySpec);
    }

    public static byte[] pubKey(PublicKey input) {
        return Base64.decode(input.getEncoded(), Base64.NO_WRAP);
    }

    public static byte[] priKey(PrivateKey input) {
        return Base64.decode(input.getEncoded(), Base64.NO_WRAP);
    }

    public static byte[] sign(byte[] input, PrivateKey key) throws NoSuchAlgorithmException,
            InvalidKeyException, SignatureException {
        Signature s = Signature.getInstance("DSA");
        s.initSign(key);
        s.update(input);
        return s.sign();
    }

    public static boolean verify(byte[] input, PublicKey key) throws NoSuchAlgorithmException,
            InvalidKeyException, SignatureException {
        Signature s = Signature.getInstance("DSA");
        s.initVerify(key);
        return s.verify(input);
    }
}
