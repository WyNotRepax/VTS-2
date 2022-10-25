
package hsosstp.server;

import hsosstp.Main;

import java.io.*;
import java.net.*;
import java.util.*;

public class Server {

    private static DatagramSocket socket = null;

    private static ArrayList<Session> sessions = new ArrayList<>();


    public static void start(int port) {
        try {
            socket = new DatagramSocket(port);
            run();
        } catch (SocketException e) {
            System.err.println("Could not Create Socket!");
            System.exit(-1);
        }
    }


    private static void run() {

        while (true) {
            try {
                byte[] buf = new byte[256];

                // receive request
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                System.out.println("Waiting for request:");
                socket.receive(packet);

                String requestString = new String(buf, hsosstp.Main.CHARSET).trim();
                System.out.println("Got request: " + requestString);
                String[] args = requestString.split(";");

                if (args.length > 1) {
                    switch (args[0]) {
                        case Main.INITX:
                            initSession(socket, packet);
                            break;
                        case Main.GETXX:
                            getData(socket, packet);
                            break;
                        default:
                            System.out.println("Malformed Request; Not Responding");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void initSession(DatagramSocket socket, DatagramPacket packet) throws IOException {
        String[] args = new String(packet.getData()).trim().split(";");
        if (args.length == 3) {
            try {
                int chunkSize = Integer.parseInt(args[1]);
                String filename = args[2];
                RandomAccessFile in = new RandomAccessFile(new File(filename), "r");
                Session newSession = new Session(in, chunkSize);
                sessions.add(newSession);
                sendSIDXX(socket, packet.getAddress(), packet.getPort(), newSession.sessionId);

            } catch (NumberFormatException e) {
                System.out.println("Malformed Request; Not Responding");
            } catch (FileNotFoundException e) {
                sendFNF(socket, packet.getAddress(), packet.getPort());
            }
        }
    }

    private static void getData(DatagramSocket socket, DatagramPacket packet) throws IOException {
        String[] args = new String(packet.getData()).trim().split(";");
        try {
            int sessionId = Integer.parseInt(args[1]);
            int chunkNumber = Integer.parseInt(args[2]);
            for (Session session : sessions) {
                if (session.sessionId == sessionId) {
                    System.out.println("Getting chunk " + chunkNumber + " for " + sessionId);
                    byte[] chunk = session.getChunk(chunkNumber);
                    //System.out.println("Chunk: " + new String(chunk));
                    if (chunk.length == 0) {
                        sendCNF(socket, packet.getAddress(), packet.getPort());
                    }
                    sendDATAX(socket, packet.getAddress(), packet.getPort(), chunkNumber, chunk);
                    return;
                }
            }
            sendNOS(socket, packet.getAddress(), packet.getPort());
        } catch (NumberFormatException e) {
            System.out.println("Malformed Request; Not Responding");
        }

    }

    private static void sendStr(DatagramSocket socket, InetAddress address, int port, String message) throws IOException {
        byte[] msgBuff = message.getBytes(Main.CHARSET);
        DatagramPacket packet = new DatagramPacket(msgBuff, msgBuff.length, address, port);
        socket.send(packet);
    }

    private static void sendFNF(DatagramSocket socket, InetAddress address, int port) throws IOException {
        sendStr(socket, address, port, Main.ERROR + ";FNF");
    }

    private static void sendCNF(DatagramSocket socket, InetAddress address, int port) throws IOException {
        sendStr(socket, address, port, Main.ERROR + ";CNF");
    }

    private static void sendNOS(DatagramSocket socket, InetAddress address, int port) throws IOException {
        sendStr(socket, address, port,Main.ERROR + ";NOS");
    }

    private static void sendSIDXX(DatagramSocket socket, InetAddress address, int port, int sessionId) throws IOException {
        sendStr(socket, address, port, Main.SIDXX + ";" + sessionId);
    }

    private static void sendDATAX(DatagramSocket socket, InetAddress address, int port, int chunkNumber, byte[] data) throws IOException {
        byte[] prefix = (Main.DATAX + ";" + chunkNumber + ";" + data.length + ";").getBytes(Main.CHARSET);
        byte[] msgBuff = new byte[prefix.length + data.length];

        System.arraycopy(prefix, 0, msgBuff, 0, prefix.length);
        System.arraycopy(data, 0, msgBuff, prefix.length, data.length);

        DatagramPacket packet = new DatagramPacket(msgBuff, msgBuff.length, address, port);
        socket.send(packet);
    }


}