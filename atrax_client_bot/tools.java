
package Atrax.atrax_client_bot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
/**************************************************
 * this class:
 *  contains some of the supporting functionality
 *  for the bot
 * @author sindat
 **************************************************/
public class tools {
    /***********************************************
     * this method: 
     *  is responsible for the sleep functionality 
     *  of the bot.
     * @param cycle
     * @param randomness
     * @param debug 
     ***********************************************/
    public static void sleep(int cycle, int randomness, boolean debug) {
        // random number generator
        Random randomGenerator = new Random();
        
        try {
            Thread.currentThread();
            int randomInt = randomGenerator.nextInt(randomness);
            int sleepTime = cycle + randomInt;
            if (debug) {
                System.out.println("SLEEPING FOR " + sleepTime + " seconds.");
                Thread.sleep(sleepTime * 1000);
            }
        } catch (InterruptedException e) {
        }
    }
    /*************************************************
         * this method:
         *  returns the md5 checksum of a string
     * @param msg
     * @return 
     * @throws java.io.UnsupportedEncodingException
     * @throws java.security.NoSuchAlgorithmException
    **************************************************/
    public static String computeMD5 (String msg) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        byte[] bytesOfMessage = null;
        try {
            bytesOfMessage = msg.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
          }
        MessageDigest msg_digest = null;
        try {
            msg_digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
        }
        byte[] byteDigest = msg_digest.digest(bytesOfMessage);
        BigInteger bigInt = new BigInteger(1, byteDigest);
        String hashText = bigInt.toString(16);
        while (hashText.length() < 32) {
            hashText = "0" + hashText;
        }
        return hashText;
    }
    
    /*******************************************
     * this method:
     *  fires up a command on the command line
     *  of the host
     *  logs the results 
     * NOTICE
     * this is for LINUX HOSTS ONLY
     * @param command
     * @return 
     * @throws java.io.IOException 
     *******************************************/
    public static String runCmd(String command) throws IOException{
        String cmdOutput = "";
        String s = null;
        
        try {
            Process p = Runtime.getRuntime().exec(command);
            BufferedReader std_input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            
            while ((s = std_input.readLine()) != null) {
                cmdOutput += s + "\n";
            }
        } catch(IOException e) {
                System.exit(-1);
          }
        return cmdOutput;
    }
}
