package com.nicoGuti.optica.util;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class FuncionesVarias  {

    public String generarPasswordAleatoria() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(8);
        for (int i = 0; i < 8; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }


    public static String generarTokenDeClave(){
        String caracteres="0123456789";
        SecureRandom random=new SecureRandom();
        StringBuilder sb=new StringBuilder();
        for(int i=0;i<7; i++){
            int index = random.nextInt(caracteres.length());
            sb.append(caracteres.charAt(index));
        }
        return sb.toString();

    }

}
