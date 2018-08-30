package org.launchcode.stocks.models;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public abstract class Hash {

    /**
     * This function creates a salt to further encrypt passwords.
     * @return
     */
    private static String makeSalt()
    {
        Random rand = new Random();
        String salt = "";
        int val;
        char c;

        for (int i=0; i<8; i++)
        {
            val = rand.nextInt(128);
            c = (char) val;
            salt += c;
        }

        return salt;
    }

    /**
     * This function hashes a given password.
     *
     * The code was derived from the following youtube video:
     *
     * Generating MD5, SHA hash in java
     * https://www.youtube.com/watch?v=9eisErB4MO8
     *
     * @param password the password to be hashed.
     * @return The password to be hashed.
     */

    public static String hashPassword(String password) {
        String salt = makeSalt();
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-256");

            password += salt;
            md.update(password.getBytes());
            byte[] bytes = md.digest();
            StringBuffer hash = new StringBuffer();
            for (byte b : bytes)
            {
                hash.append(Integer.toHexString(b & 0xff));
            }

            return String.format("%s;%s", hash.toString(), salt);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     *
     * @param password
     * @param salt
     * @return
     */
    private static String hashPassword(String password, String salt) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");

            password += salt;
            md.update(password.getBytes());
            byte[] bytes = md.digest();
            StringBuffer hash = new StringBuffer();
            for (byte b : bytes)
            {
                hash.append(Integer.toHexString(b & 0xff).toString());
            }

            return String.format("%s;%s", hash.toString(), salt);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    public static boolean checkPassword(String password, String hash) {
        String salt = hash.split(";")[1];
        if (hashPassword(password, salt).equals(hash))
        {
            return true;
        }
        else
        {
            return false;
        }
    }
}
