package jvn;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class JvnMainServer {
    public static void main(String[] args) throws Exception {
        JvnRemoteCoord jvn = new JvnCoordImpl();
        Registry registry;
        registry = LocateRegistry.createRegistry(6090);
        registry.rebind("coord", jvn);
    }

}
