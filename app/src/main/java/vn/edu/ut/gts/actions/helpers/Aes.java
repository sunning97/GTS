package vn.edu.ut.gts.actions.helpers;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
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

public class Aes {
    private static final String KEY = "e84ad660c4721ae0e84ad660c4721ae0";
    private static final String PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA1";
    private static final String AES_ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final byte[] SALT_BYTES = ("CryptographyPMT-EMS").getBytes();
    private static final int KEY_LENGTH = 128;
    private static final int ITERATIONS = 1000;

    private static byte[] HASHED = null;

    private static SecretKey generateKey(String hash) {
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance(PBKDF2_ALGORITHM);
            KeySpec spec = new PBEKeySpec(hash.toCharArray(), SALT_BYTES, ITERATIONS, KEY_LENGTH);
            return new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
        }catch(Exception e) {}
        return null;
    }
    private static byte[] hexToBytes(String str) {
        try {
            return Hex.decodeHex(str.toCharArray());
        } catch (DecoderException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
    public static Aes encrypt(String privateKey, String password) {
        try {
            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, generateKey(privateKey), new IvParameterSpec(hexToBytes(KEY)));
            HASHED = cipher.doFinal(password.getBytes());
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return new Aes();
    }
    public String toBase64() {
        return new String(Base64.encodeBase64(HASHED));
    }
}
