package com.soco.SoCoClient.control.util;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Formatter;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import android.util.Log;

public class SignatureUtil {

    public static String tag = "SignatureUtil";

    private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";

    private static String toHexString(byte[] bytes) {
        Formatter formatter = new Formatter();
        for (byte b : bytes)
            formatter.format("%02x", b);
        return formatter.toString();
    }

    public static String genSHA1(String data, String key) {
        Log.d(tag, "Generate SHA1 signature of data: " + data + ", key: " + key);
        String s = null;
        try {
            SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), HMAC_SHA1_ALGORITHM);
            Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
            mac.init(signingKey);
            s = toHexString(mac.doFinal(data.getBytes()));
            Log.d(tag, "Signature generated: " + s);
        } catch (Exception e) {
            Log.e(tag, "Error generating SHA1 signature");
            e.printStackTrace();
        }
        return s;
    }

}