package chatsystem;
import chatsystem.client.ClientProxyImpl;
import chatsystem.server.ChatServerImpl;
import java.rmi.registry.Registry;

public class Main {

    public static final int PORT = Registry.REGISTRY_PORT;
    public static final String NAME = "ChatServer";

    public static void main(String[] args) {
        if(args.length < 1){
            printUsage();
            System.exit(1);
        }
        switch (args[0]){
            case "client":
                ClientProxyImpl.main(args);
                break;
            case "server":
                ChatServerImpl.main(args);
                break;
            default:
                printUsage();
        }

    }

    private static void printUsage(){
        System.err.println("Usage:");
        System.err.println("chatsystem client [hostaddress]");
        System.err.println("chatsystem server");
    }
}
