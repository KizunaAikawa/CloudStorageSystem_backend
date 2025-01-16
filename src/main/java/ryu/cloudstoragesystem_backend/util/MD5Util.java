package ryu.cloudstoragesystem_backend.util;

import java.security.MessageDigest;

public class MD5Util {
    public static String getMD5(byte[] data) {
        String result = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(data);
            data = md.digest();
            result = bytesToHex(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte i : bytes) {
            String hex = Integer.toHexString(i & 0xFF);
            if (hex.length() < 2) {
                sb.append(0);
            }
            sb.append(hex);
        }
        return sb.toString();
    }
}
