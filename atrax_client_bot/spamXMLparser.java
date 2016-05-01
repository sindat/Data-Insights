
package Atrax.atrax_client_bot;

import java.io.IOException;
import java.util.ArrayList;
import javax.xml.parsers.ParserConfigurationException;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
/**************************************
 * this class:
 *  parses the XML config parameters 
 *  file, which contains the response
 *  from the C&C server
 * @author sindat
 **************************************/
public class spamXMLparser {
    public ArrayList<spamXMLparser> spamConfigArrayList = new 
        ArrayList<>();
    public String victimAddress;
    public String victimFirstName;
    public String victimLastName;
    public String emailFromAddress;
    public String emailSubject;
    public String emailBody;
    
    // empty object for reference 
    public spamXMLparser() {
    
    }
    
    public spamXMLparser(String theVictimAddress, String theVictimFirstName, String theVictimLastName,
            String theEmailSubject, String theEmailFromAddress, String theEmailBody) {
        spamConfigArrayList = null;
        victimAddress = theVictimAddress;
        victimFirstName = theVictimFirstName;
        victimLastName = theVictimLastName;
        emailSubject = theEmailSubject;
        emailFromAddress = theEmailFromAddress;
        emailBody = theEmailBody;
    }
    
    public ArrayList<spamXMLparser> returnSpamConfigs (String destFile) throws SAXException, ParserConfigurationException, IOException {
        try {
            SAXParserFactory theFactory = SAXParserFactory.newInstance();
            SAXParser theSaxParser = theFactory.newSAXParser();
            
            DefaultHandler theHandler = new DefaultHandler() {
                boolean bvictdetails = false;
                @SuppressWarnings("unused")
                boolean bemaildetails = false;
                boolean baddr = false;
                boolean bfname = false;
                boolean blname = false;
                boolean bsubject = false;
                boolean bfromaddr = false;
                boolean bbody = false;
                
                @Override
                public void startElement (String uri, String localName, String qName, Attributes attributes) 
                    throws SAXException {
                        if (qName.equalsIgnoreCase("victimDetails")) {
                            bvictdetails = false;
                        } 
                        if (qName.equalsIgnoreCase("emailDetails")) {
                            bemaildetails = false;
                        }
                        if (qName.equalsIgnoreCase("address")) {
                            baddr = true;
                        }
                        if (qName.equalsIgnoreCase("firstName")) {
                            bfname = true;
                        }
                        if (qName.equalsIgnoreCase("lastName")) {
                            blname = true;
                        }
                        if (qName.equalsIgnoreCase("subject")) {
                            bsubject = true;
                        }
                        if (qName.equalsIgnoreCase("fromAddress")) {
                            bfromaddr = true;
                        }
                        if (qName.equalsIgnoreCase("body")) {
                            bbody = true;
                        }
                    }
                    
                    public void endElement(String uri, String localName, String qName) throws SAXException {
                        if (qName.equalsIgnoreCase("victimDetails")) {
                            bvictdetails = true;
                        }
                        if (qName.equalsIgnoreCase("emailDetails")) {
                            bemaildetails = true;
                        }
                    }
                    public void characters (char ch[], int start, int length) {
                        if (bvictdetails) {
                            spamXMLparser theCurrentVictim = new spamXMLparser(victimAddress, victimFirstName, 
                            victimLastName, emailSubject, emailFromAddress, emailBody);
                            if (theCurrentVictim != null) {
                                spamConfigArrayList.add(theCurrentVictim);
                            }
                            bvictdetails = false;
                        }
                        
                        if (baddr) {
                            victimAddress = new String(ch, start, length);
                            baddr = false;
                        }
                        
                        if (bfname) {
                            victimFirstName = new String(ch, start, length);
                            bfname = false;
                        }
                        
                        if (blname) {
                            victimLastName = new String(ch, start, length);
                            blname = false;
                        }
                        
                        if (bsubject) {
                            emailSubject = new String(ch, start, length);
                            bsubject = false;
                        }
                        
                        if (bfromaddr) {
                            emailFromAddress = new String(ch, start, length);
                            bfromaddr = false;
                        }
                        
                        if (bbody) {
                            emailBody = new String(ch, start, length);
                            bbody = false;
                        }
                    }  
                };
            theSaxParser.parse(destFile, theHandler);
            
        } catch(ParserConfigurationException | SAXException | IOException e) {
        }
        return spamConfigArrayList;
    }
}
