/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatsystem.servlet;
import chatsystem.ClientProxy;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Base64;
/**
 *
 * @author luca
 */
public class ClientProxyImpl extends UnicastRemoteObject implements ClientProxy{

    public final ArrayList<Message> messages = new ArrayList();
    
    ClientProxyImpl() throws RemoteException {
        super();
    }
    
    @Override
    public void receiveMessage(String username, String message) throws RemoteException {
        messages.add(new Message(username,message));
    }
    
    public static class Message{
        private final String username;
        private final String message;
        
        Message(String username, String message){
            this.username = username;
            this.message = message;
        }
        
        @Override
        public String toString(){
            
            return String.format("%s: %s\n",username,message);
        }
    }
}
