package com.code.challenge.util;

import com.code.challenge.entities.Transaction;

import java.security.SecureRandom;

// Creamos una clase Util para generar todos aquellos métodos reutilizables por el sistema
public class Util {
    // Creamos un método que devuelva una lista de caracteres alfanuemericos de forma aleatoria
    public String getAlphaNumeric(int len) {

        char[] ch = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

        char[] c = new char[len];
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < len; i++) {
            c[i] = ch[random.nextInt(ch.length)];
        }

        return new String(c);
    }
}
