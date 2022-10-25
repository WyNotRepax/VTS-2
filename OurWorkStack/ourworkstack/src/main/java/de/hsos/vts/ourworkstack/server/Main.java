package de.hsos.vts.ourworkstack.server;

import de.hsos.vts.ourworkstack.common.Server;
import de.hsos.vts.ourworkstack.common.Util;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Main {
    public static void main(String[] args) {
        try {
            LocateRegistry.createRegistry(Util.PORT);
            Server stub = (ServerImpl) new ServerImpl();
            Registry registry = LocateRegistry.getRegistry(Util.PORT);
            registry.rebind(Util.NAME, stub);

System.exit(0);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
