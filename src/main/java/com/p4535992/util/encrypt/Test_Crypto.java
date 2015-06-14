/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.p4535992.util.encrypt;

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
