
package hsosstp.client;

import hsosstp.Main;

import java.io.*;
import java.net.*;


public class Client {

    public static final int CHUNKSIZE = 20;
    public static final int HEADSPACE = 100;
    public static final int BUFFSIZE = CHUNKSIZE + HEADSPACE;

    public static void run(String address, String filename,int port) throws IOException {

        // get a datagram socket
        DatagramSocket socket = new DatagramSocket();

        // Create Initial message
        String msg = Main.INITX + ";" + CHUNKSIZE + ";" + filename;
        byte[] msgBuf = msg.getBytes(Main.CHARSET);

        // Resolve Address
        InetAddress inetAddress = InetAddress.getByName(address);

        // Create Packet
        DatagramPacket packet = new DatagramPacket(msgBuf, msgBuf.length, inetAddress, port);

        // Send Packet
        socket.send(packet);

        // Receive SessionId

        byte[] responseBuffer = new byte[BUFFSIZE];
        DatagramPacket responsePacket = new DatagramPacket(responseBuffer, responseBuffer.length);
        System.out.println("Waiting for SessionId:");
        socket.receive(responsePacket);
        System.out.println("Got response:");
        System.out.println(new String(responseBuffer));
        String responseString = new String(responseBuffer).trim();
        String[] responseStrings = responseString.split(";");
        if(responseStrings.length <= 1){
            malformedResponse(responseString);
        }
        if(responseStrings[0].equals(Main.ERROR) && responseStrings[1].equals("FNF")){
            System.out.println("File Not Found on hsosstp.server!");
            System.exit(0);
        }
        try {
            int sessionId = Integer.parseInt(responseString.split(";")[1]);System.out.println("SessionId: " + sessionId);

            int chunkNr = 0;
            RandomAccessFile out = new RandomAccessFile(new File(filename),"rw");
            while (true) {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                String request = Main.GETXX + ";" + sessionId + ";" + chunkNr;
                byte[] requestBytes = request.getBytes(Main.CHARSET);
                DatagramPacket requestPacket = new DatagramPacket(requestBytes, requestBytes.length, inetAddress, port);
                socket.send(requestPacket);


                byte[] responseBytes = new byte[BUFFSIZE];
                responsePacket = new DatagramPacket(responseBytes, responseBytes.length);
                socket.receive(responsePacket);
                responseString = new String(responseBytes, Main.CHARSET).trim();
                responseStrings = responseString.split(";");
                if(responseStrings.length <= 1) {
                    malformedResponse(responseString);
                }
                else if (responseStrings[0].equals(Main.ERROR) && responseStrings[1].equals("CNF")) {
                    break;
                }
                else if (responseStrings[0].equals(Main.DATAX) && responseStrings.length >= 4) {
                        int chunkNumber = Integer.parseInt(responseStrings[1]);
                        int chunkSize = Integer.parseInt(responseStrings[2]);
                        int offset = responseStrings[0].length() + responseStrings[1].length() + responseStrings[2].length() + 3;
                        byte[] data = new byte[chunkSize];
                        System.arraycopy(responseBytes, offset, data, 0, data.length);
                        out.seek(CHUNKSIZE * chunkNumber);
                        out.write(data);
                } else {
                    malformedResponse(responseString);
                }


                chunkNr++;
            }
            out.close();
        }catch (NumberFormatException e){
            malformedResponse(responseString);
        }

        socket.close();
    }

    private static void malformedResponse(String responseString){

        System.err.println("Malformed Response: " + responseString);
    }


}