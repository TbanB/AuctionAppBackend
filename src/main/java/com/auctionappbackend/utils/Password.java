package com.auctionappbackend.utils;

import org.mindrot.jbcrypt.BCrypt;

public class Password {

    /**
     * Genera un hash de la contraseña usando BCrypt.
     *
     * @param plainPassword La contraseña en texto plano.
     * @return El hash cifrado de la contraseña.
     */
    public static String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }

    /**
     * Verifica si una contraseña en texto plano coincide con el hash almacenado.
     *
     * @param plainPassword La contraseña en texto plano.
     * @param hashedPassword El hash almacenado de la contraseña.
     * @return `true` si coinciden, `false` en caso contrario.
     */
    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
}
