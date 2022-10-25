package chatsystem.server;

import chatsystem.ChatProxy;
import chatsystem.ChatServer;
import chatsystem.ClientProxy;
import chatsystem.Main;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class ChatServerImpl extends UnicastRemoteObject implements ChatServer {

    private class ChatProxyImpl extends UnicastRemoteObject implements ChatProxy {

        private String username;

        ChatProxyImpl(String username) throws RemoteException {
            this.username = username;
        }

        @Override
        public void sendMessage(String message) {
            for (int i = 0; i < clientProxies.size(); i++) {
                try {
                    clientProxies.get(i).receiveMessage(username, message);
                } catch (RemoteException e) {
                    System.err.printf("Could not send message to client %s (%d)! Removing that client\n", usernames.get(i), i);
                    unsubscribeUser(usernames.get(i));
                }
            }

        }
    }

    private ArrayList<ClientProxy> clientProxies;
    private ArrayList<String> usernames;

    private ChatProxy serverChatProxy;

    public ChatServerImpl() throws RemoteException {
        clientProxies = new ArrayList<>();
        usernames = new ArrayList<>();
        serverChatProxy = new ChatProxyImpl("Server");
    }

    public static void main(String[] args) {
        try {
            LocateRegistry.createRegistry(Main.PORT);
            ChatServer stub = (ChatServer) new ChatServerImpl();
            Registry registry = LocateRegistry.getRegistry(Main.PORT);
            registry.rebind(Main.NAME, stub);

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ChatProxy subscribeUser(String username, ClientProxy handle) {
        try {
            System.out.printf("%s Subscribed\n", username);
            clientProxies.add(handle);
            usernames.add(username);
            serverChatProxy.sendMessage(String.format("%s joined the chat", username));
            return new ChatProxyImpl(username);
        } catch (RemoteException e) {
            System.err.println("Could not subscribe user");
        }
        return null;
    }

    @Override
    public boolean unsubscribeUser(String username) {
        try {
            for (int i = 0; i < usernames.size(); i++) {
                if (usernames.get(i).equals(username)) {
                    usernames.remove(i);
                    clientProxies.remove(i);
                    System.out.printf("%s Unsubscribed\n", username);
                    serverChatProxy.sendMessage(String.format("%s left the chat", username));
                    return true;
                }
            }
            System.out.printf("%s tried to Unsubscribe\n", username);
            return false;
        } catch (RemoteException e) {
            System.err.println("Could not unsubscribe user.");
        }
        return false;
    }
}
