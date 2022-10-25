package hsosstp;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class Main {
    public static final int PORT = 8999;

    public static final String PROTOCOL_NAME = "HSOSSTP";
    public static final Charset CHARSET = StandardCharsets.US_ASCII;
    public static final String INITX = PROTOCOL_NAME + "_INITX";
    public static final String GETXX = PROTOCOL_NAME + "_GETXX";
    public static final String ERROR = PROTOCOL_NAME + "_ERROR";
    public static final String SIDXX = PROTOCOL_NAME + "_SIDXX";
    public static final String DATAX = PROTOCOL_NAME + "_DATAX";

    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            printUsage();
        } else if (args[0].equals("server")) {
            int port = PORT;
            if (args.length == 3) {
                if (args[1].equals("-p")) {
                    try {
                        port = Integer.parseInt(args[2]);
                    } catch (NumberFormatException e) {
                        printUsage();
                    }
                } else {
                    printUsage();
                }
            }
            hsosstp.server.Server.start(port);
        } else if(args[0].equals("client")){
            if(args.length == 3 || args.length == 5){
                String address = args[1];
                String filename = args[2];
                int port = PORT;
                if(args.length == 5){
                    if (args[3].equals("-p")) {
                        try {
                            port = Integer.parseInt(args[4]);
                        } catch (NumberFormatException e) {
                            printUsage();
                        }
                    } else {
                        printUsage();
                    }
                }
                hsosstp.client.Client.run(address,filename,port);
            }
        }
    }

    private static void printUsage() {
        System.err.println("Usage:");
        System.err.println("hsosstp server [-p port]");
        System.err.println("hsosstp client <address> <filename> [-p port]");
        System.exit(0);
    }


}
