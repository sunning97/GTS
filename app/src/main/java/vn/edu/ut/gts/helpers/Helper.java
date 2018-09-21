package vn.edu.ut.gts.helpers;

import android.content.SharedPreferences;
import android.support.annotation.Nullable;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.Normalizer;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class Helper {
    public static String md5(String str) {
        String md5 = null;
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] thedigest = digest.digest(str.getBytes());
            md5 = new String(Hex.encodeHex(thedigest));
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return md5;

    }
    public static String base64Encode(String str) {
        byte[] base = Base64.decodeBase64(str);
        return new String(base);
    }

    public static String toSlug(String str){
        str = str.trim();
        String tmp = Normalizer.normalize(str, Normalizer.Form.NFD);
        Pattern p = Pattern.compile("\\p{InCOMBINING_DIACRITICAL_MARKS}+");
        return p.matcher(tmp).replaceAll("").toLowerCase().replace("đ","d").replaceAll("\\s","_");
    }
}
