package com.pavanahv.allakumarreddy.moneybook.test;

import com.pavanahv.allakumarreddy.moneybook.utils.SecurityUtils;

import java.security.SecureRandom;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class Test {

    public static void main(String args[]) throws Exception {
        StringBuilder data = new StringBuilder("string");
        for (int i = 0; i < 500; i++) {
            data.append("STRING");
        }
        byte[] b = data.toString().getBytes();

        byte[] keyStart = "this is a keythis is a keythis is a keythis is a keythis is a keythis is a key".getBytes();
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        sr.setSeed(keyStart);
        kgen.init(128, sr); // 192 and 256 bits may not be available
        SecretKey skey = kgen.generateKey();
        byte[] key = skey.getEncoded();

        byte[] encryptedData = SecurityUtils.encrypt(key, b);
        byte[] decryptedData = SecurityUtils.decrypt(key, encryptedData);
        String s = new String(decryptedData);
        System.out.println(s);
    }
}
