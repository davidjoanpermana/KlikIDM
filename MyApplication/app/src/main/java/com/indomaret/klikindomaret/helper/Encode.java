package com.indomaret.klikindomaret.helper;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by indomaretitsd7 on 6/10/16.
 */
public class Encode {
    public static final String md5(String toEncrypt) {
        try {
            final MessageDigest digest = MessageDigest.getInstance("md5");
            digest.update(toEncrypt.getBytes());
            final byte[] bytes = digest.digest();
            final StringBuilder sb = new StringBuilder();

            for (int i = 0; i < bytes.length; i++) {
                sb.append(String.format("%02X", bytes[i]));
            }

            return sb.toString().toLowerCase();
        } catch (Exception exc) {
            return "";
        }
    }

    public String sha256(String param) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(param.getBytes());

        byte byteData[] = md.digest();
        StringBuffer hexString = new StringBuffer();

        for (int i=0;i<byteData.length;i++) {
            String hex=Integer.toHexString(0xff & byteData[i]);
            if(hex.length()==1) hexString.append('0');
            hexString.append(hex);
        }

        return hexString.toString();
    }
}
