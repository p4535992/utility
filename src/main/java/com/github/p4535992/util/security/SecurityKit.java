package com.github.p4535992.util.security;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by 4535992 on 14/07/2015.
 * @author 4535992.
 * @version 2015-07-14.
 */
@SuppressWarnings("unused")
public class SecurityKit {

    class HidePasswordFromCommandLine extends Thread {
        boolean stopThread = false;
        boolean hideInput = false;
        boolean shortMomentGone = false;

        public void run() {
            try {
                sleep(500);
            } catch (InterruptedException e) {
            }
            shortMomentGone = true;
            while (!stopThread) {
                if (hideInput) {
                    System.out.print("\b*");
                }
                try {
                    sleep(1);
                } catch (InterruptedException e) {
                }
            }
        }

        public void main(String[] arguments) {
            String name= "";
            String password= "";
            HidePasswordFromCommandLine hideThread= new HidePasswordFromCommandLine();
            hideThread.start();
            BufferedReader in= new BufferedReader(new InputStreamReader(System.in));
            try {
                System.out.println("Name: ");
                // Aspetta l'immissione dello username e pulisce il buffer della tastiera
                do {
                    name= in.readLine();
                }
                while (!hideThread.shortMomentGone);
                //
                // Nasconde il thread e sovrascrive l'input con "*"
                hideThread.hideInput= true;
                // Legge la password
                System.out.println("\nPassword:");
                System.out.print(" ");
                password = in.readLine();
                hideThread.stopThread= true;
            }
            catch (Exception e) {}
            System.out.print("\b \b");
            // Solo per testing, eliminare!
            System.out.println("\n\nLogin= " + name);
            System.out.println("Password= " + password);
        }
    }
}
