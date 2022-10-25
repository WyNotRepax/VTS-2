package chatsystem;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ChatProxy extends Remote {
    public void sendMessage(String message) throws RemoteException;
}
