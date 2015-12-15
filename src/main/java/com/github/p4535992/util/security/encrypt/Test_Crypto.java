package com.github.p4535992.util.security.encrypt;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

/**
 *
 * @author Marco
 */
public class Test_Crypto {
    
    
    //TEST
    
    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException, Exception {
        String cleartext = "Università degli studi di Firenze";
        String masterpassword ="AAAA";
        String crypto = SimpleCrypto.encrypt(masterpassword, cleartext);
        System.out.println(crypto);
        cleartext = SimpleCrypto.decrypt(masterpassword, crypto);
        System.out.println(cleartext);
        
    }//main
    
}
