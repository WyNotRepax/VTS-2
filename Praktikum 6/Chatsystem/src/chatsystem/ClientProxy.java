package chatsystem;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientProxy extends Remote {
    public void receiveMessage(String username,String message)throws RemoteException;
}
