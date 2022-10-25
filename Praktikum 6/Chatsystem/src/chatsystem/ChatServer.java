package chatsystem;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ChatServer extends Remote {
    public ChatProxy subscribeUser(String username, ClientProxy handle) throws RemoteException;
    public boolean unsubscribeUser(String username) throws RemoteException;
}
