package com.ferre.config;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public final class Security {
    private Security(){}
    public static String sha256Hex(String text){
        try{
            var md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(text.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(hash.length * 2);
            for (byte b : hash){
                sb.append(String.format("%02X", b)); // HEX mayúsculas (MySQL SHA2 usa hex)
            }
            return sb.toString();
        }catch(Exception e){ throw new RuntimeException(e); }
    }
}
